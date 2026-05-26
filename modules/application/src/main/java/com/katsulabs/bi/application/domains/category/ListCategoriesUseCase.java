package com.katsulabs.bi.application.domains.category;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.domains.category.CategoryRepository;
import com.katsulabs.bi.domain.domains.category.CategorySummary;

@RequiredArgsConstructor
public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;


    public List<CategorySummary> execute() {
        return categoryRepository.findAll();
    }
}
