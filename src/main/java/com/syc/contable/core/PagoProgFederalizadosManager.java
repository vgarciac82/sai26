package com.syc.contable.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.jfree.util.Log;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class PagoProgFederalizadosManager {

    public PagoProgFederalizadosManager() {
        super();
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
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
            rs = generaEncabezadoFederalizado(conn, listaIds, 5, 8);
            while (rs.next()) {
                String nFolio, nFolioCompromiso = rs.getString(1);
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String vcReferencia1 = rs.getString(23).trim();
                String vcCtaBancaria = arrCuentasBancarias[intIndice].trim().trim();
                String vcBenef = rs.getString(18).trim();
                String vcLeyenda = arrLeyendas[intIndice].trim().trim();
                String vRFC = rs.getString(20).trim();
                if (!"".equals(vcReferencia1)) {
                    vcBenef = "S24676";
                    vcCtaBancaria = "22800100000100";
                    vcLeyenda = "2";
                    vRFC = "6001";
                }
                String encabezado = rs.getString(2) + "," + arrFechas[intIndice].trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + rs.getString(16).trim() + "," + vcLeyenda + "," + vcBenef + "," + vcCtaBancaria + "," + vRFC + "," + rs.getString(21).trim() + "," + rs.getString(22).trim() + "," + rs.getString(23).trim() + "," + rs.getString(24).trim() + "," + rs.getString(25).trim().replaceAll("[\r\n]{2,}", " ") + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim() + "," + rs.getString(30).trim() + "," + rs.getString(31).trim() + "," + rs.getString(32).trim() + "," + rs.getString(33).trim() + "," + rs.getString(34).trim() + "," + rs.getString(35).trim() + "," + rs.getString(36).trim() + "," + rs.getString(37).trim() + "," + rs.getString(38).trim() + "," + rs.getString(39).trim() + "," + rs.getString(40).trim() + "," + rs.getString(41);
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                rs2 = generaDetalleCompromisoFed(conn, rs.getString(1));
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 44; i++) {
                        //URVP.27012015 se cambia la condicion de <42 a 38 debido a las 5 columnas que se eliminaron
                        if (i == 28) {
                            revisarTotal = rs2.getBigDecimal(i);
                            total = total.add(revisarTotal);
                            if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                                throw new Exception("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + listaIds);
                            } else {
                                detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                token = ",";
                            }
                        } else if (i >= 33 && i <= 38) {
                            //Suma el importe de las retenciones
                            revisarRete = rs2.getBigDecimal(i);
                            retenciones = retenciones.add(revisarRete);
                            detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                            token = ",";
                        } else {
                            detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                            token = ",";
                        }
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                //Aqui grabamos dentro de layouts creados detalle
                insertLayoutDetalle(conn, listaIds, nFolioCompromiso);
            }
            //Valida que el total del Layout sea igual a los pagos
            validarTotalLayout(conn, total, listaIds);
            //Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetenciones(conn, retenciones, listaIds);
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
        }
    }

    public static ArrayList<String> BuscaCompromisosMenorUMA(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
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
            // encabezado SP
            rs = generaEncabezadoFederalizado(conn, listaIds, 1, 3);
            while (rs.next()) {
                String nFolio, nFolioCompromiso = rs.getString(1);
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String vcReferencia1 = rs.getString(23).trim();
                String vcReferencia2 = rs.getString(24).trim();
                String vcCtaBancaria = arrCuentasBancarias[intIndice].trim().trim();
                String vcBenef = rs.getString(18).trim();
                String vcLeyenda = arrLeyendas[intIndice].trim().trim();
                String vRFC = rs.getString(20).trim();
                String vcEstatus = "";
                String vIntermFin = "";
                String vnClaveAMF = "";
                /*String vcEstatus = rs.getString(42).trim();
					String vIntermFin = rs.getString(43).trim();
					String vnClaveAMF = rs.getString(44).trim();*/
                if (!"".equals(vnClaveAMF)) {
                    vcBenef = "S24676";
                    vcCtaBancaria = "22800100000100";
                    vcLeyenda = "1";
                    vRFC = "6001";
                }
                if ("Operada Pagada".equals(vcEstatus)) {
                    vcCtaBancaria = vIntermFin;
                }
                if (!"".equals(vcReferencia1) && "".equals(vcReferencia2)) {
                    vcLeyenda = "0";
                }
                String encabezado = rs.getString(2) + "," + arrFechas[intIndice].trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + rs.getString(16).trim() + "," + vcLeyenda + "," + vcBenef + "," + vcCtaBancaria + "," + vRFC + "," + rs.getString(21).trim() + "," + rs.getString(22).trim() + "," + rs.getString(23).trim() + "," + rs.getString(24).trim() + "," + rs.getString(25).trim().replaceAll("[\r\n]{2,}", " ") + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim() + "," + rs.getString(30).trim() + "," + rs.getString(31).trim() + "," + rs.getString(32).trim() + "," + rs.getString(33).trim() + "," + rs.getString(34).trim() + "," + rs.getString(35).trim() + "," + rs.getString(36).trim() + "," + rs.getString(37).trim() + "," + rs.getString(38).trim() + "," + rs.getString(39).trim() + "," + rs.getString(40).trim() + "," + rs.getString(41);
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                // detalle de SP
                rs2 = generaDetalleFederalizado(conn, rs.getString(1));
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 40; i++) {
                        if (i == 27) {
                            revisarTotal = rs2.getBigDecimal(i);
                            total = total.add(revisarTotal);
                            if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                                throw new Exception("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + listaIds);
                            } else {
                                detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                token = ",";
                            }
                        } else if (i >= 32 && i <= 37) {
                            //Suma el importe de las retenciones
                            revisarRete = rs2.getBigDecimal(i);
                            retenciones = retenciones.add(revisarRete);
                            detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                            token = ",";
                        } else {
                            detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                            token = ",";
                        }
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                // Aqui grabamos dentro de layouts creados detalle
                insertLayoutDetalle(conn, listaIds, nFolioCompromiso);
            }
            //Valida que el total del Layout sea igual a los pagos
            validarTotalLayout(conn, total, listaIds);
            //Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetenciones(conn, retenciones, listaIds);
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
        }
    }

    public static void insertLayoutDetalle(Connection conn, String listaIds, String nFolioCompromiso) throws Exception {
        PreparedStatement pstmntHLayoutDet = null;
        StringBuilder SqlLayoutGrabadoDet = new StringBuilder();
        try {
            SqlLayoutGrabadoDet.append(" INSERT INTO tLayoutsCreadosPagoObrasDetalle ");
            SqlLayoutGrabadoDet.append("SELECT DISTINCT " + nFolioCompromiso + ", '1' ID_EVENTO,'24.0.001' EVENTO,ltrim(TCEP.cRamo) ID_RAMO_ML, 'RHQ', TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion,");
            SqlLayoutGrabadoDet.append(" CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral");
            SqlLayoutGrabadoDet.append(", tCEP.cActividadInstitucional, tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)) CCAP_157, substring(cpartida,2,1) CCON_158, substring(cpartida,3,1) CPARG_300, substring(cpartida,4,2) CPAR_159");
            SqlLayoutGrabadoDet.append(", tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, tCEP.cCartera,ltrim('0000000' + tCEP.cUnidadEjecutora), substring(TCEP.cUnidadNorativa,2,2) CCOP_163, '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3, ");
            SqlLayoutGrabadoDet.append(" TPDE.mImporteNeto, MONTH(GETDATE()) MES_149, '0' NRES,ltrim(TPDD.ID_TIPO_CONCEPTO) TIPO_CONTRATO, '000' CONC_MOV, CONVERT(decimal(17,2),DC.DCD_ISR), CONVERT(decimal(17,2),DCD_IVA), CONVERT(decimal(17,2),DC.DCD_MIL5), CONVERT(decimal(17,2),DCD_MIL2)");
            SqlLayoutGrabadoDet.append(", CONVERT(decimal(17,2),DC.DCD_CONTRIBUCION), CONVERT(decimal(17,2),DC.DCD_OTRAS_RET), CONVERT(decimal(17,2),TPDD.mImporteIva) IVADesglosado, CONVERT(decimal(17,2),DC.DCD_PENALIZACION), '' id_ctr_intdet ");
            SqlLayoutGrabadoDet.append(" FROM TPAGOFEDERALIZADODETALLE TPDD inner join TPAGOFEDERALIZADOENCABEZADO TPDE ");
            SqlLayoutGrabadoDet.append(" on TPDD.NFOLIOPAGOFEDERALIZADO = TPDE.NFOLIOPAGOFEDERALIZADO ");
            SqlLayoutGrabadoDet.append(" inner join tCatalogoEP TCEP on TPDD.EP = TCEP.EP ");
            SqlLayoutGrabadoDet.append(" LEFT JOIN v_pagosDocComprobatoria DC ON DC.caNoContrarrecibo = TPDE.caNoContrarrecibo AND DC.cTipoPago = 'PAGOFEDERALIZADO' ");
            SqlLayoutGrabadoDet.append(" where TPDD.NFOLIOPAGOFEDERALIZADO in (" + listaIds + ") ");
            pstmntHLayoutDet = conn.prepareStatement(SqlLayoutGrabadoDet.toString());
            pstmntHLayoutDet.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntHLayoutDet);
        }
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            //PSC
            String Sql = " SELECT nFolioPAGOFEDERALIZADO, 'H' H, cRamo, 'RHQ', '' SOL_PAGO, '3', caNoContrarrecibo FOLIO_INTERNO, caNoContrarrecibo COMODIN " + " FROM tPAGOFEDERALIZADOEncabezado (NOLOCK) " + " WHERE nFolioPAGOFEDERALIZADO in (" + listaIds + ") " + " ORDER BY nFolioPAGOFEDERALIZADO";
            pstmntH = conn.prepareStatement(Sql);
            Log.debug("Object: " + String.valueOf(Sql));
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                rs2 = generaDocumentacionComprobatoriaDet(conn, nFolioCompromiso);
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
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmntH);
            CloseObject.closeObject(rs2);
        }
    }

    public static ResultSet generaDocumentacionComprobatoriaDet(Connection conn, String nFolioCompromiso) throws Exception {
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct PDE.cRamo, ");
        sql.append("REPLACE(REPLACE(DCD.DCD_FACTURA,',',''),'\"',''), ");
        sql.append("CONVERT(nvarchar(10), DCD.fAplicacion,103), ");
        sql.append("CONVERT(nvarchar(10), DCD.fRecepcion,103) + ' 12:00:00 a.m.', ");
        sql.append("ISNULL(BB.CBEN, isnull(DCD.DCD_CBEN,'')), ");
        sql.append("case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen', ");
        sql.append("DCD.DCD_TIPO_OPE, ");
        sql.append(" 	CASE WHEN CONVERT(int,PDE.mImporteIVA) = 0 THEN '05' ");
        sql.append("		ELSE  '07'	END TIVA, ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_VALOR), ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_IMP_BRUTO),  ");
        sql.append(" CONVERT(decimal(17,2), PDE.mImporteIVA) DCD_IVA, ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_IVADES) DCD_IVADES,  ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_ISR) DCD_ISR, ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_MIL5) DCD_MIL5, ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_MIL2) DCD_MIL2 , ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_OTRAS_RET) DCD_OTRAS_RET, ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_PENALIZACION) DCD_PENALIZACION, ");
        sql.append(" CONVERT(decimal(17,2), DCD.DCD_CONTRIBUCION) DCD_CONTRIBUCION, ");
        sql.append(" DCD.DCD_CTOEXT, ");
        sql.append(" REPLACE(REPLACE(DCD.cConcepto,',',''),'\"','')");
        sql.append(" , PDE.nFolioPAGOFEDERALIZADO ");
        sql.append(" from dbo.tPAGOFEDERALIZADOEncabezado PDE (NOLOCK) ");
        sql.append(" inner join dbo.v_pagosDocComprobatoria DCD (NOLOCK) on PDE.caNoContrarrecibo = DCD.caNoContrarrecibo AND DCD.cTipoPago = 'PAGOFEDERALIZADO' ");
        sql.append(" LEFT JOIN pContratoFederalizadoSesion sd (NOLOCK) ON PDE.cFolioContratoObra = sd.cIdContrato and bActivo = 1 AND sd.fSesionVigencia = (select convert(date, isnull(min(pcs.fSesionVigencia),'1900-01-01')) from pContratoFederalizadoSesion pcs where pcs.cIdContrato = sd.cIdContrato and bActivo = 1 and pcs.fSesionVigencia >= convert(date, GETDATE())) ");
        sql.append(" LEFT JOIN tBeneficiario BB (NOLOCK) ON BB.dRFC = sd.cIdRFCSesion ");
        sql.append(" inner join [dbo].[tBeneficiario] B (NOLOCK) on PDE.RFC = B.dRFC ");
        sql.append(" where PDE.nFolioPAGOFEDERALIZADO = " + nFolioCompromiso);
        sql.append(" ORDER BY PDE.nFolioPAGOFEDERALIZADO ");
        pstmntD = conn.prepareStatement(sql.toString());
        rs = pstmntD.executeQuery();
        return rs;
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGOFEDERALIZADOEncabezado SET nEnviadoSICOP = 1 WHERE nFolioPAGOFEDERALIZADO in (" + listaIds + ")");
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
            pstmnt = conn.prepareStatement("UPDATE tPAGOFEDERALIZADOEncabezado SET nEnviadoSICOP = 0 WHERE nFolioPAGOFEDERALIZADO in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso) throws SQLException {
        PreparedStatement pstmntL = null;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + "                             WHERE caNoCompromiso = '" + caNoCompromiso + "'";
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
        String queryInsert = "INSERT INTO tLayoutCompromisos(" + "                                               cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso," + "                                               cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP," + "                                               nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud," + "                                               cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC," + "                                               caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento," + "                                               nDocumento,cDescripcion)" + "             VALUES(" + "'" + clave + "','" + cRamo + "','" + cUnidadResponsable + "','" + folioSICOP + "','" + idProceso + "'," + "'" + cCentroContable + "','" + fExpedicion + "'," + total + ",'" + cTipoPoliza + "','" + nFolioPoliza + "'," + "'" + nPolizaCancelacion + "','" + tipoMovimiento + "','" + origenPresupuesto + "','" + cuentaBancaria + "','" + noSolicitud + "'," + "'" + tCambio + "','" + tMoneda + "','" + tSolicitud + "','" + volante + "','" + rfc + "'," + "'" + caNoCompromiso + "','" + codSemarnat2 + "','" + estatus + "','" + fAplicacion + "','" + documento + "'," + "'" + nDocumento + "','" + descripcion + "')";
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

    public static ResultSet generaEncabezadoFederalizado(Connection conn, String listaIds, int origen, int tipoSol) throws Exception {
        PreparedStatement pstmntH = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT distinct ");
        sql.append(" tCE.nFolioPAGOFEDERALIZADO, ");
        sql.append(" 'H' AS Header, ");
        sql.append(" CONVERT(nvarchar(10), tCE.fAplicacion ,103), ");
        sql.append(" CONVERT(nvarchar(10), tCE.fAplicacion,103), ");
        sql.append(" tCE.cRamo, ");
        sql.append(" tCE.cRamo, ");
        sql.append(" tCE.cRamo, ");
        sql.append(" 'RHQ' Responsable,");
        sql.append(" 'RHQ' UnidadResponsable, ");
        sql.append(" 'RHQ' UnidadResponsable, ");
        sql.append(" 'N' ID_TIPO_MOVIMIENTO, ");
        sql.append(" ' " + origen + "' AS OrigenPpto, ");
        sql.append(" ' " + tipoSol + "' AS TipoSol, ");
        sql.append(" 'MXN' TipoMoneda , ");
        sql.append(" '1' TipoCambio, ");
        sql.append(" '1' TIPO_PAGO, ");
        sql.append(" 'PENDIENTE' CveLeyenda, ");
        sql.append(" ISNULL(BB.CBEN, isnull(B.CBEN,'')), ");
        sql.append(" 'Cuenta' CUENTA_BANCARIA, ");
        sql.append(" rtrim(ISNULL(SD.cIdRFCSesion, tCE.RFC)), ");
        sql.append(" 'FAC', ");
        sql.append(" '' FechaReferencia, ");
        sql.append(" isnull(ta.nClaveAMF, '') Referencia1, ");
        sql.append(" isnull(ta.numFolioAMF, '') Referencia2, ");
        sql.append(" LEFT(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LTRIM(RTRIM(tCE.cConcepto)), CHAR(160), ' '), '  ', ' ') , ',', ''), CHAR(10), ''),CHAR(9), ''),CHAR(13), '') , 70), ");
        sql.append(" '' NotasReverso, ");
        sql.append(" isnull(ta.nClaveAMF, '') AMF, ");
        sql.append(" rtrim(DC.caNoContrarrecibo) NO_ACMI, ");
        sql.append(" rtrim(DC.caNoContrarrecibo) AuxiliarComodin, ");
        sql.append(" '' CTR , ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_ISR, 0)) ISR, ");
        sql.append(" CONVERT(decimal(17, 2), isNull(DC.DCD_IVADES,0)) RETIVA, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL5, 0)) R5MILLAR, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL2, 0)) R2MILLAS, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_OTRAS_RET, 0)) OTRASRET, ");
        sql.append(" CASE when DC.DCD_PENALIZACION = 0 or DC.DCD_PENALIZACION is null THEN '0' else CONVERT(varchar (20),DC.DCD_PENALIZACION) end PENALIZA, ");
        sql.append(" CASE when DC.DCD_CONTRIBUCION = 0 or DC.DCD_CONTRIBUCION is null THEN '0' else CONVERT(varchar (20),DC.DCD_CONTRIBUCION) end CONTRIB, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(tCE.mimporteIVA, 0)) IVA, ");
        sql.append(" CONVERT(decimal(17, 2), tCE.mAmortizacionAnticipo - (tCE.mAmortizacionAnticipo / ( 1 + (mImporteIVA * .01)))) IVAANT, ");
        sql.append(" '' FolioDC, ");
        sql.append(" 'NA' ID_DESTINO_GASTO ");
        sql.append(" FROM tPAGOFEDERALIZADOEncabezado tCE (NOLOCK)");
        sql.append(" LEFT JOIN pContratoFEDERALIZADOSesion sd (NOLOCK) ON tCE.cFolioContratoObra = sd.cIdContrato and bActivo = 1 AND sd.fSesionVigencia = (select convert(date, isnull(min(pcs.fSesionVigencia), '1900-01-01')) from pContratoFederalizadoSesion pcs where pcs.cIdContrato = sd.cIdContrato and bActivo = 1 and pcs.fSesionVigencia >= convert(date, GETDATE())) ");
        sql.append(" LEFT JOIN tBeneficiario B (NOLOCK) ON tce.RFC = B.dRFC ");
        sql.append(" LEFT JOIN tBeneficiario BB (NOLOCK) ON BB.dRFC = sd.cIdRFCSesion ");
        sql.append(" LEFT JOIN tPagoAMF ta (NOLOCK) ON tCE.NumPagoAMF = ta.numPagoAMF ");
        sql.append(" INNER JOIN tBeneficiarioCuentasBancarias BCB (NOLOCK) ON tCE.RFC = BCB.dRFC ");
        sql.append(" LEFT JOIN pCatalogoTipoDocumento CTD (NOLOCK) ON tCE.cIdTipoDocumento =	CTD.cIdTipoDocumento ");
        sql.append(" LEFT JOIN v_pagosDocComprobatoria DC (NOLOCK) ON DC.caNoContrarrecibo =tCE.caNoContrarrecibo AND DC.cTipoPago = 'PAGOFEDERALIZADO' ");
        sql.append(" WHERE tCE.nFolioPAGOFEDERALIZADO in (" + listaIds + ") ");
        sql.append(" order by tCE.nFolioPAGOFEDERALIZADO ");
        pstmntH = conn.prepareStatement(sql.toString());
        Log.debug("Object: " + String.valueOf(sql.toString()));
        rs = pstmntH.executeQuery();
        return rs;
    }

    public static ResultSet generaDetalleCompromisoFed(Connection conn, String ids) throws Exception {
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT  ");
        /*A*/
        sql.append(" '1' ID_EVENTO, ");
        /*B*/
        sql.append(" '24.0.001' EVENTO, ");
        /*C*/
        sql.append(" ltrim(TCEP.cRamo) ID_RAMO_ML,  ");
        /*D*/
        sql.append(" 'RHQ',  ");
        /*E*/
        sql.append(" TCEP.aEjercicioFiscal,  ");
        /*F*/
        sql.append(" TCEP.cGrupoFuncional,  ");
        /*G*/
        sql.append(" tCEP.cFuncion,  ");
        /*H*/
        sql.append(" tCEP.cSubFuncion,  ");
        /*I*/
        sql.append(" CASE WHEN tCEP.cProgramaGeneral IN ( SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK) ) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, ");
        /*J*/
        sql.append(" tCEP.cActividadInstitucional,  ");
        /*K*/
        sql.append(" tCEP.cProgramaPresupuestario,  ");
        /*L*/
        sql.append(" ltrim(substring(cpartida,1,1)) CCAP_157,  ");
        /*M*/
        sql.append(" substring(cpartida,2,1) CCON_158,  ");
        /*N*/
        sql.append(" substring(cpartida,3,1) CPARG_300,  ");
        /*O*/
        sql.append(" substring(cpartida,4,2) CPAR_159,  ");
        /*P*/
        sql.append(" tCEP.cTipoGasto,  ");
        /*Q*/
        sql.append(" tCEP.cFuenteFinanciamiento,  ");
        /*R*/
        sql.append(" tCEP.cEntidadFederativa,  ");
        /*S*/
        sql.append(" SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) cCartera, ");
        /*T*/
        sql.append(" '0000000000', ");
        /*U*/
        sql.append(" '00', ");
        /*V*/
        sql.append(" '000' PL,  ");
        /*W*/
        sql.append(" '000' OFI,  ");
        /*X*/
        sql.append("  '00000' AUX1,  ");
        /*Y*/
        sql.append(" '00000' AUX2,  ");
        /*Z*/
        sql.append(" '0000000000' AUX3,  ");
        /*AA*/
        sql.append(" ISNULL( (SELECT TOP 1 nFolioAutSICOP FROM tCompromisoEncabezado WITH (NOLOCK) where cIdContrato IN (TPDE.cFolioContratoObra ) and nFolioSuficiencia is not null and nFolioSuficiencia !=1 ORDER BY nFolioCompromiso DESC ), '') folioCompromiso,");
        /*AB*/
        sql.append(" SUM(CONVERT(decimal(17, 2), TPDD.mImporteNeto)) Monto, ");
        /*AC*/
        sql.append(" MONTH(GETDATE()) MES_149,  ");
        /*AD*/
        sql.append(" ( SELECT top 1 nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) where cIdContrato IN (TPDE.cFolioContratoObra ) and nFolioSuficiencia is not null and nFolioSuficiencia !=1 ORDER BY nFolioCompromiso DESC ) folioSuficiencia, ");
        /*AE*/
        sql.append(" 'GD' TIPO_CONTRATO,  ");
        /*AF*/
        sql.append(" '000' CONC_MOV,  ");
        /*AG*/
        sql.append(" CONVERT(decimal(17, 2), sum(TPDD.mISRHonorarios + TPDD.mISRArrenda + TPDD.mimporteISRResico)) DCD_ISR, ");
        /*AH*/
        sql.append(" CONVERT(decimal(17, 2), sum(TPDD.m23Iva + mImporteIvaHonorarios + mImporteIvaArrenda + TPDD.mImporteFlete23 + ISNULL(TPDD.mImporteFlete4, 0)+ ISNULL(TPDD.mImporteIva6, 0))) DCD_IVA, ");
        /*AI*/
        sql.append(" CONVERT(decimal(17, 2), sum(TPDD.mObra5)) DCD_MIL5, ");
        /*AJ*/
        sql.append(" ISNULL( CONVERT(decimal(17, 2), sum(TPDD.mCNIC + TPDD.mIMDT)) , 0)DCD_MIL2, ");
        /*AK*/
        sql.append(" ISNULL( CONVERT(decimal(17, 2), 0) , 0) DCD_CONTRIBUCION, ");
        /*AL*/
        sql.append(" ISNULL( CONVERT(decimal(17, 2), sum(ISNULL(TPDD.mRetImpuestoCedular, 0)))  , 0) AS  DCD_OTRAS_RET, ");
        /*AM*/
        sql.append(" ISNULL( CONVERT(decimal(17,2), sum(ISNULL(TPDD.mIva,0))) , 0), ");
        /*AN*/
        sql.append(" ISNULL( CONVERT(decimal(17,2), Sum(TPDD.mAmortizacionAnticipo)) , 0), ");
        /*AO*/
        sql.append(" ISNULL( CONVERT(decimal(17, 2), sum(TPDD.mPenalizacion + TPDD.mTesofe)) , 0) DCD_PENALIZACION, ");
        /*AP*/
        sql.append(" ISNULL( CONVERT(decimal(17,2), Sum( TPDD.mAmortizacionAnticipo - (TPDD.mAmortizacionAnticipo / ( 1 + (TPDE.mImporteIVA * .01))) )), 0 )  ANTIVA, ");
        /*AQ*/
        sql.append(" '' id_ctr_intdet ");
        sql.append(" , TPDD.nFolioPAGOFEDERALIZADO ");
        sql.append(" FROM  tPAGOFEDERALIZADODetalle TPDD (NOLOCK) inner join tPAGOFEDERALIZADOEncabezado TPDE  (NOLOCK) ");
        sql.append(" 	on TPDD.nFolioPAGOFEDERALIZADO = TPDE.nFolioPAGOFEDERALIZADO   ");
        sql.append(" inner join tCatalogoEP TCEP (NOLOCK) ");
        sql.append(" 	 on TPDD.EP = TCEP.EP   ");
        sql.append(" LEFT JOIN v_pagosDocComprobatoria DC (NOLOCK) ");
        sql.append(" 	 ON DC.caNoContrarrecibo = TPDE.caNoContrarrecibo AND DC.cTipoPago = 'PAGOFEDERALIZADO'  ");
        sql.append(" LEFT OUTER JOIN tUnidadProyecto ueProyecto WITH(NOLOCK) ");
        sql.append("    ON SUBSTRING( TPDD.ep, 61,3 ) = ueProyecto.cUnidadResponsable ");
        sql.append(" WHERE TPDD.nFolioPAGOFEDERALIZADO in (" + ids + ") AND cEvento <> 'ANTICIPO' ");
        sql.append(" GROUP BY TCEP.cRamo, ");
        sql.append("	TCEP.aEjercicioFiscal,");
        sql.append("	TCEP.cGrupoFuncional, ");
        sql.append("	tCEP.cFuncion, ");
        sql.append("	tCEP.cSubFuncion, ");
        sql.append("	cProgramaGeneral,");
        sql.append("	tCEP.cActividadInstitucional,");
        sql.append("	tCEP.cProgramaPresupuestario, ");
        sql.append("	cpartida, ");
        sql.append("	tCEP.cTipoGasto, ");
        sql.append("	tCEP.cFuenteFinanciamiento, ");
        sql.append("	tCEP.cEntidadFederativa, ");
        sql.append("	SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11), ");
        sql.append("   ltrim(TPDD.ID_TIPO_CONCEPTO), cFolioContratoObra, TPDE.cCentroContable  ");
        sql.append("   ,ueProyecto.nIdProyecto,  TPDD.nFolioPAGOFEDERALIZADO");
        sql.append("   order by TPDD.nFolioPAGOFEDERALIZADO ");
        pstmntD = conn.prepareStatement(sql.toString());
        rs = pstmntD.executeQuery();
        return rs;
    }

    public static ResultSet generaDetalleFederalizado(Connection conn, String folio) throws Exception {
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT '1' ID_EVENTO,'24.0.001' EVENTO,ltrim(TCEP.cRamo) ID_RAMO_ML, 'RHQ', TCEP.aEjercicioFiscal, ");
        sql.append(" TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, ");
        sql.append("  CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, tCEP.cActividadInstitucional,");
        sql.append(" tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)) CCAP_157, substring(cpartida,2,1) CCON_158, substring(cpartida,3,1) CPARG_300,  ");
        sql.append(" substring(cpartida,4,2) CPAR_159, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, ");
        sql.append(" SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) cCartera, ");
        sql.append(" '0000000000','00'CCOP_163, ");
        sql.append(" '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, ");
        sql.append(" '0000000000' AUX3,   CONVERT(decimal(17,2),SUM(tpdd.mImporteNeto))  Monto, ");
        sql.append("  MONTH(GETDATE()) MES_149, '0' NRES, CASE WHEN cpartida='35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO, '000' CONC_MOV, ");
        sql.append(" CONVERT(decimal(17,2),Sum(TPDD.mISRHonorarios + TPDD.mISRArrenda + TPDD.mimporteISRResico)) DCD_ISR,  ");
        sql.append("  CONVERT(decimal(17, 2),sum(TPDD.m23Iva + mImporteIvaHonorarios + mImporteIvaArrenda + TPDD.mImporteFlete23 + ISNULL(TPDD.mImporteIva6,0)+ ISNULL(TPDD.mImporteFlete4, 0))) DCD_IVA, ");
        sql.append("  CONVERT(decimal(17,2),Sum(TPDD.mObra5))  DCD_MIL5,  ");
        sql.append("  CONVERT(decimal(17,0),Sum(TPDD.mCNIC + TPDD.mIMDT)) DCD_MIL2, ");
        sql.append("  CONVERT(decimal(17, 0), 0) DCD_CONTRIBUCION,  ");
        sql.append("  CONVERT(decimal(17,2),Sum(TPDD.mRetImpuestoCedular)) DCD_OTRAS_RET, ");
        sql.append("   CONVERT( DECIMAL( 17,0), DCD_PENALIZACION )  DCD_PENALIZACION , '' id_ctr_intdet,  TPDD.NFOLIOPAGOFEDERALIZADO ");
        sql.append("  FROM TPAGOFEDERALIZADODETALLE TPDD WITH(NOLOCK) inner join TPAGOFEDERALIZADOENCABEZADO TPDE WITH(NOLOCK)  on TPDD.NFOLIOPAGOFEDERALIZADO = TPDE.NFOLIOPAGOFEDERALIZADO and Left(TPDD.cEvento, 8) <> 'ANTICIPO' ");
        sql.append("  INNER JOIN tCatalogoEP TCEP WITH(NOLOCK) on TPDD.EP = TCEP.EP  ");
        sql.append("  LEFT JOIN v_pagosDocComprobatoria DC WITH(NOLOCK) ON DC.caNoContrarrecibo = TPDE.caNoContrarrecibo AND DC.cTipoPago = 'PAGOFEDERALIZADO'  ");
        sql.append("  WHERE TPDD.NFOLIOPAGOFEDERALIZADO in (" + folio + " ) ");
        sql.append("  group by TCEP.cRamo,  	TCEP.aEjercicioFiscal , ");
        sql.append(" 	TCEP.cGrupoFuncional,  	tCEP.cFuncion,  	tCEP.cSubFuncion, cProgramaGeneral,");
        sql.append(" 	tCEP.cActividadInstitucional, 	tCEP.cProgramaPresupuestario,  	cpartida,  	tCEP.cTipoGasto,  	tCEP.cFuenteFinanciamiento,  	tCEP.cEntidadFederativa,  	tCEP.cCartera,");
        sql.append("    ltrim(TPDD.ID_TIPO_CONCEPTO), TPDE.NFOLIOPAGOFEDERALIZADO, TPDE.cCentroContable, DCD_PENALIZACION, TPDD.NFOLIOPAGOFEDERALIZADO, SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11)  ");
        sql.append("  ORDER BY TPDD.NFOLIOPAGOFEDERALIZADO  ");
        pstmntD = conn.prepareStatement(sql.toString());
        rs = pstmntD.executeQuery();
        return rs;
    }

    public static void validarTotalLayout(Connection con, BigDecimal total, String listaIds) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteNeto) FROM tPAGOFEDERALIZADODetalle (NOLOCK) WHERE nFolioPAGOFEDERALIZADO IN (" + listaIds + ")";
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
        String query = "SELECT SUM(mimporteISRResico + mImporteISRLaudos + mISROtros +mimporteIvaArrenda + mimporteivahonorarios + mimporteflete23 + mImporteFlete4 + mObra5) totalRetenciones " + " FROM tPAGOFEDERALIZADODetalle (NOLOCK) WHERE nFolioPAGOFEDERALIZADO IN (" + listaIds + ")";
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            int resultado = total.compareTo(importe);
            if (resultado != 0) {
                throw new Exception("El importe de las Retenciones es diferente de la suma de los pagos. Reporte al administrador.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }
}
