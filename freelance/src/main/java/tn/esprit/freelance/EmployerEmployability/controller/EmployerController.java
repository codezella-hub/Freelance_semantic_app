package tn.esprit.freelance.EmployerEmployability.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.EmployerEmployability.dto.EmployerDto;
import tn.esprit.freelance.EmployerEmployability.repository.EmployerRepository;
import tn.esprit.freelance.EmployerEmployability.service.EmployerService;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {
    private final EmployerService service;

    @GetMapping
    public List<EmployerDto> all() { return service.list(); }

    @GetMapping("/{id}")
    public ResponseEntity<EmployerDto> getById(@PathVariable String id) {
        EmployerDto d = service.getById(id);
        return (d != null) ? ResponseEntity.ok(d) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public EmployerDto create(@RequestBody EmployerDto dto) { return service.create(dto); }

    @PutMapping("/{id}")
    public ResponseEntity<EmployerDto> update(@PathVariable String id, @RequestBody EmployerDto dto) {
        EmployerDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public List<EmployerDto> top(@RequestParam(defaultValue = "10") int limit) {
        return service.topByScore(limit);
    }


    @GetMapping("/by-score")
    public List<EmployerDto> byScore(@RequestParam(name = "min", defaultValue = "80") double min) {
        return service.byMinScore(min);
    }

    @GetMapping("/avg-score-by-type")
    public List<EmployerRepository.TypeAverage> avgByType() {
        return service.avgScoreByType();
    }

    @GetMapping("/infer/high-potential")
    public ResponseEntity<String> inferHighPotentialEmployers() {
        service.inferHighPotentialEmployers();
        return ResponseEntity.ok("Inférence appliquée : les employeurs à haut potentiel ont été ajoutés !");
    }

    @GetMapping("/infer/low-potential")
    public ResponseEntity<String> inferLowPotentialEmployers() {
        service.inferLowPotentialEmployers();
        return ResponseEntity.ok("Inférence appliquée : les employeurs à faible potentiel ont été ajoutés !");
    }
    @GetMapping("/high-potential")
    public List<EmployerDto> getHighPotentialEmployers() {
        return service.findEmployersByTypeClass("HighPotentialEmployer");
    }

    @GetMapping("/low-potential")
    public List<EmployerDto> getLowPotentialEmployers() {
        return service.findEmployersByTypeClass("LowPotentialEmployer");
    }


}
