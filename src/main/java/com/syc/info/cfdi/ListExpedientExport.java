package com.syc.info.cfdi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.fortimax.core.ExpedientExporteThread;
import com.syc.fortimax.core.ExpedientExporterBusinessLogic;
import com.syc.fortimax.core.ExportLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ListExpedientExport {

    private static Logger log;

    public static void main(String[] args) throws Exception {
        ListExpedientExport eea = new ListExpedientExport();
        eea.execListExport();
    }

    public ListExpedientExport() throws Exception {
        log = LoggerFactory.getLogger(ListExpedientExport.class);
    }

    private void escribeLogs(List<ExportLog> exportLog, String nombre) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            String pathExport = System.getProperty("user.home") + File.separatorChar + nombre + "_" + CFDIUtils.getTodayFile() + ".csv";
            fw = new FileWriter(pathExport);
            bw = new BufferedWriter(fw);
            for (Iterator<ExportLog> i = exportLog.iterator(); i.hasNext(); ) {
                ExportLog renglon = i.next();
                bw.write(renglon.toCSV() + "\n");
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            log.error("Problemas escribiendo el log del proceso: " + e, e);
        } finally {
            fw = null;
            bw = null;
            System.gc();
        }
    }

    public void execListExport() throws Exception {
        List<ExportLog> logGeneral = new ArrayList<ExportLog>();
        ExpedientExporterBusinessLogic eebl = new ExpedientExporterBusinessLogic();
        List<String> expedients = new LinkedList<String>(Arrays.asList(eebl.listaExportarAuditoria(true)));
        if (expedients == null || expedients.isEmpty())
            return;
        ExpedientExporteThread thAux = new ExpedientExporteThread(logGeneral, null, expedients);
        thAux.listExpedientsAuditoria();
        escribeLogs(logGeneral, "logGeneral");
        log.info("Proceso de listado terminado completamente");
    }
}
