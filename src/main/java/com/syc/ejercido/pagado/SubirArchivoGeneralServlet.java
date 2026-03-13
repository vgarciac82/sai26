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
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import com.syc.contable.core.AplicacionContable;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "SubirArchivoGeneralServlet", urlPatterns = { "/gstnmngr/SubirArchivoGeneralServlet" })
public class SubirArchivoGeneralServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(AplicacionContable.class);

    /**
     * Constructor of the object.
     */
    public SubirArchivoGeneralServlet() {
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
        String valor = "";
        List<FileItem> fileItems = new ArrayList<FileItem>();
        Map<String, String> fieldMap = new Hashtable<String, String>();
        List<FileItem> fileList = new ArrayList<FileItem>();
        String pathUrl = request.getContextPath();
        String pathBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathUrl + "/";
        String mensaje = "";
        try {
            fileItems = procesaArchivos(request);
            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    fieldMap.put(item.getFieldName(), item.getString());
                } else {
                    fileList.add(item);
                }
            }
            SubirArchivoGeneral SubirArchivo = new SubirArchivoGeneral();
            InputStream in = fileList.get(0).getInputStream();
            String tipo = fieldMap.get("tipo");
            if (tipo.equals("archivosTxtRDB")) {
                String banco = fieldMap.get("tBancosRDB");
                String tipoBancomer = fieldMap.get("tipoBancomer");
                String cuentaBancaria = fieldMap.get("cuentaBancaria");
                String fechaCarga = fieldMap.get("fecha");
                String usuario = fieldMap.get("usuario");
                valor = SubirArchivo.enviaRuta(in, tipo, banco, tipoBancomer, cuentaBancaria, fechaCarga, usuario);
            }
            if (valor == "guardado") {
                mensaje = "Archivo Cargado Correctamente";
            } else if (valor.equals("no_guardado")) {
                mensaje = "No Se Guardaron Los Registros Correctamente";
            } else {
                mensaje = "Verificar el Archivo, el Numero de Columnas no Corresponden al Banco ";
            }
        } catch (Exception e) {
            log.error("Error: " + e);
            mensaje = "Error: No Se Guardaron Los Registros Correctamente RDB";
        } finally {
        }
        response.sendRedirect(pathBase + "Generador/rdbCargaArchivoBanco.jsp?mensaje=" + mensaje);
    }

    public List<FileItem> procesaArchivos(HttpServletRequest request) {
        String szPath;
        List<FileItem> fileItems = new ArrayList<FileItem>();
        try {
            // Se construye un objeto para que parsee la petición
            DiskFileUpload fu = new DiskFileUpload();
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setSizeMax(-1);
            // Si excede el 1 Gb en memoria lo
            fu.setSizeThreshold(1048576);
            // escribe a disco
            szPath = getServletContext().getRealPath("/upload/ejercidoPagado");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fu.setRepositoryPath(szPath);
            fileItems = fu.parseRequest(request);
        } catch (Exception e) {
            log.error("Error: " + e);
            System.out.println("Error de Aplicación " + e.getMessage());
        }
        return fileItems;
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
