package com.syc.cfdi.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
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
import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "UploadREPZipServlet", urlPatterns = { "/AdjuntaREP" })
public class UploadREPZipServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -8876688669675838892L;

    private String jniName = "";

    private static final Logger log = LoggerFactory.getLogger(UploadREPZipServlet.class);

    private static String TEMP_DIR = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msgRetorno = "";
        Usuario u = null;
        boolean validaContraSAT = false;
        boolean notificaFacturasInvalidasSAT = false;
        boolean notificaFacturasEFA = false;
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
        }
        if ("".equals(msgRetorno)) {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
            validaContraSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_VALIDACION_SAT"));
            notificaFacturasEFA = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_EFA"));
            notificaFacturasInvalidasSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_SAT"));
            List<?> fileItems = null;
            Iterator<?> iter = null;
            DataInputStream archivoCargaStream = null;
            String nombreDestino = "";
            try {
                fileItems = Util.parseRequest(req, TEMP_DIR, -1);
                iter = fileItems.iterator();
                String nombreArchivo = "";
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (!item.isFormField()) {
                        archivoCargaStream = new DataInputStream(item.getInputStream());
                        nombreArchivo = item.getName();
                        String extension = Util.getFileExtencion(nombreArchivo);
                        if (!"zip".equalsIgnoreCase(extension))
                            throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                        log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                        nombreDestino = FacturaUtils.generaNombreZip(TEMP_DIR, extension);
                        log.trace("Object: {}", "Se genero el sig. nombre: " + nombreDestino);
                        Util.copiaArchivo(archivoCargaStream, nombreDestino);
                        File ref = new File(nombreDestino);
                        log.trace("Object: {}", "Validando el archivo copiado: \nRuta: " + ref.getAbsolutePath() + "\nExiste: " + ref.exists() + "\nRead: " + ref.canRead() + "\nRead: " + ref.canRead() + "\nWrite: " + ref.canWrite() + "\nParent: " + ref.getParent() + "\nLength: " + ref.length());
                        item.delete();
                    }
                }
                FacturaBusinessLogic fbl = new FacturaBusinessLogic(jniName, validaContraSAT, u);
                fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
                fbl.setNotificaErroresEFA(notificaFacturasEFA);
                fbl.setPermiteVersionAnterior(false);
                fbl.setUsuario(u);
                ExtraccionFacturas ef = fbl.extraeComprobantesPago(nombreDestino);
                if (ef.getErrores().size() == 0) {
                    Map<String, ComponentesFactura> comprobantesPago = ef.getFacturas();
                    File filename = fbl.insertaComprobantesDePagoBatch(comprobantesPago);
                    Util.doDownload(resp, filename.getAbsolutePath(), filename.getName(), "text/csv");
                    return;
                } else {
                    String token = "";
                    for (int i = 0; i < ef.getErrores().size(); i++) {
                        msgRetorno += token + ef.getErrores().get(i);
                        token = "\n";
                    }
                    throw new Exception(msgRetorno);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                msgRetorno = "Ocurrio el siguiente error al cargar el archivo:" + e.getMessage();
            } finally {
                if (archivoCargaStream != null)
                    try {
                        archivoCargaStream.close();
                    } catch (Exception e) {
                        log.error("Error occurred", "Error cerrando flujo DataInputStream" + e);
                    }
                archivoCargaStream = null;
                if (!"".equals(nombreDestino)) {
                    File toDelete = new File(nombreDestino);
                    if (!toDelete.delete())
                        toDelete.deleteOnExit();
                }
            }
        } else {
            resp.sendRedirect("procesos/adjuntaREP.jsp?msgError=" + msgRetorno);
        }
        session.setAttribute("RESULT", msgRetorno);
        resp.sendRedirect("procesos/adjuntaREP.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/Facturas/";
                log.info("Object: {}", "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/Reps/";
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
    }
}
