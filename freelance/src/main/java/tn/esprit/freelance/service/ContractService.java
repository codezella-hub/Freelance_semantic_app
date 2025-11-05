package tn.esprit.freelance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.entity.Contract;
import tn.esprit.freelance.repository.ContractRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ContractService {
    
    @Autowired
    private ContractRepository contractRepository;

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Optional<Contract> getContractById(Long id) {
        return contractRepository.findById(id);
    }

    public Contract createContract(Contract contract) {
        return contractRepository.save(contract);
    }

    public Contract updateContract(Long id, Contract contract) {
        if (contractRepository.existsById(id)) {
            contract.setId(id);
            return contractRepository.save(contract);
        }
        return null;
    }

    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }
}