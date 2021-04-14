package com.yapily.marvel.app.config;

import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
    @Primary
    @Bean
    public MarvelApiClient apiClient() {
        return new MockApiClient();
    }
}
