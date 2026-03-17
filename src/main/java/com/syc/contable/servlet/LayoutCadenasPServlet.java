package com.syc.contable.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.CadenasPBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "LayoutCadenasPServlet", urlPatterns = { "/gstnmngr/generaLayoutCadenasP" })
public class LayoutCadenasPServlet extends HttpServlet {

    /**
     */
    private String CualArchivo = "";

    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public LayoutCadenasPServlet() {
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
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String sUsuario = usuario.getLogin();
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        //String sLeyenda = (request.getParameter("sDataHLeyenda")!= null)? request.getParameter("sDataHLeyenda").trim(): "";
        String sContra = (request.getParameter("sDataHcontrarecibo") != null) ? request.getParameter("sDataHcontrarecibo").trim() : "";
        CualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
        int valor = sFolio.length() - 1;
        String sFolioQuery = sFolio.substring(0, valor);
        int valor1 = sContra.length() - 1;
        String sContrar = sContra.substring(0, valor1);
        ArrayList<String> arrListPago = null;
        ArrayList<String> arrListDocu = null;
        CadenasPBussinessLogic cmpBL = new CadenasPBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            //para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            //StringBuffer arrListDocumentacion = new StringBuffer();
            arrListPago = cmpBL.buscaCompromisos(sFolioQuery, sCuentaBancaria, sFecha, sUsuario, sContrar);
            //arrListDocu = cmpBL.ArmaDocumentoComprobatorio(sFolioQuery);
            //Long date = System.currentTimeMillis();
            // para dar el nombre del archivo Pago Directo
            String layoutPago = "Cadenas" + sufijo.trim() + ".txt";
            // para dar el nombre del archivo Documentación Comprobatoria
            String layoutDocu = "DOCCOMP" + sufijo.trim() + ".csv";
            // para dar el nombre del archivo Pago Directo ZIP
            String layoutPagoZip = "Cadenas" + sufijo.trim() + ".zip";
            // para dar el nombre del archivo Documentación Comprobatoria ZIP
            String layoutDocuZip = "DOCCOMP" + sufijo.trim() + ".zip";
            //String layoutZip = "CadenasTodo" + sufijo + ".zip";
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
            // envío de archivo CSV
            ServletOutputStream outS = null;
            ByteArrayInputStream byteArrayInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            try {
                response.setContentType("text/csv");
                String disposition = "attachment; fileName=" + layoutPago;
                response.setHeader("Content-Disposition", disposition);
                outS = response.getOutputStream();
                //setup the input as the blob to write out to the client
                byte[] blobData = outTextPago.getBytes();
                byteArrayInputStream = new ByteArrayInputStream(blobData);
                bufferedOutputStream = new BufferedOutputStream(outS);
                int length = blobData.length;
                response.setContentLength(length);
                //byte[] buff = new byte[length];
                byte[] buff = new byte[(1024 * 1024) * 2];
                //now lets shove the data down
                int bytesRead;
                // Simple read/write loop.
                while (-1 != (bytesRead = byteArrayInputStream.read(buff, 0, buff.length))) {
                    bufferedOutputStream.write(buff, 0, bytesRead);
                }
                //out.flush();
                out.close();
            } catch (Exception e) {
                System.err.println(e);
                throw e;
            } finally {
                if (out != null)
                    out.close();
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            }
            File ficheroPag = new File(layoutPago);
            ficheroPag.delete();
            File ficheroDoc = new File(layoutDocu);
            ficheroDoc.delete();
            File ficheroPagZip = new File(layoutPagoZip);
            ficheroPagZip.delete();
            File ficheroDocZip = new File(layoutDocuZip);
            ficheroDocZip.delete();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
