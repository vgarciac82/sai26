package com.syc.gestion.documental;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionInterface;

public class GetFolioCasoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass());
	private String jniName = null;

	public void init() throws ServletException {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = GestionInterface.ATT_CONEXION;
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException {
		
		log.info("[GetFolioCasoServlet]");
	    String sid = req.getParameter("id");

	    try {
		    int id = Integer.parseInt(sid);
	
		    CasoBusinessLogic cb = new CasoBusinessLogic(jniName);
		    Caso c = cb.getCaso(id);

			log.info("[GetFolioCasoServlet] folio="+c.getFolio());

			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");
			res.getWriter().write("<folio>" + c.getFolio() + "</folio>");
		}
		catch (GestionException e) {
			log.warn("Error al leer folio para id="+sid, e);
		}	    
	}
}
