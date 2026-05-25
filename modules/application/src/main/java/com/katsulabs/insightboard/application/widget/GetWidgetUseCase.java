package com.katsulabs.insightboard.application.widget;

import com.katsulabs.insightboard.domain.widget.WidgetDetail;
import com.katsulabs.insightboard.domain.widget.WidgetRepository;

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
