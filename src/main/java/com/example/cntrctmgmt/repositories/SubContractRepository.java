package com.example.cntrctmgmt.repositories;

import com.example.cntrctmgmt.entities.SubContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubContractRepository extends JpaRepository<SubContract, Integer> {
}
