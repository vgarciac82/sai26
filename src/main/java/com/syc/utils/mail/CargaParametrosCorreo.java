package com.syc.utils.mail;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;

public class CargaParametrosCorreo extends DataSourceManager {

	private static Logger	log	= Logger.getLogger(CargaParametrosCorreo.class);
	public boolean			correoProduccion	          = false;
	private String				jniName				= null;
	

	public CargaParametrosCorreo(String jniName) {

		super.init(jniName);
	}
	
	
	public ParametrosCorreo CargaParametros( String  TipoProceso ){
		ParametrosCorreo ParamMail = new ParametrosCorreo();
		Connection conn = null;
		try{
			conn = getConnection();
//			CargaParametrosBD CPBD = new CargaParametrosBD();
			try {
//			ParamMail = CPBD.getParameterDB(conn, TipoProceso );
			} catch(Exception e){
				System.out.println("Error en facade de ParametrosCorreo ------> "+ e.getMessage() );
				e.fillInStackTrace();
			}
	
		}catch (Exception e){
			System.out.println("Error en facade de ParametrosCorreo ------> "+ e.getMessage() );
			e.fillInStackTrace();
		}
		
		return ParamMail;	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
