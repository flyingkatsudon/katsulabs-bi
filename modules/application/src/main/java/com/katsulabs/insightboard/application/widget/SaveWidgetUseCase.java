package com.katsulabs.insightboard.application.widget;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.widget.WidgetRepository;

public class SaveWidgetUseCase {

    private final WidgetRepository widgetRepository;

    public SaveWidgetUseCase(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public ServiceResult execute(String userId, WidgetWriteCommand command) {
        if (widgetRepository.existsByName(userId, command.name(), command.categoryName(), null)) {
            return ServiceResult.fail("Duplicated name");
        }
        long id = widgetRepository.insert(userId, command.name(), command.categoryName(), command.dataJson());
        return ServiceResult.success("success", id);
    }
}
