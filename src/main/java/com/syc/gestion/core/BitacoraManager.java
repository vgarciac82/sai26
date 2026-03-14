package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class BitacoraManager extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(BitacoraManager.class);

    java.util.Date toDay = new java.util.Date();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    java.sql.Timestamp t = new java.sql.Timestamp(toDay.getTime());

    public static int insert(Connection conn, Bitacora b) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        log.info("inserta desde biacora 1");
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_bitacora " + "(id_bitacora, b_id_caso, b_id_caso_oper, b_c_id_gabinete, b_c_folio" + ", b_c_fecha_ini, b_c_tiempo_limite, b_c_status, b_id_tc, b_id_oper, b_co_fecha_ini" + ", b_co_tiempo_limite, b_co_responsable_ejec, b_co_id_caso_oper_sigte" + ", b_co_responsable_sigte, b_co_operacion_sigte, b_co_observacion, b_co_status,B_NOMBRE_EQUIPO,B_IP_EQUIPO) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            pstmnt.setInt(1, b.getIdBitacora());
            pstmnt.setInt(2, b.getIdCaso());
            pstmnt.setInt(3, b.getIdCasoOper());
            pstmnt.setInt(4, b.getIdGabinete());
            pstmnt.setString(5, b.getFolio());
            pstmnt.setTimestamp(6, b.getFechaInicioCaso());
            pstmnt.setInt(7, b.getTiempoLimiteCaso());
            pstmnt.setInt(8, b.getCasoStatus());
            pstmnt.setInt(9, b.getIdTC());
            pstmnt.setInt(10, b.getIdOperacion());
            pstmnt.setTimestamp(11, b.getFechaInicioCasoOper());
            pstmnt.setInt(12, b.getTiempoLimiteCasoOper());
            pstmnt.setString(13, b.getResponsableEjec());
            pstmnt.setInt(14, b.getIdCasoOperSigte());
            pstmnt.setString(15, b.getResponsableSigte());
            pstmnt.setString(16, b.getOperacionSigte());
            pstmnt.setString(17, b.getObservacion());
            pstmnt.setInt(18, b.getCasoOperacionStatus());
            pstmnt.setString(19, b.getB_nombre_equipo());
            pstmnt.setString(20, b.getB_ip_equipo());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public int insert(String u_login, String nombre_equipo, String ip_equipo, String accion, String obs) throws SQLException, GestionException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        Connection conn = null;
        log.info("inserta desde biacora 2");
        String query = "INSERT INTO cg_bitacora " + "(id_bitacora, b_id_caso, b_id_caso_oper, b_c_id_gabinete, b_c_folio" + ", b_c_fecha_ini, b_c_tiempo_limite, b_c_status, b_id_tc, b_id_oper, b_co_fecha_ini" + ", b_co_tiempo_limite, b_co_responsable_ejec, b_co_id_caso_oper_sigte" + ", b_co_responsable_sigte, b_co_operacion_sigte, b_co_observacion, b_co_status,B_NOMBRE_EQUIPO,B_IP_EQUIPO) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        /*
		String query2="INSERT INTO cg_bitacora "
			+ "(id_bitacora, b_id_caso, b_id_caso_oper, b_c_id_gabinete, b_c_folio"
			+ ", b_c_fecha_ini, b_c_tiempo_limite, b_c_status, b_id_tc, b_id_oper, b_co_fecha_ini"
			+ ", b_co_tiempo_limite, b_co_responsable_ejec, b_co_id_caso_oper_sigte"
			+ ", b_co_responsable_sigte, b_co_operacion_sigte, b_co_observacion, b_co_status,B_NOMBRE_EQUIPO,B_IP_EQUIPO) "
			+ "VALUES (9999, 0, 1, -1, "+accion+", "+String.valueOf(t)+", -1, 1, 0, 1, "+String.valueOf(t)+", -1,"+u_login+", 1, "+obs+", "+accion+", "+obs+", 1, "+nombre_equipo+", "+ip_equipo+")";
		log.info(query2);
		*/
        try {
            conn = getConnection();
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, getNextIdBitacora(conn));
            //b.getIdCaso());
            pstmnt.setInt(2, 0);
            //b.getIdCasoOper());
            pstmnt.setInt(3, 1);
            // b.getIdGabinete());
            pstmnt.setInt(4, -1);
            // b.getFolio());
            pstmnt.setString(5, accion);
            //b.getFechaInicioCaso());
            pstmnt.setTimestamp(6, t);
            //b.getTiempoLimiteCaso());
            pstmnt.setInt(7, -1);
            //b.getCasoStatus());
            pstmnt.setInt(8, 1);
            //b.getIdTC());
            pstmnt.setInt(9, 0);
            //b.getIdOperacion());
            pstmnt.setInt(10, 1);
            // b.getFechaInicioCasoOper());
            pstmnt.setTimestamp(11, t);
            //b.getTiempoLimiteCasoOper());
            pstmnt.setInt(12, -1);
            // b.getResponsableEjec());
            pstmnt.setString(13, u_login);
            //b.getIdCasoOperSigte());
            pstmnt.setInt(14, 1);
            //b.getResponsableSigte());
            pstmnt.setString(15, obs);
            // b.getOperacionSigte());
            pstmnt.setString(16, accion);
            // b.getObservacion());
            pstmnt.setString(17, obs);
            //b.getCasoOperacionStatus());
            pstmnt.setInt(18, 1);
            //NOMBRE EQUIPO
            pstmnt.setString(19, nombre_equipo);
            //IP EQUIPO
            pstmnt.setString(20, ip_equipo);
            retval = pstmnt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (conn != null)
                conn.close();
            pstmnt = null;
            conn = null;
        }
        return retval;
    }

    public int insert(Connection conn, String u_login, String nombre_equipo, String ip_equipo, String accion, String obs) throws SQLException, GestionException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        log.info("inserta desde biacora 3");
        String query = "INSERT INTO cg_bitacora " + "(id_bitacora, b_id_caso, b_id_caso_oper, b_c_id_gabinete, b_c_folio" + ", b_c_fecha_ini, b_c_tiempo_limite, b_c_status, b_id_tc, b_id_oper, b_co_fecha_ini" + ", b_co_tiempo_limite, b_co_responsable_ejec, b_co_id_caso_oper_sigte" + ", b_co_responsable_sigte, b_co_operacion_sigte, b_co_observacion, b_co_status,B_NOMBRE_EQUIPO,B_IP_EQUIPO) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        try {
            //conn = getConnection();
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, getNextIdBitacora(conn));
            //b.getIdCaso());
            pstmnt.setInt(2, 0);
            //b.getIdCasoOper());
            pstmnt.setInt(3, 1);
            // b.getIdGabinete());
            pstmnt.setInt(4, -1);
            // b.getFolio());
            pstmnt.setString(5, accion);
            //b.getFechaInicioCaso());
            pstmnt.setTimestamp(6, t);
            //b.getTiempoLimiteCaso());
            pstmnt.setInt(7, -1);
            //b.getCasoStatus());
            pstmnt.setInt(8, 1);
            //b.getIdTC());
            pstmnt.setInt(9, 0);
            //b.getIdOperacion());
            pstmnt.setInt(10, 1);
            // b.getFechaInicioCasoOper());
            pstmnt.setTimestamp(11, t);
            //b.getTiempoLimiteCasoOper());
            pstmnt.setInt(12, -1);
            // b.getResponsableEjec());
            pstmnt.setString(13, u_login);
            //b.getIdCasoOperSigte());
            pstmnt.setInt(14, 1);
            //b.getResponsableSigte());
            pstmnt.setString(15, obs);
            // b.getOperacionSigte());
            pstmnt.setString(16, accion);
            // b.getObservacion());
            pstmnt.setString(17, obs);
            //b.getCasoOperacionStatus());
            pstmnt.setInt(18, 1);
            //NOMBRE EQUIPO
            pstmnt.setString(19, nombre_equipo);
            //IP EQUIPO
            pstmnt.setString(20, ip_equipo);
            retval = pstmnt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public int insert(String u_login, String nombre_equipo, String ip_equipo, String accion, String respsgte, String obs) throws SQLException, GestionException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        Connection conn = null;
        log.info("inserta desde biacora 2");
        String query = "INSERT INTO cg_bitacora " + "(id_bitacora, b_id_caso, b_id_caso_oper, b_c_id_gabinete, b_c_folio" + ", b_c_fecha_ini, b_c_tiempo_limite, b_c_status, b_id_tc, b_id_oper, b_co_fecha_ini" + ", b_co_tiempo_limite, b_co_responsable_ejec, b_co_id_caso_oper_sigte" + ", b_co_responsable_sigte, b_co_operacion_sigte, b_co_observacion, b_co_status,B_NOMBRE_EQUIPO,B_IP_EQUIPO) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        /*
		String query2="INSERT INTO cg_bitacora "
			+ "(id_bitacora, b_id_caso, b_id_caso_oper, b_c_id_gabinete, b_c_folio"
			+ ", b_c_fecha_ini, b_c_tiempo_limite, b_c_status, b_id_tc, b_id_oper, b_co_fecha_ini"
			+ ", b_co_tiempo_limite, b_co_responsable_ejec, b_co_id_caso_oper_sigte"
			+ ", b_co_responsable_sigte, b_co_operacion_sigte, b_co_observacion, b_co_status,B_NOMBRE_EQUIPO,B_IP_EQUIPO) "
			+ "VALUES (9999, 0, 1, -1, "+accion+", "+String.valueOf(t)+", -1, 1, 0, 1, "+String.valueOf(t)+", -1,"+u_login+", 1, "+obs+", "+accion+", "+obs+", 1, "+nombre_equipo+", "+ip_equipo+")";
		log.info(query2);
		*/
        try {
            conn = getConnection();
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, getNextIdBitacora(conn));
            //b.getIdCaso());
            pstmnt.setInt(2, 0);
            //b.getIdCasoOper());
            pstmnt.setInt(3, 1);
            // b.getIdGabinete());
            pstmnt.setInt(4, -1);
            // b.getFolio());
            pstmnt.setString(5, accion);
            //b.getFechaInicioCaso());
            pstmnt.setTimestamp(6, t);
            //b.getTiempoLimiteCaso());
            pstmnt.setInt(7, -1);
            //b.getCasoStatus());
            pstmnt.setInt(8, 1);
            //b.getIdTC());
            pstmnt.setInt(9, 0);
            //b.getIdOperacion());
            pstmnt.setInt(10, 1);
            // b.getFechaInicioCasoOper());
            pstmnt.setTimestamp(11, t);
            //b.getTiempoLimiteCasoOper());
            pstmnt.setInt(12, -1);
            // b.getResponsableEjec());
            pstmnt.setString(13, u_login);
            //b.getIdCasoOperSigte());
            pstmnt.setInt(14, 1);
            //b.getResponsableSigte());
            pstmnt.setString(15, respsgte);
            // b.getOperacionSigte());
            pstmnt.setString(16, accion);
            // b.getObservacion());
            pstmnt.setString(17, obs);
            //b.getCasoOperacionStatus());
            pstmnt.setInt(18, 1);
            //NOMBRE EQUIPO
            pstmnt.setString(19, nombre_equipo);
            //IP EQUIPO
            pstmnt.setString(20, ip_equipo);
            retval = pstmnt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (conn != null)
                conn.close();
            pstmnt = null;
            conn = null;
        }
        return retval;
    }

    public static void registraCasoOperacion(Connection conn, String u_login, Caso c, int[] idCasoOper, String[] resp, String[] oper) throws SQLException {
        registraCasoOperacion(conn, u_login, c, idCasoOper, resp, oper, false);
    }

    public static void registraCasoOperacion(Connection conn, String u_login, Caso c, int[] idCasoOper, String[] resp, String[] oper, boolean registraAll) throws SQLException {
        Bitacora b = new Bitacora();
        int maxIdx = registraAll ? c.getCasoOperacion().size() : 1;
        for (int idx = 0; idx < maxIdx; idx++) {
            b.setIdCaso(c.getIdCaso());
            b.setIdCasoOper(c.getCasoOperacion(idx).getIdCasoOper());
            b.setIdGabinete(c.getIdGabinete());
            b.setFolio(c.getFolio());
            b.setFechaInicioCaso(c.getFechaInicio());
            b.setTiempoLimiteCaso(c.getTiempoLimite());
            b.setCasoStatus(c.getStatus());
            b.setIdTC(c.getIdTC());
            b.setIdOperacion(c.getCasoOperacion(idx).getIdOperacion());
            b.setFechaInicioCasoOper(c.getCasoOperacion(idx).getFechaInicio());
            b.setTiempoLimiteCasoOper(c.getCasoOperacion(idx).getTiempoLimite());
            b.setResponsableEjec(u_login);
            b.setObservacion(c.getCasoOperacion(idx).getObservacion());
            b.setCasoOperacionStatus(c.getCasoOperacion(idx).getStatus());
            b.setB_nombre_equipo(c.getC_nom_equipoIni());
            b.setB_ip_equipo(c.getC_nom_equipoUser());
            for (int i = 0; i < resp.length; i++) {
                b.setIdBitacora(getNextIdBitacora(conn));
                b.setIdCasoOperSigte(idCasoOper[i]);
                b.setResponsableSigte(resp[i]);
                b.setOperacionSigte(oper[i]);
                insert(conn, b);
            }
        }
    }

    private static int getNextIdBitacora(Connection conn) throws SQLException {
        int retval = -1;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT MAX(id_bitacora) FROM cg_bitacora with(nolock)");
            if (rs.next())
                retval = rs.getInt(1) + 1;
        } finally {
            if (rs != null)
                rs.close();
            if (stmnt != null)
                stmnt.close();
            rs = null;
            stmnt = null;
        }
        return retval;
    }
}
