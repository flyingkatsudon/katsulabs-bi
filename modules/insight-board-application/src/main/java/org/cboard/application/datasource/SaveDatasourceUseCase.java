package org.cboard.application.datasource;

import org.cboard.application.common.ServiceResult;
import org.cboard.domain.datasource.DatasourceRepository;

public class SaveDatasourceUseCase {

    private final DatasourceRepository datasourceRepository;

    public SaveDatasourceUseCase(DatasourceRepository datasourceRepository) {
        this.datasourceRepository = datasourceRepository;
    }

    public ServiceResult execute(String userId, DatasourceWriteCommand command) {
        if (datasourceRepository.existsByName(userId, command.name(), null)) {
            return ServiceResult.fail("Duplicated Name!");
        }
        long id = datasourceRepository.insert(userId, command.name(), command.type(), command.configJson());
        return ServiceResult.success("success", id);
    }
}
