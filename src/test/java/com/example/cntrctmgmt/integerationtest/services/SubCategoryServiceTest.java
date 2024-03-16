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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class SubCategoryServiceTest {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;


    enum KEYS {
        CATEGORY1, CATEGORY2, SUBCATEGORY1, SUBCATEGORY2
    }

    EnumMap<KEYS, Category> categoryEnumMap = new EnumMap<KEYS, Category>(KEYS.class);
    EnumMap<KEYS, SubCategory> subCategoryEnumMap = new EnumMap<KEYS, SubCategory>(KEYS.class);

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


        // subcategory

        SubCategory subCategory1 = new SubCategory();
        subCategory1.setTitle(faker.animal().name());

        SubCategory subCategory2 = new SubCategory();
        subCategory2.setTitle(faker.name().name());


        /**
         * Join
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        category1.setSubCategoryList(new ArrayList<>(List.of(subCategory1, subCategory2)));
        subCategory1.addToCategoryList(category1);
        subCategory2.addToCategoryList(category1);

        category2.setSubCategoryList(new ArrayList<>(List.of(subCategory1)));
        subCategory1.addToCategoryList(category2);

        // save category
        Category returnedCategory1 = this.categoryService.addCategory(category1);
        Category returnedCategory2 = this.categoryService.addCategory(category2);

        // save subcategory
        SubCategory returnedSubCategory1 = this.subCategoryService.addSubCategory(subCategory1);
        SubCategory returnedSubCategory2 = this.subCategoryService.addSubCategory(subCategory2);

        /**
         * Store for references
         */
        categoryEnumMap.put(KEYS.CATEGORY1, returnedCategory1);
        categoryEnumMap.put(KEYS.CATEGORY2, returnedCategory2);
        subCategoryEnumMap.put(KEYS.SUBCATEGORY1, returnedSubCategory1);
        subCategoryEnumMap.put(KEYS.SUBCATEGORY2, returnedSubCategory2);

    }


    @Test
    void getAllSubCategories() {

        /**
         * given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */

        /**
         * when
         */
        List<SubCategory> subCategories = this.subCategoryService.getAllSubCategories();

        /**
         * then
         */
        int expecetedSize = 2;
        assertEquals(expecetedSize, subCategories.size());

    }

    @Test
    void updateSubCategory() throws DuplicateEntityException, UnknownException {

        /**
         * given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        SubCategory subCategory = subCategoryEnumMap.get(KEYS.SUBCATEGORY1);
        subCategory.setTitle(faker.company().name());

        /**
         * when
         */
        this.subCategoryService.updateSubCategory(subCategory);

        /**
         * then
         */
        // get the category1 and check if the associated subCategory1 is updated
        Category category1 = categoryEnumMap.get(KEYS.CATEGORY1);
        SubCategory subCategory1 = category1.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory.getId()).findAny().orElse(null);

        assert subCategory1 != null;
        assertEquals(subCategory.getTitle(), subCategory1.getTitle());

    }

    @Test
    void deleteSubCategory() {
        /**
         * given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        SubCategory subCategory = subCategoryEnumMap.get(KEYS.SUBCATEGORY1);
        subCategory.setTitle(faker.company().name());

        /**
         * when
         */
        this.subCategoryService.deleteSubCategory(subCategory);

        /**
         * then
         * Check if the association with category1 and category2
         * has been removed too - in the persistence context
         */
        Category category1 = categoryEnumMap.get(KEYS.CATEGORY1);
        Category category2 = categoryEnumMap.get(KEYS.CATEGORY2);

        SubCategory subCategory1FoundInCategory1 = category1.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory.getId()).findAny().orElse(null);
        SubCategory subCategory1FoundInCategory2 = category2.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory.getId()).findAny().orElse(null);

        assertNull(subCategory1FoundInCategory1);
        assertNull(subCategory1FoundInCategory2);

        this.subCategoryService.getAllSubCategories();

        /**
         * Check there is no association in db
         */
        List<Category> categories = this.categoryService.getAllCategories();

        assertEquals(2, categories.size());
        Category c1 = categories.get(0);
        Category c2 = categories.get(1);

        SubCategory sc1 = c1.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory.getId()).findAny().orElse(null);
        SubCategory sc2 = c2.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory.getId()).findAny().orElse(null);

        assertNull(sc1);
        assertNull(sc2);
    }

    @Test
    void deleteAllSubCategories() {
        /**
         * given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        /**
         * when
         */
        this.subCategoryService.deleteAllSubCategories();

        /**
         * then
         * Check there is sub categories in persistence context
         */
        Category category1 = categoryEnumMap.get(KEYS.CATEGORY1);
        Category category2 = categoryEnumMap.get(KEYS.CATEGORY2);
        assertEquals(0, category1.getSubCategoryList().size());
        assertEquals(0, category2.getSubCategoryList().size());

        /**
         * Check there is no sub categories in database
         */
        SubCategory subCategory1 = subCategoryEnumMap.get(KEYS.SUBCATEGORY1);
        SubCategory subCategory2 = subCategoryEnumMap.get(KEYS.SUBCATEGORY2);

        Optional<SubCategory> sc1 = this.subCategoryService.getSubCategoryById(subCategory1.getId());
        Optional<SubCategory> sc2 = this.subCategoryService.getSubCategoryById(subCategory2.getId());

        assertFalse(sc1.isPresent());
        assertFalse(sc2.isPresent());

        /**
         * there is no association in database
         */
        Category c1 = this.categoryService.getCategoryById(category1.getId()).orElse(null);
        Category c2 = this.categoryService.getCategoryById(category2.getId()).orElse(null);

        assert c1 != null;
        assertEquals(0, c1.getSubCategoryList().size());
        assert c2 != null;
        assertEquals(0, c2.getSubCategoryList().size());
    }

    @Test
    void deleteSubCategories() {
        /**
         * Given
         *
         * {category1} has {subCategory1, subCategory2}
         * {category2} has {subCategory1}
         * --------------------------------------------
         * {subCategory1} has {category1,category2}
         * {subCategory2} has {category1}
         */
        SubCategory subCategory1 = subCategoryEnumMap.get(KEYS.SUBCATEGORY1);
        SubCategory subCategory2 = subCategoryEnumMap.get(KEYS.SUBCATEGORY2);
        List<SubCategory> subCategories = new ArrayList<>(List.of(subCategory1, subCategory2));

        /**
         * when
         */
        this.subCategoryService.deleteSubCategories(subCategories);

        /**
         * then
         * Check if the association with category1 and category2
         * has been removed too - in the persistence context
         */
        Category category1 = categoryEnumMap.get(KEYS.CATEGORY1);
        Category category2 = categoryEnumMap.get(KEYS.CATEGORY2);
        SubCategory subCategory1FoundInCategory1 = category1.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory1.getId()).findAny().orElse(null);
        SubCategory subCategory2oundInCategory1 = category1.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory2.getId()).findAny().orElse(null);
        SubCategory subCategory1FoundInCategory2 = category2.getSubCategoryList().stream().filter(sc -> sc.getId() == subCategory1.getId()).findAny().orElse(null);

        assertNull(subCategory1FoundInCategory1);
        assertNull(subCategory2oundInCategory1);
        assertNull(subCategory1FoundInCategory2);


        /**
         * Check there is no sub sub-categories in database
         */

        Optional<SubCategory> sc1 = this.subCategoryService.getSubCategoryById(subCategory1.getId());
        Optional<SubCategory> sc2 = this.subCategoryService.getSubCategoryById(subCategory2.getId());

        assertFalse(sc1.isPresent());
        assertFalse(sc2.isPresent());

        /**
         * there is no association in database
         */
        Category c1 = this.categoryService.getCategoryById(category1.getId()).orElse(null);
        Category c2 = this.categoryService.getCategoryById(category2.getId()).orElse(null);

        assert c1 != null;
        assertEquals(0, c1.getSubCategoryList().size());
        assert c2 != null;
        assertEquals(0, c2.getSubCategoryList().size());
    }
}