package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import java.util.Base64;

public class DescripcionManager extends DataSourceManager {

    public static Map select(Connection conn, String titulo_aplicacion) throws SQLException {
        Map m = new LinkedHashMap();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM imx_descripcion WHERE titulo_aplicacion = ? " + "ORDER BY posicion_campo");
            pstmnt.setString(1, titulo_aplicacion);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Descripcion rd = new Descripcion();
                rd.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                rd.setNombreCampo(rs.getString("nombre_campo"));
                rd.setNombreColumna(rs.getString("nombre_columna"));
                rd.setPosicionCampo(rs.getInt("posicion_campo"));
                rd.setNombreTipoDatos(rs.getString("nombre_tipo_datos"));
                rd.setIdTipoDatos(rs.getInt("id_tipo_datos"));
                rd.setLongitudCampo(rs.getInt("longitud_campo"));
                rd.setValorDefCampo(rs.getString("valor_def_campo"));
                rd.setMascaraCampo(rs.getString("mascara_campo"));
                rd.setNombreIndice(rs.getString("nombre_indice"));
                rd.setIndiceTipo(rs.getInt("indice_tipo"));
                rd.setMultiValuado(rs.getString("multivaluado"));
                rd.setRequerido(rs.getString("requerido"));
                rd.setEditable(rs.getString("editable"));
                rd.setLista(rs.getString("lista"));
                m.put(rd.getNombreCampo(), rd);
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

    public static Map selectAvanzada(Connection conn, String titulo_aplicacion, String tipoasunto, boolean usa_lista_campos) throws SQLException {
        Map m = new LinkedHashMap();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String lstCamposIn = "(";
        String sep = "";
        String[][] APP_LST_CAMPOS = null;
        APP_LST_CAMPOS = (tipoasunto.equals("I") ? GestionInterface.APP_LST_CAMPOS_AVANZADA_I : GestionInterface.APP_LST_CAMPOS_AVANZADA_E);
        for (int i = 0; i < APP_LST_CAMPOS.length; i++) {
            lstCamposIn += sep + "'" + APP_LST_CAMPOS[i][0] + "'";
            sep = ",";
        }
        lstCamposIn += ")";
        String query = " SELECT *" + " FROM   imx_descripcion" + " WHERE  titulo_aplicacion = ?" + (usa_lista_campos ? " AND    nombre_campo IN " + lstCamposIn : "") + " ORDER BY posicion_campo";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, titulo_aplicacion);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Descripcion rd = new Descripcion();
                rd.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                rd.setNombreCampo(rs.getString("nombre_campo"));
                rd.setNombreColumna(rs.getString("nombre_columna"));
                rd.setPosicionCampo(rs.getInt("posicion_campo"));
                rd.setNombreTipoDatos(rs.getString("nombre_tipo_datos"));
                rd.setIdTipoDatos(rs.getInt("id_tipo_datos"));
                rd.setLongitudCampo(rs.getInt("longitud_campo"));
                rd.setValorDefCampo(rs.getString("valor_def_campo"));
                rd.setMascaraCampo(rs.getString("mascara_campo"));
                rd.setNombreIndice(rs.getString("nombre_indice"));
                rd.setIndiceTipo(rs.getInt("indice_tipo"));
                rd.setMultiValuado(rs.getString("multivaluado"));
                rd.setRequerido(rs.getString("requerido"));
                rd.setEditable(rs.getString("editable"));
                rd.setLista(rs.getString("lista"));
                m.put(rd.getNombreCampo(), rd);
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

    public static String[][] getData(Connection conn, String titulo_aplicacion, int id_gabinete) throws SQLException {
        Statement stmnt = null;
        ResultSet rs = null;
        String[][] data = new String[0][0];
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT COUNT(id_gabinete) FROM imx" + titulo_aplicacion.toLowerCase() + " WHERE id_gabinete = " + id_gabinete);
            if (!rs.next())
                return data;
            int totRows = rs.getInt(1);
            rs = stmnt.executeQuery("SELECT * FROM imx" + titulo_aplicacion.toLowerCase() + " WHERE id_gabinete = " + id_gabinete + " ORDER BY 3");
            int totCols = rs.getMetaData().getColumnCount() - 1;
            data = new String[totRows][totCols];
            for (int row = 0; rs.next(); row++) {
                for (int i = 0; i < totCols; i++) {
                    boolean isEmpty = rs.getString(i + 2) == null;
                    data[row][i] = (i == 0) ? rs.getString("id_gabinete") : ((isEmpty) ? "" : rs.getString(i + 2).trim());
                }
            }
        } finally {
            if (rs != null)
                rs.close();
            if (stmnt != null)
                stmnt.close();
            rs = null;
            stmnt = null;
        }
        return data;
    }

