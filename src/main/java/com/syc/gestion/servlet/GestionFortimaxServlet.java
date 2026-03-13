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

import org.apache.log4j.Logger;

import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;

public class GestionFortimaxServlet extends HttpServlet implements GestionInterface {

	public static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionFortimaxServlet.class);

	private String frtimxCtx = null;
	private String frtimxUsr = null;
	private String frtimxPwd = null;

	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		try {
			InitialContext ic = new InitialContext();
			frtimxCtx = (String) ic.lookup("java:comp/env/FortImaxUrl");

			if (frtimxCtx == null) {
				frtimxCtx = "/fortimax";
				log.info("Environment Entry \"FortImaxUrl\" nula usando default \"" + frtimxCtx + "\"");
			} else
				log.info("FortImaxUrl: Usando \"" + frtimxCtx + "\", como prefijo de URL a FortImax");
		} catch (NamingException exc) {
			frtimxCtx = "/fortimax";
			log.info("Environment Entry \"FortImaxUrl\" no definida usando default \"" + frtimxCtx + "\"");
		}

		frtimxCtx = frtimxCtx.trim();
		frtimxCtx += frtimxCtx.lastIndexOf('/') != frtimxCtx.length() ? "/" : "";

		try {
			InitialContext ic = new InitialContext();
			frtimxUsr = (String) ic.lookup("java:comp/env/FortImaxUser");

			if (frtimxUsr == null) {
				frtimxUsr = "gestion";
				log.info("Environment Entry \"FortImaxUser\" nula usando default \"" + frtimxUsr + "\"");
			} else
				log.info("FortImaxUser: Usando \"" + frtimxUsr + "\", como usuario de FortImax");
		} catch (NamingException exc) {
			frtimxUsr = "gestion";
			log.info("Environment Entry \"FortImaxUser\" no definida usando default \"" + frtimxUsr + "\"");
		}

		frtimxUsr = frtimxUsr.trim();

		try {
			InitialContext ic = new InitialContext();
			frtimxPwd = (String) ic.lookup("java:comp/env/FortImaxPwd");

			if (frtimxPwd == null) {
				frtimxPwd = "GESTION";
				log.info("Environment Entry \"FortImaxPwd\" nula usando default");
			} else
				log.info("FortImaxPwd: Usando no-default password como autenticacion con FortImax");
		} catch (NamingException exc) {
			frtimxUsr = "GESTION";
			log.info("Environment Entry \"FortImaxPwd\" no definida usando default");
		}

		frtimxPwd = frtimxPwd.trim();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("No hay sesion");
			resp.sendRedirect("../index.jsp");
			return;
		}

		Usuario userGestion = (Usuario) session.getAttribute(ATT_USER);
		if (userGestion == null) {
			log.warn("No hay Usuario en sesion");
			session.invalidate();
			resp.sendRedirect("../index.jsp");
			return;
		}

		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null) {
			log.error("No hay Caso en la sesion");
			throw new ServletException("No hay Caso en la sesion");
		}

		String data = c.getTipoCaso().getGavetaAsociada() + "|" + c.getIdGabinete() + "|" + frtimxUsr + "|" + frtimxPwd;
		// SecretKey key = Util.genSecretKey();

		String redirectURL = "frtimxgest?data=" + data;
		// String redirectURL = "frtimxgest?id=" + Util.secretKeyToString(key) +
		// "data=" + Util.encrypt(key, data);

		resp.sendRedirect(frtimxCtx + redirectURL);
	}
}
