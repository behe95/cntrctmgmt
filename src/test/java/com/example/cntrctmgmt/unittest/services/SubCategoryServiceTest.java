package com.example.cntrctmgmt.unittest.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.UnknownException;
import com.example.cntrctmgmt.repositories.SubCategoryRepository;
import com.example.cntrctmgmt.services.SubCategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
class SubCategoryServiceTest {

    @Mock
    private SubCategoryRepository subCategoryRepositoryMock;

    @InjectMocks
    private SubCategoryService subCategoryServiceUnderTest;

    @Test
    void addSubCategory() throws DuplicateEntityException, UnknownException {
        SubCategory subCategory = new SubCategory("Insurance");
        SubCategory mockReturnSubCategory = new SubCategory("Insurance");
        mockReturnSubCategory.setId(1);

        // given mock
        given(this.subCategoryRepositoryMock.save(subCategory)).willReturn(mockReturnSubCategory);

        // when
        SubCategory returnedSubCategory = this.subCategoryServiceUnderTest.addSubCategory(subCategory);

        // then
        // verify that saved method was called only once
        ArgumentCaptor<SubCategory> argumentCaptor = ArgumentCaptor.forClass(SubCategory.class);
        then(this.subCategoryRepositoryMock).should(times(1)).save(argumentCaptor.capture());

        // make sure what passed argument is received
        assertEquals(subCategory, argumentCaptor.getValue());

        // make sure the return value is the same as the one received from
        // saving into the database
        assertEquals(mockReturnSubCategory, returnedSubCategory);


    }

