package com.katsulabs.bi.application.category;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.category.CategoryRepository;
import com.katsulabs.bi.domain.category.CategorySummary;

@RequiredArgsConstructor
public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;


    public List<CategorySummary> execute() {
        return categoryRepository.findAll();
    }
}
