package com.syc.fortimax.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.WriteOutContentHandler;
import org.xml.sax.ContentHandler;

import com.syc.dbms.DBMS;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.retrieval.BadScanFilter;
import com.syc.fortimax.retrieval.SpanishAnalyzer;
import com.syc.gestion.core.Caso;
import com.syc.utils.ToolBox;

public class PaginaIndexManager {

    private String OCR_PROGRAM_PATH = null;

    private String OCR_IN_FILENAME = null;

    private String OCR_PARAMETER1 = null;

    private String OCR_PARAMETER2 = null;

    private String OCR_OUT_FILENAME = null;

    private String LUCENE_DB_PATH = null;

    private String LUCENE_STOPWORDS_PATH = null;

    private int LUCENE_MERGEFACTOR = 100;

    private int LUCENE_MAXMERGEDOCS = 100000;

    public PaginaIndexManager(String OCRProgramPath, String OCRParameter1, String OCRParameter2, String luceneDbPath, String luceneStopwordsPath, int luceneMergeFactor, int luceneMaxMergeDocs) {
        this.OCR_PROGRAM_PATH = OCRProgramPath;
        this.OCR_PARAMETER1 = OCRParameter1;
        this.OCR_PARAMETER2 = OCRParameter2;
        this.LUCENE_DB_PATH = luceneDbPath;
        this.LUCENE_STOPWORDS_PATH = luceneStopwordsPath;
        this.LUCENE_MERGEFACTOR = luceneMergeFactor;
        this.LUCENE_MAXMERGEDOCS = luceneMaxMergeDocs;
    }

    public boolean insertPaginaIndex(Connection conn, Documento d, String nombre_original) throws SQLException {
        DBMS oDBMS = null;
        oDBMS = new DBMS(conn.getMetaData().getDatabaseProductName());
        boolean retVal = false;
        try {
            String myQuery = "INSERT INTO imx_pagina_index " + "(titulo_aplicacion" + ", id_gabinete" + ", id_carpeta_padre" + ", id_documento" + ", numero_pagina" + ", fh_modificacion" + ", procesado" + ", documento_original" + ") " + "VALUES ('" + d.getTituloAplicacion() + "', " + d.getIdGabinete() + ", " + d.getIdCarpetaPadre() + ", " + d.getIdDocumento() + ", " + d.getNumeroPaginas() + ", " + oDBMS.SQLFunc_Now() + ", 0, '" + nombre_original + "')";
            //System.out.println ("Query=["+myQuery+"]");
            PreparedStatement pstmnt = conn.prepareStatement(myQuery);
            pstmnt.execute();
            retVal = true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            //ignore
        }
        return retVal;
    }

