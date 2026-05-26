package com.katsulabs.bi.application.widget;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.widget.WidgetRepository;

@RequiredArgsConstructor
public class SaveWidgetUseCase {

    private final WidgetRepository widgetRepository;


    public ServiceResult execute(String userId, WidgetWriteCommand command) {
        if (widgetRepository.existsByName(userId, command.name(), command.categoryName(), null)) {
            return ServiceResult.fail("Duplicated name");
        }
        long id = widgetRepository.insert(userId, command.name(), command.categoryName(), command.dataJson());
        return ServiceResult.success("success", id);
    }
}
