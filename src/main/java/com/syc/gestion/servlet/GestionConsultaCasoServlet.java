package com.syc.gestion.servlet;

import java.io.IOException;
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
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "GestionConsultaCasoServlet", urlPatterns = { "/caso/show-caso" })
public class GestionConsultaCasoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionConsultaCasoServlet.class);

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

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            resp.sendRedirect("../index.jsp");
            // <script language="javascript">self.top.location.href =
            // "../index.jsp";</script>
            return;
        }
        try {
            Caso c = ejecutaCaso(req);
            session.setAttribute(ATT_CASE, c);
            resp.sendRedirect("../caso/exec-container.jsp?search=true&Consulta=true");
        } catch (GestionException exc) {
            log.error("Ejecuntando Caso", exc);
            throw new ServletException(exc);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private Caso ejecutaCaso(HttpServletRequest req) throws GestionException {
        HttpSession session = req.getSession();
        String idCaso = req.getParameter(PRM_CASE);
        String idCasoOper = req.getParameter(PRM_CASE_OPER);
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        if (idCaso == null) {
            log.error("Llamada invalida, sin identificador de caso");
            throw new GestionException("Llamada inválida, sin identificador de caso");
        }
        if (idCasoOper == null) {
            log.error("Llamada invalida, sin identificador de caso operacion");
            throw new GestionException("Llamada inválida, sin identificador de caso operación");
        }
        int id_caso = Integer.parseInt(idCaso);
        int id_caso_oper = Integer.parseInt(idCasoOper);
        if (id_caso <= 0) {
            log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
        }
        if (id_caso_oper <= 0) {
            log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
        }
        Caso c = casoTx.consultaCaso(id_caso, id_caso_oper);
        ITree tree = casoTx.getArbolCaso(c);
        session.setAttribute(ATT_TREE, tree);
        return c;
    }
}
