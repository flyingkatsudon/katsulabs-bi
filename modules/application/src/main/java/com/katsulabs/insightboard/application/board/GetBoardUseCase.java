package com.katsulabs.insightboard.application.board;

import com.katsulabs.insightboard.domain.board.BoardDetail;
import com.katsulabs.insightboard.domain.board.BoardRepository;

public class GetBoardUseCase {

    private final BoardRepository boardRepository;

    public GetBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public BoardDetail execute(long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }
}
