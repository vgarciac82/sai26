package com.syc.ejercido.pagado;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.MesContableBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;

public class ActualizaFechaAplicacionServlet extends HttpServlet implements GestionInterface {
	private static final long	serialVersionUID	= 8026132162784738805L;
	private static String		jniName;
	private static final Logger	log					= Logger.getLogger(ActualizaFechaAplicacionServlet.class);

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

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion ha terminado. Reingrese al sistema");
			return;
		}
		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion ha terminado. Reingrese al sistema");
			return;
		}

		String tipoPago = req.getParameter("documento");
		String folioPagoStr = req.getParameter("folio");
		int nFolioPago = -1;

		if (StringUtils.isEmpty(tipoPago) || StringUtils.isEmpty(folioPagoStr)) {
			ResponseSender.sendClientSimpleMessage(resp, false, String.format("No se recibieron los parametros completos [documento][%s][folio][%s]", tipoPago, folioPagoStr));
			return;
		}

		try {
			nFolioPago = Integer.parseInt(folioPagoStr,10);
		} catch (Exception e) {
			ResponseSender.sendClientSimpleMessage(resp, false, String.format("Valor no numerico para el [folio][%s]", folioPagoStr));
			return;
		}

		MesContableBusinessLogic mcbl = new MesContableBusinessLogic(jniName);

		try {
			int afectados = mcbl.cambiaFechaAplicacion(tipoPago, nFolioPago);
			ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(afectados));
		} catch (Exception e) {
			ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
		}
	}

}
