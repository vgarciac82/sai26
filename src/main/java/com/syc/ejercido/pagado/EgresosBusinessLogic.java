package com.syc.ejercido.pagado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import com.axtel.contratos.core.QuestionnaireManager;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.axtel.egresos.entities.PaymentDiference;
import com.axtel.egresos.repositories.PaymentValidatorRepository;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.core.CalculaImpuestosRetencionesManager;
import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.EgresoImpuestos;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.DetallePago;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.AmortizacionDetalle;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.PenaConvencional;
import com.syc.egresos.core.impl.EgresoPAGODIVERSOEncabezado;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.servlet.EgresosServlet;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoDetalleManager;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.PasivoDiferidoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgresosBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(EgresosBusinessLogic.class);

    private String folioGenerator = GestionInterface.FOLIO_GENERATOR;

    public static EgresoDetalle instanciaDetalle(EgresoEncabezado encabezado) throws Exception {
        String egresoBLName = GestionInterface.EGRESOS_IMPLEMENTACION_PKG + "." + "Egreso" + encabezado.getTipoPago() + "Detalle";
        log.info(String.format("Se cargara tipo de egreso [%s] desde la clase: [%s]", encabezado.getTipoPago(), egresoBLName));
        EgresoDetalle egresoDetalle = Util.instanceEgresoDetalle(egresoBLName);
        return egresoDetalle;
    }

    private int folio;

    private String jniName;

    private int regimenFiscal;

    private String tipoEgreso;

    private final PaymentValidatorRepository validator = new PaymentValidatorRepository();

    public EgresosBusinessLogic() {
    }

    public EgresosBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public int actualizaHeader(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int borrados = encabezado.delete(conn);
            log.info("Se elimino el encabezado: " + encabezado + ". Eliminados: " + borrados);
            int insertados = encabezado.save(conn);
            log.info("Se inserto el encabezado: " + encabezado);
            insertados += encabezado.avanzaEstatus(conn);
            log.info("Se avanzo el pago al siguiente estatus");
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int actualizaMontosRetencion(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int actualizados = encabezado.actualizaMontosRetencion(conn);
            actualizados += encabezado.avanzaEstatus(conn);
            log.info("Se actualizaron los montos de retencion en el folio: " + encabezado.getFolioPago() + " del pago: " + getTipoEgreso());
            conn.commit();
            return actualizados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int actualizaRetencion(EgresoEncabezado encabezado, int idTipoRetencion, BigDecimal valorRetencion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int actualizados = encabezado.actualizaRetencion(conn, idTipoRetencion, valorRetencion);
            log.info("Se actualizo la retencion " + idTipoRetencion + " en el folio: " + encabezado.getFolioPago() + " Al valor: " + actualizados);
            conn.commit();
            return actualizados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void apartaPago(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String cxp = encabezado.getContrarecibo();
            if (StringUtils.isBlank(cxp)) {
                cxp = encabezado.generaContrarecibo(conn);
                encabezado.setContrarecibo(cxp);
                encabezado.actualizaContrarecibo(conn);
            }
            int folioPagoApartado = encabezado.getFolioApartado(conn);
            if (folioPagoApartado <= 0) {
                if (!"PAGODIRECTO".equalsIgnoreCase(encabezado.getTipoPago())) {
                    folioPagoApartado = encabezado.insertaApartado(conn);
                    EgresosManager.aplicaApartadoTramite(conn, folioPagoApartado);
                }
                encabezado.avanzaEstatus(conn);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    /**
     * Realiza el calculo del detalle de impuestos y retenciones y lo inserta en
     * la tabla detalle. Aplica el motor contable.
     *
     * @param encabezado
     *            Encabezado del pago.
     * @param u
     *            Usuario que realiza la autorizacion
     * @return Numero de elementos insertados
     * @throws Exception
     */
    public void autorizaPago(Connection conn, EgresoEncabezado encabezado, Usuario u) throws Exception {
        List<EgresoCalendario> calendarioPago = cargaCalendarioEgreso(conn, encabezado);
        List<EgresoImpuestos> impuestos = null;
        if ("PAGODIVERSO".equalsIgnoreCase(encabezado.getTipoPago()) && ((EgresoPAGODIVERSOEncabezado) encabezado).isCargaMasiva())
            impuestos = cargaImpuestosPagoDiversoMasivo(conn, encabezado);
        else
            impuestos = cargaImpuestos(conn, encabezado);
        List<EgresoRetencion> retenciones = cargaRetenciones(conn, encabezado);
        int eliminados = EgresoDetalleManager.borraDetalle(conn, encabezado);
        log.info("Se eliminaron " + eliminados + " renglones del detalle para tipo de pago " + encabezado.getTipoPago() + " con folio " + encabezado.getFolioPago());
        int resultado = CalculaImpuestosRetencionesManager.calculaImpuestosRetenciones(conn, encabezado, calendarioPago, retenciones, impuestos);
        log.info("Se insertaron: " + resultado + "registros en la DB");
        /*
		 * Antes de aplicar el tramite valida la congruencia. En caso de
		 * encontrar diferencias lanza excepcion.
		 */
        if ("PAGODIVERSO".equals(encabezado.getTipoPago())) {
            List<PaymentDiference> diferencias = validator.validarPagos(encabezado.getFolioPago(), conn);
            if (!diferencias.isEmpty()) {
                throw gnerateDiferencesException(diferencias);
            }
        }
        EgresosManager.aplicaTramite(conn, encabezado);
        PasivoDiferidoManager.aplicarPasivoDiferido(conn, encabezado.getTipoPago(), String.valueOf(encabezado.getFolioPago()), encabezado.getTablaEncabezado(), encabezado.getTablaDetalle(), encabezado.getCampoLlave());
        avanzaEstatus(conn, encabezado);
        if ("S".equalsIgnoreCase(String.valueOf(encabezado.getEsFirmaElectronica()))) {
            procesFirmaElectronica(conn, encabezado, u);
        }
    }

    private List<EgresoImpuestos> cargaImpuestosPagoDiversoMasivo(Connection conn, EgresoEncabezado encabezado) throws Exception {
        List<EgresoImpuestos> impuestosSAI = cargaImpuestos(conn, encabezado);
        List<EgresoImpuestos> impuestosCFDI = cargaImpuestos(conn, encabezado);
        EgresoPAGODIVERSOEncabezado egredoDiverso = (EgresoPAGODIVERSOEncabezado) encabezado;
        if (impuestosSAI.size() > 1)
            throw new Exception("Error en numero de impuestos obtenidos en el contrato: " + egredoDiverso.getIdContrato() + ". Para carga masiva solo se considera IVA");
        if (impuestosCFDI.size() != 1)
            throw new Exception("Error en numero de impuestos obtenidos en la factura del contrato: " + egredoDiverso.getIdContrato() + ". Para carga masiva solo se considera IVA, Impuestos encontrados: " + impuestosCFDI.size());
        BigDecimal ivaSai = impuestosSAI.get(0).getMontoIVA();
        BigDecimal ivaCfdi = impuestosCFDI.get(0).getMontoIVA();
        BigDecimal diferenciaIVA = ivaSai.subtract(ivaCfdi);
        if (Math.abs(diferenciaIVA.doubleValue()) > .02)
            throw new Exception("Error en monto de impuestos. La diferencia de " + Util.formatNumber(diferenciaIVA) + " Es mayor a la tolerancia. Monto IVA SAI: " + Util.formatNumber(ivaSai) + " Monto IVA Factura" + Util.formatNumber(ivaCfdi));
        return impuestosCFDI;
    }

    private RuntimeException gnerateDiferencesException(List<PaymentDiference> diferencias) {
        StringBuilder mensaje = new StringBuilder("Se encontraron diferencias:\n");
        for (PaymentDiference dif : diferencias) {
            mensaje.append("- ").append(dif.toString()).append("\n");
        }
        log.warn(mensaje.toString());
        return new RuntimeException(mensaje.toString());
    }

    /**
     * Realiza el calculo del detalle de impuestos y retenciones y lo inserta en
     * la tabla detalle. Aplica el motor contable.
     *
     * @param encabezado
     *            Encabezado del pago.
     * @param u
     *            Usuario que realiza la autorizacion
     * @return Numero de elementos insertados
     * @throws Exception
     */
    public void autorizaPagoDirecto(EgresoEncabezado encabezado, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            autorizaPago(conn, encabezado, u);
            String centroContableUser = u.getPropiedad("CCENTROCONTABLE").getValor();
            FolioGeneratorInterface fg = Util.getFolioGenerator(folioGenerator);
            Caso casoCompromiso = CompromisoManager.generaCasoCompromiso(conn, u, fg, u.getLogin());
            int folioCompromiso = Integer.parseInt(casoCompromiso.getFolio().substring(casoCompromiso.getFolio().lastIndexOf('-') + 1));
            CFSequenceManager sequence = CFSequenceManager.getInstance();
            int seqFolio = 100000 + sequence.nextVal("CO-" + centroContableUser);
            String contrarecibo = centroContableUser + "CO" + EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal() + String.valueOf(seqFolio);
            CompromisoManager.insertaCompromisoRGEncabezado(conn, folioCompromiso, encabezado.getContrarecibo(), contrarecibo, u, "PAGODIRECTO");
            CompromisoManager.insertaCompromisoPDNominaDetalle(conn, folioCompromiso, String.valueOf(folio));
            CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(jniName);
            cbl.avanzaCasoCompromiso(conn, casoCompromiso, u);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void autorizaPago(EgresoEncabezado encabezado, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            autorizaPago(conn, encabezado, u);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void autorizaPagoNomina(EgresoEncabezado encabezado, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            List<EgresoCalendario> calendarioPago = cargaCalendarioEgreso(conn, encabezado);
            List<EgresoImpuestos> impuestos = cargaImpuestosNomina(conn, encabezado);
            List<EgresoRetencion> retenciones = cargaRetenciones(conn, encabezado);
            int eliminados = EgresoDetalleManager.borraDetalle(conn, encabezado);
            log.info("Se eliminaron " + eliminados + " renglones del detalle para tipo de pago " + encabezado.getTipoPago() + " con folio " + encabezado.getFolioPago());
            int resultado = CalculaImpuestosRetencionesManager.calculaImpuestosRetenciones(conn, encabezado, calendarioPago, retenciones, impuestos);
            log.info("Se insertaron: " + resultado + "registros en la DB");
            EgresosManager.aplicaTramite(conn, encabezado);
            PasivoDiferidoManager.aplicarPasivoDiferido(conn, encabezado.getTipoPago(), String.valueOf(encabezado.getFolioPago()), encabezado.getTablaEncabezado(), encabezado.getTablaDetalle(), encabezado.getCampoLlave());
            avanzaEstatus(conn, encabezado);
            if ("S".equalsIgnoreCase(String.valueOf(encabezado.getEsFirmaElectronica()))) {
                procesFirmaElectronica(conn, encabezado, u);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int avanzaEstatus(Connection conn, EgresoEncabezado encabezado) throws Exception {
        int actualizados = encabezado.avanzaEstatus(conn);
        return actualizados;
    }

    public int avanzaEstatus(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int actualizados = avanzaEstatus(conn, encabezado);
            conn.commit();
            return actualizados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private List<EgresoCalendario> cargaCalendarioEgreso(Connection conn, EgresoEncabezado encabezado) throws Exception {
        return cargaCalendarioEgreso(conn, encabezado.getTipoPago(), encabezado.getFolioPago());
    }

    public List<EgresoCalendario> cargaCalendarioEgreso(Connection conn, String tipoEgreso, int folioEgreso) throws Exception {
        List<EgresoCalendario> calEgreso = null;
        calEgreso = EgresosManager.cargaCalendarioEgreso(conn, tipoEgreso, folioEgreso);
        return calEgreso;
    }

    public List<EgresoDetalle> cargaDetalleEgreso(String tipoEgreso, int folioEgreso) throws Exception {
        String egresoBLName = GestionInterface.EGRESOS_IMPLEMENTACION_PKG + "." + "Egreso" + tipoEgreso + "Detalle";
        log.info(String.format("Se cargara tipo de egreso [%s] desde la clase: [%s]", tipoEgreso, egresoBLName));
        EgresoDetalle egresoDetalle = Util.instanceEgresoDetalle(egresoBLName);
        egresoDetalle.setJniName(this.jniName);
        return egresoDetalle.cargaDetalle(folioEgreso);
    }

    public EgresoEncabezado cargaEncabezadoEgreso() throws Exception {
        return cargaEncabezadoEgreso(getTipoEgreso(), getFolio());
    }

    public EgresoEncabezado cargaEncabezadoEgreso(HttpServletRequest req) throws Exception {
        return cargaEncabezadoEgreso(req, getTipoEgreso(), getFolio());
    }

    public EgresoEncabezado cargaEncabezadoEgreso(HttpServletRequest req, String tipoEgreso, int folioEgreso) throws Exception {
        EgresoEncabezado egresoEncabezado = generaInstancia(tipoEgreso, folioEgreso);
        return egresoEncabezado.cargaEncabezado(req);
    }

    public EgresoEncabezado cargaEncabezadoEgreso(String tipoEgreso, int folioEgreso) throws Exception {
        EgresoEncabezado egresoEncabezado = generaInstancia(tipoEgreso, folioEgreso);
        return egresoEncabezado.cargaEncabezado(folioEgreso);
    }

    private List<EgresoImpuestos> cargaImpuestos(Connection conn, EgresoEncabezado encabezado) throws Exception {
        return cargaImpuestos(conn, encabezado.getTipoPago(), encabezado.getFolioPago());
    }

    public List<EgresoImpuestos> cargaImpuestos(Connection conn, String tipoEgreso, int folioEgreso) throws Exception {
        List<EgresoImpuestos> impuestos = null;
        impuestos = EgresosManager.cargaImpuestos(conn, tipoEgreso, folioEgreso);
        return impuestos;
    }

    private List<EgresoImpuestos> cargaImpuestosNomina(Connection conn, EgresoEncabezado encabezado) throws Exception {
        return cargaImpuestosNomina(conn, encabezado.getTipoPago(), encabezado.getFolioPago());
    }

    public List<EgresoImpuestos> cargaImpuestosNomina(Connection conn, String tipoEgreso, int folioEgreso) throws Exception {
        List<EgresoImpuestos> impuestos = null;
        impuestos = EgresosManager.cargaImpuestosNomina(conn, tipoEgreso, folioEgreso);
        return impuestos;
    }

    public List<PenaConvencional> cargaPenas(Connection conn, String tipoEgreso, int folioEgreso) throws Exception {
        List<PenaConvencional> penas = null;
        penas = EgresosManager.cargaPenas(conn, tipoEgreso, folioEgreso);
        return penas;
    }

    private List<EgresoRetencion> cargaRetenciones(Connection conn, EgresoEncabezado encabezado) throws Exception {
        return cargaRetenciones(conn, encabezado.getTipoPago(), encabezado.getFolioPago());
    }

    public List<EgresoRetencion> cargaRetenciones(Connection conn, String tipoEgreso, int folioEgreso) throws Exception {
        List<EgresoRetencion> retenciones = null;
        retenciones = EgresosManager.cargaRetenciones(conn, tipoEgreso, folioEgreso);
        return retenciones;
    }

    public Firmante consultaFirmante(EgresoEncabezado encabezado, String tipoFirmante) throws Exception {
        return encabezado.consultaFirmante(tipoFirmante);
    }

    public Firmante consultaFirmanteSuplente(EgresoEncabezado encabezado, String tipoFirmante) throws Exception {
        return encabezado.consultaFirmanteSuplente(tipoFirmante);
    }

    public int eliminaRetencion(EgresoEncabezado encabezado, int idTipoRetencion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = 0;
            int eliminados = 0;
            if (encabezado.retencionEliminable(conn, idTipoRetencion)) {
                insertados = encabezado.bitacoraRetEliminada(conn, idTipoRetencion);
                log.info("Se registro la bitacora para la eliminacion de la retencion " + idTipoRetencion + " en el folio: " + encabezado.getFolioPago() + " Insertados: " + insertados);
                eliminados = encabezado.eliminaRetencion(conn, idTipoRetencion);
                log.info("Se elimino la retencion " + idTipoRetencion + " en el folio: " + encabezado.getFolioPago() + " Eliminados: " + eliminados);
            } else
                throw new Exception("La retencion esta marcada como obligatoria por lo que no se puede eliminar.");
            conn.commit();
            return eliminados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean esPagoConLayout(Caso c) throws Exception {
        Connection conn = null;
        boolean esPagoConLayout = false;
        try {
            conn = getConnection();
            esPagoConLayout = EgresosManager.esPagoConLayout(conn, c);
            return esPagoConLayout;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public String generaContrarecibo(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String cxp = encabezado.generaContrarecibo(conn);
            encabezado.setContrarecibo(cxp);
            encabezado.actualizaContrarecibo(conn);
            encabezado.avanzaEstatus(conn);
            conn.commit();
            return cxp;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<AmortizacionDetalle> generaDetalleAmortizacion(Connection conn, String tipoPago, int folioPago, Amortizacion amortizacion, List<EgresoImpuestos> impuestos) throws Exception {
        List<AmortizacionDetalle> amortizacionDetalle = new ArrayList<>();
        List<String> eps = EgresosManager.seleccionaClavesPago(conn, tipoPago, folioPago);
        int mesAmortiza = Util.getCurrentMonth();
        BigDecimal montoAmortizar = amortizacion.getMontoAmortizacion();
        BigDecimal amortizacionXEP = montoAmortizar.divide(new BigDecimal(eps.size()), 2, RoundingMode.HALF_UP);
        // --- IVA global para esta amortización ---
        BigDecimal porcentajeIVAEntero = BigDecimal.ZERO;
        // 0.16, 0.08, etc
        BigDecimal tasaIVAFraction = BigDecimal.ZERO;
        if (impuestos != null && !impuestos.isEmpty()) {
            // asumo un solo registro
            EgresoImpuestos imp = impuestos.get(0);
            // de IVA para el pago
            BigDecimal porcentajeIVA = imp.getPorcentajeIVA();
            if (porcentajeIVA != null && porcentajeIVA.compareTo(BigDecimal.ZERO) > 0) {
                // porcentajeIVA puede venir como 0.16 o 16 -> lo normalizamos a
                // entero (16)
                if (porcentajeIVA.compareTo(BigDecimal.ONE) < 0) {
                    porcentajeIVAEntero = porcentajeIVA.multiply(new BigDecimal("100"));
                } else {
                    porcentajeIVAEntero = porcentajeIVA;
                }
                porcentajeIVAEntero = porcentajeIVAEntero.setScale(0, RoundingMode.HALF_UP);
                // y de ahí obtenemos la fracción 0.16, 0.08, etc.
                tasaIVAFraction = porcentajeIVAEntero.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
            }
        }
        for (int i = 0; i < eps.size(); i++) {
            String epMes = eps.get(i);
            BigDecimal montoMes;
            if (i == eps.size() - 1) {
                // último EP: se lleva el residuo para cuadrar
                montoMes = montoAmortizar;
            } else {
                montoMes = amortizacionXEP;
            }
            montoAmortizar = montoAmortizar.subtract(montoMes);
            // --- separar bruto + IVA = neto (montoMes) ---
            BigDecimal importeBruto = montoMes;
            BigDecimal importeImpuestos = BigDecimal.ZERO;
            if (tasaIVAFraction.compareTo(BigDecimal.ZERO) > 0) {
                // 1
                BigDecimal divisor = BigDecimal.ONE.add(tasaIVAFraction);
                // +
                // 0.16
                importeBruto = montoMes.divide(divisor, 2, RoundingMode.HALF_UP);
                importeImpuestos = montoMes.subtract(importeBruto);
            }
            AmortizacionDetalle detAux = new AmortizacionDetalle();
            detAux.setEP(epMes);
            detAux.setFolioPago(folioPago);
            // neto
            detAux.setImporteAmortizacion(montoMes);
            // base sin IVA
            detAux.setImporteBruto(importeBruto);
            // IVA
            detAux.setImporteImpuestos(importeImpuestos);
            // 0, 8, 16...
            detAux.setPorcentajeIVA(porcentajeIVAEntero);
            detAux.setTipoPago(tipoPago);
            detAux.setMesAmortiza(mesAmortiza);
            amortizacionDetalle.add(detAux);
        }
        return amortizacionDetalle;
    }

    public void generaInformeComision(Connection conn, EgresoEncabezado encabezado, Usuario u, String reporthPath) throws Exception {
        SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
        solicitudPagoPrinter.setDetail(encabezado.getTablaDetalle());
        solicitudPagoPrinter.setDocument(encabezado.getTipoPago());
        solicitudPagoPrinter.setField(encabezado.getCampoLlave());
        solicitudPagoPrinter.setFileExtension("pdf");
        solicitudPagoPrinter.setHeader(encabezado.getTablaEncabezado());
        solicitudPagoPrinter.setIdField(encabezado.getFolioPago());
        solicitudPagoPrinter.setReportPath(reporthPath);
        solicitudPagoPrinter.setUsuario(u);
        solicitudPagoPrinter.setFormato15D(encabezado.isAplica15D());
        solicitudPagoPrinter.setDocName("Informe de Comision");
        try {
            FirmaElectronicaManager.generaArchivoInformeComision(conn, solicitudPagoPrinter, "Solicitud de Pago", false);
        } finally {
        }
    }

    public EgresoEncabezado generaInstancia(String tipoEgreso, int folioEgreso) throws Exception {
        String className = "Egreso" + tipoEgreso + "Encabezado";
        String egresoBLName = GestionInterface.EGRESOS_IMPLEMENTACION_PKG + "." + className;
        EgresoEncabezado egresoEncabezado = Util.instanceEgresoEncabezado(egresoBLName);
        egresoEncabezado.setJniName(this.jniName);
        egresoEncabezado.setTipoPago(tipoEgreso);
        egresoEncabezado.setFolioPago(folioEgreso);
        return egresoEncabezado;
    }

    public int generaRetenciones(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = encabezado.generaRetenciones(conn);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int getFolio() {
        return folio;
    }

    public int getRegimenFiscal() {
        return regimenFiscal;
    }

    public String getTipoEgreso() {
        return tipoEgreso;
    }

    public void guardaPago(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String cxp = encabezado.getContrarecibo();
            if (StringUtils.isBlank(cxp)) {
                cxp = encabezado.generaContrarecibo(conn);
                encabezado.setContrarecibo(cxp);
                encabezado.actualizaContrarecibo(conn);
                encabezado.avanzaEstatus(conn);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int insertaCalendario(EgresoEncabezado encabezado, String ep, BigDecimal importeBruto, String cuentaOrigen) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            DetallePago renglon = new DetallePago();
            renglon.setEp(ep);
            renglon.setTipoPago(getTipoEgreso());
            renglon.setFolioPago(getFolio());
            renglon.setImporteBruto(importeBruto);
            renglon.setIdTipoConcepto(encabezado.getIdTipoConcepto());
            renglon.setIdTipoMovimiento(encabezado.getIdTipoMovimiento());
            renglon.setImporteRetencion(new BigDecimal("0.00"));
            int insertados = encabezado.insertaCalendario(conn, renglon, cuentaOrigen);
            if ("PAGOFEDERALIZADO".equals(renglon.getTipoPago())) {
                insertados += encabezado.actualizaDestinoFed(conn);
            } else {
                insertados += encabezado.actualizaDestino(conn);
            }
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void procesFirmaElectronica(Connection conn, EgresoEncabezado encabezado, Usuario u) throws Exception {
        SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
        solicitudPagoPrinter.setDetail(encabezado.getTablaDetalle());
        solicitudPagoPrinter.setDocument(encabezado.getTipoPago());
        solicitudPagoPrinter.setField(encabezado.getCampoLlave());
        solicitudPagoPrinter.setFileExtension("pdf");
        solicitudPagoPrinter.setHeader(encabezado.getTablaEncabezado());
        solicitudPagoPrinter.setIdField(encabezado.getFolioPago());
        solicitudPagoPrinter.setReportPath(EgresosServlet.reportPath);
        solicitudPagoPrinter.setUsuario(u);
        solicitudPagoPrinter.setFormato15D(encabezado.isAplica15D());
        String lastDocName = solicitudPagoPrinter.getDocName();
        solicitudPagoPrinter.setDocName("Solicitud de Pago Firmada");
        try {
            FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Solicitud de Pago", false);
            solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
        } finally {
            solicitudPagoPrinter.setDocName(lastDocName);
        }
    }

    public void procesFirmaElectronica(Connection conn, EgresoEncabezado encabezado, Usuario u, String reporthPath) throws Exception {
        SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
        solicitudPagoPrinter.setDetail(encabezado.getTablaDetalle());
        solicitudPagoPrinter.setDocument(encabezado.getTipoPago());
        solicitudPagoPrinter.setField(encabezado.getCampoLlave());
        solicitudPagoPrinter.setFileExtension("pdf");
        solicitudPagoPrinter.setHeader(encabezado.getTablaEncabezado());
        solicitudPagoPrinter.setIdField(encabezado.getFolioPago());
        solicitudPagoPrinter.setReportPath(reporthPath);
        solicitudPagoPrinter.setUsuario(u);
        solicitudPagoPrinter.setFormato15D(encabezado.isAplica15D());
        String lastDocName = solicitudPagoPrinter.getDocName();
        solicitudPagoPrinter.setDocName("Solicitud de Pago Firmada");
        try {
            FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Solicitud de Pago", false);
            solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
        } finally {
            solicitudPagoPrinter.setDocName(lastDocName);
        }
    }

    public void rechazaPago(EgresoEncabezado encabezado, String motivoRechazo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            encabezado.rechazaPago(conn, motivoRechazo);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<Map<String, String>> resumenCalendario(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            List<Map<String, String>> resumen = encabezado.resumenCalendario(conn);
            return resumen;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Map<String, String> resumenConcepto(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> resumen = encabezado.resumenConcepto(conn);
            return resumen;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Map<String, String> resumenPago(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> resumen = encabezado.resumenPago(conn);
            return resumen;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<Map<String, String>> resumenRetenciones(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            List<Map<String, String>> resumen = encabezado.resumenRetenciones(conn);
            return resumen;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int saveAnswers(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = QuestionnaireManager.insertPaymentsQuestionnaire(conn, encabezado);
            insertados += QuestionnaireManager.insertAnswersPayments(conn, encabezado);
            log.debug("Se insertaron:  " + insertados + " registros de respuesta.");
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int saveHeader(EgresoEncabezado encabezado) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = encabezado.save(conn);
            insertados += encabezado.avanzaEstatus(conn);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas ejecutando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void setFolio(int folio) {
        this.folio = folio;
    }

    public void setFolioEgreso(int folio) {
        this.folio = folio;
    }

    public void setRegimenFiscal(int regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    public void setTipoEgreso(String tipoEgreso) {
        this.tipoEgreso = tipoEgreso;
    }

    public List<EgresoExcedeUMA> validaTopeUMASUnidad(EgresoEncabezado encabezado, String rfc) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            List<EgresoExcedeUMA> partidas = encabezado.validaTopeUMASUnidad(conn, rfc);
            return partidas;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
