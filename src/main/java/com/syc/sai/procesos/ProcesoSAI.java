package com.syc.sai.procesos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;

public class ProcesoSAI {

    public ProcesoSAI(String urlConn, String driverName, String user, String pass) {
        super();
        this.urlConn = urlConn;
        this.driverName = driverName;
        this.user = user;
        this.pass = pass;
    }

    public String getUrlConn() {
        return urlConn;
    }

    public void setUrlConn(String urlConn) {
        this.urlConn = urlConn;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    private String urlConn;

    private String driverName;

    private String user;

    private String pass;

    public Connection createConn() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName(getDriverName());
        conn = DriverManager.getConnection(getUrlConn(), getUser(), getPass());
        conn.setAutoCommit(false);
        return conn;
    }
}
