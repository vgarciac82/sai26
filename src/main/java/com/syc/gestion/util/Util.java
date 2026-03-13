package com.syc.gestion.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import com.axtel.ws.clients.NotificaAdecuacionMetasCliente;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.core.ReporteAdecuacionesPorUN;
import com.syc.contable.core.ReportePrespuestal;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.ExportLogDetallado;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.UnidadEjecutora;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscal;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import net.sf.jasperreports.engine.JasperRunManager;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String dbPropertiesFilePath = "/procesos/config/dbConfig.properties";

    public static final int ALRM_UNKNOW = -1;

    public static final int ALRM_END = 0;

    public static final int ALRM_MAIL = 1;

    public static final String NUMBER_DOUBLE_FORMAT_CELL = "0.00";

    public static final String CURRENCY_FORMAT = "###,##0.00";

    public static final String NUMBER_INT_FORMAT_CELL = "0";

    public static final String[] NOMBRE_MESES_MX = { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" };

    public static final String[] NOMBRE_MESES_ADECUACIONES = { "ANUAL", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" };

    private static Logger log = LoggerFactory.getLogger(Util.class.getName());

    private static HSSFCellStyle styleInt = null;

    private static HSSFCellStyle styleFloat = null;

    private static HSSFCellStyle styleDate = null;

    private static final DecimalFormat CURRENCY_FORMATTER = new DecimalFormat(CURRENCY_FORMAT);

    public static final int[] DIAS_POR_MES = { 31, Util.diasFebrero(), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public static final String urlFilePathReporte = "c:/procesos/plantillas_reportes/";

    public static final String dbPropertiesFilePathGRM = "/procesos/config/dbConfig.propertiesGRM";

    public static String getTipoFuente() {
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic("jdbc/gestion");
        return cabl.getSystemSetting("FUENTE_INSTITUCIONAL");
    }

    public static int getAlarmaType(String str) {
        if (str == null) {
            log.debug("Comando nulo");
            return ALRM_UNKNOW;
        }
        if (str.startsWith("end")) {
            log.debug(str);
            return ALRM_END;
        }
        if (str.startsWith("mail")) {
            log.debug(str);
            return ALRM_MAIL;
        }
        log.debug("No implementado: " + str);
        return ALRM_UNKNOW;
    }

    private static int diasFebrero() {
        int year = (new GregorianCalendar()).get(Calendar.YEAR);
        if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
            return 29;
        else
            return 28;
    }

    public static String firstUpper(String name) {
        if (name.length() <= 0)
            return "";
        if (name.length() == 1)
            return name.toUpperCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String getFechaHoraActual(String tz) {
        DateFormat df = DateFormat.getInstance();
        df.setTimeZone(TimeZone.getTimeZone(tz));
        return df.format(new Date());
    }

    public static String getFechaHoraActual(Timestamp timestamp) {
        return getFechaHoraActual(null, timestamp);
    }

    public static String getFechaHoraActual(String tz, Timestamp timestamp) {
        DateFormat df = DateFormat.getInstance();
        if (tz != null)
            df.setTimeZone(TimeZone.getTimeZone(tz));
        return df.format(timestamp);
    }

    public static String getNombreTipoArchivo(int id_tca) {
        String s;
        switch(id_tca) {
            case 1:
                s = "js";
                break;
            case 2:
                s = "xml";
                break;
            case 3:
                s = "css";
                break;
            case 4:
                s = "html";
                break;
            default:
                log.error("Tipo de Archivo desconocido (" + id_tca + ")");
                throw new RuntimeException("Tipo de Archivo desconocido (" + id_tca + ")");
        }
        log.debug(id_tca + " = " + s);
        return s;
    }

    public static String getTipoDato(int tcv_tipo) {
        String s;
        switch(tcv_tipo) {
            case 3:
                s = "Small Integer";
                break;
            case 4:
                s = "Long Integer";
                break;
            case 5:
                s = "Decimal";
                break;
            case 7:
                s = "Double, Float";
                break;
            case 8:
                s = "Date";
                break;
            case 10:
                s = "String";
                break;
            case 12:
                s = "Long String";
                break;
            default:
                log.error("Tipo de Dato desconocido (" + tcv_tipo + ")");
                throw new RuntimeException("Tipo de Dato desconocido (" + tcv_tipo + ")");
        }
        log.debug(tcv_tipo + " = " + s);
        return s;
    }

    public static Timestamp agregaTiempo(Timestamp time, int timeToAdd, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(time.getTime());
        calendar.add(field, timeToAdd);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static boolean alcanzoTiempoLimite(Timestamp actual, Timestamp limite) {
        return actual.after(limite);
    }

    public static long getPromedioTimestamp(Timestamp inicio, Timestamp actual, Timestamp limite) {
        return (actual.getTime() - inicio.getTime()) * 100 / (limite.getTime() - inicio.getTime());
    }

    public static int getPromedioIndex(int promedio) {
        if (promedio <= 50)
            return 0;
        if ((promedio >= 51) && (promedio <= 99))
            return 1;
        return 2;
    }

    public static boolean notInFilter(int filter, int prom) {
        if (filter == -1)
            return false;
        switch(filter) {
            case 1:
                if (prom <= 50)
                    return false;
                break;
            case 2:
                if (prom >= 51 && prom <= 99)
                    return false;
                break;
            case 3:
                if (prom >= 100)
                    return false;
        }
        return true;
    }

    public static String getColorName(int promedio) {
        if (promedio <= 50)
            return "green";
        if (promedio >= 51 && promedio <= 99)
            return "yellow";
        return "red";
    }

    private static final String algorithm = "DESede";

    private static final String charsetName = "UTF8";

    public static final BigDecimal ZERO = new BigDecimal(0.00f);

    public static String encrypt(SecretKey key, String str) {
        try {
            byte[] utf8 = str.getBytes(charsetName);
            Cipher ecipher = Cipher.getInstance(algorithm);
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] enc = ecipher.doFinal(utf8);
            return new BASE64Encoder().encode(enc);
        } catch (NoSuchPaddingException e) {
            log.error(e);
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        } catch (InvalidKeyException e) {
            log.error(e);
        } catch (BadPaddingException e) {
            log.error(e);
        } catch (IllegalBlockSizeException e) {
            log.error(e);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        return null;
    }

    public static String decrypt(SecretKey key, String str) {
        try {
            byte[] dec = new BASE64Decoder().decodeBuffer(str);
            Cipher dcipher = Cipher.getInstance(algorithm);
            dcipher.init(Cipher.DECRYPT_MODE, key);
            byte[] utf8 = dcipher.doFinal(dec);
            return new String(utf8, charsetName);
        } catch (NoSuchPaddingException e) {
            log.error(e);
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        } catch (InvalidKeyException e) {
            log.error(e);
        } catch (BadPaddingException e) {
            log.error(e);
        } catch (IllegalBlockSizeException e) {
            log.error(e);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    public static SecretKey genSecretKey() {
        try {
            return KeyGenerator.getInstance(algorithm).generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        }
        return null;
    }

    public static String secretKeyToString(SecretKey k) {
        return new BASE64Encoder().encode(k.getEncoded());
    }

    public static SecretKey stringToSecretKey(String str) {
        try {
            return new SecretKeySpec(new BASE64Decoder().decodeBuffer(str), algorithm);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    public static String makeJSName(String str) {
        StringBuffer jsName = new StringBuffer();
        boolean makeCapital = false;
        for (int i = 0; i < str.length(); i++) {
            if ("_".equals(str.substring(i, i + 1))) {
                makeCapital = true;
                continue;
            }
            jsName.append(makeCapital ? str.substring(i, i + 1).toUpperCase() : str.substring(i, i + 1).toLowerCase());
            makeCapital = false;
        }
        return jsName.toString();
    }

    public static String encodeJS(String s) {
        String cReplaceStrong = "";
        cReplaceStrong = s.replace("\n", "\\n");
        cReplaceStrong = cReplaceStrong.replace("'", "");
        cReplaceStrong = cReplaceStrong.replace("[", "");
        cReplaceStrong = cReplaceStrong.replace("]", "");
        // cReplaceStrong = cReplaceStrong.replace("\\", "");
        return cReplaceStrong;
    }

    /**
     * Enmascara la funcion de extraccion de un Multipart-Form.
     *
     * @param req
     *            Request
     * @param tempDir
     *            Directorio temporal de carga
     * @param maxFileSize
     *            Maximo tamaño del archivo permitido. -1 sin limite
     * @return Lista con los componentes multipart del request.
     * @throws ServletException
     *             si ocurre un error en la extraccion.
     */
    public static List<?> parseRequest(HttpServletRequest req, String tempDir, long maxFileSize) throws ServletException {
        DiskFileUpload upload = new DiskFileUpload();
        upload.setRepositoryPath(tempDir);
        upload.setSizeMax(maxFileSize);
        try {
            return upload.parseRequest(req);
        } catch (FileUploadException fe) {
            fe.printStackTrace();
            throw new ServletException("Error de recepcion " + fe.getMessage());
        }
    }

    public static Map<String, String> requestToMap(HttpServletRequest req) throws Exception {
        StringBuilder jsonBuff = new StringBuilder();
        String line = null;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            String result = java.net.URLDecoder.decode(line, StandardCharsets.UTF_8.name());
            jsonBuff.append(result);
        }
        System.out.println("Request JSON string :" + jsonBuff.toString());
        JSONObject jsonObject = new JSONObject(jsonBuff.toString());
        return new HashMap<String, String>();
    }

    /**
     * Copia el contenido de un flujo de entrada a un archivo nuevo.
     *
     * @param archivoCargaStream
     *            Flujo de entrada abierto
     * @param nombreArchivoDestino
     *            Nombre del archivo donde se copiara el destino. Debe incluir
     *            la ruta completa.
     * @return true si y solo si se puede copiar el archivo con exito.
     *
     * @throws IOException
     *             En caso de error de Entrada/Salida
     */
    public static boolean copiaArchivo(InputStream archivoCargaStream, File archivoDestino) throws IOException {
        return copiaArchivo(archivoCargaStream, archivoDestino.getAbsolutePath());
    }

    /**
     * Copia el contenido de un flujo de entrada a un archivo nuevo.
     *
     * @param archivoCargaStream
     *            Flujo de entrada abierto
     * @param nombreArchivoDestino
     *            Nombre del archivo donde se copiara el destino. Debe incluir
     *            la ruta completa.
     * @return true si y solo si se puede copiar el archivo con exito.
     *
     * @throws IOException
     *             En caso de error de Entrada/Salida
     */
    public static boolean copiaArchivo(InputStream archivoCargaStream, String nombreArchivoDestino) throws IOException {
        File outpuFile = new File(nombreArchivoDestino);
        OutputStream outStream = new FileOutputStream(outpuFile);
        byte[] buffer = new byte[1024 * 1024 * 2];
        int length;
        long fSize = 0l;
        log.debug("Copiando archivo  desde stream: " + archivoCargaStream + " a " + nombreArchivoDestino);
        while ((length = archivoCargaStream.read(buffer)) > 0) {
            log.trace("Escribiendo buffer " + length);
            outStream.write(buffer, 0, length);
            fSize = fSize + length;
        }
        log.debug("Se copiaron: " + fSize + " Kbytes");
        outStream.flush();
        outStream.close();
        return true;
    }

    /**
     * Copia el contenido de un archivo a un archivo nuevo.
     *
     * @param nombreArchivoOrigen
     *            archivo de entrada a copiar
     * @param archivoDestino
     *            Nombre del archivo donde se copiara el destino. Debe incluir
     *            la ruta completa.
     * @return true si y solo si se puede copiar el archivo con exito.
     * @throws IOException
     *             En caso de error de Entrada/Salida
     */
    public static boolean copiaArchivo(String nombreArchivoOrigen, File archivoDestino) throws IOException {
        return copiaArchivo(nombreArchivoOrigen, archivoDestino.getAbsolutePath());
    }

    /**
     * Copia el contenido de un archivo a un archivo nuevo.
     *
     * @param nombreArchivoOrigen
     *            archivo de entrada a copiar
     * @param nombreArchivoDestino
     *            Nombre del archivo donde se copiara el destino. Debe incluir
     *            la ruta completa.
     * @return true si y solo si se puede copiar el archivo con exito.
     * @throws IOException
     *             En caso de error de Entrada/Salida
     */
    public static boolean copiaArchivo(String nombreArchivoOrigen, String nombreArchivoDestino) throws IOException {
        InputStream inputStream = null;
        try {
            File inputFile = new File(nombreArchivoOrigen);
            inputStream = new FileInputStream(inputFile);
            return copiaArchivo(inputStream, nombreArchivoDestino);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.warn("Problemas cerrando flujo de entrada: " + e);
                }
        }
    }

    /**
     * Regresa la extension, de existir, de un archivo.
     *
     * @param fileName
     *            Nombre del archivo.
     * @return Extension del archivo.
     */
    public static String getFileExtencion(String fileName) {
        String ext = "";
        int indexPunto = fileName.lastIndexOf(".");
        if (indexPunto > 0)
            ext = fileName.substring(indexPunto + 1);
        return ext;
    }

    /**
     * Regresa el nombre del archivo sin extencion.
     *
     * @param fileName
     *            Nombre del archivo.
     * @return Extension del archivo.
     */
    public static String getFileWithoutExtencion(String fileName) {
        String sinExt = "";
        int indexPunto = fileName.lastIndexOf(".");
        if (indexPunto > 0)
            sinExt = fileName.substring(0, indexPunto);
        return sinExt;
    }

    /**
     * Genera script de insercion desde un mapa. Considera que la llave del mapa
     * son los nombres de los campos en la tabla y el valor sera el valor a
     * insertar.
     *
     * @param tableName
     *            Nombre de la tabla en la que se insertaran los valores del
     *            mapa.
     * @param info
     *            Informacion a insertar.
     * @return Script de insercion ANSI SQL.
     */
    public static String genInsertFromMap(String tableName, Map<String, String> info) {
        String queryEncabezado = "INSERT INTO " + tableName + "(";
        String queryValores = "VALUES(";
        String tokenEncabezado = "";
        String tokenValores = "";
        for (Iterator<String> i = info.keySet().iterator(); i.hasNext(); ) {
            String encabezado = i.next();
            queryEncabezado = queryEncabezado + tokenEncabezado + encabezado;
            queryValores = queryValores + tokenValores + "'" + info.get(encabezado) + "'";
            tokenEncabezado = ", ";
            tokenValores = ", ";
        }
        return queryEncabezado + ")" + queryValores + ")";
    }

    /**
     * Genera script de insercion/actualizacion desde un mapa. Considera que la
     * llave del mapa son los nombres de los campos en la tabla y el valor sera
     * el valor a insertar.
     *
     * @param tableName
     *            Nombre de la tabla en la que se insertaran los valores del
     *            mapa.
     * @param info
     *            Informacion a insertar.
     * @return Script de insercion ANSI SQL.
     * @throws Exception
     */
    public static String genInsertUpdateFromMap(String tableName, Map<String, String> info, String[] llavePrimaria) throws Exception {
        if (llavePrimaria == null || llavePrimaria.length == 0)
            throw new Exception("No se recibio definicion de llave primaria para la tabla: " + tableName);
        Map<String, String> pk = new LinkedHashMap<String, String>();
        String queryPrincipal = "IF EXISTS (  SELECT 1 FROM " + tableName;
        String queryInsertEncabezado = "INSERT INTO " + tableName + "(";
        String queryInsertValores = "VALUES(";
        String queryUpdateEncabezado = "UPDATE " + tableName + " SET ";
        String queryUpdateValores = " ";
        String queryUpdateWhere = " WHERE ";
        String tokenInsertEncabezado = "";
        String tokenInsertValores = "";
        String tokenUpdateEncabezado = "";
        String tokenUpdateWhere = "";
        // Genera mapa de llave primaria
        for (int i = 0; i < llavePrimaria.length; i++) {
            pk.put(llavePrimaria[i], llavePrimaria[i]);
        }
        // Genera campos de Insert/Update
        for (Iterator<String> i = info.keySet().iterator(); i.hasNext(); ) {
            String encabezado = i.next();
            queryInsertEncabezado = queryInsertEncabezado + tokenInsertEncabezado + encabezado;
            queryInsertValores = queryInsertValores + tokenInsertValores + "'" + info.get(encabezado) + "'";
            queryUpdateValores = queryUpdateValores + tokenUpdateEncabezado + encabezado + " = '" + info.get(encabezado) + "'";
            if (pk.get(encabezado) != null) {
                queryUpdateWhere = queryUpdateWhere + tokenUpdateWhere + encabezado + " = '" + info.get(encabezado) + "'";
                tokenUpdateWhere = " AND ";
            }
            tokenInsertEncabezado = ", ";
            tokenInsertValores = ", ";
            tokenUpdateEncabezado = ", ";
        }
        return queryPrincipal + queryUpdateWhere + ")" + queryUpdateEncabezado + queryUpdateValores + queryUpdateWhere + " ELSE " + queryInsertEncabezado + ")" + queryInsertValores + ")";
    }

    /**
     * Genera un archivo Excel con el contenido de un ResultSet. El encabezado
     * se forma por cada uno de los encabezados de la columna.
     *
     * @param rs
     *            ResultSet abierto
     * @param fileName
     *            Archivo destino. Si existe se sobreescribira.
     * @return Referencia al archivo creado.
     * @throws Exception
     */
    public static File ExcelFromRS(ResultSet rs, String fileName) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet hs = wb.createSheet();
        // Relacion nombre encabezado
        Map<String, Integer> encabezados = new LinkedHashMap<String, Integer>();
        /* Genera encabezado del archivo Excel */
        HSSFRow fila = hs.createRow(0);
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            fila.createCell(i).setCellValue(rsmd.getColumnName(i + 1));
            encabezados.put(rsmd.getColumnName(i + 1), rsmd.getColumnType(i + 1));
        }
        /* Ingresa las columnas como resultado */
        int row = 1;
        int cnt = 0;
        while (rs.next()) {
            fila = hs.createRow(row);
            for (Iterator<String> i = encabezados.keySet().iterator(); i.hasNext(); ) {
                String key = i.next();
                Util.createExcelCell(cnt++, wb, fila, rs, key, encabezados.get(key));
            }
            row++;
            cnt = 0;
        }
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            hs.autoSizeColumn(i);
        }
        /* Escribe el archivo final */
        File f = new File(fileName);
        if (!f.exists())
            f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        wb.write(bos);
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        styleInt = null;
        styleFloat = null;
        styleDate = null;
        return f;
    }

    /**
     * Realiza la descarga de un archivo al cliente.
     *
     * @param resp
     *            Response Abierto
     * @param filename
     *            Nombre del archivo a descargar
     * @param original_filename
     *            Nombre del archivo como se descargara
     * @param mimetype
     *            Tipo MIME del archivo a descargar
     * @throws Exception
     */
    public static void doDownload(HttpServletResponse resp, String filename, String original_filename, String mimetype) throws Exception {
        log.debug("doDownload - Start");
        log.info("doDownload - Requested file: " + filename);
        log.info("doDownload - Original filename: " + original_filename);
        log.info("doDownload - MIME type: " + mimetype);
        File f = new File(filename);
        if (!f.exists()) {
            log.error("doDownload - File not found: " + filename);
            throw new Exception("El archivo " + filename + " no existe");
        }
        log.info("doDownload - File found: " + filename + ", Size: " + f.length() + " bytes");
        ServletOutputStream out = resp.getOutputStream();
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        resp.addHeader("Content-Disposition", "attachment; filename=\"" + (StringUtils.isBlank(original_filename) ? f.getName() : original_filename) + "\";");
        // 5K buffer
        byte[] bbuf = new byte[5 * 1024];
        try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
            int length;
            log.debug("doDownload - Starting file read");
            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                out.write(bbuf, 0, length);
            }
            log.info("doDownload - File transfer completed successfully");
        } catch (IOException e) {
            log.error("doDownload - IO error during file read: " + e.getMessage(), e);
            throw new Exception("Error reading the file: " + e.getMessage());
        } finally {
            out.flush();
            out.close();
            log.debug("doDownload - End");
        }
    }

    /**
     * Realiza la descarga de un archivo al cliente sin cerrar la conexion al
     * terminar
     *
     * @param out
     *            Flujo de datos abierto
     * @param filename
     *            Nombre del archivo a descargar
     * @param original_filename
     *            Nombre del archivo como se descargara
     * @param mimetype
     *            Tipo MIME del archivo a descargar
     * @throws Exception
     */
    public static void doDownload(OutputStream out, String filename, String original_filename, String mimetype) throws Exception {
        int length = 0;
        File f = new File(filename);
        // 5K buffer
        byte[] bbuf = new byte[5 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
    }

    /**
     * Crea una celda de un archivo excel basado en el tipo de datos registrado
     * en el resultset. Si es entero o punto flotante establece el tipo de la
     * celda como numerico con dos decimales. Si es fecha, establece la celda
     * como fecha en formato dd/mm/yyyy. En caso contrario sera establecido como
     * general.
     *
     * @param index
     *            Posicion de la celda.
     * @param fila
     *            Objeto al que se agrega la celda.
     * @param tipoDato
     *            Tipo de dato.
     * @return Celda creada con formato.
     */
    public static Cell createExcelCell(int index, Row fila, ResultSet rs, String cellName, int tipoDato) throws Exception {
        Cell cell = (fila.getCell(index) == null ? fila.createCell(index) : fila.getCell(index));
        if (tipoDato == Types.BIGINT || tipoDato == Types.BIT || tipoDato == Types.INTEGER || tipoDato == Types.SMALLINT || tipoDato == Types.TINYINT) {
            // Tipos de dato enteros
            int val = rs.getInt(cellName);
            cell.setCellValue(val);
            return cell;
        } else if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
            // Tipos de dato reales
            double val = rs.getDouble(cellName);
            cell.setCellValue(val);
            return cell;
        } else if (tipoDato == Types.DATE || tipoDato == Types.TIME || tipoDato == Types.TIMESTAMP) {
            // Tipo de dato fecha
            Date d = new Date(rs.getDate(cellName).getTime());
            cell.setCellValue(d);
            return cell;
        } else {
            String val = rs.getString(cellName);
            cell.setCellValue(val);
            return cell;
        }
    }

    public static Cell createExcelCellReportePresupuestal(int index, Row fila, String valor) throws Exception {
        Cell cell = fila.createCell(index);
        cell.setCellValue(valor);
        return cell;
    }

    public static Cell createExcelCellReportePresupuestal(int index, Row fila, BigDecimal valor, CellStyle estiloTabla) throws Exception {
        Cell cell = fila.createCell(index);
        cell.setCellValue(valor.doubleValue());
        cell.setCellStyle(estiloTabla);
        return cell;
    }

    /**
     * Crea una celda de un archivo excel basado en el tipo de datos registrado
     * en el resultset. Si es entero o punto flotante establece el tipo de la
     * celda como numerico con dos decimales. Si es fecha, establece la celda
     * como fecha en formato dd/mm/yyyy. En caso contrario sera establecido como
     * general.
     *
     * @param index
     *            Posicion de la celda.
     * @param fila
     *            Objeto al que se agrega la celda.
     * @param tipoDato
     *            Tipo de dato.
     * @return Celda creada con formato.
     */
    public static HSSFCell createExcelCell(int index, HSSFWorkbook wb, HSSFRow fila, ResultSet rs, String cellName, int tipoDato) throws Exception {
        HSSFCell cell = fila.createCell(index);
        if (styleInt == null) {
            styleInt = wb.createCellStyle();
            styleInt.setDataFormat(HSSFDataFormat.getBuiltinFormat(Util.NUMBER_INT_FORMAT_CELL));
        }
        if (styleFloat == null) {
            styleFloat = wb.createCellStyle();
            styleFloat.setDataFormat(HSSFDataFormat.getBuiltinFormat(Util.NUMBER_DOUBLE_FORMAT_CELL));
        }
        if (styleDate == null) {
            styleDate = wb.createCellStyle();
            styleDate.setDataFormat(wb.createDataFormat().getFormat("dd/mm/yyyy"));
        }
        if (tipoDato == Types.BIGINT || tipoDato == Types.BIT || tipoDato == Types.INTEGER || tipoDato == Types.SMALLINT || tipoDato == Types.TINYINT) {
            // Tipos de dato enteros
            int val = rs.getInt(cellName);
            cell.setCellStyle(styleInt);
            cell.setCellValue(val);
            return cell;
        } else if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
            // Tipos de dato reales
            double val = rs.getDouble(cellName);
            cell.setCellStyle(styleFloat);
            cell.setCellValue(val);
            return cell;
        } else if (tipoDato == Types.DATE || tipoDato == Types.TIME || tipoDato == Types.TIMESTAMP) {
            // Tipo de dato fecha
            Date d = new Date(rs.getDate(cellName).getTime());
            cell.setCellStyle(styleDate);
            cell.setCellValue(d);
            return cell;
        } else {
            String val = rs.getString(cellName);
            cell.setCellValue(val);
            return cell;
        }
    }

    /**
     * Valida si el valor real puede ser convertido a entero. Si no cuenta con
     * cifras decimales puede pasar a entero sin perdida de exactitud
     *
     * @param valor
     *            Valor doble a evaluar
     * @return true si el valor puede convertirse a entero.
     */
    public static boolean esEntero(double valor) {
        double valorRouded = Double.parseDouble((new java.text.DecimalFormat("#")).format(valor));
        double valorAbs = (valor < 0.0d ? -1.0d * valor : valor);
        double resta = valorAbs - valorRouded;
        return resta == 0d;
    }

    /**
     * Escribe un archivo separado por comas con la informacion del resultset
     *
     * @param destino
     *            Archivo destino
     * @param rs
     *            ResultSet abierto
     * @throws Exception
     */
    public static void CSVFromResultSet(File destino, ResultSet rs) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] encabezados = new String[rsmd.getColumnCount()];
        FileWriter fw = new FileWriter(destino);
        String token = "";
        String linea = "";
        /* Genera encabezado del archivo Excel */
        for (int i = 0; i < encabezados.length; i++) {
            encabezados[i] = rsmd.getColumnName(i + 1);
            linea = linea + token + rsmd.getColumnName(i + 1);
            token = ",";
        }
        fw.write(linea + "\n");
        linea = "";
        token = "";
        /* Ingresa las columnas como resultado */
        while (rs.next()) {
            for (int i = 0; i < encabezados.length; i++) {
                linea = linea + token + rs.getString(encabezados[i]);
                token = ",";
            }
            fw.write(linea + "\n");
            token = "";
            linea = "";
        }
        fw.flush();
        fw.close();
    }

    /**
     * Regresa el numero de mes que corresponde en español/mexico a un mes. Se
     * sigue la norma Java donde el mes 0 corresponde a Enero hasta el mes 11
     * Diciembre.
     *
     * @param nombreMes
     *            Nombre del mes.
     * @return Numero del mes. -1 si no encuentra coincidencia con el nombre.
     */
    public static int numeroDeMes(String nombreMes) {
        int numeroMes = -1;
        for (int i = 0; i < NOMBRE_MESES_MX.length && numeroMes < 0; i++) if (NOMBRE_MESES_MX[i].equalsIgnoreCase(nombreMes))
            numeroMes = i;
        return numeroMes;
    }

    public static String nombreDeMes(int mes) {
        int numeroMes = mes - 1;
        return StringUtils.capitalize(NOMBRE_MESES_MX[numeroMes].toLowerCase());
    }

    /**
     * Junta un arreglo como cadena de caracteres separando cada elmento por el
     * caracter especificado. Si el arreglo contiene solo un elemento devuelve
     * el unico elemento como cadena de caracteres.
     *
     * @param array
     *            Arreglo a unir
     * @param joinChar
     *            Caracter separador
     * @return Cadena pegada mediante el separador con todos los elementos del
     *         arreglo.
     */
    public static String join(int[] array, char joinChar) {
        String cadenaResultado = "";
        String token = "";
        for (int i = 0; i < array.length; i++) {
            cadenaResultado += token + String.valueOf(array[i]);
            token = String.valueOf(joinChar);
        }
        return cadenaResultado;
    }

    /**
     * Junta un arreglo como cadena de caracteres separando cada elmento por el
     * caracter especificado. Si el arreglo contiene solo un elemento devuelve
     * el unico elemento como cadena de caracteres.
     *
     * @param arreglo
     *            Arreglo a unir
     * @param joinChar
     *            Caracter separador
     * @return Cadena pegada mediante el separador con todos los elementos del
     *         arreglo.
     */
    public static String join(String[] arreglo, char joinChar) {
        String cadenaResultado = "";
        String token = "";
        if (arreglo == null)
            return "";
        for (int i = 0; i < arreglo.length; i++) {
            cadenaResultado += token + arreglo[i];
            token = String.valueOf(joinChar);
        }
        return cadenaResultado;
    }

    /**
     * Junta un arreglo como cadena de caracteres separando cada elmento por el
     * caracter especificado. Si el arreglo contiene solo un elemento devuelve
     * el unico elemento como cadena de caracteres.
     *
     * @param arreglo
     *            Arreglo a unir
     * @param joinChar
     *            Caracter separador
     * @return Cadena pegada mediante el separador con todos los elementos del
     *         arreglo.
     */
    public static String joinSQL(String[] arreglo) {
        String cadenaResultado = "";
        String token = "";
        if (arreglo == null)
            return "";
        for (int i = 0; i < arreglo.length; i++) {
            cadenaResultado += token + "'" + arreglo[i] + "'";
            token = ",";
        }
        return cadenaResultado;
    }

    /**
     * @param arreglo
     * @return
     */
    public static int[] iniciaArregloNumerico(int[] arreglo) {
        for (int i = 0; i < arreglo.length; i++) {
            arreglo[i] = 0;
        }
        return arreglo;
    }

    /**
     * @param arreglo
     * @return
     */
    public static double[] iniciaArregloNumerico(double[] arreglo) {
        for (int i = 0; i < arreglo.length; i++) {
            arreglo[i] = 0.0d;
        }
        return arreglo;
    }

    /**
     * Suma dos arreglos regresando en un arreglo el resultado de la suma.
     *
     * @param sumando1
     *            Arreglo 1
     * @param sumando2
     *            Arreglo 2
     * @return Arreglo con el resultado de sumar cada entrada del arreglo 1 con
     *         el arreglo 2.
     */
    public static double[] sumaArreglos(double[] sumando1, double[] sumando2) {
        double[] resultado = new double[sumando1.length];
        for (int i = 0; i < sumando2.length; i++) {
            resultado[i] = sumando1[i] + sumando2[i];
        }
        return resultado;
    }

    /**
     * Genera el cuerpo de un Excel linear basado en el resultado de un
     * ResultSet. El orden en el que se genero la consulta debe coincidir con el
     * orden en el excel.
     *
     * @param rs
     *            ResultSet abierto
     * @param hoja
     *            Hoja a llenar activa
     * @param renglonInicio
     *            Renglon en el que inicia la insercion de informacion
     * @return Hoja de excel con la informacion del ResultSet
     * @throws Exception
     */
    public static Sheet resultSetToExcel(ResultSet rs, Sheet hoja, int renglonInicio) throws Exception {
        return resultSetToExcel(rs, hoja, renglonInicio, false);
    }

    /**
     * Genera el cuerpo de un Excel linear basado en el resultado de un
     * ResultSet. El orden en el que se genero la consulta debe coincidir con el
     * orden en el excel.
     *
     * @param rs
     *            ResultSet abierto
     * @param hoja
     *            Hoja a llenar activa
     * @param renglonInicio
     *            Renglon en el que inicia la insercion de informacion
     * @param incluirEncabezado
     *            true lee el encabezado del ResultSet y lo escribe como primer
     *            linea en el excel en el renglon [renglonInicio]
     * @return Hoja de excel con la informacion del ResultSet
     * @throws Exception
     */
    public static Sheet resultSetToExcel(ResultSet rs, Sheet hoja, int renglonInicio, boolean incluirEncabezado) throws Exception {
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        if (incluirEncabezado) {
            Row rw = (hoja.getRow(renglonInicio + cnt) == null ? hoja.createRow(renglonInicio + cnt) : hoja.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                rw.createCell(i).setCellValue(rsMetadata.getColumnName(i + 1));
            }
            cnt++;
        }
        while (rs.next()) {
            Row rw = (hoja.getRow(renglonInicio + cnt) == null ? hoja.createRow(renglonInicio + cnt) : hoja.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                createExcelCell(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
            }
            cnt++;
        }
        return hoja;
    }

    /**
     * Genera el cuerpo de un Excel linear basado en el resultado de un
     * ResultSet. El orden en el que se genero la consulta debe coincidir con el
     * orden en el excel. Los primeros "N" registros se ignoran si
     * <code>inicioResultSet</code> es mayor a 0.
     *
     * @param rs
     *            ResultSet abierto
     * @param hoja
     *            Hoja a llenar activa
     * @param renglonInicio
     *            Renglon en el que inicia la insercion de informacion
     * @param columnaInicio
     *            Columna en la que se inicia la insercion de informacion.
     * @param inicioResultSet
     *            true lee el encabezado del ResultSet y lo escribe como primer
     *            linea en el excel en el renglon [renglonInicio]
     * @param incluirEncabezado
     *            true inserta el encabezado del resultset en el renglon
     *            <code>renglonInicio</code>
     * @return Hoja de excel con la informacion del ResultSet
     * @throws Exception
     */
    public static Sheet resultSetToExcel(ResultSet rs, Sheet hoja, int renglonInicio, int columnaInicio, int inicioResultSet, boolean incluirEncabezado) throws Exception {
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        if (incluirEncabezado) {
            Row rw = (hoja.getRow(renglonInicio + cnt) == null ? hoja.createRow(renglonInicio + cnt) : hoja.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                rw.createCell(i + columnaInicio).setCellValue(rsMetadata.getColumnName(i + 1));
            }
            cnt++;
        }
        while (rs.next()) {
            Row rw = (hoja.getRow(renglonInicio + cnt) == null ? hoja.createRow(renglonInicio + cnt) : hoja.getRow(renglonInicio + cnt));
            for (int i = inicioResultSet; i < rsMetadata.getColumnCount(); i++) {
                createExcelCell(i + columnaInicio, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
            }
            cnt++;
        }
        return hoja;
    }

    public Sheet creaExcelReportePrespuestal(List lista, Sheet hoja, int renglonInicio) throws Exception {
        int renglon = 0;
        int linea = 0;
        boolean inicio = true;
        String tipoDoc = "";
        String tipoDocActual = "";
        String foliDoc = "";
        String folioDocActual = "";
        int totalTipoDoc = 0;
        int totalFolioDoc = 0;
        tipoDoc = "TipoDoc";
        foliDoc = "FolioDoc";
        for (Iterator<?> iter = lista.iterator(); iter.hasNext(); renglon++) {
            ReportePrespuestal reportePrespuestal = (ReportePrespuestal) iter.next();
            Row rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
            if (inicio) {
                tipoDoc = reportePrespuestal.getcTipoDoc();
                inicio = false;
            }
            tipoDocActual = reportePrespuestal.getcTipoDoc();
            if (!tipoDoc.equals(tipoDocActual)) {
                linea++;
                rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
                createExcelCellReportePresupuestal(0, rw, "Total Movimientos: " + totalTipoDoc);
                linea++;
                rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
                createExcelCellReportePresupuestal(0, rw, "Total Documentos: " + totalFolioDoc);
                linea++;
                totalTipoDoc = 0;
                totalFolioDoc = 0;
                tipoDoc = reportePrespuestal.getcTipoDoc();
            }
            folioDocActual = reportePrespuestal.getCfolio();
            if (!foliDoc.equals(folioDocActual)) {
                totalFolioDoc++;
                foliDoc = reportePrespuestal.getCfolio();
            }
            totalTipoDoc++;
            createExcelCellReportePresupuestal(0, rw, reportePrespuestal.getcCuenta());
            createExcelCellReportePresupuestal(1, rw, reportePrespuestal.getfFechaMov());
            createExcelCellReportePresupuestal(2, rw, reportePrespuestal.getcTipoDoc());
            createExcelCellReportePresupuestal(3, rw, reportePrespuestal.getCfolio());
            createExcelCellReportePresupuestal(4, rw, reportePrespuestal.getcEP());
            // createExcelCellReportePresupuestal(5, rw,
            // reportePrespuestal.getcCancelado());
            createExcelCellReportePresupuestal(6, rw, String.valueOf(reportePrespuestal.getcAnual()));
            createExcelCellReportePresupuestal(7, rw, String.valueOf(reportePrespuestal.getcEnero()));
            createExcelCellReportePresupuestal(8, rw, String.valueOf(reportePrespuestal.getcFebrero()));
            createExcelCellReportePresupuestal(9, rw, String.valueOf(reportePrespuestal.getcMarzo()));
            createExcelCellReportePresupuestal(10, rw, String.valueOf(reportePrespuestal.getcAbril()));
            createExcelCellReportePresupuestal(11, rw, String.valueOf(reportePrespuestal.getcMayo()));
            createExcelCellReportePresupuestal(12, rw, String.valueOf(reportePrespuestal.getcJunio()));
            createExcelCellReportePresupuestal(13, rw, String.valueOf(reportePrespuestal.getcJulio()));
            createExcelCellReportePresupuestal(14, rw, String.valueOf(reportePrespuestal.getcAgosto()));
            createExcelCellReportePresupuestal(15, rw, String.valueOf(reportePrespuestal.getcSeptiembre()));
            createExcelCellReportePresupuestal(16, rw, String.valueOf(reportePrespuestal.getcOctubre()));
            createExcelCellReportePresupuestal(17, rw, String.valueOf(reportePrespuestal.getcNoviembre()));
            createExcelCellReportePresupuestal(18, rw, String.valueOf(reportePrespuestal.getcDiciembre()));
            linea++;
        }
        Row rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
        createExcelCellReportePresupuestal(0, rw, "Total Movimientos: " + totalTipoDoc);
        linea++;
        rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
        createExcelCellReportePresupuestal(0, rw, "Total Documentos: " + totalFolioDoc);
        return hoja;
    }

    public Sheet creaExcelReporteAdecuacionesPorUN(List lista, Sheet hoja, int renglonInicio) throws Exception {
        int renglon = 0;
        int linea = 0;
        String unidadResponsable = "";
        String valor = "";
        Row rw = null;
        for (Iterator<?> iter = lista.iterator(); iter.hasNext(); renglon++) {
            ReporteAdecuacionesPorUN reporteAdecuaciones = (ReporteAdecuacionesPorUN) iter.next();
            if (!reporteAdecuaciones.getcUnidad().equals(unidadResponsable)) {
                linea++;
                rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
                valor = "Unidad: " + reporteAdecuaciones.getcUnidad();
                createExcelCellReportePresupuestal(0, rw, "Unidad: " + reporteAdecuaciones.getcUnidad());
                linea++;
                rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
                createExcelCellReportePresupuestal(0, rw, "Folio");
                createExcelCellReportePresupuestal(1, rw, "Estructura Programática");
                createExcelCellReportePresupuestal(2, rw, "Estatus");
                createExcelCellReportePresupuestal(3, rw, "Fecha");
                createExcelCellReportePresupuestal(4, rw, "Monto");
                createExcelCellReportePresupuestal(5, rw, "Folio SICOP");
                createExcelCellReportePresupuestal(6, rw, "Folio MAP");
                createExcelCellReportePresupuestal(7, rw, "Usuario");
                unidadResponsable = reporteAdecuaciones.getcUnidad();
            }
            linea++;
            rw = (hoja.getRow(renglonInicio + linea) == null ? hoja.createRow(renglonInicio + linea) : hoja.getRow(renglonInicio + linea));
            createExcelCellReportePresupuestal(0, rw, reporteAdecuaciones.getCfolio());
            createExcelCellReportePresupuestal(1, rw, reporteAdecuaciones.getEp());
            createExcelCellReportePresupuestal(2, rw, reporteAdecuaciones.getcEstatus());
            createExcelCellReportePresupuestal(3, rw, reporteAdecuaciones.getfFecha());
            createExcelCellReportePresupuestal(4, rw, reporteAdecuaciones.getcMonto());
            createExcelCellReportePresupuestal(5, rw, reporteAdecuaciones.getcFolioSICOP());
            createExcelCellReportePresupuestal(6, rw, reporteAdecuaciones.getcFolioMAP());
            createExcelCellReportePresupuestal(7, rw, reporteAdecuaciones.getcUsuario());
        }
        return hoja;
    }

    public static boolean renglonVacio(Row renglon) {
        boolean retVal = true;
        if (renglon == null)
            return retVal;
        else
            for (Iterator<Cell> i = renglon.cellIterator(); i.hasNext(); ) {
                Cell celda = i.next();
                if (celda != null && celda.getCellType() != CellType.BLANK)
                    retVal = false;
            }
        return retVal;
    }

    /**
     * Envia como pagina HTML el mensaje de error reportado en una Excepcion.
     *
     * @param out
     *            Salida
     * @param e
     *            Excepcion a reportar
     * @throws IOException
     */
    public static void sendHTMLErrorMsg(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("<title>Insert title here</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("	<table align=\"center\">");
        out.println("		<tr>");
        out.println("			<td align=\"center\">");
        out.println("				<table>");
        out.println("					<tr>");
        out.println("						<td colspan=\"2\" align=\"center\">");
        out.println("							<h1>¡Ocurrio un error!</h1></td>");
        out.println("					</tr>");
        out.println("");
        out.println("					<tr>");
        out.println("						<td>Error:</td>");
        out.println("						<td><textarea rows=\"10\" cols=\"40\">" + new String(e.toString().getBytes("UTF-8"), "ISO-8859-1") + "</textarea></td>");
        out.println("					</tr>");
        out.println("");
        out.println("					<tr>");
        out.println("						<td colspan=\"2\" align=\"left\">Por favor reportar al");
        out.println("							administrador del sistema copiando el codigo de error en este");
        out.println("							mensaje.</td>");
        out.println("					</tr>");
        out.println("				</table></td>");
        out.println("		</tr>");
        out.println("");
        out.println("	</table>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    public static String formatNumber(Double val) {
        if (val == null)
            return "";
        String output = CURRENCY_FORMATTER.format(val);
        return output;
    }

    public static String formatNumber(BigDecimal val) {
        if (val == null)
            return "";
        String output = CURRENCY_FORMATTER.format(val);
        return output;
    }

    public static String ArrayToMultilineString(List<String> errores) {
        String error = "";
        String token = "";
        for (int i = 0; i < errores.size(); i++) {
            error += token + errores.get(i);
            token = "\\\\n";
        }
        return error;
    }

    public static String getToday() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    public static String getTodayMC() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    public static String getToday(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    public static Cell createExcelCellRep(int index, Row fila, ResultSet rs, String cellName, int tipoDato, CellStyle estiloTabla) throws Exception {
        Cell cell = fila.createCell(index);
        if (tipoDato == Types.BIGINT || tipoDato == Types.BIT || tipoDato == Types.INTEGER || tipoDato == Types.SMALLINT || tipoDato == Types.TINYINT) {
            // Tipos de dato enteros
            int val = rs.getInt(cellName);
            cell.setCellValue(val);
            cell.setCellStyle(estiloTabla);
            return cell;
        } else if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
            // Tipos de dato reales
            double val = rs.getDouble(cellName);
            cell.setCellValue(val);
            cell.setCellStyle(estiloTabla);
            return cell;
        } else if (tipoDato == Types.DATE || tipoDato == Types.TIME || tipoDato == Types.TIMESTAMP) {
            if (rs.getDate(cellName) != null) {
                // Tipo de dato fecha
                Date d = new Date(rs.getDate(cellName).getTime());
                cell.setCellValue(d);
                cell.setCellStyle(estiloTabla);
            }
            return cell;
        } else {
            String val = rs.getString(cellName);
            cell.setCellValue(val);
            cell.setCellStyle(estiloTabla);
            return cell;
        }
    }

    public static Cell createExcelCellRep(int index, Row fila, String dato, CellStyle estiloTabla) throws Exception {
        Cell cell = fila.createCell(index);
        cell.setCellValue(dato);
        cell.setCellStyle(estiloTabla);
        return cell;
    }

    public static Cell deleteExcelCell(int index, Row fila, ResultSet rs) {
        Cell cell = fila.createCell(index);
        cell.setCellValue("");
        return cell;
    }

    public static Sheet resultSetToExcel(ResultSet rs, Sheet hoja, int renglonInicio, int columnaInicio, boolean incluirEncabezado) throws Exception {
        return resultSetToExcel(rs, hoja, renglonInicio, columnaInicio, 0, incluirEncabezado);
    }

    public static Sheet resultSetToExcelE(ResultSet rs, Sheet hoja, int renglonInicio, int columnaInicio, boolean incluirEncabezado, CellStyle estiloTabla) throws Exception {
        return resultSetToExcelE(rs, hoja, renglonInicio, columnaInicio, 0, incluirEncabezado, estiloTabla);
    }

    public static void copyRow(Workbook workbook, Sheet worksheet, int sourceRowNum, int destinationRowNum) {
        // Obtiene la origen y el destino
        Row newRow = worksheet.getRow(destinationRowNum);
        Row sourceRow = worksheet.getRow(sourceRowNum);
        // Si la celda de destino existe inserta la fila
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
        } else {
            newRow = worksheet.createRow(destinationRowNum);
        }
        // El ciclo para añadir las celdas al nuevo renglon
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Obtiene el viejo y el nuevo registro
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);
            // Si es nulo se va a la siguiente celda
            if (oldCell == null) {
                newCell = null;
                continue;
            }
            // Copia el estilo de la anterior a la nueva
            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);
            switch(oldCell.getCellType()) {
                case _NONE:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case BLANK:
                    break;
                case BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }
    }

    public static boolean uploadFileServerBD(String nameFile, String pathFile) throws Exception {
        String dominio;
        String usuarioRemoto;
        String passwordRemoto;
        String rutaRemoto;
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic("jdbc/gestion");
        dominio = cabl.obtenDominioRemoto();
        usuarioRemoto = cabl.obtenUsuarioRemoto();
        passwordRemoto = cabl.obtenPasswordRemoto();
        rutaRemoto = cabl.obtenRutaRemoto();
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(dominio, usuarioRemoto, passwordRemoto);
        String sharepath = rutaRemoto + nameFile;
        File file = new File(pathFile);
        FileInputStream fis = new FileInputStream(file);
        SmbFile sFile = new SmbFile(sharepath, auth);
        SmbFileOutputStream out = new SmbFileOutputStream(sFile, true);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.flush();
        out.close();
        fis.close();
        return true;
    }

    public static CellStyle generaEstilo(Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum) throws Exception {
        Font font = wk.createFont();
        font.setFontHeightInPoints((short) tamLetra);
        font.setFontName(getTipoFuente());
        font.setBold(negritas);
        DataFormat mon = wk.createDataFormat();
        CellStyle estilo = wk.createCellStyle();
        if (derecho)
            estilo.setBorderRight(BorderStyle.THIN);
        if (izquierdo)
            estilo.setBorderLeft(BorderStyle.THIN);
        if (abajo)
            estilo.setBorderBottom(BorderStyle.THIN);
        if (formatoNum)
            estilo.setDataFormat(mon.getFormat("#,###,###0.00"));
        estilo.setFont(font);
        return estilo;
    }

    public static CellStyle generaEstilo1(Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum) throws Exception {
        Font font = wk.createFont();
        font.setFontHeightInPoints((short) tamLetra);
        font.setFontName(getTipoFuente());
        font.setBold(negritas);
        DataFormat mon = wk.createDataFormat();
        CellStyle estilo = wk.createCellStyle();
        if (derecho)
            estilo.setBorderRight(BorderStyle.THIN);
        if (izquierdo)
            estilo.setBorderLeft(BorderStyle.THIN);
        if (abajo)
            estilo.setBorderBottom(BorderStyle.THIN);
        if (formatoNum)
            estilo.setDataFormat(mon.getFormat("#,###,###.##"));
        estilo.setFont(font);
        return estilo;
    }

    public static CellStyle generaEstilo2(Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean arriba, boolean formatoNum) throws Exception {
        Font font = wk.createFont();
        font.setFontHeightInPoints((short) tamLetra);
        font.setFontName(getTipoFuente());
        font.setBold(negritas);
        DataFormat mon = wk.createDataFormat();
        CellStyle estilo = wk.createCellStyle();
        if (derecho)
            estilo.setBorderRight(BorderStyle.HAIR);
        if (izquierdo)
            estilo.setBorderLeft(BorderStyle.HAIR);
        if (abajo)
            estilo.setBorderBottom(BorderStyle.HAIR);
        if (arriba)
            estilo.setBorderBottom(BorderStyle.HAIR);
        if (formatoNum)
            estilo.setDataFormat(mon.getFormat("#,###,###0.00"));
        estilo.setFont(font);
        return estilo;
    }

    public static CellStyle generaEstilo3(Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum) throws Exception {
        Font font = wk.createFont();
        font.setFontHeightInPoints((short) tamLetra);
        font.setFontName(getTipoFuente());
        font.setBold(negritas);
        DataFormat mon = wk.createDataFormat();
        CellStyle estilo = wk.createCellStyle();
        if (derecho)
            estilo.setBorderRight(BorderStyle.THIN);
        if (izquierdo)
            estilo.setBorderLeft(BorderStyle.THIN);
        if (abajo)
            estilo.setBorderBottom(BorderStyle.THIN);
        if (formatoNum)
            estilo.setDataFormat(mon.getFormat("#,###,###.#"));
        estilo.setFont(font);
        return estilo;
    }

    public static CellStyle generaEstiloBordesAnchos(Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum) throws Exception {
        Font font = wk.createFont();
        font.setFontHeightInPoints((short) tamLetra);
        font.setFontName(getTipoFuente());
        font.setBold(negritas);
        DataFormat mon = wk.createDataFormat();
        CellStyle estilo = wk.createCellStyle();
        if (derecho)
            estilo.setBorderRight(BorderStyle.THIN);
        if (izquierdo)
            estilo.setBorderLeft(BorderStyle.THIN);
        if (abajo)
            estilo.setBorderBottom(BorderStyle.THIN);
        if (formatoNum)
            estilo.setDataFormat(mon.getFormat("#,###,###0.00"));
        estilo.setFont(font);
        return estilo;
    }

    public static boolean uploadStreamServerBD(String nameFile, DataInputStream archivoCargaStream) throws Exception {
        String dominio;
        String usuarioRemoto;
        String passwordRemoto;
        String rutaRemoto;
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic("jdbc/gestion");
        dominio = cabl.obtenDominioRemoto();
        usuarioRemoto = cabl.obtenUsuarioRemoto();
        passwordRemoto = cabl.obtenPasswordRemoto();
        rutaRemoto = cabl.obtenRutaRemoto();
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(dominio, usuarioRemoto, passwordRemoto);
        String sharepath = rutaRemoto + Util.getFileName(nameFile);
        SmbFile sFile = new SmbFile(sharepath, auth);
        SmbFileOutputStream out = new SmbFileOutputStream(sFile, true);
        byte[] buffer = new byte[1024 * 256];
        int length;
        while ((length = archivoCargaStream.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.flush();
        out.close();
        return true;
    }

    public static String getFileName(String nameFile) {
        if (nameFile.indexOf("\\") > 0 || nameFile.indexOf("/") > 0) {
            String separador = nameFile.indexOf("\\") > 0 ? "\\\\" : "/";
            String[] componentes = nameFile.split(separador);
            String file = componentes[componentes.length - 1];
            return file;
        } else
            return nameFile;
    }

    public static boolean uploadStreamServerBD(String nameFile, DataInputStream archivoCargaStream, Connection conn) throws Exception {
        String dominio;
        String usuarioRemoto;
        String passwordRemoto;
        String rutaRemoto;
        dominio = ConfiguraAplicativoManager.obtenDominioRemoto(conn);
        usuarioRemoto = ConfiguraAplicativoManager.obtenUsuarioRemotoFurrt(conn);
        passwordRemoto = ConfiguraAplicativoManager.obtenPasswordRemoto(conn);
        rutaRemoto = ConfiguraAplicativoManager.obtenRutaRemoto(conn);
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(dominio, usuarioRemoto, passwordRemoto);
        String sharepath = rutaRemoto + nameFile;
        SmbFile sFile = new SmbFile(sharepath, auth);
        SmbFileOutputStream out = new SmbFileOutputStream(sFile, true);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = archivoCargaStream.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.flush();
        out.close();
        return true;
    }

    public static boolean deleteStreamServerBD(String remoteNameFile) throws Exception {
        String dominio;
        String usuarioRemoto;
        String passwordRemoto;
        String rutaRemoto;
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic("jdbc/gestion");
        dominio = cabl.obtenDominioRemoto();
        usuarioRemoto = cabl.obtenUsuarioRemoto();
        passwordRemoto = cabl.obtenPasswordRemoto();
        rutaRemoto = cabl.obtenRutaRemoto();
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(dominio, usuarioRemoto, passwordRemoto);
        String sharepath = rutaRemoto + remoteNameFile;
        SmbFile sFile = new SmbFile(sharepath, auth);
        if (sFile.exists())
            sFile.delete();
        return true;
    }

    public static boolean addToZip(ZipOutputStream zos, String nombreArchivo, String pathArchivo) throws Exception {
        byte[] buffer = new byte[1024 * 512];
        int agregados = 0;
        String file = pathArchivo;
        String extension = getFileExtencion(pathArchivo);
        String nombreEntrada = nombreArchivo + "." + "jpg";
        File f = new File(file);
        if (!f.exists()) {
            file = "/archivoImagen.jpg";
            nombreEntrada = nombreArchivo + ".jpg";
        }
        ZipEntry ze = new ZipEntry(nombreEntrada);
        zos.putNextEntry(ze);
        FileInputStream in = new FileInputStream(file);
        int len;
        while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        in.close();
        agregados++;
        return true;
    }

    public static File createTempFile(String prefix, String sufix) throws IOException {
        File tmpDir = Util.getTempDir();
        return File.createTempFile((prefix == null ? "temp_" : prefix), (sufix == null ? ".tmp" : sufix), tmpDir);
    }

    public static File generaZip(Map<String, File> archivos) throws Exception {
        OutputStream fos = null;
        ZipOutputStream zos = null;
        File zipeFile = null;
        try {
            File tmpDir = Util.getTempDir();
            zipeFile = File.createTempFile("ZipFile", ".zip", tmpDir);
            fos = new FileOutputStream(zipeFile);
            zos = new ZipOutputStream(fos);
            byte[] buffer = new byte[1024 * 512];
            for (Iterator<String> iterator = archivos.keySet().iterator(); iterator.hasNext(); ) {
                String nombreArchivo = iterator.next();
                File pathArchivo = archivos.get(nombreArchivo);
                FileInputStream in = new FileInputStream(pathArchivo);
                ZipEntry ze = new ZipEntry(pathArchivo.getName());
                zos.putNextEntry(ze);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            fos.flush();
            zos.flush();
            return zipeFile;
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception e) {
                    log.warn("Problemas cerrando flujo de entrada: " + e);
                }
            fos = null;
            zos = null;
        }
    }

    public static int addToZip(ZipOutputStream zos, Documento[] agregar) throws Exception {
        byte[] buffer = new byte[1024 * 512];
        int agregados = 0;
        for (Documento doc : agregar) {
            if (doc.getPaginasDocumento() != null && doc.getPaginasDocumento().length > 0) {
                String nombreEntrada = doc.getNombreDocumento();
                String file = doc.getPaginaDocumento(0).getFullPathFileName();
                File f = new File(file);
                if (!f.exists()) {
                    file = "/archivoImagen.jpg";
                    nombreEntrada = doc.getNombreDocumento() + ".jpg";
                } else {
                    if (nombreEntrada.indexOf('.') < 0)
                        nombreEntrada = doc.getNombreDocumento() + "." + doc.getExtension();
                }
                ZipEntry ze = new ZipEntry(nombreEntrada);
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
                agregados++;
            } else
                continue;
        }
        return agregados;
    }

    public static String arrayToCSV(ArrayList<String> encabezados, ArrayList<String> datos) throws Exception {
        String token = "";
        String linea = "";
        String destino = System.getProperty("java.io.tmpdir") + File.separatorChar + "exportCSV_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000) + ".csv";
        FileWriter fw = new FileWriter(destino);
        int cambioLinea = encabezados.size();
        /* Genera encabezado del archivo Excel */
        for (int i = 0; i < encabezados.size(); i++) {
            linea = linea + token + encabezados.get(i);
            token = ",";
        }
        fw.write(linea + "\n");
        token = "";
        linea = "";
        /* Ingresa las columnas como resultado */
        for (int i = 0; i < datos.size(); i++) {
            linea = linea + token + (datos.get(i) == null ? "" : datos.get(i).replaceAll("[,]", "").replaceAll("\r\n", " ").replaceAll("\n\r", " ").replaceAll("\n", " ").replaceAll("\r", " ").trim());
            token = ",";
            if ((i + 1) % cambioLinea == 0) {
                fw.write(linea + "\n");
                token = "";
                linea = "";
            }
        }
        fw.flush();
        fw.close();
        return destino;
    }

    public static Caso generaCaso(Connection conn, Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, Map<String, String> m) throws Exception {
        Caso c = CasoManager.nuevoCaso(conn, u, idTCaso, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        m.put("FOLIO", c.getFolio());
        m.put("DOCUMENT_DATE", fecha);
        m.put("FISCAL_YEAR", EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
        for (Iterator<String> keyIterator = m.keySet().iterator(); keyIterator.hasNext(); ) {
            String keyStr = keyIterator.next();
            c.getCasoDato(keyStr).setValor(m.get(keyStr));
        }
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
        if (id_gabinete < 0) {
            log.error("Identificador de Gabiente inválido (< 0)");
            throw new SQLException("Identificador de Gabiente inválido (< 0)");
        }
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        AplicacionManager.updateExpediente(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionManager.update(conn, co, opResponsable);
        c = CasoManager.select(conn, c);
        return c;
    }

    public static Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String jniName, Connection conn) throws Exception {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
        Caso c = CasoManager.nuevoCaso(conn, u, idTCaso, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        c.getCasoDato("EJERCICIO_FISCAL").setValor(adbl.obtenEjercicioFiscal());
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        m.put("EJERCICIO_FISCAL", adbl.obtenEjercicioFiscal());
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
        if (id_gabinete < 0) {
            log.error("Identificador de Gabiente inválido (< 0)");
            throw new SQLException("Identificador de Gabiente inválido (< 0)");
        }
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        AplicacionManager.updateExpediente(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionManager.update(conn, co, opResponsable);
        c = CasoManager.select(conn, c, c.getCasoOperacion(0).getIdCasoOper());
        return c;
    }

    public static int buscaPrimerCoincidencia(Sheet hoja, int renglonInicio, int columnaBuscar, String valor) {
        int nRenglon = -1;
        int cnt = 0;
        for (int i = renglonInicio; i < hoja.getLastRowNum(); i++) {
            if (valor.equals(hoja.getRow(i).getCell(columnaBuscar).getStringCellValue().trim())) {
                nRenglon = renglonInicio + cnt;
                break;
            }
            cnt = cnt + 1;
        }
        return nRenglon;
    }

    public static int buscaNumero(Sheet hoja, int renglonInicio, int columnaBuscar, int valor) {
        int nRenglon = -1;
        int cnt = 0;
        for (int i = renglonInicio; i < hoja.getLastRowNum(); i++) {
            if (CellType.BLANK == hoja.getRow(i).getCell(columnaBuscar).getCellType())
                break;
            if (valor == hoja.getRow(i).getCell(columnaBuscar).getNumericCellValue()) {
                nRenglon = renglonInicio + cnt;
                break;
            }
            cnt = cnt + 1;
        }
        return nRenglon;
    }

    public static Sheet resultSetToExcelRow(ResultSet rs, Sheet hoja, int renglonInicio, int columnaInicio, int inicioResultSet, boolean incluirEncabezado) throws Exception {
        ResultSetMetaData rsMetadata = rs.getMetaData();
        Row rw = (hoja.getRow(renglonInicio) == null ? hoja.createRow(renglonInicio) : hoja.getRow(renglonInicio));
        for (int i = inicioResultSet; i < rsMetadata.getColumnCount(); i++) {
            createExcelCell(i + columnaInicio, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
        }
        return hoja;
    }

    public static Sheet EvaluaFormula(Workbook workbook, Sheet hoja, int renglonInicio, int renglonFin, int columnInicio, int columnFin) throws Exception {
        FormulaEvaluator evaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        for (int i = renglonInicio; i < renglonFin; i++) {
            Row rw = (hoja.getRow(i) == null ? hoja.createRow(i) : hoja.getRow(i));
            for (int j = columnInicio; j < columnFin; j++) {
                Cell cell = (rw.getCell(j) == null ? rw.createCell(j) : rw.getCell(j));
                if (cell.getCellType() == CellType.FORMULA) {
                    evaluator.evaluateFormulaCell(cell);
                }
            }
        }
        return hoja;
    }

    public static Sheet EvaluaFormula(XSSFWorkbook workbook, XSSFSheet hoja, int renglonInicio, int renglonFin, int columnInicio, int columnFin) throws Exception {
        XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        for (int i = renglonInicio; i < renglonFin; i++) {
            XSSFRow rw = (hoja.getRow(i) == null ? hoja.createRow(i) : hoja.getRow(i));
            for (int j = columnInicio; j < columnFin; j++) {
                XSSFCell cell = (rw.getCell(j) == null ? rw.createCell(j) : rw.getCell(j));
                if (cell.getCellType() == CellType.FORMULA) {
                    evaluator.evaluateFormulaCell(cell);
                }
            }
        }
        return hoja;
    }

    /**
     * Escribe un archivo separado por comas con la informacion del resultset
     *
     * @param destino
     *            Archivo destino
     * @param rs
     *            ResultSet abierto
     * @throws Exception
     */
    public static void CSVFromResultSet(File destino, ResultSet rs, boolean incluirEncabezado) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] encabezados = new String[rsmd.getColumnCount()];
        FileWriter fw = new FileWriter(destino);
        String token = "";
        String linea = "";
        // if (incluirEncabezado){
        /* Genera encabezado del archivo Excel */
        for (int i = 0; i < encabezados.length; i++) {
            encabezados[i] = rsmd.getColumnName(i + 1);
            linea = linea + token + rsmd.getColumnName(i + 1);
            token = "|";
        }
        fw.write(linea + "\n");
        linea = "";
        token = "";
        // }
        /* Ingresa las columnas como resultado */
        while (rs.next()) {
            for (int i = 0; i < encabezados.length; i++) {
                int tipoDato = rsmd.getColumnType(i + 1);
                if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
                    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
                    df.setGroupingUsed(false);
                    df.setMaximumFractionDigits(2);
                    df.setMinimumFractionDigits(2);
                    String importe = df.format(rs.getDouble(encabezados[i]));
                    linea = linea + token + importe;
                    token = "|";
                } else {
                    linea = linea + token + rs.getString(encabezados[i]);
                    token = "|";
                }
            }
            fw.write(linea + "\n");
            token = "";
            linea = "";
        }
        fw.flush();
        fw.close();
    }

    public static FolioGeneratorInterface getFolioGenerator(String folioGenerator) {
        try {
            FolioGeneratorInterface fg = null;
            ClassLoader cl = Util.class.getClassLoader();
            Class<?> clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
            return fg;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    public static synchronized String generaNombreZip(String directorioTemporal, String extension) {
        String idRandom = String.valueOf(Math.round((1 + Math.random()) * 100000));
        String idArchivoFinal = GestionInterface.PREFIX_TEMP.substring(0, GestionInterface.PREFIX_TEMP.length() - idRandom.length()) + idRandom;
        String nombreDestino = directorioTemporal + "CARGA_ARCHIVO_ZIP_" + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension;
        return nombreDestino;
    }

    public static String normalizaEP(String ep) {
        if (StringUtils.isEmpty(ep))
            return null;
        String epNormalizada = ep.substring(0, 19) + ("09".equals(ep.substring(22, 24)) || "02".equals(ep.substring(22, 24)) || "03".equals(ep.substring(22, 24)) ? "00" : ep.substring(22, 24)) + ep.substring(21);
        return epNormalizada;
    }

    public static String getTodayESMX() {
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        return today;
    }

    public static Connection getStandAloneConnection() throws Exception {
        DBConfigurator dbConfigurator = DBConfigurator.instance(dbPropertiesFilePath);
        return getConnection(dbConfigurator, false);
    }

    public static Connection getStandAloneConnection(boolean isProcesoGRM) throws Exception {
        Connection conn = null;
        if (isProcesoGRM) {
            DBConfigurator dbConfigurator = DBConfigurator.instance(dbPropertiesFilePathGRM);
            conn = getConnection(dbConfigurator, false);
        } else {
            conn = getStandAloneConnection();
        }
        return conn;
    }

    public static Date stringToDate(String dateStr, String format) throws ParseException {
        if (StringUtils.isBlank(dateStr))
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = formatter.parse(dateStr);
        return date;
    }

    public static String getTodayFile() {
        String dateFormat = "yyyy.MM.dd.HH.mm";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    public static List<ExportLogDetallado> addToZip(ZipOutputStream zos, Documento[] agregar, String cxp) throws Exception {
        byte[] buffer = new byte[524288];
        ArrayList<ExportLogDetallado> result = new ArrayList<ExportLogDetallado>();
        Documento[] arrdocumento = agregar;
        int n = arrdocumento.length;
        int n2 = 0;
        while (n2 < n) {
            Documento doc = arrdocumento[n2];
            ExportLogDetallado logDetallado = new ExportLogDetallado();
            logDetallado.setCuentaPorPagar(cxp);
            try {
                String nombreEntrada = doc.getNombreDocumento();
                logDetallado.setPathDocumento(nombreEntrada);
                logDetallado.setCarpeta(Util.getDocPath(nombreEntrada));
                logDetallado.setDocumento(Util.getFileName(nombreEntrada));
                if (doc.getPaginasDocumento() != null && doc.getPaginasDocumento().length > 0) {
                    int len;
                    logDetallado.setCumple(true);
                    String file = doc.getPaginaDocumento(0).getFullPathFileName();
                    File f = new File(file);
                    if (!f.exists()) {
                        file = "/archivoImagen.jpg";
                        nombreEntrada = String.valueOf(doc.getNombreDocumento()) + ".jpg";
                        logDetallado.setLog("No se encontro el archivo en disco.");
                        logDetallado.setCumple(false);
                    } else {
                        if (nombreEntrada.indexOf(46) < 0) {
                            nombreEntrada = String.valueOf(doc.getNombreDocumento()) + "." + doc.getExtension();
                        }
                        logDetallado.setDocumento(Util.getFileName(nombreEntrada));
                        if (StringUtils.isEmpty((String) Util.getFileExtencion(nombreEntrada))) {
                            logDetallado.setLog("No se encontro extencion para el documento.");
                            logDetallado.setCumple(false);
                        }
                    }
                    ZipEntry ze = new ZipEntry(nombreEntrada);
                    zos.putNextEntry(ze);
                    FileInputStream in = new FileInputStream(file);
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    in.close();
                    logDetallado.setLog(String.valueOf(logDetallado.getLog()) + ";" + "Archivo Procesado");
                    logDetallado.setCumple(logDetallado.isCumple());
                } else {
                    logDetallado.setLog("El documento esta vacio.");
                    logDetallado.setCumple(false);
                }
            } catch (Exception e) {
                logDetallado.setLog("ERROR procesando documento:" + e);
                logDetallado.setCumple(false);
            }
            result.add(logDetallado);
            ++n2;
        }
        return result;
    }

    public static String getDocPath(String nameFile) {
        String path = "";
        if (!(StringUtils.isEmpty((String) nameFile) || nameFile.indexOf("\\") <= 0 && nameFile.indexOf("/") <= 0)) {
            String separador = nameFile.indexOf("\\") > 0 ? "\\\\" : "/";
            String[] componentes = nameFile.split(separador);
            int i = 0;
            while (i < componentes.length - 1) {
                path = String.valueOf(path) + "/" + componentes[i];
                ++i;
            }
        }
        return path;
    }

    /**
     * Escribe un archivo separado por comas con la informacion del resultset
     *
     * @param destino
     *            Archivo destino
     * @param rs
     *            ResultSet abierto
     * @throws Exception
     */
    public static void CSVFromResultSetSinEncabezado(File destino, ResultSet rs, boolean incluirEncabezado) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] encabezados = new String[rsmd.getColumnCount()];
        FileWriter fw = new FileWriter(destino);
        String token = "";
        String linea = "";
        String p = "";
        /* Genera encabezado del archivo Excel */
        for (int i = 0; i < encabezados.length; i++) {
            encabezados[i] = rsmd.getColumnName(i + 1);
            if (incluirEncabezado) {
                linea = linea + token + rsmd.getColumnName(i + 1);
                token = "|";
            }
        }
        if (incluirEncabezado) {
            fw.write(linea + "\n");
            linea = "";
            token = "";
        }
        /* Ingresa las columnas como resultado */
        while (rs.next()) {
            for (int i = 0; i < encabezados.length; i++) {
                int tipoDato = rsmd.getColumnType(i + 1);
                if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
                    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
                    df.setGroupingUsed(false);
                    df.setMaximumFractionDigits(2);
                    df.setMinimumFractionDigits(2);
                    String importe = df.format(rs.getDouble(encabezados[i]));
                    linea = linea + token + importe;
                    token = "|";
                } else {
                    p = rs.getString(encabezados[i]);
                    linea = linea + token + rs.getString(encabezados[i]);
                    token = "|";
                }
            }
            fw.write(linea + "\n");
            token = "";
            linea = "";
        }
        fw.flush();
        fw.close();
    }

    /**
     * Crea una celda de un archivo excel como fecha.
     *
     * @param index
     *            Posicion de la celda.
     * @param fila
     *            Objeto al que se agrega la celda.
     * @param tipoDato
     *            Tipo de dato.
     * @return Celda creada con formato.
     */
    public static HSSFCell createExcelDateCell(int index, HSSFRow fila, String dateVal, CellStyle estiloFecha) throws Exception {
        HSSFCell cell = fila.createCell(index);
        cell.setCellStyle(estiloFecha);
        cell.setCellValue(Util.stringToDate(dateVal, "dd/MM/yyyy"));
        return cell;
    }

    public static Properties loadFileProperties(String path) throws Exception {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(path);
            prop.load(input);
            return prop;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Connection getConnection(DBConfigurator dbConfigurator) throws Exception {
        return getConnection(dbConfigurator, false);
    }

    public static Connection getConnection(DBConfigurator dbConfigurator, boolean autoCommit) throws Exception {
        Class.forName(dbConfigurator.getDriverClassName());
        Connection conn = DriverManager.getConnection(dbConfigurator.getUrl(), dbConfigurator.getUserName(), dbConfigurator.getPassword());
        conn.setAutoCommit(autoCommit);
        return conn;
    }

    public static String sqlDateToString(java.sql.Date fecha, String patron) {
        Date d = new Date(fecha.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(patron);
        return sdf.format(d);
    }

    public static String resultSetToConcatenateString(ResultSet rs, String conactenateChar, int initIndex) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] encabezados = new String[rsmd.getColumnCount()];
        String token = "";
        String linea = "";
        int renglones = 0;
        /* Genera encabezado del archivo Excel */
        for (int i = 0; i < encabezados.length; i++) {
            encabezados[i] = rsmd.getColumnName(i + 1);
        }
        /* Ingresa las columnas como resultado */
        do {
            if (renglones > 0)
                linea = linea + "\n";
            for (int i = initIndex; i < encabezados.length; i++) {
                linea = linea + token + StringUtils.trimToEmpty(rs.getString(encabezados[i]));
                token = ",";
            }
            token = "";
            renglones++;
        } while (rs.next());
        return linea;
    }

    public static String dateToString(Date date, String formatoSalida) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatoSalida);
        return sdf.format(date);
    }

    public static File generaZip(File file) throws Exception {
        byte[] buffer = new byte[1024];
        String filenaName = getFileWithoutExtencion(file.getAbsolutePath()) + ".zip";
        File fOut = new File(filenaName);
        FileOutputStream fos = new FileOutputStream(fOut);
        ZipOutputStream zos = new ZipOutputStream(fos);
        ZipEntry ze = new ZipEntry(file.getName());
        zos.putNextEntry(ze);
        FileInputStream in = new FileInputStream(file);
        int len;
        while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        in.close();
        zos.closeEntry();
        zos.flush();
        zos.close();
        return fOut;
    }

    public static void generaZip(File fileZIP, File[] files) throws Exception {
        byte[] buffer = new byte[1024];
        FileOutputStream fos = new FileOutputStream(fileZIP);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (File file : files) {
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(file);
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            in.close();
            zos.closeEntry();
        }
        zos.flush();
        zos.close();
    }

    public static String parseXMLDate(XMLGregorianCalendar date, String pattern) {
        Calendar calendar = date.toGregorianCalendar();
        return dateToString(calendar.getTime(), pattern);
    }

    public static String join(List<String> array, char joinChar) {
        String cadenaResultado = "";
        String token = "";
        for (int i = 0; i < array.size(); i++) {
            cadenaResultado += token + String.valueOf(array.get(i));
            token = String.valueOf(joinChar);
        }
        return cadenaResultado;
    }

    /**
     * Calcula la fecha de aplicacion de un pago en base a las siguientes
     * reglas: Si el ejercicio fiscal activo es igual al año corriente entonces
     * la fecha de aplicacion es el dia. <br>
     * En caso contrario la fecha de aplicacion es el ultimo dia del ejercicio
     * fiscal activo. <br>
     * <br>
     * Esto ocurre cuando se siguen capturando pagos los primeros dias del
     * ejercicio siguiente.
     *
     * @param ejercicioActivo
     * @return
     */
    public static String calculaFechaAplicacion(EjercicioFiscal ejercicioActivo) {
        Calendar today = new GregorianCalendar();
        int aEjercicio = Integer.parseInt(ejercicioActivo.getaEjercicioFiscal());
        int currYear = today.get(Calendar.YEAR);
        if (currYear != aEjercicio)
            return "31/12/" + String.valueOf(aEjercicio);
        else
            return Util.getTodayESMX();
    }

    /**
     * Calcula el mes de aplicacion de un pago en base a las siguientes reglas:
     * Si el ejercicio fiscal activo es igual al año corriente entonces la fecha
     * de aplicacion es el dia. <br>
     * En caso contrario la fecha de aplicacion es el ultimo dia del ejercicio
     * fiscal activo. <br>
     * <br>
     * Esto ocurre cuando se siguen capturando pagos los primeros dias del
     * ejercicio siguiente.
     *
     * @param ejercicioActivo
     * @return
     */
    public static int calculaMesAplicacion(EjercicioFiscal ejercicioActivo) {
        Calendar today = new GregorianCalendar();
        int aEjercicio = Integer.parseInt(ejercicioActivo.getaEjercicioFiscal());
        int currYear = today.get(Calendar.YEAR);
        if (currYear != aEjercicio)
            return 12;
        else
            return today.get(Calendar.MONTH) + 1;
    }

    public static String extractZipToStream(InputStream inputStream) throws Exception {
        String directorioTemporal = System.getProperty("java.io.tmpdir");
        ZipInputStream zipIn = new ZipInputStream(inputStream);
        ZipEntry entry = zipIn.getNextEntry();
        log.trace("Iterando contenido del archivo.");
        if (entry != null) {
            String nombreElemento = FacturaUtils.obtenNombreArchivoZip(entry.getName(), false);
            String extension = FacturaUtils.obtenExtensionArchivoZip(entry.getName());
            String filePath = FacturaUtils.generaNombreArchivoTemporal(directorioTemporal, nombreElemento, extension);
            if (!entry.isDirectory()) {
                log.trace("Se trata de un archivo, se extraera");
                File f = new File(filePath);
                extractFile(zipIn, filePath);
            } else {
                log.trace("Se trata de un directorio, se notifica excepcion.");
                throw new Exception("El archivo debe estar directamente en la raiz del zip, no dentro de la carpeta " + nombreElemento);
            }
            return filePath;
        } else
            throw new Exception("El archivo zip esta vacio");
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        log.trace("Extrayendo archivo a disco. Escribiendo a flujo de salida");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[2048];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        log.trace("Archivo extraido exitosamente. Cerrando flujos");
        bos.flush();
        bos.close();
        bos = null;
        log.trace("flujos cerrados");
    }

    public static long dateLong(String fechaCaptura, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = formatter.parse(fechaCaptura);
        return date.getTime();
    }

    public static Map<String, String> readValuesCasoDato(Map<?, ?> casoDato) {
        Map<String, String> rMap = new LinkedHashMap<String, String>();
        for (Iterator<?> i = casoDato.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            CasoDato cd = (CasoDato) casoDato.get(key);
            if (cd != null)
                rMap.put(key, cd.getValor());
            else
                rMap.put(key, null);
        }
        return rMap;
    }

    public static final String listToHTMLTable(List<String> list) {
        String result = "";
        String openRow = "<tr>";
        String closeRow = "</tr>";
        String openCell = "<td>";
        String closeCell = "</td>";
        for (String str : list) {
            result = result + openRow + openCell + str + closeCell + closeRow;
        }
        return result;
    }

    public static TipoCasoInterface instanceTipoCasoInterface(String name) throws Exception {
        TipoCasoInterface tci = null;
        ClassLoader cl = Util.class.getClassLoader();
        Class<?> clase = cl.loadClass(name);
        tci = (TipoCasoInterface) clase.newInstance();
        return tci;
    }

    public static EgresoEncabezado instanceEgresoEncabezado(String name) throws Exception {
        EgresoEncabezado eEnc = null;
        ClassLoader cl = Util.class.getClassLoader();
        Class<?> clase = cl.loadClass(name);
        eEnc = (EgresoEncabezado) clase.newInstance();
        return eEnc;
    }

    public static SolicitudFirmaElectronica instanceCasoFIEL(String name) throws Exception {
        SolicitudFirmaElectronica tci = null;
        ClassLoader cl = Util.class.getClassLoader();
        Class<?> clase = cl.loadClass(name);
        tci = (SolicitudFirmaElectronica) clase.newInstance();
        return tci;
    }

    public static String parseSQLDate(java.sql.Date date) {
        Date d = new Date(date.getTime());
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(d);
    }

    public static void deleteFile(String path) {
        try {
            if (StringUtils.isBlank(path))
                return;
            File f = new File(path);
            if (f.exists())
                if (!f.delete())
                    f.deleteOnExit();
        } catch (Exception e) {
            log.warn("Problemas eliminando archivo: " + path + ". Casua: " + e);
        }
    }

    public static int ultimoDiaMes(int nMes) {
        int mes = nMes - 1;
        return DIAS_POR_MES[mes];
    }

    public static LocalDate lastDayOfMonth() {
        LocalDate fechaActual = LocalDate.now();
        LocalDate ultimoDiaDelMes = fechaActual.withDayOfMonth(fechaActual.lengthOfMonth());
        return ultimoDiaDelMes;
    }

    public static int getCurrentYear() {
        Calendar c = new GregorianCalendar();
        return c.get(Calendar.YEAR);
    }

    public static int getYearFromDate(String fechaStr) {
        int year = 0;
        fechaStr = fechaStr.replace("'", "");
        String[] fecha = fechaStr.split("/");
        if (fecha == null || fecha.length == 1) {
            fecha = fechaStr.split("-");
            year = Integer.parseInt(fecha[0]);
        } else
            year = Integer.parseInt(fecha[2]);
        return year;
    }

    public static void StringBuilderToFile(StringBuilder stringBuilder, String archivoSalida) throws Exception {
        File file = new File(archivoSalida);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(stringBuilder.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public static int getCurrentMonth() throws Exception {
        EjercicioFiscalBusinessLogic ebl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
        int efActivo = Integer.parseInt(ebl.getEjercicioFiscalActivo().getaEjercicioFiscal());
        int year = getCurrentYear();
        if (efActivo != year)
            return 12;
        else {
            Calendar c = new GregorianCalendar();
            return c.get(Calendar.MONTH) + 1;
        }
    }

    public static int getCurrentMonth(Connection conn) {
        try {
            int efActivo = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            int year = getCurrentYear();
            if (efActivo != year)
                return 12;
            else {
                Calendar c = new GregorianCalendar();
                return c.get(Calendar.MONTH) + 1;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean esIngresoPropio(String ep) {
        String fuenteFinanciamiento = ep.substring(39, 40);
        return "4".equals(fuenteFinanciamiento);
    }

    public static EgresoDetalle instanceEgresoDetalle(String name) throws Exception {
        EgresoDetalle eDet = null;
        ClassLoader cl = Util.class.getClassLoader();
        Class<?> clase = cl.loadClass(name);
        eDet = (EgresoDetalle) clase.newInstance();
        return eDet;
    }

    public static JSONObject toJson(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String objStr = mapper.writeValueAsString(obj);
        return new JSONObject(objStr);
    }

    public static int folio(Caso c) {
        return Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
    }

    public static int folio(String folio) {
        return Integer.parseInt(folio.substring(folio.lastIndexOf('-') + 1));
    }

    public static JSONArray toJSONArray(List<Map<String, String>> resumen) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < resumen.size(); i++) {
            Map<String, String> objMap = resumen.get(i);
            JSONObject object = new JSONObject(objMap);
            array.put(object);
        }
        return array;
    }

    public static File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static Calendar toDate(long millis) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(millis);
        return c;
    }

    /**
     * Envia como pagina HTML el mensaje de error reportado en una Excepcion.
     *
     * @param out
     *            Salida
     * @param e
     *            Excepcion a reportar
     */
    public static void sendHTMLSuccessMsg(HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
        out.println("<title>Operacion Exitosa!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("	<table align=\"center\">");
        out.println("		<tr>");
        out.println("			<td align=\"center\">");
        out.println("				<table>");
        out.println("					<tr>");
        out.println("						<td colspan=\"2\" align=\"center\">");
        out.println("							<h1>Operacion terminada exitosamente!</h1></td>");
        out.println("					</tr>");
        out.println("");
        out.println("					<tr>");
        out.println("						<td colspan=\"2\" align=\"left\">Por favor de clic <a href=\"#\" onclick=\"javascript:window.close();\">Aqu&iacute;</a> para cerrar esta ventana");
        out.println("							mensaje.</td>");
        out.println("					</tr>");
        out.println("				</table></td>");
        out.println("		</tr>");
        out.println("");
        out.println("	</table>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    public static String getFechaHoraActualFN() {
        String dateFormat = "yyyyddMM.HHmm";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    public static java.sql.Date toSQLDate(Date d) {
        if (d == null)
            return null;
        return new java.sql.Date(d.getTime());
    }

    public static java.sql.Date toSQLDate(String d) throws Exception {
        return toSQLDate(Util.stringToDate(d, "dd/MM/yyyy"));
    }

    public static File generateVolFile(Connection conn, String prefix, String extension) throws Exception {
        Volumen vol = VolumenManager.getVolumen(conn);
        File volDir = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen());
        String fileName = DocumentoManager.getNextFilename(null, prefix);
        File f = new File(volDir, fileName + "." + extension);
        return f;
    }

    public static int readNumericID(String folio) {
        int nFolio = Integer.parseInt(folio.substring(folio.lastIndexOf('-') + 1));
        return nFolio;
    }

    /**
     * Envia como texto plano un objeto Java en formato JSON especificando en el
     * encabezado el status http.
     *
     * @param resp
     *            Response abierto.
     * @param httpStatus
     *            Estatus HTTP. -1 para asumir Estatus OK
     * @param json
     *            Objeto a enviar
     * @throws IOException
     *             Si existen problemas de comunicacion
     */
    public static void sendJSONResponse(HttpServletResponse resp, int httpStatus, Object json) throws IOException {
        resp.setContentType("application/json; charset=" + StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(httpStatus > 0 ? httpStatus : HttpServletResponse.SC_OK);
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        // mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.writeValue(writer, json);
        writer.flush();
        writer.close();
    }

    /**
     * Envia como texto plano un objeto Java en formato JSON
     *
     * @param resp
     *            Response abierto.
     * @param json
     *            Objeto a enviar
     * @throws IOException
     *             Si existen problemas de comunicacion
     */
    public static synchronized void sendJSONResponse(HttpServletResponse resp, Object json) throws IOException {
        sendJSONResponse(resp, -1, json);
    }

    public static void compress(OutputStream os, InputStream in, String fileName) throws IOException {
        byte[] b = new byte[512];
        ZipOutputStream zout = new ZipOutputStream(os);
        ZipEntry e = new ZipEntry(fileName);
        zout.putNextEntry(e);
        int len = 0;
        while ((len = in.read(b)) != -1) {
            zout.write(b, 0, len);
        }
        zout.closeEntry();
        zout.close();
    }

    public static Date getTomorrow() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static Date todayPlus(int i) {
        LocalDate datePlusDays = LocalDate.now().plusDays(i);
        return Date.from(datePlusDays.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void generaFormatoJasper(Connection conn, HttpServletResponse resp, String ruta, Map<String, Object> parms, String reportPath) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(reportPath);
            JasperRunManager.runReportToPdfStream(in, out, parms, conn);
            resp.setContentType("application/pdf");
            out.flush();
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }

    public static String readMailTemplate(String templateURL) {
        InputStream is = NotificaAdecuacionMetasCliente.class.getResourceAsStream(templateURL);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
            return textBuilder.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.toString());
        }
    }

    public static String removeStringBOMChar(String strToBeConverted) {
        if (strToBeConverted.codePointAt(0) == 0xFEFF) {
            return strToBeConverted.substring(1);
        } else
            return strToBeConverted;
    }

    public static void writToFile(String fileName, List<String> lines) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        for (String str : lines) {
            writer.write(str + System.lineSeparator());
        }
        writer.flush();
        writer.close();
    }

    public static void sendJSONError(HttpServletResponse resp, Exception e) throws IOException {
        String errorJson = mapper.writeValueAsString(e.toString());
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write(errorJson);
    }

    public static void sendJSON(HttpServletResponse resp, Object obj) throws IOException {
        String jsonString = mapper.writeValueAsString(obj);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonString);
    }

    public static String getTodayWithTime() {
        String dateFormat = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    public static void rollback(Connection conn) {
        if (conn != null)
            try {
                conn.rollback();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
    }

    public static String readJNIName(ServletConfig config) {
        String jniName = "";
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        return jniName;
    }

    public static File copyFile(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        FileInputStream sourceIS = null;
        FileOutputStream destOS = null;
        try {
            sourceIS = new FileInputStream(source);
            destOS = new FileOutputStream(dest);
            sourceChannel = sourceIS.getChannel();
            destChannel = destOS.getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            destOS.flush();
            return dest;
        } finally {
            CloseObject.closeStream(destOS);
            CloseObject.closeStream(sourceIS);
            CloseObject.closeStream(sourceChannel);
            CloseObject.closeStream(destChannel);
        }
    }

    public static HSSFWorkbook openExcel(File workBook) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(workBook);
            HSSFWorkbook wb = new HSSFWorkbook(fis);
            return wb;
        } finally {
            CloseObject.closeStream(fis);
        }
    }

    public static Connection getSAIConnection(Connection conn) throws Exception {
        boolean ambienteDesarrollo = "true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO"));
        String eFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        String dbName = "sai_" + eFiscal + (ambienteDesarrollo ? "_desa" : "");
        DBConfigurator configDB = EjercicioFiscalManager.getEjercicioFiscalActivoDB(conn, dbName);
        return StandAloneConnection.getConnection(configDB);
    }

    public static Connection getSAIConnection(Connection conn, String dbName, String eFiscal) throws Exception {
        DBConfigurator configDB = EjercicioFiscalManager.getPropertiesDB(conn, dbName, eFiscal);
        return StandAloneConnection.getConnection(configDB);
    }

    public static String join(List<Integer> array, String joinChar) {
        StringBuilder cadenaResultado = new StringBuilder();
        String token = "";
        for (int i = 0; i < array.size(); i++) {
            cadenaResultado.append(token).append(String.valueOf(array.get(i)));
            token = String.valueOf(joinChar);
        }
        return cadenaResultado.toString();
    }

    public static int getCapitulo(String ep) {
        String[] partes = ep.split("\\.");
        String partidaPresupuestal = partes[9];
        char primerDigito = partidaPresupuestal.charAt(0);
        // Convertir el primer dígito a entero
        int capitulo = Character.getNumericValue(primerDigito);
        return capitulo;
    }

    public static List<String> readFileByLine(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        return lines;
    }

    public static String getConcatenatedViews(List<UnidadEjecutora> unitView, Usuario u) {
        StringBuilder euPermission = new StringBuilder();
        if (unitView == null)
            euPermission.append("'").append(u.getU_UR()).append("'");
        else {
            String token = "";
            for (UnidadEjecutora ue : unitView) {
                euPermission.append(token).append("'").append(ue.getUe()).append("'");
                if ("".equals(token))
                    token = ",";
            }
        }
        return euPermission.toString();
    }

    public static File saveFile(Part filePart) throws IOException {
        File tempFile = File.createTempFile("uploaded", ".zip");
        Files.copy(filePart.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

    public static Carpeta creaCarpeta(Connection conn, Caso c, Usuario u, String nombreCarpeta, String descripcion) throws SQLException, FortimaxException {
        Carpeta carpeta = new Carpeta();
        carpeta.setBanderaRaiz("N");
        carpeta.setDescripcion(StringUtils.isEmpty(descripcion) ? nombreCarpeta : descripcion);
        carpeta.setFechaCreacion(new java.sql.Timestamp(System.currentTimeMillis()));
        carpeta.setFechaModificacion(carpeta.getFechaCreacion());
        carpeta.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete()));
        carpeta.setIdGabinete(c.getIdGabinete());
        carpeta.setNombreCarpeta(nombreCarpeta);
        carpeta.setNombreUsuario(u.getLogin());
        carpeta.setNumeroAccesos(0);
        carpeta.setNumeroCarpetas(0);
        carpeta.setPassword("-1");
        carpeta.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
        OrgCarpeta oCarpeta = new OrgCarpeta();
        oCarpeta.setIdCarpetaHija(carpeta.getIdCarpeta());
        oCarpeta.setIdCarpetaPadre(1);
        oCarpeta.setIdGabinete(carpeta.getIdGabinete());
        oCarpeta.setNombreHija(carpeta.getNombreCarpeta());
        oCarpeta.setTituloAplicacion(carpeta.getTituloAplicacion());
        carpeta = CarpetaManager.insertaCarpeta(conn, carpeta);
        OrgCarpetaManager.insert(conn, oCarpeta);
        return carpeta;
    }

    public static Documento creaDocumento(Connection conn, Caso c, Carpeta cPadre, Usuario u, String nombreDocumento, String descripcion) throws FortimaxException, SQLException {
        Documento d = new Documento();
        d.setNombreTipoDocto("EXTERNO");
        d.setDescripcion(StringUtils.isBlank(descripcion) ? nombreDocumento : descripcion);
        d.setIdCarpetaPadre(cPadre.getIdCarpeta());
        d.setIdDocumento(DocumentoManager.getNextIdDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), cPadre.getIdCarpeta()));
        d.setIdGabinete(c.getIdGabinete());
        d.setNombreDocumento(nombreDocumento);
        d.setNombreUsuario(u.getNombre());
        d.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
        DocumentoManager.insertDocumento(conn, d);
        return DocumentoManager.selectDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
    }

    public static Sheet resultSetToExcelE(ResultSet rs, Sheet hoja, int renglonInicio, int columnaInicio, int inicioResultSet, boolean incluirEncabezado, CellStyle estiloTabla) throws Exception {
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        if (incluirEncabezado) {
            Row rw = (hoja.getRow(renglonInicio + cnt) == null ? hoja.createRow(renglonInicio + cnt) : hoja.getRow(renglonInicio + cnt));
            Cell cell = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            CellStyle estiloTitulo = cell.getCellStyle();
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                rw.createCell(i + columnaInicio).setCellValue(rsMetadata.getColumnName(i + 1));
                rw.getCell(i + columnaInicio).setCellStyle(estiloTitulo);
            }
            cnt++;
        }
        while (rs.next()) {
            Row rw = (hoja.getRow(renglonInicio + cnt) == null ? hoja.createRow(renglonInicio + cnt) : hoja.getRow(renglonInicio + cnt));
            for (int i = inicioResultSet; i < rsMetadata.getColumnCount(); i++) {
                createExcelCellRep(i + columnaInicio, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        return hoja;
    }

    public static List<Path> unzipToTemp(File zipFile) throws IOException {
        if (zipFile == null || !zipFile.exists()) {
            throw new IllegalArgumentException("El archivo ZIP no existe.");
        }
        List<Path> extractedFiles = new ArrayList<>();
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "unzip_" + UUID.randomUUID().toString());
        Files.createDirectories(tempDir);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path resolvedPath = tempDir.resolve(entry.getName()).normalize();
                if (!resolvedPath.startsWith(tempDir)) {
                    throw new IOException("Entrada ZIP invalida: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    if (resolvedPath.getParent() != null) {
                        Files.createDirectories(resolvedPath.getParent());
                    }
                    try (OutputStream os = Files.newOutputStream(resolvedPath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                    extractedFiles.add(resolvedPath);
                }
                zis.closeEntry();
            }
        }
        return extractedFiles;
    }
}
