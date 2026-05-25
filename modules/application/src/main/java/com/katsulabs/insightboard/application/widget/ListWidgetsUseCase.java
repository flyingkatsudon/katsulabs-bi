package com.katsulabs.insightboard.application.widget;

import java.util.List;

import com.katsulabs.insightboard.domain.widget.WidgetRepository;
import com.katsulabs.insightboard.domain.widget.WidgetSummary;

public class ListWidgetsUseCase {

    private final WidgetRepository widgetRepository;

    public ListWidgetsUseCase(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public List<WidgetSummary> execute() {
        return widgetRepository.findAllSummaries();
    }
}
