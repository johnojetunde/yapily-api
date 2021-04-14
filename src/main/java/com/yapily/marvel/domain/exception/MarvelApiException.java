package com.yapily.marvel.domain.exception;

public class MarvelApiException extends RuntimeException {
    public MarvelApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarvelApiException(String message) {
        super(message);
    }

    public MarvelApiException(Throwable cause) {
        super(cause);
    }
}
