package com.syc.alertas;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AlertasCasosVencidos", urlPatterns = {})
public class AlertaServlet extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(AlertaServlet.class);

    private String jniName = null;

    private long interval = -1L;

    private volatile Thread verificaTiempoLimiteAlerta;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        String strRunIntervalProcess = config.getInitParameter("runIntervalProcess");
        String strInterval = config.getInitParameter("sleepIntervalProcess");
        interval = Long.parseLong(strInterval);
        if (interval == -1L) {
            interval = 1000L * 60;
            log.info("Object: {}", "Init Parameter \"sleepIntervalProcess\" nulo usando default \"" + interval + "\"");
        } else
            log.info("Object: {}", "sleepIntervalProcess=" + interval);
        log.info("Object: {}", "runIntervalProcess=" + "true".equalsIgnoreCase(strRunIntervalProcess));
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
            log.error(e.getMessage(), e);
        }
    }
}
