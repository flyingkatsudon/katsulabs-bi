package com.katsulabs.bi.application.board;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.auth.KatsulabsBiRole;
import com.katsulabs.bi.application.common.AccessDeniedException;
import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.board.BoardRepository;

@RequiredArgsConstructor
public class SaveBoardUseCase {

    private final BoardRepository boardRepository;


    public ServiceResult execute(KatsulabsBiRole role, String userId, BoardWriteCommand command) {
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
