package com.syc.contratosplurianuales;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "SolicitudContratoPlurianualServlet", urlPatterns = { "/plurianuales/SolicitudPlurianual" })
public class SolicitudContratoPlurianualServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 126320606200256721L;

    private static final Logger log = LoggerFactory.getLogger(SolicitudContratoPlurianualServlet.class);

    private String jniName = "";

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separatorChar + "ConBanTMP" + File.separatorChar;

    private static Map<String, String> plantillas = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("FormatoSolContratoPLU", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_SOLContratoPLU.xlsx"));
                plantillas.put("FormatoSolContratoPLU4", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_SOLContratoPLU2.xlsx"));
                plantillas.put("FormatoSolContratoPLU5", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_SOLContratoPLU3.xlsx"));
                plantillas.put("FormatoSolContratoPLU6", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_SOLContratoPLU4.xlsx"));
                plantillas.put("FormatoSolContratoPLU7", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_SOLContratoPLU5.xlsx"));
            }
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se logro crear la carpeta temporal " + TEMP_DIR + " . Notifique a soporte.");
        } catch (Exception e) {
            throw new ServletException(e);
        }
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            generaExcelSolicitud(req, resp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ServletOutputStream out = resp.getOutputStream();
            out.println("Ocurrio el siguiente eror mientras se generaba el reporte:<br>");
            out.println(e.getMessage());
            out.println("<br>");
            out.println(e.toString());
            out.println("<br>Intente nuevamente. Si el problema persiste notifique al administrador del sistema");
            out.flush();
            out.close();
            return;
        }
    }

    private void generaExcelSolicitud(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        Usuario u = null;
        String msgError = "";
        if (session == null) {
            msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
            }
        }
        if (StringUtils.isEmpty(msgError)) {
            SolicitudContratoPlurianualBusinessLogic babl = new SolicitudContratoPlurianualBusinessLogic(jniName);
            babl.generaExcelSolicitud(req, resp, plantillas);
        } else {
            throw new Exception("Ocurrio el siguiente error generando el archivo Excel: " + msgError);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = "";
        try {
            String reportPath1 = getServletContext().getRealPath("Reportes" + File.separator);
            HttpSession session = req.getSession(false);
            if (session == null)
                throw new Exception("La sesion ha terminado. Por favor ingrese nuevamente al sistema.");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new Exception("La sesion ha terminado. Por favor ingrese nuevamente al sistema.");
            String fileName = "ArchivosPluris_" + System.currentTimeMillis() + ".zip";
            String folio = (req.getParameter("folio"));
            int nFolioContratoPlurianual = Integer.parseInt(req.getParameter("nFolioContratoPlurianual"));
            int cEsModificado = Integer.parseInt(req.getParameter("cEsModificado"));
            int esOriginal = Integer.parseInt(req.getParameter("esOriginal"));
            resp.setContentType("application/zip");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + fileName + "\";");
            ServletOutputStream out = resp.getOutputStream();
            SolicitudContratoPlurianualBusinessLogic scpl = new SolicitudContratoPlurianualBusinessLogic(jniName);
            scpl.exportaReportes(reportPath1, fileName, folio, cEsModificado, nFolioContratoPlurianual, esOriginal, out);
        } catch (Exception e) {
            msg = e.toString();
        }
    }
}
