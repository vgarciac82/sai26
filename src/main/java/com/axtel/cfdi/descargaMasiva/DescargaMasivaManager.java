package com.axtel.cfdi.descargaMasiva;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescargaMasivaManager {

    private static final Logger log = LoggerFactory.getLogger(DescargaMasivaManager.class);

    public static int registraDescarga(Connection conn, Request request) throws Exception {
        PreparedStatement ps = null;
        int afectados = 0;
        int idParam = 1;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tRequestSAT(cUUIDRequest, dFechaInicio, dFechaFin, RFCEmisor,RFCReceptor,RFCConsulta,requestStatus)");
        query.append("VALUES(?, ?, ?, ?,?,?,?)");
        log.trace("Ejecutando: \n" + query.toString() + "\n[" + request.getIdRequest() + "][" + request.getfInicio() + "][" + request.getfFin() + "][" + request.getRfcEmisor() + "][" + request.getRfcReceptor() + "][" + request.getRfcConsulta() + "][" + request.getStatusRequest() + "]");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(idParam++, request.getIdRequest());
            ps.setDate(idParam++, Util.toSQLDate(request.getfInicio()));
            ps.setDate(idParam++, Util.toSQLDate(request.getfFin()));
            ps.setString(idParam++, request.getRfcEmisor());
            ps.setString(idParam++, request.getRfcReceptor());
            ps.setString(idParam++, request.getRfcConsulta());
            ps.setString(idParam++, request.getStatusRequest());
            afectados = ps.executeUpdate();
            log.debug("Se insertaron " + afectados + " registros en la tabla tRequestSAT");
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void bloquea(Connection conn) throws SQLException {
        StringBuilder query = new StringBuilder("");
        query.append("UPDATE tSyncDescargaFIEL ");
        query.append("   SET cCampo = cCampo+1");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static List<Request> cargaRequestVerificar(Connection conn) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	* ");
        query.append("  FROM	tRequestSAT ");
        query.append(" WHERE	requestStatus = 5000 ");
        query.append("   AND	nIDStatus = '2' ");
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Request> l = new ArrayList<Request>();
        try {
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                Request r = new Request("", "", rs.getDate("dFechaFin"), rs.getDate("dFechaFin"), null, rs.getString("RFCConsulta"), rs.getString("RFCEmisor"), rs.getString("RFCReceptor"));
                r.setIdRequest(rs.getString("cUUIDRequest"));
                l.add(r);
            }
            return l;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaDescarga(Connection conn, Request request) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE  tRequestSAT");
        queryUpdate.append("   SET  requestStatus = ?,");
        queryUpdate.append("        nTotalCFDIs = ?,");
        queryUpdate.append("        cEstatusVerificado = ?,");
        queryUpdate.append("        nIDStatus = ?,");
        queryUpdate.append("        cUUIDescarga = ?");
        queryUpdate.append(" WHERE  cUUIDRequest = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryUpdate.toString());
            ps.setString(1, request.getCodigoStatus());
            ps.setInt(2, Integer.parseInt(request.getTotalCFDI()));
            ps.setString(3, "S");
            ps.setString(4, request.getStatusRequest());
            ps.setString(5, request.getIdPaquetes());
            ps.setString(6, request.getIdRequest());
            int afectados = ps.executeUpdate();
            log.info("Actualizando la informacion del request: " + request.getIdRequest() + " se afectaron: " + afectados + " registros");
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static List<Request> cargaRequestDescargar(Connection conn) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	* ");
        query.append("  FROM	tRequestSAT ");
        query.append(" WHERE	requestStatus = 5000 ");
        query.append("   AND	nIDStatus = '3' ");
        query.append("   AND	cDescargado = 'N' ");
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Request> l = new ArrayList<Request>();
        try {
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                Request r = new Request("", "", rs.getDate("dFechaFin"), rs.getDate("dFechaFin"), null, rs.getString("RFCConsulta"), rs.getString("RFCEmisor"), rs.getString("RFCReceptor"));
                r.setIdRequest(rs.getString("cUUIDRequest"));
                r.setTotalCFDI(rs.getString("nTotalCFDIs"));
                r.setIdPaquetes(rs.getString("cUUIDescarga"));
                l.add(r);
            }
            return l;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int finalizaDescarga(Connection conn, Request request) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE  tRequestSAT");
        queryUpdate.append("   SET  cArchivoDescarga = ?,");
        queryUpdate.append("        cDescargado = 'S'");
        queryUpdate.append(" WHERE  cUUIDRequest = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryUpdate.toString());
            ps.setString(1, request.getRutaDescarga().getAbsolutePath());
            ps.setString(2, request.getIdRequest());
            int afectados = ps.executeUpdate();
            log.info("Actualizando la informacion del request: " + request.getIdRequest() + " se afectaron: " + afectados + " registros");
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static List<Request> cargaRequestDescargadas(Connection conn) throws Exception {
        return cargaRequestDescargadas(conn, null);
    }

    public static List<Request> cargaRequestDescargadas(Connection conn, String uuid) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	* ");
        query.append("  FROM	tRequestSAT ");
        query.append(" WHERE	requestStatus = 5000 ");
        query.append("   AND	nIDStatus = '3' ");
        query.append("   AND	cDescargado = 'S' ");
        if (StringUtils.isBlank(uuid))
            query.append("   AND	cArchivoProcesado IS NULL ");
        else
            query.append("   AND	cUUIDRequest = ? ");
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Request> l = new ArrayList<Request>();
        try {
            ps = conn.prepareStatement(query.toString());
            if (!StringUtils.isBlank(uuid))
                ps.setString(1, uuid);
            rs = ps.executeQuery();
            while (rs.next()) {
                Request r = new Request("", "", rs.getDate("dFechaFin"), rs.getDate("dFechaFin"), null, rs.getString("RFCConsulta"), rs.getString("RFCEmisor"), rs.getString("RFCReceptor"));
                r.setIdRequest(rs.getString("cUUIDRequest"));
                r.setTotalCFDI(rs.getString("nTotalCFDIs"));
                r.setIdPaquetes(rs.getString("cUUIDescarga"));
                r.setRutaDescarga(new File(rs.getString("cArchivoDescarga")));
                if (!StringUtils.isEmpty(rs.getString("cArchivoProcesado")))
                    r.setRutaProcesado(new File(rs.getString("cArchivoProcesado")));
                l.add(r);
            }
            return l;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaArchivoProcesado(Connection conn, Request request, File fProcesado) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE  tRequestSAT");
        queryUpdate.append("   SET  cArchivoProcesado = ?");
        queryUpdate.append(" WHERE  cUUIDRequest = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryUpdate.toString());
            ps.setString(1, fProcesado.getAbsolutePath());
            ps.setString(2, request.getIdRequest());
            int afectados = ps.executeUpdate();
            log.info("Actualizando la informacion del request: " + request.getIdRequest() + " se afectaron: " + afectados + " registros");
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
