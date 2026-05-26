package com.katsulabs.bi.application.board;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.auth.KatsulabsBiRole;
import com.katsulabs.bi.domain.board.BoardDetail;
import com.katsulabs.bi.domain.board.BoardRepository;

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
