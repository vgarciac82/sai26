package com.syc.fortimax.core;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.bitacora.core.BitacoraOperacionDoctosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class DocumentoBussinessLogic extends DataSourceManager {

	private static final Logger log = Logger.getLogger( DocumentoBussinessLogic.class );

	public boolean limpiaDocumento( String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento ) throws Exception {
		Connection conn = null;
		boolean exito = false;
		try {
			conn = getConnection();
			DocumentoManager.limpiaDocumento( conn, titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento );
			conn.commit();
			exito = true;
			return exito;
		} catch ( Exception e ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( "Error realizando rollback: " + e2, e2 );
				}
			throw e;
		} finally {
			CloseObject.closeObject( conn, false );
		}
	}

	public Documento buscaDocumento( Fortimax fortimax ) throws Exception {

		Connection conn = null;
		try {
			conn = getConnection();
			return DocumentoManager.buscaDocumento( conn, fortimax );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public static Documento versionaDocumento( Connection conn, Usuario u, Documento d ) throws FortimaxException, SQLException {
		if ( d.getEsVersion() > 0 )
			throw new FortimaxException( "No se puede limpiar una version." );

		Carpeta c = CarpetaManager.getCarpeta( conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre() );

		if ( "CFDI".equalsIgnoreCase( c.getNombreCarpeta() ) )
			throw new FortimaxException( "No se pueden modificar facturas. Solicite apoyo con el administrador." );

		int versionDocumento = DocumentoVersionManager.siguienteVersionDocumento( conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getNombreDocumento() );

		String nombreDocumento = d.getNombreDocumento();
		String nombreDocumentoNuevo = nombreDocumento + "_" + "V" + String.valueOf( versionDocumento );

		DocumentoManager.cambiaNombreDocumento( conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento(), nombreDocumentoNuevo );
		DocumentoManager.marcaDocumentoVersion( conn, d );

		d.setIdDocumento( -1 );
		d.setNumeroPaginas( 0 );
		d.setNumeroAccesos( 0 );
		d.setTamanoBytes( 0 );
		d.setEsVersion( 0 );

		DocumentoManager.insertDocumento( conn, d );

		BitacoraOperacionDoctosManager.insertaBitacora( conn, u.getLogin(), d.getTituloAplicacion(), -1, -1, 11, "Se genero la version " + versionDocumento + " del documento " + nombreDocumento + "[" + d.toFortimax() + "]" );

		return d;
	}
}
