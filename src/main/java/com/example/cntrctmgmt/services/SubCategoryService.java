package com.example.cntrctmgmt.services;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.UnknownException;
import com.example.cntrctmgmt.repositories.SubCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteException;

import java.util.List;
import java.util.Optional;

@Service
public class SubCategoryService {
    private final SubCategoryRepository subCategoryRepository;

    @Autowired
    public SubCategoryService(SubCategoryRepository subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }

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

    public Optional<SubCategory> getSubCategoryById(int id) {
        return this.subCategoryRepository.findById(id);
    }

    public List<SubCategory> getAllSubCategories() {
        return this.subCategoryRepository.findAll();
    }


    @Transactional
    public void updateSubCategory(SubCategory subCategory) throws DuplicateEntityException, EntityNotFoundException, UnknownException {
        SubCategory savedSubCategory = this.subCategoryRepository.findById(subCategory.getId()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.getMessage()));

        try {
            savedSubCategory.setTitle(subCategory.getTitle());
            this.subCategoryRepository.save(savedSubCategory);

        }catch (JpaSystemException jpaSystemException) {
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

    public void deleteSubCategory(SubCategory subCategory) {
        this.subCategoryRepository.delete(subCategory);
    }


    @Transactional
    public void deleteAllSubCategories() {
        this.subCategoryRepository.deleteAll();
    }
}
