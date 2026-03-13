package com.syc.adquisiciones.servlet;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;

import com.syc.adquisiciones.businessLogic.ProcessAgreement;

public class SendEmailAgreement extends HttpServlet implements Runnable{
	private static Logger log = Logger.getLogger(SendEmailAgreement.class);
	private static final long serialVersionUID = 1L;
	private volatile Thread		verificaTiempoLimite;
	private static String jndiName = null;
	private static String urlReportes=null;
    public SendEmailAgreement() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			InitialContext ic = new InitialContext();
			jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
			if (jndiName == null) {
				jndiName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \""+ jndiName + "\"");
			} else
				log.info("dataSourceRefName=" + jndiName);
			
			urlReportes=getServletContext().getRealPath("Reportes" + File.separator);
			
		} catch (NamingException exc) {
			jndiName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \""+ jndiName + "\"");
		}
		String strRunIntervalProcess = config.getInitParameter("runIntervalProcess");
		log.info("runIntervalProcess=" + "true".equalsIgnoreCase(strRunIntervalProcess));
		boolean isNotDebugger=false;
		//Valida si esta hanilitado
		if (isNotDebugger && "true".equalsIgnoreCase(strRunIntervalProcess)) {
			log.info("inicia proceso de envio de correos para los contratos que estan por finalizar la vigencia.");
			verificaTiempoLimite = new Thread(this);
			verificaTiempoLimite.setPriority(Thread.MIN_PRIORITY);
			verificaTiempoLimite.start();//inicia el hilo
		}
	}
	public void destroy() {
		super.destroy();
	}
	@Override
	public void run() {
		ProcessAgreement process=new ProcessAgreement();
		try {
			//process.sendEmailAgreement(jndiName);
			//process.sendEmailPAAASPresupuesto( jndiName,urlReportes );
			//Manda a dormir el hilo
			Thread.sleep(3600000);//60000 es igual a un minuto, 3,600,000 igual a una hora,  86,400,000.00  esto es igual a 24 horas
		} catch (Exception exc) {
			log.warn("Error en Process:  " + this.getClass().getName(), exc);
		}finally {
			verificaTiempoLimite=null;
			process=null;
		}
		
	}

}
