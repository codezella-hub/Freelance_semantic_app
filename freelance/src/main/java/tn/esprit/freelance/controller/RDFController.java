package tn.esprit.freelance.controller;

import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.service.RDFService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for RDF/SPARQL operations
 */
@RestController
@RequestMapping("/api/rdf")
@CrossOrigin(origins = "http://localhost:4200")
public class RDFController {

    @Autowired
    private RDFService rdfService;

    /**
     * Test Fuseki connection
     * @return Connection status
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isConnected = rdfService.testConnection();
            response.put("connected", isConnected);
            response.put("message", isConnected ? "Successfully connected to Fuseki" : "Failed to connect to Fuseki");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("connected", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get count of triples in the dataset
     * @return Triple count
     */
    @GetMapping("/triple-count")
    public ResponseEntity<Map<String, Object>> getTripleCount() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = rdfService.getTripleCount();
            response.put("count", count);
            response.put("message", "Total triples in dataset");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Execute a custom SPARQL SELECT query
     * @param query The SPARQL query
     * @return Query results
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String query = request.get("query");
            if (query == null || query.trim().isEmpty()) {
                response.put("error", "Query parameter is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Map<String, String>> results = rdfService.executeSelectQuery(query);
            response.put("results", results);
            response.put("count", results.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all freelancers
     * @return List of freelancers
     */
    @GetMapping("/freelancers")
    public ResponseEntity<Map<String, Object>> getAllFreelancers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, String>> freelancers = rdfService.getAllFreelancers();
            response.put("freelancers", freelancers);
            response.put("count", freelancers.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all projects
     * @return List of projects
     */
    @GetMapping("/projects")
    public ResponseEntity<Map<String, Object>> getAllProjects() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, String>> projects = rdfService.getAllProjects();
            response.put("projects", projects);
            response.put("count", projects.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all skills
     * @return List of skills
     */
    @GetMapping("/skills")
    public ResponseEntity<Map<String, Object>> getAllSkills() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, String>> skills = rdfService.getAllSkills();
            response.put("skills", skills);
            response.put("count", skills.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get freelancers by skill
     * @param skillName The skill name
     * @return List of freelancers with the skill
     */
    @GetMapping("/freelancers/by-skill/{skillName}")
    public ResponseEntity<Map<String, Object>> getFreelancersBySkill(@PathVariable String skillName) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, String>> freelancers = rdfService.getFreelancersBySkill(skillName);
            response.put("freelancers", freelancers);
            response.put("count", freelancers.size());
            response.put("skill", skillName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Execute a SPARQL UPDATE query
     * @param request Map containing the update query
     * @return Success message
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> executeUpdate(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String updateQuery = request.get("update");
            if (updateQuery == null || updateQuery.trim().isEmpty()) {
                response.put("error", "Update parameter is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            rdfService.executeUpdate(updateQuery);
            response.put("message", "Update executed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Execute a SPARQL ASK query
     * @param request Map containing the ask query
     * @return Boolean result
     */
    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> executeAsk(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String askQuery = request.get("query");
            if (askQuery == null || askQuery.trim().isEmpty()) {
                response.put("error", "Query parameter is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean result = rdfService.executeAskQuery(askQuery);
            response.put("result", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Execute a SPARQL CONSTRUCT query
     * @param request Map containing the construct query
     * @return Model as JSON-LD
     */
    @PostMapping("/construct")
    public ResponseEntity<Map<String, Object>> executeConstruct(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String constructQuery = request.get("query");
            if (constructQuery == null || constructQuery.trim().isEmpty()) {
                response.put("error", "Query parameter is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            Model model = rdfService.executeConstructQuery(constructQuery);
            String jsonLd = rdfService.modelToJsonLd(model);
            response.put("model", jsonLd);
            response.put("tripleCount", model.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

