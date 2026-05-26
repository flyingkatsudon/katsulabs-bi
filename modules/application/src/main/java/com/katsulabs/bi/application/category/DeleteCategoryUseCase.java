package com.katsulabs.bi.application.category;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.category.CategoryRepository;

@RequiredArgsConstructor
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;


    public ServiceResult execute(long id) {
        if (!categoryRepository.findById(id).isPresent()) {
            return ServiceResult.fail("Not found");
        }
        categoryRepository.delete(id);
        return ServiceResult.success("success", id);
    }
}
