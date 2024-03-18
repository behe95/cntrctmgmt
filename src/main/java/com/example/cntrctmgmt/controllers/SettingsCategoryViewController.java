package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.constant.responsemessage.EndUserResponseMessage;
import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.InvalidInputException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Objects;

/**
 * This controller is responsible for setting up the category.
 * It is directly tied with the SettingsCategoryView.fxml that renders the settings
 * for the category section.
 * End user can select any of the categories, update and delete based on the selection.
 * End user can add a new category. Also, user can see the assigned list of sub-categories
 * for any selected category. Sub-categories can be un-assigned at any time if necessary.
 */
@Controller
public class SettingsCategoryViewController extends SettingsCategorySubCategoryTemplate<Category, SubCategory> {

    // List view to display all the categories
    @FXML
    private ListView<Category> listViewCategory;

    // List view to display all the available sub-categories
    // that can be assigned to any selected category
    @FXML
    private ListView<SubCategory> listViewAvailableSubCategory;

    // List view to display all the assigned sub-categories to
    // the currently selected categories
    @FXML
    private ListView<SubCategory> listViewAssignedSubCategory;

    // button icon to add a new category
    @FXML
    private Button btnAddNewCategory;

    // button icon to delete a selected category
    @FXML
    private Button btnDeleteCategory;

    // button icon to save or a update a selected category
    @FXML
    private Button btnSaveCategory;

    // check box to select if the category is a soft cost or not
    @FXML
    private CheckBox checkBoxSoftCost;


    @Autowired
    public SettingsCategoryViewController(CategoryService categoryService, SubCategoryService subCategoryService) {
        // service to interact with repository
        super(categoryService, subCategoryService);

    }


    @FXML
    protected void initialize() {


        // category change
        currentSelected.addListener(categoryChangeListener());

        // Get all the categories
        parentObservableList.setAll(categoryService.getAllCategories());
        // Get all the subcategories
        childObservableList.setAll(subCategoryService.getAllSubCategories());

        super.initializeButton(btnAddNewCategory, btnSaveCategory, btnDeleteCategory);
        super.initializeListView(listViewCategory, listViewAvailableSubCategory, listViewAssignedSubCategory);

        // populate list views
        super.setupCellFactoryListViewParent();
        super.setupCellFactoryListViewAvailableChildren();
        setupCellFactoryListViewAssignedChildren();

    }

    /**
     * Event handler for add button
     *
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnAddNewCategory(ActionEvent event) {
        addNewItemCategory();
//        if (listViewCategory.isFocused()) {
//            addNewItemCategory();
//        } else if (listViewAvailableSubCategory.isFocused()) {
//            addNewItemSubCategory();
//        }
    }


    /**
     * Event handler for delete button
     *
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnDeleteCategory(ActionEvent event) {
        deleteCategory(event);
    }

    /**
     * Event handler for save or update button
     *
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnSaveCategory(ActionEvent event) {
        saveOrUpdateCategory();
    }

    /**
     * Event handler for soft cost selection check box
     *
     * @param event Event that triggers this handler
     */
    @FXML
    void checkBoxSoftCostHandler(ActionEvent event) {
        listViewCategory.getSelectionModel().getSelectedItems().forEach(category -> category.setSoftCost(checkBoxSoftCost.isSelected()));
    }


    /**
     * A change listener that get triggered when end user change any category
     *
     * @return a change listener
     */
    private ChangeListener<Category> categoryChangeListener() {
        return (observableValue, oldCategory, newCategory) -> {
            if (oldCategory != newCategory && Objects.nonNull(newCategory)) {
                // gets all the available sub-categories that can be assigned
                if (!availableToBeAssigned.containsKey(newCategory)) {
                    availableToBeAssigned.put(
                            newCategory, FXCollections.observableArrayList(
                                    childObservableList
                                            .filtered(
                                                    availableSubCategory -> newCategory.getSubCategoryList().stream()
                                                            .noneMatch(assignedSubCategory -> assignedSubCategory.getId() == availableSubCategory.getId())
                                            )
                            )
                    );
                }
                // populate the ListViews with available and assigned sub-categories
                Category category = currentSelected.get();
                listViewAvailableChildren.setItems(availableToBeAssigned.get(category));
                listViewAssignedChildren.setItems(category.getSubCategoryList());
                checkBoxSoftCost.setSelected(category.getSoftCost());
            }

            // if there is no selection show empty list views
            // default view
            if (Objects.isNull(currentSelected.get())) {
                listViewAvailableChildren.setItems(null);
                listViewAssignedChildren.setItems(null);
                checkBoxSoftCost.setSelected(false);
            }

            // Disable the icon button if no category found for selection
            if (Objects.isNull(currentSelected.get())) {
                btnDeleteParent.setDisable(true);
                btnSaveParent.setDisable(true);
            } else {
                btnDeleteParent.setDisable(false);
                btnSaveParent.setDisable(false);

            }
        };
    }






    /**
     * An event handler which is part of a menu item. Triggered on action and save or update a category.
     * This method may add or update multiple categories if multiple categories are selected for
     * saving or updating at once
     *
     * @return A flag if the save or update is successful or not.
     */
    private boolean saveOrUpdateCategory() {
        return saveOrUpdateParent();
    }

    /**
     * Delete a category from the list-cell
     *
     * @param actionEvent Any event triggered on the main item that contains the event handler
     */
    private void deleteCategory(ActionEvent actionEvent) {
        deleteParent(actionEvent);
    }

    /**
     * Add a new category to the category list.
     */
    private void addNewItemCategory() {
        Category newCategory = new Category();
        addParent(newCategory);
    }


    @Override
    public void onDeleteParent() {

        // delete categories
        categoryService.deleteCategories(listViewParent.getSelectionModel().getSelectedItems());
    }

    @Override
    protected String onParentDeleteSuccessShow() {
        return EndUserResponseMessage.CATEGORY_DELETED.getMessage();
    }

    @Override
    protected String onParentDeleteFailureShow() {
        return EndUserResponseMessage.CATEGORY_DELETED_ERROR.getMessage();
    }


    @Override
    protected void onSaveOrUpdateParent() throws DuplicateEntityException {
        categoryService.addAllCategories(listViewParent.getSelectionModel().getSelectedItems());
    }

    @Override
    protected void onSaveOrUpdateParentValidate() throws InvalidInputException {
        for (Category category :
                listViewParent.getSelectionModel().getSelectedItems()) {
            if (Objects.isNull(category.getTitle())
                    || category.getTitle().equals("")) {
                throw new InvalidInputException(ExceptionMessage.EMPTY_INPUT_ERROR.getMessage());
            }
        }
    }

    @Override
    protected String onParentSaveOrUpdateSuccessShow() {
        return EndUserResponseMessage.CATEGORY_SAVED.getMessage();
    }

    @Override
    protected String onParentSaveOrUpdateFailureShow() {
        return EndUserResponseMessage.CATEGORY_SAVED_ERROR.getMessage();
    }

    @Override
    protected Category onAddNewParentCreateNewParent() {
        return new Category();
    }
}
