package com.syc.sai.fonden.model;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.fonden.Fonden;
import com.syc.sai.fonden.FondenEngineException;

public class FondenBusinessLogic extends DataSourceManager {
	Logger	log	= Logger.getLogger(FondenBusinessLogic.class);

	public List<Fonden> readFonden(Integer nIdFonden) throws FondenEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return FondenManager.readFonden(conn, nIdFonden);
		} catch (Exception e) {
			throw new FondenEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

	}
	
	public int saveOrUpdateFonden(HttpServletRequest req) throws FondenEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			String sFecha = req.getParameter("dfechaCaptura");
			java.util.Date dFecha = Date.valueOf( sFecha );
			
			Fonden fonden = new Fonden();
			fonden.setCidFonden(new Integer(req.getParameter("cidFonden")));
			fonden.setCusuarioCreador(req.getParameter("cusuarioCreador"));					
			fonden.setDfechaCaptura( (Date) dFecha );
			fonden.setCdescripcion(req.getParameter("cdescripcion"));
			fonden.setNimporteAnual(new Double(req.getParameter("nimporteAnual").replace("$", "").replace(",", "")));
			return FondenManager.saveOrUpdateFonden(conn, fonden);
		} catch (Exception e) {
			throw new FondenEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

	}	
}
