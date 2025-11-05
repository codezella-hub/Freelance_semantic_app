package tn.esprit.freelance.louay.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OllamaService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String MODEL = "llama3.2:3b";

    public List<String> extractTechnologies(String naturalQuery) {
        try {
            System.out.println("🧠 Analyse Ollama: " + naturalQuery);

            String prompt = String.format("""
                Pour cette recherche de freelancers, identifie les technologies spécifiques demandées.
                Retourne UNIQUEMENT les noms de technologies séparés par des virgules, sans texte explicatif.
                
                Exemples:
                - "développeur js" → javascript,nodejs,react,angular,vue
                - "développeur java" → java,spring,spring boot
                - "développeur web" → javascript,html,css,react,angular
                
                Recherche: "%s"
                """, naturalQuery);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                JsonNode json = mapper.readTree(responseBody);
                String text = json.path("response").asText().trim();

                System.out.println("📝 Réponse Ollama brute: " + text);
                List<String> technologies = cleanAndParseTechnologies(text);
                System.out.println("🎯 Technologies nettoyées: " + technologies);
                return technologies;
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur Ollama: " + e.getMessage());
        }

        // Fallback simple
        return getSimpleFallback(naturalQuery);
    }

    private List<String> cleanAndParseTechnologies(String response) {
        List<String> technologies = new ArrayList<>();

        // Nettoyage agressif du texte
        String cleanText = response.toLowerCase()
                .replaceAll("[\"']", "") // Supprimer tous les guillemets
                .replaceAll("pour votre recherche", "")
                .replaceAll("voici les technologies", "")
                .replaceAll("spécifiques demandées", "")
                .replaceAll("technologies:", "")
                .replaceAll("[:]", ",")
                .replaceAll("[\\n\\r]", ",")
                .replaceAll("[.]", ",")
                .replaceAll("\\s+", " ") // Espaces multiples -> un seul
                .trim();

        System.out.println("🔧 Texte nettoyé: " + cleanText);

        // Extraire uniquement les parties après le dernier ":"
        String[] parts = cleanText.split(",");

        for (String part : parts) {
            String tech = part.trim();
            // Garder uniquement les termes techniques valides
            if (isValidTechnology(tech)) {
                technologies.add(tech);
            }
        }

        return technologies;
    }

    private boolean isValidTechnology(String tech) {
        if (tech.isEmpty() || tech.length() < 2) return false;

        // Liste des termes non techniques à exclure
        String[] invalidTerms = {
                "pour", "votre", "recherche", "voici", "les", "technologies",
                "spécifiques", "demandées", "développeur", "developer", "expert",
                "senior", "junior", "web", "mobile", "fullstack"
        };

        for (String invalid : invalidTerms) {
            if (tech.equals(invalid)) return false;
        }

        return true;
    }

    private List<String> getSimpleFallback(String query) {
        String lowerQuery = query.toLowerCase();
        List<String> technologies = new ArrayList<>();

        // Détection contextuelle simple
        if (lowerQuery.contains("js") || lowerQuery.contains("javascript")) {
            technologies.addAll(Arrays.asList("javascript", "nodejs", "react", "angular", "vue", "typescript"));
        }
        if (lowerQuery.contains("java") && !lowerQuery.contains("javascript")) {
            technologies.addAll(Arrays.asList("java", "spring", "spring boot"));
        }
        if (lowerQuery.contains("python")) {
            technologies.addAll(Arrays.asList("python", "django", "flask"));
        }
        if (lowerQuery.contains("react")) {
            technologies.addAll(Arrays.asList("react", "javascript"));
        }
        if (lowerQuery.contains("angular")) {
            technologies.addAll(Arrays.asList("angular", "typescript"));
        }
        if (lowerQuery.contains("node")) {
            technologies.addAll(Arrays.asList("nodejs", "javascript"));
        }
        if (lowerQuery.contains("web") && technologies.isEmpty()) {
            technologies.addAll(Arrays.asList("javascript", "html", "css", "react", "angular"));
        }

        // Si toujours vide, recherche générale
        if (technologies.isEmpty()) {
            technologies.addAll(Arrays.asList("java", "javascript", "python", "react", "angular"));
        }

        return technologies;
    }
}