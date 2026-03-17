package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jfree.util.Log;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SubirArchivoGeneral extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public String enviaRuta(InputStream in, String tipo, String banco, String tipoBancomer, String cuentaBancaria, String fechaCarga, String usuario) throws FileNotFoundException {
        //List<String> mapa = new ArrayList<String>();
        //Hashtable<String, Integer> nomColumna = new Hashtable<String, Integer>();
        Connection conn = null;
        PreparedStatement psInsert = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String sCadenas = "";
        String valorReturn = "";
        String statusLinea = "";
        try {
            conn = getConnection();
            GuardarInformacionGeneral guardaInformacion = new GuardarInformacionGeneral();
            int lineaNumero = 1;
            while ((sCadenas = br.readLine()) != null) {
                System.out.println("linea:" + lineaNumero);
                System.out.println("cadena:" + sCadenas);
                if (tipo.equals("archivosTxtRDB")) {
                    if (banco.equals("BBVA BANCOMER") && tipoBancomer.equals("BANCOMERCOM")) {
                        sCadenas = sCadenas.replaceAll("	", " |");
                        String[] celdas = sCadenas.split("[|]");
                        statusLinea = guardarBancomerCom(conn, psInsert, celdas, guardaInformacion, lineaNumero, banco, tipoBancomer, fechaCarga, cuentaBancaria, usuario);
                    } else if (banco.equals("BBVA BANCOMER") && tipoBancomer.equals("BANCOMERNETCASH")) {
                        statusLinea = guardarBancomerCash(conn, psInsert, sCadenas, guardaInformacion, lineaNumero, banco, tipoBancomer, fechaCarga, cuentaBancaria, usuario);
                    } else if (banco.equals("BANORTE")) {
                        String[] celdas = sCadenas.split("[|]");
                        statusLinea = guardarBanorte(conn, psInsert, celdas, guardaInformacion, lineaNumero, banco, fechaCarga, cuentaBancaria, usuario);
                    } else if (banco.equals("SCOTIABANK")) {
                        //sCadenas = sCadenas.replaceAll("	", "|");
                        String[] celdas = sCadenas.split("[|]");
                        statusLinea = guardarScotiabank(conn, psInsert, celdas, guardaInformacion, lineaNumero, banco, fechaCarga, cuentaBancaria, usuario);
                    }
                    lineaNumero = lineaNumero + 1;
                    if (statusLinea.equals("no_guardado")) {
                        break;
                    }
                }
            }
            valorReturn = statusLinea;
            if (statusLinea.equals("guardado")) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception se) {
            log.error("Error occurred", "Error: " + se);
            se.printStackTrace();
            valorReturn = "Error";
            try {
                conn.rollback();
            } catch (Exception exc) {
                Log.warn("Error occurred" + " - " + "Error: cerrando rollback enviaRuta " + exc);
            }
        } finally {
            try {
                if (psInsert != null) {
                    psInsert.close();
                }
            } catch (Exception exc) {
                log.warn("Cerrando BufferedReader", exc);
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ps) {
                log.warn("Cerrando PreparedStatement ", ps);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception exc) {
                log.warn("Cerrando BufferedReader", exc);
            }
            br = null;
            psInsert = null;
            conn = null;
        }
        System.out.println("valorReturn:" + valorReturn);
        return valorReturn;
    }

    public String guardarBanorte(Connection conn, PreparedStatement psInsert, String[] celdas, GuardarInformacionGeneral guardaInformacion, int lineaNumero, String banco, String fechaCarga, String cuentaBancaria, String usuario) throws SQLException {
        String valorReturn = "";
        //System.out.println(celdas);
        //System.out.println("Numero de Encabezados: "+celdas.length);
        try {
            // Se quitaran los encabezado de txt
            if (celdas.length != 12 && lineaNumero == 1) {
                valorReturn = "columnasDiferentes";
            } else {
                String cuenta = celdas[0].trim();
                String dFechaOper = celdas[1].trim();
                String dFechaOperV = celdas[2].trim();
                String referencia = celdas[3].trim();
                String descripcion = celdas[4].trim();
                String codTransac = celdas[5].trim();
                String sucursal = celdas[6].trim();
                String deposito = celdas[7].trim();
                String retiros = celdas[8].trim();
                String saldo = celdas[9].trim();
                String movimiento = celdas[10].trim();
                String desDetallada = celdas[11].trim();
                if (!deposito.equals("")) {
                    deposito = deposito.replace("$", "");
                    deposito = deposito.replace(",", "");
                } else {
                    deposito = "0.00";
                }
                double dep = Double.parseDouble(deposito);
                if (!retiros.equals("")) {
                    retiros = retiros.replace("$", "");
                    retiros = retiros.replace(",", "");
                } else {
                    retiros = "0.00";
                }
                double ret = Double.parseDouble(retiros);
                if (!saldo.equals("")) {
                    saldo = saldo.replace("$", "");
                    saldo = saldo.replace(",", "");
                } else {
                    saldo = "0.00";
                }
                double sald = Double.parseDouble(saldo);
                String tipoMovimiento = (dep > 0.00) ? "INGRESO" : "EGRESO";
                double mImporte = (dep > 0.00) ? dep : ret;
                valorReturn = guardaInformacion.insertaLineaBanorteRDB(conn, psInsert, banco, fechaCarga, tipoMovimiento, cuentaBancaria, dFechaOper, referencia, mImporte, sald, cuenta, descripcion, sucursal, dFechaOperV, codTransac, movimiento, desDetallada, usuario);
            }
        } catch (Exception e) {
            log.warn("Error Al Guardar Informacion RDB", e);
        }
        return valorReturn;
    }

    public String guardarScotiabank(Connection conn, PreparedStatement psInsert, String[] celdas, GuardarInformacionGeneral guardaInformacion, int lineaNumero, String banco, String fechaCarga, String cuentaBancaria, String usuario) throws SQLException {
        String valorReturn = "no_guardado";
        //System.out.println(celdas);
        //System.out.println("Numero de Encabezados: "+celdas.length);
        try {
            // Se quitaran los encabezado de txt
            if (celdas.length != 11 && lineaNumero == 1) {
                valorReturn = "columnasDiferentes";
            } else {
                String sCuenta = celdas[0].trim();
                String sPlaza = celdas[1].trim();
                String sMoneda = celdas[2].trim();
                String dFecha = celdas[3].trim();
                String sReferencia = celdas[4].trim();
                String mImporte = celdas[5].trim();
                String tipoMovimiento = celdas[6].trim();
                String transConcepto = celdas[7].trim();
                String saldo = celdas[8].trim();
                String leyenda1 = "";
                String leyenda2 = "";
                if (celdas.length > 9) {
                    leyenda1 = celdas[9];
                    leyenda2 = celdas[10];
                }
                //String leyenda1 = (celdas[9].length() > 0) ? celdas[9].trim() : "";
                //String leyenda2 = (celdas[10].length() > 0) ? celdas[10].trim() : "";
                tipoMovimiento = (tipoMovimiento.equals("ABONO")) ? "INGRESO" : "EGRESO";
                valorReturn = guardaInformacion.insertaLineaScotiabankRDB(conn, psInsert, banco, fechaCarga, tipoMovimiento, cuentaBancaria, sCuenta, sPlaza, sMoneda, dFecha, sReferencia, mImporte, transConcepto, saldo, leyenda1, leyenda2, usuario);
            }
        } catch (Exception e) {
            log.warn("Error Al Guardar Informacion RDB", e);
        }
        return valorReturn;
    }

    public String guardarBancomerCom(Connection conn, PreparedStatement psInsert, String[] celdas, GuardarInformacionGeneral guardaInformacion, int lineaNumero, String banco, String tipoBancomer, String fechaCarga, String cuentaBancaria, String usuario) throws SQLException {
        String valorReturn = "no_guardado";
        //System.out.println(celdas);
        //System.out.println("Numero de Encabezados: "+celdas.length);
        try {
            // Se quitaran los encabezado de txt
            if (celdas.length != 5 && lineaNumero == 1) {
                valorReturn = "columnasDiferentes";
            } else {
                String dFechaOper = celdas[0].trim();
                String referencia = celdas[1].trim();
                // Egreso
                String cargo = celdas[2].trim();
                // Ingreso
                String abono = celdas[3].trim();
                String saldo = celdas[4].trim();
                //String comillas = "/"/g;
                if (!abono.equals("")) {
                    abono = abono.replace("$", "");
                    abono = abono.replace(",", "");
                    abono = abono.replace("\"", "");
                } else {
                    abono = "0.00";
                }
                double dep = Double.parseDouble(abono);
                if (!cargo.equals("")) {
                    cargo = cargo.replace("$", "");
                    cargo = cargo.replace(",", "");
                    cargo = cargo.replace("\"", "");
                } else {
                    cargo = "0.00";
                }
                double ret = Double.parseDouble(cargo);
                if (!saldo.equals("")) {
                    saldo = saldo.replace("$", "");
                    saldo = saldo.replace(",", "");
                    saldo = saldo.replace("\"", "");
                } else {
                    saldo = "0.00";
                }
                double sald = Double.parseDouble(saldo);
                String tipoMovimiento = (dep > 0.00) ? "INGRESO" : "EGRESO";
                double mImporte = (dep > 0.00) ? dep : ret;
                valorReturn = guardaInformacion.insertaLineaBancomerComRDB(conn, psInsert, banco, tipoBancomer, fechaCarga, tipoMovimiento, cuentaBancaria, dFechaOper, referencia, mImporte, sald, usuario);
            }
        } catch (Exception e) {
            log.warn("Error Al Guardar Informacion RDB", e);
        }
        return valorReturn;
    }

    public String guardarBancomerCash(Connection conn, PreparedStatement psInsert, String sCadenas, GuardarInformacionGeneral guardaInformacion, int lineaNumero, String banco, String tipoBancomer, String fechaCarga, String cuentaBancaria, String usuario) throws SQLException {
        String valorReturn = "no_guardado";
        //System.out.println(celdas);
        //System.out.println("Numero de Encabezados: "+celdas.length);
        try {
            // Se quitaran los encabezado de txt
            if (sCadenas.length() != 162 && lineaNumero == 1) {
                valorReturn = "columnasDiferentes";
            } else {
                // desde 1, desde 0;
                String cuenta = sCadenas.substring(1, 18);
                String fechaV = sCadenas.substring(18, 28);
                String folioBan = sCadenas.substring(28, 34);
                String transaccion = sCadenas.substring(34, 64);
                String tipoMovi = sCadenas.substring(64, 65);
                String importe = sCadenas.substring(65, 81);
                String moneda = sCadenas.substring(81, 84);
                String folioAcep = sCadenas.substring(84, 93);
                String ref = sCadenas.substring(93, 123);
                String contrato = sCadenas.substring(123, 130);
                String fechaOper = sCadenas.substring(130, 140);
                String contratoCw = sCadenas.substring(140, 152);
                String codTrans = sCadenas.substring(152, 155);
                String tipoOper = sCadenas.substring(155, 158);
                String plaza = sCadenas.substring(158, 162);
                //String comillas = "/"/g;
                double importeD = 0.00;
                if (!importe.equals("")) {
                    importeD = Double.parseDouble(importe);
                }
                // si es 0 abono(Ingreso)
                String tipoMovimiento = (tipoMovi.equals("0")) ? "INGRESO" : "EGRESO";
                //double mImporte = (dep > 0.00) ? dep : ret;
                valorReturn = guardaInformacion.insertaLineaBancomerCashRDB(conn, psInsert, banco, tipoBancomer, fechaCarga, tipoMovimiento, cuentaBancaria, cuenta, fechaV, folioBan, transaccion, tipoMovi, importe, moneda, folioAcep, ref, contrato, fechaOper, contratoCw, codTrans, tipoOper, plaza);
            }
        } catch (Exception e) {
            log.warn("Error Al Guardar Informacion RDB", e);
        }
        return valorReturn;
    }
}
