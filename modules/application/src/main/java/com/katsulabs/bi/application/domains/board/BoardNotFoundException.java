package com.katsulabs.bi.application.domains.board;

public class BoardNotFoundException extends RuntimeException {

    public BoardNotFoundException(long boardId) {
        super("대시보드를 찾을 수 없습니다: " + boardId);
    }
}
