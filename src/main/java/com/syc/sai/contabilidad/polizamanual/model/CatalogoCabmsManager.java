package com.syc.sai.contabilidad.polizamanual.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.contabilidad.polizamanual.CatalogoCabms;
import com.syc.sai.contabilidad.polizamanual.CatalogoCabmsEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogoCabmsManager {

    private static Logger log = LoggerFactory.getLogger(CatalogoCabmsManager.class);

    public static CatalogoCabms readCatalogoCabms(Connection conn, String cCabms, String cCucop, String cPartida) {
        String restrictions = " cCabms = " + cCabms;
        restrictions += " AND cCucop=" + cCucop;
        restrictions += " AND cPartida=" + cPartida;
        List<CatalogoCabms> l;
        try {
            l = readCatalogoCabmsBy(conn, restrictions);
            if (!l.isEmpty()) {
                return l.get(0);
            } else {
                return null;
            }
        } catch (CatalogoCabmsEngineException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<CatalogoCabms> readCatalogoCabmsBy(Connection conn, String restrictions) throws CatalogoCabmsEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<CatalogoCabms> l = new ArrayList<CatalogoCabms>();
        try {
            String qry = "";
            qry = "select * from tCatalogoCabms where " + restrictions + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeCatalogoCabms(rs));
            }
            return l;
        } catch (Exception e) {
            throw new CatalogoCabmsEngineException(e);
        } finally {
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int saveCatalogoCabms(Connection conn, CatalogoCabms cabms) throws CatalogoCabmsEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        try {
            String qry = "INSERT INTO tCatalogoCabms (cdescripcion, ncuenta, nidUnidadMedida, ccabms, ccucop, cpartida) values( ?,?,?,?,?,? );";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, cabms.getCdescripcion());
            pStatement.setString(cnt++, cabms.getNcuenta());
            pStatement.setString(cnt++, cabms.getNidUnidadMedida());
            pStatement.setString(cnt++, cabms.getCcabms());
            pStatement.setInt(cnt++, cabms.getCcucop());
            pStatement.setString(cnt++, cabms.getCpartida());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new CatalogoCabmsEngineException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int updateCatalogoCabms(Connection conn, CatalogoCabms cabms) throws CatalogoCabmsEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        try {
            String qry = "UPDATE tCatalogoCabms SET cdescripcion=?, ncuenta=?, nidUnidadMedida=?, ccabms=?, ccucop=?, cpartida=?;";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, cabms.getCdescripcion());
            pStatement.setString(cnt++, cabms.getNcuenta());
            pStatement.setString(cnt++, cabms.getNidUnidadMedida());
            pStatement.setString(cnt++, cabms.getCcabms());
            pStatement.setInt(cnt++, cabms.getCcucop());
            pStatement.setString(cnt++, cabms.getCpartida());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new CatalogoCabmsEngineException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int saveOrUpdateCatalogoCabms(Connection conn, CatalogoCabms cabms) throws CatalogoCabmsEngineException {
        //		if(readCatalogoCabms(conn,cabms.getID, cabms.getID).isEmpty()){
        //			return saveCatalogoCabms(conn,cabms);
        //		}else{
        //			return updateCatalogoCabms(conn, cabms);
        //		}
        return 0;
    }

    private static CatalogoCabms extraeCatalogoCabms(ResultSet rs) throws CatalogoCabmsEngineException {
        try {
            CatalogoCabms pojo = new CatalogoCabms();
            pojo.setCdescripcion(rs.getString("cdescripcion"));
            pojo.setNcuenta(rs.getString("ncuenta"));
            pojo.setNidUnidadMedida(rs.getString("nidUnidadMedida"));
            pojo.setCcabms(rs.getString("ccabms"));
            pojo.setCcucop(rs.getInt("ccucop"));
            pojo.setCpartida(rs.getString("cpartida"));
            return pojo;
        } catch (Exception e) {
            throw new CatalogoCabmsEngineException(e);
        }
    }
}
