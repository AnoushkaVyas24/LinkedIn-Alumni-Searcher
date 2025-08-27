package com.anoushka.alumni_linkedin_searcher.service;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.anoushka.alumni_linkedin_searcher.repository.AlumniProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlumniService {

    private static final Logger log = LoggerFactory.getLogger(AlumniService.class);
    private final PhantomBusterService phantomBusterService;
    private final AlumniProfileRepository alumniProfileRepository;

    public AlumniService(PhantomBusterService phantomBusterService, AlumniProfileRepository alumniProfileRepository) {
        this.phantomBusterService = phantomBusterService;
        this.alumniProfileRepository = alumniProfileRepository;
    }

    @Transactional
    public ApiResponse<List<AlumniProfile>> searchAndSaveAlumniProfiles(AlumniSearchRequest request){
        log.info("=== Starting searchAndSaveAlumniProfiles ===");
        log.info("Search request: university={}, designation={}, passoutYear={}",
                request.getUniversity(), request.getDesignation(), request.getPassoutYear());

        try {
            // Step 1: Fetch profiles from PhantomBuster
            log.info("Fetching profiles from PhantomBuster...");
            List<AlumniProfile> fetchedProfiles = phantomBusterService.fetchAlumniProfiles(request);
            log.info("Fetched {} profiles from PhantomBuster", fetchedProfiles.size());

            if (fetchedProfiles.isEmpty()) {
                log.warn("No profiles fetched from PhantomBuster");
                return new ApiResponse<>("success", fetchedProfiles);
            }

            // Step 2: Save profiles to database
            log.info("Saving {} profiles to database...", fetchedProfiles.size());
            List<AlumniProfile> savedProfiles = alumniProfileRepository.saveAll(fetchedProfiles);
            log.info("Successfully saved {} profiles to database", savedProfiles.size());

            // Log saved profiles for verification
            for (AlumniProfile profile : savedProfiles) {
                log.info("Saved profile - ID: {}, Name: {}, Role: {}, University: {}",
                        profile.getId(), profile.getName(), profile.getCurrentRole(), profile.getUniversity());
            }

            return new ApiResponse<>("success", savedProfiles);

        } catch (Exception e) {
            log.error("Error in searchAndSaveAlumniProfiles", e);
            throw new RuntimeException("Failed to search and save alumni profiles: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<AlumniProfile>> getAllAlumniProfiles(){
        log.info("=== Starting getAllAlumniProfiles ===");

        try {
            List<AlumniProfile> allProfiles = alumniProfileRepository.findAll();
            log.info("Retrieved {} profiles from database", allProfiles.size());

            // Log profiles for verification
            for (AlumniProfile profile : allProfiles) {
                log.info("Retrieved profile - ID: {}, Name: {}, Role: {}, University: {}",
                        profile.getId(), profile.getName(), profile.getCurrentRole(), profile.getUniversity());
            }

            return new ApiResponse<>("success", allProfiles);

        } catch (Exception e) {
            log.error("Error in getAllAlumniProfiles", e);
            throw new RuntimeException("Failed to retrieve alumni profiles: " + e.getMessage(), e);
        }
    }
}