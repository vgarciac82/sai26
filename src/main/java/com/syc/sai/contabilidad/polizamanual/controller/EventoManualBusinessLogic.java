package com.syc.sai.contabilidad.polizamanual.controller;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.polizamanual.EventoManual;
import com.syc.sai.contabilidad.polizamanual.EventoManualEngineException;
import com.syc.sai.contabilidad.polizamanual.model.EventoManualManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EventoManualBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(EventoManualBusinessLogic.class);

    public List<EventoManual> readEventoManualBy(String restrictions) throws EventoManualEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return EventoManualManager.readEventoManualBy(conn, restrictions);
        } catch (Exception e) {
            throw new EventoManualEngineException(e);
        } finally {
        }
    }

    public int saveOrUpdateEventoManual(HttpServletRequest req) throws EventoManualEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            EventoManual eventoManual = new EventoManual();
            eventoManual.setCevento(req.getParameter("cevento"));
            eventoManual.setNdocRenglon(new Integer(req.getParameter("ndocRenglon")));
            eventoManual.setAejercicioFiscal(new Integer(req.getParameter("aejercicioFiscal")));
            eventoManual.setCidGrupoEvento(new Integer(req.getParameter("cidGrupoEvento")));
            eventoManual.setCidSubGrupoEvento(new Integer(req.getParameter("cidSubGrupoEvento")));
            eventoManual.setCidEventoManual(req.getParameter("cidEventoManual"));
            eventoManual.setCpartida(new Integer(req.getParameter("cpartida")));
            eventoManual.setNcuenta(req.getParameter("ncuenta"));
            req.getSession().setAttribute("eventoManual", eventoManual);
            return EventoManualManager.saveOrUpdateEventoManual(conn, eventoManual);
        } catch (Exception e) {
            throw new EventoManualEngineException(e);
        } finally {
        }
    }

    public List<EventoManual> autocompleteEventoManual(HttpServletRequest req) throws EventoManualEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            String nGrupo = (String) req.getSession().getAttribute("nGrupo");
            String nSubGrupo = (String) req.getSession().getAttribute("nSubGrupo");
            String nEvento = (String) req.getSession().getAttribute("nEvento");
            String cPartida = req.getParameter("term");
            String restrictions = " cIdGrupoEvento = " + nGrupo + " AND ";
            restrictions = " cIdSubGrupoEvento = " + nSubGrupo + " AND ";
            restrictions = " cIdEventoManual = " + nEvento + " AND";
            restrictions = " cPartida like '" + cPartida + "%'";
            return EventoManualManager.readEventoManualBy(conn, restrictions);
        } catch (Exception e) {
            throw new EventoManualEngineException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.error("Error cerrando la base de datos" + e, e);
                }
        }
    }
}
