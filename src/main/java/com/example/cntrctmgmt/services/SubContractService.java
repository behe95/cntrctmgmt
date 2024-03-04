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

    /**
     * Add sub-contract to the database
     *
     * @param subContract Sub-contract to add
     * @return Saved sub-contract
     */
    public SubContract addSubContract(SubContract subContract) {
        return this.subContractRepository.save(subContract);
    }

    /**
     * Retrieve all the contracts under one contract
     *
     * @param contract Contract to retrieve its children sub-contract
     * @return Retreived sub-contracts
     */
    public List<SubContract> getAllSubContractsByContract(Contract contract) {
        return this.subContractRepository.findAllByContract(contract);
    }

    /**
     * Retrieve sub-contract by id
     *
     * @param id ID to retrieve the sun-contract
     * @return Retrieved sub-contract
     */
    public Optional<SubContract> getSubContractById(int id) {
        return this.subContractRepository.findById(id);
    }

    /**
     * Save updated sub-contract
     *
     * @param subContract Sub-contract to update
     */
    @Transactional
    public void updateSubContract(SubContract subContract) {
        this.subContractRepository.findById(subContract.getId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        this.subContractRepository.save(subContract);

    }

    /**
     * Delete sub-contract from the database
     *
     * @param subContract Sub-contract to delete
     */
    @Transactional
    public void deleteSubContract(SubContract subContract) {
        this.subContractRepository.delete(subContract);
    }

    /**
     * Delete all the sub-contracts from the database
     */
    @Transactional
    public void deleteAllSubContract() {
        this.subContractRepository.deleteAll();
    }

    /**
     * Delete multiple sub-contracts at once
     *
     * @param subContracts List of sub-contracts to delete
     */
    @Transactional
    public void deleteSubContracts(List<SubContract> subContracts) {
        this.subContractRepository.deleteAll(subContracts);
    }


}
