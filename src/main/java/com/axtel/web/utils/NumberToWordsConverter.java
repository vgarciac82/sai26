package com.axtel.web.utils;

import java.math.BigDecimal;
import java.util.Base64;

public class NumberToWordsConverter {

    private static final String[] UNIDADES = { "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete", "dieciocho", "diecinueve", "veinte" };

    private static final String[] DECENAS = { "", "", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa" };

    private static final String[] CENTENAS = { "", "cien", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos" };

    public static String convertirCantidad(BigDecimal cantidad) {
        int parteEntera = cantidad.intValue();
        int parteDecimal = cantidad.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).intValue();
        return convertirNumero(parteEntera) + " pesos " + (parteDecimal < 10 ? "0" + parteDecimal : parteDecimal) + "/100 MXN";
    }

    private static String convertirNumero(int numero) {
        if (numero < 21) {
            return UNIDADES[numero];
        } else if (numero < 100) {
            return DECENAS[numero / 10] + (numero % 10 != 0 ? " y " + UNIDADES[numero % 10] : "");
        } else if (numero < 1000) {
            return CENTENAS[numero / 100] + (numero % 100 != 0 ? " " + convertirNumero(numero % 100) : "");
        } else if (numero < 1000000) {
            return (numero / 1000 == 1 ? "mil" : convertirNumero(numero / 1000) + " mil") + (numero % 1000 != 0 ? " " + convertirNumero(numero % 1000) : "");
        } else if (numero < 1000000000) {
            return convertirNumero(numero / 1000000) + " millones" + (numero % 1000000 != 0 ? " " + convertirNumero(numero % 1000000) : "");
        } else {
            return "Número fuera de rango";
        }
    }

    public static void main(String[] args) {
        BigDecimal cantidad = new BigDecimal("10023.43");
        System.out.println(convertirCantidad(cantidad));
    }
}
