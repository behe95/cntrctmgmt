package com.example.cntrctmgmt;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class UILoaderEvent extends ApplicationEvent {
    /**
     * Initialize JavaFX stage as an event source
     * @param stage JavaFX stage
     */
    public UILoaderEvent(Stage stage) {
        super(stage);
    }

    /**
     * Get event source
     * @return JavaFX stage
     */
    public Stage getStage() {
        return ((Stage) getSource());
    }
}
