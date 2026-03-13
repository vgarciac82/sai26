package com.syc.contable.servlet;

import java.io.File;
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
import org.apache.commons.lang.StringUtils;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.RelacionGastosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "LayoutRelacionGastosCompromisoServlet", urlPatterns = { "/gstnmngr/generaLayoutRelacionGastosCompromiso" })
public class LayoutRelacionGastosCompromisoServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(LayoutRelacionGastosCompromisoServlet.class);

    /**
     */
    private static final long serialVersionUID = -2623676811387328275L;

    private String folioGenerator;

    private String jniName;

    /*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
            String tipoLayout = (StringUtils.isBlank(req.getParameter("tipoLayout")) ? "1" : req.getParameter("tipoLayout"));
            if (Integer.parseInt(tipoLayout) == 3) {
                /* Reimprime */
                CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(jniName);
                String canocompromiso = req.getParameter("caNoCompromiso");
                ArrayList<String> layout = cbl.buscaCompromisos("'" + canocompromiso + "'", null, true);
                cbl.descargaLayout(resp, layout);
            }
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
            String tipoLayout = (StringUtils.isBlank(req.getParameter("tipoLayout")) ? "1" : req.getParameter("tipoLayout"));
            String msgRetorno = "Ocurrio un error al Aplicar el Compromiso";
            boolean bExito = false;
            if (Integer.parseInt(tipoLayout) == 4) {
                /* Inserta y aplica compromisos Federalizados */
                CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(jniName);
                bExito = cbl.CompromisosFederalizados(req);
                if (bExito) {
                    msgRetorno = "Compromiso Aplicado exitosamente.";
                }
                session.setAttribute("RESULT", msgRetorno);
                resp.sendRedirect("../Generador/AutorizaCompromisosFederalizados.jsp?RESPUESTA=S");
            } else if (Integer.parseInt(tipoLayout) == 1) {
                String[] foliosIntegracion = req.getParameter("sDataH").trim().split(",");
                String[] cuentaBancaria = req.getParameter("sDataHCB").trim().split(",");
                String[] fechaIntegracion = req.getParameter("sDataHFecha").trim().split(",");
                String[] leyenda = req.getParameter("sDataHLeyenda").trim().split(",");
                CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(jniName);
                // TODO validar que si ya existe el compromiso no regenere los
                // campos
                String canocompromiso = cbl.insertaCompromisoRG(u, Util.join(foliosIntegracion, ','), Util.join(cuentaBancaria, ','), Util.join(fechaIntegracion, ','), Util.join(leyenda, ','), folioGenerator);
                ArrayList<String> layout = cbl.buscaCompromisos("'" + canocompromiso + "'", null);
                //cbl.estatusCompromiso("'" + canocompromiso + "'");
                cbl.descargaLayout(resp, layout);
                return;
            } else if (Integer.parseInt(tipoLayout) == 2) {
                File fLayout = null;
                File zipFile = null;
                File fDC = null;
                try {
                    RelacionGastosBussinessLogic rgbl = new RelacionGastosBussinessLogic(jniName, folioGenerator);
                    String caNoCompromiso = req.getParameter("caNoCompromiso");
                    fLayout = rgbl.generaLayoutRelacionGastosCompromiso(u, caNoCompromiso);
                    fDC = rgbl.generaDocComprobatoria(u, caNoCompromiso);
                    File[] arregloFile = new File[] { fLayout, fDC };
                    File tmpDir = Util.getTempDir();
                    zipFile = File.createTempFile("ZipFile", ".zip", tmpDir);
                    Util.generaZip(zipFile, arregloFile);
                    Util.doDownload(resp, zipFile.getAbsolutePath(), zipFile.getName(), "application/zip");
                    return;
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (fLayout != null && !fLayout.delete())
                        fLayout.deleteOnExit();
                    if (zipFile != null && !zipFile.delete())
                        zipFile.deleteOnExit();
                }
            }
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
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }
}
