package com.anoushka.alumni_linkedin_searcher.IntegrationTests;

import com.anoushka.alumni_linkedin_searcher.controller.AlumniController;
import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.anoushka.alumni_linkedin_searcher.service.AlumniService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlumniController.class)
public class AlumniControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlumniService alumniService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSearchAlumniProfiles_Success() throws Exception {
        AlumniProfile p1 = AlumniProfile.builder()
                .name("John Doe")
                .currentRole("Software Engineer")
                .university("University of XYZ")
                .location("New York, NY")
                .linkedinHeadline("Passionate Software Engineer at XYZ Corp")
                .passoutYear(2020)
                .build();

        when(alumniService.searchAndSaveAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenReturn(List.of(p1));

        AlumniSearchRequest request = new AlumniSearchRequest("University of XYZ", "Software Engineer", 2020);

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("John Doe"));
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
        when(alumniService.searchAndSaveAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenThrow(new RuntimeException("PhantomBuster API failed"));

        AlumniSearchRequest request = new AlumniSearchRequest("University of XYZ", "Software Engineer", null);

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value("PhantomBuster API failed"));
    }
}