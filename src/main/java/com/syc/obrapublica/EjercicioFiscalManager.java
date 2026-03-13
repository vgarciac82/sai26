package com.syc.obrapublica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.gestion.util.DBConfigurator;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class EjercicioFiscalManager {

	private static final Logger	log	= Logger.getLogger(EjercicioFiscalManager.class);

	public static EjercicioFiscal getEjercicioFiscalActivo(Connection conn) throws Exception {
		ResultSet rsEF = null;
		PreparedStatement psEF = null;
		String queryEF = "SELECT aEjercicioFiscal, cActivo FROM   tejerciciofiscal  WHERE  cactivo = 1";
		EjercicioFiscal ejercicioActivo = null;

		try {
			psEF = conn.prepareStatement(queryEF);
			rsEF = psEF.executeQuery();

			if (rsEF.next())
				ejercicioActivo = new EjercicioFiscal(rsEF.getString("aEjercicioFiscal"), rsEF.getString("cActivo"));

			return ejercicioActivo;
		} finally {
			try {
				CloseObject.closeObject(rsEF, false);
				CloseObject.closeObject(psEF, false);
			} catch (Exception e) {
				log.warn(e, e);
			}
		}

	}
	
	public static DBConfigurator getEjercicioFiscal(Connection conn) throws Exception{
		String query = "SELECT * FROM dbo.tEjercicioFiscal (NOLOCK) WHERE cActivo = 1";
		PreparedStatement ps = null;
		ResultSet rs = null;
		DBConfigurator d = new DBConfigurator();
		
		try{
		
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				d.setDriverClassName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
				d.setUserName(rs.getString("cUserBD"));
				d.setPassword(rs.getString("cPassBD"));
				d.setUrl( "jdbc:jtds:sqlserver://" + rs.getString("cDireccionServer") +  ":" + rs.getString("cPuertoBD") + "/" + rs.getString("cNombreBD"));
				d.setEjercicio(rs.getInt("aEjercicioFiscal"));
				
			}
		} finally {
			CloseObject.closeObject(ps);
			CloseObject.closeObject(rs);
		} 
		
		return d;
	}
	
	public static List<DBConfigurator> getEjerciciosFiscales(Connection conn) throws Exception{
		String query = "SELECT * FROM dbo.tEjercicioFiscal (NOLOCK)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DBConfigurator> r = new ArrayList<DBConfigurator>();
		try{
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()){
				DBConfigurator d = new DBConfigurator();
				d.setDriverClassName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
				d.setUserName(rs.getString("cUserBD"));
				d.setPassword(rs.getString("cPassBD"));
				d.setUrl( "jdbc:jtds:sqlserver://" + rs.getString("cDireccionServer") +  ":" + rs.getString("cPuertoBD") + "/" + rs.getString("cNombreBD"));
				d.setEjercicio(rs.getInt("aEjercicioFiscal"));
				r.add(d);
			}
		} finally {
			CloseObject.closeObject(ps);
			CloseObject.closeObject(rs);
		} 
		
		return r;
	}
	
	public static DBConfigurator getEjercicioFiscalActivoDB(Connection conn,String dbName) throws Exception{
		
		String query = "SELECT * FROM tEjercicioFiscal (NOLOCK) WHERE cActivo = 1";
		PreparedStatement ps = null;
		ResultSet rs = null;
		DBConfigurator r = new DBConfigurator();
		
		try{
			
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				r.setDriverClassName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
				r.setUserName(rs.getString("cUserBD"));
				r.setPassword(rs.getString("cPassBD"));
				r.setUrl( "jdbc:jtds:sqlserver://" + rs.getString("cDireccionServer") +  ":" + rs.getString("cPuertoBD") + "/" + dbName );
				r.setEjercicio(rs.getInt("aEjercicioFiscal"));
				
			}
			
		} finally {
			CloseObject.closeObject(ps);
			CloseObject.closeObject(rs);
		} 
		
		return r;
	}
public static DBConfigurator getPropertiesDB(Connection conn,String dbName,String cEjercicio) throws Exception{
		
		String query = "SELECT * FROM tEjercicioFiscal (NOLOCK) where aEjercicioFiscal=? and cNombreBD=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		DBConfigurator r = new DBConfigurator();
		
		try{
			
			ps = conn.prepareStatement(query);
			ps.setString( 1, cEjercicio );
			ps.setString( 2, dbName );
			rs = ps.executeQuery();
			
			while (rs.next()){
				r.setDriverClassName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
				r.setUserName(rs.getString("cUserBD"));
				r.setPassword(rs.getString("cPassBD"));
				r.setUrl( "jdbc:jtds:sqlserver://" + rs.getString("cDireccionServer") +  ":" + rs.getString("cPuertoBD") + "/" + dbName );
				r.setEjercicio(rs.getInt("aEjercicioFiscal"));
			}
			
		} finally {
			CloseObject.closeObject(ps);
			CloseObject.closeObject(rs);
		} 
		
		return r;
	}
}
