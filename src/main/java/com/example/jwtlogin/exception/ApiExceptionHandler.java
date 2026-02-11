package com.example.jwtlogin.exception;

import com.example.jwtlogin.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ApiExceptionHandler extends RuntimeException {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<MessageResponse> unauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(401).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> generic(Exception ex) {
        return ResponseEntity.status(500).body(new MessageResponse("Erro interno"));
    }
}
