/**
 */
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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItem;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Propietario
 */
@WebServlet(name = "AdjuntaArchivoMasivo", urlPatterns = { "/AdjuntaMasivo" })
public class AdjuntaArchivoMasivoServlet extends HttpServlet implements GestionInterface {

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    private static final long serialVersionUID = -1825759453227353947L;

    private static final Logger log = LoggerFactory.getLogger(AdjuntaArchivoMasivoServlet.class);

    private String jniName;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Iniciando proceso de adjuntar masivo");
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreDestino = "";
        String cCentroContable = "";
        int aEjercicioFiscal = -1;
        int desde = -1;
        int hasta = -1;
        String tituloAplicacion = "";
        String carpeta = "";
        String nombreDocumento = "";
        String nombreArchivo = "";
        String secPoliza = "";
        int adjuntados = 0;
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("index.jsp");
        }
        try {
            fileItems = Util.parseRequest(req, AdjuntaArchivoMasivoServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    if ("cCentroContable".equalsIgnoreCase(item.getFieldName()))
                        cCentroContable = item.getString();
                    else if ("aEjercicioFiscal".equalsIgnoreCase(item.getFieldName()))
                        aEjercicioFiscal = Integer.parseInt(item.getString());
                    else if ("desde2".equalsIgnoreCase(item.getFieldName())) {
                        if (!"".equalsIgnoreCase(item.getString()))
                            desde = Integer.parseInt(item.getString());
                    } else if ("hasta2".equalsIgnoreCase(item.getFieldName())) {
                        if (!"".equalsIgnoreCase(item.getString()))
                            hasta = Integer.parseInt(item.getString());
                    } else if ("tituloAplicacion".equalsIgnoreCase(item.getFieldName()))
                        tituloAplicacion = item.getString();
                    else if ("carpeta".equalsIgnoreCase(item.getFieldName()))
                        carpeta = item.getString();
                    else if ("nombreDocumento".equalsIgnoreCase(item.getFieldName()))
                        nombreDocumento = item.getString();
                    else if ("secPoliza2".equalsIgnoreCase(item.getFieldName())) {
                        if (!"".equalsIgnoreCase(item.getString()))
                            secPoliza = item.getString();
                    }
                    continue;
                }
                archivoCargaIS = item.getInputStream();
                archivoCargaStream = new DataInputStream(archivoCargaIS);
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                nombreDestino = AdjuntaArchivoMasivoServlet.TEMP_DIR + "CARGA_ARCHIVO_" + System.currentTimeMillis() + "." + extension;
                log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                item.delete();
                log.debug("Object: " + String.valueOf("Procesando archivo:" + nombreArchivo));
                AdjuntaArchivoMasivoBusinessLogic aambl = new AdjuntaArchivoMasivoBusinessLogic(jniName);
                if ("POLIZA".equalsIgnoreCase(tituloAplicacion)) {
                    String[] cadenacoma = secPoliza.split(",");
                    String[] cadenaguion = cadenacoma[0].split("-");
                    List<Rango> range = new ArrayList<Rango>();
                    if (cadenacoma.length == 1 && cadenaguion.length == 1) {
                        range.add(new Rango(Integer.parseInt(cadenacoma[0]), Integer.parseInt(cadenacoma[0])));
                    } else
                        for (int i = 0; i < cadenacoma.length; i++) {
                            if (cadenacoma[i] == "")
                                break;
                            else {
                                cadenaguion = cadenacoma[i].split("-");
                                if (cadenaguion.length == 1) {
                                    range.add(new Rango(Integer.parseInt(cadenaguion[0]), Integer.parseInt(cadenaguion[0])));
                                } else {
                                    range.add(new Rango(Integer.parseInt(cadenaguion[0]), Integer.parseInt(cadenaguion[1])));
                                }
                            }
                        }
                    adjuntados = aambl.adjuntaMasivoPol(tituloAplicacion, Integer.parseInt(carpeta), Integer.parseInt(nombreDocumento), cCentroContable, aEjercicioFiscal, range, nombreDestino);
                } else {
                    adjuntados = aambl.adjuntaMasivo(tituloAplicacion, Integer.parseInt(carpeta), Integer.parseInt(nombreDocumento), cCentroContable, aEjercicioFiscal, desde, hasta, nombreDestino);
                }
                log.debug("Object: " + String.valueOf("Se inserto el documento en " + adjuntados + " expedientes"));
                String msg = "Se inserto el documento en " + adjuntados + " expedientes";
                session.setAttribute("msg", msg);
                resp.sendRedirect("Generador/AdjuntaDocumentoMasivo.jsp");
                return;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (archivoCargaStream != null)
                try {
                    archivoCargaStream.close();
                } catch (Exception e) {
                    log.error("Error occurred", "Error cerrando flujo DataInputStream" + e);
                }
            if (archivoCargaIS != null)
                try {
                    archivoCargaIS.close();
                } catch (Exception e) {
                    log.error("Error occurred", "Error cerrando flujo InputStream" + e);
                }
            archivoCargaIS = null;
            archivoCargaStream = null;
            if (!"".equals(nombreDestino)) {
                File toDelete = new File(nombreDestino);
                if (!toDelete.delete())
                    toDelete.deleteOnExit();
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
