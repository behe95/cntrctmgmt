package com.example.cntrctmgmt.controllers;

import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;

@Controller
public abstract class SettingsCategorySubCategoryTemplate<P,C> {

    // service class to interact with service repository
    protected final CategoryService categoryService;
    // service class to interact with sub-category repository
    protected final SubCategoryService subCategoryService;

    // contains all the categories
    // this is a reference list which is not tied to any rendered nodes
    protected final ObservableList<Category> categoryObservableList;

    // contains all the sub-categories
    protected final ObservableList<SubCategory> subCategoryObservableList;

    // currently selected category by the end user
    protected final ObjectProperty<P> currentSelected = new SimpleObjectProperty<>();

    // contains available children for each parent
    protected final HashMap<P, ObservableList<C>> availableToBeAssigned;

    public SettingsCategorySubCategoryTemplate(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;

        // data containers
        this.categoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.subCategoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.availableToBeAssigned = new HashMap<>();
    }
}
