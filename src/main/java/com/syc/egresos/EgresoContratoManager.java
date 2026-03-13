package com.syc.egresos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgresoContratoManager {

    private static final Logger log = LoggerFactory.getLogger(EgresoContratoManager.class);

    public static String getNumeroContratoEgreso(Connection conn, String tipoPago, int folioPago) throws Exception {
        String query = "";
        if ("PAGOFEDERALIZADO".equals(tipoPago)) {
            query = "SELECT cFolioContratoObra FROM t" + tipoPago + "Encabezado with(nolock) WHERE nFolio" + tipoPago + " = ?";
        } else if ("PAGODIRECTO".equals(tipoPago)) {
            query = "SELECT cidcontrato FROM t" + tipoPago + "Encabezado with(nolock) WHERE nFolio" + tipoPago + " = ?";
        } else {
            query = "SELECT cFolio" + tipoPago + " FROM t" + tipoPago + "Encabezado with(nolock) WHERE nFolio" + tipoPago + " = ?";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            log.debug("Query generado: \n" + query + "\n Folio[" + folioPago + "]");
            ps = conn.prepareStatement(query);
            ps.setInt(1, folioPago);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else
                throw new Exception("No se encontro numero de contrato para el folio: " + folioPago + " en el tipo de pago " + tipoPago);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean existeFacturaGlobal(Connection conn, String tipoContrato, String idContrato, String uuidGlobal) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT  COUNT(*) AS total ");
        query.append("  FROM  tcontratofactura WITH(NOLOCK) ");
        query.append(" WHERE  ctipocontrato = ? ");
        query.append("   AND  cidcontrato=? ");
        query.append("   AND  cfactura  = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoContrato);
            ps.setString(2, idContrato);
            ps.setString(3, uuidGlobal);
            rs = ps.executeQuery();
            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }
            return existe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
