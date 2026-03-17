package com.syc.contable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.axtel.egresos.core.EgresoPAGODIVERSO;
import com.axtel.egresos.exceptions.EgresoException;
import com.axtel.egresos.exceptions.LayoutEgresoException;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.core.CalculaImpuestosRetencionesManager;
import com.syc.contable.core.EgresoImpuestos;
import com.syc.contable.core.PagosDiversosManager;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.DetallePago;
import com.syc.egresos.PagoCalendarioBussinessLogic;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.impl.EgresoPAGODIVERSODetalle;
import com.syc.egresos.core.impl.EgresoPAGODIVERSOEncabezado;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.DefaultFolioGenerator;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.PasivoDiferidoManager;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import com.syc.sai.procesos.CambiaEPRelacionGastos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class PagosDiversosBussinessLogic extends DataSourceManager {

    public class EgresoMasivo {

        private List<DetallePago> detalle;

        private EgresoPAGODIVERSOEncabezado encabezado;

        public EgresoMasivo(EgresoPAGODIVERSOEncabezado encabezado, List<DetallePago> detalle) {
            this.encabezado = encabezado;
            this.detalle = detalle;
        }

        public List<DetallePago> getDetalle() {
            return detalle;
        }

        public EgresoPAGODIVERSOEncabezado getEncabezado() {
            return encabezado;
        }

        public void setDetalle(List<DetallePago> detalle) {
            this.detalle = detalle;
        }

        public void setEncabezado(EgresoPAGODIVERSOEncabezado encabezado) {
            this.encabezado = encabezado;
        }

        public void setFolioPago(int folio) {
            getEncabezado().setFolioPago(folio);
            for (DetallePago detalle : getDetalle()) {
                detalle.setFolioPago(folio);
            }
        }
    }

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    private String[] encabezadosLayoutBanco = new String[] { "INS_OPERANTE", "TP_CLAVE", "INS_CLAVE", "OP_CONCEPTO_PAGO", "OP_MONTO", "OP_NOM_BEN", "OP_REF_NUMERICA", "TC_CLAVE_BEN", "OP_CUENTA_BEN", "OP_CVE_RASTREO" };

    public PagosDiversosBussinessLogic() {
    }

    public PagosDiversosBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public boolean actualizaCompromisos(Integer nEnviadoSICOP, String caNoCompromiso) throws Exception {
        boolean regActualizado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regActualizado = PagosDiversosManager.updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return regActualizado;
    }

    public boolean ActualizaStatus(String listaFolios) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            PagosDiversosManager.UpdateStatus(conn, listaFolios);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.rollback();
                throw e;
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return true;
    }

    public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagosDiversosManager.CreaDocumentacionComprobatoria(conn, listaIds);
            PagosDiversosManager.updateHeaderCompromisos(conn, listaIds);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public ArrayList<String> ArmaLayoutBanco(String nFolios, boolean tipoBanorte, String sCuentaLayout) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagosDiversosManager.ArmaLayoutBanco(conn, nFolios, tipoBanorte, sCuentaLayout);
            int act = PagosDiversosManager.avanzaLayout(conn, nFolios);
            if (act > 0)
                conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public ArrayList<String> ArmaLayoutBancoIP(String nFolios, boolean tipoBanorte, String sCuentaLayout) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagosDiversosManager.ArmaLayoutBancoIP(conn, nFolios, tipoBanorte, sCuentaLayout);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public ArrayList<String> buscaCompromisos(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, String sUsuario, String sValorUMA) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PagosDiversosManager.BuscaCompromisos(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario);
            conn.commit();
            return arrListaComp;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas con el rollback: " + e2);
                }
            }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    private void creaEncabezado(Sheet worksheet) {
        Row row1 = worksheet.createRow((short) 0);
        for (int i = 0; i < encabezadosLayoutBanco.length; i++) {
            row1.createCell(i, CellType.STRING).setCellValue(encabezadosLayoutBanco[i]);
        }
    }

    public void generaLayoutAmbiental(HttpServletResponse response, String sFolioQuery, String sCuentaBancaria, String sFecha, String sLeyenda, String sUsuario) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            /*
			 * Estas funciones se llaman para que inserte en las tablas de layut
			 */
            buscaCompromisos(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario, "0");
            ArmaDocumentoComprobatorio(sFolioQuery);
            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet worksheet = workbook.createSheet("layout pagos");
            creaEncabezado(worksheet);
            worksheet = PagosDiversosManager.generaLayoutAmbiental(conn, worksheet, sFolioQuery);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Content-Type", "application/vnd.ms-excel");
            response.addHeader("Content-Disposition", "inline; filename=\"layout.xls\"");
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    private int insertaCalendario(Connection conn, List<DetallePago> detalle) throws LayoutEgresoException {
        try {
            int insertados = 0;
            PagoCalendarioBussinessLogic pcbl = new PagoCalendarioBussinessLogic();
            for (DetallePago detallePago : detalle) {
                insertados += pcbl.insertaCalendarioPagoCompromiso(conn, detallePago);
            }
            return insertados;
        } catch (Exception e) {
            throw new LayoutEgresoException(e);
        }
    }

    public boolean insertaLineaLayout(String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        boolean regInsertado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regInsertado = PagosDiversosManager.insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return regInsertado;
    }

    // TODO Para esta version asume por defecto la retencion ISR, seria deseable
    // puedan indicar el monto y el tipo de retencion.
    private DetallePago instanceDetail(Connection conn, EgresoPAGODIVERSOEncabezado encabezado, Row renglon) throws LayoutEgresoException {
        String ep = renglon.getCell(1).getStringCellValue();
        if (StringUtils.isBlank(ep))
            throw new LayoutEgresoException("La ep esta vacia. La columna[1] del renglon [" + renglon.getRowNum() + "] no contiene informacion.");
        String tipoMovto = renglon.getCell(2).getStringCellValue();
        if (StringUtils.isBlank(tipoMovto))
            throw new LayoutEgresoException("El tipo de movimiento esta vacio. La columna[2] del renglon [" + renglon.getRowNum() + "] no contiene informacion.");
        String tipoConcepto = renglon.getCell(3).getStringCellValue();
        if (StringUtils.isBlank(tipoConcepto))
            throw new LayoutEgresoException("El tipo de concepto esta vacio. La columna[3] del renglon [" + renglon.getRowNum() + "] no contiene informacion.");
        if (renglon.getCell(4) == null || renglon.getCell(4).getNumericCellValue() <= 0.0)
            throw new LayoutEgresoException("El importe es un valor no valido. La columna[4] del renglon [" + renglon.getRowNum() + "] debe ser numerico mayor a cero.");
        BigDecimal importeMasIVA = new BigDecimal(renglon.getCell(4).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
        DetallePago detallePago = new DetallePago();
        try {
            detallePago.setEp(ep);
            detallePago.setIdTipoConcepto(tipoConcepto);
            detallePago.setIdTipoMovimiento(tipoMovto);
            detallePago.setImporteBruto(importeMasIVA);
            detallePago.setTipoPago(encabezado.getTipoPago());
            detallePago.setImporteRetencion(new BigDecimal("0.00"));
            return detallePago;
        } catch (Exception e) {
            throw new LayoutEgresoException(e);
        }
    }

    private EgresoPAGODIVERSOEncabezado instanceHeader(Row renglon, Usuario u) {
        EgresoPAGODIVERSOEncabezado header = new EgresoPAGODIVERSOEncabezado();
        header.setFolioPagoDiverso(renglon.getCell(1).getStringCellValue());
        header.setIdContrato(renglon.getCell(1).getStringCellValue());
        header.setRfc(renglon.getCell(2).getStringCellValue());
        header.setNombre(renglon.getCell(3).getStringCellValue());
        header.setCTAB(renglon.getCell(4).getStringCellValue());
        header.setImporteBruto(new BigDecimal(renglon.getCell(5).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        header.setImporteIVA(new BigDecimal(renglon.getCell(6).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        header.setImporteRetencion(new BigDecimal(renglon.getCell(7).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        header.setImporteNeto(new BigDecimal(renglon.getCell(8).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        header.setConcepto(renglon.getCell(9).getStringCellValue());
        header.setDescripcionPoliza(renglon.getCell(9).getStringCellValue());
        header.setFechaAplicacion(new Date());
        header.setFechaCaptura(new Date());
        header.setFechaCarga(new Date());
        header.setFechaRevision(new Date());
        header.setFechaProgramadaPago(Util.todayPlus(2));
        header.setIdUsuarioCaptura(u.getLogin());
        header.setIdDestinoGasto(renglon.getCell(10).getStringCellValue());
        header.setNumEmpleadoVoBo((int) renglon.getCell(11).getNumericCellValue());
        header.setNumEmpleadoAut((int) renglon.getCell(12).getNumericCellValue());
        header.setEsFirmaElectronica(renglon.getCell(13).getStringCellValue().charAt(0));
        header.setCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
        header.setImporteMasIva(header.getImporteBruto().add(header.getImporteIVA()));
        header.setIdConcepto("");
        header.setIdTipoDocumento("0");
        header.setIdTipoOperacion("1");
        header.setUnidadResponsable(u.getU_UR_Orig());
        header.setNumEmpleadoElab(Integer.parseInt(u.getNumeroEmpleado()));
        header.setDescripcionPoliza(renglon.getCell(9).getStringCellValue());
        header.setEnviadoSICOP(0);
        header.setNumPagoAMF("***");
        return header;
    }

    public int procesaPagosMasivo(Connection conn, File inputLayout, Usuario u, String reportPath) throws LayoutEgresoException, FirmaElectronicaException {
        String ejercicioFiscal = "";
        try {
            ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        } catch (Exception e) {
            throw new LayoutEgresoException("Problemas encontrando el ejercicio fiscal: " + e.toString());
        }
        List<EgresoMasivo> pagos = readPagoBeneficiariosLayout(conn, inputLayout, u);
        FolioGeneratorInterface fg = new DefaultFolioGenerator();
        CFSequenceManager sm = CFSequenceManager.getInstance(GestionInterface.ATT_CONEXION);
        AccountingEngine ae = new AccountingEngine();
        ae.setValidaInsuficienciaDeSaldo(true);
        int pagosProcesados = 0;
        int insertados = 0;
        try {
            for (EgresoMasivo pago : pagos) {
                Caso c = CasoManager.nuevoCaso(conn, u, EgresoPAGODIVERSO.ID_TC, fg);
                c.getCasoDato("FOLIO").setValor(c.getFolio());
                c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getTodayESMX());
                c.getCasoDato("EJERCICIO_FISCAL").setValor(ejercicioFiscal);
                c.getCasoDato("OPERADOR").setValor(u.getNombre());
                c.getCasoDato("MONEDA").setValor("MXP");
                c.getCasoDato("APLICADO_CONT").setValor("true");
                c.getCasoDato("FECHA_AP_CONT").setValor(Util.getTodayESMX());
                Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
                int idGabinete = AplicacionManager.createExpediente(conn, "SSANTIAGO", c, app);
                c.setIdGabinete(idGabinete);
                CasoManager.update(conn, c);
                pago.getEncabezado().setEjercicioFiscal(ejercicioFiscal);
                pago.getEncabezado().setContrarecibo(pago.getEncabezado().generaContrarecibo(conn, sm));
                pago.setFolioPago(Util.folio(c));
                pago.getEncabezado().setNoFactura(String.valueOf(pago.getEncabezado().getFolioPago()));
                pago.getEncabezado().setMes(String.valueOf(Util.getCurrentMonth(conn)));
                insertados = pago.getEncabezado().save(conn);
                insertados += insertaCalendario(conn, pago.getDetalle());
                List<EgresoCalendario> calEgreso = EgresosManager.cargaCalendarioEgreso(conn, pago.getEncabezado().getTipoPago(), pago.getEncabezado().getFolioPago());
                List<EgresoImpuestos> impuestos = new ArrayList<>();
                List<EgresoRetencion> retenciones = new ArrayList<>();
                EgresoImpuestos impuesto = new EgresoImpuestos();
                impuesto.setFolioPago(pago.getEncabezado().getFolioPago());
                impuesto.setIdContrato(pago.getEncabezado().getFolioPagoDiverso());
                impuesto.setMontoIVA(new BigDecimal(0.0));
                impuesto.setMontoOtrosImpuestos(new BigDecimal(0.0));
                impuesto.setMontoSinIVA(pago.getEncabezado().getImporteIVA());
                impuesto.setTotal(pago.getEncabezado().getImporteIVA());
                impuesto.setTotalMasIVA(pago.getEncabezado().getImporteIVA());
                impuestos.add(impuesto);
                EgresoRetencion retencion = new EgresoRetencion();
                retencion.setComponente("importeISROtros");
                retencion.setContrarecibo(pago.getEncabezado().getContrarecibo());
                retencion.setFolioPago(pago.getEncabezado().getFolioPago());
                retencion.setIdContrato(pago.getEncabezado().getFolioPagoDiverso());
                retencion.setIdTipoRetencion(4);
                retencion.setImporteBruto(pago.getEncabezado().getImporteRetencion());
                retencion.setImporteRetencion(pago.getEncabezado().getImporteRetencion());
                retenciones.add(retencion);
                EgresoPAGODIVERSODetalle renglonNuevo = new EgresoPAGODIVERSODetalle();
                renglonNuevo.setFolioPAGODIVERSO(pago.getEncabezado().getFolioPago());
                renglonNuevo.setNumeroMes(Integer.parseInt(pago.getEncabezado().getMes()));
                renglonNuevo.setEjercicioFiscal(ejercicioFiscal);
                renglonNuevo.setcIdEntidadContable(pago.getEncabezado().getCentroContable());
                renglonNuevo.setcIdRelacion(pago.getEncabezado().getNombre());
                renglonNuevo.setNumeroPoliza(0);
                renglonNuevo.setIdTipoMovimiento(pago.getDetalle().get(0).getIdTipoMovimiento());
                renglonNuevo.setIdTipoConcepto(pago.getDetalle().get(0).getIdTipoConcepto());
                renglonNuevo.setCentroContable(pago.getEncabezado().getCentroContable());
                renglonNuevo.setRfc(pago.getEncabezado().getRfc());
                renglonNuevo.setAlm(pago.getEncabezado().getAlm());
                renglonNuevo.setDocumentoAplicado("");
                renglonNuevo.setFolioPoliza(0);
                renglonNuevo.setPeriodo13('N');
                renglonNuevo.setAdefas('N');
                renglonNuevo.setAltaAlmacen("||");
                List<EgresoDetalle> detalle = new ArrayList<>();
                detalle.add(renglonNuevo);
                CalculaImpuestosRetencionesManager.recalculaImpuestosRetenciones(conn, pago.getEncabezado(), detalle, calEgreso, retenciones, impuestos, new ArrayList<>());
                ae.makeAccountingApplication(conn, "PAGODIVERSO", String.valueOf(pago.getEncabezado().getFolioPago()), "tPAGODIVERSOEncabezado", "tPAGODIVERSODetalle", "nFolioPAGODIVERSO");
                PasivoDiferidoManager.aplicarPasivoDiferido(conn, "PAGODIVERSO", String.valueOf(pago.getEncabezado().getFolioPago()), "tPAGODIVERSOEncabezado", "tPAGODIVERSODetalle", "nFolioPAGODIVERSO");
                if ("S".equalsIgnoreCase(String.valueOf(pago.getEncabezado().getEsFirmaElectronica()))) {
                    SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
                    solicitudPagoPrinter.setDocName("Solicitud de Pago Firmada");
                    solicitudPagoPrinter.setDetail("tPAGODIVERSODetalle");
                    solicitudPagoPrinter.setDocument("PAGODIVERSO");
                    solicitudPagoPrinter.setField("nFolioPAGODIVERSO");
                    solicitudPagoPrinter.setFileExtension("pdf");
                    solicitudPagoPrinter.setHeader("tPAGODIVERSOEncabezado");
                    solicitudPagoPrinter.setIdField(pago.getEncabezado().getFolioPago());
                    solicitudPagoPrinter.setReportPath(reportPath);
                    solicitudPagoPrinter.setUsuario(u);
                    FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Solicitud de Pago", false);
                    FirmaElectronicaManager.avanzaEstatusSICOP(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.VO_BO_SICOP);
                }
                avanzaCasoConsulta(conn, c, u);
                log.debug("Object: " + String.valueOf("Se inserto: " + insertados + " en encabeazado;"));
                pagosProcesados++;
            }
        } catch (SQLException | GestionException | EgresoException | AccountingEngineException e) {
            throw new LayoutEgresoException(e);
        }
        return pagosProcesados;
    }

    public List<EgresoMasivo> readPagoBeneficiariosLayout(Connection conn, File inputLayout, Usuario u) throws LayoutEgresoException {
        String ef = null;
        try {
            ef = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        } catch (Exception e) {
            throw new LayoutEgresoException("No fue posible encontrar el EF Activo debido al error: " + e);
        }
        List<String> errores = new ArrayList<>();
        List<EgresoMasivo> pagos = new ArrayList<>();
        int numRenglon = 0;
        InputStream fis = null;
        XSSFWorkbook layout = null;
        XSSFSheet hojaInformacion = null;
        try {
            fis = new FileInputStream(inputLayout);
            layout = new XSSFWorkbook(fis);
            hojaInformacion = layout.getSheetAt(0);
            Iterator<Row> rowIterator = hojaInformacion.iterator();
            boolean encabezadoCargado = false;
            while (rowIterator.hasNext()) {
                numRenglon++;
                try {
                    Row renglon = rowIterator.next();
                    String tipoRenglon = (renglon.getCell(0) != null ? renglon.getCell(0).getStringCellValue() : "");
                    if (EgresoEncabezado.HEADER.equals(tipoRenglon)) {
                        Row renglonSiguiente = hojaInformacion.getRow(renglon.getRowNum() + 1);
                        if (renglonSiguiente == null || (renglonSiguiente != null && EgresoEncabezado.HEADER.equals(renglonSiguiente.getCell(0).getStringCellValue()))) {
                            errores.add("Renglon: " + renglon.getRowNum() + " Encabezado sin detalle.");
                            encabezadoCargado = false;
                            continue;
                        }
                        EgresoPAGODIVERSOEncabezado headerObj = instanceHeader(renglon, u);
                        headerObj.setEjercicioFiscal(ef);
                        pagos.add(new EgresoMasivo(headerObj, new ArrayList<>()));
                        if (!cuentaBancariaValida(conn, headerObj.getRfc(), headerObj.getCTAB()))
                            errores.add("Renglon: " + renglon.getRowNum() + " No corresponde la cuenta bancaria " + headerObj.getCTAB() + " con el beneficiario " + headerObj.getRfc() + " o la cuenta esta inactiva.");
                        encabezadoCargado = true;
                    } else if (EgresoEncabezado.DETAIL.equals(tipoRenglon)) {
                        if (!encabezadoCargado) {
                            errores.add("Renglon: " + renglon.getRowNum() + "detalle sin encabezado.");
                            continue;
                        }
                        pagos.get(pagos.size() - 1).getDetalle().add(instanceDetail(conn, pagos.get(pagos.size() - 1).getEncabezado(), renglon));
                    } else {
                        errores.add("Renglon: " + renglon.getRowNum() + "tipo de renglon desconocido: [" + tipoRenglon + "]");
                    }
                } catch (Exception e) {
                    errores.add("Error generado por la informacion del renglon " + numRenglon + " : " + e.toString());
                    log.error("Error en el renglon " + numRenglon, e);
                }
            }
            if (errores.size() > 0)
                throw new LayoutEgresoException(errores);
            return pagos;
        } catch (IOException e) {
            log.error("Error general procesando archivo: " + e, e);
            errores.add("Error general procesando archivo. " + e.toString());
            throw new LayoutEgresoException(errores);
        } finally {
            CloseObject.closeObject(fis);
            CloseObject.closeObject(layout);
        }
    }

    private boolean cuentaBancariaValida(Connection conn, String rfc, String ctab) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select COUNT(*) AS existe from tBeneficiarioCuentasBancarias WITH (NOLOCK)  where  cStatusCuenta = 1 and subCuentaBancaria = ? and replace(drfc,'-','') = replace(?,'-','')");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, ctab);
            ps.setString(2, rfc);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static Caso avanzaCasoConsulta(Connection conn, Caso caso, Usuario u) throws LayoutEgresoException {
        try {
            return CambiaEPRelacionGastos.avanzaCaso(conn, caso, u.getLogin(), "", new String[] { "CONSULTA_PAGODIVERSO" }, new String[] { "consulta_factura" }, Util.readValuesCasoDato(caso.getCasoDato()), null);
        } catch (Exception e) {
            throw new LayoutEgresoException(e.toString());
        }
    }

    public int procesaPagosMasivo(File file, Usuario u, String reportDir) throws LayoutEgresoException {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = procesaPagosMasivo(conn, file, u, reportDir);
            conn.commit();
            return insertados;
        } catch (LayoutEgresoException | SQLException | FirmaElectronicaException e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new LayoutEgresoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean esPagoPROFOEM(int folioPagoDiverso) {
        Connection conn = null;
        try {
            conn = getConnection();
            return EgresosManager.esPagoPROFOEM(conn, "PAGODIVERSO", folioPagoDiverso);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
