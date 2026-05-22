package com.katsulabs.insightboard.application.board;

import com.katsulabs.insightboard.application.auth.InsightBoardRole;
import com.katsulabs.insightboard.application.common.AccessDeniedException;
import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.board.BoardRepository;

public class SaveBoardUseCase {

    private final BoardRepository boardRepository;

    public SaveBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public ServiceResult execute(InsightBoardRole role, String userId, BoardWriteCommand command) {
        boolean publish = command.publishedToViewers();
        if (publish && !role.canPublishBoardToViewers()) {
            throw new AccessDeniedException("Viewer 게시 권한이 없습니다.");
        }
        if (boardRepository.existsByName(userId, command.name(), null)) {
            return ServiceResult.fail("Duplicated name");
        }
        long id = boardRepository.insert(
                userId,
                command.name(),
                command.categoryId(),
                command.layoutJson(),
                publish,
                publish ? userId : null);
        return ServiceResult.success("success", id);
    }
}
