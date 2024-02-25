package com.example.cntrctmgmt;

import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CntrctmgmtUIInitializer implements ApplicationListener<UILoaderEvent> {
    @Override
    public void onApplicationEvent(UILoaderEvent event) {
        // listen to spring context published event
        // show the ui
        Stage stage = event.getStage();
        stage.show();

        System.out.println(LocalDateTime.now());
    }
}
