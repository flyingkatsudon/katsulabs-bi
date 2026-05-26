package com.katsulabs.bi.application.domains.datasource;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.domains.datasource.DatasourceRepository;

@RequiredArgsConstructor
public class TestDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;
    private final DatasourceConnectionTestPort connectionTestPort;


    public ServiceResult execute(long id) {
        var detail = datasourceRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터소스를 찾을 수 없습니다: " + id));
        return testJdbc(detail.type(), detail.configJson());
    }

    public ServiceResult testConfig(DatasourceWriteCommand command) {
        return testJdbc(command.type(), command.configJson());
    }

    private ServiceResult testJdbc(String type, String configJson) {
        if (!"jdbc".equalsIgnoreCase(type)) {
            return ServiceResult.fail("JDBC 데이터소스만 테스트할 수 있습니다.");
        }
        try {
            connectionTestPort.testJdbcConfig(configJson);
            return ServiceResult.success("Connection OK");
        } catch (Exception e) {
            return ServiceResult.fail(e.getMessage() != null ? e.getMessage() : "Connection failed");
        }
    }
}
