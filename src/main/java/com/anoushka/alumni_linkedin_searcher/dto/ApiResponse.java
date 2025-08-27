package com.anoushka.alumni_linkedin_searcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private String status;
    private T data;

    public ApiResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }
}