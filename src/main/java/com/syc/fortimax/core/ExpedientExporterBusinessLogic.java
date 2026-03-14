package com.syc.fortimax.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ExpedientExporterBusinessLogic extends DataSourceManager {

    private String jniName = "";

    private static final Logger log = LoggerFactory.getLogger(ExpedientExporterBusinessLogic.class);

    public ExpedientExporterBusinessLogic() {
    }

    public ExpedientExporterBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public String[] exporSeveraltExpedients(String[] exportList, String carpetaExportar, boolean standAlone) throws Exception {
        Connection conn = null;
        String zipExportNameRaiz = System.getProperty("java.io.tmpdir") + File.separatorChar + "export_" + System.currentTimeMillis() + "_" + (Math.random() * 1000);
        List<String> rutas = new ArrayList<String>();
        try {
            if (standAlone)
                conn = Util.getStandAloneConnection();
            else
                conn = getConnection();
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            int i = 0;
            int cntFile = 1;
            for (String cFolio : exportList) {
                if (i % 1000 == 0) {
                    if (fos != null) {
                        zos.flush();
                        zos.closeEntry();
                        zos.close();
                    }
                    String zipExportName = zipExportNameRaiz + "_" + cntFile + ".zip";
                    fos = new FileOutputStream(zipExportName);
                    zos = new ZipOutputStream(fos);
                    cntFile++;
                    rutas.add(zipExportName);
                }
                System.out.println("Exportando expediente " + (i + 1) + " de " + exportList.length + " Folio: " + cFolio);
                Caso sc = new Caso();
                sc.setFolio(cFolio);
                Caso c = CasoManager.select(conn, sc);
                if (c == null) {
                    System.out.println("Expediente sin caso: " + cFolio);
                    i++;
                    continue;
                }
                Carpeta cRaiz = null;
                String raiz = "";
                if (StringUtils.isBlank(carpetaExportar)) {
                    cRaiz = CarpetaManager.getCarpetaRaiz(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete());
                    raiz = cRaiz.getNombreCarpeta();
                } else {
                    cRaiz = CarpetaManager.getCarpetaByName(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpetaExportar);
                    if (cRaiz == null) {
                        System.out.println("Expediente sin facturas: " + cFolio);
                        continue;
                    }
                    raiz = cFolio;
                }
                List<Documento> exportL = DocumentoManager.listaDocumentosExportar(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), raiz, StringUtils.isBlank(carpetaExportar) ? -1 : cRaiz.getIdCarpeta());
                if (exportL != null && exportL.size() > 0)
                    Util.addToZip(zos, exportL.toArray(new Documento[exportL.size()]));
                else {
                    System.out.println("Expediente sin facturas: " + cFolio);
                }
                i++;
            }
            try {
                zos.flush();
                zos.closeEntry();
                zos.close();
            } catch (Exception e) {
                System.err.println("Problemas cerradno flujo: " + e);
            }
            return rutas.toArray(new String[rutas.size()]);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String[] exporSeveraltExpedients(String[] exportList, boolean standAlone) throws Exception {
        Connection conn = null;
        String zipExportNameRaiz = System.getProperty("java.io.tmpdir") + File.separatorChar + "export_" + System.currentTimeMillis() + "_" + (Math.random() * 1000);
        List<String> rutas = new ArrayList<String>();
        try {
            conn = CFDIUtils.getStandAloneConnection();
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            int i = 0;
            int cntFile = 1;
            for (String cFolio : exportList) {
                if (i % 1000 == 0) {
                    if (fos != null) {
                        zos.flush();
                        zos.closeEntry();
                        zos.close();
                    }
                    String zipExportName = zipExportNameRaiz + "_" + cntFile + ".zip";
                    fos = new FileOutputStream(zipExportName);
                    zos = new ZipOutputStream(fos);
                    cntFile++;
                    rutas.add(zipExportName);
                }
                System.out.println("Exportando expediente " + (i + 1) + " de " + exportList.length + " Folio: " + cFolio);
                Caso sc = new Caso();
                sc.setFolio(cFolio);
                Caso c = CasoManager.select(conn, sc);
                Carpeta cRaiz = CarpetaManager.getCarpetaRaiz(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete());
                String raiz = cRaiz.getNombreCarpeta();
                List<Documento> exportL = DocumentoManager.listaDocumentosExportar(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), raiz);
                CFDIUtils.addToZip(zos, exportL.toArray(new Documento[exportL.size()]));
                i++;
            }
            try {
                zos.flush();
                zos.closeEntry();
                zos.close();
            } catch (Exception e) {
                System.err.println("Problemas cerradno flujo: " + e);
            }
            return rutas.toArray(new String[rutas.size()]);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String exportExpedients(String[] exportList) throws Exception {
        return exportExpedients(exportList, false);
    }

    public String exportExpedients(String[] exportList, boolean standAlone) throws Exception {
        Connection conn = null;
        String zipExportName = System.getProperty("java.io.tmpdir") + File.separatorChar + "export_" + System.currentTimeMillis() + "_" + (Math.random() * 1000) + ".zip";
        try {
            FileOutputStream fos = new FileOutputStream(zipExportName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            if (standAlone)
                conn = Util.getStandAloneConnection();
            else
                conn = getConnection();
            for (String cFolio : exportList) {
                Caso sc = new Caso();
                sc.setFolio(cFolio);
                Caso c = CasoManager.select(conn, sc);
                Carpeta cRaiz = CarpetaManager.getCarpetaRaiz(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete());
                String raiz = cRaiz.getNombreCarpeta();
                List<Documento> exportL = DocumentoManager.listaDocumentosExportar(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), raiz);
                CFDIUtils.addToZip(zos, exportL.toArray(new Documento[exportL.size()]));
            }
            zos.flush();
            zos.closeEntry();
            zos.close();
            return zipExportName;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void exportExpedientsAuditoria(String[] export, boolean standAlone) throws Exception {
        String carpetaSolicitud = "Solicitud de Pago";
        String carpetaCFDI = "CFDI";
        List<ExportLog> logGeneral = new ArrayList<ExportLog>();
        List<ExportLog> logDetallado = new ArrayList<ExportLog>();
        try {
            int cnt = 0;
            for (String infoRenglon : export) {
                cnt++;
                Connection conn = null;
                ExportLogGeneral logGeneralRenglon = new ExportLogGeneral();
                try {
                    if (standAlone)
                        conn = Util.getStandAloneConnection();
                    else
                        conn = getConnection();
                    String[] toExport = infoRenglon.split(";");
                    String cuentaPorPagar = StringUtils.trim(toExport[0]);
                    String folioCaso = StringUtils.trim(toExport[1]);
                    int idGabinete = Integer.parseInt(StringUtils.trim(toExport[2]));
                    String concepto = toExport[3];
                    log.info("Object: {}", String.format("Exportando expediente %d de %d CxP: %s Folio: %s Gabinete: %d", cnt, export.length, cuentaPorPagar, folioCaso, idGabinete));
                    String zipExportName = System.getProperty("java.io.tmpdir") + File.separatorChar + cuentaPorPagar + ".zip";
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
                        FileOutputStream fos = new FileOutputStream(zipExportName);
                        ZipOutputStream zos = new ZipOutputStream(fos);
                        List<Documento> exportL = DocumentoManager.listaDocumentosExportarAuditoria(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), cuentaPorPagar, carpetaSolicitud, "Solicitud de Pago Firmada", concepto);
                        List<Documento> exportC = DocumentoManager.listaDocumentosExportarAuditoria(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), cuentaPorPagar, carpetaCFDI, null, null);
                        if (exportC != null && !exportC.isEmpty())
                            exportL.addAll(exportC);
                        List<ExportLogDetallado> detalleRenglon = CFDIUtils.addToZip(zos, exportL.toArray(new Documento[exportL.size()]), cuentaPorPagar);
                        if (detalleRenglon != null && detalleRenglon.size() > 0)
                            logDetallado.addAll(detalleRenglon);
                        zos.flush();
                        zos.closeEntry();
                        zos.close();
                        carpetaCFDI = "CFDI";
                        logGeneralRenglon.setLog("Archivo generado exitosamente.");
                    } else {
                        logGeneralRenglon.setLog("El tramite no contiene expediente");
                    }
                } catch (Exception e) {
                    logGeneralRenglon.setLog("Ocurrio el siguiente error al procesar el expediente: " + e);
                    log.error(e.getMessage(), e);
                } finally {
                    CloseObject.closeObject(conn);
                }
                logGeneral.add(logGeneralRenglon);
            }
        } catch (Exception e) {
            System.err.println("Error general:" + e);
            throw e;
        } finally {
            escribeLogs(logGeneral, "logGeneral");
            escribeLogs(logDetallado, "logDetallado");
        }
    }

    private void escribeLogs(List<ExportLog> exportLog, String nombre) {
        try {
            String pathExport = System.getProperty("java.io.tmpdir") + File.separatorChar + nombre + "_" + CFDIUtils.getTodayFile() + ".csv";
            FileWriter fw = new FileWriter(pathExport);
            BufferedWriter bw = new BufferedWriter(fw);
            for (Iterator<ExportLog> i = exportLog.iterator(); i.hasNext(); ) {
                ExportLog renglon = i.next();
                bw.write(renglon.toCSV() + "\n".toPath());
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            log.error("Problemas escribiendo el log del proceso: " + e, e);
        }
    }

    public String getJniName() {
        return jniName;
    }

    public String[] listaExportarAuditoria(boolean standalone) throws Exception {
        Connection conn = null;
        try {
            if (standalone)
                conn = Util.getStandAloneConnection();
            else
                conn = getConnection();
            return ExpedienteExporterManager.listaExportarAuditoria(conn);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String[] listaExportarAuditoriaError(boolean standalone) throws Exception {
        Connection conn = null;
        try {
            if (standalone)
                conn = Util.getStandAloneConnection();
            else
                conn = getConnection();
            return ExpedienteExporterManager.listaExportarAuditoriaError(conn);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void setJniName(String jniName) {
        this.jniName = jniName;
    }
}
