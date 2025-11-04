package tn.esprit.freelance.EmployerEmployability.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.EmployerEmployability.dto.EmployabilityDto;
import tn.esprit.freelance.EmployerEmployability.service.EmployabilityService;

import java.util.List;

@RestController
@RequestMapping("/api/employability")
@RequiredArgsConstructor

public class EmployabilityController {
    private final EmployabilityService service;

    @GetMapping
    public List<EmployabilityDto> all() { return service.list(); }

    @GetMapping("/{id}")
    public ResponseEntity<EmployabilityDto> getById(@PathVariable String id) {
        EmployabilityDto d = service.getById(id);
        return (d != null) ? ResponseEntity.ok(d) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public EmployabilityDto create(@RequestBody EmployabilityDto dto) { return service.create(dto); }

    @PutMapping("/{id}")
    public ResponseEntity<EmployabilityDto> update(@PathVariable String id, @RequestBody EmployabilityDto dto) {
        EmployabilityDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
