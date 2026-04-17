package com.Nulances.exception;

public class CpfJaCadastradoException extends RuntimeException {
    public CpfJaCadastradoException() {
        super("CPF já registrado");
    }
}