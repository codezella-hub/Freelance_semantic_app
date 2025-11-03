package tn.esprit.freelance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.service.NaturalLanguageQueryService;

import java.util.Map;

@RestController
@RequestMapping("/api/nlp")
@CrossOrigin(origins = "http://localhost:4200")
public class NaturalLanguageQueryController {

    @Autowired
    private NaturalLanguageQueryService nlpService;

    /**
     * Endpoint pour traiter les requêtes en langage naturel
     * 
     * Exemples de requêtes supportées:
     * - "donne moi les événements premium"
     * - "liste les événements de formation"
     * - "montre moi les événements publics"
     * - "trouve les événements AWS"
     * - "donne moi les certifications formelles"
     * - "liste les certifications valides"
     * - "montre moi les certifications expirées"
     * - "trouve les certifications Docker"
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> processQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "La requête ne peut pas être vide"
            ));
        }
        
        Map<String, Object> response = nlpService.processNaturalLanguageQuery(query);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour obtenir des exemples de requêtes
     */
    @GetMapping("/examples")
    public ResponseEntity<Map<String, Object>> getExamples() {
        return ResponseEntity.ok(Map.of(
            "eventExamples", new String[]{
                "donne moi les événements premium",
                "liste les événements publics",
                "montre moi les formations",
                "trouve les ateliers",
                "donne moi les conférences",
                "liste les workshops",
                "montre moi les événements AWS",
                "trouve les événements Docker",
                "donne moi les événements Kubernetes",
                "liste les événements React",
                "montre moi les événements Angular",
                "trouve les événements Python",
                "donne moi les événements de sécurité"
            },
            "certificationExamples", new String[]{
                "donne moi les certifications formelles",
                "liste les certifications informelles",
                "montre moi les certifications valides",
                "trouve les certifications expirées",
                "donne moi les certifications AWS",
                "liste les certifications Docker",
                "montre moi les certifications Microsoft",
                "trouve les certifications Google",
                "donne moi les certifications Kubernetes",
                "liste les certifications React",
                "montre moi les certifications Angular",
                "trouve les certifications Python",
                "donne moi les certifications de sécurité"
            }
        ));
    }
}

