package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class PagosNominaManager {

    public PagosNominaManager() {
        super();
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String listaIds, String cConcepto, String listaFechas, String listaLeyendas, String sUsuario, String solicitudPago, String sCuentaBancaria) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] arrFolios = listaIds.split(",");
        int intIndice = -1;
        String nFolioSicop = "";
        //PSC
        String Sql = " SELECT distinct tCE.nFolioNomina, " + " 'H' AS Header, " + " CONVERT(nvarchar(10), tCE.fAplicacion,103), " + " CONVERT(nvarchar(10), tCE.fAplicacion,103) ," + " tCE.cRamo," + " tCE.cRamo," + " tCE.cRamo, " + " 'RHQ' UnidadResponsable," + " 'RHQ' UnidadResponsable," + " 'RHQ' UnidadResponsable, " + " 'N' ID_TIPO_MOVIMIENTO, " + " '7' AS OrigenPpto," + " '5' AS TipoSol, " + " 'MXN' TipoMoneda , " + " '1' TipoCambio, " + " 'NOM' TIPO_PAGO, " + " 'PENDIENTE' CveLeyenda, " + " isnull(B.sSicop,''), " + " 'Cuenta' CUENTA_BANCARIA, " + " tCE.RFC, " + " '1000', " + " rtrim(tCE.caNoContrarrecibo) FechaReferencia, " + " rtrim(tCE.caNoContrarrecibo) Referencia1, " + " rtrim(tCE.caNoContrarrecibo) Referencia2, " + " REPLACE(LEFT(tCE.cConcepto, 70),',','') cConcepto, " + " '' NotasReverso, " + " '' AMF, " + " '' NO_ACMI, " + " '' AuxiliarComodin, " + " '' CTR , " + " '' FolioDC, " + " '' IVAANT, " + " 'NA' ID_DESTINO_GASTO, " + " tComp.nFolioSicop AS nFolioSicop " + " FROM tNominaEncabezado tCE WITH (NOLOCK) " + " LEFT JOIN tBeneficiarioCapituloMil B WITH (NOLOCK) ON tce.RFC = B.sCodigoEntidad " + " LEFT JOIN pCatalogoTipoDocumento CTD WITH (NOLOCK) ON tCE.cIdTipoDocumento = CTD.cIdTipoDocumento " + " LEFT JOIN tCompromisoNominaEncabezado tComp WITH (NOLOCK) ON tComp.caNoCompromiso = tCE.caNoCompromiso " + " WHERE tCE.caNOcontrarrecibo in ('" + listaIds + "') ";
        try {
            pstmntH = conn.prepareStatement(Sql);
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                nFolioSicop = rs.getString(34);
                String nFolio, nFolioCompromiso = rs.getString(1);
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String vcBenef = "01";
                String vcLeyenda = rs.getString(25).trim().replaceAll("[\r\n]{2,}", " ");
                String vRFC = "01";
                String encabezado = //A
                rs.getString(2) + "," + //B
                rs.getString(3) + "," + //C
                rs.getString(4).trim() + "," + //D
                rs.getString(5).trim() + "," + //E
                rs.getString(6).trim() + "," + //F
                rs.getString(7).trim() + "," + //G
                rs.getString(8).trim() + "," + //H
                rs.getString(9).trim() + "," + //I
                rs.getString(10).trim() + "," + //J
                rs.getString(11).trim() + "," + //K
                rs.getString(12).trim() + "," + //L
                rs.getString(13).trim() + "," + //M
                rs.getString(14).trim() + "," + //N
                rs.getString(15).trim() + "," + //O
                rs.getString(16).trim() + "," + //P
                vcLeyenda + //Q
                "," + "," + //R
                vcBenef + "," + //S
                vRFC + "," + //T
                rs.getString(21).trim() + "," + //U
                rs.getString(22).trim() + "," + //V
                rs.getString(23).trim() + "," + //W
                rs.getString(24).trim() + //X
                "," + "," + //Y
                rs.getString(33).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                //PSC Aqui termina el grabado dentro de layouts creados encabezado
                String id_evento = "67";
                String evento = "409_SOL_3";
                String filtro_concepto = "";
                if (solicitudPago != "") {
                    id_evento = "622";
                    evento = "404_CLC";
                    filtro_concepto = " and TPDD.ID_TIPO_CONCEPTO IN (" + cConcepto + ")";
                }
                //'" + solicitudPago + "'
                String Sql2 = " SELECT DISTINCT '" + id_evento + "' ID_EVENTO,'" + evento + "' EVENTO,ltrim(TCEP.cRamo) ID_RAMO_ML, 'RHQ', TCEP.aEjercicioFiscal, " + " TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, " + // CUANDO ES PROGRAMA GENERAL '09' SE MODIFICA PARA QUE MUESTRE '00' PARA EVITAR ERROR EN SICOP.
                " /*tCEP.cProgramaGeneral*/ CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, " + " tCEP.cActividadInstitucional, tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)) CCAP_157, substring(cpartida,2,1) CCON_158, " + " substring(cpartida,3,1) CPARG_300, substring(cpartida,4,2) CPAR_159, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, " + " SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) AS cCartera, " + //URVP.20102014 Se cambia el campo de cUnidadEjecutora a '0000000000' y CCOP_163 a '00'
                " '0000000000' as cUnidadEjecutora, '00' CCOP_163, " + " '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3, " + " '" + nFolioSicop + "', " + " convert(decimal(16,2), SUM(TPDD.mImporteNeto)), " + " MONTH(GETDATE()) MES_149, " + " CASE " + "	WHEN (SELECT dbo.fn_u_existenPartidasGD(" + rs.getString(1) + "))>0 THEN 'GD' " + "	WHEN cPartida IN (SELECT cPartida FROM cat_tipo_concepto_partida WITH (NOLOCK) WHERE id_tipo_concepto='GD') THEN 'GD' " + //TPDD.ID_TIPO_CONCEPTO TIPO_CONTRATO, Right(TPDD.ID_TIPO_MOVIMIENTO, 3) CONC_MOV, " + //URVP.19012014 SE CAMBIA A PT 000 DEBIDO A QUE SE INDICA QUE PARA EL ARCHIVO DE CARGA A SICOP SIEMPRE VA ESTA VARIABLE
                "	ELSE 'PT' END TIPO_CONTRATO, " + " '000' CONC_MOV, " + //, '' id_ctr_intdet, '' id_ctr_intdet2 " +
                " isnull(TPDE.solicitudPago, '') SPAG_176 " + " FROM tNominaDetalle TPDD WITH (NOLOCK) inner join tNominaEncabezado TPDE WITH (NOLOCK)" + " on TPDD.nFolioNomina = TPDE.nFolioNomina " + " inner join tCatalogoEP TCEP WITH (NOLOCK) on TPDD.EP = TCEP.EP " + " LEFT JOIN tLayoutCompromisos LC WITH (NOLOCK) ON TPDD.cIdRelacion = LC.caNoCompromiso " + " where TPDD.nFolioNomina = " + rs.getString(1) + filtro_concepto + " group by TCEP.cRamo, TCEP.aEjercicioFiscal,TCEP.cGrupoFuncional, tCEP.cFuncion, " + " tCEP.cSubFuncion, tCEP.cProgramaGeneral, tCEP.cActividadInstitucional,tCEP.cProgramaPresupuestario, " + " TCEP.cPartida,TCEP.cTipoGasto,TCEP.cFuenteFinanciamiento,TCEP.cEntidadFederativa,SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11), " + " LC.nDocumento,TPDD.ID_TIPO_CONCEPTO,TPDD.ID_TIPO_MOVIMIENTO, TPDE.solicitudPago ";
                pstmntD = conn.prepareStatement(Sql2);
                rs2 = pstmntD.executeQuery();
                // SE CAMBIA DE 40 A 33 YA QUE SE MODIFICO LAYOUT QUE RECIBE SICOP  20/09/2018 FAV.
                int nLoop = 33;
                //int nLoop2 = 1;
                if (solicitudPago != "") {
                    // SE CAMBIA DE 40 A 33 YA QUE SE MODIFICO LAYOUT QUE RECIBE SICOP  20/09/2018 FAV.
                    nLoop = 33;
                }
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < nLoop; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                    //nLoop2++;
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

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds, String sConcepto) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        //PSC
        String Sql = " select PDE.nFolioNomina, 'H' H, PDE.cRamo, 'RHQ', '' SOL_PAGO, '4', PDE.caNoContrarrecibo FOLIO_INTERNO, PDE.caNoContrarrecibo COMODIN from " + " dbo.tNominaEncabezado PDE WITH (NOLOCK) " + " WHERE PDE.caNOcontrarrecibo in ('" + listaIds + "') ";
        try {
            pstmntH = conn.prepareStatement(Sql);
            System.out.println(Sql);
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                String Sql2 = " SELECT top(1) DISTINCT PDE.cRamo, " + " DCD.DCD_FACTURA, " + " CONVERT(nvarchar(10), DCD.DCD_FECHA_FACTURA,103), " + " CONVERT(nvarchar(10), DCD.DCD_FECHA_RECEPCION,103) + ' 12:00:00 a.m.', " + " case cEstatus when 'Operada Pagada' then cpd.cCampoAdi3 else ISNULL(BB.CBEN, isnull(DCD.DCD_CBEN,'')) end, " + " case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen', " + " DCD.DCD_TIPO_OPE, " + " CONVERT(decimal(17,2), DCD.DCD_VALOR), " + " CONVERT(decimal(17,2), PDE.mImporteNeto),  " + " CONVERT(decimal(17,2), DCD.DCD_IVADES), " + " CONVERT(decimal(17,2), DCD.DCD_IVA), " + " CONVERT(decimal(17,2), DCD.DCD_ISR), " + " CONVERT(decimal(17,2), DCD.DCD_MIL5), " + " CONVERT(decimal(17,2), DCD.DCD_MIL2), " + " CONVERT(decimal(17,2), DCD.DCD_OTRAS_RET), " + " CONVERT(decimal(17,2), DCD.DCD_PENALIZACION), " + " CONVERT(decimal(17,2), DCD.DCD_CONTRIBUCION), " + " DCD.DCD_CTOEXT, LEFT(DCD.DCD_CONCEPTO, 70) " + " from dbo.tNominaEncabezado PDE WITH (NOLOCK) " + " inner join dbo.tNominaDetalle PDD WITH (NOLOCK) on PDE.nFolioNomina = PDD.nFolioNomina " + " inner join [dbo].[tBeneficiario] B WITH (NOLOCK) on PDE.RFC = B.dRFC " + " where PDE.nFolioNomina = " + nFolioCompromiso + " --and PDD.ID_TIPO_CONCEPTO = '" + sConcepto + "'";
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
            pstmnt = conn.prepareStatement("UPDATE tNominaEncabezado SET nEnviadoSICOP = 1 WHERE nFolioNomina in (" + listaIds + ")");
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

    public static int UpdateStatus(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tNominaEncabezado SET nEnviadoSICOP = 0 WHERE nFolioNomina in (" + listaIds + ")");
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

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso) throws SQLException {
        PreparedStatement pstmntL = null;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + "                             WHERE caNoCompromiso = '" + caNoCompromiso + "'";
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
        String queryInsert = "INSERT INTO tLayoutCompromisos(" + "                                               cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso," + "                                               cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP," + "                                               nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud," + "                                               cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC," + "                                               caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento," + "                                               nDocumento,cDescripcion)" + "             VALUES(" + "" + "'" + clave + "','" + cRamo + "','" + cUnidadResponsable + "','" + folioSICOP + "','" + idProceso + "'," + "" + "'" + cCentroContable + "','" + fExpedicion + "'," + total + ",'" + cTipoPoliza + "','" + nFolioPoliza + "'," + "" + "'" + nPolizaCancelacion + "','" + tipoMovimiento + "','" + origenPresupuesto + "','" + cuentaBancaria + "','" + noSolicitud + "'," + "" + "'" + tCambio + "','" + tMoneda + "','" + tSolicitud + "','" + volante + "','" + rfc + "'," + "" + "'" + caNoCompromiso + "','" + codSemarnat2 + "','" + estatus + "','" + fAplicacion + "','" + documento + "'," + "" + "'" + nDocumento + "','" + descripcion + "')";
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

    public static ArrayList<String> BuscaCLCNomina(Connection conn, String clcNomina) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        PreparedStatement pstmntCLC = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        //ENCABEZADO DE CLC DE NOMINA
        String sqlEnc = " SELECT DISTINCT tnclcE.nFolioNOMINACLC, " + "'H' AS Header, " + "CONVERT(nvarchar(10), tnclcE.fAplicacion,103) AS fAplicacion, " + "CONVERT(nvarchar(10), tnclcE.fAplicacion,103) ,  " + "tnclcE.cRamo, " + "tnclcE.cRamo, " + "tnclcE.cRamo, " + "'RHQ' UnidadResponsable, " + "'RHQ' UnidadResponsable, " + "'RHQ' UnidadResponsable, " + "'N' ID_TIPO_MOVIMIENTO,  " + "'7' AS OrigenPpto, " + "'5' AS TipoSol, " + "'MXN' TipoMoneda , " + "'1' TipoCambio, " + //URVP.27012015 Se cambia a dato de columna agregada al catalogo de benef cap mil
        "B.cTipoPago TIPO_PAGO, " + //URVP.27012015 Se cambia a dato de columna agregada al catalogo de benef cap mil
        "CASE WHEN B.sSicop='S24676' AND cConcepto LIKE 'FONAC%' THEN '16' ELSE B.cCveLeyenda END CveLeyenda, " + "isnull(B.sSicop,''), " + "tnclcE.cuentaBancaria AS CUENTA_BANCARIA, " + "tnclcE.cIdRFC, " + "'NOM', " + "'' AS FechaReferencia, " + "CASE " + "		WHEN B.sSicop='S24676' AND cConcepto LIKE 'FONAC%' THEN '0001-08082011FONAC' " + "		WHEN B.sSicop='S24676' AND cConcepto LIKE 'ISR%' THEN REPLACE(cConcepto,' ','') + 'VIAPEC' " + "       WHEN B.sSicop=BR.sSicop AND tnclcE.cIdRFC = BR.cRFC THEN BR.cReferancia " + "		ELSE '' " + "  	END Referencia1, " + "'' Referencia2, " + "REPLACE(cConcepto,',','') cConcepto, " + "'' NotasReverso, " + "rtrim(tnclcE.caNoContrarreciboCLC) NO_ACMI, " + "rtrim(tnclcE.caNoContrarreciboCLC) AuxiliarComodin, " + "'' CTR , " + "'' FolioDC, " + "'' IVAANT, " + //URVP.23012015 se cambia el campo 'NA' por un 6
        "'6' ID_DESTINO_GASTO " + " FROM tNOMINACLCEncabezado AS tnclcE (NOLOCK) LEFT JOIN tNOMINACLCDetalle (NOLOCK) AS tnclcD ON tnclcE.nFolioNOMINACLC = tnclcD.nFolioNOMINACLC " + " LEFT JOIN tBeneficiarioCapituloMil B (NOLOCK) ON tnclcE.cIdRFC = B.scodigoentidad " + " LEFT JOIN tBeneficiarioCapituloMilRef BR WITH (NOLOCK) ON tnclcE.cIdRFC = BR.cRFC AND B.sSicop = BR.sSicop " + " WHERE caNoContrarreciboCLC = '" + clcNomina + "'";
        try {
            pstmntH = conn.prepareStatement(sqlEnc);
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String nFolioCLC = rs.getString(1);
                String encabezado = //A
                rs.getString(2) + "," + //B
                rs.getString(3) + "," + //C
                rs.getString(4).trim() + "," + //D
                rs.getString(5).trim() + "," + //E
                rs.getString(6).trim() + "," + //F
                rs.getString(7).trim() + "," + //G
                rs.getString(8).trim() + "," + //H
                rs.getString(9).trim() + "," + //I
                rs.getString(10).trim() + "," + //J
                rs.getString(11).trim() + "," + //K
                rs.getString(12).trim() + "," + //L
                rs.getString(13).trim() + "," + //M
                rs.getString(14).trim() + "," + //N
                rs.getString(15).trim() + "," + //O
                rs.getString(16).trim() + "," + //P
                rs.getString(17).trim() + "," + //Q
                rs.getString(18).trim() + "," + //R
                rs.getString(19).trim() + "," + //S
                rs.getString(20).trim() + "," + //T
                rs.getString(21).trim() + "," + //U
                rs.getString(22).trim() + "," + //V
                rs.getString(23).trim() + "," + //W
                rs.getString(24).trim() + "," + //X
                rs.getString(25).trim() + "," + //Y
                rs.getString(26).trim() + "," + //Z
                rs.getString(27).trim() + "," + //AA
                rs.getString(28).trim() + "," + //AB
                rs.getString(29).trim() + "," + //AC
                rs.getString(30).trim() + "," + //AD
                rs.getString(31).trim() + "," + //AE
                rs.getString(32).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                String sqlDet = "  SELECT '622' ID_EVENTO, " + "'404_CLC' EVENTO, " + "Rtrim(tCEP.cRamo) ID_RAMO_ML, " + "'RHQ', " + "tCEP.aEjercicioFiscal, " + "TCEP.cGrupoFuncional, " + "tCEP.cFuncion, " + "tCEP.cSubFuncion, " + // CUANDO ES PROGRAMA GENERAL '09' SE MODIFICA PARA QUE MUESTRE '00' PARA EVITAR ERROR EN SICOP.
                "/*tCEP.cProgramaGeneral*/ CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, " + "tCEP.cActividadInstitucional, " + "tCEP.cProgramaPresupuestario, " + "ltrim(substring(cpartida,1,1)) CCAP_157, " + "substring(cpartida,2,1) CCON_158, " + "substring(cpartida,3,1) CPARG_300, " + "substring(cpartida,4,2) CPAR_159, " + "tCEP.cTipoGasto, " + "tCEP.cFuenteFinanciamiento, " + "tCEP.cEntidadFederativa, " + "SUBSTRING( dbo.CambiaEPCarteraMeta(tnDet.EP),45, 11) AS cCartera," + //"ltrim('0000000' + RTRIM(tCEP.cUnidadEjecutora)), " +
                //"substring(TCEP.cUnidadNorativa,2,2) CCOP_163, " +
                " '0000000000' AS cUnidadEjecutora, " + // URVP.03122014 Se cambia de 0 a 0000000000
                " '00' AS cUnidadNorativa, " + // URVP.03122014 Se cambia de 0 a 0000000000
                " '000' PL, " + "'000' OFI, " + "'00000' AUX1, " + "'00000' AUX2, " + "'0000000000' AUX3, " + " isnull( CONVERT(decimal(17,2), tnDet.mImporteNeto),'0.00') Monto2, " + " isnull( CONVERT(decimal(17,2), tnDet.mImporteNeto),'0.00') Monto, " + " MONTH(GETDATE()) MES_149, " + "'01' NRES, " + //20.07.2015 SE CAMBIA A "01" (NOMINA ORDINARIA) Y NO "02" (EXTRAORDINARIA)
                "'01' AS PPAG_177, " + " CASE " + "	WHEN (SELECT dbo.fn_u_existenPartidasGD((SELECT TOP 1 nFolioNOMINA FROM tNOMINACLCDetalle WITH(NOLOCK) WHERE nFolioNOMINACLC=" + nFolioCLC + ")))>0 THEN 'GD' " + "	WHEN cPartida IN (SELECT cPartida FROM cat_tipo_concepto_partida WHERE id_tipo_concepto='GD') THEN 'GD' " + "	ELSE 'PT' END TIPO_CONTRATO, " + // Right(tnDet.ID_TIPO_MOVIMIENTO, 3) CONC_MOV, "
                "'000' CONC_MOV," + " '' solicitudPago, '' id_ctr_intdet, '' id_ctr_intdet2, tnDet.caNoContrarrecibo AS recibo, " + " ( select top 1 cne.nFolioSicop from tNOMINAEncabezado ne, tCompromisoNominaEncabezado cne where tnDet.caNoContrarrecibo = ne.caNoContrarrecibo and ne.caNoCompromiso = cne.caNoCompromiso ) , " + " ( select top 1 ne.solicitudPago from tNOMINAEncabezado ne, tCompromisoNominaEncabezado cne where tnDet.caNoContrarrecibo = ne.caNoContrarrecibo and ne.caNoCompromiso = cne.caNoCompromiso ) " + " FROM tNOMINACLCEncabezado tNEnc INNER JOIN tNOMINACLCDetalle tnDet ON tNEnc.nFolioNOMINACLC = tnDet.nFolioNOMINACLC " + " INNER JOIN tCatalogoEP tCEP ON tCEP.EP = tnDet.EP " + " LEFT JOIN tLayoutCompromisos LC ON tnDet.cIdRelacion = LC.caNoCompromiso " + " WHERE tnDet.nFolioNOMINACLC = " + nFolioCLC + " ";
                pstmntD = conn.prepareStatement(sqlDet);
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String solicitudPago = "";
                    String caNoRecibo = rs2.getString("recibo");
                    String sqlclcSolPag = "SELECT TOP 1 SPAG_176 FROM CLC_SICOP WHERE NCTR_47 = '" + caNoRecibo + "'";
                    pstmntCLC = conn.prepareStatement(sqlclcSolPag);
                    rs3 = pstmntCLC.executeQuery();
                    while (rs3.next()) {
                        solicitudPago = rs3.getString("SPAG_176");
                    }
                    String detalle = rs2.getString(1) + "," + rs2.getString(2) + "," + rs2.getString(3) + "," + rs2.getString(4).trim() + "," + rs2.getString(5).trim() + "," + rs2.getString(6) + "," + rs2.getString(7) + "," + rs2.getString(8) + "," + rs2.getString(9) + "," + rs2.getString(10).trim() + "," + rs2.getString(11).trim() + "," + rs2.getString(12) + "," + rs2.getString(13) + "," + rs2.getString(14) + "," + rs2.getString(15) + "," + rs2.getString(16).trim() + "," + rs2.getString(17).trim() + "," + rs2.getString(18) + "," + rs2.getString(19) + "," + rs2.getString(20) + "," + rs2.getString(21) + "," + rs2.getString(22).trim() + "," + rs2.getString(23).trim() + "," + rs2.getString(24) + "," + rs2.getString(25) + "," + rs2.getString(26) + "," + rs2.getString(27) + "," + rs2.getString(29).trim() + "," + rs2.getString(28).trim() + "," + rs2.getString(38) + "," + rs2.getString(30) + "," + rs2.getString(31) + "," + rs2.getString(32) + "," + rs2.getString(33) + "," + rs2.getString(39);
                    detalle = detalle + "\r\n";
                    arrListaComp.add(detalle.toString());
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntH);
            CloseObject.closeObject(pstmntD);
        }
        return arrListaComp;
    }

    public static int UpdateStatusGenerar(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tNominaEncabezado SET nEnviadoSICOP = 1 WHERE caNoContrarrecibo in ('" + listaIds + "')");
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

    public static ArrayList<String> BuscaEjercidoComparativo(Connection conn) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        PreparedStatement pstmnTotal = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        try {
            String encabezado = "Unidad Sicop, Num CLC Sicop, Total Sicop, Unidad SAI, Num CXP SAI, Total SAI, Diferencia";
            encabezado = encabezado + "\r\n";
            arrListaComp.add(encabezado);
            String sqlDet = " SELECT UnidadResponsableSicop, sicop, CONVERT(money,Total_Ejercido_SICOP) , UnidadResponsableCXP, CXP, CONVERT(money,Total_Ejercido_SAI), CONVERT(money,diferencia) FROM v_EjercidoComparativo ORDER BY SUBSTRING(UnidadResponsableSicop,2,2) ";
            pstmntD = conn.prepareStatement(sqlDet);
            rs2 = pstmntD.executeQuery();
            while (rs2.next()) {
                String detalle = rs2.getString(1) + "," + rs2.getString(2) + "," + rs2.getString(3) + "," + rs2.getString(4).trim() + "," + rs2.getString(5).trim() + "," + " " + rs2.getString(6) + "," + rs2.getString(7);
                detalle = detalle + "\r\n";
                arrListaComp.add(detalle.toString());
            }
            pstmnTotal = conn.prepareStatement(" SELECT SUM(sicop) sicop, SUM(convert(money, Total_Ejercido_SICOP)) totalEjercidoSICOP, SUM(CXP) cxp, SUM(convert(money,Total_Ejercido_SAI)) totalEjercidoSAI, SUM(convert(money, Total_Ejercido_SICOP)) - SUM(convert(money,Total_Ejercido_SAI)) totalDiferencia FROM  v_EjercidoComparativo ");
            rs4 = pstmnTotal.executeQuery();
            if (rs4.next()) {
                String totales = "\r\n";
                totales += "Totales SAI: ," + rs4.getString(1) + "," + rs4.getString(2) + ",Totales SICOP: ," + rs4.getString(3) + "," + rs4.getString(4) + "," + rs4.getString(5);
                arrListaComp.add(totales.toString());
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs3);
            CloseObject.closeObject(rs4);
            CloseObject.closeObject(pstmntH);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(pstmnTotal);
        }
        return arrListaComp;
    }

    public static ArrayList<String> BuscaEjercidoComparativoCapitulo(Connection conn) throws Exception {
        ArrayList<String> arrListValor = new ArrayList<String>();
        PreparedStatement pstEnc = null, pstDet = null, pstTotal = null;
        ResultSet rsEnc = null, rsDet = null, rsTotal = null;
        String encabezado = "Unidad Sicop, 1000, 2000, 3000, 4000, 5000, 6000, Total General, Unidad SAI, 1000, 2000, 3000, 4000, 5000, 6000, Total General, Diferencia ";
        encabezado += "\r\n";
        arrListValor.add(encabezado);
        String qryEnc = " SELECT vEC.unidadSicop, " + " isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 1 " + "		 and vE.unidadSicop = vEC.unidadSicop " + "		),0.00) as mil, " + " isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 2 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		),0.00) as dosMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 3 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		),0.00) as tresMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 4 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		),0.00) as cuatroMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 5 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		),0.00)as cincoMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 6 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		),0.00) as seisMil, " + "  ( " + "	  isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + " ) as totalGeneral, " + //sai
        " vEC.unidadSicop, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + " 			where tEE.nFolioEjercido = tED.nFolioEjercido " + " 			and SUBSTRING(EP,57,3) = vEC.unidadSicop " + "			and tEE.cDocumentoHaplicado = 'S' " + " 			and SUBSTRING(EP,32,1) = 1 " + " ),0.00) as milSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + " 			where tEE.nFolioEjercido = tED.nFolioEjercido " + " 			and SUBSTRING(EP,57,3) = vEC.unidadSicop " + " 			and tEE.cDocumentoHaplicado = 'S' " + " 			and SUBSTRING(EP,32,1) = 2 " + " ),0.00) as dosMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + " 			where tEE.nFolioEjercido = tED.nFolioEjercido " + "			and SUBSTRING(EP,57,3) = vEC.unidadSicop " + "			and tEE.cDocumentoHaplicado = 'S' " + " 	  		and SUBSTRING(EP,32,1) = 3 " + " ),0.00) as tresMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "			where tEE.nFolioEjercido = tED.nFolioEjercido " + "   		and SUBSTRING(EP,57,3) = vEC.unidadSicop " + "          and tEE.cDocumentoHaplicado = 'S' " + "	        and SUBSTRING(EP,32,1) = 4 " + " ),0.00) as cuatroMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "		   where tEE.nFolioEjercido = tED.nFolioEjercido " + " 		   and SUBSTRING(EP,57,3) = vEC.unidadSicop " + "         and tEE.cDocumentoHaplicado = 'S' " + "		   and SUBSTRING(EP,32,1) = 5 " + "	),0.00) as cincoMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "        where tEE.nFolioEjercido = tED.nFolioEjercido " + "		  and SUBSTRING(EP,57,3) = vEC.unidadSicop " + "        and tEE.cDocumentoHaplicado = 'S' " + "        and SUBSTRING(EP,32,1) = 6 " + " ),0.00) as seisMilSAI, " + //totalGeneralSAI
        " (  isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " + "	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) " + "	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) " + "  + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) " + "  + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) " + "  + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) " + " ) as totalGeneralSAI, " + //Diferencia
        " ( " + "	  isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop ),0.00) " + "	) " + "	- " + "	( " + "	  isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " + "	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) " + "	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) " + "	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) " + "	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) " + "	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) " + " ) as diferencia " + " FROM v_EjercidoComparativoCapitulo vEC WITH(NOLOCK) GROUP BY vEC.unidadSicop ORDER BY SUBSTRING(unidadSicop,2,2) ";
        pstEnc = conn.prepareStatement(qryEnc);
        rsEnc = pstEnc.executeQuery();
        //, mil = "", dosMil = "", tresMil = "", cuatroMil = "", cincoMil = "", seisMil = "", totalGeneral = "";
        String unidadSicop = "";
        //String milSAI = "", dosMilSAI = "", tresMilSAI = "", cuatroMilSAI = "", cincoMilSAI = "", seisMilSAI = "", totalGeneralSAI= "", diferencia = "";
        String encArr = "", detArr = "", detTotal = "";
        while (rsEnc.next()) {
            unidadSicop = rsEnc.getString("unidadSicop");
            System.out.println("Unidad: " + unidadSicop);
            encArr = rsEnc.getString("unidadSicop") + "," + rsEnc.getString("mil") + "," + rsEnc.getString("dosMil") + "," + rsEnc.getString("tresMil") + "," + rsEnc.getString("cuatroMil") + "," + rsEnc.getString("cincoMil") + "," + rsEnc.getString("seisMil") + "," + rsEnc.getString("totalGeneral") + "," + rsEnc.getString("unidadSicop") + "," + rsEnc.getString("milSAI") + "," + rsEnc.getString("dosMilSAI") + "," + rsEnc.getString("tresMilSAI") + "," + rsEnc.getString("cuatroMilSAI") + "," + rsEnc.getString("cincoMilSAI") + "," + rsEnc.getString("seisMilSAI") + "," + rsEnc.getString("totalGeneralSAI") + "," + rsEnc.getString("diferencia");
            encArr += "\r\n";
            arrListValor.add(encArr);
            String qryDet = " SELECT vEC.unidadSicop, vEC.partidaSicop, " + "  isnull((select SUM(vE.importe_148Sicop) " + " 		from v_EjercidoComparativoCapitulo vE with(nolock) " + "  	where vE.capituloSicop = 1 " + " 		and vE.unidadSicop = vEC.unidadSicop " + " 		and vE.partidaSicop = vEC.partidaSicop " + "		),0.00) as mil, " + "  isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 2 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		and vE.partidaSicop = vEC.partidaSicop " + "		),0.00) as dosMil, " + "	isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 3 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		and vE.partidaSicop = vEC.partidaSicop " + "		),0.00) as tresMil, " + "  isnull((select SUM(vE.importe_148Sicop) " + "  	from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 4 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		and vE.partidaSicop = vEC.partidaSicop " + "		),0.00) as cuatroMil, " + "	isnull((select SUM(vE.importe_148Sicop) " + "		from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 5 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		and vE.partidaSicop = vEC.partidaSicop " + "		),0.00) as cincoMil, " + "  isnull((select SUM(vE.importe_148Sicop) " + "  	from v_EjercidoComparativoCapitulo vE with(nolock) " + "		where vE.capituloSicop = 6 " + "		and vE.unidadSicop = vEC.unidadSicop " + "		and vE.partidaSicop = vEC.partidaSicop " + "		),0.00) as seisMil, " + " ( " + "	 isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + "	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + " ) as TotalGenera ," + //SAI
            " vEC.unidadSicop, vEC.partidaSicop, " + "	isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' " + "			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 1 " + "	),0.00) as milSAI, " + "	isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' " + "			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 2 " + "	),0.00) as dosMilSAI, " + "	isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' " + "			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 3 " + "	),0.00) as tresMilSAI, " + "	isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' " + "			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 4 " + "	),0.00) as cuatroMilSAI, " + "	isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' " + "			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 5 " + "	),0.00) as cincoMilSAI, " + "	isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' " + "			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 6 " + "	),0.00) as seisMilSAI, " + // totalGeneralDetalle
            " ( isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 1 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 2 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 3 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 4 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 5 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 6 ),0.00) " + " ) as totalGeneralDetalle, " + //Diferencia
            " ( isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) " + "	) " + " - " + " ( isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 1 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 2 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 3 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 4 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 5 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 6 ),0.00) " + " ) as Diferencia " + " FROM v_EjercidoComparativoCapitulo vEC WITH(NOLOCK) " + " WHERE unidadSicop = '" + unidadSicop + "' GROUP BY vEC.unidadSicop, vEC.partidaSicop " + " ORDER BY SUBSTRING(unidadSicop,2,2) ";
            pstDet = conn.prepareStatement(qryDet);
            rsDet = pstDet.executeQuery();
            while (rsDet.next()) {
                System.out.println("Partida: " + rsDet.getString("partidaSicop"));
                detArr = rsDet.getString("partidaSicop") + "," + rsDet.getString("mil") + "," + rsDet.getString("dosMil") + "," + rsDet.getString("tresMil") + "," + rsDet.getString("cuatroMil") + "," + rsDet.getString("cincoMil") + "," + rsDet.getString("seisMil") + "," + rsDet.getString("TotalGenera") + "," + rsDet.getString("partidaSicop") + "," + rsDet.getString("milSAI") + "," + rsDet.getString("dosMilSAI") + "," + rsDet.getString("tresMilSAI") + "," + rsDet.getString("cuatroMilSAI") + "," + rsDet.getString("cincoMilSAI") + "," + rsDet.getString("seisMilSAI") + "," + rsDet.getString("totalGeneralDetalle") + "," + rsDet.getString("diferencia");
                detArr += "\r\n";
                arrListValor.add(detArr);
            }
            arrListValor.add(detArr);
            if (rsDet != null) {
                rsDet.close();
            }
            if (pstDet != null) {
                pstDet.close();
            }
        }
        //Total General
        String qryTotal = "SELECT	top 1 " + " isnull((select SUM(vE.importe_148Sicop) " + " 		 from v_EjercidoComparativoCapitulo vE with(nolock) " + " 		 where vE.capituloSicop = 1 " + " 	),0.00) as mil, " + " isnull((select SUM(vE.importe_148Sicop) " + "   	 from v_EjercidoComparativoCapitulo vE with(nolock) " + "   	 where vE.capituloSicop = 2 " + " ),0.00) as dosMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "   	   from v_EjercidoComparativoCapitulo vE with(nolock) " + "   	   where vE.capituloSicop = 3 " + " ),0.00) as tresMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "   							from v_EjercidoComparativoCapitulo vE with(nolock) " + "   							where vE.capituloSicop = 4 " + " ),0.00) as cuatroMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "   							from v_EjercidoComparativoCapitulo vE with(nolock) " + "   							where vE.capituloSicop = 5 " + " ),0.00) as cincoMil, " + " isnull((select SUM(vE.importe_148Sicop) " + "   							from v_EjercidoComparativoCapitulo vE with(nolock) " + "   							where vE.capituloSicop = 6 " + " ),0.00) as seisMil, " + " ( " + "   isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 ),0.00) " + " ) as totalGeneral, " + //SAI
        " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "  where tEE.nFolioEjercido = tED.nFolioEjercido  " + "	  and tEE.cDocumentoHaplicado = 'S' " + "	  and SUBSTRING(EP,32,1) = 1 " + "  ),0.00) as milSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "  where tEE.nFolioEjercido = tED.nFolioEjercido  " + "		and tEE.cDocumentoHaplicado = 'S' " + "		and SUBSTRING(EP,32,1) = 2 " + " ),0.00) as dosMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "   where tEE.nFolioEjercido = tED.nFolioEjercido " + "		 and tEE.cDocumentoHaplicado = 'S' " + "		 and SUBSTRING(EP,32,1) = 3 " + " ),0.00) as tresMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "  where tEE.nFolioEjercido = tED.nFolioEjercido  " + "  and tEE.cDocumentoHaplicado = 'S' " + "  and SUBSTRING(EP,32,1) = 4 " + " ),0.00) as cuatroMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "  where tEE.nFolioEjercido = tED.nFolioEjercido " + "  and tEE.cDocumentoHaplicado = 'S' " + "  and SUBSTRING(EP,32,1) = 5 " + " ),0.00) as cincoMilSAI, " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) " + "   where tEE.nFolioEjercido = tED.nFolioEjercido  " + "   and tEE.cDocumentoHaplicado = 'S' " + "   and SUBSTRING(EP,32,1) = 6 " + " ),0.00) as seisMilSAI, " + " ( " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) " + " ) as totalGeneralSAI, " + //Diferencia
        "  ( " + " isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 ),0.00) " + " + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 ),0.00) " + " ) " + " - " + " ( " + " isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) " + " + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) " + " ) as diferencia " + " FROM v_EjercidoComparativoCapitulo vEC " + " WITH(NOLOCK) " + " GROUP BY vEC.capituloSicop ";
        pstTotal = conn.prepareStatement(qryTotal);
        rsTotal = pstTotal.executeQuery();
        if (rsTotal.next()) {
            System.out.println("Total: ");
            detTotal = "\r\n";
            detTotal += "Total," + rsTotal.getString("mil") + "," + rsTotal.getString("dosMil") + "," + rsTotal.getString("tresMil") + "," + rsTotal.getString("cuatroMil") + "," + rsTotal.getString("cincoMil") + "," + rsTotal.getString("seisMil") + "," + rsTotal.getString("totalGeneral") + ",Total," + rsTotal.getString("milSAI") + "," + rsTotal.getString("dosMilSAI") + "," + rsTotal.getString("tresMilSAI") + "," + rsTotal.getString("cuatroMilSAI") + "," + rsTotal.getString("cincoMilSAI") + "," + rsTotal.getString("seisMilSAI") + "," + rsTotal.getString("totalGeneralSAI") + "," + rsTotal.getString("diferencia");
            System.out.println(detTotal);
            arrListValor.add(detTotal);
        }
        if (rsEnc != null) {
            rsEnc.close();
        }
        if (rsDet != null) {
            rsDet.close();
        }
        if (rsTotal != null) {
            rsTotal.close();
        }
        if (pstEnc != null) {
            pstEnc.close();
        }
        if (pstDet != null) {
            pstDet.close();
        }
        if (pstTotal != null) {
            pstTotal.close();
        }
        return arrListValor;
    }
}
