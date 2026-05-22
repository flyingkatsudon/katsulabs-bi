package com.katsulabs.bi.application.board;

import java.util.List;

import com.katsulabs.bi.application.auth.KatsulabsBiRole;
import com.katsulabs.bi.domain.board.BoardRepository;
import com.katsulabs.bi.domain.board.BoardSummary;

public class ListBoardsUseCase {

    private final BoardRepository boardRepository;

    public ListBoardsUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardSummary> execute(KatsulabsBiRole role) {
        boolean publishedOnly = role == KatsulabsBiRole.VIEWER;
        return boardRepository.findAllSummaries(publishedOnly);
    }
}
