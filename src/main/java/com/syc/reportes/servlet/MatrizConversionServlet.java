package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.MatrizConversionBusinessLogic;

public class MatrizConversionServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(MatrizConversionServlet.class);
	private static String				jniName				= "jdbc/gestion";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");
				
		String ruta = getServletContext().getRealPath("Reportes" + File.separator);	 //ruta del jasper			
		String formato = req.getParameter("formato"); 		
		String tipo = req.getParameter("reporte");
		
		
		MatrizConversionBusinessLogic rrs = new MatrizConversionBusinessLogic(jniName);				
		
		try {
								
			rrs.cosulta(req, resp, ruta, formato);	
		
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		// Put your code here
	}

	@Override
	public void init() throws ServletException {
		// Put your code here
	}
}
