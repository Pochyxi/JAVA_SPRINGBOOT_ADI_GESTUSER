package com.adi.gestuser.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class appException extends RuntimeException {

    private final HttpStatus status;

    private final String message;

    public appException( HttpStatus status, String message ) {
        this.status = status;
        this.message = message;

    }


}
