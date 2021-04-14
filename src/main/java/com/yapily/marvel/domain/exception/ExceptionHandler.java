package com.yapily.marvel.domain.exception;

import org.springframework.http.HttpStatus;
import retrofit2.adapter.java8.HttpException;

import java.util.concurrent.CompletionException;

public class ExceptionHandler {
    private ExceptionHandler() {
    }

    public static RuntimeException handleCompletionException(Throwable ex) {
        return (ex instanceof CompletionException && ex.getCause() != null)
                ? handleException(ex.getCause())
                : handleException(ex);
    }

    public static RuntimeException handleException(Throwable ex) {
        return ex instanceof HttpException
                ? handleHttpException((HttpException) ex)
                : new MarvelApiException(ex);
    }

    public static RuntimeException handleHttpException(HttpException e) {
        return HttpStatus.NOT_FOUND.value() == e.code()
                ? new MarvelNotFoundException("Not found", e)
                : new MarvelApiException(e);
    }
}
