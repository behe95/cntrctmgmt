package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class CategoryServiceTest {
    private final CategoryService categoryService;

    @Autowired
    CategoryServiceTest(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @Test
    void addCategory() {
        Category category = new Category();
        category.setTitle("Labor");
        category.setSoftCost(false);
        Category saveCategory = Assertions.assertDoesNotThrow(() -> this.categoryService.addCategory(category));
        Assertions.assertEquals(category.getTitle(), saveCategory.getTitle());

        // add extra
        try {
            this.categoryService.addCategory(new Category("Cnstrctn", false));
        } catch (DuplicateEntityException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addCategoryToCheckIfDuplicateEntityChecked() {
        Category category = new Category();
        category.setTitle("Labor");
        category.setSoftCost(false);
        Assertions.assertThrows(DuplicateEntityException.class, () -> this.categoryService.addCategory(category));
    }

    @Test
    void getCategoryById() {
        Optional<Category> category = this.categoryService.getCategoryById(1);
        Assertions.assertTrue(category.isPresent());
    }

    @Test
    void getAllCategories() {
        List<Category> categoryList = this.categoryService.getAllCategories();
        Assertions.assertEquals(2,categoryList.size());
    }

    @Test
    void updateCategory() {
        Category category = new Category();
        category.setTitle("Meal");
        category.setSoftCost(false);
        category.setPkcmCategory(2);
        Assertions.assertDoesNotThrow(() -> this.categoryService.updateCategory(category));

        Optional<Category> updatedCategory = this.categoryService.getCategoryById(2);

        Assertions.assertEquals("Meal", updatedCategory.get().getTitle());

    }

    @Test
    void updateCategoryToCheckIfDuplicateEntityChecked() {
        Category category = new Category();
        category.setTitle("Meal");
        category.setSoftCost(false);
        category.setPkcmCategory(1);
        Assertions.assertThrows(DuplicateEntityException.class, () -> this.categoryService.updateCategory(category));
    }

    @Test
    void updateCategoryToCheckIfValidateCategoryNotExists() {
        Category category = new Category();
        category.setTitle("Meal");
        category.setSoftCost(false);
        category.setPkcmCategory(3);
        Assertions.assertThrows(EntityNotFoundException.class, () -> this.categoryService.updateCategory(category));
    }

    @Test
    void deleteCategory() {
    }
}