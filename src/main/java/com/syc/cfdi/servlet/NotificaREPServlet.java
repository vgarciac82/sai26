package com.syc.cfdi.servlet;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.interfaces.CFDIBusinessLogic;

public class NotificaREPServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= 715260361207686246L;
	private static final Logger	log					= Logger.getLogger(NotificaREPServlet.class);
	private String				jniName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);

		if (session == null){
			resp.sendRedirect("../index.jsp");
			return;
		}
		
		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null){
			resp.sendRedirect("../index.jsp");
			return;
		}

		String mensaje = "Notificacion enviada exitosamente.";
		
		try {
			
			String unidadEjecutora = req.getParameter("cUnidadEjecutoraSel");
			String RFCBeneficiario = req.getParameter("cRFC");

			CFDIBusinessLogic cfdibl = new CFDIBusinessLogic(jniName);
			cfdibl.notificaREPFaltantes(u, unidadEjecutora, RFCBeneficiario);
			
		} catch (Exception e) {
			log.error(e,e);
			mensaje = "Ocurrio el siguiente error mientras se enviaban las notificaciones: " + e;
		}
		
		session.setAttribute("MSG", mensaje);
		resp.sendRedirect("Generador/listadoRecibosElectronicos.jsp");

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
	}
}
