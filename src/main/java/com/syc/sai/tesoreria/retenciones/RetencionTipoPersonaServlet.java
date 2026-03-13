package com.syc.sai.tesoreria.retenciones;

import java.io.IOException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.tesoreria.retenciones.core.Retencion;

public class RetencionTipoPersonaServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= 6032637620845214068L;
	static Logger				log					= Logger.getLogger(RetencionTipoPersonaServlet.class);
	private String				jndiName;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session == null) {
			log.info("Intento de acceso sin session");
			// TODO enviar error al cliente
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);

		if (u == null) {
			log.info("Intento de acceso sin session");
			// TODO enviar error al cliente
			return;
		}

		String cRFC = request.getParameter("cIDRFC");
		String partidas = request.getParameter("partidasSeleccionadas");
		String cc = request.getParameter("cContable");
		String tipoPago = request.getParameter("tipo");
		String folioPago = request.getParameter("folio");
		List<Retencion> retenciones = retencionesObligatorias(cRFC, partidas, cc, tipoPago, folioPago);

		Retenciones resultado = new Retenciones(retenciones, true, "");
		response.setContentType("application/json");
		ServletOutputStream out = response.getOutputStream();

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, resultado);
		out.flush();
		out.close();
	}

	public List<Retencion> retencionesObligatorias(String rfc, String partidasSeleccionadas, String cc, String tipoPago, String folioPago) throws ServletException {
		try {
			RetencionBusinessLogic rtbl = new RetencionBusinessLogic(jndiName);

			String tipoPersona = rtbl.obtieneTipoPersona(rfc);
			List<Retencion> retenciones = rtbl.obtieneRetenciones(tipoPersona, partidasSeleccionadas, cc, tipoPago, folioPago);

			return retenciones;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			InitialContext ic = new InitialContext();
			jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
			if (jndiName == null) {
				jndiName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
			} else
				log.info("dataSourceRefName=" + jndiName);
		} catch (NamingException exc) {
			jndiName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
		}
	}
}