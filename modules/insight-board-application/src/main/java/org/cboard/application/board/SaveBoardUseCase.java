package org.cboard.application.board;

import org.cboard.application.common.ServiceResult;
import org.cboard.domain.board.BoardRepository;

public class SaveBoardUseCase {

    private final BoardRepository boardRepository;

    public SaveBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public ServiceResult execute(String userId, BoardWriteCommand command) {
        if (boardRepository.existsByName(userId, command.name(), null)) {
            return ServiceResult.fail("Duplicated name");
        }
        long id = boardRepository.insert(userId, command.name(), command.categoryId(), command.layoutJson());
        return ServiceResult.success("success", id);
    }
}
