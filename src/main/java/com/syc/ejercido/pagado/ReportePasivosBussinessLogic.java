package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportePasivosBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    /**
     * H.A. Carga Layout Relacion Gastos Masivo  **
     */
    /* Valida Informacion de Layout Relacion Gastos Masivo */
    public String validarInformacion(InputStream in, String fAplicacion, String ur, String cEjecicicioFiscal, String login, String mes, String cContable) throws FileNotFoundException {
        List<String> mapa = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String valorReturn = "";
        String sCadena = "";
        String descripcion = "";
        int fTemp = 0;
        int fTempGral = 0;
        String rfc = "", valorRfc = "", guardaEnc = "", estatus = "", mesP = "", valorMes = "", mImporteNetoEnct, pc = "";
        double mImporteNetoEnc = 0.00;
        Connection conn = null;
        try {
            conn = getConnection();
            ReportePasivosManager validaInformacion = new ReportePasivosManager();
            // Tomar Folio Encabezado
            fTemp = validaInformacion.folioTemp(conn);
            if (fTempGral == 0) {
                fTempGral = fTemp;
            }
            while ((sCadena = br.readLine()) != null) {
                estatus = "";
                String[] celdas = sCadena.split(",");
                if (!celdas[0].trim().equals("RFC")) {
                    rfc = celdas[0].trim();
                    pc = celdas[1].trim();
                    valorRfc = validaInformacion.validaRfc(conn, rfc, pc);
                    mesP = celdas[2].trim();
                    valorMes = validaInformacion.validaMes(conn, rfc, pc, mesP);
                    mImporteNetoEnct = celdas[3].trim();
                    if (mImporteNetoEnct.equals("")) {
                        estatus = estatus + " Campo del importe esta vacio - ";
                        descripcion = "Detalle";
                    }
                    try {
                        mImporteNetoEnc = Double.parseDouble(mImporteNetoEnct);
                    } catch (Exception num) {
                        estatus = estatus + " Campo importe no es numerico - ";
                        descripcion = "Detalle";
                    }
                    // Validacion si el RFC Existe o es correcto
                    if (valorRfc.equals("noExiste")) {
                        estatus = estatus + " RFC no existe como pasivo contingente laboral- ";
                        descripcion = "Detalle";
                    }
                    if (valorMes.equals("existe")) {
                        estatus = estatus + " Ya se encuentra capturado el mes para el RFC - " + rfc;
                        descripcion = "Detalle";
                    }
                    guardaEnc = validaInformacion.insertaTemp(conn, rfc, pc, mesP, mImporteNetoEnc, fAplicacion, ur, cEjecicicioFiscal, login, estatus, fTempGral);
                }
            }
            conn.commit();
            valorReturn = String.valueOf(descripcion + ":" + fTempGral);
        } catch (Exception se) {
            log.error("Error occurred", "Error: " + se);
            se.printStackTrace();
            valorReturn = "Error:Archivo" + se;
            try {
                conn.rollback();
            } catch (Exception e) {
                log.error("Error occurred", "Error al hacer Rollback: " + se);
            }
        }
        conn = null;
        System.out.println("valorReturn:" + valorReturn);
        return valorReturn;
    }

    public String aplicarInformacion(String folio, String fAplicacion, String ur, String cEjecicicioFiscal, String login, String mes, String cContable) throws SQLException {
        String valorReturn = "";
        Connection conn = null;
        ReportePasivosManager validaInformacion = new ReportePasivosManager();
        try {
            conn = getConnection();
            valorReturn = validaInformacion.insertaPasivosLaborales(conn, folio);
            conn.commit();
        } catch (Exception se) {
            log.error("Error occurred", "Error: " + se);
            se.printStackTrace();
            valorReturn = "Error:Archivo" + se;
            try {
                conn.rollback();
            } catch (Exception e) {
                log.error("Error occurred", "Error al hacer Rollback: " + se);
            }
        }
        conn = null;
        System.out.println("valorReturn:" + valorReturn);
        return valorReturn;
    }
}
