package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.anteproyecto.CargaProyectoBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Servlet que descarga el PEF tomando en cuenta la unidad ejecutora del
 * usuario.
 *
 * @author Vicente Garcia Carrillo
 */
@WebServlet(name = "DescargaExcelProyecto", urlPatterns = { "/Anteproyecto/DescargaProyecto" })
public class DescargaExcelProyectoServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(DescargaExcelProyectoServlet.class);

    private static final long serialVersionUID = 1L;

    /**
     * Directorio temporal donde se almacenara el archivo de descarga.
     */
    public static String TEMP_DIR = "";

    /**
     * JNDI para el DataSource
     */
    private static String jniName = "";

    /**
     * Capitulos a excluir.
     */
    public static final int[] EXCLUYE_CAPITULOS = new int[] { 1 };

    /**
     * Descarga el archivo de proyecto. Desde la opcion de menu:
     * <ul>
     * <li type="disc">Los usuarios de B03 descarga el archivo total.</li>
     * <li type="disc">Los usuarios de unidades normativas &lt;&gt; B03 regresa
     * el excel con todas las EPs de su U.N. sin EPs de capitulo mil</li>
     * <li type="disc">Los usuarios de unidades ejecutoras (&gt;B15) regresa el
     * excel con las EPs de su U.E. sin EPs de capitulo mil
     * </ul>
     * Desde el tramite de calendario:
     * <ul>
     * <li type="disc">Los usuarios de B03 se consideran como ejecutoras.
     * Regresa el excel con las EPs de B03 incluyendo EPs de capitulo Mil</li>
     * <li type="disc">Los usuarios de unidades normativas descargan su unidad
     * como ejecutora. Regresa el excel con EPs de su unidad sin Capitulo Mil</li>
     * <li type="disc">Los usuarios de unidades ejecutoras descargan su unidad
     * ejecutora. Regresa el excel con EPs de su unidad sin Capitulo Mil</li>
     * </ul>
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.info("Acceso sin sesion");
            resp.sendRedirect("../index.jsp");
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.info("Sesion sin usuario.");
            resp.sendRedirect("../index.jsp");
        }
        String accion = req.getParameter("accion");
        if ("DESCARGA_COMPLETA".equals(accion)) {
            ServletOutputStream out = null;
            File fOut = null;
            try {
                AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
                String ejercicioFiscal = adbl.obtenEjercicioFiscal();
                String un = req.getParameter("un");
                String ue = req.getParameter("ue");
                CargaProyectoBusinessLogic cpbl = new CargaProyectoBusinessLogic(u.getLogin());
                fOut = cpbl.generaArchivoCalendarioProyectoFinal(un, ue, Integer.parseInt(ejercicioFiscal));
                if (fOut != null) {
                    ServletContext context = getServletConfig().getServletContext();
                    String mimetype = context.getMimeType(fOut.getName());
                    out = resp.getOutputStream();
                    resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                    resp.setContentLength((int) fOut.length());
                    resp.addHeader("Content-Disposition", "inline; filename=\"" + fOut.getName() + "\";");
                    Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);
                    if (!fOut.delete())
                        fOut.deleteOnExit();
                }
            } catch (Exception e) {
                log.error("Error realizando descarga de archivo de proyecto " + e, e);
                try {
                    if (out == null)
                        out = resp.getOutputStream();
                    out.print("Ocurrio el siguiente error al intentar descargar el archivo de proyecto: " + e);
                } catch (Exception e2) {
                    log.error("Ocurrio un error al intentar notificar al usuario: " + e2, e2);
                }
            } finally {
                out.flush();
                out.close();
            }
        } else {
            ServletOutputStream out = null;
            File fOut = null;
            try {
                AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
                String ejercicioFiscal = adbl.obtenEjercicioFiscal();
                String ur = u.getU_UR();
                boolean descargaDeCalendario = "true".equalsIgnoreCase(req.getParameter("DESCARGA_CALENDARIO"));
                boolean esAdministrador = (u.getRole("ADMIN_PRESUPUESTO") != null);
                CargaProyectoBusinessLogic cpbl = new CargaProyectoBusinessLogic(u.getLogin());
                if (descargaDeCalendario) {
                    fOut = cpbl.generaArchivoCalendarioProyecto(esAdministrador, ur, Integer.parseInt(ejercicioFiscal), DescargaExcelProyectoServlet.EXCLUYE_CAPITULOS);
                } else {
                    fOut = cpbl.generaArchivoProyecto(esAdministrador, ur, Integer.parseInt(ejercicioFiscal), DescargaExcelProyectoServlet.EXCLUYE_CAPITULOS);
                }
                if (fOut != null) {
                    ServletContext context = getServletConfig().getServletContext();
                    String mimetype = context.getMimeType(fOut.getName());
                    out = resp.getOutputStream();
                    resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                    resp.setContentLength((int) fOut.length());
                    resp.addHeader("Content-Disposition", "inline; filename=\"" + fOut.getName() + "\";");
                    Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);
                    if (!fOut.delete())
                        fOut.deleteOnExit();
                }
            } catch (Exception e) {
                log.error("Error realizando descarga de archivo de proyecto " + e, e);
                try {
                    if (out == null)
                        out = resp.getOutputStream();
                    out.print("Ocurrio el siguiente error al intentar descargar el archivo de proyecto: " + e);
                } catch (Exception e2) {
                    log.error("Ocurrio un error al intentar notificar al usuario: " + e2, e2);
                }
            } finally {
                out.flush();
                out.close();
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/PEF/";
                log.info("Object: {}", "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/PEF/";
            log.info("Error occurred", "Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("Object: {}", "No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
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
