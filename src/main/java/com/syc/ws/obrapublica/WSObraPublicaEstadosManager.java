package com.syc.ws.obrapublica;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class WSObraPublicaEstadosManager {

    private static Logger log = LoggerFactory.getLogger(WSObraPublicaEstadosManager.class);

    public static JSONObject ObtenerJsonObraPublicaEstados(Connection conn, int nIdEstado) throws Exception {
        PreparedStatement psInfoPago = null;
        ResultSet rsInfoPago = null;
        JSONObject jsonResp = null;
        try {
            //Validar si el ws está encendido
            int iWSActivo = validaEstatusWS(conn);
            if (iWSActivo != 0) {
                log.info("El Web service se encuentra configurado para que no se ejecute.");
                jsonResp = new JSONObject();
                jsonResp.put("estatus", "TRUE");
                JSONObject noAplicaOpt = new JSONObject();
                noAplicaOpt.put("idRealEstate", "-1");
                noAplicaOpt.put("reName", "No Aplica");
                JSONArray arr = new JSONArray();
                arr.put(noAplicaOpt);
                jsonResp.put("estados", arr);
            } else {
                jsonResp = ObtenerObraPublicaEstadosJson(conn, nIdEstado);
            }
            return jsonResp;
        } finally {
            CloseObject.closeObject(rsInfoPago);
            CloseObject.closeObject(psInfoPago);
        }
    }

    private static int validaEstatusWS(Connection conn) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        //WS inactivo
        int resp = -2;
        ResultSet rs = null;
        try {
            String query = " SELECT * FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) " + " WHERE G_NOMBRE = 'WS_OBRAPUBLICA' " + " AND GP_NOMBRE = 'Activa_WS_ObraPublica' AND GP_VALOR = 'TRUE'";
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                return 0;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            rs = null;
        }
        return resp;
    }

    private static JSONObject ObtenerObraPublicaEstadosJson(Connection conn, int nIdEstado) throws Exception {
        //"http://10.61.0.9:8080/conafor-real-estate-ws-app/services/realEstate/getRealEstatesInState";
        String url = urlWS_ObraPublicaEstados(conn);
        String metodo = "GET", tipoRespuesta = "application/json;charset=UTF-8";
        JSONObject respWS = null;
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("stateName", nIdEstado);
        url = url + "/" + nIdEstado;
        respWS = conexionWebService(url, metodo, tipoRespuesta, jsonObj);
        return respWS;
    }

    private static String urlWS_ObraPublicaEstados(Connection conn) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        String url = "";
        String GP_NOMBRE = "Ruta_WS_Estados";
        ResultSet rs = null;
        try {
            String query = " SELECT * FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) " + " WHERE G_NOMBRE = 'WS_OBRAPUBLICA' " + " AND GP_NOMBRE = ? ";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, GP_NOMBRE);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                url = rs.getString("GP_VALOR");
            }
            System.out.println("URL: " + url);
            log.info("Object: {}", "URL: " + url);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            rs = null;
        }
        return url;
    }

    private static JSONObject conexionWebService(String urlCad, String metodo, String tipoRespuesta, JSONObject inputJson) throws JSONException, UnsupportedEncodingException {
        HttpURLConnection conn = null;
        String output = "";
        JSONObject jsonOutput = new JSONObject();
        try {
            URL url = new URL(urlCad);
            log.info("Object: {}", "URL::\n" + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(metodo);
            conn.setRequestProperty("Content-Type", tipoRespuesta);
            String input = inputJson.toString();
            log.info("Object: {}", "Datos enviados:\n" + input);
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            log.info("Output from Server ....");
            jsonOutput.put("msg", "OK");
            jsonOutput.put("estatus", "TRUE");
            while ((output = br.readLine()) != null) {
                jsonOutput.put("estados", new JSONArray(URLDecoder.decode(output, "UTF-8")));
                log.info("Object: {}", output);
            }
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                jsonOutput.put("msg", "Failed : HTTP error code : " + conn.getResponseCode());
                jsonOutput.put("estatus", "FALSE");
                log.error("Error occurred", "Failed : HTTP error code : " + conn.getResponseCode());
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
        } catch (Exception e) {
            // TODO: handle exception
            try {
                jsonOutput.put("msg", e.getMessage());
                jsonOutput.put("estatus", "FALSE");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                log.error("Error occurred", "Error en JSON WebService: " + e1);
                e1.printStackTrace();
            }
            log.error("Error en la conexión del Web Service: " + e, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            conn = null;
        }
        return jsonOutput;
    }
}
