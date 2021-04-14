package com.yapily.marvel.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "marvel")
public class MarvelConfig {
    private String baseUrl;
    private String publicKey;
    private String privateKey;
    private Long limitPerPage;
}
