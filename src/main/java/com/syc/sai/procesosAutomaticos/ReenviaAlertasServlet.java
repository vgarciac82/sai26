package com.syc.sai.procesosAutomaticos;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.syc.gestion.CorreosPendientesBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;

public class ReenviaAlertasServlet extends HttpServlet implements GestionInterface, Runnable {

	/**
	 * 
	 */
	private static final long	serialVersionUID		= 1465222012852864177L;
	private static final Logger	log						= Logger.getLogger(ReenviaAlertasServlet.class);
	private String				jniName;
	private static boolean		activarReenvioAlertas	= false;
	private static long			tiempoEsperaAlertas		= -1;
	private static Thread		MAIL_SENDER_DEMON;

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

		ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
		ReenviaAlertasServlet.activarReenvioAlertas = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_REENVIO_ALERTAS"));
		ReenviaAlertasServlet.tiempoEsperaAlertas = Long.parseLong(cabl.getSystemSetting("TIEMPO_REENVIO_ALERTAS"));

		log.info("Se leyeron los siguientes parametros:[ACTIVA_REENVIO_ALERTAS]=[" + activarReenvioAlertas + "];[TIEMPO_REENVIO_ALERTAS][" + tiempoEsperaAlertas + "]");
		log.info("Creando demonio");
		MAIL_SENDER_DEMON = new Thread(this);
		MAIL_SENDER_DEMON.setPriority(Thread.MIN_PRIORITY);
		MAIL_SENDER_DEMON.start();

	}

	public void run() {
		log.info("Iniciando hilo de reenvio de alertas pendientes ");
		ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
		while (true) {

			if (ReenviaAlertasServlet.activarReenvioAlertas) {
				if (ReenviaAlertasServlet.tiempoEsperaAlertas > 0)
					try {
						log.info("Iniciando el reenvio de alertas pendientes ");
						long start = System.currentTimeMillis();

						CorreosPendientesBusinessLogic cpbl = new CorreosPendientesBusinessLogic(jniName);
						int reenviados = cpbl.reenviaAlertasPendientes();

						long stop = System.currentTimeMillis();
						log.info("Termino el reenvio de " + reenviados + " alertas en " + ((stop - start) / 1000));

						Thread.sleep(ReenviaAlertasServlet.tiempoEsperaAlertas);
					} catch (Exception e) {
						log.error(e, e);
					}

				ReenviaAlertasServlet.activarReenvioAlertas = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_REENVIO_ALERTAS"));
				ReenviaAlertasServlet.tiempoEsperaAlertas = Long.parseLong(cabl.getSystemSetting("TIEMPO_REENVIO_ALERTAS"));
			}else{
				break;
			}
		}
	}

}
