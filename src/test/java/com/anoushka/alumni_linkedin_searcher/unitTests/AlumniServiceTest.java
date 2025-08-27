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
    void testSearchAndSaveAlumniProfiles() {
        AlumniSearchRequest request = new AlumniSearchRequest("University of XYZ", "Software Engineer", 2020);

        AlumniProfile p1 = AlumniProfile.builder()
                .name("John Doe")
                .currentRole("Software Engineer")
                .university("University of XYZ")
                .location("New York, NY")
                .linkedinHeadline("Passionate Software Engineer")
                .passoutYear(2020)
                .build();

        AlumniProfile p2 = AlumniProfile.builder()
                .name("Jane Smith")
                .currentRole("Data Scientist")
                .university("University of XYZ")
                .location("San Francisco, CA")
                .linkedinHeadline("AI Enthusiast")
                .passoutYear(2019)
                .build();

        List<AlumniProfile> mockProfiles = List.of(p1, p2);

        when(phantomBusterService.fetchAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenReturn(mockProfiles);
        when(alumniProfileRepository.saveAll(mockProfiles)).thenReturn(mockProfiles);

        List<AlumniProfile> result = alumniService.searchAndSaveAlumniProfiles(request);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());

        verify(phantomBusterService, times(1)).fetchAlumniProfiles(any(AlumniSearchRequest.class));
        verify(alumniProfileRepository, times(1)).saveAll(mockProfiles);
    }

    @Test
    void testSearchAndSaveAlumniProfiles_EmptyList() {
        AlumniSearchRequest request = new AlumniSearchRequest("University of XYZ", "Software Engineer", null);

        when(phantomBusterService.fetchAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenReturn(Collections.emptyList());

        when(alumniProfileRepository.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<AlumniProfile> result = alumniService.searchAndSaveAlumniProfiles(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(phantomBusterService, times(1)).fetchAlumniProfiles(any(AlumniSearchRequest.class));
        verify(alumniProfileRepository, times(1)).saveAll(Collections.emptyList());
    }

    @Test
    void testSearchAndSaveAlumniProfiles_ExceptionThrown() {
        AlumniSearchRequest request = new AlumniSearchRequest("University of XYZ", "Software Engineer", null);

        when(phantomBusterService.fetchAlumniProfiles(any(AlumniSearchRequest.class)))
                .thenThrow(new RuntimeException("PhantomBuster API failed"));

        assertThrows(RuntimeException.class,
                () -> alumniService.searchAndSaveAlumniProfiles(request));

        verify(alumniProfileRepository, never()).saveAll(any());
    }
}
