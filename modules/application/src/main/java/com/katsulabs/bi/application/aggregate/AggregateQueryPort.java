package com.katsulabs.bi.application.aggregate;

public interface AggregateQueryPort {

    AggregateResultDto query(AggregateQueryCommand command);

    String viewQuery(AggregateQueryCommand command);
}
