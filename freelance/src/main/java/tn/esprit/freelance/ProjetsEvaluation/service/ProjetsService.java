package tn.esprit.freelance.ProjetsEvaluation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.ProjetsEvaluation.dto.ProjetsDto;
import tn.esprit.freelance.ProjetsEvaluation.repository.ProjetsRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjetsService {
    private final ProjetsRepository repo;

    public List<ProjetsDto> list() {
        return repo.findAll();
    }

    public ProjetsDto create(ProjetsDto dto) {
        return repo.create(dto);
    }

    public void delete(String id) {
        repo.delete(id);
    }

    public List<ProjetsDto> findSimilarProjects(String projectId) {
        return repo.findSimilarProjects(projectId);
    }

    public Map<String, Object> getProjectStats() {
        return repo.getProjectStats();
    }
    // service/ProjetsService.java
    public void linkEvaluationToProject(String projectId, String evalId) {
        repo.linkEvaluationToProject(projectId, evalId);
    }

}

