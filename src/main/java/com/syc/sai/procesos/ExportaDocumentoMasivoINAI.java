package com.syc.sai.procesos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportaDocumentoMasivoINAI {

    private static final Logger log = LoggerFactory.getLogger(ExportaDocumentoMasivoINAI.class);

    public static void main(String[] args) {
        String inputFile = args[0];
        String docName = args[1];
        String outDir = args[2];
        new ExportaDocumentoMasivoINAI().makeExport(inputFile, docName, outDir);
    }

    public int makeExport(String inputFile, String nombreDocumento, String dirSalida) {
        File fEntrada = new File(inputFile);
        if (!fEntrada.exists())
            throw new RuntimeException("No existe el archivo de entrada");
        File fDirSalida = new File(dirSalida);
        if (!fDirSalida.exists())
            fDirSalida.mkdirs();
        String baseFileName = "Export_" + Util.getFechaHoraActualFN();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        int totalDocs = 0;
        int cntFile = 1;
        Connection conn = null;
        List<File> rutas = new ArrayList<File>();
        try {
            List<DocumentoExtraer> lista = cargaInformacion(fEntrada);
            int i = 0;
            conn = Util.getStandAloneConnection();
            for (DocumentoExtraer docto : lista) {
                log.info(String.format("Exportando: %s Folio: %d", docto.getTituloAplicacion(), docto.getFolioDocumento()));
                if (i % 1000 == 0) {
                    if (fos != null) {
                        zos.flush();
                        zos.closeEntry();
                        zos.close();
                    }
                    File zipExportName = new File(fDirSalida, baseFileName + "_" + cntFile + ".zip");
                    fos = new FileOutputStream(zipExportName);
                    zos = new ZipOutputStream(fos);
                    cntFile++;
                    rutas.add(zipExportName);
                }
                Caso c = CasoManager.findByFolioLike(conn, docto.getTituloAplicacion(), String.valueOf(docto.getFolioDocumento()));
                if (c == null) {
                    log.info(String.format("Expediente sin caso: %s folio %d", docto.getTituloAplicacion(), docto.getFolioDocumento()));
                    i++;
                    continue;
                }
                Carpeta cRaiz = CarpetaManager.getCarpetaRaiz(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete());
                String raiz = cRaiz.getNombreCarpeta();
                List<Documento> exportL = DocumentoManager.listaDocumentoExportar(conn, raiz, docto.getTituloAplicacion(), c.getIdGabinete(), nombreDocumento);
                if (exportL != null && exportL.size() > 0)
                    Util.addToZip(zos, exportL.toArray(new Documento[exportL.size()]));
                else {
                    log.info(String.format("Expediente sin contenido en el documento %s en la aplicacion: %s con  folio %d ", nombreDocumento, docto.getTituloAplicacion(), docto.getFolioDocumento()));
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
            log.info("Se exportaron " + totalDocs + " Expedientes");
            return totalDocs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private List<DocumentoExtraer> cargaInformacion(File fEntrada) throws NumberFormatException, IOException {
        BufferedReader entrada = null;
        List<DocumentoExtraer> l = new ArrayList<DocumentoExtraer>();
        try {
            entrada = new BufferedReader(new FileReader(fEntrada));
            while (entrada.ready()) {
                String renglon = entrada.readLine();
                String[] info = renglon.split(",");
                DocumentoExtraer docto = new DocumentoExtraer();
                docto.setTituloAplicacion(info[0]);
                docto.setFolioDocumento(Integer.parseInt(info[1]));
                l.add(docto);
            }
            return l;
        } finally {
            if (entrada != null)
                try {
                    entrada.close();
                    entrada = null;
                } catch (Exception e) {
                    log.warn("Problemas cerrando archivo de entrada" + e);
                }
        }
    }

    private class DocumentoExtraer {

        private String tituloAplicacion;

        private int folioDocumento;

        public String getTituloAplicacion() {
            return tituloAplicacion;
        }

        public void setTituloAplicacion(String tituloAplicacion) {
            this.tituloAplicacion = tituloAplicacion;
        }

        public int getFolioDocumento() {
            return folioDocumento;
        }

        public void setFolioDocumento(int folioDocumento) {
            this.folioDocumento = folioDocumento;
        }
    }
}
