package com.syc.contable.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.jfree.util.Log;
import java.util.Base64;

public class SaldosInicialesManager {

    public SaldosInicialesManager() {
    }

    public static boolean leerGenerales(Connection conn, InputStream in) throws FileNotFoundException, IOException {
        boolean primeraLinea = true;
        boolean insertados = false;
        List<String> mapa = new ArrayList<String>();
        List<String> lista = new ArrayList<String>();
        mapa.add("AÑO");
        mapa.add("ENTIDAD");
        mapa.add("CUENTA");
        mapa.add("NATURALEZA");
        mapa.add("NIVEL");
        mapa.add("SALDO_INICIAL");
        mapa.add("FECHA");
        mapa.add("DESCRIPCION");
        PreparedStatement SaldosIni = null;
        boolean insertado = false;
        ResultSet Rs = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String sCadena = "";
        Hashtable<String, Integer> nomColumna = new Hashtable<String, Integer>();
        try {
            while ((sCadena = br.readLine()) != null) {
                String[] llaves = sCadena.split(",");
                if (primeraLinea) {
                    for (int j = 0; j < llaves.length; j++) {
                        if (mapa.contains(llaves[j])) {
                            nomColumna.put(new String(llaves[j]), new Integer(j));
                        }
                    }
                    primeraLinea = false;
                } else {
                    //Query 1ra hoja
                    String[] celdas = sCadena.split(",");
                    Integer nFolioPoliza = Integer.parseInt(celdas[0]);
                    Integer nFolioDocPoliza = Integer.parseInt(celdas[1]);
                    String nCuenta = celdas[2];
                    String cCentroContable = celdas[3];
                    String cRamo = celdas[4];
                    String cEjercicio = celdas[5];
                    String cUnidadResponsable = celdas[6];
                    String cTipoPoliza = celdas[7];
                    String cDescripcionPoliza = celdas[8];
                    String fCreacion = celdas[9];
                    String fAplicacion = celdas[10];
                    Double mTotalCargo = Double.parseDouble(celdas[11]);
                    Double mTotalabono = Double.parseDouble(celdas[12]);
                    String qry = "INSERT INTO tDocPolizaEncabezado (nFolioPoliza,nFolioDocPoliza,nCuenta,cCentroContable,cRamo,cEjercicio,cUnidadResponsable,cTipoPoliza,cDescripcionPoliza,fCreacion,fAplicacion,mTotalCargo,mTotalabono)	VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                    SaldosIni = conn.prepareStatement(qry);
                    SaldosIni.setInt(1, nFolioPoliza);
                    SaldosIni.setInt(2, nFolioDocPoliza);
                    SaldosIni.setString(3, nCuenta);
                    SaldosIni.setString(4, cCentroContable);
                    SaldosIni.setString(5, cRamo);
                    SaldosIni.setString(6, cEjercicio);
                    SaldosIni.setString(7, cUnidadResponsable);
                    SaldosIni.setString(8, cTipoPoliza);
                    SaldosIni.setString(9, cDescripcionPoliza);
                    SaldosIni.setString(10, fCreacion);
                    SaldosIni.setString(11, fAplicacion);
                    SaldosIni.setDouble(12, mTotalCargo);
                    SaldosIni.setDouble(13, mTotalabono);
                    SaldosIni.executeQuery();
                    insertados = true;
                    //Query 2ra hoja
                    String[] celdasD = sCadena.split(",");
                    Integer nFolioDocPolizaD = Integer.parseInt(celdasD[0]);
                    String cCentroContableD = celdas[1];
                    String cEjercicioD = celdas[2];
                    String cEventoD = celdas[3];
                    String cCuentaContableD = celdas[4];
                    String cSubcuentaD = celdas[5];
                    String CTA1 = celdas[6];
                    String CTA2 = celdas[7];
                    String CTA3 = celdas[8];
                    String CTA4 = celdas[9];
                    Double mImporte = Double.parseDouble(celdas[10]);
                    String cDescripcionMovimiento = celdas[11];
                    String qryD = "INSERT INTO tDocPolizaDetalle (nFolioDocPoliza,cCentroContable,cEjercicio,cEvento,cCuentaContable,cSubcuenta,CTA1,CTA2,CTA3,CTA4,mImporte,cDescripcionMovimiento)	VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                    SaldosIni = conn.prepareStatement(qryD);
                    SaldosIni.setInt(1, nFolioDocPolizaD);
                    SaldosIni.setString(2, cCentroContableD);
                    SaldosIni.setString(3, cEjercicioD);
                    SaldosIni.setString(4, cEventoD);
                    SaldosIni.setString(5, cCuentaContableD);
                    SaldosIni.setString(6, cSubcuentaD);
                    SaldosIni.setString(7, CTA1);
                    SaldosIni.setString(8, CTA2);
                    SaldosIni.setString(9, CTA3);
                    SaldosIni.setString(10, CTA4);
                    SaldosIni.setDouble(11, mImporte);
                    SaldosIni.setString(12, cDescripcionMovimiento);
                    SaldosIni.executeQuery();
                    insertados = true;
                }
                //fin else
            }
            //fin while
        } catch (Exception exc) {
            Log.warn("Cerrando BufferedReader", exc);
        } finally {
            if (br != null) {
                br.close();
            }
            br = null;
        }
        //return listaArchivo;
        return insertados;
    }
}
