package com.yapily.marvel.domain.service;

import com.yapily.marvel.domain.model.Response;
import com.yapily.marvel.domain.model.Response.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaginatedRequestLoaderTest {
    private PageResult result;

    @BeforeEach
    void setUp() {
        result = Mockito.mock(PageResult.class);
        when(result.getTotal())
                .thenReturn(30L);
        when(result.getResults()).thenReturn(List.of(new Object()));
        when(result.getLimit())
                .thenReturn(10L);
    }

    @Test
    void testPaginatedMethod() {
        MockApiRequest apiRequest = Mockito.spy(new MockApiRequest());
        var queryMap = new HashMap<String, Object>();

        Set<Object> result = PaginatedRequestLoader.loadPagedRequest(
                apiRequest::loadFirstPage,
                apiRequest::mapper,
                apiRequest::loadOtherPages,
                queryMap).join();

        assertThat(result.size()).isEqualTo(5);

        verify(apiRequest).loadFirstPage(queryMap);
        verify(apiRequest).loadOtherPages(10L, 10L, queryMap);
        verify(apiRequest).loadOtherPages(20L, 10L, queryMap);
        verify(apiRequest).mapper(anyList());

    }

    private class MockApiRequest {
        public CompletableFuture<Response> loadFirstPage(Map<String, Object> map) {
            var response = new Response();
            response.setData(result);
            return completedFuture(response);
        }

        public CompletableFuture<Collection<Object>> loadOtherPages(Long offset, Long limit, Map<String, Object> map) {
            return completedFuture(List.of(new Object(), new Object()));
        }

        public List<Object> mapper(List<Object> object) {
            return object;
        }
    }
}