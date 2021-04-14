package com.yapily.marvel.domain.exception;

public class MarvelNotFoundException extends MarvelApiException {
    public MarvelNotFoundException(String message) {
        super(message);
    }

    public MarvelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
