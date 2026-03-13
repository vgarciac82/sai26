package com.syc.pasivoscontingentes;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.pasivoscontingentes.PasivosContingentesBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AltaPasivosContingentesServlet", urlPatterns = { "/reportes/AltaPasivosContingentes" })
public class AltaPasivosContingentesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(AltaPasivosContingentesServlet.class);

    private static String jniName = "jdbc/gestion";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msg = "";
        String RFC = req.getParameter("cRFC");
        String esPat = req.getParameter("tipoPasivoChk");
        String PC = req.getParameter("cSubcuenta");
        try {
            if (session == null) {
                session = req.getSession(true);
                throw new ServletException("Su session a caducado");
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new ServletException("Su session a caducado");
            PasivosContingentesBusinessLogic rrs = new PasivosContingentesBusinessLogic(jniName);
            String fAplicacion = req.getParameter("fecha_aplicacion");
            String reportPath = req.getParameter("reportPath");
            if (StringUtils.isEmpty(fAplicacion))
                throw new Exception("No se recibio la fecha de aplicacion.");
            //laboral no genera poliza de alta
            if ("1".equals(esPat)) {
                int nFolioDocPoliza = rrs.generaPolizaAlta(fAplicacion, u.getU_UR(), u.getPropiedad("CCENTROCONTABLE").getValor(), u.getLogin(), u.getNombre(), RFC, PC, reportPath, u);
                msg = "Se genero exitosamente la poliza de alta con folio: " + nFolioDocPoliza + ". Con Firma Electronica.";
            } else
                msg = "Se dio de alta el pasivo correctamente";
            session.setAttribute("RESULT", msg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = "Ocurrio el siguiente error: " + e.getMessage();
            session.setAttribute("RESULT", msg);
        }
        resp.sendRedirect("../Generador/AperturaPasivoContingentePatrimonial.jsp");
        return;
    }
}
