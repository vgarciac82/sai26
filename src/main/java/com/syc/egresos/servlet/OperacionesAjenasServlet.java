package com.syc.egresos.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.syc.contable.OperacionAjenaBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class OperacionesAjenasServlet extends HttpServlet implements GestionInterface {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3273304253562873899L;
	private static final Logger	log					= Logger.getLogger(OperacionesAjenasServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject respuesta = new JSONObject();
		try {
			// almacena mensajes de error
			String message = null;
			// validar sesion
			HttpSession session = req.getSession(false);

			if (session == null) {
				message = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
			}
			// validar usuario en sesion
			Usuario u = (Usuario) session.getAttribute(ATT_USER);
			if (u == null) {
				message = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
			}

			/*
			 * Respuesta cuando no tiene error
			 * {success:true,message:"",resultObj:{...}} Respuesta cuando hay
			 * error {success:false,message:"Motivo Error",resultObj:null}
			 */

			if (message != null) {
				throw new Exception(message);
			} else {
				String fInicio = req.getParameter("fInicio");
				String fFin = req.getParameter("fFin");
				int grupoRet = Integer.parseInt(req.getParameter("idGrupo"));
				Boolean esIP = Boolean.parseBoolean(req.getParameter("esIP"));
				Boolean esRGconOC =("S".equals(req.getParameter("esRGconOC")) ? true : false);
				int cc = Integer.parseInt(req.getParameter("centroCont"));
				Boolean ejer = Boolean.parseBoolean(req.getParameter("ejercido"));
				Boolean fFactura = Boolean.parseBoolean(req.getParameter("fechaFactura"));
				
				int ejercido = 0;
				if (ejer)
					ejercido = 1;
				else
					ejercido = 2;
				
				if (fFactura)
					ejercido = 3;

				OperacionAjenaBussinessLogic oabl = new OperacionAjenaBussinessLogic(ATT_CONEXION);
				String arr = oabl.buscaSolicitudesJSON(fInicio, fFin, grupoRet, esIP, esRGconOC, cc, ejercido).toString();
				JSONArray array = new JSONArray(arr);
				respuesta.put("success", "true");
				respuesta.put("message", "");
				respuesta.put("resultObj", array);

				resp.setContentType("text/x-json; charset=ISO-8859-1");
			}
		} catch (Exception e) {
			log.error(e, e);
			try {
				respuesta.put("success", "false");
				respuesta.put("message", e.toString());
				respuesta.put("resultObj", "");
			} catch (Exception e2) {
				log.error("No se pudo generar la respuesta: " + e2, e2);
				throw new ServletException(e2);
			}
		}

		ServletOutputStream out = resp.getOutputStream();
		out.println(respuesta.toString());
		out.flush();
		out.close();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

}
