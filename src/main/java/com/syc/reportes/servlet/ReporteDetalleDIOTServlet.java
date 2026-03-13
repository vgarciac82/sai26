package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReporteDetalleDIOTBusinessLogic;

@SuppressWarnings("unused")
public class ReporteDetalleDIOTServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(Reporte005AServlet.class);
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");
	   	
		String ruta = getServletContext().getRealPath("Reportes");
		
		ReporteDetalleDIOTBusinessLogic rrs = new ReporteDetalleDIOTBusinessLogic(jniName);
		
		try {			
				rrs.generaReporte(req, resp, plantillas);			
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		synchronized (this) {
			if (plantillas == null){
				plantillas = new HashMap<String, String>();

			plantillas.put("DetalleDIOT", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteDetalleDIOT.xlsx"));
			}									
		}

	}
}
