package org.cboard.adapter.web.board;

import java.util.List;

import org.cboard.application.board.BoardWriteCommand;
import org.cboard.application.board.DeleteBoardUseCase;
import org.cboard.application.board.GetBoardUseCase;
import org.cboard.application.board.ListBoardsUseCase;
import org.cboard.application.board.SaveBoardUseCase;
import org.cboard.application.board.UpdateBoardUseCase;
import org.cboard.application.common.CurrentUserProvider;
import org.cboard.application.common.ServiceResult;
import org.cboard.domain.board.BoardDetail;
import org.cboard.domain.board.BoardSummary;
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
public class BoardController {

    private final ListBoardsUseCase listBoardsUseCase;
    private final GetBoardUseCase getBoardUseCase;
    private final SaveBoardUseCase saveBoardUseCase;
    private final UpdateBoardUseCase updateBoardUseCase;
    private final DeleteBoardUseCase deleteBoardUseCase;
    private final CurrentUserProvider currentUserProvider;

    public BoardController(
            ListBoardsUseCase listBoardsUseCase,
            GetBoardUseCase getBoardUseCase,
            SaveBoardUseCase saveBoardUseCase,
            UpdateBoardUseCase updateBoardUseCase,
            DeleteBoardUseCase deleteBoardUseCase,
            CurrentUserProvider currentUserProvider) {
        this.listBoardsUseCase = listBoardsUseCase;
        this.getBoardUseCase = getBoardUseCase;
        this.saveBoardUseCase = saveBoardUseCase;
        this.updateBoardUseCase = updateBoardUseCase;
        this.deleteBoardUseCase = deleteBoardUseCase;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public List<BoardResponse> list() {
        return listBoardsUseCase.execute().stream().map(BoardController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public BoardResponse get(@PathVariable long id) {
        return toResponse(getBoardUseCase.execute(id));
    }

    @PostMapping
    public ServiceResult create(@RequestBody BoardWriteRequest request) {
        return saveBoardUseCase.execute(
                currentUserProvider.requireUserId(),
                new BoardWriteCommand(request.name(), request.categoryId(), request.layoutJson()));
    }

    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable long id, @RequestBody BoardWriteRequest request) {
        return updateBoardUseCase.execute(
                currentUserProvider.requireUserId(),
                id,
                new BoardWriteCommand(request.name(), request.categoryId(), request.layoutJson()));
    }

    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable long id) {
        return deleteBoardUseCase.execute(id);
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
                detail.createdAt(),
                detail.updatedAt());
    }
}
