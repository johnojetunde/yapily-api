package com.yapily.marvel.app.controller;

import com.yapily.marvel.domain.model.MarvelCharacter;
import com.yapily.marvel.domain.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/characters", produces = APPLICATION_JSON_VALUE)
public class CharacterController {

    private final CharacterService marvelCharacterService;

    @GetMapping
    public Set<Long> getCharacterIds() {
        return marvelCharacterService.getCharacterIds();
    }

    @GetMapping("/{characterId}")
    public MarvelCharacter getCharacter(@PathVariable("characterId") Long characterId) {
        return marvelCharacterService.getCharacter(characterId);
    }
}
