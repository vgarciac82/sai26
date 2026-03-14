package com.syc.contable.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.axtel.egresos.exceptions.EgresoException;
import com.syc.contable.anteproyecto.EPManager;
import com.syc.egresos.core.AmortizacionDetalle;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.PenaConvencional;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculaImpuestosRetencionesManager {

    public class Impuesto {

        private BigDecimal montoImpuesto = new BigDecimal(0);

        private BigDecimal porcentajeImpuesto = new BigDecimal(0);

        public Impuesto(BigDecimal porcentajeImpuesto, BigDecimal montoImpuesto) {
            super();
            this.porcentajeImpuesto = porcentajeImpuesto;
            this.montoImpuesto = montoImpuesto;
        }

        public BigDecimal getMontoImpuesto() {
            return montoImpuesto;
        }

        public BigDecimal getPorcentajeImpuesto() {
            return porcentajeImpuesto;
        }

        public void setMontoImpuesto(BigDecimal montoImpuesto) {
            this.montoImpuesto = montoImpuesto;
        }

        public void setPorcentajeImpuesto(BigDecimal porcentajeImpuesto) {
            this.porcentajeImpuesto = porcentajeImpuesto;
        }
    }

    private static final Map<String, String> campoMasIVA = InitCamposDB();

    private static final Logger log = LoggerFactory.getLogger(CalculaImpuestosRetencionesManager.class);

    private static BigDecimal[] actualizaIVARetenciones(Connection conn, int renglon, BigDecimal montoIVA, BigDecimal montoOtrosImpuestos, BigDecimal ivaAcumulado, BigDecimal otrosImpuestosAcumulado, PreparedStatement psUpdateDetalle, PreparedStatement psUpdateImpuestosDetalle, int nFolioPago) throws Exception {
        log.debug("Object: {}", "Iniciando calculo del IVA final para el renglon [" + renglon + "] Importe de IVA [" + montoIVA + "] IVA Acumulado[" + ivaAcumulado + "]");
        BigDecimal ivaResultado = montoIVA.subtract(ivaAcumulado).setScale(2, RoundingMode.HALF_UP);
        log.trace("Object: {}", "Monto de IVA restante[" + ivaResultado + "]");
        log.debug("Object: {}", "Iniciando calculo de Otros Impuestos final para el renglon [" + renglon + "] Importe de Otros Impuestos [" + montoOtrosImpuestos + "] Otros impuestos acumulado[" + otrosImpuestosAcumulado + "]");
        BigDecimal otrosImpuestosResultado = montoOtrosImpuestos.subtract(otrosImpuestosAcumulado).setScale(2, RoundingMode.HALF_UP);
        log.trace("Object: {}", "Monto de Otros Impuestos restante[" + otrosImpuestosResultado + "]");
        psUpdateDetalle.setBigDecimal(1, ivaResultado);
        psUpdateDetalle.setInt(2, nFolioPago);
        psUpdateDetalle.setInt(3, renglon);
        log.debug("Object: {}", "Actualizando renglon [" + renglon + "] con monto de IVA [" + ivaResultado + "] con folio [" + nFolioPago + "]");
        int afectados = psUpdateDetalle.executeUpdate();
        log.debug("Object: {}", "Se actualizaron [" + afectados + "] renglones");
        psUpdateImpuestosDetalle.setBigDecimal(1, otrosImpuestosResultado);
        psUpdateImpuestosDetalle.setInt(2, nFolioPago);
        psUpdateImpuestosDetalle.setInt(3, renglon);
        log.debug("Object: {}", "Actualizando renglon [" + renglon + "] con monto de Otros Impuestos [" + otrosImpuestosResultado + "] con folio [" + nFolioPago + "]");
        afectados = psUpdateImpuestosDetalle.executeUpdate();
        log.debug("Object: {}", "Se actualizaron [" + afectados + "] renglones");
        return new BigDecimal[] { ivaResultado, otrosImpuestosResultado };
    }

    private static BigDecimal[] actualizaIVARetencionesRenglon(Connection conn, int renglon, BigDecimal porcentajeImpuestos, BigDecimal importeMasIVA, BigDecimal porcentajeIVA, BigDecimal porcentajeOtrosImpuestos, PreparedStatement psUpdateDetalle, PreparedStatement psUpdateImpuestosDetalle, int folioPago) throws Exception {
        BigDecimal montoOtrosImpuestos = new BigDecimal("0.00");
        log.debug("Object: {}", "Iniciando calculo del IVA para el renglon [" + renglon + "] Importe con IVA (Renglon) [" + importeMasIVA + "] Porcentaje IVA [" + porcentajeIVA + "] Porcentaje Impuestos[" + porcentajeImpuestos + "]");
        log.trace("Object: {}", "Calculando total de impuestos del renglon [" + renglon + "]");
        BigDecimal dividendoTotalImpuestos = (new BigDecimal("1").add(porcentajeImpuestos)).setScale(8, RoundingMode.HALF_UP);
        log.trace("Object: {}", String.format("Porcentaje total impuestos [%f]", dividendoTotalImpuestos));
        BigDecimal totalSinImpuestosRenglon = importeMasIVA.divide(dividendoTotalImpuestos, 8, RoundingMode.HALF_UP);
        log.trace("Object: {}", String.format("Importe sin impuestos [%f]", totalSinImpuestosRenglon));
        BigDecimal dividendoIVA = porcentajeIVA.add(new BigDecimal(1));
        log.trace("Object: {}", "Porcentaje IVA [" + dividendoIVA + "]");
        BigDecimal montoIVA = totalSinImpuestosRenglon.multiply(porcentajeIVA).setScale(2, RoundingMode.HALF_UP);
        log.trace("Object: {}", "Monto de IVA[" + montoIVA + "]");
        montoOtrosImpuestos = importeMasIVA.setScale(2, RoundingMode.HALF_UP).subtract(totalSinImpuestosRenglon.setScale(2, RoundingMode.HALF_UP)).subtract(montoIVA.setScale(2, RoundingMode.HALF_UP));
        log.trace("Object: {}", "Monto de Otros Impuestos[" + montoOtrosImpuestos + "]");
        log.debug("Object: {}", "Actualizando renglon [" + renglon + "] con monto de IVA [" + montoIVA + "] con folio [" + folioPago + "]");
        psUpdateDetalle.setBigDecimal(1, montoIVA);
        psUpdateDetalle.setInt(2, folioPago);
        psUpdateDetalle.setInt(3, renglon);
        int afectados = psUpdateDetalle.executeUpdate();
        log.debug("Object: {}", "Se actualizaron [" + afectados + "] renglones");
        log.debug("Object: {}", "Actualizando renglon [" + renglon + "] con monto de Otros Impuestos [" + montoOtrosImpuestos + "] con folio [" + folioPago + "]");
        psUpdateImpuestosDetalle.setBigDecimal(1, montoOtrosImpuestos);
        psUpdateImpuestosDetalle.setInt(2, folioPago);
        psUpdateImpuestosDetalle.setInt(3, renglon);
        afectados = psUpdateImpuestosDetalle.executeUpdate();
        log.debug("Object: {}", "Se actualizaron [" + afectados + "] renglones");
        return new BigDecimal[] { montoIVA, montoOtrosImpuestos };
    }

    private static Impuesto calculaImpuestos(BigDecimal importeSinImpuestos, BigDecimal importeConImpuestos) {
        log.trace("Object: {}", "Calculando el porcentaje de Impuestos. Importe con Impuestos[" + importeConImpuestos + "] Importe sin impuestos[" + importeSinImpuestos + "] ");
        Impuesto impuestos = null;
        CalculaImpuestosRetencionesManager cirm = new CalculaImpuestosRetencionesManager();
        BigDecimal porcentajeImpuestos = new BigDecimal(0);
        BigDecimal totalImpuestos = importeConImpuestos.subtract(importeSinImpuestos);
        if (totalImpuestos.compareTo(new BigDecimal(0)) == 0) {
            impuestos = cirm.new Impuesto(new BigDecimal(0), new BigDecimal(0));
        } else {
            porcentajeImpuestos = totalImpuestos.divide(importeSinImpuestos, 8, RoundingMode.HALF_UP);
            impuestos = cirm.new Impuesto(porcentajeImpuestos, totalImpuestos.setScale(2, RoundingMode.HALF_UP));
        }
        log.debug("Object: {}", "Importe de Otros Impuestos [" + totalImpuestos + "] Porcentaje de Otros Impuestos [" + porcentajeImpuestos + "]");
        return impuestos;
    }

    public static int calculaImpuestosRetenciones(Connection conn, EgresoEncabezado encabezado, List<EgresoCalendario> calendarioPago, List<EgresoRetencion> retenciones, List<EgresoImpuestos> impuestos) throws Exception {
        int insertados = 0;
        List<EgresoDetalle> egresoDetalleNuevo = new ArrayList<EgresoDetalle>();
        BigDecimal pcIVA = null;
        pcIVA = (impuestos.size() > 0 ? impuestos.get(0).getPorcentajeIVA() : new BigDecimal("0.00"));
        BigDecimal pcOtrosImpuestos = null;
        pcOtrosImpuestos = (impuestos.size() > 0 ? impuestos.get(0).getPorcentajeOtrosImpuestos() : new BigDecimal("0.00"));
        /*
		 * Se calendariza las retenciones
		 */
        for (EgresoRetencion retencion : retenciones) {
            BigDecimal montoRetencion = retencion.getImporteRetencion();
            List<SaldoMensual> calendarioRetencion = SaldoManager.calendarioSaldoRetencion(calendarioPago, montoRetencion);
            List<EgresoDetalle> detalleRetenciones = EgresosManager.generaDetalleRetencion(conn, calendarioRetencion, retencion, encabezado, pcIVA, pcOtrosImpuestos);
            if (detalleRetenciones != null && detalleRetenciones.size() > 0)
                egresoDetalleNuevo.addAll(detalleRetenciones);
        }
        /*
		 * Por ultimo se calendariza el neto
		 */
        for (EgresoCalendario importeDetalle : calendarioPago) {
            BigDecimal montoMes = importeDetalle.getImporteBruto();
            if (montoMes.compareTo(Util.ZERO) > 0) {
                EgresoDetalle detalleNeto = EgresoDetalleManager.generaDetalle(conn, importeDetalle, encabezado, pcIVA, pcOtrosImpuestos);
                egresoDetalleNuevo.add(detalleNeto);
            }
        }
        int renglon = 1;
        /* Se inserta el nuevo detalle */
        for (EgresoDetalle renglonDet : egresoDetalleNuevo) {
            renglonDet.setcIdRelacion(encabezado.getNombre());
            renglonDet.setNumeroRenglon(renglon);
            renglonDet.setCentroContable(encabezado.getCentroContable());
            renglonDet.setcIdEntidadContable("00");
            renglonDet.setNumeroMes(Util.getCurrentMonth(conn));
            renglonDet.setEjercicioFiscal(encabezado.getEjercicioFiscal());
            insertados = insertados + renglonDet.insertaRenglon(conn);
            renglon++;
        }
        return insertados;
    }

    public static int calculaImpuestosRetencionesRG(Connection conn, EgresoEncabezado encabezado, List<EgresoCalendarioRG> calendarioPago, List<EgresoImpuestos> impuestos) throws Exception {
        int insertados = 0;
        List<EgresoDetalle> egresoDetalleNuevo = new ArrayList<EgresoDetalle>();
        BigDecimal pcIVA = null;
        pcIVA = (impuestos.size() > 0 ? impuestos.get(0).getPorcentajeIVA() : new BigDecimal("0.00"));
        BigDecimal pcOtrosImpuestos = null;
        pcOtrosImpuestos = (impuestos.size() > 0 ? impuestos.get(0).getPorcentajeOtrosImpuestos() : new BigDecimal("0.00"));
        for (EgresoCalendarioRG importeDetalle : calendarioPago) {
            BigDecimal montoMes = importeDetalle.getImporteBruto();
            if (montoMes.compareTo(Util.ZERO) > 0) {
                EgresoDetalle detalleNeto = EgresoDetalleManager.generaDetalleRG(conn, importeDetalle, encabezado, pcIVA, pcOtrosImpuestos);
                egresoDetalleNuevo.add(detalleNeto);
            }
        }
        int renglon = 1;
        /* Se inserta el nuevo detalle */
        for (EgresoDetalle renglonDet : egresoDetalleNuevo) {
            renglonDet.setNumeroRenglon(renglon);
            insertados = insertados + renglonDet.insertaRenglon(conn);
            renglon++;
        }
        return insertados;
    }

    private static Impuesto calculaOtrosImpuestos(BigDecimal importeSinIVA, BigDecimal otrosImpuestos) {
        log.trace("Object: {}", "Calculando el porcentaje de Otros Impuestos. Importe de Otros Impuestos[" + importeSinIVA + "] Importe sin Impuestos[" + otrosImpuestos + "]");
        CalculaImpuestosRetencionesManager cirm = new CalculaImpuestosRetencionesManager();
        if (importeSinIVA.setScale(2).compareTo(new BigDecimal(0).setScale(2)) == 0)
            return cirm.new Impuesto(new BigDecimal(0), new BigDecimal(0));
        BigDecimal porcentajeOtrosImpuestos = importeSinIVA.divide(otrosImpuestos, 8, RoundingMode.HALF_UP);
        Impuesto otros = cirm.new Impuesto(porcentajeOtrosImpuestos, importeSinIVA.setScale(2, RoundingMode.HALF_UP));
        log.debug("Object: {}", "Importe de Otros Impuestos [" + importeSinIVA + "] Porcentaje de Otros Impuestos [" + porcentajeOtrosImpuestos + "]");
        return otros;
    }

    public static synchronized Map<String, String> InitCamposDB() {
        if (campoMasIVA == null) {
            Map<String, String> campos = new HashMap<String, String>();
            campos.put("TPAGOFEDERALIZADOENCABEZADO", "mimportemasiva");
            campos.put("TPAGODIRECTOENCABEZADO", "mImporteNeto");
            campos.put("tRELACIONGASTOSEncabezado", "mImporteMasIva");
            campos.put("TPAGOFEDERALIZADODETALLE", "mIVA");
            campos.put("TPAGODIRECTODETALLE", "mImporteIva");
            campos.put("tRELACIONGASTOSDetalle", "mIVA");
            return campos;
        } else {
            return campoMasIVA;
        }
    }

    public static int insertaAmortizacion(Connection conn, int renglonInicio, EgresoEncabezado encabezado, List<EgresoDetalle> detalle, List<AmortizacionDetalle> amortizaciones) throws Exception {
        int insertados = 0;
        /* Se toma un renglon del detalle para tomarlo de muestra */
        EgresoDetalle muestra = detalle.get(0);
        if (amortizaciones != null && amortizaciones.size() > 0)
            for (AmortizacionDetalle amortizacion : amortizaciones) {
                BigDecimal montoAmortizacion = amortizacion.getImporteAmortizacion();
                EgresoDetalle renglon = muestra.renglonNuevo();
                renglon.setNumeroRenglon(renglonInicio);
                renglon.setEp(amortizacion.getEP());
                renglon.setImporteAmortizacionAnticipo(montoAmortizacion);
                renglon.setImporteAmortiza(montoAmortizacion);
                renglon.setRfc(muestra.getRfc());
                renglon.setImporteComprometido(montoAmortizacion);
                renglon.setImporteNeto(new BigDecimal(0.00d));
                renglon.setImporteBruto(amortizacion.getImporteBruto());
                renglon.setImporteMasIva(new BigDecimal(0.00d));
                renglon.setImporteIva(amortizacion.getPorcentajeIVA());
                renglon.setCapitulo(EPManager.getComponente(amortizacion.getEP(), "CAPITULO"));
                renglon.setImporteRetencion(Util.ZERO);
                renglon.setImporteImporteNegativo(montoAmortizacion.multiply(new BigDecimal(-1.00)));
                renglon.setIva(amortizacion.getImporteImpuestos());
                renglon.setObgt(EPManager.getComponente(amortizacion.getEP(), "CAPITULO"));
                renglon.setMesCalendario(String.valueOf(amortizacion.getMesAmortiza()));
                renglon.setEvento("D_" + EgresoDetalleManager.calculaEvento(conn, encabezado, renglon));
                insertados += renglon.insertaRenglon(conn);
            }
        return insertados;
    }

    public static int recalculaImpuestosRetenciones(Connection conn, EgresoEncabezado encabezado, List<EgresoDetalle> detalle, List<EgresoCalendario> calendarioPago, List<EgresoRetencion> retenciones, List<EgresoImpuestos> impuestos, List<PenaConvencional> penas) throws EgresoException {
        List<EgresoDetalle> egresoDetalleNuevo = new ArrayList<EgresoDetalle>();
        /* Se toma un renglon del detalle para tomarlo de muestra */
        EgresoDetalle muestra = detalle.get(0);
        BigDecimal pcIVA = impuestos.get(0).getPorcentajeIVA();
        BigDecimal pcOtrosImpuestos = impuestos.get(0).getPorcentajeOtrosImpuestos();
        /*
		 * Primero calendariza las penas,
		 */
        if (penas != null && penas.size() > 0)
            for (PenaConvencional pena : penas) {
                BigDecimal montoSancion = pena.getImporteSancionNeto();
                List<SaldoMensual> calendarioPenas = SaldoManager.calendarioSaldoRetencion(calendarioPago, montoSancion);
                List<EgresoDetalle> detallePenas = EgresosManager.generaDetallePenas(conn, calendarioPenas, pena, encabezado, muestra, pcIVA, pcOtrosImpuestos);
                if (detallePenas != null && detallePenas.size() > 0)
                    egresoDetalleNuevo.addAll(detallePenas);
            }
        /*
		 * posterior se calendariza las retenciones
		 */
        for (EgresoRetencion retencion : retenciones) {
            BigDecimal montoRetencion = retencion.getImporteRetencion();
            List<SaldoMensual> calendarioRetencion = SaldoManager.calendarioSaldoRetencion(calendarioPago, montoRetencion);
            List<EgresoDetalle> detalleRetenciones = EgresosManager.generaDetalleRetencion(conn, calendarioRetencion, retencion, encabezado, muestra, pcIVA, pcOtrosImpuestos);
            if (detalleRetenciones != null && detalleRetenciones.size() > 0)
                egresoDetalleNuevo.addAll(detalleRetenciones);
        }
        /*
		 * por ultimo se calendariza el neto
		 */
        for (EgresoCalendario importeDetalle : calendarioPago) {
            BigDecimal montoMes = importeDetalle.getImporteBruto();
            if (montoMes.compareTo(Util.ZERO) > 0) {
                EgresoDetalle detalleNeto = EgresoDetalleManager.generaDetalle(conn, importeDetalle, encabezado, muestra, pcIVA, pcOtrosImpuestos);
                egresoDetalleNuevo.add(detalleNeto);
            }
        }
        int renglon = 1;
        /* Se inserta el nuevo detalle */
        for (EgresoDetalle renglonDet : egresoDetalleNuevo) {
            renglonDet.setNumeroRenglon(renglon);
            try {
                renglonDet.insertaRenglon(conn);
            } catch (Exception e) {
                throw new EgresoException(e);
            }
            renglon++;
        }
        return renglon;
    }

    public static int recalculaMontoImpuestos(Connection conn, String tablaPagoEncabezado, String tablaPagoDetalle, String nombreCampo, int nFolioPago) throws Exception {
        final long t0 = System.currentTimeMillis();
        log.trace("Object: {}", String.format("Iniciando recalculo de detalle. Tabla Encabezado[%s] Tabla Detalle [%s]  Campo Llave[%s] Folio[%d]", tablaPagoEncabezado, tablaPagoDetalle, nombreCampo, nFolioPago));
        final String ncImporteMasIVAEnc = (CalculaImpuestosRetencionesManager.campoMasIVA.get(tablaPagoEncabezado.toUpperCase()) == null ? "mImporteMasIVA" : CalculaImpuestosRetencionesManager.campoMasIVA.get(tablaPagoEncabezado.toUpperCase()));
        final String ncImporteIVADet = (CalculaImpuestosRetencionesManager.campoMasIVA.get(tablaPagoDetalle.toUpperCase()) == null ? "mIVA" : CalculaImpuestosRetencionesManager.campoMasIVA.get(tablaPagoDetalle.toUpperCase()));
        log.debug("Object: {}", "Column mapping -> Encabezado.masIVA=" + ncImporteMasIVAEnc + " | Detalle.IVA=" + ncImporteIVADet);
        final String queryTotalRenglones = "SELECT COUNT(*) AS renglones FROM " + tablaPagoDetalle + " WITH(NOLOCK) WHERE " + nombreCampo + " = ?";
        final String queryEncabezadoPago = "SELECT " + nombreCampo + ", mimportebruto AS importeSinIVA, mimporteiva AS pctIVA, " + ncImporteMasIVAEnc + " AS importeMasIVA, motrosimpuestos AS otrosImpuestos " + "FROM " + tablaPagoEncabezado + " WITH(NOLOCK) WHERE " + nombreCampo + " = ?";
        final String queryDetallePago = "SELECT " + nombreCampo + ", mimportebruto, mimporteiva, mimporteneto, mimportemasiva, " + "motrosimpuestos, " + ncImporteIVADet + ", nDocRenglon " + "FROM " + tablaPagoDetalle + " WITH(NOLOCK) WHERE " + nombreCampo + " = ?";
        final String queryUpdateIVADetalle = "UPDATE " + tablaPagoDetalle + " SET " + ncImporteIVADet + " = ? WHERE " + nombreCampo + " = ? AND nDocRenglon = ?";
        final String queryUpdateImpuestosDetalle = "UPDATE " + tablaPagoDetalle + " SET motrosimpuestos = ? WHERE " + nombreCampo + " = ? AND nDocRenglon = ?";
        PreparedStatement psRenglones = null;
        PreparedStatement psEncabezadoPago = null;
        PreparedStatement psUpdateIVADetalle = null;
        PreparedStatement psUpdateImpuestosDetalle = null;
        PreparedStatement psDetallePago = null;
        ResultSet rsRenglones = null;
        ResultSet rsEncabezadoPago = null;
        ResultSet rsDetallePago = null;
        int renglones = 0;
        BigDecimal importeSinImpuestos = BigDecimal.ZERO;
        BigDecimal porcentajeIVA = BigDecimal.ZERO;
        BigDecimal importeMasImpuestos = BigDecimal.ZERO;
        BigDecimal otrosImpuestos = BigDecimal.ZERO;
        try {
            // Preparación de statements
            psRenglones = conn.prepareStatement(queryTotalRenglones);
            psUpdateIVADetalle = conn.prepareStatement(queryUpdateIVADetalle);
            psUpdateImpuestosDetalle = conn.prepareStatement(queryUpdateImpuestosDetalle);
            psEncabezadoPago = conn.prepareStatement(queryEncabezadoPago);
            psDetallePago = conn.prepareStatement(queryDetallePago);
            // Conteo de renglones detalle
            log.trace("Object: {}", "Se ejecutará la consulta de conteo [" + queryTotalRenglones + "] [" + nFolioPago + "]");
            psRenglones.setInt(1, nFolioPago);
            rsRenglones = psRenglones.executeQuery();
            if (!rsRenglones.next()) {
                throw new Exception("No se pudo obtener conteo de renglones en [" + tablaPagoDetalle + "] para folio [" + nFolioPago + "]");
            }
            renglones = rsRenglones.getInt(1);
            log.trace("Object: {}", "El detalle cuenta con [" + renglones + "] renglones");
            if (renglones <= 0) {
                throw new Exception("No se encuentra detalle en [" + tablaPagoDetalle + "] con el folio [" + nFolioPago + "]");
            }
            // Encabezado
            log.trace("Object: {}", "Se ejecutará consulta de encabezado [" + queryEncabezadoPago + "] [" + nFolioPago + "]");
            psEncabezadoPago.setInt(1, nFolioPago);
            rsEncabezadoPago = psEncabezadoPago.executeQuery();
            if (!rsEncabezadoPago.next()) {
                throw new Exception("No se encuentra encabezado en [" + tablaPagoEncabezado + "] con el folio [" + nFolioPago + "]");
            }
            // Defensas por nulos
            importeSinImpuestos = safeBD(rsEncabezadoPago.getBigDecimal("importeSinIVA"));
            porcentajeIVA = safeBD(rsEncabezadoPago.getBigDecimal("pctIVA"));
            importeMasImpuestos = safeBD(rsEncabezadoPago.getBigDecimal("importeMasIVA"));
            otrosImpuestos = safeBD(rsEncabezadoPago.getBigDecimal("otrosImpuestos"));
            log.trace("Object: {}", "Importe sin Impuestos[" + importeSinImpuestos + "]");
            log.trace("Object: {}", "Porcentaje IVA[" + porcentajeIVA + "]");
            log.trace("Object: {}", "Importe más Impuestos[" + importeMasImpuestos + "]");
            log.trace("Object: {}", "Otros Impuestos[" + otrosImpuestos + "]");
            // Cálculos
            CalculaImpuestosRetencionesManager cirm = new CalculaImpuestosRetencionesManager();
            Impuesto impuestos = calculaImpuestos(importeSinImpuestos, importeMasImpuestos);
            Impuesto otros = calculaOtrosImpuestos(otrosImpuestos, importeSinImpuestos);
            Impuesto iva = cirm.new Impuesto(impuestos.getPorcentajeImpuesto().subtract(otros.getPorcentajeImpuesto()), importeMasImpuestos.subtract(importeSinImpuestos).subtract(otros.getMontoImpuesto()));
            log.debug("Object: {}", String.format("Porcentaje de Impuestos [%s] Monto[%s]", impuestos.getPorcentajeImpuesto(), impuestos.getMontoImpuesto()));
            log.debug("Object: {}", String.format("Porcentaje de Otros Impuestos [%s] Monto[%s]", otros.getPorcentajeImpuesto(), otros.getMontoImpuesto()));
            log.debug("Object: {}", String.format("Porcentaje de IVA [%s] Monto[%s]", iva.getPorcentajeImpuesto(), iva.getMontoImpuesto()));
            // Detalle
            log.trace("Object: {}", "Ejecutando detalle [" + queryDetallePago + "][" + nFolioPago + "]");
            psDetallePago.setInt(1, nFolioPago);
            rsDetallePago = psDetallePago.executeQuery();
            int cntRenglon = -1;
            BigDecimal ivaAcumulado = BigDecimal.ZERO;
            BigDecimal otrosImpuestosAcumulado = BigDecimal.ZERO;
            while (rsDetallePago.next()) {
                cntRenglon++;
                BigDecimal ivaRenglon = safeBD(rsDetallePago.getBigDecimal("mimportemasiva"));
                int renglon = rsDetallePago.getInt("nDocRenglon");
                if (log.isTraceEnabled()) {
                    log.trace("Object: {}", "Procesando renglon=" + renglon + " (idx=" + cntRenglon + "/" + (renglones - 1) + "), ivaRenglon=" + ivaRenglon);
                }
                if (cntRenglon < (renglones - 1)) {
                    BigDecimal[] calc = actualizaIVARetencionesRenglon(conn, renglon, impuestos.getPorcentajeImpuesto(), ivaRenglon, iva.getPorcentajeImpuesto(), otros.getPorcentajeImpuesto(), psUpdateIVADetalle, psUpdateImpuestosDetalle, nFolioPago);
                    ivaAcumulado = ivaAcumulado.add(calc[0]);
                    otrosImpuestosAcumulado = otrosImpuestosAcumulado.add(calc[1]);
                    continue;
                }
                BigDecimal[] calcUltimo = actualizaIVARetenciones(conn, renglon, iva.montoImpuesto, otros.getMontoImpuesto(), ivaAcumulado, otrosImpuestosAcumulado, psUpdateIVADetalle, psUpdateImpuestosDetalle, nFolioPago);
                ivaAcumulado = ivaAcumulado.add(calcUltimo[0]);
                otrosImpuestosAcumulado = otrosImpuestosAcumulado.add(calcUltimo[1]);
            }
            log.info("Object: {}", "Recalculo de IVA, Impuestos y retenciones terminado. IVA[" + ivaAcumulado + "] Impuestos[" + otrosImpuestosAcumulado + "]");
            log.info("Object: {}", "Se modificaron [" + renglones + "] renglones del pago [" + nFolioPago + "] en la tabla [" + tablaPagoDetalle + "]");
            log.trace("Object: {}", "Fin OK en " + (System.currentTimeMillis() - t0) + " ms");
            return renglones;
        } finally {
            try {
                CloseObject.closeObject(psRenglones, false);
            } catch (Exception e) {
                log.warn("Cerrando psRenglones: " + e, e);
            }
            try {
                CloseObject.closeObject(psEncabezadoPago, false);
            } catch (Exception e) {
                log.warn("Cerrando psEncabezadoPago: " + e, e);
            }
            try {
                CloseObject.closeObject(psUpdateIVADetalle, false);
            } catch (Exception e) {
                log.warn("Cerrando psUpdateIVADetalle: " + e, e);
            }
            try {
                CloseObject.closeObject(psUpdateImpuestosDetalle, false);
            } catch (Exception e) {
                log.warn("Cerrando psUpdateImpuestosDetalle: " + e, e);
            }
            try {
                CloseObject.closeObject(psDetallePago, false);
            } catch (Exception e) {
                log.warn("Cerrando psDetallePago: " + e, e);
            }
            try {
                CloseObject.closeObject(rsRenglones, false);
            } catch (Exception e) {
                log.warn("Cerrando rsRenglones: " + e, e);
            }
            try {
                CloseObject.closeObject(rsEncabezadoPago, false);
            } catch (Exception e) {
                log.warn("Cerrando rsEncabezadoPago: " + e, e);
            }
            try {
                CloseObject.closeObject(rsDetallePago, false);
            } catch (Exception e) {
                log.warn("Cerrando rsDetallePago: " + e, e);
            }
        }
    }

    /**
     * Devuelve BigDecimal.ZERO si val es null.
     */
    private static BigDecimal safeBD(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }
}
