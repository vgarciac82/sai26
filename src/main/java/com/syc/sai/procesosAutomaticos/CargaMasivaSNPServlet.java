package com.syc.sai.procesosAutomaticos;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.syc.contable.caja.CargaMasivaSNPBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CargaMasivaSNPServlet", urlPatterns = { "/CargaMasivaSNP", "/ExportaExpedientesPendientes" })
public class CargaMasivaSNPServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 8024939888422492922L;

    private static final Logger log = Logger.getLogger(CargaMasivaSNPServlet.class);

    private static String jniName = "jdbc/gestion";

    private static String TEMP_DIR;

    private static final String[] TRAMITES_AUDITABLES = { "PAGODIVERSO", "RELACIONGASTOS", "CAJA", "PAGODIRECTO", "PAGOFEDERALIZADO", "PAGOOBRA", "POLIZA" };

    private static String plantillaReportePath = "";

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("Peticion sin sesion o sesion invalida");
            ServletOutputStream out = resp.getOutputStream();
            out.println("Sin sesion. Por favor cierre esta ventana y reingrese al sistema para continuar.");
            out.flush();
            out.close();
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("Peticion sin usuario o sesion invalida");
            ServletOutputStream out = resp.getOutputStream();
            out.println("Sin sesion. Por favor cierre esta ventana y reingrese al sistema para continuar.");
            out.flush();
            out.close();
            return;
        }
        String fileDownload = null;
        try {
            CargaMasivaSNPBusinessLogic cmsnpbl = new CargaMasivaSNPBusinessLogic(jniName);
            List<String> filtros = new ArrayList<String>();
            String ur = u.getU_UR();
            for (int i = 0; i < TRAMITES_AUDITABLES.length; i++) {
                if (!StringUtils.isEmpty(req.getParameter(TRAMITES_AUDITABLES[i])))
                    filtros.add(TRAMITES_AUDITABLES[i]);
            }
            if (filtros.size() > 0) {
                fileDownload = cmsnpbl.generaReporteExpedientesFaltantes(plantillaReportePath, filtros, ("A02".equals(ur) ? null : ur));
                Util.doDownload(resp, fileDownload, "ReporteExpedientesFaltantes.xls", null);
            } else {
                ServletOutputStream out = resp.getOutputStream();
                out.println("No se recibieron parametros para generar el reporte.");
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            log.error(e, e);
            ServletOutputStream out = resp.getOutputStream();
            out.println("Ocurrio el siguiente error mientras se generaba el expediente. " + e);
            out.println("Intente nuevamente. Si persiste el problema reporte al administrador del sistema");
            out.flush();
            out.close();
        } finally {
            if (fileDownload != null) {
                File f = new File(fileDownload);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mensajeRetorno = "";
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("Peticion sin sesion o sesion invalida");
            resp.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("Peticion sin usuario o sesion invalida");
            resp.sendRedirect("../index.jsp");
            return;
        }
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreDestino = "";
        try {
            fileItems = Util.parseRequest(req, CargaMasivaSNPServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            String nombreArchivo = "";
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField())
                    continue;
                archivoCargaIS = item.getInputStream();
                archivoCargaStream = new DataInputStream(item.getInputStream());
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                if (!"xls".equalsIgnoreCase(extension))
                    throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                nombreDestino = CargaMasivaSNPServlet.TEMP_DIR + "CARGA_PROYECTO_" + System.currentTimeMillis() + "." + extension;
                log.info("Copiando archivo :" + nombreArchivo);
                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                item.delete();
                log.debug("Procesando archivo:" + nombreArchivo);
                mensajeRetorno = "Archivo cargado exitosamente";
                break;
            }
            CargaMasivaSNPBusinessLogic cmsnpbl = new CargaMasivaSNPBusinessLogic(jniName);
            List<String> log = cmsnpbl.procesaArchivoMasivo(nombreDestino, u);
        } catch (Exception exc) {
            log.error(exc, exc);
            mensajeRetorno = "No se pudo procesar el excel debido al siguiente error:\\n" + exc;
        } finally {
            if (archivoCargaStream != null)
                try {
                    archivoCargaStream.close();
                } catch (Exception e) {
                    log.error("Error cerrando flujo DataInputStream" + e);
                }
            if (archivoCargaIS != null)
                try {
                    archivoCargaIS.close();
                } catch (Exception e) {
                    log.error("Error cerrando flujo InputStream" + e);
                }
            archivoCargaIS = null;
            archivoCargaStream = null;
            if (!"".equals(nombreDestino)) {
                File toDelete = new File(nombreDestino);
                if (!toDelete.delete())
                    toDelete.deleteOnExit();
            }
        }
        session.setAttribute("MENSAJE_CARGA", mensajeRetorno);
        resp.sendRedirect("Generador/CargaMasivaSNP.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/PEF/";
                log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/PEF/";
            log.info("Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
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
        plantillaReportePath = getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteExpFaltantes.xls");
    }
}
