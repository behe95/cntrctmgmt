package com.example.cntrctmgmt.integerationtest.services;

import com.example.cntrctmgmt.entities.Contract;
import com.example.cntrctmgmt.entities.SubContract;
import com.example.cntrctmgmt.entities.TransactionType;
import com.example.cntrctmgmt.services.ContractService;
import com.example.cntrctmgmt.services.SubContractService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContractServiceTest {



    @Autowired
    private ContractService contractService;

    @Autowired
    private SubContractService subContractService;

    @Test
    void addContract() {

        // given
        Contract contract = new Contract("Insurance Policy for Property Coverage");

        // when
        Contract savedContract = this.contractService.addContract(contract);

        // then
        assertEquals(1, savedContract.getId());
    }

    @Test
    void getContractById() {


        // given
        Contract newContract = new Contract("Insurance Policy for Property Coverage");
        Contract saveContract = this.contractService.addContract(newContract);

        // when
        Optional<Contract> contract = this.contractService.getContractById(saveContract.getId());

        // then
        assertTrue(contract.isPresent());
        assertEquals(0, contract.get().getSubContracts().size());
    }

    @Test
    void updateContract() {

        // given
        Contract newContract = new Contract("Insurance Policy for Property Coverage");
        Contract savedContract = this.contractService.addContract(newContract);

        // modify -- add subcontracts
        // args subcontract
        // transaction type
        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();

        Contract contract = new Contract();
        contract.setId(1);
        contract.setTitle("Software upgrade");
        contract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        contract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract subContract = new SubContract();
        subContract.setTitle("Security improvement");
        subContract.setOrderNumber(1);
        subContract.setSubContractNumber("A10212");
        subContract.setTransactionType(transactionType);
        subContract.setDescription("Need to improve the overall security across the platform");
        subContract.setAmount(100000.00);
        subContract.setStartDate(subContractStartDate);
        subContract.setEndDate(null);       // not ended
        subContract.setContract(contract);


        savedContract.addSubContract(subContract);


        // when

        this.contractService.updateContract(savedContract);

        // then

        Optional<Contract> updatedContract = this.contractService.getContractById(savedContract.getId());

        assertTrue(updatedContract.isPresent());
        assertEquals(1, updatedContract.get().getSubContracts().size());

        System.out.println(this.contractService.getAllContracts().size());

    }

    @Test
    void deleteContract() {

        // given
        Contract newContract = new Contract("Insurance Policy for Property Coverage");
        Contract savedContract = this.contractService.addContract(newContract);

        // when
        this.contractService.deleteContract(savedContract);


        // then
        Optional<Contract> deletedContract = this.contractService.getContractById(1);
        assertFalse(deletedContract.isPresent());

    }

    @Test
    void deleteAllContracts() {
        // given
        Contract newContract = new Contract("Insurance Policy for Property Coverage");
        Contract savedContract = this.contractService.addContract(newContract);

        // when
        this.contractService.deleteAllContracts();


        // then
        Optional<Contract> deletedContract = this.contractService.getContractById(1);
        assertFalse(deletedContract.isPresent());
    }

    @Test
    void deleteContracts() {

        // given
        Contract newContract1 = new Contract("Insurance Policy for Property Coverage");
        // transaction type
        TransactionType transactionType1 = new TransactionType();
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


        Contract savedContract1 = this.contractService.addContract(newContract1);

        Contract newContract2 = new Contract("Upgrade system");
        Contract savedContract2 = this.contractService.addContract(newContract2);

        Contract newContract3 = new Contract("Implement new business model");
        Contract savedContract3 = this.contractService.addContract(newContract3);

        // when
        this.contractService.deleteContracts(List.of(savedContract1, savedContract3));


        // then
        Optional<Contract> deletedContract1 = this.contractService.getContractById(savedContract1.getId());
        assertFalse(deletedContract1.isPresent());

        Optional<Contract> deletedContract2 = this.contractService.getContractById(savedContract3.getId());
        assertFalse(deletedContract2.isPresent());


        List<Contract> contracts = this.contractService.getAllContracts();
        assertEquals(1, contracts.size());

        assertEquals(savedContract2.getId(), contracts.get(0).getId());
        assertEquals(savedContract2.getTitle(), contracts.get(0).getTitle());
        assertEquals(savedContract2.getCreated(), contracts.get(0).getCreated());
        assertEquals(savedContract2.getModified(), contracts.get(0).getModified());



    }
}