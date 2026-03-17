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
import com.syc.ejercido.pagado.Anexo1BusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "LayoutAnexo1Servlet", urlPatterns = { "/gstnmngr/generaLayoutAnexo1" })
public class LayoutAnexo1Servlet extends HttpServlet {

    private String CualArchivo = "";

    private static String folioGenerator;

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LayoutAnexo1Servlet.class);

    public LayoutAnexo1Servlet() {
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
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        //String sUsuario = usuario.getLogin();
        String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
        String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
        String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";
        int valor = sFolio.length() - 1;
        String sFolioQuery = sFolio.substring(0, valor);
        ArrayList<String> arrListPago = null;
        ArrayList<String> arrListDocu = null;
        Anexo1BusinessLogic anexoBL = new Anexo1BusinessLogic(GestionInterface.ATT_CONEXION, folioGenerator);
        try {
            try {
                SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
                String sufijo = fecha.format(new Date(System.currentTimeMillis()));
                String[] arrCuentasBancarias = sCuentaBancaria.split(",");
                String sREFERENCIA1_107 = new java.text.SimpleDateFormat("yyMMddhhmmss").format(new java.util.Date());
                sREFERENCIA1_107 = "A_" + anexoBL.getUE(arrCuentasBancarias[0].trim()) + sREFERENCIA1_107;
                //SP
                arrListPago = anexoBL.buscaAnexo1Integrados(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, usuario, sREFERENCIA1_107);
                //DC
                arrListDocu = anexoBL.ArmaDocumentoComprobatorio(sFolioQuery, sREFERENCIA1_107, archivo.equals("1"));
                //Long date = System.currentTimeMillis();
                // para dar el nombre del archivo Pago Directo
                String layoutPago = "SP_ANX_" + sufijo.trim() + ".csv";
                // para dar el nombre del archivo Pago Directo ZIP
                String layoutPagoZip = "SP_ANX_" + sufijo.trim() + ".zip";
                // para dar el nombre del archivo Documentación Comprobatoria
                String layoutDocu = "DC_ANX_" + sufijo.trim() + ".csv";
                // para dar el nombre del archivo Documentación Comprobatoria ZIP
                String layoutDocuZip = "DC_ANX_" + sufijo.trim() + ".zip";
                String layoutZip = "ANXTodo_" + sufijo + ".zip";
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
                try {
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
                } catch (IOException e) {
                }
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
                // These are the files to include in the ZIP file
                String[] filenames = new String[] { layoutPago, layoutDocu };
                ServletOutputStream ouputStream;
                // Create a buffer for reading the files
                byte[] buf = new byte[1024];
                try {
                    ouputStream = response.getOutputStream();
                    // Create the ZIP file
                    ZipOutputStream outZIP = new ZipOutputStream(ouputStream);
                    for (int i = 0; i < filenames.length; i++) {
                        // Compress the files
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
                    response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutZip + "\"");
                    outZIP.finish();
                    // Complete the ZIP file
                    outZIP.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
