package com.katsulabs.bi.application.aggregate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewAggregateQueryUseCase {

    private final AggregateQueryPort aggregateQueryPort;


    public String execute(AggregateQueryCommand command) {
        return aggregateQueryPort.viewQuery(command);
    }
}
