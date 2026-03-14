package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ConsultaSaldosBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ExportaMovimientosServlet", urlPatterns = { "/reportes/exportaMovimientos" })
public class ExportaMovimientosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ExportaMovimientosServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    //	@SuppressWarnings("unused")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        ConsultaSaldosBusinessLogic csbl = new ConsultaSaldosBusinessLogic(jniName);
        try {
            csbl.generaReporteMovimientos(req, resp, plantillas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("REPCONMOVIMIENTOS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Movimientos.xlsx"));
            }
        }
    }
}