    @Test
    void addSubCategoryTestForDuplicateEntityException() {
        SubCategory subCategory = new SubCategory("Banking");


        // mock the exception

        // re-create the sqlite unique constraint exception
        SQLiteException sqLiteException = new SQLiteException("", SQLiteErrorCode.SQLITE_CONSTRAINT);
        JpaSystemException jpaSystemException = new JpaSystemException(new RuntimeException("", sqLiteException));

        // given mock
        given(this.subCategoryRepositoryMock.save(subCategory)).willThrow(jpaSystemException);

        // when
        // then
        assertThatThrownBy(() -> this.subCategoryServiceUnderTest.addSubCategory(subCategory))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());


    }

    @Test
    void addSubCategoryTestForUnknownException() {
        SubCategory subCategory = new SubCategory("Banking");


        // mock the exception

        // re-create any exception but sqlite unique constraint exception
        SQLiteException sqLiteException = new SQLiteException("", SQLiteErrorCode.SQLITE_ABORT);
        JpaSystemException jpaSystemException = new JpaSystemException(new RuntimeException("", sqLiteException));

        // given mock
        given(this.subCategoryRepositoryMock.save(subCategory)).willThrow(jpaSystemException);

        // when
        // then
        assertThatThrownBy(() -> this.subCategoryServiceUnderTest.addSubCategory(subCategory))
                .isInstanceOf(UnknownException.class)
                .hasMessage(ExceptionMessage.UNKNOWN_EXCEPTION.getMessage());

    }


    @Test
    void getSubCategoryById() {
        int id = 1;
        SubCategory mockReturnSubCategory = new SubCategory("Investments");
        mockReturnSubCategory.setId(id);

        // given mock
        given(this.subCategoryRepositoryMock.findById(id)).willReturn(Optional.of(mockReturnSubCategory));

        // when
        Optional<SubCategory> returnSubCategory = this.subCategoryServiceUnderTest.getSubCategoryById(id);

        // then

        // data exists
        assertTrue(returnSubCategory.isPresent());

        // repository called the findById method once
        verify(this.subCategoryRepositoryMock, times(1)).findById(any());

        // verify the return data is exactly the mock data
        assertEquals(mockReturnSubCategory, returnSubCategory.get());

        // make sure what passed argument is received
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);

        then(this.subCategoryRepositoryMock).should().findById(argumentCaptor.capture());

        assertEquals(id, argumentCaptor.getValue());


    }

    @Test
    void getAllSubCategories() {
        List<SubCategory> mockSubCategories = new ArrayList<>();
        SubCategory subCategory1 = new SubCategory("Airline");
        subCategory1.setId(1);
        mockSubCategories.add(subCategory1);
        SubCategory subCategory2 = new SubCategory("Railway");
        subCategory2.setId(2);
        mockSubCategories.add(subCategory2);

        // given
        given(this.subCategoryRepositoryMock.findAll()).willReturn(mockSubCategories);

        // when
        List<SubCategory> returnedSubCategories = this.subCategoryServiceUnderTest.getAllSubCategories();

        // then

        // repository called method only once
        then(this.subCategoryRepositoryMock).should(times(1)).findAll();

        // check the length of the return data
        assertEquals(mockSubCategories.size(), returnedSubCategories.size());

    }

    @Test
    void updateSubCategory() throws DuplicateEntityException, UnknownException {
        // argument data
        SubCategory argSubCategory = new SubCategory("Crops");
        argSubCategory.setId(1);

        // mock return data
        SubCategory mockSubCategory = new SubCategory("Livestock");
        mockSubCategory.setId(1);

        // given
        given(this.subCategoryRepositoryMock.findById(argSubCategory.getId())).willReturn(Optional.of(mockSubCategory));

        // when
        this.subCategoryServiceUnderTest.updateSubCategory(argSubCategory);

        // then

        /**
         * verify repository alled method only once
         * check if the passed id is the same id
         * to find the subcateogory
         */
        ArgumentCaptor<Integer> argumentCaptorId = ArgumentCaptor.forClass(Integer.class);
        then(this.subCategoryRepositoryMock)
                .should(times(1))
                .findById(argumentCaptorId.capture());

        assertEquals(argSubCategory.getId(), argumentCaptorId.getValue());

        /**
         * verify repository called the save method only once
         * verify that the object that was passed into the
         * save method has the following properties
         *          - same title as the argument sub-category
         *          - same id as the subcategory that was return by findByID
         */
        ArgumentCaptor<SubCategory> subCategoryArgumentCaptor = ArgumentCaptor.forClass(SubCategory.class);
        then(this.subCategoryRepositoryMock).should(times((1))).save(subCategoryArgumentCaptor.capture());

        assertEquals(argSubCategory.getTitle(), subCategoryArgumentCaptor.getValue().getTitle());
        assertEquals(mockSubCategory.getId(), subCategoryArgumentCaptor.getValue().getId());

    }


    @Test
    void updateSubCategoryTestForEntityNotFoundException() {
        // argument data
        SubCategory argSubCategory = new SubCategory("Crops");
        argSubCategory.setId(1);


        // given
        given(this.subCategoryRepositoryMock.findById(argSubCategory.getId())).willThrow(new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        // when
        assertThatThrownBy(() -> this.subCategoryServiceUnderTest.updateSubCategory(argSubCategory))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ExceptionMessage.ENTITY_NOT_FOUND.getMessage());

        // then

        /**
         * verify repository called method only once
         * check if the passed id is the same id
         * to find the subcateogory
         */
        ArgumentCaptor<Integer> argumentCaptorId = ArgumentCaptor.forClass(Integer.class);
        then(this.subCategoryRepositoryMock)
                .should(times(1))
                .findById(argumentCaptorId.capture());

        assertEquals(argSubCategory.getId(), argumentCaptorId.getValue());

        /**
         * verify that program doesn't
         * proceed further
         */
        then(this.subCategoryRepositoryMock).should(never()).save(any());

    }

    @Test
    void updateSubCategoryTestForDuplicateEntityException() {
        // argument data
        SubCategory argSubCategory = new SubCategory("Crops");
        argSubCategory.setId(1);

        /**
         * mock data returned
         * when called findById(...)
         */
        SubCategory mockSubCategory = new SubCategory("Livestock");
        argSubCategory.setId(1);

        // given
        given(this.subCategoryRepositoryMock.findById(argSubCategory.getId())).willReturn(Optional.of(mockSubCategory));


        // re-create the sqlite unique constraint exception
        SQLiteException sqLiteException = new SQLiteException("", SQLiteErrorCode.SQLITE_CONSTRAINT);
        JpaSystemException jpaSystemException = new JpaSystemException(new RuntimeException("", sqLiteException));

        given(this.subCategoryRepositoryMock.save(any())).willThrow(jpaSystemException);

        // when
        assertThatThrownBy(() -> this.subCategoryServiceUnderTest.updateSubCategory(argSubCategory))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());

        // then
        /**
         * verify repository called method only once
         * check if the passed id is the same id
         * to find the subcateogory
         */
        ArgumentCaptor<Integer> argumentCaptorId = ArgumentCaptor.forClass(Integer.class);
        then(this.subCategoryRepositoryMock)
                .should(times(1))
                .findById(argumentCaptorId.capture());

        assertEquals(argSubCategory.getId(), argumentCaptorId.getValue());

        /**
         * verify repository called the save method only once
         * verify that the object that was passed into the
         * save method has the following properties
         *          - same title as the argument sub-category
         *          - same id as the subcategory that was return by findByID
         */
        ArgumentCaptor<SubCategory> subCategoryArgumentCaptor = ArgumentCaptor.forClass(SubCategory.class);
        then(this.subCategoryRepositoryMock).should(times((1))).save(subCategoryArgumentCaptor.capture());

        assertEquals(argSubCategory.getTitle(), subCategoryArgumentCaptor.getValue().getTitle());
        assertEquals(mockSubCategory.getId(), subCategoryArgumentCaptor.getValue().getId());


    }


    @Test
    void updateSubCategoryTestForUnknownException() {
        // argument data
        SubCategory argSubCategory = new SubCategory("Crops");
        argSubCategory.setId(1);

        /**
         * mock data returned
         * when called findById(...)
         */
        SubCategory mockSubCategory = new SubCategory("Livestock");
        argSubCategory.setId(1);

        // given
        given(this.subCategoryRepositoryMock.findById(argSubCategory.getId())).willReturn(Optional.of(mockSubCategory));


        // re-create any exception but sqlite unique constraint exception
        SQLiteException sqLiteException = new SQLiteException("", SQLiteErrorCode.SQLITE_ABORT);
        JpaSystemException jpaSystemException = new JpaSystemException(new RuntimeException("", sqLiteException));

        given(this.subCategoryRepositoryMock.save(any())).willThrow(jpaSystemException);

        // when
        assertThatThrownBy(() -> this.subCategoryServiceUnderTest.updateSubCategory(argSubCategory))
                .isInstanceOf(UnknownException.class)
                .hasMessage(ExceptionMessage.UNKNOWN_EXCEPTION.getMessage());

        // then
        /**
         * verify repository called method only once
         * check if the passed id is the same id
         * to find the subcateogory
         */
        ArgumentCaptor<Integer> argumentCaptorId = ArgumentCaptor.forClass(Integer.class);
        then(this.subCategoryRepositoryMock)
                .should(times(1))
                .findById(argumentCaptorId.capture());

        assertEquals(argSubCategory.getId(), argumentCaptorId.getValue());

        /**
         * verify repository called the save method only once
         * verify that the object that was passed into the
         * save method has the following properties
         *          - same title as the argument sub-category
         *          - same id as the subcategory that was return by findByID
         */
        ArgumentCaptor<SubCategory> subCategoryArgumentCaptor = ArgumentCaptor.forClass(SubCategory.class);
        then(this.subCategoryRepositoryMock).should(times((1))).save(subCategoryArgumentCaptor.capture());

        assertEquals(argSubCategory.getTitle(), subCategoryArgumentCaptor.getValue().getTitle());
        assertEquals(mockSubCategory.getId(), subCategoryArgumentCaptor.getValue().getId());


    }

    @Test
    void deleteSubCategory() {
        // arg sub category to delete
        SubCategory subCategory = new SubCategory("Materials");
        subCategory.setId(1);

        /**
         * when
         * check that no exception was thrown       -- not required JPA doesn't throw any exception
         * check the delete method was called only once
         * check that the passed args is the same as
         * the {@Code subCategory}
         */
        ArgumentCaptor<SubCategory> subCategoryArgumentCaptor = ArgumentCaptor.forClass(SubCategory.class);
        this.subCategoryServiceUnderTest.deleteSubCategory(subCategory);
//        assertThatNoException().isThrownBy(() -> this.subCategoryServiceUnderTest.deleteSubCategory(subCategory));

        then(this.subCategoryRepositoryMock).should(times(1)).delete(subCategoryArgumentCaptor.capture());

        assertEquals(subCategory, subCategoryArgumentCaptor.getValue());
    }

    @Test
    @Disabled
    void deleteAllSubCategories() {
        /**
         * when
         * check the delete method was called only once
         */
        this.subCategoryServiceUnderTest.deleteAllSubCategories();
        then(this.subCategoryRepositoryMock).should(times(1)).deleteAll();
    }
}