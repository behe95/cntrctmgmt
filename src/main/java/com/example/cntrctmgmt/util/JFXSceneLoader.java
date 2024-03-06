package com.example.cntrctmgmt.util;

import com.example.cntrctmgmt.CntrctmgmtUIInitializer;
import com.example.cntrctmgmt.constant.views.SCREEN_NAMES;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.EnumMap;

@Component
public class JFXSceneLoader {

    private final ApplicationContext applicationContext;
    private final EnumMap<SCREEN_NAMES, Node> embeddedNodeEnumMap = new EnumMap<SCREEN_NAMES, Node>(SCREEN_NAMES.class);

    @Autowired
    public JFXSceneLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public void changeInnerScreen(SCREEN_NAMES screenName, GridPane gridPane) throws IOException {
        if (!embeddedNodeEnumMap.containsKey(screenName)) {
            FXMLLoader fxmlLoader = new FXMLLoader(CntrctmgmtUIInitializer.class.getResource(screenName.getFileLocation()));
            fxmlLoader.setControllerFactory(this.applicationContext::getBean);
            embeddedNodeEnumMap.put(screenName, fxmlLoader.load());
        }



        gridPane.getChildren().clear();
        gridPane.getChildren().add(embeddedNodeEnumMap.get(screenName));
    }



}
