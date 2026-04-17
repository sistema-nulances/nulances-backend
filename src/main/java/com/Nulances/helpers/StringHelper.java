package com.Nulances.helpers;

public class StringHelper {

    private StringHelper() {
    }

    public static String normalizar(String valor) {
        if (valor == null) {
            return null;
        }

        String valorTratado = valor.trim();
        return valorTratado.isBlank() ? null : valorTratado;
    }

    public static String normalizarEstado(String estado) {
        String valor = normalizar(estado);
        return valor == null ? null : valor.toUpperCase();
    }
}