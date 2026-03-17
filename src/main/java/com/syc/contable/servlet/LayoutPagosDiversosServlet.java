package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.PagosDiversosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "LayoutPagosDiversosServlet", urlPatterns = { "/gstnmngr/generaLayoutPagosDiversos" })
public class LayoutPagosDiversosServlet extends HttpServlet {

    /**
     */
    private String CualArchivo = "";

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LayoutPagosDiversosServlet.class);

    /**
     * Constructor of the object.
     */
    public LayoutPagosDiversosServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Put your code here
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String msgRetorno = "";
        String sUsuario = usuario.getLogin();
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";
        String sValorUMA = (request.getParameter("sValorUMA") != null) ? request.getParameter("sValorUMA").trim() : "";
        int valor = sFolio.length() - 1;
        String sFolioQuery = sFolio.substring(0, valor);
        ArrayList<String> arrListPago = null;
        ArrayList<String> arrListDocu = null;
        PagosDiversosBussinessLogic cmpBL = new PagosDiversosBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            //para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            //StringBuffer arrListDocumentacion = new StringBuffer();
            arrListPago = cmpBL.buscaCompromisos(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario, sValorUMA);
            arrListDocu = cmpBL.ArmaDocumentoComprobatorio(sFolioQuery);
            //Long date = System.currentTimeMillis();
            // para dar el nombre del archivo Pago Directo
            File layoutPago = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PAGOCOMPROMISODIVERSOS" + sufijo.trim() + ".csv");
            // para dar el nombre del archivo Documentación Comprobatoria
            File layoutDocu = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "DOCCOMP" + sufijo.trim() + ".csv");
            File layoutZip = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PAGOCOMPROMISODIVERSOSTodo" + sufijo + ".zip");
            // Guarda el pago
            BufferedWriter out = new BufferedWriter(new FileWriter(layoutPago));
            StringBuffer archivoPago = new StringBuffer();
            for (int i = 0; i < arrListPago.size(); i++) {
                archivoPago.append(arrListPago.get(i));
            }
            String outTextPago = archivoPago.toString();
            out.write(outTextPago);
            // fin de guarda pago
            out.close();
            // Guarda el documento
            BufferedWriter outDocu = new BufferedWriter(new FileWriter(layoutDocu));
            StringBuffer archivoDocu = new StringBuffer();
            for (int i = 0; i < arrListDocu.size(); i++) {
                archivoDocu.append(arrListDocu.get(i));
            }
            String outTextDocu = archivoDocu.toString();
            outDocu.write(outTextDocu);
            // fin de guarda documento
            outDocu.close();
            // These are the files to include in the ZIP file
            File[] filenames = new File[] { layoutPago, layoutDocu };
            ServletOutputStream ouputStream;
            // Create a buffer for reading the files
            byte[] buf = new byte[1024];
            try {
                ouputStream = response.getOutputStream();
                // Create the ZIP file
                ZipOutputStream outZIP = new ZipOutputStream(ouputStream);
                for (// Compress the files
                // Compress the files
                // Compress the files
                int i = 0; i < filenames.length; i++) {
                    FileInputStream in = new FileInputStream(filenames[i]);
                    // Add ZIP entry to output stream.
                    outZIP.putNextEntry(new ZipEntry(filenames[i].getName()));
                    int len;
                    while (// Transfer bytes from the file to the ZIP file
                    (len = in.read(buf)) > 0) {
                        outZIP.write(buf, 0, len);
                    }
                    // Complete the entry
                    outZIP.closeEntry();
                    in.close();
                }
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutZip.getName() + "\"");
                outZIP.finish();
                // Complete the ZIP file
                outZIP.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
            layoutPago.delete();
            layoutDocu.delete();
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Object: {}", e.getMessage());
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
        }
        session.setAttribute("RESULT", msgRetorno);
        response.sendRedirect("../Generador/IntegraLayoutPagosDiversos.jsp");
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
