package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.syc.contable.core.ExtraeOAManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;

public class ExtraeOABussinessLogic extends DataSourceManager{

	
	private static Logger log = Logger.getLogger(AdecuacionBusinessLogic.class);

	public ExtraeOABussinessLogic(String jniName) {

		super.init(jniName);
	}
	
	public ArrayList<String> buscaCompromisos(String szTemp, String szTabla) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			arrListaComp = ExtraeOAManager.BuscaCompromisos(conn, szTemp, szTabla);
			
		}
		catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
				conn.rollback();
				//throw  new GestionException(e.getMessage());
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return arrListaComp;
	}
}                                                                                               																																																																																								

