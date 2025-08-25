package com.anoushka.alumni_linkedin_searcher.service;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.anoushka.alumni_linkedin_searcher.repository.AlumniProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlumniService {

    private static final Logger log = LoggerFactory.getLogger(AlumniService.class);
    private final PhantomBusterService phantomBusterService;
    private final AlumniProfileRepository alumniProfileRepository;

    public AlumniService(PhantomBusterService phantomBusterService, AlumniProfileRepository alumniProfileRepository) {
        this.phantomBusterService = phantomBusterService;
        this.alumniProfileRepository = alumniProfileRepository;
    }

    //Fetches alumni from PhantomBuster API, saves them into DB, and returns them
    public ApiResponse searchAndSaveAlumniProfiles(AlumniSearchRequest request){
        log.info("Searching alumni for university={}, designation={}, passoutYear={}",
                request.getUniversity(), request.getDesignation(), request.getPassoutYear());

        //1. Call PhantomBuster API
        List<AlumniProfile> fetchedProfiles = phantomBusterService.fetchAlumniProfiles(request);

        // 2. Save to DB
        alumniProfileRepository.saveAll(fetchedProfiles);

        // 3. Return wrapped response
        return new ApiResponse("success", fetchedProfiles);
    }

    //Retrieves all saved alumni from database
    public ApiResponse getAllAlumniProfiles(){
        List<AlumniProfile> alumniProfiles = alumniProfileRepository.findAll();
        return new ApiResponse("success", alumniProfiles);
    }
}
