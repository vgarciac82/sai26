package com.syc.sai.fonden.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.fonden.FondenEngineException;
import com.syc.sai.fonden.FondenFacturacion;
import com.syc.sai.fonden.FondenFacturacionEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FondenFacturacionManager {

    private static Logger log = LoggerFactory.getLogger(FondenMovimientoManager.class);

    public static FondenFacturacion readFondenFacturacion(Connection conn, Integer cIdFonden, Integer nIdFondenMovimiento, Integer nIdFondenFacturacion) throws FondenFacturacionEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenFacturacion> l = new ArrayList<FondenFacturacion>();
        try {
            String qry = "SELECT * FROM tFondenFacturacion where cIdFonden = " + cIdFonden + " AND nIdFondenMovimiento= " + nIdFondenMovimiento + " AND nIdFondenFacturacion = " + nIdFondenFacturacion + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeFondenFacturacion(rs));
            }
            if (!l.isEmpty()) {
                return l.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new FondenFacturacionEngineException(e);
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

    public static Integer newIdFondenFacturacion(Connection conn, Integer cIdFonden, Integer nIdFondenMovimiento) throws FondenFacturacionEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenFacturacion> l = new ArrayList<FondenFacturacion>();
        try {
            String qry = "SELECT MAX(nidFondenFacturacion) FROM tFondenFacturacion where cIdFonden = " + cIdFonden + " AND nIdFondenMovimiento= " + nIdFondenMovimiento + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            Integer id = 0;
            while (rs.next()) {
                id = rs.getInt(1);
            }
            id++;
            return id;
        } catch (Exception e) {
            throw new FondenFacturacionEngineException(e);
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

    public static int saveFondenFacturacion(Connection conn, FondenFacturacion fondenFacturacion) throws FondenFacturacionEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenFacturacion> l = new ArrayList<FondenFacturacion>();
        try {
            String qry = "INSERT INTO tFondenFacturacion (cidFonden, nidFondenMovimiento, nidFondenFacturacion, cnumero, ncantidad, nimporteFactura,nTipoCambio, nIdTipoPago, nIdTipoFactura, cDescripcionFactura) values( ?,?,?,?,?,?,?,?,?,? );";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setInt(cnt++, fondenFacturacion.getCidFonden());
            pStatement.setInt(cnt++, fondenFacturacion.getNidFondenMovimiento());
            pStatement.setInt(cnt++, fondenFacturacion.getNidFondenFacturacion());
            pStatement.setString(cnt++, fondenFacturacion.getCnumero());
            pStatement.setLong(cnt++, fondenFacturacion.getNcantidad());
            pStatement.setDouble(cnt++, fondenFacturacion.getNimporteFactura());
            pStatement.setDouble(cnt++, fondenFacturacion.getnTipoCambio());
            pStatement.setInt(cnt++, fondenFacturacion.getnIdTipoPago());
            pStatement.setInt(cnt++, fondenFacturacion.getnIdTipoFactura());
            pStatement.setString(cnt++, fondenFacturacion.getcDescripcionFactura());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new FondenFacturacionEngineException(e);
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

    public static int updateFondenFacturacion(Connection conn, FondenFacturacion fondenFacturacion) throws FondenFacturacionEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenFacturacion> l = new ArrayList<FondenFacturacion>();
        try {
            String qry = "UPDATE tFondenFacturacion SET cnumero=?, ncantidad=?, nimporteFactura=?, nTipoCambio=?, nIdTipoPago=?, nIdTipoFactura=?, cDescripcionFactura=? where cidFonden=? AND nidFondenMovimiento=? AND nidFondenFacturacion=? ;";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, fondenFacturacion.getCnumero());
            pStatement.setLong(cnt++, fondenFacturacion.getNcantidad());
            pStatement.setDouble(cnt++, fondenFacturacion.getNimporteFactura());
            pStatement.setDouble(cnt++, fondenFacturacion.getnTipoCambio());
            pStatement.setInt(cnt++, fondenFacturacion.getnIdTipoPago());
            pStatement.setInt(cnt++, fondenFacturacion.getnIdTipoFactura());
            pStatement.setString(cnt++, fondenFacturacion.getcDescripcionFactura());
            pStatement.setInt(cnt++, fondenFacturacion.getCidFonden());
            pStatement.setInt(cnt++, fondenFacturacion.getNidFondenMovimiento());
            pStatement.setInt(cnt++, fondenFacturacion.getNidFondenFacturacion());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new FondenFacturacionEngineException(e);
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

    public static int saveOrUpdateFondenFacturacion(Connection conn, FondenFacturacion fondenFacturacion) throws FondenFacturacionEngineException {
        if (readFondenFacturacion(conn, fondenFacturacion.getCidFonden(), fondenFacturacion.getNidFondenMovimiento(), fondenFacturacion.getNidFondenFacturacion()) == null) {
            return saveFondenFacturacion(conn, fondenFacturacion);
        } else {
            return updateFondenFacturacion(conn, fondenFacturacion);
        }
    }

    private static FondenFacturacion extraeFondenFacturacion(ResultSet rs) throws FondenEngineException {
        try {
            FondenFacturacion pojo = new FondenFacturacion();
            pojo.setCnumero(rs.getString("cnumero"));
            pojo.setNcantidad(rs.getLong("ncantidad"));
            pojo.setNimporteFactura(rs.getDouble("nimporteFactura"));
            pojo.setCidFonden(rs.getInt("cidFonden"));
            pojo.setNidFondenMovimiento(rs.getInt("nidFondenMovimiento"));
            pojo.setNidFondenFacturacion(rs.getInt("nidFondenFacturacion"));
            pojo.setnTipoCambio(rs.getDouble("nTipoCambio"));
            pojo.setnIdTipoPago(rs.getInt("nIdTipoPago"));
            pojo.setnIdTipoFactura(rs.getInt("nIdTipoFactura"));
            pojo.setcDescripcionFactura(rs.getString("cDescripcionFactura"));
            return pojo;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        }
    }
}
