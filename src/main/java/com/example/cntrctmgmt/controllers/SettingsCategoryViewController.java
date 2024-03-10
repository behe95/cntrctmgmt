package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.constant.responsemessage.EndUserResponseMessage;
import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.exceptions.InvalidInputException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import javafx.application.Platform;
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
public class SettingsCategoryViewController {


    // service class to interact with service repository
    private final CategoryService categoryService;

    // service class to interact with sub-category repository
    private final SubCategoryService subCategoryService;

    // contains all the categories
    private final ObservableList<Category> categoryObservableList;
    // contains all the subcategories
    // this is a reference list which is not tied to any rendered nodes
    private final ObservableList<SubCategory> subCategoryObservableList;

    // contains available sub-categories for each category
    private final HashMap<Category, ObservableList<SubCategory>> availableSubCategories;

    // currently selected category by the end user
    private final ObjectProperty<Category> currentSelectedCategory = new SimpleObjectProperty<>();

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


    @Autowired
    public SettingsCategoryViewController(CategoryService categoryService, SubCategoryService subCategoryService) {
        // service to interact with repository
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;

        // data containers
        this.availableSubCategories = new HashMap<>();
        this.categoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
        this.subCategoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
    }


    @FXML
    private void initialize() {
        // Get all the categories
        categoryObservableList.setAll(categoryService.getAllCategories());
        // Get all the subcategories
        subCategoryObservableList.setAll(subCategoryService.getAllSubCategories());

        // populate ListView for all the categories
        listViewCategory.setItems(categoryObservableList);

        // category change
        currentSelectedCategory.addListener(categoryChangeListener());

        // populate list views
        setupCellFactoryListViewCategory();
        setupCellFactoryListViewAvailableSubCategory();
        setupCellFactoryListViewAssignedSubCategory();

        // initially disable to icon to interact
        // no category selected
        btnDeleteCategory.setDisable(true);
        btnSaveCategory.setDisable(true);
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
                if (!availableSubCategories.containsKey(newCategory)) {
                    availableSubCategories.put(
                            newCategory, FXCollections.observableArrayList(
                                    subCategoryObservableList
                                            .filtered(
                                                    availableSubCategory -> newCategory.getSubCategoryList().stream()
                                                            .noneMatch(assignedSubCategory -> assignedSubCategory.getId() == availableSubCategory.getId())
                                            )
                            )
                    );
                }
                // populate the ListViews with available and assigned sub-categories
                Category category = currentSelectedCategory.get();
                listViewAvailableSubCategory.setItems(availableSubCategories.get(category));
                listViewAssignedSubCategory.setItems(category.getSubCategoryList());
            }

            // Disable the icon button if no category found for selection
            if (Objects.isNull(currentSelectedCategory.get())) {
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
                                    // set currentSelectedCategory
                                    currentSelectedCategory.set(getItem());
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
                                    Category category = currentSelectedCategory.get();
                                    // remove the sub-category from the available sub-category list
                                    availableSubCategories.get(category).remove(subCategory);
                                    // assign the sub-category to the selected category
                                    category.getSubCategoryList().add(subCategory);
                                    // assign the selected category to the sub-category that has been assigned
                                    // to make association in the persistence context
                                    subCategory.getCategoryList().add(category);
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
                                    Category category = currentSelectedCategory.get();
                                    // add the sub-category to the list of available sub-categories
                                    availableSubCategories.get(category).add(subCategory);
                                    // remove the sub-category from the assigned sub-category list
                                    category.getSubCategoryList().remove(subCategory);
                                    // remove the selected category from the sub-category that has been un-assigned
                                    // to remove the association in the persistence context
                                    subCategory.getCategoryList().remove(category);
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
    private ContextMenu getCustomContextMenu(TextFieldListCell<Category> textFieldListCell) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem saveMenutItem = new MenuItem("Save");
        MenuItem addMenutItem = new MenuItem("Add New");
        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem cancelMenuItem = new MenuItem("Cancel");

        // save or update category
        // throws exception if duplicate category
        // is being saved
        saveMenutItem.setOnAction(actionEvent -> {
            Category category = textFieldListCell.listViewProperty()
                    .get().getSelectionModel().getSelectedItem();
            boolean isSavedOrUpdated = saveOrUpdateCategory(actionEvent, category);
            if (!isSavedOrUpdated) {
                textFieldListCell.startEdit();
            }
        });

        // delete category
        deleteMenuItem.setOnAction(actionEvent -> {
            Category category = textFieldListCell.listViewProperty().get().getSelectionModel().getSelectedItem();
            deleteCategory(actionEvent);
        });

        // add a new category item at the end of the ListView
        // automatically focus and start editing once added
        addMenutItem.setOnAction(actionEvent -> addNewItemCategory());

        // edit selected list-cell
        editMenuItem.setOnAction(actionEvent -> textFieldListCell.startEdit());

        // add menu items to the context menu
        contextMenu.getItems().addAll(saveMenutItem, addMenutItem, editMenuItem, deleteMenuItem, cancelMenuItem);

        return contextMenu;

    }

