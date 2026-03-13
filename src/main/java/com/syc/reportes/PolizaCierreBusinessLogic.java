package com.syc.reportes;

import java.io.File;
import java.sql.Connection;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.contable.AccountingEngine;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
//import com.syc.gestion.util.Util;
import com.syc.reportes.core.PolizaCierreManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import common.Logger;

public class PolizaCierreBusinessLogic extends DataSourceManager {

	private static final Logger log = Logger.getLogger(PolizaCierreBusinessLogic.class);
	
	public PolizaCierreBusinessLogic(String jniName) {
		super.init(jniName);
	}
	
	public void cosultaTemporal(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			file = PolizaCierreManager.consultaTemporal(conn, resp, plantillas);

			File f = new File (file);
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

	public float guardaVersion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Connection conn = null;
		float dif;
		int id_documento;
		
		try {
			conn = getConnection();
			
			String usuario = req.getParameter("usuario");
			String ur = req.getParameter("cUnidadResponsable");
			String cc = req.getParameter("cCentroContable");

			id_documento = PolizaCierreManager.obtieneFolioDocumento(conn);
			dif = PolizaCierreManager.creaPolizaCierreManager(conn, usuario, ur, cc, id_documento);

			if (dif == 0) {
				AccountingEngine ac = new AccountingEngine();
				ac.setValidaInsuficienciaDeSaldo(true);
				ac.makeAccountingApplicationWithoutEvent(conn, "POLIZACIERRE", String.valueOf(id_documento), "tPolizaCierreEncabezado", "tPolizaCierreDetalle", "nFolioPolizaCierre");
				conn.commit();
			} else
				throw new Exception("La poliza esta descuadrada! Diferencia entre cargos y abonos: " + dif);

			return dif;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Ocurrio un error al realizar rollback:" + e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}

	}
	
	
	
}
