package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.constant.responsemessage.EndUserResponseMessage;
import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.InvalidInputException;
import com.example.cntrctmgmt.exceptions.UnknownException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
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
        setupCellFactoryListViewCategory();
        setupCellFactoryListViewAvailableSubCategory();
        setupCellFactoryListViewAssignedSubCategory();

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
                listViewAvailableSubCategory.setItems(availableToBeAssigned.get(category));
                listViewAssignedSubCategory.setItems(category.getSubCategoryList());
                checkBoxSoftCost.setSelected(category.getSoftCost());
            }

            // if there is no selection show empty list views
            // default view
            if (Objects.isNull(currentSelected.get())) {
                listViewAvailableSubCategory.setItems(null);
                listViewAssignedSubCategory.setItems(null);
                checkBoxSoftCost.setSelected(false);
            }

            // Disable the icon button if no category found for selection
            if (Objects.isNull(currentSelected.get())) {
                btnDeleteCategory.setDisable(true);
                btnSaveCategory.setDisable(true);
            } else {
                btnDeleteCategory.setDisable(false);
                btnSaveCategory.setDisable(false);

            }
        };
    }


    /**
     * All the available categories will be presented.
     * Method populates the ListView with all the available categories
     * from queried from the database.
     * The ListView is editable.
     * Each ListCell is represented with TextFieldListCell to provide editing
     * option to the end user.
     * On selection of any category from the list, the ListView for the
     * assigned sub-categories and available sub-categories to assign to this category will be populated.
     * User can right-click on any of the ListCell that will pop-up a context menu for
     * further interaction.
     */
    private void setupCellFactoryListViewCategory() {
        // make ListView editable
        listViewCategory.setEditable(true);
        // multiple cell selection from the ListView
        listViewCategory.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // Show Category title in listviewCategory
        listViewCategory.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> categoryListView) {

                TextFieldListCell<Category> textFieldListCell = new TextFieldListCell<Category>() {

                    // Contains text value inside the listcell when in editing state
                    private StringProperty tempTextProperty = new SimpleStringProperty("");

                    // check if the list-cell editing state is turned off by pressing
                    // ESCAPE Key or not
                    boolean isCancelledEditingByEscapeKey = false;

                    // check if the list-cell editing state is turned off by
                    // pressing Enter key when committed
                    boolean isCommitedEditingByEnterKey = false;

                    // focused lost
                    boolean isFocusLost = false;

                    // change listener when focus lost
                    ChangeListener<? super Boolean> focusLostListener = new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasFocused, Boolean isFocused) {
                            if (!isFocused) {
                                cancelEdit();
                            }
                        }
                    };

                    // Temp event handler that handles the key press of list-cell's textfield when editing
                    EventHandler<KeyEvent> escapeKeyEventHandler = keyEvent -> {
                        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                            isCancelledEditingByEscapeKey = true;
                        }
                        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                            isCommitedEditingByEnterKey = true;
                        }
                    };

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        // contains the temp text-field inside the list-cell
                        if (Objects.nonNull(getGraphic())) {
                            TextField textField = (TextField) getGraphic();
                            // binding to capture the edited values
                            tempTextProperty.bind(textField.textProperty());
                            textField.addEventHandler(KeyEvent.KEY_PRESSED, escapeKeyEventHandler);
                            textField.focusedProperty().addListener(focusLostListener);
                        }


                    }

                    @Override
                    public void cancelEdit() {

                        // cleanup
                        if (Objects.nonNull(getGraphic()) && getGraphic() instanceof TextField textField) {
                            textField.focusedProperty().removeListener(focusLostListener);
                            textField.removeEventHandler(KeyEvent.KEY_PRESSED, escapeKeyEventHandler);
                        }


                        if (!isCancelledEditingByEscapeKey && !isCommitedEditingByEnterKey) {
                            // commit changes before cancelling
                            String editedValues = tempTextProperty.get();
                            setText(editedValues);
                            Category category = getItem();
                            category.setTitle(editedValues);
                            updateItem(category, false);
                            commitEdit(category);
                            listViewCategory.getSelectionModel().clearSelection();
                            currentSelected.set(null);
                        }


                        if (isCancelledEditingByEscapeKey) {
                            isCancelledEditingByEscapeKey = false;
                        }

                        if (isCommitedEditingByEnterKey) {
                            isCommitedEditingByEnterKey = false;
                        }

                        /**
                         * TODO:    if user left a cell blank remove it
                         */
//                        if (!isEmpty()
//                                && Objects.isNull(getItem().getTitle())
//                                && getItem().getSubCategoryList().size() == 0) {
//                            availableToBeAssigned.remove(getItem());
//                            parentObservableList.remove(getItem());
//                        }
                        super.cancelEdit();

                    }

                    @Override
                    public void commitEdit(Category category) {
                        if (!isEmpty()) {
                            super.commitEdit(category);
                            setText(category.getTitle());
                        }
                    }

                    // convert object to make it compatible for the TextFieldListCell
                    @Override
                    public void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);


                        setConverter(new StringConverter<Category>() {
                            @Override
                            public String toString(Category category) {
                                return Objects.nonNull(category) ? category.getTitle() : "";
                            }

                            @Override
                            public Category fromString(String s) {
                                getItem().setTitle(s);
                                return getItem();
                            }
                        });


                        // show available sub-categories and assigned categories
                        this.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                if (Objects.nonNull(category) && !empty) {
                                    // set currentSelected
                                    currentSelected.set(getItem());
                                }
                            }
                        });
                    }


                };


                textFieldListCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    // show context menu for interacting with the selected list-cell
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        textFieldListCell.setContextMenu(getCustomContextMenu(textFieldListCell));
                    }
                });


                return textFieldListCell;
            }
        });


    }

    /**
     * All the available sub-categories will be presented that can be assigned to a selected category.
     * Method populates the ListView with all the available sub-categories
     * except for the assigned sub-categories.
     * End user can double-click on any of the sub-categories which will move
     * the sub-category to the assigned sub-category.
     */
    private void setupCellFactoryListViewAvailableSubCategory() {
        // Show SubCategory title in listViewAvailableSubCategory
        listViewAvailableSubCategory.setCellFactory(new Callback<ListView<SubCategory>, ListCell<SubCategory>>() {
            @Override
            public ListCell<SubCategory> call(ListView<SubCategory> subCategoryListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(SubCategory subCategory, boolean empty) {
                        super.updateItem(subCategory, empty);
                        if (Objects.nonNull(subCategory) && !empty) {
                            setText(subCategory.getTitle());
                        } else {
                            setText(null);
                        }

                        // double click to assign the sub-category
                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(subCategory)) {
                                    assign(subCategory);
                                }
                            }
                        });
                    }
                };
            }
        });
    }

    /**
     * All the assigned sub-categories will be presented.
     * Method populates the ListView with all the assigned sub-categories
     * except for the un-assigned sub-categories.
     * End user can double-click on any of the sub-categories which will move
     * the sub-category to the ListView that contains un-assigned or available sub-categories.
     */
    private void setupCellFactoryListViewAssignedSubCategory() {
        // Show SubCategory title in listviewAssignedSubCategory
        listViewAssignedSubCategory.setCellFactory(new Callback<ListView<SubCategory>, ListCell<SubCategory>>() {
            @Override
            public ListCell<SubCategory> call(ListView<SubCategory> subCategoryListView) {
                return new ListCell<SubCategory>() {
                    @Override
                    protected void updateItem(SubCategory subCategory, boolean empty) {
                        super.updateItem(subCategory, empty);
                        if (Objects.nonNull(subCategory) && !empty) {
                            setText(subCategory.getTitle());
                        } else {
                            setText(null);
                        }

                        // double click to un-assign the sub-category
                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(subCategory)) {
                                    unassign(subCategory);
                                }
                            }
                        });
                    }
                };
            }
        });
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
