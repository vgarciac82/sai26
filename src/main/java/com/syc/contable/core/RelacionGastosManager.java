package com.syc.contable.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.axtel.egresos.core.CargaMasivaRG;
import com.axtel.egresos.core.MasiveOperation;
import com.axtel.egresos.core.RGMasiva;
import com.syc.comprobacionlaudos.ComprobacionLaudosManager;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.contable.caja.core.CajaManager;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.RelacionGastosDetalle;
import com.syc.egresos.core.RelacionGastosEncabezado;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelacionGastosManager {

    private static final Logger log = LoggerFactory.getLogger(RelacionGastosManager.class);

    public static final String CARGA_MASIVA_LAUDOS = "5";

    public RelacionGastosManager() {
        super();
    }

    public static boolean remanenteSuficiente(Connection conn, int nFolioCaja, double montosRelacion) throws Exception {
        log.info("Calculando remanente para la solicitud " + nFolioCaja);
        String query = "SELECT mMontoRemanente FROM dbo.tEstadoDeCuentaViaticosEncabezado (NOLOCK) WHERE nFolioCaja = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        double remanente = 0.0d;
        boolean remanenteSuficiente = false;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioCaja);
            rs = ps.executeQuery();
            if (rs.next()) {
                remanente = rs.getDouble("mMontoRemanente");
                remanenteSuficiente = (remanente - montosRelacion) >= 0;
                return remanenteSuficiente;
            } else
                throw new Exception("No se encontro el folio de caja [" + nFolioCaja + "] para calcular su remanente");
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] arrFolios = listaIds.split(",");
        String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
        String[] arrFechas = listaFechas.split(",");
        String[] arrLeyendas = listaLeyendas.split(",");
        int intIndice = -1;
        BigDecimal total = new BigDecimal(0);
        BigDecimal revisar = new BigDecimal(0);
        BigDecimal retenciones = new BigDecimal(0);
        BigDecimal revisarRete = new BigDecimal(0);
        String cxp = ValidaNoTengaLayuout(conn, listaIds);
        if (!cxp.equals("")) {
            throw new Exception("Existen solicitudes que ya habian sido integradas :" + cxp);
        }
        // Genera encabezado
        StringBuilder query = new StringBuilder();
        query.append("  SELECT distinct tCE.nFolioRELACIONGASTOS, 'H' AS Header," + arrFechas[intIndice].trim() + ", CONVERT(nvarchar(10), tCE.fAplicacion,103) ");
        query.append("		,tCE.cRamo,tCE.cRamo,tCE.cRamo, 'RHQ' UnidadResponsable,'RHQ' UnidadResponsable,'RHQ' UnidadResponsable");
        query.append("		, 'N' ID_TIPO_MOVIMIENTO, '1' AS OrigenPpto,'3' AS TipoSol, 'MXN' TipoMoneda , '1' TipoCambio, '1' TIPO_PAGO");
        query.append("		, " + arrLeyendas[intIndice].trim().trim() + " AS CveLeyenda, B.CBEN, " + arrCuentasBancarias[intIndice].trim().trim() + " CUENTA_BANCARIA, rtrim(tCE.cIdRFC), 'FAC'");
        query.append("		, '' FechaReferencia, '' Referencia1, '' Referencia2, LEFT(REPLACE(REPLACE(tCE.cConcepto,',',''),'\"',''), 70)");
        query.append("		, '' NotasReverso, '' AMF, rtrim(tCE.caNoContrarrecibo) NO_ACMI, rtrim(tCE.caNoContrarrecibo) AuxiliarComodin");
        query.append("		, '' CTR , '' FolioDC, CONVERT(decimal(17, 2), DC.DCD_ISR), CONVERT(decimal(17, 2),DCD_IVADES)");
        query.append("		, CONVERT(decimal(17, 2),DC.DCD_MIL5), CONVERT(decimal(17, 2),DCD_MIL2), mImporteRetencion");
        query.append("		, CONVERT(decimal(17, 2),DC.DCD_PENALIZACION), CONVERT(decimal(17, 2), DC.DCD_CONTRIBUCION)");
        query.append("		, CONVERT(decimal(17, 2),DC.DCD_IVA), '' IVAANT, 'NA' ID_DESTINO_GASTO ");
        query.append("		 FROM tRELACIONGASTOSEncabezado tCE (NOLOCK) ");
        query.append("		 LEFT JOIN tBeneficiario B (NOLOCK) ON tce.cIdRFC = B.dRFC ");
        query.append("		 INNER JOIN tBeneficiarioCuentasBancarias BCB (NOLOCK) ON tCE.cIdRFC =	BCB.dRFC ");
        query.append("		 LEFT JOIN pCatalogoTipoDocumento CTD (NOLOCK) ON tCE.cIdTipoDocumento =	CTD.cIdTipoDocumento ");
        query.append("		 LEFT JOIN v_DCD_RELGASTO DC (NOLOCK) ON DC.NFOLIORELACIONGASTOS =tCE.nFolioRELACIONGASTOS ");
        query.append("	 WHERE tCE.nFolioRELACIONGASTOS in ( " + listaIds + "  )  ");
        try {
            pstmntH = conn.prepareStatement(query.toString());
            rs = pstmntH.executeQuery();
            log.debug(query.toString());
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                log.debug(arrFolios[0].trim());
                String nFolio, nFolioCompromiso = rs.getString(1);
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String tok = "";
                StringBuffer encabezado = new StringBuffer();
                for (int i = 2; i <= rsmd.getColumnCount(); i++) {
                    encabezado.append(tok).append(rs.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                    tok = ",";
                }
                encabezado.append("\r\n");
                arrListaComp.add(encabezado.toString());
                // Genera detalle Layout
                rs2 = generaLayoutDetalleRG(conn, nFolioCompromiso);
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                while (rs2.next()) {
                    for (int i = 1; i < 40; i++) {
                        // Validar que el importe no sea negativo
                        if (i == 27) {
                            revisar = rs2.getBigDecimal(i);
                            total = total.add(revisar);
                            if (revisar.compareTo(BigDecimal.ZERO) < 0) {
                                throw new Exception("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + listaIds);
                            } else {
                                detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                token = ",";
                            }
                        } else if (i >= 32 && i <= 36) {
                            // Suma el importe de las retenciones
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
            }
            validarTotalLayout(conn, total, listaIds);
            validarTotalRetenciones(conn, retenciones, listaIds);
        } finally {
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(pstmntH);
        }
        return arrListaComp;
    }

    public static void validarTotalLayout(Connection con, BigDecimal total, String listaIds) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteNeto) FROM tRELACIONGASTOSDetalle (NOLOCK) WHERE nFolioRELACIONGASTOS IN (" + listaIds + ")";
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

    public static void validarTotalLayoutIntegrada(Connection con, BigDecimal total, int folioIntegrada) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteNeto) FROM tRELACIONGASTOSDetalle (NOLOCK) WHERE nFolioRELACIONGASTOS in (SELECT	nFolioRelacionGastos FROM	tRelacionGastosCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioRelacionGastosCompromiso = ? )";
        try {
            pst = con.prepareStatement(query);
            pst.setInt(1, folioIntegrada);
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
        String query = "SELECT SUM(mimporteISRResico + mImporteISRLaudos + mISRHonorarios  + mISROtros+mISRArrenda +mimporteIvaArrenda + mimporteivahonorarios + mimporteflete23 + mImporteFlete4 + mObra5) totalRetenciones " + " FROM tRELACIONGASTOSDetalle (NOLOCK) WHERE nFolioRELACIONGASTOS IN (" + listaIds + ")";
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

    public static void validarTotalRetencionesIntegrada(Connection con, BigDecimal total, int folioIntegrado) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mimporteISRResico + mImporteISRLaudos + mISRHonorarios  + mISROtros+mISRArrenda +mimporteIvaArrenda + mimporteivahonorarios + mimporteflete23 + mImporteFlete4 + mObra5) totalRetenciones " + " FROM tRELACIONGASTOSDetalle (NOLOCK) WHERE nFolioRELACIONGASTOS in (SELECT	nFolioRelacionGastos FROM	tRelacionGastosCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioRelacionGastosCompromiso = ? )";
        try {
            pst = con.prepareStatement(query);
            pst.setInt(1, folioIntegrado);
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

    public static ArrayList<String> buscaRelacionGastosIntegrados(Connection conn, String listaIds, String listaCuentaBancaria, String listaLeyendas, Usuario usuario, String sTimeStamp, String folioGenerator) throws Exception {
        // String sUsuario = usuario.getLogin();
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null, pstmntH2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rsCXP = null;
        PreparedStatement psCXP = null;
        BigDecimal total = new BigDecimal(0);
        BigDecimal retenciones = new BigDecimal(0);
        BigDecimal revisarTotal = new BigDecimal(0);
        BigDecimal revisarRete = new BigDecimal(0);
        try {
            String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
            String[] arrLeyendas = listaLeyendas.split(",");
            String cxp = "";
            cxp = ValidaNoTengaLayuout(conn, listaIds);
            if (!cxp.equals("")) {
                throw new Exception("Existen solicitudes que ya habian sido integradas :" + cxp);
            }
            // int intIndice = -1;
            String foliosCXP = "";
            String separadorCXP = "";
            psCXP = conn.prepareStatement("SELECT caNoContrarrecibo FROM dbo.tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS IN (" + listaIds + ")");
            rsCXP = psCXP.executeQuery();
            while (rsCXP.next()) {
                foliosCXP += separadorCXP;
                foliosCXP += rsCXP.getString("caNoContrarrecibo");
                separadorCXP = " ";
            }
            // Obtiene Ejercicio Fiscal
            // String ejercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(
            // conn );
            String sREFERENCIA1_107 = sTimeStamp;
            // Genera Encabezado
            StringBuilder Sql = new StringBuilder();
            Sql.append(" SELECT TOP 1 'RELACIONGASTOS', 'H' AS Header, CONVERT(nvarchar(10), GETDATE(),103), CONVERT(nvarchar(10), GETDATE(),103), ");
            Sql.append(" tCE.cRamo,tCE.cRamo,tCE.cRamo, 'RHQ' UnidadResponsable,'RHQ' UnidadResponsable,'RHQ' UnidadResponsable, 'N' ID_TIPO_MOVIMIENTO, '1' AS OrigenPpto,");
            Sql.append("'3' AS TipoSol, 'MXN' TipoMoneda , '1' TipoCambio, '1' TIPO_PAGO, " + arrLeyendas[0].trim() + " AS CveLeyenda, 'S04929' CBEN, '");
            Sql.append(arrCuentasBancarias[0].trim() + "' CUENTA_BANCARIA, '16RHQ', 'FAC', '' FechaReferencia, '' Referencia1, '' Referencia2, ");
            Sql.append(" LEFT('Integracion " + sREFERENCIA1_107 + " de RG-" + foliosCXP + "',500), '' NotasReverso, '' AMF, '");
            Sql.append(sREFERENCIA1_107 + "' NO_ACMI, '" + sREFERENCIA1_107 + "' AuxiliarComodin, ");
            Sql.append("'' CTR , ");
            Sql.append(" CONVERT(decimal(17, 2), sum(DCD_ISR),0)  AS ISR_303, ");
            Sql.append(" CONVERT(decimal(17, 2),sum(DCD_IVADES), 0) IVA, ");
            Sql.append(" CONVERT(decimal(17, 2), sum(DCD_MIL5),0) mil5, ");
            Sql.append(" CONVERT(decimal(17, 2),sum(DCD_MIL2),0) mil2, ");
            Sql.append(" CONVERT(varchar(20), sum(ISNULL(DCD_OTRAS_RET,0)))  OTRASRET, ");
            Sql.append("'0' PENAS, ");
            Sql.append("'0' CONTRIB, ");
            Sql.append(" CONVERT(decimal(17, 2), sum(DCD_IVA),0) AS IVADES_310,	");
            Sql.append("'' IVAANT, ");
            Sql.append("'' FolioDC, ");
            Sql.append("'NA' ID_DESTINO_GASTO   ");
            Sql.append(" FROM tRELACIONGASTOSEncabezado tCE (NOLOCK)");
            Sql.append("	inner join v_DCD_RELGASTO dcd on dcd.NFOLIORELACIONGASTOS = tce.NFOLIORELACIONGASTOS");
            Sql.append(" WHERE tCE.nFolioRELACIONGASTOS in (" + listaIds + ") ");
            Sql.append(" GROUP BY  cRamo");
            pstmntH2 = conn.prepareStatement(Sql.toString());
            log.debug(Sql);
            rs = pstmntH2.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                String tok = "";
                StringBuffer encabezado = new StringBuffer();
                for (int i = 2; i <= rsmd.getColumnCount(); i++) {
                    encabezado.append(tok).append(rs.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                    tok = ",";
                }
                encabezado.append("\r\n");
                arrListaComp.add(encabezado.toString());
                // Se guarda el detalle de RG
                rs2 = generaLayoutDetalleRG(conn, listaIds);
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 40; i++) {
                        if (i == 27) {
                            revisarTotal = rs2.getBigDecimal(i);
                            total = total.add(revisarTotal);
                            if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                                throw new Exception("El importe de uno de los registros del layout es menor que cero. Revise los pagos.");
                            } else {
                                detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                token = ",";
                            }
                        } else if (i >= 32 && i <= 36) {
                            // Suma el importe de las retenciones
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
            }
            // Valida que el total del Layout sea igual a los pagos
            validarTotalLayout(conn, total, listaIds);
            // Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetenciones(conn, retenciones, listaIds);
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(rsCXP, false);
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(psCXP, false);
            CloseObject.closeObject(pstmntH2, false);
        }
    }

    private static String generaFolioOficio(Connection conn, int folioComp, String anio) throws Exception {
        PreparedStatement pst2 = null;
        String folio = "";
        ResultSet rs2 = null;
        try {
            pst2 = conn.prepareStatement("SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WHERE GP_NOMBRE = 'NOMENCLATURA_OFICIOS_COMP'");
            rs2 = pst2.executeQuery();
            if (rs2.next()) {
                folio = rs2.getString(1);
            }
            StringBuilder oficio = new StringBuilder();
            String folioCompPadded = String.format("%04d", folioComp);
            oficio.append(folio).append("-").append(folioCompPadded).append("-").append(anio);
            log.debug("Folio Oficio " + oficio.toString());
            return oficio.toString();
        } finally {
            CloseObject.closeObject(pst2);
            CloseObject.closeObject(rs2);
        }
    }

    public static String ValidaNoTengaLayuout(Connection conn, String listaIds) throws Exception {
        PreparedStatement pst = null;
        String cxp = "";
        ResultSet rs = null;
        String query = "SELECT sNoContrarrecibo FROM tLayoutsCreadosRelacionGastosHeader (NOLOCK) WHERE nFolio in (  " + listaIds + " ) and (cEstatus is null or  cEstatus = 'Activo') ";
        try {
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            StringBuffer detalle = new StringBuffer();
            String token = new String();
            while (rs.next()) {
                detalle.append(token).append(rs.getString(1));
                token = ",";
            }
            cxp = detalle.toString();
            log.debug("Folios repetidos " + cxp);
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return cxp;
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds, String sTimeStamp, boolean bIntegra) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String sREFERENCIA1_107 = "'" + sTimeStamp + "'";
        try {
            if (bIntegra)
                sREFERENCIA1_107 = "DCD.caNoContrarrecibo";
            // URVP -- ENCABEZADO DEL DOCCOMP
            StringBuilder sql = new StringBuilder();
            sql.append(" select distinct PDE.nFolioRELACIONGASTOS, 'H' H, PDE.cRamo, 'RHQ', '' SOL_PAGO, '3', " + sREFERENCIA1_107 + " FOLIO_INTERNO, " + sREFERENCIA1_107 + " COMODIN ");
            sql.append(" from  dbo.tRELACIONGASTOSEncabezado PDE (NOLOCK) ");
            sql.append(" inner join v_DCD_RELGASTO DCD  on PDE.NFOLIORELACIONGASTOS = DCD.NFOLIORELACIONGASTOS  ");
            sql.append(" WHERE PDE.nFolioRELACIONGASTOS in (" + listaIds + ") ");
            pstmntH = conn.prepareStatement(sql.toString());
            System.out.println(sql.toString());
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
                // URVP DETALLE DEL DOCCOMP
                StringBuilder Sql2 = new StringBuilder();
                Sql2.append(" select distinct PDE.cRamo, CASE WHEN ISNULL(DCD.DCD_FACTURA,'')<>'' THEN REPLACE(REPLACE(DCD.DCD_FACTURA,',',''),'\"','') ELSE REPLACE(REPLACE(PDE.cIdRelacion,',',''),'\"','') END DCD_FACTURA");
                Sql2.append(", CONVERT(nvarchar(10), DCD.fAplicacion,103), CONVERT(nvarchar(10), DCD.fRecepcion,103) + ' 12:00:00 a.m.', " + sCampo + ", case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen'");
                Sql2.append(", DCD.DCD_TIPO_OPE, DCD.DCD_TIVA 'TIVA', CONVERT(decimal(17, 2), ISNULL(DCD.DCD_VALOR,0)), CONVERT(decimal(17, 2), DCD_IMP_BRUTO) MONTO, ");
                Sql2.append(" CONVERT(decimal(17, 2), DCD.DCD_IVA), CONVERT(decimal(17, 2), DCD.DCD_IVADES), ");
                Sql2.append("  CONVERT(decimal(17, 2), DCD.DCD_ISR) , CONVERT(decimal(17, 2), DCD.DCD_MIL5), CONVERT(decimal(17, 2), DCD.DCD_MIL2)");
                Sql2.append(", CONVERT(decimal(17, 2), DCD.DCD_OTRAS_RET), CONVERT(decimal(17, 2), DCD.DCD_PENALIZACION), " + " CONVERT(decimal(17, 2), DCD.DCD_CONTRIBUCION), DCD.DCD_CTOEXT, ");
                Sql2.append(" CASE WHEN ISNULL(DCD.DCD_FACTURA,'')<>'' THEN REPLACE(REPLACE(DCD.DCD_FACTURA,',',''),'\"','') ELSE REPLACE(REPLACE(PDE.cIdRelacion,',',''),'\"','') END DCD_FACTURA, REPLACE(REPLACE(DCD.cConcepto,',',''),'\"',''), PDE.caNoContrarrecibo ");
                Sql2.append(" from dbo.tRELACIONGASTOSEncabezado PDE (NOLOCK) ");
                Sql2.append(" INNER JOIN dbo.tRELACIONGASTOSDetalle RGD (NOLOCK) ON PDE.nFolioRELACIONGASTOS = RGD.nFolioRELACIONGASTOS ");
                Sql2.append(" inner join dbo.v_pagosDocComprobatoria DCD (NOLOCK)  on PDE.caNoContrarrecibo = DCD.caNoContrarrecibo  AND DCD.cTipoPago = 'RELACIONGASTOS' ");
                Sql2.append(" inner join [dbo].[tBeneficiario] B (NOLOCK)  on PDE.cIdRFC = B.dRFC ");
                Sql2.append(" left join [dbo].[CAT_TIPO_IVA] TI (NOLOCK) on TI.TIVA = DCD.DCD_TIVA  ");
                Sql2.append(" FULL OUTER JOIN tComprobacionLaudos Comp WITH (NOLOCK) ");
                Sql2.append(" ON PDE.nFolioRELACIONGASTOS = Comp.nFolioRELACIONGASTOS ");
                Sql2.append(" where PDE.nFolioRELACIONGASTOS in (" + listaIds + ")");
                pstmntD = conn.prepareStatement(Sql2.toString());
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
                if (!bIntegra)
                    break;
            }
        } finally {
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmntD, false);
            CloseObject.closeObject(pstmntH, false);
        }
        return arrListaComp;
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tRELACIONGASTOSEncabezado SET nEnviadoSICOP = 1 WHERE [nFolioRELACIONGASTOS] in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
            conn.commit();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static int UpdateStatus(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tRELACIONGASTOSEncabezado SET nEnviadoSICOP = 0 WHERE [nFolioRELACIONGASTOS] in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
            conn.commit();
        } finally {
            CloseObject.closeObject(pstmnt);
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
            CloseObject.closeObject(pstmntL);
        }
    }

    public static boolean insertRegisterLayout(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean insertReg;
        String queryInsert = "INSERT INTO tLayoutCompromisos(cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso,cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP,nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud,cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC," + "                  caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento,nDocumento,cDescripcion)" + "            		 VALUES(" + "" + "'" + clave + "','" + cRamo + "','" + cUnidadResponsable + "','" + folioSICOP + "','" + idProceso + "'," + "" + "'" + cCentroContable + "','" + fExpedicion + "'," + total + ",'" + cTipoPoliza + "','" + nFolioPoliza + "'," + "" + "'" + nPolizaCancelacion + "','" + tipoMovimiento + "','" + origenPresupuesto + "','" + cuentaBancaria + "','" + noSolicitud + "'," + "" + "'" + tCambio + "','" + tMoneda + "','" + tSolicitud + "','" + volante + "','" + rfc + "'," + "" + "'" + caNoCompromiso + "','" + codSemarnat2 + "','" + estatus + "','" + fAplicacion + "','" + documento + "'," + "" + "'" + nDocumento + "','" + descripcion + "')";
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

    public static int esRelacionComprobacion(Connection conn, int nFolio) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int nFolioCaja = -1;
        log.info("Buscando el folio de relacion de gastos para regresar el folio de caja");
        try {
            String queryFolCaja = "SELECT  CASE WHEN ID_DESTINO_GASTO IN ('NORE','CQRE') THEN 0 ELSE nFolioCaja END AS nFolioCaja  FROM dbo.tRELACIONGASTOSEncabezado (NOLOCK)  WHERE nFolioRELACIONGASTOS = ?";
            pstmnt = conn.prepareStatement(queryFolCaja);
            pstmnt.setInt(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next())
                nFolioCaja = rs.getInt("nFolioCaja");
            if (nFolioCaja == 0)
                nFolioCaja = -1;
            return nFolioCaja;
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static boolean tieneInformeComision(Connection conn, int nFolio) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        boolean resultado = true;
        String informe = "";
        try {
            String queryFolCaja = "SELECT cInformeComision FROM tRELACIONGASTOSEncabezado (NOLOCK) WHERE nFolioRELACIONGASTOS = ?";
            pstmnt = conn.prepareStatement(queryFolCaja);
            pstmnt.setInt(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next())
                informe = rs.getString("cInformeComision");
            if (StringUtils.isBlank(informe))
                resultado = false;
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
        return resultado;
    }

    public static boolean esViaticos(Connection conn, int nFolio) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        boolean resultado = true;
        int id = 0;
        try {
            String query = "select COUNT(*) id from tRELACIONGASTOSDetalle (NOLOCK) where substring(OBGT,1,2) = '37' and nFolioRELACIONGASTOS = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next())
                id = rs.getInt("id");
            if (id == 0) {
                resultado = false;
            }
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
        return resultado;
    }

    public static double getMontoRelacionGastos(Connection conn, int nFolio) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        double montosRelacion = 0;
        log.info("Buscando el monto de la relacion de gastos");
        try {
            String queryMontoRelacion = "SELECT mImporteNeto FROM dbo.tRELACIONGASTOSEncabezado (NOLOCK)  WHERE nFolioRELACIONGASTOS = ?";
            pstmnt = conn.prepareStatement(queryMontoRelacion);
            pstmnt.setInt(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next())
                montosRelacion = rs.getDouble("mImporteNeto");
            return montosRelacion;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmnt, false);
        }
    }

    public static int insertaConsolidacinRelacionGastos(Connection conn, String sTimeStamp, Usuario sUsuario, String ejercicioFiscal, String folioGenerator) throws Exception {
        log.info("Insertando consolidacion para la relacion de gastos. Folio Integracion[" + sTimeStamp + "] Usuario[" + sUsuario + "] Ejercicio Fiscal[" + ejercicioFiscal + "]");
        int insertados = 0;
        StringBuilder sqlInsertConsolidacion = new StringBuilder();
        sqlInsertConsolidacion.append("INSERT INTO tconsolidacionrelaciongastosencabezado ");
        sqlInsertConsolidacion.append(" (nFolioConsolidacion, nidintegracion, fcarga, faplicacion, ctipopoliza, u_login, cunidadresponsablecontable, cdescripcionpoliza, cramo,  cunidadresponsable,  aejerciciofiscal) ");
        sqlInsertConsolidacion.append("SELECT ?	AS nFolioConsolidacion,");
        sqlInsertConsolidacion.append("       ? AS nIdIntegracion, " + " Getdate() AS fCarga, " + " Getdate() AS fAplicacion, ");
        sqlInsertConsolidacion.append("    'IN' AS cTipoPoliza, ");
        sqlInsertConsolidacion.append("       ? AS U_LOGIN, 'RHQ' AS cUnidadResponsableContable, ");
        sqlInsertConsolidacion.append(" 'Poliza de Ingreso Devengado y Recaudado de la Integración " + sTimeStamp + "' AS cDescripcionPoliza, ");
        sqlInsertConsolidacion.append("  '16' AS cRamo, ");
        sqlInsertConsolidacion.append("       ? AS cUnidadResponsable, ");
        sqlInsertConsolidacion.append("       ? AS aEjercicioFiscal ");
        StringBuilder sqlInsertConsolidacionDetalle = new StringBuilder();
        sqlInsertConsolidacionDetalle.append("INSERT INTO dbo.tconsolidacionrelaciongastosdetalle  ( nDocRenglon , nFolioConsolidacion , ep , cevento , ccentrocontable , cmes , ID_destino_gasto ,ID_TIPO_CONCEPTO , partida , tipogasto ,mimportemasiva ,mImporteNegativo , nidintegracion ,CTAB ,RFC ,ALM ,OBGT )");
        sqlInsertConsolidacionDetalle.append(" SELECT Row_number() OVER (   ORDER BY nfolioconsolidacion) AS nDocRenglon,   consolidacion_encabezado.nfolioconsolidacion AS nFolioConsolidacion,   ep,   dbo.Fn_define_evento_integracion_rg(ENCABEZADO.id_destino_gasto,   detalle.id_tipo_concepto, Substring(detalle.ep, 32, 5),   Substring(detalle.ep, 38, 1)) AS cevento,   DETALLE.ccentrocontable,   DETALLE.cmes,   ENCABEZADO.id_destino_gasto,   detalle.id_tipo_concepto,   Substring(detalle.ep, 32, 5)                 AS partida,   Substring(detalle.ep, 38, 1)                 AS tipogasto, ");
        sqlInsertConsolidacionDetalle.append("  Sum(  CASE comprobacionLaudos.cRetSICOP ");
        sqlInsertConsolidacionDetalle.append(" 	    WHEN  'S'  THEN  detalle.mImporteNeto");
        sqlInsertConsolidacionDetalle.append(" 		WHEN  'N' then DETALLE.mimportemasiva ");
        sqlInsertConsolidacionDetalle.append("  ELSE detalle.mImporteNeto   END ) AS mimportemasiva,");
        sqlInsertConsolidacionDetalle.append("     -1 * Sum(  CASE comprobacionLaudos.cRetSICOP");
        sqlInsertConsolidacionDetalle.append("  WHEN  'S'  THEN  detalle.mImporteNeto ");
        sqlInsertConsolidacionDetalle.append("  WHEN  'N' then DETALLE.mimportemasiva ");
        sqlInsertConsolidacionDetalle.append("  ELSE detalle.mImporteNeto   END ) AS  mImporteNegativo,");
        sqlInsertConsolidacionDetalle.append(" LAYOUT.sauxiliarcomodin AS nidintegracion,  LAYOUT.scuenta_bancaria AS CTAB, CASE  WHEN ENCABEZADO.rfc <> 'TESOFE' THEN '' ELSE ENCABEZADO.rfc   END AS RFC,   detalle.alm,   Substring(detalle.ep, 32, 5)  AS OBGT ");
        sqlInsertConsolidacionDetalle.append(" FROM   dbo.trelaciongastosencabezado ENCABEZADO WITH (nolock)   INNER JOIN dbo.trelaciongastosdetalle DETALLE WITH (nolock)       ON ENCABEZADO.nfoliorelaciongastos = DETALLE.nfoliorelaciongastos   ");
        sqlInsertConsolidacionDetalle.append(" LEFT OUTER JOIN dbo.tlayoutscreadosrelaciongastosheader LAYOUT WITH (               nolock)            ON ENCABEZADO.canocontrarrecibo = LAYOUT.snocontrarrecibo ");
        sqlInsertConsolidacionDetalle.append(" LEFT OUTER JOIN dbo.tComprobacionLaudos comprobacionLaudos WITH(NOLOCK)            ON ENCABEZADO.nFolioRELACIONGASTOS = comprobacionLaudos.nFolioRELACIONGASTOS   ");
        sqlInsertConsolidacionDetalle.append(" INNER JOIN dbo.tconsolidacionrelaciongastosencabezado          consolidacion_encabezado       ON LAYOUT.sauxiliarcomodin =  consolidacion_encabezado.nidintegracion ");
        sqlInsertConsolidacionDetalle.append(" WHERE  LAYOUT.sauxiliarcomodin IS NOT NULL   AND nfolioconsolidacion = ? ");
        sqlInsertConsolidacionDetalle.append(" GROUP  BY ep, consolidacion_encabezado.nfolioconsolidacion, DETALLE.ccentrocontable, DETALLE.cmes, ENCABEZADO.id_destino_gasto,  detalle.id_tipo_concepto,  LAYOUT.sauxiliarcomodin,  scuenta_bancaria, CASE  WHEN ENCABEZADO.RFC <> 'TESOFE' THEN '' ELSE ENCABEZADO.RFC  END ,  detalle.alm,  Substring(detalle.ep, 32, 5)  ");
        PreparedStatement psInsertaEncabezado = null;
        PreparedStatement psInsertaDetalle = null;
        ResultSet rsFolioConsolidacion = null;
        int nFolioConsolidacion = -1;
        try {
            Caso c = RelacionGastosManager.generaCaso(conn, sUsuario, folioGenerator);
            nFolioConsolidacion = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            log.debug("Query Insert Encabezado[" + sqlInsertConsolidacion.toString() + "]");
            log.debug("Query Insert Detalle[" + sqlInsertConsolidacionDetalle.toString() + "]");
            psInsertaEncabezado = conn.prepareStatement(sqlInsertConsolidacion.toString(), Statement.RETURN_GENERATED_KEYS);
            psInsertaDetalle = conn.prepareStatement(sqlInsertConsolidacionDetalle.toString());
            psInsertaEncabezado.setInt(1, nFolioConsolidacion);
            psInsertaEncabezado.setString(2, sTimeStamp);
            psInsertaEncabezado.setString(3, sUsuario.getLogin());
            psInsertaEncabezado.setString(4, sUsuario.getU_UR());
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
        ClassLoader cl = RelacionGastosManager.class.getClassLoader();
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
        // CasoBusinessLogic casoTx;
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
            String queryEf = "select aEjercicioFiscal from tEjercicioFiscal where cActivo = 1 ";
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

    public static int UpdateStatusPagoDiversoRelGastos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGODIVERSOEncabezado SET nEnviadoSICOP = 0 WHERE nFolioPAGODIVERSO IN (SELECT nFolio FROM tLayoutsCreadosPagosDiversosRelGastosHeader WHERE sAuxiliarComodin = '" + listaIds + "')");
            retval = pstmnt.executeUpdate();
            conn.commit();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static List<Integer> obtenFoliosCargaMasiva(Connection conn, int folioCargaMasiva) throws Exception {
        String query = "SELECT nFolioRelacionGastos FROM tRelacionGastosEncabezado WITH(nolock) WHERE nFolioCargaMasiva = " + folioCargaMasiva;
        Statement stmnt = null;
        ResultSet rs = null;
        List<Integer> r = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            while (rs.next()) {
                if (r == null)
                    r = new ArrayList<Integer>();
                r.add(rs.getInt(1));
            }
            return r;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }

    public static int obtenFolioContrarrecibo(Connection conn, String canocontrarecibo) throws Exception {
        String query = "SELECT nFolioRelacionGastos FROM tRelacionGastosEncabezado WITH(nolock) WHERE caNoContrarrecibo = ?";
        PreparedStatement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.prepareStatement(query);
            stmnt.setString(1, canocontrarecibo);
            rs = stmnt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new RuntimeException("No se encontro RG con CxP " + canocontrarecibo);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }

    public static int actualizaFirmantesCargaMasiva(Connection conn, List<Integer> foliosCargados, String[] firmanteElabora, String[] firmanteVoBo, String[] firmanteAutoriza, String[] firmanteDelegatorio) throws Exception {
        PreparedStatement psActualizaFirmantes = null;
        PreparedStatement psEliminaFirmanteDelegatorio = null;
        PreparedStatement psInsertaFirmanteDelegatorio = null;
        String queryActualizaFirmantes = "UPDATE tRELACIONGASTOSEncabezado SET sFirmanteVoBo = ?, sPuestoVoBo = ?, sFirmanteAut = ?, sPuestoAut = ?, sFirmanteEla = ?, sPuestoEla = ?" + " WHERE nFolioRELACIONGASTOS = ?";
        String queryEliminaFirmanteDelegatorio = "DELETE FROM dbo.tPagoFirmanteDelagatorio WHERE cTipoPago = 'RELACIONGASTOS' AND nFolioPago = ?";
        String queryInsertaFirmanteDelegatorio = "INSERT INTO dbo.tPagoFirmanteDelagatorio( cTipoPago ,nFolioPago ,cFolioOficio ,dFechaOficio ,cNombreTitular ,cApellidoPaternoTitular ,cApellidoMaternoTitular ,cPuestoTitular, nTipoSuplencia) " + "VALUES  ( 'RELACIONGASTOS', ?, ?, ?, ?, ?, ?, ?, ?  )";
        int afectados = 0;
        try {
            psActualizaFirmantes = conn.prepareStatement(queryActualizaFirmantes);
            if (firmanteDelegatorio != null && firmanteDelegatorio.length > 0) {
                psEliminaFirmanteDelegatorio = conn.prepareStatement(queryEliminaFirmanteDelegatorio);
                psInsertaFirmanteDelegatorio = conn.prepareStatement(queryInsertaFirmanteDelegatorio);
            }
            for (Iterator<Integer> i = foliosCargados.iterator(); i.hasNext(); ) {
                int nFolioRelacionGastos = i.next();
                psActualizaFirmantes.setString(1, firmanteVoBo[0]);
                psActualizaFirmantes.setString(2, firmanteVoBo[1]);
                psActualizaFirmantes.setString(3, firmanteAutoriza[0]);
                psActualizaFirmantes.setString(4, firmanteAutoriza[1]);
                psActualizaFirmantes.setString(5, firmanteElabora[0]);
                psActualizaFirmantes.setString(6, firmanteElabora[1]);
                psActualizaFirmantes.setInt(7, nFolioRelacionGastos);
                afectados += psActualizaFirmantes.executeUpdate();
                if (firmanteDelegatorio != null && firmanteDelegatorio.length > 0) {
                    psEliminaFirmanteDelegatorio.setInt(1, nFolioRelacionGastos);
                    psEliminaFirmanteDelegatorio.executeUpdate();
                    psInsertaFirmanteDelegatorio.setInt(1, nFolioRelacionGastos);
                    psInsertaFirmanteDelegatorio.setString(2, firmanteDelegatorio[0]);
                    psInsertaFirmanteDelegatorio.setDate(3, new java.sql.Date(Util.stringToDate(firmanteDelegatorio[1], "dd/MM/yyyy").getTime()));
                    psInsertaFirmanteDelegatorio.setString(4, firmanteDelegatorio[2]);
                    psInsertaFirmanteDelegatorio.setString(5, firmanteDelegatorio[3]);
                    psInsertaFirmanteDelegatorio.setString(6, firmanteDelegatorio[4]);
                    psInsertaFirmanteDelegatorio.setString(7, firmanteDelegatorio[5]);
                    psInsertaFirmanteDelegatorio.setString(8, firmanteDelegatorio[6]);
                    psInsertaFirmanteDelegatorio.executeUpdate();
                }
            }
            return afectados;
        } finally {
            CloseObject.closeObject(psActualizaFirmantes);
            CloseObject.closeObject(psEliminaFirmanteDelegatorio);
            CloseObject.closeObject(psInsertaFirmanteDelegatorio);
        }
    }

    public static void exportaSolicitudesMasiva(Connection conn, String reportPath, String fileName, int nFolioCargaMasiva, OutputStream out) throws Exception {
        String querySel = "SELECT RTRIM(LTRIM( caNoContrarrecibo ) ) AS caNoContrarrecibo  FROM tRELACIONGASTOSEncabezado WITH(NOLOCK) WHERE nFolioCargaMasiva = " + nFolioCargaMasiva;
        ResultSet rs = null;
        Statement stmnt = null;
        boolean first = true;
        ZipOutputStream zos = null;
        InputStream in = null;
        try {
            Map<String, Object> parametrosReporte = new HashMap<String, Object>();
            parametrosReporte.put("SUBREPORT_DIR", reportPath);
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(querySel);
            while (rs.next()) {
                in = new FileInputStream(reportPath + "\\" + "PolizaPago.jasper");
                if (first) {
                    zos = new ZipOutputStream(out);
                    first = false;
                }
                String canoContrarecibo = rs.getString("caNoContrarrecibo");
                String pdfName = canoContrarecibo + ".pdf";
                ZipEntry ze = new ZipEntry(pdfName);
                zos.putNextEntry(ze);
                parametrosReporte.put("folio", canoContrarecibo);
                parametrosReporte.put("whereFolio", " and CR.caNoContrarrecibo = '" + canoContrarecibo + "'");
                JasperRunManager.runReportToPdfStream(in, zos, parametrosReporte, conn);
                log.info(canoContrarecibo);
                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e) {
                        log.warn("Problemas cerrando reporte. " + e);
                    }
            }
            if (zos != null) {
                zos.flush();
                zos.closeEntry();
                zos.close();
                out.flush();
                out.close();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }

    public static synchronized boolean esRelacionPorOficio(Connection conn, int nFolioTramite) throws Exception {
        boolean relacionPorOficio = false;
        log.trace("Verificando si la RG " + nFolioTramite + " es por oficio");
        String query = "SELECT	COUNT( * ) AS totalOficios FROM	tPagoFactura WITH(NOLOCK)  WHERE	cTipoPago = 'RELACIONGASTOS'  AND	nFolioPago = ?  AND	SUBSTRING( cRFCFactura, 1,6 ) = 'OFICIO'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioTramite);
            log.trace("Ejecutando Query: " + query);
            log.trace("Parametros: " + nFolioTramite);
            rs = ps.executeQuery();
            if (rs.next())
                relacionPorOficio = rs.getInt(1) > 0;
            return relacionPorOficio;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String generaFolioIntegracion(Connection conn, String cuentasBancariasCSV) throws Exception {
        PreparedStatement pstmntH = null;
        ResultSet rs3 = null;
        String Sql3 = " select strUnidadEjecutora from [tUECuentasBancarias] where strclabe = ?";
        String ueCtaBancaria = "";
        try {
            pstmntH = conn.prepareStatement(Sql3);
            String[] arrCuentasBancarias = cuentasBancariasCSV.split(",");
            String sREFERENCIA1_107 = new SimpleDateFormat("yyMMddhhmmss").format(new java.util.Date());
            pstmntH.setString(1, arrCuentasBancarias[0]);
            rs3 = pstmntH.executeQuery();
            if (rs3.next()) {
                ueCtaBancaria = rs3.getString(1);
            } else
                throw new Exception("No se encontro UE para la cuenta bancaria " + arrCuentasBancarias[0]);
            sREFERENCIA1_107 = ueCtaBancaria + sREFERENCIA1_107;
            return sREFERENCIA1_107;
        } finally {
            CloseObject.closeObject(rs3);
            CloseObject.closeObject(pstmntH);
        }
    }

    public static File generaLayoutCompromisoRG(Connection conn, Usuario usuario, String caNoCompromiso, String folioGenerator) throws Exception {
        String nombreArchivo = "PDRELGASTOS" + "_" + caNoCompromiso + ".csv";
        File archivoLayout = new File(new File(System.getProperty("java.io.tmpdir")), nombreArchivo);
        PrintWriter out = null;
        StringBuilder queryInformacionCompromiso = new StringBuilder();
        queryInformacionCompromiso.append("SELECT  RGCOmpromisoEnc.nFolioRelacionGastosCompromiso , RGCOmpromisoEnc.canocontrarrecibo , compromisoEnc.nFolioAutSICOP ,  RGCOmpromisoEnc.cCuentaBancaria , ");
        queryInformacionCompromiso.append("         RGCOmpromisoEnc.dfechaIntegracion ,  RGCOmpromisoEnc.nLeyenda,   compromisoEnc.cIdContrato, compromisoEnc.nFolioSuficiencia ");
        queryInformacionCompromiso.append("   FROM	tRelacionGastosCompromisoEncabezado RGCOmpromisoEnc WITH(NOLOCK) ");
        queryInformacionCompromiso.append(" 		INNER JOIN  tCompromisoEncabezado compromisoEnc WITH(NOLOCK) ");
        queryInformacionCompromiso.append(" 		ON RGCOmpromisoEnc.nFolioCompromiso = compromisoEnc.nFolioCompromiso ");
        queryInformacionCompromiso.append("  WHERE	canocontrarrecibo = ?");
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;
        PreparedStatement psInformacionCompromiso = null;
        ResultSet rsInformacionCompromiso = null;
        int folioRelacionGastosCompromiso = -1;
        String folioAutSICOP = null;
        String folioSuficiencia = null;
        int leyenda = -1;
        String IDContratoIntegracion = null;
        String cuentaBancaria = null;
        try {
            psInformacionCompromiso = conn.prepareStatement(queryInformacionCompromiso.toString());
            psInformacionCompromiso.setString(1, caNoCompromiso);
            rsInformacionCompromiso = psInformacionCompromiso.executeQuery();
            if (rsInformacionCompromiso.next()) {
                folioRelacionGastosCompromiso = rsInformacionCompromiso.getInt("nFolioRelacionGastosCompromiso");
                folioAutSICOP = rsInformacionCompromiso.getString("nFolioAutSICOP");
                folioSuficiencia = rsInformacionCompromiso.getString("nFolioSuficiencia");
                IDContratoIntegracion = rsInformacionCompromiso.getString("cIdContrato").trim();
                cuentaBancaria = rsInformacionCompromiso.getString("cCuentaBancaria");
                leyenda = rsInformacionCompromiso.getInt("nLeyenda");
            } else
                throw new Exception("No fue posible generar layout debido a que no se encontro informacion para el compromiso: " + caNoCompromiso);
            boolean layoutPrevioCreado = existeLayout(conn, IDContratoIntegracion);
            if (layoutPrevioCreado) {
                updateLayoutRGHeaderStatus(conn, IDContratoIntegracion);
            }
            String foliosRGIntegrados = getFoliosIntegradosCompromiso(conn, folioRelacionGastosCompromiso);
            String encabezadoLayout = generaEncabezadoLayoutCompRG(conn, folioRelacionGastosCompromiso, cuentaBancaria, IDContratoIntegracion, foliosRGIntegrados, leyenda);
            String detalleLayout = generaDetalleLayoutCompRG(conn, folioRelacionGastosCompromiso, folioAutSICOP, folioSuficiencia);
            out = new PrintWriter(archivoLayout);
            out.println(encabezadoLayout);
            out.print(detalleLayout);
            out.flush();
            out.close();
            out = null;
            return archivoLayout;
        } finally {
            CloseObject.closeObject(rsInformacionCompromiso);
            CloseObject.closeObject(psInformacionCompromiso);
            CloseObject.closeObject(psHeader);
            CloseObject.closeObject(psDetail);
        }
    }

    public static void insertaTablasCompromisoRG(Connection conn, Usuario usuario, String caNoCompromiso, String folioGenerator) throws Exception {
        StringBuilder queryInformacionCompromiso = new StringBuilder();
        queryInformacionCompromiso.append("SELECT  RGCOmpromisoEnc.nFolioRelacionGastosCompromiso , RGCOmpromisoEnc.canocontrarrecibo , compromisoEnc.nFolioAutSICOP ,  RGCOmpromisoEnc.cCuentaBancaria , ");
        queryInformacionCompromiso.append("         RGCOmpromisoEnc.dfechaIntegracion ,  RGCOmpromisoEnc.nLeyenda,   compromisoEnc.cIdContrato ");
        queryInformacionCompromiso.append("   FROM	tRelacionGastosCompromisoEncabezado RGCOmpromisoEnc WITH(NOLOCK) ");
        queryInformacionCompromiso.append(" 		INNER JOIN  tCompromisoEncabezado compromisoEnc WITH(NOLOCK) ");
        queryInformacionCompromiso.append(" 		ON RGCOmpromisoEnc.nFolioCompromiso = compromisoEnc.nFolioCompromiso ");
        queryInformacionCompromiso.append("  WHERE	canocontrarrecibo = ?");
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;
        PreparedStatement psInformacionCompromiso = null;
        ResultSet rsInformacionCompromiso = null;
        int folioRelacionGastosCompromiso = -1;
        String folioAutSICOP = null;
        int leyenda = -1;
        String IDContratoIntegracion = null;
        String cuentaBancaria = null;
        try {
            psInformacionCompromiso = conn.prepareStatement(queryInformacionCompromiso.toString());
            psInformacionCompromiso.setString(1, caNoCompromiso);
            rsInformacionCompromiso = psInformacionCompromiso.executeQuery();
            if (rsInformacionCompromiso.next()) {
                folioRelacionGastosCompromiso = rsInformacionCompromiso.getInt("nFolioRelacionGastosCompromiso");
                folioAutSICOP = rsInformacionCompromiso.getString("nFolioAutSICOP");
                IDContratoIntegracion = rsInformacionCompromiso.getString("cIdContrato").trim();
                cuentaBancaria = rsInformacionCompromiso.getString("cCuentaBancaria");
                leyenda = rsInformacionCompromiso.getInt("nLeyenda");
            } else
                throw new Exception("No fue posible generar layout debido a que no se encontro informacion para el compromiso: " + caNoCompromiso);
            boolean layoutPrevioCreado = existeLayout(conn, IDContratoIntegracion);
            if (layoutPrevioCreado) {
                updateLayoutRGHeaderStatus(conn, IDContratoIntegracion);
            }
            String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
            String foliosRGIntegrados = getFoliosIntegradosCompromiso(conn, folioRelacionGastosCompromiso);
            if (!layoutPrevioCreado) {
                CFSequenceManager cfSequence = CFSequenceManager.getInstance();
                int idLayout = cfSequence.nextVal("LAYOUT_RELACIONGASTOS");
                int folioComp = cfSequence.nextVal("OFICIOCOMPROMISO");
                String folioOficio = generaFolioOficio(conn, folioComp, ejercicioFiscal);
                insertaLayoutCompRGEncabezado(conn, idLayout, leyenda, cuentaBancaria, IDContratoIntegracion, foliosRGIntegrados, usuario.getLogin(), folioRelacionGastosCompromiso, folioOficio);
                insertaLayoutCompRGDetalle(conn, idLayout, folioAutSICOP, folioRelacionGastosCompromiso);
                insertaConsolidacinRelacionGastos(conn, IDContratoIntegracion, usuario, ejercicioFiscal, folioGenerator);
            }
        } finally {
            CloseObject.closeObject(rsInformacionCompromiso);
            CloseObject.closeObject(psInformacionCompromiso);
            CloseObject.closeObject(psHeader);
            CloseObject.closeObject(psDetail);
        }
    }

    // Laudos
    private static void insertaLayoutCompRGEncabezado(Connection conn, int idLayout, int leyenda, String ctaBancaria, String noIntegracion, String foliosRGIntegrados, String usuario, int folioRelacionGastosCompromiso, String folioOficio) throws Exception {
        StringBuilder queryInsertaLayoutEnc = new StringBuilder();
        // A
        queryInsertaLayoutEnc.append("INSERT INTO  tLayoutsCreadosRelacionGastosHeader(Id, fCreacionLayout, nFolio, Header, fCarga, fAplicacion, sRamo, sRamo1, sRamo2, sUnidadResponsable, sUnidadResponsable2, sUnidadResponsable3, sTipoMovimiento, sOrigenPpto, sTipoSol, sTipoMoneda, sTipoCambio, sTIPO_PAGO, sCveLeyenda, sCBEN, sCUENTA_BANCARIA, sIdRFC, sFAC, fFechaReferencia, sReferencia1, sReferencia2, sConcepto, sNotasReverso, sAMF, sNoContrarrecibo, sAuxiliarComodin, sCTR, sFolioDC, mISR, mIVA, mMil5, mMil2, mOtrasRet, mPenalizacion, mContribucion, mIvaDes, mIvaAnt, sDestinoGasto, sLogin, cEstatus, cfolioOficio)").append("SELECT " + idLayout + ",getdate(), tCE.nFolioRELACIONGASTOS, " + "      	'H' AS Header, ");
        // B
        queryInsertaLayoutEnc.append("     	CONVERT(nvarchar(10), tCE.fAplicacion,103) FECHA_EXP, ");
        // C
        queryInsertaLayoutEnc.append("     	CONVERT(nvarchar(10), tCE.fAplicacion,103) FECHA_APL, ");
        // D
        queryInsertaLayoutEnc.append("     	tCE.cRamo ID_RAMO, ");
        // E
        queryInsertaLayoutEnc.append("     	tCE.cRamo ID_RAMO_CR, ");
        // F
        queryInsertaLayoutEnc.append("     	tCE.cRamo ID_RAMO_REC, ");
        // G
        queryInsertaLayoutEnc.append("     	'RHQ'  ID_UNIDAD, ");
        // H
        queryInsertaLayoutEnc.append("     	'RHQ'  ID_UNIDAD_CR, ");
        // I
        queryInsertaLayoutEnc.append("     	'RHQ'  ID_UNIDAD_REC, ");
        // J
        queryInsertaLayoutEnc.append("     	'N' ID_TIPO_MOVIMIENTO,  ");
        // K
        queryInsertaLayoutEnc.append("     	'1' AS OrigenPpto, ");
        // L
        queryInsertaLayoutEnc.append("     	'3' AS TipoSol, ");
        // M
        queryInsertaLayoutEnc.append("     	'MXN' TipoMoneda, ");
        // N
        queryInsertaLayoutEnc.append("     	'1' TipoCambio, ");
        // O
        queryInsertaLayoutEnc.append("     	'1' TIPO_PAGO, ");
        // P
        queryInsertaLayoutEnc.append("     	'" + leyenda + "' AS CVE_LEYENDA_66,");
        // Q
        queryInsertaLayoutEnc.append("     	'S04929' CBEN, ");
        // R
        queryInsertaLayoutEnc.append("       '" + ctaBancaria.trim() + "' CUENTA_BANCARIA, ");
        // S
        queryInsertaLayoutEnc.append("     	'16RHQ' RFC_227, ");
        // T
        queryInsertaLayoutEnc.append("     	'FAC' TDOC_87, ");
        // U
        queryInsertaLayoutEnc.append("     	 '' FechaReferencia, ");
        // V
        queryInsertaLayoutEnc.append("     	'' Referencia1, ");
        // W
        queryInsertaLayoutEnc.append("     	'' Referencia2, ");
        // X
        queryInsertaLayoutEnc.append("    	LEFT('Integracion " + noIntegracion + " de RG-" + foliosRGIntegrados + "',500) CPAG_76, ");
        // Y
        queryInsertaLayoutEnc.append("      	'' NotasReverso, ");
        // Z
        queryInsertaLayoutEnc.append("     	'' AMF, ");
        // AA
        queryInsertaLayoutEnc.append("      	tCE.canocontrarrecibo NO_ACMI, ");
        // AB
        queryInsertaLayoutEnc.append("      	'" + noIntegracion + "' AuxiliarComodin, ");
        // AC
        queryInsertaLayoutEnc.append("     	'' CTR, ");
        // AD
        queryInsertaLayoutEnc.append("     	'' FolioDC, ");
        // AE
        queryInsertaLayoutEnc.append("     	CONVERT(decimal(17, 2),SUM(DCD_ISR),0)  AS ISR_303, ");
        // AF
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2), SUM(DCD_IVADES), 0) IVA_304, ");
        // AG
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2),0) MIL5_305, ");
        // AH
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2),0) MIL2_306, ");
        // AI
        queryInsertaLayoutEnc.append("      	0 mImporteRetencion, ");
        // AJ
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2),0) PENALIZACION_308, ");
        // AK
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2), 0) CONTRIBUCION_309, ");
        // AL
        queryInsertaLayoutEnc.append("      	CONVERT(DECIMAL(17, 2), SUM(DCD_IVA), 0) AS IVADES_310,	");
        // AM
        queryInsertaLayoutEnc.append("      	null IVAANT_311, ");
        // AN
        queryInsertaLayoutEnc.append("     	'NA' ID_DESTINO_GASTO,   ");
        queryInsertaLayoutEnc.append("      	'" + usuario + "', " + "      	'ACTIVO' AS cEstatus,  '" + folioOficio + "'");
        queryInsertaLayoutEnc.append("  FROM	tRELACIONGASTOSEncabezado tCE (NOLOCK)");
        queryInsertaLayoutEnc.append("     	FULL OUTER JOIN 	tComprobacionLaudos Comp WITH (NOLOCK) ");
        queryInsertaLayoutEnc.append("     	ON tCE.nFolioRELACIONGASTOS = Comp.nFolioRELACIONGASTOS ");
        queryInsertaLayoutEnc.append("  inner join v_DCD_RELGASTO dcd WITH (NOLOCK) ");
        queryInsertaLayoutEnc.append("				     	on dcd.NFOLIORELACIONGASTOS = tCE.NFOLIORELACIONGASTOS ");
        queryInsertaLayoutEnc.append(" WHERE	tCE.nFolioRELACIONGASTOS in ( SELECT	nFolioRelacionGastos ");
        queryInsertaLayoutEnc.append(" 		  FROM	tRelacionGastosCompromisoDetalle WITH(NOLOCK) ");
        queryInsertaLayoutEnc.append(" 		 WHERE	nFolioRelacionGastosCompromiso = ? )");
        queryInsertaLayoutEnc.append(" Group by  tCE.nFolioRELACIONGASTOS, tce.fAplicacion, tce.cramo, tCE.canocontrarrecibo ");
        PreparedStatement psInsertaEncabezadoLayout = null;
        try {
            psInsertaEncabezadoLayout = conn.prepareStatement(queryInsertaLayoutEnc.toString());
            psInsertaEncabezadoLayout.setInt(1, folioRelacionGastosCompromiso);
            psInsertaEncabezadoLayout.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsertaEncabezadoLayout);
        }
    }

    /*
	 * // Sin compromiso private static void insertaLayoutRGEncabezado(
	 * Connection conn, int idLayout, String leyenda, String ctaBancaria, String
	 * foliosRG, String integrada, String usuario ) throws Exception {
	 * 
	 * StringBuilder queryInsertaLayoutEnc = new StringBuilder();
	 * queryInsertaLayoutEnc.append(
	 * "INSERT INTO  tLayoutsCreadosRelacionGastosHeader(Id, fCreacionLayout, nFolio, Header, fCarga, fAplicacion, sRamo, sRamo1, sRamo2, sUnidadResponsable, sUnidadResponsable2, sUnidadResponsable3, sTipoMovimiento, sOrigenPpto, sTipoSol, sTipoMoneda, sTipoCambio, sTIPO_PAGO, sCveLeyenda, sCBEN, sCUENTA_BANCARIA, sIdRFC, sFAC, fFechaReferencia, sReferencia1, sReferencia2, sConcepto, sNotasReverso, sAMF, sNoContrarrecibo, sAuxiliarComodin, sCTR, sFolioDC, mISR, mIVA, mMil5, mMil2, mOtrasRet, mPenalizacion, mContribucion, mIvaDes, mIvaAnt, sDestinoGasto, sLogin, cEstatus)"
	 * ); queryInsertaLayoutEnc.append( " SELECT " + idLayout +
	 * ",getdate(), tCE.nFolioRELACIONGASTOS, 	'H' AS Header, " );// A
	 * queryInsertaLayoutEnc.append(
	 * "     	CONVERT(nvarchar(10), tCE.fAplicacion,103) FECHA_EXP, " );// B
	 * queryInsertaLayoutEnc.append(
	 * "     	CONVERT(nvarchar(10), tCE.fAplicacion,103) FECHA_APL, " ); // C
	 * queryInsertaLayoutEnc.append( "     	tCE.cRamo ID_RAMO, " ); // D
	 * queryInsertaLayoutEnc.append( "     	tCE.cRamo ID_RAMO_CR, " ); // E
	 * queryInsertaLayoutEnc.append( "     	tCE.cRamo ID_RAMO_REC, " ); // F
	 * queryInsertaLayoutEnc.append( "     	'RHQ'  ID_UNIDAD, " ); // G
	 * queryInsertaLayoutEnc.append( "     	'RHQ'  ID_UNIDAD_CR, " ); // H
	 * queryInsertaLayoutEnc.append( "     	'RHQ'  ID_UNIDAD_REC, " ); // I
	 * queryInsertaLayoutEnc.append( "     	'N' ID_TIPO_MOVIMIENTO,  " ); // J
	 * queryInsertaLayoutEnc.append( "     	'1' AS OrigenPpto, " ); // K
	 * queryInsertaLayoutEnc.append( "     	'3' AS TipoSol, " ); // L
	 * queryInsertaLayoutEnc.append( "     	'MXN' TipoMoneda, " ); // M
	 * queryInsertaLayoutEnc.append( "     	'1' TipoCambio, " ); // N
	 * queryInsertaLayoutEnc.append( "     	'1' TIPO_PAGO, " ); // O
	 * queryInsertaLayoutEnc.append( "     	'" + leyenda +
	 * "' AS CVE_LEYENDA_66," ); // P queryInsertaLayoutEnc.append(
	 * "     	'S04929' CBEN, " ); // Q queryInsertaLayoutEnc.append(
	 * "       '" + ctaBancaria.trim() + "' CUENTA_BANCARIA, " );// R
	 * queryInsertaLayoutEnc.append( "     	'16RHQ' RFC_227, " ); // S
	 * queryInsertaLayoutEnc.append( "     	'FAC' TDOC_87, " ); // T
	 * queryInsertaLayoutEnc.append( "     	 '' FechaReferencia, " ); // U
	 * queryInsertaLayoutEnc.append( "     	'' Referencia1, " ); // V
	 * queryInsertaLayoutEnc.append( "     	'' Referencia2, " ); // W
	 * queryInsertaLayoutEnc.append( "    	LEFT('" + integrada + " RG-" +
	 * foliosRG + "',500) CPAG_76, " );// X queryInsertaLayoutEnc.append(
	 * "      	'' NotasReverso, " ); // Y queryInsertaLayoutEnc.append(
	 * "     	'' AMF, " ); // Z queryInsertaLayoutEnc.append(
	 * "      	tCE.canocontrarrecibo NO_ACMI, " ); // AA
	 * queryInsertaLayoutEnc.append( "      '" + integrada +
	 * "'	AuxiliarComodin, " ); // AB queryInsertaLayoutEnc.append(
	 * "     	'' CTR, " ); // AC queryInsertaLayoutEnc.append(
	 * "     	'' FolioDC, " ); // AD queryInsertaLayoutEnc.append(
	 * "     	CONVERT(decimal(17, 2),SUM(DCD_ISR),0)  AS ISR_303, " ); // AE
	 * queryInsertaLayoutEnc.append(
	 * "      	CONVERT(decimal(17, 2), SUM(DCD_IVA), 0) IVA_304, " ); // AF
	 * queryInsertaLayoutEnc.append(
	 * "      	CONVERT(decimal(17, 2),0) MIL5_305, " ); // AG
	 * queryInsertaLayoutEnc.append(
	 * "      	CONVERT(decimal(17, 2),0) MIL2_306, " ); // AH
	 * queryInsertaLayoutEnc.append( "      	0 mImporteRetencion, " ); // AI
	 * queryInsertaLayoutEnc.append(
	 * "      	CONVERT(decimal(17, 2),0) PENALIZACION_308, " ); // AJ
	 * queryInsertaLayoutEnc.append(
	 * "      	CONVERT(decimal(17, 2), 0) CONTRIBUCION_309, " ); // AK
	 * queryInsertaLayoutEnc.append(
	 * "      	CONVERT(DECIMAL(17, 2), 0) AS IVADES_310,	" ); // AL
	 * queryInsertaLayoutEnc.append( "      	null IVAANT_311, " ); // AM
	 * queryInsertaLayoutEnc.append( "     	'NA' ID_DESTINO_GASTO,   " ); // AN
	 * queryInsertaLayoutEnc.append( "      	'" + usuario + "', " +
	 * "      	'ACTIVO' AS cEstatus " ); queryInsertaLayoutEnc.append(
	 * "  FROM	tRELACIONGASTOSEncabezado tCE (NOLOCK)" );
	 * queryInsertaLayoutEnc.append(
	 * "     	FULL OUTER JOIN 	tComprobacionLaudos Comp WITH (NOLOCK) " );
	 * queryInsertaLayoutEnc.append(
	 * "     	ON tCE.nFolioRELACIONGASTOS = Comp.nFolioRELACIONGASTOS " );
	 * queryInsertaLayoutEnc.append(
	 * "  inner join v_DCD_RELGASTO dcd WITH (NOLOCK) " );
	 * queryInsertaLayoutEnc.append(
	 * "				     	on dcd.NFOLIORELACIONGASTOS = tCE.NFOLIORELACIONGASTOS "
	 * ); queryInsertaLayoutEnc.append( " WHERE	tCE.nFolioRELACIONGASTOS in ( "
	 * + foliosRG + " )" ); queryInsertaLayoutEnc.append(
	 * " Group by  tCE.nFolioRELACIONGASTOS, tce.fAplicacion, tce.cramo, tCE.canocontrarrecibo "
	 * );
	 * 
	 * PreparedStatement psInsertaEncabezadoLayout = null;
	 * 
	 * try { psInsertaEncabezadoLayout = conn.prepareStatement(
	 * queryInsertaLayoutEnc.toString() );
	 * psInsertaEncabezadoLayout.executeUpdate();
	 * 
	 * } finally { CloseObject.closeObject( psInsertaEncabezadoLayout ); }
	 * 
	 * }
	 * 
	 * private static void insertaLayoutRGDetalle( Connection conn, int
	 * idLayout, String nFolios ) throws Exception { StringBuilder
	 * SqlLayoutGrabadoDet = new StringBuilder(); SqlLayoutGrabadoDet.append(
	 * " INSERT INTO tLayoutsCreadosRelacionGastosDetalle(nFolilo,sID_EVENTO,sEVENTO,sID_RAMO_ML,sUnidadResponsable,"
	 * ); SqlLayoutGrabadoDet.append(
	 * " saEjercicioFiscal,sGrupoFuncional,sFuncion,sSubFuncion,sProgramaGeneral,sActividadInstitucional,sProgramaPresupuestario,sCCAP_157,"
	 * ); SqlLayoutGrabadoDet.append(
	 * " sCCON_158,sCPARG_300,sCPAR_159,sTipoGasto,sFuenteFinanciamiento,sEntidadFederativa,sCartera,sUnidadEjecutora2,sCCOP_163,sPL,sOFI,"
	 * ); SqlLayoutGrabadoDet.append(
	 * " sAUX1,sAUX2,sAUX3,mImporteNeto,nMES_149,sNRES,sTIPO_CONTRATO,sCONC_MOV,mISR,mIVA,mMil5,mMil2,mContribucion,mOtrasRet,mPenalizacion,"
	 * ); SqlLayoutGrabadoDet.append( " sid_ctr_intdet)" );
	 * SqlLayoutGrabadoDet.append( " SELECT " + idLayout +
	 * ", '1' ID_EVENTO, '24.0.001' EVENTO, ltrim(TCEP.cRamo) ID_RAMO_ML, 'RHQ', TCEP.aEjercicioFiscal,  TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, "
	 * ); SqlLayoutGrabadoDet.append(
	 * " CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral,  "
	 * ); SqlLayoutGrabadoDet.append(
	 * " tCEP.cActividadInstitucional,  tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)) CCAP_157, substring(cpartida,2,1) CCON_158,"
	 * ); SqlLayoutGrabadoDet.append(
	 * " substring(cpartida,3,1) CPARG_300, substring(cpartida,4,2) CPAR_159, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento,  tCEP.cEntidadFederativa, tCEP.cCartera, '0000000000','00' CCOP_163,"
	 * ); SqlLayoutGrabadoDet.append(
	 * " '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3, convert(decimal(16, 2), SUM( TPDD.mImporteNeto)) Monto, MONTH(GETDATE()) MES_149,"
	 * ); SqlLayoutGrabadoDet.append(
	 * " '0' NRES, ltrim(TPDD.ID_TIPO_CONCEPTO) TIPO_CONTRATO, '000' CONC_MOV,"
	 * ); SqlLayoutGrabadoDet.append(
	 * " CONVERT(decimal(17, 2),ISNULL(SUM(mimporteISRResico + mImporteISRLaudos + mISROtros),0),0),  "
	 * ); SqlLayoutGrabadoDet.append(
	 * " CONVERT(decimal(17, 2),isnull(SUM(mimporteIvaArrenda + mimporteivahonorarios + mimporteflete23 + mImporteFlete4),0)) "
	 * ); SqlLayoutGrabadoDet.append(
	 * " ,  CONVERT(decimal(17, 2),sum(mImporteObra))" );
	 * SqlLayoutGrabadoDet.append( ", 0,  0, 0, 0, '' id_ctr_intdet  " );
	 * SqlLayoutGrabadoDet.append(
	 * " FROM tRELACIONGASTOSDetalle TPDD (nolock), tRELACIONGASTOSEncabezado TPDE (nolock), tCatalogoEP TCEP (nolock) "
	 * ); SqlLayoutGrabadoDet.append(
	 * " where TPDD.nFolioRELACIONGASTOS = TPDE.nFolioRELACIONGASTOS AND  TPDD.EP = TCEP.EP and  TPDD.nFolioRELACIONGASTOS  in ("
	 * + nFolios + ")" ); SqlLayoutGrabadoDet.append(
	 * " group by TCEP.aEjercicioFiscal, TPDD.ID_TIPO_CONCEPTO, TCEP.cRamo, TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, cProgramaGeneral,	tCEP.cActividadInstitucional, "
	 * ); SqlLayoutGrabadoDet.append(
	 * " tCEP.cProgramaPresupuestario, cpartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, tCEP.cCartera "
	 * );
	 * 
	 * PreparedStatement psInsertLayoutCompRG = null;
	 * 
	 * try { psInsertLayoutCompRG = conn.prepareStatement(
	 * SqlLayoutGrabadoDet.toString() ); psInsertLayoutCompRG.executeUpdate();
	 * 
	 * } finally { CloseObject.closeObject( psInsertLayoutCompRG ); }
	 * 
	 * }
	 */
    private static String getFoliosIntegradosCompromiso(Connection conn, int folioRelacionGastosCompromiso) throws Exception {
        String token = "";
        String cxpIntegradas = "";
        StringBuilder queryBuscaIntegradas = new StringBuilder();
        queryBuscaIntegradas.append("SELECT RTRIM(LTRIM(caNoContrarrecibo)) AS caNoContrarrecibo ");
        queryBuscaIntegradas.append("FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) ");
        queryBuscaIntegradas.append("WHERE nFolioRELACIONGASTOS IN ( ");
        queryBuscaIntegradas.append("SELECT nFolioRelacionGastos ");
        queryBuscaIntegradas.append("FROM tRelacionGastosCompromisoDetalle WITH (NOLOCK) ");
        queryBuscaIntegradas.append("WHERE nFolioRelacionGastosCompromiso = ? ");
        queryBuscaIntegradas.append(");");
        PreparedStatement psCXP = null;
        ResultSet rsCXP = null;
        try {
            psCXP = conn.prepareStatement(queryBuscaIntegradas.toString());
            psCXP.setInt(1, folioRelacionGastosCompromiso);
            rsCXP = psCXP.executeQuery();
            while (rsCXP.next()) {
                cxpIntegradas += token + rsCXP.getString("caNoContrarrecibo");
                token = " ";
            }
            return cxpIntegradas;
        } finally {
            CloseObject.closeObject(rsCXP);
            CloseObject.closeObject(psCXP);
        }
    }

    public static String getRFCCuentaBancaria(Connection conn, String ctaBan) throws Exception {
        String queryCtaBan = "SELECT	Rtrim(Ltrim(Replace(strrfc, '-', '')))   FROM	tuecuentasbancarias WITH(nolock)  WHERE	RTRIM( LTRIM( strclabe ) ) = RTRIM( LTRIM( ? ) )  ";
        PreparedStatement psCtaBan = null;
        ResultSet rsCtaBan = null;
        String RFCCtaBan = null;
        try {
            psCtaBan = conn.prepareStatement(queryCtaBan);
            psCtaBan.setString(1, ctaBan.trim());
            rsCtaBan = psCtaBan.executeQuery();
            if (rsCtaBan.next()) {
                RFCCtaBan = rsCtaBan.getString(1);
            }
            return RFCCtaBan;
        } finally {
            CloseObject.closeObject(rsCtaBan);
            CloseObject.closeObject(psCtaBan);
        }
    }

    // Layout Encabezado Laudos
    private static String generaEncabezadoLayoutCompRG(Connection conn, int folioRelacionGastosCompromiso, String ctaBancaria, String noIntegracion, String foliosRGIntegrados, int leyenda) throws Exception {
        StringBuilder queryEncabezadoLayout = new StringBuilder();
        // A
        queryEncabezadoLayout.append("SELECT top 1	1, " + "      	'H' AS Header, ");
        // B
        queryEncabezadoLayout.append("      	CONVERT(nvarchar(10), GETDATE(), 103) FECHA_EXP, ");
        // C
        queryEncabezadoLayout.append("      	CONVERT(nvarchar(10), GETDATE(), 103) FECHA_APL, ");
        // D
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO, ");
        // E
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO_CR, ");
        // F
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO_REC, ");
        // G
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD, ");
        // H
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD_CR, ");
        // I
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD_REC, ");
        // J
        queryEncabezadoLayout.append("      	'N' ID_TIPO_MOVIMIENTO,  ");
        // K
        queryEncabezadoLayout.append("      	'5' AS OrigenPpto, ");
        // L
        queryEncabezadoLayout.append("      	'9' AS TipoSol, ");
        // M
        queryEncabezadoLayout.append("      	'MXN' TipoMoneda, ");
        // N
        queryEncabezadoLayout.append("      	'1' TipoCambio, ");
        // O
        queryEncabezadoLayout.append("      	'1' TIPO_PAGO, ");
        // P
        queryEncabezadoLayout.append("      	'" + leyenda + "' AS CVE_LEYENDA_66,");
        // Q
        queryEncabezadoLayout.append("      	'S04929' CBEN, ");
        // R
        queryEncabezadoLayout.append("        '" + ctaBancaria.trim() + "' CUENTA_BANCARIA, ");
        // S
        queryEncabezadoLayout.append("      	'16RHQ' RFC_227, ");
        // T
        queryEncabezadoLayout.append("      	'FAC' TDOC_87, ");
        // U
        queryEncabezadoLayout.append("      	 '' FechaReferencia, ");
        // V
        queryEncabezadoLayout.append("      	'' Referencia1, ");
        // W
        queryEncabezadoLayout.append("      	'' Referencia2, ");
        // X
        queryEncabezadoLayout.append("      	LEFT('Integracion " + noIntegracion + " de RG-" + foliosRGIntegrados + "',500) CPAG_76, ");
        // Y
        queryEncabezadoLayout.append("      	'' NotasReverso, ");
        // Z
        queryEncabezadoLayout.append("      	'' AMF, ");
        // AA
        queryEncabezadoLayout.append("      	'" + noIntegracion + "' NO_ACMI, ");
        // AB
        queryEncabezadoLayout.append("      	'" + noIntegracion + "' AuxiliarComodin, ");
        // AC
        queryEncabezadoLayout.append("      	'" + noIntegracion + "' CTR, ");
        // Ad
        queryEncabezadoLayout.append("      	CONVERT(decimal(17, 2), sum(DCD_ISR),0)  AS ISR_303,");
        // AE
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_IVADES), 0) IVA_304, ");
        // AF
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_MIL5),0) MIL5_305, ");
        // AG
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_MIL2),0) MIL2_306, ");
        // AH
        queryEncabezadoLayout.append("       	ISNULL(CONVERT(varchar(20), sum(DCD_OTRAS_RET)),0) OTRASRET, ");
        // AI
        queryEncabezadoLayout.append("       	'0' PENALIZACION_308, ");
        // AJ
        queryEncabezadoLayout.append("       	'0' CONTRIBUCION_309, ");
        // AK
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_IVA), 0) AS IVADES_310,	");
        // AL
        queryEncabezadoLayout.append("       	'' IVAANT_311, ");
        // AM
        queryEncabezadoLayout.append("      	'' FolioDC, ");
        // AN
        queryEncabezadoLayout.append("      	'NA' ID_DESTINO_GASTO,   ");
        // AO
        queryEncabezadoLayout.append("      	'' TTRANS_60, ");
        // AP
        queryEncabezadoLayout.append("      	'' ANT_CON_IVA_413");
        queryEncabezadoLayout.append("  FROM	tRELACIONGASTOSEncabezado tCE WITH(NOLOCK) ");
        queryEncabezadoLayout.append("      	inner join v_DCD_RELGASTO dcd WITH (NOLOCK) ");
        queryEncabezadoLayout.append("      	on dcd.NFOLIORELACIONGASTOS = tce.NFOLIORELACIONGASTOS ");
        queryEncabezadoLayout.append(" WHERE	tCE.nFolioRELACIONGASTOS IN (");
        queryEncabezadoLayout.append(" 		SELECT	nFolioRelacionGastos FROM	tRelacionGastosCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioRelacionGastosCompromiso = ? )");
        queryEncabezadoLayout.append(" GROUP BY cRamo");
        PreparedStatement psHeader = null;
        ResultSet rsHeader = null;
        try {
            log.debug(queryEncabezadoLayout.toString());
            psHeader = conn.prepareStatement(queryEncabezadoLayout.toString());
            psHeader.setInt(1, folioRelacionGastosCompromiso);
            rsHeader = psHeader.executeQuery();
            if (rsHeader.next())
                return Util.resultSetToConcatenateString(rsHeader, ",", 1);
            else
                throw new Exception("No fue posible armar el encabezado del layout ya que no se retornaron registros de informacion");
        } finally {
            CloseObject.closeObject(rsHeader);
            CloseObject.closeObject(psHeader);
        }
    }

    private static String generaDetalleLayoutCompRG(Connection conn, int folioRelacionGastosCompromiso, String folioAutSICOP, String folioSuficiencia) throws Exception {
        BigDecimal total = new BigDecimal(0);
        BigDecimal retenciones = new BigDecimal(0);
        BigDecimal revisarTotal = new BigDecimal(0);
        BigDecimal revisarRete = new BigDecimal(0);
        StringBuilder queryLayoutDetalle = new StringBuilder();
        // A
        queryLayoutDetalle.append(" SELECT '1' ID_EVENTO,  	");
        // B
        queryLayoutDetalle.append(" '24.0.001' 	  EVENTO,  	");
        // C
        queryLayoutDetalle.append(" Substring(D.EP, 6, 2)  ID_RAMO_ML, ");
        // D
        queryLayoutDetalle.append(" 'RHQ',  					");
        // E
        queryLayoutDetalle.append(" Substring(D.EP, 1, 4)  aEjercicioFiscal,");
        // F
        queryLayoutDetalle.append(" Substring(D.EP, 13, 1) cGrupoFuncional, ");
        // G
        queryLayoutDetalle.append(" Substring(D.EP, 15, 1) cFuncion,  ");
        // H
        queryLayoutDetalle.append(" Substring(D.EP, 17, 2) cSubFuncion, ");
        // I
        queryLayoutDetalle.append(" CASE WHEN Substring(D.EP, 20, 2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE Substring(D.EP, 20, 2) END AS cProgramaGeneral, ");
        // J
        queryLayoutDetalle.append(" Substring(D.EP, 23, 3) cActividadInstitucional, ");
        // K
        queryLayoutDetalle.append(" Substring(D.EP, 27, 4) cProgramaPresupuestario,");
        // L
        queryLayoutDetalle.append(" Substring(D.EP, 32, 1) CCAP_157,  ");
        // M
        queryLayoutDetalle.append(" Substring(D.EP, 33, 1) CCON_158,  ");
        // N
        queryLayoutDetalle.append(" Substring(D.EP, 34, 1) CPARG_300, ");
        // O
        queryLayoutDetalle.append(" Substring(D.EP, 35, 2) CPAR_159,  ");
        // P
        queryLayoutDetalle.append(" Substring(D.EP, 38, 1) cTipoGasto,");
        // Q
        queryLayoutDetalle.append(" Substring(D.EP, 40, 1) cFuenteFinanciamiento,");
        // R
        queryLayoutDetalle.append(" Substring(D.EP, 42, 2) cEntidadFederativa, ");
        // S
        queryLayoutDetalle.append(" SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11) AS cCartera,");
        // T
        queryLayoutDetalle.append(" '0000000000' AS CCAU_162,  		");
        // U
        queryLayoutDetalle.append(" '00'                   CCOP_163,  	");
        // V
        queryLayoutDetalle.append(" '000'                  PL,  		");
        // W
        queryLayoutDetalle.append(" '000'                  OFI,  		");
        // X
        queryLayoutDetalle.append(" '00000'                AUX1,  		");
        // Y
        queryLayoutDetalle.append(" '00000'                AUX2,  		");
        // Z
        queryLayoutDetalle.append(" '0000000000'           AUX3,  		");
        // AA
        queryLayoutDetalle.append(" '" + StringUtils.trimToEmpty(folioAutSICOP) + "' AS NCOM_35,");
        // AB
        queryLayoutDetalle.append(" CASE  WHEN cRetSICOP = 'N' THEN CONVERT(DECIMAL(17, 2), CONVERT(DECIMAL(17, 2), Sum( D.mImporteMasIva))) ELSE CONVERT(DECIMAL(17, 2), CONVERT(DECIMAL(17, 2), Sum(D.mImporteNeto))) END  AS MONTO,  ");
        // AC
        queryLayoutDetalle.append(" D.cmes                 MES_149,  	");
        // AD
        queryLayoutDetalle.append(" '" + StringUtils.trimToEmpty(folioSuficiencia) + "'  Suficiencia, ");
        // AE
        queryLayoutDetalle.append(" CASE WHEN Substring(ep, 32, 5) = '35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO, ");
        // AF
        queryLayoutDetalle.append(" '000'                  CONC_MOV,  	");
        // AG
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(mimporteISRResico + mISRArrenda + mISRHonorarios+ mISROtros + mImporteISRLaudos),0)  AS RETENCIONES, ");
        // AH
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(mImporteIvaArrenda + mImporteIvaHonorarios + mImporteFlete23+  mImporteFlete4),0) IVA_43,  	");
        // AI
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(mObra5, 0) + isnull(mImporteObra,0))) MIL5_44,  						");
        // AJ
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(m2Millar,0))) MIL2_312,  						");
        // AK
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(mRetImpuestoCedular,0))) OTRAS_RET_313, ");
        // AL
        queryLayoutDetalle.append(" 0 IVADES_45,  						");
        // AM
        queryLayoutDetalle.append(" 0 ANTICIPO_46,  					");
        // AN
        queryLayoutDetalle.append(" 0 PENALIZACION_314,  				");
        // AO
        queryLayoutDetalle.append(" 0 IVAANT_47,  						");
        // AP
        queryLayoutDetalle.append(" ''  id_ctr_intdet  				");
        queryLayoutDetalle.append(" FROM   tRELACIONGASTOSDetalle D WITH (NOLOCK)  ");
        queryLayoutDetalle.append(" FULL OUTER JOIN tComprobacionLaudos Comp WITH (NOLOCK)  ");
        queryLayoutDetalle.append(" 	ON D.nFolioRELACIONGASTOS = Comp.nFolioRELACIONGASTOS ");
        queryLayoutDetalle.append(" WHERE  d.nFolioRELACIONGASTOS in (SELECT	nFolioRelacionGastos FROM	tRelacionGastosCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioRelacionGastosCompromiso = ? ) ");
        queryLayoutDetalle.append(" GROUP  BY Substring(D.EP, 61, 3),  D.EP, D.cmes,mISRHonorarios,  D.ID_TIPO_CONCEPTO, cRetSICOP, SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11)");
        PreparedStatement psLayoutDetalle = null;
        ResultSet rsLayoutDetalle = null;
        StringBuilder detalle = new StringBuilder();
        try {
            psLayoutDetalle = conn.prepareStatement(queryLayoutDetalle.toString());
            psLayoutDetalle.setInt(1, folioRelacionGastosCompromiso);
            rsLayoutDetalle = psLayoutDetalle.executeQuery();
            while (rsLayoutDetalle.next()) {
                String token = new String("");
                for (int i = 1; i <= 42; i++) {
                    if (i == 28) {
                        revisarTotal = rsLayoutDetalle.getBigDecimal(i);
                        total = total.add(revisarTotal);
                        if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                            throw new Exception("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + folioRelacionGastosCompromiso);
                        } else {
                            detalle.append(token).append(rsLayoutDetalle.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                            token = ",";
                        }
                    } else if (i >= 33 && i <= 37) {
                        // Suma el importe de las retenciones
                        revisarRete = rsLayoutDetalle.getBigDecimal(i);
                        retenciones = retenciones.add(revisarRete);
                        detalle.append(token).append(rsLayoutDetalle.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    } else {
                        detalle.append(token).append(rsLayoutDetalle.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                }
                token = "";
                detalle.append("\r\n");
                log.debug("Detalle: " + detalle.toString());
            }
            // Valida que el total del Layout sea igual a los pagos
            validarTotalLayoutIntegrada(conn, total, folioRelacionGastosCompromiso);
            // Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetencionesIntegrada(conn, retenciones, folioRelacionGastosCompromiso);
            return detalle.toString();
        } finally {
            CloseObject.closeObject(psLayoutDetalle);
            CloseObject.closeObject(rsLayoutDetalle);
        }
    }

    private static void insertaLayoutCompRGDetalle(Connection conn, int nFolioLayout, String folioAutSICOP, int folioRelacionGastosCompromiso) throws Exception {
        StringBuilder queryInsertLayoutCompRG = new StringBuilder();
        queryInsertLayoutCompRG.append("INSERT INTO tLayoutsCreadosRelacionGastosDetalle(nFolilo, sID_EVENTO, sEVENTO, sID_RAMO_ML, sUnidadResponsable, saEjercicioFiscal, sGrupoFuncional, sFuncion, sSubFuncion, sProgramaGeneral, ");
        queryInsertLayoutCompRG.append(" 		sActividadInstitucional, sProgramaPresupuestario, sCCAP_157, sCCON_158, sCPARG_300, sCPAR_159, sTipoGasto, sFuenteFinanciamiento, sEntidadFederativa, sCartera, sUnidadEjecutora2, sCCOP_163, sPL, sOFI, sAUX1, sAUX2, sAUX3, NCOM_35, ");
        queryInsertLayoutCompRG.append(" 		mImporteNeto, nMES_149, sNRES, sTIPO_CONTRATO, sCONC_MOV, mISR, mIVA, mMil5, mMil2, mContribucion, mOtrasRet, IVADES_45, ANTICIPO_46, mPenalizacion, IVAANT_47, sid_ctr_intdet)");
        queryInsertLayoutCompRG.append(" SELECT " + nFolioLayout + ", ");
        // A
        queryInsertLayoutCompRG.append("       '1'                    ID_EVENTO, ");
        // B
        queryInsertLayoutCompRG.append("       '24.0.001'             EVENTO, ");
        // C
        queryInsertLayoutCompRG.append("       Substring(D.EP, 6, 2)  ID_RAMO_ML, ");
        // D
        queryInsertLayoutCompRG.append("       'RHQ', ");
        // E
        queryInsertLayoutCompRG.append("       Substring(D.EP, 1, 4)  aEjercicioFiscal, ");
        // F
        queryInsertLayoutCompRG.append("       Substring(D.EP, 13, 1) cGrupoFuncional, ");
        // G
        queryInsertLayoutCompRG.append("       Substring(D.EP, 15, 1) cFuncion, ");
        // H
        queryInsertLayoutCompRG.append("       Substring(D.EP, 17, 2) cSubFuncion, ");
        // I
        queryInsertLayoutCompRG.append("       CASE WHEN Substring(D.EP, 20, 2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE Substring(D.EP, 20, 2)  END   AS cProgramaGeneral, ");
        // J
        queryInsertLayoutCompRG.append("       Substring(D.EP, 23, 3) cActividadInstitucional, ");
        // K
        queryInsertLayoutCompRG.append("       Substring(D.EP, 27, 4) cProgramaPresupuestario, ");
        // L
        queryInsertLayoutCompRG.append("       Substring(D.EP, 32, 1) CCAP_157, ");
        // M
        queryInsertLayoutCompRG.append("       Substring(D.EP, 33, 1) CCON_158, ");
        // N
        queryInsertLayoutCompRG.append("       Substring(D.EP, 34, 1) CPARG_300, ");
        // O
        queryInsertLayoutCompRG.append("       Substring(D.EP, 35, 2) CPAR_159, ");
        // P
        queryInsertLayoutCompRG.append("       Substring(D.EP, 38, 1) cTipoGasto, ");
        // Q
        queryInsertLayoutCompRG.append("       Substring(D.EP, 40, 1) cFuenteFinanciamiento, ");
        // R
        queryInsertLayoutCompRG.append("       Substring(D.EP, 42, 2) cEntidadFederativa, ");
        // S
        queryInsertLayoutCompRG.append("       SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11) AS cCartera, ");
        // T
        queryInsertLayoutCompRG.append("       '0000000000' AS CCAU_162, ");
        // U
        queryInsertLayoutCompRG.append("       '00'                   CCOP_163, ");
        // V
        queryInsertLayoutCompRG.append("       '000'                  PL, ");
        // W
        queryInsertLayoutCompRG.append("       '000'                  OFI, ");
        // X
        queryInsertLayoutCompRG.append("       '00000'                AUX1, ");
        // Y
        queryInsertLayoutCompRG.append("       '00000'                AUX2, ");
        // Z
        queryInsertLayoutCompRG.append("       '0000000000'           AUX3, ");
        // AA
        queryInsertLayoutCompRG.append("       '" + StringUtils.trimToEmpty(folioAutSICOP) + "' AS NCOM_35,");
        // AB
        queryInsertLayoutCompRG.append("       CASE WHEN cRetSICOP = 'N' THEN CONVERT(DECIMAL(17, 2), CONVERT(DECIMAL(17, 2), Sum(D.mImporteMasIva))) ELSE CONVERT(DECIMAL(17, 2), CONVERT(DECIMAL(17, 2), Sum(D.mImporteNeto)))  END   AS MONTO, ");
        // AC
        queryInsertLayoutCompRG.append("       D.cmes                 MES_149, ");
        // AD
        queryInsertLayoutCompRG.append("       '0'                    NRES, ");
        // AE
        queryInsertLayoutCompRG.append("       CASE WHEN Substring(ep, 32, 5) = '35801' THEN 'GD' ELSE 'PN' END  TIPO_CONTRATO, ");
        // AF
        queryInsertLayoutCompRG.append("       '000'                  CONC_MOV, ");
        // AG
        queryInsertLayoutCompRG.append("        CONVERT(decimal(17, 2),ISNULL(SUM(mimporteISRResico + mImporteISRLaudos + mISROtros + mISRArrenda + mISRHonorarios),0),0)    AS RETENCIONES, ");
        // AH
        queryInsertLayoutCompRG.append("        CONVERT(decimal(17, 2),SUM(mimporteivaArrenda + mimporteivahonorarios + mimporteflete23 + mImporteFlete4 ),0) IVA_43, ");
        // AI
        queryInsertLayoutCompRG.append("       0 MIL5_44, ");
        // AJ
        queryInsertLayoutCompRG.append("       0 MIL2_312, ");
        // AK
        queryInsertLayoutCompRG.append("       0 CONTRIBUCION_48, ");
        // AL
        queryInsertLayoutCompRG.append("       0 OTRAS_RET_313, ");
        // AM
        queryInsertLayoutCompRG.append("       0 IVADES_45, ");
        // AN
        queryInsertLayoutCompRG.append("       0 ANTICIPO_46, ");
        // AO
        queryInsertLayoutCompRG.append("       0 PENALIZACION_314, ");
        // AP
        queryInsertLayoutCompRG.append("       0 IVAANT_47, ");
        // AQ
        queryInsertLayoutCompRG.append("       ''  id_ctr_intdet ");
        queryInsertLayoutCompRG.append(" FROM   tRELACIONGASTOSDetalle D WITH (NOLOCK) ");
        queryInsertLayoutCompRG.append("       FULL OUTER JOIN tComprobacionLaudos Comp WITH (NOLOCK) ");
        queryInsertLayoutCompRG.append("                    ON D.nFolioRELACIONGASTOS = Comp.nFolioRELACIONGASTOS ");
        queryInsertLayoutCompRG.append(" WHERE  d.nFolioRELACIONGASTOS in ( SELECT	nFolioRelacionGastos FROM	tRelacionGastosCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioRelacionGastosCompromiso = ? )");
        queryInsertLayoutCompRG.append(" GROUP  BY Substring(D.EP, 61, 3),  D.EP,  D.cmes,     D.ID_TIPO_CONCEPTO, cRetSICOP, SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11)");
        PreparedStatement psInsertLayoutCompRG = null;
        try {
            psInsertLayoutCompRG = conn.prepareStatement(queryInsertLayoutCompRG.toString());
            psInsertLayoutCompRG.setInt(1, folioRelacionGastosCompromiso);
            psInsertLayoutCompRG.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsertLayoutCompRG);
        }
    }

    public static String getListaRGCompromisoIntegradas(Connection conn, String caNoCompromiso) throws Exception {
        String querySel = "SELECT	nFolioRelacionGastos  FROM	tRelacionGastosCompromisoEncabezado rgCompEnc WITH(NOLOCK) INNER JOIN  tRelacionGastosCompromisoDetalle rgCompDet WITH(NOLOCK) ON  rgCompEnc.nFolioRelacionGastosCompromiso = rgCompDet.nFolioRelacionGastosCompromiso WHERE  canocontrarrecibo = ?";
        PreparedStatement psSel = null;
        ResultSet rsSel = null;
        String listaFoliosRG = "";
        try {
            psSel = conn.prepareStatement(querySel);
            psSel.setString(1, caNoCompromiso);
            rsSel = psSel.executeQuery();
            String token = "";
            while (rsSel.next()) {
                listaFoliosRG += token + StringUtils.trimToEmpty(rsSel.getString(1));
                token = ",";
            }
            return listaFoliosRG;
        } finally {
            CloseObject.closeObject(rsSel);
            CloseObject.closeObject(psSel);
        }
    }

    public static String getIntegracionRGCompromisoIntegradas(Connection conn, String caNoCompromiso) throws Exception {
        String querySel = "SELECT	cIdContrato   FROM	tCompromisoEncabezado rgCompEnc WITH(NOLOCK)  WHERE	caNoCompromiso= ? ";
        PreparedStatement psSel = null;
        ResultSet rsSel = null;
        String integracion = null;
        try {
            psSel = conn.prepareStatement(querySel);
            psSel.setString(1, caNoCompromiso);
            rsSel = psSel.executeQuery();
            if (rsSel.next())
                integracion = rsSel.getString(1);
            return integracion;
        } finally {
            CloseObject.closeObject(rsSel);
            CloseObject.closeObject(psSel);
        }
    }

    private static boolean existeLayout(Connection conn, String folioIntegracionRGCompromiso) throws Exception {
        String querySel = "SELECT	COUNT(*) AS Existe  FROM	tLayoutsCreadosRelacionGastosHeader WITH(NOLOCK) WHERE	sAuxiliarComodin = ?";
        ResultSet rsSel = null;
        PreparedStatement psSel = null;
        try {
            psSel = conn.prepareStatement(querySel);
            psSel.setString(1, folioIntegracionRGCompromiso);
            rsSel = psSel.executeQuery();
            return rsSel.next() && rsSel.getInt(1) > 0;
        } finally {
            CloseObject.closeObject(rsSel);
            CloseObject.closeObject(psSel);
        }
    }

    private static boolean updateLayoutRGHeaderStatus(Connection conn, String sFolioIntegracionRG) throws Exception {
        boolean respuesta = true;
        PreparedStatement psUpdateLayoutRG = null;
        try {
            String sUpdateLayoutRG = " UPDATE tLayoutsCreadosRelacionGastosHeader SET cEstatus = 'ACTIVO' WHERE	sAuxiliarComodin = ? ";
            psUpdateLayoutRG = conn.prepareStatement(sUpdateLayoutRG);
            psUpdateLayoutRG.setString(1, sFolioIntegracionRG);
            int updateVuelos = psUpdateLayoutRG.executeUpdate();
            log.info("Se Actualizaron: " + updateVuelos + " Registros.");
        } finally {
            CloseObject.closeObject(psUpdateLayoutRG);
        }
        return respuesta;
    }

    public static void insertaTipoDocumentacionRG(Connection conn, int nFolioPago, String destinoGasto) throws Exception {
        String queryExiste = "SELECT COUNT(*) FROM tRelacionGastosTipoDocumentacion WITH(NOLOCK) WHERE nFolioRelacionGastos = ? AND aEjercicioFiscal = ?";
        String queryInsert = "INSERT INTO tRelacionGastosTipoDocumentacion(nFolioRelacionGastos, aEjercicioFiscal, cDestinoGasto) VALUES(?,?,?)";
        PreparedStatement psInserta = null;
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
            int ejercicioFiscal = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            psSelect = conn.prepareStatement(queryExiste);
            psSelect.setInt(1, nFolioPago);
            psSelect.setInt(2, ejercicioFiscal);
            rs = psSelect.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    psInserta = conn.prepareStatement(queryInsert);
                    psInserta.setInt(1, nFolioPago);
                    psInserta.setInt(2, ejercicioFiscal);
                    psInserta.setString(3, destinoGasto);
                    psInserta.executeUpdate();
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psInserta);
            CloseObject.closeObject(psSelect);
        }
    }

    public static int actualizaTipoRG(Connection conn, int folioRelacionGastos) throws Exception {
        String queryUpdate = "UPDATE	rgEncabezado  SET	nContieneFacturas =  ( CASE WHEN lPagoConFacturas > 0 THEN 1 ELSE 0 end), " + "      	nEsComisionExtranjero =  ( CASE WHEN lOficioExtranjero > 0 THEN 1 ELSE 0 end)  , " + "      	nEsCertificadoTransito = ( CASE WHEN lCertificadoTransito > 0 THEN 1 ELSE 0 end)  , " + "      	nEsAlimentacionBrigadistas = ( CASE WHEN lAlimentacionBrigadistas > 0 THEN 1 ELSE 0 end) " + "  FROM	tRELACIONGASTOSEncabezado rgEncabezado WITH(NOLOCK) " + "      	INNER JOIN " + "      	tRelacionGastosTipoDocumentacion rgTipoDocumentacion WITH(NOLOCK) ON rgEncabezado.nFolioRELACIONGASTOS = rgTipoDocumentacion.nFolioRelacionGastos  WHERE	rgEncabezado.nFolioRelacionGastos = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryUpdate);
            ps.setInt(1, folioRelacionGastos);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaTipoDocumentacion(Connection conn, int ejercicioFiscal, int nFolioPago, String campo, int numDocumentos) throws Exception {
        String queryUpdate = "UPDATE tRelacionGastosTipoDocumentacion SET " + campo + " = ( " + campo + " + ? )WHERE nFolioRelacionGastos = ? AND aEjercicioFiscal = ?";
        int afectados = 0;
        PreparedStatement psUpdate = null;
        try {
            psUpdate = conn.prepareStatement(queryUpdate);
            psUpdate.setInt(1, numDocumentos);
            psUpdate.setInt(2, nFolioPago);
            psUpdate.setInt(3, ejercicioFiscal);
            afectados = psUpdate.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    /*
	 * 
	 * 
	 */
    public static boolean esRGLaudos(Connection conn, int folio) throws Exception {
        String query = "SELECT	mImporteISRLaudos FROM	tRELACIONGASTOSDetalle WITH(NOLOCK)  WHERE	nFolioRELACIONGASTOS = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getDouble(1) > 0.0d;
            else
                throw new Exception("No se registro informacion del pago: " + folio);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void insertaInformacionLaudos(Connection conn, int folio) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT  encabezado.nFolioRELACIONGASTOS,encabezado.nFolioCaja, SUM( detalle.mImporteISRLaudos ) AS mImporteRet,SUM( detalle.mImporteMasIva) AS mImporteNeto,'N' AS cRetSicop, 'N' AS cEsDevengado, 'N' AS cEsLiquidacion  ");
        query.append("  FROM	tRELACIONGASTOSEncabezado encabezado WITH(NOLOCK)  ");
        query.append("		INNER JOIN  tRELACIONGASTOSDetalle detalle WITH(NOLOCK)  ");
        query.append("      	ON encabezado.nFolioRELACIONGASTOS = detalle.nFolioRELACIONGASTOS  ");
        query.append("      	WHERE	encabezado.nFolioRELACIONGASTOS = ?   ");
        query.append("      	GROUP BY encabezado.nFolioRELACIONGASTOS,   encabezado.nFolioCaja ");
        String qCaja = "SELECT	mMontoSolicitud FROM	tcajaencabezado WITH(NOLOCK)  WHERE	nFoliocaja = ?";
        PreparedStatement psInfoCaja = null;
        PreparedStatement psMontoCaja = null;
        ResultSet rsInfoCaja = null;
        ResultSet rsMontoCaja = null;
        try {
            psInfoCaja = conn.prepareStatement(query.toString());
            psInfoCaja.setInt(1, folio);
            rsInfoCaja = psInfoCaja.executeQuery();
            if (rsInfoCaja.next()) {
                ComprobacionLaudos comprobacion = ComprobacionLaudos.instance(rsInfoCaja);
                psMontoCaja = conn.prepareStatement(qCaja);
                psMontoCaja.setInt(1, comprobacion.getFolioCaja());
                rsMontoCaja = psMontoCaja.executeQuery();
                if (rsMontoCaja.next()) {
                    comprobacion.setImporteCaja(rsMontoCaja.getDouble("mMontoSolicitud"));
                    ComprobacionLaudosManager.insertaInformacionLaudos(conn, comprobacion);
                } else {
                    throw new Exception("No se encontro el monto de solicitud de caja para el folio de caja: " + comprobacion.getFolioCaja());
                }
            } else {
                throw new Exception("No se encontro informacion de caja para el folio: " + folio);
            }
        } finally {
            CloseObject.closeObject(rsInfoCaja);
            CloseObject.closeObject(rsMontoCaja);
            CloseObject.closeObject(psInfoCaja);
            CloseObject.closeObject(psMontoCaja);
        }
    }

    public static double leeMontoLaudos(Connection conn, int folio) throws Exception {
        String query = "SELECT	SUM(mImporteISRLaudos) AS mISRLaudos  FROM	tRELACIONGASTOSDetalle det WITH(NOLOCK) WHERE	nFolioRELACIONGASTOS = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        double isrLaudos = 0.0d;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next())
                isrLaudos = rs.getDouble(1);
            return isrLaudos;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean esRGJuegosDeportivos(Connection conn, int nFolioTramite) throws Exception {
        String query = "SELECT	ISNULL(nEsBoxLunch,0) AS nEsBoxLunch FROM	tRELACIONGASTOSEncabezado WITH(NOLOCK) WHERE	nFolioRELACIONGASTOS = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioTramite);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean esRGAlimentacionBrigadistas(Connection conn, int nFolioTramite) throws Exception {
        String query = "SELECT	nEsAlimentacionBrigadistas FROM	tRELACIONGASTOSEncabezado WITH(NOLOCK) WHERE	nFolioRELACIONGASTOS = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioTramite);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static RelacionGastosEncabezado clonaRelacionGastos(Connection conn, Integer folioRG) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT  encabezado.fAplicacion , encabezado.cRamo ,encabezado.cUnidadResponsable ,encabezado.cEjercicio , encabezado.cIdEntidadContable , encabezado.cIdRelacion ,  encabezado.cIdTipoDocumento ,");
        query.append("     encabezado.cIdTipoRelacion , encabezado.cIdTipoMontoDesembolso , encabezado.cIdRFC , CAST(encabezado.fRecepcion AS DATE ) fRecepcion, CAST(encabezado.fRevision AS DATE ) fRevision ,   ");
        query.append("      CAST(encabezado.fProgramadaPago AS DATE ) fProgramadaPago , encabezado.cConcepto , encabezado.mImporteNeto ,  encabezado.cIdUnidadAdministrativa , encabezado.cIdGRegional ,  ");
        query.append("      encabezado.cIdGEstatal ,  encabezado.cIdDistritoRiego , encabezado.cIdTipoFondo , '' AS caNoContrarrecibo , encabezado.caNoAP , encabezado.cIdEstadoRelacion , encabezado.lContrarreciboImpreso , ");
        query.append("      encabezado.nIdConcepto , encabezado.cIdTipoLimiteDlls , encabezado.cIdUsuarioCaptura , encabezado.cIdUsuarioImpresion , encabezado.cIdUsuarioRevision , encabezado.cIdUsuarioAprobacion , ");
        query.append("      encabezado.cIdUsuarioRechazo , encabezado.lSuficienciaAnualValidada , encabezado.lSuficienciaMensualValidada , encabezado.ID_DESTINO_GASTO , encabezado.ID_TIPO_MOVIMIENTO , encabezado.ID_TIPO_CONCEPTO , ");
        query.append("      encabezado.cnombre , encabezado.TIPO_OPERACION , encabezado.cEvento , encabezado.aEjercicioFiscal , encabezado.cCentroContable , encabezado.cMes , encabezado.RFC , encabezado.mImporteBruto , ");
        query.append("      encabezado.mImporteMasIva , NULL AS cDocumentoHaplicado , NULL AS nFolioPoliza , encabezado.cTipoPoliza , encabezado.ALM , encabezado.capitulo , encabezado.cReferenciaPRODDER , encabezado.nPorcImpuestoCedular ,");
        query.append("      encabezado.lAplicaImpuestoCedular , encabezado.nTipoCambio , encabezado.cOficioDiferenciaCambiaria , encabezado.U_LOGIN , 0 AS nEnviadoSICOP , encabezado.cUnidadResponsableContable , ");
        query.append("      encabezado.nFolioPolizaCancelacion , encabezado.fCancelacion , encabezado.cDescripcionPoliza , encabezado.c_origen , encabezado.sFirmanteVoBo , encabezado.sPuestoVoBo , encabezado.sFirmanteAut , ");
        query.append("      encabezado.sPuestoAut , encabezado.nFolioCaja , encabezado.CTAB , encabezado.polManual , encabezado.reclasificada , encabezado.cRadicado , encabezado.nidprograma , encabezado.cSubPrograma , encabezado.nIdComision , ");
        query.append("      encabezado.cInformeComision , encabezado.nAcompanantes , encabezado.nFolioCargaMasiva , encabezado.sFirmanteEla , encabezado.sPuestoEla , encabezado.cBoletoReservacion , encabezado.mMontoBoleto, ");
        query.append("      encabezado.cPagoReferenciado, encabezado.cReferenciaBancaria , encabezado.nEsCertificadoTransito , encabezado.nEsAlimentacionBrigadistas , encabezado.nEsComisionNacional , encabezado.nEsComisionExtranjero , ");
        query.append("      encabezado.nContieneFacturas , encabezado.nNumEmpleadoVoBo , encabezado.nNumEmpleadoAut , encabezado.nNumEmpleadoElab , encabezado.cEsFirmaElectronica");
        query.append("  FROM	tRELACIONGASTOSEncabezado encabezado ");
        query.append("  WHERE	encabezado.nFolioRELACIONGASTOS = ?");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioRG);
            rs = ps.executeQuery();
            Map<String, String> values = RSToTable.rsToMap(rs);
            RelacionGastosEncabezado rgEncabezado = new RelacionGastosEncabezado();
            DateConverter converter = new DateConverter(null);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(rgEncabezado, values);
            return rgEncabezado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<RelacionGastosDetalle> getRelacionGastosDetalle(Connection conn, Integer folioRG) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT nFolioRELACIONGASTOS, nDocRenglon, nMes, cEjercicio, cIdEntidadContable, cIdRelacion, EP, cIdCuentaContable, mComprometido,       ");
        query.append(" nPoliza, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cEvento, aEjercicioFiscal, cCentroContable, cMes, RFC, mImporteNeto, ALM, mImporteBruto,       ");
        query.append(" mImporteMasIva, mImporteIva, nCapitulo, cDocumentoHaplicado, nFolioPoliza, cTipoPoliza, mSancion, mDevolucion, mImporteAmortiza, mRetencion,  ");
        query.append(" mPenalizacion, m2Millar, m23IVA, mISRHonorarios, mObra5, mImporteFlete4, mISRArrenda, mRetImpuestoCedular, mBruto, mAmortizacionAnticipo, mIVA,");
        query.append(" mNeto, m5Millar, mFletes, mCedular, mImporte, mImporteIvaArrenda, mImporteIvaHonorarios + isnull(mImporteIva6 , 0 ), mImporteFlete23, ");
        query.append(" mImporteIvaProv, mImporteObra, mCNIC, mIMDT, mTesofe, altaAlmacen, Periodo13, ADEFAS, OBGT,CTAB, mImporteISRLaudos,mImporteHospedaje,FFM,    ");
        query.append(" mImporteNegativo, mISROtros, cPasivo ");
        query.append(" FROM   trelaciongastosdetalle detalle ");
        query.append(" WHERE  nFolioRELACIONGASTOS = ? ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioRG);
            rs = ps.executeQuery();
            boolean continuar = false;
            List<RelacionGastosDetalle> detalle = new ArrayList<RelacionGastosDetalle>();
            int renglones = 0;
            do {
                RelacionGastosDetalle detalleRenglon = new RelacionGastosDetalle();
                Map<String, String> objMap = RSToTable.rsToMap(rs);
                if (objMap != null) {
                    renglones++;
                    log.debug("Poblando el renglon " + renglones + " del detalle");
                    BeanUtils.populate(detalleRenglon, objMap);
                    detalle.add(detalleRenglon);
                    continuar = true;
                } else
                    continuar = false;
            } while (continuar);
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<RelacionGastosDetalle> generaCalendario(Connection conn, Map<String, BigDecimal> detalleGenealNuevo) throws Exception {
        int ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscal(conn).getEjercicio();
        int currYear = (new GregorianCalendar()).get(Calendar.YEAR);
        int mesInicio = 1;
        int tope = 12;
        int mesActual = (ejercicioFiscal == currYear ? (new GregorianCalendar()).get(Calendar.MONTH) + 1 : Calendar.DECEMBER + 1);
        List<RelacionGastosDetalle> detalle = new ArrayList<RelacionGastosDetalle>();
        int nDocRenglon = 1;
        /* Ciclo que recorres las EP */
        for (Iterator<String> detalleIterator = detalleGenealNuevo.keySet().iterator(); detalleIterator.hasNext(); ) {
            boolean continuar = true;
            String ep = detalleIterator.next();
            BigDecimal montoPorCubrir = detalleGenealNuevo.get(ep);
            log.debug("Buscando saldo para cubrir: " + Util.formatNumber(montoPorCubrir) + " de la EP[" + ep + "]");
            List<SaldoMensual> saldoCuenta = SaldoManager.obtenSaldoMensual(conn, "82106", ep);
            /*
			 * Los ingresos propios van del mes 1 al 12, los ingresos fiscales
			 * del mes actual al 1
			 */
            boolean esIngresoPropio = esIngresoPropio(ep);
            if (esIngresoPropio) {
                mesInicio = 1;
                tope = 12;
            } else {
                mesInicio = mesActual;
                tope = 0;
            }
            /* Ciclo que recorre los saldos intentando cubrir el monto */
            while (continuar) {
                log.debug("Buscando cubrir el monto " + Util.formatNumber(montoPorCubrir) + " con el mes " + (mesInicio - 1) + " Con saldo: " + Util.formatNumber(saldoCuenta.get(mesInicio - 1).getMontoSaldo()));
                /* Existe saldo en el mes */
                if (saldoCuenta.get(mesInicio - 1).getMontoSaldo().compareTo(new BigDecimal(0.0d)) > 0) {
                    /*
					 * Si lo que hay en el mes alcanza para cubrir el monto del
					 * pago se toma todo, se actualiza en el monto en el mapa y
					 * se continua
					 */
                    if (montoPorCubrir.compareTo(saldoCuenta.get(mesInicio - 1).getMontoSaldo()) <= 0) {
                        RelacionGastosDetalle renglon = new RelacionGastosDetalle();
                        renglon.setcMes(String.valueOf(mesInicio));
                        renglon.setmImporteMasIva(montoPorCubrir);
                        renglon.setnDocRenglon(nDocRenglon++);
                        renglon.setEP(ep);
                        saldoCuenta.get(mesInicio - 1).setMontoSaldo(saldoCuenta.get(mesInicio - 1).getMontoSaldo().subtract(montoPorCubrir));
                        montoPorCubrir = new BigDecimal(0.0d);
                        detalle.add(renglon);
                        break;
                    } else {
                        RelacionGastosDetalle renglon = new RelacionGastosDetalle();
                        renglon.setcMes(String.valueOf(mesInicio));
                        renglon.setmImporteMasIva(saldoCuenta.get(mesInicio - 1).getMontoSaldo());
                        renglon.setnDocRenglon(nDocRenglon++);
                        renglon.setEP(ep);
                        /*
						 * Si lo que hay en el saldo del mes no cubre todo lo
						 * necesario, entonces se toma todo lo que hay en el mes
						 * y se actualiza el monto por cubrir para intentar
						 * terminar en otro mes
						 */
                        montoPorCubrir = montoPorCubrir.subtract(saldoCuenta.get(mesInicio - 1).getMontoSaldo());
                        saldoCuenta.get(mesInicio - 1).setMontoSaldo(new BigDecimal(0.0d));
                        detalle.add(renglon);
                    }
                }
                /*
				 * Si es ingreso propio aumentamos el mes, en caso contrario lo
				 * decrementamos
				 */
                if (esIngresoPropio)
                    mesInicio++;
                else
                    mesInicio--;
                /*
				 * Si el mes actual alanzo el tope y aun no han conseguido todo
				 * el recurso se lanza una excepcion ya que no hay saldo
				 * suficiente en la EP para cubrir el monto
				 */
                if (mesInicio == tope)
                    throw new Exception("No se logro completar el recurso. Faltan: " + Util.formatNumber(montoPorCubrir));
            }
        }
        return detalle;
    }

    private static boolean esIngresoPropio(String ep) {
        String fuenteFinanciamiento = ep.substring(39, 40);
        return "4".equals(fuenteFinanciamiento);
    }

    public static String generaContrarecibo(Connection conn, Usuario u) throws Exception {
        String cc = StringUtils.trimToNull(u.getPropiedad("CCENTROCONTABLE").getValor());
        if (cc == null)
            throw new Exception("Se recibio CC nulo. No se puede generar CxP");
        String prefijo = StringUtils.trimToNull(ConfiguraAplicativoManager.getSystemSetting(conn, "CXP_PREFIJO"));
        if (prefijo == null)
            throw new Exception("No se ha configurado prefijo para la CxP. Notifique al administrador");
        String ef = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        String seqName = "CR-" + cc;
        CFSequenceManager seq = CFSequenceManager.getInstance(null);
        int numeroCxP = seq.nextVal(conn, seqName);
        String cxp = cc + prefijo + ef + numeroCxP;
        log.info("Se genero cxp: " + cxp);
        return cxp;
    }

    public static int insertRelacionGastosEncabezado(Connection conn, RelacionGastosEncabezado rgEnc) throws SQLException {
        StringBuilder qInsert = new StringBuilder();
        qInsert.append("INSERT INTO tRelacionGastosEncabezado(");
        qInsert.append("cEjercicio,cEsFirmaElectronica,cIdDistritoRiego,cIdEntidadContable,");
        qInsert.append("cIdEstadoRelacion,cIdGEstatal,cIdGRegional,cIdRelacion,cIdRFC,");
        qInsert.append("cIdTipoDocumento,cIdTipoRelacion,cIdUnidadAdministrativa,cInformeComision,");
        qInsert.append("cUnidadResponsableContable,fRecepcion,lContrarreciboImpreso,lSuficienciaAnualValidada,");
        qInsert.append("lSuficienciaMensualValidada,mMontoBoleto,nAcompanantes,nContieneFacturas,nEsAlimentacionBrigadistas,");
        qInsert.append("nEsCertificadoTransito,nEsComisionExtranjero,nEsComisionNacional,nFolioRELACIONGASTOS,sFirmanteEla,");
        qInsert.append("sPuestoEla,aEjercicioFiscal,ALM,c_origen,caNoAP,caNoContrarrecibo,");
        qInsert.append("capitulo,cBoletoReservacion,cCentroContable,cDescripcionPoliza,cDocumentoHaplicado,cEvento,cIdTipoFondo,cIdTipoLimiteDlls,");
        qInsert.append("cIdTipoMontoDesembolso,cIdUsuarioAprobacion,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRechazo,cIdUsuarioRevision,");
        qInsert.append("cRamo,ID_DESTINO_GASTO,ID_TIPO_CONCEPTO,ID_TIPO_MOVIMIENTO,nNumEmpleadoAut,nNumEmpleadoElab,nNumEmpleadoVoBo,");
        qInsert.append("lAplicaImpuestoCedular,mImporteBruto,mImporteMasIva,mImporteNeto,mImporteRetencion,");
        qInsert.append("nEnviadoSICOP,cMes,cnombre,cRadicado,cSubPrograma,CTAB,cTipoPoliza,cUnidadResponsable,");
        qInsert.append("nFolioCaja,nFolioCargaMasiva,nFolioPoliza,nFolioPolizaCancelacion,nIdComision,nIdComisionReloj,");
        qInsert.append("fAplicacion,fCancelacion,fProgramadaPago,fRevision,");
        qInsert.append("RFC,sFirmanteAut,sFirmanteVoBo,sPuestoAut,sPuestoVoBo,");
        qInsert.append("TIPO_OPERACION,U_LOGIN,nIdConcepto,nidprograma,cConcepto)");
        qInsert.append(" VALUES(");
        qInsert.append("?,?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,?,?,?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,?,?,?,");
        qInsert.append("?,?,?,?,?,?,");
        qInsert.append("?,?,?,?,");
        qInsert.append("?,?,?,?,?,");
        qInsert.append("?,?,?,?,?)");
        PreparedStatement ps = null;
        int resultados = 0;
        try {
            ps = conn.prepareStatement(qInsert.toString());
            int cnt = 1;
            ps.setString(cnt++, rgEnc.getcEjercicio());
            ps.setString(cnt++, rgEnc.getcEsFirmaElectronica());
            ps.setString(cnt++, rgEnc.getcIdDistritoRiego());
            ps.setString(cnt++, rgEnc.getcIdEntidadContable());
            ps.setInt(cnt++, rgEnc.getcIdEstadoRelacion());
            ps.setString(cnt++, rgEnc.getcIdGEstatal());
            ps.setString(cnt++, rgEnc.getcIdGRegional());
            ps.setString(cnt++, rgEnc.getcIdRelacion());
            ps.setString(cnt++, rgEnc.getcIdRFC());
            ps.setInt(cnt++, rgEnc.getcIdTipoDocumento());
            ps.setInt(cnt++, rgEnc.getcIdTipoRelacion());
            ps.setString(cnt++, rgEnc.getcIdUnidadAdministrativa());
            ps.setString(cnt++, rgEnc.getcInformeComision());
            ps.setString(cnt++, rgEnc.getcUnidadResponsableContable());
            ps.setDate(cnt++, java.sql.Date.valueOf(LocalDate.now()));
            ps.setString(cnt++, String.valueOf(rgEnc.getlContrarreciboImpreso()));
            ps.setString(cnt++, String.valueOf(rgEnc.getlSuficienciaAnualValidada()));
            ps.setString(cnt++, String.valueOf(rgEnc.getlSuficienciaMensualValidada()));
            ps.setBigDecimal(cnt++, rgEnc.getmMontoBoleto());
            ps.setInt(cnt++, rgEnc.getnAcompanantes());
            ps.setInt(cnt++, rgEnc.getnContieneFacturas());
            ps.setInt(cnt++, rgEnc.getnEsAlimentacionBrigadistas());
            ps.setInt(cnt++, rgEnc.getnEsCertificadoTransito());
            ps.setInt(cnt++, rgEnc.getnEsComisionExtranjero());
            ps.setInt(cnt++, rgEnc.getnEsComisionNacional());
            ps.setInt(cnt++, rgEnc.getnFolioRELACIONGASTOS());
            ps.setString(cnt++, rgEnc.getsFirmanteEla());
            ps.setString(cnt++, rgEnc.getsPuestoEla());
            ps.setString(cnt++, rgEnc.getaEjercicioFiscal());
            ps.setString(cnt++, rgEnc.getALM());
            ps.setString(cnt++, rgEnc.getC_origen());
            ps.setString(cnt++, rgEnc.getCaNoAP());
            ps.setString(cnt++, rgEnc.getCaNoContrarrecibo());
            ps.setString(cnt++, rgEnc.getCapitulo());
            ps.setString(cnt++, rgEnc.getcBoletoReservacion());
            ps.setString(cnt++, rgEnc.getcCentroContable());
            ps.setString(cnt++, rgEnc.getcDescripcionPoliza());
            ps.setString(cnt++, String.valueOf(rgEnc.getcDocumentoHaplicado()));
            ps.setString(cnt++, rgEnc.getcEvento());
            ps.setString(cnt++, rgEnc.getcIdTipoFondo());
            ps.setString(cnt++, rgEnc.getcIdTipoLimiteDlls());
            ps.setString(cnt++, String.valueOf(rgEnc.getcIdTipoMontoDesembolso()));
            ps.setString(cnt++, rgEnc.getcIdUsuarioAprobacion());
            ps.setString(cnt++, rgEnc.getcIdUsuarioCaptura());
            ps.setString(cnt++, rgEnc.getcIdUsuarioImpresion());
            ps.setString(cnt++, rgEnc.getcIdUsuarioRechazo());
            ps.setString(cnt++, rgEnc.getcIdUsuarioRevision());
            ps.setString(cnt++, rgEnc.getcRamo());
            ps.setString(cnt++, rgEnc.getID_DESTINO_GASTO());
            ps.setString(cnt++, rgEnc.getID_TIPO_CONCEPTO());
            ps.setString(cnt++, rgEnc.getID_TIPO_MOVIMIENTO());
            ps.setInt(cnt++, rgEnc.getnNumEmpleadoAut());
            ps.setInt(cnt++, rgEnc.getnNumEmpleadoElab());
            ps.setInt(cnt++, rgEnc.getnNumEmpleadoVoBo());
            ps.setString(cnt++, rgEnc.getlAplicaImpuestoCedular());
            ps.setBigDecimal(cnt++, rgEnc.getmImporteBruto());
            ps.setBigDecimal(cnt++, rgEnc.getmImporteMasIva());
            ps.setBigDecimal(cnt++, rgEnc.getmImporteNeto());
            ps.setBigDecimal(cnt++, rgEnc.getmImporteRetencion());
            ps.setInt(cnt++, rgEnc.getnEnviadoSICOP());
            ps.setString(cnt++, rgEnc.getcMes());
            ps.setString(cnt++, rgEnc.getCnombre());
            ps.setString(cnt++, String.valueOf(rgEnc.getcRadicado()));
            ps.setString(cnt++, rgEnc.getcSubPrograma());
            ps.setString(cnt++, rgEnc.getCTAB());
            ps.setString(cnt++, rgEnc.getcTipoPoliza());
            ps.setString(cnt++, rgEnc.getcUnidadResponsable());
            ps.setInt(cnt++, rgEnc.getnFolioCaja());
            ps.setInt(cnt++, rgEnc.getnFolioCargaMasiva());
            ps.setInt(cnt++, rgEnc.getnFolioPoliza());
            ps.setInt(cnt++, rgEnc.getnFolioPolizaCancelacion());
            ps.setInt(cnt++, rgEnc.getnIdComision());
            ps.setInt(cnt++, rgEnc.getnIdComisionReloj());
            ps.setDate(cnt++, java.sql.Date.valueOf(LocalDate.now()));
            if (rgEnc.getfCancelacion() != null)
                ps.setDate(cnt++, new java.sql.Date(rgEnc.getfCancelacion().getTime()));
            else
                ps.setNull(cnt++, Types.DATE);
            if (rgEnc.getfProgramadaPago() != null)
                ps.setDate(cnt++, new java.sql.Date(rgEnc.getfProgramadaPago().getTime()));
            else
                ps.setNull(cnt++, Types.DATE);
            if (rgEnc.getfRevision() != null)
                ps.setDate(cnt++, new java.sql.Date(rgEnc.getfRevision().getTime()));
            else
                ps.setNull(cnt++, Types.DATE);
            ps.setString(cnt++, rgEnc.getRFC());
            ps.setString(cnt++, rgEnc.getsFirmanteAut());
            ps.setString(cnt++, rgEnc.getsFirmanteVoBo());
            ps.setString(cnt++, rgEnc.getsPuestoAut());
            ps.setString(cnt++, rgEnc.getsPuestoVoBo());
            ps.setString(cnt++, rgEnc.getTIPO_OPERACION());
            ps.setString(cnt++, rgEnc.getU_LOGIN());
            ps.setInt(cnt++, rgEnc.getnIdConcepto());
            ps.setString(cnt++, rgEnc.getNidprograma());
            ps.setString(cnt++, rgEnc.getcConcepto());
            log.debug(rgEnc.toString());
            resultados = ps.executeUpdate();
            return resultados;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaRelacionGastosDetalle(Connection conn, List<RelacionGastosDetalle> detalle) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO trelaciongastosdetalle(aEjercicioFiscal, ALM, altaAlmacen, cCentroContable, ");
        query.append("cEjercicio, cEvento, cIdCuentaContable, cIdEntidadContable, cIdRelacion, ");
        query.append("cMes, cPasivo, CTAB, cTipoPoliza, EP, FFM, ID_TIPO_CONCEPTO, ID_TIPO_MOVIMIENTO, m23IVA, ");
        query.append("m2Millar, m5Millar, mAmortizacionAnticipo, mBruto, mCedular, mCNIC, mComprometido, ");
        query.append("mDevolucion, mFletes, mIMDT, mImporte, mImporteAmortiza, mImporteBruto, mImporteFlete23, ");
        query.append("mImporteFlete4, mImporteHospedaje, mImporteISRLaudos, mImporteIva, mImporteIvaArrenda, ");
        query.append("mImporteIvaHonorarios, mImporteIvaProv, mImporteMasIva, mImporteNegativo, mImporteNeto, ");
        query.append("mImporteObra, mISRArrenda, mISRHonorarios, mISROtros, mIVA, mNeto, mObra5, mPenalizacion, ");
        query.append("mRetencion, mRetImpuestoCedular, mSancion, mTesofe, nCapitulo, nDocRenglon, nFolioPoliza, ");
        query.append("nFolioRELACIONGASTOS, nMes, nPoliza, OBGT, RFC, mimporteISRResico)");
        query.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
        query.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
        query.append("?, ?, ?, ?, ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            int cnt = 1;
            int insertados = 0;
            for (RelacionGastosDetalle renglon : detalle) {
                ps.setString(cnt++, renglon.getaEjercicioFiscal());
                ps.setString(cnt++, renglon.getALM());
                ps.setString(cnt++, renglon.getAltaAlmacen());
                ps.setString(cnt++, renglon.getcCentroContable());
                ps.setString(cnt++, renglon.getcEjercicio());
                ps.setString(cnt++, renglon.getcEvento());
                ps.setString(cnt++, renglon.getcIdCuentaContable());
                ps.setString(cnt++, renglon.getcIdEntidadContable());
                ps.setString(cnt++, renglon.getcIdRelacion());
                ps.setString(cnt++, renglon.getcMes());
                ps.setString(cnt++, renglon.getcPasivo());
                ps.setString(cnt++, renglon.getCTAB());
                ps.setString(cnt++, renglon.getcTipoPoliza());
                ps.setString(cnt++, renglon.getEP());
                ps.setString(cnt++, renglon.getFFM());
                ps.setString(cnt++, renglon.getID_TIPO_CONCEPTO());
                ps.setString(cnt++, renglon.getID_TIPO_MOVIMIENTO());
                ps.setString(cnt++, renglon.getM23IVA());
                ps.setString(cnt++, renglon.getM2Millar());
                ps.setString(cnt++, renglon.getM5Millar());
                ps.setString(cnt++, renglon.getmAmortizacionAnticipo());
                ps.setString(cnt++, renglon.getmBruto());
                ps.setString(cnt++, renglon.getmCedular());
                ps.setBigDecimal(cnt++, renglon.getmCNIC());
                ps.setBigDecimal(cnt++, renglon.getmComprometido());
                ps.setString(cnt++, renglon.getmDevolucion());
                ps.setString(cnt++, renglon.getmFletes());
                ps.setBigDecimal(cnt++, renglon.getmIMDT());
                ps.setString(cnt++, renglon.getmImporte());
                ps.setString(cnt++, renglon.getmImporteAmortiza());
                ps.setBigDecimal(cnt++, renglon.getmImporteBruto());
                ps.setBigDecimal(cnt++, renglon.getmImporteFlete23());
                ps.setString(cnt++, renglon.getmImporteFlete4());
                ps.setBigDecimal(cnt++, renglon.getmImporteHospedaje());
                ps.setBigDecimal(cnt++, renglon.getmImporteISRLaudos());
                ps.setBigDecimal(cnt++, renglon.getmImporteIva());
                ps.setBigDecimal(cnt++, renglon.getmImporteIvaArrenda());
                ps.setBigDecimal(cnt++, renglon.getmImporteIvaHonorarios());
                ps.setBigDecimal(cnt++, renglon.getmImporteIvaProv());
                ps.setBigDecimal(cnt++, renglon.getmImporteMasIva());
                ps.setBigDecimal(cnt++, renglon.getmImporteNegativo());
                ps.setBigDecimal(cnt++, renglon.getmImporteNeto());
                ps.setBigDecimal(cnt++, renglon.getmImporteObra());
                ps.setString(cnt++, renglon.getmISRArrenda());
                ps.setString(cnt++, renglon.getmISRHonorarios());
                ps.setBigDecimal(cnt++, renglon.getmISROtros());
                ps.setString(cnt++, renglon.getmIVA());
                ps.setString(cnt++, renglon.getmNeto());
                ps.setString(cnt++, renglon.getmObra5());
                ps.setString(cnt++, renglon.getmPenalizacion());
                ps.setString(cnt++, renglon.getmRetencion());
                ps.setString(cnt++, renglon.getmRetImpuestoCedular());
                ps.setString(cnt++, renglon.getmSancion());
                ps.setBigDecimal(cnt++, renglon.getmTesofe());
                ps.setString(cnt++, renglon.getnCapitulo());
                ps.setInt(cnt++, renglon.getnDocRenglon());
                ps.setInt(cnt++, renglon.getnFolioPoliza());
                ps.setInt(cnt++, renglon.getnFolioRELACIONGASTOS());
                ps.setInt(cnt++, renglon.getnMes());
                ps.setInt(cnt++, renglon.getnPoliza());
                ps.setString(cnt++, renglon.getOBGT());
                ps.setString(cnt++, renglon.getRFC());
                ps.setBigDecimal(cnt++, renglon.getmISRResico());
                insertados = ps.executeUpdate();
                cnt = 1;
            }
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaPagoApartado(Connection conn, EgresoEncabezado encabezado) throws Exception {
        StringBuilder queryInsertaEnc = new StringBuilder();
        queryInsertaEnc.append(" INSERT INTO tPagoApartadoEncabezado ( nFolioPagoApartado ,   cTipoPago ,   nFolioPago ,   fAplicacion ,   cRamo ,   ");
        queryInsertaEnc.append(" caNoContrarrecibo ,   aEjercicioFiscal ,   cDocumentoHaplicado ,   nFolioPoliza ,   cTipoPoliza ,   cUnidadResponsableContable ,   ");
        queryInsertaEnc.append(" nFolioPolizaCancelacion ,   fCancelacion ,   cDescripcionPoliza ) ");
        queryInsertaEnc.append(" SELECT	? AS nFolioPagoApartado, 'RELACIONGASTOS' AS cTipoPago , ");
        queryInsertaEnc.append(" nFolioRELACIONGASTOS AS nFolioPago ,	fAplicacion , cRamo, caNoContrarrecibo,	aEjercicioFiscal,	NULL AS cDocumentoHaplicado ,	");
        queryInsertaEnc.append(" NULL  AS nFolioPoliza , 'PR' AS cTipoPoliza, cUnidadResponsableContable, NULL AS nFolioPolizaCancelacion , NULL AS fCancelacion ,	");
        queryInsertaEnc.append(" 'Apartado del pago: ' + caNoContrarrecibo AS cDescripcionPoliza		");
        queryInsertaEnc.append(" FROM trelaciongastosencabezado WITH(NOLOCK)		WHERE nfoliorelaciongastos = ?");
        StringBuilder queryInsertDet = new StringBuilder();
        queryInsertDet.append(" INSERT INTO tPagoApartadoDetalle ( nFolioPagoApartado ,   nDocRenglon ,   cMes ,   cEvento ,   cEjercicio ,   cCentroContable ,   EP,   mImporte )");
        queryInsertDet.append(" SELECT	? ASnFolioPagoApartado, nDocRenglon, cMes ,	'APARTADO' AS cEvento ,	cEjercicio, cCentroContable, EP, mImporteMasIva AS mImporte  ");
        queryInsertDet.append(" FROM	tRELACIONGASTOSDetalle detalle WITH(NOLOCK) WHERE	nFolioRELACIONGASTOS = ?");
        PreparedStatement psEncabezado = null;
        PreparedStatement psDetalle = null;
        try {
            CFSequenceManager seq = CFSequenceManager.getInstance();
            int insertados = 0;
            int nFolioPagoApartado = seq.nextVal(conn, "APARTADO");
            psEncabezado = conn.prepareStatement(queryInsertaEnc.toString());
            psDetalle = conn.prepareStatement(queryInsertDet.toString());
            psEncabezado.setInt(1, nFolioPagoApartado);
            psEncabezado.setInt(2, encabezado.getFolioPago());
            psDetalle.setInt(1, nFolioPagoApartado);
            psDetalle.setInt(2, encabezado.getFolioPago());
            insertados = psEncabezado.executeUpdate();
            insertados += psDetalle.executeUpdate();
            log.debug("Se insertaron " + insertados + "registros");
            return nFolioPagoApartado;
        } finally {
            CloseObject.closeObject(psEncabezado);
            CloseObject.closeObject(psDetalle);
        }
    }

    public static int insertaPagoApartado(Connection conn, RelacionGastosEncabezado encabezado) throws Exception {
        StringBuilder queryInsertaEnc = new StringBuilder();
        queryInsertaEnc.append(" INSERT INTO tPagoApartadoEncabezado ( nFolioPagoApartado ,   cTipoPago ,   nFolioPago ,   fAplicacion ,   cRamo ,   ");
        queryInsertaEnc.append(" caNoContrarrecibo ,   aEjercicioFiscal ,   cDocumentoHaplicado ,   nFolioPoliza ,   cTipoPoliza ,   cUnidadResponsableContable ,   ");
        queryInsertaEnc.append(" nFolioPolizaCancelacion ,   fCancelacion ,   cDescripcionPoliza ) ");
        queryInsertaEnc.append(" SELECT	? AS nFolioPagoApartado, 'RELACIONGASTOS' AS cTipoPago , ");
        queryInsertaEnc.append(" nFolioRELACIONGASTOS AS nFolioPago ,	fAplicacion , cRamo, caNoContrarrecibo,	aEjercicioFiscal,	NULL AS cDocumentoHaplicado ,	");
        queryInsertaEnc.append(" NULL  AS nFolioPoliza , 'PR' AS cTipoPoliza, cUnidadResponsableContable, NULL AS nFolioPolizaCancelacion , NULL AS fCancelacion ,	");
        queryInsertaEnc.append(" 'Apartado del pago: ' + caNoContrarrecibo AS cDescripcionPoliza		");
        queryInsertaEnc.append(" FROM trelaciongastosencabezado WITH(NOLOCK)		WHERE nfoliorelaciongastos = ?");
        StringBuilder queryInsertDet = new StringBuilder();
        queryInsertDet.append(" INSERT INTO tPagoApartadoDetalle ( nFolioPagoApartado ,   nDocRenglon ,   cMes ,   cEvento ,   cEjercicio ,   cCentroContable ,   EP,   mImporte )");
        queryInsertDet.append(" SELECT	? ASnFolioPagoApartado, nDocRenglon, cMes ,	'APARTADO' AS cEvento ,	cEjercicio, cCentroContable, EP, mImporteMasIva AS mImporte  ");
        queryInsertDet.append(" FROM	tRELACIONGASTOSDetalle detalle WITH(NOLOCK) WHERE	nFolioRELACIONGASTOS = ?");
        PreparedStatement psEncabezado = null;
        PreparedStatement psDetalle = null;
        try {
            CFSequenceManager seq = CFSequenceManager.getInstance();
            int insertados = 0;
            int nFolioPagoApartado = seq.nextVal(conn, "APARTADO");
            psEncabezado = conn.prepareStatement(queryInsertaEnc.toString());
            psDetalle = conn.prepareStatement(queryInsertDet.toString());
            psEncabezado.setInt(1, nFolioPagoApartado);
            psEncabezado.setInt(2, encabezado.getnFolioRELACIONGASTOS());
            psDetalle.setInt(1, nFolioPagoApartado);
            psDetalle.setInt(2, encabezado.getnFolioRELACIONGASTOS());
            insertados = psEncabezado.executeUpdate();
            insertados += psDetalle.executeUpdate();
            log.debug("Se insertaron " + insertados + "registros");
            return nFolioPagoApartado;
        } finally {
            CloseObject.closeObject(psEncabezado);
            CloseObject.closeObject(psDetalle);
        }
    }

    public static RelacionGastosEncabezado seleccionaRG(Connection conn, Integer folioRG) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT  *   FROM	tRELACIONGASTOSEncabezado encabezado  WHERE	encabezado.nFolioRELACIONGASTOS = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folioRG);
            rs = ps.executeQuery();
            Map<String, String> values = RSToTable.rsToMap(rs);
            RelacionGastosEncabezado rgEncabezado = new RelacionGastosEncabezado();
            DateConverter converter = new DateConverter(null);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(rgEncabezado, values);
            return rgEncabezado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String buscaIntegradora(Connection conn, String caNoCompromiso) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String integradora = null;
        String query = "SELECT  cIdContrato FROM tCompromisoEncabezado  (NOLOCK) WHERE	caNoCompromiso = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, caNoCompromiso);
            rs = ps.executeQuery();
            if (rs.next())
                integradora = rs.getString(1);
            return integradora;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static File generaDocComp(Connection conn, String Integradora) throws Exception {
        String nombreArchivo = "DC" + "_" + Integradora + ".csv";
        File archivoLayout = new File(new File(System.getProperty("java.io.tmpdir")), nombreArchivo);
        String encabezadoLayout = null;
        PrintWriter out = null;
        StringBuilder queryEncabezadoLayout = new StringBuilder();
        queryEncabezadoLayout.append(" SELECT nFolioConsolidacion, 'H' H, ");
        queryEncabezadoLayout.append(" cRamo, ");
        queryEncabezadoLayout.append(" 'RHQ' UR, ");
        queryEncabezadoLayout.append(" '' SOL_PAGO, ");
        queryEncabezadoLayout.append(" '3' NO_SOL, ");
        queryEncabezadoLayout.append(" nIdIntegracion FOLIO_INTERNO, ");
        queryEncabezadoLayout.append(" nIdIntegracion COMODIN ");
        queryEncabezadoLayout.append(" FROM dbo.tconsolidacionrelaciongastosEncabezado WITH(NOLOCK) ");
        queryEncabezadoLayout.append(" WHERE nIdIntegracion = ? ");
        PreparedStatement psHeader = null;
        ResultSet rsHeader = null;
        try {
            log.debug(queryEncabezadoLayout);
            psHeader = conn.prepareStatement(queryEncabezadoLayout.toString());
            psHeader.setString(1, Integradora);
            rsHeader = psHeader.executeQuery();
            if (rsHeader.next())
                encabezadoLayout = Util.resultSetToConcatenateString(rsHeader, ",", 1);
            else
                throw new Exception("No fue posible armar el encabezado del layout ya que no se retornaron registros de informacion");
            String detalleLayout = generaDetalleDC(conn, Integradora);
            out = new PrintWriter(archivoLayout);
            out.println(encabezadoLayout);
            out.print(detalleLayout);
            out.flush();
            out.close();
            out = null;
            return archivoLayout;
        } catch (Exception c) {
            throw new Exception("No se encontro informacion para armar el detalle con folio de compromiso :" + Integradora);
        } finally {
            CloseObject.closeObject(rsHeader);
            CloseObject.closeObject(psHeader);
        }
    }

    private static String generaDetalleDC(Connection conn, String Integradora) throws Exception {
        StringBuilder queryLayoutDetalle = new StringBuilder();
        queryLayoutDetalle.append("	SELECT DISTINCT PDE.cramo, ");
        queryLayoutDetalle.append(" Replace(Replace(pde.nIdIntegracion, ',', ''), '\"', '') INTEGRACION, ");
        queryLayoutDetalle.append(" CONVERT(NVARCHAR(10), pde.fAplicacion, 103) FECHA ,");
        queryLayoutDetalle.append(" CONVERT(NVARCHAR(10), pde.fAplicacion, 103) + ' 12:00:00 a.m.' FECHAAPL, ");
        queryLayoutDetalle.append(" 'S04929' DCD_CBEN,");
        queryLayoutDetalle.append(" '04'   'TipoBen',");
        queryLayoutDetalle.append(" '85' AS dcd_tipo_ope,");
        queryLayoutDetalle.append(" '07' AS TIVA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), 0.00) TASA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), (select sum(mimportemasiva) from tconsolidacionrelaciongastosdetalle");
        queryLayoutDetalle.append(" where nFolioConsolidacion = pde.nfolioconsolidacion) + sum(DCD.mISR) + sum(DCD.mIVA) - sum(DCD.mIVADes)) BRUTO,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mIvaDes) )                     IVA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mIVA + DCD.mContribucion))  RETIVA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mISR))                ISR,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mMil5))               R5MILLAR,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mMil2))               R2MILLAS,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum (DCD.mOtrasRet) )          OTRASRET,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mPenalizacion))       PENALIZA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), 0)		                 CONTRIB,");
        queryLayoutDetalle.append(" 0.00 AS dcd_ctoext,");
        queryLayoutDetalle.append(" Replace(Replace(pde.nIdIntegracion, ',', ''), '\"', '') ");
        queryLayoutDetalle.append(" FROM   dbo.tconsolidacionrelaciongastosEncabezado PDE");
        queryLayoutDetalle.append("        INNER JOIN dbo.tLayoutsCreadosRelacionGastosHeader DCD");
        queryLayoutDetalle.append("                ON sAuxiliarComodin = nIdIntegracion   ");
        queryLayoutDetalle.append(" WHERE  nIdIntegracion = '" + Integradora + "'");
        queryLayoutDetalle.append(" group by PDE.cramo, pde.nIdIntegracion, pde.fAplicacion, pde.nfolioconsolidacion ");
        PreparedStatement psLayoutDetalle = null;
        ResultSet rsLayoutDetalle = null;
        try {
            log.debug("Generando Detalle Integracion (LAYOUT) Query: " + queryLayoutDetalle);
            psLayoutDetalle = conn.prepareStatement(queryLayoutDetalle.toString());
            rsLayoutDetalle = psLayoutDetalle.executeQuery();
            if (rsLayoutDetalle.next()) {
                String detalle = Util.resultSetToConcatenateString(rsLayoutDetalle, ",", 0);
                log.debug("Detalle para DC de la integracion " + Integradora + "\n" + detalle);
                return detalle;
            } else
                throw new Exception("No se encontro informacion para armar el detalle con folio de compromiso :" + Integradora);
        } finally {
            CloseObject.closeObject(psLayoutDetalle);
            CloseObject.closeObject(rsLayoutDetalle);
        }
    }

    public static List<RelacionGastosEncabezado> getIntegradasResumen(Connection conn, int folioIntegracion) throws Exception {
        List<RelacionGastosEncabezado> rg = new ArrayList<RelacionGastosEncabezado>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT	caNoContrarrecibo,");
        query.append("      	cIdRFC, ");
        query.append("      	cnombre, ");
        query.append("      	cConcepto, ");
        query.append("      	mImporteMasIva, ");
        query.append("      	nFolioRelacionGastos ");
        query.append("  FROM	tRELACIONGASTOSEncabezado WITH(nolock) ");
        query.append(" WHERE	nFolioCargaMasiva = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioIntegracion);
            rs = ps.executeQuery();
            while (rs.next()) {
                RelacionGastosEncabezado rgAux = new RelacionGastosEncabezado();
                rgAux.setCaNoContrarrecibo(rs.getString("caNoContrarrecibo"));
                rgAux.setcIdRFC(rs.getString("cIdRFC"));
                rgAux.setCnombre(rs.getString("cnombre"));
                rgAux.setcConcepto(rs.getString("cConcepto"));
                rgAux.setmImporteMasIva(rs.getBigDecimal("mImporteMasIva"));
                rgAux.setnFolioRELACIONGASTOS(rs.getInt("nFolioRelacionGastos"));
                rgAux.setFolioApartado(getFolioApartado(conn, rgAux.getnFolioRELACIONGASTOS()));
                rg.add(rgAux);
            }
            return rg;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int getFolioApartado(Connection conn, int folioRG) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	nFolioPagoApartado");
        query.append("  FROM	tPagoApartadoEncabezado WITH(nolock) ");
        query.append(" WHERE	cTipoPago = 'RELACIONGASTOS'");
        query.append("   AND	nFolioPago = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioRG);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else
                return -1;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void cancelaRelacionGastos(Connection conn, AccountingEngine accEng, RelacionGastosEncabezado rg) throws AccountingEngineException, Exception {
        accEng.cancelAccountingApplication(conn, "RELACIONGASTOS", String.valueOf(rg.getnFolioRELACIONGASTOS()), "tRelacionGastosEncabezado", "tRelacionGastosDetalle", "nFolioRelacionGastos", Util.getTodayMC());
        if (rg.getFolioApartado() > 0)
            accEng.cancelAccountingApplication(conn, "PAGOAPARTADO", String.valueOf(rg.getFolioApartado()), "tPagoApartadoEncabezado", "tPagoApartadoDetalle", "nFolioPagoApartado", Util.getTodayMC());
    }

    public static boolean updateEliminaInfoVuelosRG(Connection conn, int folio) throws SQLException {
        boolean respuesta = true;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement psUpdateLayoutVuelos = null;
        PreparedStatement psDeleteInfoVuelos = null;
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT	CASE ");
            query.append("      	WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS existe  ");
            query.append("  FROM	tInfoBoleto WITH (NOLOCK)  ");
            query.append(" WHERE	nFolioRelacionGastos = ? ");
            pstm = conn.prepareStatement(query.toString());
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String existe = rs.getString("existe");
                if ("1".equalsIgnoreCase(existe)) {
                    String sUpdateLayoutVuelos = " UPDATE	vuelosdet SET vuelosdet.Status = 'A' " + " FROM		tLayoutVuelosDet vuelosdet WITH (NOLOCK) " + " INNER JOIN tInfoBoleto infoboleto WITH(NOLOCK) " + " ON " + " ( " + "	infoboleto.cNumeroBoleto = vuelosdet.cReferencia " + "	AND infoboleto.RFCVuelo = vuelosdet.RFC " + "	AND infoboleto.cNombreRFC = vuelosdet.cNombre " + "	AND infoboleto.mImporteBoleto = vuelosdet.mTotal " + " ) " + " WHERE infoboleto.nFolioRelacionGastos = ? ";
                    String sDeleteInfoVuelos = " DELETE FROM tInfoBoleto WHERE nFolioRelacionGastos = ? ";
                    psUpdateLayoutVuelos = conn.prepareStatement(sUpdateLayoutVuelos);
                    psUpdateLayoutVuelos.setInt(1, folio);
                    psDeleteInfoVuelos = conn.prepareStatement(sDeleteInfoVuelos);
                    psDeleteInfoVuelos.setInt(1, folio);
                    int updateVuelos = psUpdateLayoutVuelos.executeUpdate();
                    log.info("Se Actualizaron: " + updateVuelos + " Vuelos.");
                    int deleteVuelos = psDeleteInfoVuelos.executeUpdate();
                    log.info("Se eliminaron: " + deleteVuelos + " Vuelos.");
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(psUpdateLayoutVuelos);
            CloseObject.closeObject(psDeleteInfoVuelos);
        }
        return respuesta;
    }

    public static void liberaCaja(Connection conn, Usuario usuario, int nFolioRelacionGastos) throws Exception {
        PreparedStatement pstmnCaja = null;
        ResultSet rsCaja = null;
        try {
            pstmnCaja = conn.prepareStatement("SELECT	nFolioCaja FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?");
            pstmnCaja.setInt(1, nFolioRelacionGastos);
            rsCaja = pstmnCaja.executeQuery();
            String us = usuario.getLogin();
            if (rsCaja.next()) {
                Integer folioCaja = rsCaja.getInt("nFolioCaja");
                CajaManager.borraDetalleViaticos(conn, folioCaja, nFolioRelacionGastos, us);
            }
        } finally {
            CloseObject.closeObject(pstmnCaja);
        }
    }

    public static String getLoginInicia(Connection conn, int folioMasivo) throws Exception {
        String login = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT  TOP 1 cIdUsuarioCaptura ");
        query.append("  FROM  tRELACIONGASTOSEncabezado_temp WITH(nolock) ");
        query.append(" WHERE  folioTempGral = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioMasivo);
            rs = ps.executeQuery();
            if (rs.next())
                login = rs.getString(1);
            return login;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaEstatus(Connection conn, RelacionGastosEncabezado rg, int status) throws Exception {
        String query = "UPDATE tRelacionGastosEncabezado SET nEnviadoSICOP = ? WHERE nFolioRelacionGastos = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, status);
            ps.setInt(2, rg.getnFolioRELACIONGASTOS());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static String ReporteAlimentacion(Connection conn, int nFolioCargaMasiva, Map<String, String> plantilla) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT ENC.caNoContrarrecibo, ");
        query.append("		DET.cUnidadResponsable, ");
        query.append("		DET.RFC, ");
        query.append("		cnombre, ");
        query.append("		cconcepto, ");
        query.append("		ENC.CTAB, ");
        query.append("		EP, ");
        query.append("		ENC.mimportemasiva AS mImporte, ");
        query.append("		VOBO.U_NOMBRE AS firmaVoBo, ");
        query.append("		AUT.U_NOMBRE AS firmaAut ");
        query.append("FROM dbo.trelaciongastosencabezado AS ENC WITH(NOLOCK) ");
        query.append("JOIN tRELACIONGASTOSDetalle AS DET WITH (NOLOCK) ON ENC.nFolioRELACIONGASTOS = DET.nFolioRELACIONGASTOS ");
        query.append("LEFT JOIN (SELECT nFolioDocumento, U_NOMBRE, cOperacion FROM tBitacoraFirma AS VOBO WITH (NOLOCK) JOIN CG_USUARIO AS US WITH (NOLOCK) ON VOBO.U_LOGIN = US.U_LOGIN WHERE VOBO.cTipoDocumento = 'RELACIONGASTOS') AS VOBO ON ENC.nFolioRELACIONGASTOS = VOBO.nFolioDocumento AND VOBO.cOperacion = 'VOBO' ");
        query.append("LEFT JOIN (SELECT nFolioDocumento, U_NOMBRE, cOperacion FROM tBitacoraFirma AS AUT WITH (NOLOCK) JOIN CG_USUARIO AS US WITH (NOLOCK) ON AUT.U_LOGIN = US.U_LOGIN WHERE AUT.cTipoDocumento = 'RELACIONGASTOS') AS AUT ON ENC.nFolioRELACIONGASTOS = AUT.nFolioDocumento AND AUT.cOperacion = 'AUT' ");
        query.append("WHERE nFolioCargaMasiva = ? ");
        String fileName = "";
        String mensaje = "";
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolioCargaMasiva);
            rs = ps.executeQuery();
            fileName = generaReporteAlimentacion(rs, plantilla.get("REPALIMENTACION"), mensaje, nFolioCargaMasiva);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporteAlimentacion(ResultSet rs, String plantilla, String mensaje, int nFolioCargaMasiva) throws Exception {
        File cFileExcelPlantilla = new File(plantilla);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteAlimentacionBrigadistas" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int RowIni = 9;
        int cnt = 0;
        String folioMasivo = null;
        double total = 0;
        folioMasivo = "Folio Carga Masiva: " + nFolioCargaMasiva;
        Row rwEnc1 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell1 = (rwEnc1.getCell(1) == null ? rwEnc1.createCell(1) : rwEnc1.getCell(1));
        cell1.setCellValue(folioMasivo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            total = total + Double.parseDouble(rs.getString(8));
            Row rw = (sheet0.getRow(RowIni + cnt) == null ? sheet0.createRow(RowIni + cnt) : sheet0.getRow(RowIni + cnt));
            for (int i = 1; i < rsMetadata.getColumnCount() + 1; i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloTabla);
            }
            cnt++;
        }
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(7) == null ? rwEnc2.createCell(7) : rwEnc2.getCell(7));
        cell2.setCellValue("Total de la Carga: " + total);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        workbook.close();
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static int actualizaSICOPCargaMasiva(Connection conn, int nFolioCargaMasiva) throws SQLException {
        PreparedStatement ps = null;
        String queryActualizaSICOP = "UPDATE tRELACIONGASTOSEncabezado SET nEnviadoSICOP = 2 WHERE nFolioCargaMasiva = " + nFolioCargaMasiva;
        int afectados = 0;
        try {
            ps = conn.prepareStatement(queryActualizaSICOP);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static ResultSet generaLayoutDetalleRG(Connection conn, String foliosRG) throws Exception {
        ResultSet rs = null;
        PreparedStatement pstmntD = null;
        StringBuilder Sql2 = new StringBuilder();
        Sql2.append(" SELECT '1'   ID_EVENTO, '24.0.001'  EVENTO, Substring(D.EP, 6, 2)  ID_RAMO_ML, 'RHQ', Substring(D.EP, 1, 4)  aEjercicioFiscal, ");
        Sql2.append("       Substring(D.EP, 13, 1) cGrupoFuncional, 	Substring(D.EP, 15, 1) cFuncion, Substring(D.EP, 17, 2) cSubFuncion, ");
        Sql2.append("       CASE  WHEN Substring(D.EP, 20, 2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00'  ELSE Substring(D.EP, 20, 2)  END AS cProgramaGeneral, ");
        Sql2.append("       Substring(D.EP, 23, 3) cActividadInstitucional,  Substring(D.EP, 27, 4) cProgramaPresupuestario, Substring(D.EP, 32, 1) CCAP_157, ");
        Sql2.append("       Substring(D.EP, 33, 1) CCON_158, Substring(D.EP, 34, 1) CPARG_300,   Substring(D.EP, 35, 2) CPAR_159, ");
        Sql2.append("       Substring(D.EP, 38, 1) cTipoGasto, ");
        Sql2.append("       Substring(D.EP, 40, 1) cFuenteFinanciamiento, ");
        Sql2.append("       Substring(D.EP, 42, 2) cEntidadFederativa, ");
        Sql2.append("		 SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11) AS cCartera, ");
        Sql2.append("       '0000000000', ");
        Sql2.append("       '00'                   CCOP_163, ");
        Sql2.append("       '000'                  PL, ");
        Sql2.append("       '000'                  OFI, ");
        Sql2.append("       '00000'                AUX1, ");
        Sql2.append("       '00000'                AUX2, ");
        Sql2.append("       '0000000000'           AUX3, ");
        Sql2.append("       CONVERT(DECIMAL(17, 2),  Sum(D.mImporteNeto))     AS MONTO, ");
        Sql2.append("       Month(Getdate())       MES_149, ");
        Sql2.append("       '0'                    NRES, ");
        Sql2.append("       CASE  WHEN Substring(ep, 32, 5) = '35801' THEN 'GD' ELSE 'PN' END  TIPO_CONTRATO, ");
        Sql2.append("       '000'                  CONC_MOV, ");
        Sql2.append("       CONVERT(decimal(17, 2),sum(isnull(misrHonorarios,0) + isnull(mISRArrenda,0) + isnull(mImporteISRLaudos,0) + isnull(mISROtros,0) +  isnull(mimporteISRResico,0)),0)  AS RETENCIONES, ");
        Sql2.append("       CONVERT(decimal(17, 2),sum(isnull(mImporteIva6,0) + isnull(mImporteIvaArrenda,0)+ isnull(mImporteIvaHonorarios,0) +isnull(mImporteFlete23,0)+ isnull(mImporteFlete4,0)),0),    ");
        Sql2.append(" 		 SUM( isnull(mObra5,0) + isnull(mImporteObra,0)  )AS DCD_MIL5, ");
        Sql2.append("		 SUM( convert( decimal(17,2),m2Millar,0) )AS DCD_MIL2, ");
        Sql2.append("		 SUM(  convert( decimal(17,2),mRetImpuestoCedular,0)   )AS DCD_OTRAS_RET,");
        Sql2.append("		 0,        0,         ''   id_ctr_intdet");
        Sql2.append("	FROM   tRELACIONGASTOSDetalle D WITH (NOLOCK) ");
        Sql2.append("	WHERE  d.nFolioRELACIONGASTOS in (" + foliosRG + ") ");
        Sql2.append("	GROUP  BY Substring(D.EP, 61, 3),  D.EP,  mISRHonorarios,  D.ID_TIPO_CONCEPTO,  SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11)  ");
        pstmntD = conn.prepareStatement(Sql2.toString());
        rs = pstmntD.executeQuery();
        return rs;
    }

    public static int actualizaSICOPRechazo(Connection conn, int nFolioCargaMasiva) throws Exception {
        PreparedStatement ps = null;
        String queryActualizaSICOP = "UPDATE tRELACIONGASTOSEncabezado SET nEnviadoSICOP = -4 WHERE nFolioCargaMasiva = " + nFolioCargaMasiva + " AND nEnviadoSICOP > -4 ";
        int afectados = 0;
        try {
            ps = conn.prepareStatement(queryActualizaSICOP);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void saveCargaMasivaRG(Connection conn, CargaMasivaRG cargaMasivaRG) throws SQLException {
        log.info("Insertando carga masiva: " + cargaMasivaRG);
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder("INSERT INTO tRelacionGastosCargaMasiva(nFolioCargaMasiva, nIDEstatusCarga)VALUES(?,?)");
        log.trace("Consulta: " + query);
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cargaMasivaRG.getFolioCargaMasiva());
            ps.setInt(2, cargaMasivaRG.getEstatusCarga());
            log.trace("Se ejecutara consulta para insertar objeto: " + cargaMasivaRG);
            int inserted = ps.executeUpdate();
            log.debug("Se insertaron " + inserted + " registros en tRelacionGastosCargaMasiva");
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void updateCargaMasivaRG(Connection conn, CargaMasivaRG cargaMasivaRG) throws SQLException {
        log.info("Actualizando carga masiva: " + cargaMasivaRG);
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder("UPDATE tRelacionGastosCargaMasiva WITH(ROWLOCK) SET nIDEstatusCarga = ? WHERE nFolioCargaMasiva = ?");
        log.trace("Consulta: " + query);
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cargaMasivaRG.getEstatusCarga());
            ps.setInt(2, cargaMasivaRG.getFolioCargaMasiva());
            log.trace("Se ejecutara consulta para actualizar objeto: " + cargaMasivaRG);
            int inserted = ps.executeUpdate();
            log.debug("Se actualizaron: " + inserted + " registros en tRelacionGastosCargaMasiva");
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static List<MasiveOperation> readRGMasiva(Connection conn, int folioGeneral) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT	nFolioRELACIONGASTOS, ");
        sbQuery.append("     	cCentroContable, ");
        sbQuery.append("     	aEjercicioFiscal, ");
        sbQuery.append("     	cRadicado,  ");
        sbQuery.append("     	nTipoCarga  ");
        sbQuery.append("  FROM	tRELACIONGASTOSEncabezado_temp WITH(NOLOCK)  ");
        sbQuery.append(" WHERE	folioTempGral = ? ");
        List<MasiveOperation> listRG = new ArrayList<MasiveOperation>();
        try {
            ps = conn.prepareStatement(sbQuery.toString());
            ps.setInt(1, folioGeneral);
            rs = ps.executeQuery();
            while (rs.next()) {
                RGMasiva rg = new RGMasiva(rs);
                listRG.add(rg);
            }
            return listRG;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<?> readRGMasivaAplicada(Connection conn, int folioGeneral) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT aEjercicioFiscal     AS ejercicioFiscal, ");
        query.append("		 cCentroContable      AS centroContable, ");
        query.append("		 caNoContrarrecibo    AS contrarecibo, ");
        query.append("		 cRadicado            AS radicado, ");
        query.append("		 -1                   AS folioTramiteTemporal, ");
        query.append("		 nFolioRelacionGastos AS folioTramite, ");
        query.append("		'RELACIONGASTOS'  AS application ");
        query.append("  FROM	tRELACIONGASTOSEncabezado WITH(nolock)  ");
        query.append(" WHERE	nFolioCargaMasiva = ? ");
        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<RGMasiva>> h = new BeanListHandler<RGMasiva>(RGMasiva.class);
        List<RGMasiva> rgMasivaList = null;
        rgMasivaList = run.query(conn, query.toString(), h, Integer.valueOf(folioGeneral));
        return rgMasivaList;
    }

    public static boolean esGreenMex(Connection conn, int nFolio) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        boolean resultado = true;
        int id = 0;
        try {
            String query = "SELECT COUNT(*) AS id FROM tEstadoDeCuentaGreenMexDetalle WHERE nFolioComprobacion = ?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next())
                id = rs.getInt("id");
            if (id == 0) {
                resultado = false;
            }
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
        return resultado;
    }

    public static String esPagoCuotasLAUDOS(Connection conn, int nFolio) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String cEsPagoCuotas = "";
        log.info("Buscando el RFC de para cuotas de laudos en caso de tener el regsitro");
        try {
            String queryesPagoCuotas = "SELECT cEsPagoCuotas FROM tComprobacionLaudos WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?";
            pstmnt = conn.prepareStatement(queryesPagoCuotas);
            pstmnt.setInt(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next())
                cEsPagoCuotas = rs.getString("cEsPagoCuotas");
            return cEsPagoCuotas;
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static void updateRFCCuotas(Connection conn, int nFolio) throws SQLException {
        log.info("Actualizando RFC de Cuotas");
        PreparedStatement psEnc = null, psDet = null;
        StringBuilder queryEnc = new StringBuilder("");
        StringBuilder queryDet = new StringBuilder("");
        queryEnc.append("UPDATE ENC SET cIdRFC = cRFCCuotas, RFC = cRFCCuotas, cnombre = NOMBRE ");
        queryEnc.append("FROM tRELACIONGASTOSEncabezado ENC WITH (NOLOCK) ");
        queryEnc.append("LEFT JOIN tComprobacionLaudos COMP WITH (NOLOCK) ON ENC.nFolioRELACIONGASTOS = COMP.nFolioRELACIONGASTOS ");
        queryEnc.append("LEFT JOIN V_CATALOGORFC_BENEFICIARIOLAUDOS V WITH (NOLOCK) ON cRFCCuotas = dRFC_Cuotas ");
        queryEnc.append("WHERE ENC.nFolioRELACIONGASTOS = ? ");
        queryDet.append("UPDATE DET SET RFC = cRFCCuotas ");
        queryDet.append("FROM tRELACIONGASTOSDetalle DET WITH (NOLOCK) ");
        queryDet.append("JOIN tComprobacionLaudos COMP WITH (NOLOCK) ON DET.nFolioRELACIONGASTOS = COMP.nFolioRELACIONGASTOS ");
        queryDet.append("WHERE DET.nFolioRELACIONGASTOS = ? ");
        log.trace("Consulta: " + queryEnc);
        log.trace("Consulta: " + queryDet);
        try {
            psEnc = conn.prepareStatement(queryEnc.toString());
            psEnc.setInt(1, nFolio);
            psDet = conn.prepareStatement(queryDet.toString());
            psDet.setInt(1, nFolio);
            int insertedEnc = psEnc.executeUpdate();
            int insertedDet = psDet.executeUpdate();
            log.debug("Se actualizaron: " + insertedEnc + " registros en tRELACIONGASTOSEncabezado");
            log.debug("Se actualizaron: " + insertedDet + " registros en tRELACIONGASTOSDetalle");
        } finally {
            CloseObject.closeObject(psEnc);
            CloseObject.closeObject(psDet);
        }
    }
}
