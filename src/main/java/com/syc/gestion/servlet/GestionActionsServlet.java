package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;

public class GestionActionsServlet extends HttpServlet implements GestionInterface {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionActionsServlet.class);

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

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		boolean isFolder = "true".equals(req.getParameter("fldr"));
		boolean isDocImg = "true".equals(req.getParameter("img"));

		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("No hay sesion");
			resp.sendRedirect("../index.jsp");
			// <script language="javascript">self.top.location.href = "../index.jsp";</script>
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.warn("No hay Usuario en sesion");
			session.invalidate();
			resp.sendRedirect("../index.jsp");
			return;
		}

		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null) {
			log.error("Llamada invalida, sin Caso seleccionado");
			throw new ServletException("Llamada invalida, sin Caso seleccionado");
		}

		String idNode = req.getParameter("select");
		if (idNode == null) {
			log.warn("Llamada invalida");
			session.invalidate();
			resp.sendRedirect("../index.jsp");
		}

		String nombre = req.getParameter("nombre");
		if (nombre == null)
			nombre = isFolder ? "Nueva carpeta" : "Nuevo Documento";

		String desc = req.getParameter("descripcion");
		if (desc == null)
			desc = new String();
		
		Timestamp vigencia = null;
		if(req.getParameter("FECHA_VIGENCIA")!=null&&!"".equals(req.getParameter("FECHA_VIGENCIA"))){
			int anio=0;
			int mes=0;
			int dia=0;
			Calendar calendar = new GregorianCalendar();
			dia=new Integer(req.getParameter("FECHA_VIGENCIA").substring(0, 2)).intValue();
			mes=new Integer(req.getParameter("FECHA_VIGENCIA").substring(3, 5)).intValue()-1;
			anio=new Integer(req.getParameter("FECHA_VIGENCIA").substring(6)).intValue();
			calendar.set(anio,mes,dia,23,59,59);
			vigencia = new Timestamp(calendar.getTimeInMillis());
		}

		Fortimax fimx = new Fortimax(idNode);
		CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);

		try {
			if (isFolder) {
				cbl.creaCarpeta(u, fimx, nombre, desc);
			} else {
				cbl.creaDocumento(u, fimx, nombre, desc, isDocImg,vigencia);
			}

			ITree tree = cbl.getArbolCaso(c);

			tree.select(idNode);

			session.setAttribute(ATT_TREE, tree);
		} catch (GestionException exc) {
			log.error("Creando " + (isFolder ? "carpeta" : "documento " + (isDocImg ? "Fortimax" : "externo")), exc);
			throw new ServletException("Creando "
					+ (isFolder ? "carpeta" : "documento " + (isDocImg ? "Fortimax" : "externo")), exc);
		}

		PrintWriter out = resp.getWriter();

		out.println("<script type=\"text/javascript\">");
		out.println("parent.frames[\"main\"].location.href=\"resp-carpeta-docto.jsp?fldr=" + isFolder + "&img="
				+ isDocImg + "&name=" + nombre + "\"");
		out.println("parent.frames[\"doctree\"].location.href=\"document-tree.jsp\"");
		out.println("</script>");
		
		out.flush();
		out.close();
	}
}
