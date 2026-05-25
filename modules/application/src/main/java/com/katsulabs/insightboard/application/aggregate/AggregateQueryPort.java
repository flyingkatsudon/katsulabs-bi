package com.katsulabs.insightboard.application.aggregate;

public interface AggregateQueryPort {

    AggregateResultDto query(AggregateQueryCommand command);

    String viewQuery(AggregateQueryCommand command);
}
