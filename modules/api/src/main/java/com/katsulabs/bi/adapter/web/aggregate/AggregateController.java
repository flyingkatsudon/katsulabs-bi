package com.katsulabs.bi.adapter.web.aggregate;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.aggregate.AggregateQueryCommand;
import com.katsulabs.bi.application.aggregate.AggregateResultDto;
import com.katsulabs.bi.application.aggregate.QueryAggregateUseCase;
import com.katsulabs.bi.application.aggregate.ViewAggregateQueryUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/aggregate", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AggregateController {

    private final QueryAggregateUseCase queryAggregateUseCase;
    private final ViewAggregateQueryUseCase viewAggregateQueryUseCase;


    @PostMapping
    public AggregateResultDto query(@Valid @RequestBody AggregateRequest request) {
        return queryAggregateUseCase.execute(toCommand(request));
    }

    @PostMapping("/view-query")
    public AggregateViewQueryResponse viewQuery(@Valid @RequestBody AggregateRequest request) {
        String sql = viewAggregateQueryUseCase.execute(toCommand(request));
        return new AggregateViewQueryResponse(sql);
    }

    private static AggregateQueryCommand toCommand(AggregateRequest request) {
        return new AggregateQueryCommand(
                request.datasourceId(),
                request.datasetId(),
                request.query(),
                request.cfg(),
                request.reload() != null && request.reload());
    }
}
