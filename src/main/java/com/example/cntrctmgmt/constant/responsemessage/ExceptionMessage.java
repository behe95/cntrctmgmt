package com.example.cntrctmgmt.constant.responsemessage;

public enum ExceptionMessage {
    DUPLICATE_ENTITY_EXCEPTION("Record already exists!");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
