package com.syc.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encripta {

	public static String codePassword(String nombre_usuario) throws Exception {
		return (code(makePassword(nombre_usuario)));
	}

	public static String makePassword(String nombre_usuario) throws Exception {
		char[] id = nombre_usuario.toUpperCase().toCharArray();
		char[] reverse = new char[id.length];

		for (int i = id.length - 1, j = 0; i >= 0; i--, j++) {
			reverse[j] = id[i];
		}

		return (new String(reverse));
	}

	public static String code(String key) throws Exception {
		String sKey = "SYCIMAXFILE";
		String sPass = key;

		String[] a_arrCodigo = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
				"R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		String[] a_arrBase = { "Q00", "A01", "Z02", "W03", "S04", "X05", "E06", "D07", "C08", "R09", "F10", "V11",
				"T12", "G13", "B14", "Y15", "H16", "N17", "U18", "J19", "M20", "I21", "K22", "O23", "L24", "P25",
				"Q26", "A27", "Z28", "W29", "S30", "X31", "E32", "D33", "C34", "R35" };

		String[][] a_arrMatriz = new String[36][36];
		StringBuffer sPassEncriptado = new StringBuffer("IMAXFL");

		// Crea las 2 dimensiones del arreglo
		int indice = 0;
		for (int i = 0; i < 36; i++) {
			for (int a = 0; a < 36; a++) {
				indice = a + i;
				if (indice > 35)
					indice -= 36;

				a_arrMatriz[i][a] = a_arrBase[indice];
			}
		}

		// Vemos si la longitud de la clave es mayor a la longitud de la llave
		if (sPass.length() > sKey.length())
			sPass = sPass.substring(0, sKey.length());

		int int_posPass = 0;
		int int_posKey = 0;
		for (int s = 1; s <= sPass.length(); s++) {
			// Buscamos el caracter clave, el no...
			for (int a = 0; a < a_arrCodigo.length; a++) {
				if (sPass.substring(s - 1, s).equals(a_arrCodigo[a])) {
					int_posPass = a;
					break;
				}
			}

			// Buscamos el caracter llave, el no...
			for (int a = 0; a < a_arrCodigo.length; a++) {
				if (sKey.substring(s - 1, s).equals(a_arrCodigo[a])) {
					int_posKey = a;
					break;
				}
			}

			// Concatenamos
			sPassEncriptado.append(a_arrMatriz[int_posKey][int_posPass]);
		}

		System.out.println("[" + new Encripta().getClass().getName() + ".code] Password(" + key + " [" + key.length()
				+ "]) = (" + sPassEncriptado + " [" + sPassEncriptado.length() + "])");
		return (sPassEncriptado.toString());
	}

	public static String code32(String key) throws Exception {

		String sPass = key;
		// encriptamos pass con md5
		byte[] mbyte = sPass.getBytes();
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] md5digest = md5.digest(mbyte);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < md5digest.length; i++) {
			sb.append((Integer.toHexString((md5digest[i] & 0xFF) | 0x100)).substring(1, 3));
		}

		sPass = sb.toString();

		System.out.println("[" + new Encripta().getClass().getName() + ".code32] Password(" + key + " [" + key.length()
				+ "]) = (" + sPass + " [" + sPass.length() + "])");

		return (sPass.toString());
	}

	private static final String algorithm = "DESede";
	private static final String charsetName = "UTF8";

	public static String encrypt(SecretKey key, String str) {
		try {
			byte[] utf8 = str.getBytes(charsetName);
			Cipher ecipher = Cipher.getInstance(algorithm);
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] enc = ecipher.doFinal(utf8);

			return new BASE64Encoder().encode(enc);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String decrypt(SecretKey key, String str) {
		try {
			byte[] dec = new BASE64Decoder().decodeBuffer(str);
			Cipher dcipher = Cipher.getInstance(algorithm);
			dcipher.init(Cipher.DECRYPT_MODE, key);
			byte[] utf8 = dcipher.doFinal(dec);

			return new String(utf8, charsetName);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static SecretKey genSecretKey() {
		try {
			return KeyGenerator.getInstance(algorithm).generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String secretKeyToString(SecretKey k) {
		return new BASE64Encoder().encode(k.getEncoded());
	}

	public static SecretKey stringToSecretKey(String str) {
		try {
			return new SecretKeySpec(new BASE64Decoder().decodeBuffer(str), algorithm);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
