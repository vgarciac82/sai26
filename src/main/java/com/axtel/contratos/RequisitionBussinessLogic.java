package com.axtel.contratos;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.LogManager;
import com.axtel.contratos.core.RequisitionManager;
import com.axtel.contratos.exception.ContratoException;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequisitionBussinessLogic extends DataSourceManager {

    private static final Logger log = LogManager.getLogger(RequisitionBussinessLogic.class);

    public RequisitionBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public Requisition getRequisition(Caso c) throws IllegalAccessException, InvocationTargetException, SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return RequisitionManager.readRequisition(conn, c.getFolio());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean cancelRequisition(Requisition requisition) throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            boolean success = cancelRequisition(conn, requisition);
            conn.commit();
            return success;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new ContratoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean cancelRequisition(Connection conn, Requisition requisition) throws ContratoException {
        try {
            int updated = RequisitionManager.changeStatusCancel(conn, requisition);
            updated += RequisitionManager.changeLinesStatusCancel(conn, requisition);
            RequisitionManager.insertOperationLog(conn, requisition);
            RequisitionManager.cancelAccountingMovements(conn, requisition);
            return updated > 0;
        } catch (Exception e) {
            throw new ContratoException(e);
        }
    }
}
