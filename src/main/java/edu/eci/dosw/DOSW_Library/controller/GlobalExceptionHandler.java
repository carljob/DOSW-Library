package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvaliableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({BookNotAvaliableException.class, LoanLimitExceededException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }
}

