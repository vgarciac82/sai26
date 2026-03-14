package com.syc.fortimax.retrieval;

import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.PaginaIndexManager;
import com.syc.gestion.core.Caso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ExtractPageContent extends Thread {

    private static Logger log = LoggerFactory.getLogger(ExtractPageContent.class);

    private Caso caso = null;

    private Documento documento = null;

    private Pagina pagina = null;

    private String OCR_PROGRAM_PATH = null;

    private String OCR_IN_FILENAME = null;

    private String OCR_PARAMETER1 = null;

    private String OCR_PARAMETER2 = null;

    private String OCR_OUT_FILENAME = null;

    private String LUCENE_DB_PATH = null;

    private String LUCENE_STOPWORDS_PATH = null;

    private int LUCENE_MERGEFACTOR = 100;

    private int LUCENE_MAXMERGEDOCS = 100000;

    public ExtractPageContent(Caso c, Documento d, Pagina p, String OCRProgramPath, String OCRInFileName, String OCRParameter1, String OCRParameter2, String OCROutFileName, String luceneDbPath, String luceneStopwordsPath, int luceneMergeFactor, int luceneMaxMergeDocs) {
        this.caso = c;
        this.documento = d;
        this.pagina = p;
        this.OCR_PROGRAM_PATH = OCRProgramPath;
        this.OCR_IN_FILENAME = OCRInFileName;
        this.OCR_PARAMETER1 = OCRParameter1;
        this.OCR_PARAMETER2 = OCRParameter2;
        this.OCR_OUT_FILENAME = OCROutFileName;
        this.LUCENE_DB_PATH = luceneDbPath;
        this.LUCENE_STOPWORDS_PATH = luceneStopwordsPath;
        this.LUCENE_MERGEFACTOR = luceneMergeFactor;
        this.LUCENE_MAXMERGEDOCS = luceneMaxMergeDocs;
    }

    public void run() {
        //System.out.println("INICIA LUCENE");
        try {
            //GAF aqui va lo de la extraccion de contenido!!!!
            if (pagina != null) {
                //verifica que no se haya procesado
                if (pagina.getProcesado() != '1') {
                    PaginaIndexManager pim = new PaginaIndexManager(this.OCR_PROGRAM_PATH, this.OCR_PARAMETER1, this.OCR_PARAMETER2, this.LUCENE_DB_PATH, this.LUCENE_STOPWORDS_PATH, this.LUCENE_MERGEFACTOR, this.LUCENE_MAXMERGEDOCS);
                    pim.extraeContenidoPagina(caso, documento, pagina.getNomArchivoVol(), pagina.getNumeroPagina());
                } else {
                    //System.out.println("Esta pagina ya se proceso!");
                }
            } else {
                //System.out.println("Danger! Danger! La pagina es nula!");
            }
        } catch (Exception exc) {
            log.warn("Error occurred", "Error en LUCENE: " + exc.getMessage());
            exc.printStackTrace(System.out);
        }
        //System.out.println("FINALIZA LUCENE");
    }
}
