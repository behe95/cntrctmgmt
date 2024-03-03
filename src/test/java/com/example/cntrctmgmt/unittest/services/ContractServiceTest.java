package com.example.cntrctmgmt.unittest.services;

import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.Contract;
import com.example.cntrctmgmt.repositories.CategoryRepository;
import com.example.cntrctmgmt.repositories.ContractRepository;
import com.example.cntrctmgmt.services.ContractService;
import org.junit.jupiter.api.Test;


import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.UnknownException;
import com.example.cntrctmgmt.repositories.SubCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.jpa.JpaSystemException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepositoryMock;

    @InjectMocks
    private ContractService contractServiceUnderTest;


    @Test
    void addContract() {
        // args contract
        Contract contract = new Contract("Insurance Policy for Property Coverage");


        // mock contract
        Contract mockContract = new Contract("Insurance Policy for Property Coverage");
        mockContract.setId(1);


        // given
        given(this.contractRepositoryMock.save(contract)).willReturn(mockContract);

        // when
        Contract savedContractReturn = this.contractServiceUnderTest.addContract(contract);

        /**
         * then
         * verify the save method was called once
         * return data after save has the following properties
         *          - id same as {@Code mockCategory}
         *          - title same as the passed argument's title
         * check if the passed args used to call the save(....) method
         */
        ArgumentCaptor<Contract> contractArgumentCaptor = ArgumentCaptor.forClass(Contract.class);
        then(this.contractRepositoryMock).should(times(1)).save(contractArgumentCaptor.capture());
        assertEquals(mockContract.getId(), savedContractReturn.getId());
        assertEquals(contract.getTitle(), savedContractReturn.getTitle());
        assertEquals(contract, contractArgumentCaptor.getValue());

    }

    @Test
    void getContractById() {
        // argument data
        Contract argContract = new Contract("Software Development Agreement with XYZ Corp");
        argContract.setId(1);

        // given
        given(this.contractRepositoryMock.findById(1)).willReturn(Optional.of(argContract));

        // when
        Optional<Contract> contract = this.contractServiceUnderTest.getContractById(1);

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
        assertTrue(contract.isPresent());
        assertEquals(1, contract.get().getId());
        verify(this.contractRepositoryMock, times(1)).findById(idArgumentCaptor.capture());
        assertEquals(1,idArgumentCaptor.getValue());

    }

    @Test
    void updateContract() {

        // argument data
        Contract argContract = new Contract("Insurance Policy for Property Coverage");
        argContract.setId(1);

        // mock return data
        Contract mockContract = new Contract("Software Development Agreement with XYZ Corp");
        mockContract.setId(1);

        // given
        given(this.contractRepositoryMock.findById(argContract.getId())).willReturn(Optional.of(mockContract));

        // when
        this.contractServiceUnderTest.updateContract(argContract);

        // then

        /**
         * verify repository called method only once
         * check if the passed id is the same id
         * to find the contract
         */
        ArgumentCaptor<Integer> argumentCaptorId = ArgumentCaptor.forClass(Integer.class);
        then(this.contractRepositoryMock)
                .should(times(1))
                .findById(argumentCaptorId.capture());

        assertEquals(argContract.getId(), argumentCaptorId.getValue());

        /**
         * verify repository called the save method only once
         * verify that the object that was passed into the
         * save method has the following properties
         *          - same title as the argument contract
         *          - same id as the contract that was return by findByID
         */
        ArgumentCaptor<Contract> contractArgumentCaptor = ArgumentCaptor.forClass(Contract.class);
        then(this.contractRepositoryMock).should(times((1))).save(contractArgumentCaptor.capture());

        assertEquals(argContract.getTitle(), contractArgumentCaptor.getValue().getTitle());
        assertEquals(mockContract.getId(), contractArgumentCaptor.getValue().getId());
    }

    @Test
    void updateSubCategoryTestForEntityNotFoundException() {

        // argument data
        Contract argContract = new Contract("Insurance Policy for Property Coverage");
        argContract.setId(1);


        // given
        given(this.contractRepositoryMock.findById(argContract.getId())).willThrow(new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        // when
        assertThatThrownBy(() -> this.contractServiceUnderTest.updateContract(argContract))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ExceptionMessage.ENTITY_NOT_FOUND.getMessage());

        // then

        /**
         * verify repository called method only once
         * check if the passed id is the same id
         * to find the contract
         */
        ArgumentCaptor<Integer> argumentCaptorId = ArgumentCaptor.forClass(Integer.class);
        then(this.contractRepositoryMock)
                .should(times(1))
                .findById(argumentCaptorId.capture());

        assertEquals(argContract.getId(), argumentCaptorId.getValue());

        /**
         * verify that program doesn't
         * proceed further
         */
        then(this.contractRepositoryMock).should(never()).save(any());

    }

    @Test
    void deleteContract() {
        // arg data to delete
        Contract contract = new Contract("Insurance Policy for Property Coverage");
        contract.setId(1);

        /**
         * when
         * check that no exception was thrown       -- not required JPA doesn't throw any exception
         * check the delete method was called only once
         * check that the passed args is the same as
         * the {@Code contract}
         */
        ArgumentCaptor<Contract> contractArgumentCaptor = ArgumentCaptor.forClass(Contract.class);
        this.contractServiceUnderTest.deleteContract(contract);
//        assertThatNoException().isThrownBy(() -> this.subCategoryServiceUnderTest.deleteSubCategory(subCategory));

        then(this.contractRepositoryMock).should(times(1)).delete(contractArgumentCaptor.capture());

        assertEquals(contract, contractArgumentCaptor.getValue());
    }

    @Test
    void deleteAllContracts() {
        /**
         * when
         * check the delete method was called only once
         */
        this.contractServiceUnderTest.deleteAllContracts();
        then(this.contractRepositoryMock).should(times(1)).deleteAll();
    }

    @Test
    void deleteCategories() {

        List<Contract> contracts = new ArrayList<>();

        // argument data
        Contract contract1 = new Contract("Insurance Policy for Property Coverage");
        contract1.setId(1);
        contracts.add(contract1);

        // mock return data
        Contract contract2 = new Contract("Software Development Agreement with XYZ Corp");
        contract2.setId(2);
        contracts.add(contract2);

        // when
        this.contractServiceUnderTest.deleteCategories(contracts);

        /**
         * verify {@code deleteAll(...)} got called once
         * verify the passed args same as
         * args used in {@code deleteAll(...)}
         */
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Contract>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(this.contractRepositoryMock, times(1)).deleteAll(argumentCaptor.capture());
        List<Contract> contractList = argumentCaptor.getValue();
        assertEquals(contracts.size(), contractList.size());
        assertEquals(contracts, argumentCaptor.getValue());
    }
}