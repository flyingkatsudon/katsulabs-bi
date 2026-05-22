package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DatasetColumnMapper {
    List<DatasetColumnRow> findByDatasetId(@Param("datasetId") long datasetId);

    void deleteByDatasetId(@Param("datasetId") long datasetId);

    void insert(DatasetColumnRow row);
}
