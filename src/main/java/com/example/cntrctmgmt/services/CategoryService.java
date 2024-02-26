package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.RollbackException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public Category addCategory(Category category) throws DuplicateEntityException {
        Category saveCategory = null;
        try {
            this.categoryRepository.save(category);
            saveCategory = this.categoryRepository.save(category);
        }catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION);
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
        Category savedCategory = this.categoryRepository.findById(category.getPkcmCategory()).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        try {
            savedCategory.setTitle(category.getTitle());
            savedCategory.setSoftCost(category.getSoftCost());
            this.categoryRepository.save(savedCategory);

        }catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION);
                }

            }
        }
    }

    public void deleteCategory(Category category) {
        this.categoryRepository.delete(category);
    }


    public void deleteAllCategories() {
        this.categoryRepository.deleteAll();
    }

    /**
     * TODO:    Not working properly!!!
     * @param categories
     */
    public void deleteCategories(List<Category> categories) {
        this.categoryRepository.deleteAll(categories);
    }
}
