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

import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.FormatoAltaProveedorBusinessLogic;

public class FormatoAltaProveedorServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(FormatoAltaProveedorServlet.class);
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null || u == null)
			throw new ServletException("Su session a caducado");

		FormatoAltaProveedorBusinessLogic rpt = new FormatoAltaProveedorBusinessLogic(jniName);
		try {
			
			String folio = c.getFolio();
			Boolean altaRapida = Boolean.parseBoolean(  StringUtils.trimToEmpty(  req.getParameter( "altaRapida" ) ) );
			
			rpt.generaReporteFormatoAltaProveedor(req, resp, plantillas,folio, altaRapida);
			
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}

	
	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		doGet( req, resp );
	}


	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		synchronized (this) {
			if (plantillas == null) {
				plantillas = new HashMap<String, String>();
				plantillas.put("FmtoAltaProveedor", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_FormatoAltaProveedor.xls"));
			}
		}

	}

}
