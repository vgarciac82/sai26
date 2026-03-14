package com.syc.adquisiciones.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletException;
import com.syc.adquisiciones.vo.ConexionesBD;
import com.syc.gestion.documental.CatalogosManager;

public class UnificacionUsuariosManager {

    public static int insertaRol(String queryRol, String queryOpcionRol, String rol) throws SQLException {
        int val = -1;
        PreparedStatement ps = null;
        Connection conn = null;
        ComparativaBusinessLogic cbl = new ComparativaBusinessLogic("jdbc/gestion");
        List<ConexionesBD> conexiones = null;
        ConexionesBD cbd = null;
        try {
            conn = cbl.getConnection();
            conexiones = CatalogosManager.getBasesDeDatos(conn, "SELECT * FROM TEJERCICIOFISCAL");
        } catch (Exception e) {
            conn.rollback();
        } finally {
            if (ps != null)
                ps.close();
            if (conn != null)
                conn.close();
            ps = null;
            conn = null;
        }
        Iterator<ConexionesBD> ite = conexiones.iterator();
        while (ite.hasNext()) {
            cbd = ite.next();
            try {
                conn = getConectionCatalogo(cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD());
                ps = conn.prepareStatement(queryRol);
                val = ps.executeUpdate();
                ps = conn.prepareStatement(queryOpcionRol);
                val = ps.executeUpdate();
                //OPCIONES DE CERRAR SESION
                //ps = conn.prepareStatement("insert into CG_ROLE_OPCION (R_NOMBRE, ID_OPCION)   (select '"+rol+"' ,[ID_OPCION] FROM CG_OPCION WHERE ID_OPCION IN (67,122))");
                //val=ps.executeUpdate();
                conn.commit();
            } catch (SQLException exc) {
                conn.rollback();
                exc.printStackTrace();
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
        return val;
    }

    private static Connection getConectionCatalogo(String server, String port, String bd, String user, String pass) {
        Connection conn = null;
        String url = null;
        try {
            url = "jdbc:jtds:sqlserver://" + server + ":" + port + "/" + bd;
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
