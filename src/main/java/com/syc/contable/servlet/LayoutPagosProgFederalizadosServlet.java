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
import com.syc.contable.ProgFederalizadosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "LayoutPagosProgFederalizadosServlet", urlPatterns = { "/gstnmngr/generaLayoutPagosProgFederalizados" })
public class LayoutPagosProgFederalizadosServlet extends HttpServlet {

    /**
     */
    private String CualArchivo = "";

    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public LayoutPagosProgFederalizadosServlet() {
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

    /**
     * The doDelete method of the servlet. <br>
     *
     * This method is called when a HTTP delete request is received.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String msgRetorno = "";
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String sUsuario = usuario.getLogin();
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";
        String sValorUMA = (request.getParameter("sValorUMA") != null) ? request.getParameter("sValorUMA").trim() : "";
        CualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
        int valor = sFolio.length() - 1;
        String sFolioQuery = sFolio.substring(0, valor);
        ArrayList<String> arrListPago = null;
        ArrayList<String> arrListDocu = null;
        ProgFederalizadosBussinessLogic cmpBL = new ProgFederalizadosBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            //para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            arrListPago = cmpBL.buscaCompromisos(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario, sValorUMA);
            arrListDocu = cmpBL.ArmaDocumentoComprobatorio(sFolioQuery);
            // para dar el nombre del archivo Pago Directo
            File layoutPago = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PAGOFEDERALIZADOS" + sufijo.trim() + ".csv");
            // para dar el nombre del archivo Documentación Comprobatoria
            File layoutDocu = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "DOCCOMP" + sufijo.trim() + ".csv");
            File layoutZip = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PAGOFEDERALIZADOSTodo" + sufijo + ".zip");
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
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
        }
        session.setAttribute("RESULT", msgRetorno);
        response.sendRedirect("../Generador/IntegraLayoutPagoFederalizados.jsp");
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
