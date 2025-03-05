package com.digitalhouse.court_rental.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> data;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private boolean hasNextPage;
    private boolean hasPrevPage;

    public PagedResponse(List<T> data, int currentPage, int pageSize, long totalElements) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.hasNextPage = currentPage < totalPages - 1;
        this.hasPrevPage = currentPage > 0;
    }
}
