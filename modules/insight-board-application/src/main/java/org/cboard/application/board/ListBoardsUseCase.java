package org.cboard.application.board;

import java.util.List;

import org.cboard.domain.board.BoardRepository;
import org.cboard.domain.board.BoardSummary;

public class ListBoardsUseCase {

    private final BoardRepository boardRepository;

    public ListBoardsUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardSummary> execute() {
        return boardRepository.findAllSummaries();
    }
}
