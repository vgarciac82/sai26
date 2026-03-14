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
import java.util.Base64;

@WebServlet(name = "LayoutRelacionGastosServlet", urlPatterns = { "/gstnmngr/generaLayoutRelacionGastos" })
public class LayoutRelacionGastosServlet extends HttpServlet {

    /**
     */
    private String CualArchivo = "";

    private static String folioGenerator;

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LayoutRelacionGastosServlet.class);

    /**
     * Constructor of the object.
     */
    public LayoutRelacionGastosServlet() {
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
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Put your code here
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String archivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
        if (archivo == null) {
            throw new ServletException("El archivo no debe ir nulo");
        }
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String sUsuario = usuario.getLogin();
        String msgRetorno = "";
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";
        int valor = sFolio.length() - 1;
        String sFolioQuery = sFolio.substring(0, valor);
        ArrayList<String> arrListPago = null;
        ArrayList<String> arrListDocu = null;
        RelacionGastosBussinessLogic cmpBL = new RelacionGastosBussinessLogic(GestionInterface.ATT_CONEXION, folioGenerator);
        try {
            try {
                // para dar el nombre del archivo
                SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
                String sufijo = fecha.format(new Date(System.currentTimeMillis()));
                String[] arrCuentasBancarias = sCuentaBancaria.split(",");
                String sREFERENCIA1_107 = new java.text.SimpleDateFormat("yyMMddhhmmss").format(new java.util.Date());
                sREFERENCIA1_107 = cmpBL.getUE(arrCuentasBancarias[0].trim()) + sREFERENCIA1_107;
                if (archivo.equals("1"))
                    arrListPago = cmpBL.buscaCompromisos(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario);
                else
                    arrListPago = cmpBL.buscaRelacionGastosIntegrados(sFolioQuery, sCuentaBancaria, sLeyenda, usuario, sREFERENCIA1_107);
                arrListDocu = cmpBL.ArmaDocumentoComprobatorio(sFolioQuery, sREFERENCIA1_107, archivo.equals("1"));
                File layoutPago = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PDRELGASTOS" + sufijo.trim() + ".csv");
                File layoutDocu = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "DOCCOMP" + sufijo.trim() + ".csv");
                File layoutZip = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PDRELGASTOSTodo" + sufijo + ".zip");
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
                        outZIP.closeEntry();
                        in.close();
                    }
                    response.setContentType("application/zip");
                    response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutZip.getName() + "\"");
                    outZIP.finish();
                    outZIP.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    throw e;
                }
                layoutPago.delete();
                layoutDocu.delete();
                layoutZip.delete();
            } catch (FileNotFoundException ex) {
                log.error(ex.getMessage(), ex);
                throw ex;
            }
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
        }
        session.setAttribute("RESULT", msgRetorno);
        response.sendRedirect("../Generador/IntegraLayoutRelacionGastos.jsp");
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
