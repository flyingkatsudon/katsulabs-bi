package org.cboard.application.widget;

import org.cboard.application.common.ServiceResult;
import org.cboard.domain.widget.WidgetRepository;

public class UpdateWidgetUseCase {

    private final WidgetRepository widgetRepository;

    public UpdateWidgetUseCase(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public ServiceResult execute(String userId, long id, WidgetWriteCommand command) {
        if (widgetRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("위젯을 찾을 수 없습니다.");
        }
        if (widgetRepository.existsByName(userId, command.name(), command.categoryName(), id)) {
            return ServiceResult.fail("Duplicated name");
        }
        widgetRepository.update(id, command.name(), command.categoryName(), command.dataJson());
        return ServiceResult.success("success");
    }
}
