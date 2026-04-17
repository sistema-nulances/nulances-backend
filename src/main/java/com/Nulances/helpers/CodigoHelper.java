package com.Nulances.helpers;

import java.security.SecureRandom;

public final class CodigoHelper {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CodigoHelper() {
    }

    public static String gerarCodigo4Digitos() {
        int numero = 1000 + SECURE_RANDOM.nextInt(9000);
        return String.valueOf(numero);
    }

    public static String gerarCodigo6Digitos() {
        int numero = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(numero);
    }
}