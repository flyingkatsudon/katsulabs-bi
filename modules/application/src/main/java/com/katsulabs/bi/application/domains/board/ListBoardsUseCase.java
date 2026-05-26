package com.katsulabs.bi.application.domains.board;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.application.domains.auth.KatsulabsBiRole;
import com.katsulabs.bi.domain.domains.board.BoardRepository;
import com.katsulabs.bi.domain.domains.board.BoardSummary;

@RequiredArgsConstructor
public class ListBoardsUseCase {

    private final BoardRepository boardRepository;


    public List<BoardSummary> execute(KatsulabsBiRole role) {
        boolean publishedOnly = role == KatsulabsBiRole.VIEWER;
        return boardRepository.findAllSummaries(publishedOnly);
    }
}
