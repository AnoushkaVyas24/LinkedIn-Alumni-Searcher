package com.anoushka.alumni_linkedin_searcher.controller;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.dto.ApiResponse;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.anoushka.alumni_linkedin_searcher.service.AlumniService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    private static final Logger log = LoggerFactory.getLogger(AlumniController.class);
    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<AlumniProfile>>> searchAlumni(@Valid @RequestBody AlumniSearchRequest request) {
        log.info("=== POST /api/alumni/search called ===");
        log.info("Request received: {}", request);
        log.info("University: {}", request.getUniversity());
        log.info("Designation: {}", request.getDesignation());
        log.info("PassoutYear: {}", request.getPassoutYear());

        try {
            ApiResponse<List<AlumniProfile>> response = alumniService.searchAndSaveAlumniProfiles(request);
            log.info("Returning response with {} profiles", response.getData().size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in searchAlumni endpoint", e);
            ApiResponse<List<AlumniProfile>> errorResponse = new ApiResponse<>("error", List.of());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AlumniProfile>>> getAllAlumni() {
        log.info("=== GET /api/alumni/all called ===");

        try {
            ApiResponse<List<AlumniProfile>> response = alumniService.getAllAlumniProfiles();
            log.info("Returning response with {} profiles", response.getData().size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in getAllAlumni endpoint", e);
            ApiResponse<List<AlumniProfile>> errorResponse = new ApiResponse<>("error", List.of());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Debug endpoint to test basic connectivity
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        log.info("=== GET /api/alumni/health called ===");
        ApiResponse<String> response = new ApiResponse<>("success", "Alumni service is running!");
        return ResponseEntity.ok(response);
    }

    // Debug endpoint to test database connectivity
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getAlumniCount() {
        log.info("=== GET /api/alumni/count called ===");

        try {
            ApiResponse<List<AlumniProfile>> allProfiles = alumniService.getAllAlumniProfiles();
            long count = allProfiles.getData().size();

            ApiResponse<Long> response = new ApiResponse<>("success", count);
            log.info("Total alumni profiles in database: {}", count);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting alumni count", e);
            ApiResponse<Long> errorResponse = new ApiResponse<>("error", 0L);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}