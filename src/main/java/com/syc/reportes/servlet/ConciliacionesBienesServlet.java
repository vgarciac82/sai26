package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ConciliacionesBienesBusinessLogic;

public class ConciliacionesBienesServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(ConciliacionesBienesServlet.class);
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;	
	//private static Boolean 				tipo_plantilla 		= false;
	
//	@SuppressWarnings("unused")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");
		
		ConciliacionesBienesBusinessLogic rrs = new ConciliacionesBienesBusinessLogic(jniName);
		try {
			
			rrs.conciliacion(req, resp, plantillas);
			
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}
					
	}

	@Override
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
		
		super.init(config);
		synchronized (this) {
			if (plantillas == null){
				plantillas = new HashMap<String, String>();
				plantillas.put("CONCILIABM", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliacionBienesMuebles.xls"));
				plantillas.put("CONCILIABC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliacionBienesConsumibles.xls"));
				plantillas.put("CONCILIABI", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ConciliacionBienesInmuebles.xls"));
			}			
			
		}

	}
}
