package tn.esprit.freelance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.service.AIService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/analyze-pricing")
    public ResponseEntity<String> analyzePricing(
            @RequestParam String description,
            @RequestParam String skills,
            @RequestParam int duration) {
        String analysis = aiService.analyzePricing(description, skills, duration);
        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/improve-description")
    public ResponseEntity<String> improveDescription(
            @RequestParam String description) {
        String improved = aiService.improveDescription(description);
        return ResponseEntity.ok(improved);
    }
}