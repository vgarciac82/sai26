package com.syc.egresos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.SaldosBussinessLogic;
import com.syc.contable.core.SaldoManager;
import com.syc.contable.core.SaldoMensual;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.core.CalendarioPago;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.PagoCalendarioManager;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagoCalendarioBussinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(PagoCalendarioBussinessLogic.class);

    private EgresosBusinessLogic ebl;

    private String jniName;

    private SaldosBussinessLogic sbl;

    public PagoCalendarioBussinessLogic() {
        ebl = new EgresosBusinessLogic();
        sbl = new SaldosBussinessLogic();
    }

    public PagoCalendarioBussinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
        ebl = new EgresosBusinessLogic(jniName);
        sbl = new SaldosBussinessLogic(jniName);
    }

    public EgresosBusinessLogic getEbl() {
        return ebl;
    }

    private int insertaCalendarioPago(Connection conn, DetallePago detallePago, Map<Integer, SaldoMensual> saldos) throws Exception {
        log.trace("insertaCalendarioPago(): inicio");
        int mesInicio = 1;
        int tope = 0;
        int mesActual = Util.getCurrentMonth(conn);
        int insertados = 0;
        mesInicio = mesActual;
        BigDecimal montoPorCubrir = detallePago.getImporteBruto();
        boolean continuar = true;
        log.debug("Object: {}", "Parámetros: ep=" + detallePago.getEp() + ", folioPago=" + detallePago.getFolioPago() + ", tipoPago=" + detallePago.getTipoPago() + ", idTipoConcepto=" + detallePago.getIdTipoConcepto() + ", idTipoMovimiento=" + detallePago.getIdTipoMovimiento() + ", importeBruto=" + detallePago.getImporteBruto() + ", importeRetencion=" + detallePago.getImporteRetencion() + ", mesActual=" + mesActual + ", tope=" + tope + ", montoPorCubrir=" + montoPorCubrir);
        CalendarioPago calendario = new CalendarioPago();
        calendario.setTipoPago(detallePago.getTipoPago());
        calendario.setFolioPago(detallePago.getFolioPago());
        calendario.setEp(detallePago.getEp());
        calendario.setImporteBruto(detallePago.getImporteBruto());
        calendario.setIdTipoConcepto(detallePago.getIdTipoConcepto());
        calendario.setIdTipoMovimiento(detallePago.getIdTipoMovimiento());
        calendario.setImporteRetencion(detallePago.getImporteRetencion());
        while (continuar) {
            calendario.setMes(mesInicio);
            SaldoMensual saldoMes = saldos.get(mesInicio);
            BigDecimal montoSaldoMes = (saldoMes != null && saldoMes.getMontoSaldo() != null) ? saldoMes.getMontoSaldo() : BigDecimal.ZERO;
            log.trace("Object: {}", "Iteración: mes=" + mesInicio + ", saldoMes=" + montoSaldoMes + ", montoPorCubrir=" + montoPorCubrir);
            if (montoSaldoMes.compareTo(BigDecimal.ZERO) > 0) {
                if (montoPorCubrir.compareTo(montoSaldoMes) <= 0) {
                    log.trace("Caso: saldo del mes cubre el monto restante.");
                    calendario.setImporteBrutoMes(montoPorCubrir);
                    saldoMes.setMontoSaldo(montoSaldoMes.subtract(montoPorCubrir));
                    log.debug("Object: {}", "Actualización saldo mes " + mesInicio + ": nuevoSaldo=" + saldoMes.getMontoSaldo());
                    montoPorCubrir = BigDecimal.ZERO;
                    continuar = false;
                } else {
                    log.trace("Caso: saldo del mes NO cubre, se toma todo el mes y se continúa.");
                    calendario.setImporteBrutoMes(montoSaldoMes);
                    montoPorCubrir = montoPorCubrir.subtract(montoSaldoMes);
                    saldoMes.setMontoSaldo(BigDecimal.ZERO);
                    log.debug("Object: {}", "Actualización saldo mes " + mesInicio + ": nuevoSaldo=0.00, montoPorCubrir=" + montoPorCubrir);
                }
                int filas = PagoCalendarioManager.insertaCalendario(conn, calendario);
                insertados += filas;
                log.info("Object: {}", "Insert calendar: mes=" + mesInicio + ", filas=" + filas + ", acumuladoInsertados=" + insertados);
                calendario.setImporteRetencion(new BigDecimal("0.00"));
                log.debug("Retención seteada a 0.00 para siguientes meses.");
            } else {
                log.trace("Object: {}", "Mes " + mesInicio + " sin saldo disponible. Se continúa.");
            }
            mesInicio--;
            log.trace("Object: {}", "Decrementa mes: nuevo mesInicio=" + mesInicio);
            if (mesInicio == tope && continuar) {
                String faltante = Util.formatNumber(montoPorCubrir);
                log.error("Object: {}", "No se logró completar el recurso. Faltante=" + faltante);
                throw new Exception("No se logro completar el recurso. Faltan: " + faltante);
            }
        }
        log.info("Object: {}", "insertaCalendarioPago(): fin. Registros insertados=" + insertados);
        return insertados;
    }

    private int insertaCalendarioPago(DetallePago detallePago, Map<Integer, SaldoMensual> saldos) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = insertaCalendarioPago(conn, detallePago, saldos);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback");
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int insertaCalendarioPagoCompromiso(Connection conn, DetallePago detallePago) throws Exception {
        Map<Integer, SaldoMensual> saldos = SaldoManager.iniciaSaldos(Integer.parseInt(EgresoEncabezado.COMPROMETIDO));
        String numeroContrato = "";
        if (conn != null) {
            EgresosBusinessLogic ecbl = new EgresoContratoBusinessLogic();
            numeroContrato = StringUtils.trimToEmpty(((EgresoContratoBusinessLogic) ecbl).getNumeroContratoEgreso(conn, detallePago.getTipoPago(), detallePago.getFolioPago()));
            saldos = sbl.getSaldoCompromiso(conn, saldos, numeroContrato, detallePago.getTipoPago(), detallePago.getFolioPago(), detallePago.getEp());
            return insertaCalendarioPago(conn, detallePago, saldos);
        } else {
            EgresosBusinessLogic ecbl = new EgresoContratoBusinessLogic(jniName);
            numeroContrato = StringUtils.trimToEmpty(((EgresoContratoBusinessLogic) ecbl).getNumeroContratoEgreso(detallePago.getTipoPago(), detallePago.getFolioPago()));
            saldos = sbl.getSaldoCompromiso(saldos, numeroContrato, detallePago.getTipoPago(), detallePago.getFolioPago(), detallePago.getEp());
            return insertaCalendarioPago(detallePago, saldos);
        }
    }

    public int insertaCalendarioPagoCompromiso(DetallePago detallePago) throws Exception {
        return insertaCalendarioPagoCompromiso(null, detallePago);
    }

    public int insertaCalendarioPagoDisponible(DetallePago detallePago) throws Exception {
        Map<Integer, SaldoMensual> saldos = SaldoManager.iniciaSaldos(Integer.parseInt(EgresoEncabezado.DISPONIBLE_NETO));
        saldos = sbl.getSaldoDisponible(saldos, detallePago.getTipoPago(), detallePago.getFolioPago(), detallePago.getEp());
        return insertaCalendarioPago(detallePago, saldos);
    }

    public void setEbl(EgresosBusinessLogic ebl) {
        this.ebl = ebl;
    }
}
