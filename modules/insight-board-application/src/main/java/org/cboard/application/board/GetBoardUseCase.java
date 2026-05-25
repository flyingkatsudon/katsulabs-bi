package org.cboard.application.board;

import org.cboard.domain.board.BoardDetail;
import org.cboard.domain.board.BoardRepository;

public class GetBoardUseCase {

    private final BoardRepository boardRepository;

    public GetBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public BoardDetail execute(long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }
}
