package com.Nulances.helpers;

public final class CpfHelper {

    private CpfHelper() {
    }

    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }

        return cpf.replaceAll("\\D", "");
    }
}