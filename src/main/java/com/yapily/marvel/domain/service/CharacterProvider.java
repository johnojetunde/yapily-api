package com.yapily.marvel.domain.service;

import com.yapily.marvel.domain.exception.MarvelApiException;
import com.yapily.marvel.domain.model.MarvelCharacter;

import java.util.Optional;
import java.util.Set;

public interface CharacterProvider {
    Set<Long> getAllCharacterIds() throws MarvelApiException;

    Optional<MarvelCharacter> getCharacter(Long id) throws MarvelApiException;
}
