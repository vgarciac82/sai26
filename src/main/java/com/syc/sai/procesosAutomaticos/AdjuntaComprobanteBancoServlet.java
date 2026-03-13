package com.syc.sai.procesosAutomaticos;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
import org.apache.commons.lang.StringUtils;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.ejercido.pagado.CLCAttachmentBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AdjuntaComprobanteBancoServlet", urlPatterns = { "/AdjuntaComprobanteBanco" })
public class AdjuntaComprobanteBancoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 3438918300404645375L;

    private static String jniName = "jdbc/gestion";

    private static Logger log = LoggerFactory.getLogger(AdjuntaComprobanteBancoServlet.class);

    private static String TEMP_DIR = null;

    private String aEjercicioFiscal = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreArchivo = null;
        String nombreDestino = null;
        HttpSession session = req.getSession(false);
        String tipoPago = "";
        if (session == null) {
            PrintWriter out = null;
            out = resp.getWriter();
            if (out != null) {
                out.println("Su sesion a caducado. Reingrese al sistema.");
                out.flush();
                out.close();
                return;
            }
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        List<AttachResult> result = null;
        if (u == null) {
            PrintWriter out = null;
            out = resp.getWriter();
            if (out != null) {
                out.println("Su sesion a caducado. Reingrese al sistema.");
                out.flush();
                out.close();
                return;
            }
        }
        try {
            fileItems = Util.parseRequest(req, AdjuntaComprobanteBancoServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    if ("TITULO_APLICACION".equalsIgnoreCase(item.getFieldName())) {
                        tipoPago = item.getString();
                    }
                    continue;
                }
                archivoCargaIS = item.getInputStream();
                archivoCargaStream = new DataInputStream(archivoCargaIS);
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                nombreDestino = AdjuntaComprobanteBancoServlet.TEMP_DIR + "CARGA_ARCHIVO_" + System.currentTimeMillis() + "." + extension;
                log.info("Copiando archivo :" + nombreArchivo);
                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                archivoCargaStream.close();
                archivoCargaIS.close();
                archivoCargaIS = null;
                archivoCargaStream = null;
                item.delete();
            }
            String centroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
            CLCAttachmentBusinessLogic clcabl = new CLCAttachmentBusinessLogic(jniName);
            result = clcabl.attachComprobantesBancarios(centroContable, nombreDestino, "c:\\ComprobanteBancario_" + aEjercicioFiscal + "\\" + System.currentTimeMillis(), true, tipoPago);
            if (!StringUtils.isEmpty(nombreDestino)) {
                File toDelete = new File(nombreDestino);
                if (!toDelete.delete())
                    toDelete.deleteOnExit();
            }
            session.setAttribute("ATT_RESPUESTA", result);
            resp.sendRedirect("Generador/respuestaAdjuntaCB.jsp");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            PrintWriter out = null;
            out = resp.getWriter();
            if (out != null) {
                out.println("Ocurrio el siguiente error al cargar el archivo: " + e);
                out.flush();
                out.close();
            } else
                throw new ServletException(e);
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
            AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
            this.aEjercicioFiscal = adbl.obtenEjercicioFiscal();
        } catch (Exception e) {
            throw new ServletException(e);
        }
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/ComprobanteBancario/";
                log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/ComprobanteBancario/";
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
