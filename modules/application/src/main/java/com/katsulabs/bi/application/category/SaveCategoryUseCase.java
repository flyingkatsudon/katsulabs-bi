package com.katsulabs.bi.application.category;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.category.CategoryRepository;

@RequiredArgsConstructor
public class SaveCategoryUseCase {

    private final CategoryRepository categoryRepository;


    public ServiceResult execute(String userId, CategoryWriteCommand command) {
        if (categoryRepository.existsByName(command.name(), null)) {
            return ServiceResult.fail("Duplicated name");
        }
        long id = categoryRepository.insert(userId, command.name());
        return ServiceResult.success("success", id);
    }
}
