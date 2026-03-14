package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.servlet.http.HttpServletRequest;
import com.syc.contable.core.PAnticipadoManager;
import com.syc.contable.core.RefasManager;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAnticipadoBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(PAnticipadoBusinessLogic.class);

    public enum TipoFecha {

        UltimaActualizacion, Solicitud, Aplicacion, Cancelacion
    }

    public PAnticipadoBusinessLogic(String jniName) {
        if (jniName == null) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
        } else
            log.info("Object: {}", "dataSourceRefName=" + jniName);
        super.init(jniName);
    }

    public String getFecha(String folio, TipoFecha tipo) throws SQLException {
        Connection conn = null;
        Date date = null;
        String[] folioString = folio.split("-");
        int intFolio = Integer.parseInt(folioString[2]);
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String dateFormat = null;
        Integer indice = 1;
        if (tipo == TipoFecha.UltimaActualizacion) {
            indice = 1;
        }
        if (tipo == TipoFecha.Aplicacion) {
            indice = 2;
        }
        if (tipo == TipoFecha.Cancelacion) {
            indice = 3;
        }
        if (tipo == TipoFecha.Solicitud) {
            indice = 4;
        }
        try {
            conn = getConnection();
            date = PAnticipadoManager.getFecha(intFolio, conn, indice);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        if (date != null)
            dateFormat = sdf.format(date);
        return dateFormat;
    }

    public void CierreMensual() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PAnticipadoManager.CierreMensual(conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String CancelacionMasiva(String usuario, HttpServletRequest request) throws SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            mensaje = PAnticipadoManager.CancelacionMasiva(conn, usuario, request);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    public void AperturaMensual() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PAnticipadoManager.AperturaMensual(conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public void actualizaFechaUltimaAct(int folio) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PAnticipadoManager.updateFechaAct(folio, conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public void actualizaFechaSolicitud(int folio) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PAnticipadoManager.updateFechaSol(folio, conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public void actualizaEstatus(int folio, int estatus) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PAnticipadoManager.updateEstatus(folio, estatus, conn);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String getEstatus(int folio) throws SQLException {
        Connection conn = null;
        String retval = "";
        try {
            conn = getConnection();
            retval = PAnticipadoManager.getEstatus(folio, conn);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return retval;
    }
}
