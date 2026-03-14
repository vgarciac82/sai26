package com.syc.viewer.tags;

import java.io.File;
import java.net.URLEncoder;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.syc.viewer.custom.ImageViewerInterface;
import com.syc.viewer.servlet.ViewerParametersInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageListTag extends TagSupport implements ViewerParametersInterface {

    public static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ImageListTag.class);

    private String param = null;

    private String paginate = null;

    private int paginateCount = 10;

    private String prefix = null;

    public void setParam(String node) {
        this.param = node;
    }

    public String getParam() {
        return param;
    }

    public void setPaginate(String paginate) {
        this.paginate = paginate;
    }

    public String getPaginate() {
        return paginate;
    }

    public void setPaginateCount(int paginateCount) {
        this.paginateCount = paginateCount;
    }

    public int getPaginateCount() {
        return this.paginateCount;
    }

    public String getThumbnailPrefix() {
        return prefix;
    }

    public void setThumbnailPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = request.getSession();
        if (session == null)
            return SKIP_PAGE;
        String selectId = request.getParameter(getParam());
        if (selectId == null) {
            throw new JspException("No se recibio ningun atributo \"param\"");
        }
        String jniName = null;
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
        String imageviewer = null;
        try {
            InitialContext ic = new InitialContext();
            imageviewer = (String) ic.lookup("java:comp/env/imageViewerInterface");
            if (imageviewer == null)
                imageviewer = "com.syc.custom.imageViewerDefault";
        } catch (NamingException exc) {
            imageviewer = "com.syc.custom.imageViewerDefault";
        }
        log.info("Object: {}", "ImageViewerInterface=" + imageviewer);
        ImageViewerInterface imgview = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(imageviewer);
            imgview = (ImageViewerInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("ImageViewerInterface", exc);
            throw new JspException(exc);
        } catch (InstantiationException exc) {
            log.error("ImageViewerInterface", exc);
            throw new JspException(exc);
        } catch (IllegalAccessException exc) {
            log.error("ImageViewerInterface", exc);
            throw new JspException(exc);
        }
        imgview.init(request, jniName);
        int iniPaginate = -1;
        int finPaginate = -1;
        String paginateVal = request.getParameter(getPaginate());
        if (paginateVal != null) {
            iniPaginate = Integer.parseInt(paginateVal.substring(0, paginateVal.indexOf('-')));
            finPaginate = Integer.parseInt(paginateVal.substring(paginateVal.indexOf('-') + 1));
        }
        File[] filesNames = imgview.getFilesList();
        iniPaginate = (iniPaginate == -1) ? 0 : iniPaginate;
        finPaginate = ((finPaginate == -1) ? ((filesNames.length > paginateCount) ? paginateCount : filesNames.length) : finPaginate);
        try {
            if (filesNames.length > 0) {
                if (filesNames[0] != null) {
                    out.println("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
                    String thumbnail = "";
                    for (int i = iniPaginate; i < finPaginate; i++) {
                        thumbnail = filesNames[i].getName();
                        int idx = thumbnail.lastIndexOf('.');
                        if (idx > -1)
                            thumbnail = thumbnail.substring(0, idx) + prefix + thumbnail.substring(idx);
                        thumbnail = URLEncoder.encode(thumbnail, "UTF-8");
                        out.println("\t\t\t\t\t<tr>");
                        // out.print("\t\t\t\t\t\t<td align=\"center\"
                        // width=\"60\" height=\"60\">");
                        out.print("\t\t\t\t\t\t<td align=\"center\">");
                        if (i == iniPaginate)
                            out.println("<input type=\"hidden\" name=\"select\" value=\"" + selectId + "\">");
                        out.print("<a name=\"imgList\" href=\"VisualizadorDeImagen.jsp?" + getParam() + "=" + selectId + "&" + INDEX_KEY + "=" + i + "\" target=\"viewerFrame\">");
                        out.print("<img src=\"" + request.getContextPath() + "/imagestore/" + thumbnail + "?select=" + selectId + "&" + THUMBNAIL_KEY + "=true" + "&" + INDEX_KEY + "=" + i + "\" alt=\"" + filesNames[i].getName() + "\" border=\"1\">");
                        // + "\" width=\"60\" height=\"60\" border=\"0\">");
                        out.print("</a>");
                        out.println("\t\t\t\t\t\t</td>");
                        out.println("\t\t\t\t\t</tr>");
                    }
                }
            } else {
                out.println("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
                out.println("\t\t\t\t\t<tr>\n\t\t\t\t\t\t<td width=\"60\" height=\"60\">&nbsp;</td>\n\t\t\t\t\t</tr>");
            }
            out.println("\t\t\t\t</table>");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return EVAL_PAGE;
    }
}
