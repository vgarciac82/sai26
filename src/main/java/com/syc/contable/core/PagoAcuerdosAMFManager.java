package com.syc.contable.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;

public class PagoAcuerdosAMFManager {

    public PagoAcuerdosAMFManager() {
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
        ResultSet rsLayout = null;
        String[] arrFolios = listaIds.split(",");
        String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
        String[] arrFechas = listaFechas.split(",");
        String[] arrLeyendas = listaLeyendas.split(",");
        int intIndice = -1;
        // Encabezado
        String Sql = "SELECT 'H' AS Header, numPagoAMF, '16' AS ramo, 'RHQ' cUR, '1' AS tipoCLC, 'MXN' AS divisa, '1' AS TipoCambio, '16' AS ClaveBeneficiario," + " rfcAMF, cuentaBancaria, '2' AS Leyenda, '48' AS FolioAMF, CONVERT(VARCHAR,fechaPago,103) AS fechaPago " + " FROM tPagoAMF " + " WHERE numPagoAMF in (" + listaIds + ") ";
        pstmntH = conn.prepareStatement(Sql);
        rs = pstmntH.executeQuery();
        while (rs.next()) {
            String nFolio, nFolioCompromiso = rs.getString(1);
            for (int i = 0; i < arrFolios.length; i++) {
                nFolio = arrFolios[i].trim();
                if (nFolio.equals(nFolioCompromiso)) {
                    intIndice = i;
                    break;
                }
            }
            String encabezado = rs.getString(1) + "," + rs.getString(2) + "," + rs.getString(3) + "," + rs.getString(4).trim() + "," + rs.getString(5) + "," + rs.getString(6) + "," + rs.getString(7) + "," + rs.getString(8) + "," + rs.getString(9) + "," + rs.getString(10) + "," + rs.getString(11) + "," + rs.getString(12) + "," + ',' + ',' + ',' + ',' + rs.getString(13) + ',' + ',' + ',' + ',' + ',' + ',' + ',' + ',' + ',' + ',' + ',' + '1';
            encabezado = encabezado + "\r\n";
            arrListaComp.add(encabezado);
            int retval;
            String Sql2 = "SELECT '65' AS idEvento, '340004' AS Evento, 16 as Ramo, 'RHQ' cUR,CONVERT(VARCHAR,CONVERT(money,importePago)) AS importePago " + " FROM tPagoAMF " + " WHERE numPagoAMF = '" + rs.getString(2) + "'";
            pstmntD = conn.prepareStatement(Sql2);
            rs2 = pstmntD.executeQuery();
            while (rs2.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < 6; i++) {
                    detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                    token = ",";
                }
                token = "";
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
            if (rs2 != null) {
                rs2.close();
            }
            if (pstmntD != null) {
                pstmntD.close();
            }
            //PSC Aqui termina el grabado dentro de layouts creados detalle
        }
        if (rs != null) {
            rs.close();
        }
        if (pstmntH != null) {
            pstmntH.close();
        }
        return arrListaComp;
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String Sql = " SELECT " + "		nFolioOperAjenas , 'H' H, cRamo, 'RHQ', '' SOL_PAGO, " + "		0 cIdTipoPagoDirecto, caNoContrarrecibo FOLIO_INTERNO, caNoContrarrecibo COMODIN " + "	FROM  dbo.tOperAjenasEncabezado " + "	WHERE nFolioOperAjenas  in (" + listaIds + " ) ";
        pstmntH = conn.prepareStatement(Sql);
        System.out.println(Sql);
        rs = pstmntH.executeQuery();
        while (rs.next()) {
            String nFolioCompromiso = rs.getString(1);
            String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
            encabezado = encabezado + "\r\n";
            arrListaComp.add(encabezado);
            //aqui es donde se modifica documentación comprobatoria
            String Sql2 = " SELECT DISTINCT PDE.cRamo, DCD.DCD_FACTURA,	CONVERT(nvarchar(10), DCD.fAplicacion,103), " + " CONVERT(nvarchar(10), DCD.fRecepcion,103) + ' 12:00:00 a.m.', DCD.DCD_CBEN,	" + " case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen'," + " DCD.DCD_TIPO_OPE, DCD.DCD_TIVA 'TIVA', " + " CONVERT(decimal(17, 2), DCD.DCD_VALOR), " + " CONVERT(decimal(17, 2), DCD.DCD_IMP_BRUTO) BRUTO, " + " CONVERT(decimal(17, 2), DCD.DCD_IVADES) IVA, " + " CONVERT(decimal(17, 2), DCD.DCD_IVA) RETIVA, " + " CONVERT(decimal(17, 2), DCD.DCD_ISR) ISR, " + " CONVERT(decimal(17, 2), DCD.DCD_MIL5) R5MILLAR, " + " CONVERT(decimal(17, 2), DCD.DCD_MIL2) R2MILLAS, " + " CONVERT(decimal(17, 2), DCD.DCD_OTRAS_RET) OTRASRET, " + " CONVERT(decimal(17, 2), DCD.DCD_PENALIZACION) PENALIZA, " + " CONVERT(decimal(17, 2), DCD.DCD_CONTRIBUCION) CONTRIB, " + " DCD.DCD_CTOEXT, " + " PDE.cIdDocumento, " + " DCD.cConcepto " + " from dbo.tOperAjenasEncabezado PDE inner join dbo.v_pagosDocComprobatoria DCD " + " on PDE.caNoContrarrecibo = DCD.caNoContrarrecibo " + " inner join [dbo].[tBeneficiario] B (NOLOCK) on PDE.cIdRFC = B.dRFC " + " inner join [dbo].[CAT_TIPO_IVA] TI (NOLOCK) on TI.TIVA = DCD.DCD_TIVA " + " where PDE.nFolioOperAjenas  = " + nFolioCompromiso;
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
            if (rs2 != null) {
                rs2.close();
            }
            if (pstmntD != null) {
                pstmntD.close();
            }
        }
        if (rs != null) {
            rs.close();
        }
        if (pstmntH != null) {
            pstmntH.close();
        }
        return arrListaComp;
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPagoAMF SET nEnviadoSICOP = 1 WHERE numPagoAMF  in (" + listaIds + ")");
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
            //pstmnt = conn.prepareStatement("UPDATE tOperAjenasEncabezado SET nEnviadoSICOP = 0 WHERE nFolioOperAjenas in (" + listaIds + ")");
            pstmnt = conn.prepareStatement("UPDATE tPagoAMF SET nEnviadoSICOP = 0 WHERE numPagoAMF in (" + listaIds + ")");
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
        String queryUpdateEstatus = "UPDATE tOperAjenasEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + "                             WHERE caNoCompromiso = '" + caNoCompromiso + "'";
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
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return insertReg;
    }

