package com.katsulabs.insightboard.application.category;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.category.CategoryRepository;

public class SaveCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public SaveCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ServiceResult execute(String userId, CategoryWriteCommand command) {
        if (categoryRepository.existsByName(command.name(), null)) {
            return ServiceResult.fail("Duplicated name");
        }
        long id = categoryRepository.insert(userId, command.name());
        return ServiceResult.success("success", id);
    }
}
