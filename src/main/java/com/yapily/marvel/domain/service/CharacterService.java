package com.yapily.marvel.domain.service;

import com.yapily.marvel.domain.model.MarvelCharacter;

import java.util.Set;

public interface CharacterService {
    Set<Long> getCharacterIds();

    MarvelCharacter getCharacter(Long characterId);
}
