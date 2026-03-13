package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;

public class PagosNominaBussinessLogic extends DataSourceManager{

	
	//private static Logger log = Logger.getLogger(AdecuacionBusinessLogic.class);

	public PagosNominaBussinessLogic(String jniName) {

		super.init(jniName);
	}
	
	public ArrayList<String> buscaCompromisos(String listaFolios, String cConcepto, String listaFechas, String ListaLeyendas, String sUsuario, String solicitudPago, String sCuentaBancaria) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			arrListaComp = PagosNominaManager.BuscaCompromisos(conn, listaFolios, cConcepto, listaFechas, ListaLeyendas, sUsuario, solicitudPago, sCuentaBancaria);
			
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

	public boolean ActualizaStatus(String listaFolios) throws Exception{
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			PagosNominaManager.UpdateStatus(conn,listaFolios);
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
		return true;
	}

	public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds, String sConcepto) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			arrListaComp = PagosNominaManager.CreaDocumentacionComprobatoria(conn, listaIds, sConcepto);
			PagosNominaManager.updateHeaderCompromisos(conn,listaIds);
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

	public boolean actualizaCompromisos(Integer nEnviadoSICOP, String caNoCompromiso) throws Exception{
		boolean regActualizado = false;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			regActualizado = PagosNominaManager.updateHeaderCompromisosRealimentacion(conn,  nEnviadoSICOP, caNoCompromiso);
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
		return regActualizado;
	}                                                                                                      																																																																																								

	public boolean insertaLineaLayout(String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException{
		boolean regInsertado = false;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			regInsertado = PagosNominaManager.insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso,  cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
			
			conn.commit();
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
	
	public ArrayList<String> buscaCLCNomina(String clcNomina) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			arrListaComp = PagosNominaManager.BuscaCLCNomina(conn, clcNomina);
			
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
	
	public boolean actualizaStatusGenerar(String listaFolios) throws Exception{
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			PagosNominaManager.UpdateStatusGenerar(conn,listaFolios);
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
		return true;
	}
	
	public ArrayList<String> buscaEjercidoComparativo(String tipoConsulta) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			if(tipoConsulta.equals("consultaEjercidoComparativo")){
				
				arrListaComp = PagosNominaManager.BuscaEjercidoComparativo(conn);
			
			}else{
			
				arrListaComp = PagosNominaManager.BuscaEjercidoComparativoCapitulo(conn);
			
			}	
			
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
}
