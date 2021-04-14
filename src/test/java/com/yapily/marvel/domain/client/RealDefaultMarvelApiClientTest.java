package com.yapily.marvel.domain.client;

import com.yapily.marvel.domain.model.MarvelCharacter;
import com.yapily.marvel.domain.service.DefaultCharacterProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled("This test will try to connect to the live marvel API. Should be used for debugging purpose only")
@SpringBootTest
class RealDefaultMarvelApiClientTest {

    @Autowired
    private DefaultCharacterProvider defaultMarvelApiClient;

    @Test
    void getAllCharacterIds() {
        Set<Long> characterIds = defaultMarvelApiClient.getAllCharacterIds();
        assertThat(characterIds.size()).isEqualTo(1493);
    }

    @Test
    void getCharacterIds() {
        Optional<MarvelCharacter> character = defaultMarvelApiClient.getCharacter(1009374L);

        assertThat(character.isPresent()).isTrue();
        assertThat(character.get().getName()).isEqualTo("Jigsaw");
    }
}