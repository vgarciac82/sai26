/**
 */
package com.syc.sai.firmaElectronica.servlet;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Servlet que recibe solicitud para firmar nuevamente un tramite.
 *
 * @author Ana
 */
@WebServlet(name = "SolicitaRefirma", urlPatterns = { "/fiel/SolicitaRefirma" })
public class ReFirmaDocumentoServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Log del sistema
     */
    private static final Logger log = LoggerFactory.getLogger(ReFirmaDocumentoServlet.class);

    private String jniName;

    private static String TEMP_DIR;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String document = req.getParameter("DOCUMENT");
            String folio = req.getParameter("FOLIO");
            String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
            HttpSession session = req.getSession(false);
            if (session == null) {
                log.warn("No hay sesion");
                return;
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(jniName);
            SolicitudFirmaElectronica sfe = febl.instanceFromWeb(document);
            sfe.setFolios(folio);
            sfe.setReportPath(reportPath);
            sfe.setRefirma(true);
            sfe.setUsuario(u);
            String result = febl.reFirmaDocumento(sfe);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "/temp/firmaElectronica/";
                log.info("Object: {}", "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "/temp/firmaElectronica/";
            log.info("Error occurred", "Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("Object: {}", "No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
    }
}
