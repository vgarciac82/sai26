package com.syc.sai.session;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SaiSessionListener implements HttpSessionListener, GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(SaiSessionListener.class);

    public void sessionCreated(HttpSessionEvent session) {
        return;
    }

    public void sessionDestroyed(HttpSessionEvent session) {
        HttpSession sessionMng = session.getSession();
        if (sessionMng != null) {
            Usuario u = (Usuario) sessionMng.getAttribute(ATT_USER);
            if (u != null) {
                SessionBussinesLogic sbl = new SessionBussinesLogic(ATT_CONEXION);
                try {
                    int liberados = sbl.liberaCasos(u);
                    log.info("Object: {}", "Se liberaron " + liberados + " tramites del usuario.");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
