package com.katsulabs.insightboard.application.aggregate;

public class QueryAggregateUseCase {

    private final AggregateQueryPort aggregateQueryPort;

    public QueryAggregateUseCase(AggregateQueryPort aggregateQueryPort) {
        this.aggregateQueryPort = aggregateQueryPort;
    }

    public AggregateResultDto execute(AggregateQueryCommand command) {
        return aggregateQueryPort.query(command);
    }
}
