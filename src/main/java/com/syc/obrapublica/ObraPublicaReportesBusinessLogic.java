package com.syc.obrapublica;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ObraPublicaReportesBusinessLogic extends DataSourceManager {
	private static Logger	log	= Logger.getLogger(ObraPublicaReportesBusinessLogic.class);

	public String generaReporteContratosAcumulado(String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return ObraPublicaReportesManager.generaReporteContratosAcumuladoToExcel(conn, fechaI, fechaF, idunidadresponsable, idArea);
		} finally {
			try {
				CloseObject.closeObject(conn, true);
			} catch (Exception e) {
				log.error("Problema cerrando conexion: " + e, e);
			}
		}
	}

	public String generaReporteContratosAdjudicados(String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return ObraPublicaReportesManager.generaReporteContratosAdjudicadosToExcel(conn, fechaI, fechaF, idunidadresponsable, idArea);
		} finally {
			try {
				CloseObject.closeObject(conn, true);
			} catch (Exception e) {
				log.error("Problema cerrando conexion: " + e, e);
			}
		}
	}
	
	/**
	 * Genera reporte formato 10 de Obra Publica
	 * 
	 * @param cCentroContable
	 *            Centro contable
	 * @param fechaI
	 *            Fecha de Inicio
	 * @param fechaF
	 *            Fecha Final
	 * @param idunidadresponsable
	 *            UR del usuario
	 * @return Ruta en la que se guardo el archivo excel.
	 */
	public String generaReporteFormato10(String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, Workbook wb, String reportType) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return ObraPublicaReportesManager.generaReporteFormato10ToExcel(conn, cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, reportBody, wb, reportType);
		} finally {
			try {
				CloseObject.closeObject(conn, true);
			} catch (Exception e) {
				log.error("Problema cerrando conexion: " + e, e);
			}
		}
	}
	
	public ArrayList multiReporte(String cQuery, String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, Workbook wb, String reportType, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar, String strGeneral, String strResumen) throws Exception{
		Map<String, String> queryResult = null ;
		ArrayList arrDataQuery = new ArrayList();
		Connection conn = null;
		boolean bReturn=false;
		String cTipoSentencia = cQuery.substring(0,6).toUpperCase();
		try{
			conn=getConnection();
			if ("SELECT".equals(cTipoSentencia)|| "{CALL ".equals(cTipoSentencia) || "EXECUT".equals(cTipoSentencia)){
				arrDataQuery=ObraPublicaReportesManager.execMultiReporte(conn, cQuery,cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, "", wb, reportType, strUsuario, strCondicion, strCondMultiR, columnasBorrar, strGeneral, strResumen);
			}else{
				bReturn=ObraPublicaReportesManager.cambiaDatos(conn, cQuery);
				arrDataQuery.add(bReturn);
			}
		}finally{
			if (conn != null){
				conn.close();
			}
			conn=null;
		}

		return arrDataQuery;
	}
	public ArrayList exec(String cQuery) throws Exception{
		Map<String, String> queryResult = null ;
		ArrayList arrDataQuery = new ArrayList();
		Connection conn = null;
		boolean bReturn=false;
		String cTipoSentencia = cQuery.substring(0,6).toUpperCase();
		try{
			conn=getConnection();
			if ("SELECT".equals(cTipoSentencia)|| "{CALL ".equals(cTipoSentencia) || "EXECUT".equals(cTipoSentencia)){
				arrDataQuery=ObraPublicaReportesManager.execQuery(conn, cQuery);
			}else{
				bReturn=ObraPublicaReportesManager.cambiaDatos(conn, cQuery);
				arrDataQuery.add(bReturn);
			}
		}finally{
			if (conn != null){
				conn.close();
			}
			conn=null;
		}

		return arrDataQuery;
	}

	public String generaReporteFormato10(String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, Workbook wb, String reportType, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return ObraPublicaReportesManager.generaReporteFormato10ToExcel(conn, cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, reportBody, wb, reportType, strUsuario, strCondicion, strCondMultiR, columnasBorrar);
		} finally {
			try {
				CloseObject.closeObject(conn, true);
			} catch (Exception e) {
				log.error("Problema cerrando conexion: " + e, e);
			}
		}
	}
	
	public String generaReporteSeguimientoObras(String fechaI, String fechaF, String idunidadresponsable, String idArea) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return ObraPublicaReportesManager.generaReporteSeguimientodeObraToExcel(conn, fechaI, fechaF, idunidadresponsable, idArea);
		} finally {
			try {
				CloseObject.closeObject(conn, true);
			} catch (Exception e) {
				log.error("Problema cerrando conexion: " + e, e);
			}
		}
	}
	
}
