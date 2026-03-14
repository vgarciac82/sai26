package com.axtel.contratos.services;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import com.axtel.contratos.core.SuficienciaPagoDirectoEncabezado;
import com.axtel.contratos.repositories.SuficienciaPagoDirectoEncabezadoManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuficienciaPagoDirectoEncabezadoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(SuficienciaPagoDirectoEncabezadoBusinessLogic.class);

    private CFSequenceManager sequence = null;

    public SuficienciaPagoDirectoEncabezadoBusinessLogic(String jndiName) {
        super.init(jndiName);
        sequence = CFSequenceManager.getInstance(jndiName);
    }

    public void deleteByFolio(int folio) {
        Connection conn = null;
        try {
            conn = getConnection();
            SuficienciaPagoDirectoEncabezadoManager.delete(conn, folio);
            conn.commit();
        } catch (Exception ex) {
            Util.rollback(conn);
            throw new RuntimeException("Error al eliminar encabezado", ex);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public SuficienciaPagoDirectoEncabezado findById(int folio) {
        Connection conn = null;
        try {
            conn = getConnection();
            return SuficienciaPagoDirectoEncabezadoManager.findById(conn, folio);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar encabezado", ex);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private SuficienciaPagoDirectoEncabezado insert(Connection conn, SuficienciaPagoDirectoEncabezado bean) throws SQLException {
        if (StringUtils.trimToNull(bean.getUnidadResponsable()) == null)
            throw new RuntimeException("La solicitud no cuenta conn UE establecida, por lo que no es posible generar ID de Solicitud.");
        String seqPrefix = SuficienciaPagoDirectoEncabezado.CONCTRACT_TYPE;
        int nextVal = sequence.nextVal(conn, seqPrefix);
        bean.setIdContrato(seqPrefix + "-" + StringUtils.trim(bean.getUnidadResponsable()) + "-" + nextVal);
        SuficienciaPagoDirectoEncabezadoManager.insert(conn, bean);
        return bean;
    }

    public SuficienciaPagoDirectoEncabezado insert(SuficienciaPagoDirectoEncabezado bean) {
        Connection conn = null;
        try {
            conn = getConnection();
            bean = insert(conn, bean);
            conn.commit();
            return bean;
        } catch (Exception ex) {
            Util.rollback(conn);
            throw new RuntimeException("Error al insertar encabezado", ex);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public SuficienciaPagoDirectoEncabezado insert(SuficienciaPagoDirectoEncabezado bean, Caso c, Usuario user) {
        Connection conn = null;
        try {
            conn = getConnection();
            bean = insert(conn, bean);
            if (c.getIdGabinete() < 0) {
                c.getCasoDato("FOLIO").setValor(c.getFolio());
                c.getCasoDato("DOCUMENT_DATE").setValor(Util.getTodayESMX());
                c.getCasoDato("FISCAL_YEAR").setValor(String.valueOf(bean.getEjercicioFiscal()));
                Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
                int id_gabinete = AplicacionManager.createExpediente(conn, user.getLogin(), c, app);
                if (id_gabinete < 0) {
                    log.error("Identificador de Gabiente inválido (< 0)");
                    throw new RuntimeException("Identificador de Gabiente invalido (< 0)");
                }
                c.setIdGabinete(id_gabinete);
                CasoManager.update(conn, c);
            }
            conn.commit();
            return bean;
        } catch (Exception ex) {
            Util.rollback(conn);
            throw new RuntimeException("Error al insertar encabezado", ex);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean finalizarContratoDirecto(int folio) throws SQLException {
        log.info("Object: {}", "[BL] FinalizarContratoDirecto folio=" + folio);
        Connection con = null;
        boolean ok = false;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            ok = SuficienciaPagoDirectoEncabezadoManager.finalizarContratoDirecto(con, folio);
            con.commit();
            log.info("Object: {}", "[BL] FinalizarContratoDirecto OK, commit realizado. folio=" + folio);
            return ok;
        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback();
                    log.warn("[BL] Rollback realizado.");
                } catch (SQLException ignore) {
                }
            }
            log.error("[BL] Error al finalizar contrato directo. folio=" + folio + " msg=" + ex.getMessage(), ex);
            throw ex;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    public SuficienciaPagoDirectoEncabezado update(SuficienciaPagoDirectoEncabezado bean) {
        Connection conn = null;
        try {
            conn = getConnection();
            SuficienciaPagoDirectoEncabezadoManager.update(conn, bean);
            conn.commit();
            return bean;
        } catch (Exception ex) {
            Util.rollback(conn);
            throw new RuntimeException("Error al actualizar encabezado", ex);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
