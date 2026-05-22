package com.katsulabs.bi.application.board;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.board.BoardRepository;

public class DeleteBoardUseCase {

    private final BoardRepository boardRepository;

    public DeleteBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public ServiceResult execute(long id) {
        if (boardRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("대시보드를 찾을 수 없습니다.");
        }
        boardRepository.delete(id);
        return ServiceResult.success("success");
    }
}
