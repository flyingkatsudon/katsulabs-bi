package com.insightboard.application.board;

import com.insightboard.api.board.BoardRequest;
import com.insightboard.api.board.BoardResponse;
import com.insightboard.domain.metadata.DashboardBoard;
import com.insightboard.infrastructure.persistence.DashboardBoardRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BoardService {

    private final DashboardBoardRepository boardRepository;

    public BoardService(DashboardBoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardResponse> listByUser(String userId) {
        return boardRepository.findByUserIdOrderByBoardIdDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public BoardResponse create(String userId, BoardRequest request) {
        DashboardBoard board = new DashboardBoard();
        board.setUserId(userId);
        board.setBoardName(request.boardName());
        board.setCategoryId(request.categoryId());
        board.setLayoutJson(request.layoutJson());
        board.setCreateTime(Instant.now());
        board.setUpdateTime(Instant.now());
        return toResponse(boardRepository.save(board));
    }

    public BoardResponse get(Long id) {
        return boardRepository
                .findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
    }

    public void delete(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }
        boardRepository.deleteById(id);
    }

    private BoardResponse toResponse(DashboardBoard board) {
        return new BoardResponse(
                board.getBoardId(),
                board.getUserId(),
                board.getCategoryId(),
                board.getBoardName(),
                board.getLayoutJson());
    }
}
