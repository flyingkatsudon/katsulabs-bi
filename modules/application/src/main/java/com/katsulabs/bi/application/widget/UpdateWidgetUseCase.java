package com.katsulabs.bi.application.widget;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.widget.WidgetRepository;

@RequiredArgsConstructor
public class UpdateWidgetUseCase {

    private final WidgetRepository widgetRepository;


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
