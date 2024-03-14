package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This controller is responsible for setting up sub-category.
 * This is directly tied with the SettingsSubCategoryView.fxml that renders
 * the settings for the sub-category.
 * End user can select any of the sub-categories, update and delete based on the selection.
 * End user can add a new sub-category. Also, user can see the assigned list of categories
 * for any selected sub-category. Sub-categories can be un-assigned at any time if necessary.
 */
@Controller
public class SettingsSubCategoryViewController {

    // service class to interact with service repository
    private final CategoryService categoryService;

    // service class to interact with sub-category repository
    private final SubCategoryService subCategoryService;

    // contains all the categories
    // this is a reference list which is not tied to any rendered nodes
    private final ObservableList<Category> categoryObservableList;

    // contains all the sub-categories
    private final ObservableList<SubCategory> subCategoryObservableList;

    // contains available categories for each sub-category
    private final HashMap<SubCategory, ObservableList<Category>> availableCategories;

    // currently selected sub-category by the end user
    private final ObjectProperty<SubCategory> currentSelectedSubCategory = new SimpleObjectProperty<>();

    // button icon to add a new sub-category
    @FXML
    private Button btnAddNewSubCategory;

    // button icon to delete one or multiple selected sub-categories
    @FXML
    private Button btnDeleteSubCategory;

    // button icon to save or update single or multiple sub-categories
    @FXML
    private Button btnSaveSubCategory;

    // List view to display all the assigned categories to
    // the currently selected sub-category
    @FXML
    private ListView<Category> listViewAssignedCategory;

    // List view to display all the available categories
    // that can be assigned to any selected sub-categories
    @FXML
    private ListView<Category> listViewAvailableCategory;

    // List view to display all the sub-categories
    @FXML
    private ListView<SubCategory> listViewSubCategory;


    public SettingsSubCategoryViewController(CategoryService categoryService, SubCategoryService subCategoryService) {
        // service to interact with repository
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;

        // data containers
        this.availableCategories = new HashMap<>();
        this.categoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.subCategoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
    }

    @FXML
    private void initialize() {
        // initially disable the icons to interact
        // no sub-category selected initially
        btnSaveSubCategory.setDisable(true);
        btnDeleteSubCategory.setDisable(true);

        // sub-category change
        currentSelectedSubCategory.addListener(subCategoryChangeListener());

        // Get all the categories
        categoryObservableList.setAll(categoryService.getAllCategories());
        // Get all the sub-categories
        subCategoryObservableList.setAll(subCategoryService.getAllSubCategories());

        // populate ListView for all the sub-categories
        listViewSubCategory.setItems(subCategoryObservableList);
        // select the first item for the first time by default
        listViewSubCategory.getSelectionModel().selectFirst();
        currentSelectedSubCategory.set(listViewSubCategory.getSelectionModel().getSelectedItem());



    }

    private ChangeListener<? super SubCategory> subCategoryChangeListener() {
        return (ChangeListener<SubCategory>) (observableValue, oldSubCategory, newSubCategory) -> {
            if (oldSubCategory != newSubCategory && Objects.nonNull(newSubCategory))
            {
                // get all the available categories that can be assigned to
                // a selected sub-category
                if (!availableCategories.containsKey(newSubCategory)) {
                    availableCategories.put(
                            newSubCategory, FXCollections.observableArrayList(
                                    categoryObservableList
                                            .filtered(
                                                    availableCategory -> newSubCategory.getCategoryList().stream()
                                                            .noneMatch(assignedCategory -> assignedCategory.getId() == availableCategory.getId())
                                            )
                            )
                    );
                    System.out.println(newSubCategory);
                }

                // populate the ListViews with available and assigned categories
                SubCategory subCategory = currentSelectedSubCategory.get();
                listViewAvailableCategory.setItems(availableCategories.get(subCategory));
                listViewAssignedCategory.setItems(subCategory.getCategoryList());
            }

            // if there is no selection show empty list views
            // default view
            if (Objects.isNull(currentSelectedSubCategory.get())) {
                listViewAvailableCategory.setItems(null);
                listViewAssignedCategory.setItems(null);
            }

            // Disable the icon button if no sub-category found for selection
            if (Objects.isNull(currentSelectedSubCategory.get())) {
                btnSaveSubCategory.setDisable(true);
                btnDeleteSubCategory.setDisable(true);
            } else {
                btnSaveSubCategory.setDisable(false);
                btnDeleteSubCategory.setDisable(false);
            }
        };
    }

    @FXML
    void onActionBtnAddNewCategory(ActionEvent event) {

    }

    @FXML
    void onActionBtnDeleteCategory(ActionEvent event) {

    }

    @FXML
    void onActionBtnSaveCategory(ActionEvent event) {

    }

}
