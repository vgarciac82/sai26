package com.syc.sai.contabilidad.polizamanual.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.contabilidad.polizamanual.EventoManual;
import com.syc.sai.contabilidad.polizamanual.EventoManualEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EventoManualManager {

    private static Logger log = LoggerFactory.getLogger(EventoManualManager.class);

    public static EventoManual readEventoManual(Connection conn, Integer cIdGrupoEvento, Integer cIdSubGrupoEvento, String cIdEventoManual, Integer cPartida, String nCuenta) {
        String restrictions = " cIdGrupoEvento = " + cIdGrupoEvento;
        restrictions += " AND cIdSubGrupoEvento=" + cIdSubGrupoEvento;
        restrictions += " AND cIdEventoManual=" + cIdEventoManual;
        restrictions += " AND cPartida=" + cPartida;
        restrictions += " AND nCuenta=" + nCuenta;
        List<EventoManual> l;
        try {
            l = readEventoManualBy(conn, restrictions);
            if (!l.isEmpty()) {
                return l.get(0);
            } else {
                return null;
            }
        } catch (EventoManualEngineException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<EventoManual> readEventoManualBy(Connection conn, String restrictions) throws EventoManualEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<EventoManual> l = new ArrayList<EventoManual>();
        try {
            String qry = "";
            qry = "select * from tEventoManual WITH(nolock) where " + restrictions + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeEventoManual(rs));
            }
            return l;
        } catch (Exception e) {
            throw new EventoManualEngineException(e);
        } finally {
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int saveEventoManual(Connection conn, EventoManual eventoManual) throws EventoManualEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        try {
            String qry = "INSERT INTO tEventoManual (cevento, ndocRenglon, aejercicioFiscal, cidGrupoEvento, cidSubGrupoEvento, cidEventoManual, cpartida, ncuenta) values( ?,?,?,?,?,?,?,?,? );";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, eventoManual.getCevento());
            pStatement.setInt(cnt++, eventoManual.getNdocRenglon());
            pStatement.setInt(cnt++, eventoManual.getAejercicioFiscal());
            pStatement.setInt(cnt++, eventoManual.getCidGrupoEvento());
            pStatement.setInt(cnt++, eventoManual.getCidSubGrupoEvento());
            pStatement.setString(cnt++, eventoManual.getCidEventoManual());
            pStatement.setInt(cnt++, eventoManual.getCpartida());
            pStatement.setString(cnt++, eventoManual.getNcuenta());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new EventoManualEngineException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int updateEventoManual(Connection conn, EventoManual eventoManual) throws EventoManualEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        try {
            String qry = "UPDATE tEventoManual SET teventoRelacion=?, cevento=?, ndocRenglon=?, aejercicioFiscal=?, cidGrupoEvento=?, cidSubGrupoEvento=?, cidEventoManual=?, cpartida=?, ncuenta=?;";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, eventoManual.getCevento());
            pStatement.setInt(cnt++, eventoManual.getNdocRenglon());
            pStatement.setInt(cnt++, eventoManual.getAejercicioFiscal());
            pStatement.setInt(cnt++, eventoManual.getCidGrupoEvento());
            pStatement.setInt(cnt++, eventoManual.getCidSubGrupoEvento());
            pStatement.setString(cnt++, eventoManual.getCidEventoManual());
            pStatement.setInt(cnt++, eventoManual.getCpartida());
            pStatement.setString(cnt++, eventoManual.getNcuenta());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new EventoManualEngineException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int saveOrUpdateEventoManual(Connection conn, EventoManual eventoManual) throws EventoManualEngineException {
        if (readEventoManual(conn, eventoManual.getCidGrupoEvento(), eventoManual.getCidSubGrupoEvento(), eventoManual.getCidEventoManual(), eventoManual.getCpartida(), eventoManual.getNcuenta()) == null) {
            return saveEventoManual(conn, eventoManual);
        } else {
            return updateEventoManual(conn, eventoManual);
        }
    }

    private static EventoManual extraeEventoManual(ResultSet rs) throws EventoManualEngineException {
        try {
            EventoManual pojo = new EventoManual();
            pojo.setCevento(rs.getString("cevento"));
            pojo.setNdocRenglon(rs.getInt("ndocRenglon"));
            pojo.setAejercicioFiscal(rs.getInt("aejercicioFiscal"));
            pojo.setCidGrupoEvento(rs.getInt("cidGrupoEvento"));
            pojo.setCidSubGrupoEvento(rs.getInt("cidSubGrupoEvento"));
            pojo.setCidEventoManual(rs.getString("cidEventoManual"));
            pojo.setCpartida(rs.getInt("cpartida"));
            pojo.setNcuenta(rs.getString("ncuenta"));
            return pojo;
        } catch (Exception e) {
            throw new EventoManualEngineException(e);
        }
    }
}
