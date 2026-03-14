package com.syc.contable.servlet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.procesos.ProinproApartadoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AutoCancelacionPreprod", urlPatterns = {})
public class AutoCancelacionProInproServlet extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 103570127154328876L;

    private long interval = -1L;

    private long maxTiempoApartado = -1;

    private volatile Thread verificaTiempoLimiteApartado;

    private static Logger log = LoggerFactory.getLogger(AutoCancelacionProInproServlet.class);

    private String jniName;

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
        String strMaxTiempoApartado = config.getInitParameter("maxTiempoApartado");
        maxTiempoApartado = Long.parseLong(strMaxTiempoApartado);
        log.info("Object: {}", "Init Parameter \"maxTiempoApartado\"  \"" + maxTiempoApartado + "\"");
        if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
            log.info("Iniciando Background Process");
            verificaTiempoLimiteApartado = new Thread(this);
            verificaTiempoLimiteApartado.setPriority(Thread.MIN_PRIORITY);
            verificaTiempoLimiteApartado.start();
        }
    }

    public void run() {
        ProinproApartadoBusinessLogic pabl = new ProinproApartadoBusinessLogic(jniName);
        while (true) {
            try {
                Thread.sleep(interval);
                if (maxTiempoApartado > 0)
                    pabl.revisaTiempoLimite(maxTiempoApartado);
                else
                    log.warn("No se definio tiempo limite de apartado");
            } catch (InterruptedException exc) {
                log.info("Wake up Background Process " + this.getClass().getName(), exc);
            } catch (Exception exc) {
                log.warn("Error en Background Process " + this.getClass().getName(), exc);
                break;
            }
        }
        verificaTiempoLimiteApartado = null;
    }

    public void destroy() {
        verificaTiempoLimiteApartado = null;
    }
}
