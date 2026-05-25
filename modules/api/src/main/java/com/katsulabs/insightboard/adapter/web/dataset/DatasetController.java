package com.katsulabs.insightboard.adapter.web.dataset;

import java.util.List;

import com.katsulabs.insightboard.application.common.AccessControl;
import com.katsulabs.insightboard.application.common.CurrentUserProvider;
import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.application.dataset.DatasetWriteCommand;
import com.katsulabs.insightboard.application.dataset.DeleteDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.GetDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.ListDatasetsUseCase;
import com.katsulabs.insightboard.application.dataset.PreviewDatasetSqlUseCase;
import com.katsulabs.insightboard.application.dataset.PreviewDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.SaveDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.UpdateDatasetUseCase;
import com.katsulabs.insightboard.domain.dataset.DatasetDetail;
import com.katsulabs.insightboard.domain.dataset.DatasetSummary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/datasets", produces = MediaType.APPLICATION_JSON_VALUE)
public class DatasetController {

    private final ListDatasetsUseCase listDatasetsUseCase;
    private final GetDatasetUseCase getDatasetUseCase;
    private final SaveDatasetUseCase saveDatasetUseCase;
    private final UpdateDatasetUseCase updateDatasetUseCase;
    private final DeleteDatasetUseCase deleteDatasetUseCase;
    private final PreviewDatasetUseCase previewDatasetUseCase;
    private final PreviewDatasetSqlUseCase previewDatasetSqlUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessControl accessControl;

    public DatasetController(
            ListDatasetsUseCase listDatasetsUseCase,
            GetDatasetUseCase getDatasetUseCase,
            SaveDatasetUseCase saveDatasetUseCase,
            UpdateDatasetUseCase updateDatasetUseCase,
            DeleteDatasetUseCase deleteDatasetUseCase,
            PreviewDatasetUseCase previewDatasetUseCase,
            PreviewDatasetSqlUseCase previewDatasetSqlUseCase,
            CurrentUserProvider currentUserProvider,
            AccessControl accessControl) {
        this.listDatasetsUseCase = listDatasetsUseCase;
        this.getDatasetUseCase = getDatasetUseCase;
        this.saveDatasetUseCase = saveDatasetUseCase;
        this.updateDatasetUseCase = updateDatasetUseCase;
        this.deleteDatasetUseCase = deleteDatasetUseCase;
        this.previewDatasetUseCase = previewDatasetUseCase;
        this.previewDatasetSqlUseCase = previewDatasetSqlUseCase;
        this.currentUserProvider = currentUserProvider;
        this.accessControl = accessControl;
    }

    @GetMapping
    public List<DatasetResponse> list() {
        return listDatasetsUseCase.execute().stream().map(DatasetController::toSummaryResponse).toList();
    }

    @GetMapping("/{id}")
    public DatasetResponse get(@PathVariable long id) {
        return toDetailResponse(getDatasetUseCase.execute(id));
    }

    @PostMapping
    public ServiceResult create(@RequestBody DatasetWriteRequest request) {
        accessControl.requireWriteDatasourceOrDataset();
        return saveDatasetUseCase.execute(
                currentUserProvider.requireUserId(),
                new DatasetWriteCommand(request.name(), request.categoryName(), request.dataJson()));
    }

    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable long id, @RequestBody DatasetWriteRequest request) {
        accessControl.requireWriteDatasourceOrDataset();
        return updateDatasetUseCase.execute(
                currentUserProvider.requireUserId(),
                id,
                new DatasetWriteCommand(request.name(), request.categoryName(), request.dataJson()));
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable long id) {
        accessControl.requireWriteDatasourceOrDataset();
        return deleteDatasetUseCase.execute(id);
    }

    @GetMapping("/{id}/preview")
    public DatasetPreviewResponse preview(
            @PathVariable long id, @RequestParam(defaultValue = "50") int limit) {
        var result = previewDatasetUseCase.execute(id, limit);
        return new DatasetPreviewResponse(result.columns(), result.rows());
    }

    /** Load Data — 저장 전 SQL 컬럼·샘플 행 조회 */
    @PostMapping("/preview-query")
    public DatasetPreviewResponse previewQuery(@Valid @RequestBody DatasetSqlPreviewRequest request) {
        int limit = request.limit() != null ? request.limit() : 50;
        var result = previewDatasetSqlUseCase.execute(request.datasourceId(), request.sql(), limit);
        return new DatasetPreviewResponse(result.columns(), result.rows());
    }

    private static DatasetResponse toSummaryResponse(DatasetSummary summary) {
        return new DatasetResponse(
                summary.id(),
                summary.name(),
                summary.userId(),
                summary.userName(),
                summary.categoryName(),
                summary.datasourceId(),
                null,
                summary.createdAt(),
                summary.updatedAt());
    }

    private static DatasetResponse toDetailResponse(DatasetDetail detail) {
        Long datasourceId = null;
        if (detail.dataJson() != null) {
            try {
                var root = com.katsulabs.insightboard.application.support.JsonMapper.mapper().readTree(detail.dataJson());
                long ds = root.path("datasource").asLong(0);
                if (ds > 0) {
                    datasourceId = ds;
                }
            } catch (Exception ignored) {
                /* optional */
            }
        }
        return new DatasetResponse(
                detail.id(),
                detail.name(),
                detail.userId(),
                detail.userName(),
                detail.categoryName(),
                datasourceId,
                detail.dataJson(),
                detail.createdAt(),
                detail.updatedAt());
    }
}
