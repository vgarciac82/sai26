package com.syc.gestion.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "GestionSetupServlet", urlPatterns = {})
public class GestionSetupServlet extends HttpServlet implements GestionInterface, Runnable {

    public static final long serialVersionUID = 1L;

    private String jniName = null;

    private long interval = -1L;

    private volatile Thread verifyLimitTimeCaso;

    private boolean extMail = false;

    private static Logger log = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        initLogger(config);
        String strRunIntervalProcess = config.getInitParameter("runIntervalProcess");
        String strInterval = config.getInitParameter("sleepIntervalProcess");
        //Ethiel, seagrega en el web.xml para saber si se mandan mails externos o mensages internos de gestion
        extMail = "true".equalsIgnoreCase(config.getInitParameter("externalMail"));
        interval = Long.parseLong(strInterval);
        if (interval == -1L) {
            interval = 1000L * 60;
            log.info("Object: {}", "Init Parameter \"sleepIntervalProcess\" nulo usando default \"" + interval + "\"");
        } else
            log.info("Object: {}", "sleepIntervalProcess=" + interval);
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
        log.info("Object: {}", "runIntervalProcess=" + "true".equalsIgnoreCase(strRunIntervalProcess));
        if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
            log.info("Iniciando Background Process");
            verifyLimitTimeCaso = new Thread(this);
            verifyLimitTimeCaso.setPriority(Thread.MIN_PRIORITY);
            verifyLimitTimeCaso.start();
        }
    }

    public void run() {
        while (true) {
            CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
            CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
            try {
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                //Ethiel, por el momento no interesa pero hay que hacerle lo mismo que al de abajo
                cbl.revisaTiempoLimiteDeCasos(prefixPath);
                //Ethiel, se le agrega el boolean extMail
                cobl.revisaTiempoLimiteDeCasosOperacion(prefixPath, extMail);
                Thread.sleep(interval);
            } catch (InterruptedException exc) {
                log.info("Wake up Background Process " + this.getClass().getName(), exc);
            } catch (Exception exc) {
                log.warn("Error en Background Process " + this.getClass().getName(), exc);
                break;
            }
        }
        verifyLimitTimeCaso = null;
    }

    public void destroy() {
        verifyLimitTimeCaso = null;
    }

    private void initLogger(ServletConfig config) throws ServletException {
        String logDirectory = config.getInitParameter("log-directory");
        String baseLevel = config.getInitParameter("log_base_level");
        if (logDirectory == null)
            logDirectory = config.getServletContext().getRealPath("../") + "/logs";
        System.setProperty("log.directory", logDirectory);
        Properties props = new Properties();
        try {
            InputStream is = config.getServletContext().getResourceAsStream("/WEB-INF/log4j.properties");
            props.load(is);
            is.close();
            // // // // // // // PropertyConfigurator.configure(props);
            log = LoggerFactory.getLogger(GestionSetupServlet.class);
            if (baseLevel != null) {
                if (baseLevel.equalsIgnoreCase("debug"))
                    { /* log.setLevel not supported */ }
                else if (baseLevel.equalsIgnoreCase("info"))
                    { /* log.setLevel not supported */ }
                else if (baseLevel.equalsIgnoreCase("warn"))
                    { /* log.setLevel not supported */ }
                else if (baseLevel.equalsIgnoreCase("error"))
                    { /* log.setLevel not supported */ }
                else if (baseLevel.equalsIgnoreCase("fatal"))
                    { /* log.setLevel not supported */ }
                if (log.isDebugEnabled()) {
                    log.debug("Object: " + String.valueOf("log_base_level=" + "UNKNOWN-SLF4J"));
                    log.debug("Object: " + String.valueOf("log-directory=" + logDirectory));
                }
            }
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
