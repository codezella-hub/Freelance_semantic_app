package tn.esprit.freelance.louay.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.louay.dto.FreelancerDTO;
import tn.esprit.freelance.louay.dto.StatsDTO;
import tn.esprit.freelance.louay.service.FreelancerService;
import tn.esprit.freelance.louay.service.GeminiService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/freelancers")
@AllArgsConstructor
public class FreelancerController {

  FreelancerService service;


 GeminiService geminiService;



    @PostMapping
    public FreelancerDTO add(@RequestBody FreelancerDTO dto) {
        return service.add(dto);
    }

    @GetMapping
    public List<FreelancerDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<FreelancerDTO> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody FreelancerDTO dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
    @GetMapping("/search")
    public List<FreelancerDTO> search(@RequestParam String query) {
        return service.searchSemantic(query);
    }
    @GetMapping("/stats")
    public StatsDTO getStats() {
        return service.getStats();
    }

    @GetMapping("/stats/experience-level")
    public Map<String, Long> getExperienceLevelStats() {
        return service.getExperienceLevelStats();
    }

    @GetMapping("/stats/skills")
    public Map<String, Long> getSkillStats() {
        return service.getSkillStats();
    }

    @GetMapping("/stats/skill-levels")
    public Map<String, Long> getSkillLevelStats() {
        return service.getSkillLevelStats();
    }
}
