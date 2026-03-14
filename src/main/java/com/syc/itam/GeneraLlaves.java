package com.syc.itam;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.sql.DataSource;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import java.util.Base64;

public class GeneraLlaves {

    private DataSource ds = null;

    private String cve_unica = "";

    private String nombre_persona = "";

    public GeneraLlaves(DataSource ds, String nombre_persona, String cve_unica) {
        this.ds = ds;
        this.nombre_persona = nombre_persona;
        this.cve_unica = cve_unica;
    }

    public Object[] generaLlaves(String cve_unica) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        //SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //keyGen.initialize(1024, random);
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        byte[] privateKeyBytes = privateKey.getEncoded();
        byte[] publicKeyBytes = publicKey.getEncoded();
        // Get the formats of the encoded bytes
        // PKCS#8
        String formatPrivate = privateKey.getFormat();
        // X.509
        String formatPublic = publicKey.getFormat();
        System.out.println("  Private Key Format : " + formatPrivate);
        System.out.println("  Public Key Format  : " + formatPublic);
        X509Certificate cert = generaCertificado(keyPair);
        //guardamos el cert en la BD y enviamos la privada de regreso al cliente
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            String sSQL = "insert into cg_llaves (cve_unica,certificado,fecha_alta,token) values (?,?,sysdate,?)";
            PreparedStatement prep = conn.prepareStatement(sSQL);
            prep.setString(1, cve_unica);
            prep.setBytes(2, cert.getEncoded());
            //prep.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
            com.syc.utils.Cipher authCipher;
            byte[] random_number = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };
            String pass1 = "}$p4$sW0rDd3F0r71M4x!?";
            // Create authentication cipher with this key
            authCipher = new com.syc.utils.Cipher(pass1, random_number);
            // Encrypt Encryption String using authentication cipher
            byte[] encryptedData = authCipher.encrypt(privateKey.getEncoded());
            // Decrypt Encryption String using authentication cipher
            //String decryptedString = authCipher.decrypt(encryptedData);
            //prep.setBytes(4, encryptedData);
            prep.setBytes(3, encryptedData);
            prep.executeUpdate();
            prep = null;
        } catch (SQLException exc) {
            throw new Exception(exc);
        } finally {
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            conn = null;
        }
        return new Object[] { cert.getEncoded(), privateKeyBytes };
        /*File f = new File("C:\\llaveprivada.key");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
        dos.write(privateKeyBytes);
        dos.flush();
        dos.close();*/
        /*FileOutputStream fos = new FileOutputStream(new File("C:\\llaves.ks"));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(keyPair);
		oos.close();
		fos.close();
		
		fos = new FileOutputStream(new File("C:\\llave_publica.cer"));
		oos = new ObjectOutputStream(fos);
		oos.writeObject(publicKey);
		oos.close();
		fos.close();
		
		fos = new FileOutputStream(new File("C:\\llave_privada.ks"));
		oos = new ObjectOutputStream(fos);
		oos.writeObject(privateKey);
		oos.close();
		fos.close();*/
    }

    private X509Certificate generaCertificado(KeyPair keyPair) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Hashtable subject = new Hashtable();
        subject.put(X509Principal.CN, nombre_persona + " (" + cve_unica + ")");
        subject.put(X509Principal.OU, "ITAM");
        subject.put(X509Principal.O, "ITAM");
        subject.put(X509Principal.L, "DF");
        subject.put(X509Principal.ST, "DF");
        subject.put(X509Principal.C, "MX");
        Hashtable issuer = new Hashtable();
        issuer.put(X509Principal.OU, "SyC Trusted CA");
        issuer.put(X509Principal.O, "SyC Constructores de Sistemas");
        issuer.put(X509Principal.L, "Mexico");
        issuer.put(X509Principal.ST, "DF");
        issuer.put(X509Principal.C, "MX");
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        certGen.setSerialNumber(BigInteger.valueOf(2));
        certGen.setIssuerDN(new X509Principal(issuer));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10 * 60 * 1000));
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2500);
        //certGen.setNotAfter(new Date(System.currentTimeMillis() + (60 * (24 * 60 * 60 * 1000))));
        certGen.setNotAfter(new Date(calendar.getTimeInMillis()));
        certGen.setSubjectDN(new X509Principal(subject));
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
        //certGen.setSignatureAlgorithm("RSA");
        X509Certificate cert = certGen.generate(keyPair.getPrivate());
        cert.checkValidity(new Date());
        cert.verify(keyPair.getPublic());
        /*FileOutputStream fos = new FileOutputStream(new File("C:\\certificado.cer"));
		fos.write(cert.getEncoded());
		fos.flush();
		fos.close();*/
        return cert;
    }
}
