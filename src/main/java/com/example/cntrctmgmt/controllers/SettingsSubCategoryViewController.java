package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.entities.SubCategory;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class SettingsSubCategoryViewController {
    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private ListView<SubCategory> listViewSubCategory;


    @FXML
    private void initialize() {
        System.out.println("SettingsSubCategoryViewController initialize()");
    }

}
