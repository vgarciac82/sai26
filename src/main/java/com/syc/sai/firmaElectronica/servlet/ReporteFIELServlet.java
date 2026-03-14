package com.syc.sai.firmaElectronica.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
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
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.axtel.contratos.Requisition;
import com.axtel.contratos.core.RequisitionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.FirmaElectronicaReporte;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.firmaElectronica.core.ReporteFIELBussinessLogic;
import com.syc.sai.firmaElectronica.exceptions.AutRecepcionMaterialException;
import com.syc.sai.firmaElectronica.exceptions.EstimacionObraException;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteFIELServlet", urlPatterns = { "/firma/MuestraReporte", "/rm/MuestraRM", "/rm/MuestraAnexo1A", "/rm/MuestraEstimacion", "/rm/requisitionFiles" })
public class ReporteFIELServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -6458114453295516599L;

    private String jniName;

    private static final Logger log = LoggerFactory.getLogger(ReporteFIELServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                log.warn("No hay sesion");
                setMainPage(resp);
                return;
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                log.warn("No hay Usuario en sesion");
                session.invalidate();
                setMainPage(resp);
                return;
            }
            String tituloAplicacion = StringUtils.trimToEmpty(req.getParameter("documento"));
            if ("CONTRATODIVERSO".equals(tituloAplicacion)) {
                descargaNotaAutorizacion(req, resp);
            } else if ("APARTADO".equals(tituloAplicacion)) {
                descargaArchivosApartado(req, resp);
            } else if ("OBRAPUBLICA".equals(tituloAplicacion)) {
                descargaNotaEstimacionAut(req, resp);
            } else if ("CONCILIABANCOS".equals(tituloAplicacion)) {
                descargaConciliacion(req, resp);
            } else if ("ENTERASATISFACCION".equals(tituloAplicacion)) {
                String nDocto = StringUtils.trimToEmpty(req.getParameter("nDocto"));
                if ("1".equals(nDocto)) {
                    descargaAnexo1A(req, resp);
                } else if ("2".equals(nDocto)) {
                    descargaActaHechos(req, resp);
                }
            } else {
                descargaReporte(req, resp);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                Util.sendHTMLErrorMsg(resp, e);
            } catch (Exception e2) {
                log.warn("No se pudo notificar la causa de la excepcion: " + e2, e2);
            }
        }
    }

    private void descargaArchivosApartado(HttpServletRequest req, HttpServletResponse resp) throws ServletException, NumberFormatException, SQLException, IOException {
        String folioNota = req.getParameter("f");
        String idGabinete = req.getParameter("g");
        String fileName = req.getParameter("docName");
        if (folioNota == null || idGabinete == null) {
            log.warn("No se recibio folio/gabinete de archivo a descargar.");
            throw new ServletException("No se recibio folio/gabinete de archivo a descargar.");
        }
        QuestionnaireBussinessLogic qf = new QuestionnaireBussinessLogic();
        String pathReporte = qf.getPathFile(Integer.parseInt(folioNota), Integer.parseInt(idGabinete), fileName);
        if (!StringUtils.isBlank(pathReporte)) {
            doDownload(resp, pathReporte, fileName + ".pdf");
        }
    }

    private void descargaNotaAutorizacion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, NumberFormatException, SQLException, IOException, AutRecepcionMaterialException, ParseException {
        String folioNota = req.getParameter("f");
        if (folioNota == null) {
            log.warn("No se recibio folio de nota a descargar.");
            throw new ServletException("No se recibio folio de nota a descargar.");
        }
        ReporteFIELBussinessLogic rfbl = new ReporteFIELBussinessLogic(jniName);
        String pathReporte = rfbl.getPathNotaRM(Integer.parseInt(folioNota));
        if (!StringUtils.isBlank(pathReporte)) {
            doDownload(resp, pathReporte, "ReporteFirmaElectronica.pdf");
        }
    }

    private void descargaAnexo1A(HttpServletRequest req, HttpServletResponse resp) throws ServletException, NumberFormatException, SQLException, IOException, AutRecepcionMaterialException, ParseException {
        String folioNota = req.getParameter("f");
        if (folioNota == null) {
            log.warn("No se recibio folio del Anexo 1A a descargar.");
            throw new ServletException("No se recibio folio del Anexo 1A a descargar.");
        }
        ReporteFIELBussinessLogic rfbl = new ReporteFIELBussinessLogic(jniName);
        String pathReporte = rfbl.getPathAnexo1A(Integer.parseInt(folioNota));
        doDownload(resp, pathReporte, "ReporteAnexo1AFirmaElectronica.pdf");
    }

    private void descargaActaHechos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, NumberFormatException, SQLException, IOException, AutRecepcionMaterialException, ParseException {
        String folioNota = req.getParameter("f");
        if (folioNota == null) {
            log.warn("No se recibio folio del Acta de Hechos a descargar.");
            throw new ServletException("No se recibio folio del Acta de Hechos a descargar.");
        }
        ReporteFIELBussinessLogic rfbl = new ReporteFIELBussinessLogic(jniName);
        String pathReporte = rfbl.getPathActaHechos(Integer.parseInt(folioNota));
        if (!"".equalsIgnoreCase(pathReporte))
            doDownload(resp, pathReporte, "ReporteActaHechosFirmaElectronica.pdf");
    }

    private void descargaNotaEstimacionAut(HttpServletRequest req, HttpServletResponse resp) throws AutRecepcionMaterialException, IOException, NumberFormatException, SQLException, EstimacionObraException, ParseException {
        String folioNota = req.getParameter("f");
        if (folioNota == null) {
            log.warn("No se recibio folio de nota a descargar.");
            throw new AutRecepcionMaterialException("No se recibio folio de nota a descargar.");
        }
        ReporteFIELBussinessLogic rfbl = new ReporteFIELBussinessLogic(jniName);
        String pathReporte = rfbl.getPathNotaEstimacion(Integer.parseInt(folioNota));
        doDownload(resp, pathReporte, "ReporteFirmaElectronica.pdf");
    }

    private void descargaReporte(HttpServletRequest req, HttpServletResponse resp) throws NumberFormatException, Exception {
        String folioReporte = req.getParameter("f");
        if (folioReporte == null) {
            log.warn("No se recibio parametro 'folioReporte'");
            throw new ServletException("No se recibio parametro 'select'");
        }
        String tipoReporte = StringUtils.isBlank(req.getParameter("T")) ? FirmaElectronicaReporte.REPORTE : req.getParameter("T");
        ReporteFIELBussinessLogic rfbl = new ReporteFIELBussinessLogic(jniName);
        String pathReporte = rfbl.getPathReporte(Integer.parseInt(folioReporte), tipoReporte);
        doDownload(resp, pathReporte, "ReporteFirmaElectronica.pdf");
    }

    private void descargaConciliacion(HttpServletRequest req, HttpServletResponse resp) throws NumberFormatException, Exception {
        String folioReporte = req.getParameter("f");
        if (folioReporte == null) {
            log.warn("No se recibio parametro 'folioReporte'");
            throw new ServletException("No se recibio parametro 'select'");
        }
        String tipoReporte = StringUtils.isBlank(req.getParameter("T")) ? FirmaElectronicaReporte.REPORTE : req.getParameter("T");
        ReporteFIELBussinessLogic rfbl = new ReporteFIELBussinessLogic(jniName);
        String pathReporte = rfbl.getPathConciliacion(Integer.parseInt(folioReporte), tipoReporte);
        doDownload(resp, pathReporte, "ReporteFirmaElectronica.pdf");
    }

    private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {
        int length = 0;
        File f = new File(filename);
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(original_filename);
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");
        // 10K buffer
        byte[] bbuf = new byte[10 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
        in.close();
        out.flush();
        out.close();
    }

    private void setMainPage(HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<script language=\"javascript\">self.top.location.href=\"../index.jsp\";</script>");
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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
