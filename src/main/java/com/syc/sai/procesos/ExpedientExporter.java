package com.syc.sai.procesos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import com.syc.fortimax.core.ExpedientExporterBusinessLogic;
import java.util.Base64;

public class ExpedientExporter {

    public static void main(String[] args) {
        try {
            String nombreCarpeta = null;
            if (args.length > 0)
                nombreCarpeta = args[0];
            String[] export = cargaFolios();
            ExpedientExporterBusinessLogic eebl = new ExpedientExporterBusinessLogic();
            String[] fileResult = eebl.exporSeveraltExpedients(export, nombreCarpeta, true);
            for (int i = 0; i < fileResult.length; i++) {
                File f = new File(fileResult[i]);
                System.out.println("Expediente exportado en : " + f.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] cargaFolios() throws Exception {
        File f = new File("/SubirArchivo/FoliosExportar.txt");
        BufferedReader entrada = null;
        List<String> l = new ArrayList<String>();
        try {
            entrada = new BufferedReader(new FileReader(f));
            while (entrada.ready()) {
                l.add(entrada.readLine());
            }
            return l.toArray(new String[l.size()]);
        } finally {
            if (entrada != null)
                try {
                    entrada.close();
                    entrada = null;
                } catch (Exception e) {
                }
        }
    }
}
