package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.syc.contable.caja.core.CajaManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagosDiversosRGManager {

    private static final Logger log = LoggerFactory.getLogger(CajaManager.class);

    public PagosDiversosRGManager() {
        super();
    }

    public static ArrayList<String> buscaPagosDiversosRGIntegrados2(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, Usuario usuario, String sTimeStamp, String folioGenerator) throws Exception {
        String sUsuario = usuario.getLogin();
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        PreparedStatement pstmntHLayout = null;
        PreparedStatement pstmntHLayoutDet = null;
        PreparedStatement pstmUpSeqLayout = null;
        PreparedStatement pstmSeqLayout = null;
        String strFolioLayout = "";
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
        boolean esSAIAlterno = false;
        if ("true".equals(cabl.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(cabl.getSystemSetting("SAI_FONDEN"))) {
            esSAIAlterno = true;
        }
        try {
            String[] arrFolios = listaIds.split(",");
            String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
            String[] arrFechas = listaFechas.split(",");
            String[] arrLeyendas = listaLeyendas.split(",");
            // Obtiene Ejercicio Fiscal
            String ejercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
            String sREFERENCIA1_107 = sTimeStamp;
            //URVP SE ARMA EL "SP" ENCABEZADO
            StringBuilder Sql = new StringBuilder();
            Sql.append(" SELECT TOP 1 ");
            Sql.append("	SUBSTRING('" + sREFERENCIA1_107 + "',LEN('" + sREFERENCIA1_107 + "')-7,LEN('" + sREFERENCIA1_107 + "')),");
            Sql.append("	'H' AS Header,");
            Sql.append("	CONVERT(nvarchar(10), GETDATE(),103),");
            Sql.append("	CONVERT(nvarchar(10), GETDATE(),103),");
            Sql.append("	tCE.cRamo,");
            Sql.append("	tCE.cRamo,");
            Sql.append("	tCE.cRamo,");
            Sql.append("	'RHQ' UnidadResponsable,");
            Sql.append("	'RHQ' UnidadResponsable,");
            Sql.append("	'RHQ' UnidadResponsable,");
            Sql.append("	'N' ID_TIPO_MOVIMIENTO,");
            Sql.append("	'1' AS OrigenPpto,");
            Sql.append("	'3' AS TipoSol,");
            Sql.append("	'MXN' TipoMoneda,");
            Sql.append("	'1' TipoCambio,");
            Sql.append("	'1' TIPO_PAGO,");
            Sql.append("	'PENDIENTE' AS CveLeyenda,");
            Sql.append("	'S04929' CBEN,");
            Sql.append("	'" + arrCuentasBancarias[0].trim() + "' CUENTA_BANCARIA,");
            Sql.append("	'16RHQ',");
            Sql.append("	'FAC',");
            Sql.append("	'' FechaReferencia,");
            Sql.append("	'' Referencia1,");
            Sql.append("	'' Referencia2,");
            Sql.append(" 	'Integracion de Pagos Diversos RG " + sREFERENCIA1_107 + "' Concepto,");
            Sql.append("	'' NotasReverso,");
            Sql.append("	'' AMF,");
            Sql.append("	'" + sREFERENCIA1_107 + "' NO_ACMI,");
            Sql.append("	'" + sREFERENCIA1_107 + "' AuxiliarComodin,");
            Sql.append("	'' CTR,");
            Sql.append("	'' FolioDC,");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_ISR),0)),");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_IVADES),0)),");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_MIL5),0)),");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_MIL2),0)),");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_OTRAS_RET),0)),");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_PENALIZACION),0)),");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_CONTRIBUCION),0)),	");
            Sql.append("	CONVERT(decimal(17, 2), ISNULL(SUM(DC.DCD_IVA),0)),");
            Sql.append("	0 IVAANT,");
            Sql.append("	'NA' ID_DESTINO_GASTO ");
            Sql.append("FROM tPAGODIVERSOEncabezado tCE ");
            Sql.append("INNER JOIN v_pagosDocComprobatoria DC WITH (NOLOCK) ON tCE.caNoContrarrecibo = DC.caNoContrarrecibo AND DC.cTipoPago = 'PAGODIVERSO' ");
            Sql.append("WHERE tCE.nFolioPAGODIVERSO IN (" + listaIds + ") ");
            Sql.append("GROUP BY cRamo/*, nFolioPAGODIVERSO, fAplicacion,  cConcepto*/");
            pstmntH = conn.prepareStatement(Sql.toString());
            log.debug(Sql);
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                log.debug("Procesando folio[" + arrFolios[0].trim() + "]");
                String nFolio, nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2) + "," + arrFechas[0].trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + rs.getString(16).trim() + "," + arrLeyendas[0].trim().trim() + "," + rs.getString(18).trim() + "," + rs.getString(19).trim() + "," + rs.getString(20).trim() + "," + rs.getString(21).trim() + "," + rs.getString(22).trim() + "," + rs.getString(23).trim() + "," + rs.getString(24).trim() + "," + rs.getString(25).trim().replaceAll("[\r\n]{2,}", " ") + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim() + "," + rs.getString(30).trim() + "," + rs.getString(31).trim() + "," + rs.getString(32).trim() + "," + rs.getString(33).trim() + "," + rs.getString(34).trim() + "," + rs.getString(35).trim() + "," + rs.getString(36).trim() + "," + rs.getString(37).trim() + "," + rs.getString(38).trim() + "," + rs.getString(39).trim() + "," + rs.getString(40).trim() + "," + rs.getString(41);
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                //OBTENER SEQUENCE DE EJERCIDO
                pstmUpSeqLayout = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK)  SET seq_value = seq_value + 1  WHERE seq_name = 'LAYOUTDIVRG' ");
                pstmUpSeqLayout.executeUpdate();
                pstmSeqLayout = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'LAYOUTDIVRG' ");
                rs4 = pstmSeqLayout.executeQuery();
                if (rs4.next()) {
                    strFolioLayout = rs4.getString("seq_value");
                }
                //	int retval;
                // Aqui grabamos dentro de layouts creados encabezado
                StringBuilder SqlLayoutGrabado = new StringBuilder();
                SqlLayoutGrabado.append("	INSERT INTO tLayoutsCreadosPagosDiversosRGHeader ");
                SqlLayoutGrabado.append("		SELECT " + strFolioLayout + ",");
                SqlLayoutGrabado.append("			getdate(),  ");
                SqlLayoutGrabado.append("			tCE.nFolioPAGODIVERSO, ");
                SqlLayoutGrabado.append("			'H' AS Header, ");
                SqlLayoutGrabado.append("			CONVERT(nvarchar(10), tCE.fAplicacion,103),");
                SqlLayoutGrabado.append("			CONVERT(nvarchar(10), tCE.fAplicacion,103),");
                SqlLayoutGrabado.append("			tCE.cRamo,");
                SqlLayoutGrabado.append("			tCE.cRamo,");
                SqlLayoutGrabado.append("			tCE.cRamo,");
                SqlLayoutGrabado.append("			'RHQ' UnidadResponsable,");
                SqlLayoutGrabado.append("			'RHQ' UnidadResponsable,");
                SqlLayoutGrabado.append("			'RHQ' UnidadResponsable,");
                SqlLayoutGrabado.append("			'N' ID_TIPO_MOVIMIENTO,");
                SqlLayoutGrabado.append("			'1' AS OrigenPpto,");
                SqlLayoutGrabado.append("			'2' AS TipoSol,");
                SqlLayoutGrabado.append("			'MXN' TipoMoneda,");
                SqlLayoutGrabado.append("			'1' TipoCambio,");
                SqlLayoutGrabado.append("			'1' TIPO_PAGO,");
                SqlLayoutGrabado.append("			'1',");
                SqlLayoutGrabado.append("			'S04929' CBEN,");
                SqlLayoutGrabado.append("			'" + arrCuentasBancarias[0].trim() + "',");
                SqlLayoutGrabado.append("			'16RHQ',");
                SqlLayoutGrabado.append("			'FAC', ");
                SqlLayoutGrabado.append("			'' FechaReferencia,");
                SqlLayoutGrabado.append("			'' Referencia1,");
                SqlLayoutGrabado.append("			'' Referencia2,");
                SqlLayoutGrabado.append("			REPLACE(LEFT(tCE.cConcepto, 70),',',''),");
                SqlLayoutGrabado.append("			'' NotasReverso,");
                SqlLayoutGrabado.append("			'' AMF,");
                SqlLayoutGrabado.append("			rtrim(tCE.caNoContrarrecibo) NO_ACMI,");
                SqlLayoutGrabado.append("			'" + sREFERENCIA1_107 + "' AuxiliarComodin,");
                SqlLayoutGrabado.append("			'' CTR,");
                SqlLayoutGrabado.append("			'' FolioDC,");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DC.DCD_ISR,0)),");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DCD_IVA,0)),");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL5,0)),");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DCD_MIL2,0)),");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DCD_OTRAS_RET,0)),");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DC.DCD_PENALIZACION,0)),");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DC.DCD_CONTRIBUCION,0)),	");
                SqlLayoutGrabado.append("			CONVERT(decimal(17, 2), ISNULL(DC.DCD_IVADES,0)),	");
                SqlLayoutGrabado.append("			0 IVAANT,");
                SqlLayoutGrabado.append("			tCE.ID_DESTINO_GASTO,");
                SqlLayoutGrabado.append("			'" + sUsuario + "',");
                SqlLayoutGrabado.append("			'ACTIVO',");
                SqlLayoutGrabado.append("			NULL, ");
                SqlLayoutGrabado.append("			NULL");
                SqlLayoutGrabado.append("		FROM tPAGODIVERSOEncabezado tCE WITH (NOLOCK) ");
                SqlLayoutGrabado.append("		LEFT JOIN tBeneficiario B WITH (NOLOCK) ");
                SqlLayoutGrabado.append("			ON tce.RFC = B.dRFC");
                SqlLayoutGrabado.append("		LEFT JOIN pCatalogoTipoDocumento CTD WITH (NOLOCK) ");
                SqlLayoutGrabado.append("			ON tCE.cIdTipoDocumento = CTD.cIdTipoDocumento");
                SqlLayoutGrabado.append("		LEFT JOIN v_pagosDocComprobatoria DC WITH (NOLOCK) ");
                SqlLayoutGrabado.append("			ON DC.caNoContrarrecibo = tCE.caNoContrarrecibo AND DC.cTipoPago = 'PAGODIVERSO' ");
                SqlLayoutGrabado.append("		WHERE tCE.nFolioPAGODIVERSO IN (" + listaIds + ")");
                pstmntHLayout = conn.prepareStatement(SqlLayoutGrabado.toString());
                pstmntHLayout.executeUpdate();
                // PSC Aqui termina el grabado dentro de layouts creados encabezado
                //URVP SE ARMA EL "SP" DETALLE
                StringBuilder Sql2 = new StringBuilder();
                Sql2.append("	SELECT '1' ID_EVENTO, ");
                Sql2.append("	'24.0.001' EVENTO, ");
                Sql2.append("	SUBSTRING(PDD.EP,6,2) ID_RAMO_ML, ");
                Sql2.append("	'RHQ', ");
                Sql2.append("	SUBSTRING(PDD.EP,1,4) aEjercicioFiscal, ");
                Sql2.append("	SUBSTRING(PDD.EP,13,1) cGrupoFuncional, ");
                Sql2.append("	SUBSTRING(PDD.EP,15,1) cFuncion, ");
                Sql2.append("	SUBSTRING(PDD.EP,17,2) cSubFuncion, ");
                Sql2.append("	CASE WHEN SUBSTRING(PDD.EP,20,2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE SUBSTRING(PDD.EP,20,2) END AS cProgramaGeneral, ");
                Sql2.append("	SUBSTRING(PDD.EP,23,3) cActividadInstitucional, ");
                Sql2.append("	SUBSTRING(PDD.EP,27,4) cProgramaPresupuestario,  ");
                Sql2.append("	SUBSTRING(PDD.EP,32,1) CCAP_157, ");
                Sql2.append("	SUBSTRING(PDD.EP,33,1)CCON_158, ");
                Sql2.append("	SUBSTRING(PDD.EP,34,1) CPARG_300, ");
                Sql2.append("	SUBSTRING(PDD.EP,35,2) CPAR_159, ");
                Sql2.append("	SUBSTRING(PDD.EP,38,1) cTipoGasto, ");
                Sql2.append("	SUBSTRING(PDD.EP,40,1) cFuenteFinanciamiento, ");
                Sql2.append("	SUBSTRING(PDD.EP,42,2) cEntidadFederativa, ");
                Sql2.append("	SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11)  cCartera, ");
                Sql2.append("	'0000000000', ");
                Sql2.append("	'00'CCOP_163, ");
                Sql2.append("	'000' PL, ");
                Sql2.append("	'000' OFI, ");
                Sql2.append("	'00000' AUX1, ");
                Sql2.append("	'00000' AUX2, ");
                Sql2.append("	'0000000000' AUX3, ");
                if (esSAIAlterno) {
                    Sql2.append(" '0' AS NCOM_35, ");
                } else {
                    Sql2.append("	(SELECT nFolioAutSICOP FROM tCompromisoEncabezado CompEnc WITH (NOLOCK) WHERE nFolioCompromiso = ( SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = PDE.cFolioPAGODIVERSO and cDocumentoHaplicado = 'S' ) ) AS NCOM_35, ");
                }
                Sql2.append("	CONVERT(decimal(17, 2),SUM(PDD.mImporteNeto)) MONTO, ");
                Sql2.append("	PDD.cMes AS MES_149, ");
                Sql2.append("	'0' NRES, ");
                Sql2.append("	CASE WHEN SUBSTRING(ep,32,5)='35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO, ");
                Sql2.append("	'000' CONC_MOV, ");
                Sql2.append("	CONVERT(decimal(17,2), Sum(PDD.mISRHonorarios ); PDD.mISRArrenda ); PDD.mimporteISRResico)) DCD_ISR, ");
                Sql2.append("	CONVERT(decimal(17,2), sum(PDD.m23Iva ); PDD.mImporteIvaHonorarios ); PDD.mImporteIvaArrenda ); PDD.mImporteFlete23 ); PDD.mImporteFlete4); isnull(PDD.mImporteIva6,0))) DCD_IVA, ");
                Sql2.append(" 	CONVERT(decimal(17,2), Sum(PDD.mObra5))  DCD_MIL5, ");
                Sql2.append(" 	CONVERT(decimal(17,2), Sum(PDD.mCNIC ); PDD.mIMDT)) DCD_MIL2, ");
                Sql2.append(" 	CONVERT(decimal(17,2), 0) DCD_CONTRIBUCION, ");
                Sql2.append(" 	CONVERT(decimal(17,2), Sum(PDD.mRetImpuestoCedular)) DCD_OTRAS_RET, ");
                Sql2.append("	CONVERT(decimal(17, 2), 0) IVADES_45, ");
                Sql2.append("	CONVERT(decimal(17, 2), 0) ANTICIPO_46, ");
                Sql2.append("	CONVERT( DECIMAL( 17,2), DC.DCD_PENALIZACION )  DCD_PENALIZACION, ");
                Sql2.append("	CONVERT(decimal(17, 2), 0) IVAANT_47, ");
                Sql2.append("	'' id_ctr_intdet ");
                Sql2.append("	FROM tPAGODIVERSODetalle PDD WITH (NOLOCK) ");
                Sql2.append("	INNER JOIN tPAGODIVERSOEncabezado PDE WITH (NOLOCK) ");
                Sql2.append("		ON (PDE.nFolioPAGODIVERSO = PDD.nFolioPAGODIVERSO) ");
                Sql2.append("	LEFT JOIN v_pagosDocComprobatoria DC  ");
                Sql2.append("		ON DC.caNoContrarrecibo = PDE.caNoContrarrecibo AND DC.cTipoPago = 'PAGODIVERSO'  ");
                Sql2.append("	WHERE PDE.nFolioPAGODIVERSO IN ( " + listaIds + " ) ");
                Sql2.append("	GROUP BY SUBSTRING(PDD.EP,61,3), ");
                Sql2.append("	PDD.EP, PDD.mISRHonorarios,");
                Sql2.append("	PDD.ID_TIPO_CONCEPTO, ");
                Sql2.append("	PDE.cFolioPAGODIVERSO, PDD.cMes, ");
                Sql2.append(" 	SUBSTRING(rtrim(PDD.EP),45,11), ");
                Sql2.append(" 	SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11),DC.DCD_PENALIZACION ");
                pstmntD = conn.prepareStatement(Sql2.toString());
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 43; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
            }
            insertaConsolidacionPagosDiversosRG(conn, sTimeStamp, usuario, ejercicioFiscal, folioGenerator);
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(rs4, false);
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(pstmntD, false);
            CloseObject.closeObject(pstmntHLayout, false);
            CloseObject.closeObject(pstmntHLayoutDet, false);
            CloseObject.closeObject(pstmUpSeqLayout, false);
            CloseObject.closeObject(pstmSeqLayout, false);
        }
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds, String sTimeStamp, boolean bIntegra) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String sREFERENCIA1_107 = "'" + sTimeStamp + "'";
        if (bIntegra)
            sREFERENCIA1_107 = "DCD.caNoContrarrecibo";
        // PSC
        //URVP -- ENCABEZADO DEL DOCCOMP
        StringBuilder Sql = new StringBuilder();
        Sql.append("	select distinct ");
        Sql.append("		PDE.nFolioPAGODIVERSO,");
        Sql.append("		'H' H,");
        Sql.append("		PDE.cRamo,");
        Sql.append("		'RHQ',");
        Sql.append("		'' SOL_PAGO,");
        Sql.append("		'3',");
        Sql.append("		" + sREFERENCIA1_107 + " FOLIO_INTERNO,");
        Sql.append(" 		" + sREFERENCIA1_107 + " COMODIN");
        Sql.append("	from  dbo.tPAGODIVERSOEncabezado PDE");
        Sql.append("	inner join dbo.v_pagosDocComprobatoria DCD on PDE.caNoContrarrecibo = DCD.caNoContrarrecibo AND DCD.cTipoPago = 'PAGODIVERSO' ");
        Sql.append("	WHERE PDE.nFolioPAGODIVERSO in (" + listaIds + ") ");
        pstmntH = conn.prepareStatement(Sql.toString());
        System.out.println(Sql);
        rs = pstmntH.executeQuery();
        while (rs.next()) {
            String nFolioCompromiso = rs.getString(1);
            String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
            encabezado = encabezado + "\r\n";
            arrListaComp.add(encabezado);
            String sCampo = "'S04929'";
            if (bIntegra) {
                sCampo = "DCD.DCD_CBEN";
                listaIds = nFolioCompromiso;
            }
            //URVP DETALLE DEL DOCCOMP
            String Sql2 = " select distinct " + "		PDE.cRamo," + "		DCD.DCD_FACTURA, " + "		CONVERT(nvarchar(10), DCD.fAplicacion,103)," + "		CONVERT(nvarchar(10), DCD.fRecepcion,103) + ' 12:00:00 a.m.', " + "		" + sCampo + "," + "		case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen'," + "		DCD.DCD_TIPO_OPE," + "		DCD.DCD_TIVA 'TIVA'," + "		CONVERT(decimal(17, 2), DCD.DCD_VALOR)," + "		CONVERT(decimal(17, 2), DCD.DCD_IMP_BRUTO) MONTO," + "		CONVERT(decimal(17, 2), DCD.DCD_IVA)," + "		CONVERT(decimal(17, 2), DCD.DCD_IVADES)," + "		CONVERT(decimal(17, 2), DCD.DCD_ISR)," + "		CONVERT(decimal(17, 2), DCD.DCD_MIL5)," + "		CONVERT(decimal(17, 2), DCD.DCD_MIL2)," + "		CONVERT(decimal(17, 2), DCD.DCD_OTRAS_RET)," + "		CONVERT(decimal(17, 2), DCD.DCD_PENALIZACION)," + "		CONVERT(decimal(17, 2), DCD.DCD_CONTRIBUCION)," + "		DCD.DCD_CTOEXT," + "		DCD.DCD_FACTURA," + "		DCD.cConcepto," + "		PDE.caNoContrarrecibo" + "	from dbo.tPAGODIVERSOEncabezado PDE WITH (NOLOCK) " + "	INNER JOIN dbo.tPAGODIVERSODetalle RGD WITH (NOLOCK)  ON PDE.nFolioPAGODIVERSO = RGD.nFolioPAGODIVERSO" + "	inner join dbo.v_pagosDocComprobatoria DCD WITH (NOLOCK)  on PDE.caNoContrarrecibo = DCD.caNoContrarrecibo AND DCD.cTipoPago = 'PAGODIVERSO' " + "	inner join dbo.tBeneficiario B WITH (NOLOCK) on PDE.RFC = B.dRFC" + "	left join dbo.CAT_TIPO_IVA TI WITH (NOLOCK) on TI.TIVA = DCD.DCD_TIVA" + "	where PDE.nFolioPAGODIVERSO in (" + listaIds + ")";
            pstmntD = conn.prepareStatement(Sql2);
            System.out.println(Sql2);
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
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(pstmntH);
            if (!bIntegra)
                break;
        }
        return arrListaComp;
    }

    public static int updateHeaderPagosEnvioSICOP(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGODIVERSOEncabezado SET nEnviadoSICOP = 1 WHERE nFolioPAGODIVERSO IN (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return retval;
    }

    public static int insertaConsolidacionPagosDiversosRG(Connection conn, String sTimeStamp, Usuario sUsuario, String ejercicioFiscal, String folioGenerator) throws Exception {
        log.info("Insertando consolidacion para la relacion de gastos. Folio Integracion[" + sTimeStamp + "] Usuario[" + sUsuario + "] Ejercicio Fiscal[" + ejercicioFiscal + "]");
        int insertados = 0;
        String sqlInsertConsolidacion = "INSERT INTO tconsolidacionrelaciongastosencabezado " + "            (nFolioConsolidacion," + "             nidintegracion, " + "             fcarga, " + "             faplicacion, " + "             ctipopoliza, " + "             u_login, " + "             cunidadresponsablecontable, " + "             cdescripcionpoliza, " + "             cramo, " + "             cunidadresponsable, " + "             aejerciciofiscal) " + "SELECT ?								AS nFolioConsolidacion," + "        ?                             AS nIdIntegracion, " + "       Getdate()                     AS fCarga, " + "       Getdate()                     AS fAplicacion, " + "       'IN'                          AS cTipoPoliza, " + "       ?                             AS U_LOGIN, " + "       'RHQ'                         AS cUnidadResponsableContable, " + "       'Poliza de Ingreso Devengado y Recaudado de la Integración " + sTimeStamp + "/DIV' AS cDescripcionPoliza, " + "       '16'                          AS cRamo, " + "       ?                             AS cUnidadResponsable, " + "       ?                             AS aEjercicioFiscal ";
        String sqlInsertConsolidacionDetalle = "INSERT INTO dbo.tconsolidacionrelaciongastosdetalle" + "        ( nDocRenglon ," + "          nFolioConsolidacion ," + "          ep ," + "          cevento ," + "          ccentrocontable ," + "          cmes ," + "          ID_destino_gasto ," + "          ID_TIPO_CONCEPTO ," + "          partida ," + "          tipogasto ," + "          mimportemasiva ," + "          mImporteNegativo ," + "          nidintegracion ," + "          CTAB ," + "          RFC ," + "          ALM ," + "          OBGT" + "        )" + "SELECT Row_number() OVER (ORDER BY nfolioconsolidacion) AS nDocRenglon, " + "       consolidacion_encabezado.nfolioconsolidacion AS nFolioConsolidacion, " + "       ep, " + "       dbo.Fn_define_evento_integracion_rg(ENCABEZADO.id_destino_gasto, " + "       detalle.id_tipo_concepto, Substring(detalle.ep, 32, 5), " + "       Substring(detalle.ep, 38, 1))                AS cevento, " + "       DETALLE.ccentrocontable, " + "       DETALLE.cmes, " + "       ENCABEZADO.id_destino_gasto, " + "       detalle.id_tipo_concepto, " + "       Substring(detalle.ep, 32, 5)                 AS partida, " + "       Substring(detalle.ep, 38, 1)                 AS tipogasto, " + "       Sum(DETALLE.mImporteNeto)                  AS mimportemasiva, " + "       -1 * ( Sum(DETALLE.mImporteNeto) )         AS mImporteNegativo, " + "       LAYOUT.sauxiliarcomodin                      AS nidintegracion, " + "       LAYOUT.scuenta_bancaria                      AS CTAB, " + "       CASE " + "         WHEN ENCABEZADO.rfc <> 'TESOFE' THEN '' " + "         ELSE ENCABEZADO.rfc " + "       END                                          AS RFC, " + "       detalle.alm, " + "       Substring(detalle.ep, 32, 5)                 AS OBGT " + "FROM   dbo.tPAGODIVERSOencabezado ENCABEZADO WITH (nolock) " + "INNER JOIN dbo.tPAGODIVERSOdetalle DETALLE WITH (nolock) " + "		  ON ENCABEZADO.nFolioPAGODIVERSO = DETALLE.nFolioPAGODIVERSO " + "LEFT OUTER JOIN dbo.tLayoutsCreadosPagosDiversosRGHeader LAYOUT WITH (nolock) " + "       ON ENCABEZADO.canocontrarrecibo = LAYOUT.snocontrarrecibo " + "INNER JOIN dbo.tconsolidacionrelaciongastosencabezado consolidacion_encabezado WITH (nolock) " + "       ON LAYOUT.sauxiliarcomodin = consolidacion_encabezado.nidintegracion " + "WHERE  LAYOUT.sauxiliarcomodin IS NOT NULL " + "       AND nfolioconsolidacion = ? " + "GROUP  BY ep, " + "       consolidacion_encabezado.nfolioconsolidacion, " + "       DETALLE.ccentrocontable, " + "       DETALLE.cmes, " + "       ENCABEZADO.id_destino_gasto, " + "       detalle.id_tipo_concepto, " + "       LAYOUT.sauxiliarcomodin, " + "       scuenta_bancaria, " + "       CASE WHEN ENCABEZADO.RFC <> 'TESOFE' THEN '' ELSE ENCABEZADO.RFC END , " + "       detalle.alm," + "       Substring(detalle.ep, 32, 5)";
        PreparedStatement psInsertaEncabezado = null;
        PreparedStatement psInsertaDetalle = null;
        ResultSet rsFolioConsolidacion = null;
        int nFolioConsolidacion = -1;
        try {
            Caso c = PagosDiversosRGManager.generaCaso(conn, sUsuario, folioGenerator);
            nFolioConsolidacion = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            log.debug("Query Insert Encabezado[" + sqlInsertConsolidacion + "]");
            log.debug("Query Insert Detalle[" + sqlInsertConsolidacionDetalle + "]");
            psInsertaEncabezado = conn.prepareStatement(sqlInsertConsolidacion, Statement.RETURN_GENERATED_KEYS);
            psInsertaDetalle = conn.prepareStatement(sqlInsertConsolidacionDetalle);
            psInsertaEncabezado.setInt(1, nFolioConsolidacion);
            psInsertaEncabezado.setString(2, sTimeStamp);
            psInsertaEncabezado.setString(3, sUsuario.getLogin());
            psInsertaEncabezado.setString(4, "");
            psInsertaEncabezado.setString(5, ejercicioFiscal);
            insertados += psInsertaEncabezado.executeUpdate();
            log.debug("Insertados en encabezado: " + insertados + " registros ");
            rsFolioConsolidacion = psInsertaEncabezado.getGeneratedKeys();
            psInsertaDetalle.setInt(1, nFolioConsolidacion);
            insertados += psInsertaDetalle.executeUpdate();
            log.debug("Insertados en detalle: " + insertados + " registros ");
            return insertados;
        } finally {
            CloseObject.closeObject(rsFolioConsolidacion, false);
            CloseObject.closeObject(psInsertaEncabezado, false);
            CloseObject.closeObject(psInsertaDetalle, false);
        }
    }

    public static Caso generaCaso(Connection conn, Usuario u, String folioGenerator) throws Exception {
        FolioGeneratorInterface fg;
        ClassLoader cl = PagosDiversosRGManager.class.getClassLoader();
        Class<?> clase = cl.loadClass(folioGenerator);
        fg = (FolioGeneratorInterface) clase.newInstance();
        Caso c = CasoManager.nuevoCaso(conn, u, 44, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        try {
            c.getCasoDato("EJERCICIO_FISCAL").setValor(obtieneEjecicioFiscal(conn));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        try {
            m.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal(conn));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
        if (id_gabinete < 0) {
            log.error("Identificador de Gabiente invalido (< 0)");
            throw new Exception("Identificador de Gabiente invalido (< 0)");
        }
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        //CasoBusinessLogic casoTx;
        // Guarda las variables de caso.
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        AplicacionManager.updateExpediente(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        co.setIdOperacion(1);
        co.setResponsable("CONSULTA_CONSOLIDACIONRG");
        CasoOperacionManager.update(conn, co);
        return c;
    }

    public static String obtieneEjecicioFiscal(Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String EjercicioFiscal = "";
        try {
            String queryEf = "select aEjercicioFiscal from tEjercicioFiscal WITH (NOLOCK) where cActivo = 1 ";
            ps = conn.prepareStatement(queryEf);
            rs = ps.executeQuery();
            if (rs.next()) {
                EjercicioFiscal = rs.getString("aEjercicioFiscal");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return EjercicioFiscal;
    }

    public static int UpdateStatus(Connection conn, String Integracion, String usuario) throws SQLException {
        PreparedStatement pstmnt = null, updateLayout = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGODIVERSOEncabezado SET nEnviadoSICOP = 0 WHERE nFolioPAGODIVERSO IN (SELECT Folio FROM vListaPagosDiversosRGConLayout WHERE sAuxiliarComodin = '" + Integracion + "')");
            retval = pstmnt.executeUpdate();
            updateLayout = conn.prepareStatement("UPDATE tLayoutsCreadosPagosDiversosRGHeader SET cEstatus = 'DEVUELTO', fDevolucionLayout = GETDATE(), sLoginDevolucion = '" + usuario + "' WHERE sAuxiliarComodin = '" + Integracion + "'");
            updateLayout.executeUpdate();
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (updateLayout != null) {
                updateLayout.close();
            }
            pstmnt = null;
            updateLayout = null;
        }
        return retval;
    }
}
