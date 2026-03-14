package com.syc.contable.servlet;

import java.io.IOException;
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
import com.syc.gestion.util.Util;
import com.syc.reportes.ReporteConacBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "LibrosContablesServlet", urlPatterns = { "/LibroBalance" })
public class LibrosContablesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -8911823000159443289L;

    private String jniName;

    private static final Logger log = LoggerFactory.getLogger(LibrosContablesServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null)
                throw new ServletException("No se encontro sesion activa. Vuelva a ingresar al sistema.");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new ServletException("No se encontro sesion activa. Vuelva a ingresar al sistema.");
            String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            log.info("Object: {}", "Generando reporte: " + accion);
            if ("LibroBalance".equals(accion)) {
                String path = null;
                try {
                    boolean acumulado = !StringUtils.isBlank(req.getParameter("acumulado"));
                    int mes = Integer.parseInt(req.getParameter("mesLibro"));
                    String centroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
                    ReporteConacBusinessLogic rcbl = new ReporteConacBusinessLogic(jniName);
                    path = rcbl.generaLibroBalance(centroContable, mes, acumulado);
                    Util.doDownload(resp, path, "Libro de Balance.pdf", "application/pdf");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new ServletException(e);
                } finally {
                    Util.deleteFile(path);
                }
            }
        } catch (ServletException e) {
            resp.setContentType("text/html");
            ServletOutputStream out = resp.getOutputStream();
            out.println("<html>");
            out.println("<body>");
            out.println("<H1>Ocurrio el siguiente error mientras se generaba el reporte:</H1></br>");
            out.println("<textarea cols=\"80\" rows=\"15\">");
            out.println(e.toString());
            out.println("</textarea>");
            out.println("<br><input type=\"button\" value=\"Aceptar\" onclick=\"javascript:window.close();\">");
            out.println("</body>");
            out.println("</html>");
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
    }
}
