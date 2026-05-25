package org.cboard.infrastructure.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatasetMapper {

    List<DatasetRow> findAllSummaries();

    DatasetRow findById(@org.apache.ibatis.annotations.Param("datasetId") long datasetId);

    long countByName(
            @org.apache.ibatis.annotations.Param("userId") String userId,
            @org.apache.ibatis.annotations.Param("name") String name,
            @org.apache.ibatis.annotations.Param("categoryName") String categoryName,
            @org.apache.ibatis.annotations.Param("excludeId") Long excludeId);

    void insert(DatasetRow row);

    void update(DatasetRow row);

    void delete(@org.apache.ibatis.annotations.Param("datasetId") long datasetId);
}
