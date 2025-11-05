package tn.esprit.freelance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.freelance.entity.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    // Add custom queries if needed
}