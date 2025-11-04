package tn.esprit.freelance.MissionApplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.MissionApplication.dto.Mission;
import tn.esprit.freelance.MissionApplication.service.SparqlService;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final SparqlService sparqlService;

    public MissionController(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    @GetMapping
    public List<Mission> getAll() {
        return sparqlService.getAllMissions();
    }

    @GetMapping("/search")
    public List<Mission> search(@RequestParam(required = false) String q,
                                @RequestParam(required = false) String status) {
        return sparqlService.searchMissions(q, status);
    }

    @PostMapping
    public ResponseEntity<Mission> add(@RequestBody Mission mission) {
        sparqlService.addMission(mission);
        return ResponseEntity.ok(mission);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody Mission mission) {
        mission.setId(id);
        sparqlService.updateMission(mission);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sparqlService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    // Safer variants that avoid path issues with full URIs containing '#'
    @PutMapping
    public ResponseEntity<Void> updateBody(@RequestBody Mission mission) {
        if (mission.getId() == null || mission.getId().isBlank()) return ResponseEntity.badRequest().build();
        sparqlService.updateMission(mission);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteByQuery(@RequestParam("id") String id) {
        sparqlService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }
}


