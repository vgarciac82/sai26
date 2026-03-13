package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.viewer.servlet.ViewerParametersInterface;

public class GestionDelPageDocumentServlet extends HttpServlet implements GestionInterface, ViewerParametersInterface {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionDelPageDocumentServlet.class);

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

		String idxVal = null;
		idxVal = req.getParameter(INDEX_KEY);
		if (idxVal == null)
			return; // Nada que hacer

		int index = Integer.parseInt(idxVal);

		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect("index.jsp");
			return;
		}

		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null) {
			resp.sendRedirect("index.jsp");
			return;
		}

		String selectId = req.getParameter("select");
		if (selectId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
			return;
		}

		String urlPrefix = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
				+ req.getContextPath();

		CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
		int maxIdx = 0;

		try {
			maxIdx = cbl.borraPaginaDocumento(selectId, index);
			ITree tree = cbl.getArbolCaso(c);
			session.setAttribute(ATT_TREE, tree);
		} catch (GestionException exc) {
			log.error("Borrando pagina", exc);
			throw new ServletException(exc);
		}

		PrintWriter out = resp.getWriter();
		out.println("<script type=\"text/javascript\">");
		out.println("parent.frames[\"viewerFrame\"].location.href=\"" + urlPrefix
				+ "/imgmng/VisualizadorDeImagen.jsp?select=" + selectId + "&" + INDEX_KEY + "="
				+ ((index - 1) < 0 ? 0 : index - 1) + "\";");
		out.println("parent.frames[\"listFrame\"].location.href=\"" + urlPrefix + "/imgmng/ListaDeImagenes.jsp?select="
				+ selectId + "&" + INDEX_MAX + "=" + maxIdx + "\";");
		out.println("parent.parent.frames[\"doctree\"].location.href=\"" + urlPrefix
				+ "/caso/document-tree.jsp?select=" + selectId + "\"");
		out.println("</script>");
		out.flush();
		out.close();
	}
}
