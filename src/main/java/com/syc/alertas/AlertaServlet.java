package com.syc.alertas;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.syc.gestion.servlet.GestionInterface;

public class AlertaServlet extends HttpServlet implements GestionInterface, Runnable {
	private static final long	serialVersionUID	= 1L;
	private static Logger		log					= Logger.getLogger(AlertaServlet.class);
	private String				jniName				= null;
	private long				interval			= -1L;
	private volatile Thread		verificaTiempoLimiteAlerta;

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

		if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
			log.info("Iniciando Background Process");
			verificaTiempoLimiteAlerta = new Thread(this);
			verificaTiempoLimiteAlerta.setPriority(Thread.MIN_PRIORITY);
			verificaTiempoLimiteAlerta.start();
		}

	}

	public void run() {
		AlertaBusinessLogic abl = new AlertaBusinessLogic(jniName);

		try {
			while (true && abl.realizarProceso()) {

				try {
					log.info("Inicia Proceso de Casos Expirados...");
					abl.buscaCasosExpirados();
					Thread.sleep(interval);
				} catch (InterruptedException exc) {
					log.info("Wake up Background Process " + this.getClass().getName(), exc);
				} catch (Exception exc) {
					log.warn("Error en Background Process " + this.getClass().getName(), exc);
					break;
				}
			}
			verificaTiempoLimiteAlerta = null;
		} catch (Exception e) {
			log.error(e, e);
		}
	}

}
