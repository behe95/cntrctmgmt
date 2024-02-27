package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.repositories.CategoryRepository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepositoryMock;

    @InjectMocks
    private CategoryService categoryServiceUnderTest;

    @Test
    void addCategory() throws DuplicateEntityException {
        // given
        Category givenCategory = new Category("Labor", false);
        // actual operation
        // save data

        // check no exception thrown
        assertThatCode(() -> this.categoryServiceUnderTest.addCategory(givenCategory))
                .doesNotThrowAnyException();
        // check the method was called only once
        verify(this.categoryRepositoryMock, times(1)).save(givenCategory);

        // check returned data is valid
        when(this.categoryRepositoryMock.save(givenCategory)).thenReturn(givenCategory);

        Category savedCategory = this.categoryServiceUnderTest.addCategory(givenCategory);

        assertEquals(givenCategory.getTitle(), savedCategory.getTitle());
        assertEquals(givenCategory.getSoftCost(), savedCategory.getSoftCost());

    }

    @Test
    void addCategoryWithDuplicateEntityException() {
        // given
        Category categoryToAdd = new Category("Meal", true);

        // re-create the sqlite unique constraint exception
        SQLiteException sqLiteException = new SQLiteException("", SQLiteErrorCode.SQLITE_CONSTRAINT);
        JpaSystemException jpaSystemException = new JpaSystemException(new RuntimeException("", sqLiteException));

        // setup the mock environment
        when(this.categoryRepositoryMock.save(any(Category.class))).thenThrow(jpaSystemException);


        // run the test
        assertThatThrownBy(() -> this.categoryServiceUnderTest.addCategory(categoryToAdd))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.toString());


    }

    @Test
    void getCategoryById() {
        // given data
        Category category = new Category("Meal", false);
        category.setPkcmCategory(1);

        // set up mock env that returns data
        given(this.categoryRepositoryMock.findById(1)).willReturn(Optional.of(category));

        // run tests
        assertThat(this.categoryServiceUnderTest.getCategoryById(1))
                .isPresent()
                .containsInstanceOf(Category.class);


        // set up another mock env that returns nothing
        given(this.categoryRepositoryMock.findById(2)).willReturn(Optional.empty());
        // test
        assertThat(this.categoryServiceUnderTest.getCategoryById(2))
                .isEmpty();

        // verify the database call
        verify(this.categoryRepositoryMock, times(1)).findById(1);
    }

    @Test
    void getAllCategories() {
        // given mock data
        List<Category> mockCategories = new ArrayList<>();
        Category category1 = new Category("Category1", false);
        category1.setPkcmCategory(1);
        mockCategories.add(category1);
        Category category2 = new Category("Category2", true);
        category2.setPkcmCategory(2);
        mockCategories.add(category2);
        Category category3 = new Category("Category3", true);
        category3.setPkcmCategory(3);
        mockCategories.add(category3);


        // setup mock rep env
        when(this.categoryRepositoryMock.findAll()).thenReturn(mockCategories);

        // get the the data and run the test
        List<Category> categories = this.categoryServiceUnderTest.getAllCategories();
        assertEquals(3, categories.size());

        // verify that database was called once
        verify(this.categoryRepositoryMock, times(1)).findAll();
    }

    @Test
    void updateCategory() throws DuplicateEntityException {
        // given
        Category mockDataToBeUpdated = new Category("Construction", false);
        mockDataToBeUpdated.setPkcmCategory(1);

        // set up mock env so that category is found by id
        given(this.categoryRepositoryMock.findById(any())).willReturn(Optional.of(mockDataToBeUpdated));

        // call the actual method
        this.categoryServiceUnderTest.updateCategory(mockDataToBeUpdated);

        // check the passed argument as id is same as the mock data's id
        ArgumentCaptor<Integer> IdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // verify that mock repo was used to find the id
        // and called only once
        verify(this.categoryRepositoryMock, times(1)).findById(IdArgumentCaptor.capture());

        int capturedId = IdArgumentCaptor.getValue();

        assertEquals(mockDataToBeUpdated.getPkcmCategory(), capturedId);


        // check the passed argument if the properties are same
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        // verify that mock was used to save the updated data
        // and called only once
        verify(this.categoryRepositoryMock, times(1)).save(categoryArgumentCaptor.capture());

        Category capturedCategory = categoryArgumentCaptor.getValue();

        // verify the data
        assertNotNull(capturedCategory);
        assertEquals(mockDataToBeUpdated.getPkcmCategory(), capturedCategory.getPkcmCategory());
        assertEquals(mockDataToBeUpdated.getTitle(), capturedCategory.getTitle());
        assertEquals(mockDataToBeUpdated.getSoftCost(), capturedCategory.getSoftCost());

    }

    @Test
    void updateCategoryWithEntityNotFoundException() {
        // given
        Category mockDataToBeUpdated = new Category("Construction", false);
        mockDataToBeUpdated.setPkcmCategory(1);

        // set up mock env so that category is found by id
        given(this.categoryRepositoryMock.findById(any())).willReturn(Optional.empty());

        // call the actual method
        assertThatThrownBy(() -> this.categoryServiceUnderTest.updateCategory(mockDataToBeUpdated))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ExceptionMessage.ENTITY_NOT_FOUND.toString());

        // check the passed argument as id is same as the mock data's id
        ArgumentCaptor<Integer> IdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // verify that mock repo was used to find the id
        // and called only once
        verify(this.categoryRepositoryMock, times(1)).findById(IdArgumentCaptor.capture());

        int capturedId = IdArgumentCaptor.getValue();

        assertEquals(mockDataToBeUpdated.getPkcmCategory(), capturedId);

        // verify nothing was saved
        verify(this.categoryRepositoryMock, never()).save(any());
    }


    @Test
    void updateCategoryWithDuplicateEntityException() {
        // given
        Category categoryToUpdate = new Category("Meal", true);

        // re-create the sqlite unique constraint exception
        SQLiteException sqLiteException = new SQLiteException("", SQLiteErrorCode.SQLITE_CONSTRAINT);
        JpaSystemException jpaSystemException = new JpaSystemException(new RuntimeException("", sqLiteException));

        // setup the mock environment
        when(this.categoryRepositoryMock.findById(any())).thenReturn(Optional.of(categoryToUpdate));
        when(this.categoryRepositoryMock.save(any(Category.class))).thenThrow(jpaSystemException);

        // run the test
        assertThatThrownBy(() -> this.categoryServiceUnderTest.updateCategory(categoryToUpdate))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.toString());


        // check the passed argument as id is same as the mock data's id
        ArgumentCaptor<Integer> IdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // verify that mock repo was used to find the id
        // and called only once
        verify(this.categoryRepositoryMock, times(1)).findById(IdArgumentCaptor.capture());

        int capturedId = IdArgumentCaptor.getValue();

        assertEquals(categoryToUpdate.getPkcmCategory(), capturedId);

        // check the passed argument if the properties are same
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        // verify that mock was used to save the updated data
        // and called only once
        verify(this.categoryRepositoryMock, times(1)).save(categoryArgumentCaptor.capture());

        Category capturedCategory = categoryArgumentCaptor.getValue();

        // verify the data
        assertNotNull(capturedCategory);
        assertEquals(categoryToUpdate.getPkcmCategory(), capturedCategory.getPkcmCategory());
        assertEquals(categoryToUpdate.getTitle(), capturedCategory.getTitle());
        assertEquals(categoryToUpdate.getSoftCost(), capturedCategory.getSoftCost());



    }

    @Test
    void deleteCategory() {
        // given
        Category mockCategory = new Category("Meal", true);

        // call the method
        this.categoryServiceUnderTest.deleteCategory(mockCategory);

        // capture data and verify
        ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

        verify(this.categoryRepositoryMock, times(1)).delete(argumentCaptor.capture());

        Category capturedCategory = argumentCaptor.getValue();

        assertEquals(mockCategory, capturedCategory);
    }

    @Test
    void deleteAllCategories() {
        this.categoryServiceUnderTest.deleteAllCategories();
        verify(this.categoryRepositoryMock, times(1)).deleteAll();
    }

    @Test
    void deleteCategories() {
        // given mock data
        List<Category> mockCategories = new ArrayList<>();
        Category category1 = new Category("Category1", false);
        category1.setPkcmCategory(1);
        mockCategories.add(category1);
        Category category2 = new Category("Category2", true);
        category2.setPkcmCategory(2);
        mockCategories.add(category2);
        Category category3 = new Category("Category3", true);
        category3.setPkcmCategory(3);
        mockCategories.add(category3);


        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Category>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        this.categoryServiceUnderTest.deleteCategories(mockCategories);

        verify(this.categoryRepositoryMock, times(1)).deleteAll(argumentCaptor.capture());

        List<Category> categoryList = argumentCaptor.getValue();

        assertEquals(mockCategories.size(), categoryList.size());
    }
}