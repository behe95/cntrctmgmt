package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.entities.SubCategory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class SettingsSubCategoryViewController {
    @FXML
    private Button btnAddNewCategory;

    @FXML
    private Button btnDeleteCategory;

    @FXML
    private Button btnSaveCategory;

    @FXML
    private ListView<?> listViewAssignedSubCategory;

    @FXML
    private ListView<?> listViewAvailableSubCategory;

    @FXML
    private ListView<?> listViewCategory;

    @FXML
    void onActionBtnAddNewCategory(ActionEvent event) {

    }

    @FXML
    void onActionBtnDeleteCategory(ActionEvent event) {

    }

    @FXML
    void onActionBtnSaveCategory(ActionEvent event) {

    }

}
