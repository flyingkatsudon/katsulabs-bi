package com.insightboard.api.presentation.board;

import com.insightboard.api.application.board.BoardService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public List<BoardResponse> list(@AuthenticationPrincipal String userId) {
        return boardService.listByUser(userId);
    }

    @GetMapping("/{id}")
    public BoardResponse get(@PathVariable Long id) {
        return boardService.get(id);
    }

    @PostMapping
    public BoardResponse create(@AuthenticationPrincipal String userId, @Valid @RequestBody BoardRequest request) {
        return boardService.create(userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boardService.delete(id);
    }
}
