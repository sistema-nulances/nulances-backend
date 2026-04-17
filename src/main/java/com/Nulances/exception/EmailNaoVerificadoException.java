package com.Nulances.exception;

public class EmailNaoVerificadoException extends RuntimeException {

    public static final String CODE = "EMAIL_NAO_VERIFICADO";

    public EmailNaoVerificadoException(String message) {
        super(message);
    }
}