package com.syc.contable.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syc.contable.RetencionBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;

public class RetencionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8140857888553424437L;
	//private static final Logger	log					= Logger.getLogger(RetencionServlet.class);
	
	public RetencionServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		RetencionBusinessLogic retencion = new RetencionBusinessLogic(GestionInterface.ATT_CONEXION);
		
		String accion = request.getParameter("accion");
		String fApl = request.getParameter("fecha");
		String mensaje ="";
		if (accion != null && !"".equals(accion)) {
			 if("1".equals(accion)){ // Aplica
				HttpSession session = request.getSession(false);
				//PrintWriter out = response.getWriter(); 
				if (session == null) {
					response.sendRedirect("../index.jsp");
					return;
				}
				
				Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);			
			    
			    try{
			    	mensaje = retencion.aplicaRetencion(c,fApl);
			    }
			    catch (Exception ex) {
			    	
			    	ResponseSender.sendClientSimpleMessage( response, false, "No se aplico la solicitud. " + mensaje );
					
				}
			}else if("2".equals(accion)){ //CANCELA 
				HttpSession session = request.getSession(false);
				PrintWriter out = response.getWriter(); 
				if (session == null) {
					response.sendRedirect("../index.jsp");
					return;
				}
			
				Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);			
			 
			    try{
			    	out.println(new String(retencion.cancelarAppContableNuevo(c,fApl).getBytes("UTF-8"),"ISO-8859-1"));
			    }
			    catch (Exception ex) {
					
					ResponseSender.sendClientSimpleMessage( response, false, "No se cancelo la solicitud, avise al administrador" );
				}
			}
		}
		
	}

	public void init() throws ServletException {
	}

}
