package com.syc.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
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
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.procesosAutomaticos.EstadoDeCuentaBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 2151207615224437627L;

    private String jniName;

    private String folioGenerator;

    private static final Logger log = LoggerFactory.getLogger(TestServlet.class);

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separatorChar + "adjuntaSP" + File.separatorChar;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        File f = null;
        if (session == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            resp.sendRedirect("../index.jsp");
            return;
        }
        try {
            f = parseRequest(req);
            EstadoDeCuentaBusinessLogic edcbl = new EstadoDeCuentaBusinessLogic(jniName);
            FolioGeneratorInterface fg = Util.getFolioGenerator(folioGenerator);
            edcbl.adjuntaEstadoDeCuentaMasivo(f, u, 53, fg, "CONSULTA_PDF", jniName, TEMP_DIR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resp.getOutputStream().println(e.toString());
        } finally {
            if (f != null)
                if (!f.delete())
                    f.deleteOnExit();
        }
    }

    private File parseRequest(HttpServletRequest req) throws Exception {
        List<?> fileItems = Util.parseRequest(req, TEMP_DIR, -1);
        Iterator<?> iter = fileItems.iterator();
        String nombreArchivo = "";
        String nombreDestino = "";
        DataInputStream archivoCargaStream = null;
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                item.delete();
                continue;
            }
            archivoCargaStream = new DataInputStream(item.getInputStream());
            nombreArchivo = item.getName();
            String extension = Util.getFileExtencion(nombreArchivo);
            if (!"zip".equalsIgnoreCase(extension))
                throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
            log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
            nombreDestino = FacturaUtils.generaNombreZip(TEMP_DIR, extension);
            Util.copiaArchivo(archivoCargaStream, nombreDestino);
            item.delete();
            break;
        }
        return new File(nombreDestino);
    }

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
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("Object: {}", "folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                f.mkdir();
        } catch (Exception e) {
            log.error("Object: {}", "No se logro crear el directorio temporal: " + TEMP_DIR + " Causa:" + e);
        }
    }
}
