package com.syc.sai.firmaElectronica.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.UsuarioBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ConsultaArchivoFirmaElectronicaServlet", urlPatterns = { "/muestraDocumento" })
public class ConsultaArchivoFirmaElectronicaServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 1L;

    private String jniName;

    private static final Logger log = LoggerFactory.getLogger(ConsultaArchivoFirmaElectronicaServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(jniName);
            int year = Util.getCurrentYear();
            int ef = Integer.parseInt(efbl.getEjercicioFiscalActivo().getaEjercicioFiscal());
            if (year != ef) {
                String uri = req.getScheme() + "://" + req.getServerName() + ("http".equals(req.getScheme()) && req.getServerPort() == 80 || "https".equals(req.getScheme()) && req.getServerPort() == 443 ? "" : ":" + req.getServerPort()) + "/sai_" + year + "/muestraDocumento" + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
                resp.sendRedirect(uri);
                return;
            }
            String nodoFortimax = req.getParameter("fortimax");
            String numeroEmpleado = req.getParameter("u");
            Usuario u = new Usuario();
            u.setNumeroEmpleado(numeroEmpleado);
            UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(jniName);
            u = ubl.getUsuario(u);
            if (u == null) {
                log.warn("No hay Usuario en sesion");
                throw new ServletException("No se encontro usuario con el numero de empleado: " + numeroEmpleado);
            }
            Fortimax fortimax = new Fortimax(nodoFortimax);
            DocumentoBussinessLogic dbl = new DocumentoBussinessLogic();
            Documento d = dbl.buscaDocumento(fortimax);
            String pathDocumento = d.getPaginaDocumento(0).getFullPathFileName();
            doDownload(resp, pathDocumento, d.getNombreDocumento() + "." + d.getExtension());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                Util.sendHTMLErrorMsg(resp, e);
            } catch (Exception e2) {
                log.warn("No se pudo notificar la causa de la excepcion: " + e2, e2);
            }
        }
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
        // 10K buffer
        byte[] bbuf = new byte[10 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
        in.close();
        out.flush();
        out.close();
    }

    @Override
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
}
