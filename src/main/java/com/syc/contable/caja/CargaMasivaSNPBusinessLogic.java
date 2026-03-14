package com.syc.contable.caja;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.AccountingEngine;
import com.syc.contable.caja.core.CajaManager;
import com.syc.contable.caja.core.SolicitudNoPresupuestal;
import com.syc.contable.caja.core.SolicitudNoPresupuestalDetalle;
import com.syc.contable.caja.core.SolicitudNoPresupuestalEncabezado;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.solicitudviaticos.SolicitudViaticosManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CargaMasivaSNPBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CargaMasivaSNPBusinessLogic.class);

    public CargaMasivaSNPBusinessLogic() {
        super.init(GestionServlet.ATT_CONEXION);
    }

    public CargaMasivaSNPBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public SolicitudNoPresupuestal generaSolicitudNoPresupuestal(boolean generaCaso, String descripcion, String evento, double montoOperacion) throws Exception {
        if (StringUtils.isEmpty(evento))
            throw new Exception("No se puede realizar alta con evento nulo o vacio");
        if (evento.indexOf("-") < 1)
            throw new Exception("El evento esta mal formado. La estructura es: G-SG-ID (grupo-subgrupo-idEvento)");
        String[] eventoArr = evento.split("[-]");
        if (eventoArr.length != 3)
            throw new Exception("El evento esta mal formado. La estructura es: G-SG-ID (grupo-subgrupo-idEvento)");
        if (montoOperacion <= 0)
            throw new Exception("El importe de la operacion debe ser mayor a 0");
        BigDecimal bd = new BigDecimal(montoOperacion);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        SolicitudNoPresupuestal snp = new SolicitudNoPresupuestal();
        String patternFecha = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(patternFecha);
        Calendar fecha = Calendar.getInstance();
        EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic();
        SolicitudNoPresupuestalEncabezado encabezado = new SolicitudNoPresupuestalEncabezado();
        SolicitudNoPresupuestalDetalle detalle = new SolicitudNoPresupuestalDetalle();
        encabezado.setFechaAplicacion(sdf.format(fecha.getTime()));
        encabezado.setFechaCreacion(sdf.format(fecha.getTime()));
        encabezado.setMes(fecha.get(Calendar.MONTH) + 1);
        encabezado.setCentroContable("10");
        encabezado.setTipoPoliza("DI");
        encabezado.setDescripcionPoliza(descripcion);
        encabezado.setuLogin("SYSTEM");
        encabezado.setUnidadResponsableContable("RHQ");
        encabezado.setEjercicioFiscal(efbl.getEjercicioFiscalActivo().getaEjercicioFiscal());
        encabezado.setRamo("16");
        encabezado.setUnidadEjecutora("A04");
        encabezado.setIdGrupoEvento(Integer.parseInt(eventoArr[0]));
        encabezado.setMontoSolicitud(bd);
        detalle.setDocRenglon(1);
        detalle.setEvento(evento);
        detalle.setImporte(bd);
        detalle.setImporteNegativo(bd.multiply(new BigDecimal(-1).setScale(2, RoundingMode.HALF_UP)));
        snp.setEncabezado(encabezado);
        return snp;
    }

    public int aplicaSNP(SolicitudNoPresupuestal snp) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            AccountingEngine motor = new AccountingEngine();
            motor.makeAccountingApplication(conn, "CAJA", String.valueOf(snp.getEncabezado().getFolioCaja()), "tcajaencabezado", "tcajadetalle", "nFolioCaja");
            conn.commit();
            return 0;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas con rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String generaReporteExpedientesFaltantes(String reportePath, List<String> filtros, String cUnidadEjecutora) throws Exception {
        String fileName = null;
        Connection conn = null;
        try {
            conn = getConnection();
            fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteExpFaltantes_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
            Util.copiaArchivo(reportePath, fileName);
            CargaMasivaSNPManager.generaReporteExpedientesFaltantes(conn, filtros, cUnidadEjecutora, fileName);
            return fileName;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<String> procesaArchivoMasivo(String nombreDestino, Usuario u) throws Exception {
        InputStream fs = null;
        Workbook workbook = null;
        List<SolicitudNoPresupuestal> solicitudes = null;
        try {
            fs = new FileInputStream(nombreDestino);
            workbook = new HSSFWorkbook(fs);
        } catch (Exception e) {
            throw e;
        } finally {
            if (fs != null)
                fs.close();
        }
        Sheet hoja = workbook.getSheetAt(0);
        int numeroSNP = -1;
        solicitudes = new ArrayList<SolicitudNoPresupuestal>();
        AccountingEngine ae = new AccountingEngine();
        ae.setValidaInsuficienciaDeSaldo(true);
        for (int i = 0; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            log.trace("Object: {}", fila.getCell(0).getStringCellValue());
            if ("H".equalsIgnoreCase(StringUtils.trimToEmpty(fila.getCell(0).getStringCellValue()))) {
                numeroSNP++;
                solicitudes.add(numeroSNP, new SolicitudNoPresupuestal());
                solicitudes.get(numeroSNP).setEncabezado(SolicitudNoPresupuestalEncabezado.instaceFromExcel(fila, u));
            } else if ("D".equalsIgnoreCase(StringUtils.trimToEmpty(fila.getCell(0).getStringCellValue()))) {
                solicitudes.get(numeroSNP).getDetalle().add(SolicitudNoPresupuestalDetalle.instanceFromExcel(fila, u));
            }
            log.trace("Object: {}", solicitudes.get(numeroSNP));
        }
        for (int i = 0; i < solicitudes.size(); i++) {
            Connection conn = null;
            try {
                conn = getConnection();
                Caso c = generaCasoCaja(conn, u, 42);
                int nFolioCaja = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
                log.info("Object: {}", " ============================= GENERADO FOLIO " + c.getFolio() + " - " + nFolioCaja + " =====================================");
                solicitudes.get(i).setFolioSNP(nFolioCaja);
                CajaManager.insertaSolicitudNoPresupuestal(conn, solicitudes.get(i));
                ae.makeAccountingApplication(conn, "CAJA", String.valueOf(nFolioCaja), "tcajaencabezado", "tcajadetalle", "nFoliocaja");
                /*Actualizar tSaldosAntiguedad*/
                String evento = CajaManager.obtenerEvento(conn, nFolioCaja);
                String CC = "";
                /* 8_2_10 -21199 AC
				 * 5_30_1 -21191-00001-00019 AC
				 * 17_1_3 -21120 AC
				 * 5_13_3 -21150 AC
				 * 35_1_9 -11231 DD
				 * 8_2_4  -11232 DD
				 * */
                if ("8_2_10".equals(evento) || "5_30_1".equals(evento) || "17_1_3".equals(evento) || "5_13_3".equals(evento)) {
                    CC = CajaManager.validaCC(conn, nFolioCaja, "AC");
                    if (null == CC)
                        CajaManager.actualizaEsSaldoInicial(conn, nFolioCaja, "AC");
                    else
                        throw new Exception(CC);
                } else if ("35_1_9".equals(evento) || "8_2_4".equals(evento)) {
                    CC = CajaManager.validaCC(conn, nFolioCaja, "DD");
                    if (null == CC)
                        CajaManager.actualizaEsSaldoInicial(conn, nFolioCaja, "DD");
                    else
                        throw new Exception(CC);
                } else if ("8_2_7".equals(evento)) {
                    CajaManager.actualizaEstadoCuenta(conn, nFolioCaja);
                }
                conn.commit();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn(e2.getMessage(), e2);
                    }
                throw e;
            } finally {
                CloseObject.closeObject(conn);
                workbook.close();
            }
        }
        return null;
    }

    public static Caso generaCasoCaja(Connection conn, Usuario u, int idTC) throws Exception {
        FolioGeneratorInterface fg = null;
        ClassLoader cl = SolicitudViaticosManager.class.getClassLoader();
        Class<?> clase = cl.loadClass(GestionInterface.FOLIO_GENERATOR);
        fg = (FolioGeneratorInterface) clase.newInstance();
        Caso c = CasoManager.nuevoCaso(conn, u, idTC, fg);
        String fecha = Util.getTodayESMX();
        String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        c.getCasoDato("EJERCICIO_FISCAL").setValor(ejercicioFiscal);
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        m.put("EJERCICIO_FISCAL", ejercicioFiscal);
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        TipoCasoInterface tci = null;
        if (c.getTipoCaso().tieneInterface()) {
            tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
            tci.onCreateExpediente(conn, u.getLogin(), c, app);
        }
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        return c;
    }

    private static TipoCasoInterface instanceTipoCasoInterface(String name) throws Exception {
        TipoCasoInterface tci = null;
        ClassLoader cl = SolicitudViaticosManager.class.getClassLoader();
        Class<?> clase = cl.loadClass(name);
        tci = (TipoCasoInterface) clase.newInstance();
        return tci;
    }
}
