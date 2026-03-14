package com.axtel.contratos.services;

import java.sql.Connection;
import java.util.List;
import com.axtel.contratos.core.SuficienciaPagoDirectoEP;
import com.axtel.contratos.repositories.SuficienciaPagoDirectoEPManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class SuficienciaPagoDirectoEPBusinessLogic extends DataSourceManager {

    public SuficienciaPagoDirectoEPBusinessLogic(String jndiName) {
        super.init(jndiName);
    }

    public SuficienciaPagoDirectoEP insert(SuficienciaPagoDirectoEP bean) {
        Connection con = null;
        try {
            con = getConnection();
            SuficienciaPagoDirectoEPManager.insert(con, bean);
            con.commit();
            return bean;
        } catch (Exception ex) {
            Util.rollback(con);
            throw new RuntimeException("Error al insertar EP", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }

    public SuficienciaPagoDirectoEP update(SuficienciaPagoDirectoEP bean) {
        Connection con = null;
        try {
            con = getConnection();
            SuficienciaPagoDirectoEPManager.update(con, bean);
            con.commit();
            return bean;
        } catch (Exception ex) {
            Util.rollback(con);
            throw new RuntimeException("Error al actualizar EP", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }

    public void deleteByFolio(int folio, String ep) {
        Connection con = null;
        try {
            con = getConnection();
            SuficienciaPagoDirectoEPManager.deleteByFolio(con, folio, ep);
            con.commit();
        } catch (Exception ex) {
            Util.rollback(con);
            throw new RuntimeException("Error al eliminar EPs", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }

    public List<SuficienciaPagoDirectoEP> findByFolio(int folio) {
        Connection con = null;
        try {
            con = getConnection();
            return SuficienciaPagoDirectoEPManager.findByFolio(con, folio);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar EPs", ex);
        } finally {
            CloseObject.closeObject(con);
        }
    }
}
