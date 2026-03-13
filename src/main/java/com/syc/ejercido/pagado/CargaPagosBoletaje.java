package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaPagosBoletaje extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    private static final String[] ESTRUCTURA_ARCHIVO = new String[] { "RFC", "Nombre Pasajero", "Referencia", "Ruta", "Linea Aerea", "Q", "Tarifa", "IVA", "TUA", "YR", "Total", "Partida", "UnidadEjecutora", "Fecha de Salida", "Fecha de Regreso", "SERVICIO", "IVA SERVICIO", "GRAN TOTAL" };

    public CargaPagosBoletaje(String jniName) {
        super.init(jniName);
    }

    public String procesaArchivo(String fileName, int nFolioPago, String cCentroContable, String login, String nombreArchivoOriginal, BigDecimal totalPago, String cEsPago) throws Exception {
        Connection conn = null;
        String errores = "";
        try {
            conn = getConnection();
            if (!CargaPagosBoletajeManager.existeFolioPago(conn, nFolioPago)) {
                List<Vuelo> vuelos = validarArchivo(fileName);
                if ("S".equals(cEsPago)) {
                    if (validaTotalPago(vuelos, totalPago)) {
                        CargaPagosBoletajeManager.insertaVuelos(conn, vuelos, nFolioPago, cCentroContable, login, nombreArchivoOriginal);
                        conn.commit();
                    } else {
                        errores = "El importe Total de Vuelos es diferente al total del Pago. Verifique!!";
                    }
                } else {
                    int insertados = CargaPagosBoletajeManager.insertaLayoutVuelos(conn, vuelos, nFolioPago, cCentroContable, login, nombreArchivoOriginal);
                    if (insertados > 0) {
                        conn.commit();
                    } else {
                        errores = "error";
                    }
                }
            } else {
                errores = "El Folio de Pago: " + nFolioPago + " ya tiene cargado el archivo de vuelos.";
            }
            return errores;
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private List<Vuelo> validarArchivo(String fileName) throws Exception {
        List<Vuelo> vuelos = new ArrayList<CargaPagosBoletaje.Vuelo>();
        BufferedReader brr = null;
        InputStreamReader fileStream = null;
        String errores = "";
        int renglon = 0;
        try {
            fileStream = new InputStreamReader(new FileInputStream(fileName));
            brr = new BufferedReader(fileStream);
            String sCadenas = "";
            while ((sCadenas = brr.readLine()) != null) {
                renglon++;
                String[] celdas = sCadenas.split(",");
                String erroresRenglon = "";
                if (celdas.length != ESTRUCTURA_ARCHIVO.length)
                    throw new Exception("El numero de columnas en el archivo no es el esperado.");
                for (int i = 0; i < ESTRUCTURA_ARCHIVO.length; i++) {
                    if (StringUtils.isEmpty(celdas[i]))
                        erroresRenglon += "En el renglon " + renglon + " el valor de la columna [" + ESTRUCTURA_ARCHIVO[i] + "] esta vacio.\n";
                }
                if (!StringUtils.isEmpty(erroresRenglon)) {
                    errores += erroresRenglon;
                    continue;
                }
                for (int i = 6; i <= 11; i++) if (!esMontoCorrecto(celdas[i]))
                    erroresRenglon += "En el renglon " + renglon + " el campo " + ESTRUCTURA_ARCHIVO[i] + " no es numerico o es menor a cero.";
                for (int i = 15; i <= 17; i++) if (!esMontoCorrecto(celdas[i]))
                    erroresRenglon += "En el renglon " + renglon + " el campo " + ESTRUCTURA_ARCHIVO[i] + " no es numerico o es menor a cero.";
                if (!StringUtils.isEmpty(erroresRenglon)) {
                    errores += erroresRenglon;
                    continue;
                }
                // Si llega a esta linea, no hubo errores.
                Vuelo vuelo = new Vuelo();
                vuelo.setRFC(celdas[0].trim());
                vuelo.setcNombre(celdas[1].trim());
                vuelo.setcReferencia(celdas[2].trim());
                vuelo.setcRuta(celdas[3].trim());
                vuelo.setcLineaAerea(celdas[4].trim());
                // vuelo.setcClase(celdas[5].trim());
                vuelo.setmQ(new BigDecimal(celdas[5].trim()));
                vuelo.setmTarifa(new BigDecimal(celdas[6].trim().replace(",", "")));
                vuelo.setmIVA(new BigDecimal(celdas[7].trim().replace(",", "")));
                vuelo.setmTUA(new BigDecimal(celdas[8].trim().replace(",", "")));
                vuelo.setmYR(new BigDecimal(celdas[9].trim().replace(",", "")));
                vuelo.setmTotal(new BigDecimal(celdas[10].trim().replace(",", "")));
                vuelo.setcPartida(celdas[11].trim());
                vuelo.setcUnidadEjecutora(celdas[12].trim());
                vuelo.setMontoServicio(new BigDecimal(celdas[15].trim().replaceAll(",", "")));
                vuelo.setMontoIVAServicio(new BigDecimal(celdas[16].trim().replaceAll(",", "")));
                vuelo.setGranTotal(new BigDecimal(celdas[17].trim().replaceAll(",", "")));
                if (!celdas[13].trim().equals("N/A")) {
                    vuelo.setfFechaSalida(Util.stringToDate(celdas[13].trim(), "dd/MM/yyyy"));
                } else {
                    vuelo.setfFechaSalida(null);
                }
                if (!celdas[14].trim().equals("N/A")) {
                    vuelo.setfFechaRegreso(Util.stringToDate(celdas[14].trim(), "dd/MM/yyyy"));
                } else {
                    vuelo.setfFechaRegreso(null);
                }
                // vuelo.setcDFO(celdas[16].trim());
                vuelos.add(vuelo);
            }
            if (!StringUtils.isEmpty(errores))
                throw new Exception(errores);
            return vuelos;
        } finally {
            try {
                if (fileStream != null)
                    fileStream.close();
                if (brr != null)
                    brr.close();
            } catch (Exception e) {
                log.warn("Problemas cerrando archivo. " + e);
            } finally {
                brr = null;
                fileStream = null;
            }
        }
    }

    public class Vuelo {

        private String RFC;

        private String cNombre;

        private String cReferencia;

        private String cRuta;

        private String cLineaAerea;

        private String cClase;

        private BigDecimal mQ;

        private BigDecimal mTarifa;

        private BigDecimal mIVA;

        private BigDecimal mTUA;

        private BigDecimal mYR;

        private BigDecimal mTotal;

        private String cPartida;

        private String cUnidadEjecutora;

        private Date fFechaSalida;

        private Date fFechaRegreso;

        private String cDFO;

        private BigDecimal granTotal;

        private BigDecimal montoIVAServicio;

        private BigDecimal montoServicio;

        /**
         * @return the granTotal
         */
        public BigDecimal getGranTotal() {
            return granTotal;
        }

        @Override
        public String toString() {
            return "Vuelo [RFC=" + RFC + ", cNombre=" + cNombre + ", cReferencia=" + cReferencia + ", cRuta=" + cRuta + ", cLineaAerea=" + cLineaAerea + ", cClase=" + cClase + ", mQ=" + mQ + ", mTarifa=" + mTarifa + ", mIVA=" + mIVA + ", mTUA=" + mTUA + ", mYR=" + mYR + ", mTotal=" + mTotal + ", cPartida=" + cPartida + ", cUnidadEjecutora=" + cUnidadEjecutora + ", fFechaSalida=" + fFechaSalida + ", fFechaRegreso=" + fFechaRegreso + ", cDFO=" + cDFO + ", granTotal=" + granTotal + ", montoIVAServicio=" + montoIVAServicio + ", montoServicio=" + montoServicio + "]";
        }

        /**
         * @return the montoIVAServicio
         */
        public BigDecimal getMontoIVAServicio() {
            return montoIVAServicio;
        }

        /**
         * @return the montoServicio
         */
        public BigDecimal getMontoServicio() {
            return montoServicio;
        }

        public String getRFC() {
            return RFC;
        }

        public void setGranTotal(BigDecimal granTotal) {
            this.granTotal = granTotal;
        }

        public void setMontoIVAServicio(BigDecimal montoIVAServicio) {
            this.montoIVAServicio = montoIVAServicio;
        }

        public void setMontoServicio(BigDecimal montoServicio) {
            this.montoServicio = montoServicio;
        }

        public void setRFC(String rFC) {
            RFC = rFC;
        }

        public String getcNombre() {
            return cNombre;
        }

        public void setcNombre(String cNombre) {
            this.cNombre = cNombre;
        }

        public String getcReferencia() {
            return cReferencia;
        }

        public void setcReferencia(String cReferencia) {
            this.cReferencia = cReferencia;
        }

        public String getcRuta() {
            return cRuta;
        }

        public void setcRuta(String cRuta) {
            this.cRuta = cRuta;
        }

        public String getcLineaAerea() {
            return cLineaAerea;
        }

        public void setcLineaAerea(String cLineaAerea) {
            this.cLineaAerea = cLineaAerea;
        }

        public String getcClase() {
            return cClase;
        }

        public void setcClase(String cClase) {
            this.cClase = cClase;
        }

        public BigDecimal getmQ() {
            return mQ;
        }

        public void setmQ(BigDecimal mQ) {
            this.mQ = mQ;
        }

        public BigDecimal getmTarifa() {
            return mTarifa;
        }

        public void setmTarifa(BigDecimal mTarifa) {
            this.mTarifa = mTarifa;
        }

        public BigDecimal getmIVA() {
            return mIVA;
        }

        public void setmIVA(BigDecimal mIVA) {
            this.mIVA = mIVA;
        }

        public BigDecimal getmTUA() {
            return mTUA;
        }

        public void setmTUA(BigDecimal mTUA) {
            this.mTUA = mTUA;
        }

        public BigDecimal getmYR() {
            return mYR;
        }

        public void setmYR(BigDecimal mYR) {
            this.mYR = mYR;
        }

        public BigDecimal getmTotal() {
            return mTotal;
        }

        public void setmTotal(BigDecimal mTotal) {
            this.mTotal = mTotal;
        }

        public String getcPartida() {
            return cPartida;
        }

        public void setcPartida(String cPartida) {
            this.cPartida = cPartida;
        }

        public String getcUnidadEjecutora() {
            return cUnidadEjecutora;
        }

        public void setcUnidadEjecutora(String cUnidadEjecutora) {
            this.cUnidadEjecutora = cUnidadEjecutora;
        }

        public Date getfFechaSalida() {
            return fFechaSalida;
        }

        public void setfFechaSalida(Date fFechaSalida) {
            this.fFechaSalida = fFechaSalida;
        }

        public Date getfFechaRegreso() {
            return fFechaRegreso;
        }

        public void setfFechaRegreso(Date fFechaRegreso) {
            this.fFechaRegreso = fFechaRegreso;
        }

        public String getcDFO() {
            return cDFO;
        }

        public void setcDFO(String cDFO) {
            this.cDFO = cDFO;
        }
    }

    private boolean esMontoCorrecto(String s) {
        boolean correcto = true;
        try {
            BigDecimal numero = new BigDecimal(s);
            if (numero.compareTo(new BigDecimal(0.0d)) < 0)
                correcto = false;
        } catch (Exception e) {
            correcto = false;
        }
        return correcto;
    }

    private boolean validaTotalPago(List<Vuelo> vuelos, BigDecimal totalPago) {
        boolean correcto = true;
        BigDecimal totalVuelos = new BigDecimal(0.0d);
        DecimalFormat decimales = new DecimalFormat("0.00");
        try {
            for (Iterator<Vuelo> i = vuelos.iterator(); i.hasNext(); ) {
                Vuelo v = i.next();
                totalVuelos = totalVuelos.add(v.getmTotal());
            }
            totalVuelos = new BigDecimal(decimales.format(totalVuelos));
            if (!totalPago.equals(totalVuelos)) {
                correcto = false;
            }
        } catch (Exception e) {
            correcto = false;
        }
        return correcto;
    }
}
