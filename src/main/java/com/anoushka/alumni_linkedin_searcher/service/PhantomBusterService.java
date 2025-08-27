package com.anoushka.alumni_linkedin_searcher.service;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PhantomBusterService {

    private static final Logger log = LoggerFactory.getLogger(PhantomBusterService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${phantombuster.api.key}")
    private String apiKey;

    @Value("${phantombuster.agent.id}")
    private String agentId;

    @Value("${phantombuster.session.cookie}")
    private String sessionCookie;

    @Value("${phantombuster.api.url}")
    private String apiUrl;

    public PhantomBusterService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<AlumniProfile> fetchAlumniProfiles(AlumniSearchRequest request) {
        log.info("=== Starting fetchAlumniProfiles ===");
        log.info("Request: university={}, designation={}, passoutYear={}",
                request.getUniversity(), request.getDesignation(), request.getPassoutYear());

        try {
            // For now, let's return mock data to test the full flow
            // Once this works, we can fix the PhantomBuster integration
            List<AlumniProfile> mockProfiles = createMockData(request);

            // Uncomment below when you want to test with real PhantomBuster
            // List<AlumniProfile> realProfiles = callPhantomBusterAPI(request);
            // return realProfiles;

            log.info("Returning {} mock profiles", mockProfiles.size());
            return mockProfiles;

        } catch (Exception e) {
            log.error("Error in fetchAlumniProfiles", e);
            return new ArrayList<>();
        }
    }

    // This method creates mock data to test your complete flow
    private List<AlumniProfile> createMockData(AlumniSearchRequest request) {
        List<AlumniProfile> profiles = new ArrayList<>();

        // Create 3 mock profiles based on search request
        AlumniProfile profile1 = new AlumniProfile();
        profile1.setName("John Doe");
        profile1.setCurrentRole(request.getDesignation() + " at TechCorp");
        profile1.setUniversity(request.getUniversity());
        profile1.setLocation("San Francisco, CA");
        profile1.setLinkedinHeadline("Experienced " + request.getDesignation() + " | Tech Enthusiast");
        profile1.setPassoutYear(request.getPassoutYear() != null ? request.getPassoutYear() : 2020);
        profiles.add(profile1);

        AlumniProfile profile2 = new AlumniProfile();
        profile2.setName("Jane Smith");
        profile2.setCurrentRole("Senior " + request.getDesignation() + " at InnovateInc");
        profile2.setUniversity(request.getUniversity());
        profile2.setLocation("New York, NY");
        profile2.setLinkedinHeadline("Senior " + request.getDesignation() + " | Innovation Leader");
        profile2.setPassoutYear(request.getPassoutYear() != null ? request.getPassoutYear() - 1 : 2019);
        profiles.add(profile2);

        AlumniProfile profile3 = new AlumniProfile();
        profile3.setName("Mike Johnson");
        profile3.setCurrentRole("Lead " + request.getDesignation() + " at StartupXYZ");
        profile3.setUniversity(request.getUniversity());
        profile3.setLocation("Austin, TX");
        profile3.setLinkedinHeadline("Lead " + request.getDesignation() + " | Startup Enthusiast");
        profile3.setPassoutYear(request.getPassoutYear() != null ? request.getPassoutYear() + 1 : 2021);
        profiles.add(profile3);

        log.info("Created {} mock profiles for testing", profiles.size());
        return profiles;
    }

    // Real PhantomBuster API call - use this once mock data works
    private List<AlumniProfile> callPhantomBusterAPI(AlumniSearchRequest request) {
        try {
            log.info("Calling PhantomBuster API...");

            String triggerUrl = apiUrl + "?id=" + agentId;
            log.info("PhantomBuster URL: {}", triggerUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Phantombuster-Key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sessionCookie", sessionCookie);

            // Add arguments for your specific PhantomBuster agent
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("searches", buildSearchQuery(request));
            arguments.put("numberOfResultsPerSearch", 10);
            requestBody.put("arguments", arguments);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to PhantomBuster: {}", requestBody);

            ResponseEntity<String> response = restTemplate.exchange(triggerUrl, HttpMethod.POST, entity, String.class);

            log.info("PhantomBuster response status: {}", response.getStatusCode());
            log.info("PhantomBuster response body: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parsePhantomBusterResponse(response.getBody(), request);
            } else {
                log.error("PhantomBuster API call failed with status: {}", response.getStatusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("Error calling PhantomBuster API", e);
            return new ArrayList<>();
        }
    }

    private String buildSearchQuery(AlumniSearchRequest request) {
        StringBuilder query = new StringBuilder();
        query.append("site:linkedin.com/in/ ");
        query.append("\"").append(request.getUniversity()).append("\"");

        if (request.getDesignation() != null && !request.getDesignation().trim().isEmpty()) {
            query.append(" \"").append(request.getDesignation()).append("\"");
        }

        if (request.getPassoutYear() != null) {
            query.append(" ").append(request.getPassoutYear());
        }

        log.info("Built search query: {}", query.toString());
        return query.toString();
    }

    private List<AlumniProfile> parsePhantomBusterResponse(String responseBody, AlumniSearchRequest request) {
        List<AlumniProfile> profiles = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            // Check if response contains containerId (agent launched successfully)
            if (root.has("containerId")) {
                String containerId = root.get("containerId").asText();
                log.info("Agent launched with container ID: {}", containerId);

                // In a real implementation, you would:
                // 1. Wait for the agent to complete
                // 2. Fetch results using the container ID
                // 3. Parse the actual LinkedIn data

                // For now, return mock data
                return createMockData(request);
            }

            // If response contains actual data, parse it
            if (root.isArray()) {
                for (JsonNode node : root) {
                    AlumniProfile profile = parseProfileNode(node, request.getUniversity());
                    if (profile != null) {
                        profiles.add(profile);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error parsing PhantomBuster response", e);
        }

        return profiles;
    }

    private AlumniProfile parseProfileNode(JsonNode node, String university) {
        try {
            AlumniProfile profile = new AlumniProfile();

            profile.setName(getStringValue(node, "name", "fullName"));
            profile.setCurrentRole(getStringValue(node, "job", "title", "occupation"));
            profile.setUniversity(university);
            profile.setLocation(getStringValue(node, "location", "region"));
            profile.setLinkedinHeadline(getStringValue(node, "headline", "description"));

            // Try to extract passout year
            String yearStr = getStringValue(node, "graduationYear", "year");
            if (yearStr != null && !yearStr.isEmpty()) {
                try {
                    profile.setPassoutYear(Integer.parseInt(yearStr));
                } catch (NumberFormatException e) {
                    log.warn("Could not parse year: {}", yearStr);
                }
            }

            // Only return profile if it has a name
            if (profile.getName() != null && !profile.getName().trim().isEmpty()) {
                return profile;
            }

        } catch (Exception e) {
            log.error("Error parsing profile node", e);
        }

        return null;
    }

    private String getStringValue(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.path(fieldName);
            if (!field.isMissingNode() && !field.asText().trim().isEmpty()) {
                return field.asText().trim();
            }
        }
        return null;
    }
}