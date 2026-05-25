package org.cboard.application.widget;

import java.util.List;

import org.cboard.domain.widget.WidgetRepository;
import org.cboard.domain.widget.WidgetSummary;

public class ListWidgetsUseCase {

    private final WidgetRepository widgetRepository;

    public ListWidgetsUseCase(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public List<WidgetSummary> execute() {
        return widgetRepository.findAllSummaries();
    }
}
