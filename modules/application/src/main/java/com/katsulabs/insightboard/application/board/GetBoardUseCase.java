package com.katsulabs.insightboard.application.board;

import com.katsulabs.insightboard.application.auth.InsightBoardRole;
import com.katsulabs.insightboard.domain.board.BoardDetail;
import com.katsulabs.insightboard.domain.board.BoardRepository;

public class GetBoardUseCase {

    private final BoardRepository boardRepository;

    public GetBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public BoardDetail execute(InsightBoardRole role, long boardId) {
        BoardDetail detail = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
        if (role == InsightBoardRole.VIEWER && !detail.publishedToViewers()) {
            throw new BoardNotFoundException(boardId);
        }
        return detail;
    }
}
