package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class AdministracionMensajesManager {

    public static Map<Integer, String> casoGrupo = null;

    public AdministracionMensajesManager() {
        super();
    }

    public static synchronized void cargaCasoGrupo(Connection conn) throws Exception {
        if (AdministracionMensajesManager.casoGrupo == null) {
            PreparedStatement pstm = null;
            ResultSet rs = null;
            String sQuery = "select id_tc, g_nombre from tTipoCasoGrupo with(nolock)";
            try {
                pstm = conn.prepareStatement(sQuery);
                rs = pstm.executeQuery();
                casoGrupo = new HashMap<Integer, String>();
                while (rs.next()) {
                    casoGrupo.put(rs.getInt("id_tc"), rs.getString("g_nombre"));
                }
            } finally {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(pstm, false);
            }
        }
    }

    private static String[] getGrupo(int id_tc) {
        if (casoGrupo.get(id_tc) != null)
            return casoGrupo.get(id_tc).split(";");
        else
            return null;
    }

    private static String generaCondicionUsuarioGrupo(int id_tc, String u_login) {
        String[] grupos = getGrupo(id_tc);
        String condicion = "";
        if (grupos != null) {
            condicion = " u_login ='" + u_login + "' AND g_nombre in (";
            String token = "";
            for (int i = 0; i < grupos.length; i++) {
                condicion += token + "'" + grupos[i] + "'" + "," + "'" + "X" + grupos[i] + "X" + "'";
                token = ",";
            }
            condicion += ")";
        }
        return condicion;
    }

    public static ArrayList<String> UnidadesResponsables(Connection conn) throws SQLException {
        ArrayList arrmObtenDatos = new ArrayList();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String cClaveUnidad = "";
        String cDescUnidad = "";
        String sQuery = "SELECT cUnidadResponsable,cUnidadResponsable+' '+D_DESCRIPCION FROM tCatUnidadResponsable with (nolock)";
        try {
            pstm = conn.prepareStatement(sQuery);
            rs = pstm.executeQuery();
            while (rs.next()) {
                ArrayList<String> arrmODatos = new ArrayList<String>();
                cClaveUnidad = rs.getString(1);
                cDescUnidad = rs.getString(2);
                arrmODatos.add(cClaveUnidad);
                arrmODatos.add(cDescUnidad);
                arrmObtenDatos.add(arrmODatos);
                arrmODatos = null;
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

    public static List<String> UsuariosCC(Connection conn, String cc, String ur) throws SQLException {
        List<String> arrmObtenDatos = new ArrayList<String>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sQuery = "";
        String u_login = "";
        sQuery = "SELECT distinct (tbl.U_LOGIN) FROM ( SELECT ur.U_LOGIN, UR.UP_NOMBRE, UP_VALOR,admin_dueno FROM cg_usuario_propiedades ur with(nolock) INNER JOIN cg_usuario u with(nolock) ON ur.U_LOGIN = u.U_LOGIN and u.U_ESTATUS = 'A') AS tbl INNER JOIN cg_usuario_grupo ugrupo with(nolock) ON tbl.u_login = ugrupo.u_login WHERE tbl.up_valor = ? ";
        try {
            pstm = conn.prepareStatement(sQuery);
            pstm.setString(1, cc);
            rs = pstm.executeQuery();
            while (rs.next()) {
                u_login = rs.getString(1);
                arrmObtenDatos.add(u_login);
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

    public static boolean desactivaMensaje(Connection conn) throws SQLException {
        PreparedStatement pstm = null;
        boolean res = true;
        String sQuery = "";
        sQuery = "DELETE tmensajesalcance ";
        pstm = conn.prepareStatement(sQuery);
        pstm.execute();
        sQuery = "DELETE tmensajes ";
        pstm = conn.prepareStatement(sQuery);
        pstm.execute();
        res = pstm.getUpdateCount() != 0 ? true : false;
        try {
            conn.commit();
        } catch (Exception e) {
            res = false;
            conn.rollback();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
        }
        return res;
    }

    public static String mensajeInbox(Connection conn, String UR, String cCentroContable, String u_login) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String mensaje = "";
        String sQuery = "";
        sQuery = "SELECT mensaje FROM tMensajes m with (nolock) , tMensajesAlcance ma with (nolock) WHERE m.id_mensaje = ma.id_mensaje AND (Alcance = '" + UR + "'  OR Alcance= '" + cCentroContable + "' OR Alcance= '" + u_login + "' ) OR (Alcance='TOTAL') ";
        pstm = conn.prepareStatement(sQuery);
        rs = pstm.executeQuery();
        while (rs.next()) {
            mensaje = rs.getString(1);
        }
        try {
        } catch (Exception e) {
            mensaje = "";
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return mensaje;
    }

    public static boolean mensajeInboxAlerta(Connection conn, String UR, String cCentroContable, String u_login) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean alerta = false;
        String sQuery = "";
        try {
            sQuery = "SELECT count (mensaje) FROM tMensajes m with (nolock) , tMensajesAlcance ma with (nolock) WHERE m.id_mensaje = ma.id_mensaje AND (Alcance = '" + UR + "'  OR Alcance= '" + cCentroContable + "' OR Alcance= '" + u_login + "'  OR Alcance='TOTAL') AND alerta = 'S'";
            pstm = conn.prepareStatement(sQuery);
            rs = pstm.executeQuery();
            if (rs.next())
                alerta = rs.getInt(1) > 0;
            return alerta;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
    }

    public static boolean guardaMensaje(Connection conn, String mensajeAdmin, String ur, String cc, String activaAlerta) throws SQLException {
        PreparedStatement pstm = null;
        boolean res = true;
        String sQuery = "";
        try {
            sQuery = "INSERT INTO tMensajes (mensaje, ur, cc, alerta) values ( '" + mensajeAdmin + "' , " + " '" + ur + "' , '" + cc + "' , '" + activaAlerta + "')";
            pstm = conn.prepareStatement(sQuery);
            pstm.execute();
            res = pstm.getUpdateCount() != 0 ? true : false;
            conn.commit();
        } catch (Exception e) {
            res = false;
            conn.rollback();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
        }
        return res;
    }

    public static List<String> selecChecks(Connection conn) throws SQLException {
        List<String> arrmObtenDatos = new ArrayList<String>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sQuery = "";
        String opcion = "";
        sQuery = " select Alcance from tMensajesAlcance with(nolock) ";
        try {
            pstm = conn.prepareStatement(sQuery);
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

    public static boolean guardaAlcance(Connection conn, String mensajeAdmin, String ur, String cc, String idUsuario) throws SQLException {
        PreparedStatement pstm = null;
        boolean res = true;
        String sQuery = "";
        String sQuery1 = "";
        int id_mensaje = 0;
        try {
            if (idUsuario != "") {
                sQuery1 = "INSERT INTO tMensajesAlcance values ( (select max (id_mensaje) from tMensajes) , '" + idUsuario + "' )";
                pstm = conn.prepareStatement(sQuery1);
            } else if (!cc.equals("-1")) {
                sQuery1 = "INSERT INTO tMensajesAlcance values ( (select max (id_mensaje) from tMensajes) , '" + cc + "' )";
                pstm = conn.prepareStatement(sQuery1);
            } else {
                sQuery1 = "INSERT INTO tMensajesAlcance values ( (select max (id_mensaje) from tMensajes) , '" + ur + "' )";
                pstm = conn.prepareStatement(sQuery1);
            }
            pstm.execute();
            res = pstm.getUpdateCount() != 0 ? true : false;
            conn.commit();
        } catch (Exception e) {
            res = false;
            conn.rollback();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
        }
        return res;
    }

    public static boolean validaUsuario(Connection conn, String idUsuario) throws Exception {
        boolean existe = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sQuery = "select COUNT (*) from CG_USUARIO_GRUPO with(nolock) where U_LOGIN = ?";
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

    public static boolean validaUsuarioGrupo(Connection conn, String idUsuario, int tc) throws Exception {
        boolean existe = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sQuery = "select COUNT (*) from CG_USUARIO_GRUPO with(nolock) where ";
        try {
            cargaCasoGrupo(conn);
            String cond = AdministracionMensajesManager.generaCondicionUsuarioGrupo(tc, idUsuario);
            if (cond == null || "".equals(cond)) {
                throw new Exception("No existen grupos definidos para el tipo de tramite " + tc + " en la tabla tTipoCasoGrupo");
            }
            sQuery += cond;
            pstm = conn.prepareStatement(sQuery);
            rs = pstm.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
    }
}
