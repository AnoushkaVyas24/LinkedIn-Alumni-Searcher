package com.anoushka.alumni_linkedin_searcher.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status;
    private Object data;

    public ApiResponse(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public ApiResponse() {
    }
}