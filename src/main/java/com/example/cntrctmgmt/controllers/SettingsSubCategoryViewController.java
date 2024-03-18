package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.constant.responsemessage.EndUserResponseMessage;
import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.InvalidInputException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;

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
public class SettingsSubCategoryViewController extends SettingsCategorySubCategoryTemplate<SubCategory, Category> {





    public SettingsSubCategoryViewController(CategoryService categoryService, SubCategoryService subCategoryService) {
        // service to interact with repository
        super(categoryService, subCategoryService);
    }


    @FXML
    protected void initialize() {

        // sub-category change
        currentSelected.addListener(subCategoryChangeListener());

        // Get all the categories
        parentObservableList.setAll(subCategoryService.getAllSubCategories());
        // Get all the sub-categories
        childObservableList.setAll(categoryService.getAllCategories());




        super.initialize();
    }

    /**
     * Event handler for add button
     *
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnAddNewSubCategory(ActionEvent event) {
        addNewItemSubCategory();
    }


    /**
     * Event handler for delete button
     *
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnDeleteSubCategory(ActionEvent event) {
        deleteSubCategory(event);
    }

    /**
     * Event handler for save or update button
     *
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnSaveSubCategory(ActionEvent event) {
        saveOrUpdateSubCategory(event, currentSelected.get());
    }



    /**
     * An event handler which is part of a menu item. Triggered on action and save or update a sub-category.
     * This method may add or update multiple sub-categories if multiple sub-categories are selected for
     * saving or updating at once
     *
     * @param actionEvent Any event triggered on the main item that contains the event handler
     * @param subCategory Category to save or update
     * @return A flag if the save or update is successful or not.
     */
    private boolean saveOrUpdateSubCategory(ActionEvent actionEvent, SubCategory subCategory) {
        return saveOrUpdateParent();
    }


    /**
     * Delete a sub-category or multiple sub-categories from the list-cell
     *
     * @param actionEvent Any event triggered on the main item that contains the event handler
     */
    private void deleteSubCategory(ActionEvent actionEvent) {
        deleteParent(actionEvent);
    }

    private ChangeListener<? super SubCategory> subCategoryChangeListener() {
        return (ChangeListener<SubCategory>) (observableValue, oldSubCategory, newSubCategory) -> {
            if (oldSubCategory != newSubCategory && Objects.nonNull(newSubCategory)) {
                // get all the available categories that can be assigned to
                // a selected sub-category
                if (!availableToBeAssigned.containsKey(newSubCategory)) {
                    availableToBeAssigned.put(
                            newSubCategory, FXCollections.observableArrayList(
                                    childObservableList
                                            .filtered(
                                                    availableCategory -> newSubCategory.getCategoryList().stream()
                                                            .noneMatch(assignedCategory -> assignedCategory.getId() == availableCategory.getId())
                                            )
                            )
                    );
                }

                // populate the ListViews with available and assigned categories
                SubCategory subCategory = currentSelected.get();
                listViewAvailableChildren.setItems(availableToBeAssigned.get(subCategory));
                listViewAssignedChildren.setItems(subCategory.getCategoryList());
            }

            // if there is no selection show empty list views
            // default view
            if (Objects.isNull(currentSelected.get())) {
                listViewAvailableChildren.setItems(null);
                listViewAssignedChildren.setItems(null);
            }

            // Disable the icon button if no sub-category found for selection
            if (Objects.isNull(currentSelected.get())) {
                btnSaveParent.setDisable(true);
                btnDeleteParent.setDisable(true);
            } else {
                btnSaveParent.setDisable(false);
                btnDeleteParent.setDisable(false);
            }
        };
    }


    /**
     * Add a new category to the category list.
     */
    private void addNewItemSubCategory() {
        SubCategory newSubCategory = new SubCategory();
        addParent(newSubCategory);
    }


    @Override
    public void onDeleteParent() {
        // delete categories
        subCategoryService.deleteSubCategories(listViewParent.getSelectionModel().getSelectedItems());
    }

    @Override
    protected String onParentDeleteSuccessShow() {
        return EndUserResponseMessage.SUBCATEGORY_DELETED.getMessage();
    }

    @Override
    protected String onParentDeleteFailureShow() {
        return EndUserResponseMessage.SUBCATEGORY_DELETED_ERROR.getMessage();
    }


    @Override
    protected void onSaveOrUpdateParent() throws DuplicateEntityException {
        subCategoryService.addAllSubCategories(listViewParent.getSelectionModel().getSelectedItems());
    }

    @Override
    protected void onSaveOrUpdateParentValidate() throws InvalidInputException {
        for (SubCategory subCategory :
                listViewParent.getSelectionModel().getSelectedItems()) {
            if (Objects.isNull(subCategory.getTitle())
                    || subCategory.getTitle().equals("")) {
                throw new InvalidInputException(ExceptionMessage.EMPTY_INPUT_ERROR.getMessage());
            }
        }
    }

    @Override
    protected String onParentSaveOrUpdateSuccessShow() {
        return EndUserResponseMessage.SUBCATEGORY_SAVED.getMessage();
    }

    @Override
    protected String onParentSaveOrUpdateFailureShow() {
        return EndUserResponseMessage.SUBCATEGORY_SAVED_ERROR.getMessage();
    }


    @Override
    protected SubCategory onAddNewParentCreateNewParent() {
        return new SubCategory();
    }
}
