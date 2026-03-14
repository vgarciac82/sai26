package com.syc.sai.fonden.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.fonden.Fonden;
import com.syc.sai.fonden.FondenEngineException;
import com.syc.sai.fonden.FondenMovimiento;
import com.syc.sai.fonden.FondenMovimientoEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FondenMovimientoManager {

    private static Logger log = LoggerFactory.getLogger(FondenMovimientoManager.class);

    public static List<FondenMovimiento> readFondenMovimiento(Connection conn, Integer cidFonde, Integer nIdFondenMovimiento) throws FondenMovimientoEngineException {
        return readFondenMovimiento(conn, cidFonde, nIdFondenMovimiento, null);
    }

    public static List<FondenMovimiento> readFondenMovimiento(Connection conn, String restrictions) throws FondenMovimientoEngineException {
        return readFondenMovimiento(conn, null, null, restrictions);
    }

    public static List<FondenMovimiento> readFondenMovimiento(Connection conn, Integer cidFonde, Integer nIdFondenMovimiento, String restrictions) throws FondenMovimientoEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenMovimiento> l = new ArrayList<FondenMovimiento>();
        try {
            String qry = "";
            if (restrictions != null) {
                qry = "select * from tFondenMovimiento where " + restrictions + ";";
            } else {
                qry = "select * from tFondenMovimiento where cidFonden = " + cidFonde + " and nIdFondenMovimiento=" + nIdFondenMovimiento + ";";
            }
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeFondenMovimiento(rs));
            }
            return l;
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
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

    public static boolean validateNoGreaterThanImporteAnual(Connection conn, Integer cidFonden, Double nTotal) throws FondenMovimientoEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenMovimiento> l = new ArrayList<FondenMovimiento>();
        boolean result = false;
        try {
            String qry = "select ((select nImporteAnual from tFonden where cIdFonden=" + cidFonden + ") - ISNULL(SUM(fm.nTotal),0)) as SUMA from tFondenMovimiento fm where cidFonden=" + cidFonden + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                Double suma = rs.getDouble("SUMA");
                result = nTotal <= suma;
            }
            return result;
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
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

    public static FondenMovimiento readFondenMovimientoByFolio(Connection conn, String cFolio) throws FondenMovimientoEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenMovimiento> l = new ArrayList<FondenMovimiento>();
        try {
            String qry = "select * from tFondenMovimiento where cfolio = '" + cFolio + "';";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeFondenMovimiento(rs));
            }
            if (!l.isEmpty()) {
                return l.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
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

    public static List<FondenMovimiento> readFondenMovimientoBy(Connection conn, String conditions) throws FondenMovimientoEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenMovimiento> l = new ArrayList<FondenMovimiento>();
        try {
            String qry = "select * from tFondenMovimiento where " + conditions + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeFondenMovimiento(rs));
            }
            return l;
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
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

    public static int saveFondenMovimiento(Connection conn, FondenMovimiento fondenMovimiento) throws FondenMovimientoEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenMovimiento> l = new ArrayList<FondenMovimiento>();
        try {
            String qry = "INSERT INTO tFondenMovimiento (cidFonden, nidFondenMovimiento, cconcepto, nprecioUnitario, ncantidad, nimporte, crfc, cnumPedido, nnetoPedido, nprecio, ntotal, cproveedor, activo, id_Gabinete, cFolio, cCentroContable, cUnidadResponsable, nNumCaso, nCantidadTotal, nTechoDef, nTipoCambio, cIdMoneda, nIdFondenMovEstatus) values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? );";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setInt(cnt++, fondenMovimiento.getCidFonden());
            pStatement.setInt(cnt++, fondenMovimiento.getNidFondenMovimiento());
            pStatement.setString(cnt++, fondenMovimiento.getCconcepto());
            pStatement.setDouble(cnt++, fondenMovimiento.getNprecioUnitario());
            pStatement.setInt(cnt++, fondenMovimiento.getNcantidad());
            pStatement.setDouble(cnt++, fondenMovimiento.getNimporte());
            pStatement.setString(cnt++, fondenMovimiento.getCrfc());
            pStatement.setString(cnt++, fondenMovimiento.getCnumPedido());
            pStatement.setDouble(cnt++, fondenMovimiento.getNnetoPedido());
            pStatement.setDouble(cnt++, fondenMovimiento.getNprecio());
            pStatement.setDouble(cnt++, fondenMovimiento.getNtotal());
            pStatement.setString(cnt++, fondenMovimiento.getCproveedor());
            pStatement.setString(cnt++, fondenMovimiento.getActivo());
            pStatement.setInt(cnt++, fondenMovimiento.getIdGabinete());
            pStatement.setString(cnt++, fondenMovimiento.getcFolio());
            pStatement.setString(cnt++, fondenMovimiento.getcCentroContable());
            pStatement.setString(cnt++, fondenMovimiento.getcUnidadResponsable());
            pStatement.setInt(cnt++, fondenMovimiento.getnNumCaso());
            pStatement.setInt(cnt++, fondenMovimiento.getnCantidadTotal());
            pStatement.setDouble(cnt++, fondenMovimiento.getnTechoDef());
            pStatement.setDouble(cnt++, fondenMovimiento.getnTipoCambio());
            pStatement.setString(cnt++, fondenMovimiento.getcIdMoneda());
            pStatement.setInt(cnt++, fondenMovimiento.getnIdFondenMovEstatus());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
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

    public static int updateFondenMovimiento(Connection conn, FondenMovimiento fondenMovimiento) throws FondenMovimientoEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<FondenMovimiento> l = new ArrayList<FondenMovimiento>();
        try {
            String qry = "UPDATE tFondenMovimiento SET cconcepto=?, nprecioUnitario=?, ncantidad=?, nimporte=?, crfc=?, cnumPedido=?, nnetoPedido=?, nprecio=?, ntotal=?, cproveedor=?, activo=?, id_Gabinete=?, cFolio=?, cCentroContable=?, cUnidadResponsable=?, nNumCaso=?, nCantidadTotal=?, nTechoDef=?, nTipoCambio=?, cIdMoneda=?, nIdFondenMovEstatus=? where cidFonden=? and nidFondenMovimiento=?;";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, fondenMovimiento.getCconcepto());
            pStatement.setDouble(cnt++, fondenMovimiento.getNprecioUnitario());
            pStatement.setInt(cnt++, fondenMovimiento.getNcantidad());
            pStatement.setDouble(cnt++, fondenMovimiento.getNimporte());
            pStatement.setString(cnt++, fondenMovimiento.getCrfc());
            pStatement.setString(cnt++, fondenMovimiento.getCnumPedido());
            pStatement.setDouble(cnt++, fondenMovimiento.getNnetoPedido());
            pStatement.setDouble(cnt++, fondenMovimiento.getNprecio());
            pStatement.setDouble(cnt++, fondenMovimiento.getNtotal());
            pStatement.setString(cnt++, fondenMovimiento.getCproveedor());
            pStatement.setString(cnt++, fondenMovimiento.getActivo());
            pStatement.setInt(cnt++, fondenMovimiento.getIdGabinete());
            pStatement.setString(cnt++, fondenMovimiento.getcFolio());
            pStatement.setString(cnt++, fondenMovimiento.getcCentroContable());
            pStatement.setString(cnt++, fondenMovimiento.getcUnidadResponsable());
            pStatement.setInt(cnt++, fondenMovimiento.getnNumCaso());
            pStatement.setInt(cnt++, fondenMovimiento.getnCantidadTotal());
            pStatement.setDouble(cnt++, fondenMovimiento.getnTechoDef());
            pStatement.setDouble(cnt++, fondenMovimiento.getnTipoCambio());
            pStatement.setString(cnt++, fondenMovimiento.getcIdMoneda());
            pStatement.setInt(cnt++, fondenMovimiento.getnIdFondenMovEstatus());
            pStatement.setInt(cnt++, fondenMovimiento.getCidFonden());
            pStatement.setInt(cnt++, fondenMovimiento.getNidFondenMovimiento());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
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

    public static int saveOrUpdateFondenMovimiento(Connection conn, FondenMovimiento fondenMovimiento) throws FondenEngineException, FondenMovimientoEngineException {
        if (readFondenMovimiento(conn, fondenMovimiento.getCidFonden(), fondenMovimiento.getNidFondenMovimiento()).isEmpty()) {
            return saveFondenMovimiento(conn, fondenMovimiento);
        } else {
            return updateFondenMovimiento(conn, fondenMovimiento);
        }
    }

    private static FondenMovimiento extraeFondenMovimiento(ResultSet rs) throws FondenEngineException {
        try {
            FondenMovimiento pojo = new FondenMovimiento();
            pojo.setCconcepto(rs.getString("cconcepto"));
            pojo.setNprecioUnitario(rs.getDouble("nprecioUnitario"));
            pojo.setNcantidad(rs.getInt("ncantidad"));
            pojo.setNimporte(rs.getDouble("nimporte"));
            pojo.setCrfc(rs.getString("crfc"));
            pojo.setCnumPedido(rs.getString("cnumPedido"));
            pojo.setNnetoPedido(rs.getDouble("nnetoPedido"));
            pojo.setNprecio(rs.getDouble("nprecio"));
            pojo.setNtotal(rs.getDouble("ntotal"));
            pojo.setCidFonden(rs.getInt("cidFonden"));
            pojo.setCproveedor(rs.getString("cproveedor"));
            pojo.setActivo(rs.getString("activo"));
            pojo.setIdGabinete(rs.getInt("id_Gabinete"));
            pojo.setNidFondenMovimiento(rs.getInt("nidFondenMovimiento"));
            pojo.setcFolio(rs.getString("cFolio"));
            pojo.setcCentroContable(rs.getString("cCentroContable"));
            pojo.setcUnidadResponsable(rs.getString("cUnidadResponsable"));
            pojo.setnNumCaso(rs.getInt("nNumCaso"));
            pojo.setnTechoDef(rs.getDouble("nTechoDef"));
            pojo.setnTipoCambio(rs.getDouble("nTipoCambio"));
            pojo.setcIdMoneda(rs.getString("cIdMoneda"));
            pojo.setnIdFondenMovEstatus(rs.getInt("nIdFondenMovEstatus"));
            pojo.setnCantidadTotal(rs.getInt("nCantidadTotal"));
            return pojo;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        }
    }
}
