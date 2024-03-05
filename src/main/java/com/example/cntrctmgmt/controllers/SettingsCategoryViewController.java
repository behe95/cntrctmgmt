package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.services.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

    @Autowired
    public SettingsCategoryViewController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @FXML
    private void initialize() {
        CategoryService categoryService = this.applicationContext.getBean(CategoryService.class);
        ObservableList<Category> categories = FXCollections.observableList(new ArrayList<>());
        List<Category> categoryList = categoryService.getAllCategories();
        categories.setAll(categoryList);

        listViewCategory.setItems(categories);

        listViewCategory.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> categoryListView) {
                return new ListCell<Category>(){
                    @Override
                    protected void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);
                        if (Objects.nonNull(category)) {
                            setText(category.getTitle());
                        }
                    }
                };
            }
        });
    }
}
