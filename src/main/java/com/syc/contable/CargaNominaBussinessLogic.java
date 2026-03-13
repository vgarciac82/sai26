package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.contable.core.CargaNominaManager;
import com.syc.crud.dsmngr.DataSourceManager;

public class CargaNominaBussinessLogic extends DataSourceManager{
	boolean regInsertado = false;
	private static Logger log = Logger.getLogger(CargaNominaBussinessLogic.class);

	public CargaNominaBussinessLogic(String jniName) {

		super.init(jniName);
	}
	
	public String buscaClaveCNA(String EP) throws SQLException{
		String claveCNA = "";
		Connection conn = null;
		try{
			conn = getConnection();
			claveCNA = CargaNominaManager.buscaClaveCNA(conn, EP); 
		}
		catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return claveCNA;
	}
	
	public boolean insertaLineaNomina(Integer ID_Caso, String EP, String claveInterna, String concepto, String movimiento, Double saldo, String claveCNA) throws SQLException{
		 boolean regInsertado = false;
		 Connection conn = null;
			
		try{
			conn = getConnection();                   
			regInsertado = CargaNominaManager.insertaLineaNomina(conn, ID_Caso, EP, claveInterna, concepto, movimiento, saldo, claveCNA);				
		}
		catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
				conn.rollback();
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return regInsertado;
	} 
	
	public int borraRegistros(Integer ID_Caso, String listaEPs) throws SQLException{
		int regBorrados = 0;
		 Connection conn = null;
			
		try{
			conn = getConnection();                   
			regBorrados = CargaNominaManager.borraRegistrosNomina(conn, ID_Caso, listaEPs);				
		}
		catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
				
				conn.rollback();
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return regBorrados;
	}
}
