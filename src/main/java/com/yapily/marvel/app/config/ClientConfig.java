package com.yapily.marvel.app.config;

import com.yapily.marvel.domain.marvelapi.MarvelApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final MarvelConfig marvelConfig;
    private final RetrofitConfig config;

    @Bean
    public MarvelApiClient marvelApiClient() {
        return retrofitProvider().initializer(MarvelApiClient.class, marvelConfig.getBaseUrl());
    }

    private RetrofitProvider retrofitProvider() {
        return new RetrofitProvider(config.getConnectTimeout(), config.getReadTimeout(), config.getWriteTimeout());
    }
}
