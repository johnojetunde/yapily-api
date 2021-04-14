package com.yapily.marvel.app.config;

import com.yapily.marvel.domain.exception.MarvelNotFoundException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<?> handleCompletionException(Throwable ex) {
        log.error("Unhandled exception encountered: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new ResponseModel(ex.getMessage(), INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MarvelNotFoundException.class})
    public ResponseEntity<?> handleDomainException(MarvelNotFoundException ex) {
        log.error("Unhandled exception encountered: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new ResponseModel(ex.getMessage(), NOT_FOUND), NOT_FOUND);
    }

    @Value
    private static class ResponseModel {
        String error;
        HttpStatus status;
    }
}

