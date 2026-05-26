package com.katsulabs.bi.application.domains.board;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.domains.auth.KatsulabsBiRole;
import com.katsulabs.bi.domain.domains.board.BoardDetail;
import com.katsulabs.bi.domain.domains.board.BoardRepository;

@RequiredArgsConstructor
public class GetBoardUseCase {

    private final BoardRepository boardRepository;


    public BoardDetail execute(KatsulabsBiRole role, long boardId) {
        BoardDetail detail = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
        if (role == KatsulabsBiRole.VIEWER && !detail.publishedToViewers()) {
            throw new BoardNotFoundException(boardId);
        }
        return detail;
    }
}
