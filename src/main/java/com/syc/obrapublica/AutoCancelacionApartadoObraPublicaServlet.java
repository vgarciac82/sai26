package com.syc.obrapublica;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.syc.gestion.CorreosPendientesBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;

public class AutoCancelacionApartadoObraPublicaServlet extends HttpServlet implements GestionInterface, Runnable {

	private static final long	serialVersionUID	= 103570127154328876L;
	private long				interval			= -1L;
	private long				maxTiempoApartado	= -1;
	private volatile Thread		verificaTiempoLimiteApartado;
	private static Logger		log					= Logger.getLogger(AutoCancelacionApartadoObraPublicaServlet.class);
	private String				jniName;

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

		String strRunIntervalProcess = config.getInitParameter("runIntervalProcess");
		String strInterval = config.getInitParameter("sleepIntervalProcess");

		interval = Long.parseLong(strInterval);
		if (interval == -1L) {
			interval = 1000L * 60;
			log.info("Init Parameter \"sleepIntervalProcess\" nulo usando default \"" + interval + "\"");
		} else
			log.info("sleepIntervalProcess=" + interval);

		log.info("runIntervalProcess=" + "true".equalsIgnoreCase(strRunIntervalProcess));

		String strMaxTiempoApartado = config.getInitParameter("maxTiempoApartado");
		maxTiempoApartado = Long.parseLong(strMaxTiempoApartado);
		log.info("Init Parameter \"maxTiempoApartado\"  \"" + maxTiempoApartado + "\"");

		if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
			log.info("Iniciando Background Process");
			verificaTiempoLimiteApartado = new Thread(this);
			verificaTiempoLimiteApartado.setPriority(Thread.MIN_PRIORITY);
			verificaTiempoLimiteApartado.start();
		}
	}

	public void run() {
		AutoCancelacionApartadoBusinessLogic pabl = new AutoCancelacionApartadoBusinessLogic(jniName);
		
		while (true) {
			
			try {
				pabl.enviaAdvertenciaProximaCancelacion();
// IRD RO-0003 Se agrega parámetro MotivoCancela y lista folios vacíos
				pabl.enviaAdvertenciaResponsableProximaCancelacion("","");
				pabl.revisaTiempoLimite();
				Thread.sleep(interval);
			} catch (InterruptedException exc) {
				log.info("Wake up Background Process " + this.getClass().getName(), exc);
			} catch (Exception exc) {
				log.warn("Error en Background Process " + this.getClass().getName(), exc);
				break;
			} 
			try {
				String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
				CorreosPendientesBusinessLogic cpbl = new CorreosPendientesBusinessLogic();
				cpbl.setprefixPath(prefixPath);
				cpbl.reenviaAlertasPendientes();
			}catch (Exception e){
				log.warn(e,e);
			}
		}
		verificaTiempoLimiteApartado = null;

	}

	public void destroy() {
		verificaTiempoLimiteApartado = null;
	}

}
