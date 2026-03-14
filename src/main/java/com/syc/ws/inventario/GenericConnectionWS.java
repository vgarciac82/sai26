package com.syc.ws.inventario;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class GenericConnectionWS {

    private static Logger log = LoggerFactory.getLogger(GenericConnectionWS.class);

    // Sin parametros al web service
    public JSONObject connectionWebService(String urlCad, String metodo, String tipoRespuesta) {
        HttpURLConnection conn = null;
        JSONObject respJson = new JSONObject();
        try {
            URL url = new URL(urlCad);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(metodo);
            conn.setRequestProperty("Accept", tipoRespuesta);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Error occurred", "Failed : HTTP error code : " + conn.getResponseCode());
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            log.info("Output from Server .... \n");
            int i = 0;
            while ((output = br.readLine()) != null) {
                respJson.put("" + i, output);
                log.info("Object: {}", output);
                i++;
            }
        } catch (Exception e) {
            try {
                respJson.put("0", e.getMessage());
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                log.error("Error occurred", "Error en JSON webservice: " + e1);
                e1.printStackTrace();
            }
            log.error("Error occurred", "Error en la conección con el web service: " + e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            conn = null;
        }
        return respJson;
    }

    public String connectionWebService(String urlCad, String metodo, String tipoRespuesta, JSONObject inputJson) throws JSONException {
        HttpURLConnection conn = null;
        String output;
        String respOutput = "";
        try {
            URL url = new URL(urlCad);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(metodo);
            conn.setRequestProperty("Content-Type", tipoRespuesta);
            String input = inputJson.toString();
            log.info("Object: {}", "Datos enviados:\n" + input);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes("UTF-8").toPath());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            log.info("Output from Server ....");
            while ((output = br.readLine()) != null) {
                respOutput = output;
                log.info("Object: {}", output + "\n");
            }
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Error occurred", "Failed : HTTP error code : " + conn.getResponseCode());
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
        } catch (Exception e) {
            respOutput = e.getMessage();
            log.error("Error occurred", "Error en la conección del webservie: " + e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            conn = null;
        }
        return respOutput;
    }
}
