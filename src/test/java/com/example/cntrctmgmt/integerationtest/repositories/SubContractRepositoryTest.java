package com.example.cntrctmgmt.integerationtest.repositories;

import com.example.cntrctmgmt.TestConfig;
import com.example.cntrctmgmt.entities.Contract;
import com.example.cntrctmgmt.entities.SubContract;
import com.example.cntrctmgmt.entities.TransactionType;
import com.example.cntrctmgmt.repositories.ContractRepository;
import com.example.cntrctmgmt.repositories.SubContractRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback
class SubContractRepositoryTest {


    @Autowired
    private SubContractRepository subContractRepository;

    @Autowired
    private ContractRepository contractRepository;

    @BeforeAll
    static void beforeAll(@Autowired JdbcTemplate jdbcTemplate) {
        // transaction type table
        // insert default value
        TestConfig.configTransactionTypeTable(jdbcTemplate);
    }

    @Test
    void findAllByContractId() {
        // given
        Contract newContract1 = new Contract("Insurance Policy for Property Coverage");
        // transaction type
        TransactionType transactionType1 = new TransactionType();
        transactionType1.setId(1);
        transactionType1.setTitle("Credit");
        transactionType1.setMultiplier(-1);


        SubContract subContract1 = new SubContract();
        subContract1.setTitle("Security improvement");
        subContract1.setOrderNumber(1);
        subContract1.setSubContractNumber("A10212");
        subContract1.setTransactionType(transactionType1);
        subContract1.setDescription("Need to improve the overall security across the platform");
        subContract1.setAmount(100000.00);
        subContract1.setStartDate(LocalDateTime.now());
        subContract1.setEndDate(null);       // not ended

        newContract1.setSubContracts(List.of(subContract1));

        subContract1.setContract(newContract1);


        // save contract
        Contract savedContract = this.contractRepository.save(newContract1);

        // get all the sub contracts by contract
        List<SubContract> subContracts = this.subContractRepository.findAllByContract(savedContract);

        // then
        assertEquals(1, subContracts.size());
        assertEquals(savedContract.getId(), subContracts.get(0).getContract().getId());
        assertEquals(transactionType1.getTitle(), subContracts.get(0).getTransactionType().getTitle());

    }
}