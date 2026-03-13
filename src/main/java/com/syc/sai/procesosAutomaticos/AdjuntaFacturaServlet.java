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
import org.apache.commons.lang.StringUtils;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.ejercido.pagado.CLCAttachmentBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.procesosAutomaticos.core.ProcesoAdjunta;
import com.syc.sai.procesosAutomaticos.core.ProcesoAdjuntaBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AdjuntaFacturaServlet", urlPatterns = { "/AdjuntaFacturaServlet" })
public class AdjuntaFacturaServlet extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 3438918300404645375L;

    private static String jniName = "jdbc/gestion";

    private static Logger log = LoggerFactory.getLogger(AdjuntaFacturaServlet.class);

    private static String TEMP_DIR = null;

    private String ARCHIVO_CARGA = null;

    private String aEjercicioFiscal = null;

    private ProcesoAdjunta pa = null;

    private ProcesoAdjuntaBusinessLogic pabl = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<?> fileItems = null;
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreArchivo = null;
        String nombreDestino = null;
        HttpSession session = req.getSession(false);
        Usuario u;
        String ur;
        String uLogin;
        if (session == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        uLogin = u.getLogin();
        ur = u.getU_UR();
        AdecuacionBusinessLogic adbl = null;
        try {
            adbl = new AdecuacionBusinessLogic(jniName);
            pabl = new ProcesoAdjuntaBusinessLogic(jniName);
            aEjercicioFiscal = adbl.obtenEjercicioFiscal();
            pa = pabl.instanceObject(uLogin, ur);
            fileItems = Util.parseRequest(req, AdjuntaFacturaServlet.TEMP_DIR, -1);
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
                nombreDestino = AdjuntaFacturaServlet.TEMP_DIR + aEjercicioFiscal + "_CARGA_ARCHIVO_" + System.currentTimeMillis() + "." + extension;
                log.info("Copiando archivo :" + nombreArchivo);
                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                archivoCargaStream.close();
                archivoCargaIS.close();
                archivoCargaIS = null;
                archivoCargaStream = null;
                item.delete();
            }
            this.ARCHIVO_CARGA = nombreDestino;
            Thread t = new Thread(this);
            t.start();
            String msg = "Se logro ejecutar el proceso. " + (this.pa != null ? ("SU ID ES: " + pa.getIdProceso()) : "") + "\nEl proceso esta en ejecucion al terminar puede consultar el resultado.";
            session.setAttribute("MSG", msg);
            resp.sendRedirect("procesos/AdjuntaCLCResponse.jsp");
        } catch (Exception e) {
            if (pa != null) {
                try {
                    pa.setIdEstatus(-1);
                    pa.setResultadoProceso("Error: " + e);
                    pabl.actualizaResultado(pa);
                } catch (Exception e2) {
                    log.warn("Error actualizando proceso: " + e2);
                }
            }
            log.error(e.getMessage(), e);
            String msg = "No fue posible ejecutar el proceso.\nOcurrio el siguiente error al cargar el archivo: " + e;
            session.setAttribute("MSG", msg);
            resp.sendRedirect("procesos/AdjuntaCLCResponse.jsp");
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
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/Factura/";
                log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/Factura/";
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

    public void run() {
        CLCAttachmentBusinessLogic clcabl = new CLCAttachmentBusinessLogic(jniName);
        clcabl.setProcesoAdjuntaBL(pabl);
        clcabl.setProcesoAdjunta(pa);
        clcabl.attachFactura(this.ARCHIVO_CARGA, "c:\\tmp\\" + aEjercicioFiscal + "\\", true);
        if (!StringUtils.isEmpty(this.ARCHIVO_CARGA)) {
            File toDelete = new File(this.ARCHIVO_CARGA);
            if (!toDelete.delete())
                toDelete.deleteOnExit();
        }
    }
}
