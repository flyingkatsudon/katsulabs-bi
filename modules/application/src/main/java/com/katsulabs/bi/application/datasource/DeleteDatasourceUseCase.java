package com.katsulabs.bi.application.datasource;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.datasource.DatasourceRepository;

@RequiredArgsConstructor
public class DeleteDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;


    public ServiceResult execute(long id) {
        if (datasourceRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("데이터소스를 찾을 수 없습니다.");
        }
        datasourceRepository.delete(id);
        return ServiceResult.success("success");
    }
}
