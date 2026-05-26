package com.katsulabs.bi.application.domains.aggregate;

public interface AggregateQueryPort {

    AggregateResultDto query(AggregateQueryCommand command);

    String viewQuery(AggregateQueryCommand command);
}
