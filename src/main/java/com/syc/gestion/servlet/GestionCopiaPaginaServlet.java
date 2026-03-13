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
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.viewer.servlet.ViewerParametersInterface;

public class GestionCopiaPaginaServlet extends HttpServlet implements GestionInterface, ViewerParametersInterface {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionCopiaPaginaServlet.class);

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
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Llamada inválida, sin Caso seleccionado");
		}

		String srcNode = req.getParameter("srcNode");
		String srcIdx = req.getParameter("srcIdx");
		String selIdx = req.getParameter("selIdx");
		int index = Integer.parseInt(srcIdx);
		String strDocId = req.getParameter("docId");
		int docId = Integer.parseInt(strDocId);

		ITree tree = null;
		CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
		try {
			cbl.copiaPaginaADocumento(new Fortimax(srcNode), index, c.getTipoCaso().getGavetaAsociada(), docId);
			tree = cbl.getArbolCaso(c);
		} catch (GestionException e) {
			throw new ServletException(e);
		}

		session.setAttribute(ATT_TREE, tree);
		session.setAttribute(SEL_INDEX, selIdx);

		PrintWriter out = resp.getWriter();

		out.print("<script type=\"text/javascript\">");
		out.print("parent.parent.frames[\"doctree\"].location.reload(1);");
		out.print("parent.parent.frames[\"main\"].location.href=\"../imgmng/image-viewer.jsp?select=" + srcNode + "&"
				+ INDEX_KEY + "=" + srcIdx + "\";");
		out.println("</script>");

		out.flush();
		out.close();
	}
}
