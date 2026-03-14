package com.axtel.sisecop.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.axtel.sisecop.dto.ProyectoDTO;
import com.axtel.sisecop.dto.TerritoryDTO;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.entities.ProyectoServicioTerritorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.cfdi.db.CloseObject;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProyectoServicioService extends ProyectoServicioGeneral {

    private static final Logger log = LoggerFactory.getLogger(ProyectoServicioService.class);

    private CasoBusinessLogic processService;

    private NotificationGeneratorService notificationService;

    public static final Map<Integer, String> PROCESS_STEPS = new HashMap<>();

    public static final Map<Integer, String> PROCESS_RESPONSIBLES = new HashMap<>();

    static {
        PROCESS_RESPONSIBLES.put(2, "PROJECT_AUTHORIZER");
        PROCESS_RESPONSIBLES.put(8, "PROJECT_REGISTER");
        PROCESS_RESPONSIBLES.put(10, "PROJECT_CONSULT");
        PROCESS_STEPS.put(2, "autorization_project");
        PROCESS_STEPS.put(8, "register_project");
        PROCESS_STEPS.put(10, "consult_project");
    }

    public ProyectoServicioService() {
        processService = new CasoBusinessLogic();
        notificationService = new NotificationGeneratorService();
    }

    public ProyectoServicioService(String jniName) {
        super(jniName);
        processService = new CasoBusinessLogic(jniName);
        notificationService = new NotificationGeneratorService(jniName);
    }

    public List<ProyectoServicioTerritorio> readTerritoriosByServicioId(int servicioId) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return getTerritorioRepositorio().readByServicioId(conn, servicioId);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public ProyectoServicio create(ProyectoServicio proyectoServicio) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            int id = createProyectoServicio(connection, proyectoServicio);
            connection.commit();
            return getServicioRepositorio().findById(connection, id);
        } catch (SQLException e) {
            Util.rollback(connection);
            throw e;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public List<ProyectoServicio> findAll() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            List<ProyectoServicio> proyectoServicios = getServicioRepositorio().findAll(connection);
            return proyectoServicios;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public ProyectoServicio findById(int id) throws SQLException {
        try (Connection connection = getConnection()) {
            return getServicioRepositorio().findById(connection, id);
        } catch (SQLException e) {
            throw e;
        }
    }

    public ProyectoServicioTerritorio createTerritory(TerritoryDTO territory) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            int id = getTerritorioRepositorio().create(connection, territory);
            connection.commit();
            return getTerritorioRepositorio().readById(connection, id);
        } catch (SQLException e) {
            Util.rollback(connection);
            throw e;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public void deleteTerritory(int idTerritory) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            int deleted = getTerritorioRepositorio().delete(connection, idTerritory);
            log.debug("Object: {}", deleted + " rows have benn deleted from territory ");
            connection.commit();
        } catch (SQLException e) {
            Util.rollback(connection);
            throw e;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public ProyectoServicio update(ProyectoServicio proyectoServicio) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            proyectoServicio.setServicioModificacion(new Timestamp(System.currentTimeMillis()));
            getServicioRepositorio().update(connection, proyectoServicio);
            connection.commit();
            return getServicioRepositorio().findById(connection, proyectoServicio.getServicioId());
        } catch (SQLException e) {
            Util.rollback(connection);
            throw e;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public ProyectoServicio nextStep(ProyectoServicio proyectoServicio, Usuario user) {
        Connection connection = null;
        try {
            connection = getConnection();
            Caso process = processService.getCaso(proyectoServicio.getIdProcess());
            Map<String, String> datos = Util.readValuesCasoDato(process.getCasoDato());
            process = processService.avanzaCaso(connection, process, user.getLogin(), "", new String[] { PROCESS_RESPONSIBLES.get(proyectoServicio.getEstatus().getEstatusId()) }, new String[] { PROCESS_STEPS.get(proyectoServicio.getEstatus().getEstatusId()) }, datos, null);
            proyectoServicio.setServicioModificacion(new Timestamp(System.currentTimeMillis()));
            getObservationReporitory().insertObservations(connection, proyectoServicio.getServicioId());
            getServicioRepositorio().nextStep(connection, proyectoServicio);
            if (10 == proyectoServicio.getEstatus().getEstatusId())
                generarVersionProyecto(connection, proyectoServicio);
            proyectoServicio = getServicioRepositorio().findById(connection, proyectoServicio.getServicioId());
            connection.commit();
            notificationService.sendNotificacion(proyectoServicio);
            return proyectoServicio;
        } catch (SQLException | GestionException | IOException e) {
            Util.rollback(connection);
            throw new RuntimeException("Error avanzando proce so. " + e.toString(), e);
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public void generarVersionProyecto(Connection dbConnection, ProyectoServicio proyecto) throws IOException {
        String urlString = null;
        try {
            urlString = ConfiguraAplicativoManager.getSystemSetting(dbConnection, "URL_ELASTICSEARCH") + "/proyectos/_doc/";
            log.info("Object: {}", "URL configurada para Elasticsearch: " + urlString);
        } catch (Exception e) {
            log.error("No se encontró la URL configurada para Elasticsearch", e);
            throw new RuntimeException("No se encontró URL configurada: " + e.toString());
        }
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        log.info("Object: {}", "Conexión a Elasticsearch iniciada en: " + urlString);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        log.info("Se ha configurado el Content-Type a 'application/json; charset=UTF-8'");
        ProyectoDTO proyectoDTO = new ProyectoDTO(proyecto.getServicioId(), proyecto.getServicioTitulo(), proyecto.getServicioObjetivos(), proyecto.getServicioFolioPre() + "/" + proyecto.getServicioFolioAnio() + "/" + String.format("%04d", proyecto.getServicioFolioNum()));
        log.debug("Object: {}", "Generado el objeto proyecto: " + proyectoDTO);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInputString = mapper.writeValueAsString(proyectoDTO);
        log.debug("Object: {}", "JSON generado para el proyecto: " + jsonInputString);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            log.info("JSON enviado a Elasticsearch con éxito.");
        } catch (IOException e) {
            log.error("Error al enviar el JSON a Elasticsearch", e);
            throw e;
        }
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            log.info("Object: {}", "Proyecto índice enviado correctamente a Elasticsearch: " + proyectoDTO);
        } else {
            log.error("Error occurred", "Error al enviar el proyecto a indexación. Código de respuesta: " + responseCode);
            throw new RuntimeException("Error al enviar el proyecto a indexación. Código de respuesta: " + responseCode);
        }
        conn.disconnect();
        log.info("Conexión a Elasticsearch cerrada.");
    }
}
