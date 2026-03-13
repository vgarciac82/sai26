package com.syc.viewer.servlet;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import javax.media.jai.RenderedOp;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.viewer.GifEncoder;
import com.syc.viewer.ImageManagerJAI;
import com.syc.viewer.custom.ImageViewerInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GetImageServlet", urlPatterns = { "/imagestore/*" })
public class GetImageServlet extends HttpServlet implements ViewerParametersInterface {

    public static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(GetImageServlet.class);

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
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        String selectId = req.getParameter("select");
        if (selectId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
            return;
        }
        ImageViewerInterface imgview = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(imageviewer);
            imgview = (ImageViewerInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("ImageViewerInterface", exc);
            throw new ServletException(exc);
        } catch (InstantiationException exc) {
            log.error("ImageViewerInterface", exc);
            throw new ServletException(exc);
        } catch (IllegalAccessException exc) {
            log.error("ImageViewerInterface", exc);
            throw new ServletException(exc);
        }
        String fileIndexVal = req.getParameter(INDEX_KEY);
        int fileIndex = (fileIndexVal == null) ? 0 : Integer.parseInt(fileIndexVal);
        boolean isThumbnail = false;
        String thumbnailVal = req.getParameter(THUMBNAIL_KEY);
        if (thumbnailVal != null) {
            isThumbnail = "true".equals(thumbnailVal);
        }
        int outputRotate = 0;
        String rotateVal = (String) session.getAttribute(ROTATE_KEY);
        if (!isThumbnail && (rotateVal != null)) {
            outputRotate = Integer.parseInt(rotateVal);
        }
        session.removeAttribute(ROTATE_KEY);
        imgview.init(req, jniName);
        File[] filesNames = imgview.getFilesList();
        if (fileIndex > filesNames.length) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Pagina sin archivo");
            return;
        }
        if ((filesNames.length > 0) && (fileIndex >= 0)) {
            if (filesNames[fileIndex] != null) {
                int pos = filesNames[fileIndex].getName().lastIndexOf(".");
                String ext = (pos > -1) ? filesNames[fileIndex].getName().substring(pos + 1).toLowerCase() : "";
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(filesNames[fileIndex].lastModified());
                c.set(Calendar.MILLISECOND, 0);
                long lastModified = c.getTimeInMillis();
                c.setTimeInMillis(req.getDateHeader("If-Modified-Since"));
                c.set(Calendar.MILLISECOND, 0);
                long ifModifiedSince = c.getTimeInMillis();
                if (((ifModifiedSince != -1) && (lastModified > -1)) && (lastModified <= ifModifiedSince)) {
                    resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
                resp.setHeader("Cache-Control", "max-age=1, must-revalidate");
                resp.setDateHeader("Expires", System.currentTimeMillis() + (1 * 1000));
                resp.setDateHeader("Last-Modified", lastModified);
                RenderedOp outputImage = ImageManagerJAI.load(filesNames[fileIndex].getAbsolutePath());
                if ((outputImage.getWidth() > 0) && (outputImage.getHeight() > 0)) {
                    if ((isThumbnail) && ((outputImage.getWidth() > 60) || (outputImage.getHeight() > 60))) {
                        outputImage = ImageManagerJAI.thumbnail(outputImage, 60);
                    } else if (outputRotate != 0) {
                        outputImage = ImageManagerJAI.rotate(outputImage, outputRotate);
                    }
                    OutputStream os = resp.getOutputStream();
                    if ("gif".equals(ext)) {
                        resp.setContentType("image/gif");
                        try {
                            ImageManagerJAI.writeResult(os, outputImage, "GIF");
                        } catch (IOException ioe) {
                            Frame frame = null;
                            Graphics g = null;
                            try {
                                frame = new Frame();
                                frame.addNotify();
                                Image image = frame.createImage(60, 60);
                                g = image.getGraphics();
                                g.setFont(new Font("Arial", Font.PLAIN, 10));
                                g.drawString("GIF con", 10, 20);
                                g.drawString("mas de 256", 3, 30);
                                g.drawString("colores", 12, 40);
                                GifEncoder encoder = new GifEncoder(image, os);
                                encoder.encode();
                            } finally {
                                if (g != null)
                                    g.dispose();
                                if (frame != null)
                                    frame.removeNotify();
                            }
                        }
                    } else {
                        resp.setContentType("image/jpeg");
                        ImageManagerJAI.writeResult(os, outputImage, "JPEG");
                    }
                    outputImage.dispose();
                    os.flush();
                    os.close();
                }
                outputImage = null;
            }
        }
    }
}
