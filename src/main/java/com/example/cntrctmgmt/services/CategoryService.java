package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.repositories.CategoryRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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


    public Category addCategory(Category category) throws DuplicateEntityException {
        Category saveCategory = null;
        try {
            saveCategory = this.categoryRepository.save(category);
        }catch (JpaSystemException jpaSystemException) {
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


    public Optional<Category> getCategoryById(int id) {
        return this.categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }


    public void updateCategory(Category category) throws DuplicateEntityException, EntityNotFoundException  {
        this.categoryRepository.findById(category.getId()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        try {
            this.categoryRepository.save(category);

        }catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());
                }

            }
        }
    }

    @Transactional
    public void deleteCategory(Category category) {

        for (SubCategory subCategory : category.getSubCategoryList()) {
            subCategory.getCategoryList().remove(category);
        }
        this.categoryRepository.delete(category);
    }


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
     * @param categories
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
