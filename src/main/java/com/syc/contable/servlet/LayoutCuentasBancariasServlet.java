package com.syc.contable.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jfree.util.Log;
import com.syc.contable.CuentaBancariaBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso
 * para SYC Constructores de Sistemas
 * desarrollo gestion_conagua_sif
 * México D.F. 16/03/2012
 */
@WebServlet(name = "LayoutCuentasBancariasServlet", urlPatterns = { "/gstnmngr/LayoutCuentasB" })
public class LayoutCuentasBancariasServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public LayoutCuentasBancariasServlet() {
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
        Usuario u = null;
        CuentaBancariaBussinessLogic cbBL = new CuentaBancariaBussinessLogic(GestionInterface.ATT_CONEXION);
        String beneficiarioCuentasBancarias = request.getParameter("envioP");
        String listaFolios = request.getParameter("cFolios");
        if (beneficiarioCuentasBancarias == null) {
            throw new ServletException("envio no debe ir nulo");
        }
        if (listaFolios == null) {
            throw new ServletException("No se recibieron folios");
        }
        beneficiarioCuentasBancarias = beneficiarioCuentasBancarias.trim();
        if (beneficiarioCuentasBancarias.lastIndexOf(",") != -1) {
            beneficiarioCuentasBancarias = beneficiarioCuentasBancarias.substring(0, beneficiarioCuentasBancarias.length() - 1);
        }
        u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        listaFolios = listaFolios.trim();
        if (listaFolios.lastIndexOf(",") != -1) {
            listaFolios = listaFolios.substring(0, listaFolios.length() - 1);
        }
        //para dar el nombre del archivo
        StringBuffer archivoCuentasBancarias = new StringBuffer();
        boolean actualizado = false;
        try {
            archivoCuentasBancarias = cbBL.buscaCuentasB(beneficiarioCuentasBancarias);
            actualizado = cbBL.UpdateStatusBCB(beneficiarioCuentasBancarias, listaFolios, u.getLogin());
            if (actualizado) {
                //para dar el nombre del archivo
                SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmsss");
                String sufijo = fecha.format(new Date(System.currentTimeMillis()));
                String layoutCB = "";
                //se calcular el tamaño del archivo
                InputStream bis = new BufferedInputStream(new ByteArrayInputStream(archivoCuentasBancarias.toString().getBytes()));
                int size = bis.available();
                OutputStream out = response.getOutputStream();
                //Preparar la descarga como attachment
                if (size > 250) {
                    layoutCB = "RFC_CLABE_" + sufijo + ".zip";
                    response.setContentType("aplication/zip");
                    response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutCB + "\"");
                    //se avisa el tamaño del archivo al browser
                    //response.setContentLength(size);
                    Util.compress(out, bis, "rfc_clabe_" + sufijo + ".csv");
                } else {
                    layoutCB = "rfc_clabe_" + sufijo + ".csv";
                    response.setContentType("aplication/download");
                    response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutCB + "\"");
                    //se avisa el tamaño del archivo al browser
                    response.setContentLength(size);
                    byte[] b = new byte[1048576];
                    int len = 0;
                    while ((len = bis.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                }
                bis.close();
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException ex) {
            Log.error(ex.getMessage(), ex);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
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
