package com.example.cntrctmgmt.integerationtest.services;

import com.example.cntrctmgmt.TestConfig;
import com.example.cntrctmgmt.entities.*;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.ContractService;
import com.example.cntrctmgmt.services.SubCategoryService;
import com.example.cntrctmgmt.services.SubContractService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ContractServiceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;


    @Autowired
    private ContractService contractService;

    @Autowired
    private SubContractService subContractService;

    Category category;
    SubCategory subCategory;


    @BeforeAll
    static void beforeAll(@Autowired JdbcTemplate jdbcTemplate) {
        // transaction type table
        // insert default value
        TestConfig.configTransactionTypeTable(jdbcTemplate);
        TestConfig.configCategorySubCategoryTable(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        Optional<Category> c = this.categoryService.getCategoryById(1);
        Optional<SubCategory> sc = this.subCategoryService.getSubCategoryById(1);

        c.ifPresent(value -> this.category = value);
        sc.ifPresent(value -> this.subCategory = value);

    }

    @Test
    void addContract() {

        // given
        Contract contract = new Contract("Insurance Policy for Property Coverage");

        // when
        Contract savedContract = this.contractService.addContract(contract);

        // then
        assertNotNull(savedContract);
        assertEquals(0, savedContract.getSubContracts().size());
        assertEquals(contract.getTitle(), savedContract.getTitle());
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


        SubContract subContract = new SubContract();
        subContract.setTitle("Security improvement");
        subContract.setOrderNumber(1);
        subContract.setSubContractNumber("A10212");
        subContract.setTransactionType(transactionType);
        subContract.setDescription("Need to improve the overall security across the platform");
        subContract.setAmount(100000.00);
        subContract.setStartDate(subContractStartDate);
        subContract.setEndDate(null);       // not ended
        subContract.setCategory(this.category);
        subContract.setSubCategory(this.subCategory);

        subContract.setContract(savedContract);
        savedContract.addSubContract(subContract);


        // when

        this.contractService.updateContract(savedContract);

        // then

        Optional<Contract> updatedContract = this.contractService.getContractById(savedContract.getId());

        List<SubContract> savedSubContractList = this.subContractService.getAllSubContractsByContract(savedContract);


        assertTrue(updatedContract.isPresent());
        assertEquals(1, updatedContract.get().getSubContracts().size());
        assertEquals(1, savedSubContractList.size());
        assertNotNull(updatedContract.get().getSubContracts().get(0).getTransactionType());
        assertEquals(1, updatedContract.get().getSubContracts().get(0).getTransactionType().getId());
        assertEquals(this.category.getId(), updatedContract.get().getSubContracts().get(0).getCategory().getId());
        assertEquals(this.subCategory.getId(), updatedContract.get().getSubContracts().get(0).getSubCategory().getId());

    }

    @Test
    void deleteContract() {

        // given
        Contract newContract = new Contract("Insurance Policy for Property Coverage");

        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();

        SubContract subContract = new SubContract();
        subContract.setTitle("Security improvement");
        subContract.setOrderNumber(1);
        subContract.setSubContractNumber("A10212");
        subContract.setTransactionType(transactionType);
        subContract.setDescription("Need to improve the overall security across the platform");
        subContract.setAmount(100000.00);
        subContract.setStartDate(subContractStartDate);
        subContract.setEndDate(null);       // not ended
        subContract.setCategory(this.category);
        subContract.setSubCategory(this.subCategory);

        subContract.setContract(newContract);
        newContract.addSubContract(subContract);


        Contract savedContract = this.contractService.addContract(newContract);

        // when
        this.contractService.deleteContract(savedContract);


        // then
        Optional<Contract> deletedContract = this.contractService.getContractById(savedContract.getId());
        assertFalse(deletedContract.isPresent());

        // also check the child subContracts are deleted too
        List<SubContract> subContractList = this.subContractService.getAllSubContractsByContract(savedContract);
        assertEquals(0, subContractList.size());

    }

    @Test
    void deleteAllContracts() {
        // given
        Contract newContract1 = new Contract("Insurance Policy for Property Coverage");

        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();

        SubContract subContract1 = new SubContract();
        subContract1.setTitle("Security improvement");
        subContract1.setOrderNumber(1);
        subContract1.setSubContractNumber("A10212");
        subContract1.setTransactionType(transactionType);
        subContract1.setDescription("Need to improve the overall security across the platform");
        subContract1.setAmount(100000.00);
        subContract1.setStartDate(subContractStartDate);
        subContract1.setEndDate(null);       // not ended
        subContract1.setCategory(this.category);
        subContract1.setSubCategory(this.subCategory);

        subContract1.setContract(newContract1);
        newContract1.addSubContract(subContract1);


        Contract savedContract1 = this.contractService.addContract(newContract1);


        Contract newContract2 = new Contract("Upgrade system");
        Contract savedContract2 = this.contractService.addContract(newContract2);

        // when
        this.contractService.deleteAllContracts();


        // then
        List<Contract> contractsList = this.contractService.getAllContracts();
        assertEquals(0, contractsList.size());


        // also check the child subContracts are deleted too
        List<SubContract> subContractList = this.subContractService.getAllSubContractsByContract(savedContract1);
        assertEquals(0, subContractList.size());
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
        subContract1.setCategory(this.category);
        subContract1.setSubCategory(this.subCategory);

        newContract1.setSubContracts(List.of(subContract1));


        subContract1.setContract(newContract1);
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


        // also check the child subContracts are deleted too
        List<SubContract> subContractList = this.subContractService.getAllSubContractsByContract(savedContract1);
        assertEquals(0, subContractList.size());
    }
}