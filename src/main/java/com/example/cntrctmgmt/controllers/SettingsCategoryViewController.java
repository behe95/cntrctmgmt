package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.entities.SubCategory;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.services.CategoryService;
import com.example.cntrctmgmt.services.SubCategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class SettingsCategoryViewController {

    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    private ObservableList<Category> categories;
    private ObservableList<SubCategory> subCategories;

    @FXML
    private ListView<Category> listViewCategory;

    @FXML
    private ListView<SubCategory> listViewAvailableSubCategory;

    @FXML
    private ListView<SubCategory> listViewAssignedSubCategory;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SettingsCategoryViewController(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
        categories = FXCollections.observableArrayList(new ArrayList<>());
        subCategories = FXCollections.observableArrayList(new ArrayList<>());
    }


    @FXML
    private void initialize() {
        // Get all the categories
        categories.setAll(categoryService.getAllCategories());
        // Get all the subcategories
        subCategories.setAll(subCategoryService.getAllSubCategories());

        // populate list views
        initListViewCategory();
        initListViewAvailableSubCategory();
        initListViewAssignedSubCategory();
    }



    private void initListViewCategory() {
        listViewCategory.setItems(categories);
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



                        // on Category selection
                        this.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                if (Objects.nonNull(category) && !empty) {
                                    listViewAssignedSubCategory.setItems(category.getSubCategoryList());
                                    listViewAvailableSubCategory
                                            .setItems(FXCollections.observableArrayList(
                                                    subCategories.filtered(
                                                            subCategory -> category.getSubCategoryList().stream()
                                                                    .noneMatch(subCategory1 -> subCategory1.getId() == subCategory.getId())
                                                    )));
                                }
                            }

                            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                                ContextMenu contextMenu = new ContextMenu();
                                MenuItem saveMenutItem = new MenuItem("Save");
                                MenuItem addMenutItem = new MenuItem("Add New");
                                MenuItem editMenuItem = new MenuItem("Edit");
                                MenuItem deleteMenuItem = new MenuItem("Delete");
                                MenuItem cancelMenuItem = new MenuItem("Cancel");

                                saveMenutItem.setOnAction(actionEvent -> {
                                    try {
                                        categoryService.updateCategory(listViewProperty().get().getSelectionModel().getSelectedItem());
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Category updated!");
                                        alert.showAndWait();
                                    } catch (DuplicateEntityException e) {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setContentText(e.getMessage());
                                        alert.showAndWait();
                                    }
                                });

                                /**
                                 * TODO
                                 */
//                                addMenutItem.setOnAction(actionEvent -> {
//                                    Category newCategory = new Category();
//                                    listViewCategory.itemsProperty().get().add(newCategory);
//                                    listViewCategory.getSelectionModel().select(newCategory);
//                                    listViewCategory.getFocusModel().focus(listViewCategory.itemsProperty().get().size()-1);
//                                    listViewCategory.requestFocus();
//                                });

                                editMenuItem.setOnAction(actionEvent -> startEdit());

                                contextMenu.getItems().addAll(saveMenutItem, addMenutItem, editMenuItem, deleteMenuItem, cancelMenuItem);
                                this.setContextMenu(contextMenu);
                            }
                        });
                    }
                };
            }
        });
    }

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

                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(subCategory)) {
                                    listViewAvailableSubCategory.getItems().remove(subCategory);
                                    listViewCategory.getSelectionModel().getSelectedItem().getSubCategoryList().add(subCategory);
                                    subCategory.getCategoryList().add(listViewCategory.getSelectionModel().getSelectedItem());
                                }
                            }
                        });
                    }
                };
            }
        });
    }

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
                        }else {
                            setText(null);
                        }

                        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (mouseEvent.getClickCount() == 2 && Objects.nonNull(subCategory)) {
                                    listViewAvailableSubCategory.getItems().add(subCategory);
                                    listViewCategory.getSelectionModel().getSelectedItem().getSubCategoryList().remove(subCategory);
                                    subCategory.getCategoryList().add(listViewCategory.getSelectionModel().getSelectedItem());
                                }
                            }
                        });
                    }
                };
            }
        });
    }


}
