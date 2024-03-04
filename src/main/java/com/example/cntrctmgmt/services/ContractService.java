package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
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

    /**
     * Add contract to the databse
     *
     * @param contract Contract to add
     * @return Saved contract.
     */
    public Contract addContract(Contract contract) {
        return this.contractRepository.save(contract);
    }

    /**
     * Get list of all contracts.
     *
     * @return List of contracts.
     */
    public List<Contract> getAllContracts() {
        return this.contractRepository.findAll();
    }

    /**
     * Retrieve contract by the ID
     *
     * @param id ID to retreive the contract
     * @return Retrieved contract
     */
    public Optional<Contract> getContractById(int id) {
        return this.contractRepository.findById(id);
    }

    /**
     * Save updated contract.
     *
     * @param contract Contract to update
     * @throws EntityNotFoundException If the category to update is not found in the database
     */
    public void updateContract(Contract contract) throws EntityNotFoundException {
        this.contractRepository.findById(contract.getId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));


        this.contractRepository.save(contract);

    }

    /**
     * Delete contract from the database
     *
     * @param contract
     */
    @Transactional
    public void deleteContract(Contract contract) {
        this.contractRepository.delete(contract);
    }

    /**
     * Delete all the contracts from the database
     */
    @Transactional
    public void deleteAllContracts() {
        this.contractRepository.deleteAll();
    }

    /**
     * Delete multiple contracts at once
     *
     * @param contracts List of contracts to delete
     */
    @Transactional
    public void deleteContracts(List<Contract> contracts) {
        this.contractRepository.deleteAll(contracts);
    }


}
