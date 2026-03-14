package com.axtel.egresos.services.impl;

import static com.syc.gestion.servlet.GestionInterface.SYSTEM_NAME;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.RuntimeCryptoException;
import com.axtel.contratos.core.ContratoDiverso;
import com.axtel.contratos.core.ContratoDiversoManager;
import com.axtel.contratos.core.ContratoEP;
import com.axtel.egresos.core.EgresoPAGODIVERSO;
import com.axtel.egresos.entities.InvoiceSubmissionRequest;
import com.axtel.egresos.entities.MassPaymentResult;
import com.axtel.egresos.exceptions.EgresoException;
import com.axtel.egresos.exceptions.LayoutEgresoException;
import com.axtel.egresos.services.MassPaymentSupplierService;
import com.axtel.egresos.services.dto.OpinionResolveResponse;
import com.syc.adquisiciones.core.RecepcionMaterial;
import com.syc.adquisiciones.manager.RecepcionMaterialManager;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.MassPaymentInvoiceComponents;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.core.Beneficiario;
import com.syc.contable.core.BeneficiarioManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.cuentasbancarias.CuentaBancaria;
import com.syc.cuentasbancarias.DocBancarioManager;
import com.syc.egresos.DetallePago;
import com.syc.egresos.PagoCalendarioBussinessLogic;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoPAGODIVERSOEncabezado;
import com.syc.egresos.firmante.core.FirmanteManager;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.firmante.servlet.FirmanteSuplente;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.DefaultFolioGenerator;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class MassPaymentSupplierServiceImpl extends DataSourceManager implements MassPaymentSupplierService {

    private static final String EXPENDITURE_ID = "CPDV";

    private static final String GENERAL_PROGRAM = "02";

    private static final Logger log = LoggerFactory.getLogger(MassPaymentSupplierServiceImpl.class);

    private static final CFSequenceManager sm = CFSequenceManager.getInstance(GestionInterface.ATT_CONEXION);

    private String fiscalYear;

    private final EjercicioFiscalBusinessLogic fiscalYearService;

    private final FolioGeneratorInterface folioGenerator = new DefaultFolioGenerator();

    private final FacturaBusinessLogic invoiceService;

    private final EgresosBusinessLogic paymentService;

    private final CasoBusinessLogic processService;

    private final ConfiguraAplicativoBusinessLogic systemConfigService;

    private final SatOpinionQueryService satOpinionService;

    private String userLogin;

    public MassPaymentSupplierServiceImpl(String jniName) {
        super.init(jniName);
        this.systemConfigService = new ConfiguraAplicativoBusinessLogic(jniName);
        this.fiscalYearService = new EjercicioFiscalBusinessLogic(jniName);
        this.paymentService = new EgresosBusinessLogic(jniName);
        this.processService = new CasoBusinessLogic(jniName);
        this.fiscalYear = fiscalYearService.getEjercicioFiscalActivo().getaEjercicioFiscal();
        boolean validaContraSAT = "S".equalsIgnoreCase(systemConfigService.getSystemSetting("ACTIVA_VALIDACION_SAT"));
        boolean notificaFacturasEFA = "S".equalsIgnoreCase(systemConfigService.getSystemSetting("NOTIFICA_ERROR_VALIDACION_EFA"));
        boolean notificaFacturasInvalidasSAT = "S".equalsIgnoreCase(systemConfigService.getSystemSetting("NOTIFICA_ERROR_VALIDACION_SAT"));
        boolean permiteFacturasVersionAnterior = "S".equalsIgnoreCase(systemConfigService.getSystemSetting("PERMITE_VERSION_MENOR"));
        this.invoiceService = new FacturaBusinessLogic(jniName, validaContraSAT, null);
        this.satOpinionService = new SatOpinionQueryService();
        invoiceService.setNotificaErroresEFA(notificaFacturasEFA);
        invoiceService.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
        invoiceService.setPermiteVersionAnterior(permiteFacturasVersionAnterior);
    }

    private EgresoPAGODIVERSOEncabezado generateHeader(Connection conn, Usuario user, Caso process, ContratoDiverso contract, RecepcionMaterial reception, Beneficiario beneficiary, CuentaBancaria bankAccount, MassPaymentInvoiceComponents payment, Firmante voBoSigner, Firmante authSigner, Firmante[] suplierSigner) throws EgresoException {
        EgresoPAGODIVERSOEncabezado header = new EgresoPAGODIVERSOEncabezado();
        header.setFolioPago(Util.folio(process));
        header.setFolioPagoDiverso(contract.getIdContrato().trim());
        header.setIdContrato(contract.getIdContrato().trim());
        header.setRfc(contract.getRfc().replaceAll("-", ""));
        header.setNombre(beneficiary.getNombre() + StringUtils.trim(" " + StringUtils.trimToEmpty(beneficiary.getApellidoPaterno())) + StringUtils.trim(" " + StringUtils.trimToEmpty(beneficiary.getApellidoMaterno())));
        header.setCTAB(bankAccount.getClabe());
        header.setConcepto(StringUtils.trim(contract.getConceptoContrato()));
        header.setDescripcionPoliza(StringUtils.trim(contract.getConceptoContrato()) + "... CON FOLIO " + header.getFolioPago());
        header.setFechaAplicacion(new Date());
        header.setFechaCaptura(new Date());
        header.setFechaCarga(new Date());
        header.setFechaRevision(new Date());
        header.setFechaProgramadaPago(Util.todayPlus(2));
        header.setIdDestinoGasto(MassPaymentSupplierServiceImpl.EXPENDITURE_ID);
        header.setEsFirmaElectronica('S');
        header.setIdConcepto("");
        header.setIdTipoDocumento("0");
        header.setIdTipoOperacion("1");
        header.setEnviadoSICOP(0);
        header.setNumPagoAMF("***");
        header.setCentroContable(user.getPropiedad("CCENTROCONTABLE").getValor());
        header.setEjercicioFiscal(fiscalYear);
        header.setContrarecibo(header.generaContrarecibo(conn, sm));
        header.setNoFactura(String.valueOf(header.getFolioPago()));
        header.setMes(String.valueOf(Util.getCurrentMonth(conn)));
        header.setImporteBruto(payment.getComprobante().getSubTotal().setScale(2, RoundingMode.HALF_UP));
        header.setImporteIVA(payment.getComprobante().getTotalImpuestosTrasladados().setScale(2, RoundingMode.HALF_UP));
        header.setImporteRetencion(payment.getComprobante().getTotalRetenciones().setScale(2, RoundingMode.HALF_UP));
        header.setImporteNeto(payment.getComprobante().getTotal().setScale(2, RoundingMode.HALF_UP));
        header.setNumEmpleadoVoBo(voBoSigner.getNumeroEmpleado());
        header.setNumEmpleadoAut(authSigner.getNumeroEmpleado());
        header.setIdUsuarioCaptura(user.getLogin());
        header.setCentroContable(user.getPropiedad("CCENTROCONTABLE").getValor());
        header.setImporteMasIva(header.getImporteBruto().add(header.getImporteIVA()));
        header.setUnidadResponsable(user.getU_UR_Orig());
        header.setNumEmpleadoElab(Integer.parseInt(user.getNumeroEmpleado()));
        ((EgresoPAGODIVERSOEncabezado) header).setIdRecepMat(reception.getIdRecepMat());
        ((EgresoPAGODIVERSOEncabezado) header).setCargaMasiva(true);
        header.setTipoPago("PAGODIVERSO");
        header.save(conn);
        return header;
    }

    private Caso generateProcess(Connection conn, Usuario user) throws SQLException, GestionException {
        Caso process = CasoManager.nuevoCaso(conn, user, EgresoPAGODIVERSO.ID_TC, folioGenerator);
        process.getCasoDato("FOLIO").setValor(process.getFolio());
        process.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getTodayESMX());
        process.getCasoDato("EJERCICIO_FISCAL").setValor(fiscalYear);
        process.getCasoDato("OPERADOR").setValor(user.getNombre());
        process.getCasoDato("MONEDA").setValor("MXP");
        process.getCasoDato("APLICADO_CONT").setValor("true");
        process.getCasoDato("FECHA_AP_CONT").setValor(Util.getTodayESMX());
        Aplicacion app = AplicacionManager.select(conn, process.getTipoCaso().getGavetaAsociada());
        int idGabinete = AplicacionManager.createExpediente(conn, "SSANTIAGO", process, app);
        process.setIdGabinete(idGabinete);
        CasoManager.update(conn, process);
        return process;
    }

    private int insertaCalendario(Connection conn, DetallePago detail) throws LayoutEgresoException {
        try {
            int insertados = 0;
            PagoCalendarioBussinessLogic pcbl = new PagoCalendarioBussinessLogic();
            insertados += pcbl.insertaCalendarioPagoCompromiso(conn, detail);
            return insertados;
        } catch (Exception e) {
            throw new LayoutEgresoException(e);
        }
    }

    private DetallePago instanceDetail(Connection conn, EgresoEncabezado hedaer, String ep, RecepcionMaterial reception) throws LayoutEgresoException {
        String tipoMovto = "000";
        String tipoConcepto = "PN";
        BigDecimal importeMasIVA = reception.getMontoConIVA();
        DetallePago detail = new DetallePago();
        try {
            detail.setEp(ep);
            detail.setIdTipoConcepto(tipoConcepto);
            detail.setIdTipoMovimiento(tipoMovto);
            detail.setImporteBruto(importeMasIVA);
            detail.setTipoPago(hedaer.getTipoPago());
            detail.setFolioPago(hedaer.getFolioPago());
            return detail;
        } catch (Exception e) {
            throw new LayoutEgresoException(e);
        }
    }

    private Map<String, MassPaymentInvoiceComponents> loadInvoices(InvoiceSubmissionRequest submission, Usuario user) {
        Connection conn = null;
        try {
            conn = getConnection();
            String tipoModulo = "";
            Map<String, MassPaymentInvoiceComponents> invoices = invoiceService.extractInvoicesMassSupplierPayment(conn, submission, user);
            invoiceService.validateInvoices(conn, "PAGODIVERSO", invoices, false, tipoModulo);
            conn.commit();
            return invoices;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.rollback(conn);
            throw new RuntimeException("Problemas leyendo archivo de facturas: " + e.toString());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private Map<String, OpinionResolveResponse> loadOpinions(InvoiceSubmissionRequest submission) {
        try {
            Map<String, OpinionResolveResponse> opinions = new HashMap<String, OpinionResolveResponse>();
            this.satOpinionService.setApiKey(systemConfigService.getSystemSetting("SAT_OPINION_APIKEY"));
            this.satOpinionService.setUrlConnection(systemConfigService.getSystemSetting("SAT_OPINION_URL"));
            int iDesc = 1;
            List<Path> files = Util.unzipToTemp(submission.getOpinions());
            for (Path fOpinion : files) {
                try {
                    OpinionResolveResponse response = satOpinionService.consultOpinion(getUserLogin(), fOpinion.toFile(), SYSTEM_NAME);
                    if (response.getSource() != null && response.getSource().getRfc() != null)
                        opinions.put(response.getSource().getRfc(), response);
                    response.getSat().put("file", fOpinion.toFile().getAbsolutePath());
                } catch (Exception e) {
                    log.warn("Error occurred", "Error leyendo archivo: " + fOpinion + "Para determinar opinion de cumplimiento.");
                    String fileName = fOpinion.getFileName() == null ? ("DESCONOCIDO_" + iDesc++) : fOpinion.getFileName().toString();
                    OpinionResolveResponse response = new OpinionResolveResponse();
                    response.setManualValidationRequired(true);
                    response.setOk(false);
                    response.setMessage("Error en el archivo: " + fileName + " [ " + e.toString() + "]");
                    response.setReasonCode(fileName);
                    opinions.put(fileName, response);
                }
            }
            return opinions;
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    @Override
    public MassPaymentResult makePayment(String invoiceName, MassPaymentInvoiceComponents payment, Usuario user, InvoiceSubmissionRequest submission, OpinionResolveResponse sco) {
        Connection conn = null;
        MassPaymentResult result = new MassPaymentResult();
        result.setFileName(payment.getPdfFile());
        try {
            conn = getConnection();
            /* 1 Encontrar el contrato diverso */
            String rfc = payment.getComprobante().getRFCEmisor();
            String excludeUnits = systemConfigService.getSystemSetting("UE_REPORTE_ACTV_OPCIONAL");
            List<ContratoDiverso> contracts = ContratoDiversoManager.selectBySupplier(conn, rfc);
            if (contracts.size() > 1)
                throw new Exception("Se encontraron " + contracts.size() + " contratos para el proveedor " + rfc + " Solo debe existir uno");
            else if (contracts.size() == 0)
                throw new Exception("No se encontro contrato para el proveedor " + rfc);
            ContratoDiverso contract = contracts.get(0);
            /* 2 Encontrar la RM */
            List<RecepcionMaterial> receptions = RecepcionMaterialManager.selectActiveByContract(conn, contract.getIdContrato());
            if (receptions.size() > 1)
                throw new Exception("Se encontraron " + receptions.size() + " recepciones activas para el contrato " + contract.getIdContrato() + " Solo debe existir uno");
            else if (receptions.size() == 0)
                throw new Exception("No se encontro recepcion de material  activa para el contrato  " + contract.getIdContrato());
            RecepcionMaterial reception = receptions.get(0);
            /* 3 Seleccionar beneficiario */
            Beneficiario beneficiary = BeneficiarioManager.selectByRFC(conn, rfc);
            /* 4 Cuentas bancarias del proveedor. Solo debe haber una activa */
            List<CuentaBancaria> bankAccounts = DocBancarioManager.selectActiveByRFC(conn, rfc);
            if (bankAccounts.size() > 1)
                throw new Exception("Se encontraron " + bankAccounts.size() + " cuentas de banco activas para el RFC" + rfc + " Solo debe existir uno");
            else if (bankAccounts.size() == 0)
                throw new Exception("No se encontro cuenta de banco activa para el RFC " + rfc);
            CuentaBancaria bankAccount = bankAccounts.get(0);
            /* 5 Obtiene la EP del contrato para generar detalle */
            String ep = readBudgetStruct(contract, submission.getBudgetItem(), MassPaymentSupplierServiceImpl.GENERAL_PROGRAM);
            if (ep == null)
                throw new RuntimeException("No se encontro EP con partida: " + submission.getBudgetItem() + " y programa: " + MassPaymentSupplierServiceImpl.GENERAL_PROGRAM);
            /* 6 Genera Caso */
            Caso process = generateProcess(conn, user);
            /* 7 Genera encabezado de pago */
            EgresoEncabezado paymentHeader = generateHeader(conn, user, process, contract, reception, beneficiary, bankAccount, payment, submission.getVoBoFirmante(), submission.getAuthFirmante(), submission.getSuplentes());
            /* 8 Guarda las facturas y la opinion de cumplimiento. */
            Map<String, MassPaymentInvoiceComponents> invoiceContent = new HashMap<String, MassPaymentInvoiceComponents>();
            invoiceContent.put(invoiceName, payment);
            invoiceService.insertaFacturas(conn, process, user, invoiceContent, false);
            satOpinionService.saveFile(conn, process, user, sco.getSat().get("file"));
            /*
			 * 9 Guarda informe de actividades Cambio. Si la UE no tiene
			 * permitido omitir los informes no los dejara avanzar.
			 */
            if (payment.getActivityReportFile() != null) {
                saveActivityReport(conn, process, payment.getActivityReportFile(), user);
                payment.setActivityReportLoaded(true);
            }
            reception.setMontoIVA(paymentHeader.getImporteIVA());
            reception.setMontoConIVA(paymentHeader.getImporteMasIva());
            reception.setMontoSinIVA(paymentHeader.getImporteBruto());
            /* 10 Genera detalle */
            DetallePago detail = instanceDetail(conn, paymentHeader, ep, reception);
            /* 11 Genera pago calendario */
            insertaCalendario(conn, detail);
            /* 12 Inserta firmantes delego si existen */
            saveAlternateSignatory(conn, paymentHeader, submission.getSuplentes());
            /* 13 Autoriza pago */
            paymentService.autorizaPago(conn, paymentHeader, user);
            result.setFolio(process.getFolio());
            result.setLog("Pago realizado exitosamente");
            result.setRecipt(paymentHeader.getContrarecibo());
            result.setSuccess(true);
            processService.avanzaCaso(conn, process, user.getLogin(), "", new String[] { "CONSULTA_PAGODIVERSO" }, new String[] { "consulta_factura" }, Util.readValuesCasoDato(process.getCasoDato()), null);
            /* Actualiza estatus RM */
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setLog("Error generando pago: " + payment.getPdfFile() + " Causa: " + e.toString());
            Util.rollback(conn);
        } finally {
            CloseObject.closeObject(conn);
        }
        return result;
    }

    @Override
    public List<MassPaymentResult> processSubmission(InvoiceSubmissionRequest submission, Usuario user) {
        invoiceService.setUsuario(user);
        List<MassPaymentResult> result = new ArrayList<MassPaymentResult>();
        Map<String, MassPaymentInvoiceComponents> invoices = loadInvoices(submission, user);
        Map<String, OpinionResolveResponse> satOpinions = loadOpinions(submission);
        for (Iterator<String> i = invoices.keySet().iterator(); i.hasNext(); ) {
            String key = i.next();
            MassPaymentResult logPayment = new MassPaymentResult();
            logPayment.setFileName(key);
            try {
                MassPaymentInvoiceComponents payment = invoices.get(key);
                if (payment.getErrorLog().size() > 0) {
                    logPayment.setSuccess(false);
                    logPayment.setLog(String.join("\n", payment.getErrorLog()));
                    result.add(logPayment);
                    continue;
                }
                /*
				 * Si llega a esta linea no hubo error en el CFDI, por lo que es
				 * seguro continuar consultando la opinion de cumplimiento. Hay
				 * varias posibilidades. 1) No adjuntaron la opinion de
				 * cumplimiento 2) Falla de conexion al WS. 3) Ocurrio un error
				 * del servicio de consulta. Se especifica en el log. 4) Opinion
				 * de cumplimiento obtenida negativa 5) Opinion de cumplimiento
				 * positiva.
				 * 
				 * Solo si ocurre la opcion 5 intenta hacer el pago. Si hubo
				 * error se notifica por separado al usuario para que reporte a
				 * soporte.
				 * 
				 */
                String rfcSupplier = payment.getComprobante().getRFCEmisor();
                log.debug("Object: {}", "Validating query result for " + rfcSupplier);
                OpinionResolveResponse sco = null;
                if (satOpinions.get(rfcSupplier) != null) {
                    sco = satOpinions.remove(rfcSupplier);
                    if (!sco.isOk()) {
                        logPayment.setSuccess(false);
                        logPayment.setLog(String.join("\nNo se pudo consultar de manera automatica la opinion de cumplimiento del RFC: " + rfcSupplier + " debido al error: " + sco.getReasonCode() + " - " + sco.getMessage()));
                        result.add(logPayment);
                        continue;
                    }
                    if (!OpinionResolveResponse.POSITIVE.equals(sco.getSat().get("Sentido"))) {
                        logPayment.setSuccess(false);
                        logPayment.setLog(String.join("\nNo se pude realizar el pago ya que la opinion de cumplimiento no es positiva para el RFC: " + rfcSupplier));
                        result.add(logPayment);
                        continue;
                    }
                } else {
                    logPayment.setSuccess(false);
                    logPayment.setLog(String.join("\nNo se encontro adjunta la opinion de cumplimiento del RFC: " + rfcSupplier + " en el archivo"));
                    result.add(logPayment);
                    continue;
                }
                if (OpinionResolveResponse.POSITIVE.equals(sco.getSat().get("Sentido")))
                    logPayment = makePayment(key, payment, user, submission, sco);
                result.add(logPayment);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                logPayment.setSuccess(false);
                logPayment.setLog("Error procesando el pago " + e.toString());
                result.add(logPayment);
                throw new RuntimeException(e.toString(), e);
            }
        }
        return result;
    }

    private String readBudgetStruct(ContratoDiverso contract, String budgetItem, String generalProgram) {
        for (ContratoEP ep : contract.getDetalleEP()) {
            String[] components = ep.getEp().split("\\.");
            if (generalProgram.equals(components[5]) && budgetItem.equals(components[9]))
                return ep.getEp();
        }
        return null;
    }

    private void saveActivityReport(Connection conn, Caso process, File reportFile, Usuario user) {
        try {
            Carpeta cPadre = CarpetaManager.getCarpetaByName(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), "Informe de Actividades");
            if (cPadre == null) {
                cPadre = Util.creaCarpeta(conn, process, user, "Informe de Actividades", "Informe de Actividades");
            }
            Documento d = DocumentoManager.getDocumento(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), cPadre.getIdCarpeta(), "Reporte de Actividades");
            if (d == null) {
                d = Util.creaDocumento(conn, process, cPadre, user, "Reporte de Actividades", "Documento con el reporte de actividades del mes.");
            } else if (d.getPaginasDocumento() != null && d.getPaginasDocumento().length > 0)
                return;
            DocumentoManager.insertPaginaDocumento(conn, d, reportFile);
        } catch (Exception e) {
            throw new RuntimeException("Error guardando reporte de actividades: " + e, e);
        }
    }

    private void saveAlternateSignatory(Connection conn, EgresoEncabezado header, FirmanteSuplente[] suplentes) throws Exception {
        if (suplentes[0] != null) {
            suplentes[0].setTipoAutorizador("SUPVOBO");
            FirmanteManager.saveFirmanteSuplente(conn, suplentes[0], header.getTipoPago(), header.getFolioPago());
        }
        if (suplentes[1] != null) {
            suplentes[1].setTipoAutorizador("SUPAUT");
            FirmanteManager.saveFirmanteSuplente(conn, suplentes[1], header.getTipoPago(), header.getFolioPago());
        }
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
