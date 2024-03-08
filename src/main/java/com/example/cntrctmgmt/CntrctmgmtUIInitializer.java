package com.example.cntrctmgmt;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CntrctmgmtUIInitializer implements ApplicationListener<UILoaderEvent> {

    private final ApplicationContext applicationContext;

    public CntrctmgmtUIInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(UILoaderEvent event) {
        // listen to spring context published event
        // show the ui
        FXMLLoader fxmlLoader = new FXMLLoader(CntrctmgmtUIInitializer.class.getResource("MainView.fxml"));
        fxmlLoader.setControllerFactory(this.applicationContext::getBean);
        try {
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = event.getStage();
            stage.setTitle("Contract Management");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
