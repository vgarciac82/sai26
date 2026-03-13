package com.syc.sai.contabilidad.polizamanual.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.sai.contabilidad.polizamanual.CatalogoPartida;
import com.syc.sai.contabilidad.polizamanual.CatalogoPartidaEngineException;

public class CatalogoPartidaManager {
	private static Logger	log	= Logger.getLogger(CatalogoPartidaManager.class);
	
	public static CatalogoPartida readCatalogoPartida(
		Connection conn, 
		String cPartida){
		
		String restrictions = " cPartida = "+ cPartida;		
		List<CatalogoPartida> l;
		try {
			l = readCatalogoPartidaBy(conn, restrictions);
			
			if(!l.isEmpty()){
				return l.get(0);
			}else{
				return null;
			}
		} catch (CatalogoPartidaEngineException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<CatalogoPartida> readCatalogoPartidaBy(Connection conn, String restrictions) throws CatalogoPartidaEngineException {
		PreparedStatement pStatement = null;
		ResultSet rs = null;
		List<CatalogoPartida> l = new ArrayList<CatalogoPartida>();
		try {
			String qry = "";			
			qry = "select * from tCatalogoPartida where "+restrictions+";";
			
			pStatement = conn.prepareStatement(qry);
			int cnt = 1;
			rs = pStatement.executeQuery();

			while (rs.next()) {
				l.add(extraeCatalogoPartida(rs));
			}
			return l;
		} catch (Exception e) {
			throw new CatalogoPartidaEngineException(e);
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando PreparedStatement " + e2.toString());
				}
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando ResultSet " + e2.toString());
				}
		}
	}
	
	public static int saveCatalogoPartida(Connection conn, CatalogoPartida catalogoPartido) throws CatalogoPartidaEngineException {
		PreparedStatement pStatement = null;
		ResultSet rs = null;		
		try {
                        String qry = "INSERT INTO tCatalogoPartida (cpartida, dpartida) values( ?,? );";
                        pStatement = conn.prepareStatement(qry);
			int cnt = 1;			
                        pStatement.setString(cnt++,catalogoPartido.getCpartida());
				pStatement.setString(cnt++,catalogoPartido.getDpartida());
				
                        int nRows = pStatement.executeUpdate();
			conn.commit();
			return nRows;
		} catch (Exception e) {
			throw new CatalogoPartidaEngineException(e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando PreparedStatement " + e2.toString());
				}
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando ResultSet " + e2.toString());
				}
		}
	}

	public static int updateCatalogoPartida(Connection conn, CatalogoPartida catalogoPartido) throws CatalogoPartidaEngineException {
		PreparedStatement pStatement = null;
		ResultSet rs = null;		
		try {
			String qry = "UPDATE tCatalogoPartida SET dpartida=? where cpartida=?;";
			pStatement = conn.prepareStatement(qry);
			int cnt = 1;			
			pStatement.setString(cnt++,catalogoPartido.getDpartida());
			pStatement.setString(cnt++,catalogoPartido.getCpartida());
				
			int nRows = pStatement.executeUpdate();
			conn.commit();
			return nRows;
		} catch (Exception e) {
			throw new CatalogoPartidaEngineException(e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando PreparedStatement " + e2.toString());
				}
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando ResultSet " + e2.toString());
				}
		}
	}
	
	public static int saveOrUpdateCatalogoPartida(Connection conn, CatalogoPartida catalogoPartida) throws CatalogoPartidaEngineException{
		if(readCatalogoPartida(conn,catalogoPartida.getCpartida())!=null){
			return saveCatalogoPartida(conn,catalogoPartida);
		}else{
			return updateCatalogoPartida(conn, catalogoPartida);
		}
	}
	
	private static CatalogoPartida extraeCatalogoPartida(ResultSet rs)
			throws CatalogoPartidaEngineException {
		try {
			CatalogoPartida pojo = new CatalogoPartida();
			pojo.setCpartida(rs.getString("cpartida"));
			pojo.setDpartida(rs.getString("dpartida"));
			
			return pojo;
		} catch (Exception e) {
			throw new CatalogoPartidaEngineException(e);
		}
	}
	
}

