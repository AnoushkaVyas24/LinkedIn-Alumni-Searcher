package com.anoushka.alumni_linkedin_searcher.controller;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.service.AlumniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alumni")
@RequiredArgsConstructor
public class AlumniController {
    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    //Utilize PhantomBuster to save profiles to database:
    @PostMapping("/search")
    public ResponseEntity<ApiResponse> searchAlumni(@RequestBody AlumniSearchRequest request){
        ApiResponse response = alumniService.searchAndSaveAlumniProfiles(request);
        return ResponseEntity.ok(response);
    }

    //Fetch all saved profiles from the database:
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllAlumni(){
        ApiResponse response = alumniService.getAllAlumniProfiles();
        return ResponseEntity.ok(response);
    }
}
