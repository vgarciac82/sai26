package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.syc.contable.AccountingEngineException;
import com.syc.sai.contabilidad.polizamanual.CatalogoCabms;
import com.syc.sai.contabilidad.polizamanual.EventoRelacion;
import com.syc.sai.contabilidad.polizamanual.GrupoEvento;
import com.syc.sai.contabilidad.polizamanual.SubGrupoEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CuentaContableManager {

    private static Logger log = LoggerFactory.getLogger(CuentaContableManager.class);

    public static boolean cuentaBloqueada(Connection conn, String nCuenta, int nMes, String cCentroContable, String operacion) throws AccountingEngineException {
        boolean result = false;
        String qry = "SELECT ncuenta, " + "       cnivelbloqueo, " + "       Substring(cbloqueaabonos, ?, 1) AS bloqueoAbono, " + "       Substring(cbloqueacargos, ?, 1) AS bloqueoCargo  " + "FROM   tcuentas t WITH(nolock)  " + " WHERE  cnivelbloqueo IS NOT NULL " + "       AND ncuenta = ? " + "       AND ( cnivelbloqueo = 'T' " + "              OR cnivelbloqueo = ? ) ";
        String nivelBloqueo = CuentaContable.CENTRO_CONTABLE_LOCAL.equals(cCentroContable) ? "C" : "F";
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        try {
            pStmnt = conn.prepareStatement(qry);
            pStmnt.setInt(1, nMes);
            pStmnt.setInt(2, nMes);
            pStmnt.setString(3, nCuenta);
            pStmnt.setString(4, nivelBloqueo);
            rs = pStmnt.executeQuery();
            if (rs.next()) {
                if (operacion.equals(CuentaContable.ABONO)) {
                    result = rs.getInt("bloqueoAbono") > 0;
                } else {
                    result = rs.getInt("bloqueoCargo") > 0;
                }
            }
            return result;
        } catch (Exception e) {
            throw new AccountingEngineException("Error analizando cuenta bloqueada " + e, e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatement " + e2, e2);
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando ResultSet " + e2, e2);
                }
            pStmnt = null;
            rs = null;
        }
    }

    public static String[] cuentasBloqueadas(Connection conn, String cuentasCargo, String cuentasAbono, String cCentroContable, int nMes) throws AccountingEngineException {
        List<String> result = new ArrayList<String>();
        String nivelBloqueo = CuentaContable.CENTRO_CONTABLE_LOCAL.equals(cCentroContable) ? "C" : "F";
        String token = "";
        String qry = "SELECT ncuenta " + "FROM   tcuentas t WITH(nolock) " + "WHERE  cnivelbloqueo IS NOT NULL " + "       AND ( ";
        if (cuentasCargo != null && !"".equals(cuentasCargo)) {
            qry += "			( ncuenta IN ( " + cuentasCargo + " ) " + "               AND ( cnivelbloqueo = 'T' " + "                      OR cnivelbloqueo = '" + nivelBloqueo + "' ) " + "               AND Substring(cbloqueacargos, " + String.valueOf(nMes) + ", 1) = 1 " + "            ) ";
            token = (cuentasAbono != null && !"".equals(cuentasAbono)) ? " OR " : "";
        }
        if (cuentasAbono != null && !"".equals(cuentasAbono)) {
            qry += token;
            qry += "            ( ncuenta IN ( " + cuentasAbono + " ) " + "                   AND ( cnivelbloqueo = 'T' " + "                          OR cnivelbloqueo = '" + nivelBloqueo + "' ) " + "                   AND Substring(cbloqueaabonos, " + nMes + ", 1) = 1 " + "			)  ";
        }
        qry += "           )  ";
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        try {
            pStmnt = conn.prepareStatement(qry);
            rs = pStmnt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("ncuenta"));
            }
            return result.toArray(new String[] {});
        } catch (Exception e) {
            throw new AccountingEngineException("Error validando bloqueo de cuentas " + e.toString(), e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatement " + e2, e2);
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando ResultSet " + e2, e2);
                }
            pStmnt = null;
            rs = null;
        }
    }

    public static String calculaCuentaPadre(CuentaContable cuenta) {
        if (cuenta.getnCuentaPadre() != null && !"".equals(cuenta.getnCuentaPadre()))
            return cuenta.getnCuentaPadre();
        else {
            String cta = cuenta.getnCuenta();
            String[] ctaSplit = cta.split("-");
            String token = "-";
            String ctaPadre = new String(ctaSplit[0]);
            for (int i = 1; i < ctaSplit.length; i++) {
                if (i < cuenta.getNivelCuenta() - 1)
                    ctaPadre += token + ctaSplit[i];
                else
                    ctaPadre += token + "00000";
            }
            return ctaPadre;
        }
    }

    public static List<CuentaContable> buscaCuentasContables(Connection conn, CuentaContable modelo) throws AccountingEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String queryBase = " SELECT	* " + " FROM	tCuentas WITH(nolock) ";
        String cond = "";
        String token = "WHERE";
        List<CuentaContable> l = new ArrayList<CuentaContable>();
        try {
            if (modelo.getnCuenta() != null && !"".equalsIgnoreCase(modelo.getnCuenta())) {
                cond = cond + " " + token + " nCuenta LIKE ? ";
                token = "AND";
            }
            if (modelo.getdCuenta() != null && !"".equalsIgnoreCase(modelo.getdCuenta())) {
                cond = cond + " " + token + " dCuenta LIKE ? ";
                token = "AND";
            }
            /*
			 * if (modelo.getTipoCuenta() != null &&
			 * !"".equalsIgnoreCase(modelo.getTipoCuenta())) { cond = cond + " "
			 * + token + " TipoCuenta = ? "; token = "AND"; }
			 */
            if (modelo.getnCuentaPadre() != null && !"".equalsIgnoreCase(modelo.getnCuentaPadre())) {
                cond = cond + " " + token + " nCuentaPadre = ? ";
                token = "AND";
            }
            /*
			 * if (modelo.getTipoBalance() != null &&
			 * !"".equalsIgnoreCase(modelo.getTipoBalance())) { cond = cond +
			 * " " + token + " TipoBalance = ? "; token = "AND"; } if
			 * (modelo.getVerificaSaldo() != null &&
			 * !"".equalsIgnoreCase(modelo.getVerificaSaldo())) { cond = cond +
			 * " " + token + " VerificaSaldo = ? "; token = "AND"; } if
			 * (modelo.getNaturalezaCuenta() != null &&
			 * !"".equalsIgnoreCase(modelo.getNaturalezaCuenta())) { cond = cond
			 * + " " + token + " NaturalezaCuenta = ? "; token = "AND"; }
			 * 
			 * if (modelo.getNivelCuenta() > 0) { cond = cond + " " + token +
			 * " NivelCuenta = ? "; token = "AND"; } if
			 * (modelo.getAplicacionCuenta() != null &&
			 * !"".equalsIgnoreCase(modelo.getAplicacionCuenta())) { cond = cond
			 * + " " + token + " AplicacionCuenta = ? "; token = "AND"; }
			 */
            if (modelo.getcSubcuenta() != null && !"".equalsIgnoreCase(modelo.getcSubcuenta())) {
                cond = cond + " " + token + " cSubcuenta = ? ";
                token = "AND";
            }
            /*
			 * if (modelo.getnCuentaLike() != null &&
			 * !"".equalsIgnoreCase(modelo.getnCuentaLike())) { cond = cond +
			 * " " + token + " nCuentaLike = ? "; token = "AND"; } if
			 * (modelo.getnOrdenBalanza() > 0) { cond = cond + " " + token +
			 * "  nOrdenBalanza = ? "; token = "AND"; } if
			 * (modelo.getnNivelBalanza() > 0) { cond = cond + " " + token +
			 * " nNivelBalanza = ? "; token = "AND"; }
			 */
            String qry = queryBase + cond + token + "  ( not (TipoBalance='P' and TipoCuenta='P') AND nCuenta NOT LIKE '0%' ) ";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            if (modelo.getnCuenta() != null && !"".equalsIgnoreCase(modelo.getnCuenta())) {
                pStatement.setString(cnt++, modelo.getnCuenta() + "%");
            }
            if (modelo.getdCuenta() != null && !"".equalsIgnoreCase(modelo.getdCuenta())) {
                pStatement.setString(cnt++, modelo.getdCuenta() + "%");
            }
            /*
			 * if (modelo.getTipoCuenta() != null &&
			 * !"".equalsIgnoreCase(modelo.getTipoCuenta())) {
			 * pStatement.setString(cnt++, modelo.getTipoCuenta()); }
			 */
            if (modelo.getnCuentaPadre() != null && !"".equalsIgnoreCase(modelo.getnCuentaPadre())) {
                pStatement.setString(cnt++, modelo.getnCuentaPadre());
            }
            /*
			 * if (modelo.getTipoBalance() != null &&
			 * !"".equalsIgnoreCase(modelo.getTipoBalance())) {
			 * pStatement.setString(cnt++, modelo.getTipoBalance()); }
			 * 
			 * if (modelo.getVerificaSaldo() != null &&
			 * !"".equalsIgnoreCase(modelo.getVerificaSaldo())) {
			 * pStatement.setString(cnt++, modelo.getVerificaSaldo()); }
			 * 
			 * if (modelo.getNaturalezaCuenta() != null &&
			 * !"".equalsIgnoreCase(modelo.getNaturalezaCuenta())) {
			 * pStatement.setString(cnt++, modelo.getNaturalezaCuenta()); }
			 * 
			 * if (modelo.getNivelCuenta() > 0) { pStatement.setInt(cnt++,
			 * modelo.getNivelCuenta()); }
			 * 
			 * if (modelo.getAplicacionCuenta() != null &&
			 * !"".equalsIgnoreCase(modelo.getAplicacionCuenta())) {
			 * pStatement.setString(cnt++, modelo.getAplicacionCuenta()); }
			 */
            if (modelo.getcSubcuenta() != null && !"".equalsIgnoreCase(modelo.getcSubcuenta())) {
                pStatement.setString(cnt++, modelo.getcSubcuenta());
            }
            /*
			 * if (modelo.getnCuentaLike() != null &&
			 * !"".equalsIgnoreCase(modelo.getnCuentaLike())) {
			 * pStatement.setString(cnt++, modelo.getnCuentaLike()); } if
			 * (modelo.getnOrdenBalanza() > 0) { pStatement.setInt(cnt++,
			 * modelo.getnOrdenBalanza()); } if (modelo.getnNivelBalanza() > 0)
			 * { pStatement.setInt(cnt++, modelo.getnNivelBalanza()); }
			 */
            log.info("Object: {}", qry);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeCuentaContable(rs));
            }
            return l;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
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

    public static List<CuentaContable> buscaCuentasDeAplicacion(Connection conn, CuentaContable modelo) throws AccountingEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String queryBase = " SELECT	* " + " FROM	tCuentas WITH(nolock) ";
        String cond = "";
        String token = "WHERE";
        List<CuentaContable> l = new ArrayList<CuentaContable>();
        try {
            if (modelo.getnCuenta() != null && !"".equalsIgnoreCase(modelo.getnCuenta())) {
                cond = cond + " " + token + " nCuenta LIKE ?";
                token = "AND";
            }
            if (modelo.getdCuenta() != null && !"".equalsIgnoreCase(modelo.getdCuenta())) {
                cond = cond + " " + token + " dCuenta = ? ";
                token = "AND";
            }
            if (modelo.getnCuentaPadre() != null && !"".equalsIgnoreCase(modelo.getnCuentaPadre())) {
                cond = cond + " " + token + " nCuentaPadre = ? ";
                token = "AND";
            }
            if (modelo.getcSubcuenta() != null && !"".equalsIgnoreCase(modelo.getcSubcuenta())) {
                cond = cond + " " + token + " cSubcuenta = ? ";
                token = "AND";
            }
            String qry = queryBase + cond + token + "  ( not (TipoBalance='P' and TipoCuenta='P') AND nCuenta NOT LIKE '0%' AND AplicacionCuenta = 'S' ) ";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            if (modelo.getnCuenta() != null && !"".equalsIgnoreCase(modelo.getnCuenta())) {
                pStatement.setString(cnt++, modelo.getnCuenta() + "%");
            }
            if (modelo.getdCuenta() != null && !"".equalsIgnoreCase(modelo.getdCuenta())) {
                pStatement.setString(cnt++, modelo.getdCuenta());
            }
            if (modelo.getnCuentaPadre() != null && !"".equalsIgnoreCase(modelo.getnCuentaPadre())) {
                pStatement.setString(cnt++, modelo.getnCuentaPadre());
            }
            if (modelo.getcSubcuenta() != null && !"".equalsIgnoreCase(modelo.getcSubcuenta())) {
                pStatement.setString(cnt++, modelo.getcSubcuenta());
            }
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeCuentaContable(rs));
            }
            return l;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
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

    //*********************** Polizas Eventos Manuales 2014 *********************
    // Ing J. Luis DR
    public static List<GrupoEvento> autoCompletaGrupoEvento(Connection conn, String aux) throws AccountingEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String queryBase = "SELECT * FROM tGrupoEvento where nIdGrupoEvento like '" + aux + "%'";
        List<GrupoEvento> l = new ArrayList<GrupoEvento>();
        try {
            String qry = queryBase;
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeGrupoEvento(rs));
            }
            return l;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
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

    public static List<SubGrupoEvento> autoCompletaSubGrupoEvento(Connection conn, String nGrupo, String aux) throws AccountingEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String queryBase = "select * from tSubGrupoEvento where nIdGrupoEvento=" + nGrupo + " and nIdSubGrupoEvento like '" + aux + "%'";
        List<SubGrupoEvento> l = new ArrayList<SubGrupoEvento>();
        try {
            String qry = queryBase;
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeSubGrupoEvento(rs));
            }
            return l;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
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

    public static List<EventoRelacion> autoCompletaEventoRelacion(Connection conn, String nGrupo, String nSubGrupo, String nEvento) throws AccountingEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String queryBase = "select * from tEventoRelacion where nIdGrupoEvento=" + nGrupo + " and nIdSubGrupoEvento = " + nSubGrupo + " and cEvento like '" + nEvento + "%'";
        List<EventoRelacion> l = new ArrayList<EventoRelacion>();
        try {
            String qry = queryBase;
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeEventoRelacion(rs));
            }
            return l;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
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

    public static List<CatalogoCabms> autoCompletaCABMS(Connection conn, String nGrupo, String nSubGrupo, String nEvento, String nCAMBS) throws AccountingEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<CatalogoCabms> l = new ArrayList<CatalogoCabms>();
        try {
            String qry = "select * from tCatalogoCABMS where cPartida in(select DISTINCT cPartida from tEventoManual where cIdGrupoEvento = " + nGrupo + " and cIdSubGrupoEvento = " + nSubGrupo + " and cIdEventoManual = " + nEvento + ") and cCABMS like '" + nCAMBS + "%'";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeCatalogoCabms(rs));
            }
            return l;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
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

    public static List<CatalogoCabms> selectCABMS(Connection conn, String type, String id) throws AccountingEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<CatalogoCabms> l = new ArrayList<CatalogoCabms>();
        try {
            String field = "";
            if (type.equals("nCABMS")) {
                field = "cCABMS";
            } else if (type.equals("nCUCOP")) {
                field = "cCUCOP";
            } else if (type.equals("nCOG")) {
                field = "cPartida";
            }
            String qry = "select * from tCatalogoCABMS where " + field + " like '" + id + "'";
            pStatement = conn.prepareStatement(qry);
            int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeCatalogoCabms(rs));
            }
            return l;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
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

    private static SubGrupoEvento extraeSubGrupoEvento(ResultSet rs) throws AccountingEngineException {
        try {
            SubGrupoEvento pojo = new SubGrupoEvento();
            pojo.setCnombreSubGrupo(rs.getString("cNombreSubGrupo"));
            pojo.setNidGrupoEvento(rs.getInt("nIdGrupoEvento"));
            pojo.setNidSubGrupoEvento(rs.getInt("nIdSubGrupoEvento"));
            return pojo;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        }
    }

    private static GrupoEvento extraeGrupoEvento(ResultSet rs) throws AccountingEngineException {
        try {
            GrupoEvento pojo = new GrupoEvento();
            pojo.setCnombreGrupo(rs.getString("cNombregrupo"));
            pojo.setNidGrupoEvento(rs.getInt("nIdGrupoEvento"));
            return pojo;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        }
    }

    private static EventoRelacion extraeEventoRelacion(ResultSet rs) throws AccountingEngineException {
        try {
            EventoRelacion pojo = new EventoRelacion();
            pojo.setCevento(rs.getString("cevento"));
            pojo.setNidGrupoEvento(rs.getInt("nidGrupoEvento"));
            pojo.setNidSubGrupoEvento(rs.getInt("nidSubGrupoEvento"));
            pojo.setDevento(rs.getString("devento"));
            return pojo;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        }
    }

    private static CatalogoCabms extraeCatalogoCabms(ResultSet rs) throws AccountingEngineException {
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
            throw new AccountingEngineException(e);
        }
    }

    //***********************************************************************
    private static CuentaContable extraeCuentaContable(ResultSet rs) throws AccountingEngineException {
        try {
            CuentaContable cc = new CuentaContable();
            cc.setnCuenta(rs.getString("nCuenta"));
            cc.setdCuenta(rs.getString("dCuenta"));
            cc.setTipoCuenta(rs.getString("TipoCuenta"));
            cc.setnCuentaPadre(rs.getString("nCuentaPadre"));
            cc.setTipoBalance(rs.getString("TipoBalance"));
            cc.setVerificaSaldo(rs.getString("VerificaSaldo"));
            cc.setNaturalezaCuenta(rs.getString("NaturalezaCuenta"));
            cc.setNivelCuenta(rs.getInt("NivelCuenta"));
            cc.setAplicacionCuenta(rs.getString("AplicacionCuenta"));
            cc.setcSubcuenta(rs.getString("cSubcuenta") == null ? "" : rs.getString("cSubcuenta"));
            cc.setnCuentaLike(rs.getString("nCuentaLike"));
            cc.setnOrdenBalanza(rs.getInt("nOrdenBalanza"));
            cc.setnNivelBalanza(rs.getInt("nNivelBalanza"));
            return cc;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        }
    }

    public static int insertCuentaContable(Connection conn, CuentaContable cuenta) throws AccountingEngineException {
        PreparedStatement pStmnt = null;
        String qry = "INSERT INTO tCuentas (nCuenta, dCuenta, TipoCuenta, nCuentaPadre, TipoBalance, " + "					   VerificaSaldo, NaturalezaCuenta, NivelCuenta, AplicacionCuenta, cSubcuenta, " + "					   nCuentaLike, nOrdenBalanza, nNivelBalanza) " + "VALUES (?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?," + "?, ?, ?)";
        try {
            if (cuenta.getnCuentaPadre() == null || "".equals(cuenta.getnCuentaPadre()))
                cuenta.setnCuentaPadre(calculaCuentaPadre(cuenta));
            int cnt = 1;
            pStmnt = conn.prepareStatement(qry);
            pStmnt.setString(cnt++, cuenta.getnCuenta());
            pStmnt.setString(cnt++, cuenta.getdCuenta());
            pStmnt.setString(cnt++, cuenta.getTipoCuenta());
            pStmnt.setString(cnt++, cuenta.getnCuentaPadre());
            pStmnt.setString(cnt++, cuenta.getTipoBalance());
            pStmnt.setString(cnt++, cuenta.getVerificaSaldo());
            pStmnt.setString(cnt++, cuenta.getNaturalezaCuenta());
            pStmnt.setInt(cnt++, cuenta.getNivelCuenta());
            pStmnt.setString(cnt++, cuenta.getAplicacionCuenta());
            pStmnt.setString(cnt++, ("".equals(cuenta.getcSubcuenta()) ? null : cuenta.getcSubcuenta()));
            pStmnt.setString(cnt++, cuenta.getnCuentaLike());
            pStmnt.setInt(cnt++, cuenta.getnOrdenBalanza());
            pStmnt.setInt(cnt++, cuenta.getnNivelBalanza());
            int r = pStmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement" + e2);
                }
            pStmnt = null;
        }
    }

    public static int updateCuentaContable(Connection conn, CuentaContable cuenta) throws AccountingEngineException {
        PreparedStatement pStmnt = null;
        String qry = "UPDATE	tcuentas ";
        String set = "SET";
        String token = "";
        String where = "WHERE	nCuenta = ?";
        try {
            if (cuenta.getdCuenta() != null && !"".equals(cuenta.getdCuenta())) {
                set += token + " dCuenta = ?  ";
                token = ", ";
            }
            if (cuenta.getAplicacionCuenta() != null && !"".equals(cuenta.getAplicacionCuenta())) {
                set += token + "		AplicacionCuenta = ?";
                token = ", ";
            }
            if (cuenta.getcSubcuenta() != null && !"".equals(cuenta.getcSubcuenta())) {
                set += token + "		cSubcuenta = ? ";
                token = ", ";
            }
            if (cuenta.getTipoCuenta() != null && !"".equals(cuenta.getTipoCuenta())) {
                set += token + "		TipoCuenta = ? ";
                token = ", ";
            }
            if (cuenta.getTipoBalance() != null && !"".equals(cuenta.getTipoBalance())) {
                set += token + "		TipoBalance = ? ";
                token = ", ";
            }
            if (cuenta.getNaturalezaCuenta() != null && !"".equals(cuenta.getNaturalezaCuenta())) {
                set += token + "		NaturalezaCuenta = ? ";
                token = ", ";
            }
            int cnt = 1;
            qry = qry + set + where;
            pStmnt = conn.prepareStatement(qry);
            if (cuenta.getdCuenta() != null && !"".equals(cuenta.getdCuenta()))
                pStmnt.setString(cnt++, cuenta.getdCuenta());
            if (cuenta.getAplicacionCuenta() != null && !"".equals(cuenta.getAplicacionCuenta()))
                pStmnt.setString(cnt++, cuenta.getAplicacionCuenta());
            if (cuenta.getcSubcuenta() != null && !"".equals(cuenta.getcSubcuenta()))
                pStmnt.setString(cnt++, cuenta.getcSubcuenta());
            if (cuenta.getTipoCuenta() != null && !"".equals(cuenta.getTipoCuenta()))
                pStmnt.setString(cnt++, cuenta.getTipoCuenta());
            if (cuenta.getTipoBalance() != null && !"".equals(cuenta.getTipoBalance()))
                pStmnt.setString(cnt++, cuenta.getTipoBalance());
            if (cuenta.getNaturalezaCuenta() != null && !"".equals(cuenta.getNaturalezaCuenta()))
                pStmnt.setString(cnt++, cuenta.getNaturalezaCuenta());
            pStmnt.setString(cnt++, cuenta.getnCuenta());
            int r = pStmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement" + e2);
                }
            pStmnt = null;
        }
    }

    public static int eliminaCuentaContable(Connection conn, CuentaContable cuenta) throws AccountingEngineException {
        PreparedStatement pStmnt = null;
        String qry = "DELETE FROM tCuentas WHERE nCuenta = ?";
        try {
            pStmnt = conn.prepareStatement(qry);
            pStmnt.setString(1, cuenta.getnCuenta());
            int r = pStmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement" + e2);
                }
            pStmnt = null;
        }
    }

    public static int[] obtenOrdenNivelBalanza(Connection conn, String nCuenta) throws AccountingEngineException {
        PreparedStatement pStmnt = null;
        String qry = "select nOrdenBalanza+1 nOrdenBalanza,nNivelBalanza+1 nNivelBalanza from tCuentas with(nolock) where nCuenta= ?";
        ResultSet rs = null;
        int[] OrdenNivelBalanza = new int[2];
        try {
            pStmnt = conn.prepareStatement(qry);
            pStmnt.setString(1, nCuenta);
            rs = pStmnt.executeQuery();
            if (rs.next()) {
                OrdenNivelBalanza[0] = rs.getInt("nOrdenBalanza");
                OrdenNivelBalanza[1] = rs.getInt("nNivelBalanza");
            }
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        } finally {
            try {
                if (pStmnt != null) {
                    pStmnt.close();
                    pStmnt = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception e2) {
                log.warn("Object: {}", "Problemas cerrando PreparedStatement" + e2);
            }
        }
        return OrdenNivelBalanza;
    }

    public static ArrayList<CuentaContable> obtenCuentasArbol(Connection conn) throws AccountingEngineException {
        ResultSet rs = null;
        CuentaContable cuenta;
        ArrayList<CuentaContable> Cuentas = new ArrayList<CuentaContable>();
        Statement stm = null;
        String qry = " SELECT nCuenta,dcuenta,TipoCuenta,TipoBalance,NivelCuenta, nOrdenBalanza,nNivelBalanza,nCuentaPadre,isnull(cSubcuenta,'') cSubcuenta, AplicacionCuenta," + " NaturalezaCuenta,isnull((select top 1 tm.cTipoMovimiento from tMovimiento tm with(nolock) where tm.nCuenta=tc.nCuenta),'') Movimientos" + " ,isnull((select top 1 th.TipoCuenta from tCuentas th with (nolock) where th.nCuentaPadre=tc.nCuenta),'') Hijos" + " FROM tCuentas AS tc with (nolock) WHERE	not (TipoBalance='P' and TipoCuenta='P')";
        System.out.println(qry);
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(qry);
            while (rs.next()) {
                cuenta = new CuentaContable();
                cuenta.setnCuenta(rs.getString("nCuenta"));
                cuenta.setdCuenta(rs.getString("dcuenta"));
                cuenta.setTipoCuenta(rs.getString("TipoCuenta"));
                cuenta.setTipoBalance(rs.getString("TipoBalance"));
                cuenta.setNivelCuenta(rs.getInt("NivelCuenta"));
                cuenta.setnOrdenBalanza(rs.getInt("nOrdenBalanza"));
                cuenta.setnNivelBalanza(rs.getInt("nNivelBalanza"));
                cuenta.setnCuentaPadre(rs.getString("nCuentaPadre"));
                cuenta.setAplicacionCuenta(rs.getString("AplicacionCuenta"));
                cuenta.setNaturalezaCuenta(rs.getString("NaturalezaCuenta"));
                cuenta.settieneMovimientos(!rs.getString("Movimientos").trim().equals(""));
                cuenta.setcontieneHijos(!rs.getString("Hijos").trim().equals(""));
                cuenta.setcSubcuenta(rs.getString("cSubcuenta"));
                Cuentas.add(cuenta);
            }
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        } finally {
            try {
                if (stm != null) {
                    stm.close();
                    stm = null;
                }
            } catch (Exception e2) {
                log.warn("Object: {}", "Problemas cerrando PreparedStatement" + e2);
            }
        }
        return Cuentas;
    }

    //obten cuentas
    private static ArrayList<CuentaContable> obtenHijos(ArrayList<CuentaContable> Cuentas, String CuentaPadre, int[] nivelCuenta, int nivelActual) {
        ArrayList<CuentaContable> hijos = new ArrayList<CuentaContable>();
        for (int j = 0; j < Cuentas.size(); j++) if (Cuentas.get(j).getnCuentaPadre().equals(CuentaPadre))
            hijos.add(Cuentas.get(j));
        return hijos;
    }
}
