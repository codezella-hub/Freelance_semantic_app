package tn.esprit.freelance.MissionApplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.MissionApplication.dto.ApplicationDto;
import tn.esprit.freelance.MissionApplication.service.SparqlService;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final SparqlService sparqlService;

    public ApplicationController(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    @GetMapping
    public List<ApplicationDto> getAll() {
        return sparqlService.getAllApplications();
    }

    @GetMapping("/search")
    public List<ApplicationDto> search(@RequestParam(required = false) String status,
                                       @RequestParam(required = false) String missionUri) {
        return sparqlService.searchApplications(status, missionUri);
    }

    @PostMapping
    public ResponseEntity<ApplicationDto> add(@RequestBody ApplicationDto applicationDto) {
        sparqlService.addApplication(applicationDto);
        return ResponseEntity.ok(applicationDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody ApplicationDto applicationDto) {
        applicationDto.setId(id);
        sparqlService.updateApplication(applicationDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sparqlService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    // Safer variants avoiding path issues with full URIs
    @PutMapping
    public ResponseEntity<Void> updateBody(@RequestBody ApplicationDto dto) {
        if (dto.getId() == null || dto.getId().isBlank()) return ResponseEntity.badRequest().build();
        sparqlService.updateApplication(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteByQuery(@RequestParam("id") String id) {
        sparqlService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}


