package com.syc.ejercido.pagado;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Propietario
 */
@WebServlet(name = "CargaArchivosSicopServlet", urlPatterns = { "/CargaArchivosSicopServlet" })
public class CargaArchivosSicopServlet extends HttpServlet implements GestionInterface {

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    private static final long serialVersionUID = -1825759453227353947L;

    private static final Logger log = LoggerFactory.getLogger(CargaArchivosSicopServlet.class);

    private String jniName;

    private static String jndiName = null;

    private Connection conn = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        log.info("Iniciando proceso de carga de Archivos de SICOP");
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreArchivo = "";
        String extencionArchivo = "";
        String archivoExtraido = null;
        CallableStatement clc = null;
        PreparedStatement ps = null;
        if (session == null) {
            resp.sendRedirect("index.jsp");
        }
        try {
            fileItems = Util.parseRequest(req, CargaArchivosSicopServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if ("archivoCargar".equals(item.getFieldName())) {
                    log.info("Copiando archivo: " + nombreArchivo);
                    nombreArchivo = item.getName();
                    extencionArchivo = Util.getFileExtencion(nombreArchivo);
                    if ("zip".equalsIgnoreCase(extencionArchivo)) {
                        archivoExtraido = Util.extractZipToStream(item.getInputStream());
                        archivoCargaIS = new FileInputStream(new File(archivoExtraido));
                        nombreArchivo = (nombreArchivo.toLowerCase()).replaceAll(".zip", ".csv");
                    } else {
                        archivoCargaIS = item.getInputStream();
                    }
                    archivoCargaStream = new DataInputStream(archivoCargaIS);
                    File f = new File(nombreArchivo);
                    nombreArchivo = f.getName();
                    conn = DataSourceManager.getConnection(jndiName);
                    Util.deleteStreamServerBD(nombreArchivo);
                    Util.uploadStreamServerBD(nombreArchivo, archivoCargaStream);
                    archivoCargaIS.close();
                    archivoCargaStream.close();
                    item.delete();
                    log.info("Archivo cargado: " + nombreArchivo);
                }
            }
            String msg = "Archivo cargado exitosamente.";
            session.setAttribute("RESULT", msg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            String msg = "Notifique al Administrador. Ocurrio el siguiente error al cargar el archivo: " + e;
            session.setAttribute("RESULT", msg);
            //ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error: " + e);
        } finally {
            if (archivoExtraido != null) {
                File fBorrar = new File(archivoExtraido);
                if (!fBorrar.delete())
                    fBorrar.deleteOnExit();
            }
            CloseObject.closeObject(clc);
            CloseObject.closeObject(ps);
            CloseObject.closeObject(conn);
        }
        resp.sendRedirect("Generador/SubirArchivoSicop.jsp");
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
