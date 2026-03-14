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
import org.apache.commons.lang.StringUtils;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CasoExpedienteServlet", urlPatterns = { "/expediente/CreaExpediente" })
public class CasoExpedienteServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 2627255063057086011L;

    private static final Logger log = LoggerFactory.getLogger(CasoExpedienteServlet.class);

    private static String jniName = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msgRetorno = "";
        Usuario u = null;
        Caso c = null;
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null || u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
            if ("".equals(msgRetorno)) {
                CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
                try {
                    if (c.getIdGabinete() == -1) {
                        AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
                        c.getCasoDato("FOLIO").setValor(c.getFolio());
                        c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
                        c.getCasoDato("EJERCICIO_FISCAL").setValor(adbl.obtenEjercicioFiscal());
                        //u.getNombre()
                        c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());
                        c.setIdGabinete(cbl.creaExpediente(u.getLogin(), c));
                        ITree tree = cbl.getArbolCaso(c);
                        session.setAttribute(ATT_CASE, c);
                        session.setAttribute("tree.model", tree);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
                }
            }
        }
        if (StringUtils.isEmpty(msgRetorno)) {
            ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(c.getIdGabinete()));
        } else {
            ResponseSender.sendClientSimpleMessage(resp, false, msgRetorno);
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
