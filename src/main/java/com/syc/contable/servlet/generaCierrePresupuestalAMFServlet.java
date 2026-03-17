package com.syc.contable.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.PagoAcuerdosAMFBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "generaCierrePresupuestalAMFServlet", urlPatterns = { "/gstnmngr/generaCierrePresupuestalAMF" })
public class generaCierrePresupuestalAMFServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public generaCierrePresupuestalAMFServlet() {
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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        out.println("  <BODY>");
        out.print("    This is ");
        out.print(this.getClass());
        out.println(", using the GET method");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
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
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String sUsuario = usuario.getLogin();
        String caNoContrarrecibo = (request.getParameter("numRecibos") != null) ? request.getParameter("numRecibos").trim() : "";
        int valor = caNoContrarrecibo.length() - 1;
        String sFolioQuery = caNoContrarrecibo.substring(0, valor);
        ArrayList<String> arrListPago = null;
        PagoAcuerdosAMFBussinessLogic cmpBL = new PagoAcuerdosAMFBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            //para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            //StringBuffer arrListDocumentacion = new StringBuffer();
            arrListPago = cmpBL.buscaCierreAMFExport(sFolioQuery);
            Long date = System.currentTimeMillis();
            // para dar el nombre del archivo Pago Directo
            String Cierre = "Cierre" + sufijo.trim() + ".csv";
            // Guarda el pago
            BufferedWriter out = new BufferedWriter(new FileWriter(Cierre));
            StringBuffer archivoPago = new StringBuffer();
            for (int i = 0; i < arrListPago.size(); i++) {
                archivoPago.append(arrListPago.get(i));
            }
            String outTextPago = archivoPago.toString();
            out.write(outTextPago);
            out.close();
            // envío de archivo CSV
            ServletOutputStream outS = null;
            ByteArrayInputStream byteArrayInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            try {
                response.setContentType("text/csv");
                String disposition = "attachment; fileName=" + Cierre;
                response.setHeader("Content-Disposition", disposition);
                outS = response.getOutputStream();
                //setup the input as the blob to write out to the client
                byte[] blobData = outTextPago.getBytes();
                byteArrayInputStream = new ByteArrayInputStream(blobData);
                bufferedOutputStream = new BufferedOutputStream(outS);
                int length = blobData.length;
                response.setContentLength(length);
                byte[] buff = new byte[(1024 * 1024) * 2];
                int bytesRead;
                while (-1 != (bytesRead = byteArrayInputStream.read(buff, 0, buff.length))) {
                    bufferedOutputStream.write(buff, 0, bytesRead);
                }
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
            File ficheroPag = new File(Cierre);
            ficheroPag.delete();
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
