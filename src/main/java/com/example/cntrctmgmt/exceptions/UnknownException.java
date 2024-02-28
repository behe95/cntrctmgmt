package com.example.cntrctmgmt.exceptions;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;

public class UnknownException extends Exception{
    public UnknownException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.name());
    }
}
