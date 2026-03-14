package com.syc.sai.fonden.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.fonden.FondenEngineException;
import com.syc.sai.fonden.FondenMoneda;
import com.syc.sai.fonden.FondenTipoFactura;
import com.syc.sai.fonden.FondenTipoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FondenCatalogsManager {

    private static Logger log = LoggerFactory.getLogger(FondenCatalogsManager.class);

    public static List readCatalog(Connection conn, String tableName) throws FondenEngineException {
        return readCatalog(conn, tableName, null);
    }

    public static List readCatalog(Connection conn, String tableName, String restrictions) throws FondenEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List l = new ArrayList();
        try {
            String qry = "select * from " + tableName;
            if (restrictions != null) {
                qry += "where " + restrictions + ";";
            } else {
                qry += ";";
            }
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                if (tableName.toUpperCase().equals("tFondenTipoFactura".toUpperCase())) {
                    l.add(extraeFondenTipoFactura(rs));
                } else if (tableName.toUpperCase().equals("tFondenTipoPago".toUpperCase())) {
                    l.add(extraeFondenTipoPago(rs));
                } else if (tableName.toUpperCase().equals("tFondenMoneda".toUpperCase())) {
                    l.add(extraeFondenMoneda(rs));
                }
            }
            return l;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        } finally {
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    private static FondenTipoFactura extraeFondenTipoFactura(ResultSet rs) throws FondenEngineException {
        try {
            FondenTipoFactura pojo = new FondenTipoFactura();
            pojo.setNidTipoFactura(rs.getInt("nidTipoFactura"));
            pojo.setCdescripcion(rs.getString("cdescripcion"));
            return pojo;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        }
    }

    private static FondenTipoPago extraeFondenTipoPago(ResultSet rs) throws FondenEngineException {
        try {
            FondenTipoPago pojo = new FondenTipoPago();
            pojo.setNidTipoPago(rs.getInt("nidTipoPago"));
            pojo.setCdescripcion(rs.getString("cdescripcion"));
            return pojo;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        }
    }

    private static FondenMoneda extraeFondenMoneda(ResultSet rs) throws FondenEngineException {
        try {
            FondenMoneda pojo = new FondenMoneda();
            pojo.setCidMoneda(rs.getString("cidMoneda"));
            pojo.setCdescripcion(rs.getString("cdescripcion"));
            pojo.setNtipoCambio(rs.getDouble("ntipoCambio"));
            return pojo;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        }
    }
}
