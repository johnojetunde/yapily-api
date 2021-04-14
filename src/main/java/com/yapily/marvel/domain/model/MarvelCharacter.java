package com.yapily.marvel.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MarvelCharacter {
    private Long id;
    private String name;
    private String description;
    private Thumbnail thumbnail;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Thumbnail {
        private String path;
        private String extension;
    }
}
