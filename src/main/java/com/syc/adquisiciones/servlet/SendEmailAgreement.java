package com.syc.adquisiciones.servlet;

import java.io.File;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import com.syc.adquisiciones.businessLogic.ProcessAgreement;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "SendEmailAgreement", urlPatterns = { "/SendEmailAgreement" })
public class SendEmailAgreement extends HttpServlet implements Runnable {

    private static Logger log = LoggerFactory.getLogger(SendEmailAgreement.class);

    private static final long serialVersionUID = 1L;

    private volatile Thread verificaTiempoLimite;

    private static String jndiName = null;

    private static String urlReportes = null;

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
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jndiName);
            urlReportes = getServletContext().getRealPath("Reportes" + File.separator);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        String strRunIntervalProcess = config.getInitParameter("runIntervalProcess");
        log.info("Object: {}", "runIntervalProcess=" + "true".equalsIgnoreCase(strRunIntervalProcess));
        boolean isNotDebugger = false;
        //Valida si esta hanilitado
        if (isNotDebugger && "true".equalsIgnoreCase(strRunIntervalProcess)) {
            log.info("inicia proceso de envio de correos para los contratos que estan por finalizar la vigencia.");
            verificaTiempoLimite = new Thread(this);
            verificaTiempoLimite.setPriority(Thread.MIN_PRIORITY);
            //inicia el hilo
            verificaTiempoLimite.start();
        }
    }

    public void destroy() {
        super.destroy();
    }

    @Override
    public void run() {
        ProcessAgreement process = new ProcessAgreement();
        try {
            //process.sendEmailAgreement(jndiName);
            //process.sendEmailPAAASPresupuesto( jndiName,urlReportes );
            //Manda a dormir el hilo
            //60000 es igual a un minuto, 3,600,000 igual a una hora,  86,400,000.00  esto es igual a 24 horas
            Thread.sleep(3600000);
        } catch (Exception exc) {
            log.warn("Error en Process:  " + this.getClass().getName(), exc);
        } finally {
            verificaTiempoLimite = null;
            process = null;
        }
    }
}
