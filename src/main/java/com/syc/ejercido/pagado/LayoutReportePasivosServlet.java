package com.syc.ejercido.pagado;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItem;
import com.syc.contable.core.AplicacionContable;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;

@WebServlet(name = "LayoutReportePasivosServlet", urlPatterns = { "/gstnmngr/LayoutReportePasivosServlet" })
public class LayoutReportePasivosServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = -2544499444970670159L;

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    /**
     * Constructor of the object.
     */
    public LayoutReportePasivosServlet() {
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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        out.println("  <BODY>");
        out.print("    This is ");
        out.print(this.getClass());
        out.println(", using the GET method");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
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
        if (session == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        List<FileItem> fileItems = new ArrayList<FileItem>();
        Map<String, String> fieldMap = new Hashtable<String, String>();
        List<FileItem> fileList = new ArrayList<FileItem>();
        String pathUrl = request.getContextPath();
        String pathBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathUrl + "/";
        String valor = "";
        try {
            ReportePasivosBussinessLogic guardaInformacion = new ReportePasivosBussinessLogic();
            fileItems = procesaArchivos(request);
            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    fieldMap.put(item.getFieldName(), item.getString());
                } else {
                    fileList.add(item);
                }
            }
            String f = fieldMap.get("fAplicacion");
            String[] fA = f.split("/");
            String fAplicacion = fA[0] + "/" + fA[1] + "/" + fA[2];
            String mes = fA[1];
            String ur = fieldMap.get("cUnidadResponsable");
            String cEjecicicioFiscal = fieldMap.get("aEjercicioFiscal");
            String login = fieldMap.get("login");
            String cc = fieldMap.get("cCentroContable");
            String folio = fieldMap.get("folioTempGral");
            if (!folio.equals("")) {
                valor = guardaInformacion.aplicarInformacion(folio, fAplicacion, ur, cEjecicicioFiscal, login, mes, cc);
            } else {
                InputStream in = fileList.get(0).getInputStream();
                valor = guardaInformacion.validarInformacion(in, fAplicacion, ur, cEjecicicioFiscal, login, mes, cc);
            }
        } catch (Exception e) {
            log.error("Error: no se cargo archivo");
            valor = "Error";
        }
        log.info("Object: {}", valor);
        //out.println(valor);
        response.sendRedirect(pathBase + "Generador/LayoutActualizaPasivoContingenteLaboral.jsp?mensaje=" + valor);
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }

    public List<FileItem> procesaArchivos(HttpServletRequest request) {
        String szPath;
        List<FileItem> fileItems = new ArrayList<FileItem>();
        try {
            DiskFileItemFactory factory = DiskFileItemFactory.builder().setBufferSize(1024).get();
        factory.setRepository(new File(szPath));
            // Se construye un objeto para que parsee la petición
            JakartaServletFileUpload fu = new JakartaServletFileUpload(factory);
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setFileSizeMax(-1);
            // Si excede el 1 Gb en memoria lo
            fu.setSizeThreshold(1048576);
            // escribe a disco
            szPath = getServletContext().getRealPath("/upload/ejercidoPagado");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fileItems = fu.parseRequest(request);
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            System.out.println("Error de Aplicación " + e.getMessage());
        }
        return fileItems;
    }
}
