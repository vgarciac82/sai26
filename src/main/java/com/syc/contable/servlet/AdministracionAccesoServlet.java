package com.syc.contable.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.contable.AdministracionAccesoBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;

public class AdministracionAccesoServlet extends HttpServlet {

	private static final long	serialVersionUID	= -2132300865011700319L;

	private static final Logger	log					= Logger.getLogger(AdministracionAccesoServlet.class);

	public AdministracionAccesoServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null)
			response.sendRedirect("../index.jsp");

		String listadoUsuario = request.getParameter("listadoUsuario");
		String accion = request.getParameter("accion");

		String opcion = "";
		String[] lu = request.getParameterValues("checks");

		AdministracionAccesoBusinessLogic aaBL = new AdministracionAccesoBusinessLogic(GestionInterface.ATT_CONEXION);

		List<String> l;

		String mensaje = "";

		if ("LISTA_OPCIONES".equals(accion)) {
			try {
				l = aaBL.getselecChecks(listadoUsuario);
				String[] r = new String[l.size()];
				l.toArray(r);
				ResponseSender.sendResult(response, r, "data");
				return;
			} catch (Exception e) {
				log.error("Error cargando opciones delegadas al usuario: " + e, e);
				ResponseSender.sendError(response, "Error cargando opciones delegadas al usuario: " + e);
			}
		} else if ("INSERTA_ACTUALIZA".equals(accion)) {
			try {
				if (lu != null) {
					aaBL.desasignar(listadoUsuario);
					for (int i = 0; i < lu.length; i++) {
						opcion = lu[i];
						aaBL.asignar(listadoUsuario, opcion);
					}
				} else
					aaBL.desasignar(listadoUsuario);
				
				session.setAttribute("mensaje", "Operacion terminada con exito");
				response.sendRedirect("../plantillasCasos/administracionAccesos.jsp");
				return;
			} catch (Exception e) {
				log.error("Error asignando opciones delegadas al usuario: " + e, e);
				mensaje = "Error asignando opciones delegadas al usuario: " + e;
				if (session != null)
					session.setAttribute("mensaje", mensaje);
				response.sendRedirect("../plantillasCasos/administracionAccesos.jsp");
			}
		} else if ("listarOpcion".equals(accion)) {
			try {
				l = aaBL.getOpcionDelegable();
				String[] r = new String[l.size()];
				l.toArray(r);
				ResponseSender.sendResult(response, r, "data");
				return;
			} catch (Exception e) {
				log.error("Error listando opciones delegables: " + e, e);
				ResponseSender.sendError(response, "Error listando opciones delegables: " + e);
			}
		}

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
