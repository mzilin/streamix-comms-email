package com.mariuszilinskas.streamix.comms.email.exception;

public class EmailSendException extends RuntimeException {

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }

}
