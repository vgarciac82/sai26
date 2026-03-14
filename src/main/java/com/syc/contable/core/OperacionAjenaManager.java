package com.syc.contable.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import com.axtel.egresos.CLC_SOLXPAGAR;
import com.syc.cfdi.db.CloseObject;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoOPERAJENASEncabezado;
import com.syc.gestion.core.Caso;
import com.syc.gestion.documental.CatalogosManager;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperacionAjenaManager {

    private static final Map<Integer, String> camposTipoRetencion = new HashMap<>();

    private static final Map<Integer, String> camposTipoCondicion = new HashMap<>();

    private static final Map<Integer, String> tipoTabla = new HashMap<>();

    public static final Logger log = LoggerFactory.getLogger(OperacionAjenaManager.class);

    static {
        camposTipoRetencion.put(1, "(mImporteIvaHonorarios + mImporteIvaArrenda +mISRHonorarios +mISRArrenda + mImporteFlete4 +mimporteIva6) mTotal , Round((mImporteIvaHonorarios + mImporteIvaArrenda +mISRHonorarios +mISRArrenda + mImporteFlete4 +mimporteIva6),0,1) mEntero, ((mImporteIvaHonorarios + mImporteIvaArrenda +mISRHonorarios +mISRArrenda + mImporteFlete4 +mimporteIva6) - Round((mImporteIvaHonorarios + mImporteIvaArrenda +mISRHonorarios +mISRArrenda + mImporteFlete4 +mimporteIva6),0,1)) nAcumulado,mImporteIvaHonorarios + mImporteIvaArrenda mImporteFlete23, mISRHonorarios, mISRArrenda, mImporteFlete4, cnic, imdt, 0 mObra5, mTesofe, 0 mRetImpuestoCedular, 0 mImporteISRLaudos , 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda,  cIdRFC, cMes, ID_DESTINO_GASTO, 0 mISROtros, mimporteIva6, mimporteISRResico ");
        camposTipoRetencion.put(4, " mObra5 mTotal, round(mObra5,0,1) mEntero , mObra5 -round(mObra5,0,1) nAcumulado, 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda, 0 mISRHonorarios, 0 mISRArrenda, 0 mImporteFlete4, cnic, imdt, mObra5, 0 mTesofe, 0 mRetImpuestoCedular, 0 mImporteISRLaudos , 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda,  cIdRFC, cMes, ID_DESTINO_GASTO, 0 mISROtros, 0 mimporteIva6, 0 mimporteISRResico ");
        camposTipoRetencion.put(6, "mRetImpuestoCedular mTotal, round(mRetImpuestoCedular,0,1) mEntero , mRetImpuestoCedular -round(mRetImpuestoCedular,0,1) nAcumulado,0 mImporteIvaHonorarios, 0 mImporteIvaArrenda, 0 mISRHonorarios, 0 mISRArrenda, 0 mImporteFlete4, cnic, imdt, 0 mObra5, 0 mTesofe, mRetImpuestoCedular, 0 mImporteISRLaudos , 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda,  cIdRFC, cMes, ID_DESTINO_GASTO, 0 mISROtros, 0 mimporteIva6, 0 mimporteISRResico ");
        camposTipoRetencion.put(7, "(mImporteIvaHonorarios + mImporteIvaArrenda + mImporteFlete4 +mimporteIva6) mTotal , Round((mImporteIvaHonorarios + mImporteIvaArrenda + mImporteFlete4 +mimporteIva6),0,1) mEntero, ((mImporteIvaHonorarios + mImporteIvaArrenda + mImporteFlete4 +mimporteIva6) - Round((mImporteIvaHonorarios + mImporteIvaArrenda + mImporteFlete4 +mimporteIva6),0,1)) nAcumulado,mImporteIvaHonorarios, mImporteIvaArrenda, 0 mISRHonorarios, 0 mISRArrenda, mImporteFlete4, cnic, imdt, 0 mObra5, mTesofe, 0 mRetImpuestoCedular, 0 mImporteISRLaudos , 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda,  cIdRFC, cMes, ID_DESTINO_GASTO, 0 mISROtros, mimporteIva6, 0 mimporteISRResico ");
        camposTipoRetencion.put(8, "(mISRHonorarios + mISRArrenda + mimporteISRResico) mTotal , Round((mISRHonorarios + mISRArrenda + mimporteISRResico),0,1) mEntero, ((mISRHonorarios + mISRArrenda + mimporteISRResico) - Round((mISRHonorarios + mISRArrenda + mimporteISRResico),0,1)) nAcumulado, 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda, mISRHonorarios, mISRArrenda, 0 mImporteFlete4, cnic, imdt, 0 mObra5, mTesofe, 0 mRetImpuestoCedular, 0 mImporteISRLaudos , 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda,  cIdRFC, cMes, ID_DESTINO_GASTO, 0 mISROtros, mimporteIva6, mimporteISRResico ");
        camposTipoRetencion.put(9, "mImporteISRLaudos mTotal, round(mImporteISRLaudos,0,1) mEntero , mImporteISRLaudos -round(mImporteISRLaudos,0,1) nAcumulado,0 mImporteIvaHonorarios, 0 mImporteIvaArrenda, 0 mISRHonorarios, 0 mISRArrenda, 0 mImporteFlete4, cnic, imdt, 0 mObra5, 0 mTesofe, 0 mRetImpuestoCedular, mImporteISRLaudos , 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda,  cIdRFC, cMes, ID_DESTINO_GASTO, 0 mISROtros, 0 mimporteIva6, 0 mimporteISRResico ");
        camposTipoRetencion.put(10, "mISROtros mTotal, round(mISROtros,0,1) mEntero , mISROtros -round(mISROtros,0,1) nAcumulado,0 mImporteIvaHonorarios, 0 mImporteIvaArrenda, 0 mISRHonorarios, 0 mISRArrenda, 0 mImporteFlete4, cnic, imdt, 0 mObra5, 0 mTesofe, 0 mRetImpuestoCedular, 0 mImporteISRLaudos , 0 mImporteIvaHonorarios, 0 mImporteIvaArrenda,  cIdRFC, cMes, ID_DESTINO_GASTO, mISROtros, 0 mimporteIva6, 0 mimporteISRResico ");
        camposTipoCondicion.put(1, "AND esTesofe1 = 1");
        camposTipoCondicion.put(4, "AND esObra = 1");
        camposTipoCondicion.put(6, "AND esCedular = 1");
        camposTipoCondicion.put(7, "AND esTesofe1IVA = 1");
        camposTipoCondicion.put(8, "AND esTesofe1ISR = 1");
        camposTipoCondicion.put(9, "AND esISRLaudos = 1");
        camposTipoCondicion.put(10, "AND esisrOtros = 1 ");
        tipoTabla.put(1, " FROM v_operacionesajenassaldoEjercido ");
        tipoTabla.put(2, " FROM v_operacionesajenassaldo ");
        tipoTabla.put(3, " FROM v_OperacionesAjenasSaldoFechaFactura ");
    }

    public OperacionAjenaManager() {
        super();
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntHLayout = null;
        PreparedStatement pstmntHLayoutDet = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] arrFolios = listaIds.split(",");
        String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
        String[] arrFechas = listaFechas.split(",");
        String[] arrLeyendas = listaLeyendas.split(",");
        int intIndice = -1;
        // PSC
        String Sql = " SELECT distinct tCE.nFolioOperAjenas,  'H' AS Header,  CONVERT(nvarchar(10), tCE.fCaptura,103), CONVERT(nvarchar(10), tCE.fAplicacion,103), tCE.cRamo, tCE.cRamo, tCE.cRamo, 'RHQ' UnidadResponsable, 'RHQ' UnidadResponsable, 'RHQ' UnidadResponsable, tCE.cIdTipoDocumento,  '1' AS OrigenPpto, 'MXN' TipoMoneda , '1.00' TipoCambio, '2' TIPO_PAGO, '3' AS CveLeyenda, B.CBEN, 'Cuenta' CUENTA_BANCARIA, rtrim(tCE.cIdRFC), 'OTR', '' FechaReferencia, LEFT(tCE.cConcepto, 40) Referencia1, '' Referencia2, LEFT(tCE.cConcepto, 40), '' AMF, rtrim(tCE.caNoContrarrecibo) NO_ACMI, rtrim(tCE.caNoContrarrecibo) AuxiliarComodin, '' CTR , '' FolioDC  FROM tOperAjenasEncabezado tCE  WITH (NOLOCK) LEFT JOIN tBeneficiario B  WITH (NOLOCK) ON tce.cIdRFC = B.dRFC  INNER JOIN tBeneficiarioCuentasBancarias BCB  WITH (NOLOCK) ON tCE.cIdRFC =	BCB.dRFC  LEFT JOIN pCatalogoTipoDocumento CTD  WITH (NOLOCK) ON tCE.cIdTipoDocumento = CTD.cIdTipoDocumento  LEFT JOIN v_pagosDocComprobatoria DC ON DC.caNoContrarrecibo =tCE.caNoContrarrecibo  WHERE tCE.nFolioOperAjenas in (" + listaIds + ") ";
        try {
            pstmntH = conn.prepareStatement(Sql);
            log.debug("Object: {}", Sql.toString());
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                log.debug("Object: {}", arrFolios[0].trim());
                String nFolio, nFolioCompromiso = rs.getString(1);
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String vreferencia = rs.getString(22).trim();
                vreferencia = vreferencia.replaceAll(",", " ");
                vreferencia = vreferencia.replaceAll(":", "");
                vreferencia = vreferencia.replaceAll(";", "");
                vreferencia = vreferencia.replaceAll("\\(", "");
                vreferencia = vreferencia.replaceAll("/", "");
                vreferencia = vreferencia.replaceAll("\\)", "");
                vreferencia = vreferencia.replaceAll("%", "");
                String encabezado = rs.getString(2) + "," + arrFechas[intIndice].trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + arrLeyendas[intIndice].trim().trim() + "," + rs.getString(17).trim() + "," + arrCuentasBancarias[intIndice].trim().trim() + "," + rs.getString(19).trim() + "," + rs.getString(20).trim() + "," + rs.getString(21).trim() + "," + vreferencia + "," + rs.getString(23).trim() + "," + vreferencia + "," + rs.getString(25).trim() + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                String[] fecha = arrFechas[intIndice].trim().split("/");
                String fechaProgramada = fecha[2] + "-" + fecha[1] + "-" + fecha[0];
                // Aqui grabamos dentro de layouts creados encabezado
                String SqlLayoutGrabado = " INSERT INTO [tLayoutsCreadosOperAjenaHeader] SELECT distinct getdate(),  tCE.nFolioOperAjenas, 'H' AS Header, CONVERT(nvarchar(10), tCE.faplicacion,120), '" + fechaProgramada + "' ,tCE.cRamo,tCE.cRamo,tCE.cRamo, 'RHQ' UnidadResponsable,'RHQ' UnidadResponsable,'RHQ' UnidadResponsable, 'N' ID_TIPO_MOVIMIENTO, '1' AS OrigenPpto,'3' AS TipoSol, 'MXN' TipoMoneda , '1' TipoCambio, '1' TIPO_PAGO, '" + arrLeyendas[intIndice].trim().trim() + "', B.CBEN, '" + arrCuentasBancarias[intIndice].trim().trim() + "', rtrim(tCE.cIdRFC), 'FAC', '' FechaReferencia, '' Referencia1, '' Referencia2, tCE.cConcepto, '' NotasReverso, '' AMF, rtrim(caNoContrarrecibo) NO_ACMI, rtrim(caNoContrarrecibo) AuxiliarComodin, '' CTR , '' FolioDC, '0', '0', '0', '0', '0', '0', '0', '0', '0' IVAANT, 0 ID_DESTINO_GASTO, '" + sUsuario + "'  FROM tOperAjenasEncabezado tCE  WITH (NOLOCK) LEFT JOIN tBeneficiario B  WITH (NOLOCK) ON tce.cIdRFC = B.dRFC  INNER JOIN tBeneficiarioCuentasBancarias BCB  WITH (NOLOCK) ON tCE.cIdRFC =	BCB.dRFC  LEFT JOIN pCatalogoTipoDocumento CTD  WITH (NOLOCK) ON tCE.cIdTipoDocumento =	CTD.cIdTipoDocumento  WHERE tCE.nFolioOperAjenas  in (" + rs.getString(1) + ") ";
                pstmntHLayout = conn.prepareStatement(SqlLayoutGrabado);
                pstmntHLayout.executeUpdate();
                String Sql2 = " SELECT  '1' AS ID_EVENTO,   '000' AS EVENTO,  ltrim(TCEP.cRamo) ID_RAMO_ML,  'RHQ',  TCEP.aEjercicioFiscal,  TCEP.cGrupoFuncional,  tCEP.cFuncion,	 tCEP.cSubFuncion,  tCEP.cProgramaGeneral,  tCEP.cActividadInstitucional,  tCEP.cProgramaPresupuestario, 	ltrim(substring(cpartida,1,1)) CCAP_157,  substring(cpartida,2,1) CCON_158,  substring(cpartida,3,1) CPARG_300, substring(cpartida,4,2) CPAR_159,  tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, tCEP.cCartera, ltrim('0000000' + tCEP.cUnidadEjecutora),  substring(TCEP.cUnidadNorativa,2,2) CCOP_163, '000' PL,  '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3,  CONVERT(decimal(17, 2), Sum(TPDD.mTotal)) mImporteNeto,  MONTH(GETDATE()) MES_149,  '0' NRES,  ISNULL( CLCS.NCOM_15, '') as numcompromiso,  '',  '',  ltrim('PN') TIPO_CONTRATO,  '000' CONC_MOV, isnull(TEE.solicitudPago, 0) nFolioSICOP, CASE ISNULL( CLCS.SPAG_176, '') WHEN '' THEN '' ELSE ISNULL(CLCS.CBEN_16, '') END,  '' 	FROM tOperAjenasDetalle TPDD with(nolock) INNER JOIN tOperAjenasEncabezado TPDE with(nolock) ON 		 TPDD.nFolioOperAjenas  = TPDE.nFolioOperAjenas INNER JOIN 		 tEjercidoEncabezado TEE with(nolock) ON TEE.canocontrarrecibo = TPDD.canocontrarrecibo INNER JOIN      tCatalogoEP TCEP with(nolock) ON rtrim(TCEP.EP) = rtrim(TPDD.EP) LEFT JOIN  (select DISTINCT NCOM_15, CBEN_16, NCTR_47, SPAG_176  from CLC_SICOP with(nolock) ) CLCS ON        CLCS.NCTR_47 = TPDD.canocontrarrecibo AND CLCS.SPAG_176 = TEE.solicitudPago AND CLCS.NCOM_15 IS NOT NULL  WHERE TPDD.nFolioOperAjenas  in (" + rs.getString(1) + ")  group by TCEP.cRamo, 		TCEP.aEjercicioFiscal,		TCEP.cGrupoFuncional, 		tCEP.cFuncion, 		tCEP.cSubFuncion, 		tCEP.cProgramaGeneral,		tCEP.cActividadInstitucional,		tCEP.cProgramaPresupuestario, 		cpartida, 		tCEP.cTipoGasto, 		tCEP.cFuenteFinanciamiento, 		tCEP.cEntidadFederativa, 		tCEP.cCartera, 		tCEP.cUnidadEjecutora, 		substring(TCEP.cUnidadNorativa,2,2),      TEE.solicitudPago,  TPDD.canocontrarrecibo, CLCS.NCOM_15, CLCS.CBEN_16, CLCS.SPAG_176 ";
                pstmntD = conn.prepareStatement(Sql2);
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 38; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                String SqlLayoutGrabadoDet = " INSERT INTO tLayoutsCreadosOperAjenaDetalle SELECT DISTINCT " + nFolioCompromiso + ", '1' ID_EVENTO,'24.0.001' EVENTO,ltrim(TCEP.cRamo) ID_RAMO_ML, 'RHQ', TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, tCEP.cProgramaGeneral, tCEP.cActividadInstitucional, tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)) CCAP_157, substring(cpartida,2,1) CCON_158, substring(cpartida,3,1) CPARG_300, substring(cpartida,4,2) CPAR_159, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, tCEP.cCartera,ltrim('0000000' + tCEP.cUnidadEjecutora), substring(TCEP.cUnidadNorativa,2,2) CCOP_163, '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3, TPDD.mTotal, MONTH(GETDATE()) MES_149, '0' NRES,ltrim('PN') TIPO_CONTRATO, '000' CONC_MOV, 0, 0, 0, 0, 0, 0, 0, '' id_ctr_intdet  FROM tOperAjenasDetalle TPDD  WITH (NOLOCK) inner join tOperAjenasEncabezado TPDE  WITH (NOLOCK)  on TPDD.nFolioOperAjenas  = TPDE.nFolioOperAjenas   inner join tCatalogoEP TCEP  WITH (NOLOCK) on TPDD.EP = TCEP.EP  where TPDD.nFolioOperAjenas in (" + listaIds + ") ";
                pstmntHLayoutDet = conn.prepareStatement(SqlLayoutGrabadoDet);
                pstmntHLayoutDet.executeUpdate();
                // PSC Aqui termina el grabado dentro de layouts creados detalle
                conn.commit();
            }
        } finally {
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmntH);
            CloseObject.closeObject(pstmntHLayout);
            CloseObject.closeObject(pstmntHLayoutDet);
        }
        return arrListaComp;
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            String Sql = " SELECT 		nFolioOperAjenas , 'H' H, cRamo, 'RHQ', '' SOL_PAGO, 		0 cIdTipoPagoDirecto, caNoContrarrecibo FOLIO_INTERNO, caNoContrarrecibo COMODIN 	FROM  dbo.tOperAjenasEncabezado (nolock)	WHERE nFolioOperAjenas  in (" + listaIds + " ) ";
            pstmntH = conn.prepareStatement(Sql);
            System.out.println(Sql);
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                // aqui es donde se modifica documentación comprobatoria
                String Sql2 = " SELECT DISTINCT PDE.cRamo, DCD.DCD_FACTURA,	CONVERT(nvarchar(10), DCD.fAplicacion,103),  CONVERT(nvarchar(10), DCD.fRecepcion,103) + ' 12:00:00 a.m.', DCD.DCD_CBEN,	 case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen', DCD.DCD_TIPO_OPE, DCD.DCD_TIVA 'TIVA',  CONVERT(decimal(17, 2), DCD.DCD_VALOR),  CONVERT(decimal(17, 2), DCD.DCD_IMP_BRUTO) BRUTO,  CONVERT(decimal(17, 2), DCD.DCD_IVADES) IVA,  CONVERT(decimal(17, 2), DCD.DCD_IVA) RETIVA,  CONVERT(decimal(17, 2), DCD.DCD_ISR) ISR,  CONVERT(decimal(17, 2), DCD.DCD_MIL5) R5MILLAR,  CONVERT(decimal(17, 2), DCD.DCD_MIL2) R2MILLAS,  CONVERT(decimal(17, 2), DCD.DCD_OTRAS_RET) OTRASRET,  CONVERT(decimal(17, 2), DCD.DCD_PENALIZACION) PENALIZA,  CONVERT(decimal(17, 2), DCD.DCD_CONTRIBUCION) CONTRIB,  DCD.DCD_CTOEXT,  PDE.cIdDocumento,  DCD.cConcepto  from dbo.tOperAjenasEncabezado PDE (nolock) inner join dbo.v_pagosDocComprobatoria DCD (nolock)  on PDE.caNoContrarrecibo = DCD.caNoContrarrecibo  inner join [dbo].[tBeneficiario] B (nolock) on PDE.cIdRFC = B.dRFC  inner join [dbo].[CAT_TIPO_IVA] TI (nolock) on TI.TIVA = DCD.DCD_TIVA  where PDE.nFolioOperAjenas  = " + nFolioCompromiso;
                log.debug("Inicia la documentacion comprobatoria");
                pstmntD = conn.prepareStatement(Sql2);
                log.debug("Object: {}", Sql2.toString());
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
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmntH);
        }
        return arrListaComp;
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tOperAjenasEncabezado SET nEnviadoSICOP = 1 WHERE nFolioOperAjenas  in (" + listaIds + ")");
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
            pstmnt = conn.prepareStatement("UPDATE tOperAjenasEncabezado SET nEnviadoSICOP = 0 WHERE nFolioOperAjenas in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso) throws SQLException {
        PreparedStatement pstmntL = null;
        String queryUpdateEstatus = "UPDATE tOperAjenasEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + "  WHERE caNoCompromiso = '" + caNoCompromiso + "'";
        try {
            pstmntL = conn.prepareStatement(queryUpdateEstatus);
            pstmntL.executeUpdate();
            return true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
    }

    public static boolean insertRegisterLayout(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean insertReg;
        String queryInsert = "INSERT INTO tLayoutCompromisos( cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso, cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP,  nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud, cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC, caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento, nDocumento,cDescripcion)             " + "VALUES('" + clave + "','" + cRamo + "','" + cUnidadResponsable + "','" + folioSICOP + "','" + idProceso + "','" + cCentroContable + "','" + fExpedicion + "'," + total + ",'" + cTipoPoliza + "','" + nFolioPoliza + "','" + nPolizaCancelacion + "','" + tipoMovimiento + "','" + origenPresupuesto + "','" + cuentaBancaria + "','" + noSolicitud + "','" + tCambio + "','" + tMoneda + "','" + tSolicitud + "','" + volante + "','" + rfc + "','" + caNoCompromiso + "','" + codSemarnat2 + "','" + estatus + "','" + fAplicacion + "','" + documento + "','" + nDocumento + "','" + descripcion + "')";
        try {
            pstmnt = conn.prepareStatement(queryInsert);
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

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static ArrayList<String> buscaAjenasIntegradas(Connection conn, String cxpAI, String fecha, String cBancaria, String Leyenda, String nFolio, String cBEN, int tipo) throws Exception {
        CallableStatement cll = null;
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            // ENCABEZADO DE LAYOUT OP AJENA INTEGRADA
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT ").append("tCE.nFolioOperAjenasInt, 'H' AS Header, ").append("CONVERT(nvarchar(10), tCE.fCaptura, 103) FECHA_EXP, ").append("CONVERT(nvarchar(10), tCE.fCaptura, 103) FECHA_APL, ").append("tCE.cRamo, tCE.cRamo, tCE.cRamo, ").append("'RHQ' ID_UNIDAD, 'RHQ' ID_UNIDAD_CR, 'RHQ' ID_UNIDAD_REC, ").append("CASE WHEN opa.cIdTipoDocumento = 'L' THEN 'I' ELSE opa.cIdTipoDocumento END AS TIPO_MOVTO, ").append("'1' AS OrigenPpto, 'MXN' TipoMoneda, '1.00' TipoCambio, ").append("CASE WHEN opa.cIdTipodocumento = 'O' THEN '1' ELSE '2' END TIPO_PAGO, ").append("'").append(Leyenda).append("' CveLeyenda, ").append("'").append(cBEN).append("' CBEN, ").append("'").append(cBancaria).append("' CUENTA_BANCARIA, ").append("RTRIM(tCE.cIdRFC) RFC, 'OTR', '' FechaReferencia, ").append("LEFT(opa.cConcepto, 40) Referencia1, '' Referencia2, ").append("LEFT(opa.cConcepto, 40), '' AMF, ").append("RTRIM(tCE.caNoContrarreciboInt) NO_ACMI, ").append("RTRIM(tCE.caNoContrarreciboInt) AuxiliarComodin, ").append("'' CTR, '' FolioDC ").append("FROM tOperAjenasIntegradoEnc tCE (NOLOCK) ").append("LEFT JOIN tBeneficiario B (NOLOCK) ON tCE.cIdRFC = B.dRFC ").append("INNER JOIN tBeneficiarioCuentasBancarias BCB (NOLOCK) ON tCE.cIdRFC = BCB.dRFC ").append("INNER JOIN tGrupoOpAjenas opa (NOLOCK) ON opa.nIdGrupoOpAjena = tCE.cBeneficiario ").append("WHERE tCE.nFolioOperAjenasInt = ").append(nFolio);
            pstmntH = conn.prepareStatement(sql.toString());
            rs = pstmntH.executeQuery();
            if (rs.next()) {
                String vreferencia = rs.getString(22);
                if (vreferencia != null) {
                    vreferencia = vreferencia.trim().replaceAll("[,:;()/%/]", " ");
                } else {
                    vreferencia = "";
                }
                StringJoiner joiner = new StringJoiner(",");
                joiner.add(rs.getString(2)).add(rs.getString(3)).add(trim(rs.getString(4))).add(trim(rs.getString(5))).add(trim(rs.getString(6))).add(trim(rs.getString(7))).add(trim(rs.getString(8))).add(trim(rs.getString(9))).add(trim(rs.getString(10))).add(trim(rs.getString(11))).add(trim(rs.getString(12))).add(trim(rs.getString(13))).add(trim(rs.getString(14))).add(trim(rs.getString(15))).add(Leyenda).add(trim(rs.getString(17))).add(cBancaria).add(trim(rs.getString(19))).add(trim(rs.getString(20))).add(trim(rs.getString(21))).add(vreferencia).add(trim(rs.getString(23))).add(vreferencia).add(trim(rs.getString(25))).add(trim(rs.getString(26))).add(trim(rs.getString(27))).add(trim(rs.getString(28))).add(trim(rs.getString(29)));
                String encabezado = joiner.toString() + "\r\n";
                arrListaComp.add(encabezado);
            }
            String query = "";
            if (tipo == 1) {
                rs2 = generaLayoutDetalle(conn, nFolio);
            } else if (tipo == 2) {
                query = "{call sp_generaLayoutOA( ? )}";
                cll = conn.prepareCall(query);
                cll.setInt(1, Integer.parseInt(nFolio));
                rs2 = cll.executeQuery();
            } else {
                query = "{call sp_generaLayoutOA2( ? )}";
                cll = conn.prepareCall(query);
                cll.setInt(1, Integer.parseInt(nFolio));
                rs2 = cll.executeQuery();
            }
            while (rs2.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < 38; i++) {
                    detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                    token = ",";
                }
                token = "";
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(cll);
            CloseObject.closeObject(pstmntH);
        }
        return arrListaComp;
    }

    public static ResultSet generaLayoutDetalle(Connection conn, String nFolio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        // DETALLE LAYOUT OP AJENA INTEGRADA
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT '796' AS ID_EVENTO, '401_CLC_T' AS EVENTO, ");
        sql.append(" LTRIM(TCEP.cRamo) ID_RAMO_ML, 'RHQ', ");
        sql.append(" TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, ");
        sql.append(" tCEP.cFuncion, tCEP.cSubFuncion, ");
        sql.append(" CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, ");
        sql.append(" tCEP.cActividadInstitucional, ");
        sql.append(" tCEP.cProgramaPresupuestario, ");
        sql.append(" LTRIM(SUBSTRING(cpartida,1,1)) CCAP_157, SUBSTRING(cpartida,2,1) CCON_158, SUBSTRING(cpartida,3,1) CPARG_300, ");
        sql.append(" SUBSTRING(cpartida,4,2) CPAR_159, ");
        sql.append(" tCEP.cTipoGasto, ");
        sql.append(" tCEP.cFuenteFinanciamiento, ");
        sql.append(" tCEP.cEntidadFederativa, ");
        sql.append(" SUBSTRING(dbo.CambiaEPCarteraMeta(TPDD.EP),45,11) cCartera, ");
        sql.append(" '0000000000', '00' CCOP_163, '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3, ");
        sql.append(" CONVERT(decimal(17,2), SUM(TPDD.mTotal)) mImporteNeto, ");
        sql.append(" (SELECT dbo.fn_mesPagadoSicopCXP(TPDD.caNoContrarrecibo)) AS MES_149, ");
        sql.append(" 0 ImportePpto, ");
        sql.append(" COALESCE((SELECT nfolioautsicop FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = LAYOUT.sAuxiliarComodin), ");
        sql.append(" (SELECT nfolioautsicop FROM tCompromisoEncabezado WHERE cIdContrato = TPDD.canocontrarrecibo), ");
        sql.append(" CASE WHEN ISNULL((SELECT TOP 1 NCOM_15 FROM CLC_SICOP (NOLOCK) WHERE NCTR_47 = TPDD.caNoContrarrecibo), '') = '' ");
        sql.append(" 	THEN '' ELSE RIGHT(REPLICATE('0',6) + (SELECT TOP 1 NCOM_15 FROM CLC_SICOP (NOLOCK) WHERE NCTR_47 = TPDD.caNoContrarrecibo), 6) END) numcompromiso, ");
        sql.append(" CASE WHEN COALESCE((SELECT nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = LAYOUT.sAuxiliarComodin), ");
        sql.append("	(SELECT nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = TPDD.canocontrarrecibo), '') = -1 THEN '' ");
        sql.append("	ELSE COALESCE((SELECT nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = LAYOUT.sAuxiliarComodin), ");
        sql.append("	(SELECT nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = TPDD.canocontrarrecibo), '') END suficiencia, ");
        sql.append(" '' NO_OLI, 'PN' TIPO_CONTRATO, '000' CONC_MOV, ");
        sql.append(" COALESCE(rete.nfolioSolSICOP, ISNULL(TEE.solicitudPago, 0)) nFolioSICOP, ");
        sql.append(" COALESCE((SELECT TOP 1 BENE_69 FROM CLC_SICOP WITH (NOLOCK) WHERE NCTR_47 = Layout.sAuxiliarComodin), ");
        sql.append(" ISNULL((SELECT TOP 1 BENE_69 FROM CLC_SICOP (NOLOCK) WHERE NCTR_47 = TPDD.caNoContrarrecibo), '')) CBEN, ");
        sql.append(" TPDD.caNoContrarrecibo ");
        sql.append(" FROM tOperAjenasIntegradoDet TPDD WITH (NOLOCK) ");
        sql.append(" INNER JOIN tOperAjenasIntegradoEnc TPDE WITH (NOLOCK) ON TPDD.nFolioOperAjenasInt = TPDE.nFolioOperAjenasInt ");
        sql.append(" INNER JOIN tEjercidoEncabezado TEE WITH (NOLOCK) ON TEE.canocontrarrecibo = TPDD.canocontrarrecibo ");
        sql.append(" INNER JOIN tCatalogoEP TCEP WITH (NOLOCK) ON RTRIM(TCEP.EP) = RTRIM(TPDD.EP) ");
        sql.append(" LEFT JOIN tRetencionEncabezado rete WITH (NOLOCK) ON rete.caNoContrarrecibo = TPDD.caNoContrarrecibo AND rete.cDocumentoHaplicado = 'S' ");
        sql.append(" LEFT JOIN tLayoutsCreadosRelacionGastosHeader LAYOUT WITH (NOLOCK) ON TPDD.caNoContrarrecibo = LAYOUT.sNoContrarrecibo AND cEstatus = 'ACTIVO' ");
        sql.append(" WHERE TPDE.nFolioOperAjenasInt = " + nFolio);
        sql.append(" GROUP BY TCEP.cRamo, TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, tCEP.cFuncion, ");
        sql.append(" tCEP.cSubFuncion, tCEP.cProgramaGeneral, tCEP.cActividadInstitucional, ");
        sql.append(" tCEP.cProgramaPresupuestario, cpartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, ");
        sql.append(" tCEP.cEntidadFederativa, TPDD.cMes, SUBSTRING(dbo.CambiaEPCarteraMeta(TPDD.EP),45,11), ");
        sql.append(" TEE.solicitudPago, TPDD.canocontrarrecibo, rete.nCompromisoSICOP, rete.nFolioSolSICOP, Layout.sAuxiliarComodin ");
        pst = conn.prepareStatement(sql.toString());
        rs = pst.executeQuery();
        return rs;
    }

    public static boolean esPagoFonden(Connection conn, int nFolioPago) throws Exception {
        int fonden = 0;
        StringBuilder queryIntegraAjenas = new StringBuilder();
        queryIntegraAjenas.append("SELECT ajenasDetalle.cTipoDoc,  ajenasDetalle.nFolioDoc, ajenasDetalle.caNoContrarrecibo 	").append(" FROM   toperajenasencabezado ajenasEncabezado WITH(nolock) ").append(" INNER JOIN  toperajenasdetalle ajenasDetalle WITH(nolock) ").append(" 	ON ajenasEncabezado.nFolioOperAjenas = ajenasDetalle.nFolioOperAjenas  	").append("	WHERE  ajenasEncabezado.nFolioOperAjenas = ?");
        String queryBuscaFonden = "";
        ResultSet rsIntegraAjenas = null;
        ResultSet rsBuscaFonden = null;
        PreparedStatement psIntegraAjenas = null;
        PreparedStatement psBuscaFonden = null;
        try {
            psIntegraAjenas = conn.prepareStatement(queryIntegraAjenas.toString());
            psIntegraAjenas.setInt(1, nFolioPago);
            rsIntegraAjenas = psIntegraAjenas.executeQuery();
            while (rsIntegraAjenas.next()) {
                String tipoPago = rsIntegraAjenas.getString("cTipoDoc");
                int folioPago = rsIntegraAjenas.getInt("nFolioDoc");
                if ("FEDERALIZADO".equals(tipoPago)) {
                    tipoPago = "PagoFederalizado";
                }
                queryBuscaFonden = "SELECT Count(*) AS esPagoFonden FROM   t" + tipoPago + "encabezado encabezado WITH(nolock)         INNER JOIN t" + tipoPago + "detalle detalle WITH(nolock)  ON encabezado.nfolio" + tipoPago + " = detalle.nfolio" + tipoPago + " WHERE  encabezado.nfolio" + tipoPago + "= " + folioPago + "       AND detalle.id_tipo_concepto IN ( 'PC', 'PB' ) ";
                psBuscaFonden = conn.prepareStatement(queryBuscaFonden);
                rsBuscaFonden = psBuscaFonden.executeQuery();
                while (rsBuscaFonden.next()) {
                    fonden += rsBuscaFonden.getInt(1);
                }
                psBuscaFonden.close();
                psBuscaFonden = null;
            }
        } finally {
            CloseObject.closeObject(rsIntegraAjenas);
            CloseObject.closeObject(rsBuscaFonden);
            CloseObject.closeObject(psIntegraAjenas);
            CloseObject.closeObject(psBuscaFonden);
        }
        return fonden > 0;
    }

    public static String calculaEventoFonden(String ep) {
        int capitulo = Integer.parseInt(ep.substring(32, 33));
        String evento = "";
        switch(capitulo) {
            case 1:
            case 2:
            case 3:
                evento = "PF_AJENA123";
                break;
            case 4:
                evento = "PF_AJENA4";
                break;
            case 5:
            case 6:
                evento = "PF_AJENA4";
                break;
            default:
                break;
        }
        return evento;
    }

    public static String[][] buscaSolicitudes(Connection conn, String finicio, String fFin, int tipo, boolean esIP, boolean esRGconOC, int cc, int ejercido) throws Exception {
        String query = "SELECT	caNoContrarrecibo, nFolioPagoDirecto, fAplicacion, tDocumento, EP, ";
        query += camposTipoRetencion.get(tipo);
        query += tipoTabla.get(ejercido);
        query += " WHERE faplicacion BETWEEN CAST('" + finicio + "' AS DATE) AND CAST('" + fFin + "' AS DATE)";
        query += esIP ? " AND SUBSTRING(ep,40,1) = 4 " : " AND SUBSTRING(ep,40,1) <> 4  ";
        query += camposTipoCondicion.get(tipo);
        query += " and cCentroContable = " + cc;
        log.debug("Object: {}", query.toString());
        String[][] retVal = CatalogosManager.getSelectQuery(conn, query);
        return retVal;
    }

    public static EgresoEncabezado cargEncabezado(Connection conn, int folioEgreso) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT  cBeneficiario AS beneficiario,");
        query.append("        cCentroContable AS centroContable,");
        query.append("        cConcepto AS concepto,");
        query.append("        caNoContrarrecibo AS contrarecibo,");
        query.append("        cDescripcionPoliza AS descripcionPoliza,");
        query.append("        cDocumentoHaplicado AS documentoAplicado,");
        query.append("        ISNULL(aEjercicioFiscal,-1) AS ejercicioFiscal,");
        query.append("        ISNULL(nEnviadoSICOP,-1) AS enviadoSICOP,");
        query.append("        cEsFirmaElectronica AS esFirmaElectronica,");
        query.append("        fAplicacion AS fechaAplicacion,");
        query.append("        fCancelacion AS fechaCancelacion,");
        query.append("        fCaptura AS fechaCaptura,");
        query.append("        fDesde AS fechaDesde,");
        query.append("        FHasta AS fechaHasta,");
        query.append("        sFirmanteAut AS firmanteAut,");
        query.append("        sFirmanteVoBo AS firmanteVoBo,");
        query.append("        ISNULL(nFolioOperAjenas,-1)  AS folioPago,");
        query.append("        ISNULL(nFolioPoliza,-1)  AS folioPoliza,");
        query.append("        ISNULL(nFolioPolizaCancelacion,-1)  AS folioPolizaCancelacion,");
        query.append("        ISNULL(nFolioSICOP,-1)  AS folioSICOP,");
        query.append("        ID_DESTINO_GASTO AS idDestinoGasto,");
        query.append("        cIdTipoDocumento AS idTipoDocumento,");
        query.append("        cIdUsuarioAprobacion AS idUsuarioAprobacion,");
        query.append("        cIdUsuarioCaptura AS idUsuarioCaptura,");
        query.append("        cIdUsuarioRechazo AS idUsuarioRechazo,");
        query.append("        mImportes AS importeBruto,");
        query.append("        mImportes AS importeMasIva,");
        query.append("        mImportes AS importeNeto,");
        query.append("        mImportes AS importes,");
        query.append("        cIngresosPropios AS ingresosPropios,");
        query.append("        U_LOGIN AS login,");
        query.append("        cNombre AS nombre,");
        query.append("        ISNULL(nNumEmpleadoAut,-1)  AS numEmpleadoAut,");
        query.append("        ISNULL(nNumEmpleadoElab,-1) AS numEmpleadoElab,");
        query.append("        ISNULL(nNumEmpleadoVoBo,-1) AS numEmpleadoVoBo,");
        query.append("        NumPagoAMF AS numPagoAMF,");
        query.append("        sPuestoAut AS puestoAut,");
        query.append("        sPuestoVoBo AS puestoVoBo,");
        query.append("        cRadicado AS radicado,");
        query.append("        cRamo AS ramo,");
        query.append("        cIDRFC AS rfc,");
        query.append("        'OPERAJENAS' AS tipoPago,");
        query.append("        cTipoPoliza AS tipoPoliza,");
        query.append("        cUnidadResponsable AS unidadResponsable,");
        query.append("        cUnidadResponsableContable AS unidadResponsableContable");
        query.append("  FROM  tOperAjenasEncabezado (NOLOCK)");
        query.append(" WHERE  nFolioOperAjenas = ?");
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            EgresoOPERAJENASEncabezado encabezado = new EgresoOPERAJENASEncabezado();
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioEgreso);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            // Se convierte el formato de fecha
            DateConverter converter = new DateConverter(null);
            converter.setPatterns(new String[] { "dd/mm/yyyy", "yyyy-MM-dd" });
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(encabezado, resultObj);
            return encabezado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    /**
     * Valida que el caso tenga actualizado su ID Gabinete.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param c
     *            Caso a validar
     * @throws Exception
     */
    public static void validaGabineteCaso(Connection conn, Caso c) throws Exception {
        if (c.getIdGabinete() <= 0) {
            PreparedStatement psUpdate = null;
            PreparedStatement psSel = null;
            ResultSet rsIDGabinete = null;
            StringBuilder queryUpdate = new StringBuilder();
            queryUpdate.append("UPDATE cg_caso SET c_id_gabinete = ? WHERE c_folio = ?");
            StringBuilder queryConsultaGabinete = new StringBuilder();
            queryConsultaGabinete.append("SELECT id_gabinete  WITH (NOLOCK) FROM imxoperajenas WHERE folio = ?");
            try {
                psSel = conn.prepareStatement(queryConsultaGabinete.toString());
                psSel.setString(1, c.getFolio());
                rsIDGabinete = psSel.executeQuery();
                if (rsIDGabinete.next()) {
                    int idGabiente = rsIDGabinete.getInt(1);
                    if (idGabiente < 0)
                        throw new Exception("No se encontro gabinete para el folio: " + c.getFolio() + " por lo que no puede continuar el proceso.");
                    psUpdate = conn.prepareStatement(queryUpdate.toString());
                    psUpdate.setInt(1, idGabiente);
                    psUpdate.setString(2, c.getFolio());
                    psUpdate.executeUpdate();
                    c.setIdGabinete(idGabiente);
                } else
                    throw new Exception("No se encontro gabinete para el folio: " + c.getFolio() + " por lo que no puede continuar el proceso.");
            } finally {
                CloseObject.closeObject(rsIDGabinete);
                CloseObject.closeObject(psUpdate);
                CloseObject.closeObject(psSel);
            }
        }
    }

    public static void cancelaOperacionAjena(Connection conn, int nFolioOperAjenas) throws Exception {
        PreparedStatement psUpdate = null;
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE	tOperAjenasEncabezado ");
        queryUpdate.append("   SET	cDocumentoHAplicado = 'C', nEnviadoSICOP = ");
        queryUpdate.append(SolicitudFirmaElectronica.SOLICITUD_CANCELADA);
        queryUpdate.append(" WHERE nFolioOperAjenas = ?");
        try {
            psUpdate = conn.prepareStatement(queryUpdate.toString());
            psUpdate.setInt(1, nFolioOperAjenas);
            psUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    public static boolean borraTabla(Connection conn) throws Exception {
        boolean borrado = false;
        PreparedStatement pstmnt = null;
        try {
            String querySelect = "DELETE CLC_SOLXPAGAR";
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

    public static void insertarDatosCLC(Connection conn, List<CLC_SOLXPAGAR> renglonesArchivo) throws Exception {
        PreparedStatement ps = null;
        String sql = "INSERT INTO CLC_SOLXPAGAR (ID_EVENTO, EVENTO, ID_RAMO_ML, ID_UNIDAD_ML, CANI, CGFU, CFUN, CSFU, CPRG, CAIN, CPPT, CCAP, CCON, CPARG, CPAR, CTGA, CFIN, CCAU, CCOP, CGEO, CPLA, CPPI, OFIN, AUX1, AUX2, AUX3, IMPORTE_148, MES_149, NCOM_15, CBEN_16, NRES_17, NOIF_18, REMA_ISR_192, REM_IVA_193, REM_5MIL_194, IMP_NETNEG_200, REMA_CONTRUB_202, PPAG_177, TNOM_178, TCONC_49, CONC_MOV_50, SPAG_176, PROCESO, ID_UNIDAD_CR, REM_2MIL_315, REM_OTRET_316, REM_PENA_317) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(sql);
            int batchSize = 1000;
            int count = 0;
            for (CLC_SOLXPAGAR item : renglonesArchivo) {
                ps.setLong(1, item.getIdEvento());
                ps.setString(2, item.getEvento());
                ps.setString(3, item.getIdRamoMl());
                ps.setString(4, item.getIdUnidadMl());
                ps.setString(5, item.getCani());
                ps.setString(6, item.getCgfu());
                ps.setString(7, item.getCfun());
                ps.setString(8, item.getCsfu());
                ps.setString(9, item.getCprg());
                ps.setString(10, item.getCain());
                ps.setString(11, item.getCppt());
                ps.setString(12, item.getCcap());
                ps.setString(13, item.getCcon());
                ps.setString(14, item.getCparg());
                ps.setString(15, item.getCpar());
                ps.setString(16, item.getCtga());
                ps.setString(17, item.getCfin());
                ps.setString(18, item.getCcau());
                ps.setString(19, item.getCcop());
                ps.setString(20, item.getCgeo());
                ps.setString(21, item.getCpla());
                ps.setString(22, item.getCppi());
                ps.setString(23, item.getOfin());
                ps.setString(24, item.getAux1());
                ps.setString(25, item.getAux2());
                ps.setString(26, item.getAux3());
                ps.setBigDecimal(27, item.getImporte148());
                ps.setString(28, item.getMes149());
                ps.setString(29, item.getNcom15());
                ps.setString(30, item.getCben16());
                ps.setString(31, item.getNres17());
                ps.setString(32, item.getNoif18());
                ps.setBigDecimal(33, item.getRemaIsr192());
                ps.setBigDecimal(34, item.getRemIva193());
                ps.setBigDecimal(35, item.getRem5mil194());
                ps.setBigDecimal(36, item.getImpNetneg200());
                ps.setBigDecimal(37, item.getRemaContrib202());
                ps.setString(38, item.getPpag177());
                ps.setString(39, item.getTnom178());
                ps.setString(40, item.getTconc49());
                ps.setString(41, item.getConcMov50());
                ps.setString(42, item.getSpag176());
                ps.setString(43, item.getProceso());
                ps.setString(44, item.getIdUnidadCr());
                ps.setBigDecimal(45, item.getRem2mil315());
                ps.setBigDecimal(46, item.getRemOtret316());
                ps.setBigDecimal(47, item.getRemPena317());
                ps.addBatch();
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                    conn.commit();
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
}
