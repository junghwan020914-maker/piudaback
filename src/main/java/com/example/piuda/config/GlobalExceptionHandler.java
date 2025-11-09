package com.example.piuda.config;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.piuda.storage.StorageException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegal(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValid(MethodArgumentNotValidException e) {
        var fieldError = e.getBindingResult().getFieldError();
        String msg = (fieldError != null) ? fieldError.getDefaultMessage() : "요청 형식이 올바르지 않습니다.";
        return ResponseEntity.badRequest().body(Map.of("message", msg));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorage(StorageException e) {
        // S3 업로드 관련 오류를 명확하게 노출
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "message", "파일 업로드 실패",
                        "detail", e.getMessage()
                ));
    }
}
