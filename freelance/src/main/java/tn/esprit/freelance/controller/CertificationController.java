package tn.esprit.freelance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.dto.CertificationDTO;
import tn.esprit.freelance.service.CertificationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Certification CRUD operations
 */
@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "http://localhost:4200")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    /**
     * Get all certifications
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCertifications() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CertificationDTO> certifications = certificationService.getAllCertifications();
            response.put("certifications", certifications);
            response.put("count", certifications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get certification by URI (using query parameter to avoid URL encoding issues)
     */
    @GetMapping("/by-uri")
    public ResponseEntity<Map<String, Object>> getCertificationByUri(@RequestParam String uri) {
        Map<String, Object> response = new HashMap<>();
        try {
            CertificationDTO certification = certificationService.getCertificationByUri(uri);

            if (certification == null) {
                response.put("error", "Certification not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("certification", certification);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new certification
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCertification(@RequestBody CertificationDTO certificationDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            CertificationDTO createdCertification = certificationService.createCertification(certificationDTO);
            response.put("certification", createdCertification);
            response.put("message", "Certification created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update an existing certification (using query parameter)
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateCertification(
            @RequestParam String uri,
            @RequestBody CertificationDTO certificationDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            CertificationDTO updatedCertification = certificationService.updateCertification(uri, certificationDTO);
            response.put("certification", updatedCertification);
            response.put("message", "Certification updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a certification (using query parameter)
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteCertification(@RequestParam String uri) {
        Map<String, Object> response = new HashMap<>();
        try {
            certificationService.deleteCertification(uri);
            response.put("message", "Certification deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Search certifications by name
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCertifications(@RequestParam String name) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CertificationDTO> certifications = certificationService.searchCertificationsByName(name);
            response.put("certifications", certifications);
            response.put("count", certifications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get certifications by issuer
     */
    @GetMapping("/issuer/{issuer}")
    public ResponseEntity<Map<String, Object>> getCertificationsByIssuer(@PathVariable String issuer) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CertificationDTO> certifications = certificationService.getCertificationsByIssuer(issuer);
            response.put("certifications", certifications);
            response.put("count", certifications.size());
            response.put("issuer", issuer);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

