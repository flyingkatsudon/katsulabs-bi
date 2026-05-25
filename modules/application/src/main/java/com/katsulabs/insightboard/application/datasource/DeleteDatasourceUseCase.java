package com.katsulabs.insightboard.application.datasource;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.datasource.DatasourceRepository;

public class DeleteDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;

    public DeleteDatasourceUseCase(DatasourceRepository datasourceRepository) {
        this.datasourceRepository = datasourceRepository;
    }

    public ServiceResult execute(long id) {
        if (datasourceRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("데이터소스를 찾을 수 없습니다.");
        }
        datasourceRepository.delete(id);
        return ServiceResult.success("success");
    }
}
