package com.syc.sai.firmaElectronica.cert.client;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TestOSCP {

	public static void main(String[] args) throws Exception {

		TestOSCP ocsp = new TestOSCP();
		ocsp.procesaCertificados("/TestProd");

	}

	public void procesaCertificados(String path) throws Exception {

		File f = new File(path);

		if (f.isDirectory()) {

			File[] archivos = f.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					if (pathname.getAbsolutePath().toLowerCase().endsWith(".cer") || pathname.getAbsolutePath().toLowerCase().endsWith(".crt") || pathname.isDirectory())
						return true;
					else
						return false;
				}
			});

			for (int i = 0; i < archivos.length; i++) {
				File fProcesar = archivos[i];
				if (fProcesar.isDirectory()) {
					procesaCertificados(fProcesar.getAbsolutePath());
				} else {
					procesarArchivo(fProcesar.getAbsolutePath());
				}
			}
		} else {
			procesarArchivo(path);
		}

	}

	public void procesarArchivo(String fileName) throws Exception {
		System.out.println("==================== PROCESANDO CERTIFICADO " + fileName + "======================");
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			File file = new File(fileName);
			String message = "Test de archivo: " + fileName;

			HttpEntity data = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName()).addTextBody("text", message, ContentType.DEFAULT_BINARY).build();

			HttpUriRequest request = RequestBuilder.post("http://10.56.0.87:8080/OCSPService//rest/validateCert").setEntity(data).build();

			System.out.println("Executing request " + request.getRequestLine());

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						HttpEntity entity = response.getEntity();
						String cause = entity != null ? EntityUtils.toString(entity) : "";
						throw new ClientProtocolException("Unexpected response status: " + status + " Cause: " + cause);
					}
				}

			};

			String responseBody = httpclient.execute(request, responseHandler);
			System.out.println("----------------------------------------");
			System.out.println(responseBody);
		}
		System.out.println("==================================================================");
	}

}