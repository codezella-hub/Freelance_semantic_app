package tn.esprit.freelance.EmployerEmployability.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.EmployerEmployability.dto.EmployabilityDto;
import tn.esprit.freelance.EmployerEmployability.repository.EmployabilityRepository;

import java.util.List;

@Service @RequiredArgsConstructor
public class EmployabilityService {
    private final EmployabilityRepository repo;
    public List<EmployabilityDto> list() { return repo.findAll(); }
    public EmployabilityDto create(EmployabilityDto d) { return repo.create(d); }
    public EmployabilityDto getById(String id) { return repo.findById(id); }
    public EmployabilityDto update(String id, EmployabilityDto d) { return repo.update(id, d); }
    public void delete(String id) { repo.delete(id); }
}
