package com.syc.gestion.servlet;

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

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;

public class CasoExpedienteServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= 2627255063057086011L;

	private static final Logger	log					= Logger.getLogger(CasoExpedienteServlet.class);

	private static String		jniName				= "";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		String msgRetorno = "";
		Usuario u = null;
		Caso c = null;

		if (session == null) {
			msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
		} else {

			u = (Usuario) session.getAttribute(ATT_USER);
			c = (Caso) session.getAttribute(ATT_CASE);

			if (c == null || u == null) {
				msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
			}

			if ("".equals(msgRetorno)) {
				CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);

				try {
					if (c.getIdGabinete() == -1) {
						
						AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);

						c.getCasoDato("FOLIO").setValor(c.getFolio());
						c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
						c.getCasoDato("EJERCICIO_FISCAL").setValor(adbl.obtenEjercicioFiscal());
						c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());//u.getNombre()
						c.setIdGabinete(cbl.creaExpediente(u.getLogin(), c));
						
						ITree tree = cbl.getArbolCaso(c);

						session.setAttribute(ATT_CASE, c);
						session.setAttribute("tree.model", tree);
					}
				} catch (Exception e) {
					log.error(e, e);
					msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
				}
			}
		}

		if (StringUtils.isEmpty(msgRetorno)) {
			ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(c.getIdGabinete()));
		} else {
			ResponseSender.sendClientSimpleMessage(resp, false, msgRetorno);
		}

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
