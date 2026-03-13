package com.syc.gestion;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.CorreosPendientesBean;
import com.syc.gestion.core.ListadoCorreosPendientesManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.mail.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorreosPendientesBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CorreosPendientesBusinessLogic.class);

    private String prefixPath = null;

    public CorreosPendientesBusinessLogic(String jniName) {
        super.init(jniName);
        log.debug("Correos Pendientes Business Logic");
    }

    public CorreosPendientesBusinessLogic() {
        log.debug("Correos Pendientes Business Logic");
    }

    public int insertaAlertaPendiente(CorreosPendientesBean cpb) throws Exception {
        return insertaAlertaPendiente(cpb.getDestinatario(), cpb.getMensaje(), cpb.getSubject(), cpb.getError());
    }

    public int insertaAlertaPendiente(CorreosPendientesBean cpb, boolean isProcessAutGRM) throws Exception {
        if (isProcessAutGRM) {
            return insertaAlertaPendienteProcesoAut(cpb.getDestinatario(), cpb.getMensaje(), cpb.getSubject(), cpb.getError());
        } else {
            return insertaAlertaPendiente(cpb.getDestinatario(), cpb.getMensaje(), cpb.getSubject(), cpb.getError());
        }
    }

    private int insertaAlertaPendiente(String destinatario, String mensaje, String subject, String error) throws Exception {
        Connection conn = null;
        int tInsertados = 0;
        try {
            conn = getConnection();
            CorreosPendientesBean cpb = new CorreosPendientesBean(destinatario, mensaje, subject, error);
            tInsertados = ListadoCorreosPendientesManager.insert(conn, cpb);
            conn.commit();
            return tInsertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback: " + e2.toString(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    private int insertaAlertaPendienteProcesoAut(String destinatario, String mensaje, String subject, String error) throws Exception {
        Connection conn = null;
        int tInsertados = 0;
        try {
            conn = com.syc.gestion.util.Util.getStandAloneConnection(true);
            CorreosPendientesBean cpb = new CorreosPendientesBean(destinatario, mensaje, subject, error);
            tInsertados = ListadoCorreosPendientesManager.insert(conn, cpb);
            conn.commit();
            return tInsertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback: " + e2.toString(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int reenviaAlertasPendientes() throws Exception {
        Connection conn = null;
        int tEnviados = 0;
        List<CorreosPendientesBean> cpbList = new ArrayList<CorreosPendientesBean>();
        try {
            conn = getConnection();
            cpbList = ListadoCorreosPendientesManager.selectAll(conn);
            if (cpbList != null && cpbList.size() > 0)
                for (Iterator<CorreosPendientesBean> i = cpbList.iterator(); i.hasNext(); tEnviados++) try {
                    reenviaAlertaPendiente(i.next());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            return tEnviados;
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public boolean reenviaAlertaPendiente(CorreosPendientesBean cpb) {
        Connection conn = null;
        boolean exito = false;
        try {
            conn = getConnection();
            MailSender.enviaCorreoCNF(cpb, prefixPath);
            ListadoCorreosPendientesManager.delete(conn, cpb);
            conn.commit();
            exito = true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problema realizando rollback: " + e2.toString(), e2);
                }
        } finally {
            try {
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn(e);
            }
        }
        return exito;
    }

    public String getprefixPath() {
        return prefixPath;
    }

    public void setprefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
    }
}
