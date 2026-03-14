package com.syc.ejercido.pagado;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import com.axtel.web.clients.InvoiceDTO;
import com.axtel.web.clients.TimbrarCFDIViaticos;
import com.axtel.ws.clients.NotificaCFDIClientes;
import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.OperacionAjenaManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.ObraPublicaBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.polizamanual.model.GeneradorPolizaManualBusinessLogic;
import com.syc.sai.ejercido.pagado.EjercidoPagadoBusinessLogic;
import com.syc.sai.ingresos.core.ConsolidacionRGManager;
import com.syc.utils.mail.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EjercidoPagadoValidarVarios extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    static String strStatusSiaff = "";

    private String jniName = "";

    private String reportPath = "";

    public EjercidoPagadoValidarVarios(String jniName) {
        if (StringUtils.isEmpty(jniName))
            this.jniName = GestionInterface.ATT_CONEXION;
        else
            this.jniName = jniName;
        super.init(this.jniName);
    }

    public JSONObject EjercidoPagadoVarios(String NotCuentaPorPagar) throws SQLException {
        try {
            ConfiguraAplicativoBusinessLogic configSys = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean activaPagoParcial = "S".equals(configSys.getSystemSetting("PAGO_PARCIAL_AUTOMATICO"));
            String cUE = "";
            String strCaNoContrarrecibo = "";
            String cTipoPago = "";
            String aplicacionContable = "";
            StringBuilder camposDet = new StringBuilder();
            String NotCXP = "";
            String epDetSICOP = null;
            double numeMenor;
            double numeMayor;
            double numeMenorDet;
            double numeMayorDet;
            double numeMenorFor;
            double numeMayorFor;
            double numeMenorForDet;
            double numeMayorForDet;
            double centavos = 0.00;
            double importeEnc = 0.00;
            double importeDet = 0.00;
            // Campos Para Guardar tEjercidoEncabezado y tPagadoEncabezado
            String fAplicacion = "";
            String tipoPoliza = "";
            String desPoliza = "";
            String uniResp = "";
            String cRamo = "";
            String fCancelacion = null;
            String usuario = "administrador";
            String clcSicop = "";
            int nFolioPago = 0;
            int nFolPolCancelacion = 0;
            int intaEjercicioFiscal = 0;
            int folioPoliza = 0;
            // Variable Status
            String guardarDatos = "guardarDatos";
            String statusEjercido = "";
            String statusPagado = "";
            String integracion = "0";
            String msg = null;
            String strFolioEjercido = "";
            String strFolioPagado = "";
            // Se toma folio Exite En tPagadoEncabezado
            String folioPagado = "";
            String msnEnc = "";
            String FechaPagado = "";
            // Aplico Correctamente Ejercido o Pagado
            String tipoAplicacion = "";
            /**
             * Valores Para El Motor Contable **
             */
            boolean appCont = false, appContPag = false;
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            // Datos Para Aplicar Ejercido
            String cTablaPadre = "tEjercidoEncabezado";
            String cTablaHija = "tEjercidoDetalle";
            String cFolio = "nFolioEjercido";
            String cTipoDocumento = "EJERCIDO";
            // Datos Para Aplicar Pagado
            String cTablaPadrePag = "tPagadoEncabezado";
            String cTablaHijaPag = "tPagadoDetalle";
            String cFolioPag = "nFolioPagado";
            String cTipoDocumentoPag = "PAGADO";
            PreparedStatement pstmEncCXP = null, pstmntDetCXP = null, pstmEncSICOP = null, pstmDetSICOP = null, pstmInsertEncEje = null, pstmInsertDetEje = null, pstmInsertEncPag = null, pstmInsertDetPag = null, pstmCent = null, pstmSicop = null, pstmSequenceEjercido = null, pstmUpEjercido = null, pstmSeqEjercido = null, pstmSeqPagado = null, pstmUpPagado = null, pstmDiferencia = null, pstmSicopPag = null, pstmnUpdate = null, pstmEncCXPInt = null, pstmEncCXPIntcaNo = null, pstmFolInt = null, pstmEncCXPIntcaNoEnc = null, pstmDelete = null, pstmExisteSICOP = null, pstmIntegracionApl = null, pstmEncCXPIntD = null, pst = null;
            ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null, rs9 = null, rs10 = null, rs11 = null, rs12 = null, rs13 = null, rs14 = null, rs15 = null, rs16 = null, rs17 = null, rs18 = null, rs19 = null;
            Connection conn = null;
            JSONObject json = new JSONObject();
            try {
                conn = getConnection();
                NotificaCFDIClientes notificator = new NotificaCFDIClientes(ConfiguraAplicativoManager.getSystemSetting(conn, "WS_CFDI_VIATICOS"));
                pstmDelete = conn.prepareStatement("delete from tDetalleEjercidoPagado");
                pstmDelete.executeUpdate();
                json.put("status", "guardado");
                // JDS Se insertan en clc_sicop_pago los registros de Tienda digital
                insertaTiendaDigital(conn);
                java.util.Date utilDate = new java.util.Date();
                long lnMilisegundos = utilDate.getTime();
                java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                String[] fp = String.valueOf(sqlDate).split("-");
                FechaPagado = fp[0] + "-" + fp[1] + "-" + fp[2];
                /**
                 * Se Selecciona centavo **
                 */
                pstmCent = conn.prepareStatement("SELECT TOP 1 diferenciaCentavos FROM tEjercidoPagadoCentavo WITH(NOLOCK) WHERE Activo = 1");
                rs = pstmCent.executeQuery();
                if (rs.next()) {
                    centavos = rs.getDouble("diferenciaCentavos");
                }
                /**
                 * Encabezado de CXP **
                 */
                if (!NotCuentaPorPagar.equals("")) {
                    NotCXP = " AND caNoContrarrecibo NOT IN " + NotCuentaPorPagar;
                }
                StringBuilder queryCXP = new StringBuilder();
                queryCXP.append(" SELECT cTipoPago, nFolio, impNeto, RTRIM(LTRIM(caNoContrarrecibo)) caNoContrarrecibo, nFolioPoliza, cTipoPoliza, ");
                queryCXP.append(" U_LOGIN, cDescripcionPoliza, cUnidadResponsableContable,fAplicacion, cRamo, nFolioPolizaCancelacion, aEjercicioFiscal, ");
                queryCXP.append(" ejercido, pagado, folioPagado ");
                queryCXP.append(" FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) ");
                queryCXP.append(" WHERE pagado != 'pagado' AND integracion = '0' ");
                queryCXP.append(NotCXP + " ORDER BY cTipoPago, caNoContrarrecibo ");
                pstmEncCXP = conn.prepareStatement(queryCXP.toString());
                log.info("Object: {}", queryCXP.toString());
                rs1 = pstmEncCXP.executeQuery();
                /*
				 * INICIO DE LA APLICACION DE PAGOS TERSOFE A PROVEEDOR(NOMINA,
				 * FEDERALIZADO, DIVERSO, OBRA, DIRECTO)
				 */
                while (rs1.next()) {
                    guardarDatos = "guardarDatos";
                    // Tipo Documento
                    cTipoPago = rs1.getString("cTipoPago");
                    nFolioPago = Integer.parseInt(rs1.getString("nFolio"), 10);
                    strCaNoContrarrecibo = rs1.getString("caNoContrarrecibo");
                    tipoPoliza = rs1.getString("cTipoPoliza");
                    desPoliza = rs1.getString("cDescripcionPoliza");
                    uniResp = rs1.getString("cUnidadResponsableContable");
                    cRamo = rs1.getString("cRamo");
                    nFolPolCancelacion = Integer.parseInt(rs1.getString("nFolioPolizaCancelacion"), 10);
                    intaEjercicioFiscal = Integer.parseInt(rs1.getString("aEjercicioFiscal"), 10);
                    statusEjercido = rs1.getString("ejercido");
                    statusPagado = rs1.getString("pagado");
                    folioPagado = rs1.getString("folioPagado");
                    /**
                     *  Solo Aplicar Pagado si Existe en tPagadoEncabezado sin
                     *  Aplicar
                     * *
                     */
                    /**
                     * o hacer todo el procedimiento de Guardar y Aplicar **
                     */
                    log.info("Object: {}", "[EJERCIDO/PAGADO] Tipo de Pago[" + cTipoPago + "] Folio de Pago[" + nFolioPago + "] Contrarecibo[" + strCaNoContrarrecibo + "] Tipo de Poliza[" + tipoPoliza + "] Estatus Ejercido[" + statusEjercido + "] Estatus Pagado[" + statusPagado + "]");
                    if (statusEjercido.equals("ejercido") && statusPagado.equals("existeSinPagar")) {
                        pstmSicopPag = conn.prepareStatement(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, isnull(SIAFF.FECHA_PAGO,'-') as FECHA_PAGO, SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC ");
                        log.info("Object: {}", " SELECT SICOP.NCTR_47, SICOP.NCLC_43, isnull(SIAFF.FECHA_PAGO,'-') as FECHA_PAGO, SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC");
                        log.info("Object: {}", "[" + strCaNoContrarrecibo + "]");
                        pstmSicopPag.setString(1, strCaNoContrarrecibo);
                        rs10 = pstmSicopPag.executeQuery();
                        if (rs10.next()) {
                            String fPago = rs10.getString("FECHA_PAGO");
                            String strStatusSiaff = rs10.getString("ESTATUS_CLC");
                            clcSicop = rs10.getString("NCLC_43");
                            log.info("Object: {}", String.format("fPago %s strStatusSiaff %s clcSicop %s", fPago, strStatusSiaff, clcSicop));
                            log.info("Object: {}", "strStatusSiaff: " + strStatusSiaff);
                            if (fPago.length() > 1 && strStatusSiaff.equals("Pagada")) {
                                try {
                                    String[] fechPagado = fPago.split("/");
                                    String fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
                                    pstmnUpdate = conn.prepareStatement("UPDATE tPagadoEncabezado SET fAplicacion = " + fPagado + " WHERE nFolioPagado = ? ");
                                    pstmnUpdate.setString(1, folioPagado);
                                    pstmnUpdate.executeUpdate();
                                    accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                    if (cTipoPago.equals("PAGOOBRA")) {
                                        ObraPublicaBusinessLogic.enviaEstimacion(conn, strCaNoContrarrecibo);
                                    }
                                    conn.commit();
                                    if (activaPagoParcial) {
                                        cUE = obtieneUR(conn, folioPagado);
                                        GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                        gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                    }
                                } catch (Exception e) {
                                    log.error("Error aplicando contablemente " + e, e);
                                    msg = e.toString();
                                    try {
                                        json.put("status", strCaNoContrarrecibo + " - " + msg);
                                        conn.rollback();
                                    } catch (Exception ee) {
                                        log.warn("Error: cerrando rollback ", ee);
                                    }
                                    try {
                                        guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, tipoAplicacion, msg);
                                        conn.commit();
                                    } catch (Exception de) {
                                        log.warn("Error: guardar Detalle ", de);
                                        conn.rollback();
                                    }
                                }
                            }
                        }
                    } else {
                        folioPoliza = 0;
                        importeEnc = Double.parseDouble(rs1.getString("impNeto"));
                        numeMenor = Double.parseDouble(rs1.getString("impNeto")) - centavos;
                        numeMayor = Double.parseDouble(rs1.getString("impNeto")) + centavos;
                        NumberFormat formatter = new DecimalFormat("###.##");
                        numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
                        numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
                        /**
                         * Verifica si existe en SICOP **
                         */
                        String sqlExisteSICOP = "SELECT * FROM CLC_SICOP where NCTR_47 = '" + strCaNoContrarrecibo + "' ";
                        pstmExisteSICOP = conn.prepareStatement(sqlExisteSICOP);
                        rs16 = pstmExisteSICOP.executeQuery();
                        if (rs16.next()) {
                            /**
                             *  Encabezado de SICOP dependiendo de los valores de
                             *  encabezado CXP para validar si son iguales
                             * *
                             */
                            /**
                             *  APLICACION_CONTABLE = '1' Para Poder Ejercer
                             * *
                             */
                            log.debug("Object: {}", "Contrarecibo " + StringUtils.trim(strCaNoContrarrecibo));
                            String sqlCLC = "SELECT NCTR_47, NCLC_43, APLICACION_CONTABLE FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) WHERE SICOP.FOLIO_SIAFF_112 = SIAFF.FOLIO_CLC AND NCTR_47 = '" + strCaNoContrarrecibo + "' AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) WHERE NCTR_47 = '" + strCaNoContrarrecibo + "' AND FOLIO_SIAFF_112 <> '0' AND DOC_HAPLICADO=1) BETWEEN  " + String.format("%.2f", numeMenorFor) + " AND " + String.format("%.2f", numeMayorFor) + " GROUP BY NCTR_47, NCLC_43, APLICACION_CONTABLE";
                            pstmEncSICOP = conn.prepareStatement(sqlCLC);
                            rs2 = pstmEncSICOP.executeQuery();
                            // System.out.println("sql encabezado: "+sqlCLC);
                            /**
                             * Encabezado iguales **
                             */
                            if (rs2.next()) {
                                clcSicop = rs2.getString("NCLC_43");
                                aplicacionContable = rs2.getString("APLICACION_CONTABLE");
                                /**
                                 *  Valida si se Puede Ejercer
                                 *  APLICACION_CONTABLE
                                 * *
                                 */
                                if (aplicacionContable.equals("1")) {
                                    /**
                                     * Detalles Dependiendo el Documento **
                                     */
                                    if (cTipoPago.equals("AJENAS")) {
                                        camposDet = new StringBuilder("SELECT  STUFF(SUBSTRING(ep, 1, 44), 20, 2, '00')  + SUBSTRING( dbo.CambiaEPCarteraMeta(EP),45, 11)  AS EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto, cEjercicio ");
                                        camposDet.append("				FROM v_AplicarEjercidoPagadoDetalle WITH(NOLOCK) ");
                                        camposDet.append("				WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND nFolio =? AND cTipoPago = ? ");
                                        camposDet.append("				GROUP BY STUFF(SUBSTRING(ep, 1, 44), 20, 2, '00')  + SUBSTRING( dbo.CambiaEPCarteraMeta(EP),45, 11) , cEjercicio");
                                    } else {
                                        camposDet = new StringBuilder("SELECT  STUFF(SUBSTRING(ep, 1, 44), 20, 2, '00')  + SUBSTRING( dbo.CambiaEPCarteraMeta(EP),45, 11) AS EP ");
                                        camposDet.append("			, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto ");
                                        camposDet.append("			,RFC,cEjercicio, cIdRelacion,nCapitulo	");
                                        camposDet.append("			FROM v_AplicarEjercidoPagadoDetalle WITH(NOLOCK) ");
                                        camposDet.append("			WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND nFolio =? AND cTipoPago = ?  ");
                                        camposDet.append("			GROUP BY STUFF(SUBSTRING(ep, 1, 44), 20, 2, '00')  + SUBSTRING( dbo.CambiaEPCarteraMeta(EP),45, 11) , RFC,cEjercicio, cIdRelacion,nCapitulo");
                                    }
                                    log.trace("Object: {}", "\nEjercido/Pagado Ejecutando: \n[" + camposDet + "]\n[" + nFolioPago + "][" + cTipoPago + "]\n");
                                    pstmntDetCXP = conn.prepareStatement(camposDet.toString());
                                    pstmntDetCXP.setString(1, Integer.toString(nFolioPago));
                                    pstmntDetCXP.setString(2, cTipoPago);
                                    rs3 = pstmntDetCXP.executeQuery();
                                    /**
                                     *  Destalles de Documento Para Comparar Con
                                     *  SICOP
                                     * *
                                     */
                                    while (rs3.next()) {
                                        String epNormalizada = Util.normalizaEP(rs3.getString("EP"));
                                        epDetSICOP = epNormalizada != null ? epNormalizada.substring(0, 55) : "";
                                        if (rs3.getDouble("mImporteNeto") == 0.00d)
                                            continue;
                                        importeDet = rs3.getDouble("mImporteNeto");
                                        numeMenorDet = rs3.getDouble("mImporteNeto") - centavos;
                                        numeMayorDet = rs3.getDouble("mImporteNeto") + centavos;
                                        NumberFormat formatterDet = new DecimalFormat("###.##");
                                        numeMenorForDet = Double.parseDouble(formatterDet.format(numeMenorDet));
                                        numeMayorForDet = Double.parseDouble(formatterDet.format(numeMayorDet));
                                        /**
                                         *  Validacion con un centavo mas o menos
                                         *  en Detalles SICOP
                                         * *
                                         */
                                        StringBuilder sqlSicop = new StringBuilder();
                                        sqlSicop.append("SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+");
                                        sqlSicop.append(" CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+");
                                        sqlSicop.append(" CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+");
                                        sqlSicop.append(" CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+");
                                        sqlSicop.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+");
                                        sqlSicop.append(" CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END ");
                                        sqlSicop.append(" AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 ");
                                        sqlSicop.append(" FROM CLC_SICOP WITH(NOLOCK) ");
                                        sqlSicop.append(" WHERE NCTR_47 = '" + strCaNoContrarrecibo);
                                        sqlSicop.append("' AND FOLIO_SIAFF_112 <> '0' AND ");
                                        sqlSicop.append(" CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND ");
                                        sqlSicop.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND ");
                                        sqlSicop.append(" CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND ");
                                        sqlSicop.append(" CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND ");
                                        sqlSicop.append(" CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND ");
                                        sqlSicop.append(" CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND ");
                                        sqlSicop.append(" CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND ");
                                        sqlSicop.append(" ( SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+");
                                        sqlSicop.append(" CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+");
                                        sqlSicop.append(" CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+");
                                        sqlSicop.append(" CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+");
                                        sqlSicop.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+");
                                        sqlSicop.append(" CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END AS EP");
                                        sqlSicop.append(" FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + strCaNoContrarrecibo);
                                        sqlSicop.append("' AND FOLIO_SIAFF_112 <> '0' AND ");
                                        sqlSicop.append(" CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND ");
                                        sqlSicop.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND ");
                                        sqlSicop.append(" CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND ");
                                        sqlSicop.append(" CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND ");
                                        sqlSicop.append(" CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND ");
                                        sqlSicop.append(" CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND ");
                                        sqlSicop.append(" CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) ");
                                        sqlSicop.append(" GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166");
                                        sqlSicop.append(" HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet));
                                        sqlSicop.append(" AND " + String.format("%.2f", numeMayorForDet) + ") = '" + epDetSICOP + "'");
                                        sqlSicop.append(" GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166");
                                        sqlSicop.append(" HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet));
                                        sqlSicop.append(" AND " + String.format("%.2f", numeMayorForDet));
                                        System.out.println("sqlDet:" + sqlSicop.toString());
                                        pstmDetSICOP = conn.prepareStatement(sqlSicop.toString());
                                        rs4 = pstmDetSICOP.executeQuery();
                                        /**
                                         * Detalle Son Diferentes **
                                         */
                                        if (!rs4.next()) {
                                            /**
                                             * Guardar Detalles Error **
                                             */
                                            guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeDet, "EJERCIDO", "Detalle Diferente");
                                            guardarDatos = "noGuardarDetalle";
                                            json.put("status", "errorDetalles");
                                            conn.commit();
                                        }
                                    }
                                    /**
                                     *  Guardar Informacion Ejercido - Pagado
                                     * *
                                     */
                                    String valor = "";
                                    if (guardarDatos.equals("guardarDatos")) {
                                        /**
                                         * Seleccionar SEQUENCE Ejercido **
                                         */
                                        pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                                        pstmUpEjercido.executeUpdate();
                                        pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                                        rs7 = pstmSeqEjercido.executeQuery();
                                        if (rs7.next()) {
                                            strFolioEjercido = rs7.getString("seq_value");
                                        }
                                        /**
                                         * Seleccionar SEQUENCE Pagado **
                                         */
                                        if (statusPagado.equals("noPagado")) {
                                            pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                                            pstmUpPagado.executeUpdate();
                                            pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                                            rs9 = pstmSeqEjercido.executeQuery();
                                            if (rs9.next()) {
                                                strFolioPagado = rs9.getString("seq_value");
                                            }
                                        }
                                        /**
                                         *  Guardar Informacion Ejercido - Pagado
                                         * *
                                         */
                                        // ARLA SI ESTA EJERCIDO YA NO INSERTAR
                                        valor = guardarEjercidoPagado(conn, pstmSicop, pstmSequenceEjercido, pstmInsertEncEje, pstmInsertDetEje, pstmUpEjercido, pstmSeqEjercido, pstmSeqPagado, pstmUpPagado, pstmInsertEncPag, pstmInsertDetPag, strCaNoContrarrecibo, rs5, rs7, rs8, rs9, cTipoPago, nFolioPago, folioPoliza, tipoPoliza, usuario, nFolPolCancelacion, fCancelacion, desPoliza, uniResp, fAplicacion, cRamo, intaEjercicioFiscal, statusPagado, strFolioEjercido, strFolioPagado, FechaPagado, integracion);
                                        /**
                                         * Aplicacion Contable **
                                         */
                                        if (valor.equals("guardado")) {
                                            if (!statusEjercido.equals("ejercido")) {
                                                appCont = accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                                                conn.commit();
                                                tipoAplicacion = "EJERCIDO";
                                            }
                                            if ((appCont && strStatusSiaff.trim().equals("Pagada")) || (!appCont && strStatusSiaff.trim().equals("Pagada") && statusEjercido.equals("ejercido"))) {
                                                tipoAplicacion = "PAGADO";
                                                accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                                if (cTipoPago.equals("PAGOOBRA")) {
                                                    ObraPublicaBusinessLogic.enviaEstimacion(conn, strCaNoContrarrecibo);
                                                }
                                                conn.commit();
                                                int tienePPD = existeFacturaPPD(conn, strCaNoContrarrecibo);
                                                if (tienePPD > 0)
                                                    enviarCorreo(conn, strCaNoContrarrecibo);
                                                if (activaPagoParcial) {
                                                    cUE = obtieneUR(conn, strFolioPagado);
                                                    GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                    gpmbl.generaPolizaCancelaPago(strFolioPagado, cUE);
                                                }
                                            }
                                        } else if (strStatusSiaff.trim().equals("Pagada") && statusPagado.equals("existeSinPagar")) {
                                            tipoAplicacion = "PAGADO";
                                            accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                            if (cTipoPago.equals("PAGOOBRA")) {
                                                ObraPublicaBusinessLogic.enviaEstimacion(conn, strCaNoContrarrecibo);
                                            }
                                            conn.commit();
                                            // guardarDetalleDiferencia
                                            if (activaPagoParcial) {
                                                cUE = obtieneUR(conn, folioPagado);
                                                GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                            }
                                        }
                                    }
                                    if (valor.equals("guardado")) {
                                        conn.commit();
                                    } else {
                                        conn.rollback();
                                    }
                                } else {
                                    epDetSICOP = "";
                                    guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", "No Esta Para Ejercer");
                                    guardarDatos = "noGuardarDetalle";
                                    json.put("status", "errorDetalles");
                                    conn.commit();
                                }
                            } else {
                                epDetSICOP = "";
                                clcSicop = "";
                                msnEnc = "Informacion No Encontrada en SICOP o Importes Diferente de Encabezado";
                                guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", msnEnc);
                                json.put("status", "errorDetalles");
                                conn.commit();
                            }
                        } else {
                            epDetSICOP = "";
                            clcSicop = "";
                            msnEnc = "Informacion No Encontrada en SICOP ";
                            guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", msnEnc);
                            json.put("status", "errorDetalles");
                            conn.commit();
                        }
                    }
                }
                /**
                 * Aplicar Relacion de Gastos Integracion **
                 */
                StringBuilder queryRg = new StringBuilder();
                queryRg.append(" SELECT cTipoPago, SUM(impNeto) as impNeto, ejercido, pagado, integracion, folioPagado ");
                queryRg.append(" FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) ");
                queryRg.append(" WHERE pagado != 'pagado' AND caNoContrarrecibo != '' ");
                queryRg.append(" AND integracion != '0' AND cTipoPago='RELACIONGASTOS' AND laudos <> '1'" + NotCXP);
                queryRg.append(" Group By cTipoPago, ejercido, pagado, integracion, folioPagado ");
                queryRg.append(" ORDER BY integracion ");
                pstmEncCXPInt = conn.prepareStatement(queryRg.toString());
                rs11 = pstmEncCXPInt.executeQuery();
                while (rs11.next()) {
                    String fPago = "";
                    guardarDatos = "guardarDatos";
                    cTipoPago = rs11.getString("cTipoPago");
                    statusEjercido = rs11.getString("ejercido");
                    statusPagado = rs11.getString("pagado");
                    folioPagado = rs11.getString("folioPagado");
                    integracion = rs11.getString("integracion");
                    log.info("Object: {}", "[Ejercido/Pagado][Integracion] Integracion[" + integracion + "] folio Pagado[" + folioPagado + "] Estatus Pagado[" + statusPagado + "] Estatus Ejercido[" + statusEjercido + "] Tipo Pago[" + cTipoPago + "]");
                    if (statusEjercido.equals("ejercido") && statusPagado.equals("existeSinPagar")) {
                        /**
                         *  Valores SICOP SIAFF Para Guardar en tPagadoEncabezado
                         * *
                         */
                        pstmSicopPag = conn.prepareStatement(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, isnull(SIAFF.FECHA_PAGO,'-') as FECHA_PAGO, SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC ");
                        pstmSicopPag.setString(1, integracion);
                        rs12 = pstmSicopPag.executeQuery();
                        if (rs12.next()) {
                            fPago = rs12.getString("FECHA_PAGO");
                            strStatusSiaff = rs12.getString("ESTATUS_CLC");
                            clcSicop = rs12.getString("NCLC_43");
                        }
                        if (fPago.length() > 1 && strStatusSiaff.equals("Pagada")) {
                            /**
                             *  Informacion Para Tomar caNoContrarrecibo y nFolio
                             * *
                             */
                            StringBuilder query = new StringBuilder();
                            query.append("SELECT cTipoPago, nFolio, impNeto, caNoContrarrecibo, nFolioPoliza, cTipoPoliza, ");
                            query.append(" U_LOGIN, cDescripcionPoliza, cUnidadResponsableContable,fAplicacion, cRamo, nFolioPolizaCancelacion, aEjercicioFiscal, ");
                            query.append(" ejercido, pagado, folioPagado, integracion ");
                            query.append(" FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) ");
                            query.append(" WHERE pagado != 'pagado' AND caNoContrarrecibo != '' AND integracion = ? " + NotCXP);
                            pstmEncCXPIntcaNo = conn.prepareStatement(query.toString());
                            pstmEncCXPIntcaNo.setString(1, integracion);
                            rs13 = pstmEncCXPIntcaNo.executeQuery();
                            /**
                             *  Folios Para Actualizar fAplicacion
                             *  tPagadoEncabezado y Aplicar
                             * *
                             */
                            folioPagado = "";
                            while (rs13.next()) {
                                folioPagado = rs13.getString("folioPagado");
                                nFolioPago = rs13.getInt("nFolio");
                                strCaNoContrarrecibo = rs13.getString("caNoContrarrecibo");
                                try {
                                    /**
                                     *  Aplicación de las cuentas de Ingresos en
                                     *  todas las Integraciones de Relacion de
                                     *  Gastos
                                     * *
                                     */
                                    ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, getReportPath());
                                    String[] fechPagado = fPago.split("/");
                                    String fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
                                    pstmnUpdate = conn.prepareStatement("UPDATE tPagadoEncabezado SET fAplicacion = " + fPagado + " WHERE nFolioPagado = ? ");
                                    pstmnUpdate.setString(1, folioPagado);
                                    pstmnUpdate.executeUpdate();
                                    accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                    conn.commit();
                                    if (activaPagoParcial) {
                                        cUE = obtieneUR(conn, folioPagado);
                                        GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                        gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                    }
                                    /* GENERAR CFDI	 */
                                    if (notificator.esSolicitudViaticos(conn, nFolioPago) > 0) {
                                        InvoiceDTO invoiceDto = NotificaCFDIClientes.readCFDIViaticos(conn, strCaNoContrarrecibo);
                                        if (invoiceDto != null) {
                                            //Guarda en las tablas de respuesta tRespuestaGeneraCFDI
                                            notificator.generaCFDI(conn, invoiceDto);
                                            conn.commit();
                                        } else {
                                            notificator.guardaRespuestaNoEncontrado(conn, strCaNoContrarrecibo, "No se encontro CFDI");
                                        }
                                    }
                                    /* FIN GENERA CFDI */
                                } catch (Exception e) {
                                    log.error("Error aplicando contablemente " + e, e);
                                    msg = e.toString();
                                    try {
                                        json.put("status", strCaNoContrarrecibo + " - " + msg);
                                        conn.rollback();
                                    } catch (Exception ee) {
                                        log.warn("Error: cerrando rollback ", ee);
                                    }
                                    try {
                                        guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, "", 0.00, "PAGADO", msg);
                                        conn.commit();
                                    } catch (Exception de) {
                                        log.warn("Error: guardar Detalle ", de);
                                        conn.rollback();
                                    }
                                }
                            }
                        }
                    } else {
                        pstmFolInt = conn.prepareStatement("SELECT caNoContrarrecibo, nFolio, ejercido, impNeto FROM v_AplicarEjercidoPagadoEncabezado with(nolock) WHERE integracion = ?");
                        pstmFolInt.setString(1, integracion);
                        rs14 = pstmFolInt.executeQuery();
                        String caNoRelacion = "";
                        String folioInte = "";
                        String nFolioPagoInt = "";
                        String sEjercido = "";
                        double impNeto = 0.00;
                        while (rs14.next()) {
                            caNoRelacion += "'" + rs14.getString("caNoContrarrecibo") + "',";
                            folioInte += rs14.getString("nFolio") + ",";
                            sEjercido += rs14.getString("ejercido") + ",";
                            impNeto += Double.parseDouble(rs14.getString("impNeto"));
                        }
                        int numC = caNoRelacion.length() - 1;
                        int numF = folioInte.length() - 1;
                        int numE = sEjercido.length() - 1;
                        caNoRelacion = caNoRelacion.substring(0, numC);
                        nFolioPagoInt = folioInte.substring(0, numF);
                        sEjercido = sEjercido.substring(0, numE);
                        folioPoliza = 0;
                        importeEnc = impNeto;
                        numeMenor = Double.parseDouble(rs11.getString("impNeto")) - centavos;
                        numeMayor = impNeto + centavos;
                        NumberFormat formatter = new DecimalFormat("###.##");
                        numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
                        numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
                        /**
                         *  Encabezado de SICOP dependiendo de los valores de
                         *  encabezado CXP para validar si son iguales
                         * *
                         */
                        /**
                         * APLICACION_CONTABLE = '1' Para Poder Ejercer **
                         */
                        String sqlCLC = "SELECT NCTR_47, NCLC_43, APLICACION_CONTABLE, NCTR_47 FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) WHERE SICOP.FOLIO_SIAFF_112 = SIAFF.FOLIO_CLC AND NCTR_47 = '" + integracion + "' AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) WHERE NCTR_47 = '" + integracion + "' AND FOLIO_SIAFF_112 <> '0' AND DOC_HAPLICADO=1) BETWEEN  " + String.format("%.2f", numeMenorFor) + " AND " + String.format("%.2f", numeMayorFor) + " GROUP BY NCTR_47, NCLC_43, APLICACION_CONTABLE";
                        pstmEncSICOP = conn.prepareStatement(sqlCLC);
                        rs2 = pstmEncSICOP.executeQuery();
                        System.out.println("sql encabezado: " + sqlCLC);
                        /**
                         * Encabezado iguales **
                         */
                        if (rs2.next()) {
                            clcSicop = rs2.getString("NCLC_43");
                            aplicacionContable = rs2.getString("APLICACION_CONTABLE");
                            strCaNoContrarrecibo = rs2.getString("NCTR_47");
                            /**
                             *  Valida si se Puede Ejercer APLICACION_CONTABLE
                             * *
                             */
                            if (aplicacionContable.equals("1")) {
                                /**
                                 * Detalles Dependiendo el Documento **
                                 */
                                camposDet = new StringBuilder("SELECT  STUFF(SUBSTRING(ep, 1, 44), 20, 2, '00')  + SUBSTRING( dbo.CambiaEPCarteraMeta(EP),45, 11) AS EP ");
                                camposDet.append("			, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cEjercicio ");
                                camposDet.append("			FROM v_AplicarEjercidoPagadoDetalle WITH(NOLOCK) 	");
                                camposDet.append("			WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND nFolio in (").append(nFolioPagoInt).append(") AND cTipoPago = ? ");
                                camposDet.append("			GROUP BY STUFF(SUBSTRING(ep, 1, 44), 20, 2, '00')  + SUBSTRING( dbo.CambiaEPCarteraMeta(EP),45, 11) ,cEjercicio");
                                log.trace("Object: {}", "\\nEjercido/Pagado Ejecutando: \n[" + camposDet + "]\n[" + cTipoPago + "]\n");
                                pstmntDetCXP = conn.prepareStatement(camposDet.toString());
                                pstmntDetCXP.setString(1, cTipoPago);
                                rs3 = pstmntDetCXP.executeQuery();
                                /**
                                 *  Detalles de Documento Para Comparar Con SICOP
                                 * *
                                 */
                                while (rs3.next()) {
                                    String epNormalizada = Util.normalizaEP(rs3.getString("EP"));
                                    epDetSICOP = epNormalizada != null ? epNormalizada.substring(0, 55) : "";
                                    importeDet = rs3.getDouble("mImporteNeto");
                                    numeMenorDet = rs3.getDouble("mImporteNeto") - centavos;
                                    numeMayorDet = rs3.getDouble("mImporteNeto") + centavos;
                                    NumberFormat formatterDet = new DecimalFormat("###.##");
                                    numeMenorForDet = Double.parseDouble(formatterDet.format(numeMenorDet));
                                    numeMayorForDet = Double.parseDouble(formatterDet.format(numeMayorDet));
                                    /**
                                     *  Validacion con un centavo mas o menos en
                                     *  Detalles SICOP
                                     * *
                                     */
                                    StringBuilder sql = new StringBuilder();
                                    sql.append("SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+");
                                    sql.append(" CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+");
                                    sql.append(" CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+");
                                    sql.append(" CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+");
                                    sql.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+");
                                    sql.append(" CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END ");
                                    sql.append(" AS EP, SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 ");
                                    sql.append(" FROM CLC_SICOP WITH(NOLOCK) ");
                                    sql.append(" WHERE NCTR_47 = '" + integracion + "' AND ");
                                    sql.append(" CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND ");
                                    sql.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND ");
                                    sql.append(" CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND ");
                                    sql.append(" CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND ");
                                    sql.append(" CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND ");
                                    sql.append(" CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND ");
                                    sql.append(" CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND ");
                                    sql.append(" (SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+");
                                    sql.append(" CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+");
                                    sql.append(" CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+");
                                    sql.append(" CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+");
                                    sql.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166  END AS EP");
                                    sql.append(" FROM CLC_SICOP WITH(NOLOCK) ");
                                    sql.append(" WHERE NCTR_47 = '" + integracion + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND ");
                                    sql.append(" CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND ");
                                    sql.append(" CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND ");
                                    sql.append(" CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND ");
                                    sql.append(" CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND ");
                                    sql.append(" CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) ");
                                    sql.append(" GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166");
                                    sql.append(" HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet) + ") = '" + epDetSICOP + "'");
                                    // "
                                    sql.append(" GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166");
                                    sql.append(" HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet));
                                    System.out.println("Integracion sqlDet:" + sql.toString());
                                    pstmDetSICOP = conn.prepareStatement(sql.toString());
                                    rs4 = pstmDetSICOP.executeQuery();
                                    /**
                                     * Detalle Son Diferentes **
                                     */
                                    if (!rs4.next()) {
                                        /**
                                         * Guardar Detalles Error **
                                         */
                                        guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, 0, integracion, clcSicop, FechaPagado, epDetSICOP, importeDet, "EJERCIDO", "Detalle Diferente Integracion");
                                        guardarDatos = "noGuardarDetalle";
                                        json.put("status", "errorDetalles");
                                        conn.commit();
                                    }
                                }
                                /**
                                 * Guardar Informacion Ejercido - Pagado **
                                 */
                                String valor = "";
                                boolean aplicado = false;
                                if (guardarDatos.equals("guardarDatos")) {
                                    /**
                                     *  Valida que el la Integracion tampoco sea
                                     *  de Laudos Devengados, en caso de si ser
                                     *  de CLRE se omite para hacer su ejercido
                                     *  pagado manualmente
                                     * *
                                     */
                                    /**
                                     *  Valida que el la Integracion tampoco sea
                                     *  de ISN de Queretaro 2NRQ
                                     *  se omite para hacer su ejercido
                                     *  pagado manualmente
                                     * *
                                     */
                                    /**
                                     *  Valida que en la Integracion tampoco contenga
                                     *  de proveedor
                                     *  se omite para hacer su ejercido
                                     *  pagado manualmente
                                     * *
                                     */
                                    if (!ConsolidacionRGManager.esIntegracionLaudosDevengados(conn, integracion) && !ConsolidacionRGManager.esIntegracionISNQ(conn, integracion)) {
                                        /**
                                         *  Guardar Informacion Ejercido - Pagado
                                         *  Relacion de Gastos Integracion
                                         * *
                                         */
                                        // ARLA EXTRAE LOS CXP QUE NO SE
                                        // EXCLUIRAN POR RECHAZO BANCARIO
                                        String[] nCa = caNoRelacion.split(",");
                                        String[] nF = folioInte.split(",");
                                        String caNoSinRechazo = "";
                                        String folioInteSR = "";
                                        if (!"".equals(NotCuentaPorPagar)) {
                                            PreparedStatement pstmSinRechazo = conn.prepareStatement("SELECT caNoContrarrecibo, nFolio FROM v_AplicarEjercidoPagadoEncabezado with(nolock) WHERE integracion = ?" + NotCXP);
                                            pstmSinRechazo.setString(1, integracion);
                                            ResultSet rsSR = pstmSinRechazo.executeQuery();
                                            while (rsSR.next()) {
                                                caNoSinRechazo += "'" + rsSR.getString("caNoContrarrecibo") + "',";
                                                folioInteSR += rsSR.getString("nFolio") + ",";
                                            }
                                            nF = folioInteSR.split(",");
                                            nCa = caNoSinRechazo.split(",");
                                        } else {
                                            nF = folioInte.split(",");
                                            nCa = caNoRelacion.split(",");
                                        }
                                        for (int i = 0; i < nF.length; i++) {
                                            /**
                                             *  Seleccionar SEQUENCE Ejercido
                                             * *
                                             */
                                            pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                                            pstmUpEjercido.executeUpdate();
                                            pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                                            rs7 = pstmSeqEjercido.executeQuery();
                                            if (rs7.next()) {
                                                strFolioEjercido = rs7.getString("seq_value");
                                            }
                                            /**
                                             *  Seleccionar SEQUENCE Pagado
                                             * *
                                             */
                                            String[] nEj = sEjercido.split(",");
                                            if (statusPagado.equals("noPagado")) {
                                                pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                                                pstmUpPagado.executeUpdate();
                                                pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                                                rs9 = pstmSeqEjercido.executeQuery();
                                                if (rs9.next()) {
                                                    strFolioPagado = rs9.getString("seq_value");
                                                }
                                            }
                                            /**
                                             *  Trae Informacion Por Cuenta Por
                                             *  Pagar Relacion de Gastos
                                             *  Encabezado Para Guardar Ejercido
                                             *  y Pagado
                                             * *
                                             */
                                            StringBuilder s = new StringBuilder();
                                            s.append(" SELECT nFolio, impNeto, caNoContrarrecibo, cTipoPoliza, ");
                                            s.append(" cDescripcionPoliza, cUnidadResponsableContable,fAplicacion, cRamo, nFolioPolizaCancelacion, aEjercicioFiscal, ");
                                            s.append(" ejercido, pagado, folioPagado, integracion ");
                                            s.append(" FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) ");
                                            s.append(" WHERE pagado != 'pagado' AND caNoContrarrecibo = " + nCa[i]);
                                            pstmEncCXPIntcaNoEnc = conn.prepareStatement(s.toString());
                                            rs15 = pstmEncCXPIntcaNoEnc.executeQuery();
                                            if (rs15.next()) {
                                                tipoPoliza = rs15.getString("cTipoPoliza");
                                                nFolPolCancelacion = Integer.parseInt(rs15.getString("nFolioPolizaCancelacion"), 10);
                                                tipoPoliza = rs15.getString("cTipoPoliza");
                                                desPoliza = rs15.getString("cDescripcionPoliza");
                                                uniResp = rs15.getString("cUnidadResponsableContable");
                                                cRamo = rs15.getString("cRamo");
                                                intaEjercicioFiscal = Integer.parseInt(rs15.getString("aEjercicioFiscal"), 10);
                                            }
                                            /**
                                             *  Guardar Informacion Ejercido -
                                             *  Pagado
                                             * *
                                             */
                                            nCa[i] = nCa[i].replace("'", "").trim();
                                            valor = guardarEjercidoPagado(conn, pstmSicop, pstmSequenceEjercido, pstmInsertEncEje, pstmInsertDetEje, pstmUpEjercido, pstmSeqEjercido, pstmSeqPagado, pstmUpPagado, pstmInsertEncPag, pstmInsertDetPag, nCa[i], rs5, rs7, rs8, rs9, cTipoPago, Integer.parseInt(nF[i], 10), folioPoliza, tipoPoliza, usuario, nFolPolCancelacion, fCancelacion, desPoliza, uniResp, fAplicacion, cRamo, intaEjercicioFiscal, statusPagado, strFolioEjercido, strFolioPagado, FechaPagado, integracion);
                                            if (nEj[i].equals("noEjercido")) {
                                                /**
                                                 * Aplicacion Contable **
                                                 */
                                                if (valor.equals("guardado")) {
                                                    appCont = accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                                                    conn.commit();
                                                    tipoAplicacion = "EJERCIDO";
                                                    if (appCont && strStatusSiaff.trim().equals("Pagada")) {
                                                        /**
                                                         *  Aplicación de las
                                                         *  cuentas de Ingresos
                                                         *  en todas las
                                                         *  Integraciones de
                                                         *  Relacion de Gastos
                                                         * *
                                                         */
                                                        ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, "");
                                                        tipoAplicacion = "PAGADO";
                                                        accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                                        conn.commit();
                                                        int tienePPD = existeFacturaPPD(conn, strCaNoContrarrecibo);
                                                        if (tienePPD > 0)
                                                            enviarCorreo(conn, strCaNoContrarrecibo);
                                                        if (activaPagoParcial) {
                                                            cUE = obtieneUR(conn, strFolioPagado);
                                                            GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                            gpmbl.generaPolizaCancelaPago(strFolioPagado, cUE);
                                                        }
                                                    }
                                                } else if (strStatusSiaff.trim().equals("Pagada") && statusPagado.equals("existeSinPagar")) {
                                                    /**
                                                     *  Aplicación de las cuentas
                                                     *  de Ingresos en todas las
                                                     *  Integraciones de Relacion
                                                     *  de Gastos
                                                     * *
                                                     */
                                                    ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, "");
                                                    tipoAplicacion = "PAGADO";
                                                    accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                                    conn.commit();
                                                    // guardarDetalleDiferencia
                                                    if (activaPagoParcial) {
                                                        cUE = obtieneUR(conn, folioPagado);
                                                        GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                        gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                                    }
                                                }
                                            } else if (strStatusSiaff.trim().equals("Pagada") && statusPagado.equals("existeSinPagar")) {
                                                /**
                                                 *  Aplicación de las cuentas de
                                                 *  Ingresos en todas las
                                                 *  Integraciones de Relacion de
                                                 *  Gastos
                                                 * *
                                                 */
                                                ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, "");
                                                tipoAplicacion = "PAGADO";
                                                accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                                conn.commit();
                                                // guardarDetalleDiferencia
                                                if (activaPagoParcial) {
                                                    cUE = obtieneUR(conn, folioPagado);
                                                    GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                    gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                                }
                                            }
                                            // FIN VALIDACION SI SE DEBE EXCLUIR
                                        }
                                    } else {
                                        String strStatusSiaffIntegra = "";
                                        pstmSicopPag = conn.prepareStatement(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, isnull(SIAFF.FECHA_PAGO,'-') as FECHA_PAGO, SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC ");
                                        pstmSicopPag.setString(1, integracion);
                                        rs12 = pstmSicopPag.executeQuery();
                                        if (rs12.next()) {
                                            strStatusSiaffIntegra = rs12.getString("ESTATUS_CLC");
                                        }
                                        if (strStatusSiaffIntegra.trim().equals("Pagada")) {
                                            /**
                                             *  Aplicación de las cuentas de
                                             *  Ingresos en todas las
                                             *  Integraciones de Relacion de
                                             *  Gastos
                                             * *
                                             */
                                            aplicado = ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, getReportPath());
                                        }
                                        if (aplicado)
                                            valor = "guardado";
                                    }
                                }
                                if (valor.equals("guardado")) {
                                    conn.commit();
                                } else {
                                    conn.rollback();
                                }
                            } else {
                                pstmIntegracionApl = conn.prepareStatement("SELECT nFolioConsolidacion FROM tconsolidacionrelaciongastosEncabezado WHERE ISNULL(cDocumentoHaplicado,'') NOT IN ('S','C') AND nIdIntegracion = ?");
                                pstmIntegracionApl.setString(1, strCaNoContrarrecibo);
                                rs17 = pstmIntegracionApl.executeQuery();
                                if (rs17.next()) {
                                    nFolioPago = rs17.getInt(1);
                                }
                                guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", "No Esta Para Ejercer");
                                guardarDatos = "noGuardarDetalle";
                                json.put("status", "errorDetalles");
                                conn.commit();
                            }
                        } else {
                            epDetSICOP = "";
                            clcSicop = "";
                            msnEnc = "Informacion No Encontrada en SICOP o Importe Diferente de Encabezado";
                            guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, 0, integracion, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", msnEnc);
                            json.put("status", "errorDetalles");
                            conn.commit();
                        }
                    }
                }
                // COMMIENZA APLICACION DE OPERACIONES AJENAS INTEGRADAS
                /**
                 * Aplicar Operaciones Ajenas Integradas **
                 */
                pstmEncCXPInt = conn.prepareStatement(" SELECT cTipoPago, SUM(impNeto) as impNeto, ejercido, pagado, integracion, folioPagado " + " FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) " + " WHERE pagado != 'pagado' AND caNoContrarrecibo != '' AND integracion != '0' AND cTipoPago='AJENAS' " + NotCXP + " Group By cTipoPago, ejercido, pagado, integracion, folioPagado " + " ORDER BY integracion ");
                rs11 = pstmEncCXPInt.executeQuery();
                while (rs11.next()) {
                    String fPago = "";
                    // String strStatusSiaff = "";
                    guardarDatos = "guardarDatos";
                    cTipoPago = rs11.getString("cTipoPago");
                    statusEjercido = rs11.getString("ejercido");
                    statusPagado = rs11.getString("pagado");
                    folioPagado = rs11.getString("folioPagado");
                    integracion = rs11.getString("integracion");
                    log.info("Object: {}", "[Ejercido/Pagado][Integracion Operaciones Ajenas] Integracion[" + integracion + "] folio Pagado[" + folioPagado + "] Estatus Pagado[" + statusPagado + "] Estatus Ejercido[" + statusEjercido + "] Tipo Pago[" + cTipoPago + "]");
                    if (statusEjercido.equals("ejercido") && statusPagado.equals("existeSinPagar")) {
                        /**
                         *  Valores SICOP SIAFF Para Guardar en tPagadoEncabezado
                         * *
                         */
                        pstmSicopPag = conn.prepareStatement("SELECT SICOP.NCTR_47, " + "	SICOP.NCLC_43, " + "	ISNULL(SIAFF.FECHA_PAGO,'-') as FECHA_PAGO, " + "	SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK)," + " CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC " + "	AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, " + "	SICOP.NCLC_43, " + "	SIAFF.FECHA_PAGO, " + "	SIAFF.ESTATUS_CLC ");
                        pstmSicopPag.setString(1, integracion);
                        rs12 = pstmSicopPag.executeQuery();
                        if (rs12.next()) {
                            fPago = rs12.getString("FECHA_PAGO");
                            strStatusSiaff = rs12.getString("ESTATUS_CLC");
                            clcSicop = rs12.getString("NCLC_43");
                        }
                        if (fPago.length() > 1 && strStatusSiaff.equals("Pagada")) {
                            /**
                             *  Informacion Para Tomar caNoContrarrecibo y nFolio
                             * *
                             */
                            pstmEncCXPIntcaNo = conn.prepareStatement(" SELECT cTipoPago, " + "		nFolio, " + "		impNeto, " + "		caNoContrarrecibo, " + "		nFolioPoliza, " + "		cTipoPoliza, " + " 		U_LOGIN, " + "		cDescripcionPoliza, " + "		cUnidadResponsableContable," + "		fAplicacion, " + "		cRamo, " + "		nFolioPolizaCancelacion, " + "		aEjercicioFiscal, " + " 		ejercido, " + "		pagado, " + "		folioPagado, " + "		integracion " + "	FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) " + " 	WHERE pagado != 'pagado' " + "		AND caNoContrarrecibo != '' " + "		AND integracion = ? " + NotCXP);
                            pstmEncCXPIntcaNo.setString(1, integracion);
                            rs13 = pstmEncCXPIntcaNo.executeQuery();
                            /**
                             *  Folios Para Actualizar fAplicacion
                             *  tPagadoEncabezado y Aplicar
                             * *
                             */
                            folioPagado = "";
                            while (rs13.next()) {
                                folioPagado = rs13.getString("folioPagado");
                                nFolioPago = rs13.getInt("nFolio");
                                strCaNoContrarrecibo = rs13.getString("caNoContrarrecibo");
                                try {
                                    String[] fechPagado = fPago.split("/");
                                    String fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
                                    pstmnUpdate = conn.prepareStatement("UPDATE tPagadoEncabezado SET fAplicacion = " + fPagado + " WHERE nFolioPagado = ? ");
                                    pstmnUpdate.setString(1, folioPagado);
                                    pstmnUpdate.executeUpdate();
                                    accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                    conn.commit();
                                    if (activaPagoParcial) {
                                        cUE = obtieneUR(conn, folioPagado);
                                        GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                        gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                    }
                                } catch (Exception e) {
                                    log.error("Error aplicando contablemente " + e, e);
                                    msg = e.toString();
                                    try {
                                        json.put("status", strCaNoContrarrecibo + " - " + msg);
                                        conn.rollback();
                                    } catch (Exception ee) {
                                        log.warn("Error: cerrando rollback ", ee);
                                    }
                                    try {
                                        guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, "", 0.00, "PAGADO", msg);
                                        conn.commit();
                                    } catch (Exception de) {
                                        log.warn("Error: guardar Detalle ", de);
                                        conn.rollback();
                                    }
                                }
                            }
                        }
                    } else {
                        pstmFolInt = conn.prepareStatement("SELECT caNoContrarrecibo, nFolio, ejercido, impNeto " + "	FROM v_AplicarEjercidoPagadoEncabezado with(nolock) WHERE integracion = ?");
                        pstmFolInt.setString(1, integracion);
                        rs14 = pstmFolInt.executeQuery();
                        String caNoRelacion = "";
                        String folioInte = "";
                        String nFolioPagoInt = "";
                        String sEjercido = "";
                        double impNeto = 0.00;
                        while (rs14.next()) {
                            caNoRelacion += "'" + rs14.getString("caNoContrarrecibo") + "',";
                            folioInte += rs14.getString("nFolio") + ",";
                            sEjercido += rs14.getString("ejercido") + ",";
                            impNeto += Double.parseDouble(rs14.getString("impNeto"));
                        }
                        int numC = caNoRelacion.length() - 1;
                        int numF = folioInte.length() - 1;
                        int numE = sEjercido.length() - 1;
                        caNoRelacion = caNoRelacion.substring(0, numC);
                        nFolioPagoInt = folioInte.substring(0, numF);
                        sEjercido = sEjercido.substring(0, numE);
                        folioPoliza = 0;
                        importeEnc = impNeto;
                        numeMenor = Double.parseDouble(rs11.getString("impNeto")) - centavos;
                        numeMayor = impNeto + centavos;
                        NumberFormat formatter = new DecimalFormat("###.##");
                        numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
                        numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
                        /**
                         *  Encabezado de SICOP dependiendo de los valores de
                         *  encabezado CXP para validar si son iguales
                         * *
                         */
                        /**
                         * APLICACION_CONTABLE = '1' Para Poder Ejercer **
                         */
                        String sqlCLC = "SELECT NCTR_47, NCLC_43, APLICACION_CONTABLE " + "	FROM CLC_SICOP SICOP WITH(NOLOCK), " + "		CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + "	WHERE SICOP.FOLIO_SIAFF_112 = SIAFF.FOLIO_CLC " + "		AND NCTR_47 = '" + integracion + "' " + " 		AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) " + "				WHERE NCTR_47 = '" + integracion + "' " + "				AND FOLIO_SIAFF_112 <> '0' " + "				AND DOC_HAPLICADO=1) BETWEEN  " + String.format("%.2f", numeMenorFor) + " AND " + String.format("%.2f", numeMayorFor) + " " + "	GROUP BY NCTR_47, NCLC_43, APLICACION_CONTABLE";
                        pstmEncSICOP = conn.prepareStatement(sqlCLC);
                        rs2 = pstmEncSICOP.executeQuery();
                        System.out.println("sql encabezado: " + sqlCLC);
                        /**
                         * Encabezado iguales **
                         */
                        if (rs2.next()) {
                            clcSicop = rs2.getString("NCLC_43");
                            aplicacionContable = rs2.getString("APLICACION_CONTABLE");
                            /**
                             *  Valida si se Puede Ejercer APLICACION_CONTABLE
                             * *
                             */
                            if (aplicacionContable.equals("1")) {
                                /**
                                 * Detalles Dependiendo el Documento **
                                 */
                                camposDet = new StringBuilder("SELECT SUBSTRING( dbo.CambiaEPPlurianual(EP), 0,56) AS EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cEjercicio FROM v_AplicarEjercidoPagadoDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND nFolio IN (" + nFolioPagoInt + ") AND cTipoPago = ? GROUP BY SUBSTRING( dbo.CambiaEPPlurianual(EP), 0,56), cEjercicio, cIdCuentaContable ");
                                log.trace("Object: {}", "\\nEjercido/Pagado Ejecutando: \n[" + camposDet + "]\n[" + cTipoPago + "]\n");
                                pstmntDetCXP = conn.prepareStatement(camposDet.toString());
                                pstmntDetCXP.setString(1, cTipoPago);
                                rs3 = pstmntDetCXP.executeQuery();
                                /**
                                 *  Detalles de Documento Para Comparar Con SICOP
                                 * *
                                 */
                                while (rs3.next()) {
                                    epDetSICOP = rs3.getString("EP");
                                    importeDet = rs3.getDouble("mImporteNeto");
                                    numeMenorDet = rs3.getDouble("mImporteNeto") - centavos;
                                    numeMayorDet = rs3.getDouble("mImporteNeto") + centavos;
                                    NumberFormat formatterDet = new DecimalFormat("###.##");
                                    numeMenorForDet = Double.parseDouble(formatterDet.format(numeMenorDet));
                                    numeMayorForDet = Double.parseDouble(formatterDet.format(numeMayorDet));
                                    /**
                                     *  Validacion con un centavo mas o menos en
                                     *  Detalles SICOP
                                     * *
                                     */
                                    String sql = (// END
                                    // ,CCAU_162,CCOP_163
                                    // ,CCAU_162,CCOP_163
                                    "SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END " + " AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " DOC_HAPLICADO='1' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 " + " END AS EP" + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " DOC_HAPLICADO='1' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet) + ") = '" + epDetSICOP + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet));
                                    System.out.println("Integracion sqlDet:" + sql);
                                    pstmDetSICOP = conn.prepareStatement(sql);
                                    rs4 = pstmDetSICOP.executeQuery();
                                    /**
                                     * Detalle Son Diferentes **
                                     */
                                    if (!rs4.next()) {
                                        /**
                                         * Guardar Detalles Error **
                                         */
                                        guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, 0, integracion, clcSicop, FechaPagado, epDetSICOP, importeDet, "EJERCIDO", "Detalle Diferente Integracion");
                                        guardarDatos = "noGuardarDetalle";
                                        json.put("status", "errorDetalles");
                                        conn.commit();
                                    }
                                }
                                /**
                                 * Guardar Informacion Ejercido - Pagado **
                                 */
                                String valor = "";
                                if (guardarDatos.equals("guardarDatos")) {
                                    /**
                                     *  Guardar Informacion Ejercido - Pagado
                                     *  Relacion de Gastos Integracion
                                     * *
                                     */
                                    String[] nCa = caNoRelacion.split(",");
                                    String[] nF = folioInte.split(",");
                                    for (int i = 0; i < nF.length; i++) {
                                        /**
                                         * Seleccionar SEQUENCE Ejercido **
                                         */
                                        pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                                        pstmUpEjercido.executeUpdate();
                                        pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                                        rs7 = pstmSeqEjercido.executeQuery();
                                        if (rs7.next()) {
                                            strFolioEjercido = rs7.getString("seq_value");
                                        }
                                        /**
                                         * Seleccionar SEQUENCE Pagado **
                                         */
                                        String[] nEj = sEjercido.split(",");
                                        if (statusPagado.equals("noPagado")) {
                                            pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                                            pstmUpPagado.executeUpdate();
                                            pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                                            rs9 = pstmSeqEjercido.executeQuery();
                                            if (rs9.next()) {
                                                strFolioPagado = rs9.getString("seq_value");
                                            }
                                        }
                                        /**
                                         *  Trae Informacion Por Cuenta Por Pagar
                                         *  Relacion de Gastos Encabezado Para
                                         *  Guardar Ejercido y Pagado
                                         * *
                                         */
                                        String s = " SELECT nFolio, impNeto, caNoContrarrecibo, cTipoPoliza, " + " cDescripcionPoliza, cUnidadResponsableContable,fAplicacion, cRamo, nFolioPolizaCancelacion, aEjercicioFiscal, " + " ejercido, pagado, folioPagado, integracion " + " FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) " + " WHERE pagado != 'pagado' AND caNoContrarrecibo = " + nCa[i];
                                        pstmEncCXPIntcaNoEnc = conn.prepareStatement(s);
                                        rs15 = pstmEncCXPIntcaNoEnc.executeQuery();
                                        if (rs15.next()) {
                                            tipoPoliza = rs15.getString("cTipoPoliza");
                                            nFolPolCancelacion = Integer.parseInt(rs15.getString("nFolioPolizaCancelacion"), 10);
                                            tipoPoliza = rs15.getString("cTipoPoliza");
                                            desPoliza = rs15.getString("cDescripcionPoliza");
                                            uniResp = rs15.getString("cUnidadResponsableContable");
                                            cRamo = rs15.getString("cRamo");
                                            intaEjercicioFiscal = Integer.parseInt(rs15.getString("aEjercicioFiscal"), 10);
                                        }
                                        /**
                                         *  Guardar Informacion Ejercido - Pagado
                                         * *
                                         */
                                        nCa[i] = nCa[i].replace("'", "").trim();
                                        valor = guardarEjercidoPagado(conn, pstmSicop, pstmSequenceEjercido, pstmInsertEncEje, pstmInsertDetEje, pstmUpEjercido, pstmSeqEjercido, pstmSeqPagado, pstmUpPagado, pstmInsertEncPag, pstmInsertDetPag, nCa[i], rs5, rs7, rs8, rs9, cTipoPago, Integer.parseInt(nF[i], 10), folioPoliza, tipoPoliza, usuario, nFolPolCancelacion, fCancelacion, desPoliza, uniResp, fAplicacion, cRamo, intaEjercicioFiscal, statusPagado, strFolioEjercido, strFolioPagado, FechaPagado, integracion);
                                        if (nEj[i].equals("noEjercido")) {
                                            /**
                                             * Aplicacion Contable **
                                             */
                                            if (valor.equals("guardado")) {
                                                appCont = accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                                                conn.commit();
                                                tipoAplicacion = "EJERCIDO";
                                                if (appCont && strStatusSiaff.trim().equals("Pagada")) {
                                                    tipoAplicacion = "PAGADO";
                                                    accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                                    conn.commit();
                                                    int tienePPD = existeFacturaPPD(conn, strCaNoContrarrecibo);
                                                    if (tienePPD > 0)
                                                        enviarCorreo(conn, strCaNoContrarrecibo);
                                                    if (activaPagoParcial) {
                                                        cUE = obtieneUR(conn, strFolioPagado);
                                                        GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                        gpmbl.generaPolizaCancelaPago(strFolioPagado, cUE);
                                                    }
                                                }
                                            } else if (strStatusSiaff.trim().equals("Pagada") && statusPagado.equals("existeSinPagar")) {
                                                tipoAplicacion = "PAGADO";
                                                accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                                conn.commit();
                                                if (activaPagoParcial) {
                                                    cUE = obtieneUR(conn, folioPagado);
                                                    GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                    gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                                }
                                            }
                                        } else if (strStatusSiaff.trim().equals("Pagada") && statusPagado.equals("existeSinPagar")) {
                                            tipoAplicacion = "PAGADO";
                                            accEng.makeAccountingApplication(conn, cTipoDocumentoPag, folioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                                            conn.commit();
                                            if (activaPagoParcial) {
                                                cUE = obtieneUR(conn, folioPagado);
                                                GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                                                gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
                                            }
                                        }
                                    }
                                }
                                if (valor.equals("guardado")) {
                                    conn.commit();
                                } else {
                                    conn.rollback();
                                }
                            } else {
                                // aplicacion contable != 1
                                guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", "No Esta Para Ejercer");
                                guardarDatos = "noGuardarDetalle";
                                json.put("status", "errorDetalles");
                                conn.commit();
                            }
                        } else {
                            epDetSICOP = "";
                            clcSicop = "";
                            msnEnc = "Informacion No Encontrada en SICOP o Importe Diferente de Encabezado";
                            guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, 0, integracion, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", msnEnc);
                            json.put("status", "errorDetalles");
                            conn.commit();
                        }
                    }
                }
                // FIN DE APLICACION DE OPERACIONES AJENAS INTEGRADAS
                /**
                 * Aplicar Integracion de Pagos Diverso **
                 */
                pstmEncCXPIntD = conn.prepareStatement(" SELECT cTipoPago, SUM(impNeto) as impNeto, ejercido, pagado, integracion, folioPagado " + " FROM v_AplicarEjercidoPagadoEncabezado WITH(NOLOCK) WHERE pagado != 'pagado' AND caNoContrarrecibo != '' AND integracion != '0' AND cTipoPago='PAGODIVERSO' " + NotCXP + " Group By cTipoPago, ejercido, pagado, integracion, folioPagado " + " ORDER BY integracion");
                rs18 = pstmEncCXPIntD.executeQuery();
                while (rs18.next()) {
                    guardarDatos = "guardarDatos";
                    cTipoPago = rs18.getString("cTipoPago");
                    statusEjercido = rs18.getString("ejercido");
                    statusPagado = rs18.getString("pagado");
                    folioPagado = rs18.getString("folioPagado");
                    integracion = rs18.getString("integracion");
                    // strCaNoContrarrecibo = integracion;
                    pstmIntegracionApl = conn.prepareStatement("SELECT nFolioConsolidacion, nIdIntegracion, cDescripcionPoliza FROM tconsolidacionrelaciongastosEncabezado WHERE ISNULL(cDocumentoHaplicado,'') NOT IN ('S','C') AND nIdIntegracion = ?");
                    pstmIntegracionApl.setString(1, integracion);
                    rs17 = pstmIntegracionApl.executeQuery();
                    while (rs17.next()) {
                        log.info("Object: {}", "[Integracion de Pago Diverso] Integracion[" + integracion + "] folio Pagado[" + folioPagado + "] Estatus Pagado[" + statusPagado + "] Estatus Ejercido[" + statusEjercido + "] Tipo Pago[" + cTipoPago + "]");
                        if (statusEjercido.equals("noEjercido") && statusPagado.equals("noPagado")) {
                            pstmFolInt = conn.prepareStatement("SELECT caNoContrarrecibo, nFolio, ejercido, impNeto FROM v_AplicarEjercidoPagadoEncabezado with(nolock) WHERE integracion = ?");
                            pstmFolInt.setString(1, integracion);
                            rs14 = pstmFolInt.executeQuery();
                            String caNoRelacion = "";
                            String folioInte = "";
                            String nFolioPagoInt = "";
                            String sEjercido = "";
                            double impNeto = 0.00;
                            while (rs14.next()) {
                                caNoRelacion += "'" + rs14.getString("caNoContrarrecibo") + "',";
                                folioInte += rs14.getString("nFolio") + ",";
                                sEjercido += rs14.getString("ejercido") + ",";
                                impNeto += Double.parseDouble(rs14.getString("impNeto"));
                            }
                            int numC = caNoRelacion.length() - 1;
                            int numF = folioInte.length() - 1;
                            int numE = sEjercido.length() - 1;
                            caNoRelacion = caNoRelacion.substring(0, numC);
                            nFolioPagoInt = folioInte.substring(0, numF);
                            sEjercido = sEjercido.substring(0, numE);
                            folioPoliza = 0;
                            importeEnc = impNeto;
                            numeMenor = Double.parseDouble(rs18.getString("impNeto")) - centavos;
                            numeMayor = impNeto + centavos;
                            NumberFormat formatter = new DecimalFormat("###.##");
                            numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
                            numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
                            // Encabezado de SICOP dependiendo de los valores de
                            // encabezado CXP para validar si son iguales
                            // APLICACION_CONTABLE = '1' Para Poder Ejercer
                            String sqlCLC = "SELECT NCTR_47, NCLC_43, APLICACION_CONTABLE FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) WHERE SICOP.FOLIO_SIAFF_112 = SIAFF.FOLIO_CLC AND NCTR_47 = '" + integracion + "' AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) WHERE NCTR_47 = '" + integracion + "' AND FOLIO_SIAFF_112 <> '0' AND DOC_HAPLICADO=1) BETWEEN  " + String.format("%.2f", numeMenorFor) + " AND " + String.format("%.2f", numeMayorFor) + " GROUP BY NCTR_47, NCLC_43, APLICACION_CONTABLE";
                            pstmEncSICOP = conn.prepareStatement(sqlCLC);
                            rs2 = pstmEncSICOP.executeQuery();
                            System.out.println("sql encabezado: " + sqlCLC);
                            // Encabezado iguales
                            if (rs2.next()) {
                                clcSicop = rs2.getString("NCLC_43");
                                aplicacionContable = rs2.getString("APLICACION_CONTABLE");
                                // Valida si se Puede Ejercer
                                // APLICACION_CONTABLE
                                if (aplicacionContable.equals("1")) {
                                    // Detalles Dependiendo el Documento
                                    camposDet = new StringBuilder("SELECT SUBSTRING(EP,0,56) AS EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cEjercicio FROM v_AplicarEjercidoPagadoDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND nFolio IN (").append(nFolioPagoInt).append(") AND cTipoPago = ? GROUP BY SUBSTRING(EP,0,56), cEjercicio, cIdCuentaContable ");
                                    log.trace("Object: {}", "\\nEjercido/Pagado Ejecutando: \n[" + camposDet + "]\n[" + cTipoPago + "]\n");
                                    pstmntDetCXP = conn.prepareStatement(camposDet.toString());
                                    pstmntDetCXP.setString(1, cTipoPago);
                                    rs3 = pstmntDetCXP.executeQuery();
                                    // Detalles de Documento Para Comparar Con
                                    // SICOP
                                    while (rs3.next()) {
                                        String epNormalizada = Util.normalizaEP(rs3.getString("EP"));
                                        epDetSICOP = epNormalizada != null ? epNormalizada.substring(0, 55) : "";
                                        importeDet = rs3.getDouble("mImporteNeto");
                                        numeMenorDet = rs3.getDouble("mImporteNeto") - centavos;
                                        numeMayorDet = rs3.getDouble("mImporteNeto") + centavos;
                                        NumberFormat formatterDet = new DecimalFormat("###.##");
                                        numeMenorForDet = Double.parseDouble(formatterDet.format(numeMenorDet));
                                        numeMayorForDet = Double.parseDouble(formatterDet.format(numeMayorDet));
                                        // Validacion con un centavo mas o menos
                                        // en Detalles SICOP
                                        String sql = (// END
                                        // ,CCAU_162,CCOP_163
                                        // ,CCAU_162,CCOP_163
                                        "SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END  AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 " + " END AS EP" + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet) + ") = '" + epDetSICOP + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet));
                                        pstmDetSICOP = conn.prepareStatement(sql);
                                        rs4 = pstmDetSICOP.executeQuery();
                                        // Detalle Son Diferentes
                                        if (!rs4.next()) {
                                            // Guardar Detalles Error
                                            guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, 0, integracion, clcSicop, FechaPagado, epDetSICOP, importeDet, "EJERCIDO", "Detalle Diferente Integracion");
                                            guardarDatos = "noGuardarDetalle";
                                            json.put("status", "errorDetalles");
                                            conn.commit();
                                        }
                                    }
                                    String valor = "";
                                    if (guardarDatos.equals("guardarDatos")) {
                                        String strStatusSiaffIntegra = "";
                                        boolean aplicado = false;
                                        pstmSicopPag = conn.prepareStatement(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, ISNULL(SIAFF.FECHA_PAGO,'-') AS FECHA_PAGO, SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC ");
                                        pstmSicopPag.setString(1, integracion);
                                        rs12 = pstmSicopPag.executeQuery();
                                        if (rs12.next()) {
                                            strStatusSiaffIntegra = rs12.getString("ESTATUS_CLC");
                                        }
                                        if (strStatusSiaffIntegra.trim().equals("Pagada")) {
                                            aplicado = ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, "");
                                            if (aplicado)
                                                valor = "guardado";
                                        }
                                    }
                                    if (valor.equals("guardado")) {
                                        conn.commit();
                                    } else {
                                        conn.rollback();
                                    }
                                } else {
                                    // aplicacion contable != 1
                                    guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", "No Esta Para Ejercer");
                                    guardarDatos = "noGuardarDetalle";
                                    json.put("status", "errorDetalles");
                                    conn.commit();
                                }
                            } else {
                                epDetSICOP = "";
                                clcSicop = "";
                                msnEnc = "Informacion No Encontrada en SICOP o Importe Diferente de Encabezado";
                                guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, 0, integracion, clcSicop, FechaPagado, epDetSICOP, importeEnc, "EJERCIDO", msnEnc);
                                json.put("status", "errorDetalles");
                                conn.commit();
                            }
                        }
                    }
                }
                //Timbrar CFDI
                pst = conn.prepareStatement("SELECT cxp, nPeriodo FROM tRespuestaGeneraCFDI WITH (NOLOCK) WHERE  cTimbrado is null or cTimbrado = 'N'");
                rs19 = pst.executeQuery();
                while (rs19.next()) {
                    TimbrarCFDIViaticos timbre = new TimbrarCFDIViaticos();
                    String cxp = rs19.getString("cxp");
                    timbre.setPeriodo(Integer.parseInt(rs19.getString("nPeriodo")));
                    notificator.timbrarCFDI(conn, timbre, cxp);
                }
                conn.commit();
                if (!"".equals(NotCuentaPorPagar)) {
                    cxpParaRechazoBancario(conn, NotCuentaPorPagar);
                }
                // APLICACION DEL INGRESO PARA RECURSO FISCAL
                json = aplicaIngresoRIF(conn, centavos);
            } catch (Exception e) {
                log.error("Error aplicando contablemente " + e, e);
                msg = e.toString();
                try {
                    json.put("status", strCaNoContrarrecibo + " - " + msg);
                    conn.rollback();
                } catch (Exception ee) {
                    log.warn("Error: cerrando rollback ", ee);
                }
                try {
                    guardarDetalleDiferencia(conn, pstmDiferencia, cTipoPago, nFolioPago, strCaNoContrarrecibo, clcSicop, FechaPagado, epDetSICOP, importeEnc, tipoAplicacion, msg);
                    conn.commit();
                } catch (Exception de) {
                    log.warn("Error: guardar Detalle ", de);
                    conn.rollback();
                }
            } finally {
                CloseObject.closeObject(pstmEncCXP, false);
                CloseObject.closeObject(pstmntDetCXP, false);
                CloseObject.closeObject(pstmEncSICOP, false);
                CloseObject.closeObject(pstmDetSICOP, false);
                CloseObject.closeObject(pstmInsertEncEje, false);
                CloseObject.closeObject(pstmInsertDetEje, false);
                CloseObject.closeObject(pstmInsertEncPag, false);
                CloseObject.closeObject(pstmInsertDetPag, false);
                CloseObject.closeObject(pstmCent, false);
                CloseObject.closeObject(pstmSicop, false);
                CloseObject.closeObject(pstmSequenceEjercido, false);
                CloseObject.closeObject(pstmUpEjercido, false);
                CloseObject.closeObject(pstmSeqEjercido, false);
                CloseObject.closeObject(pstmSeqPagado, false);
                CloseObject.closeObject(pstmUpPagado, false);
                CloseObject.closeObject(pstmDiferencia, false);
                CloseObject.closeObject(pstmSicopPag, false);
                CloseObject.closeObject(pstmnUpdate, false);
                CloseObject.closeObject(pstmEncCXPInt, false);
                CloseObject.closeObject(pstmEncCXPIntcaNoEnc, false);
                CloseObject.closeObject(pstmEncCXPIntcaNo, false);
                CloseObject.closeObject(pstmFolInt, false);
                CloseObject.closeObject(pstmDelete, false);
                CloseObject.closeObject(pstmExisteSICOP, false);
                CloseObject.closeObject(pstmEncCXPIntD, false);
                CloseObject.closeObject(pstmIntegracionApl, false);
                CloseObject.closeObject(pst, false);
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(rs1, false);
                CloseObject.closeObject(rs2, false);
                CloseObject.closeObject(rs3, false);
                CloseObject.closeObject(rs4, false);
                CloseObject.closeObject(rs5, false);
                CloseObject.closeObject(rs6, false);
                CloseObject.closeObject(rs7, false);
                CloseObject.closeObject(rs8, false);
                CloseObject.closeObject(rs9, false);
                CloseObject.closeObject(rs10, false);
                CloseObject.closeObject(rs11, false);
                CloseObject.closeObject(rs12, false);
                CloseObject.closeObject(rs13, false);
                CloseObject.closeObject(rs14, false);
                CloseObject.closeObject(rs15, false);
                CloseObject.closeObject(rs16, false);
                CloseObject.closeObject(rs17, false);
                CloseObject.closeObject(rs18, false);
                CloseObject.closeObject(rs19, false);
                CloseObject.closeObject(conn, false);
            }
            String resultadoLaudos = "";
            EjercidoPagadoBusinessLogic epbl = new EjercidoPagadoBusinessLogic(jniName);
            try {
                epbl.ejercidoPagadoIntegracion();
                epbl.ejercidoPagadoPenas();
                resultadoLaudos = "Aplicado Exitosamente.";
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                resultadoLaudos = "Error ejecutando Ejercido Pagado de Laudos:" + e;
            }
            return json;
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            throw new SQLException(th);
        }
    }

    public String guardarEjercidoPagado(Connection conn, PreparedStatement pstmSicop, PreparedStatement pstmSequenceEjercido, PreparedStatement pstmInsertEncEje, PreparedStatement pstmInsertDetEje, PreparedStatement pstmUpEjercido, PreparedStatement pstmSeqEjercido, PreparedStatement pstmSeqPagado, PreparedStatement pstmUpPagado, PreparedStatement pstmInsertEncPag, PreparedStatement pstmInsertDetPag, String strCaNoContrarrecibo, ResultSet rs5, ResultSet rs7, ResultSet rsEjercidoDetalle, ResultSet rs9, String cTipoPago, int nFolioPago, int folioPoliza, String tipoPoliza, String usuario, int nFolPolCancelacion, String fCancelacion, String desPoliza, String uniResp, String fAplicacion, String cRamo, int intaEjercicioFiscal, String statusPagado, String strFolioEjercido, String strFolioPagado, String FechaPagado, String integracion) throws Exception {
        String valor = "";
        String strCLC = "";
        String fechaAplicadoSicop = "";
        String fechaPagoSicop = "";
        String solicitudPago = "";
        String numProceso = "";
        String folioSiaff = "";
        String fPago = "";
        String cEvento = "";
        boolean esSAIAlterno = "true".equals(ConfiguraAplicativoManager.getSystemSetting(conn, "SAI_AMBIENTAL"));
        String fPagado = null;
        try {
            boolean espagofonden = OperacionAjenaManager.esPagoFonden(conn, nFolioPago);
            String strCaNoContrarrecibos = (integracion != "0") ? integracion : strCaNoContrarrecibo;
            /**
             * Selecciona Informacion de SICOP y SIAFF **
             */
            /*
			 * JDS SE MODIFICO LA FECHA DE APLICACION DE SICOP POR LA DE SIAFF
			 * PARA QUE APLIQUEN CON LA FECHA CORRECTA QUE SE EJERCIO LOS DE
			 * TIENDA DIGITAL
			 */
            StringBuilder query = new StringBuilder();
            query.append(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_APLICACION FECHA_APL, SICOP.FECHA_PAGO_106, SICOP.SPAG_176, SICOP.PROC_CLAVE, ");
            query.append(" 	SICOP.FOLIO_SIAFF_112, isnull(SIAFF.FECHA_PAGO,'-') as FECHA_PAGO, SIAFF.ESTATUS_CLC ");
            query.append(" FROM CLC_SICOP SICOP WITH(NOLOCK) INNER JOIN  CLC_SIAFF_ENC SIAFF WITH(NOLOCK)  ON SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC ");
            query.append(" WHERE NCTR_47 = ? ");
            query.append(" GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_APLICACION, SICOP.FECHA_PAGO_106, SICOP.SPAG_176, ");
            query.append(" 	     SICOP.PROC_CLAVE, SICOP.FOLIO_SIAFF_112,NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC ");
            pstmSicop = conn.prepareStatement(query.toString());
            pstmSicop.setString(1, strCaNoContrarrecibos);
            rs5 = pstmSicop.executeQuery();
            if (rs5.next()) {
                strCLC = rs5.getString("NCLC_43");
                String[] fAS = rs5.getString("FECHA_APL").split("/");
                fechaAplicadoSicop = fAS[2] + "-" + fAS[1] + "-" + fAS[0];
                if (StringUtils.isEmpty(rs5.getString("FECHA_PAGO_106")))
                    throw new Exception("La columna FECHA_PAGO_106 en la tabla CLC_SICOP esta vacio. Corrija los archivos de carga y reintente");
                String[] fPS = rs5.getString("FECHA_PAGO_106").split("/");
                fechaPagoSicop = fPS[2] + "-" + fPS[1] + "-" + fPS[0];
                solicitudPago = rs5.getString("SPAG_176");
                numProceso = rs5.getString("PROC_CLAVE");
                folioSiaff = rs5.getString("FOLIO_SIAFF_112");
                fPago = rs5.getString("FECHA_PAGO");
                strStatusSiaff = rs5.getString("ESTATUS_CLC");
                if (fPago.length() > 1) {
                    if (StringUtils.isEmpty(fPago))
                        fPago = "01/01/2015";
                    String[] fechPagado = fPago.split("/");
                    fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
                }
                /**
                 * Guardar Encabezado Ejercido - Pagado **
                 */
                boolean existeEjercido = existeEjercido(conn, strCaNoContrarrecibo);
                boolean existePagado = existePagado(conn, strCaNoContrarrecibo);
                int anioAplicacion = Util.getYearFromDate(fechaAplicadoSicop);
                int strcEjercicio = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
                String fechaAplicacion = (strcEjercicio == anioAplicacion ? fechaAplicadoSicop : "31/12/" + strcEjercicio);
                String ctipoPoliza = "EG";
                if (!existeEjercido) {
                    String queryInsertEjercidoEncabezado = "INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza, U_LOGIN, nFolioPolizaCancelacion, fCancelacion, cDescripcionPoliza, cUnidadResponsableContable, nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "VALUES ('" + cTipoPago + "'," + nFolioPago + ",'" + strCaNoContrarrecibo + "'," + folioPoliza + ",'DI','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ",'" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + strFolioEjercido + " )";
                    // URVP.13082014. SE PONE FIJO EL TIPO DE POLIZA A DIARIO EN
                    // DEL EJERCIDO----------------^ (tipoPoliza--DI)
                    pstmInsertEncEje = conn.prepareStatement(queryInsertEjercidoEncabezado);
                    log.debug("Object: {}", "Insertando en tEjercidoEncabezado " + queryInsertEjercidoEncabezado);
                    pstmInsertEncEje.executeUpdate();
                }
                if (existePagadoAplicado(conn, strCaNoContrarrecibo))
                    return "guardado";
                if (statusPagado.equals("noPagado")) {
                    if (!existePagado) {
                        if (fPagado == null)
                            fPagado = Util.getToday("yyyy-MM-dd");
                        anioAplicacion = Util.getYearFromDate(fPagado);
                        if (strcEjercicio != anioAplicacion && "RELACIONGASTOS".equals(cTipoPago)) {
                            String esComprobacion = esComprobacion(conn, strCaNoContrarrecibo);
                            if ("NO".equalsIgnoreCase(esComprobacion))
                                ctipoPoliza = "DI";
                        }
                        strcEjercicio = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
                        fechaAplicacion = (strcEjercicio == anioAplicacion ? fPagado : "31/12/" + strcEjercicio);
                        StringBuilder queryPagadoA = new StringBuilder();
                        queryPagadoA.append("INSERT INTO tPagadoEncabezado (  cTipoPago, nFolioPAGO, caNoContrarrecibo, nFolioPoliza, cTipoPoliza, U_LOGIN,");
                        queryPagadoA.append("nFolioPolizaCancelacion, fCancelacion, cDescripcionPoliza, cUnidadResponsableContable, nFolioSICOP, fAplicacion, cRamo, cIdUsuarioCaptura,");
                        queryPagadoA.append("FechaAplicacionSicop, FechaPagoSicop, SolicitudPago, NumeroProceso, nFolioSIAFF, FechaPagado,nFolioPagado) VALUES ('");
                        queryPagadoA.append(cTipoPago + "'," + nFolioPago + ",'" + strCaNoContrarrecibo + "'," + folioPoliza + ",'" + ctipoPoliza + "','" + usuario + "',");
                        queryPagadoA.append(nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ", ");
                        queryPagadoA.append((fechaAplicacion.indexOf("'") >= 0 ? fechaAplicacion : "'" + fechaAplicacion + "'") + ",'" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop);
                        queryPagadoA.append("','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + ",'" + FechaPagado + "'," + strFolioPagado + " )");
                        pstmInsertEncPag = conn.prepareStatement(queryPagadoA.toString());
                        int insertadosPagado = pstmInsertEncPag.executeUpdate();
                        log.trace("Object: {}", "Ejecutando: " + queryPagadoA);
                        log.debug("Object: {}", "Se insertaron: " + insertadosPagado + " Registros en tPagado ");
                    } else {
                        log.debug("Object: {}", "El pago " + strCaNoContrarrecibo + " ya estaba insertado sin ser aplicado.");
                    }
                }
                /**
                 * Si ya
                 */
                /**
                 * Seleccionar Detalles Para Ingresar **
                 */
                log.trace(" Seleccionando Detalles Para Ingresar ");
                StringBuilder queryDetallePagado = new StringBuilder();
                queryDetallePagado.append(" SELECT nDocRenglon, cMes, cEjercicio, EP, cIdCuentaContable, mComprometido, isnull(nPoliza,0) AS nPoliza, ");
                queryDetallePagado.append("        ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo, ");
                queryDetallePagado.append("	     mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda, ");
                queryDetallePagado.append("        mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC, ");
                queryDetallePagado.append("        isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion, CTAB");
                queryDetallePagado.append("		, mImporteISRLaudos, mPasivoDiferido, mImporteIva6, cPasivo, cUnidadResponsable, mimporteISRResico, mISROtros");
                queryDetallePagado.append(" FROM v_AplicarEjercidoPagadoDetalle WITH(NOLOCK) ");
                queryDetallePagado.append(" WHERE cTipoPago = ? AND nFolio = ? AND cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV'");
                pstmInsertDetEje = conn.prepareStatement(queryDetallePagado.toString());
                pstmInsertDetEje.setString(1, cTipoPago);
                pstmInsertDetEje.setInt(2, nFolioPago);
                rsEjercidoDetalle = pstmInsertDetEje.executeQuery();
                log.trace("Object: {}", " Ejecutando query: " + queryDetallePagado + "\n[" + cTipoPago + "]\n[" + nFolioPago + "]");
                int nDocRenglon = 0;
                while (rsEjercidoDetalle.next()) {
                    log.trace("Iterando detalle de pagos.");
                    if (cTipoPago.equals("FEDERALIZADO")) {
                        nDocRenglon = nDocRenglon + 1;
                    } else {
                        nDocRenglon = Integer.parseInt(rsEjercidoDetalle.getString("nDocRenglon"), 10);
                    }
                    int cMes = Integer.parseInt(rsEjercidoDetalle.getString("cMes"), 10);
                    strcEjercicio = rsEjercidoDetalle.getInt("cEjercicio");
                    String EP = rsEjercidoDetalle.getString("EP");
                    String cOBGT = EP.substring(31, 36);
                    String cIdCuentaContable = rsEjercidoDetalle.getString("cIdCuentaContable");
                    String cIdEntidadContable = rsEjercidoDetalle.getString("cIdEntidadContable");
                    String cIdRelacion = rsEjercidoDetalle.getString("cIdRelacion");
                    String idTipoMovimiento = rsEjercidoDetalle.getString("ID_TIPO_MOVIMIENTO");
                    String idTipoConcepto = rsEjercidoDetalle.getString("ID_TIPO_CONCEPTO");
                    String cTAB = rsEjercidoDetalle.getString("CTAB");
                    String pasivo = rsEjercidoDetalle.getString("cPasivo");
                    String cUnidadResponsable = rsEjercidoDetalle.getString("cUnidadResponsable");
                    double mComprometido = rsEjercidoDetalle.getDouble("mComprometido");
                    double mImporteBruto = rsEjercidoDetalle.getDouble("mImporteBruto");
                    double mImporteIva = rsEjercidoDetalle.getDouble("mImporteIva");
                    double mImporteMasIva = rsEjercidoDetalle.getDouble("mImporteMasIva");
                    double mSancion = rsEjercidoDetalle.getDouble("mSancion");
                    double mDevolucion = rsEjercidoDetalle.getDouble("mDevolucion");
                    double mImporteAmortiza = rsEjercidoDetalle.getDouble("mImporteAmortiza");
                    double mRetencion = rsEjercidoDetalle.getDouble("mRetencion");
                    double mPenalizacion = rsEjercidoDetalle.getDouble("mPenalizacion");
                    double m2Millar = rsEjercidoDetalle.getDouble("m2Millar");
                    double m23IVA = rsEjercidoDetalle.getDouble("m23IVA");
                    double mImporteIvaHonorarios = rsEjercidoDetalle.getDouble("mImporteIvaHonorarios");
                    double mImporteIvaProv = rsEjercidoDetalle.getDouble("mImporteIvaProv");
                    double mImporteObra = rsEjercidoDetalle.getDouble("mImporteObra");
                    double mImporteIvaArrenda = rsEjercidoDetalle.getDouble("mImporteIvaArrenda");
                    double mImporteISRLaudos = rsEjercidoDetalle.getDouble("mImporteISRLaudos");
                    double mPasivoDiferido = rsEjercidoDetalle.getDouble("mPasivoDiferido");
                    double mIva6 = rsEjercidoDetalle.getDouble("mImporteIva6");
                    double mISRRESICO = rsEjercidoDetalle.getDouble("mimporteISRResico");
                    double mISROtro = rsEjercidoDetalle.getDouble("mISROtros");
                    int nPoliza = rsEjercidoDetalle.getInt("nPoliza");
                    String[] cEventoCa = rsEjercidoDetalle.getString("cEvento").split("_");
                    cEvento = "P";
                    if (strcEjercicio != anioAplicacion && "RELACIONGASTOS".equals(cTipoPago)) {
                        String esComprobacion = esComprobacion(conn, strCaNoContrarrecibo);
                        if ("NO".equalsIgnoreCase(esComprobacion))
                            cEvento = cEvento + "_FA";
                    }
                    for (int i = 1; i < cEventoCa.length; i++) {
                        cEvento += "_";
                        cEvento += cEventoCa[i];
                    }
                    if (esSAIAlterno && ("AJENAS".equalsIgnoreCase(cTipoPago) && espagofonden)) {
                        cEvento = OperacionAjenaManager.calculaEventoFonden(EP);
                        log.debug("Object: {}", "Se trata de sistema alterno. Cambiando evento: " + cEvento);
                    }
                    String cCentroContable = rsEjercidoDetalle.getString("cCentroContable");
                    String Rfc = rsEjercidoDetalle.getString("RFC");
                    String alm = rsEjercidoDetalle.getString("ALM");
                    String nCapitulo = rsEjercidoDetalle.getString("nCapitulo");
                    /*
					 * ARLA se le el evento de la vista
					 * v_AplicarEjercidoPagadoDetalle
					 */
                    String cEventoPagado = cEvento;
                    String altaAlmacen = rsEjercidoDetalle.getString("altaAlmacen");
                    double mImporteNeto = rsEjercidoDetalle.getDouble("mImporteNeto");
                    double mISRHonorarios = rsEjercidoDetalle.getDouble("mISRHonorarios");
                    double mObra5 = rsEjercidoDetalle.getDouble("mObra5");
                    double mImporteFlete4 = rsEjercidoDetalle.getDouble("mImporteFlete4");
                    double mRetImpuestoCedular = rsEjercidoDetalle.getDouble("mRetImpuestoCedular");
                    double mISRArrenda = rsEjercidoDetalle.getDouble("mISRArrenda");
                    double mImporteFlete23 = rsEjercidoDetalle.getDouble("mImporteFlete23");
                    double mCNIC = rsEjercidoDetalle.getDouble("mCNIC");
                    double mTesofe = rsEjercidoDetalle.getDouble("mTesofe");
                    double mIMDT = rsEjercidoDetalle.getDouble("mIMDT");
                    StringBuilder queryInsertDetEjercido = new StringBuilder();
                    queryInsertDetEjercido.append("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO");
                    queryInsertDetEjercido.append(",ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios");
                    queryInsertDetEjercido.append(",mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,");
                    queryInsertDetEjercido.append(" aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido, mImporteISRLaudos, mimporteiva6,cUnidadResponsable, mimporteISRResico, mISROtros)");
                    queryInsertDetEjercido.append(" VALUES('").append(cTipoPago).append("',").append(nFolioPago).append(",").append(nDocRenglon).append(",");
                    queryInsertDetEjercido.append(cMes).append(",'").append(strcEjercicio).append("','").append(cIdEntidadContable).append("','").append(cIdRelacion).append("','");
                    queryInsertDetEjercido.append(EP).append("','").append(cIdCuentaContable).append("',").append(mComprometido).append(",").append(nPoliza).append(",'");
                    queryInsertDetEjercido.append(idTipoMovimiento).append("','").append(idTipoConcepto).append("','EJERCIDO','").append(cMes).append("','").append(cCentroContable);
                    queryInsertDetEjercido.append("','").append(Rfc).append("',").append(mImporteNeto).append(",'").append(alm).append("',").append(mImporteBruto).append(",");
                    queryInsertDetEjercido.append(mImporteMasIva).append(",").append(mImporteIva).append(",'").append(nCapitulo).append("',").append(mSancion).append(",").append(mDevolucion).append(",");
                    queryInsertDetEjercido.append(mImporteAmortiza).append(" ,").append(mRetencion).append(",").append(mPenalizacion).append(",").append(m2Millar).append(",").append(m23IVA);
                    queryInsertDetEjercido.append(",").append(mISRHonorarios).append(",").append(mObra5).append(", ").append(mImporteFlete4).append(", ").append(mISRArrenda).append(", ").append(mRetImpuestoCedular);
                    queryInsertDetEjercido.append(", ").append(mImporteIvaArrenda).append(", ").append(mImporteIvaHonorarios).append(", ").append(mImporteFlete23).append(",").append(mImporteIvaProv).append(",").append(mImporteObra);
                    queryInsertDetEjercido.append(", ").append(mCNIC).append(",").append(mTesofe).append(",'").append(altaAlmacen).append("',").append(" '").append(intaEjercicioFiscal).append("',").append(mIMDT);
                    queryInsertDetEjercido.append(",").append(mImporteNeto).append(",").append(strFolioEjercido).append(", ").append(mImporteISRLaudos).append(", ").append(mIva6).append(",'");
                    queryInsertDetEjercido.append(cUnidadResponsable).append("',").append(mISRRESICO).append(" , ").append(mISROtro).append(")");
                    pstmInsertDetEje = conn.prepareStatement(queryInsertDetEjercido.toString());
                    if (!existeEjercido) {
                        log.trace("Object: {}", "No existe detalle. Se ejecutara: \n" + queryInsertDetEjercido);
                        pstmInsertDetEje.executeUpdate();
                    }
                    if (statusPagado.equals("noPagado")) {
                        if (!existePagado) {
                            log.trace("No Pagado. Se inicia proceso de pagado");
                            StringBuilder queryInsertPagadoDet = new StringBuilder();
                            queryInsertPagadoDet.append("INSERT INTO tPagadoDetalle (").append("cTipoPago,").append("nFolioPAGO,").append("nDocRenglon,").append("cMes,").append("cEjercicio,").append("cIdEntidadContable,");
                            queryInsertPagadoDet.append("cIdRelacion,").append("EP,").append("cIdCuentaContable,").append("mComprometido,").append("nPoliza,").append("ID_TIPO_MOVIMIENTO,").append("ID_TIPO_CONCEPTO,");
                            queryInsertPagadoDet.append("cEvento,").append("nMes,").append("cCentroContable,").append("RFC, ").append("mImporteNeto,").append("ALM").append(",mImporteBruto,").append("mImporteMasIva,");
                            queryInsertPagadoDet.append("mImporteIva,").append("nCapitulo,").append("mSancion,").append("mDevolucion,").append("mImporteAmortiza,").append("mRetencion,").append("mPenalizacion,");
                            queryInsertPagadoDet.append("m2Millar,").append("m23IVA,").append("mISRHonorarios,").append("mObra5    ,").append("mImporteFlete4,").append("mISRArrenda,").append("mRetImpuestoCedular,");
                            queryInsertPagadoDet.append("mImporteIvaArrenda,").append("mImporteIvaHonorarios ,").append("mImporteFlete23,").append("mImporteIvaProv ,").append("mImporteObra,").append("mCNIC,").append("mTesofe,");
                            queryInsertPagadoDet.append("altaAlmacen,").append("aEjercicioFiscal,").append("mIMDT,").append("mImporte,nFolioPagado,OBGT, CTAB, mImporteISRLaudos, mPasivoDiferido, mImporteIva6, cPasivo,cUnidadResponsable, mimporteISRResico, mISROtros)").append(" VALUES('");
                            queryInsertPagadoDet.append(cTipoPago).append("',").append(nFolioPago).append(",").append(nDocRenglon).append(",").append(cMes).append(",'").append(strcEjercicio);
                            queryInsertPagadoDet.append("','").append(cIdEntidadContable).append("','").append(cIdRelacion).append("','").append(EP).append("','").append(cIdCuentaContable).append("',");
                            queryInsertPagadoDet.append(mComprometido).append(",").append(nPoliza).append(",'").append(idTipoMovimiento).append("','").append(idTipoConcepto).append("','").append(cEventoPagado).append("','");
                            queryInsertPagadoDet.append(cMes).append("','").append(cCentroContable).append("','").append(Rfc).append("',").append(mImporteNeto).append(",'").append(alm).append("',").append(mImporteBruto).append(",");
                            queryInsertPagadoDet.append(mImporteMasIva).append(",").append(mImporteIva).append(",'").append(nCapitulo).append("',").append(mSancion).append(",").append(mDevolucion).append(",").append(mImporteAmortiza);
                            queryInsertPagadoDet.append(" ,").append(mRetencion).append(",").append(mPenalizacion).append(",").append(m2Millar).append(",").append(m23IVA).append(",").append(mISRHonorarios).append(",").append(mObra5);
                            queryInsertPagadoDet.append(" ,").append(mImporteFlete4).append(",").append(mISRArrenda).append(",").append(mRetImpuestoCedular).append(",").append(mImporteIvaArrenda).append(",").append(mImporteIvaHonorarios).append("	,");
                            queryInsertPagadoDet.append(mImporteFlete23).append(",").append(mImporteIvaProv).append(",").append(mImporteObra).append(",").append(mCNIC).append(",").append(mTesofe).append(",'").append(altaAlmacen);
                            queryInsertPagadoDet.append("','").append(intaEjercicioFiscal).append("',").append(mIMDT).append(",").append(mImporteNeto).append(",").append(strFolioPagado).append(",'").append(cOBGT).append("', '");
                            queryInsertPagadoDet.append(cTAB).append("',").append(mImporteISRLaudos).append(",").append(mPasivoDiferido).append(",").append(mIva6).append(", '").append(pasivo).append("','").append(cUnidadResponsable).append("',").append(mISRRESICO).append(",").append(mISROtro).append(" )");
                            pstmInsertDetPag = conn.prepareStatement(queryInsertPagadoDet.toString());
                            log.trace("Object: {}", "Se insertara detalle de pago:\n" + queryInsertPagadoDet);
                            int detPagInser = pstmInsertDetPag.executeUpdate();
                            log.debug("Object: {}", "Se insertaron " + detPagInser + " en el detalle de pagado.");
                        }
                    }
                    valor = "guardado";
                }
            } else {
                valor = "guardado";
            }
            return valor;
        } finally {
            log.debug("En finaly");
        }
    }

    public String guardarDetalleDiferencia(Connection conn, PreparedStatement pstmDiferencia, String cTipoPago, int nFolioPago, String strCaNoContrarrecibo, String clcSicop, String fechaAplicacion, String EP, double importe, String tipoDocumento, String descripcion) throws SQLException {
        String valor = "";
        try {
            descripcion = StringUtils.trimToEmpty(descripcion).replaceAll("'", "");
            log.debug("Object: {}", "INSERT INTO tDetalleEjercidoPagado (cTipoPago, nFolioPago, caNoContrarrecibo, clcSicop, fechaAplicacion, EP, importe, tipoDocumento, descripcion) " + "VALUES('" + cTipoPago + "', " + nFolioPago + ", '" + strCaNoContrarrecibo + "', '" + clcSicop + "', '" + fechaAplicacion + "', '" + EP + "', " + importe + ", '" + tipoDocumento + "', '" + descripcion + "') ");
            pstmDiferencia = conn.prepareStatement("INSERT INTO tDetalleEjercidoPagado (cTipoPago, nFolioPago, caNoContrarrecibo, clcSicop, fechaAplicacion, EP, importe, tipoDocumento, descripcion) " + "VALUES('" + cTipoPago + "', " + nFolioPago + ", '" + strCaNoContrarrecibo + "', '" + clcSicop + "', '" + fechaAplicacion + "', '" + EP + "', " + importe + ", '" + tipoDocumento + "', '" + descripcion + "') ");
            pstmDiferencia.executeUpdate();
        } finally {
        }
        return valor;
    }

    public JSONObject aplicaIngresoAnexo(Connection conn, double centavos) throws SQLException {
        PreparedStatement pstmFoliosIntegraciones = null, pstmEncSICOP = null, pstmntDetCons = null, pstmDetSICOP = null, pstmDiferencia = null, pstmEstatusSicopPagado = null, pstmListadoAnexosIntegrados = null, pstmUpdateFechaAnexo1 = null;
        ResultSet rsFoliosIntegraciones = null, rsEncSICOP = null, rsDetalleCons = null, rsDetSICOP = null, rsEstatusSicopPagado = null, rsListadoAnexosIntegrados = null;
        String valor = "", NotCXP = "", nFolioConsolidacion = "", integracion = "", estatus = "", clcSicop = "", aplicacionContable = "", camposDet = "", epDetSICOP = "", guardarDatos = "guardarDatos", FechaPagado = "", msnEnc = "", fechaUpdate = "";
        double mImporte = 0.00, numeMenor, numeMayor, numeMenorFor, numeMayorFor, importeDet, numeMenorDet, numeMayorDet, numeMenorForDet, numeMayorForDet;
        JSONObject json = new JSONObject();
        java.util.Date utilDate = new java.util.Date();
        long lnMilisegundos = utilDate.getTime();
        java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
        String[] fp = String.valueOf(sqlDate).split("-");
        FechaPagado = fp[0] + "-" + fp[1] + "-" + fp[2];
        try {
            json.put("status", "guardado");
            /**
             * Aplicar Ingreso de Anexo 1 **
             */
            pstmFoliosIntegraciones = conn.prepareStatement(" SELECT nFolioConsolidacion,Integracion,Aplicado,mImporte FROM v_AplicarIngresoAnexoEncabezado WITH(NOLOCK) WHERE Aplicado='SIN APLICAR' " + NotCXP + " ORDER BY nFolioConsolidacion");
            rsFoliosIntegraciones = pstmFoliosIntegraciones.executeQuery();
            while (rsFoliosIntegraciones.next()) {
                guardarDatos = "guardarDatos";
                nFolioConsolidacion = rsFoliosIntegraciones.getString("nFolioConsolidacion");
                integracion = rsFoliosIntegraciones.getString("Integracion");
                estatus = rsFoliosIntegraciones.getString("Aplicado");
                mImporte = Double.parseDouble(rsFoliosIntegraciones.getString("mImporte"));
                log.info("Object: {}", "[Integracion de Anexo 1] Folio Consolidacion[" + nFolioConsolidacion + "] Integracion[" + integracion + "]  Estatus[" + estatus + "] Importe[" + String.format("%.2f", mImporte) + "]");
                numeMenor = mImporte - centavos;
                numeMayor = mImporte + centavos;
                NumberFormat formatter = new DecimalFormat("###.##");
                numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
                numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
                // Encabezado de SICOP dependiendo de los valores de encabezado
                // CXP para validar si son iguales, APLICACION_CONTABLE = '1'
                // Para Poder Ejercer
                String sqlCLC = "SELECT NCTR_47, NCLC_43, APLICACION_CONTABLE FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) WHERE SICOP.FOLIO_SIAFF_112 = SIAFF.FOLIO_CLC AND NCTR_47 = '" + integracion + "' AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) WHERE NCTR_47 = '" + integracion + "' AND FOLIO_SIAFF_112 <> '0' AND DOC_HAPLICADO=1) BETWEEN  " + String.format("%.2f", numeMenorFor) + " AND " + String.format("%.2f", numeMayorFor) + " GROUP BY NCTR_47, NCLC_43, APLICACION_CONTABLE";
                pstmEncSICOP = conn.prepareStatement(sqlCLC);
                rsEncSICOP = pstmEncSICOP.executeQuery();
                System.out.println("Sql encabezado: " + sqlCLC);
                // Encabezado iguales
                if (rsEncSICOP.next()) {
                    clcSicop = rsEncSICOP.getString("NCLC_43");
                    aplicacionContable = rsEncSICOP.getString("APLICACION_CONTABLE");
                    if (aplicacionContable.equals("1")) {
                        // Detalles Dependiendo el Documento
                        camposDet = "SELECT SUBSTRING( dbo.CambiaEPPlurianual(EP), 0,56) AS EP, SUM(CONVERT(money,mimportemasiva)) AS mImporteNeto FROM v_AplicarIngresoAnexoDetalle WITH(NOLOCK) WHERE nFolioConsolidacion = ? GROUP BY SUBSTRING( dbo.CambiaEPPlurianual(EP), 0,56) ";
                        pstmntDetCons = conn.prepareStatement(camposDet);
                        pstmntDetCons.setString(1, nFolioConsolidacion);
                        rsDetalleCons = pstmntDetCons.executeQuery();
                        // Detalles de Documento Para Comparar Con SICOP
                        while (rsDetalleCons.next()) {
                            epDetSICOP = rsDetalleCons.getString("EP") != null ? rsDetalleCons.getString("EP").substring(0, 55) : "";
                            importeDet = rsDetalleCons.getDouble("mImporteNeto");
                            numeMenorDet = rsDetalleCons.getDouble("mImporteNeto") - centavos;
                            numeMayorDet = rsDetalleCons.getDouble("mImporteNeto") + centavos;
                            NumberFormat formatterDet = new DecimalFormat("###.##");
                            numeMenorForDet = Double.parseDouble(formatterDet.format(numeMenorDet));
                            numeMayorForDet = Double.parseDouble(formatterDet.format(numeMayorDet));
                            // Validacion con un centavo mas o menos en Detalles
                            // SICOP
                            String sql = ("SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END AS EP" + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet) + ") = '" + epDetSICOP + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet));
                            pstmDetSICOP = conn.prepareStatement(sql);
                            rsDetSICOP = pstmDetSICOP.executeQuery();
                            // Detalle Son Diferentes
                            if (!rsDetSICOP.next()) {
                                // Guardar Detalles Error
                                guardarDetalleDiferencia(conn, pstmDiferencia, "IntAnexo1", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, importeDet, "INGRESO", "Detalle Diferente Integracion");
                                guardarDatos = "noGuardarDetalle";
                                json.put("status", "errorDetalles");
                                conn.commit();
                            }
                        }
                        valor = "";
                        if (guardarDatos.equals("guardarDatos")) {
                            String strStatusSiaffIntegra = "";
                            boolean aplicado = false;
                            pstmEstatusSicopPagado = conn.prepareStatement(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, ISNULL(SIAFF.FECHA_PAGO,'-') AS FECHA_PAGO, SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC ");
                            pstmEstatusSicopPagado.setString(1, integracion);
                            rsEstatusSicopPagado = pstmEstatusSicopPagado.executeQuery();
                            if (rsEstatusSicopPagado.next()) {
                                strStatusSiaffIntegra = rsEstatusSicopPagado.getString("ESTATUS_CLC");
                                fechaUpdate = rsEstatusSicopPagado.getString("FECHA_PAGO");
                            }
                            if (strStatusSiaffIntegra.trim().equals("Pagada")) {
                                // APLICAR LAS SOLICITUDES EN LO INDIVIDUAL DEL
                                // ANEXO 1 INTEGRADAS EN CASO DE SI HABER
                                // APLICADO LA INTEGRACION
                                AccountingEngine motorContable = new AccountingEngine();
                                boolean integradas = false;
                                pstmListadoAnexosIntegrados = conn.prepareStatement("SELECT nFolio FROM tLayoutsCreadosAnexo1Header WITH(NOLOCK) WHERE sAuxiliarComodin = ?");
                                pstmListadoAnexosIntegrados.setString(1, integracion);
                                rsListadoAnexosIntegrados = pstmListadoAnexosIntegrados.executeQuery();
                                while (rsListadoAnexosIntegrados.next()) {
                                    pstmUpdateFechaAnexo1 = conn.prepareStatement("UPDATE tAnexo1Encabezado SET fAplicacion = ? WHERE nFolioAnexo1 = ?");
                                    pstmUpdateFechaAnexo1.setString(1, fechaUpdate);
                                    pstmUpdateFechaAnexo1.setInt(2, rsListadoAnexosIntegrados.getInt("nFolio"));
                                    pstmUpdateFechaAnexo1.executeUpdate();
                                    motorContable.makeAccountingApplication(conn, "ANEXO1", Integer.toString(rsListadoAnexosIntegrados.getInt("nFolio")), "tAnexo1Encabezado", "tAnexo1Detalle", "nFolioAnexo1");
                                    integradas = true;
                                }
                                aplicado = ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, "");
                                if (aplicado && integradas) {
                                    valor = "guardado";
                                    json.put("status", "guardado");
                                }
                                motorContable = null;
                            }
                        }
                        if (valor.equals("guardado")) {
                            conn.commit();
                        } else {
                            conn.rollback();
                        }
                    } else {
                        msnEnc = "No Esta Para Aplicar, Aplicacion Contable = 0";
                        guardarDetalleDiferencia(conn, pstmDiferencia, "IntAnexo1", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, mImporte, "INGRESO", msnEnc);
                        guardarDatos = "noGuardarDetalle";
                        json.put("status", "errorDetalles");
                        conn.commit();
                    }
                } else {
                    epDetSICOP = "";
                    clcSicop = "";
                    msnEnc = "Informacion No Encontrada en SICOP o Importe Diferente de Encabezado";
                    guardarDetalleDiferencia(conn, pstmDiferencia, "IntAnexo1", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, mImporte, "INGRESO", msnEnc);
                    json.put("status", "errorDetalles");
                    conn.commit();
                }
            }
        } catch (Exception e) {
            log.error("Error aplicando contablemente " + e, e);
            msnEnc = e.toString();
            try {
                json.put("status", integracion + " - " + msnEnc);
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
            try {
                guardarDetalleDiferencia(conn, pstmDiferencia, "IntAnexo1", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, mImporte, "INGRESO", msnEnc);
                conn.commit();
            } catch (Exception de) {
                log.warn("Error: guardar Detalle ", de);
                conn.rollback();
            }
        } finally {
            try {
                CloseObject.closeObject(pstmFoliosIntegraciones, false);
                CloseObject.closeObject(pstmEncSICOP, false);
                CloseObject.closeObject(pstmntDetCons, false);
                CloseObject.closeObject(pstmDetSICOP, false);
                CloseObject.closeObject(pstmDiferencia, false);
                CloseObject.closeObject(pstmEstatusSicopPagado, false);
                CloseObject.closeObject(pstmListadoAnexosIntegrados, false);
                CloseObject.closeObject(pstmUpdateFechaAnexo1, false);
                CloseObject.closeObject(rsFoliosIntegraciones, false);
                CloseObject.closeObject(rsEncSICOP, false);
                CloseObject.closeObject(rsDetalleCons, false);
                CloseObject.closeObject(rsDetSICOP, false);
                CloseObject.closeObject(rsEstatusSicopPagado, false);
                if (rsListadoAnexosIntegrados != null) {
                    rsListadoAnexosIntegrados.close();
                }
            } catch (Exception st) {
                log.warn("Error: cerrando PreparedStatement y ResultSet", st);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception con) {
                log.warn("Error: cerrando conexion", con);
            }
            pstmFoliosIntegraciones = null;
            pstmEncSICOP = null;
            pstmntDetCons = null;
            pstmDetSICOP = null;
            pstmDiferencia = null;
            pstmEstatusSicopPagado = null;
            pstmListadoAnexosIntegrados = null;
            pstmUpdateFechaAnexo1 = null;
            rsFoliosIntegraciones = null;
            rsEncSICOP = null;
            rsDetalleCons = null;
            rsDetSICOP = null;
            rsEstatusSicopPagado = null;
            rsListadoAnexosIntegrados = null;
        }
        return json;
    }

    public JSONObject aplicaIngresoRIF(Connection conn, double centavos) throws SQLException {
        PreparedStatement pstmFoliosIntegraciones = null, pstmEncSICOP = null, pstmntDetCons = null, pstmDetSICOP = null, pstmDiferencia = null, pstmEstatusSicopPagado = null, pstmListadoRIFIntegrados = null, pstmUpdateFechaRIF = null;
        ResultSet rsFoliosIntegraciones = null, rsEncSICOP = null, rsDetalleCons = null, rsDetSICOP = null, rsEstatusSicopPagado = null, rsListadoRIFIntegrados = null;
        String valor = "", nFolioConsolidacion = "", integracion = "", estatus = "", clcSicop = "", aplicacionContable = "", camposDet = "", epDetSICOP = "", guardarDatos = "guardarDatos", FechaPagado = "", msnEnc = "", fechaUpdate = "";
        double mImporte = 0.00, numeMenor, numeMayor, numeMenorFor, numeMayorFor, importeDet, numeMenorDet, numeMayorDet, numeMenorForDet, numeMayorForDet;
        CallableStatement cs = null;
        JSONObject json = new JSONObject();
        java.util.Date utilDate = new java.util.Date();
        long lnMilisegundos = utilDate.getTime();
        java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
        String[] fp = String.valueOf(sqlDate).split("-");
        FechaPagado = fp[0] + "-" + fp[1] + "-" + fp[2];
        try {
            json.put("status", "guardado");
            /**
             * Aplicar Ingreso de Recurso Fiscal **
             */
            pstmFoliosIntegraciones = conn.prepareStatement(" SELECT nFolioConsolidacion,Integracion,Aplicado,mImporte FROM v_AplicarRIFEncabezado WITH(NOLOCK) WHERE Aplicado='SIN APLICAR' ORDER BY nFolioConsolidacion");
            rsFoliosIntegraciones = pstmFoliosIntegraciones.executeQuery();
            while (rsFoliosIntegraciones.next()) {
                guardarDatos = "guardarDatos";
                nFolioConsolidacion = rsFoliosIntegraciones.getString("nFolioConsolidacion");
                integracion = rsFoliosIntegraciones.getString("Integracion");
                estatus = rsFoliosIntegraciones.getString("Aplicado");
                mImporte = Double.parseDouble(rsFoliosIntegraciones.getString("mImporte"));
                log.info("Object: {}", "[Integracion de RIF] Folio Consolidacion[" + nFolioConsolidacion + "] Integracion[" + integracion + "]  Estatus[" + estatus + "] Importe[" + String.format("%.2f", mImporte) + "]");
                numeMenor = mImporte - centavos;
                numeMayor = mImporte + centavos;
                NumberFormat formatter = new DecimalFormat("###.##");
                numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
                numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
                // Encabezado de SICOP dependiendo de los valores de encabezado
                // CXP para validar si son iguales, APLICACION_CONTABLE = '1'
                // Para Poder Ejercer
                String sqlCLC = "SELECT NCTR_47, NCLC_43, APLICACION_CONTABLE FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) WHERE SICOP.FOLIO_SIAFF_112 = SIAFF.FOLIO_CLC AND NCTR_47 = '" + integracion + "' AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) WHERE NCTR_47 = '" + integracion + "' AND FOLIO_SIAFF_112 <> '0' AND DOC_HAPLICADO=1) BETWEEN  " + String.format("%.2f", numeMenorFor) + " AND " + String.format("%.2f", numeMayorFor) + " GROUP BY NCTR_47, NCLC_43, APLICACION_CONTABLE";
                pstmEncSICOP = conn.prepareStatement(sqlCLC);
                rsEncSICOP = pstmEncSICOP.executeQuery();
                System.out.println("Sql encabezado: " + sqlCLC);
                // Encabezado iguales
                if (rsEncSICOP.next()) {
                    clcSicop = rsEncSICOP.getString("NCLC_43");
                    aplicacionContable = rsEncSICOP.getString("APLICACION_CONTABLE");
                    if (aplicacionContable.equals("1")) {
                        // Detalles Dependiendo el Documento
                        camposDet = "SELECT SUBSTRING(EP,0,56) AS EP, SUM(CONVERT(money,mimportemasiva)) AS mImporteNeto FROM v_AplicarRIFDetalle WITH(NOLOCK) WHERE nFolioConsolidacion = ? GROUP BY SUBSTRING(EP,0,56)";
                        pstmntDetCons = conn.prepareStatement(camposDet);
                        pstmntDetCons.setString(1, nFolioConsolidacion);
                        rsDetalleCons = pstmntDetCons.executeQuery();
                        // Detalles de Documento Para Comparar Con SICOP
                        while (rsDetalleCons.next()) {
                            epDetSICOP = rsDetalleCons.getString("EP") != null ? rsDetalleCons.getString("EP").substring(0, 55) : "";
                            importeDet = rsDetalleCons.getDouble("mImporteNeto");
                            numeMenorDet = rsDetalleCons.getDouble("mImporteNeto") - centavos;
                            numeMayorDet = rsDetalleCons.getDouble("mImporteNeto") + centavos;
                            NumberFormat formatterDet = new DecimalFormat("###.##");
                            numeMenorForDet = Double.parseDouble(formatterDet.format(numeMenorDet));
                            numeMayorForDet = Double.parseDouble(formatterDet.format(numeMayorDet));
                            // Validacion con un centavo mas o menos en Detalles
                            // SICOP
                            String sql = (// +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                            // END
                            // +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                            "SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END " + " AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 " + " END AS EP" + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCTR_47 = '" + integracion + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet) + ") = '" + epDetSICOP + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + String.format("%.2f", numeMenorForDet) + " AND " + String.format("%.2f", numeMayorForDet));
                            pstmDetSICOP = conn.prepareStatement(sql);
                            rsDetSICOP = pstmDetSICOP.executeQuery();
                            // Detalle Son Diferentes
                            if (!rsDetSICOP.next()) {
                                // Guardar Detalles Error
                                guardarDetalleDiferencia(conn, pstmDiferencia, "IntRIF", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, importeDet, "INGRESO", "Detalle Diferente Integracion");
                                guardarDatos = "noGuardarDetalle";
                                json.put("status", "errorDetalles");
                                conn.commit();
                            }
                        }
                        valor = "";
                        if (guardarDatos.equals("guardarDatos")) {
                            String strStatusSiaffIntegra = "";
                            boolean aplicado = false;
                            pstmEstatusSicopPagado = conn.prepareStatement(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, ISNULL(SIAFF.FECHA_PAGO,'-') AS FECHA_PAGO, SIAFF.ESTATUS_CLC " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = ? " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC ");
                            pstmEstatusSicopPagado.setString(1, integracion);
                            rsEstatusSicopPagado = pstmEstatusSicopPagado.executeQuery();
                            if (rsEstatusSicopPagado.next()) {
                                strStatusSiaffIntegra = rsEstatusSicopPagado.getString("ESTATUS_CLC");
                                fechaUpdate = rsEstatusSicopPagado.getString("FECHA_PAGO");
                            }
                            if (strStatusSiaffIntegra.trim().equals("Pagada")) {
                                // APLICAR LAS SOLICITUDES EN LO INDIVIDUAL DEL
                                // RIF INTEGRADAS EN CASO DE SI HABER APLICADO
                                // LA INTEGRACION
                                AccountingEngine motorContable = new AccountingEngine();
                                boolean integradas = false;
                                pstmListadoRIFIntegrados = conn.prepareStatement("SELECT nFolio FROM tLayoutsCreadosRegistroIngresoEncabezado WITH(NOLOCK) WHERE sAuxiliarComodin = ?");
                                pstmListadoRIFIntegrados.setString(1, integracion);
                                rsListadoRIFIntegrados = pstmListadoRIFIntegrados.executeQuery();
                                while (rsListadoRIFIntegrados.next()) {
                                    pstmUpdateFechaRIF = conn.prepareStatement("UPDATE tRegistroIngresoEncabezado SET fAplicacion = ? WHERE nFolioRegistroIngreso = ?");
                                    pstmUpdateFechaRIF.setString(1, fechaUpdate);
                                    pstmUpdateFechaRIF.setInt(2, rsListadoRIFIntegrados.getInt("nFolio"));
                                    pstmUpdateFechaRIF.executeUpdate();
                                    motorContable.makeAccountingApplication(conn, "REGISTROINGRESO", Integer.toString(rsListadoRIFIntegrados.getInt("nFolio")), "tRegistroIngresoEncabezado", "tRegistroIngresoDetalle", "nFolioRegistroIngreso");
                                    integradas = true;
                                }
                                aplicado = ConsolidacionRGManager.aplicaConsolidacionRelacionGastos(conn, integracion, "");
                                if (aplicado && integradas) {
                                    valor = "guardado";
                                    json.put("status", "guardado");
                                    String query = "{call sp_l_actualizaFoliosRIF( ? )}";
                                    cs = conn.prepareCall(query);
                                    cs.setString(1, integracion);
                                    cs.executeUpdate();
                                    if (cs.getUpdateCount() > 0)
                                        System.out.println("Se actualizó folio SICOP y SIAFF.");
                                    else
                                        throw new Exception("No se actualizao Folio SICOP y SIAFF en la solicitud de registro.");
                                }
                                motorContable = null;
                            }
                        }
                        if (valor.equals("guardado")) {
                            conn.commit();
                        } else {
                            conn.rollback();
                        }
                    } else {
                        msnEnc = "No Esta Para Aplicar, Aplicacion Contable = 0";
                        guardarDetalleDiferencia(conn, pstmDiferencia, "IntRIF", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, mImporte, "INGRESO", msnEnc);
                        guardarDatos = "noGuardarDetalle";
                        json.put("status", "errorDetalles");
                        conn.commit();
                    }
                } else {
                    epDetSICOP = "";
                    clcSicop = "";
                    msnEnc = "Informacion No Encontrada en SICOP o Importe Diferente de Encabezado";
                    guardarDetalleDiferencia(conn, pstmDiferencia, "IntRIF", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, mImporte, "INGRESO", msnEnc);
                    json.put("status", "errorDetalles");
                    conn.commit();
                }
            }
        } catch (Exception e) {
            log.error("Error aplicando contablemente " + e, e);
            msnEnc = e.toString();
            try {
                json.put("status", integracion + " - " + msnEnc);
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
            try {
                guardarDetalleDiferencia(conn, pstmDiferencia, "IntRIF", Integer.parseInt(nFolioConsolidacion, 10), integracion, clcSicop, FechaPagado, epDetSICOP, mImporte, "INGRESO", msnEnc);
                conn.commit();
            } catch (Exception de) {
                log.warn("Error: guardar Detalle ", de);
                conn.rollback();
            }
        } finally {
            try {
                CloseObject.closeObject(pstmFoliosIntegraciones, false);
                CloseObject.closeObject(pstmEncSICOP, false);
                CloseObject.closeObject(pstmntDetCons, false);
                CloseObject.closeObject(pstmDetSICOP, false);
                CloseObject.closeObject(pstmDiferencia, false);
                CloseObject.closeObject(pstmEstatusSicopPagado, false);
                CloseObject.closeObject(pstmListadoRIFIntegrados, false);
                CloseObject.closeObject(pstmUpdateFechaRIF, false);
                CloseObject.closeObject(rsFoliosIntegraciones, false);
                CloseObject.closeObject(rsEncSICOP, false);
                CloseObject.closeObject(rsDetalleCons, false);
                CloseObject.closeObject(rsDetSICOP, false);
                CloseObject.closeObject(rsEstatusSicopPagado, false);
                CloseObject.closeObject(rsListadoRIFIntegrados, false);
                CloseObject.closeObject(cs, false);
                CloseObject.closeObject(conn, false);
            } catch (Exception st) {
                log.warn("Error: cerrando PreparedStatement, ResultSet, Connection", st);
            }
        }
        return json;
    }

    public String obtieneUR(Connection conn, String folioPagado) throws SQLException {
        PreparedStatement pstmObtieneUR = null, pstmTipoPago = null;
        ResultSet rsObtieneUR = null, rsTipoPago = null;
        String UR = "", tipoPago = "", query = "", caNoContrarrecibo = "";
        try {
            pstmTipoPago = conn.prepareStatement("SELECT cTipoPago, caNoContrarrecibo FROM tPagadoEncabezado WITH (NOLOCK) WHERE nFolioPagado = " + folioPagado);
            rsTipoPago = pstmTipoPago.executeQuery();
            if (rsTipoPago.next()) {
                tipoPago = rsTipoPago.getString("cTipoPago");
                caNoContrarrecibo = rsTipoPago.getString("caNoContrarrecibo");
            }
            if ("PAGOOBRA".equals(tipoPago))
                query = "SELECT cUnidadResponsable FROM tPAGOOBRAEncabezado WITH (NOLOCK) WHERE caNoContrarrecibo = '" + caNoContrarrecibo + "'";
            else if ("PAGODIVERSO".equals(tipoPago))
                query = "SELECT cUnidadResponsable FROM tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE caNoContrarrecibo = '" + caNoContrarrecibo + "'";
            else if ("AJENAS".equals(tipoPago))
                query = "SELECT cUnidadResponsable FROM tOperAjenasEncabezado WITH (NOLOCK) WHERE caNoContrarrecibo = '" + caNoContrarrecibo + "'";
            else if ("FEDERALIZADO".equals(tipoPago))
                query = "SELECT cUnidadResponsable FROM tPAGOFEDERALIZADOEncabezado WITH (NOLOCK) WHERE caNoContrarrecibo = '" + caNoContrarrecibo + "'";
            else if ("PAGODIRECTO".equals(tipoPago))
                query = "SELECT cUnidadResponsable FROM tPagoDirectoEncabezado WITH (NOLOCK) WHERE caNoContrarrecibo = '" + caNoContrarrecibo + "'";
            else if ("RELACIONGASTOS".equals(tipoPago))
                query = "SELECT cUnidadResponsable FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE caNoContrarrecibo = '" + caNoContrarrecibo + "'";
            else if ("NOMINA".equals(tipoPago))
                query = "SELECT cUnidadResponsable FROM tNOMINAEncabezado WITH (NOLOCK) WHERE caNoContrarrecibo = '" + caNoContrarrecibo + "'";
            pstmObtieneUR = conn.prepareStatement(query);
            rsObtieneUR = pstmObtieneUR.executeQuery();
            if (rsObtieneUR.next())
                UR = rsObtieneUR.getString("cUnidadResponsable");
        } catch (Exception e) {
            log.error("Error obteniendo la UR del Pagado." + e, e);
            e.printStackTrace();
        } finally {
            try {
                CloseObject.closeObject(pstmTipoPago, false);
                CloseObject.closeObject(pstmObtieneUR, false);
                CloseObject.closeObject(rsObtieneUR, false);
                CloseObject.closeObject(rsTipoPago, false);
            } catch (Exception st) {
                log.warn("Error: cerrando PreparedStatement y ResultSet", st);
            }
        }
        return UR;
    }

    private static boolean existeEjercido(Connection conn, String caNoContrarrecibo) throws Exception {
        String sql = " SELECT	COUNT(*) as existeEjercido " + "  FROM	tEjercidoEncabezado WITH(nolock) " + " WHERE	caNoContrarrecibo = '" + caNoContrarrecibo + "' " + "   AND  cDocumentoHaplicado = 'S'";
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(sql);
            int total = 0;
            if (rs.next())
                total = rs.getInt(1);
            return total > 0;
        } finally {
            CloseObject.closeObject(stmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    private static void insertaTiendaDigital(Connection conn) throws Exception {
        String query = "insert into CLC_SICOP_PAGO (COD_SEMARNAT_2, NO_DOCTO) " + " SELECT  NCTR_47, SOLP_48 FROM CLC_SICOP (NOLOCK) WHERE NCTR_47 NOT IN (SELECT COD_SEMARNAT_2 FROM CLC_SICOP_PAGO (NOLOCK)) AND NCTR_47 <> 'X'";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(query);
            int i = pst.executeUpdate();
            log.debug("Object: {}", "Se insertaron " + i + " registros en CLC_SICOP_PAGO ");
        } finally {
            CloseObject.closeObject(pst, false);
        }
    }

    private static boolean existePagadoAplicado(Connection conn, String caNoContrarrecibo) throws Exception {
        String sql = " SELECT	COUNT(*) as existeEjercido FROM	tPagadoEncabezado WITH(nolock) WHERE rtrim(ltrim(caNoContrarrecibo)) = rtrim(ltrim('" + caNoContrarrecibo + "'))  AND  cDocumentoHaplicado = 'S'";
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(sql);
            int total = 0;
            if (rs.next())
                total = rs.getInt(1);
            return total > 0;
        } finally {
            CloseObject.closeObject(stmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    private static boolean existePagado(Connection conn, String caNoContrarrecibo) throws Exception {
        String sql = " SELECT COUNT(*) as existeEjercido FROM tPagadoEncabezado WITH(nolock)  WHERE	rtrim(ltrim(caNoContrarrecibo)) = rtrim(ltrim('" + caNoContrarrecibo + "'))";
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(sql);
            int total = 0;
            if (rs.next())
                total = rs.getInt(1);
            return total > 0;
        } finally {
            CloseObject.closeObject(stmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static void cxpParaRechazoBancario(Connection conn, String notCuentaPorPagar) throws Exception {
        PreparedStatement ps = null;
        /*-5 ENVIADO SICOP, RECHAZO BANCARIO PARA RG*/
        String query = "UPDATE tRELACIONGASTOSEncabezado SET nEnviadoSICOP = -5 WHERE caNoContrarrecibo IN " + notCuentaPorPagar;
        try {
            ps = conn.prepareStatement(query);
            log.debug("Object: {}", "Contrarecibo enviado a rechazo bancario " + notCuentaPorPagar + ", " + query);
            int afectados = ps.executeUpdate();
            log.debug("Object: {}", "Se actualizaron " + afectados);
            conn.commit();
        } catch (Exception e) {
            log.info("Error occurred", "Error de Aplicación " + e.getMessage());
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static String esComprobacion(Connection conn, String strCaNoContrarrecibo) {
        PreparedStatement pstmTipoPago = null;
        ResultSet rsTipoPago = null;
        String esComprobacion = "";
        try {
            pstmTipoPago = conn.prepareStatement("SELECT TOP 1 CASE WHEN EV.ID_DESTINO_GASTO IS NULL THEN 'NO' ELSE 'SI' END AS esComprobacion FROM tRELACIONGASTOSEncabezado ENC\r\n" + "LEFT JOIN tCatalogoEventosComprobacion EV ON ENC.ID_DESTINO_GASTO = EV.ID_DESTINO_GASTO\r\n" + "WHERE caNoContrarrecibo = '" + strCaNoContrarrecibo + "'");
            rsTipoPago = pstmTipoPago.executeQuery();
            if (rsTipoPago.next()) {
                esComprobacion = rsTipoPago.getString("esComprobacion");
            }
        } catch (Exception e) {
            log.error("Error obteniendo el Destino Gasto del pago" + e, e);
            e.printStackTrace();
        } finally {
            try {
                CloseObject.closeObject(pstmTipoPago, false);
                CloseObject.closeObject(rsTipoPago, false);
            } catch (Exception st) {
                log.warn("Error: cerrando PreparedStatement y ResultSet", st);
            }
        }
        return esComprobacion;
    }

    public static int existeFacturaPPD(Connection conn, String strCaNoContrarrecibo) throws Exception {
        int existe = 0;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT  COUNT(*) tienePPD" + " FROM v_AplicarEjercidoPagadoEncabezado aplica " + " INNER JOIN tPagoFactura factura (NOLOCK) " + "		ON aplica.nFolio = factura.nFolioPago AND aplica.cTipoPago = factura.cTipoPago " + " WHERE cMetodoPago = 'PPD' AND caNoContrarrecibo = ? ";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, strCaNoContrarrecibo);
            rs = pst.executeQuery();
            if (rs.next()) {
                existe = rs.getInt(1);
            }
            return existe;
        } finally {
            CloseObject.closeObject(pst, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static void enviarCorreo(Connection conn, String cxp) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String nombre = "", puesto = "", correo = "";
        String query = "SELECT TOP 1 COALESCE(COBRANZA.cCorreoNotificaciones, dEmailFiscal, 'bherreraa@conafor.gob.mx') correo, " + " 	COALESCE(ISNULL(COBRANZA.cNombreResponsable, '') + ' ' + ISNULL(Cobranza.cApPaternoResponsable,'') + ' ' + ISNULL(Cobranza.cApMaternoResponsable,''), " + " 	ISNULL(dNombre, '') + ' ' + ISNULL(dApellidoPaterno,'') + ' ' + ISNULL(dApellidoMaterno,'')) NombreCorreo, cCargoResponsable " + " FROM v_pagosBeneficiarios2 PAGOS " + " INNER JOIN tBeneficiario BEN (NOLOCK) " + " 	ON BEN.dRFC = PAGOS.RFC " + " LEFT JOIN tBeneficiarioCobranza COBRANZA (NOLOCK) " + " 	ON COBRANZA.dRFC = BEN.dRFC " + " WHERE cxp = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, cxp);
            rs = pst.executeQuery();
            if (rs.next()) {
                correo = rs.getString(1);
                nombre = rs.getString(2);
                puesto = rs.getString(3);
            }
            String cuerpoCorreo = generaCuerpoCorreo(conn, cxp, nombre, puesto);
            MailSender.enviaCorreoCNF(correo, cuerpoCorreo, "Solicitud de Comprobante de Pago CONAFOR");
        } finally {
            CloseObject.closeObject(pst, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static String generaCuerpoCorreo(Connection conn, String cxp, String nombreResponsable, String puestoResponsable) throws Exception {
        String mailBody = "<html>";
        PreparedStatement psFacturas = null;
        ResultSet rsFacturas = null;
        String query = "SELECT  nFolioPago, caNoContrarrecibo, cfactura, dFechaFactura, mimporteconiva, cRazonSocial " + " FROM  v_AplicarEjercidoPagadoEncabezado aplica " + " INNER JOIN tPagoFactura factura " + " ON aplica.nFolio = factura.nFolioPago " + " AND aplica.cTipoPago = factura.cTipoPago " + " WHERE cMetodoPago = 'PPD' AND caNoContrarrecibo = ?";
        try {
            mailBody += "<head>";
            mailBody += "<meta charset=\"UTF-8\">";
            mailBody += "<style type=\"text/css\">";
            mailBody += "body {";
            mailBody += "	font-family: verdana, arial, sans-serif;";
            mailBody += "	font-size: 12px;";
            mailBody += "}";
            mailBody += "table {";
            mailBody += "	font-size: 12px;";
            mailBody += "	color: #333333;";
            mailBody += "	border-width: 1px;";
            mailBody += "	border-color: #666666;";
            mailBody += "	border-collapse: collapse;";
            mailBody += "}";
            mailBody += "table th {";
            mailBody += "	border-width: 1px;";
            mailBody += "	padding: 8px;";
            mailBody += "	border-style: solid;";
            mailBody += "	border-color: #666666;";
            mailBody += "	background-color: #dedede;";
            mailBody += "}";
            mailBody += "table td {";
            mailBody += "	border-width: 1px;";
            mailBody += "	padding: 8px;";
            mailBody += "	border-style: solid;";
            mailBody += "	border-color: #666666;";
            mailBody += "	background-color: #ffffff;";
            mailBody += "}";
            mailBody += "</style>";
            mailBody += "</head>";
            mailBody += "<body>";
            mailBody += "	<b>" + nombreResponsable + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + puestoResponsable + "</b>";
            mailBody += "	<br>";
            mailBody += "<p> Por medio de la presente le informo que se han realizado exitosamente los siguientes pagos a su favor";
            mailBody += " por lo que se solicita amablemente generar el complemento de pago de dichos documentos para así dar";
            mailBody += " cumplimiento a los tiempos y documentacion requerida SAT </p>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "	<table>";
            mailBody += "		<thead>";
            mailBody += "			<tr>";
            mailBody += "				<th>Documento CONAFOR</th>";
            mailBody += "				<th>Razon social</th>";
            mailBody += "				<th>Fecha Factura</th>";
            mailBody += "				<th>UUID</th>";
            mailBody += "				<th>Monto Factura</th>";
            mailBody += "			</tr>";
            mailBody += "		</thead>";
            mailBody += "		<tbody>";
            psFacturas = conn.prepareStatement(query);
            psFacturas.setString(1, cxp);
            rsFacturas = psFacturas.executeQuery();
            while (rsFacturas.next()) {
                mailBody += "<tr>";
                mailBody += "\n<td>" + rsFacturas.getString("caNoContrarrecibo") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("cRazonSocial") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("dFechaFactura") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("cfactura") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("mimporteconiva") + "</td>";
                mailBody += "</tr>";
            }
            mailBody += "</tbody>";
            mailBody += "</table>";
            mailBody += "<br />";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Esta es una Notificacion Automatica por lo que le suplicamos no responda directamente sobre este correo. Responda a la persona indicada.";
            mailBody += "	</p>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } finally {
            CloseObject.closeObject(psFacturas, false);
            CloseObject.closeObject(rsFacturas, false);
        }
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }
}
