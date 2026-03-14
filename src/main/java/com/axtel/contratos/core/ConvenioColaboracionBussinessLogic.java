package com.axtel.contratos.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.contratos.exception.LayoutConvenioColaboracionException;
import com.axtel.egresos.compromiso.Compromiso;
import com.axtel.egresos.compromiso.CompromisoDetalle;
import com.axtel.egresos.compromiso.CompromisoEncabezado;
import com.axtel.egresos.compromiso.PrecompromisoFinancieroDetalle;
import com.axtel.egresos.compromiso.PrecompromisoFinancieroEncabezado;
import com.syc.contable.AccountingEngine;
import com.syc.contable.ContratoDiversoBusinessLogic;
import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.PrecompromisoFinanciero;
import com.syc.crud.dsmngr.DataSourceManager;
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
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvenioColaboracionBussinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ConvenioColaboracionBussinessLogic.class);

    /**
     * Instancia un nuevo encabezado del convenio de colaboracion obteniendo los
     * datos del request.
     *
     * @param req
     *            Request
     * @param folio
     *            Folio del tramite
     * @return Objeto Convenio Encabezado extraido del request
     * @throws ParseException
     */
    private static ConvenioColaboracionEncabezado instaceHeader(HttpServletRequest req, Usuario u) throws ParseException {
        ConvenioColaboracionEncabezado cce = new ConvenioColaboracionEncabezado();
        cce.setConceptoConvenio(req.getParameter("concepto"));
        cce.setEsPlurianual(req.getParameter("esPlurianual"));
        cce.setFechaCaptura(new Date());
        cce.setFechaConvenioInicio(Util.stringToDate(req.getParameter("fInicio"), "dd/MM/yyyy"));
        cce.setFechaFinConvenio(Util.stringToDate(req.getParameter("fFin"), "dd/MM/yyyy"));
        cce.setFechaFirmaConvenio(Util.stringToDate(req.getParameter("fInicio"), "dd/MM/yyyy"));
        cce.setIdContrato(req.getParameter("cIdContrato"));
        cce.setIdUnidadAdministrativa(u.getU_UR());
        cce.setLoginCaptura(u.getLogin());
        cce.setmImporteBruto(new BigDecimal(req.getParameter("importeBruto")).setScale(2, RoundingMode.HALF_UP));
        cce.setmImporteConvenio(new BigDecimal(req.getParameter("importeTotal")).setScale(2, RoundingMode.HALF_UP));
        cce.setmImporteIVA(new BigDecimal(req.getParameter("importeIVA")).setScale(2, RoundingMode.HALF_UP));
        cce.setmImporteTotal(new BigDecimal(req.getParameter("importeTotal")).setScale(2, RoundingMode.HALF_UP));
        cce.setPorcIvaAplicable(Integer.parseInt(req.getParameter("pcIVA")));
        cce.setRfc(req.getParameter("rfc"));
        cce.setUnidadEjecutora(u.getU_UR());
        cce.setCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
        return cce;
    }

    /**
     * Instancia un nuevo detalle del convenio de colaboracion obteniendo los
     * datos del request.
     *
     * @param req
     *            Request
     * @param folio
     *            Folio del tramite
     * @return Objeto Convenio extraido del request
     * @throws ParseException
     */
    private static List<ConvenioColaboracionDetalle> instanceDetail(HttpServletRequest req, int folio) {
        List<ConvenioColaboracionDetalle> detalle = new ArrayList<ConvenioColaboracionDetalle>();
        String[] eps = req.getParameterValues("ep");
        for (String ep : eps) {
            ConvenioColaboracionDetalle ccd = new ConvenioColaboracionDetalle();
            ccd.setEp(ep);
            ccd.setFolioConvenioColaboracion(folio);
            detalle.add(ccd);
        }
        return detalle;
    }

    /**
     * Instancia un nuevo convenio de colaboracion obteniendo los datos del
     * request.
     *
     * @param req
     *            Request
     * @param folio
     *            Folio del tramite
     * @return Objeto Convenio extraido del request
     * @throws ParseException
     */
    public static ConvenioColaboracion instanceFromRequest(HttpServletRequest req, int folio, Usuario u) throws ParseException {
        ConvenioColaboracionEncabezado cce = instaceHeader(req, u);
        cce.setFolioConvenioColaboracion(folio);
        log.trace("Object: {}", "Leido: " + cce);
        List<ConvenioColaboracionDetalle> detalle = instanceDetail(req, folio);
        log.trace("Object: {}", "Leido: " + detalle);
        ConvenioColaboracion cc = new ConvenioColaboracion(cce, detalle);
        return cc;
    }

    public ConvenioColaboracionBussinessLogic() {
    }

    public ConvenioColaboracionBussinessLogic(String jniName) {
        super.init(jniName);
    }

    /**
     * @param conn
     * @param inputLayout
     * @param u
     * @return
     * @throws LayoutConvenioColaboracionException
     */
    public List<ConvenioColaboracion> generaConveniosPagoBeneficiarioMasivo(Connection conn, File inputLayout, Usuario u) throws LayoutConvenioColaboracionException {
        List<ConvenioColaboracion> convenios = readConvenioPagoBeneficiariosLayout(conn, inputLayout, u);
        FolioGeneratorInterface fg = new DefaultFolioGenerator();
        CFSequenceManager sm = CFSequenceManager.getInstance(GestionInterface.ATT_CONEXION);
        int insertados = 0;
        AccountingEngine ae = new AccountingEngine();
        ae.setValidaInsuficienciaDeSaldo(true);
        try {
            String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
            for (ConvenioColaboracion convenio : convenios) {
                Caso c = CasoManager.nuevoCaso(conn, u, 70, fg);
                int nextVal = sm.nextVal("DIV-" + ConvenioColaboracion.TIPO_CONVENIO_PAGO);
                String idContrato = ConvenioColaboracion.TIPO_CONVENIO_PAGO + "-" + u.getU_UR() + "-" + nextVal + "/" + ejercicioFiscal;
                convenio.setFolioConvenio(Util.folio(c));
                convenio.getEncabezado().setIdContrato(idContrato);
                insertados += insertaContratoColaboracion(conn, convenio);
                //insertados += insertaJustificacionCNET( conn, convenio, ConvenioColaboracion.JUSTIFICACION_PAGO_BENEF );
                int folioCompromiso = insertaCompromiso(conn, u, fg, convenio);
                insertados += insertaPrecompromisoFinanciero(conn, convenio, folioCompromiso);
                ae.makeAccountingApplication(conn, "PRECOMFINANCIERO", String.valueOf(folioCompromiso), "tPrecomFinancieroEncabezado", "tPrecomFinancieroDetalle", "nFolioPrecomFinanciero");
            }
            log.info("Object: {}", "Se insertaron: " + insertados + " registros");
            return convenios;
        } catch (SQLException e) {
            throw new LayoutConvenioColaboracionException("Error de base de datos al registrar convenios: " + e.toString(), e);
        } catch (GestionException e) {
            throw new LayoutConvenioColaboracionException("Error en gestion al registrar convenios: " + e, e);
        } catch (Exception e) {
            throw new LayoutConvenioColaboracionException("Error no esperado al registrar convenios: " + e, e);
        }
    }

    public List<ConvenioColaboracion> generaConveniosPagoBeneficiarioMasivo(File inputLayout, Usuario u) throws LayoutConvenioColaboracionException {
        Connection conn = null;
        try {
            conn = getConnection();
            List<ConvenioColaboracion> conveniosColaboracion = generaConveniosPagoBeneficiarioMasivo(conn, inputLayout, u);
            conn.commit();
            return conveniosColaboracion;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception e2) {
                log.warn("Problemas en rollback: " + e2, e2);
            }
            throw new LayoutConvenioColaboracionException(e.getMessage().substring(e.getMessage().indexOf("Reng")));
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private int insertaCompromiso(Connection conn, Usuario u, FolioGeneratorInterface fg, ConvenioColaboracion convenio) throws NumberFormatException, Exception {
        log.trace("Generando caso");
        Caso c = CompromisoManager.generaCasoCompromiso(conn, u, fg, "VENTANILLA_COMPROMISO");
        log.debug("Object: {}", "Caso generado: " + c);
        log.trace("Generando compromiso");
        CompromisoEncabezado compromisoEncabezado = CompromisoEncabezado.instanceFrom(conn, convenio);
        List<CompromisoDetalle> compromisoDetalle = CompromisoDetalle.instanceFrom(conn, convenio);
        Compromiso compromiso = new Compromiso();
        compromiso.setEncabezado(compromisoEncabezado);
        compromiso.setDetalle(compromisoDetalle);
        compromiso.setFolioCompromiso(Util.folio(c));
        log.debug("Object: {}", "Compromiso generado: " + compromiso);
        int registros = CompromisoManager.insertaCompromiso(conn, compromiso);
        log.trace("Object: {}", "Se afectaron: " + registros + " al insertar compromiso.");
        return Util.folio(c);
    }

    /**
     * Inserta convenio de colaboracion y si tiene exito ciera la conexion.
     *
     * @param cc
     *            Convenio a guradar
     * @return numero de registros insertados.
     * @throws ContratoException
     */
    public int insertaContratoColaboracion(Connection conn, ConvenioColaboracion cc) throws ContratoException {
        ContratoDiversoBusinessLogic cdbl = new ContratoDiversoBusinessLogic();
        try {
            int insertados = insertaConvenioColaboracion(conn, cc);
            ContratoDiverso contrato = ContratoDiverso.generaContratoDiverso(conn, cc);
            contrato.setDetalleEP(ContratoDiverso.generaDetalleContratoDiverso(conn, cc));
            contrato.setAnticipo(ContratoDiverso.generaDetalleAnticipo(conn, cc));
            insertados += cdbl.insertaContratoDiverso(conn, contrato);
            return insertados;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ContratoException(e);
        }
    }

    /**
     * Inserta convenio de colaboracion y si tiene exito ciera la conexion.
     *
     * @param cc
     *            Convenio a guradar
     * @return numero de registros insertados.
     * @throws ContratoException
     */
    public int insertaContratoColaboracion(ConvenioColaboracion cc) throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = insertaContratoColaboracion(conn, cc);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error(e2.getMessage(), e2);
                }
            throw new ContratoException(e);
        }
    }

    /**
     * @param conn
     * @param cc
     * @return
     * @throws Exception
     */
    public int insertaConvenioColaboracion(Connection conn, ConvenioColaboracion cc) throws Exception {
        int insertados = ConvenioColaboracionManager.insertaConvenioColaboracionEncabezado(conn, cc.getEncabezado());
        insertados += ConvenioColaboracionManager.insertaConvenioColaboracionDetalle(conn, cc.getDetalle());
        return insertados;
    }

    private int insertaJustificacionCNET(Connection conn, ConvenioColaboracion convenio, String justificacionPagoBenef) throws SQLException {
        return ConvenioColaboracionManager.insertaJustificacionCNET(conn, convenio.getEncabezado(), justificacionPagoBenef);
    }

    private int insertaPrecompromisoFinanciero(Connection conn, ConvenioColaboracion convenio, int folioCompromiso) throws NumberFormatException, Exception {
        PrecompromisoFinancieroEncabezado precomEncabezado = PrecompromisoFinancieroEncabezado.instanceFrom(conn, convenio, folioCompromiso);
        List<PrecompromisoFinancieroDetalle> precomDetalle = PrecompromisoFinancieroDetalle.instanceFrom(conn, convenio, folioCompromiso);
        PrecompromisoFinanciero precompromiso = new PrecompromisoFinanciero();
        precompromiso.setEncabezado(precomEncabezado);
        precompromiso.setDetalle(precomDetalle);
        int insertados = CompromisoManager.insertaPrecomFinanciero(conn, precompromiso);
        return insertados;
    }

    private ConvenioColaboracionDetalle instanceDetail(Row renglon) {
        ConvenioColaboracionDetalle detalle = new ConvenioColaboracionDetalle();
        detalle.setEp(renglon.getCell(1).getStringCellValue());
        detalle.setMes((int) renglon.getCell(2).getNumericCellValue());
        detalle.setImporte(new BigDecimal(renglon.getCell(3).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        return detalle;
    }

    private ConvenioColaboracionEncabezado instanceHeader(Row renglon, Usuario u) {
        ConvenioColaboracionEncabezado encabezado = new ConvenioColaboracionEncabezado();
        encabezado.setCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
        encabezado.setConceptoConvenio(renglon.getCell(2).getStringCellValue());
        encabezado.setFechaConvenioInicio(new Date());
        encabezado.setFechaFinConvenio(Util.getTomorrow());
        encabezado.setFechaFirmaConvenio(new Date());
        encabezado.setIdTipoAdjudicacion("3");
        encabezado.setIdUnidadAdministrativa(u.getU_UR());
        encabezado.setLoginCaptura(u.getLogin());
        encabezado.setmImporteBruto(new BigDecimal(renglon.getCell(3).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        encabezado.setmImporteConvenio(new BigDecimal(renglon.getCell(5).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        encabezado.setmImporteIVA(new BigDecimal(renglon.getCell(4).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        encabezado.setmImporteTotal(new BigDecimal(renglon.getCell(5).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
        encabezado.setPorcIvaAplicable((int) renglon.getCell(6).getNumericCellValue());
        encabezado.setRfc(renglon.getCell(1).getStringCellValue());
        encabezado.setUnidadEjecutora(u.getU_UR());
        return encabezado;
    }

    public List<ConvenioColaboracion> readConvenioPagoBeneficiariosLayout(Connection conn, File inputLayout, Usuario u) throws LayoutConvenioColaboracionException {
        List<String> errores = new ArrayList<>();
        List<ConvenioColaboracion> convenios = new ArrayList<>();
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
                    if (ConvenioColaboracion.HEADER.equals(tipoRenglon)) {
                        Row renglonSiguiente = hojaInformacion.getRow(renglon.getRowNum() + 1);
                        if (renglonSiguiente == null || (renglonSiguiente != null && ConvenioColaboracion.HEADER.equals(renglonSiguiente.getCell(0).getStringCellValue()))) {
                            errores.add("Renglon: " + renglon.getRowNum() + " Encabezado sin detalle.");
                            encabezadoCargado = false;
                            continue;
                        }
                        convenios.add(new ConvenioColaboracion(instanceHeader(renglon, u), new ArrayList<>()));
                        encabezadoCargado = true;
                    } else if (ConvenioColaboracion.DETAIL.equals(tipoRenglon)) {
                        if (!encabezadoCargado) {
                            errores.add("Renglon: " + renglon.getRowNum() + " detalle sin encabezado.");
                            continue;
                        }
                        convenios.get(convenios.size() - 1).getDetalle().add(instanceDetail(renglon));
                    } else {
                        errores.add("Renglon: " + renglon.getRowNum() + " tipo de renglon desconocido: [" + tipoRenglon + "]");
                    }
                } catch (Exception e) {
                    errores.add("Error generado por la informacion del renglon " + numRenglon + " : " + e.toString());
                    log.error("Error en el renglon " + numRenglon, e);
                }
            }
            if (errores.size() > 0)
                throw new LayoutConvenioColaboracionException(errores);
            return convenios;
        } catch (IOException e) {
            log.error("Error general procesando archivo: " + e, e);
            errores.add("Error general procesando archivo. " + e.toString());
            throw new LayoutConvenioColaboracionException(errores);
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando archivo: " + e2, e2);
                }
            if (layout != null)
                try {
                    layout.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando archivo: " + e2, e2);
                }
        }
    }
}
