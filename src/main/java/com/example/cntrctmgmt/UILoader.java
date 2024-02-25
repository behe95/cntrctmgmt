package com.example.cntrctmgmt;

import com.example.cntrctmgmt.CntrctmgmtApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class UILoader extends Application {

    /**
     * Spring boot application context
     */
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        this.applicationContext = SpringApplication.run(CntrctmgmtApplication.class);
    }


    @Override
    public void start(Stage stage) throws Exception {
        // create event when ui stage is ready
        UILoaderEvent uiLoaderEvent = new UILoaderEvent(stage);

        // publish event from the spring boot context
        this.applicationContext.publishEvent(uiLoaderEvent);
    }

    @Override
    public void stop() throws Exception {
        this.applicationContext.close();
        Platform.exit();
    }
}