    public Descripcion[] selectDescripcion(String titulo_aplicacion) {
        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        Descripcion[] desc = null;
        try {
            conn = getConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT COUNT(*)" + " FROM imx_descripcion" + " WHERE titulo_aplicacion ='" + titulo_aplicacion + "'");
            if (!rs.next())
                return null;
            desc = new Descripcion[rs.getInt(1)];
            rs = stmnt.executeQuery("SELECT titulo_aplicacion, nombre_campo, nombre_columna, posicion_campo, nombre_tipo_datos," + " id_tipo_datos, longitud_campo, valor_def_campo, mascara_campo, nombre_indice," + " indice_tipo, multivaluado, requerido, editable, lista" + " FROM imx_descripcion" + " WHERE titulo_aplicacion ='" + titulo_aplicacion + "' ORDER BY posicion_campo");
            for (int i = 0; rs.next(); i++) {
                desc[i] = new Descripcion(rs.getString("titulo_aplicacion"), rs.getString("nombre_campo"), rs.getString("nombre_columna"), rs.getInt("posicion_campo"), rs.getString("nombre_tipo_datos"), rs.getInt("id_tipo_datos"), rs.getInt("longitud_campo"), rs.getString("valor_def_campo"), rs.getString("mascara_campo"), rs.getString("nombre_indice"), rs.getInt("indice_tipo"), rs.getString("multivaluado"), rs.getString("requerido"), rs.getString("editable"), rs.getString("lista"));
            }
        } catch (SQLException se) {
            System.err.println("[" + this.getClass().getName() + "] Error SQL");
            se.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmnt != null)
                    stmnt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                System.err.println("[" + this.getClass().getName() + "] Error SQL cerrando");
                se.printStackTrace();
            }
            rs = null;
            stmnt = null;
            conn = null;
            return desc;
        }
    }

    public ListaImaxfile[] selectListaImaxfile(String titulo_aplicacion, String nombre_campo) {
        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        ResultSet rsInfoCat = null;
        ListaImaxfile[] lstimx = null;
        String str_tbl_catalogo = "";
        try {
            conn = getConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT nombre_catalogo" + " FROM imx_campo_catalogo" + " WHERE titulo_aplicacion ='" + titulo_aplicacion + "'" + " AND nombre_campo ='" + nombre_campo + "'");
            if (!rs.next())
                return null;
            rsInfoCat = stmnt.executeQuery("SELECT tbl_catalogo" + " FROM imx_catalogo" + " WHERE nombre_catalogo ='" + rs.getString("nombre_catalogo") + "'");
            if (!rsInfoCat.next())
                return null;
            str_tbl_catalogo = rsInfoCat.getString("tbl_catalogo");
            rs = stmnt.executeQuery("SELECT COUNT(*) FROM " + str_tbl_catalogo);
            if (!rs.next())
                return null;
            lstimx = new ListaImaxfile[rs.getInt(1)];
            rs = stmnt.executeQuery("SELECT * FROM " + str_tbl_catalogo + " ORDER BY 1");
            for (int i = 0; rs.next(); i++) {
                lstimx[i] = new ListaImaxfile(rs.getString("consecutivo"), rs.getString("valor"));
            }
        } catch (SQLException se) {
            System.err.println("[" + this.getClass().getName() + "] Error SQL");
            se.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (rsInfoCat != null)
                    rsInfoCat.close();
                if (stmnt != null)
                    stmnt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                System.err.println("[" + this.getClass().getName() + "] Error SQL cerrando");
                se.printStackTrace();
            }
            rs = null;
            rsInfoCat = null;
            stmnt = null;
            conn = null;
        }
        return lstimx;
    }

    public ListaImaxfile[] selectLista(String nombre_lista) {
        //debe ser el nombre exacto de la tabla porque este no es un catalogo de fortimax
        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        ResultSet rsInfoCat = null;
        ListaImaxfile[] lstimx = null;
        try {
            conn = getConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT COUNT(*) FROM " + nombre_lista);
            if (!rs.next())
                return null;
            lstimx = new ListaImaxfile[rs.getInt(1)];
            rs = stmnt.executeQuery("SELECT * FROM " + nombre_lista + " ORDER BY 1");
            for (int i = 0; rs.next(); i++) {
                lstimx[i] = new ListaImaxfile(rs.getString("consecutivo"), rs.getString("valor"));
            }
        } catch (SQLException se) {
            System.err.println("[" + this.getClass().getName() + "] Error SQL");
            se.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (rsInfoCat != null)
                    rsInfoCat.close();
                if (stmnt != null)
                    stmnt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                System.err.println("[" + this.getClass().getName() + "] Error SQL cerrando");
                se.printStackTrace();
            }
            rs = null;
            rsInfoCat = null;
            stmnt = null;
            conn = null;
        }
        return lstimx;
    }
}
