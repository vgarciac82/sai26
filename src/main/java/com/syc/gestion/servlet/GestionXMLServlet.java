package com.syc.gestion.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;

public class GestionXMLServlet extends HttpServlet implements GestionInterface {

	public static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionXMLServlet.class.getName());

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
			log.error("No hay sesion");
			throw new ServletException("No hay sesión");
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.error("No hay Usuario en la sesion");
			throw new ServletException("No hay Usuario en la sesión");
		}

		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null) {
			log.error("No hay Caso en la sesion");
			throw new ServletException("No hay Caso en la sesión");
		}

		String filename;
		CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);

		try {
			filename = cbl.getFilenamePath(c);
		} catch (GestionException exc) {
			throw new ServletException(exc);
		}

		if (filename == null)
			filename = "gestion.tif";

		File f = new File(filename);

		if (f.exists() && ( c.getIdTC() != 42 && c.getIdTC() != 11 ) ) {

			resp.setContentType("text/xml");
			resp.setContentLength((int) f.length());

			ServletOutputStream op = resp.getOutputStream();
			DataInputStream in = new DataInputStream(new FileInputStream(f));

			try {

				int length = 0;
				byte[] bbuf = new byte[16 * 1024]; // 16K buffer

				while ((in != null) && ((length = in.read(bbuf)) != -1)) {
					op.write(bbuf, 0, length);
				}
			} finally {
				if (in != null)
					in.close();

				if (op != null) {
					op.flush();
					op.close();
				}
				
				in = null;
				op = null;
			}

			if (log.isDebugEnabled())
				log.debug("Enviando archivo gestion.xml de fortimax");
		} else {

			PrintWriter out = resp.getWriter();

			try {
				out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.println();
				out.println("<gestion>");
				out.println("<plantilla>");
				out.println("</plantilla>");
				out.println("</gestion>");
			} finally {
				out.flush();
				out.close();
			}

			if (log.isDebugEnabled())
				log.debug("Enviando archivo gestion.xml vacio");
		}
	}
}
