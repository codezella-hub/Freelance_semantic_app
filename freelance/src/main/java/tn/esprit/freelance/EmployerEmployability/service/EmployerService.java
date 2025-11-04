package tn.esprit.freelance.EmployerEmployability.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.EmployerEmployability.dto.EmployerDto;
import tn.esprit.freelance.EmployerEmployability.repository.EmployerRepository;

import java.util.List;

@Service @RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository repo;
    public List<EmployerDto> list() { return repo.findAll(); }
    public EmployerDto create(EmployerDto d) { return repo.create(d); }
    public EmployerDto getById(String id) { return repo.findById(id); }
    public EmployerDto update(String id, EmployerDto d) { return repo.update(id, d); }
    public void delete(String id) { repo.delete(id); }

    public List<EmployerDto> topByScore(int limit) {
        return repo.findTopByScore(limit);
    }

    public List<EmployerDto> byMinScore(double minScore) {
        return repo.findByMinScore(minScore);
    }

    public List<EmployerRepository.TypeAverage> avgScoreByType() {
        return repo.avgScoreByEmployerType();
    }


    public void inferHighPotentialEmployers() {
        repo.inferHighPotentialEmployers();
    }
    public void inferLowPotentialEmployers() {
        repo.inferLowPotentialEmployers();
    }
    public List<EmployerDto> findEmployersByTypeClass(String typeClass) {
        return repo.findEmployersByTypeClass(typeClass);
    }


}
