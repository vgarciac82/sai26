package com.syc.fortimax.retrieval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import java.util.Base64;
import java.nio.file.Paths;

public final class OCRIndex extends Thread {

    private static final int APLICACION = 0;

    private static final int GABINETE = 1;

    private static final int CARPETA = 2;

    private static final int DOCUMENTO = 3;

    private static final int PAGINA = 4;

    Connection conn = null;

    IndexWriter myWriter = null;

    FileWriter myLogFileWriter = null;

    private int numDocto = -1;

    private String[] OCRExecParams = null;

    private String[] luceneData = null;

    private String DBSysDateFunction = null;

    public OCRIndex(ThreadGroup someTG, String someString, Connection someConn, IndexWriter someWriter, FileWriter someLogFileWriter, String[] someOCRParams, String[] someLuceneData, String someDBSysDateFunction, int someNumDocto) {
        super(someTG, someString);
        myWriter = someWriter;
        myLogFileWriter = someLogFileWriter;
        OCRExecParams = someOCRParams;
        luceneData = someLuceneData;
        numDocto = someNumDocto;
        DBSysDateFunction = someDBSysDateFunction;
        conn = someConn;
    }

    public void run() {
        super.run();
        //El OCR regresa cero si todo OK
        int retVal = doOCR();
        if (retVal == 0) {
            //La indizacion de Lucene regresa cero si todo OK
            retVal = doIndex();
        }
        //Actualiza la base de datos SIEMPRE
        retVal = updateDB(retVal != 0);
        if (retVal < 0) {
            //quiere decir que no se pudo actualizar
            //la base de datos, por lo que tenemos
            //que borrar el archivo txt del OCR
            //para que se vuelva a intentar despues
            doDeleteOCRTxtFile();
        }
        //this.notify();
    }

    public int doOCR() {
        int retVal = -1;
        Runtime rt = Runtime.getRuntime();
        System.out.println("runtime=[" + rt.toString() + "]");
        Process tempProc = null;
        try {
            tempProc = rt.exec(OCRExecParams);
            //ESTO DEBERIA GARANTIZAR LA EJECUCION
            //DEL TOPOCR.EXE SIN QUE SE QUEDARA COLGADO
            //PERO NO ESTA SUCEDIENDO, Y SIN EMBARGO SI
            //ESTA DISMINUYENDO EL RENDIMIENTO A UNA
            //CUARTA PARTE, POR LO QUE SE DESHABILITARA
            /*
	            //Se redirige la salida de error a un OSS
	            OSStreamHandler errStream = new 
	            OSStreamHandler ("ErrStreamThread",
	            				 true,
	            				 tempProc.getErrorStream(), 
	            				 "ERROR", 
	            				 myLogFileWriter);            
	            
	            // any output?
	            OSStreamHandler outStream = new 
	            OSStreamHandler("OutStreamThread",
	            				false,
	            				tempProc.getInputStream(), 
	            				"OUTPUT",
	            				myLogFileWriter);
	                
	            // kick them off
	            errStream.start();
	            outStream.start();
	          */
            try {
                tempProc.waitFor();
            } catch (InterruptedException ie) {
                myLogFileWriter.write("[" + new Date() + "] INTERRUPTEDEXCEPTION: Error esperando al thread... " + printLuceneData() + "\n");
                tempProc.destroy();
                tempProc = null;
                //ie.printStackTrace();
            }
            retVal = tempProc.exitValue();
            if (retVal != 0) {
                StringBuffer execString = new StringBuffer();
                for (int i = 0; i < OCRExecParams.length; i++) {
                    execString.append(OCRExecParams[i]);
                    execString.append(" ");
                }
                myLogFileWriter.write("[" + new Date() + "] OCR ERROR: retVal=[" + retVal + "], " + printLuceneData() + ", execString=[" + execString.toString() + "]\n");
            }
        } catch (IOException ioe) {
            try {
                myLogFileWriter.write("[" + new Date() + "] I/O EXCEPTION: retVal=[" + retVal + "], " + printLuceneData() + "\n");
            } catch (IOException ioe2) {
                //ignore
            }
        }
        return retVal;
    }

