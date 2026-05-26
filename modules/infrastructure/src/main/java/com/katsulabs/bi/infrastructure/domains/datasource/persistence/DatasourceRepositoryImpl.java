package com.katsulabs.bi.infrastructure.domains.datasource.persistence;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import com.katsulabs.bi.domain.domains.datasource.DatasourceDetail;
import com.katsulabs.bi.domain.domains.datasource.DatasourceRepository;
import com.katsulabs.bi.domain.domains.datasource.DatasourceSummary;
import com.katsulabs.bi.infrastructure.domains.datasource.persistence.mybatis.DatasourceMapper;
import com.katsulabs.bi.infrastructure.domains.datasource.persistence.mybatis.DatasourceRow;
import com.katsulabs.bi.infrastructure.common.persistence.PersistenceMapperSupport;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DatasourceRepositoryImpl implements DatasourceRepository {

    private final DatasourceMapper datasourceMapper;


    @Override
    public List<DatasourceSummary> findAllSummaries() {
        return datasourceMapper.findAllSummaries().stream().map(DatasourceRepositoryImpl::toSummary).toList();
    }

    @Override
    public Optional<DatasourceDetail> findById(long datasourceId) {
        DatasourceRow row = datasourceMapper.findById(datasourceId);
        return row == null ? Optional.empty() : Optional.of(toDetail(row));
    }

    private static DatasourceSummary toSummary(DatasourceRow row) {
        return new DatasourceSummary(
                row.getId(),
                row.getName(),
                row.getType(),
                row.getUserId(),
                row.getUserName(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static DatasourceDetail toDetail(DatasourceRow row) {
        return new DatasourceDetail(
                row.getId(),
                row.getName(),
                row.getType(),
                row.getUserId(),
                row.getUserName(),
                row.getConfig(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    @Override
    public boolean existsByName(String userId, String name, Long excludeId) {
        return datasourceMapper.countByName(userId, name, excludeId) > 0;
    }

    @Override
    public long insert(String userId, String name, String type, String configJson) {
        DatasourceRow row = new DatasourceRow();
        row.setUserId(userId);
        row.setName(name);
        row.setType(type);
        row.setConfig(configJson);
        datasourceMapper.insert(row);
        return row.getId();
    }

    @Override
    public void update(long id, String name, String type, String configJson) {
        DatasourceRow row = new DatasourceRow();
        row.setId(id);
        row.setName(name);
        row.setType(type);
        row.setConfig(configJson);
        datasourceMapper.update(row);
    }

    @Override
    public void delete(long id) {
        datasourceMapper.delete(id);
    }
}
