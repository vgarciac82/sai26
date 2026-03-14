/**
 */
package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import com.syc.contable.PagosDiversosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;

/**
 * @author Propietario
 */
@WebServlet(name = "GeneraLayoutDiversoAmbiental", urlPatterns = { "/gstnmngr/generaLayoutAmbiental" })
public class LayoutPagoDiversoAmbientalServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -1896573958975677714L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("No se encuentra session activa. Reingrese al sistema he intente de nuevo.");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("No se encuentra session activa. Reingrese al sistema he intente de nuevo.");
        //String sUsuario = u.getLogin();
        String sFolio = (req.getParameter("sDataH") != null) ? req.getParameter("sDataH").trim() : "";
        String sCuentaBancaria = (req.getParameter("sDataHCB") != null) ? req.getParameter("sDataHCB").trim() : "";
        //String sFecha = (req.getParameter("sDataHFecha") != null) ? req.getParameter("sDataHFecha").trim() : "";
        //String sLeyenda = (req.getParameter("sDataHLeyenda") != null) ? req.getParameter("sDataHLeyenda").trim() : "";
        //int valor = sFolio.length() - 1;
        //String sFolioQuery = sFolio.substring(0, valor);
        ArrayList<String> LayoutBanorte = null;
        ArrayList<String> LayoutSpei = null;
        ArrayList<String> LayoutBanorteIP = null;
        ArrayList<String> LayoutSpeiIP = null;
        String cuentaIP = "072320012524618946";
        PagosDiversosBussinessLogic cmpBL = new PagosDiversosBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            String val = sFolio.substring(sFolio.length() - 1);
            if (",".equals(val))
                sFolio = sFolio.substring(0, sFolio.length() - 1);
            // r.substring(0, r.length()-1);
            LayoutBanorte = cmpBL.ArmaLayoutBanco(sFolio, true, sCuentaBancaria);
            LayoutSpei = cmpBL.ArmaLayoutBanco(sFolio, false, sCuentaBancaria);
            LayoutBanorteIP = cmpBL.ArmaLayoutBancoIP(sFolio, true, cuentaIP);
            LayoutSpeiIP = cmpBL.ArmaLayoutBancoIP(sFolio, false, cuentaIP);
            //cmpBL.generaLayoutAmbiental(resp, sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario);
            String strlayoutBanorte = "LayoutBanorte_" + sufijo.trim() + ".txt";
            String strlayoutSpei = "LayoutSpei_" + sufijo.trim() + ".txt";
            String strlayoutBanorteIP = "LayoutBanorte_IP2821_" + sufijo.trim() + ".txt";
            String strlayoutSpeiIP = "LayoutSpei_IP2821_" + sufijo.trim() + ".txt";
            String layoutBancoZip = "LayoutsBanco_" + sufijo.trim() + ".zip";
            // Guarda el layout BANORTE
            BufferedWriter out = new BufferedWriter(new FileWriter(strlayoutBanorte));
            StringBuffer archivoBanorte = new StringBuffer();
            for (int i = 0; i < LayoutBanorte.size(); i++) {
                archivoBanorte.append(LayoutBanorte.get(i));
            }
            String outTextBanorte = archivoBanorte.toString();
            out.write(outTextBanorte);
            // fin Layout BANORTE
            out.close();
            // Guarda el layout SPEI
            BufferedWriter outSpei = new BufferedWriter(new FileWriter(strlayoutSpei));
            StringBuffer archivoSpei = new StringBuffer();
            for (int i = 0; i < LayoutSpei.size(); i++) {
                archivoSpei.append(LayoutSpei.get(i));
            }
            String outTextSpei = archivoSpei.toString();
            outSpei.write(outTextSpei);
            // fin Layout SPEI
            outSpei.close();
            // Guarda el layout BANORTE IP
            BufferedWriter outIP = new BufferedWriter(new FileWriter(strlayoutBanorteIP));
            StringBuffer archivoBanorteIP = new StringBuffer();
            for (int i = 0; i < LayoutBanorteIP.size(); i++) {
                archivoBanorteIP.append(LayoutBanorteIP.get(i));
            }
            String outTextBanorteIP = archivoBanorteIP.toString();
            outIP.write(outTextBanorteIP);
            // fin Layout BANORTE
            outIP.close();
            // Guarda el layout SPEI IP
            BufferedWriter outSpeiIP = new BufferedWriter(new FileWriter(strlayoutSpeiIP));
            StringBuffer archivoSpeiIP = new StringBuffer();
            for (int i = 0; i < LayoutSpeiIP.size(); i++) {
                archivoSpeiIP.append(LayoutSpeiIP.get(i));
            }
            String outTextSpeiIP = archivoSpeiIP.toString();
            outSpeiIP.write(outTextSpeiIP);
            // fin Layout SPEI
            outSpeiIP.close();
            // These are the files to include in the ZIP file
            String[] filenames = new String[] { strlayoutBanorte, strlayoutSpei, strlayoutBanorteIP, strlayoutSpeiIP };
            ServletOutputStream ouputStream;
            // Create a buffer for reading the files
            byte[] buf = new byte[1024];
            resp.setContentType("application/zip");
            resp.setHeader("Content-Disposition", "attachment;filename=\"" + layoutBancoZip + "\"");
            try {
                ouputStream = resp.getOutputStream();
                // Create the ZIP file
                ZipOutputStream outZIP = new ZipOutputStream(ouputStream);
                for (int i = 0; i < filenames.length; i++) {
                    // Compress the files
                    FileInputStream in = new FileInputStream(filenames[i]);
                    // Add ZIP entry to output stream.
                    outZIP.putNextEntry(new ZipEntry(filenames[i]));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        // Transfer bytes from the file to the ZIP file
                        outZIP.write(buf, 0, len);
                    }
                    // Complete the entry
                    outZIP.closeEntry();
                    in.close();
                }
                outZIP.finish();
                // Complete the ZIP file
                outZIP.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File ficheroBanorte = new File(strlayoutBanorte);
            ficheroBanorte.delete();
            File ficheroSpei = new File(strlayoutSpei);
            ficheroSpei.delete();
            File ficheroBanorteIP = new File(strlayoutBanorteIP);
            ficheroBanorteIP.delete();
            File ficheroSpeiIP = new File(strlayoutSpeiIP);
            ficheroSpeiIP.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
