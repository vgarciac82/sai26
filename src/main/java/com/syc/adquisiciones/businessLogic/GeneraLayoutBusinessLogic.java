package com.syc.adquisiciones.businessLogic;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.manager.GeneraLayoutManager;
import com.syc.dsmngr.DataSourceManager;


public class GeneraLayoutBusinessLogic extends DataSourceManager {
	private static Logger log = Logger.getLogger(GeneraLayoutBusinessLogic.class);
	
	public StringBuffer generaLayoutApartado(String jndiName,String cadenaFolios)throws Exception {
		log.info( "Inicia la generación de layouts" );
		Connection conn=null;
		GeneraLayoutManager manager=null;
		StringBuffer archivoLayout = null;
		try {
			conn=getConnection(jndiName);
			manager=new GeneraLayoutManager();
			archivoLayout = new StringBuffer();
			//SE obtienen los datos del encabezado
			archivoLayout=manager.getEncabezadoLayout( conn, cadenaFolios );
			if(null==archivoLayout || archivoLayout.length()==0) {
				throw new Exception("No hay datos en el encabezado y detalle");
			}
			//Guarda la generación de layouts
			manager.guardaDatosLayoutApartado( conn, cadenaFolios );
			//Actualiza estatus
			manager.updateEstatusIntegraRequi( conn, cadenaFolios,1 );
			conn.commit();
		} catch ( SQLException e ) {
			if(conn!=null) {
				conn.rollback();
			}
			log.error( e );
			throw e;
		}finally {
			if(conn!=null) {
				conn.close();
			}
			conn=null;
			manager=null;
		}
		return archivoLayout;
	}
	public Respuesta InitEstatusLayoutApartado(String jndiName,String cadenaFolios)throws Exception {
		log.info( "Inicia estatus de layouts" );
		Connection conn=null;
		GeneraLayoutManager manager=null;
		Respuesta resp=null;
		try {
			conn=getConnection(jndiName);
			manager=new GeneraLayoutManager();
			resp=new Respuesta();
			//delete datos de layout
			manager.deleteLayoutIntegraRequi( conn, cadenaFolios );
			//Actualiza estatus
			manager.updateEstatusIntegraRequi( conn, cadenaFolios,0 );
			resp.setMsg( "Estatus actualizados" );
			resp.setResp( true );
			conn.commit();
		} catch ( SQLException e ) {
			if(conn!=null) {
				conn.rollback();
			}
			log.error( e );
			throw new Exception(e);
		}finally {
			if(conn!=null) {
				conn.close();
			}
			conn=null;
			manager=null;
		}
		return resp;
	}
}
