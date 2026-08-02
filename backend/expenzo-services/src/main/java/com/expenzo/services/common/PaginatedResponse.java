package com.expenzo.services.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedResponse<T> {

    private List<T> content;
    private boolean hasNext;
    private int size;

    public PaginatedResponse(List<T> content, boolean hasNext, int size) {
        this.content = content;
        this.hasNext = hasNext;
        this.size = size;
    }
}
