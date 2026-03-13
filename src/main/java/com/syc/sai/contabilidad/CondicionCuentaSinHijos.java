package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.contable.AccountingEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CondicionCuentaSinHijos extends CondicionCuentaContable {

    Logger log = LoggerFactory.getLogger(CondicionCuentaSinHijos.class);

    @Override
    public void preEjecucion(Connection con, int nMes, int aEjercicioFiscal, String cCentroContable) throws AccountingEngineException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean CumpleCondicion(Connection conn, int nMes, int aEjercicioFiscal, String cCentroContable) throws AccountingEngineException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void preEjecucion(Connection con) throws AccountingEngineException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean CumpleCondicion(Connection conn, CuentaContable cuenta) throws AccountingEngineException {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String qry = "SELECT	TOP 1 nCuenta " + " FROM	tCuentas WITH(nolock) " + " WHERE	nCuentaPadre = ? " + "  AND nCuenta != nCuentaPadre";
        boolean cumple = true;
        try {
            pStmnt = conn.prepareStatement(qry);
            pStmnt.setString(1, cuenta.getnCuenta());
            rs = pStmnt.executeQuery();
            if (rs.next()) {
                setMensaje("No se puede modificar una cuenta con hijos");
                cumple = false;
            }
            return cumple;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando PreparedStatement");
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando ResultSet");
                }
        }
    }
}
