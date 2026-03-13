package com.syc.ws.controlinventarios;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSConnectionControlInventarios {

    private static Logger log = LoggerFactory.getLogger(WSConnectionControlInventarios.class);

    public JSONArray connectionWebService(String urlCad, String metodo, String tipoRespuesta, JSONObject inputJson) throws JSONException {
        HttpURLConnection conn = null;
        String output;
        JSONArray jsonOutput = new JSONArray();
        try {
            URL url = new URL(urlCad);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(metodo);
            conn.setRequestProperty("Content-Type", tipoRespuesta);
            String input = inputJson.toString();
            log.info("Datos enviados:\n" + input);
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            log.info("Output from Server ....");
            while ((output = br.readLine()) != null) {
                JSONObject obj = new JSONObject(output);
                JSONArray arrayJson = obj.getJSONArray("details");
                for (int i = 0; i < arrayJson.length(); i++) {
                    JSONObject jsonObj = (JSONObject) arrayJson.get(i);
                    JSONObject jsonrow = new JSONObject();
                    jsonrow.put("fAplicacion", jsonObj.getLong("fechaAutorizacion"));
                    jsonrow.put("cPartida", jsonObj.getString("partida"));
                    jsonrow.put("mImporte", jsonObj.getDouble("monto"));
                    jsonrow.put("nFolioPoliza", jsonObj.getInt("folioPoliza"));
                    jsonrow.put("cTipoPoliza", jsonObj.getString("tipoPoliza"));
                    jsonrow.put("cEvento", jsonObj.getString("eventoContable"));
                    jsonOutput.put(jsonrow);
                }
                //log.info(output+"\n");
            }
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Failed : HTTP error code : " + conn.getResponseCode());
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
        } catch (Exception e) {
            log.error("Error en la conexión del Web Service: " + e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            conn = null;
        }
        return jsonOutput;
    }
}
