package com.syc.egresos.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.egresos.ResponseJSON;
import com.syc.egresos.firmante.FirmanteBussinessLogic;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "FirmanteServlet", urlPatterns = { "/firmante/save" })
public class FirmanteServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -1182925380152047296L;

    private static final Logger log = Logger.getLogger(FirmanteServlet.class);

    private String jniName;

    private static final String GUARDA_FIRMANTES = "save";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("Su sesion a caducado. Ingrese nuevamente al sistema ");
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                throw new Exception("Su sesion a caducado. Ingrese nuevamente al sistema ");
            }
            Caso c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null) {
                throw new Exception("Su sesion a caducado. Ingrese nuevamente al sistema ");
            }
            String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            if (GUARDA_FIRMANTES.equals(accion)) {
                FirmanteBussinessLogic fbl = new FirmanteBussinessLogic(jniName);
                List<Firmante> firmantes = fbl.readFromRequest(req);
                String tipoPago = req.getParameter("cTipoPago");
                int folioPago = Integer.parseInt(req.getParameter("nFolioPago"));
                boolean esFirmaElectronica = (req.getParameter("firmaElectronica") != null);
                int guardados = fbl.saveFirmantes(firmantes, tipoPago, folioPago, esFirmaElectronica);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(guardados) })));
                sendJSONResponse(resp, responseJSON);
            }
        } catch (Exception e) {
            log.error("Ocurrio el siguiente error: " + e, e);
            List<String> errores = new ArrayList<String>();
            errores.add(e.toString());
            ResponseJSON responseJSON = new ResponseJSON(false, errores, null);
            try {
                sendJSONResponse(resp, responseJSON);
            } catch (Exception e2) {
                throw new ServletException(e2);
            }
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
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }

    private void sendJSONResponse(HttpServletResponse resp, ResponseJSON responseJSON) throws Exception {
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            resp.getWriter();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            out.print(responseJSON.toJSON());
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e2) {
                log.warn("Problemas cerrando flujo: " + e2);
            }
        }
    }
}
