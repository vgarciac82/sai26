package com.syc.gestion.core;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.jfree.util.Log;
import com.syc.utils.URIComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CasoDatoManager {

    private static final Logger log = LoggerFactory.getLogger(CasoDatoManager.class);

    public static Map<String, CasoDato> createCasoDato(Connection conn, int id_tc, int id_caso) throws SQLException {
        Map<String, CasoDato> m = new LinkedHashMap<>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso_variable WITH(NOLOCK) WHERE id_tc = ?");
            pstmnt.setInt(1, id_tc);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CasoDato cd = new CasoDato();
                cd.setIdCaso(id_caso);
                cd.setIdCD(rs.getInt("id_tcv"));
                cd.setIdTC(rs.getInt("id_tc"));
                cd.setValor(null);
                cd.setTipoCasoVariable(TipoCasoVariableManager.select(conn, cd.getIdTC(), cd.getIdCD()));
                insert(conn, cd);
                m.put(rs.getString("tcv_nombre"), cd);
            }
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return m;
    }

    public static int delete(Connection conn, int id_caso, int id_cd) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_caso_dato with(rowlock) WHERE id_caso = ? AND id_cd = ?");
            pstmnt.setInt(1, id_caso);
            pstmnt.setInt(2, id_cd);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, CasoDato cd) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_caso_dato (id_caso, id_cd, id_tc, cd_valor) VALUES (?, ?, ?, ?)");
            pstmnt.setInt(1, cd.getIdCaso());
            pstmnt.setInt(2, cd.getIdCD());
            pstmnt.setInt(3, cd.getIdTC());
            pstmnt.setString(4, cd.getValor());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Map<String, String> readValuesCasoDato(HttpServletRequest req, Map<String, CasoDato> casoDato, boolean isParameter) {
        Map<String, String> m = new Hashtable<>();
        for (Iterator<String> iter = casoDato.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            CasoDato cd = (CasoDato) casoDato.get(name);
            String value = null;
            if (isParameter) {
                if (req.getParameter(cd.getTipoCasoVariable().getNombreJS()) != null) {
                    value = URIComponentUtils.decodeURIComponent(req.getParameter(cd.getTipoCasoVariable().getNombreJS()));
                }
            } else {
                value = req.getHeader(cd.getTipoCasoVariable().getNombre().toLowerCase());
                if (value == null)
                    continue;
                value = URIComponentUtils.decodeURIComponent(value);
            }
            if (value != null) {
                log.trace("Object: {}", "Putting data for variable: " + name + ": " + value);
                m.put(name, value);
            }
        }
        return m;
    }

    public static Map<String, CasoDato> select(Connection conn, CasoDato cd) throws SQLException {
        Map<String, CasoDato> m = new LinkedHashMap<>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            if (cd.getIdCaso() > 0)
                where.append(" AND id_caso = ?");
            if (cd.getIdCD() > 0)
                where.append(" AND id_cd = ?");
            if (cd.getIdTC() > 0)
                where.append(" AND id_tc = ?");
            if (cd.getValor() != null)
                where.append(" AND cd_valor = ?");
            pstmnt = conn.prepareStatement("SELECT * FROM cg_caso_dato cd WITH(NOLOCK), cg_tipo_caso_variable tcv WITH(NOLOCK) " + "WHERE tcv.id_tc = cd.id_tc AND id_tcv = cd.id_cd" + where.toString());
            int i = 1;
            if (cd.getIdCaso() > 0)
                pstmnt.setInt(i++, cd.getIdCaso());
            if (cd.getIdCD() > 0)
                pstmnt.setInt(i++, cd.getIdCD());
            if (cd.getIdTC() > 0)
                pstmnt.setInt(i++, cd.getIdTC());
            if (cd.getValor() != null)
                pstmnt.setString(i++, cd.getValor());
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CasoDato lcd = new CasoDato();
                lcd.setIdCaso(rs.getInt("id_caso"));
                lcd.setIdCD(rs.getInt("id_cd"));
                lcd.setIdTC(rs.getInt("id_tc"));
                lcd.setValor(rs.getString("cd_valor"));
                TipoCasoVariable rtcv = new TipoCasoVariable();
                rtcv.setIdTC(rs.getInt("id_tc"));
                rtcv.setIdTCV(rs.getInt("id_tcv"));
                rtcv.setNombre(rs.getString("tcv_nombre"));
                rtcv.setDescripcion(rs.getString("tcv_descripcion"));
                rtcv.setEtiqueta(rs.getString("tcv_etiqueta"));
                rtcv.setTipo(rs.getInt("tcv_tipo"));
                rtcv.setLongitud(rs.getInt("tcv_longitud"));
                rtcv.setIndice(rs.getInt("tcv_indice"));
                rtcv.setEnGaveta(rs.getString("tcv_en_gaveta"));
                lcd.setTipoCasoVariable(rtcv);
                m.put(rs.getString("tcv_nombre"), lcd);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return m;
    }

    public static int update(Connection conn, CasoDato cd) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        Log.debug("Object: " + String.valueOf("Updating CASO_DATO:" + cd));
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_caso_dato with(rowlock) SET id_tc = ?, cd_valor = ? WHERE id_caso = ? AND id_cd = ?");
            pstmnt.setInt(1, cd.getIdTC());
            pstmnt.setString(2, cd.getValor());
            pstmnt.setInt(3, cd.getIdCaso());
            pstmnt.setInt(4, cd.getIdCD());
            if ((7 == cd.getIdCD() || 8 == cd.getIdCD() || 9 == cd.getIdCD()) && ((cd.getIdTC() == 3) && "".equals(cd.getValor()))) {
                retval = 1;
            } else {
                retval = pstmnt.executeUpdate();
            }
        } catch (SQLException e) {
            // FIXME quitar este catch cuando re resuelva lo del interbloqueo
            Log.warn("Error occurred", e);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static void update(Connection conn, int id_tc, int id_caso, Map<String, String> data) throws SQLException {
        TipoCasoVariable tcv = new TipoCasoVariable();
        tcv.setIdTC(id_tc);
        Map mTcv = TipoCasoVariableManager.select(conn, tcv);
        for (Iterator<String> iter = data.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String value = (String) data.get(name);
            if (("FECHA_AP_CONT".equals(name) || "APLICADO_CONT".equals(name)) && ((id_tc == 3) && "".equals(value))) {
                continue;
            }
            if (value == null)
                continue;
            tcv = (TipoCasoVariable) mTcv.get(name);
            if (tcv == null)
                throw new SQLException("No existe la variable \"" + name + "\" en Tipo Caso Variable (" + id_tc + ")");
            CasoDato cd = new CasoDato();
            cd.setIdCaso(id_caso);
            cd.setIdCD(tcv.getIdTCV());
            cd.setIdTC(id_tc);
            cd.setValor(value);
            cd.setTipoCasoVariable(tcv);
            update(conn, cd);
        }
    }

    public static void updateCoResponsable(Connection conn, int id_caso, int id_tc, String responsable) throws SQLException {
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("update CG_CASO_OPERACION with(rowlock) set CO_RESPONSABLE=? where ID_CASO=? and ID_TC=?");
            pstmnt.setString(1, responsable);
            pstmnt.setInt(2, id_caso);
            pstmnt.setInt(3, id_tc);
            pstmnt.executeUpdate();
        } catch (SQLException e) {
            Log.warn("Error occurred", e);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
    }

    public static void updateResponsable(Connection conn, int id_caso, int id_tc) throws SQLException {
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("update CG_CASO_OPERACION with(rowlock) set CO_RESPONSABLE=(select O_RESPONSABLE from CG_OPERACION with(nolock) where ID_TC=? and ID_OPER=(select ID_OPER from CG_CASO_OPERACION with(nolock) where ID_CASO=?)) where ID_CASO=?");
            pstmnt.setInt(1, id_tc);
            pstmnt.setInt(2, id_caso);
            pstmnt.setInt(3, id_caso);
            pstmnt.executeUpdate();
        } catch (SQLException e) {
            Log.warn("Error occurred", e);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
    }
}
