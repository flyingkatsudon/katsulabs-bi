package com.katsulabs.insightboard.application.board;

import java.util.List;

import com.katsulabs.insightboard.application.auth.InsightBoardRole;
import com.katsulabs.insightboard.domain.board.BoardRepository;
import com.katsulabs.insightboard.domain.board.BoardSummary;

public class ListBoardsUseCase {

    private final BoardRepository boardRepository;

    public ListBoardsUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardSummary> execute(InsightBoardRole role) {
        boolean publishedOnly = role == InsightBoardRole.VIEWER;
        return boardRepository.findAllSummaries(publishedOnly);
    }
}
