package com.axtel.contratos.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import com.axtel.web.exceptions.SessionExpiredException;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Servicio para operaciones con convenios de colaboracion.
 *
 * @author vicente.garcia
 */
@WebServlet(name = "ConvenioColaboracionServlet", urlPatterns = { "/contratos/registraConvenio" })
public class ConvenioColaboracionServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 5496072673639795589L;

    private static final Logger log = LoggerFactory.getLogger(ConvenioColaboracionServlet.class);

    private String jniName;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ConvenioColaboracion cc = null;
        HttpSession session = req.getSession(false);
        String msg = "";
        Caso c = null;
        Usuario u = null;
        boolean success = false;
        try {
            if (session == null) {
                throw new SessionExpiredException("Ha terminado se session. Ingrese nuevamente al sistema.");
            } else {
                c = (Caso) session.getAttribute(ATT_CASE);
                u = (Usuario) session.getAttribute(ATT_USER);
                if (c == null || u == null) {
                    throw new ServletException("Ha terminado se session. Ingrese nuevamente al sistema.");
                }
            }
            int folio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
            cc = ConvenioColaboracionBussinessLogic.instanceFromRequest(req, folio, u);
            ConvenioColaboracionBussinessLogic ccbl = new ConvenioColaboracionBussinessLogic(jniName);
            int insertados = ccbl.insertaContratoColaboracion(cc);
            log.info("Object: {}", "Se insertaron " + insertados + " registros exitosamente");
            msg = "Se inserto correctamente el convenio de colaboracion";
            success = true;
        } catch (SessionExpiredException e) {
            log.error(e.getMessage(), e);
            session = req.getSession(true);
            msg = e.getMessage();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = e.toString();
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", success);
        result.put("message", msg);
        try {
            JSONObject resultJSON = Util.toJson(result);
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.println(resultJSON.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
        } catch (Exception exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        log.debug("Object: {}", "Environment Entry \"dataSourceRefName\" \"" + jniName + "\"");
    }
}
