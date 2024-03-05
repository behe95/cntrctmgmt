package com.example.cntrctmgmt.controllers;


import com.example.cntrctmgmt.constant.views.SCREEN_NAMES;
import com.example.cntrctmgmt.util.JFXSceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
@Controller
public class MainViewController {
    private final JFXSceneLoader jfxSceneLoader;

    private final ApplicationContext applicationContext;

    @FXML
    private GridPane gridpaneDynamicScreenLoader;

    @FXML
    private Label labelScreenTitle;

    @FXML
    private Label labelSettingsCategoryManagement;

    @FXML
    private Label labelSettingsSubCategoryManagement;

    @FXML
    private AnchorPane mainViewAnchorPane;

    @Autowired
    public MainViewController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        jfxSceneLoader = applicationContext.getBean(JFXSceneLoader.class);
    }

    @FXML
    private void initialize() {
        System.out.println("MainViewController initialize()");
    }

    @FXML
    void labelSettingsCategoryManagementOnMouseClicked(MouseEvent event) throws IOException {
        jfxSceneLoader.changeInnerScreen(SCREEN_NAMES.SETTINGS_CATEGORY_SCREEN, gridpaneDynamicScreenLoader);
    }

    @FXML
    void labelSettingsSubCategoryManagementOnMouseClicked(MouseEvent event) throws IOException {
        jfxSceneLoader.changeInnerScreen(SCREEN_NAMES.SETTINGS_SUBCATEGORY_SCREEN, gridpaneDynamicScreenLoader);

    }

}
