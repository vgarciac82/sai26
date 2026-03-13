package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.utils.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLCAttachmentLogManager {

    public static final Logger log = LoggerFactory.getLogger(CLCAttachmentLogManager.class);

    public static int insertLog(Connection conn, String nombreArchivo, int clc, String caNoContraRecibo, int folioPago, String tipoPago, char estatusPago, String log) throws Exception {
        return insertLog(conn, nombreArchivo, clc, caNoContraRecibo, folioPago, tipoPago, estatusPago, log, null);
    }

    public static int insertLogCLC(Connection conn, String nombreArchivo, int clc, String caNoContraRecibo, int folioPago, String tipoPago, char estatusPago, String log, String proceso, String cIDProceso) throws Exception {
        String query = "INSERT INTO tLogAdjuntaCLC" + "( fProceso, " + "  nombreArchivo, " + "  noCLC, " + "  caNoContraRecibo, " + "  nFolioPago, " + "  cTipoPago, " + "  cEstatusAdjunto, " + "  cLogOperacion " + (StringUtils.isEmpty(proceso) ? "" : ", cProceso") + (StringUtils.isEmpty(cIDProceso) ? "" : ", cIDProceso") + ")" + "VALUES  ( GETDATE() ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ?" + (StringUtils.isEmpty(proceso) ? "" : ", ?") + (StringUtils.isEmpty(cIDProceso) ? "" : ", ?") + "        )";
        PreparedStatement ps = null;
        int insertados = 0;
        int cnt = 1;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(cnt++, nombreArchivo);
            ps.setInt(cnt++, clc);
            ps.setString(cnt++, caNoContraRecibo);
            ps.setInt(cnt++, folioPago);
            ps.setString(cnt++, tipoPago);
            ps.setInt(cnt++, estatusPago);
            ps.setString(cnt++, log);
            if (!StringUtils.isEmpty(proceso))
                ps.setString(cnt++, proceso);
            if (!StringUtils.isEmpty(cIDProceso))
                ps.setString(cnt++, cIDProceso);
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static int insertLog(Connection conn, String nombreArchivo, int clc, String caNoContraRecibo, int folioPago, String tipoPago, char estatusPago, String log, String proceso) throws Exception {
        String query = "INSERT INTO tLogAdjuntaCLC" + "( fProceso, " + "  nombreArchivo, " + "  noCLC, " + "  caNoContraRecibo, " + "  nFolioPago, " + "  cTipoPago, " + "  cEstatusAdjunto, " + "  cLogOperacion " + (StringUtils.isEmpty(proceso) ? "" : ", cProceso") + ")" + "VALUES  ( GETDATE() ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ?" + (StringUtils.isEmpty(proceso) ? "" : ", ?") + "        )";
        PreparedStatement ps = null;
        int insertados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nombreArchivo);
            ps.setInt(2, clc);
            ps.setString(3, caNoContraRecibo);
            ps.setInt(4, folioPago);
            ps.setString(5, tipoPago);
            ps.setInt(6, estatusPago);
            ps.setString(7, log);
            if (!StringUtils.isEmpty(proceso))
                ps.setString(8, proceso);
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }
}
