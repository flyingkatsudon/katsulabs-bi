package com.katsulabs.bi.application.datasource;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.datasource.DatasourceRepository;
import com.katsulabs.bi.domain.datasource.DatasourceSummary;

@RequiredArgsConstructor
public class ListDatasourcesUseCase {

    private final DatasourceRepository datasourceRepository;


    public List<DatasourceSummary> execute() {
        return datasourceRepository.findAllSummaries();
    }
}
