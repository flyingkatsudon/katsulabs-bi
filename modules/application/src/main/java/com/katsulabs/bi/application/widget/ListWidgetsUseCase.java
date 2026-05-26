package com.katsulabs.bi.application.widget;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.widget.WidgetRepository;
import com.katsulabs.bi.domain.widget.WidgetSummary;

@RequiredArgsConstructor
public class ListWidgetsUseCase {

    private final WidgetRepository widgetRepository;


    public List<WidgetSummary> execute() {
        return widgetRepository.findAllSummaries();
    }
}
