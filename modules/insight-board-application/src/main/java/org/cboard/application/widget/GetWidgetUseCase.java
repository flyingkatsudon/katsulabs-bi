package org.cboard.application.widget;

import org.cboard.domain.widget.WidgetDetail;
import org.cboard.domain.widget.WidgetRepository;

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
