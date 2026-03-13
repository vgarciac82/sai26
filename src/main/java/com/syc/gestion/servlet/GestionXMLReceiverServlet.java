package com.syc.gestion.servlet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;

public class GestionXMLReceiverServlet extends HttpServlet implements GestionInterface {

	public static final long serialVersionUID = 1l;

	private static Logger log = Logger.getLogger(GestionXMLReceiverServlet.class.getName());

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
				log.info("dataSourceRefName: Usando \"" + jniName + "\", para conexion a base de datos");
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("No hay sesión");
			throw new ServletException("No hay sesión");
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.warn("No hay Usuario en la sesión");
			throw new ServletException("No hay Usuario en la sesión");
		}

		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null) {
			log.warn("No hay Caso en la sesión");
			throw new ServletException("No hay Caso en la sesión");
		}

		CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
		
		
		PrintWriter out = resp.getWriter();
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><resp-gestion>");

		resp.setContentType("text/xml");

		try {
			c = cbl.getCaso( c.getIdCaso());
			Map data = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), false);

			List errList = cbl.validaCasoDatos(c, data);
			if (!errList.isEmpty()) {

				sb.append("<status>error</status><errors>");
				sb.append("<error>En el Tipo de Caso (" + c.getIdTC() + ")</error>");

				for (Iterator iter = errList.iterator(); iter.hasNext();) {
					String msg = (String) iter.next();
					sb.append("<error>" + msg + "</error>");
				}

				sb.append("</errors></resp-gestion>");

				out.write(sb.toString());

				return;
			}

			c = cbl.actualizaCasoDato(c, data);

			if (c.getIdGabinete() == -1)
				c.setIdGabinete(cbl.creaExpediente(u.getLogin(), c));

			cbl.recibeDocumentoGestion(c, new DataInputStream(req.getInputStream()));
			ITree tree = cbl.getArbolCaso(c);

			session.setAttribute(ATT_CASE, c);
			session.setAttribute("tree.model", tree);

			sb.append("<status>ok</status></resp-gestion>");

			out.write(sb.toString());
		} catch (GestionException exc) {
			exc.printStackTrace(System.out);
			resp.setContentType("text/xml");

			sb.append("<status>error</status><errors>");
			sb.append("<error>" + exc.getMessage() + "</error>");
			sb.append("<error>" + exc.getCause().getMessage() + "</error>");
			sb.append("</errors></resp-gestion>");

			out.write(sb.toString());

			//log.error("No se logro recibir el archivo gestion.xml", exc);
		} 
		catch(Exception exc){
			exc.printStackTrace(System.out);
			resp.setContentType("text/xml");
			//FIXME quitar cuando se resuelva el problema de la cancelacion
//			sb.append("<status>error</status><errors>");
//			sb.append("<error>" + exc.getMessage() + "</error>");
//			sb.append("<error>" + exc.getCause().getMessage() + "</error>");
//			sb.append("</errors></resp-gestion>");
//
//			out.write(sb.toString());
		}
		finally {
			out.flush();
			out.close();
		}
	}
}
