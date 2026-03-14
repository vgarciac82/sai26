package com.syc.gestion.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Base64;

public class ReporteManager {

    public static int delete(Connection conn, int id_reporte) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_reporte WHERE id_reporte = ?");
            pstmnt.setInt(1, id_reporte);
            //aqui se deberia borrar los parametros del reporte
            //GrupoPropiedadesManager.delete(conn, g_nombre);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, Reporte r) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_reporte (id_reporte, r_nombre, r_path, r_descripcion) VALUES (?, ?, ?, ?)");
            pstmnt.setInt(1, r.getIdReporte());
            pstmnt.setString(2, r.getNombre());
            pstmnt.setString(3, r.getRuta());
            pstmnt.setString(4, r.getDescripcion());
            if (!r.getParametros().isEmpty()) {
                for (Iterator iter = r.getParametros().keySet().iterator(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    ReporteParametroManager.insert(conn, r.getParametro(name));
                }
            }
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Reporte select(Connection conn, Reporte r) throws SQLException {
        Reporte nr = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int a = -1;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (r != null && r.getIdReporte() > -1) {
                where.append(token + "id_reporte = ?");
                token = " AND ";
                a = r.getIdReporte();
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_reporte " + where.toString());
            pstmnt.setInt(1, a);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nr = new Reporte();
                nr.setIdReporte(rs.getInt("id_reporte"));
                nr.setNombre(rs.getString("r_nombre"));
                nr.setRuta(rs.getString("r_path"));
                nr.setDescripcion(rs.getString("r_descripcion"));
                nr.setParametros(ReporteParametroManager.select(conn, nr.getIdReporte()));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return nr;
    }

    public static List selectAll(Connection conn) throws SQLException {
        List grpList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_reporte ORDER BY r_nombre");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Reporte r = new Reporte();
                r.setIdReporte(rs.getInt("id_reporte"));
                r.setNombre(rs.getString("r_nombre"));
                r.setRuta(rs.getString("r_path"));
                r.setDescripcion(rs.getString("r_descripcion"));
                r.setParametros(ReporteParametroManager.select(conn, r.getIdReporte()));
                grpList.add(r);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return grpList;
    }

    public static int update(Connection conn, Reporte r) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_reporte SET r_nombre = ?, r_path = ?, r_descripcion = ? WHERE id_reporte = ?");
            pstmnt.setString(1, r.getNombre());
            pstmnt.setString(2, r.getRuta());
            pstmnt.setString(3, r.getDescripcion());
            pstmnt.setInt(4, r.getIdReporte());
            // FIXME Solo se actualizan o tambien se insertan?
            if (!r.getParametros().isEmpty()) {
                for (Iterator iter = r.getParametros().keySet().iterator(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    ReporteParametro rp = r.getParametro(name);
                    if (rp != null) {
                        ReporteParametroManager.update(conn, rp);
                    } else {
                        ReporteParametroManager.insert(conn, rp);
                    }
                }
            }
            retval = pstmnt.executeUpdate();
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
}
