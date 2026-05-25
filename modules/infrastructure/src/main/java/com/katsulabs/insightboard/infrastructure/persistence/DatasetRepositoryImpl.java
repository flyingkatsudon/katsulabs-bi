package com.katsulabs.insightboard.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.katsulabs.insightboard.application.support.JsonMapper;
import com.katsulabs.insightboard.domain.dataset.DatasetDetail;
import com.katsulabs.insightboard.domain.dataset.DatasetRepository;
import com.katsulabs.insightboard.domain.dataset.DatasetSummary;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.DatasetMapper;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.DatasetRow;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;

@Repository
public class DatasetRepositoryImpl implements DatasetRepository {

    private final DatasetMapper datasetMapper;

    public DatasetRepositoryImpl(DatasetMapper datasetMapper) {
        this.datasetMapper = datasetMapper;
    }

    @Override
    public List<DatasetSummary> findAllSummaries() {
        return datasetMapper.findAllSummaries().stream().map(DatasetRepositoryImpl::toSummary).toList();
    }

    @Override
    public Optional<DatasetDetail> findById(long datasetId) {
        DatasetRow row = datasetMapper.findById(datasetId);
        return row == null ? Optional.empty() : Optional.of(toDetail(row));
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
        row.setData(dataJson);
        datasetMapper.insert(row);
        return row.getId();
    }

    @Override
    public void update(long id, String name, String categoryName, String dataJson) {
        DatasetRow row = new DatasetRow();
        row.setId(id);
        row.setName(name);
        row.setCategoryName(defaultCategory(categoryName));
        row.setData(dataJson);
        datasetMapper.update(row);
    }

    @Override
    public void delete(long id) {
        datasetMapper.delete(id);
    }

    private static DatasetSummary toSummary(DatasetRow row) {
        return new DatasetSummary(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryName(),
                parseDatasourceId(row.getData()),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static Long parseDatasourceId(String dataJson) {
        if (dataJson == null || dataJson.isBlank()) {
            return null;
        }
        try {
            JsonNode root = JsonMapper.mapper().readTree(dataJson);
            long id = root.path("datasource").asLong(0);
            return id > 0 ? id : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static DatasetDetail toDetail(DatasetRow row) {
        return new DatasetDetail(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryName(),
                row.getData(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static String defaultCategory(String categoryName) {
        return categoryName == null || categoryName.isBlank() ? "默认分类" : categoryName;
    }
}
