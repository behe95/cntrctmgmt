package com.example.cntrctmgmt.constant.responsemessage;

public enum ExceptionMessage {

    UNKNOWN_EXCEPTION("Something went wrong!"),
    DUPLICATE_ENTITY_EXCEPTION("Record already exists!"),
    ENTITY_NOT_FOUND("Not found!");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
