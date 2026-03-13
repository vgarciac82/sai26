package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.GeneraCedulasBusinessLogic;


public class GeneraCedulasServlet extends HttpServlet implements GestionInterface {

	
	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(GeneraCedulasServlet.class);
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;	

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		ServletOutputStream out = res.getOutputStream();
		HttpSession session = req.getSession(false);
		
		try {
			
			if (session == null) {
				log.error("Acceso sin sesion");
				throw new ServletException("Acceso sin sesion. Por favor reingrese al sistema.");
			}

			Usuario u = (Usuario) session.getAttribute(ATT_USER);
			if (u == null) {
				log.error("Acceso sin usuario");
				throw new ServletException("Acceso sin usuario. Por favor reingrese al sistema.");
			}
			
						
			GeneraCedulasBusinessLogic csw = new GeneraCedulasBusinessLogic(jniName);
			
			try{
				csw.generacedulasSIIWEB(req,res,plantillas);	
				
			}catch(Exception e){
				log.error(e,e);
				throw new ServletException(e);
			}	
			
		} catch (Exception e) {
			
			out.println("Ocurrio el siguiente error al generar el reporte:<br/>");
			out.println(e.toString() + "<br/>");
			out.println("Por favor notifique al administrador.<br/>");
			out.flush();
			out.close();
		}
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		synchronized (this) {
			if (plantillas == null){
				plantillas = new HashMap<String, String>();
				plantillas.put("CEDE06", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaE06.xls"));
				plantillas.put("CEDE02", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_CedulaE02.xls"));
			
			}				
		}
	}
}