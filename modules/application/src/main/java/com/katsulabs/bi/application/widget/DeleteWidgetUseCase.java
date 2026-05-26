package com.katsulabs.bi.application.widget;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.widget.WidgetRepository;

@RequiredArgsConstructor
public class DeleteWidgetUseCase {

    private final WidgetRepository widgetRepository;


    public ServiceResult execute(long id) {
        if (widgetRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("위젯을 찾을 수 없습니다.");
        }
        widgetRepository.delete(id);
        return ServiceResult.success("success");
    }
}
