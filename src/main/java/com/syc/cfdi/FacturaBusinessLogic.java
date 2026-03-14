package com.syc.cfdi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import com.axtel.cfdi.ComplementoCombustible.Bonificacion;
import com.axtel.cfdi.ComplementoCombustible.LectorComplementoCombustible;
import com.axtel.cfdi.ComplementoCombustible.custom.AdendaECC;
import com.axtel.cfdi.ComplementoCombustible.custom.AdendaEdenredUtils;
import com.axtel.cfdi.ComplementoCombustible.custom.EdrConceptoDispersion;
import com.axtel.cfdi.ComplementoCombustible.vales.AddendaEfectivale;
import com.axtel.egresos.entities.InvoiceSubmissionRequest;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.cfdi.v3332.Comprobante.Comprobante;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.EgresoContratoManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.ListadoCorreosPendientesBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.zip.ZipManager;
import mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12.EstadoDeCuentaCombustible;
import mx.grupocorasa.sat.common.Pagos10.Pagos.Pago;
import mx.grupocorasa.sat.common.Pagos10.Pagos.Pago.DoctoRelacionado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class FacturaBusinessLogic extends DataSourceManager {

    public class DocumentoSAI {

        private int folioDocumento;

        private int idCaso;

        private String tipoDocumento;

        private String UUID;

        /**
         * @return the folioDocumento
         */
        public int getFolioDocumento() {
            return folioDocumento;
        }

        /**
         * @return the idCaso
         */
        public int getIdCaso() {
            return idCaso;
        }

        /**
         * @return the tipoDocumento
         */
        public String getTipoDocumento() {
            return tipoDocumento;
        }

        /**
         * @return the uUID
         */
        public String getUUID() {
            return UUID;
        }

        /**
         * @param folioDocumento
         *            the folioDocumento to set
         */
        public void setFolioDocumento(int folioDocumento) {
            this.folioDocumento = folioDocumento;
        }

        /**
         * @param idCaso
         *            the idCaso to set
         */
        public void setIdCaso(int idCaso) {
            this.idCaso = idCaso;
        }

        /**
         * @param tipoDocumento
         *            the tipoDocumento to set
         */
        public void setTipoDocumento(String tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
        }

        /**
         * @param uUID
         *            the uUID to set
         */
        public void setUUID(String uUID) {
            UUID = uUID;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
        @Override
        public String toString() {
            return "DocumentoSAI [UUID=" + UUID + ", tipoDocumento=" + tipoDocumento + ", folioDocumento=" + folioDocumento + ", idCaso=" + idCaso + "]";
        }
    }

    private static final String REGIMEN_FISCAL_DEFAULT = "603";

    private JAXBContext jaxbContext = null;

    private Unmarshaller jaxbUnmarshaller = null;

    private static final Logger log = LoggerFactory.getLogger(FacturaBusinessLogic.class);

    private String directorioTemporal;

    private String jniName;

    private boolean facturaVales;

    private boolean notificaErroresEFA;

    private boolean notificaErroresSAT;

    private boolean permiteVersionAnterior = true;

    private String RFCBeneficiario = "CNF010405EG1";

    private Usuario usuario;

    private String regimenFiscalCliente;

    private boolean validacionSAT;

    private boolean viaticos;

    public boolean isViaticos() {
        return viaticos;
    }

    public FacturaBusinessLogic() {
        try {
            this.regimenFiscalCliente = REGIMEN_FISCAL_DEFAULT;
            jaxbContext = JAXBContext.newInstance(com.axtel.cfdi.ComplementoCombustible.vales.Comprobante.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("No fue posible crear los parseadores de vales de combustible. " + e.toString(), e);
        }
    }

    public Map<String, MassPaymentInvoiceComponents> extractInvoicesMassSupplierPayment(Connection conn, InvoiceSubmissionRequest submission, Usuario user) throws Exception {
        Map<String, MassPaymentInvoiceComponents> invoices;
        invoices = FacturaManager.validateInvoiceFiles(submission.getInvoices());
        setDirectorioTemporal(System.getProperty("java.io.tmpdir"));
        invoices = loadMassInoviceSupplierPayment(submission, invoices, user);
        return invoices;
    }

    public List<String> validateSinglePayInvoice(Connection conn, String rfc, String paymentType, Map<String, ComponentesFactura> invoices, boolean validateIssuer, boolean isCreditNote) {
        List<String> generalErrors = new ArrayList<String>();
        if (validateIssuer) {
            List<String> erroresEmisor = FacturaManager.validaEmisor(invoices, rfc);
            if (erroresEmisor.size() > 0) {
                generalErrors.addAll(erroresEmisor);
            }
        }
        List<String> erroresBeneficiario = FacturaManager.validaBeneficiarios(invoices, getRFCBeneficiario());
        if (erroresBeneficiario.size() > 0)
            generalErrors.addAll(erroresBeneficiario);
        return generalErrors;
    }

    public List<String> validateInvoices(Connection conn, String paymentType, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito, String tipoModulo) {
        List<String> generalErrors = new ArrayList<String>();
        /*
		 * Valida que la forma de pago este en la configuracion, en otro caso se
		 * rechza.
		 */
        List<String> erroresFormaPago = null;
        if ("RELACIONGASTOS".equalsIgnoreCase(paymentType))
            erroresFormaPago = FacturaManager.validaFormaPagoRG(conn, facturas, esNotaCredito);
        else
            erroresFormaPago = FacturaManager.validaFormaPago(conn, facturas, esNotaCredito);
        if (erroresFormaPago.size() > 0)
            generalErrors.addAll(erroresFormaPago);
        /*
		 * Se valida que: Si es nota de credito debe ser tipo "E" (Egreso) Si es
		 * factura debe ser tipo "I" (Ingreso)
		 */
        List<String> erroresTipoFactura = FacturaManager.validaTipoFactura(facturas, esNotaCredito);
        if (erroresTipoFactura.size() > 0)
            generalErrors.addAll(erroresTipoFactura);
        /*
		 * Se valida que: Los CFDI cargados sean del mismo EF
		 */
        List<String> erroresEF = FacturaManager.validaEjercicioFactura(conn, facturas);
        if (erroresEF.size() > 0)
            generalErrors.addAll(erroresEF);
        /*
		 * Si esta activado, valida contra el WS del SAT la vigencia de la
		 * factura.
		 */
        if (isValidacionSAT()) {
            List<String> erroresSAT = FacturaManager.validaSAT(facturas, notificaErroresSAT);
            if (erroresSAT.size() > 0) {
                generalErrors.addAll(erroresSAT);
                if (isNotificaErroresSAT()) {
                    ListadoCorreosPendientesBusinessLogic lcpbl = new ListadoCorreosPendientesBusinessLogic(getJniName());
                    lcpbl.setUsuario(getUsuario());
                    lcpbl.insertaNotificacionesFactura(erroresSAT);
                }
            }
        }
        if (!isViaticos()) {
            List<String> erroresRegimenFiscal = FacturaManager.validaRegimenReceptor(facturas, this.regimenFiscalCliente);
            if (erroresRegimenFiscal.size() > 0)
                generalErrors.addAll(erroresRegimenFiscal);
            /*
			 * Valida que el uso del cfdi sea G03 Gastos en general. En otro
			 * caso se rechza.
			 */
            List<String> erroresUsoCFDI = FacturaManager.validaUsoFactura(conn, facturas, esNotaCredito);
            if (erroresUsoCFDI.size() > 0)
                generalErrors.addAll(erroresUsoCFDI);
        }
        List<String> erroresFactExistente = FacturaManager.validaFacturasRepetidas(conn, facturas);
        if (erroresFactExistente.size() > 0)
            generalErrors.addAll(erroresFactExistente);
        List<String> erroresFactExistenteEFA = FacturaManager.validaFacturasRepetidasEjerciciosAnteriores(conn, facturas);
        if (erroresFactExistenteEFA.size() > 0) {
            generalErrors.addAll(erroresFactExistenteEFA);
            if (isNotificaErroresEFA()) {
                ListadoCorreosPendientesBusinessLogic lcpbl = new ListadoCorreosPendientesBusinessLogic(getJniName());
                lcpbl.setUsuario(getUsuario());
                lcpbl.insertaNotificacionesFacturaRepetida(erroresFactExistenteEFA);
            }
        }
        if (!isPermiteVersionAnterior()) {
            List<String> erroresFacturasVersion = FacturaManager.validaVersionFactura(conn, paymentType, facturas);
            if (erroresFacturasVersion.size() > 0)
                generalErrors.addAll(erroresFacturasVersion);
        }
        List<String> erroresTipoRetencion = FacturaManager.validaTipoPagoRetencion(conn, facturas, tipoModulo);
        if (erroresTipoRetencion.size() > 0)
            generalErrors.addAll(erroresTipoRetencion);
        return generalErrors;
    }

    public FacturaBusinessLogic(String jniName, boolean validacionSAT, Usuario u) {
        super.init(jniName);
        this.jniName = jniName;
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
        this.regimenFiscalCliente = cabl.getSystemSetting("REGIMEN_FISCAL_CLIENTE");
        if (StringUtils.isBlank(this.regimenFiscalCliente))
            this.regimenFiscalCliente = REGIMEN_FISCAL_DEFAULT;
        this.validacionSAT = validacionSAT;
        this.usuario = u;
        try {
            jaxbContext = JAXBContext.newInstance(com.axtel.cfdi.ComplementoCombustible.vales.Comprobante.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("No fue posible crear los parseadores de vales de combustible. " + e.toString(), e);
        }
        this.directorioTemporal = System.getProperty("java.io.tmpdir");
    }

    public FacturaBusinessLogic(boolean validacionSAT, Usuario u) {
        this.validacionSAT = validacionSAT;
        this.usuario = u;
        this.regimenFiscalCliente = REGIMEN_FISCAL_DEFAULT;
        try {
            jaxbContext = JAXBContext.newInstance(com.axtel.cfdi.ComplementoCombustible.vales.Comprobante.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("No fue posible crear los parseadores de vales de combustible. " + e.toString(), e);
        }
    }

    public Map<String, MassPaymentInvoiceComponents> loadMassInoviceSupplierPayment(InvoiceSubmissionRequest submission, Map<String, MassPaymentInvoiceComponents> invoices, Usuario user) throws Exception {
        log.trace("Object: {}", "Iniciando extraccion de contenido del archivo [" + submission.getInvoices() + "]");
        log.trace("Object: {}", "Abriendo archivo [" + submission.getInvoices().getAbsolutePath() + "]  para su extraccion.");
        long start = System.currentTimeMillis();
        String excludeUnits = getActivityReportExcludedUnits();
        int extractedFiles = 0;
        if (!(directorioTemporal.endsWith("/") || directorioTemporal.endsWith("\\")))
            directorioTemporal = directorioTemporal + File.separatorChar;
        log.trace("Object: {}", "Directorio temporal de extraccion[ " + directorioTemporal + "]");
        File destDir = new File(directorioTemporal);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(submission.getInvoices()));
        ZipEntry entry = zipIn.getNextEntry();
        log.trace("Iterando contenido del archivo.");
        Map<String, File> otherElements = new HashMap<String, File>();
        while (entry != null) {
            String elementName = FacturaUtils.obtenNombreArchivoZip(entry.getName(), false).toLowerCase();
            String extension = FacturaUtils.obtenExtensionArchivoZip(entry.getName()).toLowerCase();
            String filePath = FacturaUtils.generaNombreArchivoTemporal(directorioTemporal, elementName, extension).toLowerCase();
            log.trace("Object: {}", "Procesando archivo [" + elementName + "." + extension + "] dentro del archivo ZIP");
            if (!entry.isDirectory()) {
                log.trace("Se trata de un archivo, se extraera");
                File f = new File(filePath);
                ZipManager.extractFile(zipIn, filePath);
                if (invoices.get(elementName.toLowerCase()) != null) {
                    MassPaymentInvoiceComponents mic = invoices.get(elementName.toLowerCase());
                    if ("pdf".equalsIgnoreCase(extension.toLowerCase())) {
                        mic.setPdfPathFile(f.getAbsolutePath().toLowerCase());
                    } else if ("xml".equalsIgnoreCase(extension.toLowerCase())) {
                        mic.setXmlPathFile(f.getAbsolutePath().toLowerCase());
                        File fxml = new File(mic.getXmlPathFile().toLowerCase());
                        log.debug("Object Invoice loaded!");
                        Comprobante voucher = FacturaManager.cargaComprobante(fxml);
                        mic.setComprobante(voucher);
                    }
                } else {
                    otherElements.put(elementName.toLowerCase(), f);
                }
                extractedFiles++;
            } else {
                log.trace("Se trata de un directorio, se creara");
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        invoices = loadActivityReport(invoices, otherElements, (excludeUnits.toUpperCase().indexOf(user.getU_UR()) >= 0));
        long stop = System.currentTimeMillis();
        log.trace("Object: {}", "Terminada extraccion de contenido del archivo. Se extrajeron [" + extractedFiles + "] archivos en [" + ((stop - start) / 1000) + "] s.");
        return invoices;
    }

    private String getActivityReportExcludedUnits() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String setting = ConfiguraAplicativoManager.getSystemSetting(conn, "UE_REPORTE_ACTV_OPCIONAL");
            return StringUtils.trimToEmpty(setting);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private Map<String, MassPaymentInvoiceComponents> loadActivityReport(Map<String, MassPaymentInvoiceComponents> invoices, Map<String, File> otherElements, boolean allowEmptyReport) {
        for (Iterator<String> i = invoices.keySet().iterator(); i.hasNext(); ) {
            String elementName = i.next();
            MassPaymentInvoiceComponents mpic = invoices.get(elementName);
            if (mpic.getComprobante() == null)
                continue;
            String rfcIssuer = mpic.getComprobante().getRFCEmisor().replaceAll("-", "");
            String reportName = "informe_" + rfcIssuer.toLowerCase();
            File reportFile = otherElements.get(reportName);
            if (reportFile != null && reportFile.exists()) {
                mpic.setActivityReportFile(reportFile);
                mpic.setActivityReportFileName(reportName);
            } else {
                if (!allowEmptyReport)
                    mpic.getErrorLog().add("No se encontró reporte de actividades para el rfc: [" + rfcIssuer + "] de la factura [" + elementName + "] Se esperaba archivo: " + reportName);
            }
        }
        return invoices;
    }

    public Map<String, ComponentesFactura> cargaCFDI(Map<String, ComponentesFactura> facturas) throws Exception {
        LectorComplementoCombustible lectorCombustible = new LectorComplementoCombustible();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String nombreFact = i.next();
            ComponentesFactura cf = facturas.get(nombreFact);
            File fxml = new File(cf.getXmlPathFile());
            log.trace("Object: {}", "Iniciando proceso de : " + cf.getXmlPathFile());
            Comprobante comprobante = FacturaManager.cargaComprobante(fxml);
            cf.setComprobante(comprobante);
            boolean esCFDICombustible = comprobante.esCFDICombustible();
            if (esCFDICombustible) {
                AdendaECC adenda = lectorCombustible.readMontosCFDI(fxml);
                Bonificacion bonificacion = lectorCombustible.readBonificacion(fxml);
                EstadoDeCuentaCombustible edoCtaCombustible = comprobante.leeEstadoDeCuenta();
                comprobante.setComplementoCombustible(true);
                comprobante.setEstadoDeCuentaCombustible(edoCtaCombustible);
                comprobante.setBonificacion(bonificacion);
                comprobante.setAdendaECC(adenda);
            } else if (isFacturaVales()) {
                com.axtel.cfdi.ComplementoCombustible.vales.Comprobante comprobanteXML = (com.axtel.cfdi.ComplementoCombustible.vales.Comprobante) jaxbUnmarshaller.unmarshal(fxml);
                AddendaEfectivale adenda = comprobanteXML.getAddenda();
                comprobante.setFacturaVales(true);
                comprobante.setAdenda(adenda);
            }
        }
        return facturas;
    }

    public int cargaFacturasPagoProveedor(Caso c, Usuario u, Map<String, String> datosCarga) throws Exception {
        Connection conn = null;
        int insertados = 0;
        /*
		 * Verifica si adjuntaron Zip con facturas. En ese caso valida los CFDI
		 */
        try {
            String rutaArchivoFacturas = datosCarga.get("archivoZipFacturasPP");
            conn = getConnection();
            int ejercicioFiscal = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            String destinoGasto = datosCarga.get("destinoGasto");
            String RFC = datosCarga.get("RFC");
            ExtraccionFacturas ef = null;
            String tipoModulo = "";
            if (!StringUtils.isBlank(rutaArchivoFacturas)) {
                int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
                if (destinoGasto.equals("CPRP")) {
                    ef = extraeFacturas(rutaArchivoFacturas, c.getTipoCaso().getGavetaAsociada(), RFC, nFolioPago, true, false, tipoModulo);
                } else {
                    ef = extraeFacturas(rutaArchivoFacturas, c.getTipoCaso().getGavetaAsociada(), "CNF010405EG1", nFolioPago, false, false, tipoModulo);
                }
                RelacionGastosManager.insertaTipoDocumentacionRG(conn, nFolioPago, destinoGasto);
                if (ef.getErrores().size() == 0) {
                    Map<String, ComponentesFactura> facturas = ef.getFacturas();
                    Carpeta cfdi = FacturaManager.obtenCarpetaDestino(conn, c, "CFDI", u.getLogin());
                    for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                        String facturaNombre = i.next();
                        log.debug("Object: {}", "Insertando factura [" + facturaNombre + "] ");
                        ComponentesFactura cf = facturas.get(facturaNombre);
                        insertados += FacturaManager.insertaArchivosFactura(conn, facturaNombre, cf, cfdi, c, u.getLogin());
                    }
                    FacturaManager.insertaInformacionFacturas(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, facturas);
                    RelacionGastosManager.actualizaTipoDocumentacion(conn, ejercicioFiscal, nFolioPago, "lPagoFacturasProveedor", insertados);
                } else {
                    String msgRetorno = "";
                    String token = "";
                    for (int i = 0; i < ef.getErrores().size(); i++) {
                        msgRetorno += token + ef.getErrores().get(i);
                        token = "<br>";
                    }
                    throw new Exception(msgRetorno);
                }
            } else
                throw new Exception("No se recibio el archivo zip con las facturas o el archivo esta dañado. Intente nuevamente.");
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "No fue posible realiar rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int cargaInformacionAlimentacion(Caso c, Usuario u, Map<String, String> datosCarga) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            if (c.getIdGabinete() <= 0)
                throw new Exception("No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas.");
            int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            int ejercicioFiscal = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            String destinoGasto = datosCarga.get("destinoGasto");
            RelacionGastosManager.insertaTipoDocumentacionRG(conn, nFolioPago, destinoGasto);
            /*
			 * Verifica si tiene informacion referente a pagos por alimentacion.
			 */
            String archivoComprobacionAlimentacion = datosCarga.get("archivoComprobacionAlimentacion");
            double montoAlimentacion = Double.parseDouble(StringUtils.isBlank(datosCarga.get("montoAlimentacion")) ? "0.00" : datosCarga.get("montoAlimentacion"));
            if (!StringUtils.isBlank(archivoComprobacionAlimentacion) && montoAlimentacion > 0.0d) {
                Carpeta oficioAlimentacion = FacturaManager.obtenCarpetaDestino(conn, c, "LISTA DE PAGA", u.getLogin());
                insertados += FacturaManager.insertaDoctoExtranjero(conn, archivoComprobacionAlimentacion, oficioAlimentacion, c, u.getLogin());
                FacturaManager.insertaAlimentacionBrigadistas(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, "ALIMENTACION", montoAlimentacion);
                RelacionGastosManager.actualizaTipoDocumentacion(conn, ejercicioFiscal, nFolioPago, "lAlimentacionBrigadistas", insertados);
            } else
                throw new Exception("No se recibio la informacion completa para el pago de alimentacion a brigadistas. El archivo con la lista de pagos y el total a pagar son datos requeridos.");
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int cargaInformacionPagoRG(Caso c, Usuario u, Map<String, String> datosCarga) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            if (c.getIdGabinete() <= 0)
                throw new Exception("No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas.");
            int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            int ejercicioFiscal = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            String destinoGasto = datosCarga.get("destinoGasto");
            String tipoModulo = "";
            RelacionGastosManager.insertaTipoDocumentacionRG(conn, nFolioPago, destinoGasto);
            /*
			 * Verifica si tiene informacion referente a comision al extranjero
			 * y si esta completa para insertala.
			 */
            String rutaArchivoCExtranjero = datosCarga.get("archivoComisionExtranjero");
            String folioComExtranjero = datosCarga.get("folioComisionExtranjero");
            double montoComExtranjero = Double.parseDouble(StringUtils.isBlank(datosCarga.get("montoComisionExtranjero")) ? "0.00" : datosCarga.get("montoComisionExtranjero"));
            if (!StringUtils.isBlank(rutaArchivoCExtranjero) || !StringUtils.isBlank(folioComExtranjero) || montoComExtranjero > 0.0d) {
                validaDocExtranjeroCompleta(rutaArchivoCExtranjero, folioComExtranjero, montoComExtranjero);
                Carpeta oficioComExtranjero = FacturaManager.obtenCarpetaDestino(conn, c, "COMISION AL EXTRANJERO", u.getLogin());
                insertados += FacturaManager.insertaDoctoExtranjero(conn, rutaArchivoCExtranjero, oficioComExtranjero, c, u.getLogin());
                FacturaManager.insertaInformacionDoctoExtranjero(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, folioComExtranjero, montoComExtranjero);
                RelacionGastosManager.actualizaTipoDocumentacion(conn, ejercicioFiscal, nFolioPago, "lOficioExtranjero", insertados);
            }
            /*
			 * Verifica si adjuntaron Zip con facturas. En ese caso valida los
			 * CFDI
			 */
            String rutaArchivoFacturas = datosCarga.get("archivoZipFacturas");
            if (!StringUtils.isBlank(rutaArchivoFacturas)) {
                ExtraccionFacturas ef = extraeFacturas(rutaArchivoFacturas, c.getTipoCaso().getGavetaAsociada(), "CNF010405EG1", nFolioPago, false, false, tipoModulo);
                if (ef.getErrores().size() == 0) {
                    Map<String, ComponentesFactura> facturas = ef.getFacturas();
                    Carpeta cfdi = FacturaManager.obtenCarpetaDestino(conn, c, "CFDI", u.getLogin());
                    for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                        String facturaNombre = i.next();
                        log.debug("Object: {}", "Insertando factura [" + facturaNombre + "] ");
                        ComponentesFactura cf = facturas.get(facturaNombre);
                        insertados += FacturaManager.insertaArchivosFactura(conn, facturaNombre, cf, cfdi, c, u.getLogin());
                    }
                    FacturaManager.insertaInformacionFacturas(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, facturas);
                    RelacionGastosManager.actualizaTipoDocumentacion(conn, ejercicioFiscal, nFolioPago, "lPagoConFacturas", insertados);
                } else {
                    String msgRetorno = "";
                    String token = "";
                    for (int i = 0; i < ef.getErrores().size(); i++) {
                        msgRetorno += token + ef.getErrores().get(i);
                        token = "<br>";
                    }
                    throw new Exception(msgRetorno);
                }
            }
            /* Verifica si adjuntaron informacion para no comprobables. */
            String rutaArcAutNoComprobable = datosCarga.get("archivoAutNoComprobable");
            double montoNoComprobable = Double.parseDouble(StringUtils.isBlank(datosCarga.get("montoNoComprobable")) ? "0.00" : datosCarga.get("montoNoComprobable"));
            if (!StringUtils.isBlank(rutaArcAutNoComprobable) || montoNoComprobable > 0.0d) {
                validaNoComprobablesCompleta(rutaArcAutNoComprobable, montoNoComprobable);
                Carpeta oficioAutNoComprobable = FacturaManager.obtenCarpetaDestino(conn, c, "COMISION SIN FACTURAS", u.getLogin());
                insertados += FacturaManager.insertaDoctoAutNoComprobable(conn, rutaArcAutNoComprobable, oficioAutNoComprobable, c, u.getLogin());
                FacturaManager.insertaGastosNoComprobables(conn, nFolioPago, montoNoComprobable);
                RelacionGastosManager.actualizaTipoDocumentacion(conn, ejercicioFiscal, nFolioPago, "lNoComprobables", insertados);
            }
            /*
			 * Verifica si adjuntaron certificado de transito
			 */
            String rutaArchivoCertTransito = datosCarga.get("archivoOficioTransito");
            String folioCertTransito = datosCarga.get("noCertificado");
            double montoCertTransito = Double.parseDouble(StringUtils.isBlank(datosCarga.get("montoCertificado")) ? "0.00" : datosCarga.get("montoCertificado"));
            if (!StringUtils.isBlank(rutaArchivoCertTransito) || !StringUtils.isBlank(folioCertTransito) || montoCertTransito > 0) {
                validaCertificadoTransitoCompleto(rutaArchivoCertTransito, folioCertTransito, montoCertTransito);
                Carpeta certificadoTransito = FacturaManager.obtenCarpetaDestino(conn, c, "CERTIFICADO DE TRANSITO", u.getLogin());
                insertados += FacturaManager.insertaOficioDeTransito(conn, rutaArchivoCertTransito, certificadoTransito, c, u.getLogin());
                FacturaManager.insertaInformacionOficioTransito(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, folioCertTransito, montoCertTransito);
                RelacionGastosManager.actualizaTipoDocumentacion(conn, ejercicioFiscal, nFolioPago, "lCertificadoTransito", insertados);
            }
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int eliminaFacturas(String tituloAplicacion, String nFolioPago, String folioGestion, String login) throws Exception {
        Connection conn = null;
        int eliminados = 0;
        try {
            conn = getConnection();
            eliminados = FacturaManager.eliminaFacturas(conn, tituloAplicacion, nFolioPago, folioGestion, login);
            conn.commit();
            return eliminados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No fue posible realizar el rollback debido al siguiente error: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public boolean esPagoConFacturas(String gavetaAsociada, int nFolioPago) throws Exception {
        Connection con = null;
        try {
            con = getConnection();
            int nFacturasCapturadas = FacturaManager.facturasCapturadas(con, gavetaAsociada, nFolioPago);
            return nFacturasCapturadas > 0;
        } finally {
            CloseObject.closeObject(con, false);
        }
    }

    public ExtraccionFacturas extraeComprobantesPago(String rutaArchivo) {
        Connection conn = null;
        List<String> errores = new ArrayList<String>();
        /* Valida el archivo ZIP */
        Map<String, ComponentesFactura> facturas = new HashMap<String, ComponentesFactura>();
        try {
            conn = getConnection();
            errores = FacturaManager.validaArchivoFacturas(rutaArchivo);
            /*
			 * Si no hubo errores extrae las facturas en el archivo para
			 * validarlas
			 */
            if (errores.size() == 0) {
                if (getDirectorioTemporal() == null)
                    setDirectorioTemporal(System.getProperty("java.io.tmpdir"));
                facturas = ZipManager.extraeArchivosFactura(rutaArchivo, getDirectorioTemporal());
                facturas = cargaCFDI(facturas);
                /*
				 * Si esta activado, valida contra el WS del SAT la vigencia de
				 * la factura.
				 */
                if (isValidacionSAT()) {
                    List<String> erroresSAT = FacturaManager.validaSAT(facturas, notificaErroresSAT);
                    if (erroresSAT.size() > 0) {
                        errores.addAll(erroresSAT);
                        if (isNotificaErroresSAT()) {
                            ListadoCorreosPendientesBusinessLogic lcpbl = new ListadoCorreosPendientesBusinessLogic(getJniName());
                            lcpbl.setUsuario(getUsuario());
                            lcpbl.insertaNotificacionesFactura(erroresSAT);
                        }
                    }
                }
                /*
				 * El REP se correponde con uno o varios pagos, por lo que los
				 * pagos a los que hace referencia deben ser del mismo RFC
				 */
                // List<String> erroresBeneficiario =
                // FacturaManager.validaBeneficiarios(facturas,
                // getRFCBeneficiario());
                // if (erroresBeneficiario.size() > 0)
                // errores.addAll(erroresBeneficiario);
                /*
				 * El REP inlcuye un nodo con los pagos que reporta, por lo que
				 * estos pagos deben existir su UUI en pago factura.
				 */
                // List<String> erroresFactExistente =
                // FacturaManager.validaComprobantePagoRepetido(conn, facturas
                // );
                // if (erroresFactExistente.size() > 0)
                // errores.addAll(erroresFactExistente);
                /*
				 * El REP se considera como un CFDI "especial" solo debe existir
				 * un UUID de REP con uno y solo uno UUID de CFDI
				 */
                // List<String> erroresFactExistente =
                // FacturaManager.validaComprobantePagoRepetido(conn, facturas
                // );
                // if (erroresFactExistente.size() > 0)
                // errores.addAll(erroresFactExistente);
                /*
				 * El REP se considera como un CFDI "especial" solo debe existir
				 * un UUID de REP con uno y solo uno UUID de CFDI por ejercicio
				 */
                // List<String> erroresFactExistenteEFA =
                // FacturaManager.validaFacturasRepetidasEjerciciosAnteriores(conn,
                // facturas );
                // if (erroresFactExistenteEFA.size() > 0) {
                // errores.addAll(erroresFactExistenteEFA);
                // if (isNotificaErroresEFA()) {
                // ListadoCorreosPendientesBusinessLogic lcpbl = new
                // ListadoCorreosPendientesBusinessLogic(getJniName());
                // lcpbl.setUsuario(getUsuario());
                // lcpbl.insertaNotificacionesFacturaRepetida(erroresFactExistenteEFA);
                // }
                // }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            errores.add("Ocurrio el siguiente error de E/S mientras se leian las facturas: " + e);
        } catch (Exception e) {
            errores.add("Ocurrio el siguiente error mientras se leian las facturas: " + e);
            log.error(e.getMessage(), e);
        } finally {
            try {
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        ExtraccionFacturas ef = new ExtraccionFacturas();
        ef.setErrores(errores);
        ef.setFacturas(facturas);
        return ef;
    }

    public ExtraccionFacturas extraeFacturas(String rutaArchivo, String tipoPago, String rfc, int nFolioPago, boolean validaEmisor, boolean esNotaCredito, String tipoModulo) {
        Connection conn = null;
        try {
            conn = getConnection();
            ExtraccionFacturas ef = extraeFacturas(conn, rutaArchivo, tipoPago, rfc, nFolioPago, validaEmisor, esNotaCredito, (StringUtils.isBlank(tipoModulo) ? tipoPago : tipoModulo));
            return ef;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Problemas procesando archivo con facturas: " + e);
        } finally {
            try {
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public ExtraccionFacturas extraeFacturas(Connection conn, String rutaArchivo, String tipoPago, String rfc, int nFolioPago, boolean validaEmisor, boolean esNotaCredito, String tipoModulo) {
        List<String> errores = new ArrayList<String>();
        /* Valida el archivo ZIP */
        Map<String, ComponentesFactura> facturas = new HashMap<String, ComponentesFactura>();
        try {
            facturas = ZipManager.extraeArchivosFactura(rutaArchivo, getDirectorioTemporal());
            errores = FacturaManager.validaArchivoFacturas(rutaArchivo);
            if (errores.size() == 0) {
                facturas = cargaCFDI(facturas);
                errores.addAll(validateInvoices(conn, tipoPago, facturas, esNotaCredito, tipoModulo));
                errores.addAll(validateSinglePayInvoice(conn, rfc, tipoPago, facturas, validaEmisor, esNotaCredito));
            }
        } catch (Exception e) {
            errores.add("Ocurrio el siguiente error mientras se leian las facturas: " + e);
            log.error(e.getMessage(), e);
        }
        ExtraccionFacturas ef = new ExtraccionFacturas();
        ef.setErrores(errores);
        ef.setFacturas(facturas);
        return ef;
    }

    public ExtraccionFacturas extraeFacturasContrato(String rutaArchivo, String tipoContrato, String rfc, String idContrato, boolean esNotaCredito) {
        return extraeFacturasContrato(rutaArchivo, tipoContrato, rfc, idContrato, new BigDecimal(0.0f), new BigDecimal(0.0f), esNotaCredito);
    }

    public ExtraccionFacturas extraeFacturasContrato(String rutaArchivo, String tipoContrato, String rfc, String idContrato, BigDecimal montoContrato, BigDecimal montoIVAContrato, boolean esNotaCredito) {
        return extraeFacturasContrato(rutaArchivo, tipoContrato, rfc, idContrato, new BigDecimal(0.0f), new BigDecimal(0.0f), 1, esNotaCredito);
    }

    public ExtraccionFacturas extraeFacturasContrato(String rutaArchivo, String tipoContrato, String rfc, String idContrato, BigDecimal montoContrato, BigDecimal montoIVAContrato, int tipoFacturaGlobal, boolean esNotaCredito) {
        Connection conn = null;
        List<String> errores = new ArrayList<String>();
        /* Valida el archivo ZIP */
        Map<String, ComponentesFactura> facturas = new HashMap<String, ComponentesFactura>();
        try {
            conn = getConnection();
            errores = FacturaManager.validaArchivoFacturas(rutaArchivo);
            /*
			 * Si no hubo errores extrae las facturas en el archivo para
			 * validarlas
			 */
            if (errores.size() == 0) {
                if (getDirectorioTemporal() == null)
                    setDirectorioTemporal(System.getProperty("java.io.tmpdir"));
                facturas = ZipManager.extraeArchivosFactura(rutaArchivo, getDirectorioTemporal());
                facturas = cargaCFDI(facturas);
                /*
				 * Si esta activado, valida contra el WS del SAT la vigencia de
				 * la factura.
				 */
                if (isValidacionSAT()) {
                    List<String> erroresSAT = FacturaManager.validaSAT(facturas, notificaErroresSAT);
                    if (erroresSAT.size() > 0) {
                        errores.addAll(erroresSAT);
                        if (isNotificaErroresSAT()) {
                            ListadoCorreosPendientesBusinessLogic lcpbl = new ListadoCorreosPendientesBusinessLogic(getJniName());
                            lcpbl.setUsuario(getUsuario());
                            lcpbl.insertaNotificacionesFactura(erroresSAT);
                        }
                    }
                }
                if (montoContrato.compareTo(new BigDecimal(0.0f)) > 0) {
                    List<String> erroresMontos = FacturaManager.validaMontosContratoFactura(facturas, montoContrato, montoIVAContrato);
                    if (erroresMontos.size() > 0)
                        errores.addAll(erroresMontos);
                }
                List<String> erroresBeneficiario = FacturaManager.validaBeneficiarios(facturas, getRFCBeneficiario());
                if (erroresBeneficiario.size() > 0)
                    errores.addAll(erroresBeneficiario);
                List<String> erroresEmisor = FacturaManager.validaEmisor(facturas, rfc);
                if (erroresEmisor.size() > 0) {
                    errores.addAll(erroresEmisor);
                }
                List<String> erroresFactExistente = FacturaManager.validaFacturasRepetidasContratos(conn, facturas);
                if (erroresFactExistente.size() > 0)
                    errores.addAll(erroresFactExistente);
                List<String> erroresTipoFactura = new ArrayList<>();
                List<String> erroresConvenioFactura = new ArrayList<>();
                if (esNotaCredito) {
                    erroresTipoFactura = FacturaManager.validaTipoFactura(facturas, esNotaCredito);
                } else {
                    erroresTipoFactura = FacturaManager.validaCFDIContrato(conn, facturas);
                    if (erroresTipoFactura.size() > 0)
                        errores.addAll(erroresTipoFactura);
                    /*
					 * Validación para facturas de convenio modificatorio
					 */
                    if (tipoFacturaGlobal == 2) {
                        // Codificar
                        erroresConvenioFactura = FacturaManager.validaCFDIConvenio(conn, facturas, idContrato);
                        if (erroresConvenioFactura.size() > 0)
                            errores.addAll(erroresConvenioFactura);
                    }
                }
                if (erroresTipoFactura.size() > 0)
                    errores.addAll(erroresTipoFactura);
                if (erroresConvenioFactura.size() > 0)
                    errores.addAll(erroresConvenioFactura);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            errores.add("Ocurrio el siguiente error de E/S mientras se leian las facturas: " + e);
        } catch (Exception e) {
            errores.add("Ocurrio el siguiente error mientras se leian las facturas: " + e);
            log.error(e.getMessage(), e);
        } finally {
            try {
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        ExtraccionFacturas ef = new ExtraccionFacturas();
        ef.setErrores(errores);
        ef.setFacturas(facturas);
        return ef;
    }

    /**
     * @return the directorioTemporal
     */
    public String getDirectorioTemporal() {
        return directorioTemporal;
    }

    public String getJniName() {
        return jniName;
    }

    /**
     * @return the rFCBeneficiario
     */
    public String getRFCBeneficiario() {
        return RFCBeneficiario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public int insertaComprobantesDePago(Map<String, ComponentesFactura> comprobantesPago, String tipoContrato, String tipoPago, int folioPago) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            for (Iterator<String> i = comprobantesPago.keySet().iterator(); i.hasNext(); ) {
                String REPNombre = i.next();
                ComponentesFactura cf = comprobantesPago.get(REPNombre);
                cf.getComprobante().leePagos();
                if (cf.getComprobante().isCfd40()) {
                    insertaComprobantesDePago40(conn, REPNombre, tipoContrato, tipoPago, folioPago, cf);
                } else {
                    insertaComprobantesDePago33(conn, REPNombre, tipoContrato, tipoPago, folioPago, cf);
                }
            }
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    private int insertaComprobantesDePago33(Connection conn, String REPNombre, String tipoContrato, String tipoPago, int folioPago, ComponentesFactura cf) throws Exception {
        List<?> pagosLst = cf.getComprobante().getPagos();
        int insertados = 0;
        for (Iterator<?> itPagos = pagosLst.iterator(); itPagos.hasNext(); ) {
            Pago pagoRelacionado = (Pago) itPagos.next();
            List<DoctoRelacionado> doctosRelacionados = pagoRelacionado.getDoctoRelacionado();
            for (Iterator<DoctoRelacionado> itDoctos = doctosRelacionados.iterator(); itDoctos.hasNext(); ) {
                DoctoRelacionado docto = itDoctos.next();
                String uuidOrigen = docto.getIdDocumento();
                /*
				 * Obtiene el contrato para validar que exista una factura
				 * global con el UUID que se menciona en el cfdi
				 */
                String contratoPago = EgresoContratoManager.getNumeroContratoEgreso(conn, tipoPago, folioPago);
                if (!EgresoContratoManager.existeFacturaGlobal(conn, tipoContrato, contratoPago, uuidOrigen))
                    throw new Exception("No se encontro registrado el UUID de Factura Global[" + uuidOrigen + "] en el contrato[" + contratoPago + "] que corresponde a la combinacion " + tipoPago + "-" + folioPago);
                /*
				 * Encuentra el caso del pago para que se adjunte ahi el/los
				 * REP(s) que se incluyen
				 */
                Caso c = CasoManager.findByFolioLike(conn, tipoPago, String.valueOf(folioPago));
                if (c == null)
                    throw new Exception(String.format("No se encontro el caso para el pago/folio: [%s][%D]", tipoPago, folioPago));
                if (c.getIdGabinete() <= 0)
                    throw new Exception(String.format("No se encontro expediente en el tramite. Caso[%d]", c.getIdCaso()));
                String usrLogin = getUsuario().getLogin();
                Carpeta rep = FacturaManager.obtenCarpetaDestino(conn, c, "REP", usrLogin);
                log.debug("Object: {}", "Insertando factura [" + REPNombre + "] ");
                insertados = FacturaManager.insertaArchivosFactura(conn, REPNombre, cf, rep, c, usrLogin);
                FacturaManager.insertaInformacionReciboDePago33(conn, cf.getComprobante().getUUID(), docto, tipoPago, folioPago);
            }
        }
        return insertados;
    }

    private int insertaComprobantesDePago40(Connection conn, String REPNombre, String tipoContrato, String tipoPago, int folioPago, ComponentesFactura cf) throws Exception {
        List<?> pagosLst = cf.getComprobante().getPagos();
        int insertados = 0;
        for (Iterator<?> itPagos = pagosLst.iterator(); itPagos.hasNext(); ) {
            mx.grupocorasa.sat.common.Pagos20.Pagos.Pago pagoRelacionado = (mx.grupocorasa.sat.common.Pagos20.Pagos.Pago) itPagos.next();
            List<mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado> doctosRelacionados = pagoRelacionado.getDoctoRelacionado();
            for (Iterator<mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado> itDoctos = doctosRelacionados.iterator(); itDoctos.hasNext(); ) {
                mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado docto = itDoctos.next();
                String uuidOrigen = docto.getIdDocumento();
                /*
				 * Obtiene el contrato para validar que exista una factura
				 * global con el UUID que se menciona en el cfdi
				 */
                String contratoPago = EgresoContratoManager.getNumeroContratoEgreso(conn, tipoPago, folioPago);
                if (!EgresoContratoManager.existeFacturaGlobal(conn, tipoContrato, contratoPago, uuidOrigen))
                    throw new Exception("No se encontro registrado el UUID de Factura Global[" + uuidOrigen + "] en el contrato[" + contratoPago + "] que corresponde a la combinacion " + tipoPago + "-" + folioPago);
                /*
				 * Encuentra el caso del pago para que se adjunte ahi el/los
				 * REP(s) que se incluyen
				 */
                Caso c = CasoManager.findByFolioLike(conn, tipoPago, String.valueOf(folioPago));
                if (c == null)
                    throw new Exception(String.format("No se encontro el caso para el pago/folio: [%s][%D]", tipoPago, folioPago));
                if (c.getIdGabinete() <= 0)
                    throw new Exception(String.format("No se encontro expediente en el tramite. Caso[%d]", c.getIdCaso()));
                String usrLogin = getUsuario().getLogin();
                Carpeta rep = FacturaManager.obtenCarpetaDestino(conn, c, "REP", usrLogin);
                log.debug("Object: {}", "Insertando factura [" + REPNombre + "] ");
                insertados = FacturaManager.insertaArchivosFactura(conn, REPNombre, cf, rep, c, usrLogin);
                String xmlPath = cf.getXmlPathFile();
                EdrConceptoDispersion concepto = AdendaEdenredUtils.buscarConceptoDispersion(xmlPath, uuidOrigen);
                if (concepto != null) {
                    log.debug("Object: {}", "Se encontró la adenda EDENRED para UUID: " + uuidOrigen);
                    mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado doctoNuevo = new mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado();
                    doctoNuevo.setFolio(docto.getFolio());
                    doctoNuevo.setSerie(docto.getSerie());
                    doctoNuevo.setIdDocumento(docto.getIdDocumento());
                    doctoNuevo.setNumParcialidad(docto.getNumParcialidad());
                    doctoNuevo.setImpSaldoAnt(concepto.getImporteSaldoAnterior());
                    doctoNuevo.setImpPagado(concepto.getImportePagado());
                    doctoNuevo.setImpSaldoInsoluto(concepto.getImporteSaldoInsolutoPendientePago());
                    FacturaManager.insertaInformacionReciboDePago40(conn, cf.getComprobante().getUUID(), doctoNuevo, tipoPago, folioPago);
                } else {
                    FacturaManager.insertaInformacionReciboDePago40(conn, cf.getComprobante().getUUID(), docto, tipoPago, folioPago);
                }
            }
        }
        return insertados;
    }

    public File insertaComprobantesDePagoBatch(Map<String, ComponentesFactura> comprobantesPago) throws Exception {
        StringBuilder logStr = new StringBuilder("Archivo;RFC;RAZON SOCIAL;UUID;UUID PADRE;Tipo Pago;Folio Pago;Resultado\n");
        File result = File.createTempFile("CargaREPLog", ".csv", new File(System.getProperty("java.io.tmpdir")));
        for (Iterator<String> i = comprobantesPago.keySet().iterator(); i.hasNext(); ) {
            Connection conn = null;
            String archivo = "";
            String uuid = "";
            String uuidPadre = "";
            String rfc = "";
            String razonSocial = "";
            try {
                conn = getConnection();
                if (conn.getAutoCommit())
                    conn.setAutoCommit(false);
                log.trace("Conexion a DB Obtenida.");
                String REPNombre = i.next();
                ComponentesFactura cf = comprobantesPago.get(REPNombre);
                archivo = REPNombre;
                rfc = cf.getComprobante().getRFCEmisor();
                razonSocial = cf.getComprobante().getNombreEmisor();
                uuid = cf.getComprobante().getUUID();
                cf.getComprobante().leePagos();
                List<?> pagosLst = cf.getComprobante().getPagos();
                if (cf.getComprobante().isCfd33()) {
                    insertaComprobantesDePagoBatch33(conn, pagosLst, REPNombre, cf, logStr);
                } else {
                    insertaComprobantesDePagoBatch40(conn, pagosLst, REPNombre, cf, logStr);
                }
                conn.commit();
            } catch (Exception e) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.error("Problemas realizando rollback: " + e2, e2);
                    }
                log.error(e.getMessage(), e);
                StringBuilder logRenglon = new StringBuilder(StringUtils.trimToEmpty(archivo)).append(";").append(StringUtils.trimToEmpty(rfc)).append(";").append(StringUtils.trimToEmpty(razonSocial)).append(";").append(StringUtils.trimToEmpty(uuid)).append(";").append(StringUtils.trimToEmpty(uuidPadre)).append(";;;").append(e.toString()).append("\n");
                logStr.append(logRenglon);
            } finally {
                CloseObject.closeObject(conn, false);
            }
        }
        FileUtils.writeStringToFile(result, logStr.toString(), Charset.forName("UTF-8"));
        return result;
    }

    private StringBuilder insertaComprobantesDePagoBatch33(Connection conn, List<?> pagosLst, String REPNombre, ComponentesFactura cf, StringBuilder logStr) throws Exception {
        String rfc = cf.getComprobante().getRFCEmisor();
        String razonSocial = cf.getComprobante().getNombreEmisor();
        String uuid = cf.getComprobante().getUUID();
        for (Iterator<?> itPagos = pagosLst.iterator(); itPagos.hasNext(); ) {
            mx.grupocorasa.sat.common.Pagos10.Pagos.Pago pagoRelacionado = (mx.grupocorasa.sat.common.Pagos10.Pagos.Pago) itPagos.next();
            List<mx.grupocorasa.sat.common.Pagos10.Pagos.Pago.DoctoRelacionado> doctosRelacionados = pagoRelacionado.getDoctoRelacionado();
            for (Iterator<mx.grupocorasa.sat.common.Pagos10.Pagos.Pago.DoctoRelacionado> itDoctos = doctosRelacionados.iterator(); itDoctos.hasNext(); ) {
                StringBuilder logRenglon = new StringBuilder();
                logRenglon.append(REPNombre).append(";");
                logRenglon.append(StringUtils.trimToEmpty(rfc)).append(";");
                logRenglon.append(StringUtils.trimToEmpty(razonSocial)).append(";");
                logRenglon.append(uuid).append(";");
                mx.grupocorasa.sat.common.Pagos10.Pagos.Pago.DoctoRelacionado docto = itDoctos.next();
                String uuidPadre = docto.getIdDocumento();
                logRenglon.append(uuidPadre).append(";");
                DocumentoSAI doctoSAI = FacturaManager.getDocumentoOrigen(conn, uuidPadre);
                log.trace("Object: {}", "Documento encontrado: " + doctoSAI);
                Caso c = new Caso();
                c.setIdCaso(doctoSAI.getIdCaso());
                c = CasoManager.select(conn, c);
                if (c == null)
                    throw new Exception("No se encontro el caso: " + String.format("ID Caso[%d] UUID[%s]", doctoSAI.getIdCaso(), doctoSAI.getUUID()));
                if (c.getIdGabinete() <= 0)
                    throw new Exception(String.format("No se encontro expediente en el tramite. Caso[%d] UUID[%s]", doctoSAI.getIdCaso(), doctoSAI.getUUID()));
                String usrLogin = getUsuario().getLogin();
                Carpeta rep = FacturaManager.obtenCarpetaDestino(conn, c, "REP", usrLogin);
                log.debug("Object: {}", "Insertando factura [" + REPNombre + "] ");
                FacturaManager.insertaArchivosREP(conn, REPNombre, cf, rep, c, usrLogin);
                FacturaManager.insertaInformacionReciboDePago33(conn, cf.getComprobante().getUUID(), docto, doctoSAI.getTipoDocumento(), doctoSAI.getFolioDocumento());
                logRenglon.append(doctoSAI.getTipoDocumento()).append(";");
                logRenglon.append(doctoSAI.getFolioDocumento()).append(";");
                logRenglon.append("Adjuntado Exitosamente.\n");
                logStr.append(logRenglon);
                logRenglon = null;
            }
        }
        return logStr;
    }

    private StringBuilder insertaComprobantesDePagoBatch40(Connection conn, List<?> pagosLst, String REPNombre, ComponentesFactura cf, StringBuilder logStr) throws Exception {
        String rfc = cf.getComprobante().getRFCEmisor();
        String razonSocial = cf.getComprobante().getNombreEmisor();
        String uuid = cf.getComprobante().getUUID();
        int i = 1;
        for (Iterator<?> itPagos = pagosLst.iterator(); itPagos.hasNext(); ) {
            mx.grupocorasa.sat.common.Pagos20.Pagos.Pago pagoRelacionado = (mx.grupocorasa.sat.common.Pagos20.Pagos.Pago) itPagos.next();
            List<mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado> doctosRelacionados = pagoRelacionado.getDoctoRelacionado();
            for (Iterator<mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado> itDoctos = doctosRelacionados.iterator(); itDoctos.hasNext(); ) {
                StringBuilder logRenglon = new StringBuilder();
                logRenglon.append(REPNombre).append(";");
                logRenglon.append(StringUtils.trimToEmpty(rfc)).append(";");
                logRenglon.append(StringUtils.trimToEmpty(razonSocial)).append(";");
                logRenglon.append(uuid).append(";");
                mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado docto = itDoctos.next();
                String uuidPadre = docto.getIdDocumento();
                logRenglon.append(uuidPadre).append(";");
                DocumentoSAI doctoSAI = FacturaManager.getDocumentoOrigen(conn, uuidPadre);
                log.trace("Object: {}", "Documento encontrado: " + doctoSAI);
                Caso c = new Caso();
                c.setIdCaso(doctoSAI.getIdCaso());
                c = CasoManager.select(conn, c);
                if (c == null)
                    throw new Exception("No se encontro el caso: " + String.format("ID Caso[%d] UUID[%s]", doctoSAI.getIdCaso(), doctoSAI.getUUID()));
                if (c.getIdGabinete() <= 0)
                    throw new Exception(String.format("No se encontro expediente en el tramite. Caso[%d] UUID[%s]", doctoSAI.getIdCaso(), doctoSAI.getUUID()));
                String usrLogin = getUsuario().getLogin();
                Carpeta rep = FacturaManager.obtenCarpetaDestino(conn, c, "REP", usrLogin);
                log.debug("Object: {}", "Insertando factura [" + REPNombre + "] ");
                FacturaManager.insertaArchivosREP(conn, REPNombre, cf, rep, c, usrLogin);
                FacturaManager.insertaInformacionReciboDePago40(conn, cf.getComprobante().getUUID(), docto, doctoSAI.getTipoDocumento(), doctoSAI.getFolioDocumento());
                i++;
                logRenglon.append(doctoSAI.getTipoDocumento()).append(";");
                logRenglon.append(doctoSAI.getFolioDocumento()).append(";");
                logRenglon.append("Adjuntado Exitosamente.\n");
                logStr.append(logRenglon);
                logRenglon = null;
            }
        }
        return logStr;
    }

    public int insertaDoctosExtranjero(Caso c, Usuario u, String rutaArchivo, String cFactura, double montoDocto) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            if (c.getIdGabinete() <= 0)
                throw new Exception("No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas.");
            int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            Carpeta cfdi = FacturaManager.obtenCarpetaDestino(conn, c, "Oficios", u.getLogin());
            insertados += FacturaManager.insertaDoctoExtranjero(conn, rutaArchivo, cfdi, c, u.getLogin());
            FacturaManager.insertaInformacionDoctoExtranjero(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, cFactura, montoDocto);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int insertaFacturaContrato(Caso c, Usuario u, String tipoContrato, String idContrato, int ejercicioFiscal, Map<String, ComponentesFactura> facturas, boolean esNotaCredito) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            if (c.getIdGabinete() <= 0)
                throw new Exception("No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas.");
            Carpeta cfdi = FacturaManager.obtenCarpetaDestino(conn, c, "CFDI Contrato", u.getLogin());
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String facturaNombre = i.next();
                log.debug("Object: {}", "Insertando factura [" + facturaNombre + "] ");
                ComponentesFactura cf = facturas.get(facturaNombre);
                insertados += FacturaManager.insertaArchivosFactura(conn, facturaNombre, cf, cfdi, c, u.getLogin());
            }
            FacturaManager.insertaInformacionContratoFacturas(conn, tipoContrato, idContrato, ejercicioFiscal, facturas, esNotaCredito);
            if (esNotaCredito)
                FacturaManager.insertaRelacionNCContratoFacturas(conn, tipoContrato, idContrato, ejercicioFiscal, facturas, esNotaCredito);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int insertaFacturas(Connection conn, Caso c, Usuario u, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) throws Exception {
        int insertados = 0;
        try {
            int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            insertados = FacturaManager.insertaArchivosFactura(conn, c, u, facturas, esNotaCredito);
            FacturaManager.insertaInformacionFacturas(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, facturas, esNotaCredito);
            return insertados;
        } catch (Exception e) {
            throw e;
        }
    }

    public void insertaFacturas(Caso c, Usuario u, Map<String, ComponentesFactura> facturas) throws Exception {
        insertaFacturas(c, u, facturas, false);
    }

    public int insertaFacturas(Caso c, Usuario u, Map<String, ComponentesFactura> facturas, boolean esNotaCredito) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            insertados = insertaFacturas(conn, c, u, facturas, esNotaCredito);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int insertaOficio(Caso c, Usuario u, Map<String, String> datosCarga) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            String rutaArchivo = datosCarga.get("archivoOficioPP");
            String noOficio = "PAGO_SIN_FACTURA";
            String destinoGasto = datosCarga.get("destinoGasto");
            int ejercicioFiscal = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            double montoOficio = (Double.parseDouble(datosCarga.get("montoBrutoPP")));
            double montoImpuestos = (Double.parseDouble(datosCarga.get("montoImpuestosPP")));
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            if (c.getIdGabinete() <= 0)
                throw new Exception("No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas.");
            int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            RelacionGastosManager.insertaTipoDocumentacionRG(conn, nFolioPago, destinoGasto);
            Carpeta cfdi = FacturaManager.obtenCarpetaDestino(conn, c, "PAGO SIN FACTURA", u.getLogin());
            insertados += FacturaManager.insertaOficioDePago(conn, rutaArchivo, cfdi, c, u.getLogin());
            FacturaManager.insertaInformacionOficio(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, noOficio, montoOficio, montoImpuestos);
            RelacionGastosManager.actualizaTipoDocumentacion(conn, ejercicioFiscal, nFolioPago, "lOficioProveedor", insertados);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int insertaOficioCtoFederalizado(Caso c, Usuario u, String rutaArchivo, String noOficio, double montoOficio) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            if (c.getIdGabinete() <= 0)
                throw new Exception("No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas.");
            int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            /**/
            FacturaManager.insertaInformacionOficioCtoFederalizado(conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, noOficio, montoOficio);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public boolean isNotificaErroresEFA() {
        return notificaErroresEFA;
    }

    public boolean isNotificaErroresSAT() {
        return notificaErroresSAT;
    }

    public boolean isPermiteVersionAnterior() {
        return permiteVersionAnterior;
    }

    public boolean isValidacionSAT() {
        return validacionSAT;
    }

    /**
     * @param directorioTemporal
     *            the directorioTemporal to set
     */
    public void setDirectorioTemporal(String directorioTemporal) {
        this.directorioTemporal = directorioTemporal;
    }

    public void setJniName(String jniName) {
        this.jniName = jniName;
    }

    public void setNotificaErroresEFA(boolean notificaErroresEFA) {
        this.notificaErroresEFA = notificaErroresEFA;
    }

    public void setNotificaErroresSAT(boolean notificaErroresSAT) {
        this.notificaErroresSAT = notificaErroresSAT;
    }

    public void setPermiteVersionAnterior(boolean permiteVersionAnterior) {
        this.permiteVersionAnterior = permiteVersionAnterior;
    }

    /**
     * @param rFCBeneficiario
     *            the rFCBeneficiario to set
     */
    public void setRFCBeneficiario(String rFCBeneficiario) {
        RFCBeneficiario = rFCBeneficiario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setValidacionSAT(boolean validacionSAT) {
        this.validacionSAT = validacionSAT;
    }

    public void validaCertificadoTransitoCompleto(String rutaArchivoCertTransito, String folioCertTransito, double montoCertTransito) throws Exception {
        String mensaje = "";
        if (StringUtils.isBlank(rutaArchivoCertTransito))
            mensaje = "No se recibio archivo a adjuntar;";
        if (StringUtils.isBlank(folioCertTransito))
            mensaje = "No se recibio Folio de Certificado de Transito;";
        if (montoCertTransito <= 0.0d) {
            mensaje += "No se recibio el monto del certificado de transito;";
        }
        if (mensaje.length() > 0)
            throw new Exception("Informacion incompleta: " + mensaje);
    }

    public void validaDocExtranjeroCompleta(String rutaArchivo, String cFactura, double montoDocto) throws Exception {
        String mensaje = "";
        if (StringUtils.isBlank(rutaArchivo))
            mensaje = "No se recibio archivo a adjuntar;";
        // if (StringUtils.isBlank(cFactura))
        // mensaje += "No se recibio el No. de oficio;";
        if (montoDocto <= 0.0d) {
            mensaje += "No se recibio el monto a comprobar;";
        }
        if (mensaje.length() > 0)
            throw new Exception("Informacion incompleta: " + mensaje);
    }

    public void validaNoComprobablesCompleta(String rutaArcAutNoComprobable, double montoNoComprobable) throws Exception {
        String mensaje = "";
        if (StringUtils.isBlank(rutaArcAutNoComprobable))
            mensaje = "No se recibio archivo a adjuntar;";
        if (montoNoComprobable <= 0.0d) {
            mensaje += "No se recibio el monto no comprobable;";
        }
        if (mensaje.length() > 0)
            throw new Exception("Informacion incompleta: " + mensaje);
    }

    public boolean isFacturaVales() {
        return facturaVales;
    }

    public void setFacturaVales(boolean facturaVales) {
        this.facturaVales = facturaVales;
    }

    public void setViaticos(boolean viaticos) {
        // TODO Auto-generated method stub
        this.viaticos = viaticos;
    }

    public List<String> validacionesPreAutorizaRelacionGastos(int nFolioRelacionGastos) {
        Connection conn = null;
        try {
            conn = getConnection();
            return FacturaManager.validacionesPreAutorizaRelacionGastos(conn, nFolioRelacionGastos);
        } catch (SQLException e) {
            throw new RuntimeException("Error mientras se validaban las facturas al finalizar RG: " + e, e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
