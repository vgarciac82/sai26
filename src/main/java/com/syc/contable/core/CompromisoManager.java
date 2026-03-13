package com.syc.contable.core;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import com.axtel.egresos.compromiso.Compromiso;
import com.axtel.egresos.compromiso.CompromisoDTO;
import com.axtel.egresos.compromiso.CompromisoDetalle;
import com.axtel.egresos.compromiso.CompromisoEncabezado;
import com.axtel.egresos.compromiso.PrecompromisoFinancieroDetalle;
import com.axtel.egresos.compromiso.PrecompromisoFinancieroEncabezado;
import com.axtel.egresos.compromiso.Ramo;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.contable.DocumentAppliedException;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.BitacoraManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         desarrollo gestion_conagua_sif México D.F. 23/01/2012
 */
public class CompromisoManager {

    private static final int ID_TC_COMPROMISO = 7;

    private static final Logger log = LoggerFactory.getLogger(CompromisoManager.class);

    private static final String unidadContable = "RHQ";

    public static int actualizaEnvioSICOPCompromiso(Connection conn, int folioCompromiso, int estatusSICOP) throws Exception {
        String query = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = ? WHERE nFolioCompromiso = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, estatusSICOP);
            ps.setInt(2, folioCompromiso);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaCanoCompromiso(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        try {
            query.append("UPDATE C SET C.caNoCompromiso =  C2.caNoCompromiso, caNoSemarnat2= C2.caNoSemarnat2 ");
            query.append("		FROM COMPROMISOS_SICOP C");
            query.append("		INNER JOIN COMPROMISOS_SICOP C2 ");
            query.append("				ON C2.cIdProceso = C.cIdProceso AND C2.cEstatus = 'APLICADO' ");
            query.append("		WHERE C.caNoCompromiso = '' AND C.cEstatus = 'TRAMITE' ");
            ps = conn.prepareStatement(query.toString());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static boolean aplicaCompromiso(Connection conn, String caNoCompromiso) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String querySelect = "SELECT nFolioCompromiso, cdocumentohaplicado FROM tCompromisoEncabezado WITH(NOLOCK) WHERE caNoCompromiso =  ? ";
        try {
            AccountingEngine ae = new AccountingEngine();
            ae.setValidaInsuficienciaDeSaldo(true);
            if (!CompromisoManager.estaAplicado(conn, caNoCompromiso)) {
                pstmnt = conn.prepareStatement(querySelect);
                pstmnt.setString(1, caNoCompromiso);
                rs = pstmnt.executeQuery();
                if (rs.next()) {
                    String NoComp = Integer.toString(rs.getInt(1));
                    String cDocumentoHAplicado = rs.getString("cdocumentohaplicado");
                    if ("".equals(StringUtils.trimToEmpty(cDocumentoHAplicado)))
                        ae.makeAccountingApplication(conn, "COMPROMISO", NoComp, "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso");
                } else {
                    log.info("El compromiso: " + caNoCompromiso + " No se ha aplicado, sin embargo no se encontro el folio de compromiso.");
                }
            } else {
                log.info("El compromiso: " + caNoCompromiso + " ya habia sido aplicado. Se ignora.");
            }
            return true;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    public static boolean aplicaCompromiso(Connection conn, String folio, String folioSICOP) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            AccountingEngine ae = new AccountingEngine();
            ae.setValidaInsuficienciaDeSaldo(true);
            ae.makeAccountingApplication(conn, "COMPROMISO", folio, "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso");
            pstmnt = conn.prepareStatement(" UPDATE tCompromisoEncabezado SET nFolioAutSICOP = ?, nEnviadoSICOP = 2, nEnviadoSicopSuficiencia = 2 where nFolioCompromiso =  ?  ");
            pstmnt.setString(1, folioSICOP);
            pstmnt.setString(2, folio);
            rs = pstmnt.executeQuery();
            return true;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    public static synchronized Caso avanzaCaso(Connection conn, Caso c, String u_login, String observ, String[] resp, String[] oper, Map<String, String> data, String pathPrefix) throws Exception {
        Caso rco = null;
        if (resp.length != oper.length) {
            log.error("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
            throw new Exception("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
        }
        int[] idCasoOperSgte = new int[resp.length];
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);
        for (int i = 0; i < resp.length; i++) {
            Operacion o = new Operacion();
            o.setIdTC(c.getIdTC());
            o.setNombre(oper[i].trim());
            o = OperacionManager.select(conn, o);
            if (o == null) {
                log.error("No se localizo la Operacion \"" + oper[i] + "\"");
                throw new GestionException("No se localizo la Operación \"" + oper[i] + "\"");
            }
            CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, resp[i].trim(), observ, c, o);
            CasoOperacionManager.insert(conn, co);
            idCasoOperSgte[i] = co.getIdCasoOper();
        }
        BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper);
        CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
        if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
            c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);
        c.setStatus(c.getStatus() ^ Caso.EXECUTED);
        CasoManager.update(conn, c);
        return rco;
    }

    public static int barredoraCompromisoAnual(Connection conn, boolean ignoraCMil, Usuario u, FolioGeneratorInterface fg) throws Exception {
        String query = "SELECT csubcuenta, Sum(CASE  WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 1 THEN msaldoarrastre   ELSE 0  END)  AS saldoEnero,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 2 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoFebrero,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 3 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoMarzo,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 4 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoAbril,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 5 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoMayo,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 6 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoJunio,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 7 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoJulio,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 8 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoAgosto,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 9 THEN msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoSeptiembre,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 10 THEN  " + "              msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoOctubre,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 11 THEN  " + "              msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoNoviembre,  " + "        Sum(CASE  " + "              WHEN CONVERT(INT, Substring(ncuenta, 7, 5)) = 12 THEN  " + "              msaldoarrastre  " + "              ELSE 0  " + "            END)            AS saldoDiciembre,  " + "        Sum(msaldoarrastre) AS anual  " + " FROM   tsaldos WITH(nolock)  " + " WHERE  ncuenta LIKE '82103-%'  " + (ignoraCMil ? "        AND CONVERT(INT, Substring(csubcuenta, 32, 5)) >= 20000  " : "") + " GROUP  BY csubcuenta  " + " HAVING Sum(msaldoarrastre) > 0 ";
        int insertados = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int ejercicioFiscal = Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn));
            ps = conn.prepareStatement(query);
            Calendar fechaAplicacion = new GregorianCalendar();
            if (fechaAplicacion.get(Calendar.YEAR) != ejercicioFiscal)
                fechaAplicacion = new GregorianCalendar(ejercicioFiscal, 11, 31);
            /* Paso 1, crear el caso de compromiso */
            log.trace("Creando caso compromiso.");
            Caso caso = Util.generaCaso(u, 7, fg, "VENTANILLA_COMPROMISO", GestionInterface.ATT_CONEXION, conn);
            int nFolioCompromiso = Integer.parseInt(caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1));
            log.debug("Se creo el caso de Compromiso. Folio: " + caso.getFolio());
            /* Paso 2 Insertar encabezado */
            String caNoCompromiso = generateCaNoCompromiso(u.getPropiedad("CCENTROCONTABLE").getValor(), String.valueOf(ejercicioFiscal));
            String cDescripcion = "CIERRE ANUAL " + ejercicioFiscal + " CANCELACION TOTAL DE COMPROMETIDO";
            insertados += creaEncabezado(conn, nFolioCompromiso, "CIERRE-" + ejercicioFiscal + "-" + nFolioCompromiso, "CI", fechaAplicacion.getTime(), u.getPropiedad("CCENTROCONTABLE").getValor(), u.getU_Ramo(), u.getU_UR(), caNoCompromiso, fechaAplicacion.get(Calendar.MONTH) + 1, cDescripcion, u.getLogin(), "N");
            /* Paso 3 Ejecutar el query para ver los compromisos */
            rs = ps.executeQuery();
            int nDocRenglon = 1;
            while (rs.next()) {
                String ep = rs.getString("csubcuenta");
                for (int i = 0; i < Util.NOMBRE_MESES_MX.length; i++) {
                    if (rs.getDouble("SALDO" + Util.NOMBRE_MESES_MX[i]) > 0) {
                        int cMes = i + 1;
                        String cEvento = calculaEvento(conn, ep);
                        String cCentroContable = calculaCC(conn, ep);
                        double mImporte = rs.getDouble("SALDO" + Util.NOMBRE_MESES_MX[i]);
                        insertados += insertaCommpromisoDetalle(conn, nFolioCompromiso, nDocRenglon, ep, cEvento, -mImporte, cMes, cCentroContable);
                        nDocRenglon++;
                    }
                }
            }
            log.trace("Se insertaron " + insertados + " registros");
            return nFolioCompromiso;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int borraCompromisosCancelarLista(Connection conn, Integer nFolioCompromiso) throws Exception {
        String query = "DELETE FROM tFoliosCompromisosCancelar WHERE nFolioCompromiso = ?";
        PreparedStatement ps = null;
        int foliosBorrados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioCompromiso);
            foliosBorrados = ps.executeUpdate();
            return foliosBorrados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static boolean borraTabla(Connection conn) throws Exception {
        boolean borrado = false;
        PreparedStatement pstmnt = null;
        try {
            String querySelect = "DELETE COMPROMISOS_SICOP";
            pstmnt = conn.prepareStatement(querySelect);
            pstmnt.executeUpdate();
            borrado = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return borrado;
    }

    public static boolean borraTablaSuficiencia(Connection conn) throws Exception {
        boolean borrado = false;
        PreparedStatement pstmnt = null;
        try {
            String querySelect = "DELETE CLC_SUFICIENCIA";
            pstmnt = conn.prepareStatement(querySelect);
            pstmnt.executeUpdate();
            borrado = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return borrado;
    }

    public static boolean borraTablaAnalisis(Connection conn) throws Exception {
        boolean borrado = false;
        PreparedStatement pstmnt = null;
        try {
            String querySelect = "DELETE CLC_ANALISIS_COMPROMISOS";
            pstmnt = conn.prepareStatement(querySelect);
            pstmnt.executeUpdate();
            borrado = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return borrado;
    }

    public static ArrayList<String> BuscaSuficiencia(Connection conn, String folioSuficiencia, String esCalendario) throws Exception {
        ArrayList<String> arrListaSuf = new ArrayList<>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] arrFolios = folioSuficiencia.split(",");
        StringBuilder sbEncabezado = new StringBuilder();
        sbEncabezado.append(" SELECT 'H', ");
        sbEncabezado.append(" CASE Comp.origen WHEN  'ORIGINAL' THEN 'O'");
        sbEncabezado.append(" 				 WHEN 'AMPLIACION' THEN 'A'");
        sbEncabezado.append(" 				 WHEN 'DECREMENTO' THEN 'R' END Movimiento,");
        sbEncabezado.append("	CASE Comp.origen WHEN  'ORIGINAL' THEN '1'");
        sbEncabezado.append(" 				 WHEN 'AMPLIACION' THEN '1'");
        sbEncabezado.append(" 				 WHEN 'DECREMENTO' THEN '3' END origen,");
        sbEncabezado.append(" 0 precom, ");
        sbEncabezado.append(" 'SUFICIENCIA PRESUPUESTAL PARA EL CONTRATO ' + cIdContrato Justificacion, ");
        sbEncabezado.append(" CASE Comp.origen WHEN  'DECREMENTO' ");
        sbEncabezado.append(" 	THEN (SELECT TOP 1  nFoliosuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = Comp.cIdContrato AND cDocumentoHaplicado = 'S' AND nFolioCompromiso != COMP.nFolioCompromiso and nFolioSuficiencia IS NOT NULL AND nFolioSuficiencia != -1  ) ");
        sbEncabezado.append(" 	ELSE '' END folioSuf, ");
        sbEncabezado.append(" 'RHQ', 'RHQ', 'RHQ', 16, 16, 16,  ");
        sbEncabezado.append(" FORMAT(fCarga, 'dd/MM/yyyy') FECHA_EXP, ");
        sbEncabezado.append(" FORMAT(fCarga, 'dd/MM/yyyy')  FECHA_APL,");
        sbEncabezado.append(" folioInterno ID_CTR_INT_301, ");
        sbEncabezado.append(" replace(SUBSTRING(Comp.cIdContrato,1,30), '16-RHQ-016RHQ001-N-', '') cIdContrato, ");
        sbEncabezado.append(" COALESCE( cFolioValidacion, folioInterno) COMODIN4_502 ");
        sbEncabezado.append(" FROM v_SuficienciaPendiente Comp WITH (NOLOCK) WHERE Comp.nFolioCompromiso IN (").append(folioSuficiencia).append(")");
        sbEncabezado.append(" ORDER BY Comp.nFolioCompromiso ");
        try {
            pstmntH = conn.prepareStatement(sbEncabezado.toString());
            rs = pstmntH.executeQuery();
            int k = 0;
            while (rs.next()) {
                StringBuilder filaH = new StringBuilder();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    filaH.append(rs.getString(i)).append(",");
                }
                arrListaSuf.add(filaH.toString());
                arrListaSuf.add("\r\n");
                // Ejecutar y agregar los detalles para este folio
                StringBuilder sbDetalle = new StringBuilder();
                sbDetalle.append(" SELECT CASE WHEN SUM(tCD.mImporte) > 0 THEN 542 ELSE 522 END  as ID_EVENTO, ");
                sbDetalle.append(" CASE WHEN SUM(tCD.mImporte) > 0 THEN '156_SPOA' ELSE '158_SPRA' END as EVENTO, ");
                sbDetalle.append(" 'M' as ID_EMISOR, ");
                sbDetalle.append(" rtrim(ltrim(tCEP.cRamo)) cRamo,");
                sbDetalle.append(" rtrim(ltrim(tCEP.cUnidadResponsableEP)) cUnidadResponsableEP,  ");
                sbDetalle.append(" tCEP.aEjercicioFiscal,");
                sbDetalle.append(" tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion,  ");
                sbDetalle.append(" SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2),");
                sbDetalle.append(" tCEP.cActividadInstitucional, ");
                sbDetalle.append(" tCEP.cProgramaPresupuestario, ");
                sbDetalle.append(" SUBSTRING(tCEP.cPartida,1,1), SUBSTRING(tCEP.cPartida,2,1), SUBSTRING(tCEP.cPartida,3,1), ");
                sbDetalle.append(" SUBSTRING(tCEP.cPartida,4,2), tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, ");
                sbDetalle.append(" tCEP.cEntidadFederativa,  ");
                sbDetalle.append(" SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP),45,11), ");
                sbDetalle.append(" '0000000000', '00', '000', '000', '00000', '00000', '0000000000', ");
                sbDetalle.append(" SUM(CONVERT(decimal(14,2), ABS(tCD.mImporte))), ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 1 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Enero, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 2 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Febrero, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 3 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Marzo, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 4 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Abril, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 5 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Mayo, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 6 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Junio, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 7 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Julio, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 8 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Agosto, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 9 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Sept, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 10 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Oct, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 11 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Nov, ");
                sbDetalle.append(" SUM(CASE tCD.cMes WHEN 12 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Dic ");
                sbDetalle.append(" FROM tCompromisoDetalle tCD WITH (NOLOCK) ");
                sbDetalle.append(" JOIN tCompromisoEncabezado tCE WITH (NOLOCK) ON tCD.nFolioCompromiso = tCE.nFolioCompromiso ");
                sbDetalle.append(" JOIN tCatalogoEP tCEP WITH (NOLOCK) ON tCD.EP = tCEP.EP ");
                sbDetalle.append(" WHERE tCD.nFolioCompromiso IN (").append(arrFolios[k]).append(") ");
                sbDetalle.append(" AND SUBSTRING(tCD.EP, 40, 1) <> '4' ");
                if (esCalendario.equals("S")) {
                    sbDetalle.append(" AND mImporte > 0 ");
                } else if (esCalendario.equals("R"))
                    sbDetalle.append(" AND mImporte < 0 ");
                sbDetalle.append(" GROUP BY tCEP.cRamo, tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, ");
                sbDetalle.append(" tCEP.cFuncion, tCEP.cSubFuncion, SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2), ");
                sbDetalle.append(" tCEP.cActividadInstitucional, SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP), 45, 11), tCD.nFolioCompromiso,");
                sbDetalle.append(" tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa");
                sbDetalle.append(" ORDER BY tCD.nFolioCompromiso ");
                pstmntD = conn.prepareStatement(sbDetalle.toString());
                rs2 = pstmntD.executeQuery();
                k++;
                while (rs2.next()) {
                    StringBuilder filaD = new StringBuilder();
                    for (int j = 1; j <= rs2.getMetaData().getColumnCount(); j++) {
                        filaD.append(rs2.getString(j)).append(",");
                    }
                    arrListaSuf.add(filaD.toString());
                    arrListaSuf.add("\r\n");
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al consultar suficiencia: " + e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntH);
            CloseObject.closeObject(pstmntD);
        }
        return arrListaSuf;
    }

    public static ResultSet generaQueryCompromisoIntegrada(Connection conn, String lista_Contratos, boolean reimprime) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        // CONTRATO DIVERSO
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT INTEGRA.caNoIntegradaComp ");
        sql.append("     , 'H' AS Header ");
        sql.append("     , cast(getdate() as date) fAplicacion ");
        sql.append("     , cast(getdate() as date) fCarga ");
        sql.append("     , tCE.cRamo ");
        sql.append("     , tCE.cUnidadResponsableContable cUnidadResponsableEP ");
        sql.append("     , CASE WHEN SUM(tCD.mImporte) < 0 THEN 'R' ");
        sql.append("        WHEN (SELECT COUNT(*) FROM tCompromisoEncabezado WHERE cIdContrato = tCE.cIdContrato  ");
        sql.append("            	AND nFolioCompromiso NOT IN (SELECT nFolioCompromiso FROM tIntegraFoliosCompromiso WHERE caNoIntegradaComp = INTEGRA.caNoIntegradaComp)");
        sql.append("            	AND nFolioAutSICOP IS NOT NULL AND nFolioAutSICOP<>-1 ) = 0 ");
        sql.append("        THEN 'O' ");
        sql.append("        ELSE 'A' END MOVTO");
        sql.append("     , SUM(tCD.mImporte) as mImporteComp ");
        sql.append("     , pCD.cIdTipoContratoDiverso AS TipoContratoDiverso ");
        sql.append("     , '' AS TipoContratoObra  ");
        sql.append("     , CASE WHEN pCD.cIdTipoContratoDiverso = 7 THEN 'S24676' ELSE be.CBEN END CBEN ");
        sql.append("     , CASE WHEN pCD.cIdTipoContratoDiverso = 7 THEN 'S24676' ELSE pCD.cIdRFC END cidRFC ");
        sql.append("     , pCD.fContratoIni ");
        sql.append("     , pCD.fContratoFin ");
        sql.append("     , tCE.cIdContrato ");
        sql.append("     , REPLACE(REPLACE(REPLACE(LEFT(pCD.cConceptoContrato, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), '') as cConceptoContrato ");
        sql.append("     , CASE WHEN SUBSTRING(tCE.cIdContrato, 1, 3) = 'PLU' and pCD.lEsPlurianual=1 THEN 'S' ELSE 'N' END AS ES_PLURIANUAL ");
        sql.append("     , '3' AS ACTO_JURIDICO ");
        sql.append("     , case isnull(pCD.lEsPlurianual, '2') when '1' then '1' else '2' end AS TEMPORALIDAD ");
        sql.append("     , pCD.cAprobacionPLU AS APROB_PLA ");
        sql.append("     , CASE WHEN pCD.IEsAbierto IS NULL THEN '2' WHEN pCD.IEsAbierto = 0 THEN '2' ELSE '1' END AS CONTRATACION  ");
        sql.append("     , Isnull(pCD.id_precio, '0') AS ESQ_PRECIO ");
        sql.append("     , '' AS PRG_ASOC ");
        sql.append("     , '' AS BIEN_EXPROP ");
        sql.append("     , '' AS ID_CATASTRAL ");
        sql.append("     , ISNULL(pCTA.cIdTipoProc, '0') AS TPROC ");
        sql.append("     , '' AS POBLACION_OBJ ");
        sql.append("     ,  (SELECT cCodigoMonedaSiaff FROM pCatalogoTipoMoneda WITH (NOLOCK) WHERE cidtipoMoneda = '01') cCodigoMonedaSiaff ");
        sql.append("     , '1' AS TCAM ");
        sql.append("     , SUM(ABS(tCD.mImporte)) AS MONTO_MONORI ");
        sql.append("     , CASE WHEN SUM(tCD.mImporte) < 0 THEN SUM(pCD.mImporteTotal) ELSE SUM(ABS(tCD.mImporte)) END AS MONTO_EJER ");
        sql.append("     , CASE WHEN ISNULL(MONTOS.mMontoMinimo, 0) > 0 AND pCD.IEsAbierto = 1 THEN MONTOS.mMontoMinimo ELSE SUM(ABS(tCD.mImporte)) END AS MONTO_MIN  ");
        sql.append("     , CASE WHEN ISNULL(MONTOS.mMontoMaximo, 0) > 0 AND pCD.IEsAbierto = 1 THEN MONTOS.mMontoMaximo ELSE SUM(ABS(tCD.mImporte)) END AS MONTO_MAX  ");
        sql.append("     , CASE WHEN CONV.CNOCONVENIO IS NULL THEN 'N' ELSE 'S' END AS CONV_MOD  ");
        sql.append("     , '' AS NUM_PLAZAS ");
        sql.append("     , '' AS VAR_PLAZAS ");
        sql.append("     , INTEGRA.caNoIntegradaComp ");
        sql.append("     , MONTH(GETDATE()) nMes ");
        sql.append("     , '' AS ID_CTR_INT ");
        sql.append("     , convert(varchar(100) ,RTRIM( LTRIM( pCD.nCodExpedienteCNET ) )) AS CODIGO_EXPEDIENTE ");
        sql.append("     , SUBSTRING( RTRIM( LTRIM( pCD.cNoProcedimientoCNET ) ), 1, 30) AS NO_PROCEDIMIENTO ");
        sql.append("     , convert(varchar(100) ,pCD.nCodContratoCNET) AS CODIGO_CONTRATO ");
        sql.append("     , CASE WHEN pCD.cIdTipoContratoDiverso = 7 THEN 'TESORERIA DE LA FEDERACION' ELSE ISNULL(REPLACE(altaprov.dNombre, ',', ''),'') + ' ' + ISNULL(replace(altaprov.dApellidoPaterno, ',', ''),'') + ' ' + ISNULL(replace(altaprov.dApellidoMaterno, ',', ''),'') END AS REPRESENTANTE_LEGAL ");
        sql.append("     , ISNULL(CONV.CNOCONVENIO, 'NO APLICA') AS NUM_CONV_MOD ");
        sql.append("     , ISNULL(CONVERT(DATE,CONV.FINICIOCONV,103), '') AS FECHA_MOD_CONV ");
        sql.append("     , '' AS TTRANS_21 ");
        sql.append("     , CASE WHEN ISNULL(justCNET.cIdContrato, '') = '' THEN 'S' ELSE 'N' END AS ETIQUETA_COMPRANET ");
        sql.append("     , CASE WHEN ISNULL(justCNET.cIdContrato, '') = '' THEN '' ELSE justCNET.cJustificacion END AS JUSTIFICA_COMPRANET ");
        sql.append("     , '0' AS IVA_MON_ORIG_414 ");
        sql.append("     , '0' AS IMP_CONT_SIVA_413 ");
        sql.append("     , '0' AS IMP_CONV_MOD_415 ");
        sql.append("     , pcd.fFirmaContrato ");
        sql.append("     , ( SELECT nFolioAutSICOP FROM tCompromisoEncabezado WITH (NOLOCK)  ");
        sql.append("                 WHERE nFolioCompromiso = ( SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = tCE.cIdContrato  ");
        sql.append("                    AND ( cDocumentoHaplicado = 'S' OR ( cDocumentoHaplicado IS NULL OR 'S' = (SELECT PF.cDocumentoHaplicado FROM tPrecomFinancieroEncabezado PF WITH(NOLOCK) WHERE PF.nFolioPrecomFinanciero = nFolioCompromiso ))) AND nEnviadoSICOP = 2  ");
        sql.append("                    AND  nFolioSuficiencia IS NOT NULL)  ");
        sql.append("           )  ");
        sql.append("           AS folioSICOP  ");
        sql.append("       , (SELECT TOP 1 convert(date, ftermino,103) FROM pcontratodiversoconvenio WITH (NOLOCK) WHERE cOrigenRM  = 'CONTRATO MODIFICADO' AND cIdContrato = tCE.cIdContrato order by nConsecutivoModificacion desc)  fTermino_Ultima ");
        sql.append("   FROM tCompromisoEncabezado tCE WITH (NOLOCK) ");
        sql.append("   INNER JOIN tCompromisoDetalle tCD WITH (NOLOCK) ");
        sql.append("        ON  tCE.nFolioCompromiso = tCD.nFolioCompromiso  ");
        sql.append("   INNER JOIN pContratoDiverso pCD  WITH (NOLOCK) ");
        sql.append("        ON  tCE.cIdContrato = pCD.cIdContrato  AND tCE.cCentroContable = pCD.cIdEntidadContable ");
        sql.append("   LEFT JOIN pCatalogoTipoAdjudicacion pCTA WITH (NOLOCK) ");
        sql.append("        ON (pCTA.cIdTipoAdjudicacion = pCD.cIdTipoAdjudicacion)  ");
        sql.append("   INNER JOIN tBeneficiario altaprov WITH (NOLOCK)  ");
        sql.append("        ON ( altaprov.dRFC=pCD.cIdRFC) ");
        sql.append("   LEFT JOIN v_MontosMinMaxContratos MONTOS WITH (NOLOCK) ");
        sql.append("        ON MONTOS.cIdContratoDefinitivo = pCD.cIdContrato  ");
        sql.append("   LEFT JOIN tContratosJustCNET justCNET WITH (NOLOCK)  ");
        sql.append("        ON justCNET.cIdContrato = pCD.cIdContrato ");
        sql.append("   INNER JOIN  tBeneficiario be WITH (NOLOCK) ");
        sql.append("        ON   be.dRFC =  pCD.cIdRFC  ");
        sql.append("   LEFT JOIN v_ConvenioModificatorio CONV WITH (NOLOCK)  ");
        sql.append("        ON CONV.cIdContratoDefinitivo = tCE.cIdContrato ");
        sql.append("   INNER JOIN tIntegraFoliosCompromiso INTEGRA WITH (NOLOCK) ");
        sql.append("        ON INTEGRA.nFolioCompromiso = tCE.nFolioCompromiso ");
        sql.append("	WHERE INTEGRA.caNoIntegradaComp IN (" + lista_Contratos + ")  ");
        sql.append((reimprime ? "" : " AND tCE.nEnviadoSICOP = 0 "));
        sql.append("   	AND (tCE.cDocumentoHaplicado = 'S' OR (  tCE.cDocumentoHaplicado IS NULL OR 'S' = ( SELECT cDocumentoHaplicado FROM tPrecomFinancieroEncabezado precom WITH(NOLOCK) WHERE nFolioPrecomFinanciero = tCE.nFolioCompromiso ) )  ) ");
        sql.append(" GROUP BY INTEGRA.caNoIntegradaComp, tCE.cIdContrato, ");
        sql.append("         tCE.cRamo, ");
        sql.append("         tCE.cUnidadResponsableContable,pCD.cIdTipoContratoDiverso, ");
        sql.append("         be.CBEN,pCD.cIdRFC ,pCD.fContratoIni, pCD.fContratoFin, ");
        sql.append("         pCD.cConceptoContrato , pCD.IEsAbierto, pCD.id_precio, ");
        sql.append("         pCD.lEsPlurianual, pCD.cAprobacionPLU, ");
        sql.append("         PCD.nCodContratoCNET, pCTA.cIdTipoProc,  ");
        sql.append("         tCE.cCentroContable, pCD.cIdEntidadContable,  ");
        sql.append("         pCD.cIdTipoAdjudicacion, MONTOS.mMontoMinimo, MONTOS.mMontoMaximo,  ");
        sql.append("         altaprov.dNombre, altaprov.dApellidoPaterno, altaprov.dApellidoMaterno,  ");
        sql.append("         pcd.fFirmaContrato, pCD.nCodExpedienteCNET, pCD.cNoProcedimientoCNET,  ");
        sql.append("         justCNET.cIdContrato, justCNET.cJustificacion, ");
        sql.append("         cNoConvenio, fInicioConv, fFinConv ");
        pst = conn.prepareStatement(sql.toString());
        rs = pst.executeQuery();
        return rs;
    }

    public static ResultSet generaQueryCompromiso(Connection conn, String lista_Contratos, boolean reimprime) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder Sql = new StringBuilder();
        // CONTRATO DIVERSO
        Sql.append("SELECT tCE.nFolioCompromiso ");
        Sql.append("		 , 'H' AS Header");
        Sql.append("		 , fAplicacion");
        Sql.append("		 , tCE.fCarga");
        Sql.append("		 , tCE.cRamo");
        Sql.append("		 , tCE.cRamo");
        Sql.append("		 , tCE.cRamo");
        Sql.append("		 , tCE.cUnidadResponsableContable cUnidadResponsableEP");
        Sql.append("		 , tCE.cUnidadResponsableContable cUnidadResponsableEP");
        Sql.append("		 , tCE.cUnidadResponsableContable cUnidadResponsableEP");
        Sql.append("		 ,	( SELECT TOP 1 MOVTO FROM vListaCompromisos WHERE canocompromiso = tce.canocompromiso) AS MOVTO");
        Sql.append("		 , SUM(tCD.mImporte) as mImporteComp");
        Sql.append("		 , pCD.cIdTipoContratoDiverso AS TipoContratoDiverso");
        Sql.append("		 , '' AS TipoContratoObra ");
        Sql.append("		 , CASE WHEN pCD.cIdTipoContratoDiverso = 7 THEN 'S24676' ELSE be.CBEN END CBEN");
        Sql.append("		 , CASE WHEN pCD.cIdTipoContratoDiverso = 7 THEN 'S24676' ELSE pCD.cIdRFC END cidRFC");
        Sql.append("		 , pCD.fContratoIni");
        Sql.append("		 , pCD.fContratoFin");
        Sql.append("		 , tCE.cIdContrato");
        Sql.append("		 , REPLACE(REPLACE(REPLACE(LEFT(pCD.cConceptoContrato, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), '') as cConceptoContrato");
        Sql.append("		 , CASE WHEN SUBSTRING(tCE.cIdContrato, 1, 3) = 'PLU' and pCD.lEsPlurianual=1 THEN 'S' ELSE 'N' END AS ES_PLURIANUAL");
        Sql.append("		 , '3' AS ACTO_JURIDICO");
        Sql.append("		 , case isnull(pCD.lEsPlurianual, '2') when '1' then '1' else '2' end AS TEMPORALIDAD");
        Sql.append("		 , pCD.cAprobacionPLU AS APROB_PLA");
        Sql.append("		 , CASE WHEN pCD.IEsAbierto IS NULL THEN '2' WHEN pCD.IEsAbierto = 0 THEN '2' ELSE '1' END AS CONTRATACION ");
        Sql.append("		 , Isnull(pCD.id_precio, '0') AS ESQ_PRECIO");
        Sql.append("		 , '' AS PRG_ASOC");
        Sql.append("		 , '' AS BIEN_EXPROP");
        Sql.append("		 , '' AS ID_CATASTRAL");
        Sql.append("		 , ISNULL(pCTA.cIdTipoProc, '0') AS TPROC");
        Sql.append("		 , '' AS POBLACION_OBJ");
        Sql.append("		 ,  (SELECT cCodigoMonedaSiaff FROM pCatalogoTipoMoneda WITH (NOLOCK) WHERE cidtipoMoneda = '01') cCodigoMonedaSiaff");
        Sql.append("		 , '1' AS TCAM");
        Sql.append("		 , SUM(ABS(tCD.mImporte)) AS MONTO_MONORI");
        Sql.append("		 , CASE WHEN SUM(tCD.mImporte) < 0 THEN pCD.mImporteTotal ELSE SUM(ABS(tCD.mImporte)) END AS MONTO_EJER");
        Sql.append("		 , CASE WHEN ISNULL(MONTOS.mMontoMinimo, 0) > 0 AND pCD.IEsAbierto = 1 THEN MONTOS.mMontoMinimo ELSE SUM(ABS(tCD.mImporte)) END AS MONTO_MIN ");
        Sql.append("		 , CASE WHEN ISNULL(MONTOS.mMontoMaximo, 0) > 0 AND pCD.IEsAbierto = 1 THEN MONTOS.mMontoMaximo ELSE SUM(ABS(tCD.mImporte)) END AS MONTO_MAX ");
        Sql.append("		 , 'N' AS CONV_MOD");
        Sql.append("		 , '' AS NUM_PLAZAS");
        Sql.append("		 , '' AS VAR_PLAZAS");
        Sql.append("		 , tCE.caNoCompromiso");
        Sql.append("		 , tCE.nMes");
        Sql.append("		 , '' AS ID_CTR_INT");
        Sql.append("		 , tCE.caNoCompromiso");
        Sql.append("		 , convert(varchar(100) ,RTRIM( LTRIM( pCD.nCodExpedienteCNET ) )) AS CODIGO_EXPEDIENTE");
        Sql.append("		 , SUBSTRING( RTRIM( LTRIM( pCD.cNoProcedimientoCNET ) ), 1, 30) AS NO_PROCEDIMIENTO");
        Sql.append("		 , convert(varchar(100) ,pCD.nCodContratoCNET) AS CODIGO_CONTRATO");
        Sql.append("		 , ( CASE  WHEN pCD.cIdTipoContratoDiverso = 7 THEN 'TESORERIA DE LA FEDERACION'  ");
        Sql.append("		 			WHEN altaprov.cIdTipoPersonaRFC = 2  ");
        Sql.append("		 			THEN ISNULL(REPLACE(altaprov.dNombre, ',', ''),'') + ' ' + ISNULL(replace(altaprov.dApellidoPaterno, ',', ''),'') + ' ' + ISNULL(replace(altaprov.dApellidoMaterno, ',', ''),'') ");
        Sql.append("		 WHEN (REPLACE(altaprov.dNombreApoderado, ',', '')+ ' ' + replace(altaprov.dAPaternoApoderado, ',','') + ' ' + replace(altaprov.dAMaternoApoderado, ',','') ) IS NOT NULL ");
        Sql.append("		 THEN REPLACE(altaprov.dNombreApoderado, ',', '')+ ' ' + replace(altaprov.dAPaternoApoderado, ',','') + ' ' + replace(altaprov.dAMaternoApoderado, ',','') ");
        Sql.append("		 WHEN (REPLACE(altaprov.dNombreApoderado, ',', '')+ ' ' + replace(altaprov.dAPaternoApoderado, ',','') + ' ' + replace(altaprov.dAMaternoApoderado, ',','') ) IS NULL");
        Sql.append("		 THEN (SELECT tAltaProveedor.cNombre + ' ' + tAltaProveedor.cApellidoPaterno + ' ' + tAltaProveedor.cApellidoMaterno FROM tAltaProveedor WITH (NOLOCK) WHERE REPLACE(cIdRFC , '-', '') = pCD.cIdRFC ) ");
        Sql.append("		 END   )  AS REPRESENTANTE_LEGAL");
        Sql.append("		 , ISNULL(CONV.CNOCONVENIO, 'NO APLICA') AS NUM_CONV_MOD");
        Sql.append("		 , ISNULL(CONVERT(DATE,CONV.FINICIOCONV,103), '') AS FECHA_MOD_CONV");
        Sql.append("		 , '' AS TTRANS_21");
        Sql.append("		 , CASE WHEN ISNULL(justCNET.cIdContrato, '') = '' THEN 'S' ELSE 'N' END AS ETIQUETA_COMPRANET");
        Sql.append("		 , CASE WHEN ISNULL(justCNET.cIdContrato, '') = '' THEN '' ELSE justCNET.cJustificacion END AS JUSTIFICA_COMPRANET");
        Sql.append("		 , '0' AS IVA_MON_ORIG_414");
        Sql.append("		 , '0' AS IMP_CONT_SIVA_413");
        Sql.append("		 , '0' AS IMP_CONV_MOD_415");
        Sql.append("		 , pcd.fFirmaContrato");
        Sql.append("		 , CASE	WHEN tCE.nFolioCompromiso = (SELECT MIN(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = tCE.cIdContrato ");
        Sql.append("				AND ( cDocumentoHaplicado = 'S' OR ( cDocumentoHaplicado IS NULL OR 'S' = (SELECT PF.cDocumentoHaplicado FROM tPrecomFinancieroEncabezado PF WITH(NOLOCK) WHERE PF.nFolioPrecomFinanciero = nFolioCompromiso ))) ) ");
        Sql.append("		 		THEN '' ");
        Sql.append("		 		ELSE ( SELECT tCompromisoEncabezado.nFolioAutSICOP FROM tCompromisoEncabezado WITH (NOLOCK) ");
        Sql.append("		 			 	WHERE nFolioCompromiso = ( SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = tCE.cIdContrato ");
        Sql.append("							AND ( cDocumentoHaplicado = 'S' OR ( cDocumentoHaplicado IS NULL OR 'S' = (SELECT PF.cDocumentoHaplicado FROM tPrecomFinancieroEncabezado PF WITH(NOLOCK) WHERE PF.nFolioPrecomFinanciero = nFolioCompromiso ))) AND nEnviadoSICOP = 2 ");
        Sql.append("							AND  nFolioSuficiencia IS NOT NULL) ");
        Sql.append("		 			) ");
        Sql.append("		 	  	END AS folioSICOP ");
        Sql.append("		   , (SELECT TOP 1 convert(date, ftermino,103) FROM pcontratodiversoconvenio WITH (NOLOCK) WHERE cOrigenRM  = 'CONTRATO MODIFICADO' AND cIdContrato = tCE.cIdContrato order by nConsecutivoModificacion desc)  fTermino_Ultima");
        Sql.append("		   FROM tCompromisoEncabezado tCE WITH (NOLOCK)");
        Sql.append("		   INNER JOIN tCompromisoDetalle tCD WITH (NOLOCK)");
        Sql.append("				ON  tCE.nFolioCompromiso = tCD.nFolioCompromiso ");
        Sql.append("		   INNER JOIN pContratoDiverso pCD  WITH (NOLOCK)");
        Sql.append("		   		ON  tCE.cIdContrato = pCD.cIdContrato  AND tCE.cCentroContable = pCD.cIdEntidadContable");
        Sql.append("		   LEFT JOIN pCatalogoTipoAdjudicacion pCTA WITH (NOLOCK)");
        Sql.append("		 		ON (pCTA.cIdTipoAdjudicacion = pCD.cIdTipoAdjudicacion) ");
        Sql.append("		   INNER JOIN tBeneficiario altaprov WITH (NOLOCK) ");
        Sql.append("		   		ON ( altaprov.dRFC=pCD.cIdRFC)");
        Sql.append("		   LEFT JOIN v_MontosMinMaxContratos MONTOS WITH (NOLOCK)");
        Sql.append("		 		ON MONTOS.cIdContratoDefinitivo = pCD.cIdContrato ");
        Sql.append("		   LEFT JOIN tContratosJustCNET justCNET WITH (NOLOCK) ");
        Sql.append("		   		ON justCNET.cIdContrato = pCD.cIdContrato");
        Sql.append("		   INNER JOIN  tBeneficiario be WITH (NOLOCK)");
        Sql.append("				ON   be.dRFC =  pCD.cIdRFC ");
        Sql.append("		   LEFT JOIN v_ConvenioModificatorio CONV WITH (NOLOCK) ");
        Sql.append("				ON CONV.cIdContratoDefinitivo = tCE.cIdContrato ");
        Sql.append("		   WHERE  tCE.caNoCompromiso in (" + lista_Contratos + ")  ");
        Sql.append((reimprime ? "" : " AND tCE.nEnviadoSICOP = 0 "));
        Sql.append("		   AND (tCE.cDocumentoHaplicado = 'S' OR (  tCE.cDocumentoHaplicado IS NULL OR 'S' = ( SELECT cDocumentoHaplicado FROM tPrecomFinancieroEncabezado precom WITH(NOLOCK) WHERE nFolioPrecomFinanciero = tCE.nFolioCompromiso ) )  )");
        Sql.append("		 GROUP BY tCE.nFolioCompromiso,tCE.fAplicacion,tCE.fCarga, ");
        Sql.append("		 		 tCE.cRamo,tCD.nFolioCompromiso,tCE.cUnidadResponsable,");
        Sql.append("		 		 tCE.cIdContrato,be.CBEN,pCD.cIdRFC,pCD.fContratoIni,");
        Sql.append("		 		 pCD.fContratoFin,tCE.cIdContrato,pCD.cConceptoContrato,");
        Sql.append("		 		 tCE.caNoCompromiso,tCE.nMes, tCE.caNoCompromiso,pCD.cIdTipoContratoDiverso, tCE.cUnidadResponsableContable,");
        Sql.append("		 		 tCE.cCentroContable, pCD.cIdEntidadContable, pCD.IEsAbierto,");
        Sql.append("		 		 pCD.lEsPlurianual, pCD.cIdTipoAdjudicacion, pCD.id_precio,");
        Sql.append("		         altaprov.dNombre, altaprov.dApellidoPaterno, altaprov.dApellidoMaterno, ");
        Sql.append("		 		 pcd.fFirmaContrato, pCD.nCodExpedienteCNET, pCD.cNoProcedimientoCNET, pCD.nCodContratoCNET, pCTA.cIdTipoProc, ");
        Sql.append("		 		 pCD.cAprobacionPLU, MONTOS.mMontoMinimo, MONTOS.mMontoMaximo, justCNET.cIdContrato, justCNET.cJustificacion, pCD.mImporteTotal,");
        Sql.append("		 		 altaprov.dNombreApoderado, altaprov.dAPaternoApoderado, altaprov.dAMaternoApoderado, altaprov.dRFC,  altaprov.cIdTipoPersonaRFC");
        Sql.append("		 		  ,cNoConvenio, FINICIOCONV, FFINCONV");
        Sql.append("   UNION ALL ");
        // PAGO OBRA
        Sql.append("		 SELECT tCE.nFolioCompromiso");
        Sql.append("		 		,'H' AS Header");
        Sql.append("		 		,tCE.fAplicacion");
        Sql.append("		 		,tCE.fCarga");
        Sql.append("		 		,tCE.cRamo");
        Sql.append("		 		,tCE.cRamo");
        Sql.append("		 		,tCE.cRamo");
        Sql.append("		 		,tCE.cUnidadResponsableContable");
        Sql.append("		 		,tCE.cUnidadResponsableContable");
        Sql.append("		 		,tCE.cUnidadResponsableContable");
        Sql.append("		 		,	( SELECT TOP 1 movto FROM vListaCompromisos WHERE canocompromiso = tce.canocompromiso) AS MOVTO");
        Sql.append("		 		,SUM(tCD.mImporte) as mImporteComp");
        Sql.append("		 		,'' AS TipoContratoDiverso");
        Sql.append("		 		,pCO.cIdTipoContratoObra AS TipoContratoObra");
        Sql.append("		 		,be.CBEN");
        Sql.append("		 		,pCO.cIdRFC");
        Sql.append("		 		,pCO.fInicio");
        Sql.append("		 		,pCO.fTermino");
        Sql.append("		 		,replace(tce.cIdContrato, '16-RHQ-016RHQ001-N-', '') cIdContrato");
        Sql.append("		 		, REPLACE(REPLACE(REPLACE(LEFT(pCO.cObjetoContrato, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), '') AS cConceptoContrato ");
        Sql.append("		 		, CASE WHEN (YEAR(pCO.fInicio) < YEAR(pCO.fTermino)) THEN 'S' ELSE 'N' END AS ES_PLURIANUAL");
        Sql.append("		 		, '3' AS ACTO_JURIDICO");
        Sql.append("		 		,case isnull(pCO.lEsPlurianual, '2') when '1' then '1' else '2' end AS TEMPORALIDAD");
        Sql.append("		 		,'' AS APROB_PLA");
        Sql.append("		 		,'2' AS CONTRATACION");
        Sql.append("		 		,isnull(pCO.id_precio,'0') AS ESQ_PRECIO");
        Sql.append("		 		,'' AS PRG_ASOC");
        Sql.append("		 		,'' AS BIEN_EXPROP");
        Sql.append("		 		,'' AS ID_CATASTRAL");
        Sql.append("		 		, ISNULL(oCTA.cIdTipoProc, '') AS TPROC ");
        Sql.append("		 		,'' AS POBLACION_OBJ");
        Sql.append("		 		, (SELECT cCodigoMonedaSiaff FROM pCatalogoTipoMoneda WITH (NOLOCK) WHERE cidtipoMoneda = '01')");
        Sql.append("		 		,'1' AS TCAM");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_MONORI");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_EJER");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_MIN");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_MAX");
        Sql.append("		 		,'N' AS CONV_MOD");
        Sql.append("		 		,'' AS NUM_PLAZAS");
        Sql.append("		 		,'' AS VAR_PLAZAS");
        Sql.append("		 		,tCE.caNoCompromiso");
        Sql.append("		 		,tCE.nMes");
        Sql.append("		 		,'' AS ID_CTR_INT");
        Sql.append("		 		,tCE.caNoCompromiso ");
        Sql.append("		   		, convert(varchar(100) ,RTRIM( LTRIM( pCO.nCodExpedienteCNET ) )) AS CODIGO_EXPEDIENTE");
        Sql.append("		 		, SUBSTRING(RTRIM(LTRIM(pCO.cNoProcedimientoCNET)), 1, 30) AS NO_PROCEDIMIENTO");
        Sql.append("		 		, convert(varchar(100) ,pCO.nCodContratoCNET) AS CODIGO_CONTRATO");
        Sql.append("		 		, ISNULL(altaprov.cNombre,'') + ' ' + ISNULL(altaprov.cApellidoPaterno,'') + ' ' + ISNULL(altaprov.cApellidoMaterno,'') AS REPRESENTANTE_LEGAL");
        Sql.append("		 		, ISNULL(CONV.CNOCONVENIO, 'NO APLICA') AS NUM_CONV_MOD");
        Sql.append("		 		, ISNULL(CONVERT(DATE,CONV.FINICIOCONV,103), '') AS FECHA_MOD_CONV");
        Sql.append("		 		, '' AS TTRANS_21");
        Sql.append("		 		, CASE WHEN ISNULL(justCNET.cIdContrato, '') = '' THEN 'S' ELSE 'N' END AS ETIQUETA_COMPRANET");
        Sql.append("		 		, CASE WHEN ISNULL(justCNET.cIdContrato, '') = '' THEN '' ELSE justCNET.cJustificacion END AS JUSTIFICA_COMPRANET");
        Sql.append("		 		, '0' AS IVA_MON_ORIG_414");
        Sql.append("		 		, '0' AS IMP_CONT_SIVA_413");
        Sql.append("		 		, '0' AS IMP_CONV_MOD_415");
        Sql.append("		 		, pco.fFirmaContrato");
        Sql.append("		 		, CASE	WHEN tCE.nFolioCompromiso = (SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = tCE.cIdContrato AND ( cDocumentoHaplicado = 'S' OR ( cDocumentoHaplicado IS NULL AND 'S' = (SELECT PF.cDocumentoHaplicado FROM tPrecomFinancieroEncabezado PF WITH(NOLOCK) WHERE PF.nFolioPrecomFinanciero = nFolioCompromiso ))) ) ");
        Sql.append("		 				THEN '' ");
        Sql.append("		 				ELSE ");
        Sql.append("		 				( ");
        Sql.append("		 					SELECT nFolioAutSICOP FROM tCompromisoEncabezado WITH (NOLOCK) ");
        Sql.append("		 					WHERE nFolioCompromiso = ( SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = tCE.cIdContrato AND ( cDocumentoHaplicado = 'S' OR ( cDocumentoHaplicado IS NULL AND 'S' = (SELECT PF.cDocumentoHaplicado FROM tPrecomFinancieroEncabezado PF WITH(NOLOCK) WHERE PF.nFolioPrecomFinanciero = nFolioCompromiso ))) AND nEnviadoSICOP = 2 ) ");
        Sql.append("		 				) ");
        Sql.append("		 	 	 END AS folioSICOP ");
        Sql.append("		 	 	 , ISNULL(CONVERT(DATE,CONV.FFINCONV,103), '') fTermino_Ultima");
        Sql.append("		  FROM tCompromisoEncabezado tCE WITH (NOLOCK)");
        Sql.append("		  INNER JOIN TCOMPROMISODETALLE tCD WITH (NOLOCK)");
        Sql.append("		  	ON TCE.NFOLIOCOMPROMISO = tCD.NFOLIOCOMPROMISO");
        Sql.append("		  INNER JOIN pContratoObra pCO  WITH (NOLOCK)");
        Sql.append("		  	ON  tCE.cIdContrato = pCO.cIdContrato  AND tCE.cCentroContable = pCO.cIdEntidadContable");
        Sql.append("		  LEFT JOIN oCatalogoTipoAdjudicacion oCTA  WITH (NOLOCK)");
        Sql.append("		 	ON (oCTA.cIdTipoAdjudicacion = pCO.cIdTipoAdjudicacion) ");
        Sql.append("		  LEFT JOIN tContratosJustCNET justCNET WITH (NOLOCK) ");
        Sql.append("		   	ON justCNET.cIdContrato = pCO.cIdContrato ");
        Sql.append("		  INNER JOIN tAltaProveedor altaprov WITH (NOLOCK)");
        Sql.append("		  	ON (REPLACE(altaprov.cIdRFC,'-','') = pCO.cIdRFC)");
        Sql.append("		  INNER JOIN tBeneficiario be WITH (NOLOCK)");
        Sql.append("			ON be.dRFC = pCO.cIdRFC");
        Sql.append("		  LEFT JOIN v_ConvenioModificatorio CONV WITH (NOLOCK) ");
        Sql.append("		   	ON CONV.cIdContratoDefinitivo = tCE.cIdContrato ");
        Sql.append("		  WHERE tCE.caNoCompromiso in (" + lista_Contratos + ") ");
        Sql.append((reimprime ? "" : " AND tCE.nEnviadoSICOP = 0 "));
        Sql.append("		 	AND ( tCE.cDocumentoHaplicado = 'S'  ");
        Sql.append("		        OR ( tCE.cDocumentoHaplicado IS NULL AND 'S' = ( SELECT cDocumentoHaplicado FROM tPrecomFinancieroEncabezado precom WITH(NOLOCK) WHERE nFolioPrecomFinanciero = tCE.nFolioCompromiso ) )  ");
        Sql.append("		        OR ( tCE.cDocumentoHaplicado IS NULL AND 'S' = ( SELECT TOP 1 contratoObra.cDocumentoHaplicado FROM dbo.tObraPublicaCompromisoEncabezado contratoObra WITH(NOLOCK) WHERE contratoObra.cCveContrato = tCE.cIdContrato ) )");
        Sql.append("		     	)  ");
        Sql.append("		  GROUP BY tCD.nFolioCompromiso,tCE.fAplicacion,tCE.fCarga,tCE.cRamo, ");
        Sql.append("		 		 tCE.nFolioCompromiso,tCE.cUnidadResponsable,tCE.cIdContrato, ");
        Sql.append("		 		 be.CBEN,pCO.cIdRFC,pCO.fInicio,pCO.fTermino, tCE.cIdContrato,pCO.cObjetoContrato,");
        Sql.append("		 		 tCE.caNoCompromiso,tCE.nMes,tCE.caNoCompromiso,pCO.cIdTipoContratoObra,");
        Sql.append("		 		 tCE.cUnidadResponsableContable, pCO.cIdEntidadContable, pCO.lEsPlurianual, pCO.cIdTipoAdjudicacion, pCO.id_precio,");
        Sql.append("				 altaprov.cNombre, altaprov.cApellidoPaterno, altaprov.cApellidoMaterno, pco.fFirmaContrato, oCTA.cIdTipoProc, ");
        Sql.append("		 		 pCO.nCodExpedienteCNET, pCO.cNoProcedimientoCNET, pCO.nCodContratoCNET, tCE.cCentroContable, justCNET.cIdContrato, justCNET.cJustificacion");
        Sql.append("		 		  ,cNoConvenio, FINICIOCONV, FFINCONV");
        // CONTRATO FEDERALIZADO
        Sql.append("		  UNION ALL");
        Sql.append("		  ");
        Sql.append("		 SELECT tCE.nFolioCompromiso");
        Sql.append("		 		,'H' AS Header");
        Sql.append("		 		,tCE.fAplicacion");
        Sql.append("		 		,tCE.fCarga");
        Sql.append("		 		,tCE.cRamo");
        Sql.append("		 		,tCE.cRamo");
        Sql.append("		 		,tCE.cRamo");
        Sql.append("		 		,tCE.cUnidadResponsableContable");
        Sql.append("		 		,tCE.cUnidadResponsableContable");
        Sql.append("		 		,tCE.cUnidadResponsableContable");
        Sql.append("				,( SELECT TOP 1 movto FROM vListaCompromisos WHERE canocompromiso = tce.canocompromiso) AS MOVTO");
        Sql.append("		 		,SUM(tCD.mImporte) as mImporteComp");
        Sql.append("		 		,'' AS TipoContratoDiverso");
        Sql.append("		 		,'-1' AS TipoContratoObra");
        Sql.append("		 		,be.CBEN");
        Sql.append("		 		,pCO.cIdRFC");
        Sql.append("		 		,pCO.fInicio");
        Sql.append("		 		,pCO.fTermino");
        Sql.append("		 		,tCE.cIdContrato");
        Sql.append("		 		,REPLACE(REPLACE(REPLACE(LEFT(pCO.cObjetoContrato, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), '') AS cConceptoContrato ");
        Sql.append("		   	    , 'N' AS ES_PLURIANUAL");
        Sql.append("		 		, '1' AS ACTO_JURIDICO");
        Sql.append("		 		,'2' AS TEMPORALIDAD");
        Sql.append("		 		,'' AS APROB_PLA");
        Sql.append("		 		,'2' AS CONTRATACION");
        Sql.append("		 		,isnull(pCO.id_precio,'1') AS ESQ_PRECIO");
        Sql.append("		 		,'' AS PRG_ASOC");
        Sql.append("		 		,'' AS BIEN_EXPROP");
        Sql.append("		 		,'' AS ID_CATASTRAL");
        Sql.append("		 		,'3' AS TPROC");
        Sql.append("		 		,'' AS POBLACION_OBJ");
        Sql.append("		 		, 'MXN'");
        Sql.append("		 		,'1' AS TCAM");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_MONORI");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_EJER");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_MIN");
        Sql.append("		 		,SUM(ABS(tCD.mImporte)) AS MONTO_MAX");
        Sql.append("		 		, 'N' AS CONV_MOD");
        Sql.append("		 		, '' AS NUM_PLAZAS");
        Sql.append("		 		, '' AS VAR_PLAZAS");
        Sql.append("		 		,tCE.caNoCompromiso");
        Sql.append("		 		,tCE.nMes");
        Sql.append("		 		,'' AS ID_CTR_INT");
        Sql.append("		 		,tCE.caNoCompromiso ");
        Sql.append("		 		, '' AS CODIGO_EXPEDIENTE");
        Sql.append("		 		, '' AS NO_PROCEDIMIENTO");
        Sql.append("		 		, '' AS CODIGO_CONTRATO");
        Sql.append("		 		, ISNULL(altaprov.cNombre,'') + ' ' + ISNULL(altaprov.cApellidoPaterno,'') + ' ' + ISNULL(altaprov.cApellidoMaterno,'') AS REPRESENTANTE_LEGAL");
        Sql.append("		 		, 'NO APLICA' AS NUM_CONV_MOD");
        Sql.append("		 		, '' AS FECHA_MOD_CONV");
        Sql.append("		 		, '' AS TTRANS_21");
        Sql.append("		 		, 'N' AS ETIQUETA_COMPRANET");
        Sql.append("		 		, 'ESTE TIPO DE CONTRATO NO TIENE ETIQUETA COMPRANET' AS JUSTIFICA_COMPRANET");
        Sql.append("		 		, '0' AS IVA_MON_ORIG_414");
        Sql.append("		 		, '0' AS IMP_CONT_SIVA_413");
        Sql.append("		 		, '0' AS IMP_CONV_MOD_415");
        Sql.append("		 		, pco.fFirmaContrato");
        Sql.append("		 		, CASE	WHEN tCE.nFolioCompromiso = (SELECT MIN(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cCentroContable = tCE.cCentroContable AND cIdContrato = tCE.cIdContrato AND ( cDocumentoHaplicado = 'S' OR ( cDocumentoHaplicado IS NULL AND 'S' = (SELECT PF.cDocumentoHaplicado FROM tPrecomFinancieroEncabezado PF WITH(NOLOCK) WHERE PF.nFolioPrecomFinanciero = nFolioCompromiso ))) ) ");
        Sql.append("		 				THEN '' ");
        Sql.append("		 				ELSE ");
        Sql.append("		 				( ");
        Sql.append("		 					SELECT nFolioAutSICOP FROM tCompromisoEncabezado WITH (NOLOCK) ");
        Sql.append("		 					WHERE nFolioCompromiso = ( SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = tCE.cIdContrato ");
        Sql.append("							AND ( cDocumentoHaplicado = 'S' OR ( cDocumentoHaplicado IS NULL AND 'S' = (SELECT PF.cDocumentoHaplicado FROM tPrecomFinancieroEncabezado PF WITH(NOLOCK) WHERE PF.nFolioPrecomFinanciero = nFolioCompromiso ))) AND nEnviadoSICOP = 2 AND  nFolioSuficiencia IS NOT NULL) ");
        Sql.append("		 				) ");
        Sql.append("		 	 	 END AS folioSICOP ");
        Sql.append("		 	 	 , '' fTermino_Ultima");
        Sql.append("		  FROM tCompromisoEncabezado tCE WITH (NOLOCK)");
        Sql.append("			INNER JOIN pContratoFEDERALIZADO pCO WITH (NOLOCK)");
        Sql.append("				ON  tCE.cIdContrato = pCO.cIdContrato AND tCE.cCentroContable = pCO.cIdEntidadContable");
        Sql.append("		  	INNER JOIN tAltaProveedor altaprov WITH (NOLOCK)");
        Sql.append("		 		ON (REPLACE(altaprov.cIdRFC,'-','') = pCO.cIdRFC)");
        Sql.append("		 	INNER JOIN tCompromisoDetalle tCD WITH (NOLOCK)");
        Sql.append("				ON tCE.nFolioCompromiso = tCD.nFolioCompromiso ");
        Sql.append("			INNER JOIN tBeneficiario be WITH (NOLOCK)");
        Sql.append("				ON pCO.cIdRFC = be.dRFC 		");
        Sql.append("		  WHERE tCE.caNoCompromiso in (" + lista_Contratos + ") ");
        Sql.append((reimprime ? "" : " AND tCE.nEnviadoSICOP = 0 "));
        Sql.append("		  AND (tCE.cDocumentoHaplicado = 'S'  OR ( tCE.cdocumentohaplicado IS NULL AND 'S' = (SELECT cDocumentoHaplicado FROM tPrecomFinancieroEncabezado precom WITH(NOLOCK) WHERE precom.nFolioPrecomFinanciero = tCE.nFolioCompromiso ) ) )");
        Sql.append("		 GROUP BY tCE.nFolioCompromiso,tCE.fAplicacion,tCE.fCarga,tCE.cRamo, ");
        Sql.append("		 		 tCD.nFolioCompromiso,tCE.cUnidadResponsable,tCE.cIdContrato, ");
        Sql.append("		 		 be.CBEN,pCO.cIdRFC,pCO.fInicio,pCO.fTermino, ");
        Sql.append("		 		 tCE.cIdContrato,pCO.cObjetoContrato,");
        Sql.append("		 		 tCE.caNoCompromiso,tCE.nMes,tCE.caNoCompromiso,pCO.cIdTipoContratoObra,");
        Sql.append("		 		 tCE.cUnidadResponsableContable, pCO.cIdEntidadContable, pCO.id_precio,");
        Sql.append("		 		 pco.fFirmaContrato, tCE.cCentroContable, altaprov.cNombre, altaprov.cApellidoPaterno, altaprov.cApellidoMaterno");
        // RELACION DE GASTOS INTEGRADAS
        Sql.append("   UNION ALL  ");
        Sql.append("    ");
        Sql.append("		      SELECT tCE.nfoliocompromiso,  ");
        Sql.append("		          'H'                                     AS Header,  ");
        Sql.append("		          tCE.faplicacion,  ");
        Sql.append("		          tCE.fcarga,  ");
        Sql.append("		          tCE.cramo,  ");
        Sql.append("		          tCE.cramo,  ");
        Sql.append("		          tCE.cramo,  ");
        Sql.append("		          tCE.cUnidadResponsableContable,  ");
        Sql.append("		          tCE.cUnidadResponsableContable,");
        Sql.append("		          tCE.cUnidadResponsableContable,");
        Sql.append("		          'O'			                           AS MOVTO,  ");
        Sql.append("		          SUM(tCD.mimporte)                       AS mImporteComp,  ");
        Sql.append("		          7                                       AS TipoContratoDiverso,  ");
        Sql.append("		          ''                                      AS TipoContratoObra,  ");
        Sql.append("		          'S04929'                                AS cben,  ");
        Sql.append("		          'CNF010405EG1'                          AS cidrfc,  ");
        Sql.append("		          Getdate()                               AS fcontratoini,  ");
        Sql.append("		          DATEADD(DAY,5,GETDATE() )               AS fcontratofin,  ");
        Sql.append("		          tCE.cidcontrato,  ");
        Sql.append("		          REPLACE(REPLACE(REPLACE(LEFT(tCE.cDescripcionPoliza, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), '')  AS cConceptoContrato,  ");
        Sql.append("		          'N'                                      AS ES_PLURIANUAL,  ");
        Sql.append("		          ''                                       AS ACTO_JURIDICO,  ");
        Sql.append("		          '2'                                      AS TEMPORALIDAD,  ");
        Sql.append("		          ''                                       AS APROB_PLA,  ");
        Sql.append("		   		  '0'                                      AS CONTRATACION,  ");
        Sql.append("		           0                                       AS ESQ_PRECIO,  ");
        Sql.append("		          ''                                       AS PRG_ASOC,  ");
        Sql.append("		          ''                                       AS BIEN_EXPROP,  ");
        Sql.append("		          ''                                       AS ID_CATASTRAL,  ");
        Sql.append("		          '0'                                      AS TPROC,  ");
        Sql.append("		          ''                                       AS POBLACION_OBJ,  ");
        Sql.append("		         'MXN' ,  ");
        Sql.append("		          '1'                                     AS TCAM,  ");
        Sql.append("		          SUM(Abs(tCD.mimporte))                  AS MONTO_MONORI,  ");
        Sql.append("		          SUM(Abs(tCD.mimporte))                  AS MONTO_EJER,  ");
        Sql.append("		          SUM(Abs(tCD.mimporte))                  AS MONTO_MIN,  ");
        Sql.append("		          SUM(Abs(tCD.mimporte))                  AS MONTO_MAX,  ");
        Sql.append("		          'N'                                     AS CONV_MOD,  ");
        Sql.append("		          ''                             		 AS NUM_PLAZAS,  ");
        Sql.append("		          ''                                      AS VAR_PLAZAS,  ");
        Sql.append("		          tCE.canocompromiso,  ");
        Sql.append("		          tCE.nmes,  ");
        Sql.append("		          ''                                      AS ID_CTR_INT,  ");
        Sql.append("		          tCE.canocompromiso, ");
        Sql.append("		          ''                                      AS CODIGO_EXPEDIENTE,  ");
        Sql.append("		          ''                                      AS NO_PROCEDIMIENTO,  ");
        Sql.append("		          ''                                      AS CODIGO_CONTRATO, ");
        Sql.append("		          'TANIA ANANI LIMON MAGAÑA'              AS REPRESENTANTE_LEGAL,  ");
        Sql.append("		          'NO APLICA'                             AS NUM_CONV_MOD,  ");
        Sql.append("		          ''                                      AS FECHA_MOD_CONV,  ");
        Sql.append("		          ''                                      AS TTRANS_21,  ");
        Sql.append("		          'N'                                     AS ETIQUETA_COMPRANET,  ");
        Sql.append("		          'REPOSICION DE FONDO REVOLVENTE'        AS JUSTIFICA_COMPRANET,  ");
        Sql.append("		          '0'                                     AS IVA_MON_ORIG_414,  ");
        Sql.append("		          '0'                                     AS IMP_CONT_SIVA_413,  ");
        Sql.append("		          '0'                                     AS IMP_CONV_MOD_415,  ");
        Sql.append("		          GETDATE() -1                              AS ffirmacontrato,  ");
        Sql.append("		  	 	   '' AS folioSICOP,  ");
        Sql.append("		  	 	   '' fTermino_Ultima  ");
        Sql.append("		   FROM   tcompromisoencabezado tCE WITH (NOLOCK)");
        Sql.append("				  INNER JOIN tcompromisodetalle tCD WITH (NOLOCK)");
        Sql.append("					ON tCE.nfoliocompromiso = tCD.nfoliocompromiso   ");
        Sql.append("		   WHERE  tCE.canocompromiso IN ( " + lista_Contratos + " )  ");
        Sql.append((reimprime ? "" : "        AND tCE.nenviadosicop = 0 "));
        Sql.append("				  AND tCE.cTipoContrato = 'RE'  ");
        Sql.append("		          AND tCE.cDocumentoHaplicado = 'S'  ");
        Sql.append("		   GROUP  BY tCE.nfoliocompromiso,  tCE.faplicacion,  ");
        Sql.append("				tCE.fcarga,  tCE.cramo,  tCD.nfoliocompromiso,  ");
        Sql.append("				tCE.cunidadresponsable,  tCE.cidcontrato,  tCE.cidcontrato,  ");
        Sql.append("				tCE.canocompromiso,  tCE.nmes,  tCE.canocompromiso,  ");
        Sql.append("				tCE.cUnidadResponsableContable, tCE.ccentrocontable,  cDescripcionPoliza");
        Sql.append("	UNION ALL	");
        // PAGO DIRECTO DE NOMINA
        Sql.append("	SELECT tCE.nfoliocompromiso, ");
        Sql.append("			'H' AS Header, ");
        Sql.append("			tCE.faplicacion, ");
        Sql.append("			tCE.fcarga, ");
        Sql.append("			tCE.cramo, ");
        Sql.append("			tCE.cramo, ");
        Sql.append("			tCE.cramo, ");
        Sql.append("			tCEP.cunidadresponsableep, ");
        Sql.append("			tCEP.cunidadresponsableep, ");
        Sql.append("			tCEP.cunidadresponsableep, ");
        Sql.append("			'O'	AS MOVTO, ");
        Sql.append("			SUM(tCD.mimporte) AS mImporteComp, ");
        Sql.append("			7 AS TipoContratoDiverso, ");
        Sql.append("			'' AS TipoContratoObra, ");
        Sql.append("			CBEN, ");
        Sql.append("			dRFC AS cidrfc, ");
        Sql.append("			GETDATE() AS fcontratoini, ");
        Sql.append("			DATEADD(DAY, 5, GETDATE()) AS fcontratofin, ");
        Sql.append("			tCE.cidcontrato, ");
        Sql.append("			REPLACE(REPLACE(REPLACE(REPLACE(LEFT(tCE.cDescripcionPoliza, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), ''), ',',  '') AS cConceptoContrato, ");
        Sql.append("			'N' AS ES_PLURIANUAL, ");
        Sql.append("			'' AS ACTO_JURIDICO, ");
        Sql.append("			'2' AS TEMPORALIDAD, ");
        Sql.append("			'' AS APROB_PLA, ");
        Sql.append("			'0' AS CONTRATACION, ");
        Sql.append("			0 AS ESQ_PRECIO, ");
        Sql.append("			'' AS PRG_ASOC, ");
        Sql.append("			'' AS BIEN_EXPROP, ");
        Sql.append("			'' AS ID_CATASTRAL, ");
        Sql.append("			'0' AS TPROC, ");
        Sql.append("			'' AS POBLACION_OBJ, ");
        Sql.append("			pCTM.ccodigomonedasiaff, ");
        Sql.append("			'1' AS TCAM, ");
        Sql.append("			SUM(ABS(tCD.mimporte)) AS MONTO_MONORI, ");
        Sql.append("			SUM(ABS(tCD.mimporte)) AS MONTO_EJER, ");
        Sql.append("			SUM(ABS(tCD.mimporte)) AS MONTO_MIN, ");
        Sql.append("			SUM(ABS(tCD.mimporte)) AS MONTO_MAX, ");
        Sql.append("			'N' AS CONV_MOD, ");
        Sql.append("			'' AS NUM_PLAZAS, ");
        Sql.append("			'' AS VAR_PLAZAS, ");
        Sql.append("			tCE.canocompromiso, ");
        Sql.append("  			tCE.nmes, ");
        Sql.append("  			'' AS ID_CTR_INT, ");
        Sql.append("			tCE.canocompromiso, ");
        Sql.append("			'' AS CODIGO_EXPEDIENTE, ");
        Sql.append("			'' AS NO_PROCEDIMIENTO, ");
        Sql.append("			'' AS CODIGO_CONTRATO, ");
        Sql.append("			ISNULL(altaprov.dNombre, '') + ' ' + ISNULL(altaprov.dApellidoPaterno, '') + ' ' + ISNULL(altaprov.dApellidoMaterno, '') AS REPRESENTANTE_LEGAL, ");
        Sql.append("			'NO APLICA' AS NUM_CONV_MOD, ");
        Sql.append("			'' AS FECHA_MOD_CONV, ");
        Sql.append("			'' AS TTRANS_21, ");
        Sql.append("			'N' AS ETIQUETA_COMPRANET, ");
        Sql.append("			'ESTE TIPO DE CONTRATO NO TIENE ETIQUETA COMPRANET' AS JUSTIFICA_COMPRANET, ");
        Sql.append("			'0' AS IVA_MON_ORIG_414, ");
        Sql.append("			'0' AS IMP_CONT_SIVA_413, ");
        Sql.append("			'0' AS IMP_CONV_MOD_415, ");
        Sql.append("			GETDATE() - 1 AS ffirmacontrato, ");
        Sql.append("			ISNULL(tce.nFolioAutSICOP,'') AS folioSICOP, ");
        Sql.append("			'' fTermino_Ultima ");
        Sql.append("	FROM tcompromisoencabezado tCE WITH (NOLOCK), ");
        Sql.append("		tcompromisodetalle tCD WITH (NOLOCK), ");
        Sql.append("	    pcatalogotipomoneda pCTM WITH (NOLOCK), ");
        Sql.append("		tcatalogoep tCEP WITH (NOLOCK), ");
        Sql.append("		tPDNominaCompromisoEncabezado tRGCE WITH (NOLOCK), ");
        Sql.append("		tPDNominaCompromisoDetalle tRGCD WITH (NOLOCK), ");
        Sql.append("		tPagoDirectoEncabezado PDIR WITH (NOLOCK) ");
        Sql.append("	INNER JOIN tBeneficiario altaprov WITH (NOLOCK) ");
        Sql.append("		ON altaprov.dRFC = PDIR.cIdRFC ");
        Sql.append("	WHERE tCE.canocompromiso IN ( " + lista_Contratos + " ) ");
        Sql.append("		AND pCTM.ccodigomonedasiaff = 'MXN' ");
        Sql.append("		AND tCE.nfoliocompromiso = tCD.nfoliocompromiso ");
        Sql.append("		AND tCEP.ep = tCD.ep " + (reimprime ? "" : " AND tCE.nenviadosicop = 0 "));
        Sql.append("		AND tCE.cTipoContrato = 'RE' ");
        Sql.append("		AND tCE.cDocumentoHaplicado = 'S' ");
        Sql.append("		AND tCE.nFolioCompromiso = tRGCE.nFolioCompromiso ");
        Sql.append("		AND tRGCE.nFolioPDNominaCompromiso = tRGCD.nFolioPDNominaCompromiso ");
        Sql.append("		AND tRGCD.nFolioPagoDirecto = PDIR.nFolioPagoDirecto ");
        Sql.append("	GROUP BY tCE.nfoliocompromiso, ");
        Sql.append("       tCE.faplicacion, ");
        Sql.append("       tCE.fcarga, ");
        Sql.append("       tCE.cramo, ");
        Sql.append("       tCD.nfoliocompromiso, ");
        Sql.append("       tCE.cunidadresponsable, ");
        Sql.append("       tCE.cidcontrato, ");
        Sql.append("       tCE.cidcontrato, ");
        Sql.append("       pCTM.ccodigomonedasiaff, ");
        Sql.append("       tCE.canocompromiso, ");
        Sql.append("       tCE.nmes, ");
        Sql.append("       tCE.canocompromiso,");
        Sql.append("       tCEP.cunidadresponsableep, ");
        Sql.append("       tCE.ccentrocontable, ");
        Sql.append("       tCE.cDescripcionPoliza, ");
        Sql.append("       altaprov.dNombre, ");
        Sql.append("       altaprov.dApellidoPaterno, ");
        Sql.append("       altaprov.dApellidoMaterno, ");
        Sql.append("       tce.nFolioAutSICOP, ");
        Sql.append("       CBEN, ");
        Sql.append("       dRFC	");
        Sql.append("       UNION ALL	");
        // PAGO DIRECTO CON COMPROMISO
        Sql.append(" SELECT tCE.nfoliocompromiso,  ");
        Sql.append("          'H'                                     AS Header,  ");
        Sql.append("          tCE.faplicacion,  ");
        Sql.append("          tCE.fcarga,  ");
        Sql.append("          tCE.cramo,  ");
        Sql.append("          tCE.cramo,  ");
        Sql.append("          tCE.cramo,  ");
        Sql.append("          tCE.cUnidadResponsableContable,  ");
        Sql.append("          tCE.cUnidadResponsableContable,  ");
        Sql.append("          tCE.cUnidadResponsableContable,  ");
        Sql.append("          'O' 			                          AS MOVTO,  ");
        Sql.append("          SUM(tCD.mimporte)                       AS mImporteComp,  ");
        Sql.append("          7                                       AS TipoContratoDiverso,  ");
        Sql.append("          ''                                      AS TipoContratoObra,  ");
        Sql.append("          (select top 1  CBEN from tBeneficiario with (nolock) where dRFC = PDIR.cIdRFC)    AS cben,   ");
        Sql.append("          cIdRFC      			                  AS cidrfc,    ");
        Sql.append("          Getdate()                               AS fcontratoini,  ");
        Sql.append("          DATEADD(DAY,5,GETDATE() )               AS fcontratofin,  ");
        Sql.append("          tCE.cidcontrato,  ");
        Sql.append("          REPLACE(REPLACE(REPLACE(REPLACE(LEFT(tCE.cDescripcionPoliza, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), ''), ',' , '')  AS cConceptoContrato,  ");
        Sql.append("          'N'                                      AS ES_PLURIANUAL,  ");
        Sql.append("          ''                                       AS ACTO_JURIDICO,  ");
        Sql.append("          '2'                                      AS TEMPORALIDAD,  ");
        Sql.append("          ''                                       AS APROB_PLA,  ");
        Sql.append("   		  '0'                                      AS CONTRATACION,  ");
        Sql.append("           0                                       AS ESQ_PRECIO,  ");
        Sql.append("          ''                                       AS PRG_ASOC,  ");
        Sql.append("          ''                                       AS BIEN_EXPROP,  ");
        Sql.append("          ''                                       AS ID_CATASTRAL,  ");
        Sql.append("          '0'                                      AS TPROC,  ");
        Sql.append("          ''                                       AS POBLACION_OBJ,  ");
        Sql.append("          pCTM.ccodigomonedasiaff,  ");
        Sql.append("          '1'                                     AS TCAM,  ");
        Sql.append("          SUM(Abs(tCD.mimporte))                  AS MONTO_MONORI,  ");
        Sql.append("          SUM(Abs(tCD.mimporte))                  AS MONTO_EJER,  ");
        Sql.append("          SUM(Abs(tCD.mimporte))                  AS MONTO_MIN,  ");
        Sql.append("          SUM(Abs(tCD.mimporte))                  AS MONTO_MAX,  ");
        Sql.append("          'N'                                     AS CONV_MOD,  ");
        Sql.append("          ''                             		  AS NUM_PLAZAS,  ");
        Sql.append("          ''                                      AS VAR_PLAZAS,  ");
        Sql.append("          tCE.canocompromiso,  ");
        Sql.append("          tCE.nmes,  ");
        Sql.append("          ''                                      AS ID_CTR_INT,  ");
        Sql.append("          tCE.canocompromiso, ");
        Sql.append("          ''                                      AS CODIGO_EXPEDIENTE,  ");
        Sql.append("          ''                                      AS NO_PROCEDIMIENTO,  ");
        Sql.append("          ''                                      AS CODIGO_CONTRATO, ");
        Sql.append("       (SELECT CASE WHEN cIdTipoPersonaRFC = 2  ");
        Sql.append("         	THEN ISNULL(REPLACE(dNombre, ',', ''),'') + ' ' + ISNULL(replace(dApellidoPaterno, ',', ''),'') + ' ' + ISNULL(replace(dApellidoMaterno, ',', ''),'') ");
        Sql.append("         	ELSE ISNULL(REPLACE(dNombreApoderado, ',', '')+ ' ' + replace(dAPaternoApoderado, ',','') + ' ' + replace(dAMaternoApoderado, ',',''),  ");
        Sql.append("        	(SELECT cNombre + ' ' + cApellidoPaterno + ' ' + cApellidoMaterno FROM tAltaProveedor WITH (NOLOCK) WHERE REPLACE(cIdRFC , '-', '') = BEN.dRFC )) END  ");
        Sql.append("        	FROM tBeneficiario BEN WITH (NOLOCK) WHERE dRFC = pdir.cIdRFC)  AS REPRESENTANTE_LEGAL  , ");
        Sql.append("          'NO APLICA'                             AS NUM_CONV_MOD,  ");
        Sql.append("          ''                                      AS FECHA_MOD_CONV,  ");
        Sql.append("          ''                                      AS TTRANS_21,  ");
        Sql.append("          'N'                                     AS ETIQUETA_COMPRANET,  ");
        Sql.append("          (SELECT REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LTRIM(RTRIM(cDescripcionPoliza)), CHAR(160), ' '), '  ', ' ') , ',', ''), CHAR(10), ''),CHAR(9), ''),CHAR(13), '')  FROM tpagodirectoencabezado WITH (NOLOCK) WHERE caNoContrarrecibo = tCE.cIdContrato)       AS JUSTIFICA_COMPRANET,    ");
        Sql.append("          '0'                                     AS IVA_MON_ORIG_414,  ");
        Sql.append("          '0'                                     AS IMP_CONT_SIVA_413,  ");
        Sql.append("          '0'                                     AS IMP_CONV_MOD_415,  ");
        Sql.append("          GETDATE() -1                            AS ffirmacontrato  ,");
        Sql.append("  	 	   '' AS folioSICOP  ");
        Sql.append("  	 	 , '' fTermino_Ultima  ");
        Sql.append(" 	FROM   tcompromisoencabezado tCE WITH (NOLOCK) ");
        Sql.append("	INNER JOIN  tcompromisodetalle tCD WITH (NOLOCK) ");
        Sql.append("	ON tCE.nfoliocompromiso = tCD.nfoliocompromiso ");
        Sql.append("	INNER JOIN tPagoDirectoEncabezado PDIR WITH (NOLOCK) ");
        Sql.append("	ON PDIR.CANOCONTRARRECIBO = TCE.CIDCONTRATO, ");
        Sql.append("	pcatalogotipomoneda pCTM WITH (NOLOCK) ");
        Sql.append("	WHERE  tCE.canocompromiso IN   ( " + lista_Contratos + ")");
        Sql.append("	AND pCTM.ccodigomonedasiaff = 'MXN' ");
        Sql.append((reimprime ? "" : " AND tCE.nenviadosicop = 0 "));
        Sql.append("	          AND tCE.cTipoContrato = 'PD' ");
        Sql.append("	AND tCE.cDocumentoHaplicado = 'S' ");
        Sql.append("	GROUP  BY tCE.nfoliocompromiso,  tCE.faplicacion,  tCE.fcarga, ");
        Sql.append("			tCE.cramo,  tCD.nfoliocompromiso,  tCE.cunidadresponsable,  ");
        Sql.append("			tCE.cidcontrato,  tCE.cidcontrato,  pCTM.ccodigomonedasiaff,  ");
        Sql.append("			tCE.canocompromiso,  tCE.nmes,  tCE.canocompromiso,  ");
        Sql.append("			tCE.cUnidadResponsableContable, tCE.ccentrocontable,  tce.cDescripcionPoliza, PDIR.cIdRFC ");
        log.debug(Sql);
        pst = conn.prepareStatement(Sql.toString());
        rs = pst.executeQuery();
        return rs;
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String lista_Contratos, boolean reimprime, int esCalendario) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        rs = generaQueryCompromiso(conn, lista_Contratos, reimprime);
        while (rs.next()) {
            String nFolioCompromiso = rs.getString("nFolioCompromiso");
            SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaALayout = fecha.format(sdf.parse(rs.getString("fAplicacion")));
            String fechaLayout = fecha.format(sdf.parse(rs.getString("fCarga")));
            String fContratoIni = fecha.format(sdf.parse(rs.getString(17)));
            String fContratoFin = fecha.format(sdf.parse(rs.getString(18)));
            String fContratoFirma = fecha.format(sdf.parse(rs.getString("fFirmaContrato")));
            String fConvenio = fecha.format(sdf.parse(rs.getString("FECHA_MOD_CONV")));
            String fContratoModifica = StringUtils.isEmpty(rs.getString("fTermino_Ultima")) ? "" : fecha.format(sdf.parse(rs.getString("fTermino_Ultima")));
            String anio = "";
            if (!StringUtils.isBlank(fContratoModifica)) {
                anio = fContratoModifica.substring(6, 10);
                if ("1900".equals(anio)) {
                    fContratoModifica = "";
                }
            }
            if (!StringUtils.isBlank(fConvenio)) {
                anio = fConvenio.substring(6, 10);
                if ("1900".equals(anio)) {
                    fConvenio = "";
                }
            }
            String tipo = rs.getString("MOVTO");
            String pluri = rs.getString("ES_PLURIANUAL");
            String anioIni = fContratoIni.substring(6, 10);
            String anioApl = fechaALayout.substring(6, 10);
            // Si el año de aplicación es diferente del contrato ponerle que es
            // de año anterior
            if (!anioIni.equals(anioApl) && !pluri.equals("S")) {
                pluri = "AA";
            }
            // Si la fecha del convenio modificatorio es diferente que año
            // tambien se identifica como año anterior
            if (!"".equals(fContratoModifica)) {
                if (!anioIni.equals(anio) && !pluri.equals("S")) {
                    pluri = "AA";
                }
            }
            BigDecimal importe = rs.getBigDecimal("mImporteComp");
            String tipoMovto = "";
            if (esCalendario == 1) {
                tipoMovto = "C";
            } else {
                tipoMovto = tipo;
            }
            String compromiso = "2";
            String erogacion = "";
            Integer tipoContratoD = 0;
            Integer tipoContratoO = 0;
            if (!"".equals(rs.getString("TipoContratoDiverso")) && rs.getString("TipoContratoDiverso") != null) {
                tipoContratoD = Integer.parseInt(rs.getString("TipoContratoDiverso"));
            }
            if (!"".equals(rs.getString("TipoContratoObra")) && rs.getString("TipoContratoObra") != null) {
                tipoContratoO = Integer.parseInt(rs.getString("TipoContratoObra"));
            }
            switch(tipoContratoD) {
                case 1:
                case 4:
                    // bien
                    erogacion = "1";
                    break;
                case 2:
                case 3:
                case 6:
                case 7:
                    // servicio
                    erogacion = "2";
                    break;
                case 5:
                    // obra
                    erogacion = "3";
                    break;
                case 12:
                    // viáticos
                    erogacion = "12";
                    break;
                case -1:
                case 8:
                    // federalizados
                    erogacion = "4";
                    break;
                default:
                    // obra
                    erogacion = "3";
                    break;
            }
            if (tipoContratoO == -1) {
                // federalizados
                erogacion = "4";
            }
            String tipoOp = "1";
            DecimalFormat df = new DecimalFormat("#.00");
            String montoMonori = df.format(rs.getDouble("MONTO_MONORI"));
            String montoEjer = df.format(rs.getDouble("MONTO_EJER"));
            String montoMin = df.format(rs.getDouble("MONTO_MIN"));
            String montoMax = df.format(rs.getDouble("MONTO_MAX"));
            String folioSICOP = rs.getString("folioSICOP");
            String sFolioSICOP = (folioSICOP != null ? folioSICOP.trim() : "");
            StringBuilder encabezado = new StringBuilder();
            // A
            encabezado.append(rs.getString("Header").trim()).append(",").append(// B
            fechaALayout.trim()).append(",").append(// C
            fechaLayout.trim()).append(",").append(// D
            rs.getString("cRamo").trim()).append(",").append(// E
            rs.getString("cRamo").trim()).append(",").append(// F
            rs.getString("cRamo").trim()).append(",").append(// G
            rs.getString("cUnidadResponsableEP").trim()).append(",").append(// H
            rs.getString("cUnidadResponsableEP").trim()).append(",").append(// I
            rs.getString("cUnidadResponsableEP").trim()).append(",").append(// J
            tipoMovto).append(",").append(// K
            compromiso).append(",").append(// L
            erogacion).append(",").append(// M
            sFolioSICOP).append(",").append(// N
            tipoOp).append(",").append(// O
            rs.getString("cIdContrato").trim()).append(",").append(// P
            rs.getString("cConceptoContrato").trim().replaceAll(",", " ")).append(",").append(// Q
            rs.getString("CBEN").trim()).append(",").append(// R
            rs.getString("cIdRFC").trim()).append(",").append(// S
            rs.getString("REPRESENTANTE_LEGAL").trim()).append(",").append(// T
            rs.getString("TPROC")).append(",").append(// U
            rs.getString("ESQ_PRECIO")).append(",").append(// V
            rs.getString("CONTRATACION")).append(",").append(// W
            fContratoIni.trim()).append(",").append(// X
            fContratoFin.trim()).append(",").append(// Y
            fContratoFirma.trim()).append(",").append(// Z
            pluri.trim()).append(",").append(// AA
            rs.getString("APROB_PLA")).append(",").append(// AB
            rs.getString("ACTO_JURIDICO")).append(",").append(// AC
            montoMonori).append(",").append(// AD
            rs.getString("cCodigoMonedaSiaff")).append(",").append(// AE
            rs.getString("TCAM")).append(",").append(// AF
            montoEjer).append(",").append(// AG
            montoMin).append(",").append(// AH
            montoMax).append(",").append(// AI
            rs.getString("CONV_MOD")).append(",").append(// AJ
            rs.getString("NUM_CONV_MOD")).append(",").append(// AK
            fConvenio).append(",").append(// AL
            org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("CODIGO_EXPEDIENTE"))).append(",").append(// AM
            org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("NO_PROCEDIMIENTO"))).append(",").append(// AN
            org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("CODIGO_CONTRATO"))).append(",").append(// AO
            rs.getString("nMes").trim()).append(",").append(// AP
            rs.getString("caNoCompromiso")).append(",").append(// AQ
            rs.getString("TTRANS_21"));
            if (esCalendario == 0) {
                // AR
                encabezado.append(",").append(rs.getString("caNoCompromiso"));
                // AS
                encabezado.append(",").append(rs.getString("caNoCompromiso"));
            }
            // AT
            encabezado.append(",").append(rs.getString("ETIQUETA_COMPRANET"));
            // AU
            encabezado.append(",").append(rs.getString("JUSTIFICA_COMPRANET"));
            // AV
            encabezado.append(",").append(rs.getString("IVA_MON_ORIG_414"));
            // AW
            encabezado.append(",").append(rs.getString("IMP_CONT_SIVA_413"));
            // AX
            encabezado.append(",").append(rs.getString("IMP_CONV_MOD_415"));
            // AY
            encabezado.append(",").append(fContratoModifica.trim());
            encabezado.append("\r\n");
            arrListaComp.add(encabezado.toString());
            // Generar Detalle de compromiso
            rs2 = generaQueryDetalleComp(conn, nFolioCompromiso, reimprime);
            while (rs2.next()) {
                StringBuilder detalle = new StringBuilder();
                BigDecimal mImporteDet = rs2.getBigDecimal("Importe");
                if (esCalendario == 0) {
                    // ID_EVENTO
                    detalle.append(rs2.getString("ID_EVENTO")).append(",").append(// EVENTO
                    rs2.getString("EVENTO"));
                } else {
                    // ID_EVENTO
                    detalle.append(rs2.getString("ID_EVENTO_CAL")).append(",").append(// EVENTO
                    rs2.getString("EVENTO_CAL"));
                }
                // tCEP.cRamo
                detalle.append(",").append(rs2.getString("cRamo").trim()).append(",").append(// tCEP.cUnidadResponsableEP
                rs2.getString("cUnidadResponsableEP").trim()).append(",").append(// tCEP.aEjercicioFiscal
                rs2.getString("aEjercicioFiscal").trim()).append(",").append(// tCEP.cGrupoFuncional
                rs2.getString("cGrupoFuncional").trim()).append(",").append(// tCEP.cFuncion
                rs2.getString("cFuncion").trim()).append(",").append(// tCEP.cSubFuncion
                rs2.getString("cSubFuncion").trim()).append(",").append(// tCEP.cProgramaGeneral
                rs2.getString("cProgramaGeneral")).append(",").append(// tCEP.cActividadInstitucional
                rs2.getString("cActividadInstitucional")).append(",").append(// tCEP.cProgramaPresupuestario
                rs2.getString("cProgramaPresupuestario")).append(",").append(// SUBSTRING(tCEP.cPartida,1,1)
                rs2.getString(12)).append(",").append(// SUBSTRING(tCEP.cPartida,2,1)
                rs2.getString(13)).append(",").append(// SUBSTRING(tCEP.cPartida,3,1)
                rs2.getString(14)).append(",").append(// SUBSTRING(tCEP.cPartida,4,2)
                rs2.getString(15)).append(",").append(// tCEP.cTipoGasto
                rs2.getString("cTipoGasto")).append(",").append(// tCEP.cFuenteFinanciamiento
                rs2.getString("cFuenteFinanciamiento")).append(",").append(// tCEP.cEntidadFederativa
                rs2.getString("cEntidadFederativa")).append(",").append(// tCEP.cCartera
                rs2.getString("cCartera")).append(",").append(// CAU
                rs2.getString("CAU")).append(",").append(// COP
                rs2.getString("COP")).append(",").append(// PL
                rs2.getString("PL")).append(",").append(// OF_
                rs2.getString("OF_")).append(",").append(// AUX1
                rs2.getString("AUX1")).append(",").append(// AUX2
                rs2.getString("AUX2")).append(",").append(// AUX3
                rs2.getString("AUX3")).append(",").append(// Suficiencia
                rs2.getString("Suficiencia")).append(",").append(// Sol_OLI
                rs2.getString("Sol_OLI")).append(",").append(// Enero
                rs2.getString("Enero")).append(",").append(// Febrero
                rs2.getString("Febrero")).append(",").append(// Marzo
                rs2.getString("Marzo")).append(",").append(// Abril
                rs2.getString("Abril")).append(",").append(// Mayo
                rs2.getString("Mayo")).append(",").append(// Junio
                rs2.getString("Junio")).append(",").append(// Julio
                rs2.getString("Julio")).append(",").append(// Agosto
                rs2.getString("Agosto")).append(",").append(// Septiembre
                rs2.getString("Septiembre")).append(",").append(// Octubre
                rs2.getString("Octubre")).append(",").append(// Noviembre
                rs2.getString("Noviembre")).append(",").append(// Diciembre
                rs2.getString("Diciembre")).append(",").append(// Importe
                mImporteDet);
                if (esCalendario == 1) {
                    detalle.append(",").append(rs2.getString("tipoCalendario"));
                }
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
        }
        CloseObject.closeObject(rs);
        CloseObject.closeObject(rs2);
        CloseObject.closeObject(pstmntD);
        return arrListaComp;
    }

    public static ArrayList<String> BuscaCompromisosIntegrados(Connection conn, String lista_Contratos, boolean reimprime, int esCalendario, String cxpIntegrada) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        rs = generaQueryCompromisoIntegrada(conn, cxpIntegrada, reimprime);
        while (rs.next()) {
            String cxpInt = rs.getString("caNoIntegradaComp");
            SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaALayout = fecha.format(sdf.parse(rs.getString("fAplicacion")));
            String fechaLayout = fecha.format(sdf.parse(rs.getString("fCarga")));
            String fContratoIni = fecha.format(sdf.parse(rs.getString("fContratoIni")));
            String fContratoFin = fecha.format(sdf.parse(rs.getString("fContratoFin")));
            String fContratoFirma = fecha.format(sdf.parse(rs.getString("fFirmaContrato")));
            String fConvenio = fecha.format(sdf.parse(rs.getString("FECHA_MOD_CONV")));
            String fContratoModifica = StringUtils.isEmpty(rs.getString("fTermino_Ultima")) ? "" : fecha.format(sdf.parse(rs.getString("fTermino_Ultima")));
            String anio = "";
            if (!StringUtils.isBlank(fContratoModifica)) {
                anio = fContratoModifica.substring(6, 10);
                if ("1900".equals(anio)) {
                    fContratoModifica = "";
                }
            }
            if (!StringUtils.isBlank(fConvenio)) {
                anio = fConvenio.substring(6, 10);
                if ("1900".equals(anio)) {
                    fConvenio = "";
                }
            }
            String tipo = rs.getString("MOVTO");
            String pluri = rs.getString("ES_PLURIANUAL");
            String anioIni = fContratoIni.substring(6, 10);
            String anioApl = fechaALayout.substring(6, 10);
            // Si el año de aplicación es diferente del contrato ponerle que es
            // de año anterior
            if (!anioIni.equals(anioApl) && !pluri.equals("S")) {
                pluri = "AA";
            }
            // Si la fecha del convenio modificatorio es diferente que año
            // tambien se identifica como año anterior
            if (!"".equals(fContratoModifica)) {
                if (!anioIni.equals(anio) && !pluri.equals("S")) {
                    pluri = "AA";
                }
            }
            BigDecimal importe = rs.getBigDecimal("mImporteComp");
            String tipoMovto = "";
            if (esCalendario == 1) {
                tipoMovto = "C";
            } else {
                if ("A".equals(tipo) && importe.compareTo(new BigDecimal(0.00f)) < 0) {
                    tipoMovto = "R";
                } else if ("A".equals(tipo) && importe.compareTo(new BigDecimal(0.00f)) > 0.0) {
                    tipoMovto = "A";
                }
                if ("O".equals(tipo)) {
                    tipoMovto = "O";
                }
            }
            String compromiso = "2";
            String erogacion = "";
            Integer tipoContratoD = 0;
            Integer tipoContratoO = 0;
            if (!"".equals(rs.getString("TipoContratoDiverso")) && rs.getString("TipoContratoDiverso") != null) {
                tipoContratoD = Integer.parseInt(rs.getString("TipoContratoDiverso"));
            }
            if (!"".equals(rs.getString("TipoContratoObra")) && rs.getString("TipoContratoObra") != null) {
                tipoContratoO = Integer.parseInt(rs.getString("TipoContratoObra"));
            }
            switch(tipoContratoD) {
                case 1:
                case 4:
                    // bien
                    erogacion = "1";
                    break;
                case 2:
                case 3:
                case 6:
                case 7:
                    // servicio
                    erogacion = "2";
                    break;
                case 5:
                    // obra
                    erogacion = "3";
                    break;
                case 12:
                    // viáticos
                    erogacion = "12";
                    break;
                case -1:
                case 8:
                    // federalizados
                    erogacion = "4";
                    break;
                default:
                    // obra
                    erogacion = "3";
                    break;
            }
            if (tipoContratoO == -1) {
                // federalizados
                erogacion = "4";
            }
            String tipoOp = "1";
            DecimalFormat df = new DecimalFormat("#.00");
            String montoMonori = df.format(rs.getDouble("MONTO_MONORI"));
            String montoEjer = df.format(rs.getDouble("MONTO_EJER"));
            String montoMin = df.format(rs.getDouble("MONTO_MIN"));
            String montoMax = df.format(rs.getDouble("MONTO_MAX"));
            String folioSICOP = rs.getString("folioSICOP");
            String sFolioSICOP = (folioSICOP != null ? folioSICOP.trim() : "");
            StringBuilder encabezado = new StringBuilder();
            // A
            encabezado.append(rs.getString("Header").trim()).append(",").append(// B
            fechaALayout.trim()).append(",").append(// C
            fechaLayout.trim()).append(",").append(// D
            rs.getString("cRamo").trim()).append(",").append(// E
            rs.getString("cRamo").trim()).append(",").append(// F
            rs.getString("cRamo").trim()).append(",").append(// G
            rs.getString("cUnidadResponsableEP").trim()).append(",").append(// H
            rs.getString("cUnidadResponsableEP").trim()).append(",").append(// I
            rs.getString("cUnidadResponsableEP").trim()).append(",").append(// J
            tipoMovto).append(",").append(// K
            compromiso).append(",").append(// L
            erogacion).append(",").append(// M
            sFolioSICOP).append(",").append(// N
            tipoOp).append(",").append(// O
            rs.getString("cIdContrato").trim()).append(",").append(// P
            rs.getString("cConceptoContrato").trim().replaceAll(",", " ")).append(",").append(// Q
            rs.getString("CBEN").trim()).append(",").append(// R
            rs.getString("cIdRFC").trim()).append(",").append(// S
            rs.getString("REPRESENTANTE_LEGAL").trim()).append(",").append(// T
            rs.getString("TPROC")).append(",").append(// U
            rs.getString("ESQ_PRECIO")).append(",").append(// V
            rs.getString("CONTRATACION")).append(",").append(// W
            fContratoIni.trim()).append(",").append(// X
            fContratoFin.trim()).append(",").append(// Y
            fContratoFirma.trim()).append(",").append(// Z
            pluri.trim()).append(",").append(// AA
            rs.getString("APROB_PLA")).append(",").append(// AB
            rs.getString("ACTO_JURIDICO")).append(",").append(// AC
            montoMonori).append(",").append(// AD
            rs.getString("cCodigoMonedaSiaff")).append(",").append(// AE
            rs.getString("TCAM")).append(",").append(// AF
            montoEjer).append(",").append(// AG
            montoMin).append(",").append(// AH
            montoMax).append(",").append(// AI
            rs.getString("CONV_MOD")).append(",").append(// AJ
            rs.getString("NUM_CONV_MOD")).append(",").append(// AK
            fConvenio).append(",").append(// AL
            org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("CODIGO_EXPEDIENTE"))).append(",").append(// AM
            org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("NO_PROCEDIMIENTO"))).append(",").append(// AN
            org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("CODIGO_CONTRATO"))).append(",").append(// AO
            rs.getString("nMes").trim()).append(",").append(// AP
            rs.getString("caNoIntegradaComp")).append(",").append(// AQ
            rs.getString("TTRANS_21"));
            if (esCalendario == 0) {
                // AR
                encabezado.append(",").append(rs.getString("caNoIntegradaComp"));
                // AS
                encabezado.append(",").append(rs.getString("caNoIntegradaComp"));
            }
            // AT
            encabezado.append(",").append(rs.getString("ETIQUETA_COMPRANET"));
            // AU
            encabezado.append(",").append(rs.getString("JUSTIFICA_COMPRANET"));
            // AV
            encabezado.append(",").append(rs.getString("IVA_MON_ORIG_414"));
            // AW
            encabezado.append(",").append(rs.getString("IMP_CONT_SIVA_413"));
            // AX
            encabezado.append(",").append(rs.getString("IMP_CONV_MOD_415"));
            // AY
            encabezado.append(",").append(fContratoModifica.trim());
            encabezado.append("\r\n");
            arrListaComp.add(encabezado.toString());
            // Generar Detalle de compromiso
            rs2 = generaQueryDetalleCompIntegrada(conn, cxpInt, reimprime);
            while (rs2.next()) {
                StringBuilder detalle = new StringBuilder();
                BigDecimal mImporteDet = rs2.getBigDecimal("Importe");
                if (esCalendario == 0) {
                    // ID_EVENTO
                    detalle.append(rs2.getString("ID_EVENTO")).append(",").append(// EVENTO
                    rs2.getString("EVENTO"));
                } else {
                    // ID_EVENTO
                    detalle.append(rs2.getString("ID_EVENTO_CAL")).append(",").append(// EVENTO
                    rs2.getString("EVENTO_CAL"));
                }
                // tCEP.cRamo
                detalle.append(",").append(rs2.getString("cRamo").trim()).append(",").append(// tCEP.cUnidadResponsableEP
                rs2.getString("cUnidadResponsableEP").trim()).append(",").append(// tCEP.aEjercicioFiscal
                rs2.getString("aEjercicioFiscal").trim()).append(",").append(// tCEP.cGrupoFuncional
                rs2.getString("cGrupoFuncional").trim()).append(",").append(// tCEP.cFuncion
                rs2.getString("cFuncion").trim()).append(",").append(// tCEP.cSubFuncion
                rs2.getString("cSubFuncion").trim()).append(",").append(// tCEP.cProgramaGeneral
                rs2.getString("cProgramaGeneral")).append(",").append(// tCEP.cActividadInstitucional
                rs2.getString("cActividadInstitucional")).append(",").append(// tCEP.cProgramaPresupuestario
                rs2.getString("cProgramaPresupuestario")).append(",").append(// SUBSTRING(tCEP.cPartida,1,1)
                rs2.getString(12)).append(",").append(// SUBSTRING(tCEP.cPartida,2,1)
                rs2.getString(13)).append(",").append(// SUBSTRING(tCEP.cPartida,3,1)
                rs2.getString(14)).append(",").append(// SUBSTRING(tCEP.cPartida,4,2)
                rs2.getString(15)).append(",").append(// tCEP.cTipoGasto
                rs2.getString("cTipoGasto")).append(",").append(// tCEP.cFuenteFinanciamiento
                rs2.getString("cFuenteFinanciamiento")).append(",").append(// tCEP.cEntidadFederativa
                rs2.getString("cEntidadFederativa")).append(",").append(// tCEP.cCartera
                rs2.getString("cCartera")).append(",").append(// CAU
                rs2.getString("CAU")).append(",").append(// COP
                rs2.getString("COP")).append(",").append(// PL
                rs2.getString("PL")).append(",").append(// OF_
                rs2.getString("OF_")).append(",").append(// AUX1
                rs2.getString("AUX1")).append(",").append(// AUX2
                rs2.getString("AUX2")).append(",").append(// AUX3
                rs2.getString("AUX3")).append(",").append(// Suficiencia
                rs2.getString("Suficiencia")).append(",").append(// Sol_OLI
                rs2.getString("Sol_OLI")).append(",").append(// Enero
                rs2.getString("Enero")).append(",").append(// Febrero
                rs2.getString("Febrero")).append(",").append(// Marzo
                rs2.getString("Marzo")).append(",").append(// Abril
                rs2.getString("Abril")).append(",").append(// Mayo
                rs2.getString("Mayo")).append(",").append(// Junio
                rs2.getString("Junio")).append(",").append(// Julio
                rs2.getString("Julio")).append(",").append(// Agosto
                rs2.getString("Agosto")).append(",").append(// Septiembre
                rs2.getString("Septiembre")).append(",").append(// Octubre
                rs2.getString("Octubre")).append(",").append(// Noviembre
                rs2.getString("Noviembre")).append(",").append(// Diciembre
                rs2.getString("Diciembre")).append(",").append(// Importe
                mImporteDet);
                if (esCalendario == 1) {
                    detalle.append(",").append(rs2.getString("tipoCalendario"));
                }
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
        }
        CloseObject.closeObject(rs);
        CloseObject.closeObject(rs2);
        CloseObject.closeObject(pstmntD);
        return arrListaComp;
    }

    public static ResultSet generaQueryDetalleComp(Connection conn, String nFolioCompromiso, boolean reimprime) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT '668' as ID_EVENTO ");
        sql.append("     , '304_TOCN' as EVENTO ");
        sql.append("     , tCEP.cRamo ");
        sql.append("     , tCEP.cUnidadResponsableEP ");
        sql.append("     , tCEP.aEjercicioFiscal ");
        sql.append("     , tCEP.cGrupoFuncional ");
        sql.append("     , tCEP.cFuncion ");
        sql.append("     , tCEP.cSubFuncion ");
        sql.append("     , SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2) AS cProgramaGeneral ");
        sql.append("     , tCEP.cActividadInstitucional ");
        sql.append("     , tCEP.cProgramaPresupuestario ");
        sql.append("     , SUBSTRING(tCEP.cPartida,1,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,2,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,3,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,4,2) ");
        sql.append("     , tCEP.cTipoGasto ");
        sql.append("     , tCEP.cFuenteFinanciamiento ");
        sql.append("     , tCEP.cEntidadFederativa ");
        sql.append("     , SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11) AS cCartera ");
        sql.append("     , '000000' + '0000' as CAU ");
        sql.append("     , '00' as COP ");
        sql.append("     , '000' as PL ");
        sql.append("     , '000' as OF_ ");
        sql.append("     , '00000' as AUX1 ");
        sql.append("     , '00000' as AUX2 ");
        sql.append("     , '0000000000' as AUX3 ");
        sql.append("     , RIGHT(REPLICATE('0', 5) + CAST(ISNULL(nFoliosuficiencia,0) AS VARCHAR), 5) AS Suficiencia ");
        sql.append("     , '' as Sol_OLI ");
        sql.append("     , SUM(CASE tCD.cMes when 1 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Enero ");
        sql.append("     , SUM(CASE tCD.cMes when 2 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Febrero ");
        sql.append("     , SUM(CASE tCD.cMes when 3 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Marzo ");
        sql.append("     , SUM(CASE tCD.cMes when 4 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Abril ");
        sql.append("     , SUM(CASE tCD.cMes when 5 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Mayo ");
        sql.append("     , SUM(CASE tCD.cMes when 6 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Junio ");
        sql.append("     , SUM(CASE tCD.cMes when 7 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Julio ");
        sql.append("     , SUM(CASE tCD.cMes when 8 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Agosto ");
        sql.append("     , SUM(CASE tCD.cMes when 9 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Septiembre ");
        sql.append("     , SUM(CASE tCD.cMes when 10 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Octubre ");
        sql.append("     , SUM(CASE tCD.cMes when 11 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Noviembre ");
        sql.append("     , SUM(CASE tCD.cMes when 12 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Diciembre ");
        sql.append("     , SUM(convert(decimal(14,2), abs(tCD.mImporte))) as Importe ");
        sql.append("     , '2' tipoCalendario , '658' as ID_EVENTO_CAL, '312_ACS' as EVENTO_CAL");
        sql.append("	FROM tCompromisoDetalle tCD WITH (NOLOCK) ");
        sql.append("	INNER JOIN tCompromisoEncabezado tCE WITH (NOLOCK) ON tCD.nFolioCompromiso = tCE.nFolioCompromiso ");
        sql.append(" INNER JOIN tCatalogoEP tCEP WITH (NOLOCK) ON tCD.EP = tCEP.EP ");
        sql.append("	WHERE tCD.nFolioCompromiso = ? ");
        sql.append("  		AND tCD.EP = tCEP.EP ");
        sql.append("  		AND tCE.nFolioCompromiso = tCD.nFolioCompromiso ");
        sql.append("  		AND mImporte > 0 AND SUBSTRING(tCD.EP, 40, 1) <> '4'");
        sql.append("	GROUP BY tCEP.cRamo, tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, ");
        sql.append("         SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2), tCEP.cActividadInstitucional, SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11), ");
        sql.append("         tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, nFolioSuficiencia ");
        sql.append("UNION ");
        sql.append("	SELECT '668' as ID_EVENTO ");
        sql.append("     , '304_TOCN' as EVENTO ");
        sql.append("     , tCEP.cRamo ");
        sql.append("     , tCEP.cUnidadResponsableEP ");
        sql.append("     , tCEP.aEjercicioFiscal ");
        sql.append("     , tCEP.cGrupoFuncional ");
        sql.append("     , tCEP.cFuncion ");
        sql.append("     , tCEP.cSubFuncion ");
        sql.append("     , SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2) AS cProgramaGeneral ");
        sql.append("     , tCEP.cActividadInstitucional ");
        sql.append("     , tCEP.cProgramaPresupuestario ");
        sql.append("     , SUBSTRING(tCEP.cPartida,1,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,2,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,3,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,4,2) ");
        sql.append("     , tCEP.cTipoGasto ");
        sql.append("     , tCEP.cFuenteFinanciamiento ");
        sql.append("     , tCEP.cEntidadFederativa ");
        sql.append("     , SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11) AS cCartera ");
        sql.append("     , '000000' + '0000' as CAU ");
        sql.append("     , '00' as COP ");
        sql.append("     , '000' as PL ");
        sql.append("     , '000' as OF_ ");
        sql.append("     , '00000' as AUX1 ");
        sql.append("     , '00000' as AUX2 ");
        sql.append("     , '0000000000' as AUX3 ");
        sql.append("     , (SELECT TOP 1 RIGHT(REPLICATE('0', 5) + CAST(ISNULL(nFoliosuficiencia,0) AS VARCHAR), 5) FROM tCompromisoEncabezado C WITH (NOLOCK) where C.cIdContrato = tCE.cIdContrato and (nFolioSuficiencia is not null AND nFolioSuficiencia <> -1 )) Suficiencia ");
        sql.append("     , '' as Sol_OLI ");
        sql.append("     , SUM(CASE tCD.cMes when 1 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Enero ");
        sql.append("     , SUM(CASE tCD.cMes when 2 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Febrero ");
        sql.append("     , SUM(CASE tCD.cMes when 3 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Marzo ");
        sql.append("     , SUM(CASE tCD.cMes when 4 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Abril ");
        sql.append("     , SUM(CASE tCD.cMes when 5 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Mayo ");
        sql.append("     , SUM(CASE tCD.cMes when 6 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Junio ");
        sql.append("     , SUM(CASE tCD.cMes when 7 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Julio ");
        sql.append("     , SUM(CASE tCD.cMes when 8 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Agosto ");
        sql.append("     , SUM(CASE tCD.cMes when 9 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Septiembre ");
        sql.append("     , SUM(CASE tCD.cMes when 10 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Octubre ");
        sql.append("     , SUM(CASE tCD.cMes when 11 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Noviembre ");
        sql.append("     , SUM(CASE tCD.cMes when 12 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Diciembre ");
        sql.append("     , SUM(convert(decimal(14,2), abs(tCD.mImporte))) as Importe ");
        sql.append("     , '1' tipoCalendario, '651' as ID_EVENTO_CAL, '322_RCS' as EVENTO_CAL ");
        sql.append("	FROM tCompromisoDetalle tCD WITH (NOLOCK) ");
        sql.append("	INNER JOIN tCompromisoEncabezado tCE WITH (NOLOCK) ON tCD.nFolioCompromiso = tCE.nFolioCompromiso ");
        sql.append("	INNER JOIN tCatalogoEP tCEP WITH (NOLOCK) ON tCD.EP = tCEP.EP ");
        sql.append("	WHERE tCD.nFolioCompromiso = ? ");
        sql.append("  		AND tCD.EP = tCEP.EP ");
        sql.append("  		AND tCE.nFolioCompromiso = tCD.nFolioCompromiso ");
        sql.append("  		AND mImporte < 0 AND SUBSTRING(tCD.EP, 40, 1) <> '4'");
        sql.append("	GROUP BY tCEP.cRamo, tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, ");
        sql.append("         SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2), tCEP.cActividadInstitucional, SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11), ");
        sql.append("         tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, nFolioSuficiencia, tCE.cIdContrato ");
        pst = conn.prepareStatement(sql.toString());
        pst.setString(1, nFolioCompromiso);
        pst.setString(2, nFolioCompromiso);
        rs = pst.executeQuery();
        return rs;
    }

    public static ResultSet generaQueryDetalleCompIntegrada(Connection conn, String cxpIntegrada, boolean reimprime) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT '668' as ID_EVENTO ");
        sql.append("     , '304_TOCN' as EVENTO ");
        sql.append("     , tCEP.cRamo ");
        sql.append("     , tCEP.cUnidadResponsableEP ");
        sql.append("     , tCEP.aEjercicioFiscal ");
        sql.append("     , tCEP.cGrupoFuncional ");
        sql.append("     , tCEP.cFuncion ");
        sql.append("     , tCEP.cSubFuncion ");
        sql.append("     , SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2) AS cProgramaGeneral ");
        sql.append("     , tCEP.cActividadInstitucional ");
        sql.append("     , tCEP.cProgramaPresupuestario ");
        sql.append("     , SUBSTRING(tCEP.cPartida,1,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,2,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,3,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,4,2) ");
        sql.append("     , tCEP.cTipoGasto ");
        sql.append("     , tCEP.cFuenteFinanciamiento ");
        sql.append("     , tCEP.cEntidadFederativa ");
        sql.append("     , SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11) AS cCartera ");
        sql.append("     , '000000' + '0000' as CAU ");
        sql.append("     , '00' as COP ");
        sql.append("     , '000' as PL ");
        sql.append("     , '000' as OF_ ");
        sql.append("     , '00000' as AUX1 ");
        sql.append("     , '00000' as AUX2 ");
        sql.append("     , '0000000000' as AUX3 ");
        sql.append("     , RIGHT(REPLICATE('0', 5) + CAST(ISNULL(nFoliosuficiencia,0) AS VARCHAR), 5) AS Suficiencia ");
        sql.append("     , '' as Sol_OLI ");
        sql.append("     , SUM(CASE tCD.cMes when 1 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Enero ");
        sql.append("     , SUM(CASE tCD.cMes when 2 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Febrero ");
        sql.append("     , SUM(CASE tCD.cMes when 3 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Marzo ");
        sql.append("     , SUM(CASE tCD.cMes when 4 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Abril ");
        sql.append("     , SUM(CASE tCD.cMes when 5 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Mayo ");
        sql.append("     , SUM(CASE tCD.cMes when 6 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Junio ");
        sql.append("     , SUM(CASE tCD.cMes when 7 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Julio ");
        sql.append("     , SUM(CASE tCD.cMes when 8 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Agosto ");
        sql.append("     , SUM(CASE tCD.cMes when 9 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Septiembre ");
        sql.append("     , SUM(CASE tCD.cMes when 10 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Octubre ");
        sql.append("     , SUM(CASE tCD.cMes when 11 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Noviembre ");
        sql.append("     , SUM(CASE tCD.cMes when 12 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Diciembre ");
        sql.append("     , SUM(convert(decimal(14,2), abs(tCD.mImporte))) as Importe ");
        sql.append("     , '2' tipoCalendario , '658' as ID_EVENTO_CAL, '312_ACS' as EVENTO_CAL");
        sql.append("	FROM tCompromisoDetalle tCD WITH (NOLOCK) ");
        sql.append("	INNER JOIN tCompromisoEncabezado tCE WITH (NOLOCK) ON tCD.nFolioCompromiso = tCE.nFolioCompromiso ");
        sql.append(" 	INNER JOIN tCatalogoEP tCEP WITH (NOLOCK) ON tCD.EP = tCEP.EP ");
        sql.append(" 	INNER JOIN tIntegraFoliosCompromiso integra WITH (NOLOCK) ON  tCD.nFolioCompromiso = integra.nFolioCompromiso ");
        sql.append("	WHERE integra.caNoIntegradaComp = ? ");
        sql.append("  		AND tCD.EP = tCEP.EP ");
        sql.append("  		AND tCE.nFolioCompromiso = tCD.nFolioCompromiso ");
        sql.append("  		AND mImporte > 0 and SUBSTRING(tCD.EP,40,1) <> '4' ");
        sql.append("	GROUP BY tCEP.cRamo, tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, ");
        sql.append("         SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2), tCEP.cActividadInstitucional, SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11), ");
        sql.append("         tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, nFolioSuficiencia ");
        sql.append("UNION ");
        sql.append("	SELECT '668' as ID_EVENTO ");
        sql.append("     , '304_TOCN' as EVENTO ");
        sql.append("     , tCEP.cRamo ");
        sql.append("     , tCEP.cUnidadResponsableEP ");
        sql.append("     , tCEP.aEjercicioFiscal ");
        sql.append("     , tCEP.cGrupoFuncional ");
        sql.append("     , tCEP.cFuncion ");
        sql.append("     , tCEP.cSubFuncion ");
        sql.append("     , SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2) AS cProgramaGeneral ");
        sql.append("     , tCEP.cActividadInstitucional ");
        sql.append("     , tCEP.cProgramaPresupuestario ");
        sql.append("     , SUBSTRING(tCEP.cPartida,1,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,2,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,3,1) ");
        sql.append("     , SUBSTRING(tCEP.cPartida,4,2) ");
        sql.append("     , tCEP.cTipoGasto ");
        sql.append("     , tCEP.cFuenteFinanciamiento ");
        sql.append("     , tCEP.cEntidadFederativa ");
        sql.append("     , SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11) AS cCartera ");
        sql.append("     , '000000' + '0000' as CAU ");
        sql.append("     , '00' as COP ");
        sql.append("     , '000' as PL ");
        sql.append("     , '000' as OF_ ");
        sql.append("     , '00000' as AUX1 ");
        sql.append("     , '00000' as AUX2 ");
        sql.append("     , '0000000000' as AUX3 ");
        sql.append("     , RIGHT(REPLICATE('0', 5) + CAST(ISNULL(nFoliosuficiencia,0) AS VARCHAR), 5) AS Suficiencia ");
        sql.append("     , '' as Sol_OLI ");
        sql.append("     , SUM(CASE tCD.cMes when 1 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Enero ");
        sql.append("     , SUM(CASE tCD.cMes when 2 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Febrero ");
        sql.append("     , SUM(CASE tCD.cMes when 3 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Marzo ");
        sql.append("     , SUM(CASE tCD.cMes when 4 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Abril ");
        sql.append("     , SUM(CASE tCD.cMes when 5 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Mayo ");
        sql.append("     , SUM(CASE tCD.cMes when 6 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Junio ");
        sql.append("     , SUM(CASE tCD.cMes when 7 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Julio ");
        sql.append("     , SUM(CASE tCD.cMes when 8 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Agosto ");
        sql.append("     , SUM(CASE tCD.cMes when 9 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Septiembre ");
        sql.append("     , SUM(CASE tCD.cMes when 10 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Octubre ");
        sql.append("     , SUM(CASE tCD.cMes when 11 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Noviembre ");
        sql.append("     , SUM(CASE tCD.cMes when 12 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Diciembre ");
        sql.append("     , SUM(convert(decimal(14,2), abs(tCD.mImporte))) as Importe ");
        sql.append("     , '1' tipoCalendario, '651' as ID_EVENTO_CAL, '322_RCS' as EVENTO_CAL ");
        sql.append("	FROM tCompromisoDetalle tCD WITH (NOLOCK) ");
        sql.append("	INNER JOIN tCompromisoEncabezado tCE WITH (NOLOCK) ON tCD.nFolioCompromiso = tCE.nFolioCompromiso ");
        sql.append("	INNER JOIN tCatalogoEP tCEP WITH (NOLOCK) ON tCD.EP = tCEP.EP ");
        sql.append("	INNER JOIN tIntegraFoliosCompromiso integra WITH (NOLOCK) ON  tCD.nFolioCompromiso = integra.nFolioCompromiso ");
        sql.append("	WHERE integra.caNoIntegradaComp = ? ");
        sql.append("  		AND tCD.EP = tCEP.EP ");
        sql.append("  		AND tCE.nFolioCompromiso = tCD.nFolioCompromiso ");
        sql.append("  		AND mImporte < 0 and SUBSTRING(tCD.EP,40,1) <> '4' ");
        sql.append("	GROUP BY tCEP.cRamo, tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, ");
        sql.append("         SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2), tCEP.cActividadInstitucional, SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11), ");
        sql.append("         tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, nFolioSuficiencia ");
        pst = conn.prepareStatement(sql.toString());
        pst.setString(1, cxpIntegrada);
        pst.setString(2, cxpIntegrada);
        rs = pst.executeQuery();
        return rs;
    }

    public static String buscaFechaAplicacionCancelados(Connection conn, String compromisoCancelado) throws SQLException {
        String fAplicacion = "";
        PreparedStatement pstmnt = null;
        PreparedStatement pstmntfApp = null;
        ResultSet rs = null;
        ResultSet rsfApp = null;
        int rows = 0;
        String querySelect = "SELECT COUNT(*)FROM tLayoutCompromisos WITH (NOLOCK) WHERE caNoCompromiso = '" + compromisoCancelado + "'";
        try {
            pstmnt = conn.prepareStatement(querySelect);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                rows = rs.getInt(1);
            }
            if (rows != 0) {
                String querySelectFechas = "SELECT fAplicacionSICOP FROM tLayoutCompromisos WITH (NOLOCK) WHERE caNoCompromiso = '" + compromisoCancelado + "'";
                pstmntfApp = conn.prepareStatement(querySelectFechas);
                rsfApp = pstmntfApp.executeQuery();
                while (rsfApp.next()) {
                    fAplicacion = rsfApp.getString(1).trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(pstmntfApp);
        }
        return fAplicacion;
    }

    private static String calculaCC(Connection conn, String ep) throws Exception {
        String query = " SELECT TOP 1  ccentrocontable  FROM   tcatalogourcc with(nolock)  WHERE  cunidadresponsable = Substring( ? , 57, 3)";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, ep);
            rs = ps.executeQuery();
            String ccentroContable = "10";
            if (rs.next())
                ccentroContable = rs.getString(1);
            return ccentroContable;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static String calculaEvento(Connection conn, String ep) throws Exception {
        log.trace("Calculando el evento para la EP[ " + ep + "]");
        String query = "SELECT Count(*) AS tieneRadicado  FROM   tcompromisodetalle WITH(nolock) " + " WHERE  ep = ?   AND cevento LIKE 'R_%'";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, ep);
            log.trace("Ejecutando query:\n" + query + "\n EP[ " + ep + "]");
            rs = ps.executeQuery();
            int nRadicados = 0;
            if (rs.next()) {
                nRadicados = rs.getInt(1);
                log.trace("Radicados: " + nRadicados);
            }
            String cEvento = "";
            if (nRadicados > 0) {
                cEvento = "R_CMP001";
                log.trace("La EP se ha utilizado con compromiso radicado. El evento debe ser radicado");
            } else
                cEvento = "CMP001";
            log.debug("Se calculo el evento[" + cEvento + "] para la EP[" + ep + "]");
            return cEvento;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static int calculaMes(int ejercicioFiscal) {
        Calendar c = new GregorianCalendar();
        if (c.get(Calendar.YEAR) != ejercicioFiscal)
            return 12;
        else
            return c.get(Calendar.MONTH) + 1;
    }

    public static int creaEncabezado(Connection conn, int nFolioCompromiso, String cIdContrato, String cTipoContrato, Date fAplicacion, String cCentroContable, String cRamo, String cUnidadResponsable, String caNoCompromiso, int nMes, String cDescripcionPoliza, String usuario, String cRadicado) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO tcompromisoencabezado  ");
        sql.append("             (  ");
        sql.append("                         nfoliocompromiso ,  ");
        sql.append("                         fcarga ,  ");
        sql.append("                         cidcontrato ,  ");
        sql.append("                         ctipocontrato ,  ");
        sql.append("                         faplicacion ,  ");
        sql.append("                         ccentrocontable ,  ");
        sql.append("                         cramo ,  ");
        sql.append("                         cunidadresponsable ,  ");
        sql.append("                         canocompromiso ,  ");
        sql.append("                         nenviadosicop ,  ");
        sql.append("                         ctipopoliza ,  ");
        sql.append("                         nmes ,  ");
        sql.append("                         aejerciciofiscal ,  ");
        sql.append("                         cunidadresponsablecontable ,  ");
        sql.append("                         cdescripcionpoliza ,  ");
        sql.append("                         usuario ,  ");
        sql.append("                         cradicado  ");
        sql.append("             )  ");
        sql.append("             VALUES  ");
        sql.append("             (  ");
        sql.append("                         ? ,  ");
        sql.append("                         Getdate() ,  ");
        sql.append("                         ? ,  ");
        sql.append("                         ? ,  ");
        sql.append("                         ? ,  ");
        sql.append("                         ? ,  ");
        sql.append("                         ? ,  ");
        sql.append("                         ? ,  ");
        sql.append("                         ? ,  ");
        sql.append("                         0 ,  ");
        sql.append("                         'CO' ,  ");
        sql.append("                         ? ,(SELECT TOP 1  ");
        sql.append("                    aejerciciofiscal  ");
        sql.append("             FROM   tejerciciofiscal  WITH(NOLOCK)");
        sql.append("             WHERE  cactivo = 1 ) , '" + unidadContable + "' , ");
        sql.append("                    ? ,  ");
        sql.append("                    ? ,  ");
        sql.append("                    ?  ");
        sql.append("             )");
        PreparedStatement psInsert = null;
        try {
            String queryInsertEncabezado = sql.toString();
            psInsert = conn.prepareStatement(queryInsertEncabezado);
            psInsert.setInt(1, nFolioCompromiso);
            psInsert.setString(2, cIdContrato);
            psInsert.setString(3, cTipoContrato);
            psInsert.setDate(4, new java.sql.Date(fAplicacion.getTime()));
            psInsert.setString(5, cCentroContable);
            psInsert.setString(6, cRamo);
            psInsert.setString(7, cUnidadResponsable);
            psInsert.setString(8, caNoCompromiso);
            psInsert.setInt(9, nMes);
            psInsert.setString(10, cDescripcionPoliza);
            psInsert.setString(11, usuario);
            psInsert.setString(12, cRadicado);
            log.trace("Ejecutando Insert: " + psInsert);
            log.trace(String.format("Valores:[%d,%S,%S,%S,%S,%S,%S,%S,%d,%S,%S,%S]", nFolioCompromiso, cIdContrato, cTipoContrato, fAplicacion.toString(), cCentroContable, cRamo, cUnidadResponsable, caNoCompromiso, nMes, cDescripcionPoliza, usuario, cRadicado));
            int insertados = psInsert.executeUpdate();
            log.debug("Se insertaron " + insertados + " registros en compromiso encabezado.");
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static void ejecutaCancelacioMensual(Connection conn, int mes) throws Exception {
        CallableStatement cstmt = null;
        try {
            cstmt = conn.prepareCall("{call dbo.spCancelacionMasivaMensual ( ? ) }");
            cstmt.setInt(1, mes);
            cstmt.execute();
        } finally {
            CloseObject.closeObject(cstmt, false);
        }
    }

    public static boolean estaAplicado(Connection conn, String caNoCompromiso) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String querySelect = "SELECT cDocumentoHaplicado FROM tCompromisoEncabezado WITH(NOLOCK) WHERE caNoCompromiso = ?";
        try {
            pstmnt = conn.prepareStatement(querySelect);
            pstmnt.setString(1, caNoCompromiso);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                return !StringUtils.isBlank(rs.getString(1));
            } else
                throw new Exception("No se encontro compromiso con No. " + caNoCompromiso);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    public static boolean existeCaNoCompromiso(Connection conn, String caNoCompromiso, String codSemarnat2) throws Exception {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String querySelect = "SELECT COUNT(*) FROM tLayoutCompromisos WITH (NOLOCK) WHERE caNoCompromiso = '" + caNoCompromiso + "' AND caNoSemarnat2= '" + codSemarnat2 + "'";
            pstmnt = conn.prepareStatement(querySelect);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                existe = (rs.getInt(1) > 0);
            } else {
                existe = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return existe;
    }

    public static boolean existeCompromiso(Connection conn, String caNoCompromiso) throws Exception {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String querySelect = "SELECT COUNT(*) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE caNoCompromiso = '" + caNoCompromiso + "'";
            pstmnt = conn.prepareStatement(querySelect);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                existe = (rs.getInt(1) > 0);
            } else {
                existe = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return existe;
    }

    public static BigDecimal diferenciaSICOPvsSAI(Connection conn, String caNoCompromiso) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        BigDecimal diferencia = new BigDecimal("0");
        try {
            String querySelect = "SELECT diferencia FROM vLayoutCompromisos WHERE caNoCompromiso = ?";
            pstmnt = conn.prepareStatement(querySelect);
            pstmnt.setString(1, caNoCompromiso);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                diferencia = rs.getBigDecimal(1);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return diferencia;
    }

    public static Caso generaCasoCompromiso(Connection conn, Usuario u, FolioGeneratorInterface fg, String opResponsable) throws Exception {
        Caso c = CasoManager.nuevoCaso(conn, u, CompromisoManager.ID_TC_COMPROMISO, fg);
        String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getTodayESMX());
        c.getCasoDato("EJERCICIO_FISCAL").setValor(ejercicioFiscal);
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", Util.getTodayESMX());
        m.put("EJERCICIO_FISCAL", ejercicioFiscal);
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
        if (id_gabinete < 0) {
            log.error("Identificador de Gabiente invalido (< 0)");
            throw new SQLException("Identificador de Gabiente invalido (< 0)");
        }
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        AplicacionManager.updateExpediente(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), m);
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionManager.update(conn, co, opResponsable);
        c = CasoManager.select(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete());
        return c;
    }

    public static String generateCaNoCompromiso(String cCentroContable, String aEjercicioFiscal) throws SQLException {
        CFSequenceManager seqMngr = CFSequenceManager.getInstance(GestionInterface.ATT_CONEXION);
        String seqValue = String.valueOf(seqMngr.nextVal("CO-" + cCentroContable));
        while (seqValue.length() < 5) seqValue = "0" + seqValue;
        seqValue = "1" + seqValue;
        seqValue = cCentroContable + "CO" + aEjercicioFiscal + seqValue;
        return seqValue;
    }

    public static int insertaCommpromisoDetalle(Connection conn, int nFolioCompromiso, int nDocRenglon, String EP, String cEvento, double mImporte, int cMes, String cCentroContable) throws Exception {
        String queryInsert = "INSERT INTO tcompromisodetalle  (nfoliocompromiso, ndocrenglon, ep,  cevento,  mimporte, mimportenegativo, cmes, ccentrocontable)  VALUES      ( ?, ?, ?, ?, ?, ?, ?, ?  ) ";
        int insertados = 0;
        PreparedStatement psInsert = null;
        try {
            psInsert = conn.prepareStatement(queryInsert);
            psInsert.setInt(1, nFolioCompromiso);
            psInsert.setInt(2, nDocRenglon);
            psInsert.setString(3, EP);
            psInsert.setString(4, cEvento);
            psInsert.setDouble(5, mImporte);
            psInsert.setDouble(6, -1 * mImporte);
            psInsert.setInt(7, cMes);
            psInsert.setString(8, cCentroContable);
            log.debug("Insertando renglon " + nDocRenglon + " del compromiso " + nFolioCompromiso);
            insertados = psInsert.executeUpdate();
            log.debug("Insertado exitosamente.");
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static int insertaCompromisoEncabezado(Connection conn, CompromisoEncabezado ce) throws Exception {
        Ramo ramo = new Ramo(conn);
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tcompromisoencabezado  ");
        // 1
        query.append("             (nfoliocompromiso,  ");
        query.append("              fcarga,  ");
        // 2
        query.append("              cidcontrato,  ");
        // 3
        query.append("              ctipocontrato,  ");
        query.append("              faplicacion,  ");
        // 4
        query.append("              ccentrocontable,  ");
        // 5
        query.append("              cramo,  ");
        // 6
        query.append("              cunidadresponsable,  ");
        // 7
        query.append("              canocompromiso,  ");
        // 8
        query.append("              nenviadosicop,  ");
        // 9
        query.append("              ctipopoliza,  ");
        // 10
        query.append("              nmes,  ");
        // 11
        query.append("              aejerciciofiscal,  ");
        // 12
        query.append("              cunidadresponsablecontable,  ");
        // 13
        query.append("              cdescripcionpoliza,  ");
        // 14
        query.append("              usuario,  ");
        // 15
        query.append("              cradicado,  ");
        //16
        query.append("				 cEsCalendario)");
        query.append(" VALUES     (?,  ");
        query.append("             GETDATE(),  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             GETDATE(),  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?, ");
        query.append("             ?) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, ce.getnFolioCompromiso());
            ps.setString(2, ce.getcIdContrato());
            ps.setString(3, ce.getcTipoContrato());
            ps.setString(4, ce.getcCentroContable());
            ps.setString(5, ramo.getIdRamo());
            ps.setString(6, ce.getcUnidadResponsable());
            ps.setString(7, ce.getCaNoCompromiso());
            ps.setInt(8, ce.getnEnviadoSICOP());
            ps.setString(9, ce.getcTipoPoliza());
            ps.setInt(10, ce.getnMes());
            ps.setInt(11, ce.getaEjercicioFiscal());
            ps.setString(12, ce.getcUnidadResponsableContable());
            ps.setString(13, ce.getcDescripcionPoliza());
            ps.setString(14, ce.getUsuario());
            ps.setString(15, ce.getcRadicado());
            ps.setString(16, ce.getEsCalendario());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaCompromisoFederalizado(Connection conn, String idContrato, int folioCompromiso, String contrarecibo, String ramo, int ejercicioFiscal, String login) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT INTO tCompromisoEncabezado (nFolioCompromiso, fCarga, cIdContrato, cTipoContrato, fAplicacion, ");
        sb.append("                                    cCentroContable, cRamo, cUnidadResponsable, caNoCompromiso, cTipoPoliza,");
        sb.append("                                    nMes, aEjercicioFiscal, cUnidadResponsableContable, cDescripcionPoliza,");
        sb.append("                                    usuario, cRadicado, nFolioAutSICOP)");
        sb.append(" SELECT ").append(folioCompromiso).append("AS nFolioCompromiso,");
        sb.append("       Getdate()               AS fCarga,");
        sb.append("       cidcontrato             AS cIdContrato,");
        sb.append("       'FE'                    AS cTipoContrato,");
        sb.append("       Getdate()               AS fAplicacion,");
        sb.append("       cidentidadcontable      AS cCentroContable,");
        sb.append("       '").append(ramo).append("'                    AS cRamo,");
        sb.append("       cidunidadadministrativa AS cUnidadResponsable,");
        sb.append("       '").append(contrarecibo).append("'        AS caNoCompromiso,");
        sb.append("       'CO'                    AS cTipoPoliza,");
        sb.append("       Month(Getdate())        AS nMes,");
        sb.append("       '").append(ejercicioFiscal).append("'                      AS aEjercicioFiscal,");
        sb.append("'" + unidadContable + "' AS cUnidadResponsableContable,");
        sb.append("       'REGISTRO DEL CONTRATO FOLIO '");
        sb.append("       + cidcontrato           AS cDescripcionPoliza,");
        sb.append("       '").append(login).append("'               AS usuario,");
        sb.append("       'N'                     AS radicado,");
        sb.append("       -1                      AS nFolioAutSICOP");
        sb.append("  FROM   pcontratofederalizado ");
        sb.append(" WHERE  cidcontrato = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, idContrato);
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void insertaCompromisoRGDetalle(Connection conn, int folioCompromiso, String foliosRG) throws Exception {
        PreparedStatement psInsertDetalle = null;
        StringBuilder query = new StringBuilder();
        query.append("	 INSERT INTO dbo.tcompromisodetalle  (nfoliocompromiso, ndocrenglon,ep,cevento, mimporte, mimportenegativo, cmes, ccentrocontable) ");
        query.append(" SELECT " + folioCompromiso + " AS nFolioCompromiso, Row_number()  OVER( ORDER BY ep, cmes ASC) AS nDocRenglon");
        query.append(", ep, 'CMP001' AS cEvento, ");
        query.append("Sum(mimportemasiva) AS mImporte, ");
        query.append(" -Sum(mimportemasiva)   AS mImporteNegativo, cmes, ccentrocontable ");
        query.append("FROM   trelaciongastosdetalle rgDetalle WITH(nolock) ");
        query.append(" WHERE  rgDetalle.nfoliorelaciongastos IN ( " + foliosRG + " ) ");
        query.append(" GROUP  BY ep, cmes, ccentrocontable ");
        String queryInsert = query.toString();
        try {
            psInsertDetalle = conn.prepareStatement(queryInsert);
            psInsertDetalle.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsertDetalle);
        }
    }

    public static void insertaCompromisoRGEncabezado(Connection conn, int folioCompromiso, String idIntegracion, String contrarecibo, Usuario u, String tipo) throws Exception {
        PreparedStatement psInsert = null;
        String queryInsert = "INSERT INTO tCompromisoEncabezado( nFolioCompromiso , fCarga , cIdContrato , cTipoContrato , fAplicacion , cCentroContable , cRamo , cUnidadResponsable , cDocumentoHaplicado ,  caNoCompromiso , nEnviadoSICOP ,  cTipoPoliza , nMes ,  aEjercicioFiscal , cUnidadResponsableContable ,   cDescripcionPoliza , usuario , cRadicado ) " + " VALUES  ( ? , GETDATE(), ?, ? , GETDATE(), ?, '16',  ?, 'S' ,  ?, 0,  'DI' , ?,  ?, '" + unidadContable + "',   ? , ?, ? )";
        String descripcionPoliza = "";
        String tipoContrato = "";
        try {
            String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
            if ("RELACIONGASTOS".equals(tipo)) {
                descripcionPoliza = "REGISTRO ORIGINAL DE LA INTEGRACION DE RELACION DE GASTOS " + idIntegracion;
                tipoContrato = "RE";
            } else {
                descripcionPoliza = "REGISTRO DEL COMPROMISO DEL PAGO DIRECTO: " + contrarecibo;
                tipoContrato = "PD";
            }
            String radicado = "N";
            psInsert = conn.prepareStatement(queryInsert);
            psInsert.setInt(1, folioCompromiso);
            psInsert.setString(2, idIntegracion);
            psInsert.setString(3, tipoContrato);
            psInsert.setString(4, u.getPropiedad("CCENTROCONTABLE").getValor());
            psInsert.setString(5, u.getU_UR());
            psInsert.setString(6, contrarecibo);
            psInsert.setInt(7, calculaMes(Integer.parseInt(ejercicioFiscal)));
            psInsert.setInt(8, Integer.parseInt(ejercicioFiscal));
            psInsert.setString(9, descripcionPoliza);
            psInsert.setString(10, u.getLogin());
            psInsert.setString(11, radicado);
            psInsert.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static void insertaCompromisoRGRelacionDetalle(Connection conn, int folioRelacionGastosCompromiso, String foliosIntegracion) throws Exception {
        String queryInsert = "INSERT INTO dbo.tRelacionGastosCompromisoDetalle( nFolioRelacionGastosCompromiso , nFolioRelacionGastos ) VALUES  ( ?,? )";
        PreparedStatement psInsert = null;
        try {
            psInsert = conn.prepareStatement(queryInsert);
            String[] folios = foliosIntegracion.split(",");
            for (String i : folios) {
                psInsert.setInt(1, folioRelacionGastosCompromiso);
                psInsert.setInt(2, Integer.parseInt(i.trim()));
                psInsert.executeUpdate();
            }
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static String consultaContrarreciboPD(Connection conn, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String cxp = "";
        try {
            pst = conn.prepareStatement("SELECT caNoContrarrecibo FROM tPagoDirectoEncabezado WITH (NOLOCK) WHERE nFolioPagoDirecto = ?");
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                cxp = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return cxp;
    }

    public static int insertaCompromisoRGRelacionEncabezado(Connection conn, int folioCompromiso, String canocontrareciobo, String cuentaBancaria, String fechaIntegracion, String leyenda, Usuario u) throws Exception {
        String queryInsert = "INSERT INTO tRelacionGastosCompromisoEncabezado( nFolioRelacionGastosCompromiso ,  canocontrarrecibo , cDocumentHAplicado , fAplicacion , cCentroContable ,  cUnidaResponable ,  nFolioCompromiso , cCuentaBancaria , dfechaIntegracion , nLeyenda) " + " VALUES( ?, ?, ?, GETDATE(), ?, ?,  ?, ?, ?, ?) ";
        PreparedStatement psInsert = null;
        try {
            String[] cuentasBancarias = cuentaBancaria.split(",");
            String[] leyendas = leyenda.split(",");
            String[] fechasPago = fechaIntegracion.split(",");
            CFSequenceManager sequence = CFSequenceManager.getInstance();
            int nFolioRelacionGastosCompromiso = sequence.nextVal("RELACIONGASTOSCOMPROMISO");
            psInsert = conn.prepareStatement(queryInsert);
            psInsert.setInt(1, nFolioRelacionGastosCompromiso);
            psInsert.setString(2, canocontrareciobo);
            psInsert.setString(3, "S");
            psInsert.setString(4, u.getPropiedad("CCENTROCONTABLE").getValor());
            psInsert.setString(5, u.getU_UR());
            psInsert.setInt(6, folioCompromiso);
            psInsert.setString(7, cuentasBancarias[0]);
            psInsert.setString(8, fechasPago[0]);
            psInsert.setString(9, leyendas[0]);
            psInsert.executeUpdate();
            return nFolioRelacionGastosCompromiso;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static void insertaCompromisoPagoDirecto(Connection conn, int folioCompromiso, String canocontrarecibo, String cuentaBancaria, String fechaIntegracion, String leyenda, Usuario u, String folio, String caNoCompromiso) throws Exception {
        String queryInsert = "INSERT INTO tPagoDirectoCompromiso( nFolioPagoDirectoCompromiso ,  canocontrarrecibo , cDocumentHAplicado , fAplicacion , cCentroContable ,  cUnidaResponable ,  nFolioCompromiso , cCuentaBancaria , dfechaIntegracion , nLeyenda, caNoCompromiso) " + " VALUES( ?, ?, ?, GETDATE(), ?, ?,  ?, ?, ?, ?, ?) ";
        PreparedStatement psInsert = null;
        try {
            String[] cuentasBancarias = cuentaBancaria.split(",");
            String[] leyendas = leyenda.split(",");
            String[] fechasPago = fechaIntegracion.split(",");
            psInsert = conn.prepareStatement(queryInsert);
            psInsert.setString(1, folio);
            psInsert.setString(2, canocontrarecibo);
            psInsert.setString(3, "S");
            psInsert.setString(4, u.getPropiedad("CCENTROCONTABLE").getValor());
            psInsert.setString(5, u.getU_UR());
            psInsert.setInt(6, folioCompromiso);
            psInsert.setString(7, cuentasBancarias[0]);
            psInsert.setString(8, fechasPago[0]);
            psInsert.setString(9, leyendas[0]);
            psInsert.setString(10, caNoCompromiso);
            psInsert.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static boolean insertaCompromisosFederalizados(Connection conn, HttpServletRequest req) throws Exception {
        boolean regreso = false;
        boolean actualiza = false;
        boolean inserta = false;
        Date fAppTmp = null;
        Date fExpTmp = null;
        String caNoCompromiso = req.getParameter("caNoCompromiso");
        String sDocumento = req.getParameter("NoFolioSICOPSnd");
        java.sql.Date fAplicacion = null;
        java.sql.Date fExpedicion = null;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy");
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        StringBuilder sSql = new StringBuilder();
        sSql.append(" SELECT cClave, cRamo, cUnidadCreadora, cCentroContable, fExpedicion, mImporte, cIdTipoPoliza,  ");
        sSql.append("   CASE WHEN cIdTipoMovimiento = 'O' THEN cIdTipoMovimiento ELSE TIPOMOVTO END AS cIdTipoMovimiento,  ");
        sSql.append("   nOrigenPresupuesto, cTipoCambio, cMoneda, dRFC, caNoCompromiso, cDescripcion, TIPOMOVTO, nDocumento  ");
        sSql.append("   FROM  ");
        sSql.append("   (  ");
        sSql.append("   	SELECT '16-Medio Ambiente y Recursos Naturales' AS cClave, enc.cRamo, enc.cUnidadResponsableContable AS cUnidadCreadora,  ");
        sSql.append("   	enc.cCentroContable, enc.fAplicacion AS fExpedicion, SUM(det.mImporte) AS mImporte, 'D' AS cIdTipoPoliza,  ");
        sSql.append("   	CASE WHEN enc.nFolioCompromiso = ( SELECT MIN(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK)   ");
        sSql.append("										WHERE cCentroContable = enc.cCentroContable AND cIdContrato = enc.cIdContrato ) ");
        sSql.append("		THEN 'O' 	");
        sSql.append("		ELSE 'M' END AS cIdTipoMovimiento,	");
        sSql.append("   	1 AS nOrigenPresupuesto, '1' AS cTipoCambio, 'MXN' AS cMoneda,  ");
        sSql.append("		conFed.cIdRFC AS dRFC, enc.caNoCompromiso, 	");
        sSql.append("   	enc.cDescripcionPoliza AS cDescripcion, CASE WHEN SUM(det.mImporte) > 0 THEN 'A' ELSE 'R' END AS TIPOMOVTO,  ");
        sSql.append("  		REPLICATE('0', 6 - LEN('" + sDocumento + "')) + '" + sDocumento + "' AS nDocumento  ");
        sSql.append("   	FROM tCompromisoEncabezado enc WITH (NOLOCK)  ");
        sSql.append("   	INNER JOIN tCompromisoDetalle det WITH (NOLOCK)  ");
        sSql.append("   		ON (det.nFolioCompromiso = enc.nFolioCompromiso)  ");
        sSql.append("   	INNER JOIN pContratoFederalizado conFed WITH (NOLOCK)  ");
        sSql.append("   		ON (conFed.cIdContrato = enc.cIdContrato)  ");
        sSql.append("   	WHERE enc.caNoCompromiso = ?  ");
        sSql.append("   	GROUP BY enc.cRamo, enc.cUnidadResponsableContable, enc.cCentroContable, enc.fAplicacion, conFed.cIdRFC,  ");
        sSql.append("   	enc.caNoCompromiso, enc.cDescripcionPoliza, enc.nFolioCompromiso, enc.cIdContrato  ");
        sSql.append("   ) AS COMFED ");
        try {
            pstmnt = conn.prepareStatement(sSql.toString());
            pstmnt.setString(1, caNoCompromiso);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                String clave = rs.getString("cClave");
                String cRamo = rs.getString("cRamo");
                String cUnidadResponsable = rs.getString("cUnidadCreadora");
                String folioSICOP = "1234";
                String idProceso = "5678";
                String cCentroContable = rs.getString("cCentroContable");
                String fExp = rs.getString("fExpedicion");
                try {
                    fExpTmp = formato2.parse(fExp);
                } catch (ParseException pe) {
                    fExpTmp = formato.parse(fExp);
                }
                fExpedicion = new java.sql.Date(fExpTmp.getTime());
                BigDecimal total = new BigDecimal(rs.getString("mImporte"));
                String cTipoPoliza = rs.getString("cIdTipoPoliza");
                String nFolioPoliza = "";
                String nPolizaCancelacion = "";
                String tipoMovimiento = rs.getString("cIdTipoMovimiento");
                String origenPresupuesto = rs.getString("nOrigenPresupuesto");
                String cuentaBancaria = "";
                String noSolicitud = "";
                String tCambio = rs.getString("cTipoCambio");
                String tMoneda = rs.getString("cMoneda");
                String tSolicitud = "4";
                String volante = "";
                String rfc = rs.getString("dRFC");
                String codSemarnat2 = rs.getString("caNoCompromiso");
                String estatus = "2";
                Integer nEnviadoSICOP = Integer.parseInt(estatus);
                String fAp = req.getParameter("fAplSICOP");
                try {
                    fAppTmp = formato2.parse(fAp);
                } catch (ParseException pe) {
                    fAppTmp = formato.parse(fAp);
                }
                fAplicacion = new java.sql.Date(fAppTmp.getTime());
                String documento = "COMPROMISO";
                String nDocumento = rs.getString("nDocumento");
                String descripcion = rs.getString("cDescripcion");
                // Actualizando en tabla tCompromisosEscabezado
                actualiza = updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso, nDocumento);
                if (actualiza) {
                    // Insertando en tabla
                    inserta = insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
                    CompromisoManager.aplicaCompromiso(conn, caNoCompromiso);
                }
                if (inserta) {
                    regreso = actualiza;
                }
            }
            return regreso;
        } catch (Exception exc) {
            throw exc;
        }
    }

    public static int insertaDetalle(Connection conn, Compromiso c) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tcompromisodetalle  ");
        query.append("             (nfoliocompromiso,  ");
        query.append("              ndocrenglon,  ");
        query.append("              ep,  ");
        query.append("              cevento,  ");
        query.append("              mimporte,  ");
        query.append("              mimportenegativo,  ");
        query.append("              cmes,  ");
        query.append("              ccentrocontable)  ");
        query.append(" VALUES     (?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?,  ");
        query.append("             ?) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            int afectados = 0;
            for (Iterator<CompromisoDetalle> i = c.getDetalle().iterator(); i.hasNext(); ) {
                CompromisoDetalle cd = i.next();
                ps.setInt(1, cd.getnFolioCompromiso());
                ps.setInt(2, cd.getnDocRenglon());
                ps.setString(3, cd.getEP());
                ps.setString(4, cd.getcEvento());
                ps.setBigDecimal(5, cd.getmImporte());
                ps.setBigDecimal(6, cd.getmImporteNegativo());
                ps.setInt(7, cd.getcMes());
                ps.setString(8, cd.getcCentroContable());
                afectados += ps.executeUpdate();
            }
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaDetalleCompromisoFederalizado(Connection conn, String idContrato, int ejercicioFiscal, String centroContable, int folioCompromiso) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO tCompromisoDetalle(nFolioCompromiso, nDocRenglon, EP, cEvento, mImporte, mImporteNegativo, cMes, cCentroContable)");
        sb.append("SELECT ").append(folioCompromiso).append("AS nFolioCompromiso,");
        sb.append("       Row_number()");
        sb.append("         OVER (");
        sb.append("           ORDER BY nmes ) nDocRenglon,");
        sb.append("       ep                  AS EP,");
        sb.append("       'CMP001'            AS cEvento,");
        sb.append("       mimporte            AS mImporte,");
        sb.append("       -mimporte           AS mImporteNegativo,");
        sb.append("       nmes                AS cMes,");
        sb.append("       cidentidadcontable  AS cCentroContable ");
        sb.append(" FROM   pcontratofederalizadodetalle WITH (NOLOCK)  ");
        sb.append(" WHERE  cejercicio = ? ");
        sb.append("       AND cidentidadcontable = ? ");
        sb.append("       AND cidcontrato = ?  ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, ejercicioFiscal);
            ps.setString(2, centroContable);
            ps.setString(3, idContrato);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static boolean insertaFiltrados(Connection conn, StringBuffer archivoFiltrado) throws Exception {
        String[] celdas;
        boolean regreso = false;
        boolean actualiza = false;
        boolean inserta = false;
        Date fAppTmp = null;
        Date fExpTmp = null;
        java.sql.Date fAplicacion = null;
        java.sql.Date fExpedicion = null;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String sRegistro = archivoFiltrado.toString();
            String[] tmp = sRegistro.split("\\|");
            for (int m = 0; m < tmp.length; m++) {
                String linea = tmp[m];
                celdas = linea.split(",");
                String clave = celdas[0];
                String cRamo = celdas[1];
                String cUnidadResponsable = celdas[2];
                String folioSICOP = celdas[3];
                String idProceso = celdas[4];
                String cCentroContable = celdas[5];
                String fExp = celdas[6];
                try {
                    fExpTmp = formato2.parse(fExp);
                } catch (ParseException pe) {
                    fExpTmp = formato.parse(fExp);
                }
                fExpedicion = new java.sql.Date(fExpTmp.getTime());
                BigDecimal total = new BigDecimal(celdas[7]);
                String cTipoPoliza = celdas[8];
                String nFolioPoliza = celdas[9];
                String nPolizaCancelacion = celdas[10];
                String tipoMovimiento = celdas[11];
                String origenPresupuesto = celdas[12];
                String cuentaBancaria = celdas[13];
                String noSolicitud = celdas[14];
                String tCambio = celdas[15];
                String tMoneda = celdas[16];
                String tSolicitud = celdas[17];
                String volante = celdas[18];
                String rfc = celdas[19];
                String caNoCompromiso = celdas[20];
                String codSemarnat2 = celdas[21];
                String estatus = (celdas[22].equals("APLICADO")) ? "2" : "3";
                Integer nEnviadoSICOP = Integer.parseInt(estatus);
                String fAp = celdas[23];
                try {
                    fAppTmp = formato2.parse(fAp);
                } catch (ParseException pe) {
                    fAppTmp = formato.parse(fAp);
                }
                fAplicacion = new java.sql.Date(fAppTmp.getTime());
                String documento = celdas[24];
                String nDocumento = celdas[25];
                String descripcion = celdas[26];
                // Actualizando en tabla tCompromisosEscabezado
                actualiza = updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso, nDocumento);
                if (actualiza) {
                    // Insertando en tabla
                    inserta = insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
                    CompromisoManager.aplicaCompromiso(conn, caNoCompromiso);
                }
                if (inserta) {
                    regreso = actualiza;
                }
            }
            // fin for
            return regreso;
        } catch (Exception exc) {
            throw exc;
        }
    }

    public static boolean insertArchivoCompleto(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, String fExpedicion, BigDecimal total, String tipoMovimiento, String origenPresupuesto, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws Exception {
        PreparedStatement pstmnt = null;
        boolean insertReg = false;
        StringBuilder queryInsert = new StringBuilder();
        try {
            // falta cambiar la tabla del insert
            queryInsert.append("INSERT INTO COMPROMISOS_SICOP(" + " cClave,cRamo,cUnidadCreadora,cFolioSICOP,cIdProceso,cCentroContable,");
            queryInsert.append("									fExpedicion,mImporte,cIdTipoMovimiento,nOrigenPresupuesto,cTipoCambio,cMoneda,");
            queryInsert.append("									cTipoSolicitud,cVolante,dRFC,caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP, ");
            queryInsert.append("						            tDocumento,cCompromisoSicop,cDescripcion) VALUES('");
            queryInsert.append(clave + "','");
            queryInsert.append(cRamo + "','");
            queryInsert.append(cUnidadResponsable + "','");
            queryInsert.append(folioSICOP + "','");
            queryInsert.append(idProceso + "','");
            queryInsert.append(cCentroContable + "', ");
            queryInsert.append("cast ('" + fExpedicion + "' as DateTime2) ,");
            queryInsert.append(total + ",'" + tipoMovimiento + "','");
            queryInsert.append(origenPresupuesto + "','");
            queryInsert.append(tCambio + "','");
            queryInsert.append(tMoneda + "','");
            queryInsert.append(tSolicitud + "','");
            queryInsert.append(volante + "','");
            queryInsert.append(rfc + "','");
            queryInsert.append(caNoCompromiso + "','");
            queryInsert.append(codSemarnat2 + "','");
            queryInsert.append(estatus + "','");
            queryInsert.append(fAplicacion + "','");
            queryInsert.append(documento + "','");
            queryInsert.append(nDocumento + "','");
            queryInsert.append(descripcion + "')");
            pstmnt = conn.prepareStatement(queryInsert.toString());
            int reg = pstmnt.executeUpdate();
            if (reg == 1) {
                insertReg = true;
            } else {
                insertReg = false;
            }
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return insertReg;
    }

    public static boolean insertRegisterLayout(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, BigDecimal total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws Exception {
        PreparedStatement pstmnt = null;
        boolean insertReg = false;
        boolean existReg = false;
        existReg = existeCaNoCompromiso(conn, caNoCompromiso, codSemarnat2);
        if (existReg == false) {
            try {
                StringBuilder queryInsert = new StringBuilder();
                queryInsert.append("INSERT INTO tLayoutCompromisos( ");
                queryInsert.append("  						cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso, ");
                queryInsert.append("  						cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP, ");
                queryInsert.append("  						nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud, ");
                queryInsert.append("  						cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC, ");
                queryInsert.append("  						caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento, ");
                queryInsert.append("  						nDocumento,cDescripcion) ");
                queryInsert.append("  			   		    VALUES('");
                queryInsert.append(clave);
                queryInsert.append("','");
                queryInsert.append(cRamo);
                queryInsert.append("','");
                queryInsert.append(cUnidadResponsable);
                queryInsert.append("','");
                queryInsert.append(folioSICOP);
                queryInsert.append("','");
                queryInsert.append(idProceso);
                queryInsert.append("','");
                queryInsert.append(cCentroContable);
                queryInsert.append("','");
                queryInsert.append(fExpedicion);
                queryInsert.append("',");
                queryInsert.append(total);
                queryInsert.append(",'");
                queryInsert.append(cTipoPoliza);
                queryInsert.append("','");
                queryInsert.append(nFolioPoliza);
                queryInsert.append("','");
                queryInsert.append(nPolizaCancelacion);
                queryInsert.append("','");
                queryInsert.append(tipoMovimiento);
                queryInsert.append("','");
                queryInsert.append(origenPresupuesto);
                queryInsert.append("','");
                queryInsert.append(cuentaBancaria);
                queryInsert.append("','");
                queryInsert.append(noSolicitud);
                queryInsert.append("','");
                queryInsert.append(tCambio);
                queryInsert.append("','");
                queryInsert.append(tMoneda);
                queryInsert.append("','");
                queryInsert.append(tSolicitud);
                queryInsert.append("','");
                queryInsert.append(volante);
                queryInsert.append("','");
                queryInsert.append(rfc);
                queryInsert.append("','");
                queryInsert.append(caNoCompromiso);
                queryInsert.append("','");
                queryInsert.append(codSemarnat2);
                queryInsert.append("','");
                queryInsert.append(estatus);
                queryInsert.append("','");
                queryInsert.append(fAplicacion);
                queryInsert.append("','");
                queryInsert.append(documento);
                queryInsert.append("','");
                queryInsert.append(nDocumento);
                queryInsert.append("','");
                queryInsert.append(descripcion);
                queryInsert.append("')");
                pstmnt = conn.prepareStatement(queryInsert.toString());
                int reg = pstmnt.executeUpdate();
                if (reg == 1) {
                    insertReg = true;
                } else {
                    insertReg = false;
                }
            } finally {
                CloseObject.closeObject(pstmnt);
            }
        } else {
            insertReg = false;
        }
        return insertReg;
    }

    public static String listaRGEnCompromiso(Connection conn, String caNoCompromiso) throws Exception {
        String lista = "";
        String token = "";
        StringBuilder query = new StringBuilder();
        query.append(" SELECT rg.nfoliorelaciongastos  ");
        query.append(" FROM   trelaciongastoscompromisoencabezado rgCompromisoEnc WITH(nolock) ");
        query.append(" INNER JOIN trelaciongastoscompromisodetalle rgCompromisoDet WITH(nolock)  ");
        query.append(" ON rgCompromisoEnc.nfoliorelaciongastoscompromiso = rgCompromisoDet.nfoliorelaciongastoscompromiso ");
        query.append(" INNER JOIN tRELACIONGASTOSEncabezado RG WITH (NOLOCK) ");
        query.append(" ON RG.nFolioRELACIONGASTOS = rgCompromisoDet.nfoliorelaciongastos ");
        query.append(" WHERE  rgCompromisoEnc.canocontrarrecibo = ? AND cTipoPoliza = 'EG' ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, caNoCompromiso);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista = lista + token + String.valueOf(rs.getInt(1));
                token = ",";
            }
            return lista;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<Integer> selectCompromisosCancelar(Connection conn) throws Exception {
        String query = "SELECT nFolioCompromiso FROM tFoliosCompromisosCancelar WITH(NOLOCK)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> folios = new ArrayList<Integer>();
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                folios.add(rs.getInt("nFolioCompromiso"));
            }
            return folios;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tCompromisoEncabezado SET nEnviadoSICOP = 1 WHERE caNoCompromiso in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso, String nDocumento) throws SQLException {
        PreparedStatement pstmntL = null;
        boolean insertado = false;
        int renglones = 0;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + ", nFolioAutSICOP = '" + nDocumento + "' " + " WHERE caNoCompromiso = '" + caNoCompromiso + "'";
        try {
            pstmntL = conn.prepareStatement(queryUpdateEstatus);
            renglones = pstmntL.executeUpdate();
            if (renglones > 0)
                insertado = true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
        return insertado;
    }

    public static boolean actualizaFolioSuficiencia(Connection conn) throws SQLException {
        PreparedStatement pstmntL = null;
        boolean insertado = false;
        int renglones = 0;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE comp SET nFolioSuficiencia = NoSuficiencia ");
        query.append("		FROM CLC_SUFICIENCIA clc WITH (NOLOCK) ");
        query.append("		INNER JOIN tCompromisoEncabezado Comp WITH (NOLOCK) ");
        query.append("		on  substring(clc.controlInternoRamo,2,20) =Comp.caNoCompromiso ");
        query.append("		WHERE ( NFOLIOSUFICIENCIA IS NULL or NFOLIOSUFICIENCIA = '') and DescripcionEstatus = 'Aplicado'");
        try {
            pstmntL = conn.prepareStatement(query.toString());
            renglones = pstmntL.executeUpdate();
            if (renglones > 0)
                insertado = true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
        return insertado;
    }

    public static boolean actualizaFolioSuficienciaIntegrada(Connection conn) throws SQLException {
        PreparedStatement pstmntL = null;
        boolean insertado = false;
        int renglones = 0;
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE C SET nFolioSuficiencia = NoSuficiencia FROM CLC_SUFICIENCIA clc WITH (NOLOCK) ");
        query.append("		INNER JOIN tIntegraFoliosCompromiso integra WITH (NOLOCK) ");
        query.append("		ON  substring(clc.controlInternoRamo,2,20) = integra.caNoIntegradaComp ");
        query.append("		INNER JOIN tCompromisoEncabezado C WITH (NOLOCK) ");
        query.append("		ON C.nFolioCompromiso = integra.nFolioCompromiso ");
        query.append("		WHERE ( NFOLIOSUFICIENCIA IS NULL or NFOLIOSUFICIENCIA = '') and DescripcionEstatus = 'Aplicado' ");
        try {
            pstmntL = conn.prepareStatement(query.toString());
            renglones = pstmntL.executeUpdate();
            if (renglones > 0)
                insertado = true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
        return insertado;
    }

    public static boolean actualizaFolioCompromiso(Connection conn) throws SQLException {
        PreparedStatement pstmntL = null;
        boolean insertado = false;
        int renglones = 0;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE COMP SET nFolioAutSICOP = NO_COMPROMISO, nEnviadoSicop = 2 ");
        query.append(" FROM CLC_ANALISIS_COMPROMISOS ANALISIS");
        query.append(" INNER JOIN tCompromisoEncabezado COMP");
        query.append(" ON CONTROL_INTERNO_RAMO = caNoCompromiso");
        query.append(" WHERE DESCRIPCION_ESTATUS = 'Aplicado' and ( nFolioAutSICOP is null or nFolioAutSICOP = '') ");
        try {
            pstmntL = conn.prepareStatement(query.toString());
            renglones = pstmntL.executeUpdate();
            if (renglones > 0)
                insertado = true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
        return insertado;
    }

    public static boolean actualizaFolioCompromisoIntegrada(Connection conn) throws SQLException {
        PreparedStatement pstmntL = null;
        boolean insertado = false;
        int renglones = 0;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE COMP SET nFolioAutSICOP = NO_COMPROMISO, nEnviadoSicop = 2 ");
        query.append(" FROM CLC_ANALISIS_COMPROMISOS ANALISIS");
        query.append(" INNER JOIN tIntegraFoliosCompromiso integra WITH (NOLOCK) ");
        query.append(" ON CONTROL_INTERNO_RAMO = integra.caNoIntegradaComp ");
        query.append(" INNER JOIN tCompromisoEncabezado COMP ");
        query.append(" ON COMP.nFolioCompromiso = integra.nFolioCompromiso");
        query.append(" WHERE DESCRIPCION_ESTATUS = 'Aplicado' AND cDocumentoHaplicado = 'S' and ( nFolioAutSICOP is null or nFolioAutSICOP = '') ");
        try {
            pstmntL = conn.prepareStatement(query.toString());
            renglones = pstmntL.executeUpdate();
            if (renglones > 0)
                insertado = true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
        return insertado;
    }

    public CompromisoManager() {
        super();
    }

    public static int insertaPrecomFinanciero(Connection conn, PrecompromisoFinanciero precompromisoFinanciero) throws SQLException {
        int insertados = 0;
        insertados += insertaPrecomFinancieroEncabezado(conn, precompromisoFinanciero.getEncabezado());
        for (PrecompromisoFinancieroDetalle detalle : precompromisoFinanciero.getDetalle()) insertados += insertaPrecomFinancieroDetalle(conn, detalle);
        return insertados;
    }

    public static int insertaPrecomFinancieroEncabezado(Connection conn, PrecompromisoFinancieroEncabezado encabezado) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPrecomFinancieroEncabezado(nFolioPrecomFinanciero,  fCarga, fAplicacion,  cCentroContable, cRamo, cUnidadResponsable, cTipoPoliza");
        query.append(", aEjercicioFiscal, u_login)");
        query.append("VALUES (?, ?, ?, ?, ?, ?,  ?, ?, ?)  ");
        try {
            int param = 1;
            log.debug("Insertando: " + encabezado);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(param++, encabezado.getFolioPrecomFinanciero());
            ps.setDate(param++, java.sql.Date.valueOf(encabezado.getFechaCarga()));
            ps.setDate(param++, java.sql.Date.valueOf(encabezado.getFechaAplicacion()));
            ps.setString(param++, encabezado.getCentroContable());
            ps.setString(param++, encabezado.getRamo());
            ps.setString(param++, encabezado.getUnidadResponsable());
            ps.setString(param++, encabezado.getTipoPoliza());
            ps.setInt(param++, encabezado.getEjercicioFiscal());
            ps.setString(param++, encabezado.getLogin());
            log.trace("Ejecutando: " + query);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaPrecomFinancieroDetalle(Connection conn, PrecompromisoFinancieroDetalle detalle) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPrecomFinancieroDetalle ");
        query.append("(nDocRenglon, EP, cEvento, mImporte, mImporteNegativo, nFolioPrecomFinanciero, cMes, cCentroContable)");
        query.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            int param = 1;
            log.debug("Insertando: " + detalle);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(param++, detalle.getRenglon());
            ps.setString(param++, detalle.getEP());
            ps.setString(param++, detalle.getcEvento());
            ps.setBigDecimal(param++, detalle.getImporte());
            ps.setBigDecimal(param++, detalle.getImporteNegativo());
            ps.setInt(param++, detalle.getFolioPrecomFinanciero());
            ps.setInt(param++, detalle.getMes());
            ps.setString(param++, detalle.getCentroContable());
            log.trace("Ejecutando: " + query);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaCompromiso(Connection conn, Compromiso compromiso) throws Exception {
        int insertados = 0;
        insertados += insertaCompromisoEncabezado(conn, compromiso.getEncabezado());
        insertados += CompromisoManager.insertaDetalle(conn, compromiso);
        return insertados;
    }

    public static int compromisoEsIP(Connection conn, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        int regreso = 0;
        String query = "SELECT COUNT(*) FROM tCompromisoDetalle WITH (NOLOCK)  WHERE nFolioCompromiso =? AND SUBSTRING(EP,40,1) = '4'";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                regreso = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return regreso;
    }

    public static int compromisoNoEsIP(Connection conn, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        int regreso = 0;
        String query = "SELECT COUNT(*) FROM tCompromisoDetalle WITH (NOLOCK)  WHERE nFolioCompromiso =? AND SUBSTRING(EP,40,1) != '4'";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                regreso = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return regreso;
    }

    public static String consultaContrato(Connection conn, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String tipo = "";
        String query = "SELECT cIdContrato FROM tCompromisoEncabezado WITH (NOLOCK)  WHERE nFolioCompromiso = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                tipo = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return tipo;
    }

    public static boolean contratoAnterior(Connection conn, String contrato) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean esContratoAnt = false;
        int cuantos = 0;
        String query = "SELECT COUNT(*) esSICOP FROM tCompromisoEncabezado WITH (NOLOCK)  WHERE cIdContrato = ? and nFolioAutSICOP <> -1";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, contrato);
            rs = pst.executeQuery();
            if (rs.next()) {
                cuantos = rs.getInt(1);
            }
            if (cuantos > 0) {
                esContratoAnt = true;
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return esContratoAnt;
    }

    public static boolean contratoMenor300UMAS(Connection conn, int folio) throws Exception {
        PreparedStatement pst = null, pst2 = null;
        ResultSet rs = null, rs2 = null;
        boolean compromete = false;
        String query = "SELECT SUM(mimporte) FROM tCompromisoDetalle WITH (NOLOCK)  WHERE nFolioCompromiso = ?";
        float importeMaximo = 0, importeCompromiso = 0;
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                importeCompromiso = rs.getFloat(1);
            }
            pst2 = conn.prepareStatement("SELECT (CANT_SALARIO * SALARIO) ImporteMax FROM mCatSalario WITH (NOLOCK) ");
            rs2 = pst2.executeQuery();
            if (rs2.next()) {
                importeMaximo = rs2.getFloat(1);
            }
            if (importeCompromiso < importeMaximo) {
                compromete = true;
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(pst2);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
        }
        return compromete;
    }

    public static void insertaCompromisoPDNominaEncabezado(Connection conn, int folioCompromiso, String idIntegracion, String contrarecibo, Usuario u, String CxPPDNomina) throws Exception {
        PreparedStatement psInsert = null;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tCompromisoEncabezado( nFolioCompromiso, fCarga, cIdContrato, cTipoContrato, fAplicacion, cCentroContable, cRamo, cUnidadResponsable, cDocumentoHaplicado,  caNoCompromiso, ");
        queryInsert.append("nEnviadoSICOP,  cTipoPoliza, nMes,  aEjercicioFiscal, cUnidadResponsableContable, cDescripcionPoliza, usuario, cRadicado ) ");
        queryInsert.append(" VALUES ( ? , GETDATE(), ?, 'RE' , GETDATE(), ?, '16',  ?, 'S' ,  ?, 0,  'DI' , ?,  ?, '" + unidadContable + "',   ? , ?, ? ) ");
        try {
            String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
            String descripcionPoliza = "REGISTRO ORIGINAL DEL PAGO DIRECTO DE NOMINA" + idIntegracion;
            String radicado = "N";
            psInsert = conn.prepareStatement(queryInsert.toString());
            psInsert.setInt(1, folioCompromiso);
            psInsert.setString(2, CxPPDNomina);
            psInsert.setString(3, u.getPropiedad("CCENTROCONTABLE").getValor());
            psInsert.setString(4, u.getU_UR());
            psInsert.setString(5, contrarecibo);
            psInsert.setInt(6, calculaMes(Integer.parseInt(ejercicioFiscal)));
            psInsert.setInt(7, Integer.parseInt(ejercicioFiscal));
            psInsert.setString(8, descripcionPoliza);
            psInsert.setString(9, u.getLogin());
            psInsert.setString(10, radicado);
            psInsert.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static void insertaCompromisoPDNominaDetalle(Connection conn, int folioCompromiso, String foliosRG) throws Exception {
        PreparedStatement psInsertDetalle = null;
        StringBuilder query = new StringBuilder();
        query.append("	 INSERT INTO dbo.tcompromisodetalle  (nfoliocompromiso, ndocrenglon,ep,cevento, mimporte, mimportenegativo, cmes, ccentrocontable) ");
        query.append(" SELECT " + folioCompromiso + " AS nFolioCompromiso, Row_number()  OVER( ORDER BY ep, cmes ASC) AS nDocRenglon");
        query.append(", ep, 'CMP001' AS cEvento, ");
        query.append("Sum(mimportemasiva) AS mImporte, ");
        query.append(" -Sum(mimportemasiva)   AS mImporteNegativo, cmes, ccentrocontable ");
        query.append("FROM   tPagoDirectoDetalle rgDetalle WITH(nolock) ");
        query.append(" WHERE  rgDetalle.nFolioPagoDirecto IN ( " + foliosRG + " ) ");
        query.append(" GROUP  BY ep, cmes, ccentrocontable ");
        String queryInsert = query.toString();
        try {
            psInsertDetalle = conn.prepareStatement(queryInsert);
            psInsertDetalle.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsertDetalle);
        }
    }

    public static int insertaPDNominaCompromisoEncabezado(Connection conn, int folioCompromiso, String canocontrareciobo, String cuentaBancaria, String fechaIntegracion, String leyenda, Usuario u) throws Exception {
        PreparedStatement psInsert = null;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tPDNominaCompromisoEncabezado ( nFolioPDNominaCompromiso,  canocontrarrecibo, cDocumentHAplicado, fAplicacion, cCentroContable,  cUnidaResponable,  nFolioCompromiso, ");
        queryInsert.append("cCuentaBancaria, fProgramadaAutorizacion, nLeyenda) ");
        queryInsert.append("VALUES( ?, ?, ?, GETDATE(), ?, ?,  ?, ?, ?, ?) ");
        try {
            CFSequenceManager sequence = CFSequenceManager.getInstance();
            int nFolioPDNominaCompromiso = sequence.nextVal("PDNOMINACOMPROMISO");
            psInsert = conn.prepareStatement(queryInsert.toString());
            psInsert.setInt(1, nFolioPDNominaCompromiso);
            psInsert.setString(2, canocontrareciobo);
            psInsert.setString(3, "S");
            psInsert.setString(4, u.getPropiedad("CCENTROCONTABLE").getValor());
            psInsert.setString(5, u.getU_UR());
            psInsert.setInt(6, folioCompromiso);
            psInsert.setString(7, cuentaBancaria);
            psInsert.setString(8, fechaIntegracion);
            psInsert.setString(9, leyenda);
            psInsert.executeUpdate();
            return nFolioPDNominaCompromiso;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static void insertaPDNominaCompromisoDetalle(Connection conn, int folioPDNominaCompromiso, String folioNomina) throws Exception {
        PreparedStatement psInsert = null;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO dbo.tPDNominaCompromisoDetalle( nFolioPDNominaCompromiso , nFolioPagoDirecto ) VALUES  ( ?, ? ) ");
        try {
            psInsert = conn.prepareStatement(queryInsert.toString());
            psInsert.setInt(1, folioPDNominaCompromiso);
            psInsert.setInt(2, Integer.parseInt(folioNomina.trim()));
            psInsert.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static boolean updateHeaderCompromisosMenor300UMAS(Connection conn, Integer nEnviadoSICOP, String nFolio, String nDocumento) throws SQLException {
        PreparedStatement pstmntL = null;
        boolean insertado = false;
        int renglones = 0;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + ", nFolioAutSICOP = '" + nDocumento + "' " + " WHERE nFolioCompromiso = " + nFolio;
        try {
            pstmntL = conn.prepareStatement(queryUpdateEstatus);
            renglones = pstmntL.executeUpdate();
            if (renglones > 0)
                insertado = true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
        return insertado;
    }

    public static int actualizaEnvioSICOPSuf(Connection conn, String foliosSuf) throws Exception {
        String query = "UPDATE tCompromisoEncabezado SET nEnviadoSICOPSuficiencia = 1 WHERE nFolioCompromiso in (" + foliosSuf + ")";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaEnvioSICOPCompromiso(Connection conn, String foliosComp) throws Exception {
        String query = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = 1 WHERE caNoCompromiso in (" + foliosComp + ")";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaEnvioSICOPCompromisoIntegrada(Connection conn, String foliosComp) throws Exception {
        String query = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = 1 WHERE nFolioCompromiso in (" + foliosComp + ")";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static List<CompromisoDTO> generaListadoCompromisoAut(Connection conn) throws SQLException {
        List<CompromisoDTO> lista = new ArrayList<>();
        String sql = "SELECT cidproceso, ccompromisosicop, " + "CASE cidtipomovimiento " + "  WHEN 'R' THEN 'R - REDUCCIÓN' " + "  WHEN 'O' THEN 'O - ORIGINAL' " + "  WHEN 'A' THEN 'A - AMPLIACION' " + "END AS tipoMov, " + "'RHQ' AS ur, " + "dbo.Fn_obtenerepsporcompromiso(COMP.nfoliocompromiso) AS ep, " + "comp.cidcontrato, " + "REPLACE(sicop.cdescripcion, '\"', '') AS cdescripcion, " + "CASE norigenpresupuesto " + "  WHEN 1 THEN '1 - DISPONIBLE' " + "  WHEN 2 THEN '2 - SUFICIENCIA PRESUPUESTAL' " + "  WHEN 3 THEN '3 - SUFICIENCIA PRESUPUESTAL CON OLI' " + "  WHEN 4 THEN '4 - SERVICIOS PERSONALES' " + "END AS origen, " + "0 AS saldo, mimporte, mimporte AS disponible " + "FROM compromisos_sicop SICOP WITH(NOLOCK) " + "INNER JOIN tcompromisoencabezado COMP  WITH(NOLOCK) " + "ON SICOP.canocompromiso = COMP.canocompromiso " + "WHERE cestatus = 'TRAMITE'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CompromisoDTO dto = new CompromisoDTO();
                dto.setCidproceso(rs.getString("cidproceso"));
                dto.setCcompromisosicop(rs.getString("ccompromisosicop"));
                dto.setTipoMov(rs.getString("tipoMov"));
                dto.setUr(rs.getString("ur"));
                dto.setEp(rs.getString("ep"));
                dto.setCidcontrato(rs.getString("cidcontrato"));
                dto.setCdescripcion(rs.getString("cdescripcion"));
                dto.setOrigen(rs.getString("origen"));
                dto.setSaldo(rs.getDouble("saldo"));
                dto.setMimporte(rs.getDouble("mimporte"));
                dto.setDisponible(rs.getDouble("disponible"));
                lista.add(dto);
            }
        }
        return lista;
    }

    public static void actualizaEnvioSICOPRG(Connection conn, String foliosIntegracion) throws Exception {
        String queryInsert = "UPDATE tRELACIONGASTOSEncabezado SET nEnviadoSICOP = 1 WHERE nFolioRELACIONGASTOS = ?";
        PreparedStatement psInsert = null;
        try {
            psInsert = conn.prepareStatement(queryInsert);
            String[] folios = foliosIntegracion.split(",");
            for (String i : folios) {
                psInsert.setInt(1, Integer.parseInt(i.trim()));
                psInsert.executeUpdate();
            }
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static void insertarDatosSuficiencia(Connection conn, List<String[]> renglonesArchivo) throws Exception {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO CLC_SUFICIENCIA (");
        sb.append("Ramo, UnidadCreadora, FolioSuficiencia, NoSuficiencia, FechaExpedicion, FechaAplicacion, FechaCancelacion, FechaAutorizacion, ");
        sb.append("IDDocumento, DescripcionDocumento, FlujoDeTrabajo, NoProceso, Estatus, DescripcionEstatus, AutorizadoTramite, Movimiento, ");
        sb.append("DescripcionMovimiento, OrigenPresupuesto, DescripcionOrigenPresupuesto, NoMovimiento, Justificacion, ControlInternoRamo, ");
        sb.append("FolioAutorizacion, TotalSuficiencia, ImporteClavePresupuestaria, Anio, UnidadPresupuestaria, Finalidad, Funcion, Subfuncion, ");
        sb.append("Reasignacion, ActividadInstitucional, ProgramaPresupuestario, Capitulo, Concepto, PartidaGenerica, PartidaEspecifica, ObjetoGasto, ");
        sb.append("TipoGasto, FuenteFinanciamiento, EntidadFederativa, ClaveCartera, CentroCostos, ControlOperativo, Plurianual, OrganismoFinanciador, ");
        sb.append("Auxiliar1, Auxiliar2, Auxiliar3, Enero, Febrero, Marzo, Abril, Mayo, Junio, Julio, Agosto, Septiembre, Octubre, Noviembre, Diciembre, ");
        sb.append("RenglonSuficiencia, IDEvento, Evento, Capturista, Autorizador, Cancelador) VALUES (");
        for (int i = 0; i < 67; i++) {
            // 67 campos
            sb.append("?");
            if (i < 66) {
                sb.append(", ");
            }
        }
        sb.append(");");
        try {
            ps = conn.prepareStatement(sb.toString());
            int batchSize = 10;
            int count = 0;
            for (String[] item : renglonesArchivo) {
                StringBuilder sql2 = new StringBuilder();
                for (int i = 0; i < 67; i++) {
                    String valor = (i < item.length) ? item[i] : null;
                    // PreparedStatement es
                    ps.setString(i + 1, valor);
                    // 1-based
                    sql2.append(valor + ";");
                }
                ps.addBatch();
                log.debug("Agregando Suficiencias:" + sql2.toString());
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                    conn.commit();
                    log.debug("Commit correcto");
                }
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void insertarCompromisos(Connection conn, List<String[]> renglonesArchivo) throws SQLException {
        PreparedStatement stmt = null;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO CLC_ANALISIS_COMPROMISOS (");
        sqlBuilder.append("Ramo, Unidad_Creadora, Folio_Compromiso, No_Compromiso, Fecha_Expedicion, ");
        sqlBuilder.append("Fecha_Aplicacion, Fecha_Cancelacion, Fecha_Firma_Contrato, ID_Documento, Descripcion_Documento, ");
        sqlBuilder.append("Flujo_de_Trabajo, No_Proceso, Estatus, Descripcion_Estatus, Autorizado_o_Tramite, ");
        sqlBuilder.append("Movimiento, Descripcion_Movimiento, Origen_de_Presupuesto, Descripcion_Origen_Presupuesto, ");
        sqlBuilder.append("No_Suficiencia, No_OLI, Clave_Beneficiario, RFC, Nombre_Beneficiario, ");
        sqlBuilder.append("Razon_Social, Erogacion, Descripcion_Tipo_Erogacion, Operacion, Descripcion_Tipo_Operacion, ");
        sqlBuilder.append("Aplica_CompraNet, Justificacion_NO_CompraNet, Codigo_Expediente, No_Procedimiento, ");
        sqlBuilder.append("Codigo_Contrato, No_de_Contrato, Objeto_del_Contrato, Control_Interno_Ramo) ");
        sqlBuilder.append("VALUES (");
        for (int i = 0; i < 37; i++) {
            sqlBuilder.append("?");
            if (i < 36) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");
        String sql = sqlBuilder.toString();
        try {
            stmt = conn.prepareStatement(sql);
            int batchSize = 10;
            int count = 0;
            for (String[] row : renglonesArchivo) {
                StringBuilder sql2 = new StringBuilder();
                try {
                    stmt.setInt(1, Integer.parseInt(row[0]));
                    stmt.setString(2, row[1]);
                    stmt.setInt(3, Integer.parseInt(row[2]));
                    stmt.setString(4, row[3]);
                    stmt.setString(5, row[4]);
                    stmt.setString(6, row[5]);
                    stmt.setString(7, row[6]);
                    stmt.setString(8, row[7]);
                    stmt.setInt(9, Integer.parseInt(row[8]));
                    stmt.setString(10, row[9]);
                    stmt.setString(11, row[10]);
                    stmt.setInt(12, Integer.parseInt(row[11]));
                    stmt.setInt(13, Integer.parseInt(row[12]));
                    stmt.setString(14, row[13]);
                    stmt.setString(15, row[14]);
                    stmt.setString(16, row[15]);
                    stmt.setString(17, row[16]);
                    stmt.setInt(18, Integer.parseInt(row[17]));
                    stmt.setString(19, row[18]);
                    stmt.setString(20, row[19]);
                    stmt.setString(21, row[20]);
                    stmt.setString(22, row[21]);
                    stmt.setString(23, row[22]);
                    stmt.setString(24, row[23]);
                    stmt.setString(25, row[24]);
                    stmt.setInt(26, Integer.parseInt(row[25]));
                    stmt.setString(27, row[26]);
                    stmt.setInt(28, Integer.parseInt(row[27]));
                    stmt.setString(29, row[28]);
                    stmt.setString(30, row[29]);
                    stmt.setString(31, row[30]);
                    stmt.setString(32, row[31]);
                    stmt.setString(33, row[32]);
                    stmt.setString(34, row[33]);
                    stmt.setString(35, row[34]);
                    stmt.setString(36, row[35]);
                    stmt.setString(37, row[36]);
                    for (int h = 0; h < 37; h++) {
                        sql2.append(row[h]).append("; ");
                    }
                    stmt.addBatch();
                    log.debug("Agregando compromisos:" + sql2.toString());
                    if (++count % batchSize == 0) {
                        stmt.executeBatch();
                        conn.commit();
                        log.debug("Commit correcto");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al convertir números en fila: " + Arrays.toString(row));
                    e.printStackTrace();
                }
            }
            stmt.executeBatch();
        } finally {
            CloseObject.closeObject(stmt);
        }
    }

    public static ArrayList<String> extraeOficioRG(Connection conn, String cxp) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        ArrayList<String> resultados = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT cIdContrato, RG.caNoContrarrecibo, RG.cIdRFC, CNOMBRE, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LTRIM(RTRIM(cConcepto)), CHAR(160), ' '), '  ', ' ') , ',', ''), CHAR(10), ''),CHAR(9), ''),CHAR(13), '')  cConcepto, ");
            sb.append(" mImporteMasIva, comp.caNoCompromiso, lay.cFolioOficio ");
            sb.append(" FROM tCompromisoEncabezado COMP WITH (NOLOCK) ");
            sb.append(" INNER JOIN tLayoutsCreadosRelacionGastosHeader lay WITH (NOLOCK) ");
            sb.append(" ON lay.sAuxiliarComodin = COMP.cIdContrato ");
            sb.append(" INNER JOIN tRELACIONGASTOSEncabezado RG WITH (NOLOCK) ");
            sb.append(" ON lay.sNoContrarrecibo = RG.caNoContrarrecibo ");
            sb.append(" WHERE RG.cDocumentoHaplicado = 'S' AND COMP.caNoCompromiso = ?");
            pst = conn.prepareStatement(sb.toString());
            pst.setString(1, cxp);
            rs = pst.executeQuery();
            String encabezados = "Contrato, Contrarrecibo, RFC, Nombre, Concepto, Importe, Compromiso, Folio Oficio" + "\r\n";
            resultados.add(encabezados);
            while (rs.next()) {
                String fila = rs.getString("cIdContrato") + "," + rs.getString("caNoContrarrecibo") + "," + rs.getString("cIdRFC") + "," + rs.getString("CNOMBRE") + "," + rs.getString("cConcepto") + "," + rs.getString("mImporteMasIva") + "," + rs.getString("caNoCompromiso") + "," + rs.getString("cFolioOficio") + "\r\n";
                resultados.add(fila);
            }
            return resultados;
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
    }

    public static void aplicaCompromisos(Connection conn, String condicion) throws Exception {
        PreparedStatement pstmntL = null;
        ResultSet rs = null;
        List<Integer> folios = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT COMPROMISOS.nFolioCompromiso  ");
        query.append(" FROM CLC_ANALISIS_COMPROMISOS CLC WITH (NOLOCK) ");
        query.append(" INNER JOIN tCompromisoEncabezado COMPROMISOS WITH (NOLOCK) ");
        query.append(" ON CLC.Control_Interno_Ramo = COMPROMISOS.caNoCompromiso ");
        query.append(" INNER JOIN tCompromisoDetalle COMPDET WITH (NOLOCK) ");
        query.append(" ON COMPROMISOS.nFolioCompromiso = COMPDET.nFolioCompromiso ");
        query.append(" WHERE COMPROMISOS.cDocumentoHaplicado IS NULL AND " + condicion);
        try {
            AccountingEngine ae = new AccountingEngine(GestionInterface.ATT_CONEXION);
            ae.setValidaInsuficienciaDeSaldo(true);
            pstmntL = conn.prepareStatement(query.toString());
            rs = pstmntL.executeQuery();
            while (rs.next()) {
                folios.add(rs.getInt("nFolioCompromiso"));
            }
            for (Integer folioLista : folios) {
                if (!compromisoAplicado(conn, folioLista))
                    ae.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(folioLista), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
            }
        } finally {
            CloseObject.closeObject(pstmntL);
        }
    }

    public static void aplicaDecrementosSuficiencia(Connection conn) throws Exception {
        PreparedStatement pstmntL = null;
        ResultSet rs = null;
        List<Integer> folios = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append(" SELECT DISTINCT COMPROMISOS.nFolioCompromiso ");
        query.append(" FROM CLC_SUFICIENCIA CLC WITH (NOLOCK) ");
        query.append(" INNER JOIN tCompromisoEncabezado COMPROMISOS WITH (NOLOCK)  ");
        query.append(" ON CLC.ControlInternoRamo = 'S' + COMPROMISOS.caNoCompromiso ");
        query.append(" WHERE COMPROMISOS.cDocumentoHaplicado IS NULL AND Movimiento = 'R' and DescripcionEstatus = 'Aplicado'");
        try {
            AccountingEngine ae = new AccountingEngine(GestionInterface.ATT_CONEXION);
            ae.setValidaInsuficienciaDeSaldo(true);
            pstmntL = conn.prepareStatement(query.toString());
            rs = pstmntL.executeQuery();
            while (rs.next()) {
                folios.add(rs.getInt("nFolioCompromiso"));
            }
            for (Integer folioLista : folios) {
                if (!compromisoAplicado(conn, folioLista))
                    ae.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(folioLista), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
            }
        } finally {
            CloseObject.closeObject(pstmntL);
        }
    }

    private static boolean compromisoAplicado(Connection conn, int nFolioCompromiso) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT cDocumentoHAplicado FROM  tcompromisoencabezado WHERE nFolioCompromiso = ?");
        ResultSet rs = null;
        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            ps.setInt(1, nFolioCompromiso);
            rs = ps.executeQuery();
            if (rs.next())
                return "S".equalsIgnoreCase(rs.getString(1));
            return false;
        } finally {
            CloseObject.closeObject(rs);
        }
    }

    public static String obtenerFoliosIntegrados(Connection conn, String canoIntegradaComp) throws SQLException {
        StringBuilder resultado = new StringBuilder();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String query = "SELECT nFolioCompromiso FROM dbo.tIntegraFoliosCompromiso WITH (NOLOCK) WHERE canointegradacomp in ( " + canoIntegradaComp + "  )";
        try {
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            log.info(query + " .Integradas " + canoIntegradaComp);
            while (rs.next()) {
                if (resultado.length() > 0) {
                    resultado.append(",");
                }
                resultado.append(rs.getString("nFolioCompromiso"));
            }
        } finally {
            CloseObject.closeObject(stmt);
            CloseObject.closeObject(rs);
        }
        return resultado.toString();
    }

    public static ArrayList<String> BuscaSuficienciaIntegrada(Connection conn, String folioSuficiencia, String reimprime, String cxpIntegrada) throws Exception {
        ArrayList<String> arrListaSuf = new ArrayList<>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] arrFolios = cxpIntegrada.split(",");
        StringBuilder sbEncabezado = new StringBuilder();
        sbEncabezado.append(" SELECT DISTINCT 'H', ");
        sbEncabezado.append("	CASE WHEN (SELECT COUNT(*) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = Comp.cIdContrato AND cDocumentoHaplicado = 'S' ");
        sbEncabezado.append("				AND nFolioCompromiso != COMP.nFolioCompromiso and nFolioSuficiencia IS NOT NULL AND nFolioSuficiencia != -1 ) = 0 	");
        sbEncabezado.append("		THEN 'O' ");
        sbEncabezado.append("		WHEN (SELECT COUNT(*) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = Comp.cIdContrato AND cDocumentoHaplicado = 'S' ");
        sbEncabezado.append("			AND nFolioCompromiso != COMP.nFolioCompromiso and nFolioSuficiencia IS not NULL AND nFolioSuficiencia != -1) > 0 ");
        sbEncabezado.append("			AND (SELECT SUM(mImporte) FROM tCompromisoDetalle WITH (NOLOCK) WHERE nFolioCompromiso =  Comp.nFolioCompromiso) > 0 ");
        sbEncabezado.append("		THEN 'A' ");
        sbEncabezado.append("		WHEN (SELECT SUM(mImporte) FROM tCompromisoDetalle WITH (NOLOCK) WHERE nFolioCompromiso = Comp.nFolioCompromiso ) < 0 ");
        sbEncabezado.append("		THEN 'R' END Movimiento,  ");
        sbEncabezado.append("	CASE WHEN (SELECT COUNT(*) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = Comp.cIdContrato AND cDocumentoHaplicado = 'S' ) = 0 THEN '1' ");
        sbEncabezado.append("		 WHEN (SELECT COUNT(*) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = Comp.cIdContrato AND cDocumentoHaplicado = 'S') > 0 ");
        sbEncabezado.append("				AND (SELECT SUM(mImporte) FROM tCompromisoDetalle WITH (NOLOCK) WHERE nFolioCompromiso =  Comp.nFolioCompromiso) > 0 THEN '1' ");
        sbEncabezado.append("		 WHEN (SELECT SUM(mImporte) FROM tCompromisoDetalle WITH (NOLOCK) WHERE nFolioCompromiso =  Comp.nFolioCompromiso) < 0 THEN '3' END origen, ");
        sbEncabezado.append("	0 precom, ");
        sbEncabezado.append("	'SUFICIENCIA PRESUPUESTAL PARA EL CONTRATO ' + cIdContrato Justificacion, ");
        sbEncabezado.append("	'' ,'RHQ', 'RHQ', 'RHQ', 16, 16, 16,  ");
        sbEncabezado.append("	FORMAT(GETDATE(), 'dd/MM/yyyy') FECHA_EXP, ");
        sbEncabezado.append("	FORMAT(GETDATE(), 'dd/MM/yyyy')  FECHA_APL,");
        sbEncabezado.append("	'S' + integra.caNoIntegradaComp ID_CTR_INT_301, cIdContrato, ");
        sbEncabezado.append("	COALESCE( cFolioValidacion, 'S' + integra.caNoIntegradaComp) COMODIN4_502 ");
        sbEncabezado.append(" FROM tCompromisoEncabezado Comp WITH (NOLOCK) ");
        sbEncabezado.append(" INNER JOIN tIntegraFoliosCompromiso integra WITH (NOLOCK) ");
        sbEncabezado.append("		ON Comp.nFolioCompromiso = integra.nFolioCompromiso ");
        sbEncabezado.append(" WHERE integra.caNoIntegradaComp IN (").append(cxpIntegrada).append(");");
        try {
            pstmntH = conn.prepareStatement(sbEncabezado.toString());
            rs = pstmntH.executeQuery();
            int k = 0;
            while (rs.next()) {
                StringBuilder filaH = new StringBuilder();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    filaH.append(rs.getString(i)).append(",");
                }
                arrListaSuf.add(filaH.toString());
                arrListaSuf.add("\r\n");
                // Ejecutar y agregar los detalles para este folio
                StringBuilder sbDetalle = new StringBuilder();
                sbDetalle.append("SELECT CASE WHEN SUM(tCD.mImporte) > 0 THEN 542 ELSE 522 END  as ID_EVENTO, ");
                sbDetalle.append("	CASE WHEN SUM(tCD.mImporte) > 0 THEN '156_SPOA' ELSE '158_SPRA' END as EVENTO, ");
                sbDetalle.append("	'M' as ID_EMISOR, ");
                sbDetalle.append("	rtrim(ltrim(tCEP.cRamo)) cRamo,");
                sbDetalle.append("	rtrim(ltrim(tCEP.cUnidadResponsableEP)) cUnidadResponsableEP,  ");
                sbDetalle.append("	tCEP.aEjercicioFiscal,");
                sbDetalle.append("	tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion,  ");
                sbDetalle.append("	SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2),");
                sbDetalle.append("	tCEP.cActividadInstitucional, ");
                sbDetalle.append("	tCEP.cProgramaPresupuestario, ");
                sbDetalle.append("	SUBSTRING(tCEP.cPartida,1,1), SUBSTRING(tCEP.cPartida,2,1), SUBSTRING(tCEP.cPartida,3,1), ");
                sbDetalle.append("	SUBSTRING(tCEP.cPartida,4,2), tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, ");
                sbDetalle.append("	tCEP.cEntidadFederativa,  ");
                sbDetalle.append("	SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP),45,11), ");
                sbDetalle.append("	'0000000000', '00', '000', '000', '00000', '00000', '0000000000', ");
                sbDetalle.append("	SUM(CONVERT(decimal(14,2), ABS(tCD.mImporte))), ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 1 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Enero, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 2 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Febrero, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 3 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Marzo, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 4 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Abril, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 5 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Mayo, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 6 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Junio, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 7 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Julio, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 8 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Agosto, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 9 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Sept, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 10 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Oct, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 11 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Nov, ");
                sbDetalle.append("	SUM(CASE tCD.cMes WHEN 12 THEN CONVERT(decimal(14,2), ABS(mImporte)) ELSE 0 END) Dic ");
                sbDetalle.append("FROM tCompromisoDetalle tCD WITH (NOLOCK) ");
                sbDetalle.append("	INNER JOIN tCompromisoEncabezado tCE WITH (NOLOCK) ON tCD.nFolioCompromiso = tCE.nFolioCompromiso ");
                sbDetalle.append("	INNER JOIN tCatalogoEP tCEP WITH (NOLOCK) ON tCD.EP = tCEP.EP ");
                sbDetalle.append("	INNER JOIN tIntegraFoliosCompromiso integra WITH (NOLOCK) ON integra.nFolioCompromiso = tCD.nFolioCompromiso ");
                sbDetalle.append("WHERE integra.caNoIntegradaComp  = ").append(arrFolios[k]).append(" ");
                sbDetalle.append("	AND SUBSTRING(tCD.EP, 40, 1) <> '4' ");
                sbDetalle.append("GROUP BY tCEP.cRamo, tCEP.aEjercicioFiscal,  ");
                sbDetalle.append("	tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, ");
                sbDetalle.append("	tCEP.cFuncion, tCEP.cSubFuncion,  ");
                sbDetalle.append("	SUBSTRING(dbo.CambiaEPPlurianual(tCD.EP), 20, 2), ");
                sbDetalle.append("	tCEP.cActividadInstitucional, SUBSTRING(dbo.CambiaEPCarteraMeta(tCEP.EP), 45, 11), ");
                sbDetalle.append("	tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, ");
                sbDetalle.append("	tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa;");
                pstmntD = conn.prepareStatement(sbDetalle.toString());
                rs2 = pstmntD.executeQuery();
                k++;
                while (rs2.next()) {
                    StringBuilder filaD = new StringBuilder();
                    for (int j = 1; j <= rs2.getMetaData().getColumnCount(); j++) {
                        filaD.append(rs2.getString(j)).append(",");
                    }
                    arrListaSuf.add(filaD.toString());
                    arrListaSuf.add("\r\n");
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al consultar suficiencia: " + e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntH);
            CloseObject.closeObject(pstmntD);
        }
        return arrListaSuf;
    }

    public static void guardarCalendario(Connection conn, int folio, String ep, int mes, BigDecimal importe, String evento) throws Exception {
        PreparedStatement pst = null;
        String query = "INSERT INTO tPagoCalendario (cTipoPago, nFolioPago, EP, nMes, mImporteBruto, idTipoConcepto ) VALUES ( 'COMPROMISO', ? , ?, ?, ?, ? )";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            pst.setString(2, ep);
            pst.setInt(3, mes);
            pst.setBigDecimal(4, importe);
            pst.setString(5, evento);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void eliminarRegistroCalendario(Connection conn, int folio, String ep, String mes) throws Exception {
        PreparedStatement pst = null;
        String query = "delete tPagoCalendario where ctipoPago = 'COMPROMISO' AND nFolioPago = ? AND ep = ? AND nMes = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            pst.setString(2, ep);
            pst.setString(3, mes);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static PrecompromisoFinanciero insertaPrecompromiso(Connection conn, int ejercicioFiscal, int folio, Usuario u, String ep) throws Exception {
        PrecompromisoFinanciero pc = null;
        ResultSet rsDet = null;
        PrecompromisoFinancieroEncabezado preEnc = new PrecompromisoFinancieroEncabezado();
        preEnc.setCentroContable("10");
        preEnc.setDescripcionPoliza("Precompromiso por tramite de cambio de calendario de contrato");
        preEnc.setEjercicioFiscal(ejercicioFiscal);
        preEnc.setFechaAplicacion(LocalDate.now());
        preEnc.setFechaCarga(LocalDate.now());
        preEnc.setFolioPrecomFinanciero(folio);
        preEnc.setLogin(u.getLogin());
        preEnc.setRadicado(false);
        preEnc.setRamo("16");
        preEnc.setTipoPoliza("PR");
        preEnc.setUnidadResponsable(u.getU_UR());
        preEnc.setUnidadResponsableContable("RHQ");
        rsDet = consultaPrecompromiso(conn, folio);
        // Iniciar contador de renglones
        int nRenglon = 1;
        List<PrecompromisoFinancieroDetalle> preDet = new ArrayList<>();
        String evento = "CMP003";
        while (rsDet.next()) {
            BigDecimal importe = rsDet.getBigDecimal("mImporteBruto");
            //Calcular Evento
            if (importe.compareTo(BigDecimal.ZERO) > 0) {
                evento = "CMP005";
            } else {
                evento = "CMP003";
            }
            PrecompromisoFinancieroDetalle renglon = new PrecompromisoFinancieroDetalle();
            renglon.setFolioPrecomFinanciero(folio);
            renglon.setCentroContable("10");
            renglon.setcEvento(evento);
            renglon.setEP(rsDet.getString("EP"));
            renglon.setImporte(importe);
            renglon.setImporteNegativo(importe.multiply(new BigDecimal(-1)));
            renglon.setMes(rsDet.getInt("nMes"));
            renglon.setRenglon(nRenglon);
            renglon.setUnidadResponsable(preEnc.getUnidadResponsable());
            nRenglon++;
            preDet.add(renglon);
        }
        if (preDet.size() > 0) {
            pc = new PrecompromisoFinanciero();
            pc.setEncabezado(preEnc);
            pc.setDetalle(preDet);
            CompromisoManager.insertaPrecomFinanciero(conn, pc);
            log.info("Precompromiso " + pc.getEncabezado().getFolioPrecomFinanciero() + " insertado.");
        }
        return pc;
    }

    private static ResultSet consultaPrecompromiso(Connection conn, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT  EP, nMes, mImporteBruto, idTipoConcepto FROM tPagoCalendario WITH (NOLOCK) WHERE cTipoPago = 'COMPROMISO' AND nFolioPago = ?";
        pst = conn.prepareStatement(query);
        pst.setInt(1, folio);
        rs = pst.executeQuery();
        return rs;
    }

    public static Compromiso insertaCompromiso(Connection conn, String cxp, String ef, Usuario u, PrecompromisoFinanciero pc, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String contrato = "";
        try {
            pst = conn.prepareStatement("SELECT cidContrato FROM tCompromisoCalendarioEncabezado where nfolioCompromiso  = ? ");
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                contrato = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        CompromisoEncabezado cEnc = new CompromisoEncabezado();
        cEnc.setaEjercicioFiscal(Integer.valueOf(ef));
        cEnc.setCaNoCompromiso(cxp);
        cEnc.setcCentroContable("10");
        cEnc.setcDescripcionPoliza("Compromiso por cambio de calendario del pedido / contrato " + contrato);
        cEnc.setcIdContrato(contrato);
        cEnc.setcRadicado("N");
        cEnc.setcTipoContrato("DI");
        cEnc.setcTipoPoliza("CO");
        cEnc.setcUnidadResponsable(pc.getEncabezado().getUnidadResponsable());
        cEnc.setcUnidadResponsableContable("RHQ");
        cEnc.setfAplicacion(new Date());
        cEnc.setfCarga(new Date());
        cEnc.setnEnviadoSICOP(0);
        cEnc.setnFolioCompromiso(pc.getEncabezado().getFolioPrecomFinanciero());
        cEnc.setnMes(Util.getCurrentMonth(conn));
        cEnc.setUsuario(u.getLogin());
        cEnc.setEsCalendario("S");
        List<CompromisoDetalle> cDet = new ArrayList<>();
        String evento = "CMP004";
        for (PrecompromisoFinancieroDetalle pDet : pc.getDetalle()) {
            BigDecimal importe = pDet.getImporte();
            //Calcular Evento
            if (importe.compareTo(BigDecimal.ZERO) > 0) {
                evento = "CMP002";
            } else {
                evento = "CMP004";
            }
            CompromisoDetalle renglon = new CompromisoDetalle();
            renglon.setcEvento(evento);
            renglon.setcCentroContable("10");
            renglon.setcMes(pDet.getMes());
            renglon.setEP(pDet.getEP());
            renglon.setmImporte(pDet.getImporte());
            renglon.setmImporteNegativo(pDet.getImporteNegativo());
            renglon.setnDocRenglon(pDet.getRenglon());
            renglon.setnFolioCompromiso(cEnc.getnFolioCompromiso());
            cDet.add(renglon);
        }
        Compromiso compromiso = new Compromiso();
        compromiso.setEncabezado(cEnc);
        compromiso.setDetalle(cDet);
        CompromisoManager.insertaCompromiso(conn, compromiso);
        return compromiso;
    }

    public static boolean validarClavesCalendario(Connection conn, int folio) throws Exception {
        boolean correcto = true;
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal("0.00");
        String query = "SELECT SUBSTRING(ep,23,21) ep, SUM(mImporteBruto) importe FROM tPagoCalendario WITH (NOLOCK) WHERE nFolioPago = ?  and cTipoPago= 'COMPROMISO' GROUP BY SUBSTRING(ep,23,21)";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            while (rs.next()) {
                importe = rs.getBigDecimal(2);
                if (importe.compareTo(BigDecimal.ZERO) != 0) {
                    correcto = false;
                    throw new Exception("La clave corta " + rs.getString(1) + " no esta compensada.");
                }
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return correcto;
    }

    public static boolean validarCalendarioInterno(Connection conn, int folio) throws Exception {
        boolean interno = true;
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal("0.00");
        String query = "select sumaImporte from vCompromisoCalendarioValidar where nfoliopago = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            while (rs.next()) {
                importe = rs.getBigDecimal(1);
                if (importe.compareTo(BigDecimal.ZERO) != 0) {
                    interno = false;
                    return interno;
                }
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return interno;
    }

    public static void aplicaCompromisosIntegrados(Connection conn) throws SQLException, DocumentAppliedException, AccountingEngineException {
        PreparedStatement pstmntL = null;
        ResultSet rs = null;
        List<Integer> folios = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT COMPROMISOS.nfoliocompromiso ");
        query.append("FROM   clc_analisis_compromisos CLC WITH (nolock) ");
        query.append("       INNER JOIN tintegrafolioscompromiso COMPROMISOS WITH (nolock) ");
        query.append("               ON CLC.control_interno_ramo = COMPROMISOS.canointegradacomp ");
        query.append("       INNER JOIN tcompromisoencabezado ce ");
        query.append("               ON COMPROMISOS.nfoliocompromiso = ce.nfoliocompromiso ");
        query.append("WHERE  ce.cdocumentohaplicado IS NULL   ");
        try {
            AccountingEngine ae = new AccountingEngine(GestionInterface.ATT_CONEXION);
            ae.setValidaInsuficienciaDeSaldo(true);
            pstmntL = conn.prepareStatement(query.toString());
            rs = pstmntL.executeQuery();
            while (rs.next()) {
                folios.add(rs.getInt("nFolioCompromiso"));
            }
            for (Integer folioLista : folios) {
                if (!compromisoAplicado(conn, folioLista))
                    ae.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(folioLista), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
            }
        } finally {
            CloseObject.closeObject(pstmntL);
        }
    }

    public static boolean consultaPrecompromisoAplicado(Connection conn, String folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean esAplicado = false;
        try {
            pst = conn.prepareStatement("SELECT ISNULL(cDocumentoHaplicado, 'N') FROM tPrecomFinancieroEncabezado where nFolioPrecomFinanciero = ?");
            pst.setString(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getString(1).equals("S")) {
                    esAplicado = true;
                }
            }
        } finally {
            CloseObject.closeObject(pst);
        }
        return esAplicado;
    }

    public static void actualizarStatusCompromiso(Connection conn, String folio) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("UPDATE tCompromisoEncabezado SET cDocumentoHaplicado = 'N' where nFolioCompromiso = ?");
            pst.setString(1, folio);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void actualizarStatusPrecomp(Connection conn, String folio) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("UPDATE tPrecomFinancieroEncabezado SET cDocumentoHaplicado = 'N' where nFolioPrecomFinanciero = ?");
            pst.setString(1, folio);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static boolean existeSuficiencia(Connection conn, String folio) throws Exception {
        boolean existe = false;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) from tSuficienciaReduccion WITH (NOLOCK) WHERE nFolioCompromiso = ?");
            pst.setString(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                int existeSuficiencia = rs.getInt(1);
                if (existeSuficiencia > 0)
                    existe = true;
            }
        } finally {
            CloseObject.closeObject(pst);
        }
        return existe;
    }

    public static String cxpSuficiencia(Connection conn, String folio) throws Exception {
        String cxp = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("SELECT 'C' + caNoCompromiso FROM tCompromisoEncabezado WITH (NOLOCK)  WHERE nFolioCompromiso = ?");
            pst.setString(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                cxp = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pst);
        }
        return cxp;
    }

    public static void insertaSuficiencia(Connection conn, String folio, String cxp) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("INSERT INTO tSuficienciaReduccion (nFolioCompromiso, canoSuficiencia) VALUES ( ?,? )");
            pst.setString(1, folio);
            pst.setString(2, cxp);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static String obtenerCxpDirecto(Connection conn, String[] folios) throws Exception {
        if (folios == null || folios.length == 0) {
            return "";
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder cxps = new StringBuilder();
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < folios.length; i++) {
            in.append("?");
            if (i < folios.length - 1) {
                in.append(",");
            }
        }
        String sql = "SELECT caNoContrarrecibo " + "FROM tPagoDirectoEncabezado WITH (NOLOCK) " + "WHERE nFolioPagoDirecto IN (" + in.toString() + ")";
        try {
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < folios.length; i++) {
                pst.setInt(i + 1, Integer.parseInt(folios[i]));
            }
            rs = pst.executeQuery();
            while (rs.next()) {
                if (cxps.length() > 0) {
                    cxps.append(",");
                }
                cxps.append(rs.getString("caNoContrarrecibo"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
        return cxps.toString();
    }
}
