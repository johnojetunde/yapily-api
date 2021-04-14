package com.yapily.marvel.domain.service;

import com.yapily.marvel.app.config.CacheConfig;
import com.yapily.marvel.domain.exception.MarvelNotFoundException;
import com.yapily.marvel.domain.model.MarvelCharacter;
import com.yapily.marvel.domain.util.DataFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCharacterServiceTest {
    @Mock
    private CharacterProvider characterProvider;
    @Mock
    private CacheConfig cacheConfig;
    private DefaultCharacterService defaultCharacterService;

    @BeforeEach
    void setUp() {
        when(cacheConfig.getCharactersExpiryInMinutes()).thenReturn(10L);

        defaultCharacterService = new DefaultCharacterService(characterProvider, cacheConfig);
    }

    @Test
    void getAllCharacterIds() {
        when(characterProvider.getAllCharacterIds())
                .thenReturn(Set.of(1L, 1234L, 4353L));

        Set<Long> result = defaultCharacterService.getCharacterIds();

        assertThat(result.size()).isEqualTo(3);
        assertThat(result.containsAll(List.of(1L, 1234L, 4353L))).isTrue();

        verify(characterProvider).getAllCharacterIds();
    }

    @Test
    void getAllCharacterIds_responseShouldBeCached() {
        when(characterProvider.getAllCharacterIds())
                .thenReturn(Set.of(1L, 1234L, 4353L));

        Set<Long> result = defaultCharacterService.getCharacterIds();
        Set<Long> result2 = defaultCharacterService.getCharacterIds();
        Set<Long> result3 = defaultCharacterService.getCharacterIds();

        assertThat(result.size()).isEqualTo(3);
        assertThat(result).isEqualTo(result2).isEqualTo(result3);

        verify(characterProvider).getAllCharacterIds();
        verifyNoMoreInteractions(characterProvider);
    }

    @Test
    void getCharacterById() {
        when(characterProvider.getCharacter(1L))
                .thenReturn(Optional.of(DataFixtures.marvelCharacter(1L)));

        MarvelCharacter marvelCharacter = defaultCharacterService.getCharacter(1L);

        assertThat(marvelCharacter.getName()).isEqualTo("Jigsaw");
        assertThat(marvelCharacter.getDescription()).isEqualTo("Jigsaw");

        verify(characterProvider).getCharacter(1L);
    }

    @Test
    void getCharacterById_characterEmpty() {
        when(characterProvider.getCharacter(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultCharacterService.getCharacter(1L))
                .isInstanceOf(MarvelNotFoundException.class)
                .hasMessage("Not found");

        verify(characterProvider).getCharacter(1L);
    }
}