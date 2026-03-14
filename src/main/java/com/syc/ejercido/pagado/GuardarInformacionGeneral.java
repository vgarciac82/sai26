package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuardarInformacionGeneral extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public String insertaLineaBanorteRDB(Connection conn, PreparedStatement psInsert, String banco, String fechaCarga, String tipoMovimiento, String cuentaBancaria, String dFechaOper, String referencia, double mImporte, double saldo, String cuenta, String descripcion, String sucursal, String dFechaOperV, String codTransac, String movimiento, String desDetallada, String usuario) throws SQLException {
        String fechaCarg = null, fechaOper = null, fechaOperV = null;
        String respuesta = "no_guardado";
        try {
            // txt Banorte Sin Encabezado
            if (fechaCarga.length() > 1) {
                String[] nF = fechaCarga.split("/");
                fechaCarg = "'" + nF[2] + "-" + nF[1] + "-" + nF[0] + "'";
            }
            if (dFechaOper.length() > 1) {
                String[] nFO = dFechaOper.split("/");
                fechaOper = "'" + nFO[2] + "-" + nFO[1] + "-" + nFO[0] + "'";
            }
            if (dFechaOperV.length() > 1) {
                String[] nFOV = dFechaOperV.split("/");
                fechaOperV = "'" + nFOV[2] + "-" + nFOV[1] + "-" + nFOV[0] + "'";
            }
            psInsert = conn.prepareStatement("INSERT INTO tRdbCargaArchivoRDB (banco, fechaCarga, tipoMovimiento, cuentaBancaria, dFecha, sReferencia, mImporte, mSaldo, sCuenta, sTransaccionDescripcion, sSucursalPlaza, dFechaOperacionValor, sCodTran, sMovimiento, sDescripcionDetallada, loginUsuario, sEstatus)" + " VALUES ('" + banco + "'," + fechaCarg + ",'" + tipoMovimiento + "','" + cuentaBancaria + "'," + fechaOper + ",'" + referencia + "'," + mImporte + "," + saldo + ",'" + cuenta + "','" + descripcion + "','" + sucursal + "'," + fechaOperV + ",'" + codTransac + "','" + movimiento + "','" + desDetallada + "','" + usuario + "','CARGADO')");
            psInsert.executeUpdate();
            respuesta = "guardado";
        } catch (Exception e) {
            log.error("Error occurred", "Error Guardar Informacion Registro Diario Bancos: " + e);
        }
        return respuesta;
    }

    public String insertaLineaScotiabankRDB(Connection conn, PreparedStatement psInsert, String banco, String fechaCarga, String tipoMovimiento, String cuentaBancaria, String sCuenta, String sPlaza, String sMoneda, String dFecha, String sReferencia, String mImporte, String transConcepto, String saldo, String leyenda1, String leyenda2, String usuario) throws SQLException {
        String fechaCarg = null, dFech = null;
        String respuesta = "no_guardado";
        try {
            if (fechaCarga.length() > 1) {
                String[] nF = fechaCarga.split("/");
                fechaCarg = "'" + nF[2] + "-" + nF[1] + "-" + nF[0] + "'";
            }
            if (dFecha.length() > 1) {
                String[] nFO = dFecha.split("/");
                dFech = "'" + nFO[0] + "-" + nFO[1] + "-" + nFO[2] + "'";
            }
            psInsert = conn.prepareStatement("INSERT INTO tRdbCargaArchivoRDB (banco, fechaCarga, tipoMovimiento, cuentaBancaria, dFecha, sReferencia, mImporte, mSaldo, sCuenta, sTransaccionDescripcion, sSucursalPlaza, sMoneda, sLeyenda1, sLeyenda2, loginUsuario, sEstatus)" + " VALUES ('" + banco + "'," + fechaCarg + ",'" + tipoMovimiento + "','" + cuentaBancaria + "'," + dFech + ",'" + sReferencia + "'," + mImporte + "," + saldo + ",'" + sCuenta + "','" + transConcepto + "','" + sPlaza + "','" + sMoneda + "','" + leyenda1 + "','" + leyenda2 + "','" + usuario + "','CARGADO')");
            psInsert.executeUpdate();
            respuesta = "guardado";
        } catch (Exception e) {
            log.error("Error occurred", "Error Guardar Informacion Registro Diario Bancos: " + e);
        }
        return respuesta;
    }

    public String insertaLineaBancomerComRDB(Connection conn, PreparedStatement psInsert, String banco, String tipoBancomer, String fechaCarga, String tipoMovimiento, String cuentaBancaria, String dFechaOper, String referencia, double mImporte, double saldo, String usuario) throws SQLException {
        String fechaCarg = null, fechaOper = null;
        String respuesta = "no_guardado";
        try {
            if (fechaCarga.length() > 1) {
                String[] nF = fechaCarga.split("/");
                fechaCarg = "'" + nF[2] + "-" + nF[1] + "-" + nF[0] + "'";
            }
            if (dFechaOper.length() > 1) {
                String[] nFO = dFechaOper.split("/");
                fechaOper = "'" + nFO[2] + "-" + nFO[1] + "-" + nFO[0] + "'";
            }
            //String n = "INSERT INTO tRdbCargaArchivoRDB (banco, fechaCarga, tipoMovimiento, cuentaBancaria, dFecha, sReferencia, mImporte, mSaldo, loginUsuario, sEstatus)  VALUES ('"+banco+"',"+fechaCarg+",'"+tipoMovimiento+"','"+cuentaBancaria+"',"+fechaOper+",'"+referencia+"',"+mImporte+","+saldo+",'"+usuario+"','Cargado')";
            //System.out.println(n);
            psInsert = conn.prepareStatement("INSERT INTO tRdbCargaArchivoRDB (banco, tipoBancomer, fechaCarga, tipoMovimiento, cuentaBancaria, dFecha, sTransaccionDescripcion, mImporte, mSaldo, loginUsuario, sEstatus)" + " VALUES ('" + banco + "','" + tipoBancomer + "'," + fechaCarg + ",'" + tipoMovimiento + "','" + cuentaBancaria + "'," + fechaOper + ",'" + referencia + "'," + mImporte + "," + saldo + ",'" + usuario + "','CARGADO')");
            psInsert.executeUpdate();
            respuesta = "guardado";
        } catch (Exception e) {
            log.error("Error occurred", "Error Guardar Informacion Registro Diario Bancos: " + e);
        }
        return respuesta;
    }

    public String insertaLineaBancomerCashRDB(Connection conn, PreparedStatement psInsert, String banco, String tipoBancomer, String fechaCarga, String tipoMovimiento, String cuentaBancaria, String cuenta, String fechaV, String folioBan, String transaccion, String tipoMovi, String importe, String moneda, String folioAcep, String ref, String contrato, String fechaOper, String contratoCw, String codTrans, String tipoOper, String plaza) throws SQLException {
        String fechaCarg = null;
        String respuesta = "no_guardado";
        try {
            // txt BancomerNetCash 910
            if (fechaCarga.length() > 1) {
                String[] nF = fechaCarga.split("/");
                fechaCarg = "'" + nF[2] + "-" + nF[1] + "-" + nF[0] + "'";
            }
            if (fechaV.length() > 1) {
                String[] nFV = fechaV.split("-");
                fechaV = "'" + nFV[0] + "-" + nFV[1] + "-" + nFV[2] + "'";
            }
            if (fechaOper.length() > 1) {
                String[] nFo = fechaOper.split("/");
                fechaOper = "'" + nFo[2] + "-" + nFo[1] + "-" + nFo[0] + "'";
            }
            psInsert = conn.prepareStatement("INSERT INTO tRdbCargaArchivoRDB (banco, tipoBancomer, fechaCarga, tipoMovimiento, cuentaBancaria, sCuenta, dFecha, sFolioBanco, sTransaccionDescripcion, mImporte, sMoneda, sFolioAceptacion, sDescripcionDetallada, sReferencia, dFechaOperacionValor, sNombreContratoCW, sCodTran,  sTipoOperacion ,sSucursalPlaza, sEstatus)" + " VALUES ('" + banco + "', '" + tipoBancomer + "', " + fechaCarg + ",'" + tipoMovimiento + "','" + cuentaBancaria + "','" + cuenta + "'," + fechaV + ",'" + folioBan + "','" + transaccion + "'," + importe + ",'" + moneda + "','" + folioAcep + "', '" + ref + "', 		'" + contrato + "', " + fechaOper + ", '" + contratoCw + "', '" + codTrans + "', '" + tipoOper + "', '" + plaza + "' ,'CARGADO')");
            psInsert.executeUpdate();
            respuesta = "guardado";
        } catch (Exception e) {
            log.error("Error occurred", "Error Guardar Informacion Registro Diario Bancos: " + e);
        }
        return respuesta;
    }
}
