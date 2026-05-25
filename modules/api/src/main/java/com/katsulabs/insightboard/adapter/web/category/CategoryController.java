package com.katsulabs.insightboard.adapter.web.category;

import java.util.List;

import com.katsulabs.insightboard.application.category.CategoryWriteCommand;
import com.katsulabs.insightboard.application.category.DeleteCategoryUseCase;
import com.katsulabs.insightboard.application.category.ListCategoriesUseCase;
import com.katsulabs.insightboard.application.category.SaveCategoryUseCase;
import com.katsulabs.insightboard.application.category.UpdateCategoryUseCase;
import com.katsulabs.insightboard.application.common.AccessControl;
import com.katsulabs.insightboard.application.common.CurrentUserProvider;
import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.category.CategorySummary;
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
public class CategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;
    private final SaveCategoryUseCase saveCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessControl accessControl;

    public CategoryController(
            ListCategoriesUseCase listCategoriesUseCase,
            SaveCategoryUseCase saveCategoryUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase,
            CurrentUserProvider currentUserProvider,
            AccessControl accessControl) {
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.saveCategoryUseCase = saveCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.currentUserProvider = currentUserProvider;
        this.accessControl = accessControl;
    }

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
