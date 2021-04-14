package com.yapily.marvel.domain.service;

import com.yapily.marvel.app.config.MarvelConfig;
import com.yapily.marvel.domain.exception.MarvelApiException;
import com.yapily.marvel.domain.exception.MarvelNotFoundException;
import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import com.yapily.marvel.domain.model.MarvelCharacter;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.adapter.java8.HttpException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionException;

import static com.yapily.marvel.domain.util.DataFixtures.defaultResponse;
import static com.yapily.marvel.domain.util.DataFixtures.pageResult;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCharacterProviderTest {

    @Mock
    private MarvelApiClient apiClient;
    @Mock
    private MarvelConfig config;
    private DefaultCharacterProvider characterProvider;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2021-04-14T10:15:30.00Z"), ZoneId.systemDefault());
        characterProvider = Mockito.spy(new DefaultCharacterProvider(config, clock, apiClient));
    }

    @Test
    void getCharacterById() {
        var response = defaultResponse();

        when(apiClient.getCharacter(eq(1L), anyMap()))
                .thenReturn(completedFuture(response));

        Optional<MarvelCharacter> result = characterProvider.getCharacter(1L);

        assertThat(result.isPresent()).isTrue();

        var character = result.get();

        assertThat(character.getId()).isEqualTo(1L);
        assertThat(character.getName()).isEqualTo("Jigsaw");
        assertThat(character.getDescription()).isEqualTo("Jigsaw");
        assertThat(character.getThumbnail().getPath()).isEqualTo("path");
        assertThat(character.getThumbnail().getExtension()).isEqualTo("jpg");

        verify(characterProvider).mapToCharacter(anyList());
        verify(apiClient).getCharacter(eq(1L), anyMap());
    }

    @Test
    void getCharacterById_NotFound() {
        var httpResponse = retrofit2.Response.error(404, ResponseBody.create(MediaType.get("application/json"), "not found"));
        HttpException exception = new HttpException(httpResponse);
        CompletionException completionException = new CompletionException("Not found", exception);

        doThrow(completionException)
                .when(apiClient).getCharacter(eq(1L), anyMap());

        assertThatThrownBy(() -> characterProvider.getCharacter(1L))
                .isInstanceOf(MarvelNotFoundException.class)
                .hasMessage("Not found");

        verify(apiClient).getCharacter(eq(1L), anyMap());
    }

    @Test
    void getCharacterById_failed() {
        CompletionException completionException = new CompletionException("something failed somewhere", new Exception("something failed somewhere"));

        doThrow(completionException)
                .when(apiClient).getCharacter(eq(1L), anyMap());

        assertThatThrownBy(() -> characterProvider.getCharacter(1L))
                .isInstanceOf(MarvelApiException.class)
                .hasMessage("java.lang.Exception: something failed somewhere");

        verify(apiClient).getCharacter(eq(1L), anyMap());
    }

    @Test
    void loadCharacterIds() {
        var pageResult = pageResult(1L)
                .setTotal(2L)
                .setLimit(1L);

        var response = defaultResponse(1L).setData(pageResult);
        var secondResponse = defaultResponse(1000L);

        when(apiClient.getCharacters(anyMap()))
                .thenReturn(completedFuture(response))
                .thenReturn(completedFuture(secondResponse));

        Set<Long> result = characterProvider.getAllCharacterIds();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.containsAll(List.of(1L, 1000L))).isTrue();

        verify(characterProvider, times(2)).mapToCharacter(anyList());
        verify(apiClient, times(2)).getCharacters(anyMap());
    }

    @Test
    void loadCharacterIds_failed() {
        CompletionException completionException = new CompletionException("something failed somewhere", new Exception("something failed somewhere"));

        doThrow(completionException)
                .when(apiClient).getCharacters(anyMap());

        assertThatThrownBy(() -> characterProvider.getAllCharacterIds())
                .isInstanceOf(MarvelApiException.class)
                .hasMessage("java.lang.Exception: something failed somewhere");

        verify(characterProvider, times(0)).mapToCharacter(anyList());
        verify(apiClient).getCharacters(anyMap());
    }
}