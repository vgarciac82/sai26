package com.syc.sai.procesosAutomaticos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.interfaces.CFDIBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class NotificaREPPendienteMasivo {

    private final Connection conn;

    private final StringBuilder query = new StringBuilder("SELECT DISTINCT rfc FROM v_treciboelectronico_pagofactura WHERE ncomprobado = 0 ");

    private static final Logger log = LoggerFactory.getLogger(NotificaREPPendienteMasivo.class);

    private final CFDIBusinessLogic cfdiBusinessLogic;

    public static void main(String[] args) {
        NotificaREPPendienteMasivo notifier = null;
        try {
            notifier = new NotificaREPPendienteMasivo();
            notifier.sendNotifications();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (notifier != null)
                notifier.close();
        }
    }

    private void close() {
        CloseObject.closeObject(getConnection());
    }

    private void sendNotifications() throws SQLException {
        List<String> toList = getProviders();
        for (String to : toList) {
            log.trace("Object: {}", "Enviando notificacion a : " + to);
            try {
                getCfdiBusinessLogic().notificaREPFaltantes(conn, null, null, to);
            } catch (Exception e) {
                log.error("Error occurred", "No fue posible notificar a : " + to + " debido al error: " + e);
            }
        }
    }

    private NotificaREPPendienteMasivo() throws Exception {
        conn = Util.getStandAloneConnection();
        cfdiBusinessLogic = new CFDIBusinessLogic();
    }

    private List<String> getProviders() throws SQLException {
        Statement stmnt = null;
        ResultSet rs = null;
        List<String> providers = new ArrayList<>();
        try {
            stmnt = getConnection().createStatement();
            rs = stmnt.executeQuery(getQuery().toString());
            while (rs.next()) {
                providers.add(rs.getString(1));
            }
            return providers;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }

    private Connection getConnection() {
        return conn;
    }

    private StringBuilder getQuery() {
        return query;
    }

    public CFDIBusinessLogic getCfdiBusinessLogic() {
        return cfdiBusinessLogic;
    }
}
