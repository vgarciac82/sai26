package com.syc.obrapublica.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ObraPublicaReportesBusinessLogic;
import com.syc.sai.contabilidad.CuentaPublicaCuerpoReportes;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import java.nio.file.Paths;

@WebServlet(name = "ReportesOP", urlPatterns = { "/ObraPublica/reportes" })
public class ObraPublicaReportesServlet extends HttpServlet implements GestionInterface {

    private String tempDir = null;

    private static final long serialVersionUID = -1868632393274575540L;

    private static final String REPORTS_BODY_ROOT = "ctaPublica";

    private static final Logger log = LoggerFactory.getLogger(ObraPublicaReportesServlet.class);

    private static final String htmlBodyI = "<HTML>\n " + "\t<HEAD><style type=\"text/css\">.dollars:before { content:'$'; }</style>\n " + "\t\t<TITLE>\n</TITLE>\n " + "\t\t<style type=\"text/css\">\n " + "table td {" + "\tborder-color: black;" + "\tborder-style: solid;" + "\tborder-width: thin;" + "}" + ".encabezado {" + "\tfont-family: Arial, \"Helvetica Neue\", Helvetica, sans-serif;" + "\tfont-size: 5 px;" + "\tfont-weight: bold;" + "\ttext-align: center;" + "}" + ".titulo {" + "\tborder-style: none;" + "\tfont-family: Calibri, Verdana, Ariel, sans-serif;" + "\tfont-size: 11 px;" + "\tfont-weight: bold;" + "}" + "\t\t</style>\n " + "\t</HEAD>\n " + "\t<BODY>\n";

    private static final String htmlBodyF = "\n\t</BODY>\n" + "</HTML>";

