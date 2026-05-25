package com.katsulabs.insightboard.application.widget;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.widget.WidgetRepository;

public class DeleteWidgetUseCase {

    private final WidgetRepository widgetRepository;

    public DeleteWidgetUseCase(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public ServiceResult execute(long id) {
        if (widgetRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("위젯을 찾을 수 없습니다.");
        }
        widgetRepository.delete(id);
        return ServiceResult.success("success");
    }
}
