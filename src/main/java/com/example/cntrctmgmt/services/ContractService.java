package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.Contract;
import com.example.cntrctmgmt.repositories.ContractRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractService {


    private final ContractRepository contractRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public Contract addContract(Contract contract) {
        return this.contractRepository.save(contract);
    }

    public List<Contract> getAllContracts() {return this.contractRepository.findAll();}

    public Optional<Contract> getContractById(int id) {
        return this.contractRepository.findById(id);
    }

    public void updateContract(Contract contract) throws EntityNotFoundException {
        Contract savedContract = this.contractRepository.findById(contract.getId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        savedContract.setTitle(contract.getTitle());

        this.contractRepository.save(savedContract);

    }

    @Transactional
    public void deleteContract(Contract contract) {
        this.contractRepository.delete(contract);
    }

    @Transactional
    public void deleteAllContracts() {
        this.contractRepository.deleteAll();
    }

    @Transactional
    public void deleteContracts(List<Contract> contracts) {
        this.contractRepository.deleteAll(contracts);
    }


}
