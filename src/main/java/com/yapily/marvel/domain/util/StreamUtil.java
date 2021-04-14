package com.yapily.marvel.domain.util;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class StreamUtil {
    private StreamUtil() {
    }

    public static <T> Stream<T> emptyIfNullStream(Collection<T> collection) {
        return ofNullable(collection).stream().flatMap(Collection::stream);
    }
}
