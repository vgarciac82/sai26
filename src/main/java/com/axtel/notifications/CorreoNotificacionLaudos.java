package com.axtel.notifications;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.pasivoscontingentes.ProcesaNotificacionBusinessLogic;
import com.syc.reportes.ReporteIngresosBusinessLogic;

public class CorreoNotificacionLaudos extends HttpServlet implements GestionInterface {
	private static final Logger	log						= Logger.getLogger(CorreoNotificacionLaudos.class);
		
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("Su session a caducado");
	
		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");
	   
		String msg = "";
	
		ProcesaNotificacionBusinessLogic process = new ProcesaNotificacionBusinessLogic();
				
		try {			
			
			/*Proceso automatico que se ejecta de lunes a viernes a las 10 am
			 */
			process.enviaCorreoLaudos();
			
		} catch (Exception e) {
			log.error(e, e);
			msg = e.toString();
		}
		
		session.setAttribute("msg", msg);
	}

}
