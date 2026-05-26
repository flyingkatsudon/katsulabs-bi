package com.katsulabs.bi.application.datasource;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.domain.datasource.DatasourceDetail;
import com.katsulabs.bi.domain.datasource.DatasourceRepository;

@RequiredArgsConstructor
public class GetDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;


    public DatasourceDetail execute(long id) {
        return datasourceRepository.findById(id)
                .orElseThrow(() -> new DatasourceNotFoundException(id));
    }
}
