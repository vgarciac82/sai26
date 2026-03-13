package com.syc.ejercido.pagado;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;

public class CompromisoNOMBussinessLogic extends DataSourceManager{

	public CompromisoNOMBussinessLogic(String jniName) {

		super.init(jniName);
	}
	
	public ArrayList<String> buscaCompromisos(String lista_caNoCompromiso, String lista_cIdContrato) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			arrListaComp = CompromisoNOMManager.BuscaCompromisos(conn, lista_cIdContrato);
		}
		catch (SQLException e) {
			if(conn != null){
				conn.rollback();
				throw  new GestionException(e.getMessage());
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
		return arrListaComp;
	}
	
	public boolean estatusCompromiso(String lista_caNoCompromiso) throws FileNotFoundException, IOException, SQLException, GestionException {
		boolean cargandoArchivo = false;
		int renglonesActualizados = 0;
		Connection conn = null;
		try{
			conn=getConnection();
			renglonesActualizados = CompromisoNOMManager.updateHeaderCompromisos(conn,lista_caNoCompromiso);
			if(renglonesActualizados > 0){
				cargandoArchivo = true;
			}
		}
		catch (SQLException e) {
			if(conn != null){
				conn.rollback();
				throw  new GestionException(e.getMessage());
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return cargandoArchivo;
	}
	
	public boolean insertaArchivoFiltrado(StringBuffer archivoFiltrado) throws FileNotFoundException, IOException, SQLException, GestionException {
		boolean cargandoArchivo = false;
		Connection conn = null;
		try{
			conn=getConnection();
			cargandoArchivo = CompromisoNOMManager.insertaFiltrados(conn,archivoFiltrado);
		}
		catch (SQLException e) {
			if(conn != null){
				conn.rollback();
				throw  new GestionException(e.getMessage());
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return cargandoArchivo;
	}
	
	public String fAplicacionCancelados(String compromisoCancelado) throws FileNotFoundException, IOException, SQLException, GestionException {
		String fAplicacion = "";
		Connection conn = null;
		try{
			conn=getConnection();
			fAplicacion = CompromisoNOMManager.buscaFechaAplicacionCancelados(conn,compromisoCancelado);
		}
		catch (SQLException e) {
			if(conn != null){
				conn.rollback();
				throw  new GestionException(e.getMessage());
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return fAplicacion;
	}
	
	public boolean estatusCancelado(String caNoCompromiso) throws FileNotFoundException, IOException, SQLException, GestionException {
		boolean cargandoArchivo = false;
		Connection conn = null;
		try{
			conn=getConnection();
			cargandoArchivo = CompromisoNOMManager.updateHeaderCompromisosRealimentacion(conn, 4 , caNoCompromiso);
		}
		catch (SQLException e) {
			if(conn != null){
				conn.rollback();
				throw  new GestionException(e.getMessage());
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return cargandoArchivo;
	}
}
