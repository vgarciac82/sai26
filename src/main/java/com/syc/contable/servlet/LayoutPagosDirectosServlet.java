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
import org.apache.log4j.Logger;
import com.syc.contable.PagosDirectosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "LayoutPagosDirectosServlet", urlPatterns = { "/gstnmngr/generaLayoutPagosDirectos" })
public class LayoutPagosDirectosServlet extends HttpServlet {

    private String cualArchivo = "";

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(LayoutPagosDirectosServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
        log.trace("Tipo de layout: " + cualArchivo);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String sUsuario = usuario.getLogin();
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";
        String tipoLayout = (request.getParameter("tipoPagoLayout") != null) ? request.getParameter("tipoPagoLayout").trim() : "";
        cualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
        int valor = sFolio.length();
        String sFolioQuery = sFolio.substring(0, valor - 1);
        ArrayList<StringBuilder> arrListPago = null;
        ArrayList<StringBuilder> arrListDocu = null;
        String msgRetorno = "";
        PagosDirectosBussinessLogic cmpBL = new PagosDirectosBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            // para dar el nombre del archivo
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            if ("DIRECTO".equals(tipoLayout)) {
                arrListPago = cmpBL.generaLayoutPagosDirectos(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario);
                arrListDocu = cmpBL.armaDocumentoComprobatorio(sFolioQuery);
            } else {
                arrListPago = cmpBL.generaLayoutPagoDiversoCompromiso(usuario, sFolioQuery, sCuentaBancaria, sFecha, sLeyenda);
                arrListDocu = cmpBL.armaDocumentoComprobatorio(sFolioQuery);
                //Pagos con compromiso
            }
            /*
			 * para dar el nombre del los archivos
			 */
            File layoutPago = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PAGODIRECTO" + sufijo.trim() + ".csv");
            File layoutDocu = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "DOCCOMP" + sufijo.trim() + ".csv");
            File layoutZip = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PAGODIRECTOTodo" + sufijo + ".zip");
            /*
			 * Guarda el pago
			 */
            BufferedWriter out = new BufferedWriter(new FileWriter(layoutPago));
            StringBuffer archivoPago = new StringBuffer();
            for (int i = 0; i < arrListPago.size(); i++) {
                archivoPago.append(arrListPago.get(i));
            }
            String outTextPago = archivoPago.toString();
            out.write(outTextPago);
            // fin de guarda pago
            out.close();
            BufferedWriter outDocu = new BufferedWriter(new FileWriter(layoutDocu));
            StringBuffer archivoDocu = new StringBuffer();
            for (int i = 0; i < arrListDocu.size(); i++) {
                archivoDocu.append(arrListDocu.get(i));
            }
            String outTextDocu = archivoDocu.toString();
            outDocu.write(outTextDocu);
            // fin de guarda documento
            outDocu.close();
            // // These are the files to include in the ZIP file
            File[] filenames = new File[] { layoutPago, layoutDocu };
            ServletOutputStream ouputStream;
            byte[] buf = new byte[1024];
            try {
                ouputStream = response.getOutputStream();
                ZipOutputStream outZIP = new ZipOutputStream(ouputStream);
                for (int i = 0; i < filenames.length; i++) {
                    FileInputStream in = new FileInputStream(filenames[i]);
                    outZIP.putNextEntry(new ZipEntry(filenames[i].getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
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
            }
            layoutPago.delete();
            layoutDocu.delete();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
            log.error(e, e);
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
        }
        session.setAttribute("RESULT", msgRetorno);
        response.sendRedirect("../Generador/IntegraLayoutPagosDirecto.jsp");
    }
}
