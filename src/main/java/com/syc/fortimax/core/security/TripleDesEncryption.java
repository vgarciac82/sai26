package com.syc.fortimax.core.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.xml.bind.DatatypeConverter;
import java.util.Base64;

public class TripleDesEncryption {

    private static Logger log = LoggerFactory.getLogger(TripleDesEncryption.class);

    static {
        try {
            Cipher.getInstance("DESede");
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage(), ex);
            log.error("DESede no disponible en el runtime actual.", ex);
        } catch (NoSuchPaddingException ex) {
            log.error(ex.getMessage(), ex);
            log.error("DESede no disponible en el runtime actual.", ex);
        }
    }

    /**
     * Encripta la informacion contenida en el parametro <code>data</code>
     * utilizando la llave proporsionada.
     *
     * @param key
     *            Llave privada para generar el encriptamiento.
     * @param data
     *            Datos a encriptar.
     * @return Cadena resultado de aplicar el algoritmo de encriptamiento a la
     *         cadena <code>data</code> codificada en Base64
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String encrypt(SecretKey key, String data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new String(cipher.doFinal(data.getBytes()));
    }

    /**
     * Encripta la cadena y la enmascara en Base64.
     *
     * @param key
     *            Llave de encriptamiento.
     * @param data
     *            Informacion a encriptar.
     * @return Cadena encriptada codificada en Base64
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static String encryptAndMask(SecretKey key, String data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return DatatypeConverter.printBase64Binary(encrypt(key, data).getBytes());
    }

    /**
     * Devuelve la informacion encriptada utilizando el algoritmo 3-DES.
     * Concatenado a los parametros se incluye la llave privada de la encripcion
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static String encryptAndMask(String data) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        SecretKey key = generateKey();
        String params64 = encryptAndMask(key, data);
        String key64 = DatatypeConverter.printBase64Binary(key.getEncoded());
        String result = key64 + ":=" + params64;
        return result;
    }

    /**
     * Genera la llave privada.
     *
     * @return SecrectKey
     * @throws java.security.NoSuchAlgorithmException
     *             si el algoritmo no es soportado por la version de la JVM.
     */
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance("DESede").generateKey();
    }

    /**
     * Genera la llave en base a una cadena.
     *
     * @param bKey
     *            Cadena que contiene la llave.
     * @return Llave generada en base a los caracteres.
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static SecretKey generateKey(byte[] bKey) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        DESedeKeySpec keyspec = new DESedeKeySpec(bKey);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = keyfactory.generateSecret(keyspec);
        return key;
    }

    /**
     * Desencripta el texto utilizando la llave. Si el texto no fue encriptado
     * con la llave que se proporsiona entonces ocurrira una excepcion.
     *
     * @param key
     *            Llave para desencriptar la cadena.
     * @param bData
     *            Cadena encriptada en arreglo de bytes
     * @return Arreglo de bytes con la cadena desencriptada.
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public static byte[] decrypt(SecretKey key, byte[] bData) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] clearData = cipher.doFinal(bData);
        return clearData;
    }
}
