package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.reportes.ReporteAC01BusinessLogic;

@SuppressWarnings("unused")
public class ReporteAC01Servlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(ReporteAC01Servlet.class);
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
		String tipoIngreso = req.getParameter("TIPO_INGRESO");
		
		ReporteAC01BusinessLogic rrs = new ReporteAC01BusinessLogic(jniName);
		String fileName = null;
		try{			
			fileName = rrs.generaReporte(req, resp, plantillas);
			Util.doDownload(resp, fileName, "ReporteAC01.xls", null);
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}finally{
			if( fileName != null && (new File(fileName) ).exists() ){
				if( !(new File(fileName) ).delete() )
					(new File(fileName) ).deleteOnExit();
			}
		}
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		synchronized (this) {
			if (plantillas == null){
				plantillas = new HashMap<String, String>();
				plantillas.put("FORMATOAC01", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_FormatoAC01.xls"));
				
			}
		}
	}	
}
