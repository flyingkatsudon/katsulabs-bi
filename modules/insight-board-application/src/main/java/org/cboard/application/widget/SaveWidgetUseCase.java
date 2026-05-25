package org.cboard.application.widget;

import org.cboard.application.common.ServiceResult;
import org.cboard.domain.widget.WidgetRepository;

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
