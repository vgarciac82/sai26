package com.syc.info.cfdi.export;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipOutputStream;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Fortimax;
import java.util.Base64;
import java.nio.file.Paths;

public class DocumentExporter {

    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            throw new Exception("No se recibio el archivo de entrada.");
        File dataFile = new File(args[0]);
        if (!dataFile.exists())
            throw new Exception("El archivo especificado " + dataFile.getName() + " no existe");
        File dirRaiz = new File("/procesos/ExportarDocumentos/" + CFDIUtils.getTodayDir());
        if (!dirRaiz.exists())
            dirRaiz.mkdirs();
        System.out.println("Los archivos exportados se almacenaran en: " + dirRaiz.getAbsolutePath());
        List<String> rutas = new ArrayList<String>();
        FileReader fr = null;
        BufferedReader br = null;
        List<String> log = new ArrayList<String>();
        Connection conn = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            conn = CFDIUtils.getStandAloneConnection();
            fr = new FileReader(dataFile);
            br = new BufferedReader(fr);
            String line = "";
            int renglon = 1;
            int i = 0;
            int cntFile = 1;
            while ((line = br.readLine()) != null) {
                String logLine = "";
                try {
                    if (i % 1000 == 0) {
                        if (fos != null) {
                            zos.flush();
                            zos.closeEntry();
                            zos.close();
                        }
                        String zipExportName = dirRaiz.getAbsolutePath() + "/" + "DocumentosExportados_" + cntFile + ".zip";
                        fos = new FileOutputStream(zipExportName);
                        zos = new ZipOutputStream(fos);
                        cntFile++;
                        rutas.add(zipExportName);
                    }
                    String[] valores = line.split(",", 5);
                    Fortimax nodo = new Fortimax(valores[0], Integer.parseInt(valores[1]), Integer.parseInt(valores[2]), Integer.parseInt(valores[3]));
                    Documento d = DocumentoManager.buscaDocumento(conn, nodo);
                    if (d == null || d.getPaginasDocumento() == null || d.getPaginasDocumento().length == 0)
                        throw new Exception("El documento: " + nodo + " no contiene paginas");
                    CFDIUtils.addToZip(zos, d, valores[4]);
                    logLine = "Renglon " + renglon + "; Nodo " + nodo + ";Procesado Exitosamente.";
                } catch (Exception e) {
                    logLine = "Renglon " + renglon + "; Error: " + e.toString();
                } finally {
                    log.add(logLine);
                }
                i++;
                renglon++;
            }
            try {
                zos.flush();
                zos.closeEntry();
                zos.close();
            } catch (Exception e) {
                System.err.println("Problemas cerradno flujo: " + e);
            }
            escribeLogs(log, "DocumentosExportados");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                br.close();
            fr = null;
            br = null;
            CloseObject.closeObject(conn);
        }
    }

    private static void escribeLogs(List<String> exportLog, String nombre) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            String pathExport = System.getProperty("user.home") + File.separatorChar + nombre + "_" + CFDIUtils.getTodayFile() + ".csv";
            System.out.println("Log escrito en: " + pathExport);
            fw = new FileWriter(pathExport);
            bw = new BufferedWriter(fw);
            for (Iterator<String> i = exportLog.iterator(); i.hasNext(); ) {
                bw.write(i.next() + "\n");
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            System.out.println("Error procesando log: " + e);
        } finally {
            fw = null;
            bw = null;
            System.gc();
        }
    }
}
