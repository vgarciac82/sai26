package com.syc.signFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.Iterator;

import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;

public class FirmaArchivo {

	public static void main(String[] args) {
		try {
			FirmaArchivo fa = new FirmaArchivo();
			boolean bool = false;
			/*System.out.println(fa.firmaArchivo(new File("c:\\llave_privada.ks"), 
					"gelatinas".toCharArray(), 
					"itam", 
					new File("D:\\DoctosFortimax\\ITAM\\iText\\original.pdf")
					,null));*/
			/*System.out.println(fa.revisaFirma(new File("D:\\DoctosFortimax\\ITAM\\iText\\original.pdf"),
					new File("D:\\DoctosFortimax\\ITAM\\iText\\prof1.cer")));*/
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

	

	public boolean revisaFirma(File inFile,File cerFile) throws Exception {
		
		Security
				.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		InputStream fisfile = new FileInputStream(inFile);
		CMSSignedDataParser sdp = null;//new CMSSignedDataParser(null, null);
		try {
			// recData = sdp.getSignedContent();
			sdp.getSignedContent().drain();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return false;
		}

		SignerInformationStore signers = sdp.getSignerInfos();
		Collection c = signers.getSigners();
		Iterator it = c.iterator();
		
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(new FileInputStream(cerFile));
        PublicKey pk = cert.getPublicKey();            

		while (it.hasNext()) {
			SignerInformation signer = (SignerInformation) it.next();
			/*Collection certCollection = certs.getCertificates(signer.getSID());
			Iterator certIt = certCollection.iterator();
			X509Certificate certif = (X509Certificate) certIt.next();			
			System.out
					.println("verify returns: " + signer.verify(certif, "BC"));
			System.out
			.println("verify returns: " + signer.verify(pk, "BC"));*/
			return false;//signer.verify(pk, "BC");
		}
		
		return false;
	}

	/*public boolean firmaArchivo(File fks,char[] pass,String alias,File inFile,File outFile) throws Exception {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		KeyStore keystore = KeyStore.getInstance("JKS", "SUN");
		FileInputStream fis = new FileInputStream(fks);
		keystore.load(fis, pass);
		PrivateKey key = (PrivateKey) keystore.getKey(alias,pass);
		Certificate[] chain = keystore.getCertificateChain(alias);
		CertStore certsAndCRLs = CertStore.getInstance("Collection",
				new CollectionCertStoreParameters(Arrays.asList(chain)), "BC");
		X509Certificate cert = (X509Certificate) chain[0];

		// set up the generator
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
		gen.addSigner(key, cert, CMSSignedDataGenerator.DIGEST_SHA224);
		gen.addCertificatesAndCRLs(certsAndCRLs);

		FileInputStream fisdoc = new FileInputStream(inFile);
		byte[] inputBytes = new byte[fisdoc.available()];
		fisdoc.read(inputBytes);
		fisdoc.close();

		CMSProcessable data = new CMSProcessableByteArray(inputBytes);

		CMSSignedData signed = gen.generate(data, true, "BC");

		FileOutputStream contentStream = new FileOutputStream(outFile != null ? outFile : inFile);
		contentStream.write(signed.getEncoded());
		contentStream.close();
		
		return true;

	}*/
	
	public boolean firmaArchivo(File fks,File inFile,File outFile) throws Exception {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		FileInputStream fis = new FileInputStream(fks);
		byte[] encKey = new byte[fis.available()];
		fis.read(encKey);
		fis.close();
		X509EncodedKeySpec privKey = new X509EncodedKeySpec(encKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey key = keyFactory.generatePrivate(privKey);
		

		/*KeyStore keystore = KeyStore.getInstance("JKS", "SUN");
		PrivateKey pk = 
		FileInputStream fis = new FileInputStream(fks);
		keystore.load(fis, pass);
		PrivateKey key = (PrivateKey) keystore.getKey(alias,pass);
		Certificate[] chain = keystore.getCertificateChain(alias);
		CertStore certsAndCRLs = CertStore.getInstance("Collection",
				new CollectionCertStoreParameters(Arrays.asList(chain)), "BC");
		X509Certificate cert = (X509Certificate) chain[0];

		// set up the generator
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
		gen.addSigner(key, cert, CMSSignedDataGenerator.DIGEST_SHA224);
		gen.addCertificatesAndCRLs(certsAndCRLs);

		FileInputStream fisdoc = new FileInputStream(inFile);
		byte[] inputBytes = new byte[fisdoc.available()];
		fisdoc.read(inputBytes);
		fisdoc.close();

		CMSProcessable data = new CMSProcessableByteArray(inputBytes);

		CMSSignedData signed = gen.generate(data, true, "BC");

		FileOutputStream contentStream = new FileOutputStream(outFile != null ? outFile : inFile);
		contentStream.write(signed.getEncoded());
		contentStream.close();
		*/
		return true;

	}
}
