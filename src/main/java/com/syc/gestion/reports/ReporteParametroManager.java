package com.syc.gestion.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Base64;

public class ReporteParametroManager {

    public static int delete(Connection conn, int rpt_id) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_reporte_parametro WHERE id_reporte = ?");
            pstmnt.setInt(1, rpt_id);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int delete(Connection conn, int rpt_id, int pmt_id) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_reporte_parametro WHERE id_reporte = ? AND id_parametro = ?");
            pstmnt.setInt(1, rpt_id);
            pstmnt.setInt(2, pmt_id);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, ReporteParametro rp) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_reporte_parametro (id_reporte, id_parametro, rp_nombre, rp_tipo, rp_longitud, rp_descripcion) VALUES (?, ?, ?, ?, ?, ?)");
            pstmnt.setInt(1, rp.getIdReporte());
            pstmnt.setInt(2, rp.getIdParametro());
            pstmnt.setString(3, rp.getNombre());
            pstmnt.setInt(4, rp.getTipo());
            pstmnt.setInt(5, rp.getLongitud());
            pstmnt.setString(6, rp.getDescripcion());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Map select(Connection conn, int idReporte) throws SQLException {
        Map m = new Hashtable();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_reporte_parametro WHERE id_reporte = ?");
            pstmnt.setInt(1, idReporte);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                ReporteParametro rp = new ReporteParametro();
                rp.setIdReporte(rs.getInt("id_reporte"));
                rp.setIdParametro(rs.getInt("id_parametro"));
                rp.setNombre(rs.getString("rp_nombre"));
                rp.setTipo(rs.getInt("rp_tipo"));
                rp.setLongitud(rs.getInt("rp_longitud"));
                rp.setDescripcion(rs.getString("rp_descripcion"));
                m.put(rp.getNombre(), rp);
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

    public static ReporteParametro select(Connection conn, ReporteParametro rp) throws SQLException {
        ReporteParametro nrp = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int a = -1;
        int b = -1;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (rp != null && rp.getIdReporte() > -1) {
                where.append(token + "id_reporte = ?");
                token = " AND ";
                a = rp.getIdReporte();
            }
            if (rp != null && rp.getIdParametro() > -1) {
                where.append(token + "id_parametro = ?");
                token = " AND ";
                a = rp.getIdParametro();
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_reporte_parametro " + where.toString());
            pstmnt.setInt(1, a);
            pstmnt.setInt(2, b);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nrp = new ReporteParametro();
                nrp.setIdReporte(rs.getInt("id_reporte"));
                nrp.setIdParametro(rs.getInt("id_parametro"));
                nrp.setNombre(rs.getString("rp_nombre"));
                nrp.setTipo(rs.getInt("rp_tipo"));
                nrp.setLongitud(rs.getInt("rp_longitud"));
                nrp.setDescripcion(rs.getString("rp_descripcion"));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return nrp;
    }

    public static int update(Connection conn, ReporteParametro rp) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_reporte_parametro" + " SET rp_nombre = ?, rp_tipo = ?, rp_longitud = ?, rp_descripcion = ?" + " WHERE id_reporte = ?  AND id_parametro = ?");
            pstmnt.setString(1, rp.getNombre());
            pstmnt.setInt(2, rp.getTipo());
            pstmnt.setInt(3, rp.getLongitud());
            pstmnt.setString(4, rp.getDescripcion());
            pstmnt.setInt(5, rp.getIdReporte());
            pstmnt.setInt(6, rp.getIdParametro());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }
}
