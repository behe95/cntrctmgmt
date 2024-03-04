package com.example.cntrctmgmt;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CntrctmgmtUIInitializer implements ApplicationListener<UILoaderEvent> {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(UILoaderEvent event) {
        // listen to spring context published event
        // show the ui
        Stage stage = event.getStage();
        stage.show();

    }
}
