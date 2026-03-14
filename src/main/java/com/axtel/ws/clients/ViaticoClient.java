package com.axtel.ws.clients;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.axtel.egresos.viaticos.ViaticoRequest;
import com.axtel.egresos.viaticos.ViaticoResponse;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ViaticoClient {

    private String RUTA_ACTUALIZA_VIATICOS = "http://localhost:9190/api/asistencia/actualizar";

    private String RUTA_REVERTIR_VIATICOS = "http://localhost:9190/api/asistencia/revertir";

    private String RUTA_VERIFICA_FORMATOS = "http://localhost:9190/api/formatos/verifica";

    private String RUTA_VALIDA_VACACIONES = "http://localhost:9190/api/formatos/tieneVacaciones";

    private static final Logger log = LoggerFactory.getLogger(ViaticoClient.class.getName());

    public ViaticoClient(String urlServicio) {
        this.RUTA_ACTUALIZA_VIATICOS = urlServicio;
    }

    public ViaticoResponse actualizarViaticos(ViaticoRequest request, String url) throws Exception {
        return enviarPeticion(url, request);
    }

    public ViaticoResponse revertirAsistencia(ViaticoRequest request, String url) throws Exception {
        request.setConcepto(null);
        return enviarPeticion(url, request);
    }

    private ViaticoResponse enviarPeticion(String ruta, ViaticoRequest request) throws Exception {
        Gson gson = new Gson();
        String jsonInput = gson.toJson(request);
        log.info("Object: {}", "Enviando petición a: " + ruta);
        log.info("Object: {}", "Request JSON: " + jsonInput);
        URL url = new URL(ruta);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(jsonInput.getBytes("UTF-8"));
            wr.flush();
        }
        int responseCode = conn.getResponseCode();
        log.info("Object: {}", "Código de respuesta HTTP: " + responseCode);
        BufferedReader br = new BufferedReader(new InputStreamReader((responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"));
        StringBuilder responseStr = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            responseStr.append(line);
        }
        br.close();
        conn.disconnect();
        String respuesta = responseStr.toString().trim();
        log.info("Object: {}", "Respuesta cruda del servicio: " + respuesta);
        ViaticoResponse response = gson.fromJson(respuesta, ViaticoResponse.class);
        log.info("Object: {}", "Respuesta parseada correctamente: " + "Empleado=" + response.getIdEmpleado() + ", Success=" + response.isSuccess() + ", Mensaje=" + response.getMessage());
        return response;
    }

    public boolean verificarFormato(ViaticoRequest request, String url) throws Exception {
        String urlCompleta = String.format("%s?numeroEmpleado=%d&fechaInicio=%s&fechaFin=%s", url, request.getIdEmpleado(), request.getFechaInicio(), request.getFechaFin());
        log.info("Object: {}", "Llamando al servicio de verificación de formatos: " + urlCompleta);
        String respuesta = ejecutarGet(urlCompleta);
        boolean resultado = Boolean.parseBoolean(respuesta.trim());
        log.info("Object: {}", "Resultado parseado (formatos): " + resultado);
        return resultado;
    }

    public boolean verificarVacaciones(int idEmpleado, String url) throws Exception {
        String urlCompleta = String.format("%s/%d", url, idEmpleado);
        log.info("Object: {}", "Llamando al servicio de verificación de vacaciones: " + urlCompleta);
        String respuesta = ejecutarGet(urlCompleta);
        // El servicio devuelve "1" o "0"
        boolean tieneVacaciones = "1".equals(respuesta.trim());
        log.info("Object: {}", "✅ Resultado parseado (vacaciones): " + tieneVacaciones);
        return tieneVacaciones;
    }

    // =========================================================
    // 🔧 Método común GET (para verificar formatos/vacaciones)
    // =========================================================
    private String ejecutarGet(String urlCompleta) throws Exception {
        URL url = new URL(urlCompleta);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        int responseCode = conn.getResponseCode();
        log.info("Object: {}", "🔁 Código HTTP: " + responseCode);
        BufferedReader br = new BufferedReader(new InputStreamReader((responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"));
        StringBuilder responseStr = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            responseStr.append(line);
        }
        br.close();
        conn.disconnect();
        String respuesta = responseStr.toString().trim();
        log.info("Object: {}", "📥 Respuesta cruda GET: " + respuesta);
        return respuesta;
    }
}
