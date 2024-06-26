package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.UnknownException;
import com.example.cntrctmgmt.repositories.SubCategoryRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteException;

import java.util.List;
import java.util.Optional;

@Service
public class SubCategoryService {

    @PersistenceContext
    private EntityManager entityManager;
    private final SubCategoryRepository subCategoryRepository;

    @Autowired
    public SubCategoryService(SubCategoryRepository subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }

    /**
     * Add sub-category to the database
     *
     * @param subCategory Sub-category to add
     * @return Saved sub-category
     * @throws DuplicateEntityException If the sub-category with the same name already exists
     * @throws UnknownException         If any unknown exception found
     */
    public SubCategory addSubCategory(SubCategory subCategory) throws DuplicateEntityException, UnknownException {
        SubCategory savedSubCategory = null;
        try {
            savedSubCategory = this.subCategoryRepository.save(subCategory);
        } catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());
                } else {
                    throw new UnknownException(ExceptionMessage.UNKNOWN_EXCEPTION.getMessage());
                }

            }
        }

        return savedSubCategory;
    }

    /**
     * Add sub-categories to the database
     *
     * @param subCategories Sub-Categories to add
     * @return Saved sub-categories
     * @throws DuplicateEntityException If any sub-categories with the same name already exists
     */
    @Transactional(rollbackOn = DuplicateEntityException.class)
    public List<SubCategory> addAllSubCategories(List<SubCategory> subCategories) throws DuplicateEntityException {
        List<SubCategory> savedCategories = null;
        try {
            savedCategories = this.subCategoryRepository.saveAll(subCategories);
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
     * Retrieve the sub-category by id
     *
     * @param id ID to retrieve the sub-category
     * @return Retrieved sub-category
     */
    public Optional<SubCategory> getSubCategoryById(int id) {
        return this.subCategoryRepository.findById(id);
    }

    /**
     * Get list of all sub-categories
     *
     * @return List of sub-categories
     */
    public List<SubCategory> getAllSubCategories() {
        return this.subCategoryRepository.findAll();
    }


    /**
     * Save updated sub-category
     * @deprecated Unnecessary query to check if a record exists in the database. Instead, use {@link #addSubCategory(SubCategory)}
     *
     * @param subCategory Sub-category to update
     * @throws DuplicateEntityException If the updated sub-category's title already exists in the database
     * @throws EntityNotFoundException  If the sub-category to update is not found in the database
     * @throws UnknownException         If any unknown exception found
     */
    @Deprecated(since = "3/10/2024",forRemoval = true)
    @Transactional
    public void updateSubCategory(SubCategory subCategory) throws DuplicateEntityException, EntityNotFoundException, UnknownException {
        SubCategory savedSubCategory = this.subCategoryRepository.findById(subCategory.getId()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        try {
            savedSubCategory.setTitle(subCategory.getTitle());
            this.subCategoryRepository.save(savedSubCategory);

        } catch (JpaSystemException jpaSystemException) {
            if (jpaSystemException.getRootCause() instanceof SQLiteException) {
                int errorCode = ((SQLiteException) jpaSystemException.getRootCause()).getErrorCode();
                if (errorCode == 19) //SQLITE_CONSTRAINT
                {
                    throw new DuplicateEntityException(ExceptionMessage.DUPLICATE_ENTITY_EXCEPTION.getMessage());
                } else {
                    throw new UnknownException(ExceptionMessage.UNKNOWN_EXCEPTION.getMessage());
                }

            }
        }
    }

    /**
     * Delete sub-category from the database
     * Remove any association of the category with any other subcategories
     *
     * @param subCategory Sub-category to delete
     */
    public void deleteSubCategory(SubCategory subCategory) {
        for (Category category : subCategory.getCategoryList()) {
            category.getSubCategoryList().remove(subCategory);
        }
        this.subCategoryRepository.delete(subCategory);
    }

    /**
     * Delete all the sub-categories from the database
     */
    @Transactional
    public void deleteAllSubCategories() {
        List<SubCategory> subCategories = entityManager.createQuery("SELECT sc FROM SubCategory sc", SubCategory.class).getResultList();

        for (SubCategory subCategory : subCategories) {
            for (Category category : subCategory.getCategoryList()) {
                category.getSubCategoryList().remove(subCategory);
            }
        }
        this.subCategoryRepository.deleteAll();
    }

    /**
     * Delete multiple categories at once
     * Remove any association of the categories with any
     * other subcategories prior to deletion
     *
     * @param subCategories List of categories to delete
     */
    @Transactional
    public void deleteSubCategories(List<SubCategory> subCategories) {
        for (SubCategory subCategory : subCategories) {
            for (Category category : subCategory.getCategoryList()) {
                category.getSubCategoryList().remove(subCategory);
            }
        }

        this.subCategoryRepository.deleteAll(subCategories);
    }
}
