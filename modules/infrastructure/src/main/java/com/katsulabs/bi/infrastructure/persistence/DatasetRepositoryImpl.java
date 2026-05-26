package com.katsulabs.bi.infrastructure.persistence;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import com.katsulabs.bi.domain.dataset.DatasetDetail;
import com.katsulabs.bi.domain.dataset.DatasetRepository;
import com.katsulabs.bi.domain.dataset.DatasetSummary;
import com.katsulabs.bi.infrastructure.persistence.compat.CboardDatasetJson;
import com.katsulabs.bi.infrastructure.persistence.mybatis.DatasetColumnMapper;
import com.katsulabs.bi.infrastructure.persistence.mybatis.DatasetMapper;
import com.katsulabs.bi.infrastructure.persistence.mybatis.DatasetRow;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DatasetRepositoryImpl implements DatasetRepository {

    private final DatasetMapper datasetMapper;
    private final DatasetColumnMapper datasetColumnMapper;


    @Override
    public List<DatasetSummary> findAllSummaries() {
        return datasetMapper.findAllSummaries().stream().map(DatasetRepositoryImpl::toSummary).toList();
    }

    @Override
    public Optional<DatasetDetail> findById(long datasetId) {
        DatasetRow row = datasetMapper.findById(datasetId);
        if (row == null) {
            return Optional.empty();
        }
        var columns = datasetColumnMapper.findByDatasetId(datasetId);
        String dataJson = CboardDatasetJson.compose(row, columns);
        return Optional.of(toDetail(row, dataJson));
    }

    @Override
    public boolean existsByName(String userId, String name, String categoryName, Long excludeId) {
        return datasetMapper.countByName(userId, name, defaultCategory(categoryName), excludeId) > 0;
    }

    @Override
    public long insert(String userId, String name, String categoryName, String dataJson) {
        DatasetRow row = new DatasetRow();
        row.setUserId(userId);
        row.setName(name);
        row.setCategoryName(defaultCategory(categoryName));
        row.setPlatformShared(userId != null && userId.startsWith("admin"));
        CboardDatasetJson.persistFromJson(datasetMapper, datasetColumnMapper, row, dataJson);
        return row.getId();
    }

    @Override
    public void update(long id, String name, String categoryName, String dataJson) {
        DatasetRow row = datasetMapper.findById(id);
        if (row == null) {
            throw new IllegalArgumentException("데이터셋을 찾을 수 없습니다: " + id);
        }
        row.setName(name);
        row.setCategoryName(defaultCategory(categoryName));
        CboardDatasetJson.persistFromJson(datasetMapper, datasetColumnMapper, row, dataJson);
    }

    @Override
    public void delete(long id) {
        datasetColumnMapper.deleteByDatasetId(id);
        datasetMapper.delete(id);
    }

    private static DatasetSummary toSummary(DatasetRow row) {
        return new DatasetSummary(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryName(),
                row.getDatasourceId(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static DatasetDetail toDetail(DatasetRow row, String dataJson) {
        return new DatasetDetail(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryName(),
                dataJson,
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static String defaultCategory(String categoryName) {
        return categoryName == null || categoryName.isBlank() ? "Default Category" : categoryName;
    }
}
