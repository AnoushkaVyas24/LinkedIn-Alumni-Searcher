package com.anoushka.alumni_linkedin_searcher.unitTests;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.anoushka.alumni_linkedin_searcher.repository.AlumniProfileRepository;
import com.anoushka.alumni_linkedin_searcher.service.AlumniService;
import com.anoushka.alumni_linkedin_searcher.service.PhantomBusterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlumniServiceTest {
    @Mock
    private PhantomBusterService phantomBusterService;

    @Mock
    private AlumniProfileRepository alumniProfileRepository;

    @InjectMocks
    private AlumniService alumniService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchAndSaveAlumniProfiles(){
        //create request
        AlumniSearchRequest request = new AlumniSearchRequest();
        request.setUniversity("University of XYZ");
        request.setDesignation("Software Engineer");
        request.setPassoutYear(2020);

        //create mock profiles
        AlumniProfile p1 = new AlumniProfile();
        p1.setName("John Doe");
        p1.setCurrentRole("Software Engineer");
        p1.setUniversity("University of XYZ");
        p1.setLocation("New York, NY");
        p1.setLinkedinHeadline("Passionate Software Engineer");
        p1.setPassoutYear(2020);

        AlumniProfile p2 = new AlumniProfile();
        p2.setName("Jane Smith");
        p2.setCurrentRole("Data Scientist");
        p2.setUniversity("University of XYZ");
        p2.setLocation("San Francisco, CA");
        p2.setLinkedinHeadline("AI Enthusiast");
        p2.setPassoutYear(2019);

        List<AlumniProfile> mockProfiles = Arrays.asList(p1, p2);

        //Mock phantombuster api response:
        when(phantomBusterService.fetchAlumniProfiles(any(AlumniSearchRequest.class))).thenReturn(mockProfiles);

        //Mock Database save:
        when(alumniProfileRepository.saveAll(mockProfiles)).thenReturn(mockProfiles);

        //Call Service:
        ApiResponse response = alumniService.searchAndSaveAlumniProfiles(request);

        // Assert (response checks)
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        //Extract list from response:
        @SuppressWarnings("unchecked")
        List<AlumniProfile> result = (List<AlumniProfile>) response.getData();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(phantomBusterService, times(1)).fetchAlumniProfiles(any(AlumniSearchRequest.class));
        verify(alumniProfileRepository, times(1)).saveAll(mockProfiles);
    }

    @Test
    void testSearchAndSaveAlumniProfiles_EmptyList(){
        AlumniSearchRequest request = new AlumniSearchRequest();
        request.setUniversity("University of XYZ");
        request.setDesignation("Software Engineer");

        when(phantomBusterService.fetchAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenReturn(Collections.emptyList());

        ApiResponse response = alumniService.searchAndSaveAlumniProfiles(request);

        @SuppressWarnings("unchecked")
        List<AlumniProfile> result = (List<AlumniProfile>) response.getData();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals("success", response.getStatus());
        verify(alumniProfileRepository, times(1)).saveAll(Collections.emptyList());
    }

    @Test
    void testSearchAndSaveAlumniProfiles_ExceptionThrown(){
        AlumniSearchRequest request = new AlumniSearchRequest();
        request.setUniversity("University of XYZ");
        request.setDesignation("Software Engineer");

        when(phantomBusterService.fetchAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenThrow(new RuntimeException("PhantomBuster API failed"));

        ApiResponse response = alumniService.searchAndSaveAlumniProfiles(request);

        assertEquals("error", response.getStatus());
        assertTrue(response.getData() instanceof String); // error message stored in data
        assertEquals("PhantomBuster API failed", response.getData());
        verify(alumniProfileRepository, never()).saveAll(any());
    }
}
