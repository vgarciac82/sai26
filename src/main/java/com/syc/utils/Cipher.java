package com.syc.utils;

import java.util.Base64;

public class Cipher {

    String key;

    javax.crypto.Cipher Encrypter;

    javax.crypto.Cipher Decrypter;

    public Cipher(String inKey, byte[] salt) {
        // Declare encryption structures
        javax.crypto.spec.PBEKeySpec pbeKeySpec;
        javax.crypto.spec.PBEParameterSpec pbeParamSpec;
        javax.crypto.SecretKeyFactory keyFac;
        int iterationCount = 55;
        // use the hashed encryption key as our "password"
        key = inKey;
        // Initialize the ciphers for encryption and decryption
        try {
            Encrypter = javax.crypto.Cipher.getInstance("PBEWithMD5AndDES");
            Decrypter = javax.crypto.Cipher.getInstance("PBEWithMD5AndDES");
            // Create PBE parameter set
            pbeParamSpec = new javax.crypto.spec.PBEParameterSpec(salt, iterationCount);
            // Convert key SecretKey object
            pbeKeySpec = new javax.crypto.spec.PBEKeySpec(inKey.toCharArray());
            keyFac = javax.crypto.SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            javax.crypto.SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
            // Initialize PBE Cipher with key and parameters
            Encrypter.init(javax.crypto.Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
            Decrypter.init(javax.crypto.Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] b) {
        try {
            return Encrypter.doFinal(b);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public String decrypt(byte[] data) {
        try {
            return new String(Decrypter.doFinal(data));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/
    public byte[] decrypt(byte[] data) {
        try {
            return (Decrypter.doFinal(data));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*public Cipher{
        Cipher authCipher;
 
        byte[] random_number = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
                (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };
 
        String pass1 = "password";
        String encryptionString = "ZoMg It wOrKs9{?^j$Q}]|2";
        // Create authentication cipher with this key
        authCipher = new Cipher(pass1, random_number);
 
        // Encrypt Encryption String using authentication cipher
        byte[] encryptedData = authCipher.encrypt(encryptionString);
 
        // Decrypt Encryption String using authentication cipher
        String decryptedString = authCipher.decrypt(encryptedData);
        System.out.println("plaintext: ["+encryptionString+"]");
        System.out.println("decrypted: ["+decryptedString+"]");
    }*/
}
