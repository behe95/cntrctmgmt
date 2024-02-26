package com.example.cntrctmgmt;

import com.example.cntrctmgmt.entities.Category;
import com.example.cntrctmgmt.exceptions.DuplicateEntityException;
import com.example.cntrctmgmt.services.CategoryService;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

        CategoryService categoryService = this.applicationContext.getBean(CategoryService.class);
        try {
            categoryService.addCategory(new Category("Construction", false));
        } catch (DuplicateEntityException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(LocalDateTime.now());
    }
}
