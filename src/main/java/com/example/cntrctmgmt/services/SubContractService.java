package com.example.cntrctmgmt.services;


import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Contract;
import com.example.cntrctmgmt.entities.SubContract;
import com.example.cntrctmgmt.repositories.SubContractRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubContractService {
    private final SubContractRepository subContractRepository;

    @Autowired
    public SubContractService(SubContractRepository subContractRepository) {
        this.subContractRepository = subContractRepository;
    }

    public SubContract addSubContract(SubContract subContract) {
        return this.subContractRepository.save(subContract);
    }

    public List<SubContract> getAllSubContractsByContract(Contract contract) {
        return this.subContractRepository.findAllByContract(contract);
    }

    public Optional<SubContract> getSubContractById(int id) {
        return this.subContractRepository.findById(id);
    }

    @Transactional
    public void updateSubContract(SubContract subContract) {
         this.subContractRepository.findById(subContract.getId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        this.subContractRepository.save(subContract);

    }

    @Transactional
    public void deleteSubContract(SubContract subContract) {
        this.subContractRepository.delete(subContract);
    }

    @Transactional
    public void deleteAllSubContract() {
        this.subContractRepository.deleteAll();
    }

    @Transactional
    public void deleteSubContracts(List<SubContract> subContracts) {
        this.subContractRepository.deleteAll(subContracts);
    }


}
