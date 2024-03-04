package com.example.cntrctmgmt.integerationtest.services;

import com.example.cntrctmgmt.entities.*;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.UnknownException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class CategoryServiceTest {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;

    List<Category> savedCategories = new ArrayList<>();
    List<SubCategory> savedSubCategories = new ArrayList<>();

    Faker faker = Faker.instance();

    @BeforeEach
    void setUp() throws DuplicateEntityException, UnknownException {
        // category
        Category category1 = new Category();
        category1.setTitle(faker.animal().name());
        category1.setSoftCost(faker.bool().bool());

        Category category2 = new Category();
        category2.setTitle(faker.name().name());
        category2.setSoftCost(faker.bool().bool());

        // save category
        Category returnedCategory1 = this.categoryService.addCategory(category1);
        Category returnedCategory2 = this.categoryService.addCategory(category2);


        // subcategory

        SubCategory subCategory1 = new SubCategory();
        subCategory1.setTitle(faker.animal().name());

        SubCategory subCategory2 = new SubCategory();
        subCategory2.setTitle(faker.name().name());

        // save subcategory
        SubCategory returnedSubCategory1 = this.subCategoryService.addSubCategory(subCategory1);
        SubCategory returnedSubCategory2 = this.subCategoryService.addSubCategory(subCategory2);

        // get all the categories and subcategories
        savedCategories = this.categoryService.getAllCategories();
        savedSubCategories = this.subCategoryService.getAllSubCategories();
    }


    @Test
    void updateCategory() throws DuplicateEntityException {
        /**
         * given
         */
        Category category = this.savedCategories.get(0);
        SubCategory subCategory1 = this.savedSubCategories.get(0);
        SubCategory subCategory2 = this.savedSubCategories.get(1);

        /**
         * when
         * join between category and subcategory
         * update the category
         */
        List<SubCategory> subCategories = new ArrayList<>();
        subCategories.add(subCategory1);
        subCategories.add(subCategory2);

        category.setSubCategoryList(subCategories); // immutable list causes JPA to throw exception
        // hence modifiable list is passed
        subCategory1.setCategoryList(List.of(category));
        subCategory2.setCategoryList(List.of(category));
        this.categoryService.updateCategory(category);


        /**
         * then
         * retrieve the category
         * retrieve the subcateories
         * verify that they are joined
         */
        Optional<Category> updatedCategory = this.categoryService.getCategoryById(category.getId());
        Optional<SubCategory> updatedSubCategory1 = this.subCategoryService.getSubCategoryById(subCategory1.getId());
        Optional<SubCategory> updatedSubCategory2 = this.subCategoryService.getSubCategoryById(subCategory2.getId());

        List<Category> updateCategoryList = this.categoryService.getAllCategories();
        List<SubCategory> updatedSubCategoryList = this.subCategoryService.getAllSubCategories();

        // check data is present
        assertTrue(updatedCategory.isPresent());
        assertTrue(updatedSubCategory1.isPresent());
        assertTrue(updatedSubCategory2.isPresent());

        // check they have joined entites
        assertEquals(2, updatedCategory.get().getSubCategoryList().size());
        assertEquals(1, updatedSubCategory1.get().getCategoryList().size());
        assertEquals(1, updatedSubCategory2.get().getCategoryList().size());

        // check no extra data created
        assertEquals(2, updateCategoryList.size());
        assertEquals(2, updatedSubCategoryList.size());
    }

    @Test
    void deleteCategory() throws DuplicateEntityException {
        /**
         * given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        Category category1 = this.savedCategories.get(0);
        Category category2 = this.savedCategories.get(1);
        SubCategory subCategory1 = this.savedSubCategories.get(0);
        SubCategory subCategory2 = this.savedSubCategories.get(1);

        /**
         * join between category and subcategory
         * update the category
         */
        List<SubCategory> subCategories1 = new ArrayList<>();
        subCategories1.add(subCategory1);
        subCategories1.add(subCategory2);

        category1.setSubCategoryList(subCategories1); // immutable list causes JPA to throw exception
        // hence modifiable list is passed
        subCategory1.setCategoryList(new ArrayList<>(List.of(category1)));  // mutable
        subCategory2.setCategoryList(new ArrayList<>(List.of(category1)));  // mutable
        this.categoryService.updateCategory(category1);


        List<SubCategory> subCategories2 = new ArrayList<>();
        subCategories2.add(subCategory1);

        category2.setSubCategoryList(subCategories2); // immutable list causes JPA to throw exception
        // hence modifiable list is passed
        subCategory1.setCategoryList(new ArrayList<>(List.of(category2)));  // mutable
        this.categoryService.updateCategory(category2);


        /**
         * when
         * delete
         * {category1} has {subCategory1, subCategory2}
         */
        this.categoryService.deleteCategory(category1);


        /**
         * then
         * retrieve the category
         * retrieve the subcateories
         * verify that they are joined
         */
        /**
         * expected state in db after deleting {category1}
         *
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category2}
         * {subCategory2} has {}
         */
        Optional<Category> updatedCategory1 = this.categoryService.getCategoryById(category1.getId());
        Optional<Category> updatedCategory2 = this.categoryService.getCategoryById(category2.getId());
        Optional<SubCategory> updatedSubCategory1 = this.subCategoryService.getSubCategoryById(subCategory1.getId());
        Optional<SubCategory> updatedSubCategory2 = this.subCategoryService.getSubCategoryById(subCategory2.getId());

        List<Category> updateCategoryList = this.categoryService.getAllCategories();
        List<SubCategory> updatedSubCategoryList = this.subCategoryService.getAllSubCategories();


        // check data is present
        assertTrue(updatedCategory1.isEmpty());
        assertTrue(updatedCategory2.isPresent());
        assertTrue(updatedSubCategory1.isPresent());
        assertTrue(updatedSubCategory2.isPresent());


        // check they have joined entites
        assertEquals(1, updatedCategory2.get().getSubCategoryList().size());
        assertEquals(1, updatedSubCategory1.get().getCategoryList().size());
        assertEquals(0, updatedSubCategory2.get().getCategoryList().size());

        // check no extra data created
        assertEquals(1, updateCategoryList.size());
        assertEquals(2, updatedSubCategoryList.size());
    }

    @Test
    void deleteAllCategories() throws DuplicateEntityException {
        /**
         * given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        Category category1 = this.savedCategories.get(0);
        Category category2 = this.savedCategories.get(1);
        SubCategory subCategory1 = this.savedSubCategories.get(0);
        SubCategory subCategory2 = this.savedSubCategories.get(1);

        /**
         * join between category and subcategory
         * update the category
         */
        List<SubCategory> subCategories1 = new ArrayList<>();
        subCategories1.add(subCategory1);
        subCategories1.add(subCategory2);

        category1.setSubCategoryList(subCategories1); // immutable list causes JPA to throw exception
        // hence modifiable list is passed
        subCategory1.addToCategoryList(category1);  // mutable
        subCategory2.addToCategoryList(category1);  // mutable
        this.categoryService.updateCategory(category1);


        category2.addToSubCategoryList(subCategory1); // immutable list causes JPA to throw exception
        // hence modifiable list is passed
        subCategory1.addToCategoryList(category2);  // mutable
        this.categoryService.updateCategory(category2);


        /**
         * when
         * delete
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         */
        this.categoryService.deleteAllCategories();


        /**
         * then
         * retrieve the category
         * retrieve the subcateories
         * verify that they are joined
         */
        /**
         * expected state in db after deleting {category1}
         *
         * --------------------------------------------
         * {subCategory1} has {}
         * {subCategory2} has {}
         */
        Optional<Category> updatedCategory1 = this.categoryService.getCategoryById(category1.getId());
        Optional<Category> updatedCategory2 = this.categoryService.getCategoryById(category2.getId());
        Optional<SubCategory> updatedSubCategory1 = this.subCategoryService.getSubCategoryById(subCategory1.getId());
        Optional<SubCategory> updatedSubCategory2 = this.subCategoryService.getSubCategoryById(subCategory2.getId());

        List<Category> updateCategoryList = this.categoryService.getAllCategories();
        List<SubCategory> updatedSubCategoryList = this.subCategoryService.getAllSubCategories();


        // check data is present
        assertTrue(updatedCategory1.isEmpty());
        assertTrue(updatedCategory2.isEmpty());
        assertTrue(updatedSubCategory1.isPresent());
        assertTrue(updatedSubCategory2.isPresent());


        // check they have joined entites
        assertEquals(0, updatedSubCategory1.get().getCategoryList().size());
        assertEquals(0, updatedSubCategory2.get().getCategoryList().size());

        // check no extra data created
        assertEquals(0, updateCategoryList.size());
        assertEquals(2, updatedSubCategoryList.size());
    }

    @Test
    void deleteCategories() throws DuplicateEntityException {
        /**
         * given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        Category category1 = this.savedCategories.get(0);
        Category category2 = this.savedCategories.get(1);
        SubCategory subCategory1 = this.savedSubCategories.get(0);
        SubCategory subCategory2 = this.savedSubCategories.get(1);

        /**
         * join between category and subcategory
         * update the category
         */
        List<SubCategory> subCategories1 = new ArrayList<>();
        subCategories1.add(subCategory1);
        subCategories1.add(subCategory2);

        category1.setSubCategoryList(subCategories1); // immutable list causes JPA to throw exception
        // hence modifiable list is passed
        subCategory1.addToCategoryList(category1);  // mutable
        subCategory2.addToCategoryList(category1);  // mutable
        this.categoryService.updateCategory(category1);


        List<SubCategory> subCategories2 = new ArrayList<>();
        subCategories2.add(subCategory1);

        category2.setSubCategoryList(subCategories2); // immutable list causes JPA to throw exception
        // hence modifiable list is passed
        subCategory1.addToCategoryList(category2);  // mutable
        this.categoryService.updateCategory(category2);


        /**
         * when
         * delete
         * {category2} has {subCategory1}
         */
        this.categoryService.deleteCategories(new ArrayList<>(List.of(category2)));


        /**
         * then
         * retrieve the category
         * retrieve the subcateories
         * verify that they are joined
         */
        /**
         * expected state in db after deleting {category1}
         *
         * --------------------------------------------
         * {subCategory1} has {category1}
         * {subCategory2} has {category1}
         */
        Optional<Category> updatedCategory1 = this.categoryService.getCategoryById(category1.getId());
        Optional<Category> updatedCategory2 = this.categoryService.getCategoryById(category2.getId());
        Optional<SubCategory> updatedSubCategory1 = this.subCategoryService.getSubCategoryById(subCategory1.getId());
        Optional<SubCategory> updatedSubCategory2 = this.subCategoryService.getSubCategoryById(subCategory2.getId());

        List<Category> updateCategoryList = this.categoryService.getAllCategories();
        List<SubCategory> updatedSubCategoryList = this.subCategoryService.getAllSubCategories();


        // check data is present
        assertTrue(updatedCategory1.isPresent());
        assertTrue(updatedCategory2.isEmpty());
        assertTrue(updatedSubCategory1.isPresent());
        assertTrue(updatedSubCategory2.isPresent());


        // check they have joined entites
        assertEquals(2, updatedCategory1.get().getSubCategoryList().size());
        assertEquals(1, updatedSubCategory1.get().getCategoryList().size());
        assertEquals(1, updatedSubCategory2.get().getCategoryList().size());

        // check no extra data created
        assertEquals(1, updateCategoryList.size());
        assertEquals(2, updatedSubCategoryList.size());
    }
}