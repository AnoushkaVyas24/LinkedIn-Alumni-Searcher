package com.anoushka.alumni_linkedin_searcher.controller;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.anoushka.alumni_linkedin_searcher.service.AlumniService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {
    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    //Utilize PhantomBuster to save profiles to database:
    @PostMapping("/search")
    public ApiResponse searchAlumni(@Valid @RequestBody AlumniSearchRequest request){
        List<AlumniProfile> result = alumniService.searchAndSaveAlumniProfiles(request);
        return new ApiResponse("success", result);
    }

    //Fetch all saved profiles from the database:
    @GetMapping("/all")
    public ApiResponse getAllAlumni(){
        List<AlumniProfile> result = alumniService.getAllAlumniProfiles();
        return new ApiResponse("success", result);
    }
}
