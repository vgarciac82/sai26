package com.syc.contable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CarteraVigenteGetSaldoManager {

    private static final Logger log = LoggerFactory.getLogger(CarteraVigenteGetSaldoManager.class);

    public static double getSaldoModificado(Connection conn, String ep) throws Exception {
        ResultSet rs = null;
        double montoModificado = 0.0d;
        //modificado 81102
        PreparedStatement ps = null;
        String query = "select ep.cCartera, SUM( saldos.msaldoarrastre) as montoModificado from tSaldos saldos,  tCatalogoEP ep where saldos.csubcuenta = ep.ep " + "and nCuenta like '81102%' and ep.cCartera = ( select  SUBSTRING('" + ep + "', 45,11)) group by ep.cCartera";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                montoModificado = rs.getDouble("saldo");
            }
            return montoModificado;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }
}
