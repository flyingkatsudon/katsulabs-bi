package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DatasourceMapper {

    List<DatasourceRow> findAllSummaries();

    DatasourceRow findById(@Param("datasourceId") long datasourceId);

    long countByName(@Param("userId") String userId, @Param("name") String name, @Param("excludeId") Long excludeId);

    void insert(DatasourceRow row);

    void update(DatasourceRow row);

    void delete(@Param("datasourceId") long datasourceId);
}
