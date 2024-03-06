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
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class SettingsCategoryViewController {

    private final ApplicationContext applicationContext;

    @FXML
    private ListView<Category> listViewCategory;

    @FXML
    private ListView<SubCategory> listViewAvailableSubCategory;

    @FXML
    private ListView<SubCategory> listViewAssignedSubCategory;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SettingsCategoryViewController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @FXML
    private void initialize() {
        // Get all the categories
        CategoryService categoryService = this.applicationContext.getBean(CategoryService.class);
        List<Category> categoryList = categoryService.getAllCategories();
        ObservableList<Category> categories = FXCollections.observableArrayList(new ArrayList<>());
        categories.setAll(categoryList);
        listViewCategory.setItems(categories);

        // Get all the subcategories
        SubCategoryService subCategoryService = this.applicationContext.getBean(SubCategoryService.class);
        List<SubCategory> subCategoryList = subCategoryService.getAllSubCategories();
        ObservableList<SubCategory> subCategories = FXCollections.observableArrayList(new ArrayList<>());
        subCategories.setAll(subCategoryList);







        // Show Category title in listviewCategory
        listViewCategory.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> categoryListView) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);
                        if (Objects.nonNull(category)) {
                            setText(category.getTitle());
                        }
                        // on Category selection
                        this.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                if (Objects.nonNull(category) && !empty) {
//                                listViewAssignedSubCategory.setItems(FXCollections.observableArrayList(category.getSubCategoryList()));
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
                                MenuItem cancelMenuItem = new MenuItem("Cancel");

                                saveMenutItem.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent actionEvent) {
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
                                    }
                                });

                                contextMenu.getItems().addAll(saveMenutItem, cancelMenuItem);


                                this.setContextMenu(contextMenu);
                            }
                        });



                    }
                };
            }
        });

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
