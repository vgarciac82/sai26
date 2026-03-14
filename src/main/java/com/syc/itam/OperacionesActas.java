package com.syc.itam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.syc.gestion.servlet.GestionServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class OperacionesActas {

    private DataSource ds = null;

    public static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionServlet.class);

    private String jniName = null;

    public OperacionesActas() {
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        Context initContext;
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(jniName);
        } catch (NamingException ne) {
            try {
                initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:comp/env");
                ds = (DataSource) envContext.lookup(jniName);
            } catch (NamingException nexc) {
                try {
                    initContext = new InitialContext();
                    ds = (DataSource) initContext.lookup(jniName);
                } catch (NamingException exc) {
                    ne.printStackTrace();
                    exc.printStackTrace();
                    throw new RuntimeException("No se encontro la fuente '" + jniName + "'");
                }
            }
        }
    }

    public int getEstatus(String titulo_aplicacion, int id_gabinete) throws SQLException {
        int retVal = 0;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            PreparedStatement prep = conn.prepareStatement("select estatus from cg_itam_actas where titulo_aplicacion=? and id_gabinete=?");
            prep.setString(1, titulo_aplicacion);
            prep.setInt(2, id_gabinete);
            ResultSet rs = prep.executeQuery();
            if (rs.next())
                retVal = rs.getInt("estatus");
            rs.close();
            prep.close();
        } finally {
            if (conn != null)
                conn.close();
        }
        return retVal;
    }
}
