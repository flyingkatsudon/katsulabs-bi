package com.katsulabs.bi.application.domains.category;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.domains.category.CategoryRepository;

@RequiredArgsConstructor
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;


    public ServiceResult execute(String userId, long id, CategoryWriteCommand command) {
        if (!categoryRepository.findById(id).isPresent()) {
            return ServiceResult.fail("Not found");
        }
        if (categoryRepository.existsByName(command.name(), id)) {
            return ServiceResult.fail("Duplicated name");
        }
        categoryRepository.update(id, userId, command.name());
        return ServiceResult.success("success", id);
    }
}
