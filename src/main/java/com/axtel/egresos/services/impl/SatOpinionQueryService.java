package com.axtel.egresos.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.UUID;
import java.util.stream.Collectors;
import com.axtel.egresos.services.dto.OpinionResolveResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.cfdi.core.FacturaManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SatOpinionQueryService {

    private static final Logger log = LoggerFactory.getLogger(SatOpinionQueryService.class);

    private String apiKey;

    private String urlConnection;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUrlConnection() {
        return urlConnection;
    }

    public void setUrlConnection(String urlConnection) {
        this.urlConnection = urlConnection;
    }

    public OpinionResolveResponse consultOpinion(String login, File file, String sistema) throws IOException {
        log.trace("Inicio consultOpinion()");
        log.debug("Object: " + String.valueOf("Parametros recibidos -> login: " + login + ", sistema: " + sistema + ", archivo: " + file.getName()));
        String boundary = UUID.randomUUID().toString();
        URL url = new URL(getUrlConnection());
        log.debug("Object: " + String.valueOf("URL conexion: " + getUrlConnection()));
        log.trace("Abriendo conexion HTTP");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("X-API-Key", getApiKey());
        log.trace("Headers configurados correctamente");
        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            log.trace("Escribiendo campos del formulario");
            writeFormField(out, boundary, "login", login);
            writeFormField(out, boundary, "sistema", sistema);
            log.trace("Adjuntando archivo PDF");
            writeFileField(out, boundary, "file", file);
            out.writeBytes("--" + boundary + "--\r\n");
            log.debug("Payload multipart enviado correctamente");
        }
        int status = connection.getResponseCode();
        log.debug("Object: " + String.valueOf("HTTP Status recibido: " + status));
        InputStream responseStream = status < 400 ? connection.getInputStream() : connection.getErrorStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
            String body = reader.lines().collect(Collectors.joining("\n"));
            log.trace("Respuesta recibida del servicio");
            log.debug("Object: " + String.valueOf("Longitud del body recibido: " + body.length()));
            OpinionResolveResponse response = new ObjectMapper().readValue(body, OpinionResolveResponse.class);
            log.info("Object: {}", "Consulta de opinion completada. Status HTTP: " + status);
            return response;
        } finally {
            log.debug("Cerrando conexion HTTP");
            connection.disconnect();
        }
    }

    private void writeFormField(DataOutputStream out, String boundary, String name, String value) throws IOException {
        log.trace("Object: {}", "Escribiendo campo form-data: " + name);
        out.writeBytes("--" + boundary + "\r\n");
        out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        out.writeBytes(value + "\r\n");
    }

    private void writeFileField(DataOutputStream out, String boundary, String fieldName, File file) throws IOException {
        log.trace("Object: {}", "Escribiendo archivo en form-data: " + file.getName());
        log.debug("Object: " + String.valueOf("Tamano del archivo (bytes): " + file.length()));
        out.writeBytes("--" + boundary + "\r\n");
        out.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n");
        out.writeBytes("Content-Type: application/pdf\r\n\r\n");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        out.writeBytes("\r\n");
        log.trace("Archivo escrito correctamente en el stream");
    }

    public void saveFile(Connection conn, Caso process, Usuario user, String filePath) throws Exception {
        log.trace("Inicio saveFile()");
        log.debug("Object: " + String.valueOf("Parametros -> idGabinete: " + process.getIdGabinete() + ", usuario: " + user.getLogin() + ", filePath: " + filePath));
        if (process.getIdGabinete() <= 0) {
            log.debug("Object: " + String.valueOf("Proceso no persistido. idGabinete invalido: " + process.getIdGabinete()));
            throw new RuntimeException("No se ha guardado el tramite. Debe guardar el tramite primero para anexar documentos.");
        }
        log.trace("Obteniendo carpeta destino para Opinion de Cumplimiento");
        Carpeta folder = FacturaManager.obtenCarpetaDestino(conn, process, "Opinion de Cumplimiento", user.getLogin());
        log.debug("Object: " + String.valueOf("Carpeta destino -> tituloAplicacion: " + folder.getTituloAplicacion() + ", idGabinete: " + folder.getIdGabinete() + ", idCarpeta: " + folder.getIdCarpeta()));
        String ext = Util.getFileExtencion(filePath);
        log.debug("Object: " + String.valueOf("Extension detectada: " + ext));
        log.trace("Insertando documento en gestor documental");
        DocumentoManager.insertaDocumento(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), "SAT Formato 32D" + "." + ext, ext, user.getLogin(), filePath);
        log.info("Object: {}", "Archivo de Opinion de Cumplimiento insertado correctamente para el tramite idGabinete: " + process.getIdGabinete());
        log.trace("Fin saveFile()");
    }
}
