package com.yapily.marvel.domain.util;

import com.yapily.marvel.domain.model.MarvelCharacter;
import com.yapily.marvel.domain.model.MarvelCharacter.Thumbnail;
import com.yapily.marvel.domain.model.Response;
import com.yapily.marvel.domain.model.Response.PageResult;

import java.util.List;

public class DataFixtures {
    private DataFixtures() {
    }

    public static Response defaultResponse() {
        return defaultResponse(1L);
    }

    public static Response defaultResponse(Long characterId) {
        return new Response()
                .setData(pageResult(characterId));
    }

    public static PageResult pageResult(Long characterId) {
        return new PageResult()
                .setTotal(1L)
                .setLimit(1L)
                .setResults(List.of(marvelCharacter(characterId)));
    }

    public static MarvelCharacter marvelCharacter(Long id) {
        return new MarvelCharacter(id, "Jigsaw", "Jigsaw", new Thumbnail("path", "jpg"));
    }
}
