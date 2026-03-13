package com.syc.ejercido.pagado;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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
import com.syc.contable.OperacionAjenaBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Propietario
 */
@WebServlet(name = "CargaArchivosSicopAjenasServlet", urlPatterns = { "/CargaArchivosSicopAjenasServlet" })
public class CargaArchivosSicopAjenasServlet extends HttpServlet implements GestionInterface {

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    private static final long serialVersionUID = -1825759453227353947L;

    private static final Logger log = LoggerFactory.getLogger(CargaArchivosSicopAjenasServlet.class);

    private String jniName;

    private static String jndiName = null;

    private Connection conn = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        try {
            if (session == null) {
                response.sendRedirect("index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (usuario == null) {
                response.sendRedirect("index.jsp");
                return;
            }
            List<?> fileItems = null;
            Iterator<?> iter = null;
            DataInputStream archivoCargaStream = null;
            File nombreDestino = null;
            fileItems = Util.parseRequest(request, CargaArchivosSicopAjenasServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            boolean archivoRecibido = false;
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    archivoCargaStream = new DataInputStream(item.getInputStream());
                    String nombreArchivo = item.getName();
                    // Validar que si no es CSV lanzar excepcion
                    String extension = Util.getFileExtencion(nombreArchivo);
                    if (!"csv".equalsIgnoreCase(extension)) {
                        throw new Exception("El nombre del archivo debe contener la extensión csv.");
                    }
                    log.info("Copiando archivo :" + nombreArchivo);
                    nombreDestino = File.createTempFile("CARGA_AJENAS", ".csv", new File(System.getProperty("java.io.tmpdir")));
                    Util.copiaArchivo(archivoCargaStream, nombreDestino.getAbsolutePath());
                    archivoRecibido = true;
                    break;
                }
            }
            if (!archivoRecibido)
                throw new Exception("No se recibio archivo.");
            OperacionAjenaBussinessLogic oaBL = new OperacionAjenaBussinessLogic(GestionInterface.ATT_CONEXION);
            List<String> errores = oaBL.procesaLayoutAjenas(nombreDestino);
            if (errores == null)
                session.setAttribute("RESULT", "Archivo cargado exitosamente.");
            else
                session.setAttribute("RESULT", "El archivo cargo con errores.\n" + Util.join(errores, '\n'));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            session.setAttribute("RESULT", "Ocurrio el siguiente error: " + e.getMessage());
        }
        response.sendRedirect("Generador/SubirArchivoSicop.jsp");
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
    }
}
