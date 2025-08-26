package com.anoushka.alumni_linkedin_searcher.IntegrationTests;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class AlumniIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSearchAndSaveAlumniProfilesIntegration() throws Exception{
        // Arrange input request
        AlumniSearchRequest request = new AlumniSearchRequest();
        request.setUniversity("University of XYZ");
        request.setDesignation("Software Engineer");
        request.setPassoutYear(2020);

        // Act & Assert
        mockMvc.perform(post("/api/alumni/search") // adjust if your endpoint differs
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].university").value("University of XYZ"));
    }

    //Integration test for GET /api/alumni/all
    @Test
    void testGetSavedAlumniProfiles() throws Exception {
        mockMvc.perform(get("/api/alumni/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data").isArray());
    }
};
