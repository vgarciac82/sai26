package com.syc.fortimax.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.syc.gestion.core.FortimaxFile;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class PaginaManager {

	public static String getFilename( Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento ) throws SQLException {

		String filename = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement( "SELECT nom_archivo_vol FROM imx_pagina " + "WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ? " + "AND id_documento = ?" );

			pstmnt.setString( 1, titulo_aplicacion );
			pstmnt.setInt( 2, id_gabinete );
			pstmnt.setInt( 3, id_carpeta_padre );
			pstmnt.setInt( 4, id_documento );

			rs = pstmnt.executeQuery();

			if ( rs.next() )
				filename = rs.getString( "nom_archivo_vol" );
		} finally {
			if ( rs != null )
				rs.close();

			if ( pstmnt != null )
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return filename;
	}

	public static String getFilenamePath( Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento ) throws SQLException {

		String filename = null;

		PreparedStatement pstmnt = null;
		PreparedStatement pstmntVol = null;
		ResultSet rs = null;
		ResultSet rsVol = null;

		try {

			pstmnt = conn.prepareStatement( "SELECT volumen, nom_archivo_vol FROM imx_pagina " + "WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?" );

			pstmnt.setString( 1, titulo_aplicacion );
			pstmnt.setInt( 2, id_gabinete );
			pstmnt.setInt( 3, id_carpeta_padre );
			pstmnt.setInt( 4, id_documento );

			rs = pstmnt.executeQuery();

			if ( rs.next() ) {

				String volumen = rs.getString( "volumen" );
				String nom_archivo_vol = rs.getString( "nom_archivo_vol" );

				pstmntVol = conn.prepareStatement( "SELECT unidad_disco, ruta_base, ruta_directorio " + "FROM imx_volumen WITH (NOLOCK) WHERE volumen = ?" );
				pstmntVol.setString( 1, volumen );

				rsVol = pstmntVol.executeQuery();

				if ( rsVol.next() )
					filename = rsVol.getString( "unidad_disco" ) + rsVol.getString( "ruta_base" ) + rsVol.getString( "ruta_directorio" ) + nom_archivo_vol;
				else
					throw new SQLException( "No se encontro informacion en volumen con los valores: " + titulo_aplicacion + ", " + id_gabinete + ", " + id_carpeta_padre + ", " + id_documento );
			} 
			
			return filename;
			
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( pstmntVol );
			CloseObject.closeObject( rs );
			CloseObject.closeObject( rsVol );
		}

	}

	public static FortimaxFile[] getPaginasDeDocumento( Connection conn, String titApp, int idGab, int idCarp, int idDoc ) throws Exception {

		List<FortimaxFile> result = new ArrayList<FortimaxFile>();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement( "SELECT v.unidad_disco, v.ruta_base, v.ruta_directorio, " + "p.nom_archivo_vol, p.nom_archivo_org, d.nombre_documento, p.numero_pagina " + "FROM imx_documento d WITH (NOLOCK), imx_pagina p WITH (NOLOCK), imx_volumen v WITH (NOLOCK)" + "WHERE d.titulo_aplicacion = p.titulo_aplicacion AND d.id_gabinete = p.id_gabinete " + "AND d.id_carpeta_padre = p.id_carpeta_padre AND d.id_documento = p.id_documento " + "AND v.volumen = p.volumen AND p.titulo_aplicacion = ? AND p.id_gabinete = ? " + "AND p.id_carpeta_padre = ? AND p.id_documento = ? ORDER BY p.numero_pagina" );

			pstmnt.setString( 1, titApp );
			pstmnt.setInt( 2, idGab );
			pstmnt.setInt( 3, idCarp );
			pstmnt.setInt( 4, idDoc );

			rs = pstmnt.executeQuery();

			while ( rs.next() ) {
				String archOrg = rs.getString( "nom_archivo_org" );
				String docNom = rs.getString( "nombre_documento" );
				int pos = archOrg.indexOf( "." );
				String logName = docNom + ( ( pos != -1 ) ? archOrg.substring( pos ) : "" );
				FortimaxFile ff = new FortimaxFile( rs.getString( "unidad_disco" ), rs.getString( "ruta_base" ), rs.getString( "ruta_directorio" ), logName, rs.getString( "nom_archivo_vol" ) );
				result.add( ff );

			}
			return ( FortimaxFile[] ) result.toArray( new FortimaxFile [result.size()] );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( pstmnt );
		}

	}

	public static String[] getPaginasDocumento( Connection conn, String titApp, int idGab, int idCarp, int idDoc ) throws SQLException {

		List result = new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement( "SELECT v.unidad_disco, v.ruta_base, v.ruta_directorio, " + "p.nom_archivo_vol FROM imx_pagina p, imx_volumen v WITH (NOLOCK) WHERE v.volumen = p.volumen " + "AND p.titulo_aplicacion = ? AND p.id_gabinete = ? " + "AND p.id_carpeta_padre = ? AND p.id_documento = ? ORDER BY p.numero_pagina" );

			pstmnt.setString( 1, titApp );
			pstmnt.setInt( 2, idGab );
			pstmnt.setInt( 3, idCarp );
			pstmnt.setInt( 4, idDoc );

			rs = pstmnt.executeQuery();

			while ( rs.next() ) {
				String filename = rs.getString( "unidad_disco" ) + rs.getString( "ruta_base" ) + rs.getString( "ruta_directorio" ) + rs.getString( "nom_archivo_vol" );
				result.add( filename );
			}
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		return ( String[] ) result.toArray( new String [result.size()] );

	}

	public static int updatePagina( Connection conn, Pagina p ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "UPDATE imx_pagina SET volumen = ?, tipo_volumen = ?, " + "nom_archivo_vol = ?, nom_archivo_org = ?, tipo_pagina = ?, anotaciones = ?, " + "estado_pagina = ?, tamano_bytes = ? WHERE titulo_aplicacion = ?  AND " + "id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ? AND numero_pagina = ?" );

			pstmnt.setString( 1, p.getVolumen() );
			pstmnt.setString( 2, p.getTipoVolumen() );
			pstmnt.setString( 3, p.getNomArchivoVol() );
			pstmnt.setString( 4, p.getNomArchivoOrg() );
			pstmnt.setString( 5, p.getTipoPagina() );
			pstmnt.setString( 6, p.getAnotaciones() );
			pstmnt.setString( 7, p.getEstadoPagina() );
			pstmnt.setDouble( 8, p.getTamanoBytes() );
			pstmnt.setString( 9, p.getTituloAplicacion() );
			pstmnt.setInt( 10, p.getIdGabinete() );
			pstmnt.setInt( 11, p.getIdCarpetaPadre() );
			pstmnt.setInt( 12, p.getIdDocumento() );
			pstmnt.setInt( 13, p.getNumeroPagina() );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static Pagina selectPagina( Connection conn, String titApp, int idGab, int idCarp, int idDoc, String volName ) throws SQLException {

		Pagina rp = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement( "SELECT * FROM imx_pagina WHERE titulo_aplicacion = ?  AND " + "id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ? AND nom_archivo_vol = ?" );

			pstmnt.setString( 1, titApp );
			pstmnt.setInt( 2, idGab );
			pstmnt.setInt( 3, idCarp );
			pstmnt.setInt( 4, idDoc );
			pstmnt.setString( 5, volName );

			rs = pstmnt.executeQuery();
			if ( rs.next() ) {
				rp = new Pagina();

				rp.setTituloAplicacion( rs.getString( "titulo_aplicacion" ) );
				rp.setIdGabinete( rs.getInt( "id_gabinete" ) );
				rp.setIdCarpetaPadre( rs.getInt( "id_carpeta_padre" ) );
				rp.setIdDocumento( rs.getInt( "id_documento" ) );
				rp.setNumeroPagina( rs.getInt( "numero_pagina" ) );
				rp.setVolumen( rs.getString( "volumen" ) );
				rp.setTipoVolumen( rs.getString( "tipo_volumen" ) );
				rp.setNomArchivoVol( rs.getString( "nom_archivo_vol" ) );
				rp.setNomArchivoOrg( rs.getString( "nom_archivo_org" ) );
				rp.setTipoPagina( rs.getString( "tipo_pagina" ) );
				rp.setAnotaciones( null );
				rp.setEstadoPagina( rs.getString( "estado_pagina" ) );
				rp.setTamanoBytes( rs.getInt( "tamano_bytes" ) );
			}
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		return rp;
	}

	public static int delete( Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "DELETE FROM imx_pagina WHERE titulo_aplicacion = ? AND " + "id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?" );

			pstmnt.setString( 1, titulo_aplicacion );
			pstmnt.setInt( 2, id_gabinete );
			pstmnt.setInt( 3, id_carpeta_padre );
			pstmnt.setInt( 4, id_documento );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int deletePagina( Connection conn, Pagina p ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "DELETE FROM imx_pagina WHERE titulo_aplicacion = ?  AND " + "id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ? AND numero_pagina = ?" );

			pstmnt.setString( 1, p.getTituloAplicacion() );
			pstmnt.setInt( 2, p.getIdGabinete() );
			pstmnt.setInt( 3, p.getIdCarpetaPadre() );
			pstmnt.setInt( 4, p.getIdDocumento() );
			pstmnt.setInt( 5, p.getNumeroPagina() );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int delete( Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento, String OCRProgramPath, String OCRParameter1, String OCRParameter2, String luceneDbPath, String luceneStopwordsPath, int luceneMergeFactor, int luceneMaxMergeDocs ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "DELETE FROM imx_pagina WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?" );

			pstmnt.setString( 1, titulo_aplicacion );
			pstmnt.setInt( 2, id_gabinete );
			pstmnt.setInt( 3, id_carpeta_padre );
			pstmnt.setInt( 4, id_documento );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		PaginaIndexManager pim = new PaginaIndexManager( OCRProgramPath, OCRParameter1, OCRParameter2, luceneDbPath, luceneStopwordsPath, luceneMergeFactor, luceneMaxMergeDocs );

		pim.deleteLuceneIndexEntries( titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento, -1 );

		return retval;
	}

	public static int deletePagina( Connection conn, Pagina p, String OCRProgramPath, String OCRParameter1, String OCRParameter2, String luceneDbPath, String luceneStopwordsPath, int luceneMergeFactor, int luceneMaxMergeDocs ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "DELETE FROM imx_pagina WHERE titulo_aplicacion = ?  AND " + "id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ? AND numero_pagina = ?" );

			pstmnt.setString( 1, p.getTituloAplicacion() );
			pstmnt.setInt( 2, p.getIdGabinete() );
			pstmnt.setInt( 3, p.getIdCarpetaPadre() );
			pstmnt.setInt( 4, p.getIdDocumento() );
			pstmnt.setInt( 5, p.getNumeroPagina() );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		PaginaIndexManager pim = new PaginaIndexManager( OCRProgramPath, OCRParameter1, OCRParameter2, luceneDbPath, luceneStopwordsPath, luceneMergeFactor, luceneMaxMergeDocs );

		pim.deleteLuceneIndexEntries( p.getTituloAplicacion(), p.getIdGabinete(), p.getIdCarpetaPadre(), p.getIdDocumento(), p.getNumeroPagina() );

		return retval;
	}

}
