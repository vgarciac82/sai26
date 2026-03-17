package com.syc.sai.firmaElectronica.cert.client;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
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
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ValidaCert {

    private static String urlEndPoint = null;

    private static final Logger log = LoggerFactory.getLogger(ValidaCert.class);

    static {
        log.info("Validando URL del endpoint para validar certificados");
        if (StringUtils.isBlank(urlEndPoint)) {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            urlEndPoint = cabl.getSystemSetting("OCSP_WS_ENDPOINT");
            log.info("Object: {}", "URL End Point: " + urlEndPoint);
            cabl = null;
        }
    }

    public static String validate(String filePath, String key) throws IOException {
        log.info("Object: {}", "Iniciando validacion de certificado: " + filePath + " Key: " + key);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            log.trace("Object: {}", "HTTP Client generado exitosamente: " + httpclient);
            File testCert = new File(filePath);
            log.debug("Object: " + String.valueOf("Se validara el archivo: " + testCert.getAbsolutePath()));
            HttpEntity data = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addBinaryBody("file", testCert, ContentType.DEFAULT_BINARY, testCert.getName()).addTextBody("text", StringUtils.trimToEmpty(key), ContentType.DEFAULT_BINARY).build();
            log.trace("DATA para envio creado.");
            log.trace("Creando request ... ");
            HttpUriRequest request = RequestBuilder.post(urlEndPoint).setEntity(data).build();
            log.debug("Object: " + String.valueOf("Executing request " + request.getRequestLine()));
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    log.info("Object: {}", "Respuesta obtenida:" + response + " Se inicia su proceasmiento");
                    int status = response.getStatusLine().getStatusCode();
                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        HttpEntity entity = response.getEntity();
                        String cause = entity != null ? EntityUtils.toString(entity) : "";
                        throw new ClientProtocolException("Unexpected response status: " + status + " Cause: " + cause);
                    }
                }
            };
            log.info("Object: {}", "Executing request " + request.getRequestLine());
            String certStatus = httpclient.execute(request, responseHandler);
            log.trace("----------------------------------------");
            log.info("Object: {}", "Estatus del certificado: " + certStatus);
            return certStatus;
        }
    }
}
