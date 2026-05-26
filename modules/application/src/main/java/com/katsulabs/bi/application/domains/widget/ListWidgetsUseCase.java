package com.katsulabs.bi.application.domains.widget;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.domains.widget.WidgetRepository;
import com.katsulabs.bi.domain.domains.widget.WidgetSummary;

@RequiredArgsConstructor
public class ListWidgetsUseCase {

    private final WidgetRepository widgetRepository;


    public List<WidgetSummary> execute() {
        return widgetRepository.findAllSummaries();
    }
}
