package com.syc.contable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.syc.contable.core.PagosDiversosRGManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;

public class PagosDiversosRGBussinessLogic extends DataSourceManager{

	
	private static Logger log = Logger.getLogger(AdecuacionBusinessLogic.class);
	private String	folioGenerator;

	public PagosDiversosRGBussinessLogic(String jniName, String folioGenerator) {

		super.init(jniName);
		this.folioGenerator = folioGenerator;
	}
	
	public String getUE( String sCB) throws Exception{
		PreparedStatement pstmntH = null;
		Connection conn=null;
		ResultSet rs3 = null;
		conn=getConnection();
		String sRet = "";
		
		String Sql3 = " select strUnidadEjecutora from tUECuentasBancarias where strclabe = '" + sCB + "'";
		pstmntH = conn.prepareStatement(Sql3);
		rs3 = pstmntH.executeQuery();
		if (rs3.next()){
			sRet = rs3.getString(1);
		}
		rs3.close();
		return sRet;
	}
	
	
	
	public ArrayList<String> buscaPagosDiversosRGIntegrados(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, Usuario usuario, String pTimeStamp) throws Exception{
		ArrayList<String> arrListaComp = null;
		Connection conn=null;
		try{
			conn=getConnection();
			arrListaComp = PagosDiversosRGManager.buscaPagosDiversosRGIntegrados2(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, usuario, pTimeStamp, folioGenerator );
			conn.commit();
		}
		catch (SQLException e) {
			if(conn != null){
				log.error(e,e);
				try{
					conn.rollback();
				}catch (Exception e3) {
					log.error("Problemas haciendo rollback "  + e3, e3);
				}
				throw  new GestionException(e.getMessage());
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
	
	public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds, String sTimeStamp, boolean bIntegra) throws Exception{
		ArrayList<String> arrListaComp = null;
		Connection conn=null;
		
		try{
			conn=getConnection();
			arrListaComp = PagosDiversosRGManager.CreaDocumentacionComprobatoria(conn, listaIds, sTimeStamp, bIntegra);
			PagosDiversosRGManager.updateHeaderPagosEnvioSICOP(conn,listaIds);
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
		return arrListaComp;
	}
	
	public boolean ActualizaStatus(String listaFolios, String usuario) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		Boolean Actualizado = null; 
		try{
			conn=getConnection();
			PagosDiversosRGManager.UpdateStatus(conn,listaFolios,usuario);
			Actualizado = true;
		}
		catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
				conn.rollback();
				//throw  new GestionException(e.getMessage());
			Actualizado = false;
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return Actualizado;
	}
}
