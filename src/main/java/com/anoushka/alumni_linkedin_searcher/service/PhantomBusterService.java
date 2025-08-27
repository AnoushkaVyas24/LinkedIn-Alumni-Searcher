package com.anoushka.alumni_linkedin_searcher.service;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhantomBusterService {
    private static final Logger log = LoggerFactory.getLogger(PhantomBusterService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PhantomBusterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${phantombuster.api.key}")
    private String apiKey;

    @Value("${phantombuster.api.url}")
    private String apiUrl;

    //Calls PhantomBuster API with given input and returns alumni profiles
    public List<AlumniProfile> fetchAlumniProfiles(AlumniSearchRequest request) {
        List<AlumniProfile> profiles = new ArrayList<>();

        try {
            // Build query params
            String url = apiUrl + "?university=" + request.getUniversity()
                    + "&designation=" + request.getDesignation()
                    + (request.getPassoutYear() != null ? "&passoutYear=" + request.getPassoutYear() : "");

            log.info("Calling PhantomBuster API: {}", url);

            // Make API call
            String response = restTemplate.getForObject(url, String.class);

            // Parse JSON
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data"); // adjust based on PhantomBuster response

            for (JsonNode node : data) {
                AlumniProfile alumni = new AlumniProfile();
                alumni.setName(node.path("name").asText());
                alumni.setCurrentRole(node.path("currentRole").asText());
                alumni.setUniversity(node.path("university").asText());
                alumni.setLocation(node.path("location").asText());
                alumni.setLinkedinHeadline(node.path("linkedinHeadline").asText());
                if (node.has("passoutYear")) {
                    alumni.setPassoutYear(node.path("passoutYear").asInt());
                }
                profiles.add(alumni);
            }

        } catch (Exception e) {
            log.error("Error calling PhantomBuster API", e);
        }

        return profiles;
    }

}