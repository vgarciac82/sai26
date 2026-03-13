package com.syc.gestion.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


public class UsuarioGrupoManager {

	public static int delete( Connection conn, String u_login ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "DELETE FROM cg_usuario_grupo with(rowlock) WHERE u_login = ?" );

			pstmnt.setString( 1, u_login );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int delete( Connection conn, String u_login, String g_nombre ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "DELETE FROM cg_usuario_grupo with(rowlock) WHERE u_login = ? AND g_nombre = ?" );

			pstmnt.setString( 1, u_login );
			pstmnt.setString( 2, g_nombre );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static void insert( Connection conn, Map m ) throws SQLException {
		if ( !m.isEmpty() )
			for ( Iterator iter = m.keySet().iterator(); iter.hasNext(); )
				insert( conn, ( UsuarioGrupo ) iter.next() );
	}

	public static int insert( Connection conn, UsuarioGrupo ug ) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement( "INSERT INTO cg_usuario_grupo (u_login, g_nombre) VALUES (?, ?)" );

			pstmnt.setString( 1, ug.getLogin() );
			pstmnt.setString( 2, ug.getNombre() );

			retval = pstmnt.executeUpdate();
		} finally {
			if ( pstmnt != null )
				pstmnt.close();
			pstmnt = null;
		}

		return retval;
	}

	public static UsuarioGrupo select( Connection conn, UsuarioGrupo ug ) throws SQLException {

		UsuarioGrupo rug = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if ( ug.getLogin() != null ) {
				where.append( token + "u_login = ?" );
				token = " AND ";
			}

			if ( ug.getNombre() != null ) {
				where.append( token + "g_nombre = ?" );
				token = " AND ";
			}

			pstmnt = conn.prepareStatement( "SELECT * FROM cg_usuario_grupo with(nolock) " + where.toString() );

			int i = 1;
			if ( ug.getLogin() != null )
				pstmnt.setString( i++, ug.getLogin() );

			if ( ug.getNombre() != null )
				pstmnt.setString( i++, ug.getNombre() );

			rs = pstmnt.executeQuery();

			if ( rs.next() ) {
				rug = new UsuarioGrupo();

				rug.setLogin( rs.getString( "u_login" ) );
				rug.setNombre( rs.getString( "g_nombre" ) );
			}
		} finally {
			if ( rs != null )
				rs.close();

			if ( pstmnt != null )
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return rug;
	}

	public static Map<String, Grupo> selectGrupos( Connection conn, String u_login ) throws SQLException {

		Map<String, Grupo> m = new Hashtable<String, Grupo>();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement( "SELECT g.g_nombre, g.g_descripcion " + "FROM cg_grupo g with(nolock), cg_usuario_grupo ug with(nolock) WHERE g.g_nombre = ug.g_nombre AND ug.u_login = ?" );

			pstmnt.setString( 1, u_login );

			rs = pstmnt.executeQuery();

			while ( rs.next() ) {
				Grupo g = new Grupo();

				g.setNombre( rs.getString( "g_nombre" ) );
				g.setDescripcion( rs.getString( "g_descripcion" ) );
				g.setPropiedades( GrupoPropiedadesManager.select( conn, g.getNombre() ) );

				m.put( g.getNombre(), g );
			}
		} finally {
			if ( rs != null )
				rs.close();

			if ( pstmnt != null )
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return m;
	}

	public static Map<String, Usuario> selectUsuarios( Connection conn, String g_nombre ) throws SQLException {

		Map<String, Usuario> m = new Hashtable<String, Usuario>();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement( "SELECT u.u_login, u.u_password, u.u_nombre, u.u_descripcion " + "FROM cg_usuario u with(nolock), cg_usuario_grupo ug with(nolock) WHERE u.u_login = ug.u_login AND ug.g_nombre = ?" );

			pstmnt.setString( 1, g_nombre );

			rs = pstmnt.executeQuery();

			while ( rs.next() ) {
				Usuario u = new Usuario();

				u.setLogin( rs.getString( "u_login" ) );
				u.setPassword( rs.getString( "u_password" ) );
				u.setNombre( rs.getString( "u_nombre" ) );
				u.setDescripcion( rs.getString( "u_descripcion" ) );
				u.setPropiedades( UsuarioPropiedadesManager.select( conn, u.getLogin() ) );
				u.setGrupos( selectGrupos( conn, u.getLogin() ) );
				u.setRoles( UsuarioRoleManager.selectRoles( conn, u.getLogin() ) );

				m.put( u.getLogin(), u );
			}
		} finally {
			if ( rs != null )
				rs.close();

			if ( pstmnt != null )
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return m;
	}

	public static int update( Connection conn, UsuarioGrupo ug ) throws SQLException {
		// TODO Implementar este metodo
		return -1;
	}
}
