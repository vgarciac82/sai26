package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.syc.contable.core.MultiReportePagosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;

public class MultiReportePagosBussinessLogic extends DataSourceManager{

	
	private static Logger log = Logger.getLogger(AdecuacionBusinessLogic.class);

	public MultiReportePagosBussinessLogic(String jniName) {

		super.init(jniName);
	}
	
	public ArrayList<String> buscaCompromisos(String szTemp) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			arrListaComp = MultiReportePagosManager.BuscaCompromisos(conn, szTemp);
			
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
