package com.syc.js.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.json.JSONObject;

import com.syc.js.core.QueryBussinesLogic;
import com.syc.js.core.QueryException;

public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(QueryServlet.class);

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

		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String query = req.getParameter("stmnt");
		if (query != null)
			query = URLDecoder.decode(query, "UTF-8");

		boolean isXML = "xml".equals(req.getParameter("respType"));
		QueryBussinesLogic qm = new QueryBussinesLogic(jniName);
		Object obj = null;

		try {
			obj = qm.makeQuery(query, isXML);
		} catch (QueryException exc) {
			String msg = exc.getMessage();
			int idx = msg.indexOf(":") + 1;
			msg = msg.substring(idx);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
			return;
		}

		if (isXML) {
			resp.setContentType("text/xml");
			Document docXML = new Document((Element) obj);
			new XMLOutputter().output(docXML, resp.getWriter());
		} else {
			resp.setContentType("text/javascript");
			resp.getWriter().print((JSONObject) obj);
		}
	}
}
