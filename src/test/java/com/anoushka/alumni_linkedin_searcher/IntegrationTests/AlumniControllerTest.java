package com.anoushka.alumni_linkedin_searcher.IntegrationTests;

import com.anoushka.alumni_linkedin_searcher.controller.AlumniController;
import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.service.AlumniService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlumniController.class)
public class AlumniControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlumniService alumniService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSearchAlumniProfiles_Success() throws Exception {
        ApiResponse mockResponse = new ApiResponse("success", Collections.emptyList());

        when(alumniService.searchAndSaveAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenReturn(mockResponse);

        AlumniSearchRequest request = new AlumniSearchRequest();
        request.setUniversity("University of XYZ");
        request.setDesignation("Software Engineer");
        request.setPassoutYear(2020);

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testSearchAlumniProfiles_InvalidRequest() throws Exception {
        // Missing university/designation/year -> should trigger validation error
        AlumniSearchRequest invalidRequest = new AlumniSearchRequest();

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchAlumniProfiles_ServiceError() throws Exception {
        ApiResponse errorResponse = new ApiResponse("error", "PhantomBuster API failed");

        when(alumniService.searchAndSaveAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenReturn(errorResponse);

        AlumniSearchRequest request = new AlumniSearchRequest();
        request.setUniversity("University of XYZ");
        request.setDesignation("Software Engineer");

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value("PhantomBuster API failed"));
    }
}
