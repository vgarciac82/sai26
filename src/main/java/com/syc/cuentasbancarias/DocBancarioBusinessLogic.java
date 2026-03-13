package com.syc.cuentasbancarias;


import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.axtel.proveedores.exception.ProveedorException;
import com.syc.altaproveedor.AltaProveedorManager;
import com.syc.altaproveedor.DatosCtaBancaria;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class DocBancarioBusinessLogic extends DataSourceManager {

	private static final Logger log = Logger.getLogger( DocBancarioBusinessLogic.class );

	public DocBancarioBusinessLogic( String jniName ) {
		super.init( jniName );
	}

	public int insertaArchivo( Caso c, Usuario u, String rutaArchivo, String cuenta, CuentaBancaria cuentaBancaria ) throws Exception {
		Connection conn = null;
		int insertados = 0;

		try {
			conn = getConnection();

			if ( conn.getAutoCommit() )
				conn.setAutoCommit( false );

			if ( c.getIdGabinete() <= 0 )
				throw new Exception( "No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas." );

			Carpeta carpeta = DocBancarioManager.obtenCarpetaDestino( conn, c, "Comprobantes Bancarios", u.getLogin() );

			insertados += DocBancarioManager.insertaComprobante( conn, rutaArchivo, carpeta, c, u.getLogin(), cuenta, cuentaBancaria );

			conn.commit();
			return insertados;
		} catch ( Exception e ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.error( "Problemas realizando rollback: " + e2, e2 );
				}
			throw e;
		} finally {
			CloseObject.closeObject( conn, false );
		}
	}
	
	
	public List<Map<String, String>> getCuentasBeneficiario( String rfc ) throws ProveedorException {
		Connection conn = null;
		try {
			conn = getConnection();
			List<Map<String, String>> cuentas = DocBancarioManager.getCuentasBeneficiario( conn, rfc );
			return cuentas;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new ProveedorException( e.toString(), e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}
	
	public List<Map<String, String>> getCuentasTemporales( String folio ) throws ProveedorException {
		Connection conn = null;
		try {
			conn = getConnection();
			List<Map<String, String>> cuentas = DocBancarioManager.getCuentasTemporales( conn, folio );
			return cuentas;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new ProveedorException( e.toString(), e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public void eliminaCtaBancaria( String folio, String clabeEliminar ) throws ProveedorException {
		Connection conn = null;

		try {

			conn = getConnection();

			Caso c = new Caso( folio );
			c = CasoManager.select( conn, c );

			DatosCtaBancaria cta = AltaProveedorManager.buscaCuentaBancariaTemp( conn, folio, clabeEliminar );
			String nombreDocumento = cta.getcNameBanco() + "-" + cta.getcCuentaBancaria();
			Documento ctaDocumento = DocumentoManager.buscaDocumento( conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), nombreDocumento );

			if ( ctaDocumento != null ) {
				DocumentoManager.limpiaDocumento( conn, ctaDocumento.getTituloAplicacion(), ctaDocumento.getIdGabinete(), ctaDocumento.getIdCarpetaPadre(), ctaDocumento.getIdDocumento() );
				DocumentoManager.delete( conn, ctaDocumento.getTituloAplicacion(), ctaDocumento.getIdGabinete(), ctaDocumento.getIdCarpetaPadre(), ctaDocumento.getIdDocumento() );
				AltaProveedorManager.deleteCtaTmp( conn, cta );

			}
			conn.commit();
		} catch ( Exception e ) {
			Util.rollback( conn );
			throw new ProveedorException( e );
		} finally {
			CloseObject.closeObject( conn );
		}

	}
}
