package com.syc.ejercido.pagado;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

@WebServlet(name = "LayoutPagosNominaServlet", urlPatterns = { "/gstnmngr/generaLayoutPagosNomina" })
public class LayoutPagosNominaServlet extends HttpServlet {

    /**
     */
    private String CualArchivo = "";

    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public LayoutPagosNominaServlet() {
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
        //String path = request.getContextPath();
        //String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String sUsuario = usuario.getLogin();
        String sFolio = (request.getParameter("BuscarAplicarNomina") != null) ? request.getParameter("BuscarAplicarNomina").trim() : "";
        String sFolioCLC = (request.getParameter("BuscarAplicarLayoutCLCCapituloMil") != null) ? request.getParameter("BuscarAplicarLayoutCLCCapituloMil").trim() : "";
        String solicitudPago = (request.getParameter("solicitudPago") != null) ? request.getParameter("solicitudPago").trim() : "";
        if (solicitudPago != "") {
            sFolio = sFolioCLC;
        }
        String sConcpto = (request.getParameter("cConcepto") != null) ? request.getParameter("cConcepto").trim() : "";
        String sCuentaBancaria = (request.getParameter("cCuentaBancaria") != null) ? request.getParameter("cCuentaBancaria").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";
        if (sConcpto != "") {
            int valor = sConcpto.length() - 1;
            sConcpto = sConcpto.substring(0, valor);
        }
        //.substring(0, valor);
        String sFolioQuery = sFolio;
        ArrayList<String> arrListPago = null;
        PagosNominaBussinessLogic cmpBL = new PagosNominaBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            //para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            arrListPago = cmpBL.buscaCompromisos(sFolioQuery, sConcpto, sFecha, sLeyenda, sUsuario, solicitudPago, sCuentaBancaria);
            // para dar el nombre del archivo Pago Directo
            String layoutPago = System.getProperty("java.io.tmpdir") + File.separatorChar + "PAGONOMINA" + sufijo.trim() + ".csv";
            //String layoutDocu = "DOCNOM" + sufijo.trim() + ".csv";					// para dar el nombre del archivo Documentación Comprobatoria
            //String layoutPagoZip = "PAGONOMINA" + sufijo.trim() + ".zip";			// para dar el nombre del archivo Pago Directo ZIP
            //String layoutDocuZip = "DOCCOMP" + sufijo.trim() + ".zip";				// para dar el nombre del archivo Documentación Comprobatoria ZIP
            //String layoutZip = "PAGONOMINATodo" + sufijo + ".zip";
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
            //, layoutDocu These are the files to include in the ZIP file
            String[] filenames = new String[] { layoutPago };
            cmpBL.actualizaStatusGenerar(sFolio);
            doDownload(response, layoutPago, layoutPago);
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
