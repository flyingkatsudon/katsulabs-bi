package com.katsulabs.bi.application.category;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.category.CategoryRepository;

public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ServiceResult execute(long id) {
        if (!categoryRepository.findById(id).isPresent()) {
            return ServiceResult.fail("Not found");
        }
        categoryRepository.delete(id);
        return ServiceResult.success("success", id);
    }
}
