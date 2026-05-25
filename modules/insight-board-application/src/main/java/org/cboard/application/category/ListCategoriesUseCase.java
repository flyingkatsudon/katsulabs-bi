package org.cboard.application.category;

import java.util.List;

import org.cboard.domain.category.CategoryRepository;
import org.cboard.domain.category.CategorySummary;

public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public ListCategoriesUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategorySummary> execute() {
        return categoryRepository.findAll();
    }
}
