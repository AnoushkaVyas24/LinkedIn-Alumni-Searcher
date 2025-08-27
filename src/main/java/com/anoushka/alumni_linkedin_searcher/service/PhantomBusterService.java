package com.anoushka.alumni_linkedin_searcher.service;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Phantombuster-Key-1", apiKey);

            // Prepare input body for the phantom
            Map<String, Object> input = new HashMap<>();
            input.put("university", request.getUniversity());
            input.put("designation", request.getDesignation());
            if (request.getPassoutYear() != null) {
                input.put("passoutYear", request.getPassoutYear());
            }

            Map<String, Object> body = new HashMap<>();
            body.put("argument", input); // Phantom expects "argument" JSON field

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            log.info("Calling PhantomBuster API: {}", apiUrl);

            // Call API
            String response = restTemplate.postForObject(apiUrl, entity, String.class);

            // Parse JSON
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data"); // adjust based on actual PhantomBuster output

            for (JsonNode node : data) {
                AlumniProfile alumni = new AlumniProfile();
                alumni.setName(node.path("name").asText(null));
                alumni.setCurrentRole(node.path("currentRole").asText(null));
                alumni.setUniversity(node.path("university").asText(null));
                alumni.setLocation(node.path("location").asText(null));
                alumni.setLinkedinHeadline(node.path("linkedinHeadline").asText(null));
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