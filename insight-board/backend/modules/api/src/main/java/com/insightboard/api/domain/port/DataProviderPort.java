package com.insightboard.api.domain.port;

import com.insightboard.api.application.cboard.dto.AggregateResultDto;
import com.insightboard.api.application.cboard.dto.DataProviderResultDto;
import com.insightboard.api.application.cboard.dto.ServiceStatusDto;
import java.util.List;
import java.util.Map;

/**
 * 외부 데이터 소스(JDBC 등) 집계·조회 포트. 구현은 {@code external} 모듈.
 */
public interface DataProviderPort {

    List<Map<String, Object>> getDatasourceParams(String type);

    String getDatasourceView(String type);

    AggregateResultDto queryAggData(
            Long datasourceId, Long datasetId, String cfg, Map<String, String> queryParams);

    String viewAggDataQuery(
            Long datasourceId, Long datasetId, String cfg, Map<String, String> queryParams);

    DataProviderResultDto getColumns(
            Long datasourceId, Long datasetId, Map<String, String> queryParams);

    String[] getDimensionValues(
            Long datasourceId,
            Long datasetId,
            String columnName,
            String cfg,
            Map<String, String> queryParams);

    ServiceStatusDto testDatasource(String datasource, String query);
}
