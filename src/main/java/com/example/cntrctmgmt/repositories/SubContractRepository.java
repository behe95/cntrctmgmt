package com.example.cntrctmgmt.repositories;

import com.example.cntrctmgmt.constant.db.DBSubContractConst;
import com.example.cntrctmgmt.entities.Contract;
import com.example.cntrctmgmt.entities.SubContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubContractRepository extends JpaRepository<SubContract, Integer> {
    /**
     * Retrieve all the sub-contracts under one contract
     * @param contract  Contract that is used to retrieve its children sub-contract
     * @return  Retrieved sub-contracts
     */
    List<SubContract> findAllByContract(Contract contract);
}
