package com.syc.ejercido.pagado;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItem;
import com.syc.contable.CargaAdecuacionBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Janise Diaz Sanchez
 */
@WebServlet(name = "CargaArchivosAdecuacionesServlet", urlPatterns = { "/CargaArchivosAdecuacionesServlet", "/procesaLayout", "/consultaLayout" })
public class CargaArchivosAdecuacionesServlet extends HttpServlet implements GestionInterface {

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    private static final long serialVersionUID = -1825759453227353947L;

    private static final Logger log = LoggerFactory.getLogger(CargaArchivosAdecuacionesServlet.class);

    private String jniName;

    private static Map<String, String> plantillas = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        CargaAdecuacionBusinessLogic adecBL = new CargaAdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        File nombreDestino = null;
        try {
            String accion = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
            if (session == null) {
                response.sendRedirect("index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (usuario == null) {
                response.sendRedirect("index.jsp");
                return;
            }
            if ("procesaLayout".equals(accion)) {
                String errores2 = "";
                int mes = Integer.parseInt(request.getParameter("mesAdec"));
                errores2 = adecBL.procesaLayoutAdecuacionesMAP(null, null, request, response, mes, "S", plantillas);
                if (errores2 != null)
                    session.setAttribute("RESULT", "No se proceso el archivo cargado, favor de revisar\n" + errores2);
                else
                    session.setAttribute("RESULT", "Archivo cargado");
                log.debug("Es ingresos Propios");
                return;
            } else if ("consultaLayout".equals(accion)) {
                String errores2 = "";
                int mes = Integer.parseInt(request.getParameter("mesesAdec"));
                int version = Integer.parseInt(request.getParameter("idVersion"));
                //String cEsIP = request.getParameter("cEsIP");
                errores2 = adecBL.consultaLayoutAdecuacionesMAP(null, null, request, response, mes, version, plantillas);
                if (errores2 != null)
                    session.setAttribute("RESULT", "No se proceso el archivo cargado, favor de revisar\n" + errores2);
                else
                    session.setAttribute("RESULT", "Archivo cargado");
                log.debug("Es consulta");
                return;
            } else if ("CargaArchivosAdecuacionesServlet".equals(accion)) {
                List<?> fileItems = null;
                Iterator<?> iter = null;
                fileItems = Util.parseRequest(request, System.getProperty("java.io.tmpdir"), -1);
                iter = fileItems.iterator();
                InputStream archivoCargaIS = null;
                int mes = -1;
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        if ("mesAdec".equalsIgnoreCase(item.getFieldName()))
                            mes = Integer.parseInt(item.getString());
                    } else {
                        try {
                            DataInputStream archivoCargaStream = new DataInputStream(item.getInputStream());
                            nombreDestino = File.createTempFile("ADECUACIONES_MAP", ".csv", new File(System.getProperty("java.io.tmpdir")));
                            log.info("Object: {}", "Copiando archivo a:" + nombreDestino);
                            archivoCargaIS = item.getInputStream();
                            Util.copiaArchivo(archivoCargaIS, nombreDestino.getAbsolutePath());
                            String errores = adecBL.procesaLayoutAdecuacionesMAP(nombreDestino.getAbsolutePath(), archivoCargaStream, request, response, mes, "N", plantillas);
                            if (errores != null)
                                session.setAttribute("RESULT", "No se proceso el archivo cargado\n" + errores);
                        } finally {
                            archivoCargaIS.close();
                            archivoCargaIS = null;
                        }
                    }
                    item.delete();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            session.setAttribute("RESULT", "Ocurrio el siguiente error: " + e.getMessage() + " intente nuevamente.");
        } finally {
            if (nombreDestino != null) {
                if (!nombreDestino.delete())
                    nombreDestino.deleteOnExit();
            }
        }
        response.sendRedirect("Generador/SubirArchivoAdecuaciones.jsp");
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
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("Adecuaciones", getServletContext().getRealPath("Reportes" + File.separator + "FlujoEfectivoRedondeado.xls"));
            }
        }
    }
}
