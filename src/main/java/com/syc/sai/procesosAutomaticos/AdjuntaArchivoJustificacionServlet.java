package com.syc.sai.procesosAutomaticos;

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
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

/**
 * @author Propietario
 */
@WebServlet(name = "AdjuntaArchivoJustif", urlPatterns = { "/AdjuntaJustificacion" })
public class AdjuntaArchivoJustificacionServlet extends HttpServlet implements GestionInterface {

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    private static final long serialVersionUID = -1825759453227353947L;

    private static final Logger log = Logger.getLogger(AdjuntaArchivoJustificacionServlet.class);

    private String jniName;

    private static final String FOLDER_NAME = "Ret IVA6";

    private static final String DOC_NAME = "Archivo Justificacion";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        log.info("Iniciando proceso de adjuntar justificacion 6pc");
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreDestino = "";
        Usuario u = null;
        Caso c = null;
        String nombreArchivo = "";
        try {
            if (session == null) {
                throw new Exception("Sin session. Ingrese nuevamente al sistema.");
            }
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                throw new Exception("Sin session. Ingrese nuevamente al sistema.");
            }
            c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null) {
                throw new Exception("Sin caso en session. Ingrese nuevamente al sistema.");
            }
            AdjuntaArchivoMasivoBusinessLogic aambl = new AdjuntaArchivoMasivoBusinessLogic(jniName);
            CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
            c = cbl.getCaso(c.getIdCaso());
            int idGabinete = c.getIdGabinete();
            if (idGabinete == -1) {
                cbl.creaExpediente(u.getLogin(), c);
                c = cbl.getCaso(c.getIdCaso());
            }
            fileItems = Util.parseRequest(req, AdjuntaArchivoJustificacionServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    continue;
                }
                archivoCargaIS = item.getInputStream();
                archivoCargaStream = new DataInputStream(archivoCargaIS);
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                nombreDestino = AdjuntaArchivoJustificacionServlet.TEMP_DIR + "CARGA_ARCHIVO_" + System.currentTimeMillis() + "." + extension;
                log.info("Copiando archivo :" + nombreArchivo);
                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                item.delete();
            }
            aambl.adjuntaArchivoSimple(c.getTipoCaso().getGavetaAsociada(), u.getLogin(), c.getIdGabinete(), AdjuntaArchivoJustificacionServlet.FOLDER_NAME, AdjuntaArchivoJustificacionServlet.DOC_NAME, new File(nombreDestino));
            ITree tree = cbl.getArbolCaso(c);
            session.setAttribute("tree.model", tree);
            String msg = "Se adjunto correctamente el archivo";
            session.setAttribute("RESULT", msg);
            resp.sendRedirect("Generador/UploadJustificacionIva6.jsp");
            return;
        } catch (Exception e) {
            log.error(e, e);
            String msg = "Ocurrio el siguiente error al cargar el archivo: " + e;
            session.setAttribute("MSG", msg);
            resp.sendRedirect("Generador/UploadJustificacionIva6.jsp");
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
