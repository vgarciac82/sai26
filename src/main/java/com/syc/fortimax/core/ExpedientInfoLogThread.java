package com.syc.fortimax.core;

import java.sql.Connection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpedientInfoLogThread implements Runnable {

    public static boolean continueProc = true;

    private static final Logger log = LoggerFactory.getLogger(ExpedientInfoLogThread.class);

    private static List<ExportLog> logDetallado;

    private static List<ExportLog> logGeneral;

    private static int total;

    private static String[] carpetas = new String[] { "OFICIO PAGO", "COMISION SIN FACTURAS", "COMISION AL EXTRANJERO", "PAGO SIN FACTURA", "CFDI", "OFICIOS", "CERTIFICADO DE TRANSITO" };

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
        ExpedientInfoLogThread.continueProc = continueProc;
    }

    private static List<String> export;

    private boolean finished = false;

    boolean standAlone = false;

    private Thread t = null;

    public ExpedientInfoLogThread(List<ExportLog> logDetallado, List<ExportLog> logGeneral, String jniName, boolean standAlone, List<String> export, String threadName) {
        super();
        synchronized (this) {
            if (ExpedientInfoLogThread.export == null) {
                ExpedientInfoLogThread.export = export;
                total = export.size();
            }
            if (ExpedientInfoLogThread.logDetallado == null)
                ExpedientInfoLogThread.logDetallado = logDetallado;
            if (ExpedientInfoLogThread.logGeneral == null)
                ExpedientInfoLogThread.logGeneral = logGeneral;
        }
        this.standAlone = standAlone;
        t = new Thread(this);
        t.setName(threadName);
        t.start();
    }

    public ExpedientInfoLogThread(List<ExportLog> logGeneral, List<ExportLog> logDetallado, List<String> export) {
        ExpedientInfoLogThread.logGeneral = logGeneral;
        ExpedientInfoLogThread.logDetallado = logDetallado;
        ExpedientInfoLogThread.export = export;
        ExpedientInfoLogThread.total = export.size();
    }

    public void listExpedientsAuditoria() throws Exception {
        String carpetaSolicitud = "Solicitud de Pago";
        String carpetaCFDI = "CFDI";
        try {
            int cnt = 0;
            String infoRenglon = getNext();
            while (infoRenglon != null && continueProc) {
                cnt++;
                Connection conn = null;
                ExportLogGeneral logGeneralRenglon = new ExportLogGeneral();
                try {
                    if (standAlone)
                        conn = CFDIUtils.getStandAloneConnection();
                    String[] toExport = infoRenglon.split(";", 4);
                    String cuentaPorPagar = StringUtils.trim(toExport[0]);
                    String folioCaso = StringUtils.trim(toExport[1]);
                    int idGabinete = Integer.parseInt(StringUtils.trim(toExport[2]));
                    log.info((t == null ? "" : (t.getName() + ": ")) + ": " + String.format("Exportando expediente %d de %d CxP: %s Folio: %s Gabinete: %d", cnt, total, cuentaPorPagar, folioCaso, idGabinete));
                    logGeneralRenglon.setCuentaPorPagar(cuentaPorPagar);
                    Caso c = new Caso();
                    c.setFolio(folioCaso);
                    c = CasoManager.select(conn, c);
                    logGeneralRenglon.setCfdis(0);
                    logGeneralRenglon.setOficiosDeTransito(0);
                    for (int cntCarpetas = 0; cntCarpetas < carpetas.length; cntCarpetas++) {
                        carpetaCFDI = carpetas[cntCarpetas];
                        int totalComprobantes = CarpetaManager.getTotalDocumentosCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpetaCFDI);
                        if ("CFDI".equalsIgnoreCase(carpetaCFDI))
                            logGeneralRenglon.setCfdis(logGeneralRenglon.getCfdis() + totalComprobantes);
                        else
                            logGeneralRenglon.setOficiosDeTransito(logGeneralRenglon.getOficiosDeTransito() + totalComprobantes);
                    }
                    int totalSolicitud = CarpetaManager.getTotalDocumentosCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), carpetaSolicitud);
                    logGeneralRenglon.setSolicitudesDePago(totalSolicitud);
                    if (totalSolicitud > 0 || (logGeneralRenglon.getCfdis() + logGeneralRenglon.getOficiosDeTransito()) > 0) {
                        logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Archivo generado exitosamente.");
                    } else {
                        logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "El tramite no contiene expediente");
                    }
                } catch (Exception e) {
                    try {
                        if (conn != null)
                            conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Problemas con rollback: " + e2);
                    }
                    logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Ocurrio el siguiente error al procesar el expediente: " + e);
                    log.error((t == null ? "" : (t.getName() + ": ")) + ": " + e, e);
                } catch (Throwable thr) {
                    try {
                        if (conn != null)
                            conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Problemas con rollback: " + e2);
                    }
                    logGeneralRenglon.setLog(StringUtils.trimToEmpty(logGeneralRenglon.getLog()) + ";" + "Ocurrio el siguiente error al procesar el expediente: " + thr);
                    log.error((t == null ? "" : (t.getName() + ": ")) + ": " + thr, thr);
                } finally {
                    CloseObject.closeObject(conn);
                }
                logGeneral.add(logGeneralRenglon);
                infoRenglon = getNext();
            }
            log.info((t == null ? "" : (t.getName() + ": ")) + ": " + "Proceso terminado");
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
            log.debug((t == null ? "" : (t.getName() + ": ")) + " Devolviendo elemento en posicion " + index + " Valor: " + retStr);
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
            listExpedientsAuditoria();
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
}
