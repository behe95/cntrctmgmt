package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.repositories.CategoryRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    private EntityManager entityManager;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    /**
     * Add category to the database
     *
     * @param category Category to add
     * @return Saved category
     * @throws DuplicateEntityException If the category with the same name already exists
     */
    public Category addCategory(Category category) throws DuplicateEntityException {
        Category saveCategory = null;
        try {
            saveCategory = this.categoryRepository.save(category);
        } catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());
                }

            }
        }

        return saveCategory;
    }

    /**
     * Add categories to the database
     *
     * @param categories Categories to add
     * @return Saved categories
     * @throws DuplicateEntityException If any categories with the same name already exists
     */
    @Transactional
    public List<Category> addAllCategories(List<Category> categories) throws DuplicateEntityException {
        List<Category> savedCategories = null;
        try {
            savedCategories = this.categoryRepository.saveAll(categories);
        } catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());
                }
            }
        }

        return savedCategories;
    }


    /**
     * Retrieve category by id
     *
     * @param id ID to retrieve category
     * @return Retrieved category
     */
    public Optional<Category> getCategoryById(int id) {
        return this.categoryRepository.findById(id);
    }

    /**
     * Get list of all categories
     *
     * @return List of categories
     */
    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }


    /**
     * Save updated category
     * @deprecated Unnecessary query to check if a record exists in the database. Instead, use {@link #addCategory(Category)}
     *
     * @param category Category to update
     * @throws DuplicateEntityException If the updated category's title already exists in the database
     * @throws EntityNotFoundException  If the category to update is not found in the database
     */
    @Deprecated(since = "3/10/2024",forRemoval = true)
    public void updateCategory(Category category) throws DuplicateEntityException, EntityNotFoundException {
        this.categoryRepository.findById(category.getId()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        try {
            this.categoryRepository.save(category);

        } catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());
                }

            }
        }
    }

    /**
     * Delete category from the database
     * Remove any association of the category with any other subcategories
     *
     * @param category Category to delete
     */
    @Transactional
    public void deleteCategory(Category category) {

        for (SubCategory subCategory : category.getSubCategoryList()) {
            subCategory.getCategoryList().remove(category);
        }
        this.categoryRepository.delete(category);
    }


    /**
     * Delete all the categories from the database
     * Remove any association of the categories with any
     * other subcategories prior to deletion
     */
    @Transactional
    public void deleteAllCategories() {

        List<Category> categories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();

        for (Category category : categories) {
            for (SubCategory subCategory : category.getSubCategoryList()) {
                subCategory.getCategoryList().remove(category);
            }
        }

        this.categoryRepository.deleteAll();
    }

    /**
     * TODO:    May be Not working properly!!!
     *          Need to test with real data and in memory database
     *          Issue: skip few records when some records are deleted thorugh
     * <p>
     * Delete multiple categories at once
     * Remove any association of the categories with any
     * other subcategories prior to deletion
     *
     * @param categories List of categories to delete
     */
    @Transactional
    public void deleteCategories(List<Category> categories) {
        for (Category category : categories) {
            for (SubCategory subCategory : category.getSubCategoryList()) {
                subCategory.getCategoryList().remove(category);
            }
        }

        this.categoryRepository.deleteAll(categories);
    }
}
