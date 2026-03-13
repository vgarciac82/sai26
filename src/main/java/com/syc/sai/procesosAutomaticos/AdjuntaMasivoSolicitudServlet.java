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
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AdjuntaMasivoSolicitudServlet", urlPatterns = { "/AdjuntaDocumento" })
public class AdjuntaMasivoSolicitudServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -651372170523768237L;

    private static String jniName = "jdbc/gestion";

    private static final Logger log = LoggerFactory.getLogger(AdjuntaMasivoSolicitudServlet.class);

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separatorChar + "adjuntaSP" + File.separatorChar;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Iniciando proceso de adjuntar solicitud de pago a expedientes.");
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreDestino = "";
        String tituloAplicacion = "";
        String nombreArchivo = "";
        String sCertifTransito = "";
        HttpSession session = req.getSession(false);
        try {
            if (session == null) {
                resp.sendRedirect("index.jsp");
                return;
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                resp.sendRedirect("index.jsp");
                return;
            }
            fileItems = Util.parseRequest(req, TEMP_DIR, -1);
            iter = fileItems.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    if ("TITULO_APLICACION".equalsIgnoreCase(item.getFieldName())) {
                        tituloAplicacion = item.getString();
                    } else if ("esCertificadoTransito".equalsIgnoreCase(item.getFieldName())) {
                        sCertifTransito = item.getString();
                    }
                    continue;
                }
                archivoCargaIS = item.getInputStream();
                archivoCargaStream = new DataInputStream(archivoCargaIS);
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                if (!"zip".equalsIgnoreCase(extension))
                    throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                nombreDestino = TEMP_DIR + "CARGA_ARCHIVO_" + System.currentTimeMillis() + "." + extension;
                log.info("Copiando archivo :" + nombreArchivo);
                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                item.delete();
                log.debug("Procesando archivo:" + nombreArchivo);
                AdjuntaArchivoMasivoBusinessLogic aambl = new AdjuntaArchivoMasivoBusinessLogic(jniName);
                String logAdjuntos = "";
                if (!"1".equals(sCertifTransito)) {
                    logAdjuntos = aambl.adjuntaSolicitudPagoMasivo(tituloAplicacion, nombreDestino, TEMP_DIR, u);
                    session.setAttribute("msg", logAdjuntos);
                    resp.sendRedirect("procesos/adjuntaSolictudDePago.jsp?showResult=true");
                } else {
                    logAdjuntos = aambl.adjuntaCertificadoTransitoMasivo(tituloAplicacion, nombreDestino, TEMP_DIR, u);
                    session.setAttribute("msg", logAdjuntos);
                    resp.sendRedirect("Generador/AdjuntaCertificadoTransito.jsp?showResult=true");
                }
                return;
            }
        } catch (Exception e) {
            if (!"1".equals(sCertifTransito)) {
                session.setAttribute("msg", "Ocurrio el siguiente error: \n" + e + "\n Reintente por favor. En caso de repetir el error reporte a soporte tecnico.");
                resp.sendRedirect("procesos/adjuntaSolictudDePago.jsp?showResult=true");
            } else {
                session.setAttribute("msg", "Ocurrio el siguiente error: \n" + e + "\n Reintente por favor. En caso de repetir el error reporte a soporte tecnico.");
                resp.sendRedirect("Generador/AdjuntaCertificadoTransito.jsp?showResult=true");
            }
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
    }

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
            File f = new File(TEMP_DIR);
            if (!f.exists())
                f.mkdir();
        } catch (Exception e) {
            log.error("No se logro crear el directorio temporal: " + TEMP_DIR + " Causa:" + e);
        }
    }
}
