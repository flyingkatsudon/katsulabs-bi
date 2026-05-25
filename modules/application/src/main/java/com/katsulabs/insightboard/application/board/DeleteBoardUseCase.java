package com.katsulabs.insightboard.application.board;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.board.BoardRepository;

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
