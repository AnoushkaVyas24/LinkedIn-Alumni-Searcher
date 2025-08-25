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
                // top-level API response fields
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                // validate that returned profiles contain expected fields
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].university").value("University of XYZ"));
    }
};
