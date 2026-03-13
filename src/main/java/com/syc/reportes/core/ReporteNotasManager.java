package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteNotasManager {
	private static ConfiguraAplicativoBusinessLogic	cabl		= new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	private static String							tipoFuente	= cabl.getSystemSetting("FUENTE_INSTITUCIONAL");
	private static final Logger log = Logger.getLogger(ReporteNotasManager.class);

	/**
	 * 
	 * @param conn
	 * @param fechaFin
	 * @param miles
	 * @param plantillas
	 * @return
	 * @throws Exception
	 */
	public static String generaReporteNotasDetManager(Connection conn, String fechaFin, Integer miles, Map<String, String> plantillas) throws Exception {


		CallableStatement cs6 = null, cs15 = null, cs16 = null;
		PreparedStatement ps = null;
		ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null, rs9 = null, rs10 = null, rs11 = null, rs12 = null, rs13 = null, rs14 = null, rs15 = null, rs16 = null, rs17 = null, rs18 = null, rs19 = null, rs20 = null;

		String anio = fechaFin.substring(6, 10);
		String fileName = "";
		String fechaIni = "01/01/" + anio;
		int mes = Integer.parseInt(fechaFin.substring(3, 5));
		
		String query6 = "{call sp_ReporteDeudores_Notas ( ?,?,? )}";
		String query15 = "{call sp_a_conciliacion_ING_excel ( ?,?,? )}";
		String query16 = "{call sp_a_conciliacion_EG_excel ( ?,?,? )}";
		String query19 = "select cConcepto, mTerrenoVH, mTerrenoAvaluo, mConstruccionVH, mConstruccionAvaluo, mCapitalizacion, mconstruccionEnProceso, mDepreciacion, mTotalBMAnioActual, mTotalBMAnioAnt, nEsTotal tipo from tBienesInmuebles (NOLOCK) order by id";


		try {
			log.debug( "======================Iniciando ejecucion en DB de Stored Procedures======================" );
			
			rs = generaReportesExcel(conn, "sp_reporteAcreedores_Notas_detalle", 0, fechaFin,  miles);
			rs1 = generaReportesExcel(conn, "sp_reporteAnticipo_Notas", 0, fechaFin,  miles);
			rs2 = generaReportesExcel(conn, "sp_reporteInventario_Notas", 0, fechaFin,  miles);
			rs3 = generaReportesExcel(conn, "sp_reporteAlmacenes_notas", 0, fechaFin,  miles);
			rs4 = generaReportesExcel(conn, "sp_reporteFFM_notas", 0, fechaFin,  miles);
			rs5 = generaReportesExcel(conn, "sp_reporteIncobrables_Notas", mes, fechaFin,  miles);		
			rs7 = generaReportesExcel(conn, "sp_reporteRetenciones_Notas", 0, fechaFin,  miles);
			rs8 = generaReportesExcel(conn, "sp_reporteSubcuentasFFM_notas", 0, fechaFin,  miles);
			rs9 = generaReportesExcel(conn, "sp_reporteIngresos_Notas", 0, fechaFin,  miles);
			rs10 = generaReportesExcel(conn, "sp_ReporteGastos_notas", mes, fechaFin,  miles);
			rs11 = generaReportesExcel(conn, "sp_reportePatrimonio_notas", mes, fechaFin,  miles);
			rs12 = generaReportesExcel(conn, "sp_flujoEfectivo_notas", mes, fechaFin,  miles);
			rs13 = generaReportesExcel(conn, "sp_flujoBienesMuebles_notas", mes, fechaFin,  miles);
			rs14 = generaReportesExcel(conn, "sp_reporteIntegracionFFM_notas", 0, fechaFin,  miles);
			rs17 = generaReportesExcel(conn, "sp_reporteSP_notas", mes, fechaFin,  miles);
			rs18 = generaReportesExcel(conn, "sp_reporteCuentasOrdenPptal", mes, fechaFin,  miles);
			rs20 = generaReportesExcel(conn, "sp_reporteBienesMuebles", mes, fechaFin,  miles);
		
			cs6 = conn.prepareCall(query6);
			cs6.setString(1, fechaIni);
			cs6.setString(2, fechaFin);
			cs6.setInt(3, miles);

			rs6 = cs6.executeQuery();

			cs15 = conn.prepareCall(query15);
			cs15.setInt(1, mes);
			cs15.setString(2, anio);
			cs15.setInt(3, miles);

			rs15 = cs15.executeQuery();

			cs16 = conn.prepareCall(query16);
			cs16.setInt(1, mes);
			cs16.setString(2, anio);
			cs16.setInt(3, miles);

			rs16 = cs16.executeQuery();
			
			ps = conn.prepareStatement( query19 );
			rs19 = ps.executeQuery();
			
			fileName = generaReporteResumen(rs, rs1, rs2, rs3, rs4, rs5, rs6, rs7, rs8, rs9, rs10, rs11, rs12, rs13, rs14, rs15, rs16, rs17, rs18, rs19, rs20, plantillas.get("REPNOTAS"), fechaFin, miles);
			
			log.debug( "====================== Fin  ejecucion en DB de Stored Procedures======================" );
			return fileName;
		} finally {
		
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
			CloseObject.closeObject(rs1, false);
			CloseObject.closeObject(rs2, false);
			CloseObject.closeObject(rs3, false);
			CloseObject.closeObject(rs4, false);
			CloseObject.closeObject(rs5, false);
			CloseObject.closeObject(rs6, false);
			CloseObject.closeObject(cs6, false);
			CloseObject.closeObject(rs7, false);
			CloseObject.closeObject(rs8, false);
			CloseObject.closeObject(rs9, false);
			CloseObject.closeObject(rs10, false);
			CloseObject.closeObject(rs11, false);
			CloseObject.closeObject(rs12, false);
			CloseObject.closeObject(rs13, false);
			CloseObject.closeObject(rs14, false);
			CloseObject.closeObject(rs15, false);
			CloseObject.closeObject(cs15, false);
			CloseObject.closeObject(rs16, false);
			CloseObject.closeObject(cs16, false);
			CloseObject.closeObject(rs17, false);
			CloseObject.closeObject(rs18, false);
			CloseObject.closeObject(rs19, false);
			CloseObject.closeObject(rs20, false);
			
		}

	}

	/**
	 * Obtiene desde la base de datos la informacion para los estados financieros 
	 * @param conn COnexion a la base de datos
	 * @param fechaFin Fecha final del reporte
	 * @param miles SI es en miles o pesos
	 * @param plantillas Plantillas a utilizar
	 * @return Referencia al archivo generado-
	 * @throws Exception
	 */
	public static String generaReporteResumenManager(Connection conn, String fechaFin, Integer miles, Map<String, String> plantillas) throws Exception {

		CallableStatement cs6 = null, cs15 = null, cs16 = null;
		PreparedStatement ps = null;
		ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null, rs9 = null, rs10 = null, rs11 = null, rs12 = null, rs13 = null, rs14 = null, rs15 = null, rs16 = null, rs17 = null, rs18 = null, rs19 = null, rs20 = null;
		
		String anio = fechaFin.substring(6, 10);
		String fileName = "";
		String fechaIni = "01/01/" + anio;
		int mes = Integer.parseInt(fechaFin.substring(3, 5));
				
		String query6 = "{call sp_ReporteDeudores_Notas ( ?,?,? )}";
		String query15 = "{call sp_a_conciliacion_ING_excel ( ?,?,? )}";
		String query16 = "{call sp_a_conciliacion_EG_excel ( ?,?,? )}";
		String query19 = "select cConcepto, mTerrenoVH, mTerrenoAvaluo, mConstruccionVH, mConstruccionAvaluo, mCapitalizacion, mconstruccionEnProceso, mDepreciacion, mTotalBMAnioActual, mTotalBMAnioAnt, nEsTotal tipo from tBienesInmuebles (NOLOCK) order by id";


		try {
			log.debug( "======================Iniciando ejecucion en DB de Stored Procedures======================" );
			
			rs = generaReportesExcel(conn, "sp_reporteAcreedores_Notas", 0, fechaFin,  miles);
			rs1 = generaReportesExcel(conn, "sp_reporteAnticipo_Notas", 0, fechaFin,  miles);
			rs2 = generaReportesExcel(conn, "sp_reporteInventario_Notas", 0, fechaFin,  miles);
			rs3 = generaReportesExcel(conn, "sp_reporteAlmacenes_notas", 0, fechaFin,  miles);
			rs4 = generaReportesExcel(conn, "sp_reporteFFM_notas", 0, fechaFin,  miles);
			rs5 = generaReportesExcel(conn, "sp_reporteIncobrables_Notas", mes, fechaFin,  miles);		
			rs7 = generaReportesExcel(conn, "sp_reporteRetenciones_Notas", 0, fechaFin,  miles);
			rs8 = generaReportesExcel(conn, "sp_reporteSubcuentasFFM_notas", 0, fechaFin,  miles);
			rs9 = generaReportesExcel(conn, "sp_reporteIngresos_Notas", 0, fechaFin,  miles);
			rs10 = generaReportesExcel(conn, "sp_ReporteGastos_notas", mes, fechaFin,  miles);
			rs11 = generaReportesExcel(conn, "sp_reportePatrimonio_notas", mes, fechaFin,  miles);
			rs12 = generaReportesExcel(conn, "sp_flujoEfectivo_notas", mes, fechaFin,  miles);
			rs13 = generaReportesExcel(conn, "sp_flujoBienesMuebles_notas", mes, fechaFin,  miles);
			rs14 = generaReportesExcel(conn, "sp_reporteIntegracionFFM_notas", 0, fechaFin,  miles);
			rs17 = generaReportesExcel(conn, "sp_reporteSP_notas", mes, fechaFin,  miles);
			rs18 = generaReportesExcel(conn, "sp_reporteCuentasOrdenPptal", mes, fechaFin,  miles);
			rs20 = generaReportesExcel(conn, "sp_reporteBienesMuebles", mes, fechaFin,  miles);
		
			//cs6 = conn.prepareCall(query6);
			cs6= conn.prepareCall( query6 , ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			cs6.setString(1, fechaIni);
			cs6.setString(2, fechaFin);
			cs6.setInt(3, miles);	
			
			rs6 = cs6.executeQuery();

			cs15= conn.prepareCall( query15 , ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			cs15.setInt(1, mes);
			cs15.setString(2, anio);
			cs15.setInt(3, miles);

			rs15 = cs15.executeQuery();

			cs16= conn.prepareCall( query16 , ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			cs16.setInt(1, mes);
			cs16.setString(2, anio);
			cs16.setInt(3, miles);

			rs16 = cs16.executeQuery();
						
			ps = conn.prepareCall( query19 , ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs19 = ps.executeQuery();
			
			fileName = generaReporteResumen(rs, rs1, rs2, rs3, rs4, rs5, rs6, rs7, rs8, rs9, rs10, rs11, rs12, rs13, rs14, rs15, rs16, rs17, rs18, rs19, rs20, plantillas.get("REPNOTAS"), fechaFin, miles);
			
			log.debug( "====================== Fin  ejecucion en DB de Stored Procedures======================" );
			return fileName;
		} finally {
		
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
			CloseObject.closeObject(rs1, false);
			CloseObject.closeObject(rs2, false);
			CloseObject.closeObject(rs3, false);
			CloseObject.closeObject(rs4, false);
			CloseObject.closeObject(rs5, false);
			CloseObject.closeObject(rs6, false);
			CloseObject.closeObject(cs6, false);
			CloseObject.closeObject(rs7, false);
			CloseObject.closeObject(rs8, false);
			CloseObject.closeObject(rs9, false);
			CloseObject.closeObject(rs10, false);
			CloseObject.closeObject(rs11, false);
			CloseObject.closeObject(rs12, false);
			CloseObject.closeObject(rs13, false);
			CloseObject.closeObject(rs14, false);
			CloseObject.closeObject(rs15, false);
			CloseObject.closeObject(cs15, false);
			CloseObject.closeObject(rs16, false);
			CloseObject.closeObject(cs16, false);
			CloseObject.closeObject(rs17, false);
			CloseObject.closeObject(rs18, false);
			CloseObject.closeObject(rs19, false);
			CloseObject.closeObject(rs20, false);
			
		}

	}
	
	public static ResultSet generaReportesExcel(Connection conn, String store, int mes, String fechaFin, int miles) throws Exception {
		CallableStatement call1 = null;
		ResultSet rs = null;
		String query = "{call " +  store +"( ?,? )}";
		
		call1= conn.prepareCall( query , ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
				if (mes == 0)
					call1.setString( 1, fechaFin );
				else
					call1.setInt( 1, mes );
				
				call1.setInt( 2, miles );
				rs = call1.executeQuery();
	
	    log.debug( String.format( "Ejecutando["+query+"]" ));
		
		return rs;
	}

	private static String generaReporteResumen(ResultSet rs, ResultSet rs1, ResultSet rs2, ResultSet rs3, ResultSet rs4, ResultSet rs5, ResultSet rs6, ResultSet rs7, ResultSet rs8, ResultSet rs9, ResultSet rs10, ResultSet rs11, ResultSet rs12, ResultSet rs13, ResultSet rs14, ResultSet rs15,
		ResultSet rs16, ResultSet rs17, ResultSet rs18, ResultSet rs19, ResultSet rs20,  String plantillaPath, String fechaFin, int miles) throws Exception {
		File cFileExcelPlantilla = new File(plantillaPath);
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "ReporteNotas" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		fs.close();

		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		Workbook workbook = new HSSFWorkbook(fsArchivo);
		fsArchivo.close();

		// Hoja 1 Deudores
		ResultSetMetaData rsMetadata = rs6.getMetaData();
		int renglonInicio = 5;

		// DecimalFormat formato = new DecimalFormat ("#,###,##0.00");
		escribeExcel(0, workbook, renglonInicio, rs6, rsMetadata.getColumnCount(), 0, 2, fechaFin);

		// Hoja 2 Anticipos

		ResultSetMetaData rsMetadata7 = rs1.getMetaData();
		escribeExcel(1, workbook, renglonInicio, rs1, rsMetadata7.getColumnCount(), 0, 2, fechaFin);

		// Hoja 3 Consumo
		ResultSetMetaData rsMetadata1 = rs2.getMetaData();
		ResultSetMetaData rsMetadata2 = rs3.getMetaData();

		escribeExcel(2, workbook, renglonInicio, rs2, rsMetadata1.getColumnCount(), 0, 3, fechaFin);
		escribeExcel(2, workbook, renglonInicio + 7, rs3, rsMetadata2.getColumnCount(), 0, 10, fechaFin);

		// Hoja 4 FFM
		ResultSetMetaData rsMetadata3 = rs4.getMetaData();
		ResultSetMetaData rsMetadata8 = rs8.getMetaData();

		escribeExcel(3, workbook, renglonInicio, rs4, rsMetadata3.getColumnCount(), 0, 3, fechaFin);
		escribeExcel(3, workbook, renglonInicio + 6, rs8, rsMetadata8.getColumnCount(), 0, 9, fechaFin);

		// Hoja 4 derecha estado financiero del FFM
		ResultSetMetaData rsMetadata14 = rs14.getMetaData();

		escribeExcel(3, workbook, renglonInicio, rs14, rsMetadata14.getColumnCount(), 4, 3, fechaFin);

		// Hoja 5 CI
		ResultSetMetaData rsMetadata4 = rs5.getMetaData();

		escribeExcel(4, workbook, renglonInicio, rs5, rsMetadata4.getColumnCount(), 0, 3, fechaFin);

		// Hoja 6 Acreedores
		ResultSetMetaData rsMetadata5 = rs.getMetaData();

		escribeExcel(5, workbook, renglonInicio, rs, rsMetadata5.getColumnCount(), 0, 2, fechaFin);

		// Hoja 7 Retenciones
		ResultSetMetaData rsMetadata6 = rs7.getMetaData();

		escribeExcel(6, workbook, renglonInicio, rs7, rsMetadata6.getColumnCount(), 0, 3, fechaFin);

		// Hoja 8 Edo Actividades Ingresos
		ResultSetMetaData rsMetadata9 = rs9.getMetaData();

		escribeExcel(7, workbook, renglonInicio, rs9, rsMetadata9.getColumnCount(), 0, 3, fechaFin);

		// Egresos
		ResultSetMetaData rsMetadata10 = rs10.getMetaData();

		escribeExcel(7, workbook, renglonInicio + 9, rs10, rsMetadata10.getColumnCount(), 0, 12, fechaFin);

		// Hoja 9 Patrimonio

		ResultSetMetaData rsMetadata11 = rs11.getMetaData();

		escribeExcel(8, workbook, renglonInicio, rs11, rsMetadata11.getColumnCount(), 0, 3, fechaFin);

		// Hoja 10 Flujo Efectivo
		ResultSetMetaData rsMetadata12 = rs12.getMetaData();
		ResultSetMetaData rsMetadata13 = rs13.getMetaData();

		escribeExcel(9, workbook, renglonInicio, rs12, rsMetadata12.getColumnCount(), 1, 3, fechaFin);
		escribeExcel(9, workbook, renglonInicio + 11, rs13, rsMetadata13.getColumnCount(), 0, 14, fechaFin);

		// Hoja 11 Ingreso

		Sheet sheet11 = workbook.getSheetAt(10);
		ResultSetMetaData rsMetadata15 = rs15.getMetaData();

		escribeExcel(10, workbook, renglonInicio, rs15, rsMetadata15.getColumnCount(), 0, 1, fechaFin);

		if (miles == 0) {
			Row rwEnc32 = (sheet11.getRow(4) == null ? sheet11.createRow(4) : sheet11.getRow(4));
			Cell cell32 = (rwEnc32.getCell(0) == null ? rwEnc32.createCell(0) : rwEnc32.getCell(0));
			cell32.setCellValue("(PESOS)");
		} else {
			Row rwEnc33 = (sheet11.getRow(4) == null ? sheet11.createRow(4) : sheet11.getRow(4));
			Cell cell33 = (rwEnc33.getCell(0) == null ? rwEnc33.createCell(0) : rwEnc33.getCell(0));
			cell33.setCellValue("(MILES)");
		}
		// Hoja 12 Egreso
		Sheet sheet12 = workbook.getSheetAt(11);
		ResultSetMetaData rsMetadata16 = rs16.getMetaData();

		escribeExcel(11, workbook, renglonInicio, rs16, rsMetadata16.getColumnCount() - 1, 0, 1, fechaFin);

		if (miles == 0) {
			Row rwEnc29 = (sheet12.getRow(4) == null ? sheet12.createRow(4) : sheet12.getRow(4));
			Cell cell29 = (rwEnc29.getCell(0) == null ? rwEnc29.createCell(0) : rwEnc29.getCell(0));
			cell29.setCellValue("(PESOS)");
		} else {
			Row rwEnc31 = (sheet12.getRow(4) == null ? sheet12.createRow(4) : sheet12.getRow(4));
			Cell cell31 = (rwEnc31.getCell(0) == null ? rwEnc31.createCell(0) : rwEnc31.getCell(0));
			cell31.setCellValue("(MILES)");
		}

		// Hoja 13 Servicios Personales
			Sheet sheet13 = workbook.getSheetAt(12);
			ResultSetMetaData rsMetadata17 = rs17.getMetaData();

			escribeExcel(12, workbook, renglonInicio, rs17, rsMetadata17.getColumnCount(), 0, 1, fechaFin);

			if (miles == 0) {
					Row rwEnc30 = (sheet13.getRow(3) == null ? sheet13.createRow(3) : sheet13.getRow(3));
					Cell cell30 = (rwEnc30.getCell(0) == null ? rwEnc30.createCell(0) : rwEnc30.getCell(0));
					cell30.setCellValue("(PESOS)");
			} else {
					Row rwEnc30 = (sheet13.getRow(3) == null ? sheet13.createRow(3) : sheet13.getRow(3));
					Cell cell30 = (rwEnc30.getCell(0) == null ? rwEnc30.createCell(0) : rwEnc30.getCell(0));
					cell30.setCellValue("(MILES)");
			}
			
			// Hoja 14 Cuentas de orden
				Sheet sheet14 = workbook.getSheetAt(13);
				ResultSetMetaData rsMetadata18 = rs18.getMetaData();
					
				escribeExcel(13, workbook, renglonInicio, rs18, rsMetadata18.getColumnCount(), 0, 1, fechaFin);

				if (miles == 0) {
						Row rwEnc31 = (sheet14.getRow(3) == null ? sheet14.createRow(3) : sheet14.getRow(3));
						Cell cell31 = (rwEnc31.getCell(0) == null ? rwEnc31.createCell(0) : rwEnc31.getCell(0));
						cell31.setCellValue("(PESOS)");
				} else {
						Row rwEnc31 = (sheet14.getRow(3) == null ? sheet14.createRow(3) : sheet14.getRow(3));
						Cell cell31 = (rwEnc31.getCell(0) == null ? rwEnc31.createCell(0) : rwEnc31.getCell(0));
						cell31.setCellValue("(MILES)");
				}
				
				// Hoja 15 Bienes inmuebles
				
				ResultSetMetaData rsMetadata19 = rs19.getMetaData();
				escribeExcel(14, workbook, renglonInicio+1, rs19, rsMetadata19.getColumnCount(), 0, 2, fechaFin);
				
				// 	Hoja 16 Bienes Muebles
				
				ResultSetMetaData rsMetadata20 = rs20.getMetaData();
				escribeExcel(15, workbook, renglonInicio , rs20, rsMetadata20.getColumnCount(), 0, 1, fechaFin);
				
		File fsalida = new File(file_name);

		FileOutputStream fos = new FileOutputStream(fsalida);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
		workbook.write(bos);

		/* Cierra Flujos */
		bos.flush();
		bos.close();
		fos.close();
		return file_name;
	}

	public static void escribeExcel(int sheet, Workbook workbook, int renglonInicio, ResultSet rs, int totalcolumnas, int columnaInicio, int renglonPeriodo, String fechaFin) throws Exception {

		Sheet sheet0 = workbook.getSheetAt(sheet);

		int cnt = 0;
		ResultSetMetaData rsMetadata = rs.getMetaData();
		
		//CellStyle generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
		CellStyle estiloTabla = Util.generaEstilo (workbook, 8, false, false, false, false, false);
		CellStyle estiloTablaTotal = Util.generaEstilo (workbook, 8, true, false, false, false, false);
		CellStyle estiloMonedaTotal = Util.generaEstilo (workbook, 8, true, false, false, false, true);
		CellStyle estiloMoneda = Util.generaEstilo (workbook, 8, false, false, false, false, true);

		estiloTabla.getDataFormat();

		int rows, colTipo = 0, colFecha = 0;

		boolean contieneTipo = contieneColumna(rs, "tipo");
		if (contieneTipo)
			totalcolumnas = totalcolumnas - 1;
		
		if (columnaInicio ==0){
			colFecha = columnaInicio + totalcolumnas - 1;
		} else colFecha = columnaInicio + totalcolumnas - 2;
			
		Row rwEnc27 = (sheet0.getRow(renglonPeriodo) == null ? sheet0.createRow(renglonPeriodo) : sheet0.getRow(renglonPeriodo));
		Cell cell27 = (rwEnc27.getCell(colFecha) == null ? rwEnc27.createCell(colFecha) : rwEnc27.getCell(colFecha));
		cell27.setCellValue(fechaFin);
		
		if (columnaInicio == 0 || columnaInicio == 1) {
			while (rs.next()) {
		
				rows = renglonInicio + cnt;
				Row rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				
				if (contieneTipo) {
					colTipo = rs.getInt("tipo");
				} else colTipo = 0;
			
				if ( rs.isLast() || colTipo == 1 ) {	//Cuando es total o es la ultima fila				
					for (int i = columnaInicio; i < totalcolumnas; i++) {
						if (i == 0)
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTablaTotal);
						else
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMonedaTotal);
					}
					cnt++;
				} else if (colTipo ==2) { //Cuando es titulo del reporte
					for (int i = columnaInicio; i < totalcolumnas + columnaInicio; i++) {
						if (i == 0)
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTablaTotal);
						else
							Util.deleteExcelCell(i, rw, rs);
					}
					cnt++; 
				} else {
					for (int i = columnaInicio; i < totalcolumnas; i++) {
						if (i ==0)
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
						else
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
					}
					cnt++;
				}
			}
		} else {
			while (rs.next()) {
				rows = renglonInicio + cnt;
				Row rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));

				if (contieneTipo) {
					colTipo = rs.getInt("tipo");
				} else colTipo = 0;
				
				if ( rs.isLast() ||colTipo == 1) { //Cuando es la ultima columna o el total
					for (int i = columnaInicio; i < totalcolumnas + columnaInicio; i++) {
						if (i == 0)
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1 - columnaInicio), rsMetadata.getColumnType(i + 1 - columnaInicio), estiloTablaTotal);
						else
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1 - columnaInicio), rsMetadata.getColumnType(i + 1 - columnaInicio), estiloMonedaTotal);
					}
					cnt++;
				} else if (colTipo ==2) { //Cuando es titulo del reporte
						for (int i = columnaInicio; i < totalcolumnas + columnaInicio; i++) {
							if (i ==columnaInicio )
								Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1 - columnaInicio), rsMetadata.getColumnType(i + 1 - columnaInicio), estiloTablaTotal);
							else
								Util.deleteExcelCell(i, rw, rs);
						}
						cnt++;
				} else {
					for (int i = columnaInicio; i < totalcolumnas + columnaInicio; i++) {
						if (i == 0)
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1 - columnaInicio), rsMetadata.getColumnType(i + 1 - columnaInicio), estiloTabla);
						else
							Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1 - columnaInicio), rsMetadata.getColumnType(i + 1 - columnaInicio), estiloMoneda);
					}
					cnt++;
				}
			}
		}

	}

	private static boolean contieneColumna(ResultSet resultSet, String columnName) throws Exception {
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		int coulmnCount = resultSetMetaData.getColumnCount();
		for (int i = 1; i <= coulmnCount; i++) {
			if (columnName.equals(resultSetMetaData.getColumnName(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static XWPFDocument generaReporteNotasWord(Connection conn, String fechaFin, int miles, XWPFDocument document, String efectivo
			, String derechos, String derAnt, String almacen, String cxp, String otrascxp, String pasivo, String pasivo2, String eventos
			, String eventos2, String eventos3, String fechaAut) throws Exception {

		String dia = fechaFin.substring(0,2);
		String anio = fechaFin.substring(6, 10);
		int mes = Integer.parseInt(fechaFin.substring(3, 5));
		int anioI = Integer.parseInt( anio );
		String nomMes =Util.nombreDeMes(mes);
		String fechaIni = "01/01/" + anioI;
		ArrayList<Object> listado = new ArrayList<Object>();
		ArrayList<Object> listado2 = new ArrayList<Object>();
		ArrayList<Object> listado3 = new ArrayList<Object>();
		ArrayList<Object> listado4 = new ArrayList<Object>();
		ArrayList<Object> listado5 = new ArrayList<Object>();
		ArrayList<Object> listado6 = new ArrayList<Object>();
		ArrayList<Object> listado7 = new ArrayList<Object>();
		ArrayList<Object> listado8 = new ArrayList<Object>();
		ArrayList<Object> listado9 = new ArrayList<Object>();
		ArrayList<Object> listado10 = new ArrayList<Object>();
		ArrayList<Object> listado11 = new ArrayList<Object>();
		ArrayList<Object> listado12 = new ArrayList<Object>();
		ArrayList<Object> listado13 = new ArrayList<Object>();
		ArrayList<Object> listado14 = new ArrayList<Object>();
		ArrayList<Object> listado15 = new ArrayList<Object>();
		ArrayList<Object> listado16 = new ArrayList<Object>();
		ArrayList<Object> listado17 = new ArrayList<Object>();
		ArrayList<Object> listado18 = new ArrayList<Object>();
		ArrayList<Object> listado19 = new ArrayList<Object>();
		ArrayList<Object> listado20 = new ArrayList<Object>();
		int tempateRowId = 2;
		
		try {
				log.debug( "======================Iniciando ejecucion en DB de Stored Procedures======================" );
				replaceTextFor(document, "TIPO_REPORTE", (miles == 0 ? "Pesos" : "Miles de pesos" ));
				
				//tabla 1 Acreedores
				listado = generaReporteFini (conn, "sp_ReporteDeudores_Notas", fechaIni, fechaFin, miles);				
				XWPFTable table = document.getTableArray( 0 ) ; 		
				generaTabla (table ,  tempateRowId, listado);
					
				//tabla 2 anticipos
				listado2 = generaReporte (conn, "sp_reporteAnticipo_Notas", fechaFin, 0, miles);
				table = document.getTableArray( 1 ) ; 	
				generaTabla (table ,  tempateRowId, listado2);
				
				//tabla 3 inventario
				listado3 = generaReporte(conn, "sp_reporteInventario_Notas", fechaFin, 0, miles);			
				table = document.getTableArray( 2 ) ; 	
				generaTablaSinTitulos (table ,  1, listado3);
		
				//tabla 4 almacenes
				listado4 = generaReporte(conn, "sp_reporteAlmacenes_notas", fechaFin, 0, miles);			
				table = document.getTableArray( 3 ) ; 	
				generaTablaSinTitulos (table ,  1, listado4);
				
				//tabla 5 ffm
				listado5 = generaReporte(conn, "sp_reporteFFM_notas", fechaFin, 0, miles);			
				table = document.getTableArray(4) ; 	
				generaTablaSinTitulos (table ,  1, listado5);
				
				//tabla 6
				listado6 = generaReporte(conn, "sp_reporteSubcuentasFFM_notas", fechaFin, 0, miles);			
				table = document.getTableArray(5) ; 	
				generaTablaSinTitulos (table ,  1, listado6);
				
				//tabla 7
				listado20 = generaReporte(conn, "sp_reporteBienesMuebles", fechaFin, mes, miles);			
				table = document.getTableArray(6) ; 	
				generaTabla (table , 1, listado20);
				
				//tabla 9
				listado7 = generaReporte(conn, "sp_reporteIncobrables_Notas", fechaFin, mes, miles);			
				table = document.getTableArray(10) ; 	
				generaTablaSinTitulos (table ,  1, listado7);
				
				//tabla 10
				listado8 = generaReporte(conn, "sp_reporteSP_notas", fechaFin, mes, miles);			
				table = document.getTableArray(11) ; 	
				generaTablaSinTitulos (table ,  1, listado8);
				
				//tabla 11
				listado18 = generaReporte(conn, "sp_reporteAcreedores_Notas",  fechaFin, 0 , miles);			
				table = document.getTableArray(12) ; 	
				generaTabla (table ,  tempateRowId, listado18);
				
				//tabla 12
				listado9 = generaReporte(conn, "sp_reporteRetenciones_Notas", fechaFin, 0, miles);			
				table = document.getTableArray(13) ; 	
				generaTablaSinTitulos (table ,  1, listado9);
				
				//tabla 13
				listado10 = generaReporte(conn, "sp_reporteIngresos_Notas", fechaFin, 0, miles);			
				table = document.getTableArray(14) ; 	
				generaTablaSinTitulos (table ,  1, listado10);
				
				//tabla 14
				listado11 = generaReporte(conn, "sp_ReporteGastos_notas", fechaFin, mes, miles);			
				table = document.getTableArray(15) ; 	
				generaTablaSinTitulos (table ,  1, listado11);
			
				//tabla 15
				listado12 = generaReporte(conn, "sp_reportePatrimonio_notas", fechaFin, mes, miles);			
				table = document.getTableArray(16) ; 	
				generaTablaSinTitulos (table ,  1, listado12);
				
				//tabla 16
				listado13 = generaReporte(conn, "sp_flujoEfectivo_notas", fechaFin, mes, miles);			
				table = document.getTableArray(17) ; 	
				generaTablaSinTitulos (table ,  1, listado13);
					
				//tabla 17 materiales
				listado19 = generaReporte(conn, "sp_adquisiciones_notas", fechaFin, mes, miles);			
				table = document.getTableArray(18) ; 	
				generaTablaSinTitulos (table ,  1, listado19);
					
				//tabla 18
				listado14 = generaReporte(conn, "sp_flujoBienesMuebles_notas", fechaFin, mes, miles);			
				table = document.getTableArray(19) ; 	
				generaTablaSinTitulos (table ,  1, listado14);
				
				//tabla 19 
				listado15 = generaReporteAnio(conn, "sp_a_conciliacion_ING_excel", anioI, mes, miles);			
				table = document.getTableArray(20) ; 	
				generaTabla (table ,  1, listado15);
						
				//tabla 20
				listado16 = generaReporteAnio(conn, "sp_a_conciliacion_EG_excel", anioI, mes, miles);			
				table = document.getTableArray(21) ; 	
				generaTabla(table ,  1, listado16);
				
				//tabla 21
				listado17 = generaReporte(conn, "sp_cedulaPasivosContingentes_notas", fechaFin, 0, miles);			
				table = document.getTableArray(22) ; 	
				generaTabla (table , tempateRowId, listado17);
				
				//tabla 22 
				listado18 = generaReporte(conn, "sp_reporteCuentasOrdenPptal", fechaFin, mes, miles);			
				table = document.getTableArray(23) ; 	
				generaTablaSinTitulos (table ,  1, listado18);
				
				log.debug( "======================Fin de ejecucion en DB de Stored Procedures======================" );
					
				double ImporteEfectivo = generaSaldos (conn, mes, "111");
				double ImporteDerechos = generaSaldos (conn, mes, "112");
				double ImpAnticipos = generaSaldos (conn, mes, "113");
				double ImpInventarios = generaSaldos (conn, mes, "114");
				double Imp115 = generaSaldos (conn, mes, "115");
				double ImpFFM = generaSaldos (conn, mes, "121");
				double Imp2250 = generaSaldos (conn, mes, "225");
				double ImpDifFFM = ImpFFM - Imp2250;
				double Imp124 = generaSaldos (conn, mes, "124");
				double Imp125 = generaSaldos (conn, mes, "125");
				double Imp126 = generaSaldos (conn, mes, "depre");  
				double ImpBM = generaSaldos (conn, mes, "123");
				double Imp2161 = generaSaldos (conn, mes, "1261");
				double Imp1231 = generaSaldos (conn, mes, "1231");
				double Imp1233 = generaSaldos (conn, mes, "1233");
				double Imp1236 = generaSaldos (conn, mes, "1236");
				double Imp116 = generaSaldos (conn, mes, "116");
				double ImpPas = generaSaldos (conn, mes, "211");
				double ImpPasDif = generaSaldos (conn, mes, "215");	
				double Imp216 = generaSaldos (conn, mes, "216");
				double ImpRete = generaSaldos (conn, mes, "21621");
				double Imp4 = generaSaldos (conn, mes, "4");
				double Imp422 = generaSaldos (conn, mes, "422");
				double Imp439 = generaSaldos (conn, mes, "439");
				double Imp431 = generaSaldos (conn, mes, "431");
				double Imp5 = generaSaldos (conn, mes, "5");
				double Imp511 = generaSaldos (conn, mes, "511");
				double Imp512 = generaSaldos (conn, mes, "512");
				double Imp513 = generaSaldos (conn, mes, "513");
				double Imp524 = generaSaldos (conn, mes, "524");
				double Imp529 = generaSaldos (conn, mes, "529");
				double Imp551 = generaSaldos (conn, mes, "551");
				double Imp559 = generaSaldos (conn, mes, "559");
				double ImpResultado = Imp4 - Imp5;
				double Imp31 = generaSaldos (conn, mes, "31");
				double Imp741 = generaSaldos (conn, mes, "741");
				double Imp763 = generaSaldos (conn, mes, "763");
				
				double Imp811 = generaSaldos (conn, mes, "8111");
				double Imp813 = generaSaldos (conn, mes, "8131");
				double ImpMod = Imp811 - Imp813;
				int juicios = obtieneJuicios(conn, fechaFin);
				
				replaceTextFor(document, "DIA_ENCURSO", dia);
				replaceTextFor(document, "MES_ENCURSO", nomMes);
				replaceTextFor(document, "ANIO_ENCURSO", anio);
				
				replaceParrafo(document, "PARRAFO_VARIABLE_EFECTIVO",  efectivo);
				replaceParrafo(document, "PARRAFO_VARIABLE_DERECHOS",  derechos);
				replaceParrafo(document, "PARRAFO_VARIABLE_ANTIG_DERECHOS",  derAnt);
				replaceParrafo(document, "PARRAFO_VARIABLE_ALMACENES",  almacen);
				replaceParrafo(document, "PARRAFO_VARIABLE_CXP",  cxp); 			
				replaceParrafo(document, "PARRAFO_VARIABLE_OTRASCXP",  otrascxp); 
				replaceParrafo(document, "NOTAS_VARIABLES_FLUJO_EFECTIVO",  pasivo);
				replaceParrafo(document, "NOTAS_VARIABLES_FLUJO2",  pasivo2);
				replaceParrafo(document, "Parrafos_variable_eventos",  eventos);
				replaceParrafo(document, "Parrafos_variable_even2",  eventos2);
				replaceParrafo(document, "Parrafos_variable_even3",  eventos3);
				replaceParrafo(document, "FECHA_FIRMA_NOTAS",  fechaAut);
				
				replaceTextFor(document, "IMPORTE_EFECTIVO", ImporteEfectivo , miles);
				replaceTextFor(document, "IMPORTE_DERECHOS", ImporteDerechos , miles);
				replaceTextFor(document, "IMPORTE_ANTICIPOS", ImpAnticipos , miles);
				replaceTextFor(document, "IMPORTE_INVENTARIOS", ImpInventarios , miles);
				replaceTextFor(document, "IMPORTE_ALMACENES", Imp115 , miles);
				replaceTextFor(document, "IMPORTE_FFM", ImpFFM , miles);
				replaceTextFor(document, "IMPORTE_DIFERENCIA_FFM", ImpDifFFM, miles); 
				replaceTextFor(document, "IMPORTE_124", Imp124, miles);
				replaceTextFor(document, "IMPORTE_125", Imp125, miles);
				replaceTextFor(document, "IMPORTE_126345", Imp126, miles);			
				replaceTextFor(document, "IMPORTE_BM", ImpBM, miles);
				replaceTextFor(document, "IMPORTE_2161", Imp2161, miles);
				replaceTextFor(document, "IMPORTE_1231", Imp1231, miles);
				replaceTextFor(document, "IMPORTE_1233", Imp1233, miles);
				replaceTextFor(document, "IMPORTE_1236R", Imp1236, miles);
				replaceTextFor(document, "IMPORTE_ESTIM", Imp116, miles); 
				replaceTextFor(document, "IMPORTE_PASIVO", ImpPas, miles); 
				replaceTextFor(document, "IMPORTE_PASDIF", ImpPasDif, miles);	
				replaceTextFor(document, "IMPORTE_216", Imp216, miles); 
				replaceTextFor(document, "IMPORTE_RETE", ImpRete, miles); 	
				replaceTextFor(document, "IMPORTE_INGRESOS", Imp4, miles);
				replaceTextFor(document, "IMPORTE_TRANSF", Imp422, miles); 
				replaceTextFor(document, "IMPORTE_OTROSING", Imp439, miles);
				replaceTextFor(document, "IMPORTE_INGFIN", Imp431, miles); 
				replaceTextFor(document, "IMPORTE_GASTOS", Imp5, miles); 
				replaceTextFor(document, "IMPORTE_SP", Imp511, miles); 
				replaceTextFor(document, "IMPORTE_MATSUM", Imp512, miles);
				replaceTextFor(document, "IMPORTE_SG", Imp513, miles); 
				replaceTextFor(document, "IMPORTE_AYUDSOC", Imp524, miles); 
				replaceTextFor(document, "IMPORTE_TRANSEXT", Imp529, miles); 		
				replaceTextFor(document, "GTOS_BM", Imp551, miles); 
				replaceTextFor(document, "IMPORTE_OTGTOS", Imp559, miles); 
				replaceTextFor(document, "IMPORTE_AHORRO", ImpResultado, miles);
				replaceTextFor(document, "IMPORTE_JUICIOS", juicios, miles);
				
				replaceTextFor(document, "IMPORTE_225", Imp2250, miles);
				replaceTextFor(document, "IMPORTE_2250", Imp2250, miles);
				replaceTextFor(document, "IMPORTE_310", Imp31, miles);
				
				replaceTextFor(document, "IMPORTE_741", Imp741, miles);
				replaceTextFor(document, "IMPORTE_763", Imp763, miles);	
				replaceTextFor(document, "IMPORTE_811", Imp811, miles);
				
				document = replaceTextFor(document, "IMPORTE_MODIFICADO", ImpMod, miles);
		} catch (Exception e) {
			log.error( e );
			throw e;
		}
		
		return document;
	}
	
	private static void setFormato (XWPFRun run , String fontFamily , int fontSize , String text , boolean bold ) {
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setText(text);
        run.setBold(bold);
        
       // run.removeBreak();
    }

	private static XWPFDocument replaceTextFor(XWPFDocument doc, String findText, String replaceText)
	{
        doc.getParagraphs().forEach(p ->{
            p.getRuns().forEach(run -> {
                String text = run.text();
                if(text.contains(findText)) {
                	
                	run.setText(text.replace(findText, replaceText), 0);                      		 	
                } 
            });
        });
        return doc;
    }
	
	private static XWPFDocument replaceTextFor(XWPFDocument doc, String findText, double replaceText, int miles)
	{
        doc.getParagraphs().forEach(p ->{
            p.getRuns().forEach(run -> {
                String text = run.text();
                if(text.contains(findText)) {
                	if (miles == 1) {
                		double importe = replaceText / 1000;
                		DecimalFormat df = new DecimalFormat("###,###,##0.0");
                		run.setText(text.replace(findText, df.format(importe)), 0);
        			} else {
        				DecimalFormat df = new DecimalFormat("###,###,##0.00");
        				run.setText(text.replace(findText, df.format(replaceText)), 0);
        			}
                } 
            });
        });
        return doc;
    }
	
	private static XWPFDocument replaceParrafo(XWPFDocument doc, String findText, String parrafo)
	{
		
        doc.getParagraphs().forEach(p ->{
            p.getRuns().forEach(run -> {
                String text = run.text();
                if(text.contains(findText)) {
                	run.setText(text.replace(findText, ""),0);
                	run.setText( parrafo );
                } 
            });
        });

        return doc;
    }
	
	private static void generaTabla(XWPFTable table, int tempateRowId, ArrayList<Object> listado2) throws Exception {
		boolean encabezado = false;
		int h = 0;
		XWPFTableRow renglon = table.getRow(tempateRowId);
		CTRow ctrow = null;
		int renglones = listado2.size() / renglon.getTableCells().size() ;
			
		for (int j = 1; j <= renglones ; j ++) {
			
			XWPFTableRow oldRow = renglon;
			ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
			XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

			if ( (listado2.get((j * newRow.getTableCells().size())-1).toString()).equals( "1.00" ) || (listado2.get((j * newRow.getTableCells().size())-1).toString()).equals( "2.00" ) ){
				encabezado = true;
			} else
				encabezado = false;
			int i = 0; 
			for (XWPFTableCell cell : newRow.getTableCells()) {
				for (XWPFParagraph paragraph1 : cell.getParagraphs()) {
					for (XWPFRun run : paragraph1.getRuns()) {
						if ( newRow.getTableCells().get( newRow.getTableCells().size() -1 ) != newRow.getCell( i )) {
								
								log.debug("========================= listado2.get(h)	" + listado2.get(h) );
								if (encabezado) {
																		
									log.debug( "Armando encabezado [" + listado2.get(h) + "]"  );
									if ( listado2.get(h) != null && !StringUtils.equals( "0.00", listado2.get(h).toString() )){
										setFormato(run , "Montserrat" ,7, listado2.get(h).toString() , true);	
									}
								}else {
									log.debug( "Armando NO encabezado [" + listado2.get(h) + "]"  );
									if ( listado2.get(h) != null && !StringUtils.equals( "0.00", listado2.get(h).toString() )){
										setFormato(run , "Montserrat" , 7, listado2.get(h).toString() , false);	
									}
								}
									h++;
									i++;
						} else {
							h++;
						}
						
					}				
					}
				}
			table.addRow(newRow);
		}
		table.removeRow( tempateRowId );
	}

	private static void generaTablaSinTitulos(XWPFTable table, int tempateRowId, ArrayList<Object> listado2) throws Exception {
		
		int h = 0;
		XWPFTableRow renglon = table.getRow(tempateRowId);
		CTRow ctrow = null;
		int renglones = listado2.size() / renglon.getTableCells().size() ;
			
		for (int j = 1; j <= renglones ; j ++) {
			
			XWPFTableRow oldRow = renglon;
			ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
			XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

			for (XWPFTableCell cell : newRow.getTableCells()) {
				for (XWPFParagraph paragraph1 : cell.getParagraphs()) {
					for (XWPFRun run : paragraph1.getRuns()) {				
						if (j == renglones) {
								log.debug( "Genera tabla sin titulos [" + listado2.get(h) + "]"  );
								if (listado2.get(h) != null && !StringUtils.equals( "0.00", listado2.get(h).toString())){
									setFormato(run , "Montserrat" , 7, listado2.get(h).toString() , true);
									}
							} else {
								if (listado2.get(h) != null && !StringUtils.equals( "0.00", listado2.get(h).toString())){
									setFormato(run , "Montserrat" , 7, listado2.get(h).toString() , false);	
								}										
							}
							h++;
						}				
					}
				}
			table.addRow(newRow);
		}
		table.removeRow( tempateRowId );		
	}

	private static ArrayList<Object> generaReporte(Connection conn, String store, String fechaFin, int mes , Integer miles) throws Exception {
		int columnasRA = 0;
		ArrayList<Object> reporte= new ArrayList<Object>();
		CallableStatement call1 = null;
		ResultSet rs = null;
		String query = "{call " +  store +"( ?,? )}";
		ResultSetMetaData md = null;
		
		try {
		
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		call1= conn.prepareCall( query );
		
		if (mes == 0)
			call1.setString( 1, fechaFin );
		else
			call1.setInt( 1, mes );
		
		call1.setInt( 2, miles );
		
		rs = call1.executeQuery();
		 
		md = rs.getMetaData();
        columnasRA = md.getColumnCount();
        log.debug( String.format( "Ejecutando["+query+"]" ));
        
		while (rs.next()){
			 Object dato = new Object[columnasRA];
             for (int i = 1; i <= columnasRA; i++) {                      
            	 
            	 if (i > 1) { 	
            		 if ( (i == 2 && store =="sp_reporteCuentasOrdenPptal") ||  (i == 3 && store =="sp_cedulaPasivosContingentes_notas") ) 
            			 dato = rs.getObject(i);
            		 else 
            			 dato = df.format(rs.getBigDecimal( i ));
            	 } else {
            		 dato = rs.getObject(i);
            	 }
            	 reporte.add(dato);
             }
		}
		} catch (Exception e) {
			throw new Exception("Ocurrio un problema en el procedimiento: " + store + " Error: " + e.getMessage().toString()); 
		}finally {
			CloseObject.closeObject( call1 );
			CloseObject.closeObject( rs );
		}
		return reporte;
	}
	

	private static ArrayList<Object> generaReporteFini(Connection conn, String store, String fechaini, String fechaFin , Integer miles) throws Exception {
		int columnasRA = 0;
		ArrayList<Object> reporte= new ArrayList<Object>();
		CallableStatement call1 = null;
		ResultSet rs = null;
		String query = "{call " +  store +"( ?,?, ?  )}";
		ResultSetMetaData md = null;
		
		try {
		
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		call1= conn.prepareCall( query );
		
		call1.setString( 1, fechaini );
		call1.setString( 2, fechaFin );
		call1.setInt( 3, miles );
		
		rs = call1.executeQuery();
		 
		md = rs.getMetaData();
        columnasRA = md.getColumnCount();
        log.debug( String.format( "Ejecutando["+query+"]" ));
        
		while (rs.next()){
			 Object dato = new Object[columnasRA];
             for (int i = 1; i <= columnasRA; i++) {                      
            	 
            	 if (i > 1)  { 	
            		 dato = df.format(rs.getBigDecimal( i ));
            	 }else {
            		 dato = rs.getObject(i);
            	 }
            	 reporte.add(dato);
             }
		}
		} finally {
			CloseObject.closeObject( call1 );
			CloseObject.closeObject( rs );
		}
		return reporte;
	}
	

	private static ArrayList<Object> generaReporteAnio (Connection conn, String store, int anio, int mes , Integer miles) throws Exception {
		int columnasRA = 0;
		
		ArrayList<Object> reporte= new ArrayList<Object>();
		CallableStatement call1 = null;
		ResultSet rs = null;
		String query = "{call " +  store +"( ?,?, ? )}";
		ResultSetMetaData md = null;
		
		try {
			
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		call1= conn.prepareCall( query );
		
		call1.setInt( 1, mes );
		call1.setInt( 2, anio );
		call1.setInt( 3, miles );
		
		rs = call1.executeQuery();
		 
		md = rs.getMetaData();
        columnasRA = md.getColumnCount();
        log.debug( String.format( "Ejecutando["+query+"]" ));
        
		while (rs.next()){
			 Object dato = new Object[columnasRA];
             for (int i = 1; i <= columnasRA; i++) {                      
            	 
            	 if (i > 3) { 	
            		 dato = df.format(rs.getBigDecimal( i ));
            	 }else {
            		 dato = rs.getObject(i);
            	 }
                 reporte.add(dato);
             }
		}
		} finally {
			CloseObject.closeObject( call1 );
			CloseObject.closeObject( rs );
		}
		return reporte;
	}
	
	public static double generaSaldos(Connection con, int mes,  String cuenta) throws Exception {
		double saldo = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = " declare @mes int = ? " 
					+ "	select (case @mes  when 1 then sum(msaldo1) "  
					+ " when 2 then sum(msaldo2) " 
					+ " when 3 then sum(msaldo3) " 
					+ " when 4 then sum(msaldo4) "
					+ " when 5 then sum(msaldo5) "
					+ " when 6 then sum(msaldo6) "
					+ " when 7 then sum(msaldo7) "
					+ " when 8 then sum(msaldo8) "
					+ " when 9 then sum(msaldo9) "
					+ " when 10 then sum(msaldo10)" 
					+ " when 11 then sum(msaldo11) "
					+ " when 12 then sum(msaldo12) end) importe"
					+ " from tsaldos (NOLOCK) where ncuenta like  ? + '%'";
		try {
			if (("depre").equals(cuenta)) 
				cuenta = "'1263%' or ncuenta like '1264%' or ncuenta like '1265'";
			
			ps = con.prepareStatement( query );
			ps.setInt( 1, mes );
			ps.setString( 2, cuenta );
			
			rs = ps.executeQuery();
			
			if (rs.next())
				saldo = rs.getDouble( 1 );
			
			
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs );
		}
		
		return saldo;
	}
	public static int obtieneJuicios(Connection con, String fechaFin) throws Exception {
		int numero = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = " SELECT count(*) juicios FROM ( "
						+ " SELECT distinct cRFC juicios FROM tpasivosContingentes (NOLOCK)"
						+ " WHERE (cFechaBaja > ? OR cFechaBaja IS NULL)) as tblJuicio";
		try {
			
			ps = con.prepareStatement( query );
			ps.setString( 1, fechaFin );
			
			rs = ps.executeQuery();
			
			if (rs.next())
				numero = rs.getInt( 1 );
			
			
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs );
		}
		
		return numero;
	}
}
