package com.syc.fortimax.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.util.Util;
import com.syc.info.cfdi.CFDIMaskGenerator;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpedientExporteThread extends DataSourceManager implements Runnable {

    private static final File[] DocumentosReemplazo = new File[] { new File("/procesos/AlimentacionBrigadistas/Listado_Combatientes.xls"), new File("/procesos/AlimentacionBrigadistas/Addendum_de_fecha_21_de_octubre_de_2014.pdf") };

    private static final File[] DocumentosReemplazoBoxLunch = new File[] { new File("/procesos/AlimentacionBrigadistas/Minuta_de_apoyos_economicos_y_logisticos.pdf") };

    public static String pathSalida = System.getProperty("java.io.tmpdir");

    public static boolean continueProc = true;

    private static final Logger log = LoggerFactory.getLogger(ExpedientExporteThread.class);

    private static List<ExportLog> logDetallado;

    private static List<ExportLog> logGeneral;

    private static int total;

    private static String[] carpetas = new String[] { "OFICIO PAGO", "COMISION SIN FACTURAS", "COMISION AL EXTRANJERO", "PAGO SIN FACTURA", "CFDI", "OFICIOS", "CERTIFICADO DE TRANSITO", "Solicitud Firmada" };

    /**
     * @return the continueProc
     */
    public static boolean isContinueProc() {
        return continueProc;
    }

    /**
     * @param continueProc
     *            the continueProc to set
     */
    public static void setContinueProc(boolean continueProc) {
        ExpedientExporteThread.continueProc = continueProc;
    }

    private static List<String> export;

    private boolean finished = false;

    boolean standAlone = false;

    private Thread t = null;

    private File prntDir = null;

    public ExpedientExporteThread(List<ExportLog> logDetallado, List<ExportLog> logGeneral, String jniName, boolean standAlone, List<String> export, String threadName) throws Exception {
        super();
        synchronized (this) {
            if (!StringUtils.isEmpty(jniName))
                super.init(jniName);
            if (prntDir == null) {
                Connection conn = null;
                try {
                    conn = Util.getStandAloneConnection();
                    String ef = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
                    prntDir = new File("/EXPORT/Transparencia/" + ef + "/");
                    if (!prntDir.exists())
                        prntDir.mkdirs();
                    pathSalida = pathSalida + ef + File.separatorChar;
                    if (!new File(pathSalida).exists())
                        new File(pathSalida).mkdirs();
                } finally {
                    CloseObject.closeObject(conn);
                }
            }
            if (ExpedientExporteThread.export == null) {
                ExpedientExporteThread.export = export;
                total = export.size();
            }
            if (ExpedientExporteThread.logDetallado == null)
                ExpedientExporteThread.logDetallado = logDetallado;
            if (ExpedientExporteThread.logGeneral == null)
                ExpedientExporteThread.logGeneral = logGeneral;
        }
        this.standAlone = standAlone;
        t = new Thread(this);
        t.setName(threadName);
        t.start();
    }

    public ExpedientExporteThread(List<ExportLog> logGeneral, List<ExportLog> logDetallado, List<String> export) {
        ExpedientExporteThread.logGeneral = logGeneral;
        ExpedientExporteThread.logDetallado = logDetallado;
        ExpedientExporteThread.export = export;
        ExpedientExporteThread.total = export.size();
    }

    public void exportExpedientsAuditoria() throws Exception {
        String carpetaSolicitud = "Solicitud de Pago";
        String carpetaCFDI = "CFDI";
        try {
            int cnt = 0;
            String infoRenglon = getNext();
            while (infoRenglon != null && continueProc) {
                cnt++;
                Connection conn = null;
                ExportLogGeneral logGeneralRenglon = new ExportLogGeneral();
                CFDIMaskGenerator facturaMask = null;
                try {
                    if (standAlone)
                        conn = Util.getStandAloneConnection();
                    else
                        conn = getConnection();
                    String[] toExport = infoRenglon.split(";", 4);
                    String cuentaPorPagar = StringUtils.trim(toExport[0]);
                    String folioCaso = StringUtils.trim(toExport[1]);
                    int idGabinete = Integer.parseInt(StringUtils.trim(toExport[2]));
                    String concepto = toExport[3];
                    facturaMask = new CFDIMaskGenerator(conn);
                    log.info("Object: {}", (t == null ? "" : (t.getName() + ": ")) + ": " + String.format("Exportando expediente %d de %d CxP: %s Folio: %s Gabinete: %d", cnt, total, cuentaPorPagar, folioCaso, idGabinete));
                    String zipExportName = new File(prntDir, cuentaPorPagar + ".zip").getAbsolutePath();
                    logGeneralRenglon.setCuentaPorPagar(cuentaPorPagar);
                    Caso c = new Caso();
                    c.setFolio(folioCaso);
                    c = CasoManager.select(conn, c);
                    int nFolioTramite = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
                    boolean esAlimentacionBrigadistas = false;
                    boolean esJuegosDeportivos = false;
                    if ("RELACIONGASTOS".equalsIgnoreCase(c.getTipoCaso().getGavetaAsociada())) {
                        esAlimentacionBrigadistas = RelacionGastosManager.esRGAlimentacionBrigadistas(conn, nFolioTramite);
                        esJuegosDeportivos = RelacionGastosManager.esRGJuegosDeportivos(conn, nFolioTramite);
                    }
                    logGeneralRenglon.setCfdis(0);
                    logGeneralRenglon.setOficiosDeTransito(0);
                    if (!esAlimentacionBrigadistas && !esJuegosDeportivos)
                        for (int cntCarpetas = 0; cntCarpetas < carpetas.length; cntCarpetas++) {
                            carpetaCFDI = carpetas[cntCarpetas];
                            int totalComprobantes = CarpetaManager.getTotalDocumentosCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpetaCFDI);
                            if ("CFDI".equalsIgnoreCase(carpetaCFDI))
                                logGeneralRenglon.setCfdis(logGeneralRenglon.getCfdis() + totalComprobantes);
                            else
                                logGeneralRenglon.setOficiosDeTransito(logGeneralRenglon.getOficiosDeTransito() + totalComprobantes);
                        }
                    else
                        logGeneralRenglon.setOficiosDeTransito(2);
                    int totalSolicitud = CarpetaManager.getTotalDocumentosCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpetaSolicitud);
                    logGeneralRenglon.setSolicitudesDePago(totalSolicitud);
                    if (totalSolicitud > 0 || (logGeneralRenglon.getCfdis() + logGeneralRenglon.getOficiosDeTransito()) > 0) {
                        FileOutputStream fos = new FileOutputStream(zipExportName);
                        ZipOutputStream zos = new ZipOutputStream(fos);
                        List<Documento> exportL = null;
                        try {
                            exportL = DocumentoManager.listaDocumentosExportarAuditoria(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), cuentaPorPagar, carpetaSolicitud, "Solicitud de Pago Firmada", concepto);
                            Documento dEditable = generaReporteEditable(conn, cuentaPorPagar, c.getTipoCaso().getGavetaAsociada());
                            if (dEditable != null)
                                exportL.add(dEditable);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error(e.getMessage(), e);
                            exportL = new ArrayList<Documento>();
                            logGeneralRenglon.setSolicitudesDePago(0);
                            logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Se encontro que el expediente contiene Solicitud de Pago pero no se pudo procesar. " + e);
                        }
                        List<Documento> exportC = null;
                        if (!esAlimentacionBrigadistas && !esJuegosDeportivos)
                            for (int cntCarpetas = 0; cntCarpetas < carpetas.length; cntCarpetas++) {
                                carpetaCFDI = carpetas[cntCarpetas];
                                List<Documento> docsToExport = new ArrayList<Documento>();
                                if ("CFDI".equalsIgnoreCase(carpetaCFDI))
                                    docsToExport = DocumentoManager.listaDocumentosExportarAuditoriaCFDI(conn, facturaMask, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), cuentaPorPagar, carpetaCFDI, null, null);
                                else
                                    docsToExport = DocumentoManager.listaDocumentosExportarAuditoria(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), cuentaPorPagar, carpetaCFDI, null, null);
                                if (docsToExport != null && docsToExport.size() > 0) {
                                    if (exportC == null)
                                        exportC = new ArrayList<Documento>();
                                    exportC.addAll(docsToExport);
                                }
                            }
                        else {
                            File[] docList = esAlimentacionBrigadistas ? ExpedientExporteThread.DocumentosReemplazo : ExpedientExporteThread.DocumentosReemplazoBoxLunch;
                            String folderName = cuentaPorPagar + "/" + (esAlimentacionBrigadistas ? "Alimentacion a Brigadistas" : "Juegos Deportivos");
                            List<Documento> docsToExport = DocumentoManager.listaDocumentosExportarAlimentacion(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), folderName, docList);
                            if (docsToExport != null && docsToExport.size() > 0) {
                                if (exportC == null)
                                    exportC = new ArrayList<Documento>();
                                exportC.addAll(docsToExport);
                            }
                        }
                        if (exportC != null && !exportC.isEmpty())
                            exportL.addAll(exportC);
                        List<ExportLogDetallado> detalleRenglon = CFDIUtils.addToZip(zos, exportL.toArray(new Documento[exportL.size()]), cuentaPorPagar);
                        if (detalleRenglon != null && detalleRenglon.size() > 0)
                            logDetallado.addAll(detalleRenglon);
                        zos.flush();
                        zos.closeEntry();
                        zos.close();
                        carpetaCFDI = "CFDI";
                        logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Archivo generado exitosamente.");
                        ExpedienteExporterManager.updateExportado(conn, "tviaticosTransparencia_V2", "cExportado", 'S', "caNoContrarrecibo", cuentaPorPagar);
                        conn.commit();
                    } else {
                        logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "El tramite no contiene expediente");
                    }
                } catch (Exception e) {
                    try {
                        if (conn != null)
                            conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas con rollback: " + e2);
                    }
                    logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Ocurrio el siguiente error al procesar el expediente: " + e);
                    log.error((t == null ? "" : (t.getName() + ": ")) + ": " + e, e);
                } catch (Throwable thr) {
                    try {
                        if (conn != null)
                            conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas con rollback: " + e2);
                    }
                    logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Ocurrio el siguiente error al procesar el expediente: " + thr);
                    log.error((t == null ? "" : (t.getName() + ": ")) + ": " + thr, thr);
                } finally {
                    CloseObject.closeObject(conn);
                    facturaMask = null;
                }
                logGeneral.add(logGeneralRenglon);
                infoRenglon = getNext();
            }
            log.info("Object: {}", (t == null ? "" : (t.getName() + ": ")) + ": " + "Proceso terminado");
        } catch (Exception e) {
            System.err.println("Error general:" + e);
            throw e;
        } finally {
            setFinished(true);
        }
    }

    private synchronized String getNext() {
        int index = export.size();
        String retStr = null;
        if (export.size() > 0) {
            retStr = export.remove(index - 1);
            log.debug("Object: {}", (t == null ? "" : (t.getName() + ": ")) + " Devolviendo elemento en posicion " + index + " Valor: " + retStr);
        }
        return retStr;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    public void run() {
        try {
            exportExpedientsAuditoria();
        } catch (Exception e) {
            log.error((t == null ? "" : (t.getName() + ": ")) + ": " + e, e);
        }
    }

    /**
     * @param finished
     *            the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void listExpedientsAuditoria() throws Exception {
        String carpetaSolicitud = "Solicitud de Pago";
        String carpetaCFDI = "CFDI";
        try {
            int cnt = 0;
            String infoRenglon = getNext();
            while (infoRenglon != null) {
                cnt++;
                Connection conn = null;
                ExportLogGeneral logGeneralRenglon = new ExportLogGeneral();
                try {
                    conn = CFDIUtils.getStandAloneConnection();
                    String[] toExport = infoRenglon.split(";");
                    String cuentaPorPagar = StringUtils.trim(toExport[0]);
                    String folioCaso = StringUtils.trim(toExport[1]);
                    int idGabinete = Integer.parseInt(StringUtils.trim(toExport[2]));
                    log.info("Object: {}", (t == null ? "" : (t.getName() + ": ")) + String.format("Exportando expediente %d de %d CxP: %s Folio: %s Gabinete: %d", cnt, total, cuentaPorPagar, folioCaso, idGabinete));
                    logGeneralRenglon.setCuentaPorPagar(cuentaPorPagar);
                    Caso c = new Caso();
                    c.setFolio(folioCaso);
                    c = CasoManager.select(conn, c);
                    int nFolioTramite = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
                    if ("RELACIONGASTOS".equalsIgnoreCase(c.getTipoCaso().getGavetaAsociada()))
                        carpetaCFDI = (RelacionGastosManager.esRelacionPorOficio(conn, nFolioTramite) ? "Oficios" : "CFDI");
                    int totalSolicitud = CarpetaManager.getTotalDocumentosCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpetaSolicitud);
                    int totalComprobantes = CarpetaManager.getTotalDocumentosCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpetaCFDI);
                    if (RelacionGastosManager.esRelacionPorOficio(conn, nFolioTramite))
                        logGeneralRenglon.setOficiosDeTransito(totalComprobantes);
                    else
                        logGeneralRenglon.setCfdis(totalComprobantes);
                    logGeneralRenglon.setSolicitudesDePago(totalSolicitud);
                    if (totalSolicitud > 0 || totalComprobantes > 0) {
                        logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "El tramite contiene expediente.");
                    } else {
                        logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "El tramite no contiene expediente");
                    }
                } catch (Exception e) {
                    try {
                        if (conn != null)
                            conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas con rollback: " + e2);
                    }
                    logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Ocurrio el siguiente error al procesar el expediente: " + e);
                    log.error((t == null ? "" : (t.getName() + ": ")) + e, e);
                } catch (Throwable thr) {
                    try {
                        if (conn != null)
                            conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas con rollback: " + e2);
                    }
                    logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Ocurrio el siguiente error al procesar el expediente: " + thr);
                    log.error((t == null ? "" : (t.getName() + ": ")) + ": " + thr, thr);
                } finally {
                    CloseObject.closeObject(conn);
                }
                logGeneral.add(logGeneralRenglon);
                infoRenglon = getNext();
            }
            log.info("Object: {}", (t == null ? "" : (t.getName() + ": ")) + ": " + "Proceso terminado");
        } catch (Exception e) {
            System.err.println("Error general:" + e);
            throw e;
        } finally {
            setFinished(true);
        }
    }

    private static Documento generaReporteEditable(Connection conn, String canoContrarecibo, String tituloAplicacion) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        Documento d = null;
        try {
            Map<String, Object> parametrosReporte = new HashMap<String, Object>();
            if ("COMSINVIATICOS".equals(tituloAplicacion)) {
                in = new FileInputStream("/reports/PolizaInformeComision.jasper");
                parametrosReporte.put("whereFolio", new Integer(canoContrarecibo));
                parametrosReporte.put("SUBREPORT_DIR", "/reports/");
            } else {
                in = new FileInputStream("/reports/PolizaPagoN2.jasper");
                parametrosReporte.put("folio", canoContrarecibo);
                parametrosReporte.put("whereFolio", " CR.caNoContrarrecibo = '" + canoContrarecibo + "'");
            }
            File pdfName = File.createTempFile("informe", ".pdf", new File(pathSalida));
            out = new FileOutputStream(pdfName);
            JasperRunManager.runReportToPdfStream(in, out, parametrosReporte, conn);
            out.flush();
            d = new Documento();
            Pagina[] paginas = new Pagina[1];
            paginas[0] = new Pagina();
            paginas[0].setNomArchivoVol(pdfName.getAbsolutePath());
            paginas[0].setNomArchivoOrg(pdfName.getAbsolutePath());
            d.setPaginasDocumento(paginas);
            d.setNombreDocumento(canoContrarecibo + "/Informe de la comisi\u00F3n/Informe de la comisi\u00F3n reutilizable");
            d.setExtension("pdf");
            return d;
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo de Salida: " + e);
                }
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo de Entrada: " + e);
                }
        }
    }
}
