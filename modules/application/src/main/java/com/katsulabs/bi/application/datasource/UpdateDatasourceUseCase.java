package com.katsulabs.bi.application.datasource;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.datasource.DatasourceRepository;

public class UpdateDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;

    public UpdateDatasourceUseCase(DatasourceRepository datasourceRepository) {
        this.datasourceRepository = datasourceRepository;
    }

    public ServiceResult execute(String userId, long id, DatasourceWriteCommand command) {
        if (datasourceRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("데이터소스를 찾을 수 없습니다.");
        }
        if (datasourceRepository.existsByName(userId, command.name(), id)) {
            return ServiceResult.fail("Duplicated Name!");
        }
        datasourceRepository.update(id, command.name(), command.configJson());
        return ServiceResult.success("success");
    }
}
