package com.syc.ejercido.pagado;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

//import com.syc.zip.ZipEntry;
//import com.syc.zip.ZipOutputStream;
/**
 * @author Martha Aurora Sánchez Valdivieso
 * para SYC Constructores de Sistemas
 * desarrollo gestion_conagua_sif
 * México D.F. 23/01/2012
 */
@WebServlet(name = "LayoutCompromisosNOMServlet", urlPatterns = { "/gstnmngr/generaLayoutNOMCompromisos" })
public class LayoutCompromisosNOMServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(LayoutCompromisosNOMServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CompromisoNOMBussinessLogic cmpbl = new CompromisoNOMBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            // recupera los campos caNoCompromisos de la lista de los que serán enviados
            String compromisos = (request.getParameter("BuscarAplicarComprometido") != null) ? request.getParameter("BuscarAplicarComprometido").trim() : "";
            //int valor = compromisos.length();
            //.substring(0, valor);
            String compromisosQuery = compromisos;
            // recupera los campos idContratos de la lista de los que serán enviados
            String contratos = (request.getParameter("BuscarAplicarComprometido") != null) ? request.getParameter("BuscarAplicarComprometido").trim() : "";
            //int valor2 = contratos.length() ;
            //.substring(0, valor2);
            String contratosQuery = contratos;
            if ("".equals(compromisosQuery) && "".equals(contratosQuery)) {
                compromisos = (request.getParameter("BuscarAplicarComprometidoAmpliacion") != null) ? request.getParameter("BuscarAplicarComprometidoAmpliacion").trim() : "";
                compromisosQuery = compromisos;
                contratos = (request.getParameter("BuscarAplicarComprometidoAmpliacion") != null) ? request.getParameter("BuscarAplicarComprometidoAmpliacion").trim() : "";
                contratosQuery = contratos;
            }
            // Da el nombre del archivo
            DateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmsss");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            String filename = System.getProperty("java.io.tmpdir") + File.separatorChar + "Compromisos" + sufijo + ".csv";
            String filenzip = System.getProperty("java.io.tmpdir") + File.separatorChar + "Compromisos" + sufijo + ".zip";
            ArrayList<String> cvsDataSQL = cmpbl.buscaCompromisos(compromisosQuery, contratosQuery);
            // Guarda el pago
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            StringBuffer archivoPago = new StringBuffer();
            for (int i = 0; i < cvsDataSQL.size(); i++) {
                archivoPago.append(cvsDataSQL.get(i));
            }
            String outTextPago = archivoPago.toString();
            out.write(outTextPago);
            out.flush();
            // fin de guarda pago
            out.close();
            ServletOutputStream outS = null;
            ByteArrayInputStream byteArrayInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            try {
                response.setContentType("text/csv");
                String disposition = "attachment; fileName=" + filename;
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
            File ficheroPag = new File(filename);
            ficheroPag.delete();
            File ficheroDoc = new File(filenzip);
            ficheroDoc.delete();
            cmpbl.estatusCompromiso(compromisosQuery);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new ServletException(exc);
        } finally {
            //if (out != null)
            //	out.close();
        }
    }
}
