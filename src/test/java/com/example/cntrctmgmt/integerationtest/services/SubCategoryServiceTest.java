package com.example.cntrctmgmt.integerationtest.services;


import com.example.cntrctmgmt.entities.*;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
@Rollback
class SubCategoryServiceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;

    Faker faker = Faker.instance();

    @Test
    void addSubCategory() {


        // subcategory

        SubCategory subCategory1 = new SubCategory();
        subCategory1.setTitle(faker.animal().name());

        SubCategory subCategory2 = new SubCategory();
        subCategory2.setTitle(faker.name().name());
    }

    @Test
    void getSubCategoryById() {
    }

    @Test
    void getAllSubCategories() {
    }

    @Test
    void updateSubCategory() {
    }

    @Test
    void deleteSubCategory() {
    }

    @Test
    void deleteAllSubCategories() {
    }
}