package com.syc.ws.controlinventarios;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlInventariosWSServlet extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 3830246252504144684L;

    private static final Logger log = LoggerFactory.getLogger(ControlInventariosWSServlet.class);

    private String jniName;

    private long interval = -1L;

    private volatile Thread verificaTiempoLimiteCI;

    private int month = 0;

    private int year = 0;

    private int type = 0;

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
        String strMonth = config.getInitParameter("month");
        month = Integer.parseInt(strMonth);
        log.info("Object: {}", "Init Parameter \"month\"  \"" + month + "\"");
        String strYear = config.getInitParameter("year");
        year = Integer.parseInt(strYear);
        log.info("Object: {}", "Init Parameter \"year\"  \"" + year + "\"");
        String strType = config.getInitParameter("type");
        type = Integer.parseInt(strType);
        log.info("Object: {}", "Init Parameter \"type\"  \"" + type + "\"");
        if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
            log.info("Iniciando Background Process");
            verificaTiempoLimiteCI = new Thread(this);
            verificaTiempoLimiteCI.setPriority(Thread.MIN_PRIORITY);
            verificaTiempoLimiteCI.start();
        }
    }

    public void run() {
        WSControlInventariosBusinessLogic wscibl = new WSControlInventariosBusinessLogic(jniName);
        while (true) {
            try {
                wscibl.sendCtrlInventory(month, year, type);
                Thread.sleep(interval);
            } catch (InterruptedException exc) {
                log.info("Wake up Background Process " + this.getClass().getName(), exc);
            } catch (Exception exc) {
                log.warn("Error en Background Process " + this.getClass().getName(), exc);
                break;
            }
        }
        verificaTiempoLimiteCI = null;
    }

    public void destroy() {
        verificaTiempoLimiteCI = null;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        String aEjercicioFiscal = "";
        try {
            aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
        } catch (Exception e) {
            throw new ServletException(e);
        }
        try {
            WSControlInventariosBusinessLogic wscibl = new WSControlInventariosBusinessLogic(GestionInterface.ATT_CONEXION);
            wscibl.sendCtrlInventory(6, 2018, 1);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
