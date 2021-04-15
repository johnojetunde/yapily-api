package com.yapily.marvel.domain.service;

import com.yapily.marvel.app.config.MarvelConfig;
import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import com.yapily.marvel.domain.model.MarvelCharacter;
import com.yapily.marvel.domain.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.yapily.marvel.domain.exception.ExceptionHandler.handleCompletionException;
import static com.yapily.marvel.domain.service.PaginatedRequestService.loadPagedRequest;
import static com.yapily.marvel.domain.util.StreamUtil.emptyIfNullStream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

@Slf4j
@Service
public class DefaultCharacterProvider implements CharacterProvider {

    private final MarvelConfig config;
    private final Clock clock;
    private final MarvelApiClient apiProvider;

    private static final Long INITIAL_OFFSET = 0L;
    private static final String TIME_STAMP_KEY = "ts";
    private static final String API_KEY = "apikey";
    private static final String HASH_KEY = "hash";
    private static final String LIMIT_KEY = "limit";
    private static final String OFFSET_KEY = "offset";

    public DefaultCharacterProvider(MarvelConfig config,
                                    Clock clock,
                                    MarvelApiClient apiProvider) {
        this.config = config;
        this.clock = clock;
        this.apiProvider = apiProvider;
    }

    @Override
    public Optional<MarvelCharacter> getCharacter(Long id) {
        try {
            log.info("--- fetching character details by id: {} ----", id);
            Map<String, Object> queryMap = buildQueryMap();
            return apiProvider.getCharacter(id, queryMap)
                    .thenApply(re -> ofNullable(
                            mapToCharacter(re.getData().getResults()).get(0))
                    ).join();
        } catch (Exception e) {
            throw handleCompletionException(e);
        }
    }

    @Override
    public Set<Long> getAllCharacterIds() {
        try {
            log.info("--- fetching all character ids from marvel --");
            Map<String, Object> queryMap = buildQueryMap();
            return loadPagedRequest(
                    apiProvider::getCharacters,
                    this::mapToCharacterIds,
                    this::loadCharacterIds,
                    queryMap
            ).join();
        } catch (Exception e) {
            throw handleCompletionException(e);
        }
    }

    List<MarvelCharacter> mapToCharacter(List<Object> characters) {
        var marvelCharacters = MapperUtil.convert(characters, MarvelCharacter.class);
        return emptyIfNullStream(marvelCharacters)
                .collect(toList());
    }

    private CompletableFuture<Collection<Long>> loadCharacterIds(Long offset,
                                                                 Long limit,
                                                                 Map<String, Object> queryMap) {
        queryMap.put(LIMIT_KEY, limit);
        queryMap.put(OFFSET_KEY, offset);
        return apiProvider.getCharacters(queryMap)
                .thenApply(re -> mapToCharacterIds(re.getData().getResults()));
    }

    private List<Long> mapToCharacterIds(List<Object> characters) {
        var marvelCharacters = mapToCharacter(characters);
        return emptyIfNullStream(marvelCharacters)
                .map(MarvelCharacter::getId)
                .collect(toList());
    }

    private Map<String, Object> buildQueryMap() {
        Long timestamp = clock.millis();
        String hash = buildHash(timestamp);

        var queryMap = new HashMap<String, Object>();
        queryMap.put(TIME_STAMP_KEY, timestamp);
        queryMap.put(HASH_KEY, hash);
        queryMap.put(LIMIT_KEY, config.getLimitPerPage());
        queryMap.put(OFFSET_KEY, INITIAL_OFFSET);
        queryMap.put(API_KEY, config.getPublicKey());

        return queryMap;
    }

    private String buildHash(Long timestamp) {
        String authDetails = timestamp + config.getPrivateKey() + config.getPublicKey();
        return md5DigestAsHex(authDetails.getBytes());
    }
}
