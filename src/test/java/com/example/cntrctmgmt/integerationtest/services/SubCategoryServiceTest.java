package com.example.cntrctmgmt.integerationtest.services;


import com.example.cntrctmgmt.TestConfig;
import com.example.cntrctmgmt.entities.*;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.UnknownException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.ContractService;
import com.example.cntrctmgmt.services.SubCategoryService;
import com.example.cntrctmgmt.services.SubContractService;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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