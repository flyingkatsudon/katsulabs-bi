package com.katsulabs.bi.adapter.web.domains.category;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.application.domains.category.CategoryWriteCommand;
import com.katsulabs.bi.application.domains.category.DeleteCategoryUseCase;
import com.katsulabs.bi.application.domains.category.ListCategoriesUseCase;
import com.katsulabs.bi.application.domains.category.SaveCategoryUseCase;
import com.katsulabs.bi.application.domains.category.UpdateCategoryUseCase;
import com.katsulabs.bi.application.common.AccessControl;
import com.katsulabs.bi.application.common.CurrentUserProvider;
import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.domains.category.CategorySummary;
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
@RequestMapping(path = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;
    private final SaveCategoryUseCase saveCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessControl accessControl;


    @GetMapping
    public List<CategoryResponse> list() {
        return listCategoriesUseCase.execute().stream().map(CategoryController::toResponse).toList();
    }

    @PostMapping
    public ServiceResult create(@RequestBody CategoryWriteRequest request) {
        accessControl.requireWriteDashboardContent();
        return saveCategoryUseCase.execute(
                currentUserProvider.requireUserId(), new CategoryWriteCommand(request.name()));
    }

    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable long id, @RequestBody CategoryWriteRequest request) {
        accessControl.requireWriteDashboardContent();
        return updateCategoryUseCase.execute(
                currentUserProvider.requireUserId(), id, new CategoryWriteCommand(request.name()));
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable long id) {
        accessControl.requireWriteDashboardContent();
        return deleteCategoryUseCase.execute(id);
    }

    private static CategoryResponse toResponse(CategorySummary summary) {
        return new CategoryResponse(summary.id(), summary.name(), summary.userId());
    }
}
