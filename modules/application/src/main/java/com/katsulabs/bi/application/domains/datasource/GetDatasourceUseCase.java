package com.katsulabs.bi.application.domains.datasource;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.domain.domains.datasource.DatasourceDetail;
import com.katsulabs.bi.domain.domains.datasource.DatasourceRepository;

@RequiredArgsConstructor
public class GetDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;


    public DatasourceDetail execute(long id) {
        return datasourceRepository.findById(id)
                .orElseThrow(() -> new DatasourceNotFoundException(id));
    }
}
