package com.katsulabs.insightboard.application.category;

import java.util.List;

import com.katsulabs.insightboard.domain.category.CategoryRepository;
import com.katsulabs.insightboard.domain.category.CategorySummary;

public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public ListCategoriesUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategorySummary> execute() {
        return categoryRepository.findAll();
    }
}
