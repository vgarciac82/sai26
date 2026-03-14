package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Base64;

public class FileStorageCapacityManager {

    public static boolean isNotMeetQuota(Connection conn, String nombre_usuario, double bytes) throws SQLException {
        Statement stmntA = null;
        Statement stmntB = null;
        ResultSet rs = null;
        boolean retVal = false;
        double bytesAutorizados = 0;
        double bytesUsadosUsr = 0;
        double bytesUsadosCia = 0;
        try {
            stmntA = conn.createStatement();
            stmntB = conn.createStatement();
            rs = stmntA.executeQuery("SELECT bytes_autorizados, bytes_usados FROM imx_usuario_expediente" + " WHERE nombre_usuario = '" + nombre_usuario + "'");
            if (!rs.next())
                throw new SQLException("No se encontro el usuario(" + nombre_usuario + ")");
            bytesAutorizados = rs.getDouble(1);
            bytesUsadosUsr = rs.getDouble(2) + bytes;
            if ((bytesAutorizados > 0) && (bytesUsadosUsr > bytesAutorizados)) {
                throw new SQLException("Se ha excedido el espacio autorizado del Usuario " + nombre_usuario);
            } else {
                rs = stmntA.executeQuery("SELECT bytes_autorizados, bytes_usados FROM imx_empresa");
                if (!rs.next()) {
                    stmntB.addBatch("INSERT INTO imx_empresa (bytes_autorizados, bytes_usados) VALUES(52428800, 0)");
                } else {
                    bytesAutorizados = rs.getDouble(1);
                    bytesUsadosCia = rs.getDouble(2) + bytes;
                    if ((bytesAutorizados > 0) && (bytesUsadosCia > bytesAutorizados)) {
                        throw new SQLException("Se ha excedido el espacio autorizado de la Empresa");
                    }
                }
            }
            stmntB.addBatch("UPDATE imx_usuario_expediente SET bytes_usados = " + bytesUsadosUsr + " WHERE nombre_usuario = '" + nombre_usuario + "'");
            stmntB.addBatch("UPDATE imx_empresa SET bytes_usados = " + bytesUsadosCia);
            stmntB.executeBatch();
            retVal = true;
        } finally {
            if (rs != null)
                rs.close();
            if (stmntB != null)
                stmntB.close();
            if (stmntA != null)
                stmntA.close();
            rs = null;
            stmntA = null;
            stmntB = null;
        }
        return retVal;
    }

    public static boolean decrementQuota(Connection conn, String nombre_usuario, double bytes) throws SQLException {
        Statement stmntA = null;
        Statement stmntB = null;
        ResultSet rs = null;
        boolean retVal = true;
        double bytesUsados = 0;
        try {
            stmntA = conn.createStatement();
            stmntB = conn.createStatement();
            rs = stmntA.executeQuery("SELECT bytes_usados" + " FROM imx_usuario_expediente" + " WHERE nombre_usuario = '" + nombre_usuario + "'");
            if (!rs.next())
                throw new SQLException("No se encontro el usuario(" + nombre_usuario + ")");
            bytesUsados = (rs.getDouble(1) - bytes) < 0 ? 0 : rs.getDouble(1) - bytes;
            stmntB.addBatch("UPDATE imx_usuario_expediente SET bytes_usados = " + bytesUsados + " WHERE nombre_usuario = '" + nombre_usuario + "'");
            rs = stmntA.executeQuery("SELECT bytes_usados FROM imx_empresa");
            if (!rs.next()) {
                bytesUsados = bytes;
                stmntB.addBatch("INSERT INTO imx_empresa (bytes_autorizados, bytes_usados) VALUES(52428800, 0)");
            } else {
                bytesUsados = (rs.getDouble(1) - bytes) < 0 ? 0 : rs.getDouble(1) - bytes;
            }
            stmntB.addBatch("UPDATE imx_empresa SET bytes_usados = " + bytesUsados);
            stmntB.executeBatch();
            retVal = true;
        } finally {
            if (rs != null)
                rs.close();
            if (stmntA != null)
                stmntA.close();
            if (stmntB != null)
                stmntB.close();
            rs = null;
            stmntA = null;
            stmntB = null;
        }
        return retVal;
    }

    public static String conversion(double value) {
        int i;
        double bytes = value;
        String[] units = { "bytes", "Kb", "Mb", "Gb", "Tb" };
        for (i = 0; i < units.length; i++) {
            if (value >= Math.pow(2, i * 10) && (value < Math.pow(2, (i + 1) * 10))) {
                if (i > 0)
                    bytes = value / Math.pow(2, i * 10);
                break;
            }
        }
        if (i == units.length)
            return "0 bytes";
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        return nf.format(bytes) + " " + units[i];
    }
}
