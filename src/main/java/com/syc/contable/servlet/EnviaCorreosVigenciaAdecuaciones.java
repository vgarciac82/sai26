package com.syc.contable.servlet;

import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "EnviaCorreosVigenciaAdecuaciones", urlPatterns = { "/servlet/VigenciaAdecuaciones" })
public class EnviaCorreosVigenciaAdecuaciones extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 103570127154328876L;

    private long interval = -1L;

    private volatile Thread verificaTiempoLimiteApartado;

    Logger log = LoggerFactory.getLogger(EnviaCorreosVigenciaAdecuaciones.class);

    private String jniName;

    /**
     * Constructor of the object.
     */
    public EnviaCorreosVigenciaAdecuaciones() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init(ServletConfig config) throws ServletException {
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
            verificaTiempoLimiteApartado = new Thread(this);
            verificaTiempoLimiteApartado.setPriority(Thread.MIN_PRIORITY);
            verificaTiempoLimiteApartado.start();
        }
    }

    public void run() {
        AdecuacionBusinessLogic abl = new AdecuacionBusinessLogic(jniName);
        while (true) {
            try {
                //operacion, dias
                abl.mandaCorreoVigenciaAdecuacion(1, 3);
                //operacion, dias
                abl.mandaCorreoVigenciaAdecuacion(5, 3);
                abl.mandaCorreoCincoDias(5, 5);
                //operacion, dias
                abl.cancelaAdecuacionAutomatica(1, 4);
                //operacion, dias
                abl.cancelaAdecuacionAutomatica(5, 4);
                Thread.sleep(interval);
            } catch (SQLException e) {
                e.printStackTrace();
                break;
            } catch (InterruptedException exc) {
                log.info("Wake up Background Process " + this.getClass().getName(), exc);
                break;
            } catch (Exception exc) {
                log.warn("Error en Background Process " + this.getClass().getName(), exc);
                break;
            }
        }
    }
}
