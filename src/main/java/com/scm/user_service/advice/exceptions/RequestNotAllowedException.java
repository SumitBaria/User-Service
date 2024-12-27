package com.scm.user_service.advice.exceptions;

public class RequestNotAllowedException extends RuntimeException {
    public RequestNotAllowedException(String msg) {
        super(msg);
    }

    public RequestNotAllowedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
