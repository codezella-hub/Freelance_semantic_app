// controller/Projets.java
package tn.esprit.freelance.ProjetsEvaluation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.ProjetsEvaluation.dto.ProjetsDto;
import tn.esprit.freelance.ProjetsEvaluation.service.ProjetsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
@CrossOrigin
public class Projets {
    private final ProjetsService service;

    @GetMapping
    public List<ProjetsDto> all() {
        return service.list();
    }

    @PostMapping
    public ProjetsDto create(@RequestBody ProjetsDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    // 🔍 Récupérer les projets similaires à un projet donné
    @GetMapping("/{id}/similar")
    public List<ProjetsDto> getSimilarProjects(@PathVariable String id) {
        return service.findSimilarProjects(id);
    }

    // 📊 Obtenir des statistiques globales sur les projets
    @GetMapping("/stats")
    public Map<String, Object> getProjectStats() {
        return service.getProjectStats();
    }
    // controller/Projets.java
    @PostMapping("/{projectId}/link-evaluation/{evalId}")
    public ResponseEntity<Void> linkEvaluation(
            @PathVariable String projectId,
            @PathVariable String evalId) {
        service.linkEvaluationToProject(projectId, evalId);
        return ResponseEntity.ok().build();
    }



}
