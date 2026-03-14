package com.syc.utils;

import java.util.Base64;

/**
 * Esta utileria proporciona equivalentes a las funciones JavaScript
 * encodeURIComponent(uri) y decodeURIComponent(encodedURI).
 *
 * @author Edgar Rivera Viveros Fecha: 09.06.2009
 */
public abstract class URIComponentUtils {

    public static String encodeURIComponent(String uri) {
        throw new UnsupportedOperationException("No implementada todavia!");
    }

    public static String decodeURIComponent(String encodedURI) {
        char actualChar;
        StringBuffer buffer = new StringBuffer();
        int bytePattern, sumb = 0;
        for (int i = 0, more = -1; i < encodedURI.length(); i++) {
            actualChar = encodedURI.charAt(i);
            switch(actualChar) {
                case '%':
                    actualChar = encodedURI.charAt(++i);
                    int hb = (Character.isDigit(actualChar) ? actualChar - '0' : 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
                    actualChar = encodedURI.charAt(++i);
                    int lb = (Character.isDigit(actualChar) ? actualChar - '0' : 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
                    bytePattern = (hb << 4) | lb;
                    break;
                case '+':
                    bytePattern = ' ';
                    break;
                default:
                    bytePattern = actualChar;
            }
            //* Decodifica patron de bytes comno UTF-8, sumb collecciona caracteres incompletos */
            if ((bytePattern & 0xc0) == 0x80) {
                // 10xxxxxx
                sumb = (sumb << 6) | (bytePattern & 0x3f);
                if (--more == 0)
                    buffer.append((char) sumb);
            } else if ((bytePattern & 0x80) == 0x00) {
                // 0xxxxxxx
                buffer.append((char) bytePattern);
            } else if ((bytePattern & 0xe0) == 0xc0) {
                // 110xxxxx
                sumb = bytePattern & 0x1f;
                more = 1;
            } else if ((bytePattern & 0xf0) == 0xe0) {
                // 1110xxxx
                sumb = bytePattern & 0x0f;
                more = 2;
            } else if ((bytePattern & 0xf8) == 0xf0) {
                // 11110xxx
                sumb = bytePattern & 0x07;
                more = 3;
            } else if ((bytePattern & 0xfc) == 0xf8) {
                // 111110xx
                sumb = bytePattern & 0x03;
                more = 4;
            } else {
                // 1111110x
                sumb = bytePattern & 0x01;
                more = 5;
            }
        }
        return buffer.toString();
    }
}
