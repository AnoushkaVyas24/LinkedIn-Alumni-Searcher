package com.anoushka.alumni_linkedin_searcher.IntegrationTests;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.anoushka.alumni_linkedin_searcher.repository.AlumniProfileRepository;
import com.anoushka.alumni_linkedin_searcher.service.PhantomBusterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class AlumniIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlumniProfileRepository alumniProfileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Replace PhantomBusterService bean with a Mockito mock
    @MockitoBean
    private PhantomBusterService phantomBusterService;

    @BeforeEach
    void setup() {
        // Clean database before each test
        alumniProfileRepository.deleteAll();

        // Mock returned alumni profiles
        AlumniProfile alumni = new AlumniProfile();
        alumni.setName("John Doe");
        alumni.setUniversity("University of XYZ");
        alumni.setCurrentRole("Software Engineer");
        alumni.setPassoutYear(2020);

        Mockito.when(phantomBusterService.fetchAlumniProfiles(Mockito.any()))
                .thenReturn(List.of(alumni));
    }

    @Test
    void testSearchAndSaveAlumniProfilesIntegration() throws Exception {
        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AlumniSearchRequest("University of XYZ", "Software Engineer", 2020)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.data[0].university").value("University of XYZ"));
    }

    @Test
    void testGetSavedAlumniProfiles() throws Exception {
        mockMvc.perform(get("/api/alumni/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data").isArray());
    }
}