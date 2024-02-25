package com.example.cntrctmgmt;

import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CntrctmgmtUIInitializer implements ApplicationListener<UILoaderEvent> {
    @Override
    public void onApplicationEvent(UILoaderEvent event) {
        // listen to spring context published event
        // show the ui
        Stage stage = event.getStage();
        stage.show();
    }
}
