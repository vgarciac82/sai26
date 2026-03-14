package com.syc.adquisiciones.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.core.DatosProcedSAC;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class BaseSACManager {

    private static Logger log = LoggerFactory.getLogger(BaseSACManager.class);

    public JSONArray obtieneDatQuery(Connection conn, String query) throws SQLException, JSONException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            int token = 0;
            while (rs.next()) {
                if (token > 0) {
                    jsonObj = new JSONObject();
                }
                jsonObj.put("Descripcion", rs.getString(1));
                jsonObj.put("Id", rs.getString(2));
                token++;
                arrayObj.put(jsonObj);
                jsonObj = null;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (JSONException e1) {
            throw new JSONException(e1);
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            pstmt = null;
            rs = null;
        }
        return arrayObj;
    }

    public JSONArray datGuardados(Connection conn, String query) throws SQLException, JSONException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsMetadata = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            rsMetadata = rs.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            if (rs.next()) {
                jsonObj.put("HAYINFO", true);
                for (int i = 1; i <= totalcolumnas; i++) {
                    jsonObj.put(rsMetadata.getColumnName(i), ("null".equalsIgnoreCase(rs.getString(i)) || null == rs.getString(i) ? "NULL" : rs.getString(i)));
                }
            } else {
                jsonObj.put("HAYINFO", false);
            }
            arrayObj.put(jsonObj);
        } finally {
            jsonObj = null;
            if (pstmt != null) {
                pstmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            rsMetadata = null;
            pstmt = null;
            rs = null;
        }
        return arrayObj;
    }

    public boolean addNewProcedimiento(Connection conn, DatosProcedSAC datos) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into mProcedimientoSAC (cIdProcedimientoSAC	,fSolicitud 	,cOficioSolicitud	,cAreaTecnica	,cProcedimientoTurnado	,nTipoProcedimiento\r\n" + "	,nIdMateriaProcedimiento	,cDenominacionProced	,cProyectoConvocatoria	,fAutConvocatoria	,fConvocatoria	,fJuntaAclaraciones\r\n" + "	,fAperturaProposiciones	,fEvaluacionTecnica	,fFallo_ActaAdjucdicacion	,fGeneracionContratoCNET	,fExpedienteTurnadoContrato\r\n" + "	,nProveedorDadoAlta_SAICNET	,fCaptura	,cLoginCaptura	,nEstatus) \r\n" + "	values(?,convert(date,?),?,?,?,?,?,?,?\r\n" + "	,convert(date,?),convert(date,?),convert(date,?),convert(date,?),convert(date,?),convert(date,?),convert(date,?),convert(date,?)\r\n" + "	,?,convert(date,GETDATE()),?,1)";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, datos.getcNumeroProcedimiento());
            ps.setString(2, datos.getcFechaSolicitud());
            ps.setString(3, datos.getcOficioSolicitud());
            ps.setString(4, datos.getcAreaTecnica());
            ps.setString(5, datos.getcProcedimientoContratacionTurnado());
            ps.setInt(6, datos.getnTipoProcedimiento());
            ps.setInt(7, datos.getnMateriaProcedimiento());
            ps.setString(8, datos.getcDenominacionProced());
            ps.setString(9, datos.getcProyectoConvocatoria());
            ps.setString(10, datos.getcFechaAutConvocatoria());
            ps.setString(11, datos.getcFechaPublicacionConvocatoria());
            ps.setString(12, datos.getcFechaJuntaAclara());
            ps.setString(13, datos.getcFechaApertProposiciones());
            ps.setString(14, datos.getcFechaEvaluacionTecnica());
            ps.setString(15, datos.getcFechaFallo());
            ps.setString(16, datos.getcFechaGeneracionContrato());
            ps.setString(17, datos.getcFechaExpediente());
            ps.setInt(18, datos.getnProveedorDadoAlta());
            ps.setString(19, datos.getcLogin());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public int readIdProcedimiento(Connection conn, DatosProcedSAC datos) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nIdProced = 0;
        String query = "select *from mProcedimientoSAC with(Nolock) where cOficioSolicitud=?";
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, datos.getcOficioSolicitud());
            rs = ps.executeQuery();
            if (rs.next()) {
                nIdProced = rs.getInt("nIdProcedimientoSAC");
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return nIdProced;
    }

    public boolean addAreaResp(Connection conn, String cAreaResp, int nIdProced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into mAreaResponsableProcedSAC (nIdProcedimientoSAC,cAreaResponsable) values(?,?)";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, nIdProced);
            ps.setString(2, cAreaResp);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addAreaReq(Connection conn, String cAreaReq, int nIdProced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into mAreaRequirenteProcedSAC (nIdProcedimientoSAC,cAreaRequirente) values(?,?)";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, nIdProced);
            ps.setString(2, cAreaReq);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addParticipantes(Connection conn, ArrayList<List<String>> tabla, int nIdProced) throws Exception {
        List<String> fila = new ArrayList<String>();
        Iterator<List<String>> itr = tabla.iterator();
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            while (itr.hasNext()) {
                fila = itr.next();
                if (existParticipante(conn, fila, nIdProced)) {
                    continue;
                }
                query = "insert into mParticipantesProceso (nIdProcedimientoSAC, nNumeroEmpleado, cNombre, cApellidoPat, cApellidoMat)\r\n" + "values(?,?,?,?,?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, nIdProced);
                ps.setString(2, fila.get(0));
                ps.setString(3, fila.get(1));
                ps.setString(4, fila.get(2));
                ps.setString(5, fila.get(3));
                if (ps.executeUpdate() <= 0) {
                    log.error("Error occurred", "Error mo se pudo guardar el participante " + fila.get(1) + " " + fila.get(2) + " " + fila.get(3));
                    throw new Exception("Error mo se pudo guardar el participante " + fila.get(1) + " " + fila.get(2) + " " + fila.get(3));
                }
            }
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existParticipante(Connection conn, List<String> fila, int nIdProced) throws Exception {
        boolean success = false;
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            query = "select *from mParticipantesProceso with(Nolock) where nIdProcedimientoSAC=? and nNumeroEmpleado=? and cNombre=? and cApellidoPat=? and cApellidoMat=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, nIdProced);
            ps.setString(2, fila.get(0));
            ps.setString(3, fila.get(1));
            ps.setString(4, fila.get(2));
            ps.setString(5, fila.get(3));
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateProcedSAC(Connection conn, DatosProcedSAC datos) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "update mProcedimientoSAC \r\n" + "set cIdProcedimientoSAC='" + datos.getcNumeroProcedimiento() + "'\r\n" + ",fSolicitud=convert(date,'" + datos.getcFechaSolicitud() + "')\r\n" + ",cOficioSolicitud='" + datos.getcOficioSolicitud() + "'\r\n" + ",cAreaTecnica='" + datos.getcAreaTecnica() + "'\r\n" + ",cProcedimientoTurnado='" + datos.getcProcedimientoContratacionTurnado() + "'\r\n" + ",nTipoProcedimiento=" + datos.getnTipoProcedimiento() + "\r\n" + ",nIdMateriaProcedimiento=" + datos.getnMateriaProcedimiento() + "\r\n" + ",cDenominacionProced='" + datos.getcDenominacionProced() + "'\r\n" + ",cProyectoConvocatoria='" + datos.getcProyectoConvocatoria() + "'\r\n" + ",fAutConvocatoria=convert(date,'" + datos.getcFechaAutConvocatoria() + "')\r\n" + ",fConvocatoria=convert(date,'" + datos.getcFechaPublicacionConvocatoria() + "')\r\n" + ",fJuntaAclaraciones=convert(date,'" + datos.getcFechaJuntaAclara() + "')\r\n" + ",fAperturaProposiciones=convert(date,'" + datos.getcFechaApertProposiciones() + "')\r\n" + ",fEvaluacionTecnica=convert(date,'" + datos.getcFechaEvaluacionTecnica() + "')\r\n" + ",fFallo_ActaAdjucdicacion=convert(date,'" + datos.getcFechaFallo() + "')\r\n" + ",fGeneracionContratoCNET=convert(date,'" + datos.getcFechaGeneracionContrato() + "')\r\n" + ",fExpedienteTurnadoContrato=convert(date,'" + datos.getcFechaExpediente() + "')\r\n" + ",nProveedorDadoAlta_SAICNET=" + datos.getnProveedorDadoAlta() + "\r\n" + ",nIdProcesoContratacion=" + datos.getnProcesoContratacion() + "\r\n" + ",fAtencion=convert(date,'" + datos.getcFechaAtencion() + "')\r\n" + ",nEstatus=2" + ",cObservaciones='" + datos.getcObservaciones() + "'" + "where nIdProcedimientoSAC=" + datos.getnIdProcedimientoSAC();
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateEstatusProcedSAC(Connection conn, DatosProcedSAC datos) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "update mProcedimientoSAC \r\n" + "set nEstatus=" + datos.getnEstatus() + " where nIdProcedimientoSAC=" + datos.getnIdProcedimientoSAC();
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteAreaReq(Connection conn, int nIdProced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete mAreaRequirenteProcedSAC where nIdProcedimientoSAC=?";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, nIdProced);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteAreaResponsable(Connection conn, int nIdProced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete mAreaResponsableProcedSAC where nIdProcedimientoSAC=?";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, nIdProced);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }
}
