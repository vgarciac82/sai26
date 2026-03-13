package com.syc.sai.procesos;


import java.sql.Connection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.util.Util;


public class ReemplazaFacturas {

	private static final Logger log = Logger.getLogger( ReemplazaFacturas.class );

	public static void main( String args[] ) throws Exception {

		Connection conn = null;

		String rutaArchivo = args[0];
		String rfc = args[1];
		boolean esNotaCredito = "S".equalsIgnoreCase( args[2] );
		Integer idCaso = new Integer( args[3] );
		
		
		String msgRetorno = new String();
		try {
			
			conn = Util.getStandAloneConnection();
			
			Usuario u = new Usuario();
			u.setLogin( "admin" );
			
			Caso c = new Caso();
			c.setIdCaso( idCaso );
			
			boolean validaPagos = true;
			
			FacturaBusinessLogic fbl = new FacturaBusinessLogic();
			fbl.setNotificaErroresSAT( false );
			fbl.setNotificaErroresEFA( false );
			fbl.setPermiteVersionAnterior( true );
			fbl.setDirectorioTemporal( System.getProperty( "java.io.tmpdir" ) );	
			
			c = CasoManager.select( conn, c ); 
			u = UsuarioManager.select( conn, u );
			
			String tipoPago = c.getTipoCaso().getGavetaAsociada();
			String tipoModulo = "";
			int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
			
			ExtraccionFacturas ef = fbl.extraeFacturas(conn, rutaArchivo, tipoPago, StringUtils.trim(rfc), nFolioPago, validaPagos, esNotaCredito, tipoModulo);

			if (ef.getErrores().size() == 0) {
				Map<String, ComponentesFactura> facturas = ef.getFacturas();
				FacturaManager.insertaInformacionFacturas( conn, c.getTipoCaso().getGavetaAsociada(), nFolioPago, facturas, esNotaCredito );
				FacturaManager.insertaArchivosFactura(conn, c, u, facturas, esNotaCredito);
			} else {
				String token = "";
				for (int i = 0; i < ef.getErrores().size(); i++) {
					msgRetorno += token + ef.getErrores().get(i);
					token = "<br>";
				}
				throw new Exception(msgRetorno);
			}
			
			conn.commit();
		} catch ( Exception e ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( "Problemas realizando rollback: " + e2 );
				}
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}

	}
}
