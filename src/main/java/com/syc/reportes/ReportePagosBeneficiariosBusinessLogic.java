package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReportePagosBeneficiariosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.apache.log4j.Logger;

public class ReportePagosBeneficiariosBusinessLogic extends DataSourceManager {
	private static final Logger	log	= Logger.getLogger( ReportePagosBeneficiariosBusinessLogic.class );
	
	public ReportePagosBeneficiariosBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			String fechaInicio = req.getParameter("fecha_inicio");
			String fechaFin = req.getParameter("fecha_fin");
			String cContable = req.getParameter("cCentroContable");
			String ep = req.getParameter("ep");
			String rfc = req.getParameter("rfc");
			String estatus = req.getParameter("estatus");
			String cRazonSocial = new String( req.getParameter("cRazonSocial").getBytes("ISO-8859-1"),"UTF-8"   );//==null? "":req.getParameter("cRazonSocial");
			String cContrato = req.getParameter("cContrato") == null? "": req.getParameter("cContrato");
			String tipo = req.getParameter("reporteTipo");
			String cUR = req.getParameter("cUnidadResponsable");
			String usuario = req.getParameter("cUsuario");
			String ctaBanco = req.getParameter("cBanco");
			
			file = ReportePagosBeneficiariosManager.ReporteManager(conn, fechaInicio, fechaFin, cContable, ep, rfc,cRazonSocial, cContrato, estatus, tipo, cUR, usuario, ctaBanco, plantilla);
			
			File f = new File(file);
			log.info("-------Se genera el archivo " +  f.getName());
			resp.setContentType("application/octet-stream");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = resp.getOutputStream();
			log.info("Se envia el archivo al doDownload");
			Util.doDownload(out, file, file, "");

			conn.commit();
			out.flush();
			out.close();
			f =null;
		} catch (Exception e) {
			log.error(e, e);
			throw e;
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
