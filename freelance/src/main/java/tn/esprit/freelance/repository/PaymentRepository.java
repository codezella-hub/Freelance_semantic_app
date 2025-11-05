package tn.esprit.freelance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.freelance.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Add custom queries if needed
}