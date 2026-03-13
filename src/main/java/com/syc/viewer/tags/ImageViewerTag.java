package com.syc.viewer.tags;

import java.io.IOException;
import java.net.URLEncoder;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.syc.viewer.custom.ImageViewerInterface;
import com.syc.viewer.servlet.ViewerParametersInterface;

public class ImageViewerTag extends TagSupport implements ViewerParametersInterface {

	public static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(ImageViewerTag.class);

	private String id = null;
	private String param = null;
	private float quality = 0.75F;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getParam() {
		return param;
	}

	public void setQuality(String quality) {
		this.quality = Float.parseFloat(quality);
		if ((this.quality < 0.0F) || (this.quality > 1.0F))
			throw new IllegalArgumentException("Calidad \"" + quality + "\" fuera de rango (0.0 - 1.0)");
	}

	public String getQuality() {
		return Float.toString(quality);
	}

	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpSession session = request.getSession();

		if (session == null) {
			return SKIP_BODY;
		}

		String selectId = request.getParameter(getParam());
		if (selectId == null) {
			return SKIP_BODY;
		}


		String jniName = null;
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

		String imageviewer = null;
		try {
			InitialContext ic = new InitialContext();
			imageviewer = (String) ic.lookup("java:comp/env/imageViewerInterface");

			if (imageviewer == null)
				imageviewer = "com.syc.custom.imageViewerDefault";
		} catch (NamingException exc) {
			imageviewer = "com.syc.custom.imageViewerDefault";
		}

		log.info("ImageViewerInterface=" + imageviewer);

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

		int fileIndex = -1;
		String fileIndexVal = request.getParameter(INDEX_KEY);
		if (fileIndexVal == null) {
			return EVAL_PAGE;
		} else
			fileIndex = Integer.parseInt(fileIndexVal);

		String fileName = "vacio";
		String fullPathFileName = "vacio";

		if ((imgview.getFilesList().length > 0) && (fileIndex > -1)) {
			fileName = imgview.getFilesList()[fileIndex].getName();
			fullPathFileName = imgview.getFilesList()[fileIndex].getPath();
		}

		try {
			boolean reset = "true".equals(request.getParameter("reset"));

			int heightSize = 0;
			String heightVal = reset ? "0" : request.getParameter(HEIGHT_KEY);
			if (heightVal != null) {
				session.setAttribute(HEIGHT_KEY, heightVal);
				heightVal = (String) session.getAttribute(HEIGHT_KEY);
				if (heightVal != null)
					heightSize = Integer.parseInt(heightVal);
			}

			int widthSize = 0;
			String sizeVal = reset ? "0" : request.getParameter(WIDTH_KEY);
			if (sizeVal != null) {
				session.setAttribute(WIDTH_KEY, sizeVal);
				sizeVal = (String) session.getAttribute(WIDTH_KEY);
				if (sizeVal != null)
					widthSize = Integer.parseInt(sizeVal);
			}

			int outputRotate = 0;
			String rotateVal = reset ? "0" : request.getParameter(ROTATE_KEY);
			if (rotateVal != null) {
				session.setAttribute(ROTATE_KEY, rotateVal);
				rotateVal = (String) session.getAttribute(ROTATE_KEY);
				if (rotateVal != null)
					outputRotate = Integer.parseInt(rotateVal);
			}

			RenderedOp outputImage = null;
			if (!"vacio".equals(fullPathFileName)) {
				outputImage = JAI.create("fileload", fullPathFileName);

				if ((widthSize == 0) && (heightSize == 0)) {
					if ((outputRotate == 90) || (outputRotate == 270)) {
						widthSize = outputImage.getHeight();
						heightSize = outputImage.getWidth();
					} else {
						widthSize = outputImage.getWidth();
						heightSize = outputImage.getHeight();
					}
				} else {
					int tmpSize = widthSize;
					widthSize = heightSize;
					heightSize = tmpSize;
				}
			}

			String newFileName = fileName;
			int lastDot = newFileName.lastIndexOf('.');
			if (lastDot > -1)
				newFileName = newFileName.substring(0, lastDot + 1);

			newFileName = newFileName + ((outputRotate == 0) ? "jpg" : outputRotate + ".jpg");
			newFileName = URLEncoder.encode(newFileName, "UTF-8");

			out.print("<img id=\"" + getId() + "\" src=\"" + request.getContextPath() + "/imagestore/" + newFileName
				+ "?" + getParam() + "=" + selectId + "&" + INDEX_KEY + "=" + fileIndex + "\" width=\"" + widthSize
				+ "\" height=\"" + heightSize + "\" alt=\"Tamaño " + widthSize + " x " + heightSize + "\">");

			out.print("<input type=\"hidden\" name=\"" + getParam() + "\" value=\"" + selectId + "\">");
			out.print("<input type=\"hidden\" name=\"" + NAME_KEY + "\" value=\"" + fileName + "\">");
			out.print("<input type=\"hidden\" name=\"" + INDEX_KEY + "\" value=\"" + fileIndex + "\">");
			out.print("<input type=\"hidden\" name=\"" + WIDTH_KEY + "\" value=\"" + widthSize + "\">");
			out.print("<input type=\"hidden\" name=\"" + HEIGHT_KEY + "\" value=\"" + heightSize + "\">");
			out.println("<input type=\"hidden\" name=\"" + ROTATE_KEY + "\" value=\"" + outputRotate + "\">");

			outputImage = null;
		} catch (IOException e) {
			log.error(e);
		}

		return EVAL_PAGE;
	}

	/**
	 * Regresa el path de contexto del JSP o Servlet incluyendo la ultima diagonal.
	 */
	public String getContextPath(PageContext pageContext) {
		return getContextPath((HttpServletRequest) pageContext.getRequest());
	}

	/**
	 * Regresa el path de contexto del JSP o Servlet incluyendo la ultima diagonal.
	 */
	public String getContextPath(HttpServletRequest req) {
		String servletPath = req.getServletPath();
		ServletContext servletContext = pageContext.getServletContext();
		String realPath = servletContext.getRealPath(servletPath);
		int lastSlash = realPath.lastIndexOf(System.getProperty("file.separator"));
		if (lastSlash > -1) {
			String contextPath = realPath.substring(0, lastSlash + 1);
			return contextPath;
		}
		return "";
	}
}
