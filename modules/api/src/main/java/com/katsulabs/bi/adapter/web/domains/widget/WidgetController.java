package com.katsulabs.bi.adapter.web.domains.widget;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.application.common.AccessControl;
import com.katsulabs.bi.application.common.CurrentUserProvider;
import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.application.domains.widget.DeleteWidgetUseCase;
import com.katsulabs.bi.application.domains.widget.GetWidgetUseCase;
import com.katsulabs.bi.application.domains.widget.ListWidgetsUseCase;
import com.katsulabs.bi.application.domains.widget.SaveWidgetUseCase;
import com.katsulabs.bi.application.domains.widget.UpdateWidgetUseCase;
import com.katsulabs.bi.application.domains.widget.WidgetWriteCommand;
import com.katsulabs.bi.domain.domains.widget.WidgetDetail;
import com.katsulabs.bi.domain.domains.widget.WidgetSummary;
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
@RequestMapping(path = "/api/v1/widgets", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class WidgetController {

    private final ListWidgetsUseCase listWidgetsUseCase;
    private final GetWidgetUseCase getWidgetUseCase;
    private final SaveWidgetUseCase saveWidgetUseCase;
    private final UpdateWidgetUseCase updateWidgetUseCase;
    private final DeleteWidgetUseCase deleteWidgetUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessControl accessControl;


    @GetMapping
    public List<WidgetResponse> list() {
        return listWidgetsUseCase.execute().stream().map(WidgetController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public WidgetResponse get(@PathVariable long id) {
        return toResponse(getWidgetUseCase.execute(id));
    }

    @PostMapping
    public ServiceResult create(@RequestBody WidgetWriteRequest request) {
        accessControl.requireWriteDashboardContent();
        return saveWidgetUseCase.execute(
                currentUserProvider.requireUserId(),
                new WidgetWriteCommand(request.name(), request.categoryName(), request.dataJson()));
    }

    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable long id, @RequestBody WidgetWriteRequest request) {
        accessControl.requireWriteDashboardContent();
        return updateWidgetUseCase.execute(
                currentUserProvider.requireUserId(),
                id,
                new WidgetWriteCommand(request.name(), request.categoryName(), request.dataJson()));
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable long id) {
        accessControl.requireWriteDashboardContent();
        return deleteWidgetUseCase.execute(id);
    }

    private static WidgetResponse toResponse(WidgetSummary summary) {
        return new WidgetResponse(
                summary.id(),
                summary.name(),
                summary.userId(),
                summary.userName(),
                summary.categoryName(),
                null,
                summary.createdAt(),
                summary.updatedAt());
    }

    private static WidgetResponse toResponse(WidgetDetail detail) {
        return new WidgetResponse(
                detail.id(),
                detail.name(),
                detail.userId(),
                detail.userName(),
                detail.categoryName(),
                detail.dataJson(),
                detail.createdAt(),
                detail.updatedAt());
    }
}
