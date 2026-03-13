package com.syc.gestion.servlet;

import java.io.IOException;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.bitacora.BitacoraOperacionDoctosBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GestionTreeActionsServlet", urlPatterns = { "/caso/actions.jsp" })
public class GestionTreeActionsServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionTreeActionsServlet.class);

    private String jniName = null;

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

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            resp.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            log.error("Llamada invalida, sin Caso seleccionado");
            throw new ServletException("Llamada inv\u00E1lida, sin Caso seleccionado");
        }
        try {
            ITree tree = (ITree) session.getAttribute(ATT_TREE);
            String nodeId = ((ITreeNode) tree.getSelectedNodes().iterator().next()).getId();
            Map<?, ?> m = req.getParameterMap();
            /* VGC Variables para el log */
            String uLogin = u.getLogin();
            String modulo = c.getTipoCaso().getGavetaAsociada();
            int idTC = c.getIdTC();
            int nFolio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
            BitacoraOperacionDoctosBusinessLogic bodbl = new BitacoraOperacionDoctosBusinessLogic(jniName, uLogin, modulo, idTC, nFolio);
            if (m.containsKey("newfldr.x")) {
                bodbl.insertaBitacora(5, "El usuario intenta crear una nueva carpeta en el expediente");
                req.getRequestDispatcher("crear-carpeta-docto.jsp?select=" + nodeId + "&o=c").forward(req, resp);
            } else if (m.containsKey("newimg.x")) {
                bodbl.insertaBitacora(6, "El usuario intenta crear una nueva imagen en el expediente");
                req.getRequestDispatcher("crear-carpeta-docto.jsp?select=" + nodeId + "&o=d&t=i").forward(req, resp);
            } else if (m.containsKey("newdoc.x")) {
                bodbl.insertaBitacora(7, "El usuario intenta crear un nuevo documento en el expediente");
                req.getRequestDispatcher("crear-carpeta-docto.jsp?select=" + nodeId + "&o=d&t=o").forward(req, resp);
                // Agregado para validacion Firmar documento
            } else if (m.containsKey("firmdoc.x")) {
                bodbl.insertaBitacora(8, "El usuario intenta firmar un documento en el expediente");
                req.getRequestDispatcher("crear-carpeta-docto.jsp?select=" + nodeId + "&o=d&t=f").forward(req, resp);
            } else if (m.containsKey("deldoc.x") || m.containsKey("cleandoc.x")) {
                String docName = null;
                CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
                try {
                    if (m.containsKey("deldoc.x")) {
                        bodbl.insertaBitacora(9, "El usuario intenta eliminar el documento " + nodeId + " en el expediente");
                        docName = cbl.borraDocumento(nodeId);
                    } else {
                        bodbl.insertaBitacora(10, "El usuario intenta limpiar un documento " + nodeId + " en el expediente");
                        docName = cbl.versionaDocumento(bodbl, nodeId);
                    }
                    tree = cbl.getArbolCaso(c);
                    tree.select(nodeId);
                    session.setAttribute(ATT_TREE, tree);
                    session.setAttribute(ATT_MSG, "Documento " + docName + " versionado exitosamente.");
                } catch (GestionException exc) {
                    log.error("Borrando documento (" + nodeId + ")", exc);
                    session.setAttribute(ATT_MSG, exc.getMessage() == null ? exc.toString() : exc.getMessage());
                }
                req.getRequestDispatcher("resp-borra-docto.jsp?name=" + docName).forward(req, resp);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }
}
