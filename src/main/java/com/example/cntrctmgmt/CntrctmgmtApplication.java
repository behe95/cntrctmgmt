package com.example.cntrctmgmt;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CntrctmgmtApplication {

    public static void main(String[] args) {
        Application.launch(UILoader.class, args);
    }

}
