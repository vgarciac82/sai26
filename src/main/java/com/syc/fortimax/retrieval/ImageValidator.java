package com.syc.fortimax.retrieval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Base64;
import java.nio.file.Paths;

public class ImageValidator extends Thread {

    private String DRIVER_CLASS_NAME = null;

    private String CONNECTION_STRING = null;

    private String LOG_FILE_PATH = null;

    private FileWriter myLogFileWriter = null;

    private File myLogFile = null;

    private boolean searchExistent = false;

    public ImageValidator() {
        java.util.Properties props = new java.util.Properties();
        try {
            java.io.FileInputStream fis = new java.io.FileInputStream("fortindexer.properties");
            props.load(fis);
            fis.close();
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        this.DRIVER_CLASS_NAME = props.getProperty("driver.class.name");
        this.CONNECTION_STRING = props.getProperty("connection.string");
        this.LOG_FILE_PATH = props.getProperty("log.file.path");
        this.searchExistent = Boolean.parseBoolean(props.getProperty("search.existent"));
        myLogFile = new File(LOG_FILE_PATH + "IMAGE_VALIDATOR.log");
        try {
            myLogFileWriter = new FileWriter(myLogFile, false);
            myLogFileWriter.write("[" + new Date() + "] INICIA PROCESO\n");
        } catch (IOException ioe) {
            //ignore
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ImageValidator iv = new ImageValidator();
        System.out.println("Inicia=[" + new java.util.Date() + "]");
        iv.run();
        System.out.println("Termina=[" + new java.util.Date() + "]");
    }

    public void run() {
        // TODO Auto-generated method stub
        super.run();
        searchFortimaxDB(connectToDB());
    }

    public void searchFortimaxDB(Connection conn) {
        PreparedStatement prep0 = null;
        ResultSet rs0 = null;
        //obtenemos la ruta de la unidad de indice (ESTADO_UNIDAD=5)
        try {
            String query0 = "SELECT TITULO_APLICACION FROM IMX_APLICACION ";
            prep0 = conn.prepareStatement(query0);
            rs0 = prep0.executeQuery();
            int reg = 0;
            while (!conn.isClosed() && rs0.next()) {
                String strTitApp = rs0.getString("TITULO_APLICACION");
                System.out.println("Procesando la aplicacion=[" + strTitApp + "]");
                String query1 = "SELECT DISTINCT VOLUMEN FROM IMX_PAGINA WHERE TITULO_APLICACION='" + strTitApp + "'";
                PreparedStatement prep1 = conn.prepareStatement(query1);
                ;
                ResultSet rs1 = prep1.executeQuery();
                while (!conn.isClosed() && rs1.next()) {
                    String strVolumen = rs1.getString("VOLUMEN");
                    System.out.println("Procesando el volumen=[" + strVolumen + "]");
                    String query2 = "SELECT V.UNIDAD_DISCO, V.RUTA_BASE, " + "V.RUTA_DIRECTORIO, P.NOM_ARCHIVO_VOL, " + "P.NOM_ARCHIVO_ORG, P.TIPO_PAGINA, " + "P.TITULO_APLICACION, P.ID_GABINETE, " + "P.ID_CARPETA_PADRE, P.ID_DOCUMENTO, " + "P.NUMERO_PAGINA, P.VOLUMEN, D.NOMBRE_DOCUMENTO  " + "FROM IMX_PAGINA P, IMX_DOCUMENTO D, IMX_VOLUMEN V WITH (NOLOCK) " + "WHERE V.VOLUMEN = P.VOLUMEN " + "AND P.TITULO_APLICACION = D.TITULO_APLICACION " + "AND P.ID_GABINETE = D.ID_GABINETE " + "AND P.ID_CARPETA_PADRE = D.ID_CARPETA_PADRE " + "AND P.ID_DOCUMENTO = D.ID_DOCUMENTO " + "AND P.TITULO_APLICACION='" + strTitApp + "' " + "AND P.VOLUMEN='" + strVolumen + "' " + "ORDER BY P.TITULO_APLICACION, P.ID_GABINETE, " + "P.ID_CARPETA_PADRE, P.ID_DOCUMENTO, P.NUMERO_PAGINA ";
                    PreparedStatement prep2 = conn.prepareStatement(query2);
                    ResultSet rs2 = prep2.executeQuery();
                    while (!conn.isClosed() && rs2.next()) {
                        String nombreArchivoImg = rs2.getString("UNIDAD_DISCO") + rs2.getString("RUTA_BASE") + rs2.getString("RUTA_DIRECTORIO") + rs2.getString("NOM_ARCHIVO_VOL");
                        //CONTABILIDAD_G271C0D0&image.index=9
                        String claveExpediente = rs2.getString("TITULO_APLICACION") + "_G" + rs2.getString("ID_GABINETE") + "C" + rs2.getString("ID_CARPETA_PADRE") + "D" + rs2.getString("ID_DOCUMENTO") + "&image.index=" + rs2.getString("NUMERO_PAGINA");
                        File tmpFile = new File(nombreArchivoImg);
                        if (searchExistent) {
                            if (tmpFile.exists()) {
                                String logLine = "Si existe el archivo clave=[" + claveExpediente + "] nombre archivo=[" + nombreArchivoImg + "] nombre documento=[" + rs2.getString("NOMBRE_DOCUMENTO") + "]\n";
                                //System.out.print(logLine);
                                myLogFileWriter.write(logLine);
                            }
                        } else {
                            if (!tmpFile.exists()) {
                                String logLine = "No existe el archivo clave=[" + claveExpediente + "] nombre archivo=[" + nombreArchivoImg + "] nombre documento=[" + rs2.getString("NOMBRE_DOCUMENTO") + "]\n";
                                //System.out.println(logLine);
                                myLogFileWriter.write(logLine);
                            }
                        }
                        /*
						String nombreArchivoTxt = rutaOCRText
												+ rs.getString("VOLUMEN")
												+ "_"
											 	+ ToolBox.getFileName(rs.getString("NOM_ARCHIVO_VOL"))
											 	+ ".txt";

						//Datos para indexar
						String[] luceneData = new String[5];
						luceneData[0]	= rs.getString("TITULO_APLICACION");
						luceneData[1] 	= rs.getString("ID_GABINETE");
						luceneData[2] 	= rs.getString("ID_CARPETA_PADRE");
						luceneData[3] 	= rs.getString("ID_DOCUMENTO");
						luceneData[4] 	= rs.getString("NUMERO_PAGINA");
						*/
                        reg++;
                        if (reg % 10000 == 0) {
                            String logLine = "registros procesados=[" + reg + "], date=[" + new java.util.Date() + "]\n";
                            System.out.print(logLine);
                            //myLogFileWriter.write(logLine);
                        }
                    }
                    rs2.close();
                    rs2 = null;
                    prep2.close();
                    prep2 = null;
                }
                rs1.close();
                rs1 = null;
                prep1.close();
                prep1 = null;
            }
            rs0.close();
            rs0 = null;
            prep0.close();
            prep0 = null;
            conn.close();
            conn = null;
            //Ahora solo se optimiza al final del proceso
            myLogFileWriter.write("[" + new Date() + "] FIN DEL PROCESO\n");
            myLogFileWriter.flush();
            myLogFileWriter.close();
        } catch (SQLException sqle) {
            try {
                myLogFileWriter.write("Excepcion=[" + sqle.toString() + "]");
            } catch (Exception e) {
                //ignore!
            }
            sqle.printStackTrace();
        } catch (IOException ioe) {
            try {
                myLogFileWriter.write("Excepcion=[" + ioe.toString() + "]");
            } catch (Exception e) {
                //ignore!
            }
            ioe.printStackTrace();
        } catch (Exception e1) {
            try {
                myLogFileWriter.write("Excepcion=[" + e1.toString() + "]");
            } catch (Exception e) {
                //ignore!
            }
            e1.printStackTrace();
        }
        // System.gc();
    }

    public Connection connectToDB() {
        Connection conn = null;
        try {
            Class.forName(DRIVER_CLASS_NAME);
            conn = java.sql.DriverManager.getConnection(CONNECTION_STRING);
        } catch (ClassNotFoundException cnf) {
            System.out.println("Error en connectToDB...");
            cnf.printStackTrace();
        } catch (SQLException sqle) {
            System.out.println("Error en connectToDB...");
            sqle.printStackTrace();
        }
        return conn;
    }
}
