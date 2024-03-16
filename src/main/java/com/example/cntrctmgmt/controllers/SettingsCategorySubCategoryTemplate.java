package com.example.cntrctmgmt.controllers;

import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import org.springframework.stereotype.Controller;

@Controller
public abstract class SettingsCategorySubCategoryTemplate {

    // service class to interact with service repository
    protected final CategoryService categoryService;
    // service class to interact with sub-category repository
    protected final SubCategoryService subCategoryService;

    public SettingsCategorySubCategoryTemplate(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
    }
}
