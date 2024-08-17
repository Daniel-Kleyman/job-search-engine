package com.danielkleyman.jobsearchlnk.gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {


    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIService(RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
        this.apiUrl = System.getenv("API_URL");
        this.apiKey = System.getenv("API_KEY");
    }

    public int getResponse(String input) {
        // Create the prompt string
        String rolePrompt = Constants.task1 + Constants.CV + Constants.task2;
        HttpEntity<String> requestEntity = new HttpEntity<>(getRequestBody(rolePrompt, input), getHeaders());
        // Make the API call
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
        // Check if the response is successful
        if (response.getStatusCode() == HttpStatus.OK) {
            // Extract the body as a String
            String responseBody = response.getBody();
            // Parse the response body to an integer
            try {
                // Parse the JSON response to extract the content field
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode contentNode = rootNode.path("choices").get(0).path("message").path("content");
                String contentValue = contentNode.asText();
                return Integer.parseInt(contentValue); // This is the return statement
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse response body to integer: " + responseBody, e);
            }
        } else {
            throw new RuntimeException("API request failed with status: " + response.getStatusCode());
        }
    }

    private String getRequestBody(String rolePrompt, String input) {
        // Create request body
        return String.format(
                "{\"model\": \"gpt-4o-mini\", \"messages\": [" +
                        "{\"role\": \"system\", \"content\": \"%s\"}, " +
                        "{\"role\": \"user\", \"content\": \"%s\"}]" +
                        "}",
                escapeJson(rolePrompt),
                escapeJson(input)
        );
    }

    private HttpHeaders getHeaders() {
        // Construct request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
