package com.yapily.marvel.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class Response {
    private PageResult data;

    @Accessors(chain = true)
    @Data
    public static class PageResult {
        private Long limit;
        private Long total;
        private List<Object> results;
    }
}
