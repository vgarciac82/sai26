package com.syc.obrapublica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import com.syc.contable.core.Saldo;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfiguraAplicativoBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ConfiguraAplicativoBusinessLogic.class);

    public ConfiguraAplicativoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public String getSystemSetting(String settingName) {
        Connection conn = null;
        String val = null;
        try {
            conn = getConnection();
            val = ConfiguraAplicativoManager.getSystemSetting(conn, settingName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return val;
    }

    public String getSystemSetting(String settingGName, String settingGPName) {
        Connection conn = null;
        String val = null;
        try {
            conn = getConnection();
            val = ConfiguraAplicativoManager.getSystemSetting(conn, settingGName, settingGPName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return val;
    }

    /*
	 * public String obtenConfiguracionTitulo(String cRamo) throws SQLException
	 * { String cTitulo = "Titulo no Definido"; Connection conn = null; try {
	 * conn = getConnection(); cTitulo =
	 * ConfiguraAplicativoManager.obtenConfiguracionTitulo(cRamo, conn); }
	 * finally { if (conn != null) { conn.close(); } conn = null; }
	 * 
	 * return cTitulo; }
	 */
    /*
	 * public String obtenUnidaREsponsableSHCP(String cRamo) throws SQLException
	 * { String cUnidadResponsble = ""; Connection conn = null; try { conn =
	 * getConnection(); cUnidadResponsble =
	 * ConfiguraAplicativoManager.obtenConfiguracionUR(cRamo, conn); } finally {
	 * if (conn != null) { conn.close(); } conn = null; } return
	 * cUnidadResponsble; }
	 */
    /*
	 * public String obtenFechaAplica(String cRamo) throws SQLException { String
	 * cActivo = "NO"; Connection conn = null; try { conn = getConnection();
	 * cActivo = ConfiguraAplicativoManager.obtenFechaAplica(cRamo, conn); }
	 * finally { if (conn != null) { conn.close(); } conn = null; }
	 * 
	 * return cActivo; }
	 */
    /*
	 * public String usaSicopMap(String cRamo) throws SQLException { String
	 * cActivar = "NO"; Connection conn = null; try { conn = getConnection();
	 * cActivar = ConfiguraAplicativoManager.obtenSicopMap(cRamo, conn); }
	 * finally { if (conn != null) { conn.close(); } conn = null; }
	 * 
	 * return cActivar; }
	 */
    /*
	 * public HashMap<String, String> buscaDocuemnto(int id_tc, String cRamo,
	 * String cUnidadResponsable, String cEjercicioFiscal) throws SQLException {
	 * HashMap<String, String> mapConfgapl = new HashMap<String, String>();
	 * Connection conn = null; try { conn = getConnection(); mapConfgapl =
	 * ConfiguraAplicativoManager.getConfigurationD(conn, id_tc, cRamo,
	 * cUnidadResponsable, cEjercicioFiscal); } finally { if (conn != null) {
	 * conn.close(); } conn = null; } return mapConfgapl;
	 * 
	 * }
	 */
    /*
	 * public int agregaValidacionDocto(int id_tc, String cRamo, String
	 * cUnidadResponsable, String cEjercicioFiscal, String cLLave, String
	 * cValor) throws SQLException { int iAgregado = 0; Connection conn = null;
	 * try { conn = getConnection(); iAgregado =
	 * ConfiguraAplicativoManager.agregaValidaDocto(conn, id_tc, cRamo,
	 * cUnidadResponsable, cEjercicioFiscal, cLLave, cValor); conn.commit();
	 * 
	 * } finally { if (conn != null) { conn.close(); } conn = null;
	 * 
	 * } return iAgregado; }
	 */
    /*
	 * public String creaPDF(HashMap<String, String> mapMetaData,
	 * HashMap<String, String> mapTituloPagina, Caso c, String tempDir, Usuario
	 * u) throws IOException, DocumentException { String cRuta = ""; //
	 * DiskFileUpload upload = new DiskFileUpload(); //
	 * upload.setRepositoryPath(tempDir); // String ruta_destino =
	 * upload.getRepositoryPath() + "//"+c.getFolio();
	 * 
	 * // si destino es diferente de null if (tempDir != null) { try { // se
	 * crea instancia del documento Document mipdf = new Document() { }; // se
	 * establece una instancia a un documento pdf PdfWriter writer =
	 * PdfWriter.getInstance(mipdf, new FileOutputStream(tempDir));
	 * mipdf.open(); addMetaData(mipdf, mapMetaData); addTitlePage(mipdf,
	 * mapTituloPagina, writer); // addContent(mipdf); mipdf.add(new
	 * Paragraph("cUERPO DEL pdf")); // se añade el // contendio del PDF
	 * mipdf.close(); // se cierra el PDF& //
	 * JOptionPane.showMessageDialog(null,"Documento PDF creado"); } finally {
	 * 
	 * } }
	 * 
	 * return cRuta; }
	 * 
	 * private static void addEmptyLine(Paragraph paragraph, int number) { for
	 * (int i = 0; i < number; i++) { paragraph.add(new Paragraph(" ")); } }
	 * 
	 * private void addMetaData(Document document, HashMap<String, String>
	 * mapMetaData) { String ckValue = ""; try { ckValue =
	 * mapMetaData.get("cTitulo"); document.addTitle(ckValue); // se añade el
	 * titulo ckValue = mapMetaData.get("cAutor"); document.addAuthor(ckValue);
	 * // se añade el autor del documento ckValue = mapMetaData.get("cAsunto");
	 * document.addSubject(ckValue); // se añade el asunto del documento ckValue
	 * = mapMetaData.get("cPassword"); document.addKeywords(ckValue); // Se
	 * agregan palabras claves
	 * 
	 * } finally {
	 * 
	 * }
	 * 
	 * }
	 * 
	 * private static void addTitlePage(Document document, HashMap<String,
	 * String> mapTituloPagina, PdfWriter writer) throws DocumentException,
	 * IOException { Paragraph preface = new Paragraph(); String foobar = "";
	 * 
	 * // We add one empty line addEmptyLine(preface, 1); Chunk c;
	 * 
	 * // Dependencia Font helvetica = new Font(FontFamily.HELVETICA, 12);
	 * BaseFont bf_helv = helvetica.getCalculatedBaseFont(false); foobar =
	 * mapTituloPagina.get("cTituloDependencia"); float width_helv =
	 * bf_helv.getWidthPoint(foobar, 12); c = new Chunk(foobar + ": " +
	 * width_helv, helvetica); preface.add(new Paragraph(c));
	 * 
	 * // Direccion BaseFont bf_times =
	 * BaseFont.createFont("c:/windows/fonts/times.ttf", BaseFont.WINANSI,
	 * BaseFont.EMBEDDED); Font times = new Font(bf_times, 12); foobar =
	 * mapTituloPagina.get("cTituloDireGen"); float width_times =
	 * bf_times.getWidthPoint(foobar, 12); c = new Chunk(foobar + ": " +
	 * width_times, times); preface.add(new Paragraph(c));
	 * 
	 * // Sub Direccion foobar = mapTituloPagina.get("cTituloSubDireGen");
	 * width_times = bf_times.getWidthPoint(foobar, 12); c = new Chunk(foobar +
	 * ": " + width_times, times); preface.add(new Paragraph(c));
	 * 
	 * foobar = mapTituloPagina.get("cTituloGerencia");
	 * 
	 * width_times = bf_times.getWidthPoint(foobar, 10); c = new Chunk(foobar +
	 * ": " + width_times, times); preface.add(new Paragraph(c));
	 * 
	 * preface.add(new Paragraph(Line2D.relativeCCW(50, 50, 80, 80, 10, 10)));
	 * PdfContentByte canvas = writer.getDirectContent(); canvas.saveState();
	 * canvas.setLineWidth(0.05f); canvas.moveTo(400, 806);
	 * 
	 * preface.add(new Paragraph("Fecha de solicitud",
	 * FontFactory.getFont("arial", 9, Alignment.TOP_RIGHT_value)));
	 * 
	 * preface.add(new
	 * Paragraph("FORMATO DE SOLICITUD DE SUFICIENCIA PRESUPUESTAL",
	 * FontFactory.getFont("arial", 12, Alignment.CENTER_value)));
	 * 
	 * addEmptyLine(preface, 1); // Will create: Report generated by: _name,
	 * _date preface.add(new Paragraph("Para: Lic. Ignacio Soberanes Cortés"));
	 * addEmptyLine(preface, 3); preface.add(new
	 * Paragraph("De:   Lic. Martín Edmundo Pimienta Fernandez de Lara"));
	 * 
	 * addEmptyLine(preface, 8);
	 * 
	 * preface.add(new Paragraph(
	 * "Una vez cumplidos y verificados los requisitos normativos y procedimentales establecidos, se solicita Suficiencia Presupuest a la Gerencia de Presupuesto, para efectuar las erogaciones que a continuacion se señalan."
	 * ));
	 * 
	 * document.add(preface); }
	 * 
	 * private static void addContent(Document document) throws
	 * DocumentException {
	 * 
	 * 
	 * }
	 * 
	 * private static void createTable(Section subCatPart) throws
	 * BadElementException { PdfPTable table = new PdfPTable(3);
	 * 
	 * // t.setBorderColor(BaseColor.GRAY); // t.setPadding(4); //
	 * t.setSpacing(4); // t.setBorderWidth(1);
	 * 
	 * PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
	 * c1.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(c1);
	 * 
	 * c1 = new PdfPCell(new Phrase("Table Header 2"));
	 * c1.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(c1);
	 * 
	 * c1 = new PdfPCell(new Phrase("Table Header 3"));
	 * c1.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(c1);
	 * table.setHeaderRows(1);
	 * 
	 * table.addCell("1.0"); table.addCell("1.1"); table.addCell("1.2");
	 * table.addCell("2.1"); table.addCell("2.2"); table.addCell("2.3");
	 * 
	 * subCatPart.add(table);
	 * 
	 * }
	 */
    private String validaLinea(String laLinea, ArrayList arrLDetalle, String tablaDestino, String elDelim) {
        laLinea = laLinea.replace("'", " ");
        String elRegreso = "/*OK*/";
        ArrayList arrDetalle = new ArrayList();
        int j = 3;
        int laPos = 0;
        int laCol = 0;
        String elValor = "";
        String elTipo = "";
        String elWhere = "";
        String insertCampos = "";
        String updCampos = "";
        String insertValores = "";
        String tokenWhere = " where ";
        String tokenInsert = "";
        String tokenUpdate = "";
        String cVisible = "";
        String cIgnore = "";
        String cFormato = "";
        if ("|".equalsIgnoreCase(elDelim)) {
            elDelim = "\t";
            laLinea = laLinea.replace("|", "\t");
        } else {
            elDelim = elDelim.trim();
        }
        while (j < (arrLDetalle.size())) {
            arrDetalle = (ArrayList) arrLDetalle.get(j);
            int laLong = Integer.parseInt((String) arrDetalle.get(15));
            elTipo = (String) arrDetalle.get(5);
            cVisible = (String) arrDetalle.get(8);
            cIgnore = (String) arrDetalle.get(14);
            cFormato = (String) arrDetalle.get(7);
            if ("1".equals(cIgnore)) {
                j++;
                laCol++;
                laPos = laPos + laLong;
                continue;
            }
            if ("".equalsIgnoreCase(elDelim)) {
                if (laLinea.length() < laPos + laLong) {
                    return "El registro es menor al tamaño definido en el layout";
                }
                elValor = laLinea.substring(laPos, laPos + laLong).replaceAll("\"", "").trim();
            } else {
                String[] columnas;
                columnas = laLinea.split(elDelim);
                if (",".equals(elDelim))
                    columnas = parseCommaDelimited(columnas);
                if (columnas.length < arrLDetalle.size() - (3)) {
                    return "El registro tiene menos columnas que las definidas en el layout";
                }
                if (columnas.length > arrLDetalle.size() - (3)) {
                    return "El registro tiene más columnas que las definidas en el layout";
                }
                String borrar = columnas[0];
                String vFeccap = columnas[0].trim();
                elValor = columnas[laCol].replaceAll("\"", "").trim();
            }
            if ("integer".equalsIgnoreCase(elTipo) || "int".equalsIgnoreCase(elTipo)) {
                try {
                    if ("".equalsIgnoreCase(elValor.trim()) || "N/A".equalsIgnoreCase(elValor.trim()))
                        elValor = "0";
                    int elEntero = Integer.parseInt(elValor.trim());
                    elValor = "'" + elValor + "'";
                } catch (NumberFormatException e) {
                    int jj = j - 2;
                    return "El formato de la posición " + jj + " debe ser un número entero";
                }
            } else if ("date".equals(elTipo)) {
                if (elValor != null && ("".equals(elValor.trim()) || "0".equals(elValor.trim())))
                    elValor = "null";
                else {
                    if ("DDMMYYYY".equalsIgnoreCase(cFormato))
                        elValor = "convert(date,'" + elValor.substring(0, 2) + "/" + elValor.substring(2, 4) + "/" + elValor.substring(4, 8) + "',103)";
                    else
                        elValor = "'" + elValor + "'";
                }
            } else if ("decimal".equalsIgnoreCase(elTipo)) {
                try {
                    if ("".equalsIgnoreCase(elValor.trim()) || "N/A".equalsIgnoreCase(elValor.trim()))
                        elValor = "0";
                    float elDecimal = Float.parseFloat(elValor.replaceAll(",", "").trim());
                    if ("###00".equalsIgnoreCase(cFormato))
                        elValor = "" + elDecimal / 100 + "";
                    else
                        elValor = "" + elValor + "";
                } catch (NumberFormatException e) {
                    /*
					 * En algunos casos cuando se importa el CSV de excel
					 * reemplaza los nulos o ceros con el caracter "-". Si es el
					 * caso, asigna 0 al valor.
					 */
                    if ("-".equals(elValor))
                        elValor = "'0'";
                    else
                        return "El formato de la posición " + j + " debe ser un decimal";
                }
            } else {
                elValor = "'" + elValor + "'";
            }
            if ((String) arrDetalle.get(1) == "1") {
                elWhere = elWhere + tokenWhere + "[" + (String) arrDetalle.get(10) + "] = " + elValor + "";
                tokenWhere = " and ";
            } else {
                updCampos = updCampos + tokenUpdate + "[" + (String) arrDetalle.get(10) + "] = " + elValor + "";
                tokenUpdate = ", ";
            }
            insertCampos = insertCampos + tokenInsert + "[" + (String) arrDetalle.get(10) + "]";
            insertValores = insertValores + tokenInsert + "" + elValor + "";
            tokenInsert = ", ";
            laPos = laPos + laLong;
            j++;
            laCol++;
        }
        if ("".equalsIgnoreCase(elWhere))
            elRegreso = elRegreso + " insert into " + tablaDestino + " (" + insertCampos + ") values (" + insertValores + ")" + "\n";
        else
            elRegreso = elRegreso + "if exists(select 1 from " + tablaDestino + elWhere + " ) update " + tablaDestino + " set " + updCampos + elWhere + " else insert into " + tablaDestino + " (" + insertCampos + ") values (" + insertValores + ")" + "\n";
        return elRegreso;
    }

    public List<String> validaArchivo(String archivo, Caso c, Usuario usuario, int cLayout, String cNameFile, String aEjercicioFiscal, String uUR) throws Exception {
        // public List<String> validaArchivo(String archivo, Caso c, Usuario
        // usuario, int cLayout, String cNameFile, String aEjercicioFiscal)
        // throws Exception {
        Connection conn = null;
        // if (cLayout >= 9 && cLayout < 50) { //IRD 20131010 Mayor que nueve
        // son los layout en código duro de vicente los mayores a 50 son
        // dinámicos
        // ProcesaLayoutFurrt plf = new ProcesaLayoutFurrt();
        // return plf.procesaLayout(archivo, c, usuario, cLayout, cNameFile);
        // }
        ArrayList arrmMontosCalendario = new ArrayList();
        ArrayList arrLHeader = new ArrayList();
        ArrayList arrLDetalle = new ArrayList();
        ArrayList arrHeader = new ArrayList();
        ArrayList arrCamposDetalle = new ArrayList();
        // ArrayList arrCampoTipDatDetalle = new ArrayList();
        String cNameCampo = "";
        String cTipoDato = "";
        String linea = "";
        int sinError = 0;
        int maxErrores = 10;
        String elUpdate = "";
        int nFolio = -1;
        try {
            if (c == null) {
                nFolio = 0;
            } else
                nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            try {
                conn = getConnection();
                arrLHeader = ConfiguraAplicativoManager.layoutHeader(conn, cLayout);
                arrLDetalle = ConfiguraAplicativoManager.layoutDetalle(conn, cLayout);
                arrHeader = (ArrayList) arrLHeader.get(3);
            } catch (Exception exc) {
                log.error(exc);
                conn.rollback();
                throw new Exception(exc);
            } finally {
                if (conn != null) {
                    conn.close();
                }
                conn = null;
            }
            String elDelim = "|";
            String pattern = "############.##";
            DecimalFormat myFormatter = new DecimalFormat(pattern);
            String cFileExcel = "";
            String cLogReady = "";
            String cValor = "";
            double dValor = 0.0;
            int iDetalle = 0;
            int iValor = 0;
            int IterRegElx = 0;
            int i = 0;
            int j = 0;
            int nCelIgnore = 0;
            String cVisible = "";
            String cConstante = "";
            String cIgnore = "";
            String ctipolayout = (String) arrHeader.get(0);
            int itipolayout = Integer.valueOf(ctipolayout);
            int g = 0;
            while (cNameFile.length() < 40) {
                cNameFile = cNameFile + " ";
                g++;
            }
            int excluye = Integer.parseInt((String) arrHeader.get(12));
            conn = getConnection();
            String queryInicial = (String) arrHeader.get(7);
            queryInicial = queryInicial.replace("{?USUARIO}", usuario.getLogin()).replace("{?UUR}", uUR);
            if (!"".equalsIgnoreCase(queryInicial.trim())) {
                try {
                    ConfiguraAplicativoManager.actTablaLayout(conn, queryInicial);
                } catch (Exception exc) {
                    log.error(exc);
                    arrmMontosCalendario.add("Error en query inicial");
                    conn.rollback();
                    if (conn != null) {
                        conn.close();
                    }
                    conn = null;
                    throw new Exception(exc);
                } finally {
                }
            }
            if (itipolayout == 1) {
                System.out.println("es un archivo de EXCEL");
                cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";
                File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
                File fFileOrig = new File(archivo);
                if (!(c == null)) {
                    FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
                    FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
                    in.transferTo(0, fFileOrig.length(), out);
                    in.close();
                    out.close();
                }
                FileInputStream fileInputStream = new FileInputStream(cFileExcel);
                POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
                try {
                    HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
                    HSSFSheet hssfSheet = workBook.getSheetAt(0);
                    Iterator rowIterator = hssfSheet.rowIterator();
                    System.out.println("Inicia Lectura de Excel: " + new Timestamp(System.currentTimeMillis()));
                    cLogReady = "Inicia Lectura de Excel: " + new Timestamp(System.currentTimeMillis());
                    String cIniciaRow = (String) arrHeader.get(12);
                    int iIniciaRow = Integer.valueOf(cIniciaRow);
                    while (rowIterator.hasNext()) {
                        if (i <= excluye - 1) {
                            i++;
                            rowIterator.next();
                            continue;
                        }
                        j = 3;
                        IterRegElx++;
                        HSSFRow hssfRow = (HSSFRow) rowIterator.next();
                        Iterator iterator = hssfRow.cellIterator();
                        if (iIniciaRow <= i) {
                            List cellTempList = new ArrayList();
                            elDelim = "";
                            int numCol = 3;
                            while (numCol < arrLDetalle.size()) {
                                arrCamposDetalle = (ArrayList) arrLDetalle.get(numCol);
                                cNameCampo = (String) arrCamposDetalle.get(3);
                                cTipoDato = (String) arrCamposDetalle.get(5);
                                cVisible = (String) arrCamposDetalle.get(8);
                                cIgnore = (String) arrCamposDetalle.get(13);
                                cConstante = (String) arrCamposDetalle.get(9);
                                numCol++;
                                if ("0".equalsIgnoreCase(cVisible)) {
                                    // se consideran las colubnas
                                    nCelIgnore++;
                                    // ignoradas en medio ya que
                                    // falla al calcular el
                                    // total del arreglo contra
                                    // el total del celdas
                                    // lleidas
                                    HSSFCell hssfCell = null;
                                    if (iterator.hasNext()) {
                                        hssfCell = (HSSFCell) iterator.next();
                                    }
                                    if (!"".equalsIgnoreCase(cConstante)) {
                                        // cValor = cConstante;
                                        cValor = cConstante.replace("{?USUARIO}", usuario.getLogin()).replace("{?UUR}", uUR);
                                        linea += elDelim + cValor.trim();
                                    } else if ("nFolioFURRT".equals(cNameCampo)) {
                                        linea += elDelim + nFolio;
                                    } else if ("nDocRenglon".equals(cNameCampo)) {
                                        linea += elDelim + iDetalle;
                                    } else if ("cNameFile".equals(cNameCampo)) {
                                        linea += elDelim + cNameFile;
                                    } else if ("aEjercicioFiscal".equals(cNameCampo)) {
                                        linea += elDelim + aEjercicioFiscal;
                                    } else {
                                        linea += elDelim + "";
                                    }
                                } else {
                                    HSSFCell hssfCell = null;
                                    try {
                                        if (iterator.hasNext()) {
                                            hssfCell = (HSSFCell) iterator.next();
                                        }
                                        if ("date".equalsIgnoreCase(cTipoDato)) {
                                            // cValor =
                                            // hssfCell.getDateCellValue().toString();
                                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                                            Date c3 = hssfCell.getDateCellValue();
                                            // c3.setTime(sdf2.parse(fechUltBal));
                                            cValor = sdf2.format(c3.getTime());
                                        } else
                                            cValor = hssfCell.getStringCellValue();
                                    } catch (Exception exc) {
                                        try {
                                            cValor = new String(myFormatter.format(hssfCell.getNumericCellValue()));
                                        } catch (Exception exc2) {
                                            cValor = "";
                                        }
                                    }
                                    linea += elDelim + cValor.trim();
                                }
                                elDelim = "|";
                            }
                            /*
							 * while (iterator.hasNext()) { arrCamposDetalle =
							 * (ArrayList) arrLDetalle.get(j); cNameCampo =
							 * (String) arrCamposDetalle.get(3); cTipoDato =
							 * (String) arrCamposDetalle.get(5); cVisible =
							 * (String) arrCamposDetalle.get(8); cConstante =
							 * (String) arrCamposDetalle.get(9); j++; HSSFCell
							 * hssfCell = (HSSFCell) iterator.next(); if (
							 * "0".equalsIgnoreCase(cVisible)) { if (
							 * !"".equalsIgnoreCase(cConstante)) { cValor =
							 * cConstante; } } else { try { cValor =
							 * hssfCell.getStringCellValue(); } catch (Exception
							 * exc) { cValor = new
							 * String(myFormatter.format(hssfCell
							 * .getNumericCellValue())); }
							 * 
							 * } linea += elDelim + cValor.trim(); elDelim =
							 * "|"; }
							 */
                            iDetalle++;
                            // linea += elDelim + nFolio + elDelim + iDetalle +
                            // elDelim + cNameFile + elDelim + aEjercicioFiscal;
                            String tablaDestino = (String) arrHeader.get(2);
                            // el
                            String elRegreso = validaLinea(linea, arrLDetalle, tablaDestino, elDelim);
                            // 4
                            // son
                            // los
                            // que
                            // tienen
                            // que
                            // ir
                            // Folio,
                            // consecutivo
                            // del
                            // detalle,
                            // nombre
                            // del
                            // archivo,
                            // Ejercicio
                            // Fiscal
                            if ("/*OK*/".compareToIgnoreCase(elRegreso.substring(0, 6)) >= 0) {
                                elUpdate = elUpdate + elRegreso;
                                try {
                                    if (!"".equals(elRegreso)) {
                                        ConfiguraAplicativoManager.actTablaLayout(conn, elRegreso);
                                    }
                                } catch (Exception exc) {
                                    log.debug(elUpdate);
                                    log.error(exc);
                                    arrmMontosCalendario.add("Error en Renglon " + IterRegElx + " del archivo : '" + exc.getMessage() + "'");
                                    conn.rollback();
                                    if (conn != null) {
                                        conn.close();
                                    }
                                    conn = null;
                                    throw new Exception(exc);
                                } finally {
                                }
                            } else {
                                arrmMontosCalendario.add("Error en Renglon " + IterRegElx + " del archivo : '" + elRegreso + "'");
                                sinError++;
                                if (sinError > maxErrores) {
                                    break;
                                }
                            }
                            // System.out.println(linea);
                        }
                        i++;
                        linea = "";
                    }
                } catch (Exception exc) {
                    log.error(exc);
                    conn.rollback();
                    throw new Exception(exc);
                } finally {
                    fileInputStream.close();
                }
            } else {
                File fFileOrig = new File(archivo);
                BufferedReader entrada;
                int numLinea = 1;
                try {
                    entrada = new BufferedReader(new FileReader(fFileOrig));
                    while (entrada.ready()) {
                        linea = entrada.readLine();
                        if (numLinea <= excluye) {
                            numLinea++;
                            nCelIgnore++;
                            continue;
                        }
                        elDelim = (String) arrHeader.get(5);
                        String tablaDestino = (String) arrHeader.get(2);
                        String cFolio = "00000000" + nFolio + "";
                        String cnumLinea = "00000000" + numLinea + "";
                        cnumLinea = cnumLinea.substring(cnumLinea.length() - 8, cnumLinea.length());
                        cFolio = cFolio.substring(cFolio.length() - 8, cFolio.length());
                        linea = linea + elDelim + cFolio + elDelim + cnumLinea + elDelim + cNameFile + elDelim + aEjercicioFiscal;
                        String elRegreso = validaLinea(linea, arrLDetalle, tablaDestino, elDelim);
                        if ("/*OK*/".compareToIgnoreCase(elRegreso.substring(0, 6)) >= 0) {
                            elUpdate = elUpdate + elRegreso;
                            log.debug(elRegreso);
                        } else {
                            arrmMontosCalendario.add("Error en Renglon " + numLinea + " del archivo : '" + elRegreso + "'");
                            sinError++;
                            if (sinError > maxErrores) {
                                break;
                            }
                        }
                        numLinea++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new Exception(e);
                }
            }
            if (sinError == 0) {
                // conn = getConnection();
                try {
                    if (!"".equals(elUpdate)) {
                        // System.out.println(elUpdate);
                        // ConfiguraAplicativoManager.actTablaLayout(conn,
                        // elUpdate);
                        String queryFinal = (String) arrHeader.get(13);
                        queryFinal = queryFinal.replace("{?USUARIO}", usuario.getLogin()).replace("{?UUR}", uUR);
                        sinError++;
                        if (!"".equalsIgnoreCase(queryFinal)) {
                            CallableStatement cs1 = null;
                            cs1 = conn.prepareCall(queryFinal);
                            ResultSet rs = null;
                            // MLR
                            rs = cs1.executeQuery();
                            sinError = 0;
                            while (rs.next()) {
                                Saldo epSaldo = new Saldo();
                                arrmMontosCalendario.add(rs.getString("mensaje"));
                                sinError = 1;
                            }
                        }
                        if (sinError == 0)
                            conn.commit();
                        else
                            conn.rollback();
                    }
                } catch (Exception exc) {
                    log.error(exc);
                    conn.rollback();
                    throw new Exception(exc);
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
                    conn = null;
                }
            }
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
            log.error(e.getMessage(), e);
            arrmMontosCalendario.add("Ocurrio el siguiente error mientras se procesaba el archivo:" + e);
        }
        return arrmMontosCalendario;
    }

    private String[] parseCommaDelimited(String[] columnas) {
        List<String> elems = new ArrayList<String>();
        int i = 0;
        int j = 0;
        String val = "";
        while (i < columnas.length) {
            val = columnas[i];
            if (columnas[i].trim().startsWith("\"") && !columnas[i].trim().endsWith("\"")) {
                i++;
                val += columnas[i];
                while (!columnas[i].endsWith("\"") && i < columnas.length) {
                    val += columnas[i];
                    i++;
                }
            }
            elems.add(j, val);
            j++;
            i++;
            val = "";
        }
        return elems.toArray(new String[elems.size()]);
    }

    public String obtenUsuarioRemoto() throws Exception {
        String path = "Titulo no Definido";
        Connection conn = null;
        try {
            conn = getConnection();
            path = ConfiguraAplicativoManager.obtenUsuarioRemotoFurrt(conn);
            return path;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public String obtenPasswordRemoto() throws Exception {
        String path = "Titulo no Definido";
        Connection conn = null;
        try {
            conn = getConnection();
            path = ConfiguraAplicativoManager.obtenPasswordRemoto(conn);
            return path;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public String obtenDominioRemoto() throws Exception {
        String path = "Titulo no Definido";
        Connection conn = null;
        try {
            conn = getConnection();
            path = ConfiguraAplicativoManager.obtenDominioRemoto(conn);
            return path;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public String obtenRutaRemoto() throws Exception {
        String path = "Titulo no Definido";
        Connection conn = null;
        try {
            conn = getConnection();
            path = ConfiguraAplicativoManager.obtenRutaRemoto(conn);
            return path;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }
    /*
	 * public String obtenRutaArchivosFurrt() throws Exception { String path =
	 * "Titulo no Definido"; Connection conn = null; try { conn =
	 * getConnection(); path =
	 * ConfiguraAplicativoManager.obtenRutaArchivosFurrt(conn); return path; }
	 * finally { if (conn != null) { conn.close(); } conn = null; }
	 * 
	 * }
	 */
    /*
	 * public String replicaCataPresup(String aEjercicioFiscal, String
	 * cNewEjercicioFiscal, String cReplicaCatalogos, String cCierreAnual)
	 * throws Exception{ String cMensaje=""; Connection conn = null; try{ conn =
	 * getConnection();
	 * cMensaje=ConfiguraAplicativoManager.replicaCataPresup(conn,
	 * aEjercicioFiscal, cNewEjercicioFiscal, cReplicaCatalogos, cCierreAnual);
	 * conn.commit(); } catch (Exception exc) { log.error(exc); conn.rollback();
	 * //throw new Exception(exc); cMensaje = exc.getLocalizedMessage(); }
	 * finally{ if (conn != null){ conn.close(); } conn = null; } return
	 * cMensaje; }
	 */
}
