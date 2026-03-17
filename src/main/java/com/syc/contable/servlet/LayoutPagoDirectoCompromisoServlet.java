package com.syc.contable.servlet;

import java.io.IOException;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "LayoutPagoDirectoCompromisoServlet", urlPatterns = { "/gstnmngr/generaLayoutPDCompromiso" })
public class LayoutPagoDirectoCompromisoServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(LayoutPagoDirectoCompromisoServlet.class);

    /**
     */
    private static final long serialVersionUID = -2623676811387328275L;

    private String folioGenerator;

    private String jniName;

    /*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, jakarta.servlet.http.HttpServletResponse)
	 */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null)
                throw new Exception("Sesion expirada. Reingrese al sistema e intente nuevamente su operacion");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new Exception("Sin usuario en sesion. Reingrese al sistema e intente nuevamente su operacion");
            /* Reimprime */
            CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(jniName);
            String canocompromiso = req.getParameter("caNoCompromiso");
            ArrayList<String> layout = cbl.buscaCompromisos("'" + canocompromiso + "'", null, true);
            cbl.descargaLayout(resp, layout);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null)
                throw new Exception("Sesion expirada. Reingrese al sistema e intente nuevamente su operacion");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                throw new Exception("Sin usuario en sesion. Reingrese al sistema e intente nuevamente su operacion");
            String[] foliosIntegracion = req.getParameter("sDataH").trim().split(",");
            //String cuentaBancaria[] = req.getParameter("sDataHCB").trim().split(",");
            //String fechaIntegracion[] = req.getParameter("sDataHFecha").trim().split(",");
            //String leyenda[] = req.getParameter("sDataHLeyenda").trim().split(",");
            CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
            String canocompromiso = cbl.obtenerContrarreciboPD(foliosIntegracion);
            //cbl.insertaCompromisoPagoDirecto(u, foliosIntegracion, Util.join(cuentaBancaria, ','), Util.join(fechaIntegracion, ','), Util.join(leyenda, ','), folioGenerator);
            ArrayList<String> layout = cbl.buscaCompromisos("'" + canocompromiso + "'", null);
            cbl.estatusCompromiso("'" + canocompromiso + "'");
            cbl.descargaLayout(resp, layout);
            return;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
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
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("Object: {}", "folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }
}
