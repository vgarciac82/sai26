package com.syc.cfdi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.codec.binary.Base64;
// // // // import java.util.Base64; (Ambiguous with Commons Codec) (Ambiguous with Commons Codec) (Ambiguous with Commons Codec) (Ambiguous with Commons Codec)

public class CertificateUtil {

    private static final int BUFFER_SIZE = 65535;

    /**
     * @param certificateFilePath
     * @return the base64 encoded array from given certificate content.
     *
     * @throws Exception
     */
    public static byte[] convertCertificateToByteArray(String certificateFilePath) throws Exception {
        if (certificateFilePath == null || certificateFilePath.isEmpty()) {
            throw new Exception("certificateFilePath should not be null or empty");
        }
        File file = new File(certificateFilePath);
        if (!file.exists()) {
            throw new Exception("File not exist : " + file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            throw new Exception("File shouldn't be a directory : " + file.getAbsolutePath());
        }
        InputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int numberOfReadBytes;
        while ((numberOfReadBytes = inputStream.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, numberOfReadBytes);
        }
        byte[] certificateInBytes = byteArrayOutputStream.toByteArray();
        byte[] encodedBytes = Base64.encodeBase64(certificateInBytes);
        inputStream.close();
        return encodedBytes;
    }

    /**
     * Convert the certificate into base64 encoded string.
     *
     * @param certificateFilePath
     * @return
     * @throws Exception
     */
    public static String convertCertificateToString(String certificateFilePath) throws Exception {
        byte[] encodedBytes = convertCertificateToByteArray(certificateFilePath);
        String str = new String(encodedBytes);
        return str;
    }

    public static void main(String[] args) throws Exception {
        String certificateFilePath = "C:\\Vicente\\certs\\gacv820307ql8.cer";
        String certificateInfo = CertificateUtil.convertCertificateToString(certificateFilePath);
        System.out.println(certificateInfo);
    }
}
