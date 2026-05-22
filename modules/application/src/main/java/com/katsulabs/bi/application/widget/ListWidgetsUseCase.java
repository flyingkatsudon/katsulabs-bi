package com.katsulabs.bi.application.widget;

import java.util.List;

import com.katsulabs.bi.domain.widget.WidgetRepository;
import com.katsulabs.bi.domain.widget.WidgetSummary;

public class ListWidgetsUseCase {

    private final WidgetRepository widgetRepository;

    public ListWidgetsUseCase(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public List<WidgetSummary> execute() {
        return widgetRepository.findAllSummaries();
    }
}
