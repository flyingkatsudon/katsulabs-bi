package com.katsulabs.bi.application.aggregate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryAggregateUseCase {

    private final AggregateQueryPort aggregateQueryPort;


    public AggregateResultDto execute(AggregateQueryCommand command) {
        return aggregateQueryPort.query(command);
    }
}
