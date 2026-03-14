package com.syc.contable.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contratos.core.ContratoFederalizadoBean;
import java.util.Base64;

public class ContratoFederalizadoManager {

    public static int insertDetail(Connection conn, String ep, ContratoFederalizadoBean contrato, Map<String, BigDecimal> calendario) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO pContratoFederalizadoDetalle(cEjercicio, cIdEntidadContable, cIdContrato, EP, nMes, mImporte)");
        query.append("VALUES(?, ?, ?, ?, ?, ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            int insertados = 0;
            for (Iterator<String> iterator = calendario.keySet().iterator(); iterator.hasNext(); ) {
                String mes = iterator.next();
                BigDecimal valor = calendario.get(mes);
                if (Util.ZERO.compareTo(valor) < 0) {
                    int i = 1;
                    ps.setInt(i++, contrato.getEjercicioFiscal());
                    ps.setString(i++, contrato.getCentroContable());
                    ps.setString(i++, contrato.getIdContrato());
                    ps.setString(i++, ep);
                    ps.setInt(i++, Util.numeroDeMes(mes) + 1);
                    ps.setBigDecimal(i++, valor);
                    insertados += ps.executeUpdate();
                    ps.clearParameters();
                }
            }
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int deleteDetail(Connection conn, String ep, ContratoFederalizadoBean contrato) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("DELETE ");
        query.append("  FROM	pContratoFederalizadoDetalle ");
        query.append(" WHERE	cEjercicio = ? ");
        query.append("   AND	cIdEntidadContable = ? ");
        query.append("   AND	cIdContrato = ? ");
        query.append("   AND	EP = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, contrato.getEjercicioFiscal());
            ps.setString(2, contrato.getCentroContable());
            ps.setString(3, contrato.getIdContrato());
            ps.setString(4, ep);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
