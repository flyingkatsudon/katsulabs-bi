package com.katsulabs.bi.adapter.web;

import java.util.Map;

import com.katsulabs.bi.application.auth.LoginException;
import com.katsulabs.bi.application.common.AccessDeniedException;
import com.katsulabs.bi.application.board.BoardNotFoundException;
import com.katsulabs.bi.application.datasource.DatasourceNotFoundException;
import com.katsulabs.bi.application.widget.WidgetNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Map<String, String>> loginFailed(LoginException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> forbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler({BoardNotFoundException.class, DatasourceNotFoundException.class, WidgetNotFoundException.class})
    public ResponseEntity<Map<String, String>> notFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        String message = ex.getMessage() == null || ex.getMessage().isBlank()
                ? "요청을 처리할 수 없습니다."
                : ex.getMessage();
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage() == null ? "요청 값이 올바르지 않습니다." : err.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
