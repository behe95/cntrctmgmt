package com.example.cntrctmgmt.controllers;

import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
    protected final ObservableList<P> parentObservableList;

    // contains all the sub-categories
    protected final ObservableList<C> childObservableList;

    // currently selected category by the end user
    protected final ObjectProperty<P> currentSelected = new SimpleObjectProperty<>();

    // contains available children for each parent
    protected final HashMap<P, ObservableList<C>> availableToBeAssigned;

    // List view to display all the categories
    protected ListView<P> listViewParent;

    protected Button btnAddParent;
    protected Button btnSaveParent;
    protected Button btnDeleteParent;

    public SettingsCategorySubCategoryTemplate(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;

        // data containers
        this.parentObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.childObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.availableToBeAssigned = new HashMap<>();
    }

    protected final void initializeListView(ListView<P> listViewParent, ListView<C> listViewAvailableChildren, ListView<C> listViewAssignedChildren) {
        // populate ListView for all the categories
        listViewParent.setItems(parentObservableList);
        // select the first item for the first time by default
        listViewParent.getSelectionModel().selectFirst();
        currentSelected.set(listViewParent.getSelectionModel().getSelectedItem());
    }

    protected final void initializeButton(Button btnAddParent, Button btnSaveParent, Button btnDeleteParent) {
        this.btnAddParent = btnAddParent;
        this.btnSaveParent = btnSaveParent;
        this.btnDeleteParent = btnDeleteParent;

        // initially disable to icon to interact
        // no category selected
        this.btnSaveParent.setDisable(true);
        this.btnDeleteParent.setDisable(true);
    }

    /**
     * This method assign a child to a parent
     *
     * @param child Child to assign
     */
    protected void assign(C child) {
        P parent = currentSelected.get();
        // remove the sub-category from the available sub-category list
        availableToBeAssigned.get(parent).remove(child);
        if (parent instanceof Category category && child instanceof SubCategory subCategory) {
            // assign the sub-category to the selected category
            category.getSubCategoryList().add(subCategory);
            // assign the selected category to the sub-category that has been assigned
            // to make association in the persistence context
            subCategory.getCategoryList().add(category);
        } else if (parent instanceof SubCategory subCategory && child instanceof Category category) {
            // assign the category to the selected sub-category
            subCategory.getCategoryList().add(category);
            // assign the selected sub-category to the category that has been assigned
            // to make association in the persistence context
            category.getSubCategoryList().add(subCategory);
        }
    }

    /**
     * This method un-assign a child from a parent
     *
     * @param child Child to un-assign
     */
    protected void unassign(C child) {
        P parent = currentSelected.get();
        // add the sub-category to the list of available sub-categories
        availableToBeAssigned.get(parent).add(child);
        if (parent instanceof Category category && child instanceof SubCategory subCategory) {
            // remove the child from the assigned parent list
            category.getSubCategoryList().remove(subCategory);
            // remove the selected parent from the child that has been un-assigned
            // to remove the association in the persistence context
            subCategory.getCategoryList().remove(category);
        } else if (parent instanceof SubCategory subCategory && child instanceof Category category) {
            // remove the category from the assigned category list
            subCategory.getCategoryList().remove(category);
            // remove the selected sub-category from the category that has been un-assigned
            // to remove the association in the persistence context
            category.getSubCategoryList().remove(subCategory);
        }
    }
}
