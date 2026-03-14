package com.syc.sai.contabilidad.polizamanual.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.contabilidad.polizamanual.Cuentas;
import com.syc.sai.contabilidad.polizamanual.CuentasEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuentasManager {

    private static Logger log = LoggerFactory.getLogger(CuentasManager.class);

    public static Cuentas readCuentas(Connection conn, String nCuenta) {
        String restrictions = " nCuenta = " + nCuenta;
        List<Cuentas> l;
        try {
            l = readCuentasBy(conn, restrictions);
            if (!l.isEmpty()) {
                return l.get(0);
            } else {
                return null;
            }
        } catch (CuentasEngineException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readEventoBy(Connection conn, String parametros) throws CuentasEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String[] x = new String[2];
        int i = 0;
        List<Cuentas> l = new ArrayList<Cuentas>();
        try {
            String qry = "";
            qry = "select COUNT( cPartida) cPartida from tEventoManual with (nolock) where " + parametros + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                x[i] = (extraePartida(rs));
                i++;
            }
            return x[0];
        } catch (Exception e) {
            throw new CuentasEngineException(e);
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

    public static List<Cuentas> readCuentasBy(Connection conn, String restrictions) throws CuentasEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<Cuentas> l = new ArrayList<Cuentas>();
        try {
            String qry = "";
            qry = "select c.* from tCuentas c with (nolock) " + restrictions + ";";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeCuentas(rs));
            }
            return l;
        } catch (Exception e) {
            throw new CuentasEngineException(e);
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

    public static int saveCuentas(Connection conn, Cuentas cuenta) throws CuentasEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        try {
            String qry = "INSERT INTO tCuentas (ncuenta, cSubcuenta, dcuenta, tipoCuenta, ncuentaPadre, tipoBalance, verificaSaldo, naturalezaCuenta, nivelCuenta, aplicacionCuenta, ncuentaLike, nordenBalanza, nnivelBalanza, cbloqueaAbonos, cbloqueaCargos, cnivelBloqueo, cuentaBloqueada) values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? );";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, cuenta.getNcuenta());
            pStatement.setString(cnt++, cuenta.getDcuenta());
            pStatement.setString(cnt++, cuenta.getTipoCuenta());
            pStatement.setString(cnt++, cuenta.getNcuentaPadre());
            pStatement.setString(cnt++, cuenta.getTipoBalance());
            pStatement.setString(cnt++, cuenta.getVerificaSaldo());
            pStatement.setString(cnt++, cuenta.getNaturalezaCuenta());
            pStatement.setShort(cnt++, cuenta.getNivelCuenta());
            pStatement.setString(cnt++, cuenta.getAplicacionCuenta());
            pStatement.setString(cnt++, cuenta.getNcuentaLike());
            pStatement.setInt(cnt++, cuenta.getNordenBalanza());
            pStatement.setInt(cnt++, cuenta.getNnivelBalanza());
            pStatement.setString(cnt++, cuenta.getCbloqueaAbonos());
            pStatement.setString(cnt++, cuenta.getCbloqueaCargos());
            pStatement.setString(cnt++, cuenta.getCnivelBloqueo());
            pStatement.setString(cnt++, cuenta.getCuentaBloqueada());
            pStatement.setString(cnt++, cuenta.getcSubcuenta());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new CuentasEngineException(e);
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

    public static int updateCuentas(Connection conn, Cuentas cuenta) throws CuentasEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        try {
            String qry = "UPDATE tCuentas SET ncuenta=?, cSubcuenta=?, dcuenta=?, tipoCuenta=?, ncuentaPadre=?, tipoBalance=?, verificaSaldo=?, naturalezaCuenta=?, nivelCuenta=?, aplicacionCuenta=?, ncuentaLike=?, nordenBalanza=?, nnivelBalanza=?, cbloqueaAbonos=?, cbloqueaCargos=?, cnivelBloqueo=?, cuentaBloqueada=?;";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            pStatement.setString(cnt++, cuenta.getNcuenta());
            pStatement.setString(cnt++, cuenta.getDcuenta());
            pStatement.setString(cnt++, cuenta.getTipoCuenta());
            pStatement.setString(cnt++, cuenta.getNcuentaPadre());
            pStatement.setString(cnt++, cuenta.getTipoBalance());
            pStatement.setString(cnt++, cuenta.getVerificaSaldo());
            pStatement.setString(cnt++, cuenta.getNaturalezaCuenta());
            pStatement.setShort(cnt++, cuenta.getNivelCuenta());
            pStatement.setString(cnt++, cuenta.getAplicacionCuenta());
            pStatement.setString(cnt++, cuenta.getNcuentaLike());
            pStatement.setInt(cnt++, cuenta.getNordenBalanza());
            pStatement.setInt(cnt++, cuenta.getNnivelBalanza());
            pStatement.setString(cnt++, cuenta.getCbloqueaAbonos());
            pStatement.setString(cnt++, cuenta.getCbloqueaCargos());
            pStatement.setString(cnt++, cuenta.getCnivelBloqueo());
            pStatement.setString(cnt++, cuenta.getCuentaBloqueada());
            pStatement.setString(cnt++, cuenta.getcSubcuenta());
            int nRows = pStatement.executeUpdate();
            conn.commit();
            return nRows;
        } catch (Exception e) {
            throw new CuentasEngineException(e);
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

    public static int saveOrUpdateCuentas(Connection conn, Cuentas cuenta) throws CuentasEngineException {
        if (readCuentas(conn, cuenta.getNcuenta()) == null) {
            return saveCuentas(conn, cuenta);
        } else {
            return updateCuentas(conn, cuenta);
        }
    }

    private static Cuentas extraeCuentas(ResultSet rs) throws CuentasEngineException {
        try {
            Cuentas pojo = new Cuentas();
            pojo.setNcuenta(rs.getString("ncuenta"));
            pojo.setDcuenta(rs.getString("dcuenta"));
            pojo.setTipoCuenta(rs.getString("tipoCuenta"));
            pojo.setNcuentaPadre(rs.getString("ncuentaPadre"));
            pojo.setTipoBalance(rs.getString("tipoBalance"));
            pojo.setVerificaSaldo(rs.getString("verificaSaldo"));
            pojo.setNaturalezaCuenta(rs.getString("naturalezaCuenta"));
            pojo.setNivelCuenta(rs.getShort("nivelCuenta"));
            pojo.setAplicacionCuenta(rs.getString("aplicacionCuenta"));
            pojo.setNcuentaLike(rs.getString("ncuentaLike"));
            pojo.setNordenBalanza(rs.getInt("nordenBalanza"));
            pojo.setNnivelBalanza(rs.getInt("nnivelBalanza"));
            pojo.setCbloqueaAbonos(rs.getString("cbloqueaAbonos"));
            pojo.setCbloqueaCargos(rs.getString("cbloqueaCargos"));
            pojo.setCnivelBloqueo(rs.getString("cnivelBloqueo"));
            pojo.setCuentaBloqueada(rs.getString("cuentaBloqueada"));
            pojo.setcSubcuenta(rs.getString("cSubcuenta"));
            return pojo;
        } catch (Exception e) {
            throw new CuentasEngineException(e);
        }
    }

    private static String extraePartida(ResultSet rs) throws CuentasEngineException {
        try {
            Cuentas pojo = new Cuentas();
            pojo.setPartida(rs.getString("cPartida"));
            String partida = pojo.getPartida();
            return partida;
        } catch (Exception e) {
            throw new CuentasEngineException(e);
        }
    }
}
