package com.katsulabs.bi.application.board;

import com.katsulabs.bi.application.auth.KatsulabsBiRole;
import com.katsulabs.bi.application.common.AccessDeniedException;
import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.board.BoardRepository;

public class UpdateBoardUseCase {

    private final BoardRepository boardRepository;

    public UpdateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public ServiceResult execute(KatsulabsBiRole role, String userId, long id, BoardWriteCommand command) {
        var existing = boardRepository.findById(id);
        if (existing.isEmpty()) {
            return ServiceResult.fail("대시보드를 찾을 수 없습니다.");
        }
        boolean publish = command.publishedToViewers();
        if (publish && !role.canPublishBoardToViewers()) {
            throw new AccessDeniedException("Viewer 게시 권한이 없습니다.");
        }
        if (boardRepository.existsByName(userId, command.name(), id)) {
            return ServiceResult.fail("Duplicated name");
        }
        boardRepository.update(
                id,
                command.name(),
                command.categoryId(),
                command.layoutJson(),
                publish,
                publish ? userId : null);
        return ServiceResult.success("success");
    }
}
