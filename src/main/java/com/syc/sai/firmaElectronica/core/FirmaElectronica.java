package com.syc.sai.firmaElectronica.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.io.FileUtils;
import org.apache.commons.ssl.PKCS8Key;
import com.syc.cfdi.CertificateUtil;
import sun.security.x509.RDN;
import sun.security.x509.X500Name;
import java.util.Base64;

public class FirmaElectronica {

    private X509Certificate cert;

    private PrivateKey key;

    private String keyString;

    private String password;

    private String rfc;

    public FirmaElectronica(File fCert, File fKey, String password) throws Exception {
        this.password = password;
        readCertificate(fCert);
        readPrivateKey(fKey, password);
        this.keyString = CertificateUtil.convertCertificateToString(fCert.getAbsolutePath());
        Map<String, String> informacionCertificado = getCNCertificado(getCert());
        this.rfc = informacionCertificado.get("OID.2.5.4.45");
    }

    public FirmaElectronica() {
    }

    public X509Certificate getCert() {
        return cert;
    }

    public Map<String, String> getCNCertificado(X509Certificate fact) throws Exception {
        X500Principal datos = fact.getSubjectX500Principal();
        X500Name x500name = new X500Name(datos.getName());
        List<RDN> rdns = x500name.rdns();
        Map<String, String> valores = new HashMap<String, String>();
        for (Iterator<RDN> i = rdns.iterator(); i.hasNext(); ) {
            RDN rdn = i.next();
            String[] elementos = rdn.toString().split("=", 2);
            valores.put(elementos[0], elementos[1]);
        }
        return valores;
    }

    public PrivateKey getKey() {
        return key;
    }

    public String getKeyString() {
        return keyString;
    }

    public String getPassword() {
        return password;
    }

    public String getRfc() {
        return rfc;
    }

    private void readCertificate(File cerFile) throws Exception {
        InputStream isCer = new FileInputStream(cerFile);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate fact = (X509Certificate) cf.generateCertificate(isCer);
        setCert(fact);
    }

    /**
     * Retorna una llave privada utilizando los datos y la passphrase indicada. Se
     * utiliza not-yet-commons-ssl para esto ya que no hay manera simple de hacerlo
     * directo con java
     *
     * @throws Exception
     */
    private PrivateKey readPrivateKey(byte[] encryptedKey, String passphrase) throws Exception {
        PKCS8Key pkcs8 = new PKCS8Key(encryptedKey, passphrase.toCharArray());
        return pkcs8.getPrivateKey();
    }

    private void readPrivateKey(File pkeyFile, String passphrase) throws Exception {
        byte[] keyBytes = FileUtils.readFileToByteArray(pkeyFile);
        setKey(readPrivateKey(keyBytes, passphrase));
    }

    public void setCert(X509Certificate cert) {
        this.cert = cert;
    }

    public void setKey(PrivateKey key) {
        this.key = key;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }
}
