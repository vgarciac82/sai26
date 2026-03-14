package com.axtel.contratos.services;

import java.sql.Connection;
import java.util.List;
import com.axtel.contratos.core.SuficienciaPagoDirectoDetalle;
import com.axtel.contratos.repositories.SuficienciaPagoDirectoDetalleManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class SuficienciaPagoDirectoDetalleBusinessLogic extends DataSourceManager {

    public SuficienciaPagoDirectoDetalleBusinessLogic(String jndiName) {
        super.init(jndiName);
    }

    public SuficienciaPagoDirectoDetalle insert(SuficienciaPagoDirectoDetalle bean) {
        Connection con = null;
        try {
            con = getConnection();
            SuficienciaPagoDirectoDetalleManager.insert(con, bean);
            con.commit();
            return bean;
        } catch (Exception ex) {
            Util.rollback(con);
            throw new RuntimeException("Error al insertar Detalle", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }

    public SuficienciaPagoDirectoDetalle update(SuficienciaPagoDirectoDetalle bean) {
        Connection con = null;
        try {
            con = getConnection();
            SuficienciaPagoDirectoDetalleManager.update(con, bean);
            con.commit();
            return bean;
        } catch (Exception ex) {
            Util.rollback(con);
            throw new RuntimeException("Error al actualizar Detalle", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }

    public void deleteByFolio(int folio) {
        Connection con = null;
        try {
            con = getConnection();
            SuficienciaPagoDirectoDetalleManager.deleteByFolio(con, folio);
            con.commit();
        } catch (Exception ex) {
            Util.rollback(con);
            throw new RuntimeException("Error al eliminar Detalles", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }

    public List<SuficienciaPagoDirectoDetalle> findByFolio(int folio) {
        Connection con = null;
        try {
            con = getConnection();
            return SuficienciaPagoDirectoDetalleManager.findByFolio(con, folio);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar Detalles", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }
}
