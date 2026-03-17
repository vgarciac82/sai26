package com.syc.gestion.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
// // // // import org.apache.commons.ssl.PKCS8Key;
import com.jenkov.prizetags.tree.itf.ITree;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "GestionSignFileReceiverServlet", urlPatterns = { "/caso/firmardoc" })
public class GestionSignFileReceiverServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionSignFileReceiverServlet.class);

    private String jniName = null;

    private String tempDir = null;

    private int id_tc = -1;

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
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    private List parseRequest(HttpServletRequest req) throws ServletException {
        JakartaServletFileUpload upload = new JakartaServletFileUpload();
        try {
            return upload.parseRequest(req);
        } catch (FileUploadException fe) {
            fe.printStackTrace();
            throw new ServletException("Error de recepcion " + fe.getMessage());
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean updToAlbum = "true".equalsIgnoreCase(req.getParameter("upd"));
        if ((!JakartaServletFileUpload.isMultipartContent(req)) && (!updToAlbum)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No es una peticion multipart/form-data");
            return;
        }
        HttpSession session = req.getSession(false);
        //inicio sesion
        if (session == null) {
            log.warn("No hay sesion");
            resp.sendRedirect("../index.jsp");
            return;
        }
        ITree tree = (ITree) session.getAttribute(ATT_TREE);
        if (tree == null) {
            log.error("Llamada invalida, sin Arbol");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Llamada invalida, sin Arbol");
        }
        int idx = 0;
        String tmpFile = null;
        List fileItems = parseRequest(req);
        Iterator i = fileItems.iterator();
        InputStream insArchivoAFirmar = null;
        String archivoAFirmarPath = "";
        InputStream isCer = null;
        String cerPath = "";
        InputStream isKey = null;
        String keyPath = null;
        String uPassword = "";
        //uPassword="EJchar54";//req.getParameter("password");
        DataInputStream disArchivoAFirmar = null;
        DataInputStream diskey = null;
        int count = 0;
        while (i.hasNext()) {
            FileItem item = null;
            //los campos 0,1,2 y tienen folio,operador,fecha_carga y ejercicio_fiscal
            if (//campos tipo file
            count < 3)
                item = (FileItem) i.next();
            if (count == 3) {
                //campo del password
                uPassword = ((DiskFileItem) i.next()).getString();
            }
            if (item == null) {
                count++;
                continue;
            }
            try {
                if (count == 0) {
                    System.out.println(item.getName());
                    insArchivoAFirmar = item.getInputStream();
                    disArchivoAFirmar = new DataInputStream(item.getInputStream());
                    archivoAFirmarPath = item.getName();
                }
                if (count == 1) {
                    System.out.println(item.getName());
                    isKey = item.getInputStream();
                    keyPath = item.getName();
                    FileOutputStream fos = new FileOutputStream(getServletContext().getRealPath("/upload/" + session.getId() + "firmado.key"));
                    diskey = new DataInputStream(item.getInputStream());
                    int length = 0;
                    byte[] buffer = new byte[4 * 1024];
                    //length = diskey.read(buffer);
                    while ((diskey != null) && ((length = diskey.read(buffer)) != -1)) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }
                if (count == 2) {
                    System.out.println(item.getName());
                    isCer = item.getInputStream();
                    cerPath = item.getName();
                }
                item.delete();
                count++;
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            }
        }
        try {
            if (!"".equals(keyPath) && keyPath != null) {
                //En caso de que no venga la llave no intenta firmarlo
                File keyFile = new File(getServletContext().getRealPath("/upload/" + session.getId() + "firmado.key"));
                FileInputStream in = new FileInputStream(keyFile);
                PrivateKey pkcs = null;
                byte[] fileBytes = new byte[(int) keyFile.length()];
                in.read(fileBytes);
                char[] pass = uPassword.toCharArray();
                try {
                    pkcs = null; // null; // null; // new PKCS8Key(fileBytes, pass);
                } catch (Exception e) {
                    throw new Exception("El Password de la llave privada es incorrecto");
                }
                PrivateKey privateKey = pkcs;
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate fact = (X509Certificate) cf.generateCertificate(isCer);
                Certificate[] chain = { fact };
                PdfReader reader = new PdfReader(insArchivoAFirmar);
                File outputFile = new File(getServletContext().getRealPath("/upload/" + session.getId() + "firmado.pdf"));
                PdfStamper pdfStamper;
                pdfStamper = PdfStamper.createSignature(reader, null, '\0', outputFile);
                PdfSignatureAppearance sap = pdfStamper.getSignatureAppearance();
                //				sap.setCrypto(privateKey, chain, null, PdfSignatureAppearance.CERTIFIED_FORM_FILLING);
                sap.setReason("Documento Firmado por Seguridad");
                sap.setLocation("CONAGUA");
                pdfStamper.setFormFlattening(true);
                pdfStamper.close();
                disArchivoAFirmar = null;
                disArchivoAFirmar = new DataInputStream(new FileInputStream(outputFile));
                outputFile.delete();
            }
            //Se obtiene el caso para subir el archivo al expediente
            Caso c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null) {
                log.warn("No hay Caso en la sesión");
                throw new ServletException("No hay Caso en la sesión");
            }
            //se usa en la exception
            id_tc = c.getIdTC();
            CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
            //Carpeta en la que se desea subir el archivo, se define en la JSP
            int carpeta = new Integer(req.getParameter("carpeta")).intValue();
            String select = c.getTipoCaso().getGavetaAsociada() + "_G" + c.getIdGabinete() + "C" + carpeta;
            if (select == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
                return;
            }
            Fortimax fimx = new Fortimax(select);
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            String nombre_documento = "Archivo";
            //Siempre lo pondremos en el documento 1, si ya existe se sustituye
            try {
                nombre_documento = cbl.getDocumentosDeCarpeta(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpeta).get(0).getNombreDocumento();
                cbl.borraDocumento(select + "D1");
            } catch (Exception exb) {
                log.error(exb.getMessage(), exb);
            }
            ;
            cbl.creaDocumento(usuario, fimx, nombre_documento, "", false);
            String ext = archivoAFirmarPath.substring(archivoAFirmarPath.indexOf("."), archivoAFirmarPath.length());
            cbl.recibeDocumentoGestion(c, carpeta, nombre_documento, ext, disArchivoAFirmar, true);
            tree = cbl.getArbolCaso(c);
            session.setAttribute(ATT_TREE, tree);
            if (c.getIdTC() == 2)
                resp.sendRedirect("../plantillasCasos/cargaPresupuestal.jsp");
            if (c.getIdTC() == 3) {
                //resp.sendRedirect("../gstnmngr/Adecuacion?carpeta="+carpeta);
                String cSuperReduccion = req.getParameter("cSuperReduccion");
                String cSRInterna = req.getParameter("cSRInterna");
                resp.sendRedirect("../plantillasCasos/adecuacionPresupuestal.jsp?cSuperReduccion=" + cSuperReduccion + "&cSRInterna=" + cSRInterna);
            }
            if (c.getIdTC() == 15)
                resp.sendRedirect("../plantillasCasos/reintegroPresupuestal.jsp?leeExcel=1");
            if (c.getIdTC() == 26)
                resp.sendRedirect("../plantillasCasos/reintegroPresupuestalMil.jsp?leeExcel=1");
            if (c.getIdTC() == 27)
                resp.sendRedirect("../plantillasCasos/rectificacionPresupuestalMil.jsp?leeExcel=1");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            if (id_tc == 2)
                resp.sendRedirect("../plantillasCasos/cargaPresupuestal.jsp?msg=" + exc.getMessage());
            if (id_tc == 3)
                resp.sendRedirect("../plantillasCasos/adecuacionPresupuestal.jsp?msg=" + exc.getMessage());
            if (id_tc == 15)
                resp.sendRedirect("../plantillasCasos/reintegroPresupuestal.jsp?msg=" + exc.getMessage());
            if (id_tc == 26)
                resp.sendRedirect("../plantillasCasos/reintegroPresupuestalMil.jsp?msg=" + exc.getMessage());
            if (id_tc == 27)
                resp.sendRedirect("../plantillasCasos/rectificacionPresupuestalMil.jsp?msg=" + exc.getMessage());
        }
    }
}
