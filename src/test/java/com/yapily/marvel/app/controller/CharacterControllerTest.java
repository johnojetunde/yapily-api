package com.yapily.marvel.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapily.marvel.app.config.TestConfig;
import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import retrofit2.adapter.java8.HttpException;

import java.util.concurrent.CompletionException;

import static com.yapily.marvel.domain.util.DataFixtures.defaultResponse;
import static com.yapily.marvel.domain.util.DataFixtures.pageResult;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class CharacterControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private MarvelApiClient marvelApiClient;

    @BeforeEach
    void setUp() {
        var pageResult = pageResult(1L)
                .setTotal(2L)
                .setLimit(1L);

        var response = defaultResponse(1L).setData(pageResult);
        var secondResponse = defaultResponse(1000L);

        when(marvelApiClient.getCharacters(anyMap()))
                .thenReturn(completedFuture(response))
                .thenReturn(completedFuture(secondResponse));
    }

    @Test
    public void getCharacters() throws Exception {
        makeGetCharactersCall();
        makeGetCharactersCall();
        makeGetCharactersCall();

        verify(marvelApiClient, times(0)).getCharacters(anyMap());
    }

    @Test
    public void getCharacterById() throws Exception {
        when(marvelApiClient.getCharacter(eq(1L), anyMap()))
                .thenReturn(completedFuture(defaultResponse(1L)));

        mvc.perform(get("/characters/1")
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        verify(marvelApiClient).getCharacter(eq(1L), anyMap());
    }

    @Test
    public void getCharacterById_notFound() throws Exception {
        var httpResponse = retrofit2.Response.error(404, ResponseBody.create(MediaType.get("application/json"), "not found"));
        HttpException exception = new HttpException(httpResponse);
        CompletionException completionException = new CompletionException("Not found", exception);

        doThrow(completionException)
                .when(marvelApiClient).getCharacter(eq(1L), anyMap());

        mvc.perform(get("/characters/1")
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(marvelApiClient).getCharacter(eq(1L), anyMap());
    }

    private void makeGetCharactersCall() throws Exception {
        mvc.perform(get("/characters")
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }
}