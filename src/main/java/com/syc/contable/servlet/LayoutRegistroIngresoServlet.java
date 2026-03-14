package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import java.util.Map;
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
//import com.syc.gestion.core.Caso;
//import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.registroingresos.RegistroIngresosBussinesLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "LayoutRegistroIngresoServlet", urlPatterns = { "/gstnmngr/generaLayoutRegistroIngreso" })
public class LayoutRegistroIngresoServlet extends HttpServlet {

    private String CualArchivo = "";

    private static String folioGenerator;

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LayoutRegistroIngresoServlet.class);

    public LayoutRegistroIngresoServlet() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";
        int valor = sFolio.length() - 1;
        String sFolioQuery = sFolio.substring(0, valor);
        ArrayList<String> arrListPago = null;
        ArrayList<String> arrListDocu = null;
        RegistroIngresosBussinesLogic RegIngBL = new RegistroIngresosBussinesLogic(GestionInterface.ATT_CONEXION);
        try {
            try {
                SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
                String sufijo = fecha.format(new Date(System.currentTimeMillis()));
                String[] arrCuentasBancarias = sCuentaBancaria.split(",");
                String sREFERENCIA1_107 = new java.text.SimpleDateFormat("yyMMddhhmmss").format(new java.util.Date());
                sREFERENCIA1_107 = "IF_" + RegIngBL.getUE(arrCuentasBancarias[0].trim()) + sREFERENCIA1_107;
                String centroContableUser = usuario.getPropiedad("CCENTROCONTABLE").getValor();
                //SP
                arrListPago = RegIngBL.buscaRIFIntegrados(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, usuario, sREFERENCIA1_107, folioGenerator, centroContableUser);
                //DC
                arrListDocu = RegIngBL.ArmaDocumentoComprobatorio(sFolioQuery, sREFERENCIA1_107, archivo.equals("1"));
                // para dar el nombre del archivo Pago Directo
                File layoutPago = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "SP_IF_" + sufijo.trim() + ".csv");
                // para dar el nombre del archivo Documentación Comprobatoria
                File layoutDocu = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "DC_IF_" + sufijo.trim() + ".csv");
                File layoutZip = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "RIFTodo_" + sufijo + ".zip");
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
                byte[] buf = new byte[1024];
                // Guarda el pago Zip
                byte[] bufPag = new byte[1024];
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
                    outZIP.close();
                } catch (IOException e) {
                }
                layoutPago.delete();
                layoutDocu.delete();
                layoutZip.delete();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
