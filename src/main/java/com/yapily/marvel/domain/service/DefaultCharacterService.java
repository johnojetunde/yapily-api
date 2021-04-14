package com.yapily.marvel.domain.service;

import com.google.common.base.Supplier;
import com.yapily.marvel.app.config.CacheConfig;
import com.yapily.marvel.domain.exception.MarvelNotFoundException;
import com.yapily.marvel.domain.model.MarvelCharacter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class DefaultCharacterService implements CharacterService {

    private final CharacterProvider characterProvider;
    private final CacheConfig cacheConfig;
    private final Supplier<Set<Long>> cache;

    public DefaultCharacterService(CharacterProvider characterProvider,
                                   CacheConfig cacheConfig) {
        this.characterProvider = characterProvider;
        this.cacheConfig = cacheConfig;
        this.cache = cacheSetup();
    }

    @SuppressWarnings("Guava")
    private Supplier<Set<Long>> cacheSetup() {
        return memoizeWithExpiration(characterProvider::getAllCharacterIds, cacheConfig.getCharactersExpiryInMinutes(), MINUTES);
    }

    @Override
    public Set<Long> getCharacterIds() {
        return cache.get();
    }

    @Override
    public MarvelCharacter getCharacter(Long characterId) {
        return characterProvider.getCharacter(characterId)
                .orElseThrow(() -> new MarvelNotFoundException("Not found"));
    }

    @PostConstruct
    public void loadAllCharacterIds() {
        getCharacterIds();
    }
}
