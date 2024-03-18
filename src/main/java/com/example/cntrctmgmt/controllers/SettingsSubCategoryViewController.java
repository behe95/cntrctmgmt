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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
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


        super.initializeButton(btnAddNewSubCategory, btnSaveSubCategory, btnDeleteSubCategory);
        super.initializeListView(listViewSubCategory, listViewAvailableCategory, listViewAssignedCategory);


        // setup the list views
        // setup corresponding list cells
        super.setupCellFactoryListViewParent();
        super.setupCellFactoryListViewAvailableChildren();
        setupCellFactoryListViewAssignedCategory();
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
     * All the assigned categories will be presented.
     * Method populates the ListView with all the assigned categories
     * except for the un-assigned categories.
     * End user can double-click on any of the categories which will move
     * the category to the ListView that contains un-assigned or available categories.
     */
    private void setupCellFactoryListViewAssignedCategory() {
        // Show SubCategory title in listviewAssignedCategory
        listViewAssignedCategory.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> categoryListView) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);
                        if (Objects.nonNull(category) && !empty) {
                            setText(category.getTitle());
                        } else {
                            setText(null);
                        }

                        // double click to un-assign the category
                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(category)) {
                                    unassign(category);
                                }
                            }
                        });
                    }
                };
            }
        });
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
                listViewAvailableCategory.setItems(availableToBeAssigned.get(subCategory));
                listViewAssignedCategory.setItems(subCategory.getCategoryList());
            }

            // if there is no selection show empty list views
            // default view
            if (Objects.isNull(currentSelected.get())) {
                listViewAvailableCategory.setItems(null);
                listViewAssignedCategory.setItems(null);
            }

            // Disable the icon button if no sub-category found for selection
            if (Objects.isNull(currentSelected.get())) {
                btnSaveSubCategory.setDisable(true);
                btnDeleteSubCategory.setDisable(true);
            } else {
                btnSaveSubCategory.setDisable(false);
                btnDeleteSubCategory.setDisable(false);
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
