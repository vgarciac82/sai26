package com.syc.contable.core;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "AutoEnviaCorreoCompromisos", urlPatterns = {})
public class AutoEnviaCorreoCompromisosServlet extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 103570127154328876L;

    private long interval = -1L;

    private long maxTiempoCompromiso = -1;

    private volatile Thread verificaTiempoLimiteCompromiso;

    private static Logger log = Logger.getLogger(AutoEnviaCorreoCompromisosServlet.class);

    private String jniName;

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
        String strMaxTiempoCompromiso = config.getInitParameter("maxTiempoCompromiso");
        maxTiempoCompromiso = Long.parseLong(strMaxTiempoCompromiso);
        log.info("Init Parameter \"maxTiempoCompromiso\"  \"" + maxTiempoCompromiso + "\"");
        if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
            log.info("Iniciando Background Process");
            verificaTiempoLimiteCompromiso = new Thread(this);
            verificaTiempoLimiteCompromiso.setPriority(Thread.MIN_PRIORITY);
            verificaTiempoLimiteCompromiso.start();
        }
    }

    public void run() {
        AutoEnviaCorreoCompromisosBusinessLogic aeccbl = new AutoEnviaCorreoCompromisosBusinessLogic(jniName);
        while (true) {
            try {
                aeccbl.enviaAlertasCompromisosPendientes();
                Thread.sleep(interval);
            } catch (InterruptedException exc) {
                log.info("Wake up Background Process " + this.getClass().getName(), exc);
            } catch (Exception exc) {
                log.warn("Error en Background Process " + this.getClass().getName(), exc);
                break;
            }
        }
        verificaTiempoLimiteCompromiso = null;
    }

    public void destroy() {
        verificaTiempoLimiteCompromiso = null;
    }
}
