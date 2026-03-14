package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.naming.InitialContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import com.syc.fortimax.core.ExpedientBussinessLogic;
import com.syc.fortimax.core.ExpedientNode;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.TipoCaso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ExpedientManager", urlPatterns = { "/fortimax/documents" })
public class ExpedientServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -1934704829306609107L;

    private static final Logger log = LoggerFactory.getLogger(ExpedientServlet.class);

    private static final String SEND_TREE = "send_tree";

    private static final String GET_DOC_LIST = "get_doc_list";

    private String jniName;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        try {
            if (session == null)
                throw new ServletException("Su sesion ha terminado. Ingrese nuevamente al sistema.");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new ServletException("Su sesion ha terminado. Ingrese nuevamente al sistema.");
            String action = req.getParameter("accion");
            if (SEND_TREE.equals(action)) {
                ExpedientBussinessLogic ebl = new ExpedientBussinessLogic(jniName);
                ebl.setCaso((Caso) session.getAttribute(ATT_CASE));
                List<ExpedientNode> nodes = ebl.getExpedientNodes();
                JSONArray arr = new JSONArray();
                if (nodes != null)
                    for (ExpedientNode node : nodes) {
                        arr.put(Util.toJson(node));
                    }
                JSONObject nodos = new JSONObject("{nodos:" + arr + "}");
                resp.setContentType("application/json;charset=UTF-8");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.println(nodos.toString());
                out.flush();
                out.close();
            } else if (GET_DOC_LIST.equals(action)) {
                ExpedientBussinessLogic ebl = new ExpedientBussinessLogic(jniName);
                String folio = req.getParameter("folio");
                CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
                TipoCaso tc = new TipoCaso();
                tc.setGavetaAsociada(folio);
                Caso c = cbl.getCaso(folio);
                ebl.setCaso(c);
                List<ExpedientNode> nodes = ebl.getExpedientNodes();
                session.setAttribute(GestionInterface.ATT_TREE, cbl.getArbolCaso(c));
                JSONArray arr = new JSONArray();
                if (nodes != null)
                    for (ExpedientNode node : nodes) {
                        arr.put(Util.toJson(node));
                    }
                JSONObject nodos = new JSONObject("{nodos:" + arr + "}");
                resp.setContentType("application/json;charset=UTF-8");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.println(nodos.toString());
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseSender.sendError(resp, e);
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
        } catch (Exception exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
