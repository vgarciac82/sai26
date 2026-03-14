package com.syc.obrapublica;

import java.io.File;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import com.syc.gestion.CorreosPendientesBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AutoCancelacionApartadoOP", urlPatterns = {})
public class AutoCancelacionApartadoObraPublicaServlet extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 103570127154328876L;

    private long interval = -1L;

    private long maxTiempoApartado = -1;

    private volatile Thread verificaTiempoLimiteApartado;

    private static Logger log = LoggerFactory.getLogger(AutoCancelacionApartadoObraPublicaServlet.class);

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
        AutoCancelacionApartadoBusinessLogic pabl = new AutoCancelacionApartadoBusinessLogic(jniName);
        while (true) {
            try {
                pabl.enviaAdvertenciaProximaCancelacion();
                // IRD RO-0003 Se agrega parámetro MotivoCancela y lista folios vacíos
                pabl.enviaAdvertenciaResponsableProximaCancelacion("", "");
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
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        verificaTiempoLimiteApartado = null;
    }

    public void destroy() {
        verificaTiempoLimiteApartado = null;
    }
}
