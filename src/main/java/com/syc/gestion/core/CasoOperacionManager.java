package com.syc.gestion.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import com.syc.dbms.DBMS;
import com.syc.gestion.util.PaginaData;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasoOperacionManager {

    private static Logger log = LoggerFactory.getLogger(CasoOperacionManager.class);

    public static int deleteAll(Connection conn, int id_caso) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_caso_operacion with(rowlock) WHERE id_caso = ?");
            pstmnt.setInt(1, id_caso);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int delete(Connection conn, int id_caso, int id_caso_oper) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_caso_operacion with(rowlock) WHERE id_caso = ? AND id_caso_oper <= ?");
            pstmnt.setInt(1, id_caso);
            pstmnt.setInt(2, id_caso_oper);
            retval = pstmnt.executeUpdate();
            System.out.println("ELIMINANDO CASO: " + id_caso + ", " + id_caso_oper + ", r: " + retval);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, CasoOperacion co) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_caso_operacion " + "(id_caso, id_caso_oper, id_tc, id_oper, co_fecha_ini, co_tiempo_limite, " + "co_responsable, co_observacion, co_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmnt.setInt(1, co.getIdCaso());
            pstmnt.setInt(2, co.getIdCasoOper());
            pstmnt.setInt(3, co.getIdTC());
            pstmnt.setInt(4, co.getIdOperacion());
            pstmnt.setTimestamp(5, co.getFechaInicio());
            pstmnt.setInt(6, co.getTiempoLimite());
            pstmnt.setString(7, co.getResponsable());
            pstmnt.setString(8, co.getObservacion());
            pstmnt.setInt(9, co.getStatus());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static CasoOperacion nuevoCasoOperacion(Connection conn, String u_nombre, String observ, Caso c, Operacion o) throws SQLException {
        if (c == null)
            throw new NullPointerException("El caso no debe ser nulo");
        if (o == null)
            throw new NullPointerException("La operacion no debe ser nula");
        CasoOperacion rco = new CasoOperacion();
        rco.setIdCaso(c.getIdCaso());
        rco.setIdCasoOper(getNextIdCasoOperacion(conn, c.getIdCaso()));
        rco.setIdTC(c.getIdTC());
        rco.setIdOperacion(o.getIdOperacion());
        rco.setFechaInicio(new Timestamp(System.currentTimeMillis()));
        rco.setTiempoLimite(o.getTiempoLimite());
        rco.setResponsable(u_nombre);
        rco.setObservacion(observ);
        rco.setStatus(CasoOperacion.CREATED);
        rco.setOperacion(o);
        return rco;
    }

    public static CasoOperacion select(Connection conn, int id_caso, int id_caso_oper) throws SQLException {
        CasoOperacion rco = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_caso_operacion with(nolock) WHERE id_caso = ? AND id_caso_oper = ?");
            pstmnt.setInt(1, id_caso);
            pstmnt.setInt(2, id_caso_oper);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                rco = new CasoOperacion();
                rco.setIdCaso(rs.getInt("id_caso"));
                rco.setIdCasoOper(rs.getInt("id_caso_oper"));
                rco.setIdTC(rs.getInt("id_tc"));
                rco.setIdOperacion(rs.getInt("id_oper"));
                rco.setFechaInicio(rs.getTimestamp("co_fecha_ini"));
                rco.setTiempoLimite(rs.getInt("co_tiempo_limite"));
                rco.setResponsable(rs.getString("co_responsable"));
                rco.setObservacion(rs.getString("co_observacion"));
                rco.setStatus(rs.getInt("co_status"));
                Operacion o = new Operacion();
                o.setIdTC(rco.getIdTC());
                o.setIdOperacion(rco.getIdOperacion());
                rco.setOperacion(OperacionManager.select(conn, o));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rco;
    }

    public static Vector select(Connection conn, CasoOperacion co) throws SQLException {
        Vector v = new Vector();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (co.getIdCaso() > 0) {
                where.append(token + "id_caso = ?");
                token = " AND ";
            }
            if (co.getIdCasoOper() > 0) {
                where.append(token + "id_caso_oper = ?");
                token = " AND ";
            }
            if (co.getIdTC() > 0) {
                where.append(token + "id_tc = ?");
                token = " AND ";
            }
            if (co.getIdOperacion() > 0) {
                where.append(token + "id_oper = ?");
                token = " AND ";
            }
            if (co.getFechaInicio() != null) {
                where.append(token + "co_fecha_ini = ?");
                token = " AND ";
            }
            if (co.getTiempoLimite() > 0) {
                where.append(token + "c_tiempo_limite = ?");
                token = " AND ";
            }
            if (co.getResponsable() != null) {
                where.append(token + "co_responsable = ?");
                token = " AND ";
            }
            if (co.getObservacion() != null) {
                where.append(token + "co_observacion = ?");
                token = " AND ";
            }
            if (co.getStatus() > 0) {
                where.append(token + "co_status = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_caso_operacion with(nolock) " + where.toString());
            int i = 1;
            if (co.getIdCaso() > 0)
                pstmnt.setInt(i++, co.getIdCaso());
            if (co.getIdCasoOper() > 0)
                pstmnt.setInt(i++, co.getIdCasoOper());
            if (co.getIdTC() > 0)
                pstmnt.setInt(i++, co.getIdTC());
            if (co.getIdOperacion() > 0)
                pstmnt.setInt(i++, co.getIdOperacion());
            if (co.getFechaInicio() != null)
                pstmnt.setTimestamp(i++, co.getFechaInicio());
            if (co.getTiempoLimite() > 0)
                pstmnt.setInt(i++, co.getTiempoLimite());
            if (co.getResponsable() != null)
                pstmnt.setString(i++, co.getResponsable());
            if (co.getObservacion() != null)
                pstmnt.setString(i++, co.getObservacion());
            if (co.getStatus() > 0)
                pstmnt.setInt(i++, co.getStatus());
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CasoOperacion lco = new CasoOperacion();
                lco.setIdCaso(rs.getInt("id_caso"));
                lco.setIdCasoOper(rs.getInt("id_caso_oper"));
                lco.setIdTC(rs.getInt("id_tc"));
                lco.setIdOperacion(rs.getInt("id_oper"));
                lco.setFechaInicio(rs.getTimestamp("co_fecha_ini"));
                lco.setTiempoLimite(rs.getInt("co_tiempo_limite"));
                lco.setResponsable(rs.getString("co_responsable"));
                lco.setObservacion(rs.getString("co_observacion"));
                lco.setStatus(rs.getInt("co_status"));
                Operacion o = new Operacion();
                o.setIdTC(lco.getIdTC());
                o.setIdOperacion(lco.getIdOperacion());
                lco.setOperacion(OperacionManager.select(conn, o));
                v.add(lco);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return v;
    }

    public static Map consultaCasoOperacion(Connection conn, String titulo_aplicacion, int id_gabinete) throws SQLException {
        Map m = new Hashtable();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT co.id_caso, co.id_caso_oper " + "FROM cg_tipo_caso tc with(nolock) , cg_caso c with(nolock) , cg_caso_operacion co with(nolock) , cg_operacion o with(nolock) " + "WHERE tc.id_tc = co.id_tc AND c.id_caso = co.id_caso AND o.id_tc = co.id_tc " + "AND o.id_oper = co.id_oper AND tc.tc_gaveta_asociada = ? AND c.c_id_gabinete = ? " + "GROUP BY co.id_caso, co.id_caso_oper");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                m.put("id_caso", new Integer(rs.getInt("id_caso")));
                m.put("id_caso_oper", new Integer(rs.getInt("id_caso_oper")));
            }
        } finally {
            if (rs == null)
                rs.close();
            if (pstmnt == null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return m;
    }

    public static Map selectCasoOperacion(Connection conn, String u_login, String titulo_aplicacion, int id_gabinete) throws SQLException {
        Map m = new Hashtable();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT co.id_caso, co.id_caso_oper " + "FROM cg_tipo_caso tc with(nolock) , cg_caso c with(nolock) , cg_caso_operacion co with(nolock) , cg_operacion o with(nolock) , " + "cg_usuario u with(nolock) , cg_usuario_grupo ug with(nolock) " + "WHERE tc.id_tc = co.id_tc AND c.id_caso = co.id_caso AND o.id_tc = co.id_tc " + "AND o.id_oper = co.id_oper AND (u.u_nombre = co.co_responsable OR " + "ug.g_nombre = co_responsable) " + "AND ug.u_login = u.u_login AND u.u_login = ? AND tc.tc_gaveta_asociada = ? " + "AND c.c_id_gabinete = ? GROUP BY co.id_caso, co.id_caso_oper");
            pstmnt.setString(1, u_login);
            pstmnt.setString(2, titulo_aplicacion);
            pstmnt.setInt(3, id_gabinete);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                m.put("id_caso", new Integer(rs.getInt("id_caso")));
                m.put("id_caso_oper", new Integer(rs.getInt("id_caso_oper")));
            }
        } finally {
            if (rs == null)
                rs.close();
            if (pstmnt == null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return m;
    }

    public static List<Caso> selectInbox(Connection conn, String u_login, String orderBy, String UR, String cCentroContable) throws SQLException, GestionException {
        List<Caso> l = new ArrayList<Caso>();
        CallableStatement cstmnt = null;
        ResultSet rs = null;
        String qry = " {CALL dbo.sp_inbox (?,?,?)}";
        try {
            Usuario u = new Usuario();
            u.setLogin(u_login);
            u = UsuarioManager.select(conn, u);
            log.debug(qry + "[" + u.getNombre() + ", " + cCentroContable + ", " + UR + "]");
            cstmnt = conn.prepareCall(qry);
            cstmnt.setString(1, u.getNombre());
            cstmnt.setString(2, cCentroContable);
            cstmnt.setString(3, UR);
            rs = cstmnt.executeQuery();
            while (rs.next()) {
                Caso c = new Caso();
                c.setFolio(rs.getString("c_folio"));
                c.setFechaInicio(rs.getTimestamp("c_fecha_ini"));
                TipoCaso tc = new TipoCaso();
                tc.setDescripcion(rs.getString("tc_descripcion"));
                c.setTipoCaso(tc);
                Operacion o = new Operacion();
                o.setIdTC(rs.getInt("id_tc"));
                o.setIdOperacion(rs.getInt("id_oper"));
                o.setNumero(rs.getInt("o_numero"));
                o.setNombre(rs.getString("o_nombre"));
                o.setResponsable(rs.getString("o_responsable"));
                o.setDescripcion(rs.getString("o_descripcion"));
                o.setPlantilla(rs.getString("o_plantilla"));
                o.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                o.setAlarma(rs.getString("o_alarma"));
                o.setPostDisplay(rs.getString("o_post_display"));
                o.setPostSubmit(rs.getString("o_post_submit"));
                o.setOnLoad(rs.getString("o_on_load"));
                o.setOnSubmit(rs.getString("o_on_submit"));
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(rs.getInt("id_caso"));
                co.setIdCasoOper(rs.getInt("id_caso_oper"));
                co.setFechaInicio(rs.getTimestamp("co_fecha_ini"));
                co.setTiempoLimite(rs.getInt("co_tiempo_limite"));
                co.setResponsable(rs.getString("co_responsable"));
                co.setObservacion(rs.getString("co_observacion"));
                co.setStatus(rs.getInt("co_status"));
                co.setImporte(rs.getString("mImporte"));
                co.setStatusC(rs.getString("ESTATUS"));
                co.setFolioSicop(rs.getString("FolioSICOP"));
                co.setFolioMap(rs.getString("FolioMAP"));
                co.setTipoAdecuacion(rs.getString("TipoAdecuacion"));
                co.setNivelAdecuacion(rs.getString("NivelAdecuacion"));
                co.setDocumento(rs.getString("documento"));
                co.setfechaAppCont(rs.getString("fechaAppCont"));
                co.setOperador(rs.getString("operador"));
                co.setOperacion(o);
                co.setFirmaElectrionica(rs.getString("firmaElectronica"));
                c.setCasoOperacion(co);
                l.add(c);
            }
            return l;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(cstmnt);
        }
    }

    public static List<CasoOperacion> selectCasoOperacionFiltros(Connection conn, String u_login, String orderBy, String UR, String cCentroContable, HttpServletRequest request) throws SQLException, UnsupportedEncodingException {
        String WhereFiltros = "";
        String SelectPaginado = " select * from ( select ROW_NUMBER () OVER (ORDER BY tbl.o_descripcion DESC, tbl.co_fecha_ini ) as rowNum, tbl.* from ( ";
        String WherePaginado = " ) as tbl ) as inbox ";
        String WhereRegistros = "  ORDER BY inbox.rowNum ";
        String fSAI = request.getParameter("fSAI");
        if (fSAI != null && !"".equals(fSAI))
            WhereFiltros += " AND C_FOLIO like '%" + new String(fSAI.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        String PolizaDocumento = request.getParameter("PolizaDocumento");
        String Fecha = request.getParameter("Fecha");
        String Tramite = request.getParameter("Tramite");
        String Operacion = request.getParameter("Operacion");
        String Aplicacion = request.getParameter("Aplicacion");
        String Importe = request.getParameter("Importe");
        String SICOP = request.getParameter("SICOP");
        String MAP = request.getParameter("MAP");
        String Operador = request.getParameter("Operador");
        String FirmaElectronica = request.getParameter("FirmaElectronica");
        if (PolizaDocumento != null && !"".equals(PolizaDocumento) && !"INTEGRADOR_ADECUACIONES".equals(u_login))
            WhereFiltros += " AND documento like '%" + new String(PolizaDocumento.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (Fecha != null && !"".equals(Fecha))
            WhereFiltros += " AND FECHA_DOCUMENTO like '%" + new String(Fecha.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (Tramite != null && !"".equals(Tramite))
            WhereFiltros += " AND TC_DESCRIPCION like '%" + new String(Tramite.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (Operacion != null && !"".equals(Operacion))
            WhereFiltros += " AND O_DESCRIPCION like '%" + new String(Operacion.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (Aplicacion != "")
            WhereFiltros += " AND fechaAppCont like '%" + new String(Aplicacion.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (Importe != null && !"".equals(Importe))
            WhereFiltros += " AND mImporte like '%" + new String(Importe.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (SICOP != null && !"".equals(SICOP))
            WhereFiltros += " AND FolioSICOP like '%" + new String(SICOP.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (MAP != null && !"".equals(MAP))
            WhereFiltros += " AND FolioMAP like '%" + new String(MAP.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (Operador != null && !"".equals(Operador))
            WhereFiltros += " AND OPERADOR like '%" + new String(Operador.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        if (FirmaElectronica != null && !"".equals(FirmaElectronica))
            WhereFiltros += " AND FirmaElectronica like '%" + new String(FirmaElectronica.trim().getBytes("ISO-8859-1"), "UTF-8") + "%'";
        List<CasoOperacion> l = new ArrayList<CasoOperacion>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String qry = "";
        try {
            Usuario u = new Usuario();
            u.setLogin(u_login);
            u = UsuarioManager.select(conn, u);
            // ADECUACIONES
            if (u.getGrupo("INTEGRA_ADECUACIONES") != null) {
                qry = "SELECT * FROM (SELECT (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 2) AS FECHA_DOCUMENTO, (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 4) AS operador, ISNULL(operacion.co_observacion,'') AS co_observacion, co_status, co_tiempo_limite, co_fecha_ini, co_responsable, id_caso, id_caso_oper, id_oper, id_tc, o_numero, o_nombre, o_responsable, o_descripcion, o_plantilla, o_tiempo_limite, o_alarma, o_post_display, o_post_submit, o_on_load, o_on_submit, tc_descripcion, c_folio, " + "					dbo.Fn_montooperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS mImporte, " + "	                dbo.Fn_statusoperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS ESTATUS, " + "	                dbo.Fn_foliosicop(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioSICOP, " + "	                dbo.Fn_foliomap(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioMAP, " + "	                dbo.Fn_tipoadecuacion(Substring(operacion.c_folio, 10, 10)) AS TipoAdecuacion, " + "	                dbo.Fn_niveladecuacion(Substring(operacion.c_folio, 10, 10)) AS NivelAdecuacion, " + "	                dbo.Fn_documento(operacion.id_tc, Substring(operacion.c_folio, 10, 10), operacion.id_caso) AS documento, " + "	                dbo.Fn_fechaappcont(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS fechaAppCont, " + "					firmaElectronica " + "	FROM   voperacionesinbox AS operacion WITH(nolock) " + "	WHERE  1 = 1 AND co_responsable = '" + u.getNombre() + "'" + "	       OR ((operacion.c_unidad_folio IN (SELECT DISTINCT ur " + "	                                            FROM   dbo.tvistasur WITH(nolock) " + "	                                            WHERE  usuario = '" + u_login + "' )) " + "		   AND co_responsable IN (SELECT g_nombre FROM dbo.CG_USUARIO_GRUPO WITH(NOLOCK) WHERE U_LOGIN = '" + u_login + "') " + "			)) AS TRAMITES " + "			WHERE TRAMITES.c_unidad_folio = (SELECT cUnidadResponsable " + "											FROM dbo.CG_CAT_EMPLEADO AS emp WITH (NOLOCK) " + "											JOIN dbo.tCatalogoUnidadResponsable AS uni WITH (NOLOCK) ON emp.ID_AREA = uni.ID_AREA " + "											WHERE CE_OS_RESPONSABLE = '" + u_login + "') " + "			OR (id_tc IN (3) AND id_oper IN (2)) " + "			OR (id_tc IN (22) AND id_oper IN (1,2,3))";
                // VENTANILLA
            } else if (u.getRole("JEFATURA_PAGOS") != null || u.getRole("ADMIN_REINTEGROS") != null) {
                qry = "SELECT * FROM (SELECT (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 2) AS FECHA_DOCUMENTO, (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 4) AS operador, ISNULL(operacion.co_observacion,'') AS co_observacion, co_status, co_tiempo_limite, co_fecha_ini, co_responsable, id_caso, id_caso_oper, id_oper, id_tc, o_numero, o_nombre, o_responsable, o_descripcion, o_plantilla, o_tiempo_limite, o_alarma, o_post_display, o_post_submit, o_on_load, o_on_submit, tc_descripcion, c_folio, " + "					dbo.Fn_montooperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS mImporte, " + "	                dbo.Fn_statusoperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS ESTATUS, " + "	                dbo.Fn_foliosicop(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioSICOP, " + "	                dbo.Fn_foliomap(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioMAP, " + "	                dbo.Fn_tipoadecuacion(Substring(operacion.c_folio, 10, 10)) AS TipoAdecuacion, " + "	                dbo.Fn_niveladecuacion(Substring(operacion.c_folio, 10, 10)) AS NivelAdecuacion, " + "	                dbo.Fn_documento(operacion.id_tc, Substring(operacion.c_folio, 10, 10), operacion.id_caso) AS documento, " + "	                dbo.Fn_fechaappcont(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS fechaAppCont, " + "					firmaElectronica " + "	FROM   voperacionesinbox AS operacion WITH(nolock) " + "	WHERE  1 = 1 AND co_responsable = '" + u.getNombre() + "'" + "       		OR(( operacion.centrocontable LIKE CASE WHEN " + cCentroContable + " = '00' THEN '%' ELSE " + cCentroContable + " END " + "					OR ( operacion.id_tc = 42 AND operacion.id_oper = 2 )) " + "			AND co_responsable IN (SELECT g_nombre FROM dbo.CG_USUARIO_GRUPO WITH(NOLOCK) WHERE U_LOGIN = '" + u_login + "' ) " + "			)) AS TRAMITES " + "		WHERE 1=1 " + "			OR (id_tc IN (4,5,6,11,13,21,24,42,15,48,50,51,52,43) AND id_oper IN(1,2,5)) " + "			OR (id_tc IN (15) AND id_oper IN(3,4,6)) " + "			OR (id_tc IN (17) AND id_oper IN(4,6)) " + "			OR (id_tc IN (49) AND id_oper IN (1,2,3,4,6)) " + "			OR (id_tc IN (60) AND id_oper IN (4)) " + "			OR (id_tc IN (59) AND id_oper IN (2,3) )";
                // CONTABILIDAD
            } else if (u.getRole("ADMIN_CONTABILIDAD") != null || u.getRole("ADMIN_REINTEGROS") != null) {
                if ("10".equals(cCentroContable) || "00".equals(cCentroContable)) {
                    qry = "SELECT * FROM (SELECT (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 2) AS FECHA_DOCUMENTO, (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 4) AS operador, ISNULL(operacion.co_observacion,'') AS co_observacion, co_status, co_tiempo_limite, co_fecha_ini, co_responsable, id_caso, id_caso_oper, id_oper, id_tc, o_numero, o_nombre, o_responsable, o_descripcion, o_plantilla, o_tiempo_limite, o_alarma, o_post_display, o_post_submit, o_on_load, o_on_submit, tc_descripcion, c_folio, " + "					dbo.Fn_montooperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS mImporte, " + "                	dbo.Fn_statusoperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS ESTATUS, " + "                	dbo.Fn_foliosicop(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioSICOP, " + "                	dbo.Fn_foliomap(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioMAP, " + "                	dbo.Fn_tipoadecuacion(Substring(operacion.c_folio, 10, 10)) AS TipoAdecuacion, " + "                	dbo.Fn_niveladecuacion(Substring(operacion.c_folio, 10, 10)) AS NivelAdecuacion, " + "                	dbo.Fn_documento(operacion.id_tc, Substring(operacion.c_folio, 10, 10), operacion.id_caso) AS documento, " + "                	dbo.Fn_fechaappcont(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS fechaAppCont " + "					firmaElectronica " + "	FROM   voperacionesinbox AS operacion WITH(nolock) " + "	WHERE  1 = 1 AND co_responsable = '" + u.getNombre() + "'" + "		    OR co_responsable IN (SELECT g_nombre FROM dbo.CG_USUARIO_GRUPO WITH(NOLOCK) WHERE U_LOGIN = '" + u_login + "' ) " + "		) AS TRAMITE " + "		WHERE centrocontable = ( CASE when id_tc = 13 AND id_oper = 2 THEN centrocontable " + "									ELSE (SELECT UP_VALOR FROM dbo.CG_USUARIO_PROPIEDADES " + "										WHERE UP_NOMBRE = 'CCENTROCONTABLE' " + "											AND U_LOGIN = '" + u_login + "' ) END ) " + "			OR (id_tc IN (49) AND id_oper IN (3,4,6))";
                } else {
                    qry = "SELECT (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 2) AS FECHA_DOCUMENTO, (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 4) AS operador, ISNULL(operacion.co_observacion,'') AS co_observacion, co_status, co_tiempo_limite, co_fecha_ini, co_responsable, id_caso, id_caso_oper, id_oper, id_tc, o_numero, o_nombre, o_responsable, o_descripcion, o_plantilla, o_tiempo_limite, o_alarma, o_post_display, o_post_submit, o_on_load, o_on_submit, tc_descripcion, c_folio, " + "			  dbo.Fn_montooperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS mImporte, " + "              dbo.Fn_statusoperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS ESTATUS, " + "              dbo.Fn_foliosicop(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioSICOP, " + "              dbo.Fn_foliomap(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioMAP, " + "              dbo.Fn_tipoadecuacion(Substring(operacion.c_folio, 10, 10)) AS TipoAdecuacion, " + "              dbo.Fn_niveladecuacion(Substring(operacion.c_folio, 10, 10)) AS NivelAdecuacion, " + "              dbo.Fn_documento(operacion.id_tc, Substring(operacion.c_folio, 10, 10), operacion.id_caso) AS documento, " + "              dbo.Fn_fechaappcont(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS fechaAppCont, " + "			  firmaElectronica " + "	FROM   voperacionesinbox AS operacion WITH(nolock) " + "	WHERE  1 = 1 " + "		AND centrocontable = '" + cCentroContable + "'";
                }
                // GENERAL
            } else {
                qry = "SELECT (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 2) AS FECHA_DOCUMENTO, (SELECT cd_valor FROM cg_caso_dato WHERE id_caso = operacion.id_caso AND id_cd = 4) AS operador, ISNULL(operacion.co_observacion,'') AS co_observacion, co_status, co_tiempo_limite, co_fecha_ini, co_responsable, id_caso, id_caso_oper, id_oper, id_tc, o_numero, o_nombre, o_responsable, o_descripcion, o_plantilla, o_tiempo_limite, o_alarma, o_post_display, o_post_submit, o_on_load, o_on_submit, tc_descripcion, c_folio, " + "				dbo.Fn_montooperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS mImporte, " + "	            dbo.Fn_statusoperacion(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS ESTATUS, " + "	            dbo.Fn_foliosicop(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioSICOP, " + "	            dbo.Fn_foliomap(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS FolioMAP, " + "	            dbo.Fn_tipoadecuacion(Substring(operacion.c_folio, 10, 10)) AS TipoAdecuacion, " + "	            dbo.Fn_niveladecuacion(Substring(operacion.c_folio, 10, 10)) AS NivelAdecuacion, " + "	            dbo.Fn_documento(operacion.id_tc, Substring(operacion.c_folio, 10, 10), operacion.id_caso) AS documento, " + "	            dbo.Fn_fechaappcont(operacion.id_tc, Substring(operacion.c_folio, 10, 10)) AS fechaAppCont, " + "			    firmaElectronica " + "	FROM   voperacionesinbox AS operacion WITH(nolock) " + "	WHERE  1 = 1 AND co_responsable = '" + u.getNombre() + "'" + "	       OR  ( ( ( operacion.c_unidad_folio IN (SELECT DISTINCT ur  FROM   dbo.tvistasur WITH(nolock)  WHERE  usuario = '" + u_login + "' ) ) " + "					AND	co_responsable IN (SELECT g_nombre FROM dbo.CG_USUARIO_GRUPO WITH(NOLOCK) WHERE U_LOGIN = '" + u_login + "' ) ) " + "				OR ( co_responsable IN (SELECT g_nombre FROM dbo.CG_USUARIO_GRUPO WITH(NOLOCK) WHERE U_LOGIN = '" + u_login + "' ) " + "					AND id_tc IN (49,6) AND id_oper IN (3,6,4) ) )";
            }
            String query = SelectPaginado + " " + qry + " " + WherePaginado + " " + " WHERE 1=1 " + WhereFiltros + WhereRegistros;
            log.info("Query Carga Inbox:" + query);
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Operacion o = new Operacion();
                o.setIdTC(rs.getInt("id_tc"));
                o.setIdOperacion(rs.getInt("id_oper"));
                o.setNumero(rs.getInt("o_numero"));
                o.setNombre(rs.getString("o_nombre"));
                o.setResponsable(rs.getString("o_responsable"));
                o.setDescripcion(rs.getString("o_descripcion"));
                o.setPlantilla(rs.getString("o_plantilla"));
                o.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                o.setAlarma(rs.getString("o_alarma"));
                o.setPostDisplay(rs.getString("o_post_display"));
                o.setPostSubmit(rs.getString("o_post_submit"));
                o.setOnLoad(rs.getString("o_on_load"));
                o.setOnSubmit(rs.getString("o_on_submit"));
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(rs.getInt("id_caso"));
                co.setIdCasoOper(rs.getInt("id_caso_oper"));
                co.setFechaInicio(rs.getTimestamp("co_fecha_ini"));
                co.setTiempoLimite(rs.getInt("co_tiempo_limite"));
                co.setResponsable(rs.getString("operador"));
                co.setObservacion(rs.getString("co_observacion"));
                co.setStatus(rs.getInt("co_status"));
                co.setImporte(rs.getString("mImporte"));
                co.setStatusC(rs.getString("ESTATUS"));
                co.setFolioSicop(rs.getString("FolioSICOP"));
                co.setFolioMap(rs.getString("FolioMAP"));
                co.setTipoAdecuacion(rs.getString("TipoAdecuacion"));
                co.setNivelAdecuacion(rs.getString("NivelAdecuacion"));
                co.setDocumento(rs.getString("documento"));
                co.setfechaAppCont(rs.getString("fechaAppCont"));
                co.setOperacion(o);
                co.setFirmaElectrionica(rs.getString("firmaElectronica"));
                l.add(co);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return l;
    }

    public static List<CasoOperacion> selectCasoConsulta(Connection conn, String gavetaAsociada, String noFolio, String fDesde, String fHasta, String documento, String operador, String estatus, String importe, String folioSICOP, String folioMAP, String folioCAL, Usuario u) throws SQLException {
        List<CasoOperacion> l = new ArrayList<CasoOperacion>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String queryConsulta = "";
        try {
            if ("ADECUACION".equals(gavetaAsociada)) {
                queryConsulta = "SELECT * FROM (SELECT DISTINCT co.*,o.o_numero,o.o_nombre,o.o_responsable,o.o_descripcion,o.o_plantilla,o.o_tiempo_limite,o.o_alarma,o.o_post_display,o.o_post_submit,o.o_on_load,o.o_on_submit, " + " dbo.fn_MontoOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS mImporte, " + " dbo.fn_StatusOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS ESTATUS, " + " dbo.fn_FolioSICOP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioSICOP, " + " dbo.fn_FolioMAP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioMAP,   " + " dbo.fn_FolioCAL(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioCAL,   " + " dbo.fn_TipoAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) AS TipoAdecuacion,	" + " dbo.fn_NivelAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) AS NivelAdecuacion, " + " dbo.fn_documento(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10),ca.ID_CASO) AS documento, dbo.Fn_fechaappcont(o.id_tc, Substring(ca.C_FOLIO, 10, 10)) AS fechaAppCont " + " FROM cg_caso_operacion co with(nolock) , cg_operacion o with(nolock) , dbo.CG_CASO ca with(nolock) , dbo.CG_TIPO_CASO AS t with(nolock) " + (!"".equals(operador) && operador != null ? ",dbo.cg_caso_dato cd with(nolock)" : "") + " WHERE o.id_tc = co.id_tc  " + (!"".equals(operador) && operador != null ? "AND cd.ID_CASO = ca.ID_CASO AND cd.ID_TC = ca.ID_TC AND cd.ID_CASO = co.ID_CASO AND cd.ID_TC = co.ID_TC AND cd.ID_CD = 4 AND cd.CD_VALOR like '%" + operador + "%'" : "") + "	AND o.id_oper = co.id_oper " + "	AND co.ID_TC = ca.ID_TC " + "	AND co.ID_TC = t.ID_TC " + "	AND co.ID_CASO = ca.ID_CASO " + "	AND t.TC_GAVETA_ASOCIADA = '" + gavetaAsociada + "'" + (u.getPropiedad("CONSULTA_NACIONAL") != null && "SI".equals(u.getPropiedad("CONSULTA_NACIONAL").getValor()) ? "" : " AND (ca.C_FOLIO LIKE '%" + u.getU_UR() + "%' or ca.C_FOLIO LIKE '%-C" + u.getPropiedad("CCENTROCONTABLE").getValor() + "%' OR dbo.fn_ContratoDesCentralizado(SUBSTRING(ca.C_FOLIO,10, 20),'" + gavetaAsociada + "', '" + u.getU_UR() + "') > 0 OR ca.ID_CASO in(	select ca.ID_CASO from tAdecuacionEncabezado enc,tConsolidacionDetalle cod,(select * from CG_CASO where ID_TC=22) ca where enc.cUnidadResponsable='" + u.getU_UR() + "' and cod.nFolioAdecuacion=enc.nFolioAdecuacion and substring(ca.C_FOLIO,10,20) = convert(varchar,cod.nFolioCONSOLIDACION)))") + (!"".equals(noFolio) && noFolio != null ? "AND ca.C_FOLIO LIKE '%" + noFolio + "%'" : "") + " ) tabla WHERE 1=1 " + (!"".equals(documento) && documento != null ? " AND tabla.documento like '%" + documento + "%'" : "") + (!"".equals(importe) && importe != null ? " AND tabla.mImporte like '%" + importe + "%'" : "") + (!"".equals(estatus) && estatus != null ? " AND tabla.estatus like '%" + estatus + "%'" : "") + (!"".equals(folioSICOP) && folioSICOP != null ? " AND tabla.folioSICOP like '%" + folioSICOP + "%'" : "") + (!"".equals(folioMAP) && folioMAP != null ? " AND tabla.folioMap like '%" + folioMAP + "%'" : "") + (!"".equals(folioCAL) && folioCAL != null ? " AND tabla.folioCAL like '%" + folioCAL + "%'" : "") + (!"".equals(fDesde) && fDesde != null ? "AND CONVERT (DATE,tabla.CO_FECHA_INI, 103) BETWEEN CONVERT (DATE,'" + fDesde + "', 103) AND CONVERT (DATE,'" + fHasta + "',103)" : "");
            } else if ("REINTEGRO".equals(gavetaAsociada)) {
                queryConsulta = "SELECT * FROM (SELECT DISTINCT co.*,o.o_numero,o.o_nombre,o.o_responsable,o.o_descripcion,o.o_plantilla,o.o_tiempo_limite,o.o_alarma,o.o_post_display," + " o.o_post_submit,o.o_on_load,o.o_on_submit,  dbo.fn_MontoOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS mImporte,  " + " dbo.fn_StatusOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS ESTATUS,  " + " dbo.fn_FolioSICOP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioSICOP,  dbo.fn_FolioMAP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioMAP, dbo.fn_FolioCAL(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioCAL,   " + " dbo.fn_TipoAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) AS TipoAdecuacion,	 dbo.fn_NivelAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) AS NivelAdecuacion,  " + " dbo.fn_documento(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10),ca.ID_CASO) AS documento, dbo.Fn_fechaappcont(o.id_tc, Substring(ca.C_FOLIO, 10, 10)) AS fechaAppCont   " + " FROM cg_caso_operacion co with(nolock) , cg_operacion o with(nolock) , dbo.CG_CASO ca with(nolock) , dbo.CG_TIPO_CASO AS t with(nolock)  " + " WHERE o.id_tc = co.id_tc  	AND o.id_oper = co.id_oper 	AND co.ID_TC = ca.ID_TC 	AND co.ID_TC = t.ID_TC 	AND co.ID_CASO = ca.ID_CASO 	" + " AND (co.CO_RESPONSABLE LIKE 'CONSULTA_%') and 	t.TC_GAVETA_ASOCIADA = 'REINTEGRO' " + (!"".equals(noFolio) && noFolio != null ? " AND ca.C_FOLIO LIKE '%" + noFolio + "%'" : "") + " ) tabla WHERE 1=1" + (!"".equals(documento) && documento != null ? " AND tabla.documento like '%" + documento + "%'" : "") + (!"".equals(importe) && importe != null ? " AND tabla.mImporte like '%" + importe + "%'" : "") + (!"".equals(estatus) && estatus != null ? " AND tabla.estatus like '%" + estatus + "%'" : "") + (!"".equals(folioSICOP) && folioSICOP != null ? " AND tabla.folioSICOP like '%" + folioSICOP + "%'" : "") + (!"".equals(folioMAP) && folioMAP != null ? " AND tabla.folioMap like '%" + folioMAP + "%'" : "") + (!"".equals(folioCAL) && folioCAL != null ? " AND tabla.folioCAL like '%" + folioCAL + "%'" : "") + (!"".equals(fDesde) && fDesde != null ? "AND CONVERT (DATE,tabla.CO_FECHA_INI, 103) BETWEEN CONVERT (DATE,'" + fDesde + "', 103) AND CONVERT (DATE,'" + fHasta + "',103)" : "");
            } else if ("PROVEEDORES".equals(gavetaAsociada)) {
                queryConsulta = "SELECT * FROM (SELECT DISTINCT co.*,o.o_numero,o.o_nombre,o.o_responsable,o.o_descripcion,o.o_plantilla,o.o_tiempo_limite,o.o_alarma,o.o_post_display,o.o_post_submit,o.o_on_load,o.o_on_submit, " + " dbo.fn_MontoOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS mImporte, " + " dbo.fn_StatusOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS ESTATUS, " + " dbo.fn_FolioSICOP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioSICOP, " + " dbo.fn_FolioMAP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioMAP,   " + " dbo.fn_FolioCAL(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) AS FolioCAL, " + " dbo.fn_TipoAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) AS TipoAdecuacion,	" + " dbo.fn_NivelAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) AS NivelAdecuacion, " + " dbo.fn_documento(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10),ca.ID_CASO) AS documento, dbo.Fn_fechaappcont(o.id_tc, Substring(ca.C_FOLIO, 10, 10)) AS fechaAppCont  " + " FROM cg_caso_operacion co with(nolock) , cg_operacion o with(nolock) , dbo.CG_CASO ca with(nolock) , dbo.CG_TIPO_CASO AS t with(nolock) " + (!"".equals(operador) && operador != null ? ",dbo.cg_caso_dato cd with(nolock)" : "") + " WHERE o.id_tc = co.id_tc  " + (!"".equals(operador) && operador != null ? "AND cd.ID_CASO = ca.ID_CASO AND cd.ID_TC = ca.ID_TC AND cd.ID_CASO = co.ID_CASO AND cd.ID_TC = co.ID_TC AND cd.ID_CD = 4 AND cd.CD_VALOR like '%" + operador + "%'" : "") + "	AND o.id_oper = co.id_oper " + "	AND co.ID_TC = ca.ID_TC " + "	AND co.ID_TC = t.ID_TC " + "	AND co.ID_CASO = ca.ID_CASO " + "	AND (co.CO_RESPONSABLE LIKE 'CONSULTA_%') and " + "	t.TC_GAVETA_ASOCIADA = '" + gavetaAsociada + "' " + (!"".equals(noFolio) && noFolio != null ? " AND ca.C_FOLIO LIKE '%" + noFolio + "%'" : "") + " ) tabla WHERE 1=1 " + (!"".equals(documento) && documento != null ? " AND tabla.documento like replace ('%" + documento + "%','-','') " : "") + " AND tabla.estatus like '%APLICADA%'" + (!"".equals(fDesde) && fDesde != null ? "AND CONVERT (DATE,tabla.CO_FECHA_INI, 103) BETWEEN CONVERT (DATE,'" + fDesde + "', 103) AND CONVERT (DATE,'" + fHasta + "',103)" : "");
            } else {
                queryConsulta = "SELECT * FROM (SELECT DISTINCT co.*,o.o_numero, o.o_nombre,o.o_responsable, o.o_descripcion, '5' o_plantilla,'6' o_tiempo_limite,'7' o_alarma, '8' o_post_display,o.o_post_submit,o.o_on_load,o.o_on_submit, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_MontoOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_MontoOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 11, 10)) END AS mImporte, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_beneficiarioTramite(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_beneficiarioTramite(co.ID_TC, SUBSTRING(ca.C_FOLIO, 11, 10)) END AS beneficiario, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_StatusOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_StatusOperacion(co.ID_TC, SUBSTRING(ca.C_FOLIO, 11, 10)) END AS ESTATUS, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_FolioSICOP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_FolioSICOP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 11, 10)) END AS FolioSICOP, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_FolioMAP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_FolioMAP(co.ID_TC, SUBSTRING(ca.C_FOLIO, 11, 10)) END AS FolioMAP, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_FolioCAL(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_FolioCAL(co.ID_TC, SUBSTRING(ca.C_FOLIO, 11, 10)) END AS FolioCAL, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_TipoAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_TipoAdecuacion(SUBSTRING(ca.C_FOLIO, 11, 10)) END AS TipoAdecuacion,	" + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_NivelAdecuacion(SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.fn_NivelAdecuacion(SUBSTRING(ca.C_FOLIO, 11, 10)) END AS NivelAdecuacion, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.fn_documento(co.ID_TC, SUBSTRING(ca.C_FOLIO, 10, 10), ca.ID_CASO) ELSE dbo.fn_documento(co.ID_TC, SUBSTRING(ca.C_FOLIO, 11, 10), ca.ID_CASO) END AS documento, " + " CASE WHEN SUBSTRING(ca.C_FOLIO,9,1) = '-' THEN dbo.Fn_fechaappcont(o.id_tc, SUBSTRING(ca.C_FOLIO, 10, 10)) ELSE dbo.Fn_fechaappcont(o.id_tc, SUBSTRING(ca.C_FOLIO, 11, 10)) END AS fechaAppCont  " + " FROM cg_caso_operacion co with(nolock) , cg_operacion o with(nolock) , dbo.CG_CASO ca with(nolock) , " + " dbo.CG_TIPO_CASO AS t with(nolock) " + (!"".equals(operador) && operador != null ? ",dbo.cg_caso_dato cd with(nolock)" : "") + " WHERE o.id_tc = co.id_tc  " + (!"".equals(operador) && operador != null ? "AND cd.ID_CASO = ca.ID_CASO AND cd.ID_TC = ca.ID_TC AND cd.ID_CASO = co.ID_CASO AND cd.ID_TC = co.ID_TC AND cd.ID_CD = 4 AND cd.CD_VALOR like '%" + operador + "%'" : "") + "	AND o.id_oper = co.id_oper " + "	AND co.ID_TC = ca.ID_TC " + "	AND co.ID_TC = t.ID_TC " + "	AND co.ID_CASO = ca.ID_CASO " + "	AND (co.CO_RESPONSABLE LIKE 'CONSULTA_%') " + " and t.TC_GAVETA_ASOCIADA = '" + gavetaAsociada + "'" + (u.getPropiedad("CONSULTA_NACIONAL") != null && "SI".equals(u.getPropiedad("CONSULTA_NACIONAL").getValor()) ? "" : " AND (SUBSTRING(ca.c_folio,6,3) IN (SELECT ur FROM tVistasUR WITH(NOLOCK) WHERE usuario = '" + u.getLogin() + "') or ca.C_FOLIO LIKE '%-C" + u.getPropiedad("CCENTROCONTABLE").getValor() + "%' OR dbo.fn_ContratoDesCentralizado(SUBSTRING(ca.C_FOLIO,10, 20),'" + gavetaAsociada + "', '" + u.getU_UR() + "') > 0 or ( ('A02' = '" + u.getU_UR() + "' and substring(ca.C_FOLIO, 6, 3) <= 'B15') and o.id_tc IN(4, 11, 14, 23, 25,15,17,26,27) ) and co.id_tc not in(40) OR ca.ID_CASO in(	select ca.ID_CASO from tAdecuacionEncabezado enc,tConsolidacionDetalle cod,(select * from CG_CASO where ID_TC=22) ca where enc.cUnidadResponsable='" + u.getU_UR() + "' and cod.nFolioAdecuacion=enc.nFolioAdecuacion and substring(ca.C_FOLIO,10,20) = convert(varchar,cod.nFolioCONSOLIDACION)))") + (!"".equals(noFolio) && noFolio != null ? "AND ca.C_FOLIO LIKE '%" + noFolio + "%'" : "") + " ) tabla WHERE 1=1 " + (!"".equals(documento) && documento != null ? " AND tabla.documento like '%" + documento + "%'" : "") + (!"".equals(importe) && importe != null ? " AND tabla.mImporte like '%" + importe + "%'" : "") + (!"".equals(estatus) && estatus != null ? " AND tabla.estatus like '%" + estatus + "%'" : "") + (!"".equals(folioSICOP) && folioSICOP != null ? " AND tabla.folioSICOP like '%" + folioSICOP + "%'" : "") + (!"".equals(folioMAP) && folioMAP != null ? " AND tabla.folioMap like '%" + folioMAP + "%'" : "") + (!"".equals(folioCAL) && folioCAL != null ? " AND tabla.folioCAL like '%" + folioCAL + "%'" : "") + (!"".equals(fDesde) && fDesde != null ? "AND CONVERT (DATE,tabla.CO_FECHA_INI, 103) BETWEEN CONVERT (DATE,'" + fDesde + "', 103) AND CONVERT (DATE,'" + fHasta + "',103)" : "");
            }
            pstmnt = conn.prepareStatement(queryConsulta);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Operacion o = new Operacion();
                o.setIdTC(rs.getInt("id_tc"));
                o.setIdOperacion(rs.getInt("id_oper"));
                o.setNumero(rs.getInt("o_numero"));
                o.setNombre(rs.getString("o_nombre"));
                o.setResponsable(rs.getString("o_responsable"));
                o.setDescripcion(rs.getString("o_descripcion"));
                o.setPlantilla(rs.getString("o_plantilla"));
                o.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                o.setAlarma(rs.getString("o_alarma"));
                o.setPostDisplay(rs.getString("o_post_display"));
                o.setPostSubmit(rs.getString("o_post_submit"));
                o.setOnLoad(rs.getString("o_on_load"));
                o.setOnSubmit(rs.getString("o_on_submit"));
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(rs.getInt("id_caso"));
                co.setIdCasoOper(rs.getInt("id_caso_oper"));
                co.setFechaInicio(rs.getTimestamp("co_fecha_ini"));
                co.setTiempoLimite(rs.getInt("co_tiempo_limite"));
                co.setResponsable(rs.getString("co_responsable"));
                co.setObservacion(rs.getString("co_observacion"));
                co.setStatus(rs.getInt("co_status"));
                co.setImporte(rs.getString("mImporte"));
                co.setStatusC(rs.getString("ESTATUS"));
                co.setFolioSicop(rs.getString("FolioSICOP"));
                co.setFolioMap(rs.getString("FolioMAP"));
                co.setFolioCal(rs.getString("FolioCAL"));
                co.setTipoAdecuacion(rs.getString("TipoAdecuacion"));
                co.setDocumento(rs.getString("documento"));
                co.setNivelAdecuacion(rs.getString("NivelAdecuacion"));
                try {
                    co.setfechaAppCont(rs.getString("beneficiario"));
                } catch (Exception e) {
                    co.setfechaAppCont(rs.getString("fechaAppCont"));
                    log.warn("EL tramite no cuenta con inf. de beneficiario");
                }
                co.setOperacion(o);
                l.add(co);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return l;
    }

    public static List selectCasoOperacion(Connection conn, String u_login) throws SQLException {
        List l = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT DISTINCT co.*, o.* " + "FROM cg_caso_operacion co with(nolock) , cg_operacion o with(nolock) , cg_usuario u with(nolock) , cg_usuario_grupo ug with(nolock) " + "WHERE o.id_tc = co.id_tc AND o.id_oper = co.id_oper " + "AND (u.u_login = co.co_responsable OR u.u_nombre = co.co_responsable OR " + "ug.g_nombre LIKE '%' + co.co_responsable) AND ug.u_login = u.u_login " + "AND u.u_login = ? ORDER BY co.co_fecha_ini ASC");
            pstmnt.setString(1, u_login);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Operacion o = new Operacion();
                o.setIdTC(rs.getInt("id_tc"));
                o.setIdOperacion(rs.getInt("id_oper"));
                o.setNumero(rs.getInt("o_numero"));
                o.setNombre(rs.getString("o_nombre"));
                o.setResponsable(rs.getString("o_responsable"));
                o.setDescripcion(rs.getString("o_descripcion"));
                o.setPlantilla(rs.getString("o_plantilla"));
                o.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                o.setAlarma(rs.getString("o_alarma"));
                o.setPostDisplay(rs.getString("o_post_display"));
                o.setPostSubmit(rs.getString("o_post_submit"));
                o.setOnLoad(rs.getString("o_on_load"));
                o.setOnSubmit(rs.getString("o_on_submit"));
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(rs.getInt("id_caso"));
                co.setIdCasoOper(rs.getInt("id_caso_oper"));
                co.setFechaInicio(rs.getTimestamp("co_fecha_ini"));
                co.setTiempoLimite(rs.getInt("co_tiempo_limite"));
                co.setResponsable(rs.getString("co_responsable"));
                co.setObservacion(rs.getString("co_observacion"));
                co.setStatus(rs.getInt("co_status"));
                co.setOperacion(o);
                l.add(co);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return l;
    }

    public static int update(Connection conn, CasoOperacion co, String nuevo_reponsable) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_caso_operacion SET co_responsable = ?" + ", co_observacion = ? WHERE id_caso = ? AND id_caso_oper = ?");
            pstmnt.setString(1, nuevo_reponsable);
            pstmnt.setString(2, co.getObservacion());
            pstmnt.setInt(3, co.getIdCaso());
            pstmnt.setInt(4, co.getIdCasoOper());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int update(Connection conn, CasoOperacion co) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_caso_operacion with(rowlock) SET id_tc = ?" + ", id_oper = ?, co_fecha_ini = ?, co_tiempo_limite = ?, co_responsable = ?" + ", co_observacion = ?, co_status = ? WHERE id_caso = ? AND id_caso_oper = ?");
            pstmnt.setInt(1, co.getIdTC());
            pstmnt.setInt(2, co.getIdOperacion());
            pstmnt.setTimestamp(3, co.getFechaInicio());
            pstmnt.setInt(4, co.getTiempoLimite());
            pstmnt.setString(5, co.getResponsable());
            pstmnt.setString(6, co.getObservacion());
            pstmnt.setInt(7, co.getStatus());
            pstmnt.setInt(8, co.getIdCaso());
            pstmnt.setInt(9, co.getIdCasoOper());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int updateObservacion(Connection conn, Caso c, String observacion) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_caso_operacion with(rowlock) SET " + " co_observacion = ? WHERE id_caso = ? AND id_caso_oper = ?");
            pstmnt.setString(1, observacion);
            pstmnt.setInt(2, c.getIdCaso());
            pstmnt.setInt(3, c.getCasoOperacion(0).getIdCasoOper());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    private static int getNextIdCasoOperacion(Connection conn, int id_caso) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT MAX(id_caso_oper) FROM cg_caso_operacion with(nolock) WHERE id_caso = ?");
            pstmnt.setInt(1, id_caso);
            rs = pstmnt.executeQuery();
            if (rs.next())
                retval = rs.getInt(1) + 1;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retval;
    }

    public static void revisaTiempoLimiteDeCasosOperacion(Connection conn, String prefixPath, boolean extMail) throws IOException, SQLException, GestionException {
        // Ethiel,
        // se
        // agrga
        // extMail
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        if (log.isDebugEnabled())
            log.debug("Entrando...");
        try {
            String whereStatement;
            String dbmsName = conn.getMetaData().getDatabaseProductName();
            int currentDbms = DBMS.searchDBMSByName(dbmsName);
            switch(currentDbms) {
                case DBMS.DB2:
                    whereStatement = "WHERE (co_fecha_ini + co_tiempo_limite SECONDS) <= ? AND co_tiempo_limite > 0";
                    break;
                case DBMS.ORACLE:
                    whereStatement = "WHERE  (co_fecha_ini + (co_tiempo_limite/86400)) <= ?";
                    break;
                case DBMS.ANTS:
                case DBMS.INFORMIX:
                case DBMS.ISERIES:
                case DBMS.SQLANYWHERE:
                case DBMS.SQLSERVER:
                    whereStatement = "WHERE  (co_fecha_ini + (co_tiempo_limite/86400)) <= ?";
                    break;
                case DBMS.SYBASE:
                default:
                    throw new RuntimeException("JDBC Driver \"" + dbmsName + "\" no implementado");
            }
            pstmnt = conn.prepareStatement("SELECT id_caso, id_caso_oper FROM cg_caso_operacion with(nolock) " + whereStatement);
            switch(currentDbms) {
                case DBMS.DB2:
                case DBMS.ORACLE:
                    pstmnt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    break;
                case DBMS.ANTS:
                case DBMS.INFORMIX:
                case DBMS.ISERIES:
                case DBMS.SQLANYWHERE:
                case DBMS.SQLSERVER:
                    pstmnt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    break;
                case DBMS.SYBASE:
                default:
                    throw new RuntimeException("JDBC Driver \"" + dbmsName + "\" no implementado");
            }
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CasoOperacion co = CasoOperacionManager.select(conn, rs.getInt(1), rs.getInt(2));
                Operacion o = co.getOperacion();
                Caso c = new Caso();
                c.setIdCaso(co.getIdCaso());
                c.setIdTC(co.getIdTC());
                c = CasoManager.select(conn, c);
                co.setStatus(co.getStatus() | CasoOperacion.MSG_SENDED);
                update(conn, co);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        if (log.isDebugEnabled())
            log.debug("Saliendo");
    }

    public static AcumuladoInbox selectTotalCasoOperacionPorUsuarioBandeja(Connection conn, String u_login, String terminated) throws SQLException {
        AcumuladoInbox inbox = new AcumuladoInbox();
        // List l = new ArrayList();
        PreparedStatement pstmnt0 = null;
        ResultSet rs = null;
        // int TotalRegistros = 0;
        // oDBMS = new DBMS(conn.getMetaData().getDatabaseProductName());
        try {
            if (terminated == null || !"S".equals(terminated)) {
                terminated = "N";
            }
            String query = null;
            // Obteniendo el total de registros
            query = "SELECT " + " 		(Select Count(1) as entrada from fx_Inbox2( ?, ?) " + " 		WHERE o_nombre in ('ATENCION','COPIA_PARA','RECHAZO_RESPUESTA','RECHAZO_PRORROGA','ACEPTAR_PRORROGA')) as entrada, " + " 		(Select Count(1) as porenviar from fx_Inbox2( ?, ?)" + " 		WHERE o_nombre in ('RECEPCION')) as porenviar," + " 		(Select Count(1) from fx_Inbox2( ?, ?)" + " 		WHERE o_nombre in ('TURNADO')) as turnado," + " 		(Select Count(1) from fx_Inbox2( ?, ?)" + " 		WHERE o_nombre in ('RESPUESTA','RESPUESTA_COORD','RESPUESTA_PARC','RESPUESTA_PARC_COORD','POR_CERRAR','RECHAZAR')) as respuesta," + " 		(Select Count(1) from fx_Inbox2( ?, ?)" + " 		WHERE o_nombre in ('PRORROGA')) as prorroga";
            pstmnt0 = conn.prepareStatement(query);
            pstmnt0.setString(1, u_login);
            pstmnt0.setString(2, terminated);
            pstmnt0.setString(3, u_login);
            pstmnt0.setString(4, terminated);
            pstmnt0.setString(5, u_login);
            pstmnt0.setString(6, terminated);
            pstmnt0.setString(7, u_login);
            pstmnt0.setString(8, terminated);
            pstmnt0.setString(9, u_login);
            pstmnt0.setString(10, terminated);
            rs = pstmnt0.executeQuery();
            while (rs.next()) {
                inbox.setTotalEntrada(rs.getInt("entrada"));
                inbox.setTotalPorEnviar(rs.getInt("porenviar"));
                inbox.setTotalTurnados(rs.getInt("turnado"));
                inbox.setTotalRespuestas(rs.getInt("respuesta"));
                inbox.setTotalProrrogas(rs.getInt("prorroga"));
            }
            // GAF 2010-04-16
            // En los manager no debe haber commits!
            // solamente en los businesslogic o servlets
            // conn.commit();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt0 = null;
        }
        return inbox;
    }

    public static PaginaData transferCasoOperacion(Connection conn, String u_login, int tipoTransferencia, String param1, String param2, String separator) throws SQLException {
        String query = "select * from fx_GetOperacionesActivas with(nolock) ";
        switch(tipoTransferencia) {
            case // POR AREA
            1:
                query += "Area(?,?)";
                break;
            case //
            2:
                query += "Folio(?,?,?)";
                break;
            default:
                query += "(?)";
                break;
        }
        System.out.println(query);
        // DBMS oDBMS = null;
        PaginaData pd = new PaginaData();
        List l = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        // oDBMS = new DBMS(conn.getMetaData().getDatabaseProductName());
        try {
            // Obteniendo el total de registros
            // String query = "select * from fx_GetOperacionesActivas(?)";
            pstmnt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            pstmnt.setString(1, u_login);
            switch(tipoTransferencia) {
                case // POR AREA
                1:
                    pstmnt.setString(2, param1);
                    break;
                case //
                2:
                    pstmnt.setString(2, param2);
                    pstmnt.setString(3, separator);
                    break;
            }
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(rs.getInt(1));
                co.setIdCasoOper(rs.getInt(2));
                Vector myVector = new Vector();
                myVector.add(co);
                l.add(myVector);
            }
            // GAF 2010-04-16
            // En los manager no debe haber commits!
            // solamente en los businesslogic o servlets
            // conn.commit();
            pd.setLista(l);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return pd;
    }

    public class TotalColumnas {

        public int totColumnas = 0;

        public TotalColumnas(int n) {
            this.totColumnas = n;
        }
    }
}
