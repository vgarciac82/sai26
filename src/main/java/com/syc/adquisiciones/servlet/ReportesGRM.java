package com.syc.adquisiciones.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.axtel.contratos.Requisition;
import com.syc.adquisiciones.businessLogic.LeeArchivosBusinessLogic;
import com.syc.adquisiciones.businessLogic.ReportesGRMBusinessLogic;
import com.syc.adquisiciones.core.DatosArchivo;
import com.syc.adquisiciones.core.DatosPedidoContrato;
import com.syc.adquisiciones.core.DatosReportesGRM;
import com.syc.adquisiciones.core.ProcedimientoSAC;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReportesGRM", urlPatterns = { "/servlet/ReportesGRM" })
public class ReportesGRM extends HttpServlet {

    private static final long serialVersionUID = 6084656926088640772L;

    private static Logger log = LoggerFactory.getLogger(ReportesGRM.class);

    private static File tempDir;

    private static String jniName = null;

    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
        }
        // Obtiene el tipo de reporte
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        log.debug("Object: {}", "operacion: " + tipoOperacion);
        switch(tipoOperacion) {
            case 0:
                reportesSIIWEB(request, response);
                break;
            case 1:
                reportesPedidosContratos(request, response);
                break;
            case 2:
                reportesPAAS(request, response);
                break;
            case 3:
                consultaDoctosContrato(request, response);
                break;
            case // download files
            4:
                downloadFiles(request, response, session);
                break;
            case // Base de datos SAC
            5:
                reportesSAC(request, response);
                break;
            case // Reportes de Requisiciones
            6:
                reportesRequisiciones(request, response);
                break;
            default:
                log.warn("Operaci\u00d3n desconocida");
                break;
        }
    }

    private void downloadFiles(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        DatosArchivo datosArchivo = null;
        int nTypeFile = 0;
        LeeArchivosBusinessLogic leeArchivobusinessLogic = null;
        String[] rutas = null;
        try {
            nTypeFile = Integer.parseInt(request.getParameter("nTypeFile"));
            datosArchivo = llenaDatosFile(request);
            leeArchivobusinessLogic = new LeeArchivosBusinessLogic();
            switch(nTypeFile) {
                case // download contrato físico
                1:
                    datosArchivo.setcTituloAplicacion("CONTRATODIVERSO");
                    datosArchivo.setcNameCarpeta("DoctosContrato");
                    datosArchivo.setcNombreArchivo("Contrato Fisico");
                    rutas = leeArchivobusinessLogic.downloadContratoFisico(datosArchivo, u);
                    if (rutas != null && !StringUtils.isBlank(rutas[0])) {
                        doDownload(response, rutas[1], rutas[0], "Contrato_Fisico.zip");
                    } else {
                        // No se encontro el documento
                        throw new IOException("Bug, No se encontró el archivo solicitado ");
                    }
                    break;
                case // download Garantia
                2:
                    datosArchivo.setcTituloAplicacion("CONTRATODIVERSO");
                    datosArchivo.setcNameCarpeta("DoctosGarantia");
                    datosArchivo.setcNombreArchivo("Garantia");
                    rutas = leeArchivobusinessLogic.downloadGarantia(datosArchivo, u, false);
                    if (rutas != null && !StringUtils.isBlank(rutas[0])) {
                        doDownload(response, rutas[1], rutas[0], "DocumentoGarantia.zip");
                    } else {
                        // No se encontro el documento
                        throw new IOException("Bug, No se encontró el archivo solicitado ");
                    }
                    break;
                case // download Endoso
                3:
                    datosArchivo.setcTituloAplicacion("CONTRATODIVERSO");
                    datosArchivo.setcNameCarpeta("DoctosGarantia");
                    datosArchivo.setcNombreArchivo("Endoso");
                    rutas = leeArchivobusinessLogic.downloadGarantia(datosArchivo, u, false);
                    if (rutas != null && !StringUtils.isBlank(rutas[0])) {
                        doDownload(response, rutas[1], rutas[0], "DocumentoEndoso.zip");
                    } else {
                        // No se encontro el documento
                        throw new IOException("Bug, No se encontró el archivo solicitado ");
                    }
                    break;
                case // download Liberación de garantia
                4:
                    datosArchivo.setcTituloAplicacion("CONTRATODIVERSO");
                    datosArchivo.setcNameCarpeta("DoctosGarantia");
                    datosArchivo.setcNombreArchivo("LiberaGarantia");
                    rutas = leeArchivobusinessLogic.downloadGarantia(datosArchivo, u, false);
                    if (rutas != null && !StringUtils.isBlank(rutas[0])) {
                        doDownload(response, rutas[1], rutas[0], "GarantiaLiberada.zip");
                    } else {
                        // No se encontro el documento
                        throw new IOException("Bug, No se encontró el archivo solicitado ");
                    }
                    break;
                default:
                    log.warn("No existe el tipo de descarga de archivo");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Bug : " + e.toString());
        } finally {
            leeArchivobusinessLogic = null;
            datosArchivo = null;
        }
    }

    private void consultaDoctosContrato(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatosPedidoContrato datos = null;
        ReportesGRMBusinessLogic business;
        boolean resp = false;
        File file = null;
        ServletOutputStream out = null;
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = null;
        try {
            business = new ReportesGRMBusinessLogic();
            datos = llenaDatosPedidoContrato(request);
            switch(datos.getnTipoReporte()) {
                case // reporte de contratos físicos cargados
                1:
                    file = business.reporteContratosFisicos(datos);
                    mimetype = context.getMimeType(file.getName());
                    resp = true;
                    break;
                default:
                    log.warn("No existe el tipo de reporte");
                    break;
            }
            if (resp) {
                response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                response.addHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"; ");
                out = response.getOutputStream();
                Util.doDownload(out, file.getAbsolutePath(), file.getName(), "");
            }
        } catch (Exception e) {
            resp = false;
            log.error("Object: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            out.flush();
            if (out != null) {
                out.close();
            }
            if (!file.delete())
                file.deleteOnExit();
            out = null;
            datos = null;
            business = null;
        }
    }

    private void reportesSIIWEB(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatosReportesGRM datos = null;
        ReportesGRMBusinessLogic business;
        boolean resp = false;
        File file = null;
        ServletOutputStream out = null;
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = null;
        try {
            business = new ReportesGRMBusinessLogic();
            datos = llenaDatos(request);
            switch(datos.getnTipoReporte()) {
                case 1:
                    file = business.generaFormato1120(datos);
                    mimetype = context.getMimeType(file.getName());
                    resp = true;
                    break;
                default:
                    log.warn("No existe el tipo de reporte");
                    break;
            }
            if (resp) {
                response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                response.addHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"; ");
                out = response.getOutputStream();
                Util.doDownload(out, file.getAbsolutePath(), file.getName(), mimetype);
            }
        } catch (Exception e) {
            resp = false;
            log.error("Object: {}", e.getMessage());
        } finally {
            out.flush();
            if (out != null) {
                out.close();
            }
            if (!file.delete())
                file.deleteOnExit();
            out = null;
            datos = null;
            business = null;
        }
    }

    private void reportesPedidosContratos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatosPedidoContrato datos = null;
        ReportesGRMBusinessLogic business;
        boolean resp = false;
        File file = null;
        ServletOutputStream out = null;
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = null;
        try {
            business = new ReportesGRMBusinessLogic();
            datos = llenaDatosPedidoContrato(request);
            switch(datos.getnTipoReporte()) {
                case 1:
                    // reporte de gestion
                    file = business.generaReporteCOCODI(datos);
                    resp = true;
                    break;
                case 2:
                    file = business.generaReportePresionGasto(datos);
                    resp = true;
                    break;
                case 3:
                    file = business.generaReporte7030(datos);
                    resp = true;
                    break;
                case // reporte de pago directo
                4:
                    file = business.generaReportePagoDirecto(datos);
                    resp = true;
                    break;
                case // reporte Indicadores CNET
                5:
                    file = business.generaReporteIndicadoresCNET(datos);
                    resp = true;
                    break;
                case // reporte Totalizado
                6:
                    file = business.generaReporteTotalizado(datos);
                    resp = true;
                    break;
                case // reporte Arrendamiento
                7:
                    file = business.generaReporteArrendamiento(datos.getcNamePlantilla(), datos.getcWhere());
                    resp = true;
                    break;
                case // reporte Contratos Cap4
                8:
                    file = business.generaReporteContratosCap4(datos);
                    resp = true;
                    break;
                case // reporte de PSP´S
                9:
                    file = business.generaReportePSP(datos);
                    resp = true;
                    break;
                case // reporte de penas convencionales y deducciones al pago
                10:
                    file = business.generaReportePenasConvencionales(datos);
                    resp = true;
                    break;
                case // reporte de proveedores sacionados (incumplidos y recisión de contratos)
                11:
                    file = business.generaReporteProveedoresSancionados(datos);
                    resp = true;
                    break;
                case // reporte de proveedores sacionados (incumplidos y recisión de contratos)
                12:
                    file = business.generaReporteGarantias(datos);
                    resp = true;
                    break;
                case // reporte de Contratos con compromiso
                13:
                    file = business.generaContratosConCompromiso(datos);
                    resp = true;
                    break;
                case // reporte ENSA
                14:
                    file = business.generaReporteENSA(datos);
                    resp = true;
                    break;
                case // Formato UCAP
                15:
                    file = business.generaFormatoUCACP(datos);
                    resp = true;
                    break;
                default:
                    log.warn("No existe el tipo de reporte");
                    break;
            }
            if (resp) {
                mimetype = context.getMimeType(file.getName());
                mimetype = (mimetype != null) ? mimetype : "application/octet-stream";
                response.setContentType(mimetype);
                response.addHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"; ");
                out = response.getOutputStream();
                Util.doDownload(out, file.getAbsolutePath(), "ReportePedidosyContratos.xlsx", mimetype);
            }
        } catch (Exception e) {
            resp = false;
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            out.flush();
            if (out != null) {
                out.close();
            }
            if (!file.delete())
                file.deleteOnExit();
            out = null;
            datos = null;
            business = null;
        }
    }

    private void reportesPAAS(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatosPedidoContrato datos = null;
        ReportesGRMBusinessLogic business;
        boolean resp = false;
        File file = null;
        ServletOutputStream out = null;
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = null;
        try {
            business = new ReportesGRMBusinessLogic();
            datos = llenaDatosPedidoContrato(request);
            switch(datos.getnTipoReporte()) {
                case 1:
                    file = business.generaReportePAASCNET(datos);
                    mimetype = context.getMimeType(file.getName());
                    resp = true;
                    break;
                default:
                    log.warn("No existe el tipo de reporte");
                    break;
            }
            if (resp) {
                response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                response.addHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"; ");
                out = response.getOutputStream();
                Util.doDownload(out, file.getAbsolutePath(), file.getName(), "");
            }
        } catch (Exception e) {
            resp = false;
            log.error("Object: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            if (resp) {
                out.flush();
            }
            if (out != null) {
                out.close();
            }
            if (!file.delete())
                file.deleteOnExit();
            out = null;
            datos = null;
            business = null;
        }
    }

    private void reportesSAC(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ReportesGRMBusinessLogic business;
        boolean resp = false;
        File file = null;
        ServletOutputStream out = null;
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = null;
        ProcedimientoSAC datProced = null;
        try {
            business = new ReportesGRMBusinessLogic();
            datProced = llenaDatosProcedSAC(request);
            switch(datProced.getnTipoReporte()) {
                case //Reporte Semaforo SAC
                1:
                    file = business.generaSemaforoSAC(datProced);
                    resp = true;
                    break;
                default:
                    log.warn("No existe el tipo de reporte");
                    break;
            }
            if (resp) {
                mimetype = context.getMimeType(file.getName());
                mimetype = (mimetype != null) ? mimetype : "application/octet-stream";
                response.setContentType(mimetype);
                response.addHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"; ");
                out = response.getOutputStream();
                Util.doDownload(out, file.getAbsolutePath(), "ReportesSAC.xlsx", mimetype);
            }
        } catch (Exception e) {
            resp = false;
            log.error("Object: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            out.flush();
            if (out != null) {
                out.close();
            }
            if (!file.delete())
                file.deleteOnExit();
            out = null;
            datProced = null;
            business = null;
        }
    }

    private void reportesRequisiciones(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ReportesGRMBusinessLogic business;
        boolean resp = false;
        File file = null;
        ServletOutputStream out = null;
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = null;
        Requisition requi = null;
        try {
            business = new ReportesGRMBusinessLogic();
            requi = llenaDatosRequi(request);
            switch(requi.getnTipoReporte()) {
                case //Pre-Layout requisición
                1:
                    file = business.generaLAyoutRequi(requi);
                    resp = true;
                    break;
                default:
                    log.warn("No existe el tipo de reporte");
                    break;
            }
            if (resp) {
                mimetype = context.getMimeType(file.getName());
                mimetype = (mimetype != null) ? mimetype : "application/octet-stream";
                response.setContentType(mimetype);
                response.addHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"; ");
                out = response.getOutputStream();
                Util.doDownload(out, file.getAbsolutePath(), "ReportesSAC.xlsx", mimetype);
            }
        } catch (Exception e) {
            resp = false;
            log.error("Object: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            out.flush();
            if (out != null) {
                out.close();
            }
            if (!file.delete())
                file.deleteOnExit();
            out = null;
            requi = null;
            business = null;
        }
    }

    private DatosReportesGRM llenaDatos(HttpServletRequest request) throws Exception {
        DatosReportesGRM datos = new DatosReportesGRM();
        datos.setcEjercicioActual((request.getParameter("cEjercicioActual") == null ? "" : request.getParameter("cEjercicioActual")));
        datos.setcEjercicioAnterior((request.getParameter("cEjercicioAnterior") == null ? "" : request.getParameter("cEjercicioAnterior")));
        datos.setcFechaFin((request.getParameter("fFechaFin") == null ? "" : request.getParameter("fFechaFin")));
        datos.setcFechaInicio((request.getParameter("fFechaInicio") == null ? "" : request.getParameter("fFechaInicio")));
        datos.setcMes((request.getParameter("cMes") == null ? "" : request.getParameter("cMes")));
        datos.setcNameDB((request.getParameter("cNameDB") == null ? "" : request.getParameter("cNameDB")));
        datos.setcNamePlantilla((request.getParameter("reporteNombre") == null ? "" : getServletContext().getRealPath("Reportes" + File.separator + request.getParameter("reporteNombre"))));
        datos.setnTipoReporte((request.getParameter("nTipoReporte") == null || "".equalsIgnoreCase(request.getParameter("nTipoReporte")) ? -1 : Integer.parseInt(request.getParameter("nTipoReporte"))));
        return datos;
    }

    private DatosPedidoContrato llenaDatosPedidoContrato(HttpServletRequest request) throws Exception {
        DatosPedidoContrato datos = new DatosPedidoContrato();
        datos.setcWhere((request.getParameter("where") == null ? "" : request.getParameter("where")));
        datos.setcFechaFin((request.getParameter("fechaFin") == null ? "" : request.getParameter("fechaFin")));
        datos.setcFechaInicio((request.getParameter("fechaInicio") == null ? "" : request.getParameter("fechaInicio")));
        datos.setcNamePlantilla((request.getParameter("reporteNombre") == null ? "" : getServletContext().getRealPath("Reportes" + File.separator + request.getParameter("reporteNombre"))));
        datos.setnTipoReporte((null == request.getParameter("nTipoReporte") || "".equalsIgnoreCase(request.getParameter("nTipoReporte")) ? -1 : Integer.parseInt(request.getParameter("nTipoReporte"))));
        datos.setnTipoIngreso((null == request.getParameter("nTipoIngreso") || "".equalsIgnoreCase(request.getParameter("nTipoIngreso")) ? -1 : Integer.parseInt(request.getParameter("nTipoIngreso"))));
        datos.setcUnidadEjecutora((request.getParameter("cUnidadE") == null ? "" : request.getParameter("cUnidadE")));
        datos.setJniName(jniName);
        return datos;
    }

    private DatosArchivo llenaDatosFile(HttpServletRequest request) throws Exception {
        DatosArchivo datosArchivo = new DatosArchivo();
        datosArchivo.setcContratoCNET((request.getParameter("cContratoCNET") == null ? "" : request.getParameter("cContratoCNET")));
        datosArchivo.setcFolio((request.getParameter("cFolio") == null ? "" : request.getParameter("cFolio")));
        datosArchivo.setcIdContratoDefinitivo((request.getParameter("cContratoDefinitivo") == null ? "" : request.getParameter("cContratoDefinitivo")));
        datosArchivo.setnIdCaso((null == request.getParameter("nIdCaso") || "".equalsIgnoreCase(request.getParameter("nIdCaso")) ? -1 : Integer.parseInt(request.getParameter("nIdCaso"))));
        return datosArchivo;
    }

    private ProcedimientoSAC llenaDatosProcedSAC(HttpServletRequest request) throws Exception {
        ProcedimientoSAC datos = new ProcedimientoSAC();
        datos.setcFechaFin((request.getParameter("fechaFin") == null ? "" : request.getParameter("fechaFin")));
        datos.setcFechaInicio((request.getParameter("fechaInicio") == null ? "" : request.getParameter("fechaInicio")));
        datos.setcNamePlantilla((request.getParameter("reporteNombre") == null ? "" : getServletContext().getRealPath("Reportes" + File.separator + request.getParameter("reporteNombre"))));
        datos.setnTipoReporte((null == request.getParameter("nTipoReporte") || "".equalsIgnoreCase(request.getParameter("nTipoReporte")) ? -1 : Integer.parseInt(request.getParameter("nTipoReporte"))));
        return datos;
    }

    private Requisition llenaDatosRequi(HttpServletRequest request) throws Exception {
        Requisition requi = new Requisition();
        requi.setIdSolicitud(request.getParameter("cIdSolicitud"));
        requi.setcNamePlantilla((request.getParameter("reporteNombre") == null ? "" : getServletContext().getRealPath("Reportes" + File.separator + request.getParameter("reporteNombre"))));
        requi.setnTipoReporte((null == request.getParameter("nTipoReporte") || "".equalsIgnoreCase(request.getParameter("nTipoReporte")) ? -1 : Integer.parseInt(request.getParameter("nTipoReporte"))));
        return requi;
    }

    public static void writeXLSXFile(String excelFileName) throws IOException {
        // name of sheet
        String sheetName = "Sheet1";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        // iterating r number of rows
        for (int r = 0; r < 5; r++) {
            XSSFRow row = sheet.createRow(r);
            // iterating c number of columns
            for (int c = 0; c < 5; c++) {
                XSSFCell cell = row.createCell(c);
                cell.setCellValue("Cell " + r + " " + c);
            }
        }
        FileOutputStream fileOut = new FileOutputStream(excelFileName);
        // write this workbook to an Outputstream.
        wb.write(fileOut);
        if (wb != null) {
            wb.close();
        }
        wb = null;
        fileOut.flush();
        fileOut.close();
    }

    private void doDownload(HttpServletResponse resp, String filename, String original_filename, String nameDocto) throws IOException {
        // No eliminar el archivo "podria estar eliminando archivos del
        // fortimax"
        int length = 0;
        File f = new File(filename);
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(original_filename);
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        // resp.addHeader("Content-Disposition", "attachment; filename=\"" +
        // original_filename + "\";");
        // resp.addHeader("Content-Disposition", "attachement; filename=\"" +
        // original_filename + "\";");
        resp.addHeader("Content-Disposition", "inline; filename=\"" + nameDocto + "\";");
        // 5K buffer
        byte[] bbuf = new byte[5 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
        in.close();
        out.flush();
        out.close();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "upload");
        if (!tempDir.exists())
            if (!tempDir.mkdirs())
                throw new ServletException("No se pudo crear el directorio " + tempDir.getName());
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
