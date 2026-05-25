package com.katsulabs.insightboard.application.board;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.board.BoardRepository;

public class UpdateBoardUseCase {

    private final BoardRepository boardRepository;

    public UpdateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public ServiceResult execute(String userId, long id, BoardWriteCommand command) {
        if (boardRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("대시보드를 찾을 수 없습니다.");
        }
        if (boardRepository.existsByName(userId, command.name(), id)) {
            return ServiceResult.fail("Duplicated name");
        }
        boardRepository.update(id, command.name(), command.categoryId(), command.layoutJson());
        return ServiceResult.success("success");
    }
}
