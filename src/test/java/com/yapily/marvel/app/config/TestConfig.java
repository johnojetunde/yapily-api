package com.yapily.marvel.app.config;

import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class TestConfig {
    @Primary
    @Bean
    public MarvelApiClient apiClient() {
        return new MockApiClient();
    }
}
