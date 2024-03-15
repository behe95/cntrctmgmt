package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.constant.responsemessage.EndUserResponseMessage;
import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.InvalidInputException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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



        // setup the list views
        // setup corresponding list cells
        setupCellFactoryListViewSubCategory();
        setupCellFactoryListViewAvailableCategory();
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
        saveOrUpdateSubCategory(event, currentSelectedSubCategory.get());
    }


    /**
     * All the available sub-categories will be presented
     * The ListView is editable.
     * Each ListCell is represented with TextFieldListCell to provide editing
     * option to the end user.
     * On selection of any sub-category from the list, the ListView for the
     * assigned categories and available categories to assign to this sub-category will be populated.
     * User can right-click on any of the ListCell that will pop-up a context menu for
     * further interaction
     */
    private void setupCellFactoryListViewSubCategory() {
        // make ListView editable
        listViewSubCategory.setEditable(true);
        // multiple cell selections from the ListView
        listViewSubCategory.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // show sub-category title in listViewSubCategory
        listViewSubCategory.setCellFactory(new Callback<ListView<SubCategory>, ListCell<SubCategory>>() {
            @Override
            public ListCell<SubCategory> call(ListView<SubCategory> subCategoryListView) {

                TextFieldListCell<SubCategory> textFieldListCell = new TextFieldListCell<>(){
                    // contains text value inside the list cell when in editing state
                    private StringProperty tempTextProperty = new SimpleStringProperty("");

                    // check if the list-cell editing state is turned off by pressing
                    // ESCAPE key or not
                    boolean isCancelledEditingByEscapeKey = false;

                    // check if the list-cel editing state is turned off by pressing
                    // Enter key when commited
                    boolean isCommittedEditingByEnterKey = false;

                    // check if focus lost
                    boolean isFocusLost = false;

                    // change listener for focus lost
                    ChangeListener<? super Boolean> focusLostListener = new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                            if (!isFocusLost) {
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
                            isCommittedEditingByEnterKey = true;
                        }
                    };

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        // bind the text inside the textfiled
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

                        if (!isCancelledEditingByEscapeKey && !isCommittedEditingByEnterKey) {
                            // commit changes before cancelling
                            String editedValues = tempTextProperty.get();
                            setText(editedValues);
                            SubCategory subCategory = getItem();
                            subCategory.setTitle(editedValues);
                            updateItem(subCategory, false);
                            commitEdit(subCategory);
                            listViewSubCategory.getSelectionModel().clearSelection();
                            currentSelectedSubCategory.set(null);
                        }

                        if (isCancelledEditingByEscapeKey) {
                            isCancelledEditingByEscapeKey = false;
                        }

                        if (isCommittedEditingByEnterKey) {
                            isCommittedEditingByEnterKey = false;
                        }

                        super.cancelEdit();
                    }

                    @Override
                    public void updateItem(SubCategory subCategory, boolean empty) {
                        super.updateItem(subCategory, empty);

                        // convert object to make it compatible for the TextFieldListCell
                        // and vice-versa
                        setConverter(new StringConverter<SubCategory>() {
                            @Override
                            public String toString(SubCategory subCategory) {
                                return Objects.nonNull(subCategory) ? subCategory.getTitle() : "";
                            }

                            @Override
                            public SubCategory fromString(String s) {
                                getItem().setTitle(s);
                                return getItem();
                            }
                        });



                        // show available categories and assigned categories
                        this.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                if (Objects.nonNull(subCategory) && !empty) {
                                    currentSelectedSubCategory.set(subCategory);
                                }
                            }
                        });

                    }

                    @Override
                    public void commitEdit(SubCategory subCategory) {
                        if(!isEmpty()) {
                            super.commitEdit(subCategory);
                            setText(subCategory.getTitle());
                        }
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
     * All the available categories will be presented that can be assigned to a selected sub-category.
     * Method populates the ListView with all the available categories
     * except for the assigned categories.
     * End user can double-click on any of the categories which will move
     * the category to the assigned category.
     */
    private void setupCellFactoryListViewAvailableCategory() {
        // Show Category title in listViewAvailableCategory
        listViewAvailableCategory.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> categoryListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);
                        if (Objects.nonNull(category) && !empty) {
                            setText(category.getTitle());
                        } else {
                            setText(null);
                        }

                        // double click to assign the category
                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(category)) {
                                    assignCategory(category);
                                }
                            }
                        });
                    }
                };
            }
        });
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
                                    unassignCategory(category);
                                }
                            }
                        });
                    }
                };
            }
        });
    }


    /**
     * Creates a Context menu for user interaction with a selected ListCell
     *
     * @param textFieldListCell ListCell that will be interacted
     * @return Context Menu
     */
    private ContextMenu getCustomContextMenu(TextFieldListCell<SubCategory> textFieldListCell) {
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

        // save or update sub-category
        // throws exception if duplicate category
        // is being saved
        saveMenutItem.setOnAction(actionEvent -> {
            SubCategory subCategory = textFieldListCell.listViewProperty()
                    .get().getSelectionModel().getSelectedItem();
            boolean isSavedOrUpdated = saveOrUpdateSubCategory(actionEvent, subCategory);
            if (!isSavedOrUpdated) {
                textFieldListCell.startEdit();
            }
        });

        // delete sub-category
        deleteMenuItem.setOnAction(actionEvent -> {
            SubCategory subCategory = textFieldListCell.listViewProperty().get().getSelectionModel().getSelectedItem();
            deleteSubCategory(actionEvent);
        });

        // add a new sub-category item at the end of the ListView
        // automatically focus and start editing once added
        addMenutItem.setOnAction(actionEvent -> addNewItemSubCategory());

        // edit selected list-cell
        editMenuItem.setOnAction(actionEvent -> textFieldListCell.startEdit());

        // add menu items to the context menu
        contextMenu.getItems().addAll(saveMenutItem, addMenutItem, editMenuItem, deleteMenuItem, cancelMenuItem);

        return contextMenu;

    }

    /**
     * An event handler which is part of a menu item. Triggered on action and save or update a sub-category.
     * This method may add or update multiple sub-categories if multiple sub-categories are selected for
     * saving or updating at once
     *
     * @param actionEvent Any event triggered on the main item that contains the event handler
     * @param subCategory    Category to save or update
     * @return A flag if the save or update is successful or not.
     */
    private boolean saveOrUpdateSubCategory(ActionEvent actionEvent, SubCategory subCategory) {
        {
            boolean isSaved = true;
            try {

                if (Objects.isNull(subCategory.getTitle())
                        || subCategory.getTitle().equals("")) {
                    throw new InvalidInputException(ExceptionMessage.EMPTY_INPUT_ERROR.getMessage());
                }


                subCategoryService.addAllSubCategories(listViewSubCategory.getSelectionModel().getSelectedItems());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(EndUserResponseMessage.SUBCATEGORY_SAVED.getMessage());
                alert.showAndWait();
            } catch (DuplicateEntityException e) {
                isSaved = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(EndUserResponseMessage.SUBCATEGORY_SAVED_ERROR.getMessage() + " " + e.getMessage());
                alert.showAndWait();
            } catch (InvalidInputException e) {
                isSaved = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
            return isSaved;
        }
    }

    /**
     * Delete a sub-category or multiple sub-categories from the list-cell
     *
     * @param actionEvent Any event triggered on the main item that contains the event handler
     */
    private void deleteSubCategory(ActionEvent actionEvent) {
        try {
            // delete categories
            subCategoryService.deleteSubCategories(listViewSubCategory.getSelectionModel().getSelectedItems());
            // get the index of the deleted category
            int selectedIdx = listViewSubCategory.getSelectionModel().getSelectedIndices().stream().min(Integer::compareTo).orElse(-1);


            // clean up the available categories
            for (SubCategory key : listViewSubCategory.getSelectionModel().getSelectedItems()) {
                // remove the assigned categories
                availableCategories.get(key).clear();
                // remove the sub-category
                availableCategories.remove(key);
            }

            // remove it from the category ListView
            listViewSubCategory.itemsProperty().get().removeAll(listViewSubCategory.getSelectionModel().getSelectedItems());
            // clear selection
            listViewSubCategory.getSelectionModel().clearSelection();

            // if there are still sub-categories left, change the selection
            // and focus to either next or previous sub-category relative to the
            // index that were removed
            if (listViewSubCategory.itemsProperty().get().size() > 0) {
                if (selectedIdx >= listViewSubCategory.itemsProperty().get().size()) {
                    selectedIdx = selectedIdx - 1;
                }
                listViewSubCategory.getSelectionModel().select(selectedIdx);
                listViewSubCategory.getFocusModel().focus(selectedIdx);
                // set the current selected category
                currentSelectedSubCategory.set(listViewSubCategory.getSelectionModel().getSelectedItem());
            } else {
                // set current category
                currentSelectedSubCategory.set(null);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(EndUserResponseMessage.SUBCATEGORY_DELETED.getMessage());
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(EndUserResponseMessage.SUBCATEGORY_DELETED_ERROR.getMessage());
            alert.showAndWait();
        }
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


    /**
     * Add a new category to the category list.
     */
    private void addNewItemSubCategory() {
        SubCategory newSubCategory = new SubCategory();
        listViewSubCategory.itemsProperty().get().add(newSubCategory);
        // clear any previous selection
        listViewSubCategory.getSelectionModel().clearSelection();
        // select new item
        listViewSubCategory.getSelectionModel().select(newSubCategory);
        currentSelectedSubCategory.set(newSubCategory);


        int newAddedSubCategoryIdx = listViewSubCategory.itemsProperty().get().size() - 1;
        listViewSubCategory.layout();
        listViewSubCategory.scrollTo(newAddedSubCategoryIdx);
        listViewSubCategory.getFocusModel().focus(newAddedSubCategoryIdx);


        listViewSubCategory.edit(newAddedSubCategoryIdx);
    }


    /**
     * This method assign a category to a sub-category
     *
     * @param category category to assign
     */
    private void assignCategory(Category category) {
        SubCategory subCategory = currentSelectedSubCategory.get();
        // remove the category from the available category list
        availableCategories.get(subCategory).remove(category);
        // assign the category to the selected sub-category
        subCategory.getCategoryList().add(category);
        // assign the selected sub-category to the category that has been assigned
        // to make association in the persistence context
        category.getSubCategoryList().add(subCategory);

    }

    /**
     * This method un-assign a category from a sub-category
     *
     * @param category category to un-assign
     */
    private void unassignCategory(Category category) {
        SubCategory subCategory = currentSelectedSubCategory.get();
        // add the category to the list of available categories
        availableCategories.get(subCategory).add(category);
        // remove the category from the assigned category list
        subCategory.getCategoryList().remove(category);
        // remove the selected sub-category from the category that has been un-assigned
        // to remove the association in the persistence context
        category.getSubCategoryList().remove(subCategory);
    }

}
