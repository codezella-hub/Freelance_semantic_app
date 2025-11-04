// service/EvaluationService.java
package tn.esprit.freelance.ProjetsEvaluation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.ProjetsEvaluation.dto.EvaluationDto;
import tn.esprit.freelance.ProjetsEvaluation.repository.EvaluationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository repo;
    public List<EvaluationDto> list() { return repo.findAll(); }
    public EvaluationDto create(EvaluationDto d) { return repo.create(d); }
    public void delete(String id) { repo.delete(id); }
    public List<EvaluationDto> search(String keyword) {
        return repo.search(keyword);
    }

    public List<EvaluationDto> sort(String sortBy, String order) {
        return repo.sort(sortBy, order);
    }
}
