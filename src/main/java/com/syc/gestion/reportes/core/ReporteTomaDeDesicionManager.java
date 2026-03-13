package com.syc.gestion.reportes.core;
/***************************Version 1.0 *****************************************************/
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteTomaDeDesicionManager {

	private static final Logger	log	= Logger.getLogger(ReporteTomaDeDesicionManager.class);

	/**
	 * Congela la informacion presupuestal necesaria para la generacion de
	 * reportes.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @return Numero de registros afectados.
	 * @throws Exception
	 */
	public static int congelaInformacion(Connection conn) throws Exception {
		log.debug("Iniciando congelamiento");
		CallableStatement cs = null;

		try {
			cs = conn.prepareCall("{call fn_congela_informacion ?}");
			cs.registerOutParameter(1, Types.INTEGER);

			cs.execute();
			int r = 0;
			r = cs.getInt(1);
			return r;
		} finally {
			CloseObject.closeObject(cs, false);
			log.debug("Congelamiento Finalizado");
		}
	}

	/**
	 * Genera un archivo excel con la informacion congelada.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param nombreArchivo
	 *            Nombre del archivo a generar
	 * @return Referencia al archivo creado.
	 */
	public static File generaArchivoInformacionCongelada(Connection conn, String nombreArchivo) throws Exception {
		Statement stmnt = null;
		ResultSet rs = null;
		String query = "SELECT cDescripcion_Cuenta, cEP, cRamo_EP, cUnidad_Responsable_EP, cGrupo_Funcional, cFuncion, cSubFuncion, cPrograma_General, cActividad_Institucional, cPrograma_Presupuestario, cPartida, cTipo_Gasto, cFuente_Financiamiento, cEntidad_Federativa, cCartera, cUnidadNorativa, cUnidadEjecutora, mMonto_Anual, mMonto_Enero, mMonto_Febrero, mMonto_Marzo, mMonto_Abril, mMonto_Mayo, mMonto_Junio, mMonto_Julio, mMonto_Agosto, mMonto_Septiembre, mMonto_Octubre, mMonto_Noviembre, mMonto_Diciembre FROM tSaldo_Congelado";
		try {
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);
			return Util.ExcelFromRS(rs, nombreArchivo);
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(stmnt, false);

		}
	}

	/**
	 * Genera un archivo CSV con la informacion congelada.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param nombreArchivo
	 *            Nombre del archivo a generar
	 * @return Referencia al archivo creado.
	 */
	public static File generaArchivoCSVInformacionCongelada(Connection conn, String nombreArchivo) throws Exception {
		Statement stmnt = null;
		ResultSet rs = null;
		String query = "SELECT cDescripcion_Cuenta, cEP, cRamo_EP, cUnidad_Responsable_EP, cGrupo_Funcional, cFuncion, cSubFuncion, cPrograma_General, cActividad_Institucional, cPrograma_Presupuestario, cPartida, cTipo_Gasto, cFuente_Financiamiento, cEntidad_Federativa, cCartera, cUnidadNorativa, cUnidadEjecutora, mMonto_Anual, mMonto_Enero, mMonto_Febrero, mMonto_Marzo, mMonto_Abril, mMonto_Mayo, mMonto_Junio, mMonto_Julio, mMonto_Agosto, mMonto_Septiembre, mMonto_Octubre, mMonto_Noviembre, mMonto_Diciembre FROM tSaldo_Congelado";
		File f = null;
		try {
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);
			f = new File(nombreArchivo);
			Util.CSVFromResultSet(f, rs);
			return f;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(stmnt, false);

		}
	}

	/**
	 * Genera el encabezado del reporte en excel, para el reporte de subfuncion
	 * / programa
	 * 
	 * @param wb
	 *            Workbook de excel abierto
	 * @param hs
	 *            Hoja abierta
	 * @param mesCorte
	 *            Mes de corte
	 * @param momentosPresupuestales
	 *            Momentos presupuestales a considerar en el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @return Numero de filas insertadas
	 * @throws Exception
	 */
	public static int generaEncabezadoReporteSubfuncion(HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] momentosPresupuestales, int ejercicioFiscal) throws Exception {
		Map<String, String> cols = new HashMap<String, String>();
		cols.put("ORIGINAL", "Original");
		cols.put("MODIFICADO", "Modificado");
		cols.put("EJERCIDO_PAGADO", "Ejercido");
		cols.put("COMPROMETIDO", "Comprometido");
		cols.put("DEVENGADO", "Devengado");
		cols.put("DISPONIBLE_NETO", "Disponible");
		cols.put("PRECOMPROMETIDO", "Precomprometido");
		cols.put("APARTADO", "Apartado");

		CellStyle style = wb.createCellStyle();
		CellStyle styleTopBorder = wb.createCellStyle();
		CellStyle styleMerged = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 12);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteNomal = wb.createFont();
		fuenteNomal.setFontHeightInPoints((short) 11);
		fuenteNomal.setFontName("Calibri");
		fuenteNomal.setColor(IndexedColors.BLACK.getIndex());
		fuenteNomal.setBold(true);
		fuenteNomal.setItalic(false);

		style.setFont(fuenteEncabezado);
		log.info("Generando cuerpo de reporte al Mes: " + mesCorte + " #Momentos: " + momentosPresupuestales.length);
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);

		int rows = 0;
		Calendar c = new GregorianCalendar();
		createSimpleRow(hs, rows, 0, style, "Avance presupuestal al " + c.get(Calendar.DAY_OF_MONTH) + " de " + Util.NOMBRE_MESES_MX[c.get(Calendar.MONTH)] + " del " + String.valueOf(ejercicioFiscal), rows, 0, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 0, style, "", rows, 0, rows, (4 + momentosPresupuestales.length));

		style.setFont(fuenteNomal);
		style.setAlignment(HorizontalAlignment.CENTER);

		styleTopBorder.setFont(fuenteNomal);
		styleTopBorder.setBorderTop(BorderStyle.THIN);
		styleTopBorder.setAlignment(HorizontalAlignment.CENTER);

		styleMerged.setVerticalAlignment(VerticalAlignment.CENTER);
		styleMerged.setFont(fuenteEncabezado);
		styleMerged.setAlignment(HorizontalAlignment.CENTER);

		rows++;
		createSimpleRow(hs, rows, 0, styleMerged, "Programas Presupuestarios", rows, 0, rows + 1, 0);
		createSimpleRow(hs, rows, 1, style, "Anual", rows, 1, rows, 2);
		createSimpleRow(hs, rows, 3, style, "");
		createSimpleRow(hs, rows, 4, style, "A " + mesCorte.toLowerCase(), rows, 4, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 1, styleTopBorder, "Original");
		createSimpleRow(hs, rows, 2, styleTopBorder, "Modificado");
		createSimpleRow(hs, rows, 3, style, "");

		int celda = 4;
		for (int i = 0; i < momentosOrdenados.size(); i++) {
			if (momentosOrdenados.get(i) != null & !"".equals(momentosOrdenados.get(i))) {
				createSimpleRow(hs, rows, celda, styleTopBorder, cols.get(momentosOrdenados.get(i)) == null ? momentosOrdenados.get(i) : cols.get(momentosOrdenados.get(i)));
				celda++;
			}
		}
		rows++;
		return rows;
	}

	/**
	 * Genera el encabezado del reporte en excel, para el reporte de subfuncion
	 * / entidad federativa.
	 * 
	 * @param Workbook
	 *            de excel abierto
	 * @param hs
	 *            Hoja abierta
	 * @param mesCorte
	 *            Mes de corte
	 * @param momentosPresupuestales
	 *            Momentos presupuestales a considerar en el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @return Numero de filas insertadas
	 * @throws Exception
	 */
	public static int generaEncabezadoReporteEF(HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] momentosPresupuestales, int ejercicioFiscal) throws Exception {

		Map<String, String> cols = new HashMap<String, String>();
		cols.put("ORIGINAL", "Original");
		cols.put("MODIFICADO", "Modificado");
		cols.put("EJERCIDO_PAGADO", "Ejercido");
		cols.put("COMPROMETIDO", "Comprometido");
		cols.put("DEVENGADO", "Devengado");
		cols.put("DISPONIBLE_NETO", "Disponible");
		cols.put("PRECOMPROMETIDO", "Precomprometido");
		cols.put("APARTADO", "Apartado");

		CellStyle style = wb.createCellStyle();
		CellStyle styleTopBorder = wb.createCellStyle();
		CellStyle styleMerged = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 12);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteNomal = wb.createFont();
		fuenteNomal.setFontHeightInPoints((short) 11);
		fuenteNomal.setFontName("Calibri");
		fuenteNomal.setColor(IndexedColors.BLACK.getIndex());
		fuenteNomal.setBold(true);
		fuenteNomal.setItalic(false);

		style.setFont(fuenteEncabezado);
		log.info("Generando cuerpo de reporte al Mes: " + mesCorte + " #Momentos: " + momentosPresupuestales.length);
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);

		int rows = 0;
		Calendar c = new GregorianCalendar();
		createSimpleRow(hs, rows, 0, style, "Avance presupuestal al " + c.get(Calendar.DAY_OF_MONTH) + " de " + Util.NOMBRE_MESES_MX[c.get(Calendar.MONTH)] + " del " + String.valueOf(ejercicioFiscal), rows, 0, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 0, style, "", rows, 0, rows, (4 + momentosPresupuestales.length));

		style.setFont(fuenteNomal);
		style.setAlignment(HorizontalAlignment.CENTER);

		styleTopBorder.setFont(fuenteNomal);
		styleTopBorder.setBorderTop(BorderStyle.THIN);
		styleTopBorder.setAlignment(HorizontalAlignment.CENTER);

		styleMerged.setVerticalAlignment(VerticalAlignment.CENTER);
		styleMerged.setFont(fuenteEncabezado);
		styleMerged.setAlignment(HorizontalAlignment.CENTER);

		rows++;
		createSimpleRow(hs, rows, 0, styleMerged, "Programas Presupuestarios", rows, 0, rows + 1, 0);
		createSimpleRow(hs, rows, 1, style, "Anual", rows, 1, rows, 2);
		createSimpleRow(hs, rows, 3, style, "");
		createSimpleRow(hs, rows, 4, style, "A " + mesCorte.toLowerCase(), rows, 4, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 1, styleTopBorder, "Original");
		createSimpleRow(hs, rows, 2, styleTopBorder, "Modificado");
		createSimpleRow(hs, rows, 3, style, "");

		int celda = 4;
		for (int i = 0; i < momentosOrdenados.size(); i++) {
			if (momentosOrdenados.get(i) != null & !"".equals(momentosOrdenados.get(i))) {
				createSimpleRow(hs, rows, celda, styleTopBorder, cols.get(momentosOrdenados.get(i)) == null ? momentosOrdenados.get(i) : cols.get(momentosOrdenados.get(i)));
				celda++;
			}
		}
		rows++;
		return rows;
	}

	/**
	 * Genera el encabezado del reporte en excel, para el reporte por Unidad
	 * Ejecutora.
	 * 
	 * @param wb
	 *            Workbook de excel abierto
	 * @param hs
	 *            Hoja abierta
	 * @param mesCorte
	 *            Mes de corte
	 * @param momentosPresupuestales
	 *            Momentos presupuestales a considerar en el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @return Numero de filas insertadas
	 * @throws Exception
	 * */
	public static int generaEncabezadoReporteEstructuraEconomica(HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] momentosPresupuestales, int ejercicioFiscal) throws Exception {

		/* Estilo para el encabezado. */
		Map<String, String> cols = new HashMap<String, String>();
		cols.put("ORIGINAL", "Original");
		cols.put("MODIFICADO", "Modificado");
		cols.put("EJERCIDO_PAGADO", "Ejercido");
		cols.put("COMPROMETIDO", "Comprometido");
		cols.put("DEVENGADO", "Devengado");
		cols.put("DISPONIBLE_NETO", "Disponible");
		cols.put("PRECOMPROMETIDO", "Precomprometido");
		cols.put("APARTADO", "Apartado");
		log.info("Generando cuerpo de reporte al Mes: " + mesCorte + " #Momentos: " + momentosPresupuestales.length);
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);

		CellStyle style = wb.createCellStyle();
		CellStyle styleTopBorder = wb.createCellStyle();
		CellStyle styleMerged = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 12);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteNomal = wb.createFont();
		fuenteNomal.setFontHeightInPoints((short) 11);
		fuenteNomal.setFontName("Calibri");
		fuenteNomal.setColor(IndexedColors.BLACK.getIndex());
		fuenteNomal.setBold(true);
		fuenteNomal.setItalic(false);

		style.setFont(fuenteEncabezado);

		Calendar c = new GregorianCalendar();

		int rows = 0;
		rows++;
		createSimpleRow(hs, rows, 0, style, "Avance presupuestal al " + c.get(Calendar.DATE) + " de " + Util.NOMBRE_MESES_MX[c.get(Calendar.MONTH)] + " del " + String.valueOf(ejercicioFiscal), rows, 0, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 0, style, "Pesos", rows, 0, rows, (4 + momentosPresupuestales.length));

		style.setFont(fuenteNomal);
		style.setAlignment(HorizontalAlignment.CENTER);

		styleTopBorder.setFont(fuenteNomal);
		styleTopBorder.setBorderTop(BorderStyle.THIN);
		styleTopBorder.setAlignment(HorizontalAlignment.CENTER);

		styleMerged.setVerticalAlignment(VerticalAlignment.CENTER);
		styleMerged.setFont(fuenteEncabezado);
		styleMerged.setAlignment(HorizontalAlignment.CENTER);

		rows++;
		createSimpleRow(hs, rows, 0, styleMerged, "Clasificaci\u00F3n Econ\u00F3mica", rows, 0, rows + 1, 0);
		createSimpleRow(hs, rows, 1, style, "Anual", rows, 1, rows, 2);
		createSimpleRow(hs, rows, 3, style, "");
		createSimpleRow(hs, rows, 4, style, "A " + mesCorte.toLowerCase(), rows, 4, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 1, styleTopBorder, "Original");
		createSimpleRow(hs, rows, 2, styleTopBorder, "Modificado");
		createSimpleRow(hs, rows, 3, style, "");

		int celda = 4;
		for (int i = 0; i < momentosOrdenados.size(); i++) {
			if (momentosOrdenados.get(i) != null & !"".equals(momentosOrdenados.get(i))) {
				createSimpleRow(hs, rows, celda, styleTopBorder, cols.get(momentosOrdenados.get(i)) == null ? momentosOrdenados.get(i) : cols.get(momentosOrdenados.get(i)));
				celda++;
			}
		}

		return rows;

	}

	/**
	 * Crea una celda del excel aplicando el formato.
	 * 
	 * @param hs
	 *            Hoja abierta
	 * @param rowIndex
	 *            Indice de la columna en la que se creara la celda. Si no
	 *            existe se crea primero la fila.
	 * @param cellIndex
	 *            Indice que ocupara la celda en la fila
	 * @param style
	 *            Estilo a aplicar.
	 * @param value
	 *            Valor que contiene la celda.
	 * @param rowOrigen
	 *            Origen para la seccion. Debe ser mayor a cero
	 * @param cellOrigen
	 *            Origen de la celda para la seleccion. Debe ser mayor a cero.
	 * @param rowFinal
	 *            Fin de la fila para la seleccion. Debe ser mayor a cero.
	 * @param cellFinal
	 *            fin de la celda para la seleccion. Debe ser mayor a cero.
	 * @return Fila con la celda creada.
	 */
	private static HSSFRow createSimpleRow(HSSFSheet hs, int rowIndex, int cellIndex, CellStyle style, String value, int rowOrigen, int cellOrigen, int rowFinal, int cellFinal) {
		HSSFRow rw = hs.getRow(rowIndex);
		if (rw == null) {
			rw = hs.createRow(rowIndex);
		}

		HSSFCell cell = rw.createCell(cellIndex);
		try {
			cell.setCellValue(Double.parseDouble(value));
		} catch (Exception e) {
			cell.setCellValue(value);
		}
		if (style != null)
			cell.setCellStyle(style);

		if (rowOrigen >= 0 && cellOrigen >= 0 && rowFinal >= 0 && cellFinal >= 0)
			hs.addMergedRegion(new CellRangeAddress((short) rowOrigen, (short) cellOrigen, (short) rowFinal, (short) cellFinal));
		return rw;
	}

	/**
	 * Crea una celda del excel aplicando el formato.
	 * 
	 * @param hs
	 *            Hoja abierta
	 * @param rowIndex
	 *            Indice de la columna en la que se creara la celda. Si no
	 *            existe se crea primero la fila.
	 * @param cellIndex
	 *            Indice que ocupara la celda en la fila
	 * @param style
	 *            Estilo a aplicar.
	 * @param value
	 *            Valor que contiene la celda.
	 * @return Fila con la celda creada.
	 */
	private static HSSFRow createSimpleRow(HSSFSheet hs, int rowIndex, int cellIndex, CellStyle style, String value) {
		return createSimpleRow(hs, rowIndex, cellIndex, style, value, -1, -1, -1, -1);
	}

	/**
	 * Genera el cuerpo (Detalle) del reporte de Subfuncion/Programa llamando al
	 * stored procedure sp_reporte_funcion_programa.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param wb
	 *            Libro excel abierto
	 * @param hs
	 *            Hoja activa
	 * @param mesCorte
	 *            Mes de corte
	 * @param capitulos
	 *            Capitulos que contiene el reporte
	 * @param momentosPresupuestales
	 *            Momentos Presupuestales que contiene el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @param filaInicio
	 *            Fila en la que inica el detalle del reporte
	 * @return Numero de filas insertadas
	 * @throws Exception
	 */
	public static int generaCuerpoReporteSubfuncion(Connection conn, HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] capitulos, String[] momentosPresupuestales, int ejercicioFiscal, int filaInicio) throws Exception {
		CallableStatement cs = null;
		ResultSet rs = null;
		String query = "{call dbo.sp_reporte_funcion_programa(?, ?, ?)}";

		int nMes = Util.numeroDeMes(mesCorte);
		String capitulosStr = Util.join(capitulos, ',').replaceAll("4000", "4000,4300");
		String momentosStr = Util.join(momentosPresupuestales, ',');
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);
		
		log.info("Capitulos: " + capitulosStr);
		log.info("Momentos: " + momentosStr);

		log.info("{call dbo.sp_reporte_funcion_programa(?, ?, ?)}");
		log.info("1[" + capitulosStr + "]");
		log.info("2[" + momentosStr + "]");
		log.info("3[" + nMes + "]");

		CellStyle style = wb.createCellStyle();
		CellStyle styleNormal = wb.createCellStyle();
		CellStyle styleCurrency = wb.createCellStyle();
		CellStyle styleCurrencyEnc = wb.createCellStyle();
		CellStyle styleCurrencySubTotalEnc = wb.createCellStyle();
		CellStyle styleSubTotalEnc = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 11);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteMontos = wb.createFont();
		fuenteMontos.setFontHeightInPoints((short) 11);
		fuenteMontos.setFontName("Calibri");
		fuenteMontos.setColor(IndexedColors.BLACK.getIndex());
		fuenteMontos.setBold(false);
		fuenteMontos.setItalic(false);

		style.setFont(fuenteEncabezado);
		styleNormal.setFont(fuenteMontos);

		styleCurrency.setFont(fuenteMontos);
		styleCurrency.setDataFormat((short) 8);

		styleCurrencyEnc.setFont(fuenteEncabezado);
		styleCurrencyEnc.setDataFormat((short) 8);

		styleCurrencySubTotalEnc.setFont(fuenteEncabezado);
		styleCurrencySubTotalEnc.setDataFormat((short) 8);
		styleCurrencySubTotalEnc.setBorderBottom(BorderStyle.THIN);

		styleSubTotalEnc.setFont(fuenteEncabezado);
		styleSubTotalEnc.setBorderBottom(BorderStyle.THIN);
		Map<String, CellStyle> estilo = new HashMap<String, CellStyle>();
		estilo.put("TOTAL_TEXTO", style);
		estilo.put("SUBTOTAL_TEXTO", styleSubTotalEnc);
		estilo.put("DETALLE_TEXTO", styleNormal);
		estilo.put("DETALLE_CURRENCY", styleCurrency);
		estilo.put("TOTAL_CURRENCY", styleCurrencyEnc);
		estilo.put("SUBTOTAL_CURRENCY", styleCurrencySubTotalEnc);

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, capitulosStr);
			cs.setString(2, momentosStr);
			cs.setInt(3, nMes);

			rs = cs.executeQuery();
			String encAnual[] = { "original_anual", "modificado_anual" };

			while (rs.next()) {
				int celda = 0;
				filaInicio++;
				createSimpleRow(hs, filaInicio, celda, estilo.get(rs.getString("estilo").toUpperCase() + "_TEXTO"), rs.getString("clasificacion"));
				celda++;
				for (int i = 0; i < encAnual.length; i++) {
					createSimpleRow(hs, filaInicio, i + 1, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(encAnual[i]));
					celda++;
				}
				celda++;
				for (int i = 0; i < momentosOrdenados.size(); i++) {
					String col = momentosOrdenados.get(i);
					if ("DISPONIBLE_NETO".equals(momentosOrdenados.get(i)))
						col = "DISP_NETO";

					createSimpleRow(hs, filaInicio, i + celda, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(col + "_mes"));
				}

			}
		} finally {
			CloseObject.closeObject(cs, false);
			CloseObject.closeObject(rs, false);
		}
		return filaInicio;

	}

	/**
	 * Genera el cuerpo (Detalle) del reporte de Subfuncion/Entidad Federativa
	 * llamando al stored procedure sp_reporte_funcion_ef.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param wb
	 *            Libro excel abierto
	 * @param hs
	 *            Hoja activa
	 * @param mesCorte
	 *            Mes de corte
	 * @param capitulos
	 *            Capitulos que contiene el reporte
	 * @param momentosPresupuestales
	 *            Momentos Presupuestales que contiene el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @param filaInicio
	 *            Fila en la que inica el detalle del reporte
	 * @return Numero de filas insertadas
	 * @throws Exception
	 */
	public static int generaCuerpoReporteEntidadFederativa(Connection conn, HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] capitulos, String[] momentosPresupuestales, int ejercicioFiscal, int filaInicio) throws Exception {
		CallableStatement cs = null;
		ResultSet rs = null;
		String query = "{call dbo.sp_reporte_funcion_ef(?, ?, ?)}";
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);
		
		int nMes = Util.numeroDeMes(mesCorte);
		String capitulosStr = Util.join(capitulos, ',').replaceAll("4000", "4000,4300");
		String momentosStr = Util.join(momentosPresupuestales, ',');

		log.info("Capitulos: " + capitulosStr);
		log.info("Momentos: " + momentosStr);

		log.info("{call dbo.sp_reporte_funcion_programa(?, ?, ?)}");
		log.info("1[" + capitulosStr + "]");
		log.info("2[" + momentosStr + "]");
		log.info("3[" + nMes + "]");

		CellStyle style = wb.createCellStyle();
		CellStyle styleNormal = wb.createCellStyle();
		CellStyle styleCurrency = wb.createCellStyle();
		CellStyle styleCurrencyEnc = wb.createCellStyle();
		CellStyle styleCurrencySubTotalEnc = wb.createCellStyle();
		CellStyle styleSubTotalEnc = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 11);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteMontos = wb.createFont();
		fuenteMontos.setFontHeightInPoints((short) 11);
		fuenteMontos.setFontName("Calibri");
		fuenteMontos.setColor(IndexedColors.BLACK.getIndex());
		fuenteMontos.setBold(false);
		fuenteMontos.setItalic(false);

		style.setFont(fuenteEncabezado);
		styleNormal.setFont(fuenteMontos);

		styleCurrency.setFont(fuenteMontos);
		styleCurrency.setDataFormat((short) 8);

		styleCurrencyEnc.setFont(fuenteEncabezado);
		styleCurrencyEnc.setDataFormat((short) 8);

		styleCurrencySubTotalEnc.setFont(fuenteEncabezado);
		styleCurrencySubTotalEnc.setDataFormat((short) 8);
		styleCurrencySubTotalEnc.setBorderBottom(BorderStyle.THIN);

		styleSubTotalEnc.setFont(fuenteEncabezado);
		styleSubTotalEnc.setBorderBottom(BorderStyle.THIN);
		Map<String, CellStyle> estilo = new HashMap<String, CellStyle>();
		estilo.put("TOTAL_TEXTO", style);
		estilo.put("SUBTOTAL_TEXTO", styleSubTotalEnc);
		estilo.put("DETALLE_TEXTO", styleNormal);
		estilo.put("DETALLE_CURRENCY", styleCurrency);
		estilo.put("TOTAL_CURRENCY", styleCurrencyEnc);
		estilo.put("SUBTOTAL_CURRENCY", styleCurrencySubTotalEnc);

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, capitulosStr);
			cs.setString(2, momentosStr);
			cs.setInt(3, nMes);

			rs = cs.executeQuery();
			String encAnual[] = { "original_anual", "modificado_anual" };

			while (rs.next()) {
				int celda = 0;
				filaInicio++;
				createSimpleRow(hs, filaInicio, celda, estilo.get(rs.getString("estilo").toUpperCase() + "_TEXTO"), rs.getString("clasificacion"));
				celda++;
				for (int i = 0; i < encAnual.length; i++) {
					createSimpleRow(hs, filaInicio, i + 1, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(encAnual[i]));
					celda++;
				}
				celda++;
				for (int i = 0; i < momentosOrdenados.size(); i++) {
					String col = momentosOrdenados.get(i);
					if ("DISPONIBLE_NETO".equals(momentosOrdenados.get(i)))
						col = "DISP_NETO";

					createSimpleRow(hs, filaInicio, i + celda, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(col + "_mes"));
				}

			}
		} finally {
			CloseObject.closeObject(cs, false);
			CloseObject.closeObject(rs, false);
		}
		return filaInicio;

	}

	/**
	 * Genera el cuerpo (Detalle) del reporte de Estructura economica.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param wb
	 *            Libro excel abierto
	 * @param hs
	 *            Hoja activa
	 * @param mesCorte
	 *            Mes de corte
	 * @param capitulos
	 *            Capitulos que contiene el reporte
	 * @param momentosPresupuestales
	 *            Momentos Presupuestales que contiene el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @param filaInicio
	 *            Fila en la que inica el detalle del reporte
	 * @return Numero de filas insertadas
	 * @throws Exception
	 */
	public static int generaCuerpoReporteEstructuraEconomica(Connection conn, HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] capitulos, String[] momentosPresupuestales, int ejercicioFiscal, int filaInicio) throws Exception {

		CallableStatement cs = null;
		ResultSet rs = null;
		String query = "{call dbo.sp_reporte_clasificacion_economica(?, ?, ?)}";
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);
		
		int nMes = Util.numeroDeMes(mesCorte);
		String capitulosStr = Util.join(capitulos, ',').replaceAll("4000", "4000,4300");
		String momentosStr = Util.join(momentosPresupuestales, ',');

		log.info("Capitulos: " + capitulosStr);
		log.info("Momentos: " + momentosStr);

		log.info("{call dbo.sp_reporte_clasificacion_economica(?, ?, ?)}");
		log.info("1[" + capitulosStr + "]");
		log.info("2[" + momentosStr + "]");
		log.info("3[" + nMes + "]");

		CellStyle style = wb.createCellStyle();
		CellStyle styleNormal = wb.createCellStyle();
		CellStyle styleCurrency = wb.createCellStyle();
		CellStyle styleCurrencyEnc = wb.createCellStyle();
		CellStyle styleCurrencySubTotalEnc = wb.createCellStyle();
		CellStyle styleSubTotalEnc = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 11);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteMontos = wb.createFont();
		fuenteMontos.setFontHeightInPoints((short) 11);
		fuenteMontos.setFontName("Calibri");
		fuenteMontos.setColor(IndexedColors.BLACK.getIndex());
		fuenteMontos.setBold(false);
		fuenteMontos.setItalic(false);

		style.setFont(fuenteEncabezado);
		styleNormal.setFont(fuenteMontos);

		styleCurrency.setFont(fuenteMontos);
		styleCurrency.setDataFormat((short) 8);

		styleCurrencyEnc.setFont(fuenteEncabezado);
		styleCurrencyEnc.setDataFormat((short) 8);

		styleCurrencySubTotalEnc.setFont(fuenteEncabezado);
		styleCurrencySubTotalEnc.setDataFormat((short) 8);
		styleCurrencySubTotalEnc.setBorderBottom(BorderStyle.THIN);

		styleSubTotalEnc.setFont(fuenteEncabezado);
		styleSubTotalEnc.setBorderBottom(BorderStyle.THIN);
		Map<String, CellStyle> estilo = new HashMap<String, CellStyle>();
		estilo.put("TOTAL_TEXTO", style);
		estilo.put("SUBTOTAL_TEXTO", styleSubTotalEnc);
		estilo.put("DETALLE_TEXTO", styleNormal);
		estilo.put("DETALLE_CURRENCY", styleCurrency);
		estilo.put("TOTAL_CURRENCY", styleCurrencyEnc);
		estilo.put("SUBTOTAL_CURRENCY", styleCurrencySubTotalEnc);

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, capitulosStr);
			cs.setString(2, momentosStr);
			cs.setInt(3, nMes);

			rs = cs.executeQuery();
			String encAnual[] = { "original_anual", "modificado_anual" };

			while (rs.next()) {
				int celda = 0;
				filaInicio++;
				createSimpleRow(hs, filaInicio, celda, estilo.get(rs.getString("estilo").toUpperCase() + "_TEXTO"), rs.getString("clasificacion"));
				celda++;
				for (int i = 0; i < encAnual.length; i++) {
					createSimpleRow(hs, filaInicio, i + 1, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(encAnual[i]));
					celda++;
				}
				celda++;
				for (int i = 0; i < momentosOrdenados.size(); i++) {
					String col = momentosOrdenados.get(i);
					if ("DISPONIBLE_NETO".equals(momentosOrdenados.get(i)))
						col = "DISP_NETO";

					createSimpleRow(hs, filaInicio, i + celda, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(col + "_mes"));
				}

			}
		} finally {
			CloseObject.closeObject(cs, false);
			CloseObject.closeObject(rs, false);
		}
		return filaInicio;

	}

	/**
	 * Genera el cuerpo del reporte por Unidad Ejecutora ejecutando el Stored
	 * Procedure [sp_reporte_unidad_ejecutora]
	 * 
	 * @param wb
	 * @param hs
	 * @param mesCorte
	 * @param momentosPresupuestales
	 * @param ejercicioFiscal
	 * @return
	 * @throws Exception
	 */
	public static int generaEncabezadoReporteUE(HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] momentosPresupuestales, int ejercicioFiscal) throws Exception {
		Map<String, String> cols = new HashMap<String, String>();
		cols.put("ORIGINAL", "Original");
		cols.put("MODIFICADO", "Modificado");
		cols.put("EJERCIDO_PAGADO", "Ejercido");
		cols.put("COMPROMETIDO", "Comprometido");
		cols.put("DEVENGADO", "Devengado");
		cols.put("DISPONIBLE_NETO", "Disponible");
		cols.put("PRECOMPROMETIDO", "Precomprometido");
		cols.put("APARTADO", "Apartado");
		CellStyle style = wb.createCellStyle();
		CellStyle styleTopBorder = wb.createCellStyle();
		CellStyle styleMerged = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 12);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteNomal = wb.createFont();
		fuenteNomal.setFontHeightInPoints((short) 11);
		fuenteNomal.setFontName("Calibri");
		fuenteNomal.setColor(IndexedColors.BLACK.getIndex());
		fuenteNomal.setBold(true);
		fuenteNomal.setItalic(false);

		style.setFont(fuenteEncabezado);
		Calendar c = new GregorianCalendar();
		int rows = 0;
		createSimpleRow(hs, rows, 0, style, "Avance presupuestal al " + c.get(Calendar.DAY_OF_MONTH) + " de " + Util.NOMBRE_MESES_MX[c.get(Calendar.MONTH)] + " del " + String.valueOf(ejercicioFiscal), rows, 0, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 0, style, "", rows, 0, rows, (4 + momentosPresupuestales.length));

		style.setFont(fuenteNomal);
		style.setAlignment(HorizontalAlignment.CENTER);

		styleTopBorder.setFont(fuenteNomal);
		styleTopBorder.setBorderTop(BorderStyle.THIN);
		styleTopBorder.setAlignment(HorizontalAlignment.CENTER);

		styleMerged.setVerticalAlignment(VerticalAlignment.CENTER);
		styleMerged.setFont(fuenteEncabezado);
		styleMerged.setAlignment(HorizontalAlignment.CENTER);

		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);
		rows++;

		createSimpleRow(hs, rows, 0, styleMerged, "Programas Presupuestarios", rows, 0, rows + 1, 0);
		createSimpleRow(hs, rows, 1, style, "Anual", rows, 1, rows, 2);
		createSimpleRow(hs, rows, 3, style, "");
		createSimpleRow(hs, rows, 4, style, "A " + mesCorte.toLowerCase(), rows, 4, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 1, styleTopBorder, "Original");
		createSimpleRow(hs, rows, 2, styleTopBorder, "Modificado");
		createSimpleRow(hs, rows, 3, style, "");

		int celda = 4;
		for (int i = 0; i < momentosOrdenados.size(); i++) {
			if (momentosOrdenados.get(i) != null & !"".equals(momentosOrdenados.get(i))) {
				createSimpleRow(hs, rows, celda, styleTopBorder, cols.get(momentosOrdenados.get(i)) == null ? momentosOrdenados.get(i) : cols.get(momentosOrdenados.get(i)));
				celda++;
			}
		}
		rows++;
		return rows;
	}

	/**
	 * Genera el cuerpo (Detalle) del reporte de Unidad Ejecutora llamando al
	 * stored procedure sp_reporte_unidad_ejecutora.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param wb
	 *            Libro excel abierto
	 * @param hs
	 *            Hoja activa
	 * @param mesCorte
	 *            Mes de corte
	 * @param capitulos
	 *            Capitulos que contiene el reporte
	 * @param momentosPresupuestales
	 *            Momentos Presupuestales que contiene el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @param filaInicio
	 *            Fila en la que inica el detalle del reporte
	 * @return Numero de filas insertadas
	 * @throws Exception
	 */
	public static int generaCuerpoReporteUE(Connection conn, HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] capitulos, String[] momentosPresupuestales, int ejercicioFiscal, int filaInicio) throws Exception {
		CallableStatement cs = null;
		ResultSet rs = null;
		String query = "{call dbo.sp_reporte_unidad_ejecutora(?, ?, ?)}";
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);
		
		int nMes = Util.numeroDeMes(mesCorte);
		String capitulosStr = Util.join(capitulos, ',').replaceAll("4000", "4000,4300");
		String momentosStr = Util.join(momentosPresupuestales, ',');

		log.info("Capitulos: " + capitulosStr);
		log.info("Momentos: " + momentosStr);

		log.info("{call dbo.sp_reporte_unidad_ejecutora(?, ?, ?)}");
		log.info("3[" + capitulosStr + "]");
		log.info("1[" + momentosStr + "]");
		log.info("2[" + nMes + "]");

		CellStyle style = wb.createCellStyle();
		CellStyle styleNormal = wb.createCellStyle();
		CellStyle styleCurrency = wb.createCellStyle();
		CellStyle styleCurrencyEnc = wb.createCellStyle();
		CellStyle styleCurrencySubTotalEnc = wb.createCellStyle();
		CellStyle styleSubTotalEnc = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 11);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteMontos = wb.createFont();
		fuenteMontos.setFontHeightInPoints((short) 11);
		fuenteMontos.setFontName("Calibri");
		fuenteMontos.setColor(IndexedColors.BLACK.getIndex());
		fuenteMontos.setBold(false);
		fuenteMontos.setItalic(false);

		style.setFont(fuenteEncabezado);
		styleNormal.setFont(fuenteMontos);

		styleCurrency.setFont(fuenteMontos);
		styleCurrency.setDataFormat((short) 8);

		styleCurrencyEnc.setFont(fuenteEncabezado);
		styleCurrencyEnc.setDataFormat((short) 8);

		styleCurrencySubTotalEnc.setFont(fuenteEncabezado);
		styleCurrencySubTotalEnc.setDataFormat((short) 8);
		styleCurrencySubTotalEnc.setBorderBottom(BorderStyle.THIN);

		styleSubTotalEnc.setFont(fuenteEncabezado);
		styleSubTotalEnc.setBorderBottom(BorderStyle.THIN);
		Map<String, CellStyle> estilo = new HashMap<String, CellStyle>();
		estilo.put("TOTAL_TEXTO", style);
		estilo.put("SUBTOTAL_TEXTO", styleSubTotalEnc);
		estilo.put("DETALLE_TEXTO", styleNormal);
		estilo.put("DETALLE_CURRENCY", styleCurrency);
		estilo.put("TOTAL_CURRENCY", styleCurrencyEnc);
		estilo.put("SUBTOTAL_CURRENCY", styleCurrencySubTotalEnc);

		try {
			cs = conn.prepareCall(query);
			cs.setString(3, capitulosStr);
			cs.setString(1, momentosStr);
			cs.setInt(2, nMes);

			rs = cs.executeQuery();
			String encAnual[] = { "original_anual", "modificado_anual" };

			while (rs.next()) {
				int celda = 0;
				filaInicio++;
				createSimpleRow(hs, filaInicio, celda, estilo.get(rs.getString("estilo").toUpperCase() + "_TEXTO"), rs.getString("clasificacion"));
				celda++;
				for (int i = 0; i < encAnual.length; i++) {
					createSimpleRow(hs, filaInicio, i + 1, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(encAnual[i]));
					celda++;
				}
				celda++;
				for (int i = 0; i < momentosPresupuestales.length; i++) {
					String col = momentosOrdenados.get(i);
					if ("DISPONIBLE_NETO".equals(momentosOrdenados.get(i)))
						col = "DISP_NETO";

					createSimpleRow(hs, filaInicio, i + celda, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(col + "_mes"));
				}

			}
		} finally {
			CloseObject.closeObject(cs, false);
			CloseObject.closeObject(rs, false);
		}
		return filaInicio;

	}

	/**
	 * Genera el cuerpo del reporte por Unidad Normativa
	 * 
	 * @param wb
	 * @param hs
	 * @param mesCorte
	 * @param momentosPresupuestales
	 * @param ejercicioFiscal
	 * @return
	 * @throws Exception
	 */
	public static int generaEncabezadoReporteUN(HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] momentosPresupuestales, int ejercicioFiscal) throws Exception {
		Map<String, String> cols = new HashMap<String, String>();
		cols.put("ORIGINAL", "Original");
		cols.put("MODIFICADO", "Modificado");
		cols.put("EJERCIDO_PAGADO", "Ejercido");
		cols.put("COMPROMETIDO", "Comprometido");
		cols.put("DEVENGADO", "Devengado");
		cols.put("DISPONIBLE_NETO", "Disponible");
		cols.put("PRECOMPROMETIDO", "Precomprometido");
		cols.put("APARTADO", "Apartado");

		CellStyle style = wb.createCellStyle();
		CellStyle styleTopBorder = wb.createCellStyle();
		CellStyle styleMerged = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 12);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteNomal = wb.createFont();
		fuenteNomal.setFontHeightInPoints((short) 11);
		fuenteNomal.setFontName("Calibri");
		fuenteNomal.setColor(IndexedColors.BLACK.getIndex());
		fuenteNomal.setBold(true);
		fuenteNomal.setItalic(false);

		style.setFont(fuenteEncabezado);
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);
		Calendar c = new GregorianCalendar();
		int rows = 0;
		createSimpleRow(hs, rows, 0, style, "Avance presupuestal al " + c.get(Calendar.DAY_OF_MONTH) + " de " + Util.NOMBRE_MESES_MX[c.get(Calendar.MONTH)] + " del " + String.valueOf(ejercicioFiscal), rows, 0, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 0, style, "", rows, 0, rows, (4 + momentosPresupuestales.length));

		style.setFont(fuenteNomal);
		style.setAlignment(HorizontalAlignment.CENTER);

		styleTopBorder.setFont(fuenteNomal);
		styleTopBorder.setBorderTop(BorderStyle.THIN);
		styleTopBorder.setAlignment(HorizontalAlignment.CENTER);

		styleMerged.setVerticalAlignment(VerticalAlignment.CENTER);
		styleMerged.setFont(fuenteEncabezado);
		styleMerged.setAlignment(HorizontalAlignment.CENTER);

		rows++;
		createSimpleRow(hs, rows, 0, styleMerged, "Unidad Normativa", rows, 0, rows + 1, 0);
		createSimpleRow(hs, rows, 1, style, "Anual", rows, 1, rows, 2);
		createSimpleRow(hs, rows, 3, style, "");
		createSimpleRow(hs, rows, 4, style, "A " + mesCorte.toLowerCase(), rows, 4, rows, (4 + momentosPresupuestales.length));
		rows++;
		createSimpleRow(hs, rows, 1, styleTopBorder, "Original");
		createSimpleRow(hs, rows, 2, styleTopBorder, "Modificado");
		createSimpleRow(hs, rows, 3, style, "");

		int celda = 4;
		for (int i = 0; i < momentosOrdenados.size(); i++) {
			if (momentosOrdenados.get(i) != null & !"".equals(momentosOrdenados.get(i))) {
				createSimpleRow(hs, rows, celda, styleTopBorder, cols.get(momentosOrdenados.get(i)) == null ? momentosOrdenados.get(i) : cols.get(momentosOrdenados.get(i)));
				celda++;
			}
		}
		rows++;
		return rows;
	}

	/**
	 * Genera el cuerpo (Detalle) del reporte de Unidad Normativa llamando al
	 * stored procedure sp_reporte_unidad_normativa.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param wb
	 *            Libro excel abierto
	 * @param hs
	 *            Hoja activa
	 * @param mesCorte
	 *            Mes de corte
	 * @param capitulos
	 *            Capitulos que contiene el reporte
	 * @param momentosPresupuestales
	 *            Momentos Presupuestales que contiene el reporte
	 * @param ejercicioFiscal
	 *            Ejercicio Fiscal
	 * @param filaInicio
	 *            Fila en la que inica el detalle del reporte
	 * @return Numero de filas insertadas
	 * @throws Exception
	 */
	public static int generaCuerpoReporteUN(Connection conn, HSSFWorkbook wb, HSSFSheet hs, String mesCorte, String[] capitulos, String[] momentosPresupuestales, int ejercicioFiscal, int filaInicio) throws Exception {
		CallableStatement cs = null;
		ResultSet rs = null;
		String query = "{call dbo.sp_reporte_unidad_normativa(?, ?, ?)}";
		List<String> momentosOrdenados = generaOrdenMomentos(momentosPresupuestales);
		int nMes = Util.numeroDeMes(mesCorte);
		String capitulosStr = Util.join(capitulos, ',').replaceAll("4000", "4000,4300");
		String momentosStr = Util.join(momentosPresupuestales, ',');

		log.info("Capitulos: " + capitulosStr);
		log.info("Momentos: " + momentosStr);

		log.info("{call dbo.sp_reporte_unidad_normativa(?, ?, ?)}");
		log.info("1[" + momentosStr + "]");
		log.info("2" + nMes + "]");
		log.info("3[" + capitulosStr + "]");

		CellStyle style = wb.createCellStyle();
		CellStyle styleNormal = wb.createCellStyle();
		CellStyle styleCurrency = wb.createCellStyle();
		CellStyle styleCurrencyEnc = wb.createCellStyle();
		CellStyle styleCurrencySubTotalEnc = wb.createCellStyle();
		CellStyle styleSubTotalEnc = wb.createCellStyle();

		HSSFFont fuenteEncabezado = wb.createFont();
		fuenteEncabezado.setFontHeightInPoints((short) 11);
		fuenteEncabezado.setFontName("Calibri");
		fuenteEncabezado.setColor(IndexedColors.BLACK.getIndex());
		fuenteEncabezado.setBold(true);
		fuenteEncabezado.setItalic(false);

		HSSFFont fuenteMontos = wb.createFont();
		fuenteMontos.setFontHeightInPoints((short) 11);
		fuenteMontos.setFontName("Calibri");
		fuenteMontos.setColor(IndexedColors.BLACK.getIndex());
		fuenteMontos.setBold(false);
		fuenteMontos.setItalic(false);

		style.setFont(fuenteEncabezado);
		styleNormal.setFont(fuenteMontos);

		styleCurrency.setFont(fuenteMontos);
		styleCurrency.setDataFormat((short) 8);

		styleCurrencyEnc.setFont(fuenteEncabezado);
		styleCurrencyEnc.setDataFormat((short) 8);

		styleCurrencySubTotalEnc.setFont(fuenteEncabezado);
		styleCurrencySubTotalEnc.setDataFormat((short) 8);
		styleCurrencySubTotalEnc.setBorderBottom(BorderStyle.THIN);

		styleSubTotalEnc.setFont(fuenteEncabezado);
		styleSubTotalEnc.setBorderBottom(BorderStyle.THIN);
		Map<String, CellStyle> estilo = new HashMap<String, CellStyle>();
		estilo.put("TOTAL_TEXTO", style);
		estilo.put("SUBTOTAL_TEXTO", styleSubTotalEnc);
		estilo.put("DETALLE_TEXTO", styleNormal);
		estilo.put("DETALLE_CURRENCY", styleCurrency);
		estilo.put("TOTAL_CURRENCY", styleCurrencyEnc);
		estilo.put("SUBTOTAL_CURRENCY", styleCurrencySubTotalEnc);

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, momentosStr);
			cs.setString(3, capitulosStr);
			cs.setInt(2, nMes);

			rs = cs.executeQuery();
			String encAnual[] = { "original_anual", "modificado_anual" };

			while (rs.next()) {
				int celda = 0;
				filaInicio++;
				createSimpleRow(hs, filaInicio, celda, estilo.get(rs.getString("estilo").toUpperCase() + "_TEXTO"), rs.getString("clasificacion"));
				celda++;
				for (int i = 0; i < encAnual.length; i++) {
					createSimpleRow(hs, filaInicio, i + 1, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(encAnual[i]));
					celda++;
				}
				celda++;
				for (int i = 0; i < momentosPresupuestales.length; i++) {
					String col = momentosOrdenados.get(i);
					if ("DISPONIBLE_NETO".equals(momentosOrdenados.get(i)))
						col = "DISP_NETO";

					createSimpleRow(hs, filaInicio, i + celda, estilo.get(rs.getString("estilo").toUpperCase() + "_CURRENCY"), rs.getString(col + "_mes"));
				}

			}
		} finally {
			CloseObject.closeObject(cs, false);
			CloseObject.closeObject(rs, false);
		}
		return filaInicio;

	}

	private static List<String> generaOrdenMomentos(String[] momentosPresupuestales) {

		List<String> momentosOrdenados = new ArrayList<String>();

		String orden[] = new String[8];
		orden[0] = "ORIGINAL";
		orden[1] = "MODIFICADO";
		orden[2] = "EJERCIDO_PAGADO";
		orden[3] = "DEVENGADO";
		orden[4] = "COMPROMETIDO";
		orden[5] = "PRECOMPROMETIDO";
		orden[6] = "APARTADO";
		orden[7] = "DISPONIBLE_NETO";

		if (momentosPresupuestales != null) {
			for (int i = 0; i < orden.length; i++) {
				for (int j = 0; j < momentosPresupuestales.length; j++)
					if (orden[i].equalsIgnoreCase(momentosPresupuestales[j])) {
						momentosOrdenados.add(momentosPresupuestales[j]);
						break;
					}
			}
		}

		return momentosOrdenados;
	}
}
