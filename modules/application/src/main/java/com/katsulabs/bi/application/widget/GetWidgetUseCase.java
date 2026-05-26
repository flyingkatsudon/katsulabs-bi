package com.katsulabs.bi.application.widget;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.domain.widget.WidgetDetail;
import com.katsulabs.bi.domain.widget.WidgetRepository;

@RequiredArgsConstructor
public class GetWidgetUseCase {

    private final WidgetRepository widgetRepository;


    public WidgetDetail execute(long id) {
        return widgetRepository.findById(id)
                .orElseThrow(() -> new WidgetNotFoundException(id));
    }
}
