package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.syc.crud.dsmngr.DataSourceManager;
import java.util.Base64;

public class OperacionManager {

    public static int delete(Connection conn, int id_tc, int id_oper) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_operacion with(rowlock) WHERE id_tc = ? AND id_oper = ?");
            pstmnt.setInt(1, id_tc);
            pstmnt.setInt(2, id_oper);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, Operacion o) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_operacion " + "(id_tc, id_oper, o_numero, o_nombre, o_responsable" + ", o_descripcion, o_plantilla, o_tiempo_limite, o_alarma, o_post_display" + ", o_post_submit, o_on_load, o_on_submit, o_folder_docto) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmnt.setInt(1, o.getIdTC());
            pstmnt.setInt(2, o.getIdOperacion());
            pstmnt.setInt(3, o.getNumero());
            pstmnt.setString(4, o.getNombre());
            pstmnt.setString(5, o.getResponsable());
            pstmnt.setString(6, o.getDescripcion());
            pstmnt.setString(7, o.getPlantilla());
            pstmnt.setInt(8, o.getTiempoLimite());
            pstmnt.setString(9, o.getAlarma());
            pstmnt.setString(10, o.getPostDisplay());
            pstmnt.setString(11, o.getPostSubmit());
            pstmnt.setString(12, o.getOnLoad());
            pstmnt.setString(13, o.getOnSubmit());
            pstmnt.setString(14, o.getFolderDocto());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Operacion select(Connection conn, Operacion o) throws SQLException {
        Operacion ro = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (o.getIdTC() > 0) {
                where.append(token + "id_tc = ?");
                token = " AND ";
            }
            if (o.getIdOperacion() > 0) {
                where.append(token + "id_oper = ?");
                token = " AND ";
            }
            if (o.getNumero() > 0) {
                where.append(token + "o_numero = ?");
                token = " AND ";
            }
            if (o.getNombre() != null) {
                where.append(token + "o_nombre = ?");
                token = " AND ";
            }
            if (o.getResponsable() != null) {
                where.append(token + "o_responsable = ?");
                token = " AND ";
            }
            if (o.getDescripcion() != null) {
                where.append(token + "o_descripcion = ?");
                token = " AND ";
            }
            if (o.getPlantilla() != null) {
                where.append(token + "o_plantilla = ?");
                token = " AND ";
            }
            if (o.getTiempoLimite() > 0) {
                where.append(token + "o_tiempo_limite = ?");
                token = " AND ";
            }
            if (o.getAlarma() != null) {
                where.append(token + "o_alarma = ?");
                token = " AND ";
            }
            if (o.getPostDisplay() != null) {
                where.append(token + "o_post_display = ?");
                token = " AND ";
            }
            if (o.getPostSubmit() != null) {
                where.append(token + "o_post_submit = ?");
                token = " AND ";
            }
            if (o.getOnLoad() != null) {
                where.append(token + "o_on_load = ?");
                token = " AND ";
            }
            if (o.getOnSubmit() != null) {
                where.append(token + "o_on_submit = ?");
                token = " AND ";
            }
            if (o.getFolderDocto() != null) {
                where.append(token + "o_folder_docto = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_operacion with(nolock) " + where.toString());
            int i = 1;
            if (o.getIdTC() > 0)
                pstmnt.setInt(i++, o.getIdTC());
            if (o.getIdOperacion() > 0)
                pstmnt.setInt(i++, o.getIdOperacion());
            if (o.getNumero() > 0)
                pstmnt.setInt(i++, o.getNumero());
            if (o.getNombre() != null)
                pstmnt.setString(i++, o.getNombre());
            if (o.getResponsable() != null)
                pstmnt.setString(i++, o.getResponsable());
            if (o.getDescripcion() != null)
                pstmnt.setString(i++, o.getDescripcion());
            if (o.getPlantilla() != null)
                pstmnt.setString(i++, o.getPlantilla());
            if (o.getTiempoLimite() > 0)
                pstmnt.setInt(i++, o.getTiempoLimite());
            if (o.getAlarma() != null)
                pstmnt.setString(i++, o.getAlarma());
            if (o.getPostDisplay() != null)
                pstmnt.setString(i++, o.getPostDisplay());
            if (o.getPostSubmit() != null)
                pstmnt.setString(i++, o.getPostSubmit());
            if (o.getOnLoad() != null)
                pstmnt.setString(i++, o.getOnLoad());
            if (o.getOnSubmit() != null)
                pstmnt.setString(i++, o.getOnSubmit());
            if (o.getFolderDocto() != null)
                pstmnt.setString(i++, o.getFolderDocto());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                ro = new Operacion();
                ro.setIdTC(rs.getInt("id_tc"));
                ro.setIdOperacion(rs.getInt("id_oper"));
                ro.setNumero(rs.getInt("o_numero"));
                ro.setNombre(rs.getString("o_nombre"));
                ro.setResponsable(rs.getString("o_responsable"));
                ro.setDescripcion(rs.getString("o_descripcion"));
                ro.setPlantilla(rs.getString("o_plantilla"));
                ro.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                ro.setAlarma(rs.getString("o_alarma"));
                ro.setPostDisplay(rs.getString("o_post_display"));
                ro.setPostSubmit(rs.getString("o_post_submit"));
                ro.setOnLoad(rs.getString("o_on_load"));
                ro.setOnSubmit(rs.getString("o_on_submit"));
                ro.setFolderDocto(rs.getString("o_folder_docto"));
                ro.setOnCancel(rs.getString("o_on_cancel"));
                OperacionSiguiente os = new OperacionSiguiente();
                os.setIdTC(ro.getIdTC());
                os.setIdOperacion(ro.getIdOperacion());
                ro.setOperacionSgte(OperacionSiguienteManager.select(conn, os));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return ro;
    }

    public static Operacion primeraOperacion(Connection conn, int id_tc) throws SQLException {
        Operacion ro = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_operacion with(nolock) " + "WHERE id_tc = ? " + " AND o_numero = (SELECT MIN(o_numero) FROM cg_operacion with(nolock) WHERE id_tc = ?)");
            pstmnt.setInt(1, id_tc);
            pstmnt.setInt(2, id_tc);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                ro = new Operacion();
                ro.setIdTC(rs.getInt("id_tc"));
                ro.setIdOperacion(rs.getInt("id_oper"));
                ro.setNumero(rs.getInt("o_numero"));
                ro.setNombre(rs.getString("o_nombre"));
                ro.setResponsable(rs.getString("o_responsable"));
                ro.setDescripcion(rs.getString("o_descripcion"));
                ro.setPlantilla(rs.getString("o_plantilla"));
                ro.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                ro.setAlarma(rs.getString("o_alarma"));
                ro.setPostDisplay(rs.getString("o_post_display"));
                ro.setPostSubmit(rs.getString("o_post_submit"));
                ro.setOnLoad(rs.getString("o_on_load"));
                ro.setOnSubmit(rs.getString("o_on_submit"));
                ro.setFolderDocto(rs.getString("o_folder_docto"));
                OperacionSiguiente os = new OperacionSiguiente();
                os.setIdTC(ro.getIdTC());
                os.setIdOperacion(ro.getIdOperacion());
                ro.setOperacionSgte(OperacionSiguienteManager.select(conn, os));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return ro;
    }

    public static int update(Connection conn, Operacion o) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_operacion with(rowlock) SET o_numero = ?, o_nombre = ?" + ", o_responsable = ?, o_descripcion = ?, o_plantilla = ?, o_tiempo_limite = ?" + ", o_alarma = ?, o_post_display = ?, o_post_submit = ?, o_on_load = ?" + ", o_on_submit = ?, o_folder_docto = ? WHERE id_tc = ? AND id_oper = ?");
            pstmnt.setInt(1, o.getNumero());
            pstmnt.setString(2, o.getNombre());
            pstmnt.setString(3, o.getResponsable());
            pstmnt.setString(4, o.getDescripcion());
            pstmnt.setString(5, o.getPlantilla());
            pstmnt.setInt(6, o.getTiempoLimite());
            pstmnt.setString(7, o.getAlarma());
            pstmnt.setString(8, o.getPostDisplay());
            pstmnt.setString(9, o.getPostSubmit());
            pstmnt.setString(10, o.getOnLoad());
            pstmnt.setString(11, o.getOnSubmit());
            pstmnt.setString(12, o.getFolderDocto());
            pstmnt.setInt(13, o.getIdTC());
            pstmnt.setInt(14, o.getIdOperacion());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static List selectForUsuario(Connection conn, int id_tc, String u_login) throws SQLException {
        List operList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT DISTINCT o.* FROM cg_operacion o with(nolock), cg_usuario_grupo ug with(nolock) " + "WHERE (o.o_responsable = ug.u_login OR ug.g_nombre LIKE '%' + o.o_responsable ) " + "AND o.id_tc = ? AND ug.u_login = ?");
            pstmnt.setInt(1, id_tc);
            pstmnt.setString(2, u_login);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Operacion ro = new Operacion();
                ro.setIdTC(rs.getInt("id_tc"));
                ro.setIdOperacion(rs.getInt("id_oper"));
                ro.setNumero(rs.getInt("o_numero"));
                ro.setNombre(rs.getString("o_nombre"));
                ro.setResponsable(rs.getString("o_responsable"));
                ro.setDescripcion(rs.getString("o_descripcion"));
                ro.setPlantilla(rs.getString("o_plantilla"));
                ro.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                ro.setAlarma(rs.getString("o_alarma"));
                ro.setPostDisplay(rs.getString("o_post_display"));
                ro.setPostSubmit(rs.getString("o_post_submit"));
                ro.setOnLoad(rs.getString("o_on_load"));
                ro.setOnSubmit(rs.getString("o_on_submit"));
                ro.setFolderDocto(rs.getString("o_folder_docto"));
                OperacionSiguiente os = new OperacionSiguiente();
                os.setIdTC(ro.getIdTC());
                os.setIdOperacion(ro.getIdOperacion());
                ro.setOperacionSgte(OperacionSiguienteManager.select(conn, os));
                operList.add(ro);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return operList;
    }

    public static List selectForGrupo(Connection conn, int id_tc, String g_nombre) throws SQLException {
        List operList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT DISTINCT o.* FROM cg_operacion o with(nolock), cg_usuario_grupo ug with(nolock) " + "WHERE ug.g_nombre LIKE '%' + o.o_responsable AND o.id_tc = ? AND ug.g_nombre = ?");
            pstmnt.setInt(1, id_tc);
            pstmnt.setString(2, g_nombre);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Operacion ro = new Operacion();
                ro.setIdTC(rs.getInt("id_tc"));
                ro.setIdOperacion(rs.getInt("id_oper"));
                ro.setNumero(rs.getInt("o_numero"));
                ro.setNombre(rs.getString("o_nombre"));
                ro.setResponsable(rs.getString("o_responsable"));
                ro.setDescripcion(rs.getString("o_descripcion"));
                ro.setPlantilla(rs.getString("o_plantilla"));
                ro.setTiempoLimite(rs.getInt("o_tiempo_limite"));
                ro.setAlarma(rs.getString("o_alarma"));
                ro.setPostDisplay(rs.getString("o_post_display"));
                ro.setPostSubmit(rs.getString("o_post_submit"));
                ro.setOnLoad(rs.getString("o_on_load"));
                ro.setOnSubmit(rs.getString("o_on_submit"));
                ro.setFolderDocto(rs.getString("o_folder_docto"));
                OperacionSiguiente os = new OperacionSiguiente();
                os.setIdTC(ro.getIdTC());
                os.setIdOperacion(ro.getIdOperacion());
                ro.setOperacionSgte(OperacionSiguienteManager.select(conn, os));
                operList.add(ro);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return operList;
    }

    public static int getNextId(Connection conn, int id_tc) throws SQLException {
        int id = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT MAX(id_oper) FROM cg_operacion with(nolock) WHERE id_tc = ?");
            pstmnt.setInt(1, id_tc);
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

    public static boolean existenOperaciones(Connection conn, int id_tc, int id_oper) throws SQLException {
        boolean retVal = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT COUNT(id_oper) FROM cg_caso_operacion with(nolock) " + "WHERE id_tc = ? AND id_oper = ?");
            pstmnt.setInt(1, id_tc);
            pstmnt.setInt(2, id_oper);
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

    public boolean endOperacion(Caso caso) {
        return endOperacion(caso, null);
    }

    public boolean endOperacion(Caso caso, Connection connection) {
        int id = 0;
        PreparedStatement pstmnt = null;
        Connection conn = null;
        ResultSet rs = null;
        boolean status = true;
        try {
            if (connection != null)
                conn = connection;
            else
                conn = DataSourceManager.getConnection("jdbc/gestion");
            pstmnt = conn.prepareStatement("SELECT MAX(id_oper) FROM cg_operacion with(nolock) WHERE id_tc = ?");
            pstmnt.setInt(1, caso.getIdTC());
            rs = pstmnt.executeQuery();
            rs.next();
            id = rs.getInt(1);
            pstmnt = conn.prepareStatement(" update CG_CASO_OPERACION set ID_OPER=CO.ID_OPER, CO_RESPONSABLE=CO.O_RESPONSABLE  from CG_OPERACION CO with(nolock) " + " where CO.ID_OPER=? and  CO.ID_TC=?  and ID_CASO=?");
            pstmnt.setInt(1, id);
            pstmnt.setInt(2, caso.getIdTC());
            pstmnt.setInt(3, caso.getIdCaso());
            pstmnt.execute();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        } finally {
            try {
                if (connection == null && conn != null)
                    conn.close();
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
            } catch (Exception e) {
                ;
            }
            pstmnt = null;
            rs = null;
            return status;
        }
    }
}
