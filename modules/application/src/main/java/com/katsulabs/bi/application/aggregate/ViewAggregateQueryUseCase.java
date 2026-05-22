package com.katsulabs.bi.application.aggregate;

public class ViewAggregateQueryUseCase {

    private final AggregateQueryPort aggregateQueryPort;

    public ViewAggregateQueryUseCase(AggregateQueryPort aggregateQueryPort) {
        this.aggregateQueryPort = aggregateQueryPort;
    }

    public String execute(AggregateQueryCommand command) {
        return aggregateQueryPort.viewQuery(command);
    }
}
