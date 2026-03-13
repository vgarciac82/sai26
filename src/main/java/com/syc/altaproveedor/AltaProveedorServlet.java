package com.syc.altaproveedor;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "AltaProveedorServlet", urlPatterns = { "/ModificaProveedor" })
public class AltaProveedorServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(AltaProveedorServlet.class);

    private String jniName = null;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendError(resp, "Sin usuario en session. Por favor reingrese al sistema.");
            return;
        }
        String accion = req.getParameter("accion");
        if ("ModificaProveedor".equals(accion)) {
            AltaProveedorBusinessLogic abl = new AltaProveedorBusinessLogic(jniName);
            String folioSAI = req.getParameter("folioSAI");
            try {
                Caso c = abl.generaCasoModificaProveedor(u.getLogin(), folioSAI);
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                session.setAttribute("OP_CAPTURA_ESTIMACION", "true");
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            }
        } else if ("CtasBancarias".equals(accion)) {
            AltaProveedorBusinessLogic abl = new AltaProveedorBusinessLogic(jniName);
            String folioSAI = req.getParameter("folioSAI");
            try {
                Caso c = abl.generaCasoCtaBancaria(u.getLogin(), folioSAI);
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                session.setAttribute("OP_CAPTURA_ESTIMACION", "true");
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            }
        } else if ("ModificaTipoPersona".equals(accion)) {
            AltaProveedorBusinessLogic abl = new AltaProveedorBusinessLogic(jniName);
            String folioSAI = req.getParameter("folioSAI");
            try {
                Caso c = abl.generaCasoModificaTipoPersona(u.getLogin(), folioSAI);
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                session.setAttribute("OP_CAPTURA_ESTIMACION", "true");
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            }
        }
    }

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
}
