package com.katsulabs.bi.application.widget;

import com.katsulabs.bi.domain.widget.WidgetDetail;
import com.katsulabs.bi.domain.widget.WidgetRepository;

public class GetWidgetUseCase {

    private final WidgetRepository widgetRepository;

    public GetWidgetUseCase(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public WidgetDetail execute(long id) {
        return widgetRepository.findById(id)
                .orElseThrow(() -> new WidgetNotFoundException(id));
    }
}
