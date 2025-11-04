// controller/Evaluation.java
package tn.esprit.freelance.ProjetsEvaluation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.ProjetsEvaluation.dto.EvaluationDto;
import tn.esprit.freelance.ProjetsEvaluation.service.EvaluationService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@CrossOrigin
public class Evaluation {
    private final EvaluationService service;

    @GetMapping
    public List<EvaluationDto> all() { return service.list(); }

    @PostMapping
    public EvaluationDto create(@RequestBody EvaluationDto dto) { return service.create(dto); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    // 🔍 Recherche avancée (par mot-clé)
    @GetMapping("/search")
    public List<EvaluationDto> search(@RequestParam String keyword) {
        return service.search(keyword);
    }

    // 🔽 Tri dynamique (score, date, type)
    @GetMapping("/sort")
    public List<EvaluationDto> sort(
            @RequestParam(defaultValue = "score") String sortBy,
            @RequestParam(defaultValue = "asc") String order
    ) {
        return service.sort(sortBy, order);
    }
}
