package com.katsulabs.bi.application.domains.widget;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.domain.domains.widget.WidgetDetail;
import com.katsulabs.bi.domain.domains.widget.WidgetRepository;

@RequiredArgsConstructor
public class GetWidgetUseCase {

    private final WidgetRepository widgetRepository;


    public WidgetDetail execute(long id) {
        return widgetRepository.findById(id)
                .orElseThrow(() -> new WidgetNotFoundException(id));
    }
}
