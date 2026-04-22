package com.expenzo.coreservices.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PaginatedResponse<T> {
    private List<T> data;
    private boolean hasNext;

    public PaginatedResponse(List<T> data, boolean hasNext) {
        this.data = data;
        this.hasNext = hasNext;
    }
}
