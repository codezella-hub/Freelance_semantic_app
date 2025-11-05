package tn.esprit.freelance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class AIService {
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String analyzePricing(String description, String skills, int duration) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            
            String prompt = String.format(
                "As an AI expert in freelance pricing, analyze this project:" +
                "\nDescription: %s" +
                "\nRequired Skills: %s" +
                "\nDuration (days): %d" +
                "\nProvide a price range recommendation in USD and brief explanation based on market rates.",
                description, skills, duration
            );

            ObjectNode message = mapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            requestBody.putArray("messages").add(message);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                entity,
                JsonNode.class
            );

            return response.getBody()
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();
        } catch (Exception e) {
            return "Error analyzing pricing: " + e.getMessage();
        }
    }
    
    public String improveDescription(String description) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            
            String prompt = String.format(
                "As an AI expert in freelance project descriptions, improve this project description to be more clear, professional, and detailed:\n%s",
                description
            );

            ObjectNode message = mapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            requestBody.putArray("messages").add(message);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                entity,
                JsonNode.class
            );

            return response.getBody()
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();
        } catch (Exception e) {
            return "Error improving description: " + e.getMessage();
        }
    }
}