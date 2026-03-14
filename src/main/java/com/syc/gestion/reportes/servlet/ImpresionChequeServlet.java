package com.syc.gestion.reportes.servlet;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.ejercido.pagado.manual.ImpresionChequeBusinessLogic;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ImpresionCheque", urlPatterns = { "/reportes/ImpresionCheque" })
public class ImpresionChequeServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -6810376574978594871L;

    String jniName;

    private static final Logger log = LoggerFactory.getLogger(ImpresionChequeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String CXP = req.getParameter("folio");
        String tipo = req.getParameter("tipo");
        String usuario = req.getParameter("usuario");
        String beneficiario_temp = req.getParameter("BeneficiarioNuevo");
        log.info("Object: {}", usuario);
        log.info("Object: {}", tipo);
        log.info("Object: {}", CXP);
        log.info("Object: {}", beneficiario_temp);
        String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "ChequeManual.jasper");
        String ruta = getServletContext().getRealPath("Reportes");
        reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
        try {
            if ("3".equals(tipo)) {
                try {
                    ImpresionChequeBusinessLogic icbl = new ImpresionChequeBusinessLogic(jniName);
                    int nvoFolio = icbl.reemplazaCheque(Integer.parseInt(CXP, 10));
                    ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(nvoFolio));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    ResponseSender.sendClientSimpleMessage(resp, false, ("Ocurrio el siguiente error mientras se reemplazaba el cheque:\n" + e).replaceAll("\n", "\\\\n"));
                }
            } else {
                objReporte.reporteChequeManual(req, resp, reportPath, ruta, CXP, tipo, usuario, beneficiario_temp);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

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
