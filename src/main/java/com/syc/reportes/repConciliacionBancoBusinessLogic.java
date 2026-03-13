package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.reportes.reportes;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteConciliacionManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class repConciliacionBancoBusinessLogic extends DataSourceManager {
	private static Logger	log	= Logger.getLogger( reportesBussinesObject.class );
	
	public repConciliacionBancoBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaReporteConciliacion(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			int idConciliacion = Integer.parseInt(req.getParameter("nConciliacion"));
			int nMes = Integer.parseInt(req.getParameter("nMes"));
			String  cBan = req.getParameter("cBan");
			
			file = ReporteConciliacionManager.generaReporteConciliacionManager(conn, idConciliacion, nMes, cBan, plantilla);

			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();
			f = null;
		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
		
	}
	
	public void generaReporteConciliados(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			int idConciliacion = Integer.parseInt(req.getParameter("nConciliacion"));
			int nMes = Integer.parseInt(req.getParameter("nMes"));
			String  cBan = req.getParameter("cBan");
			
			file = ReporteConciliacionManager.generaReporteConEdoCtaManager(conn, idConciliacion, nMes, cBan, plantilla);

			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();
			f = null;
					
		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
	}
	
	public void generaReporteConAuxiliar(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			int idConciliacion = Integer.parseInt(req.getParameter("nConciliacion"));
			int nMes = Integer.parseInt(req.getParameter("nMes"));
			String  cBan = req.getParameter("cBan");
			
			file = ReporteConciliacionManager.generaReporteConAuxiliarManager(conn, idConciliacion, nMes, cBan, plantilla);

			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();

		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
		
	}
	
	public void reporteConciliacion( HttpServletRequest req, HttpServletResponse resp, String strReport, String reportName, String cEsFiel ) throws Exception {
		Connection conn = null;
		
		try {
			conn = getConnection();
			String folio =  req.getParameter( "nConciliacion" );
			String rutaCompleta = strReport + "\\" + reportName;
			
			Map<String, Object> parms = new LinkedHashMap<String, Object>();
			reportes objReporte = new reportes();
			
			parms.put( "nConciliacion", folio );
			parms.put( "SUBREPORT_DIR", strReport );
			
			if ( cEsFiel.equalsIgnoreCase( "S" ) ) {
				objReporte.executeConciliaFIEL( conn, strReport, reportName, parms );
				ReporteConciliacionManager.actualizarEstatus(conn, cEsFiel, folio);
			} else {	
				objReporte.execute( conn, req, resp, rutaCompleta, parms );
			}
			
			conn.commit();

		} finally {
			if ( conn != null )
				try {

					conn.rollback();
				} catch ( SQLException exc ) {
					log.warn( "Realizando rollback: " + exc );
				}
			CloseObject.closeObject( conn );
		}
	}
	
	public void generaReporteConEdoCta(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			int idConciliacion = Integer.parseInt(req.getParameter("nConciliacion"));
			int nMes = Integer.parseInt(req.getParameter("nMes"));
			String  cBan = req.getParameter("cBan");
			
			file = ReporteConciliacionManager.generaReporteConEdoCtaManager(conn, idConciliacion, nMes, cBan, plantilla);

			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();

		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
		
	}
	
}
