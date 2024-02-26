package com.example.cntrctmgmt.exceptions;

import com.example.cntrctmgmt.constant.responsemessage.ExceptionMessage;
import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateEntityException extends Exception {


    public DuplicateEntityException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.name());
    }
}
