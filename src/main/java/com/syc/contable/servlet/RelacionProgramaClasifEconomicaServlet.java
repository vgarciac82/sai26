package com.syc.contable.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import com.syc.contable.anteproyecto.RelacionProgramaClasifEconomicaBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "RelacionProgramaClasifEconomicaServlet", urlPatterns = { "/gstnmngr/RelacionProgramaClasifEconomica/CargaMasiva" })
public class RelacionProgramaClasifEconomicaServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = Logger.getLogger(RelacionProgramaClasifEconomicaServlet.class);

    private static final long serialVersionUID = 1L;

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mensajeRetorno = "";
        // ExportaExcel
        String accion = req.getRequestURI().indexOf("CargaMasiva") > 0 ? "CargaMasiva" : "CapturaManual";
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
        boolean esAdministrador = (u.getRole("ADMIN_PRESUPUESTO") != null);
        String ur = u.getU_UR();
        if ("CargaMasiva".equals(accion)) {
            List<?> fileItems = null;
            Iterator<?> iter = null;
            InputStream archivoCargaIS = null;
            DataInputStream archivoCargaStream = null;
            String nombreDestino = "";
            try {
                fileItems = Util.parseRequest(req, RelacionProgramaClasifEconomicaServlet.TEMP_DIR, -1);
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
                    nombreDestino = RelacionProgramaClasifEconomicaServlet.TEMP_DIR + "CATALOGO_RELACION_" + System.currentTimeMillis() + "." + extension;
                    log.info("Copiando archivo :" + nombreArchivo);
                    Util.copiaArchivo(archivoCargaStream, nombreDestino);
                    item.delete();
                    log.debug("Procesando archivo:" + nombreArchivo);
                    RelacionProgramaClasifEconomicaBusinessLogic refpbl = new RelacionProgramaClasifEconomicaBusinessLogic(u.getLogin());
                    List<String> mensajes = refpbl.cargaExcelRelacionProgramaClasifEconomica(nombreDestino);
                    if (mensajes.size() > 0) {
                        mensajeRetorno = "Error de carga: ";
                        for (int i = 0; i < mensajes.size(); i++) mensajeRetorno += "\\n" + mensajes.get(i).replace("'", "").replace('"', ' ');
                    } else
                        mensajeRetorno = "Archivo cargado exitosamente";
                    /*
					 * Solo se espera un archivo por carga, por lo que al leerlo
					 * no es necesario continuar con el ciclo.
					 */
                    break;
                }
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
            resp.sendRedirect("../../admin/RelacionProgramaClasifEconomica.jsp");
        } else if ("CapturaManual".equals(accion)) {
            String operacion = req.getParameter("accion");
            String strRedirect = "";
            try {
                strRedirect = "../../admin/RelacionProgramaClasifEconomica.jsp?";
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.debug("Redirect: " + strRedirect);
            resp.sendRedirect(strRedirect);
            log.debug("SALE  DE LA CapturaManual");
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
    }
}