    /**
     * An event handler which is part of a menu item. Triggered on action and save or update a category.
     *
     * @param actionEvent Any event triggered on the main item that contains the event handler
     * @param category    Category to save or update
     * @return A flag if the save or update is successful or not.
     */
    private boolean saveOrUpdateCategory(ActionEvent actionEvent, Category category) {
        {
            boolean isSaved = true;
            try {

                if (Objects.isNull(category.getTitle())
                        || category.getTitle().equals("")) {
                    throw new InvalidInputException(ExceptionMessage.EMPTY_INPUT_ERROR.getMessage());
                }

                if (category.getId() <= 0) {
                    categoryService.addCategory(category);
                } else {
                    categoryService.updateCategory(category);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(EndUserResponseMessage.CATEGORY_SAVED.getMessage());
                alert.showAndWait();
            } catch (DuplicateEntityException e) {
                isSaved = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(EndUserResponseMessage.CATEGORY_SAVED_ERROR.getMessage() + " " + e.getMessage());
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
     * Delete a category from the list-cell
     *
     * @param actionEvent Any event triggered on the main item that contains the event handler
     */
    private void deleteCategory(ActionEvent actionEvent) {
        try {
            // delete categories
            categoryService.deleteCategories(listViewCategory.getSelectionModel().getSelectedItems());
            // get the index of the deleted category
            int selectedIdx = listViewCategory.getSelectionModel().getSelectedIndices().stream().min(Integer::compareTo).orElse(-1);


            // clean up the available categories
            for (Category key : listViewCategory.getSelectionModel().getSelectedItems()) {
                // remove the assigned sub-categories
                availableSubCategories.get(key).clear();
                // remove the category
                availableSubCategories.remove(key);
            }

            // remove it from the category ListView
            listViewCategory.itemsProperty().get().removeAll(listViewCategory.getSelectionModel().getSelectedItems());
            // clear selection
            listViewCategory.getSelectionModel().clearSelection();

            // if there are still categories left, change the selection
            // and focus to either next or previous category relative to the
            // index that were removed
            if (listViewCategory.itemsProperty().get().size() > 0) {
                if (selectedIdx >= listViewCategory.itemsProperty().get().size()) {
                    selectedIdx = selectedIdx - 1;
                }
                listViewCategory.getSelectionModel().select(selectedIdx);
                listViewCategory.getFocusModel().focus(selectedIdx);
                // set the current selected category
                currentSelectedCategory.set(listViewCategory.getSelectionModel().getSelectedItem());
            } else {
                // set current category
                currentSelectedCategory.set(null);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(EndUserResponseMessage.CATEGORY_DELETED.getMessage());
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(EndUserResponseMessage.CATEGORY_DELETED_ERROR.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Add a new category to the category list.
     */
    private void addNewItemCategory() {
        Category newCategory = new Category();
        listViewCategory.itemsProperty().get().add(newCategory);
        // clear any previous selection
        listViewCategory.getSelectionModel().clearSelection();
        // select new item
        listViewCategory.getSelectionModel().select(newCategory);
        currentSelectedCategory.set(newCategory);


        int newAddedCategoryIdx = listViewCategory.itemsProperty().get().size() - 1;
        listViewCategory.layout();
        listViewCategory.scrollTo(newAddedCategoryIdx);
        listViewCategory.getFocusModel().focus(newAddedCategoryIdx);


        listViewCategory.edit(newAddedCategoryIdx);
    }
    /**
     * Event handler for add button
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnAddNewCategory(ActionEvent event) {
        addNewItemCategory();
    }
    /**
     * Event handler for delete button
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnDeleteCategory(ActionEvent event) {
        deleteCategory(event);
    }

    /**
     * Event handler for save or update button
     * @param event Event that triggers this handler
     */
    @FXML
    void onActionBtnSaveCategory(ActionEvent event) {
        saveOrUpdateCategory(event, currentSelectedCategory.get());
    }


}
