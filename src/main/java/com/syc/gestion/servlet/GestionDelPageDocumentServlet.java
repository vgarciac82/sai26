package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.viewer.servlet.ViewerParametersInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "GestionDelPageDocumentServlet", urlPatterns = { "/imgmng/delpagekeeper" })
public class GestionDelPageDocumentServlet extends HttpServlet implements GestionInterface, ViewerParametersInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionDelPageDocumentServlet.class);

    private String jniName = null;

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

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idxVal = null;
        idxVal = req.getParameter(INDEX_KEY);
        if (idxVal == null)
            // Nada que hacer
            return;
        int index = Integer.parseInt(idxVal);
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        String selectId = req.getParameter("select");
        if (selectId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
            return;
        }
        String urlPrefix = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        int maxIdx = 0;
        try {
            maxIdx = cbl.borraPaginaDocumento(selectId, index);
            ITree tree = cbl.getArbolCaso(c);
            session.setAttribute(ATT_TREE, tree);
        } catch (GestionException exc) {
            log.error("Borrando pagina", exc);
            throw new ServletException(exc);
        }
        PrintWriter out = resp.getWriter();
        out.println("<script type=\"text/javascript\">");
        out.println("parent.frames[\"viewerFrame\"].location.href=\"" + urlPrefix + "/imgmng/VisualizadorDeImagen.jsp?select=" + selectId + "&" + INDEX_KEY + "=" + ((index - 1) < 0 ? 0 : index - 1) + "\";");
        out.println("parent.frames[\"listFrame\"].location.href=\"" + urlPrefix + "/imgmng/ListaDeImagenes.jsp?select=" + selectId + "&" + INDEX_MAX + "=" + maxIdx + "\";");
        out.println("parent.parent.frames[\"doctree\"].location.href=\"" + urlPrefix + "/caso/document-tree.jsp?select=" + selectId + "\"");
        out.println("</script>");
        out.flush();
        out.close();
    }
}