    public static ArrayList<String> buscarCierreAMFExport(Connection conn, String listaIds) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        PreparedStatement pstmnt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        // Trae informacion para Exportar
        String exportDoc = "SELECT docto, caNoContrarrecibo, cIdRFC, cnombre, fAplicacion, mImporteNeto, mImporteTotal,nFolioPago" + " FROM vMultilistadorPagosAMFCierre " + " WHERE caNoContrarrecibo IN (" + listaIds + ")";
        pstmnt = conn.prepareStatement(exportDoc);
        rs = pstmnt.executeQuery();
        String encabezado = "Tipo Documento, Cuenta Por Pagar, RFC, Beneficiario, Fecha Aplicacion, Importe Neto, Importe Ejercer";
        encabezado = encabezado + "\r\n";
        arrListaComp.add(encabezado);
        while (rs.next()) {
            String detalles = rs.getString(1) + "," + rs.getString(2) + "," + rs.getString(3) + "," + rs.getString(4).replaceAll(",", " ") + "," + rs.getString(5) + "," + rs.getString(6).replaceAll(",", " ") + "," + rs.getString(7).replaceAll(",", " ");
            detalles = detalles + "\r\n";
            arrListaComp.add(detalles);
            String exportDocDetalle = "SELECT  EP,mImporteNeto" + " FROM vPagosDetalles " + " WHERE nfoliopagos = " + rs.getString(8) + " and documento = '" + rs.getString(1) + "' ";
            pstmnt2 = conn.prepareStatement(exportDocDetalle);
            rs2 = pstmnt2.executeQuery();
            while (rs2.next()) {
                String detallesEP = rs2.getString(1) + "," + rs2.getString(2);
                detallesEP = detallesEP + "\r\n";
                arrListaComp.add(detallesEP);
            }
        }
        if (rs != null) {
            rs.close();
        }
        if (rs2 != null) {
            rs2.close();
        }
        if (pstmnt != null) {
            pstmnt.close();
        }
        if (pstmnt2 != null) {
            pstmnt2.close();
        }
        return arrListaComp;
    }

    public static ArrayList<String> buscarAdefaExport(Connection conn, String listaIds) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        PreparedStatement pstmnt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        // Trae informacion para Exportar
        String exportDoc = "SELECT docto, caNoContrarrecibo, cIdRFC, cnombre, cUnidadResponsable, aEjercicioFiscal, fAplicacionF, mImporteNeto, nFolioEnc, docAplicado" + " FROM vAdefasEncabezado " + " WHERE caNoContrarrecibo IN (" + listaIds + ") ORDER BY docto ASC";
        pstmnt = conn.prepareStatement(exportDoc);
        rs = pstmnt.executeQuery();
        String encabezado = "Tipo Documento, Cuenta Por Pagar, RFC, Beneficiario, U. Responsable,  Ejercicio Fiscal, Fecha Aplicacion, Importe Neto, Folio, Estatus";
        encabezado = encabezado + "\r\n";
        arrListaComp.add(encabezado);
        while (rs.next()) {
            String detallesEnc = rs.getString(1) + "," + rs.getString(2) + "," + rs.getString(3) + "," + rs.getString(4).replaceAll(",", " ") + "," + rs.getString(5) + "," + rs.getString(6).replaceAll(",", " ") + "," + rs.getString(7).replaceAll(",", " ") + "," + rs.getString(8).replaceAll(",", " ") + "," + rs.getString(9).replaceAll(",", " ") + "," + rs.getString(10);
            detallesEnc = detallesEnc + "\r\n";
            arrListaComp.add(detallesEnc);
            String exportDocDetalle = "SELECT  EP, mImporteNeto" + " FROM vAdefasDetalle " + " WHERE nFolioDetalle = " + rs.getString(9) + " and documento = '" + rs.getString(1) + "' ";
            pstmnt2 = conn.prepareStatement(exportDocDetalle);
            rs2 = pstmnt2.executeQuery();
            while (rs2.next()) {
                String detallesEP = "," + rs2.getString(1) + "," + rs2.getString(2);
                detallesEP = detallesEP + "\r\n";
                arrListaComp.add(detallesEP);
            }
        }
        if (rs != null) {
            rs.close();
        }
        if (rs2 != null) {
            rs2.close();
        }
        if (pstmnt != null) {
            pstmnt.close();
        }
        if (pstmnt2 != null) {
            pstmnt2.close();
        }
        return arrListaComp;
    }
}
