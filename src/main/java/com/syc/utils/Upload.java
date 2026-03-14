package com.syc.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItem;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import java.nio.file.Paths;

@SuppressWarnings("serial")
public class Upload extends HttpServlet {

    String path;

    /**
     * Constructor of the object.
     */
    public Upload() {
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
        out.println(", using the POST method<br /><b>Error: the form is xwww url encoded<b/>");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
            //Se construye un objeto para que parsee la petición
            JakartaServletFileUpload fu = new JakartaServletFileUpload(factory);
            //tamaño máximo que aceptará el archivo
            //fu.setSizeMax(1024*64);
            path = req.getRealPath("/upload");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            List fileItems = fu.parseRequest(req);
            if (fileItems == null) {
                return;
            }
            Iterator i = fileItems.iterator();
            FileItem actual = null;
            while (i.hasNext()) {
                actual = (FileItem) i.next();
                String fileName = actual.getName();
                File archivo = new File(fu.getRepositoryPath() + "\\" + fileName);
                actual.write(archivo.toPath());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
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
