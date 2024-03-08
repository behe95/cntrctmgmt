package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.constant.responsemessage.EndUserResponseMessage;
import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Objects;

@Controller
public class SettingsCategoryViewController {


    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    private ObservableList<Category> categoryObservableList;
    private ObservableList<SubCategory> subCategoryObservableList;

    @FXML
    private ListView<Category> listViewCategory;

    @FXML
    private ListView<SubCategory> listViewAvailableSubCategory;

    @FXML
    private ListView<SubCategory> listViewAssignedSubCategory;


    @Autowired
    public SettingsCategoryViewController(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
        categoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
        subCategoryObservableList = FXCollections.observableArrayList(new ArrayList<>());
    }


    @FXML
    private void initialize() {
        // Get all the categories
        categoryObservableList.setAll(categoryService.getAllCategories());
        // Get all the subcategories
        subCategoryObservableList.setAll(subCategoryService.getAllSubCategories());

        // populate list views
        initListViewCategory();
        initListViewAvailableSubCategory();
        initListViewAssignedSubCategory();
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
    private void initListViewCategory() {
        listViewCategory.setItems(categoryObservableList);
        listViewCategory.setEditable(true);


        // Show Category title in listviewCategory
        listViewCategory.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> categoryListView) {
                return new TextFieldListCell<Category>() {
                    @Override
                    public void commitEdit(Category category) {
                        super.commitEdit(category);
                        setText(category.getTitle());
                    }

                    // convert object to make it compatible for the TextFieldListCell
                    @Override
                    public void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);
                        setConverter(new StringConverter<Category>() {
                            @Override
                            public String toString(Category category) {
                                return category.getTitle();
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
                                    listViewAssignedSubCategory.setItems(category.getSubCategoryList());
                                    listViewAvailableSubCategory
                                    .setItems(FXCollections.observableArrayList(
                                            subCategoryObservableList
                                                    .filtered(
                                                    subCategory -> category.getSubCategoryList().stream()
                                                            .noneMatch(subCategory1 -> subCategory1.getId() == subCategory.getId())
                                                    )
                                            )
                                    );
                                }
                            }

                            // show context menu for interacting with the selected list-cell
                            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                                this.setContextMenu(getCustomContextMenu(this));
                            }
                        });
                    }
                };
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
    private void initListViewAvailableSubCategory() {
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
                                    // remove the sub-category from the available sub-category list
                                    listViewAvailableSubCategory.getItems().remove(subCategory);
                                    // assign the sub-category to the selected category
                                    listViewCategory.getSelectionModel().getSelectedItem().getSubCategoryList().add(subCategory);
                                    // assign the selected category to the sub-category that has been assigned
                                    // to make association in the persistence context
                                    subCategory.getCategoryList().add(listViewCategory.getSelectionModel().getSelectedItem());
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
    private void initListViewAssignedSubCategory() {
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
                                    // add the sub-category to the list of available sub-categories
                                    listViewAvailableSubCategory.getItems().add(subCategory);
                                    // remove the sub-category from the assigned sub-category list
                                    listViewCategory.getSelectionModel().getSelectedItem().getSubCategoryList().remove(subCategory);
                                    // remove the selected category from the sub-category that has been un-assigned
                                    // to remove the association in the persistence context
                                    subCategory.getCategoryList().remove(listViewCategory.getSelectionModel().getSelectedItem());
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
     * @param textFieldListCell ListCell that will be interacted
     * @return  Context Menu
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
            try {
                Category category1 = textFieldListCell.listViewProperty()
                        .get().getSelectionModel().getSelectedItem();
                if (category1.getId() <= 0) {
                    categoryService.addCategory(category1);
                } else {
                    categoryService.updateCategory(category1);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(EndUserResponseMessage.CATEGORY_SAVED.getMessage());
                alert.showAndWait();
            } catch (DuplicateEntityException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(EndUserResponseMessage.CATEGORY_SAVED_ERROR + " " + e.getMessage());
                alert.showAndWait();
                textFieldListCell.startEdit();
            }
        });

        // delete category
        deleteMenuItem.setOnAction(actionEvent -> {
            try {
                Category category1 = textFieldListCell.listViewProperty().get().getSelectionModel().getSelectedItem();

                Category category = textFieldListCell.getItem();

                categoryService.deleteCategory(category);

                listViewCategory.itemsProperty().get().remove(category1);
                listViewCategory.getSelectionModel().clearSelection();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(EndUserResponseMessage.CATEGORY_DELETED.getMessage());
                alert.showAndWait();

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(EndUserResponseMessage.CATEGORY_DELETED_ERROR.getMessage());
                alert.showAndWait();
            }
        });

        // add a new category item at the end of the ListView
        // automatically focus and start editing once added
        addMenutItem.setOnAction(actionEvent -> {
            Category newCategory = new Category();
            listViewCategory.itemsProperty().get().add(newCategory);
            listViewCategory.getSelectionModel().select(newCategory);
            listViewCategory.edit(listViewCategory.itemsProperty().get().size() - 1);
        });

        editMenuItem.setOnAction(actionEvent -> textFieldListCell.startEdit());


        contextMenu.getItems().addAll(saveMenutItem, addMenutItem, editMenuItem, deleteMenuItem, cancelMenuItem);

        return contextMenu;

    }




}