    private int doIndex() {
        int retVal = -1;
        File txtFile = new File(OCRExecParams[3]);
        BadScanFilter.inputFileName = txtFile.getPath();
        Document myDoc = null;
        try {
            myDoc = this.createDocument(txtFile);
        } catch (FileNotFoundException fnf) {
            try {
                myLogFileWriter.write("[" + new Date() + "] FILENOTFOUNDEXCEPTION: No encontro el archivo=[" + txtFile.getPath() + "]\n");
            } catch (IOException ioe) {
                //ignore
            }
        }
        if (myDoc != null) {
            try {
                myWriter.addDocument(myDoc);
                //myWriter.optimize();
                retVal = 0;
            } catch (CorruptIndexException cie) {
                try {
                    myLogFileWriter.write("[" + new Date() + "] CorruptIndexException: Al procesar el archivo=[" + txtFile.getPath() + "]\n");
                } catch (IOException ioe) {
                    //ignore
                }
            } catch (IOException ioe) {
                try {
                    myLogFileWriter.write("[" + new Date() + "] IOException: Al procesar el archivo=[" + txtFile.getPath() + "]\n");
                } catch (IOException ioe2) {
                    //ignore
                }
            } catch (NullPointerException npe) {
                try {
                    myLogFileWriter.write("Se pachequeo con el docto=[" + numDocto + "]\n");
                } catch (IOException ioe) {
                    //ignore
                }
            }
        }
        return retVal;
    }

    private int updateDB(boolean isError) {
        String query = "";
        int retVal = -1;
        try {
            boolean noNulls = luceneData[APLICACION] != null & luceneData[GABINETE] != null & luceneData[CARPETA] != null & luceneData[DOCUMENTO] != null & luceneData[PAGINA] != null;
            if (noNulls) {
                query = "UPDATE IMX_PAGINA_INDEX " + "SET PROCESADO = ? , " + "FH_MODIFICACION = " + DBSysDateFunction + " WHERE " + "TITULO_APLICACION = ?  AND " + "ID_GABINETE       = ?  AND " + "ID_CARPETA_PADRE  = ?  AND " + "ID_DOCUMENTO      = ?  AND " + "NUMERO_PAGINA  = ? ";
                PreparedStatement prepStmt = conn.prepareStatement(query);
                //COLUMNA PROCESADO
                prepStmt.setString(1, ((isError) ? "E" : "1"));
                //CAMPOS DEL WHERE
                prepStmt.setString(2, luceneData[APLICACION]);
                prepStmt.setInt(3, Integer.parseInt(luceneData[GABINETE]));
                prepStmt.setInt(4, Integer.parseInt(luceneData[CARPETA]));
                prepStmt.setInt(5, Integer.parseInt(luceneData[DOCUMENTO]));
                prepStmt.setInt(6, Integer.parseInt(luceneData[PAGINA]));
                retVal = prepStmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            try {
                myLogFileWriter.write("[" + new Date() + "] SQLException: Al ejecutar el query=[" + query + "]\n");
            } catch (IOException ioe) {
                //ignore
            }
        }
        return retVal;
    }

    private Document createDocument(File f) throws java.io.FileNotFoundException {
        Document doc = null;
        String metatag = "";
        // make a new, empty document
        doc = new Document();
        // Add the path of the file as a field named "path".  Use a field that is
        // indexed (i.e. searchable), but don't tokenize the field into words.
        doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        // Llave de los documentos de IMAX
        // indexed (i.e. searchable), but don't tokenize the field into words.
        doc.add(new Field("aplicacion", luceneData[APLICACION], Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("gabinete", luceneData[GABINETE], Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("carpeta", luceneData[CARPETA], Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("documento", luceneData[DOCUMENTO], Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("pagina", luceneData[PAGINA], Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("metatag", metatag, Field.Store.YES, Field.Index.NOT_ANALYZED));
        // Add the contents of the file to a field named "contents".  Specify a Reader,
        // so that the text of the file is tokenized and indexed, but not stored.
        // Note that FileReader expects the file to be in the system's default encoding.
        // If that's not the case searching for special characters will fail.
        doc.add(new Field("contents", new FileReader(f)));
        // return the document
        return doc;
    }

    public void doDeleteOCRTxtFile() {
        File txtFile = new File(OCRExecParams[3]);
        if (txtFile.exists()) {
            txtFile.delete();
        }
    }

    private String printLuceneData() {
        return "Documento num[" + numDocto + "], aplicacion[" + luceneData[APLICACION] + "], gabinete[" + luceneData[GABINETE] + "], carpeta[" + luceneData[CARPETA] + "], documento[" + luceneData[DOCUMENTO] + "], pagina[" + luceneData[PAGINA] + "]";
    }
}
