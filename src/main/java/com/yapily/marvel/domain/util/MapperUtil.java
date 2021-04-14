package com.yapily.marvel.domain.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yapily.marvel.domain.model.MarvelCharacter;

import java.util.ArrayList;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class MapperUtil {
    private MapperUtil() {
    }

    public static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper()
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(FAIL_ON_EMPTY_BEANS)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule());

        MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, MarvelCharacter.class);
    }
}