    public boolean updatePaginaIndex(Pagina p, String estatus) throws SQLException {
        boolean retVal = false;
        Connection conn = null;
        PreparedStatement pstmnt = null;
        try {
            conn = DataSourceManager.getConnection("jdbc/gestion");
            String myQuery = "update imx_pagina_index " + "set procesado = '" + estatus + "' " + "where TITULO_APLICACION = '" + p.getTituloAplicacion() + "' " + " and ID_GABINETE = " + p.getIdGabinete() + " and ID_CARPETA_PADRE = " + p.getIdCarpetaPadre() + " and ID_DOCUMENTO = " + p.getIdDocumento() + " and NUMERO_PAGINA = " + p.getNumeroPagina();
            //System.out.println ("Query=["+myQuery+"]");
            pstmnt = conn.prepareStatement(myQuery);
            pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
            retVal = true;
        } catch (SQLException sqle) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException sqle2) {
            }
            sqle.printStackTrace();
            throw new SQLException(sqle.getMessage());
            //ignore
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (conn != null)
                conn.close();
            pstmnt = null;
            conn = null;
        }
        return retVal;
    }

    public boolean extraeContenidoPagina(Caso c, Documento d, String nombre_original, int numPag) throws SQLException {
        boolean retVal = false;
        //SE OBTIENE LA PAGINA QUE SE ESTA BUSCANDO
        Pagina p = d.getPaginaDocumento(numPag - 1);
        //SI ES IMAGEN, DEBE GENERAR EL ARCHIVO DE TEXTO LLAMANDO AL OCR
        //SI ES CUALQUIER OTRA COSA, DEBE DE EXTRAER CONTENIDO CON TIKA
        String inFileName = p.getFullPathFileName();
        String outFileName = ToolBox.removeFileExtension(inFileName) + ".txt";
        String inFileExtention = ToolBox.getFileExtension(p.getNomArchivoOrg());
        //System.out.println("inFileName=["+inFileName+"], inFileExtention=["+inFileExtention+"], outFileName=["+outFileName+"]");
        if (inFileExtention.matches("^(tif|jpg|jpeg|gif|bmp|png)$")) {
            //System.out.println("extractUsingOCR");
            int intRetVal = extractUsingOCR(inFileName, outFileName);
            retVal = (intRetVal == 0);
        } else {
            //System.out.println("extractUsingTika");
            retVal = extractUsingTika(inFileName, outFileName);
        }
        //Aqui procesamos el archivo TXT de contenido
        if (retVal) {
            int intRetVal = createLuceneDocument(outFileName, c, d);
            retVal = (intRetVal == 0);
        }
        //AQUI DEBEMOS ACTUALIZAR LA TABLA IMX_PAGINA_INDEX
        if (retVal) {
            retVal = updatePaginaIndex(p, "1");
        }
        return retVal;
    }

    public int extractUsingOCR(String inFileName, String outFileName) {
        String execCmd = null;
        int retVal = -1;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("win") >= 0) {
            execCmd = //+ this.OCR_PARAMETER2 + " "
            this.OCR_PROGRAM_PATH + " " + this.OCR_IN_FILENAME + " " + this.OCR_PARAMETER1 + " " + this.OCR_OUT_FILENAME;
        } else if (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0) {
            execCmd = this.OCR_PROGRAM_PATH + " " + this.OCR_IN_FILENAME + " " + this.OCR_OUT_FILENAME.substring(0, this.OCR_OUT_FILENAME.length() - 4) + " " + this.OCR_PARAMETER1;
        }
        Runtime rt = Runtime.getRuntime();
        //System.out.println("osName=["+osName+"], execCmd=["+execCmd+"]");
        Process tempProc = null;
        try {
            tempProc = rt.exec(execCmd);
            try {
                tempProc.waitFor();
            } catch (InterruptedException ie) {
                System.out.println("[" + new java.util.Date() + "] INTERRUPTEDEXCEPTION: Error esperando al thread... ");
                tempProc.destroy();
                tempProc = null;
                //ie.printStackTrace();
            }
            retVal = tempProc.exitValue();
            if (retVal != 0) {
                System.out.println("[" + new java.util.Date() + "] OCR ERROR: retVal=[" + retVal + "]");
            }
        } catch (IOException ioe) {
            System.out.println("[" + new java.util.Date() + "] I/O EXCEPTION: retVal=[" + retVal + "]");
        }
        return retVal;
    }

    public boolean extractUsingTika(String inFileName, String outFileName) {
        boolean retVal = false;
        try {
            FileInputStream fis = new FileInputStream(inFileName);
            Metadata metadata = new Metadata();
            StringWriter writer = new StringWriter();
            ContentHandler handler = new WriteOutContentHandler(writer);
            new AutoDetectParser().parse(fis, handler, metadata);
            //AQUI LO ESCRIBIMOS A UN ARCHIVO
            FileWriter fw = new FileWriter(outFileName, false);
            fw.write(writer.toString());
            fw.flush();
            fw.close();
            //System.out.println("Stop!");
            fis.close();
            retVal = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //nothing
        }
        return retVal;
    }

    public int createLuceneDocument(String outFileName, Caso c, Documento d) {
        int retVal = -1;
        int idCaso = c.getIdCaso();
        String strIdCaso = idCaso + "";
        char myChar = strIdCaso.charAt(strIdCaso.length() - 1);
        File txtFile = new File(outFileName);
        BadScanFilter.inputFileName = txtFile.getPath();
        Document myDoc = null;
        try {
            myDoc = createLuceneDocument(txtFile, c, d);
        } catch (FileNotFoundException fnf) {
            System.out.println("[" + new java.util.Date() + "] FILENOTFOUNDEXCEPTION: No encontro el archivo=[" + txtFile.getPath() + "]");
        }
        if (myDoc != null) {
            retVal = insertLuceneDocument(myDoc, txtFile, outFileName);
        }
        return retVal;
    }

    private synchronized int insertLuceneDocument(Document myDoc, File txtFile, String outFileName) {
        int retVal = -1;
        IndexWriter myWriter = openLuceneIndexWriter(this.LUCENE_DB_PATH);
        try {
            myWriter.addDocument(myDoc);
            if (null != myWriter) {
                try {
                    myWriter.close();
                    myWriter = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            retVal = 0;
        } catch (CorruptIndexException cie) {
            System.out.println("[" + new java.util.Date() + "] CorruptIndexException: Al procesar el archivo=[" + txtFile.getPath() + "]");
        } catch (IOException ioe) {
            System.out.println("[" + new java.util.Date() + "] IOException: Al procesar el archivo=[" + txtFile.getPath() + "]");
        } catch (NullPointerException npe) {
            System.out.println("Se pachequeo con el docto=[" + outFileName + "]");
        }
        return retVal;
    }

    public Document createLuceneDocument(File f, Caso c, Documento d) throws java.io.FileNotFoundException {
        Document doc = null;
        String metatag = "";
        // make a new, empty document
        doc = new Document();
        // Add the path of the file as a field named "path".  Use a field that is
        // indexed (i.e. searchable), but don't tokenize the field into words.
        doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        // Llave de los documentos de IMAX
        // indexed (i.e. searchable), but don't tokenize the field into words.
        //campos de fortimax
        //luceneData[APLICACION]
        doc.add(new Field("aplicacion", d.getTituloAplicacion(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        //luceneData[GABINETE]
        doc.add(new Field("gabinete", Integer.toString(d.getIdGabinete()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        //luceneData[CARPETA]
        doc.add(new Field("carpeta", Integer.toString(d.getIdCarpetaPadre()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        //luceneData[DOCUMENTO]
        doc.add(new Field("documento", Integer.toString(d.getIdDocumento()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        //luceneData[PAGINA]
        doc.add(new Field("pagina", Integer.toString(d.getIdDocumento()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        //campos de gestion
        String tipo_caso = null;
        String id_caso = null;
        String folio = null;
        //El caso podria ser nulo si el docto no se agrega de gestion!!!
        if (c != null) {
            tipo_caso = Integer.toString(c.getIdTC());
            id_caso = Integer.toString(c.getIdCaso());
            folio = c.getFolio();
        }
        //luceneData[TIPO_CASO]
        doc.add(new Field("tipo_caso", tipo_caso, Field.Store.YES, Field.Index.NOT_ANALYZED));
        //luceneData[ID_CASO]
        doc.add(new Field("id_caso", id_caso, Field.Store.YES, Field.Index.NOT_ANALYZED));
        //luceneData[FOLIO]
        doc.add(new Field("folio", folio, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("metatag", metatag, Field.Store.YES, Field.Index.NOT_ANALYZED));
        // Add the contents of the file to a field named "contents".  Specify a Reader,
        // so that the text of the file is tokenized and indexed, but not stored.
        // Note that FileReader expects the file to be in the system's default encoding.
        // If that's not the case searching for special characters will fail.
        doc.add(new Field("contents", new FileReader(f)));
        // return the document
        return doc;
    }

    public IndexWriter openLuceneIndexWriter(String pathToWriter) {
        IndexWriter retVal = null;
        File f = new File(pathToWriter);
        // create index if the directory does not exist
        boolean create = false;
        if (!(f.exists() && f.isDirectory()) || f.isDirectory() & f.list().length == 0) {
            create = true;
        }
        //Forzamos el unlock del indice
        if (!create) {
            File fndx = new File(pathToWriter + "write.lock");
            if (fndx.exists()) {
                fndx.delete();
            }
        }
        try {
            retVal = new IndexWriter(pathToWriter, new SpanishAnalyzer(this.LUCENE_STOPWORDS_PATH), create);
            retVal.setMergeFactor(this.LUCENE_MERGEFACTOR);
            retVal.setMaxMergeDocs(this.LUCENE_MAXMERGEDOCS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public boolean deleteLuceneIndexEntries(String tituloAplicacion, int idGabinete, int idCarpetaPadre, int idDocumento, int idPagina) {
        Hits myHits = null;
        IndexSearcher searcher = null;
        boolean retVal = false;
        IndexWriter myWriter = openLuceneIndexWriter(this.LUCENE_DB_PATH);
        Vector vCampos = new Vector();
        Vector vValores = new Vector();
        if (tituloAplicacion != null && tituloAplicacion.trim().length() > 0) {
            vCampos.add("aplicacion");
            vValores.add(tituloAplicacion);
        }
        if (idGabinete > -1) {
            vCampos.add("gabinete");
            vValores.add(Integer.toString(idGabinete));
        }
        if (idCarpetaPadre > -1) {
            vCampos.add("carpeta");
            vValores.add(Integer.toString(idCarpetaPadre));
        }
        if (idDocumento > -1) {
            vCampos.add("documento");
            vValores.add(Integer.toString(idDocumento));
        }
        if (idPagina > -1) {
            vCampos.add("pagina");
            vValores.add(Integer.toString(idPagina));
        }
        String[] campos = new String[vCampos.size()];
        vCampos.toArray(campos);
        String[] valores = new String[vValores.size()];
        vValores.toArray(valores);
        try {
            searcher = new IndexSearcher(this.LUCENE_DB_PATH);
            if (myWriter != null) {
                Query someQuery = MultiFieldQueryParser.parse(campos, valores, new WhitespaceAnalyzer());
                myHits = searcher.search(someQuery);
                //myWriter.deleteDocuments(someQuery);
                myWriter.close();
            }
        } catch (ParseException pe) {
        } catch (CorruptIndexException cie) {
            cie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myWriter = null;
        }
        return retVal;
    }
}
