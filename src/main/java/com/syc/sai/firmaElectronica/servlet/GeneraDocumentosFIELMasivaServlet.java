package com.syc.sai.firmaElectronica.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.LogManager;
import com.axtel.egresos.core.MasiveOperation;
import com.syc.contable.RelacionGastosBussinessLogic;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "generateMasiveDocs", urlPatterns = { "/fiel/generateMasiveDocs" })
public class GeneraDocumentosFIELMasivaServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 8530867307056513367L;

    private static final Logger log = LogManager.getLogger(GeneraDocumentosFIELMasivaServlet.class);

    private String reportPath;

    private RelacionGastosBussinessLogic rgbl = new RelacionGastosBussinessLogic(GestionInterface.ATT_CONEXION, null);

    private FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(GestionInterface.ATT_CONEXION);

    public String getReportPath() {
        return reportPath;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reportPath = getServletContext().getRealPath("Reportes" + File.separator);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        String msg = "";
        String folioIntegracionStr = req.getParameter("nFolioCargaMasiva");
        try {
            int folioIntegracion = Integer.parseInt(folioIntegracionStr);
            SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
            solicitudPagoPrinter.setDetail("tRelacionGastosDetalle");
            solicitudPagoPrinter.setDocument("RELACIONGASTOS");
            solicitudPagoPrinter.setField("nFolioRelacionGastos");
            solicitudPagoPrinter.setFileExtension("pdf");
            solicitudPagoPrinter.setHeader("tRELACIONGASTOSEncabezado");
            solicitudPagoPrinter.setReportPath(getReportPath());
            solicitudPagoPrinter.setUsuario(u);
            solicitudPagoPrinter.setCargaMasiva(true);
            solicitudPagoPrinter.setMasiveHeader("tRELACIONGASTOSEncabezado_temp");
            solicitudPagoPrinter.setMasiveDetail("tRELACIONGASTOSDetalle_temp");
            solicitudPagoPrinter.setMasiveField("folioTempGral");
            solicitudPagoPrinter.setMasiveID(folioIntegracion);
            solicitudPagoPrinter.setFolder("Solicitud de Pago");
            @SuppressWarnings("unchecked")
            List<MasiveOperation> rgMasivaList = (List<MasiveOperation>) rgbl.readRGMasiva(folioIntegracion);
            febl.generaArchivosFirmaMasiva(folioIntegracion, solicitudPagoPrinter, rgMasivaList, "Solicitud de Pago Firmada");
            msg = "Archivos para firma electronica generados exitosamente.";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = "Error generando archivos para firma electronica: " + e.toString();
        }
        session.setAttribute("msg", msg);
        resp.sendRedirect("../Generador/cargaRelacionGastos.jsp");
    }
}
