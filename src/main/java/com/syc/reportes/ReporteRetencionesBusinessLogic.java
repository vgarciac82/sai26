package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteRetencionesManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteRetencionesBusinessLogic extends DataSourceManager {
	public static final Logger log = Logger.getLogger( ReporteRetencionesBusinessLogic.class );
	public ReporteRetencionesBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaReporteRetenciones(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas, String conEP) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			String fechaInicio = req.getParameter("fecha_inicio");
			String fechaFin = req.getParameter("fecha_fin");
			int tipoAjena = Integer.parseInt(req.getParameter("tipo_ajena"));
			String centroContable = req.getParameter("cCentroContable");
			String UR = req.getParameter("cUnidadResponsable");
			String coord = req.getParameter("Coord");
			
			if("S".equals( coord )) {
			
				file = ReporteRetencionesManager.generaReporteRetencionesCC(conn, fechaInicio, fechaFin, tipoAjena, centroContable, UR, plantillas);
				 
			}else {
				
				file = ReporteRetencionesManager.generaReporteRetenciones(conn, fechaInicio, fechaFin, tipoAjena, centroContable, UR, plantillas, conEP);
				
			}
	
			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
			log.debug( "===== Se genero el reporte en Excel =====" );
			
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
	
	

	public void generaReporteRetencionesAcc(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillasAcumulada) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			String fechaInicio = req.getParameter("fecha_inicio");
			String fechaFin = req.getParameter("fecha_fin");
			String ur = req.getParameter( "cUnidadResponsable" );

			file = ReporteRetencionesManager.generaReporteRetencionesAcc(conn, fechaInicio, fechaFin,  ur, plantillasAcumulada);

			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

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

	public void generaResumenRetenciones(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillasResumen) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			String fechaInicio = req.getParameter("fecha_inicio");
			String fechaFin = req.getParameter("fecha_fin");
			int tipoAjena = Integer.parseInt(req.getParameter("tipo_ajena"));
			String centroContable = req.getParameter("cCentroContable");
			String ur = req.getParameter("cUnidadResponsable");

			file = ReporteRetencionesManager.generaResumenRetenciones(conn, fechaInicio, fechaFin, tipoAjena, centroContable, ur,  plantillasResumen);

			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

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
