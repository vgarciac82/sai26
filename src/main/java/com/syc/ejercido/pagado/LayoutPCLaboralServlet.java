/**
 */
package com.syc.ejercido.pagado;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
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
import org.apache.commons.fileupload2.core.FileItem;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Propietario
 */
public class LayoutPCLaboralServlet extends HttpServlet implements GestionInterface {

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    private static final long serialVersionUID = -1825759453227353947L;

    private static final Logger log = LoggerFactory.getLogger(LayoutPCLaboralServlet.class);

    private String jniName;

    private static String jndiName = null;

    private Connection conn = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        log.info("Iniciando proceso de adjuntar masivo");
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreArchivo = "";
        String archivoTabla = null;
        String mesMod = null;
        int mes = 0;
        CallableStatement clc = null;
        if (session == null) {
            resp.sendRedirect("index.jsp");
        }
        try {
            fileItems = Util.parseRequest(req, LayoutPCLaboralServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    if ("mes".equals(item.getFieldName()))
                        mesMod = item.getString();
                    mes = Integer.parseInt(mesMod);
                    item.delete();
                } else {
                    archivoCargaIS = item.getInputStream();
                    archivoCargaStream = new DataInputStream(archivoCargaIS);
                    nombreArchivo = item.getName();
                    nombreArchivo = Util.getFileName(nombreArchivo);
                    archivoTabla = nombreArchivo.substring(0, nombreArchivo.lastIndexOf("."));
                    log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                    Util.deleteStreamServerBD(nombreArchivo);
                    Util.uploadStreamServerBD(nombreArchivo, archivoCargaStream);
                    archivoCargaIS.close();
                    archivoCargaStream.close();
                    item.delete();
                }
            }
            log.info("Object: {}", "Archivo cargado :" + nombreArchivo);
            String ruta = "C:/SubirArchivo/" + nombreArchivo;
            conn = DataSourceManager.getConnection(jndiName);
            clc = conn.prepareCall(" { CALL sp_SubirActualizacionPasivosLaborales ( ?, ?, ? ) } ");
            clc.setString(1, archivoTabla);
            clc.setString(2, ruta);
            clc.setInt(3, mes);
            int res = clc.executeUpdate();
            log.info("Object: {}", "Registros Insertados: " + res);
            String msg = "Se insertaron exitosamente " + res + " registros.";
            session.setAttribute("RESULT", msg);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String msg = "Notifique al Administrador. Ocurrio el siguiente error al cargar el archivo: " + e;
            session.setAttribute("RESULT", msg);
        } finally {
            CloseObject.closeObject(clc);
            CloseObject.closeObject(conn);
        }
        resp.sendRedirect("Generador/LayoutActualizaPasivoContingenteLaboral.jsp");
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
