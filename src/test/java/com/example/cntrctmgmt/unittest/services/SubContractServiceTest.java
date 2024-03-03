package com.example.cntrctmgmt.unittest.services;

import com.example.cntrctmgmt.entities.*;
import com.example.cntrctmgmt.repositories.SubContractRepository;
import com.example.cntrctmgmt.services.SubContractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SubContractServiceTest {

    @Mock
    private SubContractRepository subContractRepositoryMock;

    @InjectMocks
    private SubContractService subContractServiceUnderTest;

    @Test
    void addSubContract() {
        // transaction type
        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();

        // args subcontract

        Category category = new Category("Technology", true);
        category.setId(1);
        SubCategory subCategory = new SubCategory("Software");
        subCategory.setId(1);

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
        subContract.setCategory(category);
        subContract.setSubCategory(subCategory);

        // mock contract
        Category mockCategory = new Category("Technology", true);
        mockCategory.setId(1);
        SubCategory mockSubCategory = new SubCategory("Software");
        mockSubCategory.setId(1);

        Contract mockContract = new Contract();
        mockContract.setId(1);
        mockContract.setTitle("Software upgrade");
        mockContract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        mockContract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract mockSubContract = new SubContract();
        mockSubContract.setId(1);
        mockSubContract.setTitle("Security improvement");
        mockSubContract.setOrderNumber(1);
        mockSubContract.setSubContractNumber("A10212");
        mockSubContract.setTransactionType(transactionType);
        mockSubContract.setDescription("Need to improve the overall security across the platform");
        mockSubContract.setAmount(100000.00);
        mockSubContract.setStartDate(subContractStartDate);
        mockSubContract.setEndDate(null);       // not ended
        mockSubContract.setContract(mockContract);
        mockSubContract.setCategory(mockCategory);
        mockSubContract.setSubCategory(mockSubCategory);

        // given
        given(this.subContractRepositoryMock.save(subContract)).willReturn(mockSubContract);

        // when
        SubContract savedSubContract = this.subContractServiceUnderTest.addSubContract(subContract);

        /**
         * then
         * verify the save method was called only once
         * return data after save has the following properties
         *      - id same as {@code mockSubContract}
         *      - rest of the properties are as same as the {@code subContract}
         * check if the passed args used to call the {@code save(...)} method
         */
        ArgumentCaptor<SubContract> subContractArgumentCaptor = ArgumentCaptor.forClass(SubContract.class);
        then(this.subContractRepositoryMock).should(times(1)).save(subContractArgumentCaptor.capture());
        assertEquals(mockSubContract.getId(), savedSubContract.getId());
        assertEquals(subContract.getTitle(), savedSubContract.getTitle());
        assertEquals(subContract.getOrderNumber(), savedSubContract.getOrderNumber());
        assertEquals(subContract.getSubContractNumber(), savedSubContract.getSubContractNumber());
        assertEquals(subContract.getTransactionType(), savedSubContract.getTransactionType());
        assertEquals(subContract.getDescription(), savedSubContract.getDescription());
        assertEquals(subContract.getAmount(), savedSubContract.getAmount());
        assertEquals(subContract.getStartDate(), savedSubContract.getStartDate());
        assertEquals(subContract.getEndDate(), savedSubContract.getEndDate());
        assertEquals(subContract, subContractArgumentCaptor.getValue());
        assertEquals(subContract.getCategory().getId(),savedSubContract.getCategory().getId());
        assertEquals(subContract.getSubCategory().getId(), savedSubContract.getSubCategory().getId());



    }

    @Test
    void getSubContractById() {
        // transaction type
        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();

        // args subcontract
        Category category = new Category("Technology", true);
        category.setId(1);
        SubCategory subCategory = new SubCategory("Software");
        subCategory.setId(1);

        Contract contract = new Contract();
        contract.setId(1);
        contract.setTitle("Software upgrade");
        contract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        contract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract subContract = new SubContract();
        subContract.setId(1);
        subContract.setTitle("Security improvement");
        subContract.setOrderNumber(1);
        subContract.setSubContractNumber("A10212");
        subContract.setTransactionType(transactionType);
        subContract.setDescription("Need to improve the overall security across the platform");
        subContract.setAmount(100000.00);
        subContract.setStartDate(subContractStartDate);
        subContract.setEndDate(null);       // not ended
        subContract.setContract(contract);
        subContract.setCategory(category);
        subContract.setSubCategory(subCategory);

        // given
        given(this.subContractRepositoryMock.findById(1)).willReturn(Optional.of(subContract));

        // when
        Optional<SubContract> returnedSubContract = this.subContractServiceUnderTest.getSubContractById(1);

        /**
         * then
         * check if the returned data is present and not null
         * check if the returned data's id same as
         * the passed id
         * verify the {@code findById(...)} method was called only once
         * verify if the passed arg used in the {@code findById(...)}
         * was the same as the passed id
         */
        ArgumentCaptor<Integer> idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        assertTrue(returnedSubContract.isPresent());
        assertEquals(1, returnedSubContract.get().getId());
        verify(this.subContractRepositoryMock, times(1)).findById(idArgumentCaptor.capture());
        assertEquals(1, idArgumentCaptor.getValue());

    }

    @Test
    void updateSubContract() {
        // transaction type
        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();
        LocalDateTime subContractEndDate = LocalDateTime.now();

        // args subcontract
        Category category = new Category("Technology", true);
        category.setId(1);
        SubCategory subCategory = new SubCategory("Software");
        subCategory.setId(1);

        Contract contract = new Contract();
        contract.setId(1);
        contract.setTitle("Software upgrade");
        contract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        contract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract subContract = new SubContract();
        subContract.setId(1);
        subContract.setTitle("Security improvement");
        subContract.setOrderNumber(1);
        subContract.setSubContractNumber("A10212");
        subContract.setTransactionType(transactionType);
        subContract.setDescription("Need to improve the overall security across the platform");
        subContract.setAmount(100000.00);
        subContract.setStartDate(subContractStartDate);
        subContract.setEndDate(subContractEndDate);       // modified here
        subContract.setContract(contract);
        subContract.setCategory(category);
        subContract.setSubCategory(subCategory);

        // mock contract
        Category mockCategory = new Category("Technology", true);
        mockCategory.setId(1);
        SubCategory mockSubCategory = new SubCategory("Software");
        mockSubCategory.setId(1);

        Contract mockContract = new Contract();
        mockContract.setId(1);
        mockContract.setTitle("Software upgrade");
        mockContract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        mockContract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract mockSubContract = new SubContract();
        mockSubContract.setId(1);
        mockSubContract.setTitle("Security improvement");
        mockSubContract.setOrderNumber(1);
        mockSubContract.setSubContractNumber("A10212");
        mockSubContract.setTransactionType(transactionType);
        mockSubContract.setDescription("Need to improve the overall security across the platform");
        mockSubContract.setAmount(100000.00);
        mockSubContract.setStartDate(subContractStartDate);
        mockSubContract.setEndDate(null);       // not ended
        mockSubContract.setContract(contract);
        mockSubContract.setCategory(mockCategory);
        mockSubContract.setSubCategory(mockSubCategory);


        // given
        given(this.subContractRepositoryMock.findById(subContract.getId())).willReturn(Optional.of(mockSubContract));

        // when
        this.subContractServiceUnderTest.updateSubContract(subContract);

        /**
         * verify repository called method only once
         * check if the passed id is the same id that were received
         * to find the subcontract
         */
        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        then(this.subContractRepositoryMock)
                .should(times(1))
                .findById(integerArgumentCaptor.capture());

        assertEquals(subContract.getId(), integerArgumentCaptor.getValue());

        /**
         * verify repository called the {@code save(...)} method only once
         * verify that the object that was passed into the
         * {@code save(...)} method has the following properties
         *      - same properties as the argment subcontract
         *      - same id as the {@code subContract} that was return by {@code findById(...)} method
         */
        ArgumentCaptor<SubContract> subContractArgumentCaptor = ArgumentCaptor.forClass(SubContract.class);
        then(this.subContractRepositoryMock).should(times(1)).save(subContractArgumentCaptor.capture());

        assertEquals(subContract, subContractArgumentCaptor.getValue());
    }

    @Test
    void updateSubContractTestForEntityNotFoundException() {
        // transaction type
        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();
        LocalDateTime subContractEndDate = LocalDateTime.now();

        // args subcontract
        Category category = new Category("Technology", true);
        category.setId(1);
        SubCategory subCategory = new SubCategory("Software");
        subCategory.setId(1);

        Contract contract = new Contract();
        contract.setId(1);
        contract.setTitle("Software upgrade");
        contract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        contract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract subContract = new SubContract();
        subContract.setId(1);
        subContract.setTitle("Security improvement");
        subContract.setOrderNumber(1);
        subContract.setSubContractNumber("A10212");
        subContract.setTransactionType(transactionType);
        subContract.setDescription("Need to improve the overall security across the platform");
        subContract.setAmount(100000.00);
        subContract.setStartDate(subContractStartDate);
        subContract.setEndDate(subContractEndDate);       // modified here
        subContract.setContract(contract);
        subContract.setCategory(category);
        subContract.setSubCategory(subCategory);

        // given
        given(this.subContractRepositoryMock.findById(subContract.getId()))
                .willThrow(new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        // when
        assertThatThrownBy(() -> this.subContractServiceUnderTest.updateSubContract(subContract))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ExceptionMessage.ENTITY_NOT_FOUND.getMessage());

        // then

        /**
         * verify repository called method only once
         * check if the passed id is the same id as the {@code subContract}
         * to find the subContract
         */
        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        then(this.subContractRepositoryMock)
                .should(times(1))
                .findById(integerArgumentCaptor.capture());
        assertEquals(subContract.getId(), integerArgumentCaptor.getValue());

        /**
         * verify that program doesn't proceed further
         * to call the method {@code save(...)}
         */
        then(this.subContractRepositoryMock).should(never()).save(subContract);
    }

    @Test
    void deleteSubContract() {
        // transaction type
        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();

        // args subcontract
        Category category = new Category("Technology", true);
        category.setId(1);
        SubCategory subCategory = new SubCategory("Software");
        subCategory.setId(1);

        Contract contract = new Contract();
        contract.setId(1);
        contract.setTitle("Software upgrade");
        contract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        contract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract subContract = new SubContract();
        subContract.setId(1);
        subContract.setTitle("Security improvement");
        subContract.setOrderNumber(1);
        subContract.setSubContractNumber("A10212");
        subContract.setTransactionType(transactionType);
        subContract.setDescription("Need to improve the overall security across the platform");
        subContract.setAmount(100000.00);
        subContract.setStartDate(subContractStartDate);
        subContract.setEndDate(null);       // not ended
        subContract.setContract(contract);
        subContract.setCategory(category);
        subContract.setSubCategory(subCategory);

        /**
         * when
         * check if the delete method was called only once
         * checck that the passed args is the same as
         * the {@code subContract}
         */
        ArgumentCaptor<SubContract> subContractArgumentCaptor = ArgumentCaptor.forClass(SubContract.class);
        this.subContractServiceUnderTest.deleteSubContract(subContract);

        // then
        then(this.subContractRepositoryMock)
                .should(times(1))
                .delete(subContractArgumentCaptor.capture());
        assertEquals(subContract, subContractArgumentCaptor.getValue());
    }

    @Test
    void deleteAllSubContract() {
        /**
         * when
         * check if the {@code deleteAll(...)} method
         * was called only once by the repos
         */
        // when
        this.subContractServiceUnderTest.deleteAllSubContract();
        //then
        then(this.subContractRepositoryMock)
                .should(times(1))
                .deleteAll();
    }

    @Test
    void deleteSubContracts() {
        // transaction type
        TransactionType transactionType = new TransactionType();
        transactionType.setId(1);
        transactionType.setTitle("Credit");
        transactionType.setMultiplier(-1);

        // Local date time of contract start
        LocalDateTime subContractStartDate = LocalDateTime.now();
        LocalDateTime subContractEndDate = LocalDateTime.now();


        List<SubContract> subContractsList = new ArrayList<>();

        // args subcontract
        Category category = new Category("Technology", true);
        category.setId(1);
        SubCategory subCategory = new SubCategory("Software");
        subCategory.setId(1);

        Contract contract = new Contract();
        contract.setId(1);
        contract.setTitle("Software upgrade");
        contract.setCreated(LocalDateTime.of(2024,1,1,0,0));
        contract.setModified(LocalDateTime.of(2024,2,1,0,0));

        SubContract subContract1 = new SubContract();
        subContract1.setId(1);
        subContract1.setTitle("Security improvement");
        subContract1.setOrderNumber(1);
        subContract1.setSubContractNumber("A10212");
        subContract1.setTransactionType(transactionType);
        subContract1.setDescription("Need to improve the overall security across the platform");
        subContract1.setAmount(100000.00);
        subContract1.setStartDate(subContractStartDate);
        subContract1.setEndDate(subContractEndDate);
        subContract1.setContract(contract);
        subContract1.setCategory(category);
        subContract1.setSubCategory(subCategory);


        SubContract subContract2 = new SubContract();
        subContract2.setId(2);
        subContract2.setTitle("Include a new feature");
        subContract2.setOrderNumber(1);
        subContract2.setSubContractNumber("B12312");
        subContract2.setTransactionType(transactionType);
        subContract2.setDescription("Need to include a button to delete everything by id");
        subContract2.setAmount(5000.00);
        subContract2.setStartDate(subContractStartDate);
        subContract2.setEndDate(subContractEndDate);
        subContract2.setContract(contract);
        subContract2.setCategory(category);
        subContract2.setSubCategory(subCategory);


        subContractsList.add(subContract1);
        subContractsList.add(subContract2);

        // when
        this.subContractServiceUnderTest.deleteSubContracts(subContractsList);

        /**
         * then
         * verify that {@code deleteAll(...)} got called once
         * verify the passed args same as
         * args used in {@code deleteAll(...)} method
         */
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SubContract>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(this.subContractRepositoryMock, times(1))
                .deleteAll(listArgumentCaptor.capture());
        assertEquals(subContractsList, listArgumentCaptor.getValue());
        assertEquals(subContractsList.size(), listArgumentCaptor.getValue().size());
    }
}