package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.ejercido.pagado.LayoutBancoRGBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "LayoutBancoRGServlet", urlPatterns = { "/gstnmngr/generaLayoutBancoRG" })
public class LayoutBancoRGServlet extends HttpServlet {

    private static String folioGenerator;

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LayoutAnexo1Servlet.class);

    private String jniName;

    public LayoutBancoRGServlet() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nFolios = (request.getParameter("nFoliosLayout") != null) ? request.getParameter("nFoliosLayout").trim() : "";
        String sTipo = (request.getParameter("sTipoLayout") != null) ? request.getParameter("sTipoLayout").trim() : "";
        String sCuentaBancaria = (request.getParameter("sCuentaLayout") != null) ? request.getParameter("sCuentaLayout").trim() : "";
        boolean esCompromisoRG = "SI".equalsIgnoreCase(request.getParameter("esIntCompromiso"));
        String caNoCompromisoRG = StringUtils.trimToEmpty(request.getParameter("caNoCompromisoRG"));
        ArrayList<String> LayoutBanorte = null;
        ArrayList<String> LayoutSpei = null;
        LayoutBancoRGBusinessLogic anexoBL = new LayoutBancoRGBusinessLogic(GestionInterface.ATT_CONEXION, folioGenerator);
        try {
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            if (esCompromisoRG) {
                CompromisoBussinessLogic cpbl = new CompromisoBussinessLogic(jniName);
                nFolios = cpbl.listaRGEnCompromiso(caNoCompromisoRG);
                cpbl = null;
            }
            LayoutBanorte = anexoBL.ArmaLayoutBancoRG(nFolios, sTipo, true, sCuentaBancaria);
            LayoutSpei = anexoBL.ArmaLayoutBancoRG(nFolios, sTipo, false, sCuentaBancaria);
            //Se cambia en nEnviadoSICOP a 1 para los que ya se les genero lay out
            if (sTipo.equals("CAJA")) {
                anexoBL.actualizaEnviadoSICOPCaja(nFolios, sTipo);
            }
            File strlayoutBanorte = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "LayoutBanorte" + sTipo + "_" + sufijo.trim() + ".txt");
            File strlayoutSpei = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "LayoutSpei" + sTipo + "_" + sufijo.trim() + ".txt");
            File layoutBancoZip = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "LayoutsBanco" + sTipo + "_" + sufijo.trim() + ".zip");
            // Guarda el layout BANORTE
            BufferedWriter out = new BufferedWriter(new FileWriter(strlayoutBanorte));
            StringBuffer archivoBanorte = new StringBuffer();
            for (int i = 0; i < LayoutBanorte.size(); i++) {
                archivoBanorte.append(LayoutBanorte.get(i));
            }
            String outTextBanorte = archivoBanorte.toString();
            out.write(outTextBanorte);
            // fin Layout BANORTE
            out.close();
            // Guarda el layout SPEI
            BufferedWriter outSpei = new BufferedWriter(new FileWriter(strlayoutSpei));
            StringBuffer archivoSpei = new StringBuffer();
            for (int i = 0; i < LayoutSpei.size(); i++) {
                archivoSpei.append(LayoutSpei.get(i));
            }
            String outTextSpei = archivoSpei.toString();
            outSpei.write(outTextSpei);
            // fin Layout SPEI
            outSpei.close();
            // These are the files to include in the ZIP file
            File[] filenames = new File[] { strlayoutBanorte, strlayoutSpei };
            ServletOutputStream ouputStream;
            // Create a buffer for reading the files
            byte[] buf = new byte[1024];
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutBancoZip.getName() + "\"");
            try {
                ouputStream = response.getOutputStream();
                // Create the ZIP file
                ZipOutputStream outZIP = new ZipOutputStream(ouputStream);
                for (int i = 0; i < filenames.length; i++) {
                    // Compress the files
                    FileInputStream in = new FileInputStream(filenames[i]);
                    // Add ZIP entry to output stream.
                    outZIP.putNextEntry(new ZipEntry(filenames[i].getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        // Transfer bytes from the file to the ZIP file
                        outZIP.write(buf, 0, len);
                    }
                    // Complete the entry
                    outZIP.closeEntry();
                    in.close();
                }
                outZIP.finish();
                // Complete the ZIP file
                outZIP.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            strlayoutBanorte.delete();
            strlayoutSpei.delete();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
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
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }
}
