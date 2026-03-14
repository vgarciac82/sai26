package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.syc.contable.core.AdministracionAccesoManager;
import com.syc.contable.core.AdministracionMensajesManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AdministracionMensajesBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdministracionMensajesBusinessLogic.class);

    public AdministracionMensajesBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> getUnidadesResponsables() throws Exception {
        Connection conn = null;
        ArrayList<String> res;
        try {
            conn = getConnection();
            res = AdministracionMensajesManager.UnidadesResponsables(conn);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public List<String> getUsuariosCC(String cc, String ur) throws Exception {
        Connection conn = null;
        List<String> res;
        try {
            conn = getConnection();
            res = AdministracionMensajesManager.UsuariosCC(conn, cc, ur);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean desactivaMensaje() throws SQLException {
        Connection conn = null;
        boolean res = true;
        try {
            conn = getConnection();
            res = AdministracionMensajesManager.desactivaMensaje(conn);
        } catch (Exception e) {
            res = false;
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback de conexi\u00F3n" + e2, e2);
                }
            throw new SQLException(e);
        } finally {
            try {
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn("Problemas realizando rollback de conexi\u00F3n" + e, e);
            }
        }
        return res;
    }

    public List<String> getselecChecks() throws Exception {
        Connection conn = null;
        List<String> res;
        try {
            conn = getConnection();
            res = AdministracionMensajesManager.selecChecks(conn);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public boolean guardaAlcance(String mensajeAdmin, String ur, String cc, String[] lu, String activaAlerta) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            AdministracionMensajesManager.desactivaMensaje(conn);
            AdministracionMensajesManager.guardaMensaje(conn, mensajeAdmin, ur, cc, activaAlerta);
            if (lu != null) {
                for (int i = 0; i < lu.length; i++) {
                    String idUsuario = lu[i];
                    if (!"".equals(idUsuario)) {
                        boolean existe = AdministracionMensajesManager.validaUsuario(conn, idUsuario);
                        if (!existe)
                            throw new SQLException("No Existe el usuario o relacion usuario-Grupo para " + idUsuario);
                    }
                    AdministracionMensajesManager.guardaAlcance(conn, mensajeAdmin, ur, cc, idUsuario);
                }
            } else {
                if (ur.equals("-1") && cc.equals("-1"))
                    AdministracionMensajesManager.guardaAlcance(conn, mensajeAdmin, "Total", cc, "");
                else
                    AdministracionMensajesManager.guardaAlcance(conn, mensajeAdmin, ur, cc, "");
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas realizando Rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public String mensajeInbox(String UR, String cCentroContable, String u_login) throws SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            mensaje = AdministracionMensajesManager.mensajeInbox(conn, UR, cCentroContable, u_login);
        } catch (Exception e) {
            mensaje = "";
            if (conn != null)
                try {
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback de conexi\u00F3n" + e2, e2);
                }
            throw new SQLException(e);
        } finally {
            try {
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn("Problemas realizando rollback de conexi\u00F3n" + e, e);
            }
        }
        return mensaje;
    }

    public boolean mensajeInboxAlerta(String UR, String cCentroContable, String u_login) throws SQLException {
        Connection conn = null;
        boolean alerta = false;
        try {
            conn = getConnection();
            alerta = AdministracionMensajesManager.mensajeInboxAlerta(conn, UR, cCentroContable, u_login);
        } catch (Exception e) {
            alerta = false;
            if (conn != null)
                try {
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback de conexi\u00F3n" + e2, e2);
                }
            throw new SQLException(e);
        } finally {
            try {
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn("Problemas realizando rollback de conexi\u00F3n" + e, e);
            }
        }
        return alerta;
    }
}
