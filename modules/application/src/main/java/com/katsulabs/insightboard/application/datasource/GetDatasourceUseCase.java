package com.katsulabs.insightboard.application.datasource;

import com.katsulabs.insightboard.domain.datasource.DatasourceDetail;
import com.katsulabs.insightboard.domain.datasource.DatasourceRepository;

public class GetDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;

    public GetDatasourceUseCase(DatasourceRepository datasourceRepository) {
        this.datasourceRepository = datasourceRepository;
    }

    public DatasourceDetail execute(long id) {
        return datasourceRepository.findById(id)
                .orElseThrow(() -> new DatasourceNotFoundException(id));
    }
}
