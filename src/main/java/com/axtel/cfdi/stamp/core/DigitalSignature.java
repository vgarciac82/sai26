package com.axtel.cfdi.stamp.core;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class DigitalSignature {

	private PrivateKey key = null;
	private String keyPassword = "";
	private String keyString = "";
	private X509Certificate cert = null;
	private String certString = "";

	public PrivateKey getKey() {
		return key;
	}

	public void setKey(PrivateKey key) {
		this.key = key;
	}

	public String getKeyPassword() {
		return this.keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	public String getKeyString() {
		return keyString;
	}

	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}

	public X509Certificate getCert() {
		return cert;
	}

	public void setCert(X509Certificate cert) {
		this.cert = cert;
	}

	public String getCertString() {
		return certString;
	}

	public void setCertString(String certString) {
		this.certString = certString;
	}

}
