package com.axtel.cfdi.stamp.core;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import com.syc.cfdi.security.TripleDesEncryption;

public class UtilSecurity {
	public static String decrypt(String cadenaEnc) throws Exception {
		String[] b64Array = cadenaEnc.split(":=");

		if (b64Array.length < 2) {
			throw new Exception("Cadena de Token mal Formada. Sin datos");
		}

		byte[] bKey = DatatypeConverter.parseBase64Binary(b64Array[0]);
		byte[] bData = DatatypeConverter.parseBase64Binary(b64Array[1]);

		SecretKey key = TripleDesEncryption.generateKey(bKey);
		byte[] clearData = TripleDesEncryption.decrypt(key, bData);
		return new String(clearData);
	}

	public static void main(String[] args) throws Exception {
		String usrEnc = "Wyy1C6Hs1oYO06RMa23j5rW66s2rns3H:=aM6YiUsJ6ifGQoilUtQ6xvCBd/Oje13JuMwxvvFax84=";
		String pwdEnc = "qKduoUyztSMpZODp02diBJ6A4BVwEIX+:=tiolOYI0ScEiL+ROH7kj3Q==";

		System.out.println("USER:[" + decrypt(usrEnc) +  "]");
		System.out.println("PSWD:[" + decrypt(pwdEnc) +  "]");
	}
}
