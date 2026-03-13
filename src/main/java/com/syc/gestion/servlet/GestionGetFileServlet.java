package com.syc.gestion.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.gestion.core.NodeInformation;
import com.syc.gestion.core.Usuario;

public class GestionGetFileServlet extends HttpServlet implements GestionInterface {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionGetFileServlet.class);

	private String jniName = null;

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
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("No hay sesion");
			setMainPage(resp);
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.warn("No hay Usuario en sesion");
			session.invalidate();
			setMainPage(resp);
			return;
		}

		String select = req.getParameter("select");
		if (select == null) {
			log.warn("No se recibio parametro 'select'");
			throw new ServletException("No se recibio parametro 'select'");
		}

		ITree tree = (ITree) session.getAttribute(ATT_TREE);
		if (tree == null) {
			log.error("No se encontro arbol en la session");
			throw new ServletException("No se encontro arbol en la session");
		}

		ITreeNode node = tree.findNode(select);
		if (node == null) {
			log.error("No se encontro nodo (" + select + ")");
			throw new ServletException("No se encontro nodo (" + select + ")");
		}

		NodeInformation nodeInf = (NodeInformation) node.getObject();
		if (nodeInf == null) {
			log.error("No hay informacion en el nodo");
			throw new ServletException("No hay informacion en el nodo");
		}

		if (nodeInf.getFisicalName() != null)
			doDownload(resp, nodeInf.getFisicalFilenamePath(), nodeInf.getNameWithExtension());
		else {
			resp.sendRedirect("caso/upload.jsp?select=" + select);
		}
	}

	private void setMainPage(HttpServletResponse resp) throws IOException {

		PrintWriter out = resp.getWriter();

		out.println("<script language=\"javascript\">self.top.location.href=\"../index.jsp\";</script>");
		out.flush();
		out.close();
	}

	private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {

		int length = 0;
		File f = new File(filename);
		ServletOutputStream out = resp.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(original_filename);

		resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		resp.setContentLength((int) f.length());

		//resp.addHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\";");
		//resp.addHeader("Content-Disposition", "attachement; filename=\"" + original_filename + "\";");
		resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");

		byte[] bbuf = new byte[5 * 1024]; // 5K buffer
		DataInputStream in = new DataInputStream(new FileInputStream(f));

		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			out.write(bbuf, 0, length);
		}

		in.close();
		out.flush();
		out.close();
	}
}
