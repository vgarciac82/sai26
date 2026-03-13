package com.syc.sai.procesosAutomaticos;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.util.Util;


public class EliminaCasosAbandonados {

	private final CasoBusinessLogic cbl = new CasoBusinessLogic();

	private class CasoTramite {

		private int		idTC;
		private String	query;
		private String	tcDescripcion;

		public int getIdTC() {
			return idTC;
		}

		public String getQuery() {
			return query;
		}

		public String getTcDescripcion() {
			return tcDescripcion;
		}

		public void setIdTC( int idTC ) {
			this.idTC = idTC;
		}

		public void setQuery( String query ) {
			this.query = query;
		}

		public void setTcDescripcion( String tcDescripcion ) {
			this.tcDescripcion = tcDescripcion;
		}

		@Override
		public String toString() {
			return "CasoTramite [idTC=" + idTC + ", query=" + query + ", tcDescripcion=" + tcDescripcion + "]";
		}

	}

	private class OperacionTramite {

		private String	coResponsable;
		private int		idOper;

		public String getCoResponsable() {
			return coResponsable;
		}

		public int getIdOper() {
			return idOper;
		}

		public void setCoResponsable( String coResponsable ) {
			this.coResponsable = coResponsable;
		}

		public void setIdOper( int idOper ) {
			this.idOper = idOper;
		}

	}

	public static void main( String[] args ) throws Throwable {
		System.out.println( "Hola Mundo!" );
		long start = System.currentTimeMillis();
		System.out.println( "Iniciando depuracion de tramite abandonados." );
		EliminaCasosAbandonados eliminador = new EliminaCasosAbandonados();
		int eliminados = eliminador.eliminaCasos();
		long stop = System.currentTimeMillis();
		System.out.println( String.format( "Terminada la depuracion en [%d] segundos, [%d] casos se movieron a consulta", ( stop - start ) / 1000, eliminados ) );
	}

	private void closeObject( Object obj ) {
		try {
			if ( obj != null ) {
				if ( obj instanceof Connection )
					( ( Connection ) obj ).close();
				else if ( obj instanceof ResultSet )
					( ( ResultSet ) obj ).close();
				else if ( obj instanceof PreparedStatement )
					( ( PreparedStatement ) obj ).close();
				else if ( obj instanceof Statement )
					( ( Statement ) obj ).close();
			}
		} catch ( Exception e ) {
			System.out.print( "Error cerrando objeto: " + obj + " Causa:" + e );
		} finally {
			obj = null;
		}
	}

	private int eliminaCasos() throws Exception {
		int eliminados = 0;
		System.out.println( "Obteniendo queries" );

		List<CasoTramite> queries = obtenQueries();

		System.out.println( String.format( "%d queries obtenidas. Iterando", ( queries == null ? 0 : queries.size() ) ) );

		for ( Iterator<CasoTramite> i = queries.iterator(); i.hasNext(); ) {

			Connection conn = null;
			CasoTramite ct = i.next();
			System.out.println( String.format( "Inicia depuracion del tramite [%S] con id [%d] ", ct.getTcDescripcion(), ct.getIdTC() ) );
			try {

				conn = Util.getStandAloneConnection();
				System.out.println( "Conexion obtenida exitosamente" );
				System.out.println( "Eliminando casos del tramite: " + ct );
				eliminados += eliminaCasos( conn, ct.getQuery() );

				conn.commit();

			} catch ( Exception e ) {
				System.out.println( "Ocurrio el siguiente error: " + e );
				if ( conn != null ) {
					try {
						conn.rollback();
					} catch ( Exception eRB ) {
						System.out.println( "Error en rollback: " + eRB );
					}
				}
			} finally {
				closeObject( conn );
			}

		}

		return eliminados;
	}

	private int eliminaCasos( Connection conn, String query ) throws Exception {

		int eliminados = 0;
		ResultSet rsBorrar = null;
		Statement stmtSelect = null;

		try {
			System.out.println( "Ejecutando[" + query + "]" );
			stmtSelect = conn.createStatement();

			rsBorrar = stmtSelect.executeQuery( query );

			while ( rsBorrar.next() ) {

				int id_caso = rsBorrar.getInt( "id_caso" );
				
				Caso casoBorrar = new Caso();
				casoBorrar.setIdCaso( id_caso );

				casoBorrar = CasoManager.select( conn, casoBorrar );
				
				if ( casoBorrar == null ) {
					System.out.println( "No se encontro caso con ID " + id_caso );
					continue;
				}
				System.out.println( "Eliminando el caso " + casoBorrar.getFolio() );
				Map<String, String> m = Util.readValuesCasoDato( casoBorrar.getCasoDato() );

				cbl.avanzaCaso( conn, casoBorrar, "sai", "Caso cancelado por inactividad", new String [] { "TERMINAR" }, new String [] { "TERMINAR" }, m, null );
				eliminados++;
			}

			return eliminados;
		} finally {
			closeObject( rsBorrar );
			closeObject( stmtSelect );
		}

	}
 
 

	private List<CasoTramite> obtenQueries() throws Exception {
		List<CasoTramite> queries = null;
		Connection conn = null;
		String query = "SELECT id_tc, tc_gaveta_asociada, cmd_select FROM v_tramites_sai WITH(NOLOCK)";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Util.getStandAloneConnection();
			ps = conn.prepareStatement( query );
			rs = ps.executeQuery();

			while ( rs.next() ) {
				if ( queries == null )
					queries = new ArrayList<CasoTramite>();
				CasoTramite aux = new CasoTramite();
				aux.setIdTC( rs.getInt( "id_tc" ) );
				aux.setTcDescripcion( rs.getString( "tc_gaveta_asociada" ) );
				aux.setQuery( rs.getString( "cmd_select" ) );

				queries.add( aux );
			}
			return queries;
		} finally {
			closeObject( rs );
			closeObject( ps );
			closeObject( conn );
		}
	}

}
