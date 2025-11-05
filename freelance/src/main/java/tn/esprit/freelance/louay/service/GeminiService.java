package tn.esprit.freelance.louay.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static final String API_KEY = "AIzaSyB4oT88ugpHFkx7saXel0eiAulUeF_RgXU";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String translateToSparql(String naturalQuery) {
        try {
            if (naturalQuery == null || naturalQuery.trim().isEmpty()) {
                System.err.println("⚠️ Requête naturelle vide !");
                return getGenericSparql(); // Correction ici
            }

            System.out.println("🚀 Appel de Gemini 2.5 Flash...");
            System.out.println("📝 Requête naturelle: " + naturalQuery);

            // Prompt plus court et plus direct
            String prompt = createOptimizedPrompt(naturalQuery);

            // Construction de la requête
            Map<String, Object> requestBody = createRequestBody(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            String urlWithKey = GEMINI_API_URL + "?key=" + API_KEY;

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Appel API
            ResponseEntity<String> response = restTemplate.postForEntity(urlWithKey, request, String.class);

            System.out.println("📨 Statut réponse: " + response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();

                String sparql = extractSparqlFromResponse(responseBody);
                if (sparql != null && !sparql.trim().isEmpty()) {
                    System.out.println("🎯 SPARQL généré avec succès!");
                    return sparql;
                } else {
                    System.err.println("⚠️ SPARQL non extrait - raison possible: MAX_TOKENS");
                }
            } else {
                System.err.println("❌ Erreur HTTP: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("💥 Exception: " + e.getMessage());
        }

        System.out.println("🔄 Utilisation du SPARQL intelligent");
        return getIntelligentFallbackSparql(naturalQuery);
    }

    private String createOptimizedPrompt(String naturalQuery) {
        // Prompt beaucoup plus court pour éviter MAX_TOKENS
        return String.format("""
            Convertir en SPARQL:
            Préfixes: PREFIX : <http://example.com/freelance#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
            Ontologie: Freelancer(rdfs:label, :experienceLevel), Skill(rdfs:label, :skillLevel), relation :hasSkill
            Variables: ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
            Utiliser OPTIONAL pour skills.
            
            Retourne uniquement la requête SPARQL.
            
            Phrase: "%s"
            """, naturalQuery.trim());
    }

    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();

        // Contents
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        // Configuration optimisée pour éviter MAX_TOKENS
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.1);
        generationConfig.put("maxOutputTokens", 512);  // Réduit pour éviter MAX_TOKENS
        generationConfig.put("topK", 20);
        generationConfig.put("topP", 0.8);
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    private String extractSparqlFromResponse(String responseBody) {
        try {
            JsonNode json = mapper.readTree(responseBody);

            // Vérifier si c'est une erreur MAX_TOKENS
            if (json.has("candidates") && json.get("candidates").isArray()) {
                JsonNode firstCandidate = json.get("candidates").get(0);
                if (firstCandidate != null) {
                    String finishReason = firstCandidate.path("finishReason").asText();

                    if ("MAX_TOKENS".equals(finishReason)) {
                        System.err.println("❌ Limite de tokens atteinte (MAX_TOKENS)");
                        return null;
                    }

                    if (firstCandidate.has("content")) {
                        JsonNode content = firstCandidate.get("content");
                        if (content.has("parts") && content.get("parts").isArray()) {
                            JsonNode firstPart = content.get("parts").get(0);
                            if (firstPart.has("text")) {
                                String text = firstPart.get("text").asText();
                                return cleanSparql(text);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur extraction: " + e.getMessage());
        }
        return null;
    }

    private String cleanSparql(String sparql) {
        if (sparql == null) return null;

        return sparql
                .replace("```sparql", "")
                .replace("```", "")
                .replaceAll("(?i)sparql query:", "")
                .replaceAll("(?i)query:", "")
                .trim();
    }

    private String getIntelligentFallbackSparql(String naturalQuery) {
        // SPARQL intelligent basé sur la requête naturelle
        String lowerQuery = naturalQuery.toLowerCase();

        if (lowerQuery.contains(".net") || lowerQuery.contains("c#")) {
            return """
                PREFIX : <http://example.com/freelance#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                
                SELECT ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
                WHERE {
                  ?freelancer a :Freelancer ;
                             rdfs:label ?name ;
                             :experienceLevel ?exp .
                  ?freelancer :hasSkill ?skill .
                  ?skill rdfs:label ?skillName .
                  FILTER (regex(?skillName, "\\\\.net|C#", "i"))
                  OPTIONAL { ?skill :skillLevel ?skillLevel . }
                }
                ORDER BY ?name
                """;
        }

        if (lowerQuery.contains("expert") || lowerQuery.contains("senior")) {
            return """
                PREFIX : <http://example.com/freelance#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                
                SELECT ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
                WHERE {
                  ?freelancer a :Freelancer ;
                             rdfs:label ?name ;
                             :experienceLevel ?exp .
                  FILTER (regex(?exp, "expert|senior", "i"))
                  OPTIONAL {
                    ?freelancer :hasSkill ?skill .
                    ?skill rdfs:label ?skillName .
                    OPTIONAL { ?skill :skillLevel ?skillLevel . }
                  }
                }
                ORDER BY ?name
                """;
        }

        if (lowerQuery.contains("java") || lowerQuery.contains("python") ||
                lowerQuery.contains("javascript") || lowerQuery.contains("react")) {

            String technology = extractTechnology(lowerQuery);
            return String.format("""
                PREFIX : <http://example.com/freelance#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                
                SELECT ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
                WHERE {
                  ?freelancer a :Freelancer ;
                             rdfs:label ?name ;
                             :experienceLevel ?exp .
                  ?freelancer :hasSkill ?skill .
                  ?skill rdfs:label ?skillName .
                  FILTER (regex(?skillName, "%s", "i"))
                  OPTIONAL { ?skill :skillLevel ?skillLevel . }
                }
                ORDER BY ?name
                """, technology);
        }

        // Fallback générique
        return getGenericSparql();
    }

    private String extractTechnology(String query) {
        if (query.contains("java")) return "java";
        if (query.contains("python")) return "python";
        if (query.contains("javascript")) return "javascript";
        if (query.contains("react")) return "react";
        if (query.contains("angular")) return "angular";
        if (query.contains("vue")) return "vue";
        if (query.contains("php")) return "php";
        if (query.contains("sql")) return "sql";
        return ".*"; // Recherche générique
    }

    // AJOUT DE LA MÉTHODE MANQUANTE
    private String getGenericSparql() {
        return """
            PREFIX : <http://example.com/freelance#>
            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
            
            SELECT ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
            WHERE {
              ?freelancer a :Freelancer ;
                         rdfs:label ?name ;
                         :experienceLevel ?exp .
              OPTIONAL {
                ?freelancer :hasSkill ?skill .
                ?skill rdfs:label ?skillName .
                OPTIONAL { ?skill :skillLevel ?skillLevel . }
              }
            }
            ORDER BY ?name
            """;
    }

    // Méthode pour tester différentes configurations
    public Map<String, Object> testWithDifferentConfigs(String query) {
        Map<String, Object> results = new HashMap<>();

        // Test avec différentes configurations de tokens
        int[] tokenLimits = {512, 1024, 2048};

        for (int tokens : tokenLimits) {
            try {
                String prompt = createOptimizedPrompt(query);
                Map<String, Object> requestBody = createRequestBody(prompt);

                // Ajuster la limite de tokens
                ((Map<String, Object>) requestBody.get("generationConfig")).put("maxOutputTokens", tokens);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String urlWithKey = GEMINI_API_URL + "?key=" + API_KEY;
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(urlWithKey, request, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    String sparql = extractSparqlFromResponse(response.getBody());
                    results.put("tokens_" + tokens, sparql != null ? "SUCCESS" : "MAX_TOKENS");
                } else {
                    results.put("tokens_" + tokens, "ERROR: " + response.getStatusCode());
                }

            } catch (Exception e) {
                results.put("tokens_" + tokens, "EXCEPTION: " + e.getMessage());
            }
        }

        return results;
    }
}