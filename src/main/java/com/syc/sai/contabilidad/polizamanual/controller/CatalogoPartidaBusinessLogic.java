package com.syc.sai.contabilidad.polizamanual.controller;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.polizamanual.CatalogoPartida;
import com.syc.sai.contabilidad.polizamanual.CatalogoPartidaEngineException;
import com.syc.sai.contabilidad.polizamanual.model.CatalogoPartidaManager;

public class CatalogoPartidaBusinessLogic extends DataSourceManager {

	Logger	log	= Logger.getLogger(CatalogoPartidaBusinessLogic.class);

	public List<CatalogoPartida> readCatalogoPartidaBy(String restrictions) throws CatalogoPartidaEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return CatalogoPartidaManager.readCatalogoPartidaBy(conn, restrictions);
		} catch (Exception e) {
			throw new CatalogoPartidaEngineException(e);
		} finally {
			
		}
	}
		
	public int saveOrUpdateCatalogoPartida(HttpServletRequest req) throws CatalogoPartidaEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			CatalogoPartida catalogoPartido = new CatalogoPartida();
			catalogoPartido.setCpartida(req.getParameter("cpartida"));
			catalogoPartido.setDpartida(req.getParameter("dpartida"));
			
			req.getSession().setAttribute("catalogoPartido",catalogoPartido);			
			return CatalogoPartidaManager.saveOrUpdateCatalogoPartida(conn, catalogoPartido);
		} catch (Exception e) {
			throw new CatalogoPartidaEngineException(e);
		} finally {
			
		}

	}	
	
	public List<CatalogoPartida> autocompleteCatalogoPartida(HttpServletRequest req)
		throws CatalogoPartidaEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			String nGrupo = (String)req.getSession().getAttribute("nGrupo");
			String nSubGrupo = (String)req.getSession().getAttribute("nSubGrupo");
			String nEvento = (String)req.getSession().getAttribute("nEvento");
			String cPartida = req.getParameter("term");
			String subQuery = "(select cPartida from dbo.tEventoManual where cIdGrupoEvento=%s and cIdSubGrupoEvento=%s and cIdEventoManual=%s and cModulo != 'CAJA')";			
			subQuery = String.format(subQuery, nGrupo, nSubGrupo, nEvento);
			String restrictions = " cPartida in  "+subQuery+" AND ";			
			restrictions = " cPartida like '"+cPartida+"%'";				
			return CatalogoPartidaManager.readCatalogoPartidaBy(conn, restrictions);
			
		} catch (Exception e) {
			throw new CatalogoPartidaEngineException(e);
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

