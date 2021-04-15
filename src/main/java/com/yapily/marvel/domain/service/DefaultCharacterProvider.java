package com.yapily.marvel.domain.service;

import com.yapily.marvel.app.config.MarvelConfig;
import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import com.yapily.marvel.domain.model.MarvelCharacter;
import com.yapily.marvel.domain.util.MapperUtil;
import com.yapily.marvel.domain.util.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.yapily.marvel.domain.exception.ExceptionHandler.handleCompletionException;
import static com.yapily.marvel.domain.service.PaginatedRequestService.loadPagedRequest;
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
            Long timestamp = clock.millis();
            String hash = buildHash(timestamp);
            Map<String, Object> queryMap = buildQueryMap(timestamp, hash);

            return apiProvider.getCharacter(id, queryMap)
                    .thenApply(re -> ofNullable(mapToCharacter(re.getData().getResults()).get(0)))
                    .join();
        } catch (Exception e) {
            throw handleCompletionException(e);
        }
    }

    @Override
    public Set<Long> getAllCharacterIds() {
        try {
            log.info("--- fetching all character ids from marvel --");
            Long timestamp = clock.millis();
            String hash = buildHash(timestamp);
            Map<String, Object> queryMap = buildQueryMap(timestamp, hash);

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
        return StreamUtil.emptyIfNullStream(marvelCharacters)
                .collect(toList());
    }

    private CompletableFuture<Collection<Long>> loadCharacterIds(Long offset,
                                                                 Long limit,
                                                                 Map<String, Object> queryMap) {
        queryMap.put("limit", limit);
        queryMap.put("offset", offset);

        return apiProvider.getCharacters(queryMap)
                .thenApply(re -> mapToCharacterIds(re.getData().getResults()));
    }

    private List<Long> mapToCharacterIds(List<Object> characters) {
        var marvelCharacters = mapToCharacter(characters);
        return StreamUtil.emptyIfNullStream(marvelCharacters)
                .map(MarvelCharacter::getId)
                .collect(toList());
    }

    private String buildHash(Long timestamp) {
        String authDetails = timestamp + config.getPrivateKey() + config.getPublicKey();
        return md5DigestAsHex(authDetails.getBytes());
    }

    private Map<String, Object> buildQueryMap(Long timestamp, String hash) {
        var queryMap = new HashMap<String, Object>();
        queryMap.put("ts", timestamp);
        queryMap.put("hash", hash);
        queryMap.put("limit", config.getLimitPerPage());
        queryMap.put("offset", INITIAL_OFFSET);
        queryMap.put("apikey", config.getPublicKey());

        return queryMap;
    }
}
