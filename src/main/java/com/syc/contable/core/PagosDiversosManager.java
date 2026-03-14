package com.syc.contable.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import com.syc.contable.caja.core.CajaManager;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.impl.EgresoPAGODIVERSODetalle;
import com.syc.egresos.core.impl.EgresoPAGODIVERSOEncabezado;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagosDiversosManager {

    public PagosDiversosManager() {
        super();
    }

    private static final Logger log = LoggerFactory.getLogger(CajaManager.class);

    private static final String unidadContable = "RHQ";

    public static ArrayList<String> BuscaCompromisos(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] arrFolios = listaIds.split(",");
        String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
        String[] arrFechas = listaFechas.split(",");
        String[] arrLeyendas = listaLeyendas.split(",");
        int intIndice = -1;
        BigDecimal total = new BigDecimal(0);
        BigDecimal retenciones = new BigDecimal(0);
        BigDecimal revisarTotal = new BigDecimal(0);
        BigDecimal revisarRete = new BigDecimal(0);
        try {
            rs = generaEncabezadoPagoDiverso(conn, listaIds, 5, 3);
            while (rs.next()) {
                String nFolio, nFolioCompromiso = rs.getString(1);
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String vtipoPago = rs.getString(15).trim();
                String vcReferencia1 = rs.getString(23).trim();
                String vcReferencia2 = rs.getString(24).trim();
                String vcCtaBancaria = arrCuentasBancarias[intIndice].trim().trim();
                String vcBenef = rs.getString(18).trim();
                String vcLeyenda = arrLeyendas[intIndice].trim().trim();
                String vRFC = rs.getString(20).trim();
                String vcEstatus = rs.getString(42).trim();
                String vIntermFin = rs.getString(43).trim();
                String vnClaveAMF = rs.getString(44).trim();
                if (!"".equals(vnClaveAMF)) {
                    vcBenef = "S24676";
                    vcCtaBancaria = "22800100000100";
                    vcLeyenda = "3";
                    vRFC = "6001";
                    vtipoPago = "2";
                }
                if ("Operada Pagada".equals(vcEstatus)) {
                    vcCtaBancaria = vIntermFin;
                }
                if (!"".equals(vcReferencia1) && "".equals(vcReferencia2)) {
                    vcLeyenda = "0";
                }
                // A
                String // A
                // B
                encabezado = // C
                rs.getString(2) + "," + arrFechas[intIndice].trim() + "," + // D
                rs.getString(4).trim() + "," + // E
                rs.getString(5).trim() + "," + // F
                rs.getString(6).trim() + "," + // G
                rs.getString(7).trim() + "," + // H
                rs.getString(8).trim() + "," + // I
                rs.getString(9).trim() + "," + // J
                rs.getString(10).trim() + "," + // K
                rs.getString(11).trim() + "," + // L
                rs.getString(12).trim() + "," + // M
                rs.getString(13).trim() + "," + // N
                rs.getString(14).trim() + "," + // O
                vtipoPago + "," + // P
                rs.getString(16).trim() + "," + // Q
                vcLeyenda + "," + // R
                vcBenef + "," + // S
                vcCtaBancaria + "," + // T
                vRFC + "," + // U
                rs.getString(21).trim() + "," + // V
                rs.getString(22).trim() + "," + // W
                rs.getString(23).trim() + "," + // X
                rs.getString(24).trim() + "," + // Y
                rs.getString(25).trim().replaceAll("[\r\n]{2,}", " ") + "," + // Z
                rs.getString(26).trim() + "," + // AA
                rs.getString(27).trim() + "," + // AB
                StringUtils.trimToEmpty(rs.getString(28)) + "," + // AC
                StringUtils.trimToEmpty(rs.getString(29)) + "," + // AD
                StringUtils.trimToEmpty(rs.getString(30)) + "," + // AE
                StringUtils.trimToEmpty(rs.getString(31)) + "," + // AF
                rs.getString(32).trim() + "," + // AG
                rs.getString(33).trim() + "," + // AH
                rs.getString(34).trim() + "," + // AI
                rs.getString(35).trim() + "," + // AJ
                rs.getString(36).trim() + "," + // AK
                rs.getString(37).trim() + "," + // AL
                rs.getString(38).trim() + "," + // AM
                rs.getString(39).trim() + "," + // AN
                rs.getString(40).trim() + "," + rs.getString(41);
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                // Aqui grabamos dentro de layouts creados encabezado
                String[] f = arrFechas[intIndice].trim().split("/");
                String fecha = f[2] + "-" + f[1] + "-" + f[0];
                insertaLayoutEncabezadoPD(conn, fecha, arrLeyendas[intIndice].trim().trim(), arrCuentasBancarias[intIndice].trim().trim(), sUsuario, listaIds, vcBenef, "1", vRFC);
                rs2 = generaDetallePagoDiversoComprometido(conn, rs.getString(1));
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int posicionColumna = 1; posicionColumna < 43; posicionColumna++) {
                        if (posicionColumna == 28) {
                            revisarTotal = rs2.getBigDecimal(posicionColumna);
                            total = total.add(revisarTotal);
                            if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                                throw new Exception("El importe de uno de los registros del layout es menor que cero. Revise los pagos.");
                            } else {
                                String valor = (StringUtils.isBlank(rs2.getString(posicionColumna)) ? "" : rs2.getString(posicionColumna).trim().replaceAll("[\r\n]{2,}", " "));
                                detalle.append(token).append(valor);
                                token = ",";
                            }
                        } else if (posicionColumna >= 33 && posicionColumna <= 37) {
                            //Suma el importe de las retenciones
                            revisarRete = rs2.getBigDecimal(posicionColumna);
                            retenciones = retenciones.add(revisarRete);
                            String valor = (StringUtils.isBlank(rs2.getString(posicionColumna)) ? "" : rs2.getString(posicionColumna).trim().replaceAll("[\r\n]{2,}", " "));
                            detalle.append(token).append(valor);
                            token = ",";
                        } else {
                            String valor = (StringUtils.isBlank(rs2.getString(posicionColumna)) ? "" : rs2.getString(posicionColumna).trim().replaceAll("[\r\n]{2,}", " "));
                            detalle.append(token).append(valor);
                            token = ",";
                        }
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                // Aqui grabamos dentro de layouts creados detalle
                insertaLayoutDetallePD(conn, nFolioCompromiso, listaIds);
            }
            //Valida que el total del Layout sea igual a los pagos
            validarTotalLayout(conn, total, listaIds);
            //Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetenciones(conn, retenciones, listaIds);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntD);
        }
        return arrListaComp;
    }

    public static ResultSet generaEncabezadoPagoDiverso(Connection conn, String listaIds, int origen, int tipo) throws Exception {
        PreparedStatement pstmntH = null;
        ResultSet rs = null;
        // PSC encabezado SP
        StringBuilder sql = new StringBuilder();
        /* 1 */
        sql.append(" SELECT distinct tCE.nFolioPagoDiverso, ");
        /* 2 */
        sql.append(" 'H' AS Header, ");
        /* 3 */
        sql.append(" CONVERT(nvarchar(10), tCE.fAplicacion,103), ");
        /* 4 */
        sql.append(" CONVERT(nvarchar(10), tCE.fAplicacion,103) ,");
        /* 5 */
        sql.append(" tCE.cRamo,");
        /* 6 */
        sql.append(" tCE.cRamo,");
        /* 7 */
        sql.append(" tCE.cRamo, ");
        /* 8 */
        sql.append("'" + unidadContable + "' as ur,");
        /* 9 */
        sql.append("'" + unidadContable + "' as ur2,");
        /* 10 */
        sql.append("'" + unidadContable + "' as ur3, ");
        /* 11 */
        sql.append(" 'N' ID_TIPO_MOVIMIENTO, ");
        /* 12 */
        sql.append(origen + "  AS OrigenPpto,");
        /* 13 */
        sql.append(tipo + " AS TipoSol, ");
        /* 14 */
        sql.append(" 'MXN' TipoMoneda , ");
        /* 15 */
        sql.append(" '1' TipoCambio, ");
        /* 16 */
        sql.append(" '1' TIPO_PAGO, ");
        /* 17 */
        sql.append(" 'PENDIENTE' CveLeyenda, ");
        /* 18 */
        sql.append(" case cEstatus when 'Operada Pagada' then cpd.cCampoAdi3 else ISNULL(BB.CBEN, isnull(B.CBEN,'')) end, ");
        /* 19 */
        sql.append(" 'Cuenta' CUENTA_BANCARIA, ");
        /* 20 */
        sql.append(" case cEstatus when 'Operada Pagada' then cpd.cCampoAdi2 else rtrim(ISNULL(SD.cIdRFCSesion, tCE.RFC)) end, ");
        /* 21 */
        sql.append(" 'FAC', ");
        /* 22 */
        sql.append(" '' FechaReferencia, ");
        /* 23 */
        sql.append(" COALESCE(ta.nClaveAMF, (select 'SEDENA' FROM pContratoDiverso cont WITH (NOLOCK) where cIdTipoContratoDiverso = 7 and tCE.cfoliopagodiverso = cont.cIdContrato ), isnull(nNumIdentificador,'')) Referencia1, ");
        /* 24 */
        sql.append(" isnull(ta.numFolioAMF, '') Referencia2, ");
        /* 25 */
        sql.append(" LEFT(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LTRIM(RTRIM(tCE.cConcepto)), CHAR(160), ' '), '  ', ' ') , ',', ''), CHAR(10), ''),CHAR(9), ''),CHAR(13), ''),CHAR(34), '') , 70), ");
        /* 26 */
        sql.append(" '' NotasReverso, ");
        /* 27 */
        sql.append(" isnull(ta.nClaveAMF, '') AMF, ");
        /* 28 */
        sql.append(" rtrim(DC.caNoContrarrecibo) NO_ACMI, ");
        /* 29 */
        sql.append(" rtrim(DC.caNoContrarrecibo) AuxiliarComodin, ");
        /* 30 */
        sql.append(" rtrim(DC.caNoContrarrecibo) CTR , ");
        /* 31 */
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_ISR, 0)) ISR, ");
        /* 32 */
        sql.append(" CONVERT(decimal(17, 2), isNull(DC.DCD_IVADES,0) + ISNULL(DC.DCD_CONTRIBUCION, 0)) RETIVA, ");
        /* 33 */
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL5, 0)) R5MILLAR, ");
        /* 34 */
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL2, 0)) R2MILLAS, ");
        /* 35 */
        sql.append(" CASE when DC.DCD_OTRAS_RET = 0 or DC.DCD_OTRAS_RET is null THEN '0' ELSE CONVERT(varchar(20), DC.DCD_OTRAS_RET) end OTRASRET, ");
        /* 36 */
        sql.append(" CASE when DC.DCD_PENALIZACION = 0 or DC.DCD_PENALIZACION is null THEN '0' else CONVERT(varchar (20),DC.DCD_PENALIZACION) end PENALIZA, ");
        /* 37 */
        sql.append(" '0' CONTRIB, ");
        /* 38 */
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_IVA, 0)) iva,  ");
        /* 39 */
        sql.append(" CONVERT(decimal(17, 2), tCE.mAmortizacionAnticipo - (tce.mAmortizacionAnticipo / ( 1 + (mImporteIVA * .01)))) IVAANT, ");
        /* 40 */
        sql.append(" '' FolioDC, ");
        /* 41 */
        sql.append(" 'NA' ID_DESTINO_GASTO,  ");
        /* 42 */
        sql.append(" isnull(cpd.cEstatus, ''), ");
        /* 43 */
        sql.append(" isnull(cpd.cCampoAdi4, ''), ");
        /* 44 */
        sql.append(" isnull(COALESCE(ta.nClaveAMF,(select cIdTipoContratoDiverso FROM pContratoDiverso cont WITH (NOLOCK) WHERE cIdTipoContratoDiverso = 7 and tCE.cfoliopagodiverso = cont.cIdContrato )), '')  ");
        sql.append(" FROM tPAGODIVERSOEncabezado tCE (NOLOCK)");
        sql.append(" LEFT JOIN pContratoDiversoSesion sd (NOLOCK) ON tCE.cFolioPAGODIVERSO = sd.cIdContrato and bActivo = 1 AND sd.fSesionVigencia = (select convert(date, isnull(min(pcs.fSesionVigencia), '1900-01-01')) from pContratoDiversoSesion pcs WITH (NOLOCK) where pcs.cIdContrato = sd.cIdContrato and bActivo = 1 and pcs.fSesionVigencia >= convert(date, GETDATE())) ");
        sql.append(" LEFT JOIN tBeneficiario B (NOLOCK) ON tce.RFC = B.dRFC ");
        sql.append(" LEFT JOIN tBeneficiario BB (NOLOCK) ON BB.dRFC = sd.cIdRFCSesion ");
        sql.append(" LEFT JOIN tPagoAMF ta (NOLOCK) ON tCE.NumPagoAMF = ta.numPagoAMF ");
        sql.append(" LEFT JOIN pCatalogoTipoDocumento CTD (NOLOCK) ON tCE.cIdTipoDocumento = CTD.cIdTipoDocumento ");
        sql.append(" INNER JOIN v_pagosDocComprobatoria DC (NOLOCK) ON DC.caNoContrarrecibo =tCE.caNoContrarrecibo AND DC.cTipoPago = 'PAGODIVERSO'  ");
        sql.append(" LEFT JOIN tCadenasPDetalle cpd (NOLOCK) ON cpd.caNoContrarrecibo = tCE.caNoContrarrecibo ");
        sql.append(" WHERE tCE.nFolioPagoDiverso in (" + listaIds + ") ");
        sql.append("  order by tCE.nFolioPagoDiverso");
        pstmntH = conn.prepareStatement(sql.toString());
        rs = pstmntH.executeQuery();
        return rs;
    }

    public static ResultSet generaDetallePagoDiversoComprometido(Connection conn, String folio) throws Exception {
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        StringBuilder sql2 = new StringBuilder();
        /* A */
        sql2.append("SELECT	DISTINCT '1' ID_EVENTO, ");
        /* B */
        sql2.append("     	'24.0.001' EVENTO, ");
        /* C */
        sql2.append("     	ltrim(TCEP.cRamo) ID_RAMO_ML,");
        /* D */
        sql2.append("'" + unidadContable + "',");
        /* E */
        sql2.append("     	TCEP.aEjercicioFiscal, ");
        /* F */
        sql2.append("     	TCEP.cGrupoFuncional, ");
        /* G */
        sql2.append("     	tCEP.cFuncion, ");
        /* H */
        sql2.append("     	tCEP.cSubFuncion, ");
        /* I */
        sql2.append("     	CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral,");
        /* J */
        sql2.append("     	tCEP.cActividadInstitucional,");
        /* K */
        sql2.append("     	tCEP.cProgramaPresupuestario,");
        /* L */
        sql2.append("     	ltrim(substring(cpartida,1,1)) CCAP_157,");
        /* M */
        sql2.append("     	substring(cpartida,2,1) CCON_158, ");
        /* N */
        sql2.append("     	substring(cpartida,3,1) CPARG_300,");
        /* O */
        sql2.append("     	substring(cpartida,4,2) CPAR_159,");
        /* P */
        sql2.append("     	tCEP.cTipoGasto,");
        /* Q */
        sql2.append("     	tCEP.cFuenteFinanciamiento, ");
        /* R */
        sql2.append("     	tCEP.cEntidadFederativa, ");
        /* S */
        sql2.append("     	SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) AS cCartera,  ");
        /* T */
        sql2.append("     	'0000000000',");
        /* U */
        sql2.append("     	'00' CCOP_163,");
        /* V */
        sql2.append("     	'000' PL,");
        /* W */
        sql2.append("     	'000' OFI,");
        /* X */
        sql2.append("     	'00000' AUX1,");
        /* Y */
        sql2.append("     	'00000' AUX2,");
        /* Z */
        sql2.append("     	'0000000000' AUX3,");
        /* AA */
        sql2.append("     	(SELECT nFolioAutSICOP FROM tCompromisoEncabezado CompEnc WITH (NOLOCK) WHERE nFolioCompromiso = ( SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = TPDE.cFolioPAGODIVERSO AND cDocumentoHaplicado = 'S' AND nFolioAutSICOP <> '-1' AND nFolioAutSICOP IS NOT NULL ) ) AS NCOM_35, ");
        /* AB */
        sql2.append("     	CONVERT(decimal(17,2),SUM(tpdd.mImporteNeto))  Monto,");
        /* AC */
        sql2.append("     	TPDD.cMes MES_149, ");
        /* AD */
        sql2.append("     	(SELECT TOP 1 nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = cFolioPAGODIVERSO AND nFolioSuficiencia IS NOT NULL AND NFOLIOSUFICIENCIA != -1  ORDER BY nFolioCompromiso DESC) NRES,");
        /* AE */
        sql2.append("     	CASE WHEN cpartida='35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO, ");
        /* AF */
        sql2.append("     	'000' CONC_MOV, ");
        /* AG */
        sql2.append("     	CONVERT(decimal(17,2),Sum(TPDD.mISRHonorarios + TPDD.mISRArrenda + TPDD.mISROtros +  TPDD.mimporteISRResico)) DCD_ISR,");
        /* AH */
        sql2.append("     	CONVERT(decimal(17, 2),sum(TPDD.m23Iva + mImporteIvaHonorarios + mImporteIvaArrenda + TPDD.mImporteFlete23 + ISNULL(TPDD.mImporteFlete4, 0) + ISNULL(TPDD.mImporteIva6, 0) ) ) DCD_IVA, ");
        /* AI */
        sql2.append("     	CONVERT(decimal(17,2),Sum(TPDD.mObra5))  DCD_MIL5, ");
        /* AJ */
        sql2.append("     	CONVERT(decimal(17,0),Sum(TPDD.mCNIC + TPDD.mIMDT)) DCD_MIL2,");
        /* AK */
        sql2.append("     	CONVERT(decimal(17,2),Sum(TPDD.mRetImpuestoCedular)) DCD_OTRAS_RET,");
        /* AL */
        sql2.append("     	CONVERT(decimal(17,2),Sum(TPDD.mPenalizacion)) DCD_PENALIZACION, ");
        /* AM */
        sql2.append("     	CONVERT(decimal(17, 0), 0) DCD_CONTRIBUCION, ");
        /* AN */
        sql2.append("     	CONVERT(decimal(17,2),Sum(TPDD.miva)) IVADES_45,");
        /* AO */
        sql2.append("     	CONVERT(decimal(17, 0), 0) ANTICIPO_46,");
        /* AP */
        sql2.append("     	CONVERT(decimal(17, 0), 0) IVAANT_47, ");
        /* AQ */
        sql2.append("     	'' id_ctr_intdet , TPDD.nFolioPagoDiverso ");
        sql2.append(" FROM	tPAGODIVERSODetalle TPDD WITH(NOLOCK) ");
        sql2.append("     	inner join tPAGODIVERSOEncabezado TPDE WITH(NOLOCK) ");
        sql2.append("     	on TPDD.nFolioPagoDiverso = TPDE.nFolioPagoDiverso ");
        sql2.append("     	and Left(TPDD.cEvento, 8) <> 'ANTICIPO' ");
        sql2.append("     	inner join tCatalogoEP TCEP WITH(NOLOCK) ");
        sql2.append("     	on TPDD.EP = TCEP.EP ");
        sql2.append(" WHERE	TPDD.nFolioPagoDiverso in (" + folio + ") ");
        sql2.append(" GROUP BY TCEP.cRamo, TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, tCEP.cFuncion, 	tCEP.cSubFuncion, cProgramaGeneral, TPDD.mimportemasiva, TPDD.mimporteiva,");
        sql2.append("     	tCEP.cActividadInstitucional,	tCEP.cProgramaPresupuestario, cpartida, tCEP.cTipoGasto,	tCEP.cFuenteFinanciamiento,		tCEP.cEntidadFederativa,");
        sql2.append("     	tCEP.cCartera, 	ltrim(TPDD.ID_TIPO_CONCEPTO),  	TPDE.cFolioPAGODIVERSO,	TPDE.cCentroContable, TPDD.cMes, TPDD.nFolioPagoDiverso, SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) ");
        sql2.append(" ORDER BY TPDD.nFolioPagoDiverso ");
        log.debug("Object: {}", sql2.toString());
        pstmntD = conn.prepareStatement(sql2.toString());
        rs = pstmntD.executeQuery();
        return rs;
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String Sql = " SELECT PDE.nFolioPagoDiverso, 'H' H, PDE.cRamo, '" + unidadContable + "', '' SOL_PAGO, '3', PDE.caNoContrarrecibo FOLIO_INTERNO, PDE.caNoContrarrecibo COMODIN " + " FROM  dbo.tPAGODIVERSOEncabezado PDE WITH (NOLOCK) WHERE PDE.nFolioPagoDiverso in (" + listaIds + ")  ORDER BY PDE.nFolioPagoDiverso  ";
        log.debug("Object: {}", "Documentacion comprobatoria encabezado " + Sql);
        try {
            pstmntH = conn.prepareStatement(Sql);
            System.out.println(Sql);
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                // PDE.mImporteNeto
                StringBuilder consulta = new StringBuilder();
                consulta.append("SELECT DISTINCT PDE.cRamo, REPLACE(REPLACE(DCD.DCD_FACTURA,',',''),'\"',''), ");
                consulta.append(" CONVERT(nvarchar(10), DCD.fAplicacion,103),  CONVERT(nvarchar(10), DCD.fRecepcion,103) + ' 12:00:00 a.m.', ");
                consulta.append(" CASE cEstatus WHEN 'Operada Pagada' THEN cpd.cCampoAdi3 ELSE ISNULL(BB.CBEN, isnull(DCD.DCD_CBEN,'')) end, ");
                consulta.append(" CASE WHEN B.cExtranjero = 1 THEN '05' ELSE '04' END 'TipoBen', ");
                consulta.append(" DCD.DCD_TIPO_OPE,  DCD_TIVA 'TIVA',  CONVERT(decimal(17,2), DCD.DCD_VALOR), ");
                consulta.append(" CASE WHEN DCD_PENALIZACION = 0 THEN CONVERT(decimal(17,2), (DCD.DCD_IMP_BRUTO)) ELSE CONVERT(decimal(17,2), (DCD.DCD_IMP_BRUTO + DCD.DCD_PENALIZACION)) END AS DCD_IMP_BRUTO,  ");
                consulta.append(" CONVERT(decimal(17,2), DCD.DCD_IVA) AS DCD_IVA, ");
                consulta.append(" CONVERT(decimal(17,2), DCD.DCD_IVADES + DCD.DCD_CONTRIBUCION), ");
                consulta.append(" CONVERT(decimal(17,2), DCD.DCD_ISR), ");
                consulta.append(" CONVERT(decimal(17,2), DCD.DCD_MIL5), ");
                consulta.append(" CONVERT(decimal(17,2), DCD.DCD_MIL2), ");
                consulta.append(" CONVERT(decimal(17,2), DCD.DCD_OTRAS_RET), ");
                consulta.append(" CONVERT(decimal(17,2), DCD.DCD_PENALIZACION), ");
                consulta.append(" CONVERT(decimal(17,2), 0), DCD.DCD_CTOEXT, LEFT(REPLACE(REPLACE(DCD.cConcepto,',',''),'\"',''), 70) , PDE.nFolioPAGODIVERSO ");
                consulta.append(" FROM dbo.tPAGODIVERSOEncabezado PDE (NOLOCK) ");
                consulta.append(" INNER JOIN dbo.tPAGODIVERSODetalle PDD (NOLOCK) on PDE.nFolioPAGODIVERSO = PDD.nFolioPAGODIVERSO ");
                consulta.append(" LEFT JOIN pContratoDiversoSesion sd (NOLOCK) ON PDE.cFolioPAGODIVERSO = sd.cIdContrato and bActivo = 1 ");
                consulta.append(" AND sd.fSesionVigencia = (select convert(date, isnull(min(pcs.fSesionVigencia), '1900-01-01')) from pContratoDiversoSesion pcs (NOLOCK) where pcs.cIdContrato = sd.cIdContrato and bActivo = 1 and pcs.fSesionVigencia >= convert(date, GETDATE())) ");
                consulta.append(" LEFT JOIN tBeneficiario BB (NOLOCK) ON BB.dRFC = sd.cIdRFCSesion ");
                consulta.append(" LEFT JOIN tCadenasPDetalle cpd (NOLOCK) ON cpd.caNoContrarrecibo = PDE.caNoContrarrecibo ");
                consulta.append(" INNER JOIN dbo.v_pagosDocComprobatoria DCD (NOLOCK) on PDE.caNoContrarrecibo = DCD.caNoContrarrecibo AND DCD.cTipoPago = 'PAGODIVERSO'  ");
                consulta.append(" INNER JOIN [dbo].[tBeneficiario] B (NOLOCK) on PDE.RFC = B.dRFC ");
                consulta.append(" WHERE PDE.nFolioPagoDiverso = " + nFolioCompromiso + " ORDER BY PDE.nFolioPAGODIVERSO");
                pstmntD = conn.prepareStatement(consulta.toString());
                log.debug("Documentacion comprobatoria detalle");
                log.debug("Object: {}", "---------------------\n" + consulta.toString() + "\n------------------------------------------");
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 21; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(pstmntH);
        }
        return arrListaComp;
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGODIVERSOEncabezado SET nEnviadoSICOP = 1 WHERE nFolioPagoDiverso in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static int UpdateStatus(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGODIVERSOEncabezado SET nEnviadoSICOP = 0 WHERE nFolioPagoDiverso in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return retval;
    }

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso) throws SQLException {
        PreparedStatement pstmntL = null;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + "  WHERE caNoCompromiso = '" + caNoCompromiso + "'";
        try {
            pstmntL = conn.prepareStatement(queryUpdateEstatus);
            pstmntL.executeUpdate();
            conn.commit();
            return true;
        } finally {
            if (pstmntL != null) {
                pstmntL.close();
            }
            pstmntL = null;
        }
    }

    public static boolean insertRegisterLayout(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean insertReg;
        String queryInsert = "INSERT INTO tLayoutCompromisos(  cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso, cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP, nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud, cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC, caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento,                                              nDocumento,cDescripcion)            VALUES(" + "" + "'" + clave + "','" + cRamo + "','" + cUnidadResponsable + "','" + folioSICOP + "','" + idProceso + "'," + "" + "'" + cCentroContable + "','" + fExpedicion + "'," + total + ",'" + cTipoPoliza + "','" + nFolioPoliza + "'," + "" + "'" + nPolizaCancelacion + "','" + tipoMovimiento + "','" + origenPresupuesto + "','" + cuentaBancaria + "','" + noSolicitud + "'," + "" + "'" + tCambio + "','" + tMoneda + "','" + tSolicitud + "','" + volante + "','" + rfc + "'," + "" + "'" + caNoCompromiso + "','" + codSemarnat2 + "','" + estatus + "','" + fAplicacion + "','" + documento + "'," + "" + "'" + nDocumento + "','" + descripcion + "')";
        try {
            pstmnt = conn.prepareStatement(queryInsert);
            int reg = pstmnt.executeUpdate();
            if (reg == 1) {
                insertReg = true;
            } else {
                insertReg = false;
            }
            conn.commit();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return insertReg;
    }

    public static Sheet generaLayoutAmbiental(Connection conn, Sheet worksheet, String sFolioQuery) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT DISTINCT CONVERT(VARCHAR(32), 40072)  AS ins_operante ");
        query.append(",  CONVERT(VARCHAR(32), 5) AS tp_clave");
        query.append(",  CONVERT(VARCHAR(32), 40 ) + CONVERT(VARCHAR(32), Substring(ctab, 1, 3) )   AS ins_clave   ");
        query.append(",  'OPERACION CUSTF'  AS OP_CONCEPTO_PAGO");
        query.append(",  pde.mImporteNeto AS OP_MONTO");
        query.append(",  SUBSTRING( Ltrim(Rtrim(ben.dnombre + ' ' + Isnull(dapellidopaterno, '' ) + ' ' + Isnull(ben.dapellidomaterno, ''))),1,23  ) AS OP_NOM_BEN");
        query.append(",  Row_number() OVER ( ORDER BY nfoliopagodiverso) AS   OP_REF_NUMERICA");
        query.append(",  CONVERT(VARCHAR(32), 40) AS TC_CLAVE_BEN ");
        query.append(",  CONVERT(VARCHAR(32), ctab)  AS OP_CUENTA_BEN ");
        query.append(", 'MANDATO FFM 744792' AS OP_CVE_RASTREO ");
        query.append(" FROM   tpagodiversoencabezado pde WITH(nolock)    ");
        query.append(" INNER JOIN tbeneficiario ben WITH(nolock)             ");
        query.append(" ON pde.rfc = ben.drfc  ");
        query.append(" WHERE  PDE.nfoliopagodiverso IN ( " + sFolioQuery + ") ORDER BY 7");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            worksheet = Util.resultSetToExcel(rs, worksheet, 1);
            return worksheet;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    /*
	public static ArrayList<String> BuscaCompromisosMenorUMA( Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario ) throws Exception {
		ArrayList<String> arrListaComp = new ArrayList<String>();
		PreparedStatement pstmntH = null;
		PreparedStatement pstmntD = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		String[] arrFolios = listaIds.split( "," );
		String[] arrCuentasBancarias = listaCuentaBancaria.split( "," );
		String[] arrFechas = listaFechas.split( "," );
		String[] arrLeyendas = listaLeyendas.split( "," );
		int intIndice = -1;
		BigDecimal total = new BigDecimal(0);
		BigDecimal retenciones = new BigDecimal(0);
		BigDecimal revisarTotal = new BigDecimal(0);
		BigDecimal revisarRete = new BigDecimal(0);
		
		
		try {	
				rs = generaEncabezadoPagoDiverso ( conn, listaIds, 1, 3 );
		
				while ( rs.next() ) {
					log.debug( arrFolios[0].trim() );
					
					String nFolio, nFolioCompromiso = rs.getString( 1 );
					for ( int i = 0; i < arrFolios.length; i++ ) {
						nFolio = arrFolios[i].trim();
		
						if ( nFolio.equals( nFolioCompromiso ) ) {
							intIndice = i;
							break;
						}
					}
		
					String vcReferencia1 = rs.getString( 23 ).trim();
					String vcReferencia2 = rs.getString( 24 ).trim();
					String vcCtaBancaria = arrCuentasBancarias[intIndice].trim().trim();
					String vcBenef = rs.getString( 18 ).trim();
					String vcLeyenda = arrLeyendas[intIndice].trim().trim();
					String vRFC = rs.getString( 20 ).trim();
					String vcEstatus = rs.getString( 42 ).trim();
					String vIntermFin = rs.getString( 43 ).trim();
					String vnClaveAMF = rs.getString( 44 ).trim();
					if ( !"".equals( vnClaveAMF ) ) {
						vcBenef = "S24676";
						vcCtaBancaria = "22800100000100";
						vcLeyenda = "1";
						vRFC = "6001";
					}
					if ( "Operada Pagada".equals( vcEstatus ) ) {
						vcCtaBancaria = vIntermFin;
					}
					if ( !"".equals( vcReferencia1 ) && "".equals( vcReferencia2 ) ) {
						vcLeyenda = "0";
					}
		
					String encabezado = rs.getString( 2 ) + "," + arrFechas[intIndice].trim() + "," + rs.getString( 4 ).trim() + "," + rs.getString( 5 ).trim() + "," + rs.getString( 6 ).trim() + "," + rs.getString( 7 ).trim() + "," + rs.getString( 8 ).trim() + "," + rs.getString( 9 ).trim() + "," + rs.getString( 10 ).trim() + "," + rs.getString( 11 ).trim() + "," + rs.getString( 12 ).trim() + "," + rs.getString( 13 ).trim() + "," + rs.getString( 14 ).trim() + "," + rs.getString( 15 ).trim() + "," + rs.getString( 16 ).trim() + "," + vcLeyenda + "," + vcBenef + "," + vcCtaBancaria + "," + vRFC + "," + rs.getString( 21 ).trim() + "," + rs.getString( 22 ).trim() + "," + rs.getString( 23 ).trim() + "," + rs.getString( 24 ).trim() + "," + rs.getString( 25 ).trim().replaceAll( "[\r\n]{2,}", " " ) + "," + rs.getString( 26 ).trim() + "," + rs.getString( 27 ).trim() + "," + rs.getString( 28 ).trim() + "," + rs.getString( 29 ).trim() + "," + rs.getString( 30 ).trim() + "," + rs.getString( 31 ).trim() + "," + rs.getString( 32 ).trim() + "," + rs.getString( 33 ).trim() + "," + rs.getString( 34 ).trim() + "," + rs.getString( 35 ).trim() + "," + rs.getString( 36 ).trim() + "," + rs.getString( 37 ).trim() + "," + rs.getString( 38 ).trim() + "," + rs.getString( 39 ).trim() + "," + rs.getString( 40 ).trim() + "," + rs.getString( 41 );
					encabezado = encabezado + "\r\n";
					arrListaComp.add( encabezado );
		
					// Aqui grabamos dentro de layouts creados encabezado
					String[] f = arrFechas[intIndice].trim().split( "/" );
					String fecha = f[2] + "-" + f[1] + "-" + f[0];
					insertaLayoutEncabezadoPD(conn, fecha, arrLeyendas[intIndice].trim().trim(), arrCuentasBancarias[intIndice].trim().trim(), sUsuario, listaIds, vcBenef, "1", vRFC);
					
					// detalle de SP
					rs2 = generaDetallePagoDiverso(conn, rs.getString( 1 ));
		
					while ( rs2.next() ) {
						String token = new String();
						StringBuffer detalle = new StringBuffer();
						for ( int posicionColumna = 1; posicionColumna < 40; posicionColumna++ ) { 
							if (posicionColumna == 27) {
								revisarTotal = rs2.getBigDecimal( posicionColumna );
								total = total.add( revisarTotal);
								if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
									throw new Exception ("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + listaIds);
								} else {
									String valor = ( StringUtils.isBlank( rs2.getString( posicionColumna ) ) ? "" : rs2.getString( posicionColumna ).trim().replaceAll( "[\r\n]{2,}", " " ) );
									detalle.append( token ).append( valor );
									token = ",";
								}
							}  else if(posicionColumna >= 32 && posicionColumna <=37 ) {
								//Suma el importe de las retenciones
								revisarRete = rs2.getBigDecimal( posicionColumna );
								retenciones = retenciones.add(revisarRete);
								String valor = ( StringUtils.isBlank( rs2.getString( posicionColumna ) ) ? "" : rs2.getString( posicionColumna ).trim().replaceAll( "[\r\n]{2,}", " " ) );
								detalle.append( token ).append( valor );
								token = ",";
								
							} else {
								String valor = ( StringUtils.isBlank( rs2.getString( posicionColumna ) ) ? "" : rs2.getString( posicionColumna ).trim().replaceAll( "[\r\n]{2,}", " " ) );
								detalle.append( token ).append( valor );
								token = ",";
							}
						}
						token = "";
						detalle.append( "\r\n" );
						arrListaComp.add( detalle.toString() );
					}
		
					// Aqui grabamos dentro de layouts creados detalle
					insertaLayoutDetallePD( conn, nFolioCompromiso, listaIds );
				}

				//Valida que el total del Layout sea igual a los pagos
				validarTotalLayout(conn, total, listaIds);

				//Valida que el total de las Retenciones sea igual a los pagos
				validarTotalRetenciones(conn, retenciones, listaIds);
				
		} finally {
			
				CloseObject.closeObject( rs2 );
				CloseObject.closeObject( rs );
				CloseObject.closeObject( pstmntD );
				CloseObject.closeObject( pstmntH );
				
		}
		
		return arrListaComp;
	}
	*/
    public static void insertaLayoutEncabezadoPD(Connection conn, String fecha, String leyenda, String cuentaBancaria, String sUsuario, String listaIds, String vcBenef, String vtipoPago, String vRFC) throws Exception {
        PreparedStatement pstmntHLayout = null;
        try {
            StringBuilder sqlLayoutGrabado = new StringBuilder();
            sqlLayoutGrabado.append(" INSERT INTO tLayoutsCreadosPagosDiversosHeader ");
            sqlLayoutGrabado.append("SELECT	distinct getdate(),  ");
            sqlLayoutGrabado.append("      	tCE.nFolioPagoDiverso,");
            sqlLayoutGrabado.append("      	'H' AS Header,");
            sqlLayoutGrabado.append("      	CONVERT(nvarchar(10), tCE.fAplicacion,120), '" + fecha + "',");
            sqlLayoutGrabado.append("      	tCE.cRamo,");
            sqlLayoutGrabado.append("      	tCE.cRamo,");
            sqlLayoutGrabado.append("      	tCE.cRamo, ");
            sqlLayoutGrabado.append("'" + unidadContable + "',");
            sqlLayoutGrabado.append("'" + unidadContable + "',");
            sqlLayoutGrabado.append("'" + unidadContable + "',");
            sqlLayoutGrabado.append("      	 'N' ID_TIPO_MOVIMIENTO,");
            sqlLayoutGrabado.append("      	'1' AS OrigenPpto,");
            sqlLayoutGrabado.append("      	'3' AS TipoSol,");
            sqlLayoutGrabado.append("      	 'MXN' TipoMoneda ,");
            sqlLayoutGrabado.append("      	 '1' TipoCambio,");
            sqlLayoutGrabado.append("'" + vtipoPago + "', ");
            sqlLayoutGrabado.append("'" + leyenda + "', ");
            sqlLayoutGrabado.append("'" + vcBenef + "', '");
            sqlLayoutGrabado.append(cuentaBancaria + "',");
            sqlLayoutGrabado.append("'" + vRFC + "', ");
            sqlLayoutGrabado.append("      	'FAC',");
            sqlLayoutGrabado.append("      	'' FechaReferencia,");
            sqlLayoutGrabado.append("      	'' Referencia1,");
            sqlLayoutGrabado.append("      	'' Referencia2,");
            sqlLayoutGrabado.append("      	tCE.cConcepto,");
            sqlLayoutGrabado.append("      	'' NotasReverso,");
            sqlLayoutGrabado.append("      	'' AMF,");
            sqlLayoutGrabado.append("      	rtrim(DC.caNoContrarrecibo) NO_ACMI,");
            sqlLayoutGrabado.append("      	rtrim(DC.caNoContrarrecibo) AuxiliarComodin,");
            sqlLayoutGrabado.append("      	'' CTR ,");
            sqlLayoutGrabado.append("      	'' FolioDC,");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2), DC.DCD_ISR), ");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2),DCD_IVA),");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2),DC.DCD_MIL5),");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2),DCD_MIL2),");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2),DC.DCD_OTRAS_RET),");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2),DC.DCD_PENALIZACION), ");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2),DC.DCD_CONTRIBUCION), ");
            sqlLayoutGrabado.append("      	CONVERT(decimal(17,2),DC.DCD_IVADES),");
            sqlLayoutGrabado.append("      	'0' IVAANT, ");
            sqlLayoutGrabado.append("      	tCE.ID_DESTINO_GASTO, '" + sUsuario + "' ");
            sqlLayoutGrabado.append(" FROM	tPAGODIVERSOEncabezado tCE (NOLOCK) ");
            sqlLayoutGrabado.append("     	LEFT JOIN tBeneficiario B (NOLOCK) ");
            sqlLayoutGrabado.append("     	ON tce.RFC = B.dRFC ");
            sqlLayoutGrabado.append("     	INNER JOIN tBeneficiarioCuentasBancarias BCB (NOLOCK) ");
            sqlLayoutGrabado.append("     	ON tCE.RFC = BCB.dRFC ");
            sqlLayoutGrabado.append("     	LEFT JOIN pCatalogoTipoDocumento CTD (NOLOCK) ");
            sqlLayoutGrabado.append("     	ON tCE.cIdTipoDocumento = CTD.cIdTipoDocumento ");
            sqlLayoutGrabado.append("     	LEFT JOIN v_pagosDocComprobatoria DC (NOLOCK) ");
            sqlLayoutGrabado.append("     	ON DC.caNoContrarrecibo = tCE.caNoContrarrecibo AND DC.cTipoPago = 'PAGODIVERSO'  ");
            sqlLayoutGrabado.append(" WHERE	tCE.nFolioPagoDiverso in (" + listaIds + ") ");
            log.debug("Object: {}", sqlLayoutGrabado.toString());
            String SqlLayoutGrabado = sqlLayoutGrabado.toString();
            pstmntHLayout = conn.prepareStatement(SqlLayoutGrabado);
            pstmntHLayout.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntHLayout);
        }
    }

    public static void insertaLayoutDetallePD(Connection conn, String nFolioCompromiso, String listaIds) throws Exception {
        PreparedStatement pstmntHLayoutDet = null;
        try {
            StringBuilder sqlLayoutDet = new StringBuilder();
            sqlLayoutDet.append(" INSERT INTO tLayoutsCreadosPagosDiversosDetalle SELECT DISTINCT " + nFolioCompromiso);
            sqlLayoutDet.append(", '1' ID_EVENTO,'24.0.001' EVENTO,ltrim(TCEP.cRamo) ID_RAMO_ML,'" + unidadContable + "', TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion,");
            sqlLayoutDet.append(" CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, ");
            sqlLayoutDet.append(" tCEP.cActividadInstitucional, tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)) CCAP_157, substring(cpartida,2,1) CCON_158, substring(cpartida,3,1) CPARG_300, ");
            sqlLayoutDet.append(" substring(cpartida,4,2) CPAR_159, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, tCEP.cCartera,ltrim('0000000' + tCEP.cUnidadEjecutora), ");
            sqlLayoutDet.append(" substring(TCEP.cUnidadNorativa,2,2) CCOP_163, '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3, ");
            sqlLayoutDet.append(" TPDE.mImporteNeto, MONTH(GETDATE()) MES_149, '0' NRES,ltrim(TPDD.ID_TIPO_CONCEPTO) TIPO_CONTRATO, '000' CONC_MOV, CONVERT(decimal(17,2),DC.DCD_ISR), CONVERT(decimal(17,2),DCD_IVA), ");
            sqlLayoutDet.append(" CONVERT(decimal(17,2),DC.DCD_MIL5), CONVERT(decimal(17,2),DCD_MIL2), CONVERT(decimal(17,2),DC.DCD_CONTRIBUCION), CONVERT(decimal(17,2),DC.DCD_OTRAS_RET), ");
            sqlLayoutDet.append(" CONVERT(decimal(17,2),TPDD.mImporteIva) IVADesglosado, CONVERT(decimal(17,2),DC.DCD_PENALIZACION), '' id_ctr_intdet ");
            sqlLayoutDet.append(" FROM tPAGODIVERSODetalle TPDD (NOLOCK) inner join tPAGODIVERSOEncabezado TPDE (NOLOCK) on TPDD.nFolioPagoDiverso = TPDE.nFolioPagoDiverso ");
            sqlLayoutDet.append(" inner join tCatalogoEP TCEP (NOLOCK) on TPDD.EP = TCEP.EP ");
            sqlLayoutDet.append(" LEFT JOIN v_pagosDocComprobatoria DC ON DC.caNoContrarrecibo = TPDE.caNoContrarrecibo AND DC.cTipoPago = 'PAGODIVERSO'  ");
            sqlLayoutDet.append(" where TPDD.nFolioPagoDiverso in (" + listaIds + ") ");
            String SqlLayoutGrabadoDet = sqlLayoutDet.toString();
            log.debug("Object: {}", SqlLayoutGrabadoDet.toString());
            pstmntHLayoutDet = conn.prepareStatement(SqlLayoutGrabadoDet);
            pstmntHLayoutDet.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntHLayoutDet);
        }
    }

    public static EgresoPAGODIVERSOEncabezado cargaEncabezado(Connection conn, int folioPagoDiverso) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	aEjercicioFiscal AS ejercicioFiscal, ");
        query.append("		ALM AS alm, ");
        query.append("		caNoContrarrecibo AS contrarecibo, ");
        query.append("		capitulo AS capitulo, ");
        query.append("		cCentroContable AS centroContable, ");
        query.append("		cConcepto AS concepto, ");
        query.append("		cDescripcionPoliza AS descripcionPoliza, ");
        query.append("		cDocumentoHaplicado AS documentoAplicado, ");
        query.append("		cEsFirmaElectronica AS esFirmaElectronica, ");
        query.append("		cEsIngresoFiscal AS esIngresoFiscal, ");
        query.append("		cEsRelacionGastos AS esRelacionGastos, ");
        query.append("		cFolioPAGODIVERSO AS idContrato, ");
        query.append("		cIdEstadoEstimacion AS idEstadoEstimacion, ");
        query.append("		cIdRecepMat AS idRecepMat, ");
        query.append("		cIdTipoDocumento AS idTipoDocumento, ");
        query.append("		cIdTipoOperacion AS idTipoOperacion, ");
        query.append("		cIdUsuarioAprobacion AS idUsuarioAprobacion, ");
        query.append("		cIdUsuarioCaptura AS idUsuarioCaptura, ");
        query.append("		cIdUsuarioImpresion AS idUsuarioImpresion, ");
        query.append("		cIdUsuarioRechazo AS idUsuarioRechazo, ");
        query.append("		cIdUsuarioRevision AS idUsuarioRevision, ");
        query.append("		cIngresosPropios AS ingresosPropios, ");
        query.append("		cMes AS mes, ");
        query.append("		cNoEstimacion AS noEstimacion, ");
        query.append("		cNoFactura AS noFactura, ");
        query.append("		cObservaciones AS observaciones, ");
        query.append("		cOficioDiferenciaCambiaria AS oficioDiferenciaCambiaria, ");
        query.append("		cPagoReferenciado AS pagoReferenciado, ");
        query.append("		cRadicado AS radicado, ");
        query.append("		cRamo AS ramo, ");
        query.append("		cReferenciaBancaria AS referenciaBancaria, ");
        query.append("		cReferenciaPRODDER AS referenciaPRODDER, ");
        query.append("		CTAB AS CTAB, ");
        query.append("		cTipoPoliza AS tipoPoliza, ");
        query.append("		cUnidadResponsable AS unidadResponsable, ");
        query.append("		cUnidadResponsableContable AS unidadResponsableContable, ");
        query.append("		CONVERT( DATE, fAplicacion, 103) AS fechaAplicacion, ");
        query.append("		fCancelacion AS fechaCancelacion, ");
        query.append("		fProgramadaPago AS fechaProgramadaPago, ");
        query.append("		ID_DESTINO_GASTO AS idDestinoGasto, ");
        query.append("		ID_TIPO_FONDO AS idTipoFondo, ");
        query.append("		ID_TIPO_MOVIMIENTO AS IdTipoMovimiento, ");
        query.append("		ID_TIPO_OPERACION AS idTipoOperacion, ");
        query.append("		lAmortizarAnticipoConEscalacion AS amortizarAnticipoConEscalacion, ");
        query.append("		lAplicaImpuestoCedular AS aplicaImpuestoCedular, ");
        query.append("		lContrarreciboImpreso AS contrarreciboImpreso, ");
        query.append("		mAcumuladoxpagar AS importeAcumuladoPagar, ");
        query.append("		mAmortizacion AS importeAmortizacion, ");
        query.append("		mAmortizacionAcumulado AS importeAmortizacionAcumulado, ");
        query.append("		mAmortizacionAnticipo AS importeAmortizacionAnticipo, ");
        query.append("		mImporteBruto AS importeBruto, ");
        query.append("		mImporteDevolucion AS importeDevolucion, ");
        query.append("		mImporteDevolucionAcumulado AS importeDevolucionAcumulado, ");
        query.append("		mImporteIVA AS importeIVA, ");
        query.append("		mImporteMasIva AS importeMasIva, ");
        query.append("		mImporteNeto AS importeNeto, ");
        query.append("		mImportePenalizacion AS importePenalizacion, ");
        query.append("		mImporteRetencion AS importeRetencion, ");
        query.append("		mImporteSancion AS importeSancion, ");
        query.append("		mImporteSancionAcumulado AS importeSancionAcumulad, ");
        query.append("		mOtrosImpuestos AS importeOtrosImpuestos, ");
        query.append("		mSaldoAnticipo AS importeSaldoAnticipo, ");
        query.append("		mSaldoCedula AS importeSaldoCedula, ");
        query.append("		nAcompanantes AS numeroAcompanantes, ");
        query.append("		nEnviadoSICOP AS enviadoSICOP, ");
        query.append("		nFolioPAGODIVERSO AS folioPagoDiverso, ");
        query.append("		nFolioPoliza AS folioPoliza, ");
        query.append("		nFolioPolizaCancelacion AS folioPolizaCancelacion, ");
        query.append("		nIdConcepto AS idConcepto, ");
        query.append("		nIdTransactionWS AS idTransactionWS, ");
        query.append("		nNumEmpleadoAut AS numEmpleadoAut, ");
        query.append("		nNumEmpleadoElab AS numEmpleadoElab, ");
        query.append("		nNumEmpleadoVoBo AS numEmpleadoVoBo, ");
        query.append("		NOMBRE AS Nombre, ");
        query.append("		nPorcAmortizacion AS porcAmortizacion, ");
        query.append("		ISNULL( nPorcImpuestoCedular, 0) AS porcImpuestoCedular, ");
        query.append("		nTipoCambio AS tipoCambio, ");
        query.append("		NumPagoAMF AS numPagoAMF, ");
        query.append("		RFC AS rfc, ");
        query.append("		sFirmanteAut AS firmanteAut, ");
        query.append("		sFirmanteEla AS firmanteEla, ");
        query.append("		sFirmanteVoBo AS firmanteVoBo, ");
        query.append("		sPuestoAut AS puestoAut, ");
        query.append("		sPuestoEla AS puestoEla, ");
        query.append("		sPuestoVoBo AS puestoVoBo, ");
        query.append("		CASE WHEN escargamasiva = 'S'  then 'true' ELSE 'false' END AS cargaMasiva, ");
        query.append("		U_LOGIN AS login");
        query.append("  FROM	tPAGODIVERSOEncabezado WITH(NOLOCK)");
        query.append(" WHERE	nFolioPAGODIVERSO = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String query2 = query.toString();
            ps = conn.prepareStatement(query2);
            ps.setInt(1, folioPagoDiverso);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            EgresoPAGODIVERSOEncabezado pdEncabezado = new EgresoPAGODIVERSOEncabezado();
            DateConverter converter = new DateConverter(null);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(pdEncabezado, resultObj);
            return pdEncabezado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<EgresoDetalle> cargaDetalle(Connection conn, int folioPagoDiverso) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT	ADEFAS AS adefas, ");
        query.append("		ALM AS alm, ");
        query.append("		altaAlmacen AS altaAlmacen, ");
        query.append("		cCentroContable AS centroContable, ");
        query.append("		cEjercicio AS ejercicioFiscal, ");
        query.append("		cEvento AS evento, ");
        query.append("		cIdCuentaContable AS idCuentaContable, ");
        query.append("		cMes AS mesCalendario, ");
        query.append("		EP AS ep, ");
        query.append("		ID_TIPO_CONCEPTO AS idTipoConcepto, ");
        query.append("		ID_TIPO_MOVIMIENTO AS idTipoMovimiento, ");
        query.append("		m23IVA AS importe23IVA, ");
        query.append("		m2Millar AS importe2Millar, ");
        query.append("		mCNIC AS importeCNIC, ");
        query.append("		mComprometido AS importeComprometido, ");
        query.append("		mDevolucion AS importeDevolucion, ");
        query.append("		mIMDT AS importeIMDT, ");
        query.append("		mImporte AS importe, ");
        query.append("		mImporteAmortiza AS importeAmortiza, ");
        query.append("		mImporteBruto AS importeBruto, ");
        query.append("		mImporteFlete23 AS importeFlete23, ");
        query.append("		mImporteFlete4 AS importeFlete4, ");
        query.append("		mImporteISRLaudos AS importeISRLaudos, ");
        query.append("		mImporteIva AS importeIva, ");
        query.append("		mImporteIvaArrenda AS importeIvaArrenda, ");
        query.append("		mImporteIvaHonorarios + isnull(mImporteIva6,0) AS importeIvaHonorarios, ");
        query.append("		mImporteIvaProv AS importeIvaProv, ");
        query.append("		mImporteMasIva AS importeMasIva, ");
        query.append("		mImporteNeto AS importeNeto, ");
        query.append("		mImporteObra AS importeObra, ");
        query.append("		mISRArrenda AS importeISRArrenda, ");
        query.append("		mISRHonorarios AS importeISRHonorarios, ");
        query.append("		mISROtros AS importeISROtros, ");
        query.append("		mObra5 AS importeObra5, ");
        query.append("		mOtrosImpuestos AS importeOtrosImpuestos, ");
        query.append("		mPenalizacion AS importePenalizacion, ");
        query.append("		mRetencion AS importeRetencion, ");
        query.append("		mRetImpuestoCedular AS importeRetImpuestoCedular, ");
        query.append("		mSancion AS importeSancion, ");
        query.append("		mTesofe AS importeTesofe, ");
        query.append("		nCapitulo AS capitulo, ");
        query.append("		nDocRenglon AS numeroRenglon, ");
        query.append("		nPoliza AS numeroPoliza, ");
        query.append("		OBGT AS obgt, ");
        query.append("		Periodo13 AS periodo13, ");
        query.append("		RFC AS rfc, ");
        query.append("		aEjercicioFiscal AS ejercicioFiscal, ");
        query.append("		cDocumentoHaplicado AS documentoAplicado, ");
        query.append("		cIdEntidadContable AS cIdEntidadContable, ");
        query.append("		cIdRelacion AS cIdRelacion, ");
        query.append("		cTipoPoliza AS cTipoPoliza, ");
        query.append("		m5Millar AS importe5Millar, ");
        query.append("		mAmortizacionAnticipo AS importeAmortizacionAnticipo, ");
        query.append("		mBruto AS importeBruto, ");
        query.append("		mCedular AS importeCedular, ");
        query.append("		mFletes AS importeFletes, ");
        query.append("		mImporteNegativo AS importeImporteNegativo, ");
        query.append("		mIVA AS importeIVA, ");
        query.append("		mNeto AS importeNeto, ");
        query.append("		nFolioPAGODIVERSO AS folioPAGODIVERSO, ");
        query.append("		nFolioPoliza AS folioPoliza, ");
        query.append("		nMes AS numeroMes,  ");
        query.append("		mimporteISRResico AS importeISRResico ");
        query.append("  FROM	tPAGODIVERSODetalle WITH(NOLOCK)");
        query.append(" WHERE	nFolioPAGODIVERSO = ?");
        List<EgresoDetalle> detalle = new ArrayList<EgresoDetalle>();
        try {
            String query2 = query.toString();
            ps = conn.prepareStatement(query2);
            ps.setInt(1, folioPagoDiverso);
            rs = ps.executeQuery();
            DateConverter converter = new DateConverter(null);
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            while (resultObj != null) {
                EgresoPAGODIVERSODetalle pdDetalle = new EgresoPAGODIVERSODetalle();
                BeanUtils.populate(pdDetalle, resultObj);
                detalle.add(pdDetalle);
                resultObj = RSToTable.rsToMapCaseSensitive(rs);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static ArrayList<String> ArmaLayoutBanco(Connection conn, String nFolios, boolean tipoBanorte, String sCuentaLayout) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmLayout = null;
        ResultSet rsLayout = null;
        String ctaBancaria = " and beneficiario.CTAB = SUBSTRING(pago.ctab, 8,10)";
        String esBanorte = " IN ";
        try {
            if (!tipoBanorte) {
                esBanorte = " NOT IN ";
                ctaBancaria = " and beneficiario.CTAB = pago.CTAB";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT CASE WHEN SUBSTRING(pago.CTAB,1,3)='072' THEN '02' ELSE '04' END OPER, ");
            sb.append(" idBancario + REPLICATE(' ', 13 -LEN(IDBANCARIO)) idBancario, ");
            sb.append(" REPLICATE('0', 10) + '0279554910' CTA_ORIGEN, ");
            sb.append(" CASE WHEN SUBSTRING(pago.CTAB,1,3)='072' THEN REPLICATE('0', 10) + SUBSTRING(pago.CTAB,8,10) ELSE REPLICATE('0', 20 - LEN(pago.CTAB)) +pago.CTAB END CUENTA_DESTINO,");
            sb.append(" REPLICATE ('0', 14- LEN(REPLACE(CONVERT(VARCHAR(32), mImporteNeto),'.','')) )+ replace(CONVERT(VARCHAR(14), mImporteNeto), '.', '') AS mImporteNeto, ");
            sb.append(" REPLICATE ('0', 10- LEN(nFolioPAGODIVERSO)) + CAST( nFolioPAGODIVERSO AS VARCHAR(10))  nFolioPAGODIVERSO ,");
            sb.append(" case when (select top 1 SUBSTRING(ep,61,3)  FROM tpagodiversodetalle det WITH (NOLOCK) where det.nfoliopagodiverso = pago.nfoliopagodiverso) = 'S01'THEN '53TP SERV AMB                 ' ELSE '53TP COMP AMB                 ' END descripcion , ");
            sb.append(" 1 MONEDA_ORIGEN, 1 MONEDA_ORDEN, 'BMN930209927 ' RFC_ORD, REPLICATE('0',14) IVA,'NA' + REPLICATE(' ',37)  EMAIL, ");
            sb.append(" FORMAT(GETDATE(), 'ddMMyyyy') FECHA_AP,REPLICATE(' ',70) INSTRUCC_PAGO ");
            sb.append(" FROM tPAGODIVERSOEncabezado pago WITH(NOLOCK)  INNER JOIN tBen_BancoAmbiental beneficiario WITH(NOLOCK) ON pago.rfc=beneficiario.dRFC ");
            sb.append(" WHERE cDocumentoHaplicado = 'S' AND nfoliopagodiverso IN ( " + nFolios + ")AND SUBSTRING(pago.CTAB,1,3)" + esBanorte + "('072')" + ctaBancaria);
            String Sql = sb.toString();
            log.info("Object: {}", "Ejecutando query de Layout  [" + Sql + "]");
            pstmLayout = conn.prepareStatement(Sql);
            rsLayout = pstmLayout.executeQuery();
            while (rsLayout.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < 15; i++) {
                    detalle.append(token).append(rsLayout.getString(i).replaceAll("[\r\n]", ""));
                    token = "";
                }
                token = "";
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
        } finally {
            CloseObject.closeObject(rsLayout, false);
            CloseObject.closeObject(pstmLayout, false);
        }
        return arrListaComp;
    }

    public static int avanzaLayout(Connection conn, String nFolios) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE tPAGODIVERSOEncabezado SET nEnviadoSICOP = 1 WHERE nFolioPAGODIVERSO IN ( " + nFolios + ")";
        int actualizado = 0;
        try {
            pst = conn.prepareStatement(query);
            actualizado = pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
        return actualizado;
    }

    public static ArrayList<String> ArmaLayoutBancoIP(Connection conn, String nFolios, boolean tipoBanorte, String sCuentaLayout) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmLayout = null;
        ResultSet rsLayout = null;
        String ctaIntegradora = sCuentaLayout;
        String esBanorte = " IN ";
        try {
            if (!tipoBanorte)
                esBanorte = " NOT IN ";
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT CASE WHEN SUBSTRING(CTAB,1,3)='072' THEN '02' ELSE '04' END, ");
            sb.append("	cIdBancario, ");
            sb.append("	SUBSTRING('" + ctaIntegradora + "',8,10), ");
            sb.append("	CASE WHEN SUBSTRING(CTAB,1,3)='072' THEN SUBSTRING(CTAB,8,10) ELSE CTAB END, ");
            sb.append("	CONVERT(VARCHAR(32), mImporteNeto) AS mImporteNeto, ");
            sb.append("	RIGHT('000000' + CONVERT(VARCHAR(6), nFolioPAGODIVERSO),6) cxp, ");
            sb.append("	LEFT( (SELECT dbo.fn_quitarCaracteresEspeciales('CONAFOR ' + (SELECT TOP 1 DESTINO_GASTO FROM CAT_DESTINO_GASTO gasto WITH (NOLOCK) WHERE gasto.ID_DESTINO_GASTO = pago.ID_DESTINO_GASTO )) ), 30), ");
            sb.append("	'CNF010405EG1', '', '', 'x'  ");
            sb.append(" FROM tPAGODIVERSOEncabezado pago WITH(NOLOCK) ");
            sb.append(" INNER JOIN tBeneficiario beneficiario WITH(NOLOCK) ON pago.RFC =beneficiario.dRFC ");
            sb.append(" WHERE cDocumentoHaplicado = 'S' ");
            sb.append("	AND nFolioPAGODIVERSO IN (" + nFolios + ")" + "	AND SUBSTRING(CTAB,1,3) " + esBanorte + " ('072')");
            String Sql = sb.toString();
            log.info("Object: {}", "Ejecutando query de Layout  [" + Sql + "]");
            pstmLayout = conn.prepareStatement(Sql);
            rsLayout = pstmLayout.executeQuery();
            while (rsLayout.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < 12; i++) {
                    detalle.append(token).append(rsLayout.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                    token = "	";
                }
                token = "";
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
        } finally {
            CloseObject.closeObject(rsLayout, false);
            CloseObject.closeObject(pstmLayout, false);
        }
        return arrListaComp;
    }

    /**
     * Valida si el pago se trata de un proceso de Tienda Digital.
     *
     * @param conn
     *            Conexion Activa a la base de datos
     * @param folioPagoDiverso
     *            Folio de pago a validar
     * @return true si y solo si es un pago de tienda digital.
     * @throws SQLException
     */
    public static boolean esPagoTiendaDigital(Connection conn, int folioPagoDiverso) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	COUNT(*) AS pagoTienda ");
        query.append("  FROM	tPAGODIVERSOEncabezado WITH(NOLOCK) ");
        query.append(" WHERE	SUBSTRING( cFolioPAGODIVERSO, 1,2 ) = 'CT' ");
        query.append("   AND	nFolioPAGODIVERSO = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioPagoDiverso);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void actualizaPagoTienda(Connection conn, int folioPago, boolean pagoTiendaDigital) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE tPAGODIVERSOEncabezado ");
        query.append("   SET cPagoTiendaDigital = ? ");
        query.append(" WHERE nFolioPAGODIVERSO = ? ");
        PreparedStatement ps = null;
        try {
            String sPagoTiendaDigital = (pagoTiendaDigital ? "S" : "N");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, sPagoTiendaDigital);
            ps.setInt(2, folioPago);
            int actualizados = ps.executeUpdate();
            log.trace("Object: {}", "Se actualizo " + actualizados + " pagos de tienda: " + sPagoTiendaDigital + " folio: " + folioPago);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void validarTotalLayout(Connection con, BigDecimal total, String listaIds) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteNeto) FROM tPAGODIVERSODetalle WITH (NOLOCK) WHERE nFolioPAGODIVERSO IN (" + listaIds + ")";
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            int resultado = total.compareTo(importe);
            if (resultado != 0) {
                throw new Exception("El importe del Layout es diferente de la suma de los pagos. Reporte al administrador.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }

    public static void validarTotalRetenciones(Connection con, BigDecimal total, String listaIds) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteIvaArrenda + mImporteIvaHonorarios + mImporteObra +mImporteFlete23 +mISRHonorarios +mISRArrenda + mImporteFlete4  + mObra5 +mImporteISRLaudos + mISROtros+mRetImpuestoCedular + isnull(mImporteIva6,0) +  isnull(mImporteIsrResico,0) ) totalRetenciones " + " FROM tPAGODIVERSODetalle WITH (NOLOCK) WHERE nFolioPAGODIVERSO IN (" + listaIds + ")";
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            int resultado = total.compareTo(importe);
            if (resultado != 0) {
                throw new Exception("No se genero el layout. El importe de las Retenciones es diferente de la suma de los pagos. Reporte al administrador.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }
}
