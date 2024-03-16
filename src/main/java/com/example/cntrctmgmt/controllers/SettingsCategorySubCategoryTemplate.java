package com.example.cntrctmgmt.controllers;

import com.example.cntrctmgmt.constant.responsemessage.EndUserResponseMessage;
import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.InvalidInputException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Controller
public abstract class SettingsCategorySubCategoryTemplate<P, C> {

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
        this.listViewParent = listViewParent;
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

    protected void addParent(P parent) {
        listViewParent.itemsProperty().get().add(parent);
        // clear any previous selection
        listViewParent.getSelectionModel().clearSelection();
        // select new item
        listViewParent.getSelectionModel().select(parent);
        currentSelected.set(parent);


        int newAddedSubCategoryIdx = listViewParent.itemsProperty().get().size() - 1;
        listViewParent.layout();
        listViewParent.scrollTo(newAddedSubCategoryIdx);
        listViewParent.getFocusModel().focus(newAddedSubCategoryIdx);


        listViewParent.edit(newAddedSubCategoryIdx);
    }

    protected boolean saveOrUpdateParent() {

        boolean isSaved = true;
        try {

            onSaveOrUpdateParentValidate();

            onSaveOrUpdateParent();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(onParentSaveOrUpdateSuccessShow());
            alert.showAndWait();
        } catch (DuplicateEntityException e) {
            isSaved = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(onParentSaveOrUpdateFailureShow() + " " + e.getMessage());
            alert.showAndWait();
        } catch (InvalidInputException e) {
            isSaved = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return isSaved;
    }

    protected abstract void onSaveOrUpdateParent() throws DuplicateEntityException;

    protected abstract void onSaveOrUpdateParentValidate() throws InvalidInputException;

    protected abstract String onParentSaveOrUpdateSuccessShow();

    protected abstract String onParentSaveOrUpdateFailureShow();


    protected void deleteParent(ActionEvent actionEvent) {
        try {
            onDeleteParent();
            // get the index of the deleted category
            int selectedIdx = listViewParent.getSelectionModel().getSelectedIndices().stream().min(Integer::compareTo).orElse(-1);


            // clean up the available categories
            for (P key : listViewParent.getSelectionModel().getSelectedItems()) {
                // remove the assigned sub-categories
                availableToBeAssigned.get(key).clear();
                // remove the category
                availableToBeAssigned.remove(key);
            }

            // remove it from the category ListView
            listViewParent.itemsProperty().get().removeAll(listViewParent.getSelectionModel().getSelectedItems());
            // clear selection
            listViewParent.getSelectionModel().clearSelection();

            // if there are still categories left, change the selection
            // and focus to either next or previous category relative to the
            // index that were removed
            if (listViewParent.itemsProperty().get().size() > 0) {
                if (selectedIdx >= listViewParent.itemsProperty().get().size()) {
                    selectedIdx = selectedIdx - 1;
                }
                listViewParent.getSelectionModel().select(selectedIdx);
                listViewParent.getFocusModel().focus(selectedIdx);
                // set the current selected category
                currentSelected.set(listViewParent.getSelectionModel().getSelectedItem());
            } else {
                // set current category
                currentSelected.set(null);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(onParentDeleteSuccessShow());
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(onParentDeleteFailureShow());
            alert.showAndWait();
        }
    }

    public abstract void onDeleteParent();

    protected abstract String onParentDeleteSuccessShow();

    protected abstract String onParentDeleteFailureShow();


    /**
     * Creates a Context menu for user interaction with a selected ListCell
     *
     * @param textFieldListCell ListCell that will be interacted
     * @return Context Menu
     */
    protected ContextMenu getCustomContextMenu(TextFieldListCell<P> textFieldListCell) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem saveMenutItem = new MenuItem("Save");
        MenuItem addMenutItem = new MenuItem("Add New");
        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem cancelMenuItem = new MenuItem("Cancel");

        // disable the following for any other cells that don't contain data
        if (textFieldListCell.isEmpty() || Objects.isNull(textFieldListCell.getItem())) {
            saveMenutItem.setDisable(true);
            editMenuItem.setDisable(true);
            deleteMenuItem.setDisable(true);
        }

        // save or update category
        // throws exception if duplicate category
        // is being saved
        saveMenutItem.setOnAction(actionEvent -> {
            boolean isSavedOrUpdated = saveOrUpdateParent();
            if (!isSavedOrUpdated) {
                textFieldListCell.startEdit();
            }
        });

        // delete category
        deleteMenuItem.setOnAction(actionEvent -> {
            textFieldListCell.listViewProperty().get().getSelectionModel().getSelectedItem();
            deleteParent(actionEvent);
        });

        // add a new category item at the end of the ListView
        // automatically focus and start editing once added
        addMenutItem.setOnAction(actionEvent -> addParent(onAddNewParentCreateNewParent()));

        // edit selected list-cell
        editMenuItem.setOnAction(actionEvent -> textFieldListCell.startEdit());

        // add menu items to the context menu
        contextMenu.getItems().addAll(saveMenutItem, addMenutItem, editMenuItem, deleteMenuItem, cancelMenuItem);

        return contextMenu;

    }

    protected abstract P onAddNewParentCreateNewParent();

}
