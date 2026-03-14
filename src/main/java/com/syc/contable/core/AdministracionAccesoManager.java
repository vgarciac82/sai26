package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.record.FormulaRecord;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class AdministracionAccesoManager {

    public static Map<Integer, String> casoGrupo = null;

    public AdministracionAccesoManager() {
        super();
    }

    public static List<String> opcionDel(Connection conn) throws SQLException {
        List<String> arrmObtenDatos = new ArrayList<String>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sQuery = "";
        String opcion = "";
        sQuery = " SELECT ID_OPCION, O_DESCRIPCION FROM CG_OPCION WHERE O_DELEGABLE ='S' ";
        try {
            pstm = conn.prepareStatement(sQuery);
            rs = pstm.executeQuery();
            while (rs.next()) {
                opcion = rs.getInt(1) + "|" + rs.getString(2).replaceAll("ñ", "n").replaceAll("Ñ", "N").replaceAll("-", "").replace((char) 160, (char) 32).replaceAll("Á", "A").replaceAll("É", "E").replaceAll("Í", "I").replaceAll("Ó", "O").replaceAll("Ú", "U").replaceAll("á", "a").replaceAll("é", "e").replaceAll("í", "i").replaceAll("ó", "o").replaceAll("ú", "u").trim();
                arrmObtenDatos.add(opcion);
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return arrmObtenDatos;
    }

    public static List<String> selecChecks(Connection conn, String listadoUsuario) throws SQLException {
        List<String> arrmObtenDatos = new ArrayList<String>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sQuery = "";
        String opcion = "";
        sQuery = " select id_opcion from cg_usuario_opcion_delegada where u_login = ? ";
        try {
            pstm = conn.prepareStatement(sQuery);
            pstm.setString(1, listadoUsuario);
            rs = pstm.executeQuery();
            while (rs.next()) {
                opcion = rs.getString(1);
                arrmObtenDatos.add(opcion);
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return arrmObtenDatos;
    }

    public static boolean validaUsuario(Connection conn, String idUsuario) throws Exception {
        boolean existe = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sQuery = "select COUNT (*) from CG_USUARIO_GRUPO where U_LOGIN = ?";
        try {
            pstm = conn.prepareStatement(sQuery);
            pstm.setString(1, idUsuario);
            rs = pstm.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
    }

    public static boolean asignar(Connection conn, String listadoUsuario, String opcion) throws SQLException {
        PreparedStatement pstm = null;
        boolean res = true;
        try {
            String sQuery = "";
            if (listadoUsuario != null || listadoUsuario != "") {
                sQuery = " INSERT cg_usuario_opcion_delegada values (" + "'" + listadoUsuario + "'" + "," + opcion + ")";
                pstm = conn.prepareStatement(sQuery);
                pstm.execute();
                res = pstm.getUpdateCount() != 0 ? true : false;
            }
            conn.commit();
        } catch (Exception e) {
            res = false;
            conn.rollback();
        }
        return res;
    }

    public static boolean desasignar(Connection conn, String listadoUsuario) throws SQLException {
        PreparedStatement pstm = null;
        boolean res = true;
        try {
            String sQuery = "";
            if (listadoUsuario != null || listadoUsuario != "") {
                sQuery = " delete cg_usuario_opcion_delegada where u_login = " + "'" + listadoUsuario + "'";
                pstm = conn.prepareStatement(sQuery);
                pstm.execute();
                res = pstm.getUpdateCount() != 0 ? true : false;
            }
            conn.commit();
        } catch (Exception e) {
            res = false;
            conn.rollback();
        }
        return res;
    }
}