    private static final String HTML_BODY_ROP_RCA_I = "<HTML>" + "\t<HEAD>" + "\t\t<TITLE></TITLE>" + "\t\t<style type=\"text/css\">" + "\t\t\t.tituloPrincipal {" + "\t\t\t\tfont-family: Arial, Helvetica, sans-serif;" + "\t\t\t\tfont-size: 16 pt;" + "\t\t\t\ttext-align: center;" + "\t\t\t\tfont-weight: bold;" + "\t\t\t\tborder-style: none;" + "\t\t\t}" + "\t\t\t" + "\t\t\t.tituloPrincipal2 {" + "\t\t\t\tfont-family: Arial, Helvetica, sans-serif;" + "\t\t\t\tfont-size: 14 pt;" + "\t\t\t\ttext-align: center;" + "\t\t\t\tfont-weight: bold;" + "\t\t\t\tborder-style: none;" + "\t\t\t}" + "\t\t\t" + "\t\t\t.tituloPrincipal3 {" + "\t\t\t\tfont-family: Arial, Helvetica, sans-serif;" + "\t\t\t\tfont-size: 14 pt;" + "\t\t\t\ttext-align: left;" + "\t\t\t\tfont-weight: bold;" + "\t\t\t\tborder-style: none;" + "\t\t\t}" + "\t\t\t" + "\t\t\t.tituloNormal {" + "\t\t\t\tfont-family: Arial, Helvetica, sans-serif;" + "\t\t\t\tfont-size: 12 pt;" + "\t\t\t\ttext-align: center;" + "\t\t\t\tfont-weight: normal;" + "\t\t\t\tborder-style: none;" + "\t\t\t}" + "\t\t\t" + "\t\t\t.centradoVertical {" + "\t\t\t\tfont-family: Arial, sans-serif;" + "\t\t\t\tfont-size: 9.0 pt;" + "\t\t\t\ttext-align: center;" + "\t\t\t\tfont-weight: normal;" + "" + "\t\t\t\tvertical-align: middle;" + "\t\t\t} \t.tituloTabla1 {" + "\t\t\t\tfont-family: Arial, sans-serif;" + "\t\t\t\tfont-size: 9.0 pt;" + "\t\t\t\ttext-align: center;" + "\t\t\t\tfont-weight: normal;" + "\t\t\t\tbackground: #CCFFFF;" + "\t\t\t\tvertical-align: middle;" + "\t\t\t}" + "\t\t\t" + "\t\t\ttable td {" + "\t\t\t\tborder-color: black;" + "\t\t\t\tborder-style: solid;" + "\t\t\t\tborder-width: thin;" + "\t\t\t\tfont-family: Arial, Helvetica, sans-serif;" + "\t\t\t\tfont-size: 8.0 pt;" + "\t\t\t\ttext-align: center;" + "\t\t\t}" + "\t\t</style>" + "\t</HEAD>" + "\t<BODY>";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Usuario u = null;
        if (session == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        String reportType = req.getParameter("rt");
        if ("FORMATO_10".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                String reportTitulo = CuentaPublicaCuerpoReportes.getReportBody("OPFormato10Titulo");
                if (reportTitulo == null)
                    reportTitulo = CuentaPublicaCuerpoReportes.getReportBody("OPFormato10Titulo", getReportStream("OPFormato10Titulo"));
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormato10Encabezado");
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormato10Encabezado", getReportStream("OPFormato10Encabezado"));
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "formato10.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                String strReport = oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, reportBody, wb, reportType);
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "formato10.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
                /*String report = "<table>" + reportTitulo + strReport + "</table>";
				sendExcel(resp, report, "OPFormato10", null);*/
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("CONSOLI_NAL".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "OPConsolidaNacionalTotal.xls";
                System.out.println(cFileExcel);
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType);
                log.info("generaReporteFormato10");
                System.out.println("aqui debe salir");
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "CONSOLI_NAL.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
                /*String report = "<table>" + reportTitulo + strReport + "</table>";
				sendExcel(resp, report, "OPFormato10", null);*/
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("CONSOLI_AREA_RESP".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "OPConsolidaAreaResponsable.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType);
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "CONSOLI_AREA_RESP.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
                /*String report = "<table>" + reportTitulo + strReport + "</table>";
				sendExcel(resp, report, "OPFormato10", null);*/
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("CONSOLI_PROYECTO".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "OPConsolidaXProyecto.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType);
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "CONSOLI_AREA_RESP.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
                /*String report = "<table>" + reportTitulo + strReport + "</table>";
				sendExcel(resp, report, "OPFormato10", null);*/
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("MULTI_REPORTE".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "MultiReporteObra.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                String strCondMultiR = "";
                //if (req.getParameter("ctasPresupuestales") != null && req.getParameter("ctasPresupuestales") != "")
                // strCondMultiR += " and cUnidadResponsable_16  = '" + req.getParameter("ctasPresupuestales") + "'";
                //if (req.getParameter("cUnidadEjecutora") != null && req.getParameter("cUnidadEjecutora") != "")
                //	strCondMultiR += " and   = '" + req.getParameter("cUnidadEjecutora") + "'";
                //if (req.getParameter("nOrden") != null && req.getParameter("nOrden") != "")
                //	 strCondMultiR += " and nClaveCNA  = '" + req.getParameter("nOrden") + "'";
                if (req.getParameter("ep") != null && req.getParameter("ep") != "")
                    strCondMultiR += " and cSubCuenta  = '" + req.getParameter("ep") + "'";
                if (req.getParameter("EjercicioFiscal") != null && req.getParameter("EjercicioFiscal") != "")
                    strCondMultiR += " and aEjercicioFiscal_1  = '" + req.getParameter("EjercicioFiscal") + "'";
                if (req.getParameter("ProgramaPresupuestario") != null && req.getParameter("ProgramaPresupuestario") != "")
                    strCondMultiR += " and cProgramaPresupuestario_9  = '" + req.getParameter("ProgramaPresupuestario") + "'";
                if (req.getParameter("RamoEP") != null && req.getParameter("RamoEP") != "")
                    strCondMultiR += " and cRamo_2  = '" + req.getParameter("RamoEP") + "'";
                if (req.getParameter("Partida") != null && req.getParameter("Partida") != "")
                    strCondMultiR += " and cPartida_10  = '" + req.getParameter("Partida") + "'";
                if (req.getParameter("UnidadResponsableEP") != null && req.getParameter("UnidadResponsableEP") != "")
                    strCondMultiR += " and cUnidadResponsable_3  = '" + req.getParameter("UnidadResponsableEP") + "'";
                if (req.getParameter("UnidadResponsableEP") != null && req.getParameter("UnidadResponsableEP") != "")
                    strCondMultiR += " and cUnidadResponsable_15  = '" + req.getParameter("UnidadResponsableEP") + "'";
                if (req.getParameter("TipoGasto") != null && req.getParameter("TipoGasto") != "")
                    strCondMultiR += " and cTipoGasto_11  = '" + req.getParameter("TipoGasto") + "'";
                if (req.getParameter("GrupoFuncional") != null && req.getParameter("GrupoFuncional") != "")
                    strCondMultiR += " and cGrupoFuncional_4  = '" + req.getParameter("GrupoFuncional") + "'";
                if (req.getParameter("FuenteFinanciamiento") != null && req.getParameter("FuenteFinanciamiento") != "")
                    strCondMultiR += " and cFuenteFinanciamiento_12  = '" + req.getParameter("FuenteFinanciamiento") + "'";
                if (req.getParameter("Funcion") != null && req.getParameter("Funcion") != "")
                    strCondMultiR += " and cFuncion_5  = '" + req.getParameter("Funcion") + "'";
                if (req.getParameter("EntidadFederativa") != null && req.getParameter("EntidadFederativa") != "")
                    strCondMultiR += " and cEntidadFederativa_13  = '" + req.getParameter("EntidadFederativa") + "'";
                if (req.getParameter("SubFuncion") != null && req.getParameter("SubFuncion") != "")
                    strCondMultiR += " and cSubFuncion_6  = '" + req.getParameter("SubFuncion") + "'";
                if (req.getParameter("Cartera") != null && req.getParameter("Cartera") != "")
                    strCondMultiR += " and cCartera_14  = '" + req.getParameter("Cartera") + "'";
                if (req.getParameter("ProgramaGeneral") != null && req.getParameter("ProgramaGeneral") != "")
                    strCondMultiR += " and cProgramaGeneral_7  = '" + req.getParameter("ProgramaGeneral") + "'";
                if (req.getParameter("ActividadInstitucional") != null && req.getParameter("ActividadInstitucional") != "")
                    strCondMultiR += " and cActividadInstitucional_8  = '" + req.getParameter("ActividadInstitucional") + "'";
                String strCondicion = "";
                /*if (req.getParameter("FOLIO") != null && req.getParameter("FOLIO") != "")
					 strCondicion += " and apa_foliosai  = '" + req.getParameter("FOLIO") + "'";*/
                if (req.getParameter("chkPasivo") != null) {
                    strCondicion += " and [Es Pago de Pasivo] = 'SI'";
                }
                if (req.getParameter("cCveContrato") != null && req.getParameter("cCveContrato") != "")
                    strCondicion += " and op.[Contrato] like '%" + req.getParameter("cCveContrato") + "%'";
                /*if (req.getParameter("fRecepcion") != null && req.getParameter("fRecepcion") != "")
					 strCondicion += " and apa_faplicacion  = '" + req.getParameter("fRecepcion") + "'";
					if (req.getParameter("UnidadNormativa") != null && req.getParameter("UnidadNormativa") != "")
					 strCondicion += " and apa_cU_UR  = '" + req.getParameter("UnidadNormativa") + "'";
					if (req.getParameter("cUnidadEjecutora") != null && req.getParameter("cUnidadEjecutora") != "")
					 strCondicion += " and apa_cU_UE  = '" + req.getParameter("cUnidadEjecutora") + "'";
					if (req.getParameter("id_area") != null && req.getParameter("id_area") != "")
					 strCondicion += " and apa_id_area  = '" + req.getParameter("id_area") + "'";
					if (req.getParameter("cIdTObra") != null && req.getParameter("cIdTObra") != "")
					 strCondicion += " and apa_cTipoObra  = '" + req.getParameter("cIdTObra") + "'";
					if (req.getParameter("cIdTipRec") != null && req.getParameter("cIdTipRec") != "")
					 strCondicion += " and apa_ctiporecurso  = '" + req.getParameter("cIdTipRec") + "'";
					if (req.getParameter("cCarteraProyec") != null && req.getParameter("cCarteraProyec") != "" &&  !"-1".equalsIgnoreCase(req.getParameter("cCarteraProyec"))  )
					 strCondicion += " and apa_cCveCartera  = '" + req.getParameter("cCarteraProyec") + "'";
					if (req.getParameter("cOLI") != null && req.getParameter("cOLI") != "" && !"Seleccionar".equalsIgnoreCase(req.getParameter("cOLI"))  )
					 strCondicion += " and apa_ccveoli  = '" + req.getParameter("cOLI") + "'";*/
                if (req.getParameter("cIdTipoAdjudica") != null && req.getParameter("cIdTipoAdjudica") != "")
                    strCondicion += " and [Tipo de procedimiento]  = '" + req.getParameter("cIdTipoAdjudica") + "'";
                /*if (req.getParameter("cIdConvocatoria") != null && req.getParameter("cIdConvocatoria") != "")
					 strCondicion += " and pc_cConvocatoria  = '" + req.getParameter("cIdConvocatoria") + "'";
					if (req.getParameter("fConvocatoria") != null && req.getParameter("fConvocatoria") != "")
					 strCondicion += " and pc_fFechaConvoca  = '" + req.getParameter("fConvocatoria") + "'";
					if (req.getParameter("fAclaracion") != null && req.getParameter("fAclaracion") != "")
					 strCondicion += " and pc_fFechaJuntAcla  = '" + req.getParameter("fAclaracion") + "'";
					if (req.getParameter("fRecepFallo") != null && req.getParameter("fRecepFallo") != "")
					 strCondicion += " and pc_fFechaFallo  = '" + req.getParameter("fRecepFallo") + "'";*/
                if (req.getParameter("cIDRFC") != null && req.getParameter("cIDRFC") != "")
                    strCondicion += " and rfc like '%" + req.getParameter("cIDRFC") + "%'";
                /*if (req.getParameter("cnombre") != null && req.getParameter("cnombre") != "")
					 strCondicion += " and com_cBeneficiario  = '" + req.getParameter("cnombre") + "'";
					if (req.getParameter("cIdTipoContratoObra") != null && req.getParameter("cIdTipoContratoObra") != "")
					 strCondicion += " and com_cTipoContrato  = '" + req.getParameter("cIdTipoContratoObra") + "'";
					if (req.getParameter("esquemaPrecios") != null && req.getParameter("esquemaPrecios") != "")
					 strCondicion += " and com_id_precio  = '" + req.getParameter("esquemaPrecios") + "'";
					//if (req.getParameter("cPluriaAnual") != null && req.getParameter("cPluriaAnual") != "")
					// strCondicion += " and com_iEsPluriAnual  = '" + req.getParameter("cPluriaAnual") + "'";
					if (req.getParameter("nofianza") != null && req.getParameter("nofianza") != "")
					 strCondicion += " and com_nofianza  = '" + req.getParameter("nofianza") + "'";
					if (req.getParameter("nPorcAnticipo") != null && req.getParameter("nPorcAnticipo") != "")
					 strCondicion += " and com_nPorceAnticipo  = '" + req.getParameter("nPorcAnticipo") + "'";
					if (req.getParameter("mAnticipo") != null && req.getParameter("mAnticipo") != "")
					 strCondicion += " and com_nMontoAnticipo  = '" + req.getParameter("mAnticipo") + "'";
					if (req.getParameter("entidadobra") != null && req.getParameter("entidadobra") != "")
					 strCondicion += " and com_cIdEntidadFederativa  = '" + req.getParameter("entidadobra") + "'";
					if (req.getParameter("cIdAdicionales") != null && req.getParameter("cIdAdicionales") != "")
					 strCondicion += " and com_cTipoAdicional  = '" + req.getParameter("cIdAdicionales") + "'";
					if (req.getParameter("cNoConvenio") != null && req.getParameter("cNoConvenio") != "")
					 strCondicion += " and cm_cnoconvenio  = '" + req.getParameter("cNoConvenio") + "'";
					if (req.getParameter("mIncremento") != null && req.getParameter("mIncremento") != "")
					 strCondicion += " and cm_mMonto  = '" + req.getParameter("mIncremento") + "'";
					if (req.getParameter("mIncrementoConIVA") != null && req.getParameter("mIncrementoConIVA") != "")
					 strCondicion += " and cm_mMontoconIva  = '" + req.getParameter("mIncrementoConIVA") + "'";
					if (req.getParameter("mConv") != null && req.getParameter("mConv") != "")
					 strCondicion += " and cm_nMes  = '" + req.getParameter("mConv") + "'";
					if (req.getParameter("fInicioConv") != null && req.getParameter("fInicioConv") != "")
					 strCondicion += " and cm_mimporte  = '" + req.getParameter("fInicioConv") + "'";
					if (req.getParameter("fFinConv") != null && req.getParameter("fFinConv") != "")
					 strCondicion += " and cm_ep  = '" + req.getParameter("fFinConv") + "'";
					if (req.getParameter("cCveContratoPas") != null && req.getParameter("cCveContratoPas") != "")
					 strCondicion += " and pas_cCveContrato  = '" + req.getParameter("cCveContratoPas") + "'";
					if (req.getParameter("noEstimacion") != null && req.getParameter("noEstimacion") != "")
					 strCondicion += " and est_noestimacion  = '" + req.getParameter("noEstimacion") + "'";
					if (req.getParameter("mesEstimado") != null && req.getParameter("mesEstimado") != "")
					 strCondicion += " and est_mesEstimado  = '" + req.getParameter("mesEstimado") + "'";
					if (req.getParameter("fperiodoEstimacionIni") != null && req.getParameter("fperiodoEstimacionIni") != "")
					 strCondicion += " and est_fperiodoEstimacionIni  = '" + req.getParameter("fperiodoEstimacionIni") + "'";
					if (req.getParameter("fperiodoEstimacionInicial") != null && req.getParameter("fperiodoEstimacionInicial") != "")
					 strCondicion += " and est_fperiodoEstimacionFin  = '" + req.getParameter("fperiodoEstimacionInicial") + "'";
					if (req.getParameter("nPorceAvanceFisicoEstimado") != null && req.getParameter("nPorceAvanceFisicoEstimado") != "")
					 strCondicion += " and est_nporceavancefisicoestimado  = '" + req.getParameter("nPorceAvanceFisicoEstimado") + "'";
					if (req.getParameter("mMontoEstimacion") != null && req.getParameter("mMontoEstimacion") != "")
					 strCondicion += " and est_mmontoestimacion  = '" + req.getParameter("mMontoEstimacion") + "'";
					if (req.getParameter("nPorceAvanceFisicoEjecutado") != null && req.getParameter("nPorceAvanceFisicoEjecutado") != "")
					 strCondicion += " and est_nporceavancefisicoejecutado  = '" + req.getParameter("nPorceAvanceFisicoEjecutado") + "'";
					if (req.getParameter("mmontoFisicoEjecutado") != null && req.getParameter("mmontoFisicoEjecutado") != "")
					 strCondicion += " and est_mmontoFisicoEjecutado  = '" + req.getParameter("mmontoFisicoEjecutado") + "'";
					if (req.getParameter("nPorceAvanceFisicoProgramado") != null && req.getParameter("nPorceAvanceFisicoProgramado") != "")
					 strCondicion += " and est_nporceavancefisicoprogramado  = '" + req.getParameter("nPorceAvanceFisicoProgramado") + "'";
					if (req.getParameter("mmontoFisicoProgramado") != null && req.getParameter("mmontoFisicoProgramado") != "")
					 strCondicion += " and est_mmontoFisicoProgramado  = '" + req.getParameter("mmontoFisicoProgramado") + "'";*/
                String columnasBorrar = "";
                String token = "";
                if (req.getParameter("original") != null) {
                    columnasBorrar = columnasBorrar + token + "'81101'";
                    token = ",";
                }
                if (req.getParameter("modificado") != null) {
                    columnasBorrar = columnasBorrar + token + "'81102'";
                    token = ",";
                }
                if (req.getParameter("ampliacionautorizada") != null) {
                    columnasBorrar = columnasBorrar + token + "'81103'";
                    token = ",";
                }
                if (req.getParameter("reduccionautorizada") != null) {
                    columnasBorrar = columnasBorrar + token + "'81104'";
                    token = ",";
                }
                if (req.getParameter("ampliacionen tramite") != null) {
                    columnasBorrar = columnasBorrar + token + "'81105'";
                    token = ",";
                }
                if (req.getParameter("reduccionen tramite") != null) {
                    columnasBorrar = columnasBorrar + token + "'81106'";
                    token = ",";
                }
                if (req.getParameter("reintegroen tramite") != null) {
                    columnasBorrar = columnasBorrar + token + "'81107'";
                    token = ",";
                }
                if (req.getParameter("rectificacion") != null) {
                    columnasBorrar = columnasBorrar + token + "'81108'";
                    token = ",";
                }
                if (req.getParameter("reduccionshcp en tramite") != null) {
                    columnasBorrar = columnasBorrar + token + "'81109'";
                    token = ",";
                }
                if (req.getParameter("reduccionshcp aplicada") != null) {
                    columnasBorrar = columnasBorrar + token + "'81110'";
                    token = ",";
                }
                if (req.getParameter("apartado") != null) {
                    columnasBorrar = columnasBorrar + token + "'82101'";
                    token = ",";
                }
                if (req.getParameter("precomprometido") != null) {
                    columnasBorrar = columnasBorrar + token + "'82102'";
                    token = ",";
                }
                if (req.getParameter("comprometido") != null) {
                    columnasBorrar = columnasBorrar + token + "'82103'";
                    token = ",";
                }
                if (req.getParameter("devengado") != null) {
                    columnasBorrar = columnasBorrar + token + "'82104'";
                    token = ",";
                }
                if (req.getParameter("ejercidono pagado") != null) {
                    columnasBorrar = columnasBorrar + token + "'82105'";
                    token = ",";
                }
                if (req.getParameter("disponibleneto") != null) {
                    columnasBorrar = columnasBorrar + token + "'82106'";
                    token = ",";
                }
                if (req.getParameter("disponiblebruto") != null) {
                    columnasBorrar = columnasBorrar + token + "'82107'";
                    token = ",";
                }
                if (req.getParameter("ejercidopagado") != null) {
                    columnasBorrar = columnasBorrar + token + "'82108'";
                    token = ",";
                }
                if (req.getParameter("chkApaDesObra") != null) {
                    columnasBorrar = columnasBorrar + token + "'81101'";
                    token = ",";
                }
                /*if (req.getParameter("chkApaMonto") == null )
					 columnasBorrar += "43,";
					if (req.getParameter("chkApaIva") == null )
					 columnasBorrar += "44,";
					if (req.getParameter("chkApaMontoIva") == null )
					 columnasBorrar += "45,";
					if (req.getParameter("chkApaEP") == null )
					 columnasBorrar += "38,";
					if (req.getParameter("chkApaMes") == null )
					 columnasBorrar += "39,";
					if (req.getParameter("chkPreEP") == null )
					 columnasBorrar += "47,";
					if (req.getParameter("chkPreMes") == null )
					 columnasBorrar += "48,";
					if (req.getParameter("chkComMonto") == null )
					 columnasBorrar += "53,";
					if (req.getParameter("chkComIva") == null )
					 columnasBorrar += "55,";
					if (req.getParameter("chkComMontoIva") == null )
					 columnasBorrar += "56,";
					if (req.getParameter("chkComEP") == null )
					 columnasBorrar += "51,";
					if (req.getParameter("chkComMes") == null )
					 columnasBorrar += "52,";
					if (req.getParameter("chkCModMonto") == null )
					 columnasBorrar += "67,";
					if (req.getParameter("chkCModEP") == null )
					 columnasBorrar += "65,";
					if (req.getParameter("chkCModMes") == null )
					 columnasBorrar += "66,";
					if (req.getParameter("chkPasMonto") == null )
					 columnasBorrar += "63,";
					if (req.getParameter("chkPasEP") == null )
					 columnasBorrar += "61,";
					if (req.getParameter("chkPasMes") == null )
					 columnasBorrar += "62,";
					if (req.getParameter("chkEstMonto") == null )
					 columnasBorrar += "59,";
					if (req.getParameter("chkEstMes") == null )
					 columnasBorrar += "58,";
					*/
                if ("".equalsIgnoreCase(columnasBorrar)) {
                    columnasBorrar = "'81101','81102','81103','81104','81105','81106','81107','81108','81109','81110','82101','82102','82103','82104','82105','82106','82107','82108'";
                }
                String strGeneral = "GENERAL";
                if (req.getParameter("TipoReporte") != null && req.getParameter("TipoReporte") != "")
                    strGeneral = req.getParameter("TipoReporte");
                String strResumen = "DESCARTAR";
                if (req.getParameter("Componentes") != null && req.getParameter("Componentes") != "")
                    strResumen = req.getParameter("Componentes");
                //Multireporte Obra Pública
                excelConPlantilla(req, resp, "60", cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", reportType, u.getLogin(), strCondicion, strCondMultiR, columnasBorrar, strGeneral, strResumen);
                //oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType, u.getLogin(), strCondicion, strCondMultiR, columnasBorrar);
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "CONSOLI_AREA_RESP.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
                /*String report = "<table>" + reportTitulo + strReport + "</table>";
				sendExcel(resp, report, "OPFormato10", null);*/
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("EXPORTA_PAOP".equals(reportType)) {
            String idunidadresponsable = req.getParameter("uUR");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "exportaPAOPCaptura.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                //Exportación desde captura PAOP
                excelConPlantilla(req, resp, "61", "", "", "", idunidadresponsable, generarVacio, "", reportType, u.getLogin(), "", "", "", "", "");
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "CONSOLI_AREA_RESP.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("CONSOLI_PROYECTO_REGION".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "OPConsolidaXProyectoXRegion.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType);
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "CONSOLI_AREA_RESP.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
                /*String report = "<table>" + reportTitulo + strReport + "</table>";
				sendExcel(resp, report, "OPFormato10", null);*/
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("REPORTE_CONTRATOS".equals(reportType)) {
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            String idArea = req.getParameter("idArea");
            String mesOtrimestre = obtieneMesOTrimestre(fechaI, fechaF);
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormatoContratosAdjudicados");
                if (reportBody == null)
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormatoContratosAdjudicados", getReportStream("OPFormatoContratosAdjudicados"), mesOtrimestre);
                String strReport = oprbl.generaReporteContratosAdjudicados(fechaI, fechaF, idunidadresponsable, idArea);
                String report = "<table>" + reportBody + strReport + "</table>";
                sendExcel(resp, report, "OPFormatoContratosAdjudicados", ObraPublicaReportesServlet.HTML_BODY_ROP_RCA_I);
                CuentaPublicaCuerpoReportes.CuerpoReportes.clear();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("REPORTE_ACUMULADO".equals(reportType)) {
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            String idArea = req.getParameter("idArea");
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormatoContratosAdjudicadosAcumulado");
                if (reportBody == null) {
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormatoContratosAdjudicadosAcumulado", getReportStream("OPFormatoContratosAdjudicadosAcumulado"));
                }
                String strReport = oprbl.generaReporteContratosAcumulado(fechaI, fechaF, idunidadresponsable, idArea);
                String report = "<table>" + reportBody + strReport + "</table>";
                sendExcel(resp, report, "OPFormatoContratosAdjudicados", ObraPublicaReportesServlet.HTML_BODY_ROP_RCA_I);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("REPORTE_SEGUIMIENTO".equals(reportType)) {
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            String idArea = req.getParameter("idArea");
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                String reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormatoReporteSeguimientoDeObra");
                if (reportBody == null) {
                    reportBody = CuentaPublicaCuerpoReportes.getReportBody("OPFormatoReporteSeguimientoDeObra", getReportStream("OPFormatoReporteSeguimientoDeObra"));
                }
                String strReport = oprbl.generaReporteSeguimientoObras(fechaI, fechaF, idunidadresponsable, idArea);
                String report = "<table>" + reportBody + strReport + "</table>";
                sendExcel(resp, report, "OPFormatoContratosAdjudicados", ObraPublicaReportesServlet.HTML_BODY_ROP_RCA_I);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("CONSOLI_POR_AREA_EJECUTORA".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "OPConsolidoPorAreaEjecutora.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                System.out.println(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType);
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "ConsolidoPorAreaEjecutora.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        } else if ("CONSOLI_POR_AREA_EJECUTORA_y_PROYECTO".equals(reportType)) {
            String cCentroContable = req.getParameter("cCentroContable");
            String fechaI = req.getParameter("fechaI");
            String fechaF = req.getParameter("fechaF");
            String idunidadresponsable = req.getParameter("idunidadresponsable");
            boolean generarVacio = false;
            try {
                ObraPublicaReportesBusinessLogic oprbl = new ObraPublicaReportesBusinessLogic();
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                String cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "OPConsolidoPorAreaEjecutorayProyecto.xls";
                InputStream inp = new FileInputStream(cFileExcel);
                Workbook wb = new HSSFWorkbook(inp);
                oprbl.generaReporteFormato10(cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType);
                resp.setContentType("application/vnd.ms-excel");
                String postFijo = "";
                resp.addHeader("Content-Disposition", "inline; filename=\"" + System.currentTimeMillis() + "Cons_AreaEjecutoraXProuecto.xls" + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
                wb.write(resp.getOutputStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendError(resp, e.toString());
            }
        }
    }

    @Override
    public /*	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}*/
    void init(ServletConfig config) throws ServletException {
        super.init(config);
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    private InputStream getReportStream(String reportType) {
        InputStream is = null;
        String reportSrcName = null;
        try {
            reportSrcName = REPORTS_BODY_ROOT + File.separator + reportType + ".bdy";
            log.debug("Object: " + String.valueOf("Report Name -> " + reportSrcName));
            String reportPath = getServletContext().getRealPath(reportSrcName);
            log.debug("Object: " + String.valueOf("Report Path-> " + reportPath));
            if (reportPath != null) {
                is = new FileInputStream(reportPath);
            } else {
                if (!reportSrcName.startsWith("/"))
                    reportSrcName = "/" + reportSrcName;
                reportSrcName.replaceAll("\\\\", "/");
                is = getServletContext().getResource(reportSrcName).openStream();
            }
        } catch (Exception e) {
            log.error("No se pudo cargar o no existe el archivo " + reportSrcName, e);
        }
        return is;
    }

    private static void sendExcel(HttpServletResponse resp, String reportResult, String reportName, String htmlHead) throws IOException {
        resp.setContentType("application/vnd.ms-excel");
        resp.addHeader("Content-Disposition", "inline; filename=\"reporte" + reportName + "_" + System.currentTimeMillis() + ".xls\";");
        PrintWriter out = resp.getWriter();
        out.println(htmlHead == null ? htmlBodyI + reportResult + htmlBodyF : htmlHead + reportResult + htmlBodyF);
        out.flush();
        out.close();
    }

    private static void sendError(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<h1>Se presento el siguiente problema mientras se llenaba el reporte</h1><br>");
        out.println("\t\t<br>" + msg + "<br>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    private String obtieneMesOTrimestre(String fIni, String fFin) {
        String[] fIniA = new String[3];
        String[] fFinA = new String[3];
        String[] fFinal = new String[5];
        DateFormat df3 = DateFormat.getDateInstance(DateFormat.LONG);
        Date now = null;
        SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
        fIniA = fIni.split("/");
        fFinA = fFin.split("/");
        String s3 = "";
        int mes = Integer.parseInt(fIniA[1]);
        int trimestre = 0;
        String cadena = "";
        if (fIniA[1].equals(fFinA[1])) {
            try {
                now = formatoDeFecha.parse(fIni);
                s3 = df3.format(now);
                fFinal = s3.split(" ");
                cadena = " mes de " + fFinal[2] + " ";
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        } else {
            if ((mes % 3) == 0) {
                trimestre = (mes / 3);
            } else {
                trimestre = ((int) (Math.floor(mes / 3))) + 1;
            }
            cadena = " trimestre " + trimestre + " ";
        }
        return cadena;
    }

    public void excelConPlantilla(HttpServletRequest request, HttpServletResponse response, String tipoReporte, String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, String reportType, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar, String strGeneral, String strResumen) throws Exception {
        HttpSession session = request.getSession(false);
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        int nConsecutivoSICOP = 0;
        Integer esPDF = 0;
        if (request.getParameter("esPDF") != null) {
            if (Integer.parseInt(request.getParameter("esPDF")) >= 0)
                esPDF = Integer.parseInt(request.getParameter("esPDF"));
        }
        Integer maxHojas = 0;
        if (request.getParameter("maxHojas") != null) {
            if (Integer.parseInt(request.getParameter("maxHojas")) >= 0)
                maxHojas = Integer.parseInt(request.getParameter("maxHojas"));
        }
        String cFileExcel = "";
        if (request.getParameter("laPlant") == null) {
            cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "MultiReporteObra.xls";
            if (tipoReporte.equals("61"))
                cFileExcel = getServletContext().getRealPath("/upload") + "\\" + "exportaPAOPCaptura.xls";
        } else
            cFileExcel = getServletContext().getRealPath("/upload") + "\\" + request.getParameter("laPlant");
        InputStream inp = new FileInputStream(cFileExcel);
        Workbook wb = new HSSFWorkbook(inp);
        ArrayList arrDetalle = new ArrayList();
        ArrayList arrHeader = new ArrayList();
        String url = "";
        ObraPublicaReportesBusinessLogic eQuery = new ObraPublicaReportesBusinessLogic();
        if (tipoReporte.equals("60")) {
            arrDetalle = eQuery.multiReporte("{call sp_pMultiReporte_syc_OP2(?,?,?,?,?,?,?,?,?,?)}", cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType, strUsuario, strCondicion, strCondMultiR, columnasBorrar, strGeneral, strResumen);
            arrHeader = eQuery.exec("select '', '','','Concentrado de Contratos de Obra Pública' ramo");
        } else if (tipoReporte.equals("61")) {
            String uUR = request.getParameter("uUR");
            String mesNuevo = request.getParameter("mesNuevo");
            //arrDetalle = eQuery.exec("select null ggg");
            String strQuery = "";
            strQuery += "select  nMes [Mes], UE, cURes.D_DESCRIPCION UR, '' [Tipo de movimiento, modificación, bajo o nuevo] ";
            strQuery += " ,cCVE_CUCOP [CLAVECUCOP(SeleccionardelCatálogo)],SUBSTRING(EP, 45, 11) [CARTERADEPROYECTOSAUTORIZADO(SeleccionardelCatálogo)] ";
            strQuery += " ,oliAutorizado [OLI AUTORIZADO], EP [CLAVE PRESUPUESTARIA], '' [CARTERA VS CLAVE], '' [CUCOP OBJETO DE GASTO] ";
            strQuery += " ,SUBSTRING(EP, 27, 4) [ProgramaPresupuestal], SUBSTRING(EP, 32, 5)[ObjetodeGasto(Partidaespecífica)] ";
            strQuery += " ,noContrato [No. De Contrato (De acuerdo a la Comité a la Normatividad de Obra Pública)] ";
            strQuery += " ,cCONCEPTO [CONCEPTO (TODO EN MAYUSCULAS) (HASTA TRES RENGLONES DE LARGO)], valorTotalObra [Valor Total De La Obra (En pesos)] ";
            strQuery += " ,mEstimadoMipymes [Valor estimado de compras a Mipymes (0 Porque no hay PYMES en Obra)] ";
            strQuery += " ,mEstimadoNoCubiertasTLC [Valor estimado de compras no cubiertas por TLC (En  pesos)], ncantidad [Cantidad (En número entero)] ";
            strQuery += " ,nUnidadMedida [Unidad de Medida (Seleccionar del Catálogo)] ";
            strQuery += "  ,ctipoProcedimiento [Carácter del procedimiento de contratación (N= NACIONAL o I= Internacional)] ";
            strQuery += " ,nEntidadFederativa [Entidad Federativa (Seleccionar del Catálogo hoja color verde)] ";
            strQuery += " ,nTrimestre1 [Trimestre1 (Enteros y 0 si no se reporta nada y la Suma de los 4 Trim = 100))] ";
            strQuery += " ,nTrimestre2 [Trimestre2 (Enteros y 0 si no se reporta nada y la Suma de los 4 Trim = 100))] ";
            strQuery += " ,nTrimestre3 [Trimestre3 (Enteros y 0 si no se reporta nada y la Suma de los 4 Trim = 100))] ";
            strQuery += " ,nTrimestre4 [Trimestre4 (Enteros y 0 si no se reporta nada y la Suma de los 4 Trim = 100))] ";
            strQuery += " ,CAST(fechaDelReporte AS date) as  [Fecha del Reporte (En los meses del 20 al 25 de cada mes)] ";
            strQuery += " ,nPlurianual [Plurianual (0=Anual y 1= Plurianual)] ";
            strQuery += " ,nEjerciciosFicales [Ejercicios Fiscales (0=Mismo Año y 2 mas de un año)] ";
            strQuery += " ,mMultianualEstimado [Monto Plurianual a Ejercer en el Presente Año (En pesos) 0 si el Cto. es anual.] ";
            strQuery += " ,CAST(fInicialContrato AS date) as [Fecha Inicial Contrato (Deberá ser Menor a la Fecha Final)] ";
            strQuery += " ,CAST(fFinalContrato  AS date) as [Fecha Final Contrato (Deberá ser Mayor a la Fecha Inicial)] ";
            strQuery += " ,cAdj.siglasTAdjudicacion [Tipo del Procedimiento (Adjudicacion) AD LP I3P ó CC] ";
            strQuery += " ,cComentario1 [comentario1 (En mayúsculas y hasta 3 Renglones De Largo)] ";
            strQuery += " ,mAnualEjercer [TOTAL] , '' [Diferencias], cStatus Estatus ";
            strQuery += " from tprogramaanualObraPublica PAOP with (nolock) ";
            strQuery += " left join tCatalogoAdjudicacion cAdj with (nolock) on cAdj.cIdTAdjudicacion = PAOP.cTipoProcContratacion ";
            strQuery += " left join tCatUnidadResponsable cURes with (nolock) on cURes.cunidadresponsable = PAOP.UE ";
            strQuery += "  where cTipoProcContratacion = cAdj.cIdTAdjudicacion ";
            strQuery += "  and cStatus = 2 ";
            if (!uUR.equals("B03")) {
                strQuery += " and ue = '" + uUR + "'";
            }
            strQuery += " and nMes = '" + mesNuevo + "'";
            arrDetalle = eQuery.exec(strQuery);
            arrHeader = eQuery.exec("select '', '','','Concentrado paop' ramo");
        }
        int numHoja = 0;
        //funcion
        HSSFFont font = (HSSFFont) wb.createFont();
        log.info("Object: {}", "Inicia Reporte: " + request.getParameter("laPlant"));
        while (numHoja <= maxHojas) {
            if (arrDetalle != null) {
                if (!arrDetalle.isEmpty())
                    generaXLS(request, response, tipoReporte, wb, arrDetalle, arrHeader, url, numHoja, font);
            }
            arrDetalle = null;
            numHoja++;
        }
        log.info("Object: {}", "Termina Reporte: " + request.getParameter("laPlant"));
        response.setContentType("application/vnd.ms-excel");
        String postFijo = "";
        if (request.getParameter("cMes") != null) {
            postFijo = request.getParameter("cMes");
        }
        String strPlantilla = "MultiReporteObra.xls";
        if (request.getParameter("laPlant") != null)
            strPlantilla = request.getParameter("laPlant");
        response.addHeader("Content-Disposition", "inline; filename=\"" + strPlantilla + postFijo + "\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
        wb.write(response.getOutputStream());
    }

    private void generaXLS(HttpServletRequest request, HttpServletResponse response, String tipoReporte, Workbook wb, ArrayList arrDetalle, ArrayList arrHeader, String url, int numHoja, HSSFFont font) throws Exception {
        Sheet sheet = wb.getSheetAt(numHoja);
        if (numHoja == 0) {
            if (!(request.getParameter("nomHoja1") == null)) {
                if (!("".equalsIgnoreCase(request.getParameter("nomHoja1")))) {
                    wb.setSheetName(numHoja, request.getParameter("nomHoja1"));
                }
            }
        }
        int nrengXLS = 3;
        if (request.getParameter("rowHead") != null)
            nrengXLS = Integer.parseInt(request.getParameter("rowHead"));
        int j = 3;
        Cell laCelda = null;
        int numberOfColumns = 0;
        int[] arrColTipo = null;
        String ctaAnterior = "";
        int ultRow = 1;
        ultRow = sheet.getLastRowNum();
        if (arrHeader != null) {
            numberOfColumns = (Integer) arrHeader.get(0);
            arrColTipo = new int[numberOfColumns];
            arrColTipo = (int[]) arrHeader.get(2);
            font.setFontName(HSSFFont.FONT_ARIAL);
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);
            //font.setColor(HSSFColor.BLUE.index);
            while (j < arrHeader.size()) {
                ArrayList arrAdecua = (ArrayList) arrHeader.get(j);
                Row RowDetIntegra = null;
                if (ultRow < nrengXLS) {
                    RowDetIntegra = sheet.createRow(nrengXLS);
                } else {
                    RowDetIntegra = sheet.getRow(nrengXLS);
                }
                int i = 0;
                //String laFecha = "";
                while (i < arrAdecua.size()) {
                    if (ultRow < nrengXLS) {
                        laCelda = RowDetIntegra.createCell(i);
                    } else {
                        laCelda = RowDetIntegra.getCell(i);
                        if (laCelda == null)
                            laCelda = RowDetIntegra.createCell(i);
                    }
                    if (arrColTipo[i] == 3) {
                        laCelda.setCellValue(Double.parseDouble((String) arrAdecua.get(i)));
                        //laCelda.setCellType(Cell.CELL_TYPE_NUMERIC);
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                        cellStyle.setFont(font);
                        //laCelda.setCellStyle(cellStyle);
                    } else {
                        //CellStyle cellStyle = wb.createCellStyle();
                        CellStyle cellStyle = laCelda.getCellStyle();
                        //cellStyle.setFont(font);
                        //cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        //laCelda.setCellStyle(cellStyle);
                        if (!"".equalsIgnoreCase((String) arrAdecua.get(i)))
                            laCelda.setCellValue((String) arrAdecua.get(i));
                    }
                    i++;
                }
                arrAdecua = null;
                j++;
                nrengXLS++;
            }
        }
        // SE colocan body
        if (request.getParameter("rowDet") != null)
            nrengXLS = Integer.parseInt(request.getParameter("rowDet"));
        else
            nrengXLS++;
        int conEncabezados = 1;
        if (request.getParameter("conEncabezados") != null) {
            if (Integer.parseInt(request.getParameter("conEncabezados")) >= 1)
                conEncabezados = Integer.parseInt(request.getParameter("conEncabezados"));
        }
        if (arrDetalle != null) {
            if (conEncabezados == 1) {
                if (arrDetalle.size() > 1 && !arrDetalle.isEmpty()) {
                    numberOfColumns = (Integer) arrDetalle.get(0);
                    String[] cColQuery = null;
                    cColQuery = new String[numberOfColumns];
                    cColQuery = (String[]) arrDetalle.get(1);
                    int i = 0;
                    Row RowDetIntegra = null;
                    if (ultRow < nrengXLS) {
                        RowDetIntegra = sheet.createRow(nrengXLS);
                    } else {
                        RowDetIntegra = sheet.getRow(nrengXLS);
                    }
                    while (i < numberOfColumns) {
                        if (ultRow < nrengXLS)
                            laCelda = RowDetIntegra.createCell(i);
                        else {
                            laCelda = RowDetIntegra.getCell(i);
                            if (laCelda == null)
                                laCelda = RowDetIntegra.createCell(i);
                        }
                        //rpt_header+=("<th heigth=12px>"+cColQuery[i]+"</th>");
                        laCelda.setCellValue((String) cColQuery[i].replace("_", " "));
                        //CellStyle cellStyle = laCelda.getCellStyle();
                        HSSFFont boldFont = (HSSFFont) wb.createFont();
                        //boldFont.setFontHeightInPoints((short)22);
                        boldFont.setBold(true);
                        HSSFCellStyle boldStyle = (HSSFCellStyle) wb.createCellStyle();
                        CellStyle cellStyle = laCelda.getCellStyle();
                        //							 boldStyle.setFont(boldFont);
                        //							 boldStyle.setAlignment(HorizontalAlignment.CENTER);
                        laCelda.setCellStyle(cellStyle);
                        i++;
                    }
                    nrengXLS++;
                }
            }
            j = 3;
            laCelda = null;
            numberOfColumns = (Integer) arrDetalle.get(0);
            arrColTipo = null;
            arrColTipo = new int[numberOfColumns];
            arrColTipo = (int[]) arrDetalle.get(2);
            ctaAnterior = "";
            int colEspacio = -1;
            if (request.getParameter("colEspacio") != null) {
                if (Integer.parseInt(request.getParameter("colEspacio")) >= 0)
                    colEspacio = Integer.parseInt(request.getParameter("colEspacio"));
            }
            ultRow = sheet.getLastRowNum();
            while (j < arrDetalle.size()) {
                ArrayList arrAdecua = (ArrayList) arrDetalle.get(j);
                if (!ctaAnterior.equalsIgnoreCase((String) arrAdecua.get(0)))
                    if (colEspacio > -1)
                        nrengXLS++;
                Row RowDetIntegra = null;
                if (ultRow < nrengXLS) {
                    RowDetIntegra = sheet.createRow(nrengXLS);
                } else {
                    RowDetIntegra = sheet.getRow(nrengXLS);
                }
                //sheet.getRow(arg0)
                int i = 0;
                //String laFecha = "";
                //String valor1 = "";
                while (i < arrAdecua.size()) {
                    if (arrAdecua.get(i) != null) {
                        //log.info(" obtiene celda '" + i + "' del renglon '" + nrengXLS + "'. ultrow = '" + ultRow + "'");
                        if (ultRow < nrengXLS)
                            laCelda = RowDetIntegra.createCell(i);
                        else {
                            try {
                                laCelda = RowDetIntegra.getCell(i);
                            } catch (Exception noHayCelda) {
                            }
                            if (laCelda == null)
                                laCelda = RowDetIntegra.createCell(i);
                        }
                        if (!"".equalsIgnoreCase((String) arrAdecua.get(i))) {
                            if (arrColTipo[i] == 3 || arrColTipo[i] == 2) {
                                CellType eltipo = laCelda.getCellType();
                                if (eltipo != CellType.FORMULA) {
                                    // 2 = formula, 0 = numerico, 1 = string
                                    String laFuncion = "";
                                    if (eltipo == CellType.STRING) {
                                        laFuncion = laCelda.getStringCellValue();
                                    }
                                    {
                                        String esVacio = "";
                                        if (eltipo.toString() == "STRING") {
                                            esVacio = laCelda.getStringCellValue();
                                        }
                                        if (esVacio == "") {
                                            laCelda.setCellValue(Double.parseDouble((String) arrAdecua.get(i)));
                                            //laCelda.setCellType(Cell.CELL_TYPE_NUMERIC);
                                            CellStyle cellStyle = laCelda.getCellStyle();
                                            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                                            laCelda.setCellStyle(cellStyle);
                                        } else {
                                            laCelda.setCellValue("");
                                        }
                                    }
                                }
                            } else {
                                //CellStyle cellStyle = wb.createCellStyle();
                                //cellStyle.setFont(font);
                                //laCelda.setCellStyle(cellStyle);
                                if (!"".equalsIgnoreCase((String) arrAdecua.get(i)))
                                    laCelda.setCellValue((String) arrAdecua.get(i));
                            }
                        }
                    }
                    if (i == colEspacio)
                        ctaAnterior = (String) arrAdecua.get(i);
                    i++;
                }
                arrAdecua = null;
                j++;
                nrengXLS++;
            }
        }
        //coloca pie del reporte
        nrengXLS++;
        //Para que recalcule las fórmulas
        //ultRow = sheet.getLastRowNum();
        //int ultCol = 100;
        int i = 1;
        if (request.getParameter("rowDet") != null)
            j = Integer.parseInt(request.getParameter("rowDet"));
        else
            j = 10;
        try {
            while (j <= ultRow) {
                Row RowDetIntegra = sheet.getRow(j);
                i = 1;
                while (i < numberOfColumns) {
                    laCelda = RowDetIntegra.getCell(i);
                    if (laCelda != null) {
                        CellType eltipo = laCelda.getCellType();
                        if (eltipo == CellType.NUMERIC) {
                            // 2 = formula, 0 = numerico, 1 = string
                            laCelda.setCellFormula(laCelda.getCellFormula());
                            //						String algo = String.valueOf(laCelda.);
                            //						String nada = algo;
                        }
                    }
                    i++;
                }
                j++;
            }
        } catch (Exception hazNada) {
            log.info("Object: {}", "En recalculando fórmulas i = '" + i + "', j = '" + j + "'");
        }
    }
}
