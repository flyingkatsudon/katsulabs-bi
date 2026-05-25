package org.cboard.adapter.web.category;

import java.util.List;

import org.cboard.application.category.ListCategoriesUseCase;
import org.cboard.domain.category.CategorySummary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(ListCategoriesUseCase listCategoriesUseCase) {
        this.listCategoriesUseCase = listCategoriesUseCase;
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return listCategoriesUseCase.execute().stream().map(CategoryController::toResponse).toList();
    }

    private static CategoryResponse toResponse(CategorySummary summary) {
        return new CategoryResponse(summary.id(), summary.name(), summary.userId());
    }
}
