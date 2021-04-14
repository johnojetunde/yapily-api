package com.yapily.marvel.domain.marvelapi;

import com.yapily.marvel.domain.model.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface MarvelApiClient {
    @GET("characters")
    CompletableFuture<Response> getCharacters(@QueryMap Map<String, Object> queryMap);

    @GET("characters/{characterId}")
    CompletableFuture<Response> getCharacter(@Path("characterId") Long characterId, @QueryMap Map<String, Object> queryMap);
}
