package com.katsulabs.insightboard.adapter.web.datasource;

import java.util.List;

import com.katsulabs.insightboard.application.common.AccessControl;
import com.katsulabs.insightboard.application.common.CurrentUserProvider;
import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.application.datasource.DatasourceWriteCommand;
import com.katsulabs.insightboard.application.datasource.DeleteDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.GetDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.ListDatasourcesUseCase;
import com.katsulabs.insightboard.application.datasource.SaveDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.TestDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.UpdateDatasourceUseCase;
import com.katsulabs.insightboard.domain.datasource.DatasourceDetail;
import com.katsulabs.insightboard.domain.datasource.DatasourceSummary;
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
    private final AccessControl accessControl;

    public DatasourceController(
            ListDatasourcesUseCase listDatasourcesUseCase,
            GetDatasourceUseCase getDatasourceUseCase,
            SaveDatasourceUseCase saveDatasourceUseCase,
            UpdateDatasourceUseCase updateDatasourceUseCase,
            DeleteDatasourceUseCase deleteDatasourceUseCase,
            TestDatasourceUseCase testDatasourceUseCase,
            CurrentUserProvider currentUserProvider,
            AccessControl accessControl) {
        this.listDatasourcesUseCase = listDatasourcesUseCase;
        this.getDatasourceUseCase = getDatasourceUseCase;
        this.saveDatasourceUseCase = saveDatasourceUseCase;
        this.updateDatasourceUseCase = updateDatasourceUseCase;
        this.deleteDatasourceUseCase = deleteDatasourceUseCase;
        this.testDatasourceUseCase = testDatasourceUseCase;
        this.currentUserProvider = currentUserProvider;
        this.accessControl = accessControl;
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
        accessControl.requireWriteDatasourceOrDataset();
        return saveDatasourceUseCase.execute(
                currentUserProvider.requireUserId(),
                new DatasourceWriteCommand(request.name(), request.type(), request.configJson()));
    }

    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable long id, @RequestBody DatasourceWriteRequest request) {
        accessControl.requireWriteDatasourceOrDataset();
        return updateDatasourceUseCase.execute(
                currentUserProvider.requireUserId(),
                id,
                new DatasourceWriteCommand(request.name(), request.type(), request.configJson()));
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable long id) {
        accessControl.requireWriteDatasourceOrDataset();
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
