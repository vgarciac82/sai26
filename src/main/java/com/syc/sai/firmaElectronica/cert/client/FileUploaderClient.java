package com.syc.sai.firmaElectronica.cert.client;

import java.io.File;
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

/**
 * This example shows how to upload files using POST requests with encoding type
 * "multipart/form-data". For more details please read the full tutorial on
 * https://javatutorial.net/java-file-upload-rest-service
 * 
 * @author javatutorial.net
 */
public class FileUploaderClient {

	public static void main(String... args) throws IOException {

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			File file = new File("/Test/gava730717ae1.cer");
			String message = "This is a multipart post";

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
	}

}