package org.cboard.adapter.web.datasource;

import java.util.List;

import org.cboard.application.common.CurrentUserProvider;
import org.cboard.application.common.ServiceResult;
import org.cboard.application.datasource.DatasourceWriteCommand;
import org.cboard.application.datasource.DeleteDatasourceUseCase;
import org.cboard.application.datasource.GetDatasourceUseCase;
import org.cboard.application.datasource.ListDatasourcesUseCase;
import org.cboard.application.datasource.SaveDatasourceUseCase;
import org.cboard.application.datasource.TestDatasourceUseCase;
import org.cboard.application.datasource.UpdateDatasourceUseCase;
import org.cboard.domain.datasource.DatasourceDetail;
import org.cboard.domain.datasource.DatasourceSummary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/datasources", produces = MediaType.APPLICATION_JSON_VALUE)
public class DatasourceController {

    private final ListDatasourcesUseCase listDatasourcesUseCase;
    private final GetDatasourceUseCase getDatasourceUseCase;
    private final SaveDatasourceUseCase saveDatasourceUseCase;
    private final UpdateDatasourceUseCase updateDatasourceUseCase;
    private final DeleteDatasourceUseCase deleteDatasourceUseCase;
    private final TestDatasourceUseCase testDatasourceUseCase;
    private final CurrentUserProvider currentUserProvider;

    public DatasourceController(
            ListDatasourcesUseCase listDatasourcesUseCase,
            GetDatasourceUseCase getDatasourceUseCase,
            SaveDatasourceUseCase saveDatasourceUseCase,
            UpdateDatasourceUseCase updateDatasourceUseCase,
            DeleteDatasourceUseCase deleteDatasourceUseCase,
            TestDatasourceUseCase testDatasourceUseCase,
            CurrentUserProvider currentUserProvider) {
        this.listDatasourcesUseCase = listDatasourcesUseCase;
        this.getDatasourceUseCase = getDatasourceUseCase;
        this.saveDatasourceUseCase = saveDatasourceUseCase;
        this.updateDatasourceUseCase = updateDatasourceUseCase;
        this.deleteDatasourceUseCase = deleteDatasourceUseCase;
        this.testDatasourceUseCase = testDatasourceUseCase;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public List<DatasourceResponse> list() {
        return listDatasourcesUseCase.execute().stream().map(DatasourceController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public DatasourceResponse get(@PathVariable long id) {
        return toResponse(getDatasourceUseCase.execute(id));
    }

    @PostMapping
    public ServiceResult create(@RequestBody DatasourceWriteRequest request) {
        return saveDatasourceUseCase.execute(
                currentUserProvider.requireUserId(),
                new DatasourceWriteCommand(request.name(), request.type(), request.configJson()));
    }

    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable long id, @RequestBody DatasourceWriteRequest request) {
        return updateDatasourceUseCase.execute(
                currentUserProvider.requireUserId(),
                id,
                new DatasourceWriteCommand(request.name(), request.type(), request.configJson()));
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable long id) {
        return deleteDatasourceUseCase.execute(id);
    }

    @PostMapping("/test")
    public ServiceResult testConfig(@RequestBody DatasourceWriteRequest request) {
        return testDatasourceUseCase.testConfig(
                new DatasourceWriteCommand(request.name(), request.type(), request.configJson()));
    }

    @PostMapping("/{id}/test")
    public ServiceResult test(@PathVariable long id) {
        return testDatasourceUseCase.execute(id);
    }

    private static DatasourceResponse toResponse(DatasourceSummary summary) {
        return new DatasourceResponse(
                summary.id(),
                summary.name(),
                summary.type(),
                summary.userId(),
                summary.userName(),
                null,
                summary.createdAt(),
                summary.updatedAt());
    }

    private static DatasourceResponse toResponse(DatasourceDetail detail) {
        return new DatasourceResponse(
                detail.id(),
                detail.name(),
                detail.type(),
                detail.userId(),
                detail.userName(),
                detail.configJson(),
                detail.createdAt(),
                detail.updatedAt());
    }
}
