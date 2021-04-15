package com.yapily.marvel.domain.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

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
    }

    public static <T> List<T> convert(List<Object> objects, Class<T> clazz) {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
        return MAPPER.convertValue(objects, type);
    }
}
