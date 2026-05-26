package com.katsulabs.bi.application.domains.datasource;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.domains.datasource.DatasourceRepository;
import com.katsulabs.bi.domain.domains.datasource.DatasourceSummary;

@RequiredArgsConstructor
public class ListDatasourcesUseCase {

    private final DatasourceRepository datasourceRepository;


    public List<DatasourceSummary> execute() {
        return datasourceRepository.findAllSummaries();
    }
}
