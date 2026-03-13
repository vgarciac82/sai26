package com.syc.viewer.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.viewer.custom.ImageViewerInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ImageViewerServlet", urlPatterns = { "/imgmng/image-viewer.jsp" })
public class ImageViewerServlet extends HttpServlet implements ViewerParametersInterface {

    public static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ImageViewerServlet.class);

    private String jniName = null;

    private String imageviewer = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            imageviewer = (String) ic.lookup("java:comp/env/imageViewerInterface");
            if (imageviewer == null) {
                imageviewer = "com.syc.custom.imageViewerDefault";
                log.info("Environment Entry \"imageViewerInterface\" nula usando default \"" + imageviewer + "\"");
            } else
                log.info("imageViewerInterface=" + imageviewer);
        } catch (NamingException exc) {
            imageviewer = "com.syc.custom.imageViewerDefault";
            log.info("Environment Entry \"imageViewerInterface\" no definida usando default \"" + imageviewer + "\"");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (session == null)
            throw new ServletException("No se logro crear la sesion");
        String treeNodeId = req.getParameter("select");
        String idxVal = req.getParameter(INDEX_KEY);
        boolean isDel = "true".equals(req.getParameter(DELETE_KEY));
        if (treeNodeId == null)
            throw new ServletException("Llamada invalida, falta parametro 'select'");
        ImageViewerInterface imgview = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(imageviewer);
            imgview = (ImageViewerInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("imageViewerInterface", exc);
            throw new ServletException(exc);
        } catch (InstantiationException exc) {
            log.error("imageViewerInterface", exc);
            throw new ServletException(exc);
        } catch (IllegalAccessException exc) {
            log.error("imageViewerInterface", exc);
            throw new ServletException(exc);
        }
        imgview.init(req, jniName);
        File[] fl = imgview.getFilesList();
        if (fl == null)
            throw new ServletException("No se logro recuperar lista de Archivos");
        int maxIndx = fl.length;
        session.setAttribute(INDEX_MAX, String.valueOf(maxIndx));
        StringBuffer queryString = new StringBuffer();
        if (treeNodeId != null) {
            queryString.append("?select=" + treeNodeId + "&" + INDEX_MAX + "=" + maxIndx);
            if ((idxVal != null) && !isDel)
                queryString.append("&" + LOAD_KEY + "=last&" + INDEX_KEY + "=" + Integer.parseInt(idxVal));
            else if ((idxVal != null) && isDel)
                queryString.append("&" + LOAD_KEY + "=current&image.index=" + Integer.parseInt(idxVal));
            else
                queryString.append("&" + INDEX_KEY + "=" + (fl.length == 0 ? "-1" : "0"));
        }
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Visor De Imagenes</title>");
        out.println("</head>");
        out.println("<frameset rows=\"*\" cols=\"84%,16%\" framespacing=\"0\" frameborder=\"no\" border=\"1\">");
        out.println("<frame name=\"viewerFrame\" src=\"VisualizadorDeImagen.jsp" + queryString.toString() + "\" scrolling=\"no\" noresize>");
        out.println("<frame name=\"listFrame\" src=\"ListaDeImagenes.jsp?select=" + treeNodeId + "&" + INDEX_MAX + "=" + maxIndx + "\" scrolling=\"no\" noresize>");
        out.println("</frameset>");
        out.println("<noframes>");
        out.println("<body>");
        out.println("<h1>Su Browser no soprta &lt;frames&gt;</h1>");
        out.println("</body>");
        out.println("</noframes>");
        out.println("</html>");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
