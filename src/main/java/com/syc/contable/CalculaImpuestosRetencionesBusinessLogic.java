package com.syc.contable;

import java.sql.Connection;
import java.util.List;
import com.syc.contable.core.CalculaImpuestosRetencionesManager;
import com.syc.contable.core.EgresoImpuestos;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.AmortizacionDetalle;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.PenaConvencional;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CalculaImpuestosRetencionesBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CalculaImpuestosRetencionesBusinessLogic.class);

    private String jniName;

    public CalculaImpuestosRetencionesBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    private int recalculaImpuestosRetenciones(String tipoPago, int nFolioPago) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
            EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso(tipoPago, nFolioPago);
            encabezado.setTipoPago(tipoPago);
            encabezado.setFolioPago(nFolioPago);
            List<EgresoDetalle> detalle = ebl.cargaDetalleEgreso(tipoPago, nFolioPago);
            List<EgresoCalendario> calendarioPago = ebl.cargaCalendarioEgreso(conn, tipoPago, nFolioPago);
            List<EgresoImpuestos> impuestos = ebl.cargaImpuestos(conn, tipoPago, nFolioPago);
            List<EgresoRetencion> retenciones = ebl.cargaRetenciones(conn, tipoPago, nFolioPago);
            List<PenaConvencional> penas = ebl.cargaPenas(conn, tipoPago, nFolioPago);
            int eliminados = EgresoDetalleManager.borraDetalle(conn, tipoPago, nFolioPago);
            log.info("Object: {}", "Se eliminaron " + eliminados + " renglones del detalle para tipo de pago " + tipoPago + " con folio " + nFolioPago);
            int resultado = CalculaImpuestosRetencionesManager.recalculaImpuestosRetenciones(conn, encabezado, detalle, calendarioPago, retenciones, impuestos, penas);
            Amortizacion amortizacion = encabezado.getAmortizacion(conn);
            if (amortizacion != null && Util.ZERO.compareTo(amortizacion.getMontoAmortizacion()) != 0) {
                List<AmortizacionDetalle> amortizaciones = ebl.generaDetalleAmortizacion(conn, tipoPago, nFolioPago, amortizacion, impuestos);
                resultado += CalculaImpuestosRetencionesManager.insertaAmortizacion(conn, resultado, encabezado, detalle, amortizaciones);
            }
            conn.commit();
            return resultado;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int recalculaMontoImpuestos(String tipoPago, int nFolioPago) throws Exception {
        log.trace("recalculaMontoImpuestos(): inicio");
        log.info("Object: {}", "Recalculando importes para el pago [" + tipoPago + "] folio [" + nFolioPago + "]");
        if ("pagodiverso".equalsIgnoreCase(tipoPago) || "pagoobra".equalsIgnoreCase(tipoPago)) {
            log.trace("Object: {}", "Tipo de pago [" + tipoPago + "] requiere recálculo con impuestos/retenciones específicos.");
            int resultado = recalculaImpuestosRetenciones(tipoPago, nFolioPago);
            log.info("Object: {}", "Recalculo completado por ruta especial. Registros afectados=" + resultado);
            log.trace("recalculaMontoImpuestos(): fin");
            return resultado;
        }
        log.trace("Object: {}", "Tipo de pago [" + tipoPago + "] no es especial. Calculando tablas dinámicas.");
        String tablaEncabezado = "t" + tipoPago + "encabezado";
        String tablaDetalle = "t" + tipoPago + "detalle";
        String nombreCampo = "nFolio" + tipoPago;
        log.debug("Object: " + String.valueOf("Tablas generadas -> Encabezado: " + tablaEncabezado + ", Detalle: " + tablaDetalle + ", Campo de enlace: " + nombreCampo + ", Folio: " + nFolioPago));
        log.trace("Invocando recalculo estándar de impuestos con tablas dinámicas...");
        int resultado = recalculaMontoImpuestos(tablaEncabezado, tablaDetalle, nombreCampo, nFolioPago);
        log.info("Object: {}", "Recalculo estándar completado. Registros afectados=" + resultado);
        log.trace("recalculaMontoImpuestos(): fin");
        return resultado;
    }

    public int recalculaMontoImpuestos(String tablaPagoEncabezado, String tablaPagoDetalle, int nFolioPago) {
        return 0;
    }

    public int recalculaMontoImpuestos(String tablaPagoEncabezado, String tablaPagoDetalle, String nombreCampo, int nFolioPago) throws Exception {
        long t0 = System.currentTimeMillis();
        log.trace("recalculaMontoImpuestos(encabezado, detalle, campo, folio): inicio");
        Connection conn = null;
        try {
            log.debug("Object: " + String.valueOf("Parámetros -> tablaPagoEncabezado=" + tablaPagoEncabezado + ", tablaPagoDetalle=" + tablaPagoDetalle + ", nombreCampo=" + nombreCampo + ", nFolioPago=" + nFolioPago));
            log.trace("Obteniendo conexión...");
            conn = getConnection();
            log.debug("Object: " + String.valueOf("Conexión obtenida: " + (conn != null ? conn.hashCode() : "null")));
            log.trace("Invocando CalculaImpuestosRetencionesManager.recalculaMontoImpuestos(...)");
            int resultado = CalculaImpuestosRetencionesManager.recalculaMontoImpuestos(conn, tablaPagoEncabezado, tablaPagoDetalle, nombreCampo, nFolioPago);
            log.info("Object: {}", "Recalculo de montos de impuestos completado. Registros afectados=" + resultado);
            log.trace("Realizando commit...");
            conn.commit();
            log.info("Commit exitoso.");
            log.trace("Object: {}", "recalculaMontoImpuestos(...): fin OK en " + (System.currentTimeMillis() - t0) + " ms");
            return resultado;
        } catch (Exception e) {
            log.error("Error en recalculaMontoImpuestos(...): " + e, e);
            if (conn != null) {
                try {
                    log.trace("Intentando rollback por error...");
                    conn.rollback();
                    log.info("Rollback realizado correctamente.");
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback. " + e2, e2);
                }
            }
            log.trace("Error occurred", "recalculaMontoImpuestos(...): fin con error en " + (System.currentTimeMillis() - t0) + " ms");
            throw e;
        } finally {
            try {
                CloseObject.closeObject(conn, false);
                log.debug("Conexión cerrada en finally.");
            } catch (Exception e3) {
                log.warn("Problemas cerrando la conexión en finally. " + e3, e3);
            }
        }
    }
}
