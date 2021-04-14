package com.yapily.marvel.app.config;

import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import com.yapily.marvel.domain.model.Response;
import com.yapily.marvel.domain.util.DataFixtures;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;


public class MockApiClient implements MarvelApiClient {
    @Override
    public CompletableFuture<Response> getCharacters(Map<String, Object> queryMap) {
        return completedFuture(DataFixtures.defaultResponse());
    }

    @Override
    public CompletableFuture<Response> getCharacter(Long characterId, Map<String, Object> queryMap) {
        return completedFuture(DataFixtures.defaultResponse(characterId));
    }
}
