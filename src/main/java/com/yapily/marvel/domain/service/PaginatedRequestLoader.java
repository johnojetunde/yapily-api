package com.yapily.marvel.domain.service;

import com.yapily.marvel.domain.model.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Slf4j
public class PaginatedRequestLoader {
    private PaginatedRequestLoader() {
    }

    public static <T> CompletableFuture<Set<T>> loadPagedRequest(Function<Map<String, Object>, CompletableFuture<Response>> loadFirstPageFunction,
                                                                 Function<List<Object>, List<T>> mapper,
                                                                 TriFunction<Long, Long, Map<String, Object>, CompletableFuture<Collection<T>>> loadSpecificPagedFunction,
                                                                 Map<String, Object> queryMap) {
        return loadFirstPageFunction.apply(queryMap)
                .thenApply(res -> {
                    var pagedResult = res.getData();
                    var mappedResult = new HashSet<>(mapper.apply(res.getData().getResults()));
                    var allOtherCharacterIds = loadAllOtherPages(pagedResult.getTotal(), pagedResult.getLimit(), queryMap, loadSpecificPagedFunction);
                    mappedResult.addAll(allOtherCharacterIds);

                    log.debug("-- successfully fetched {} results --", mappedResult.size());
                    return mappedResult;
                });
    }

    private static <T> List<T> loadAllOtherPages(long totalRecord,
                                                 long limit,
                                                 Map<String, Object> queryMap,
                                                 TriFunction<Long, Long, Map<String, Object>, CompletableFuture<Collection<T>>> executeRequestFunction) {
        double remainingRecords = totalRecord - limit;
        long pages = Math.round(remainingRecords / limit);
        long totalLoaded = limit;

        List<CompletableFuture<Collection<T>>> dataList = new ArrayList<>();
        for (int i = 1; i <= pages; i++) {
            log.debug("fetching records from {} -  {}", totalLoaded, totalLoaded + limit);
            dataList.add(executeRequestFunction.apply(totalLoaded, limit, queryMap));
            totalLoaded = totalLoaded + limit;
        }

        return dataList.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
}
