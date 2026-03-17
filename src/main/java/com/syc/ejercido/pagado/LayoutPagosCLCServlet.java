package com.syc.ejercido.pagado;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "LayoutPagosCLCServlet", urlPatterns = { "/gstnmngr/LayoutPagosCLCServlet" })
public class LayoutPagosCLCServlet extends HttpServlet {

    private static final long serialVersionUID = 6546987008358770840L;

    public LayoutPagosCLCServlet() {
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
        String clcNomina = (request.getParameter("buscarLayout") != null) ? request.getParameter("buscarLayout").trim() : "";
        ArrayList<String> arrListDocu = null;
        PagosNominaBussinessLogic cmpBL = new PagosNominaBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            //para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            arrListDocu = cmpBL.buscaCLCNomina(clcNomina);
            // para dar el nombre del archivo Pago Directo
            String layoutDoc = System.getProperty("java.io.tmpdir") + File.separatorChar + "CLCNOMINA" + sufijo.trim() + ".csv";
            // para dar el nombre del archivo Documentación Comprobatoria
            String layoutDocu = System.getProperty("java.io.tmpdir") + File.separatorChar + "DOCNOM" + sufijo.trim() + ".csv";
            // para dar el nombre del archivo Documentación Comprobatoria ZIP
            String layoutDocuZip = System.getProperty("java.io.tmpdir") + File.separatorChar + "DOCCOMP" + sufijo.trim() + ".zip";
            // Guarda el pago
            BufferedWriter out = new BufferedWriter(new FileWriter(layoutDoc));
            StringBuffer archivoPago = new StringBuffer();
            for (int i = 0; i < arrListDocu.size(); i++) {
                archivoPago.append(arrListDocu.get(i));
            }
            String outTextPago = archivoPago.toString();
            out.write(outTextPago);
            // fin de guarda pago
            out.close();
            // Guarda el Doc Zip
            byte[] bufDoc = new byte[1024];
            try {
                ZipOutputStream outDoc = new ZipOutputStream(new FileOutputStream(layoutDocuZip));
                FileInputStream inDoc = new FileInputStream(layoutDocu);
                outDoc.putNextEntry(new ZipEntry(layoutDocu));
                int lenPag;
                while ((lenPag = inDoc.read(bufDoc)) > 0) {
                    outDoc.write(bufDoc, 0, lenPag);
                }
                outDoc.closeEntry();
                inDoc.close();
                outDoc.close();
            } catch (IOException e) {
            }
            doDownload(response, layoutDoc, layoutDoc);
            File ficheroPag = new File(layoutDoc);
            ficheroPag.delete();
            File ficheroDoc = new File(layoutDocu);
            ficheroDoc.delete();
            File ficheroPagZip = new File(layoutDocuZip);
            ficheroPagZip.delete();
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

    private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {
        int length = 0;
        File f = new File(filename);
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(original_filename);
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        //resp.addHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\";");
        //resp.addHeader("Content-Disposition", "attachement; filename=\"" + original_filename + "\";");
        resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");
        // 5K buffer
        byte[] bbuf = new byte[5 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
        in.close();
        out.flush();
        out.close();
    }
}
