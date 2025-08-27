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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private String apiUrl; // should be https://api.phantombuster.com/api/v2/agents/launch

    @Value("${phantombuster.agent.id}")
    private String agentId;

    @Value("${phantombuster.session.cookie}")
    private String sessionCookie;

    @Value("${phantombuster.user.agent}")
    private String userAgent;

    public List<AlumniProfile> fetchAlumniProfiles(AlumniSearchRequest request) {
        List<AlumniProfile> profiles = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Phantombuster-Key-1", apiKey);

            // ✅ Use keywords, not LinkedIn URL
            StringBuilder keywords = new StringBuilder();
            if (request.getDesignation() != null) keywords.append(request.getDesignation()).append(" ");
            if (request.getUniversity() != null) keywords.append(request.getUniversity()).append(" ");
            if (request.getPassoutYear() != null) keywords.append(request.getPassoutYear());

            String searchQuery = keywords.toString().trim();

            // ✅ Body must have "argument" (singular), not "arguments"
            Map<String, Object> argument = new HashMap<>();
            argument.put("search", searchQuery);
            argument.put("sessionCookie", sessionCookie);
            argument.put("userAgent", userAgent);
            argument.put("numberOfProfiles", 20);

            Map<String, Object> body = new HashMap<>();
            body.put("id", agentId);
            body.put("argument", argument);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            log.info("Launching Phantom with query: {}", searchQuery);

            String response = restTemplate.postForObject(apiUrl, entity, String.class);
            log.info("Phantom launch response: {}", response);

        } catch (Exception e) {
            log.error("Error calling PhantomBuster API", e);
        }

        return profiles;
    }
}