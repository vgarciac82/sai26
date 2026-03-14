package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import jakarta.servlet.http.HttpSession;
import com.syc.contable.RelacionGastosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "LayoutPagoDiversoRelGastosServlet", urlPatterns = { "/gstnmngr/generaLayoutPagoDiversoRelGastos" })
public class LayoutPagoDiversoRelGastosServlet extends HttpServlet {

    /**
     */
    private String CualArchivo = "";

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LayoutPagoDiversoRelGastosServlet.class);

    private static String folioGenerator;

    /**
     * Constructor of the object.
     */
    public LayoutPagoDiversoRelGastosServlet() {
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
        //response.setContentType("text/html");
        //PrintWriter out = response.getWriter();
        //out
        //		.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        //out.println("<HTML>");
        //out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        //out.println("  <BODY>");
        //out.print("    This is ");
        //out.print(this.getClass());
        //out.println(", using the GET method");
        //out.println("  </BODY>");
        //out.println("</HTML>");
        //out.flush();
        //out.close();
        CualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
        //String CualArchivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
        //recupero los campos caNoCompromisos de la lista de los que serán enviados
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
        //String path = request.getContextPath();
        //String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        String archivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
        if (archivo == null) {
            throw new ServletException("El archivo no debe ir nulo");
        }
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String msgRetorno = "";
        //String sUsuario = usuario.getLogin();
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        //String sFecha = (request.getParameter("sDataHFecha")!= null)? request.getParameter("sDataHFecha").trim(): "";
        //String sLeyenda = (request.getParameter("sDataHLeyenda")!= null)? request.getParameter("sDataHLeyenda").trim(): "";
        //int valor = sFolio.length() - 1;
        //String sFolioQuery = sFolio.substring(0, valor);
        ArrayList<String> arrListPago = null;
        ArrayList<String> arrListDocu = null;
        RelacionGastosBussinessLogic cmpBL = new RelacionGastosBussinessLogic(GestionInterface.ATT_CONEXION, folioGenerator);
        try {
            //para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            String[] arrCuentasBancarias = sCuentaBancaria.split(",");
            String sREFERENCIA1_107 = new java.text.SimpleDateFormat("yyMMddhhmmss").format(new java.util.Date());
            sREFERENCIA1_107 = cmpBL.getUE(arrCuentasBancarias[0].trim()) + sREFERENCIA1_107;
            //StringBuffer arrListDocumentacion = new StringBuffer();
            /*
				if(archivo.equals("1"))
					arrListPago = cmpBL.buscaCompromisosDiversoRG(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario);
				else
					arrListPago = cmpBL.buscaPagoDiversoIntegrados(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario, sREFERENCIA1_107);
				
				arrListDocu = cmpBL.ArmaDocumentoComprobatorioDiversoRG(sFolioQuery, sREFERENCIA1_107, archivo.equals("1"));
				*/
            // para dar el nombre del archivo Pago Directo
            String layoutPago = "PAGODIRECTORELACIONGASTOS" + sufijo.trim() + ".csv";
            // para dar el nombre del archivo Documentación Comprobatoria
            String layoutDocu = "DOCCOMP" + sufijo.trim() + ".csv";
            // para dar el nombre del archivo Pago Directo ZIP
            String layoutPagoZip = "PAGODIRECTORELACIONGASTOS" + sufijo.trim() + ".zip";
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
            // Guarda el pago Zip
            byte[] bufPag = new byte[1024];
            ZipOutputStream outPag = new ZipOutputStream(new FileOutputStream(layoutPagoZip));
            FileInputStream inPag = new FileInputStream(layoutPago);
            outPag.putNextEntry(new ZipEntry(layoutPago));
            int lenPag;
            while ((lenPag = inPag.read(bufPag)) > 0) {
                outPag.write(bufPag, 0, lenPag);
            }
            outPag.closeEntry();
            inPag.close();
            outPag.close();
            // These are the files to include in the ZIP file
            String[] filenames = new String[] { layoutPago, layoutDocu };
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
                    outZIP.putNextEntry(new ZipEntry(filenames[i]));
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
                response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutPagoZip + "\"");
                outZIP.finish();
                // Complete the ZIP file
                outZIP.close();
            } catch (IOException e) {
                e.printStackTrace();
                msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
            }
            File ficheroPag = new File(layoutPago);
            ficheroPag.delete();
            File ficheroDoc = new File(layoutDocu);
            ficheroDoc.delete();
            File ficheroPagZip = new File(layoutPagoZip);
            ficheroPagZip.delete();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + ex.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
        }
        session.setAttribute("RESULT", msgRetorno);
        response.sendRedirect("../Generador/IntegraLayoutPagosDiversosRG.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
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
    }
}
