/**
 */
package com.syc.contable.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso
 * para SYC Constructores de Sistemas
 * desarrollo gestion_conagua_sif
 * México D.F. 16/03/2012
 */
public class CuentaBancariaManager {

    /**
     */
    public CuentaBancariaManager() {
        // TODO Auto-generated constructor stub
    }

    public static StringBuffer BuscaCuentas(Connection conn, String BeneficiariosCuentasBancarias) throws SQLException {
        StringBuffer archivoCtasBan = new StringBuffer();
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String FECHA_EFECTIVA = dateFormatter.format(new Date(System.currentTimeMillis()));
        PreparedStatement pstmntCB = null;
        ResultSet rsCB = null;
        String[] registros = BeneficiariosCuentasBancarias.split(",");
        String[] campos = new String[3];
        try {
            archivoCtasBan.append("H\r\n");
            for (int i = 0; i < registros.length; i++) {
                campos = registros[i].split("\\|");
                String SqlCB = "SELECT estatusL, cRamo, CBEN, clabe, CBAN, sucursal FROM vListaCuentasBancarias " + " WHERE banco = " + campos[0] + " and cuenta = " + campos[1] + " and CBEN = " + campos[2];
                pstmntCB = conn.prepareStatement(SqlCB);
                rsCB = pstmntCB.executeQuery();
                while (rsCB.next()) {
                    String registro = FECHA_EFECTIVA + "," + //activo
                    rsCB.getString(1) + "," + //cRamo
                    rsCB.getString(2) + "," + //CBEN
                    rsCB.getString(3) + "," + //clabe
                    rsCB.getString(4) + "," + //CBAN
                    rsCB.getString(5) + "," + //sucursal
                    rsCB.getString(6);
                    registro = registro + "\r\n";
                    archivoCtasBan.append(registro);
                }
                //cierra while
            }
            //cerra for
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rsCB != null) {
                rsCB.close();
            }
            if (pstmntCB != null) {
                pstmntCB.close();
            }
        }
        return archivoCtasBan;
    }

    public static int UpdateStatusBCB(Connection conn, String BeneficiariosCuentasBancarias) throws SQLException {
        PreparedStatement pstmntUpdate = null;
        int regActualizados = 0;
        String[] registrosU = BeneficiariosCuentasBancarias.split(",");
        String[] camposU = new String[3];
        try {
            for (int i = 0; i < registrosU.length; i++) {
                camposU = registrosU[i].split("\\|");
                String SqlUpdate = "UPDATE tBeneficiarioCuentasBancarias SET nBCBEnviadoSICOP = 1 " + " WHERE dCuentaBancaria = " + camposU[1] + " AND cBanco = (SELECT cBanco from tBancos where dBancoAbreviado = " + camposU[0] + ")";
                pstmntUpdate = conn.prepareStatement(SqlUpdate);
                pstmntUpdate.executeUpdate();
                regActualizados++;
            }
        } finally {
            if (pstmntUpdate != null) {
                pstmntUpdate.close();
            }
            pstmntUpdate = null;
        }
        return regActualizados;
    }
}
