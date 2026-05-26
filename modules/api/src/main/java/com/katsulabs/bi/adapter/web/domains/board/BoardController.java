package com.katsulabs.bi.adapter.web.domains.board;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.application.domains.board.BoardWriteCommand;
import com.katsulabs.bi.application.domains.board.DeleteBoardUseCase;
import com.katsulabs.bi.application.domains.board.GetBoardUseCase;
import com.katsulabs.bi.application.domains.board.ListBoardsUseCase;
import com.katsulabs.bi.application.domains.board.SaveBoardUseCase;
import com.katsulabs.bi.application.domains.board.UpdateBoardUseCase;
import com.katsulabs.bi.application.common.AccessControl;
import com.katsulabs.bi.application.common.CurrentUserProvider;
import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.domains.board.BoardDetail;
import com.katsulabs.bi.domain.domains.board.BoardSummary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/boards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BoardController {

    private final ListBoardsUseCase listBoardsUseCase;
    private final GetBoardUseCase getBoardUseCase;
    private final SaveBoardUseCase saveBoardUseCase;
    private final UpdateBoardUseCase updateBoardUseCase;
    private final DeleteBoardUseCase deleteBoardUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessControl accessControl;


    @GetMapping
    public List<BoardResponse> list() {
        return listBoardsUseCase.execute(accessControl.requireRole()).stream()
                .map(BoardController::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BoardResponse get(@PathVariable long id) {
        return toResponse(getBoardUseCase.execute(accessControl.requireRole(), id));
    }

    @PostMapping
    public ServiceResult create(@RequestBody BoardWriteRequest request) {
        accessControl.requireWriteDashboardContent();
        return saveBoardUseCase.execute(
                accessControl.requireRole(),
                currentUserProvider.requireUserId(),
                toCommand(request));
    }

    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable long id, @RequestBody BoardWriteRequest request) {
        accessControl.requireWriteDashboardContent();
        return updateBoardUseCase.execute(
                accessControl.requireRole(),
                currentUserProvider.requireUserId(),
                id,
                toCommand(request));
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable long id) {
        accessControl.requireWriteDashboardContent();
        return deleteBoardUseCase.execute(id);
    }

    private static BoardWriteCommand toCommand(BoardWriteRequest request) {
        return new BoardWriteCommand(
                request.name(),
                request.categoryId(),
                request.layoutJson(),
                Boolean.TRUE.equals(request.publishedToViewers()));
    }

    private static BoardResponse toResponse(BoardSummary summary) {
        return new BoardResponse(
                summary.id(),
                summary.name(),
                summary.userId(),
                summary.userName(),
                summary.categoryId(),
                summary.categoryName(),
                null,
                summary.publishedToViewers(),
                summary.createdAt(),
                summary.updatedAt());
    }

    private static BoardResponse toResponse(BoardDetail detail) {
        return new BoardResponse(
                detail.id(),
                detail.name(),
                detail.userId(),
                detail.userName(),
                detail.categoryId(),
                null,
                detail.layoutJson(),
                detail.publishedToViewers(),
                detail.createdAt(),
                detail.updatedAt());
    }
}
