package com.axtel.cfdi.stamp.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacInfo {

    private static final Logger log = LoggerFactory.getLogger(PacInfo.class);

    private String user;

    private String password;

    private String url;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PacInfo(Connection conn) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT PAC_USR, PAC_PWD, PAC_URL FROM cfdi_pac";
        log.trace("Starting PAC information retrieval process.");
        try {
            pst = conn.prepareStatement(query);
            log.debug("Object: {}", "Executing query to retrieve PAC info: " + query);
            rs = pst.executeQuery();
            if (rs.next()) {
                user = rs.getString("PAC_USR");
                password = rs.getString("PAC_PWD");
                url = rs.getString("PAC_URL");
                log.info("PAC information retrieved successfully.");
                log.debug("Object: {}", "PAC User: " + user);
                log.trace("Object: {}", "PAC URL: " + url);
            } else {
                log.warn("No PAC information found in database.");
            }
        } catch (SQLException e) {
            log.error("SQL Exception occurred while retrieving PAC info: " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e.getCause());
        } finally {
            log.trace("Closing ResultSet and PreparedStatement objects.");
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }
}
