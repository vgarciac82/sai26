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
import java.util.List;
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
import com.syc.contable.core.PenasConvIntDetalle;
import com.syc.contable.core.PenasConvIntEncabezado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "PenasConvIntegradasServlet", urlPatterns = { "/PenasConvencionalesInt/crear" })
public class PenasConvIntegradasServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -6209553370385667777L;

    private static final Logger log = LoggerFactory.getLogger(PenasConvIntegradasServlet.class);

    private String jniName;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        if (sesion == null) {
            return;
        }
        Usuario u = (Usuario) sesion.getAttribute(ATT_USER);
        if (u == null) {
            return;
        }
        String cxpAI = req.getParameter("caNoContrarreciboInt").trim();
        String nFolio = req.getParameter("id_caso").trim();
        String fecha = "2015/03/11";
        String cBancaria = req.getParameter("CuentaBancaria").trim();
        String Leyenda = req.getParameter("claveLeyenda").trim();
        String accion = req.getParameter("accion");
        String cBEN = req.getParameter("cIDRFC2");
        switch(Integer.parseInt(accion, 10)) {
            case 1:
                try {
                    log.info("Insertando encabezado y detalle de la Integración");
                    PenasConvIntEncabezado encabezado = PenasConvIntegradasBusinessLogic.instanceHeaderFromRequest(req);
                    List<PenasConvIntDetalle> detalle = PenasConvIntegradasBusinessLogic.instanceDetailFromRequest(req);
                    PenasConvIntegradasBusinessLogic pcIntBl = new PenasConvIntegradasBusinessLogic(jniName);
                    pcIntBl.setEncabezado(encabezado);
                    pcIntBl.setDetalle(detalle);
                    int r = pcIntBl.insert();
                    ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(r));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error insertando la informacion: " + e);
                }
                break;
            case 2:
                try {
                    log.info("Generando Layout de la integracion");
                    ArrayList<String> arrListDocu = null;
                    PenasConvIntegradasBusinessLogic cmpBL = new PenasConvIntegradasBusinessLogic(GestionInterface.ATT_CONEXION);
                    try {
                        SimpleDateFormat fecha1 = new SimpleDateFormat("yyyyMMddhhmm");
                        String sufijo = fecha1.format(new Date(System.currentTimeMillis()));
                        arrListDocu = cmpBL.buscaPenasConvIntegradas(cxpAI, fecha, cBancaria, Leyenda, nFolio, cBEN);
                        File layoutDoc = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PENASCONV_" + sufijo + ".csv");
                        //String layoutDocu = System.getProperty( "java.io.tmpdir" ) + File.separatorChar  + cxpAI.trim() +  ".csv";					// para dar el nombre del archivo Documentación Comprobatoria
                        File layoutZip = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "INTEGRACION_PENAS_" + sufijo + ".zip");
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
                        File[] filenames = new File[] { layoutDoc };
                        ServletOutputStream ouputStream;
                        byte[] buf = new byte[1024];
                        try {
                            ouputStream = resp.getOutputStream();
                            // Create the ZIP file
                            ZipOutputStream outZIP = new ZipOutputStream(ouputStream);
                            for (int i = 0; i < filenames.length; i++) {
                                // Compress the files
                                FileInputStream in = new FileInputStream(filenames[i]);
                                // Add ZIP entry to output stream.
                                outZIP.putNextEntry(new ZipEntry(filenames[i].getName()));
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    // Transfer bytes from the file to the ZIP file
                                    outZIP.write(buf, 0, len);
                                }
                                // Complete the entry
                                outZIP.closeEntry();
                                in.close();
                            }
                            resp.setContentType("application/zip");
                            resp.setHeader("Content-Disposition", "attachment;filename=\"" + layoutZip.getName() + "\"");
                            outZIP.finish();
                            // Complete the ZIP file
                            outZIP.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                            e.printStackTrace();
                        }
                        layoutDoc.delete();
                        layoutZip.delete();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
