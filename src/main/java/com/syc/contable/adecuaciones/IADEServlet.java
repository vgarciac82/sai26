package com.syc.contable.adecuaciones;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.PresupuestoBusinessLogic;
import com.syc.contable.core.AdecuacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "IADEServlet", urlPatterns = { "/adecuaciones/IADE" })
public class IADEServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -2961492078185452925L;

    private static String jniName = "";

    private static final Logger log = LoggerFactory.getLogger(IADEServlet.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema");
            return;
        }
        int folioadec;
        String folioIntegracion = req.getParameter("folioConsolidacion");
        int nFolio = Integer.parseInt(folioIntegracion.substring(folioIntegracion.lastIndexOf('-') + 1));
        String[] foliosAdec = req.getParameterValues("folioAdecuacion");
        String foliosAdecuacion = "";
        log.debug("Folio integracion: " + folioIntegracion);
        if (foliosAdec == null)
            ResponseSender.sendClientSimpleMessage(resp, false, "No se seleccionaron adecuaciones a integrar");
        else {
            for (int i = 0; i < foliosAdec.length; i++) {
                log.debug(foliosAdec[i]);
                folioadec = Integer.parseInt(foliosAdec[i].substring(foliosAdec[i].lastIndexOf('-') + 1));
                foliosAdecuacion = folioadec + "," + foliosAdecuacion;
            }
            // -----------------------Inserta en las tablas
            // tConsolidacionEncabezado y tConsolidacionDetalle
            ArrayList<String> arrResultado = null;
            AdecuacionBusinessLogic adec = new AdecuacionBusinessLogic(jniName);
            try {
                arrResultado = adec.integraAdecuaciones2(nFolio, foliosAdecuacion, u);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // ---------------------------------------------------------------------------------------------
            ResponseSender.sendClientSimpleMessage(resp, true, "2");
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
}
