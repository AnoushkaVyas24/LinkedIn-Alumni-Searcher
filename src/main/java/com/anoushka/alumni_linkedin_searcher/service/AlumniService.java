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
public class AlumniService {

    private static final Logger log = LoggerFactory.getLogger(AlumniService.class);
    private final PhantomBusterService phantomBusterService;
    private final AlumniProfileRepository alumniProfileRepository;

    //constructor injection
    public AlumniService(PhantomBusterService phantomBusterService, AlumniProfileRepository alumniProfileRepository) {
        this.phantomBusterService = phantomBusterService;
        this.alumniProfileRepository = alumniProfileRepository;
    }

    //Fetches alumni from PhantomBuster API, saves them into DB, and returns them
    public List<AlumniProfile> searchAndSaveAlumniProfiles(AlumniSearchRequest request){
        log.info("Searching alumni for university={}, designation={}, passoutYear={}",
                request.getUniversity(), request.getDesignation(), request.getPassoutYear());

        //1. Call PhantomBuster API
        List<AlumniProfile> fetchedProfiles = phantomBusterService.fetchAlumniProfiles(request);

        // 2. Save to DB
        return alumniProfileRepository.saveAll(fetchedProfiles);
    }

    //Retrieves all saved alumni from database
    public List<AlumniProfile> getAllAlumniProfiles(){
        return alumniProfileRepository.findAll();
    }
}
