package com.syc.contable.util;

import java.math.BigDecimal;
import java.util.Base64;

public class Math {

    public static void main(String[] argv) throws Exception {
        double nt = 0;
        for (double n = 100; n < 101; n += 0.01d) System.out.println(String.format("%s\t%s\t%s\t%s", n, truncate(n, 2), (nt += n), truncate(nt, 2)));
    }

    public static double round(double value, int precision) {
        return round(value, precision, BigDecimal.ROUND_UP);
    }

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        bd = bd.setScale(precision, roundingMode);
        return bd.doubleValue();
    }

    public static double truncate(double value, int decimalPlaces) {
        return round(value, decimalPlaces, BigDecimal.ROUND_HALF_DOWN);
    }
}
