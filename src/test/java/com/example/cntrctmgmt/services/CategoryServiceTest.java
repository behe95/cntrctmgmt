package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.repositories.CategoryRepository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        Category category = new Category("Meal", false);
        category.setPkcmCategory(1);

        given(this.categoryRepositoryMock.findById(1)).willReturn(Optional.of(category));

        assertThat(this.categoryServiceUnderTest.getCategoryById(1))
                .isPresent()
                .containsInstanceOf(Category.class);



        given(this.categoryRepositoryMock.findById(1)).willReturn(Optional.empty());
        assertThat(this.categoryServiceUnderTest.getCategoryById(1))
                .isEmpty();
        verify(this.categoryRepositoryMock, times(2)).findById(1);
    }

    @Test
    void getAllCategories() {
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


        when(this.categoryRepositoryMock.findAll()).thenReturn(mockCategories);

        List<Category> categories = this.categoryServiceUnderTest.getAllCategories();
        assertEquals(3, categories.size());

        verify(this.categoryRepositoryMock, times(1)).findAll();
    }

    @Test
    void updateCategory() {
    }

    @Test
    void deleteCategory() {
    }

    @Test
    void deleteAllCategories() {
    }

    @Test
    void deleteCategories() {
    }
}