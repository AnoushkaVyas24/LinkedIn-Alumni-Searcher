package com.anoushka.alumni_linkedin_searcher.service;

import com.anoushka.alumni_linkedin_searcher.dto.AlumniSearchRequest;
import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhantomBusterService {
    private static final Logger log = LoggerFactory.getLogger(PhantomBusterService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${phantombuster.api.key}")
    private String apiKey;

    @Value("${phantombuster.api.url}")
    private String apiUrl;

    //Calls PhantomBuster API with given input and returns alumni profiles
    public List<AlumniProfile> fetchAlumniProfiles(AlumniSearchRequest request){
        try {
            //build input payload:
            String payload = String.format(
                    "{ \"university\" : \"%s\", \"designation\": \"%s\", \"passoutYear\": %s }",
                    request.getUniversity(),
                    request.getDesignation(),
                    request.getPassoutYear() != null ? request.getPassoutYear() : "null"
            );

            //make request:
            var headers = new org.springframework.http.HttpHeaders();
            headers.set("X-Phantombuster-Key-1", apiKey);
            headers.set("Content-Type", "application/json");

            var httpEntity = new org.springframework.http.HttpEntity<>(payload, headers);
            var response = restTemplate.postForEntity(apiUrl, httpEntity, String.class);

            //Parse JSON response:
            JsonNode root = objectMapper.readTree(response.getBody());

            List<AlumniProfile> results = new ArrayList<>();

            //Adjust parsing depending on PhantomBuster’s actual JSON structure:
            for(JsonNode node: root.path("data")){
                AlumniProfile alumni = AlumniProfile.builder()
                        .name(node.path("name").asText())
                        .currentRole(node.path("currentRole").asText())
                        .university(node.path("university").asText())
                        .location(node.path("location").asText())
                        .linkedinHeadline(node.path("linkedinHeadline").asText())
                        .passoutYear(node.path("passoutYear").isInt() ? node.path("passoutYear").asInt() : null)
                        .build();

                results.add(alumni);
            }

            return results;
        } catch (Exception e) {
            log.error("PhantomBuster API call failed", e);
            throw new RuntimeException("Failed to fetch alumni profiles from PhantomBuster", e);
        }
    }
}