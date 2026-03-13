package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteConacManager {

	private static final Logger log = Logger.getLogger(ReporteConacManager.class);

	public static String generaReporteConacManager(Connection conn, int mesFin, int anioFin, Map<String, String> plantilla) throws Exception {

		CallableStatement cs = null;
		ResultSet rs = null;
		String query = "{call sp_FEConac( ? )}";

		String fileName = "";

		try {
			cs = conn.prepareCall(query);
			cs.setInt(1, mesFin);

			rs = cs.executeQuery();
			fileName = generaReporteFE(rs, plantilla.get("FEConac"), mesFin, anioFin);

			return fileName;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(cs, false);
		}

	}

	private static String generaReporteFE(ResultSet rs, String plantillaPath, int mesFin, int anioFin) throws Exception {
		File cFileExcelPlantilla = new File(plantillaPath);
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "REPORTE_FE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		fs.close();

		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		Workbook workbook = new HSSFWorkbook(fsArchivo);
		fsArchivo.close();

		Sheet sheet0 = workbook.getSheetAt(0);
		int cnt1 = 0;

		int mes = mesFin;
		String meses;

		meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
		meses = Util.firstUpper(meses);
		String periodo = "De Enero a " + meses + " de " + anioFin;

		Row rwEnc02 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
		Cell cell02 = (rwEnc02.getCell(0) == null ? rwEnc02.createCell(0) : rwEnc02.getCell(0));
		cell02.setCellValue(periodo);

		Row rwEnc01 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
		Cell cell01 = (rwEnc01.getCell(4) == null ? rwEnc01.createCell(4) : rwEnc01.getCell(4));
		cell01.setCellValue(anioFin);

		Row rwEnc03 = sheet0.getRow(4);
		Cell cell03 = (rwEnc03.getCell(5) == null ? rwEnc03.createCell(5) : rwEnc03.getCell(5));
		cell03.setCellValue(anioFin - 1);

		ResultSetMetaData rsMetadata = rs.getMetaData();
		int renglonInicio = 6;

		DataFormat df = workbook.createDataFormat();
		CellStyle estiloMoneda = workbook.createCellStyle();
		estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));

		while (rs.next()) {
			Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
			for (int i = 2; i < rsMetadata.getColumnCount(); i++) {
				Util.createExcelCellRep(i + 2, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);

			}
			cnt1++;
		}

		Row rwEnc1 = (sheet0.getRow(36) == null ? sheet0.createRow(36) : sheet0.getRow(36));
		Cell cell2 = rwEnc1.getCell(4);
		Cell cell3 = rwEnc1.getCell(5);
		cell2.setCellValue("");
		cell3.setCellValue("");

		Row rwEnc2 = (sheet0.getRow(47) == null ? sheet0.createRow(47) : sheet0.getRow(47));
		Cell cell4 = rwEnc2.getCell(4);
		Cell cell5 = rwEnc2.getCell(5);
		cell4.setCellValue("");
		cell5.setCellValue("");

		Row rwEnc3 = (sheet0.getRow(60) == null ? sheet0.createRow(60) : sheet0.getRow(60));
		Cell cell6 = rwEnc3.getCell(4);
		Cell cell7 = rwEnc3.getCell(5);
		cell6.setCellValue("");
		cell7.setCellValue("");

		Row rwEnc4 = (sheet0.getRow(62) == null ? sheet0.createRow(62) : sheet0.getRow(62));
		Cell cell8 = rwEnc4.getCell(4);
		Cell cell9 = rwEnc4.getCell(5);
		cell8.setCellValue("");
		cell9.setCellValue("");

		File fsalida = new File(file_name);
		FileOutputStream fos = new FileOutputStream(fsalida);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
		workbook.write(bos);
		workbook.close();

		/* Cierra Flujos */
		bos.flush();
		bos.close();
		fos.close();
		return file_name;
	}

	public static String generLibroBalanceFIEL(Connection conn, String centroContable, int mesInicio, int mesFin) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File reportPath;
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	nombreConciliacion, " );
		query.append( "			nombreMes AS nombreMesConciliacion,  " );
		query.append( "			rutaConciliacion " );
		query.append( "  FROM	vLibroBalanceFIEL WITH(NOLOCK) " );
		query.append( " WHERE	nMes >= ? " );
		query.append( "   AND	nMes <= ? " );
		query.append( "ORDER BY nMes, nIDEdoFinanciero");
		List<LibroBalancePagina> paginas = new ArrayList<LibroBalancePagina>();

		ps = conn.prepareStatement(query.toString());
		ps.setInt(1, mesInicio);
		ps.setInt(2, mesFin);

		rs = ps.executeQuery();
		while (rs.next()) {
			LibroBalancePagina pagina = new LibroBalancePagina();
			pagina.setNombreConciliacion(rs.getString("nombreConciliacion"));
			pagina.setNombreMesConciliacion(rs.getString("nombreMesConciliacion"));
			pagina.setRutaConciliacion(rs.getString("rutaConciliacion"));

			paginas.add(pagina);
		}

		if (paginas.size() == 0)
			throw new Exception("No se han cargado reportes para los meses seleccionados. ");

		reportPath = File.createTempFile("libroBalance", ".pdf", tempDir);
		String titulo = generaTitulo(conn, mesInicio, mesFin);
		generLibroBalance(titulo, paginas, reportPath);
		return reportPath.getAbsolutePath();

	}

	private static String generaTitulo(Connection conn, int mesInicio, int mesFinal) throws Exception {
		String titulo = "Comisión Nacional Forestal\n\n" + "Libro de Balances\n\n";
		String ef = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();

		if (mesInicio == mesFinal)
			titulo += "A " + Util.ultimoDiaMes(mesFinal) + " de " + Util.nombreDeMes(mesFinal) + " del " + ef;
		else
			titulo += "Al " + Util.ultimoDiaMes(mesFinal) + " de " + Util.nombreDeMes(mesFinal) + " del " + ef;

		return titulo;
	}

	public static void generLibroBalance(String titulo, List<LibroBalancePagina> paginas, File outputFile) throws IOException, DocumentException {
		OutputStream outputStream = null;
		try {
			Document document = new Document();
			outputStream = new FileOutputStream(outputFile);
			PdfCopy copy = new PdfCopy(document, outputStream);
			document.open();

			PdfReader readerTitulo = new PdfReader(ReporteConacManager.generDocumentoTitulo(titulo));
			copy.addPage(copy.getImportedPage(readerTitulo, 1));
			readerTitulo.close();
			readerTitulo = null;

			for (LibroBalancePagina pagina : paginas) {

				PdfReader reader = new PdfReader(new FileInputStream(pagina.getRutaConciliacion()));

				for (int i = 1; i <= reader.getNumberOfPages(); i++) {
					copy.newPage();
					copy.addPage(copy.getImportedPage(reader, i));
				}

				reader.close();
				reader = null;
			}
			
			outputStream.flush();
			document.close();
			outputStream.close();

		} finally {
			outputStream = null;
		}
	}

	public static byte[] generDocumentoTitulo(String titulo) throws IOException, DocumentException {

		OutputStream outputStream = null;

		outputStream = new ByteArrayOutputStream(512);
		Document document = new Document(PageSize.LETTER);
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();

		PdfContentByte cb = writer.getDirectContent();

		Paragraph preface = new Paragraph();
		preface.setAlignment(Element.ALIGN_CENTER);
		for (int renglon = 0; renglon < 20; renglon++)
			preface.add(new Paragraph(" "));
		Font font = FontFactory.getFont("ARIAL", 22, Font.BOLD);
		preface.add(new Paragraph(titulo, font));

		document.add(preface);
		outputStream.flush();
		document.close();
		outputStream.close();

		return ((ByteArrayOutputStream) outputStream).toByteArray();
	}

}
