/**
 *
 */
package com.syc.fortimax.retrieval;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

/**
 * @author Guidny
 *
 */
public class Indexer {
	private static final String INDEX_DIR ="./Index";

	private static IndexWriter myWriter = null;
	private static String myPath = null;
	private static int processedFiles = 0;

	private static final int APLICACION = 0;
	private static final int GABINETE 	= 1;
	private static final int CARPETA 	= 2;
	private static final int DOCUMENTO 	= 3;
	private static final int PAGINA 	= 4;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Indexer myIndexer=new Indexer();

		File dataDir = new File("./IndexSource");
		System.out.println("Inicia=["+new java.util.Date()+"]");

		myWriter = open(args[0], null);

		getFiles(dataDir);

		close(myWriter);

		System.out.println("Termina=["+new java.util.Date()+"]");
		System.out.println("indexo=["+processedFiles+"] documentos");



	}

	public static void index(Connection conn, File dataFile) {
		index(conn, INDEX_DIR, dataFile, null);
	}



	public static void index(Connection conn, String indexPath,
							 File dataFile, IndexWriter someWriter) {
		// 1. Convert indexed object to a document
		// 2. Write document to IndexWriter
		// directory, where to store the index
		// files
		//String indexFile = INDEX_DIR + "/fileindex";

		BadScanFilter.inputFileName=dataFile.getPath();

		// to index documents, they are 'written' or added to
		// an IndexWriter
		if (someWriter==null) {
			someWriter = open(indexPath, someWriter);
		}

		try {

				//myWriter = new IndexWriter(indexPath, new StandardAnalyzer());
				someWriter.setMergeFactor(20);

				// now add this document to the Index
				// we use an adapter class, which is given a
				// file and returns a document, which lucene
				// can index
				String[] result = searchFortimaxDB(conn, dataFile.getName());

				Document myDoc = createDocument(result, dataFile);
				if (myDoc != null) {
					myWriter.addDocument(myDoc);
					myWriter.optimize();
				}

		} catch(IOException e) {
			System.out.println("Unable to index document.");
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println("Unable to index document.");
			e.printStackTrace();
		//} finally {
		//	close(myWriter);
		}
	}

	// close writer
	public static IndexWriter open(String pathToWriter, IndexWriter writer) {
		File f = new File(pathToWriter);
		// create index if the directory does not exist
		boolean create = false;
		if (writer == null ||
			!(f.exists() && f.isDirectory())) {
			create = true;
		}

		//if(create) {
			try {
					writer = new IndexWriter(pathToWriter,
											 new SpanishAnalyzer("./spanishStopWords.dic"),
											 create,
											 IndexWriter.MaxFieldLength.LIMITED);
			} catch(Exception e) {
				e.printStackTrace();
			}
		//}

		return writer;
	}

	// close writer
	public static void close(IndexWriter writer) {
		if(null != writer) {
			try {
					writer.close();
			} catch(Exception e) {

			}
		}
	}

	public static Document createDocument(String[] result, File f)
      		throws java.io.FileNotFoundException {

		Document doc = null;

		String metatag  = "";

		if (result[APLICACION]!=null) {

		   // make a new, empty document
		   doc = new Document();

		   // Add the path of the file as a field named "path".  Use a field that is
		   // indexed (i.e. searchable), but don't tokenize the field into words.
		   doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));

		   // Llave de los documentos de IMAX
		   // indexed (i.e. searchable), but don't tokenize the field into words.
		   doc.add(new Field("aplicacion", 	result[APLICACION], Field.Store.YES, Field.Index.NOT_ANALYZED));
		   doc.add(new Field("gabinete", 	result[GABINETE], 	Field.Store.YES, Field.Index.NOT_ANALYZED));
		   doc.add(new Field("carpeta", 	result[CARPETA], 	Field.Store.YES, Field.Index.NOT_ANALYZED));
		   doc.add(new Field("documento", 	result[DOCUMENTO], 	Field.Store.YES, Field.Index.NOT_ANALYZED));
		   doc.add(new Field("pagina", 		result[PAGINA], 	Field.Store.YES, Field.Index.NOT_ANALYZED));

		   doc.add(new Field("metatag", metatag, Field.Store.YES, Field.Index.NOT_ANALYZED));

		   // Add the contents of the file to a field named "contents".  Specify a Reader,
		   // so that the text of the file is tokenized and indexed, but not stored.
		   // Note that FileReader expects the file to be in the system's default encoding.
		   // If that's not the case searching for special characters will fail.
		   doc.add(new Field("contents", new FileReader(f)));
		}
		// return the document
		return doc;
	}

	public static void getFiles(File baseFileDir) {

		try {
				java.sql.DriverManager.registerDriver (new oracle.jdbc.OracleDriver());

				Connection conn = java.sql.DriverManager.getConnection
				  ("jdbc:oracle:thin:@localhost:1521:XE", "fortimax", "fortimax");



				File[] tmpFiles = baseFileDir.listFiles();
				for (int i=0; i<tmpFiles.length; i++) {
					File tmpFile = tmpFiles[i];
					if (tmpFile.isDirectory()) {
						getFiles(tmpFile);

					} else if (tmpFile.isFile()) {
						String name = tmpFile.getName();
						String extension = name.substring(name.length()-3);
						System.out.println("Nombre=["+name+"], Extension=["+extension+"]");
						if (extension.toUpperCase().equals("TXT")) {
							index(conn, myPath, tmpFile, myWriter);
							processedFiles++;
							if (processedFiles%50 == 0) {
								System.out.println("Indexados ["+processedFiles+"] archivos");
							}
						}

					}
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String[] searchFortimaxDB(Connection conn, String fileName) {

		String[] retVal = new String[5];

		//El nombre del archivo esta compuesto de volumen y nombre
		int separadorPos =fileName.indexOf('_');

		String strVolumen = "";
		String strNombre  = fileName;
		if (separadorPos>-1) {
			strVolumen = fileName.substring(0, separadorPos);
			strNombre  = fileName.substring(separadorPos+1);
		}
		//Al nombre le quitamos la extension .txt y le ponemos .tif fija
		separadorPos = strNombre.indexOf('.');
		if (separadorPos>-1) {
			strNombre = strNombre.substring(0, separadorPos) + ".tif";
		} else {
			strNombre = strNombre + ".tif";
		}

		PreparedStatement prep 	= null;
		ResultSet rs 			= null;

		// ya tenemos el nombre del archivo, ahora buscamos
		try {
			String query = "SELECT * FROM IMX_PAGINA where VOLUMEN='"
						 + strVolumen
						 + "' AND NOM_ARCHIVO_VOL = '"
						 + strNombre
						 + "'";
						 //+ "' AND TIPO_PAGINA = 'I'";

			prep = conn.prepareStatement(query);

			rs = prep.executeQuery();

			while(rs.next()){


				System.out.println( "[" +
									rs.getString("TITULO_APLICACION")+ "] [" +
									rs.getString("ID_GABINETE")+ "] [" +
									rs.getString("ID_CARPETA_PADRE")+ "] [" +
									rs.getString("ID_DOCUMENTO")+ "] [" +
									rs.getString("NUMERO_PAGINA")+ "] [" +
									rs.getString("VOLUMEN")+ "] [" +
									rs.getString("TIPO_VOLUMEN")+ "] [" +
									rs.getString("NOM_ARCHIVO_VOL")+ "] [" +
									rs.getString("NOM_ARCHIVO_ORG")+ "] [" +
									rs.getString("TIPO_PAGINA")+ "] [" +
									rs.getString("ESTADO_PAGINA")+ "] [" +
									rs.getString("TAMANO_BYTES")+ "]" );

				retVal[APLICACION] = rs.getString("TITULO_APLICACION");
				retVal[GABINETE] = rs.getString("ID_GABINETE");
				retVal[CARPETA] = rs.getString("ID_CARPETA_PADRE");
				retVal[DOCUMENTO] = rs.getString("ID_DOCUMENTO");
				retVal[PAGINA] = rs.getString("NUMERO_PAGINA");

			}
			rs.close();
			rs = null;
			prep.close();
			prep = null;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		// System.gc();

		return retVal;
	}

}