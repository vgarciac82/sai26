package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.syc.fortimax.core.TipoDocumentoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class TipoCasoManager {

    public static int[] delete(Connection conn, int id_tc) throws SQLException {
        int[] retval = { -1, -1, -1, -1 };
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        PreparedStatement pstmnt2 = null;
        PreparedStatement pstmnt3 = null;
        try {
            pstmnt0 = conn.prepareStatement("DELETE FROM cg_operacion_siguiente with(rowlock) WHERE id_tc = ?");
            pstmnt1 = conn.prepareStatement("DELETE FROM cg_operacion with(rowlock) WHERE id_tc = ?");
            pstmnt2 = conn.prepareStatement("DELETE FROM cg_tipo_caso_variable with(rowlock) WHERE id_tc = ?");
            pstmnt3 = conn.prepareStatement("DELETE FROM cg_tipo_caso WHERE with(rowlock) id_tc = ?");
            pstmnt0.setInt(1, id_tc);
            pstmnt1.setInt(1, id_tc);
            pstmnt2.setInt(1, id_tc);
            pstmnt3.setInt(1, id_tc);
            retval[0] = pstmnt0.executeUpdate();
            retval[1] = pstmnt1.executeUpdate();
            retval[2] = pstmnt2.executeUpdate();
            retval[3] = pstmnt3.executeUpdate();
        } finally {
            if (pstmnt0 != null)
                pstmnt0.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (pstmnt2 != null)
                pstmnt2.close();
            if (pstmnt3 != null)
                pstmnt3.close();
            pstmnt0 = null;
            pstmnt1 = null;
            pstmnt2 = null;
            pstmnt3 = null;
        }
        return retval;
    }

    public static int insert(Connection conn, TipoCaso tc) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_tipo_caso " + "(id_tc, tc_descripcion, tc_gaveta_asociada, tc_tiempo_limite, tc_alarma, tc_who_can_init, tc_interface) " + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            pstmnt.setInt(1, tc.getIdTC());
            pstmnt.setString(2, tc.getDescripcion());
            pstmnt.setString(3, tc.getGavetaAsociada());
            pstmnt.setInt(4, tc.getTiempoLimite());
            pstmnt.setString(5, tc.getAlarma());
            pstmnt.setString(6, tc.getWhoCanInit());
            pstmnt.setString(7, tc.getInterface());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    /**
     * Obtiene el catalogo de operaciones, clave y descripcion para llenar un select
     * para los que es un filtro para reportes
     * @param conn
     * @return
     * @throws SQLException
     */
    public static Map selectAllOperaciones(Connection conn) throws SQLException {
        Map operaciones = new LinkedHashMap();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT ID_OPER,O_DESCRIPCION FROM CG_OPERACION with(nolock) WHERE ID_TC=11");
            rs = ps.executeQuery();
            while (rs.next()) {
                operaciones.put(rs.getString("ID_OPER"), rs.getString("O_DESCRIPCION"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            rs = null;
            ps = null;
        }
        return operaciones;
    }

    /**
     * Obtiene el catalogo de operaciones, clave y descripcion para llenar un select
     * para los que es un filtro para reportes
     * @param conn
     * @return
     * @throws SQLException
     */
    public static Map selectAllOperaciones(Connection conn, boolean op) throws SQLException {
        Map operaciones = new LinkedHashMap();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT O_NOMBRE,O_DESCRIPCION FROM CG_OPERACION with(nolock) WHERE ID_TC=11");
            rs = ps.executeQuery();
            while (rs.next()) {
                operaciones.put(rs.getString("O_NOMBRE"), rs.getString("O_DESCRIPCION"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            rs = null;
            ps = null;
        }
        return operaciones;
    }

    public static Map selectAllTipoCasos(Connection conn) throws SQLException {
        Map rm = new LinkedHashMap();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso tc with(nolock) WHERE tc_estatus = 'S' OR ID_TC = 39  ORDER BY id_tc");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(rs.getInt("id_tc"));
                tc.setDescripcion(rs.getString("tc_descripcion"));
                tc.setGavetaAsociada(rs.getString("tc_gaveta_asociada"));
                tc.setTiempoLimite(rs.getInt("tc_tiempo_limite"));
                tc.setAlarma(rs.getString("tc_alarma"));
                tc.setWhoCanInit(rs.getString("tc_who_can_init"));
                tc.setInterface(rs.getString("tc_interface"));
                TipoCasoVariable tcv = new TipoCasoVariable();
                tcv.setIdTC(tc.getIdTC());
                tc.setTipoCasoVariable(TipoCasoVariableManager.select(conn, tcv));
                tc.setDocumentos(TipoDocumentoManager.getDocumentos(conn, tc.getGavetaAsociada()));
                rm.put(tc.getDescripcion(), tc);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rm;
    }

    public static Map selectAllTipoCasos(Connection conn, String u_login) throws SQLException {
        Map rm = new LinkedHashMap();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            //TODO SQLServer vs Oracle
            try {
                pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso tc with(nolock), cg_usuario_grupo ug with(nolock) " + "WHERE tc_estatus = 'S' AND (ug.g_nombre LIKE tc.tc_who_can_init || '%' OR ug.u_login = tc.tc_who_can_init) " + "AND ug.u_login = ? ORDER BY id_tc");
                pstmnt.setString(1, u_login);
                rs = pstmnt.executeQuery();
            } catch (SQLException exq) {
                pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso tc with(nolock), cg_usuario_grupo ug with(nolock) " + " WHERE tc_estatus = 'S' AND ((ug.g_nombre LIKE tc.tc_who_can_init + '%' ) OR (ug.u_login = tc.tc_who_can_init ) OR (CASE WHEN SUBSTRING(ug.G_NOMBRE,0,16) like 'CAP_ANTPROYECTO%' THEN 'CAP_ANTPROYECTO'  ELSE '' END) " + " like SUBSTRING(tc.tc_who_can_init,0,16) + '%')  " + " AND ug.u_login = ? ORDER BY id_tc");
                pstmnt.setString(1, u_login);
                rs = pstmnt.executeQuery();
            }
            while (rs.next()) {
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(rs.getInt("id_tc"));
                tc.setDescripcion(rs.getString("tc_descripcion"));
                tc.setGavetaAsociada(rs.getString("tc_gaveta_asociada"));
                tc.setTiempoLimite(rs.getInt("tc_tiempo_limite"));
                tc.setAlarma(rs.getString("tc_alarma"));
                tc.setWhoCanInit(rs.getString("tc_who_can_init"));
                tc.setInterface(rs.getString("tc_interface"));
                TipoCasoVariable tcv = new TipoCasoVariable();
                tcv.setIdTC(tc.getIdTC());
                tc.setTipoCasoVariable(TipoCasoVariableManager.select(conn, tcv));
                tc.setDocumentos(TipoDocumentoManager.getDocumentos(conn, tc.getGavetaAsociada()));
                rm.put(tc.getDescripcion(), tc);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rm;
    }

    public static TipoCaso select(Connection conn, TipoCaso tc) throws SQLException {
        TipoCaso rtc = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (tc.getIdTC() > 0) {
                where.append(token + "id_tc = ?");
                token = " AND ";
            }
            if (tc.getDescripcion() != null) {
                where.append(token + "tc_descripcion = ?");
                token = " AND ";
            }
            if (tc.getGavetaAsociada() != null) {
                where.append(token + "tc_gaveta_asociada = ?");
                token = " AND ";
            }
            if (tc.getTiempoLimite() > 0) {
                where.append(token + "tc_tiempo_limite = ?");
                token = " AND ";
            }
            if (tc.getAlarma() != null) {
                where.append(token + "tc_alarma = ?");
                token = " AND ";
            }
            if (tc.getWhoCanInit() != null) {
                where.append(token + "tc_who_can_init = ?");
                token = " AND ";
            }
            if (tc.getInterface() != null) {
                where.append(token + "tc_interface = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso with(nolock) " + where.toString());
            int i = 1;
            if (tc.getIdTC() > 0)
                pstmnt.setInt(i++, tc.getIdTC());
            if (tc.getDescripcion() != null)
                pstmnt.setString(i++, tc.getDescripcion());
            if (tc.getGavetaAsociada() != null)
                pstmnt.setString(i++, tc.getGavetaAsociada());
            if (tc.getTiempoLimite() > 0)
                pstmnt.setInt(i++, tc.getTiempoLimite());
            if (tc.getAlarma() != null)
                pstmnt.setString(i++, tc.getAlarma());
            if (tc.getWhoCanInit() != null)
                pstmnt.setString(i++, tc.getWhoCanInit());
            if (tc.getInterface() != null)
                pstmnt.setString(i++, tc.getInterface());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                rtc = new TipoCaso();
                rtc.setIdTC(rs.getInt("id_tc"));
                rtc.setDescripcion(rs.getString("tc_descripcion"));
                rtc.setGavetaAsociada(rs.getString("tc_gaveta_asociada"));
                rtc.setTiempoLimite(rs.getInt("tc_tiempo_limite"));
                rtc.setAlarma(rs.getString("tc_alarma"));
                rtc.setWhoCanInit(rs.getString("tc_who_can_init"));
                rtc.setInterface(rs.getString("tc_interface"));
                TipoCasoVariable tcv = new TipoCasoVariable();
                tcv.setIdTC(rtc.getIdTC());
                rtc.setTipoCasoVariable(TipoCasoVariableManager.select(conn, tcv));
                rtc.setDocumentos(TipoDocumentoManager.getDocumentos(conn, rtc.getGavetaAsociada()));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rtc;
    }

    public static TipoCaso[] selectByApp(Connection conn, String app) throws SQLException {
        List<TipoCaso> tc = new ArrayList<TipoCaso>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            where.append(token + "tc_gaveta_asociada = ?");
            pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso with(nolock) " + where.toString());
            int i = 1;
            pstmnt.setString(i++, app);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                TipoCaso rtc = new TipoCaso();
                rtc.setIdTC(rs.getInt("id_tc"));
                rtc.setDescripcion(rs.getString("tc_descripcion"));
                rtc.setGavetaAsociada(rs.getString("tc_gaveta_asociada"));
                rtc.setTiempoLimite(rs.getInt("tc_tiempo_limite"));
                rtc.setAlarma(rs.getString("tc_alarma"));
                rtc.setWhoCanInit(rs.getString("tc_who_can_init"));
                rtc.setInterface(rs.getString("tc_interface"));
                TipoCasoVariable tcv = new TipoCasoVariable();
                tcv.setIdTC(rtc.getIdTC());
                rtc.setTipoCasoVariable(TipoCasoVariableManager.select(conn, tcv));
                rtc.setDocumentos(TipoDocumentoManager.getDocumentos(conn, rtc.getGavetaAsociada()));
                tc.add(rtc);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return tc.toArray(new TipoCaso[tc.size()]);
    }

    public static int update(Connection conn, TipoCaso tc) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_tipo_caso with(rowlock) SET tc_descripcion = ?, tc_gaveta_asociada = ?, tc_tiempo_limite = ?, " + "tc_alarma= ?, tc_who_can_init = ?, tc_interface = ? WHERE id_tc = ?");
            pstmnt.setString(1, tc.getDescripcion());
            pstmnt.setString(2, tc.getGavetaAsociada());
            pstmnt.setInt(3, tc.getTiempoLimite());
            pstmnt.setString(4, tc.getAlarma());
            pstmnt.setString(5, tc.getWhoCanInit());
            pstmnt.setString(6, tc.getInterface());
            pstmnt.setInt(7, tc.getIdTC());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int getNextId(Connection conn) throws SQLException {
        int id = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT MAX(id_tc) FROM cg_tipo_caso with(nolock)");
            rs = pstmnt.executeQuery();
            if (rs.next())
                id = rs.getInt(1) + 1;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
        }
        return id;
    }

    public static boolean existenCasos(Connection conn, int id_tc) throws SQLException {
        boolean retVal = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT COUNT(id_tc) FROM cg_caso with(nolock) WHERE id_tc = ?");
            pstmnt.setInt(1, id_tc);
            rs = pstmnt.executeQuery();
            if (rs.next())
                retVal = (rs.getInt(1) >= 1);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
        }
        return retVal;
    }

    public static CasoHerramientas getCasoHerramientas(Connection conn, int id_tc, int id_oper) throws Exception {
        String query = "SELECT cMuestraGuardar, " + "       cMuestraCerrar, " + "       cMuestraDescartar, " + "       cMuestraEnviar " + "FROM   cg_caso_operacion_herramientas WITH(nolock) " + "WHERE  id_tc = ? " + "       AND id_oper = ?  ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        CasoHerramientas casoHerramientas = new CasoHerramientas();
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_tc);
            ps.setInt(2, id_oper);
            rs = ps.executeQuery();
            if (rs.next()) {
                casoHerramientas.setCerrar(rs.getString("cMuestraCerrar"));
                casoHerramientas.setDescartar(rs.getString("cMuestraDescartar"));
                casoHerramientas.setEnviar(rs.getString("cMuestraEnviar"));
                casoHerramientas.setGuardar(rs.getString("cMuestraGuardar"));
            }
            return casoHerramientas;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
