package com.syc.gestion.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BitacoraCorreoManager {

	public static void create( Connection connection, BitacoraCorreo correo ) throws SQLException {
		String query = "INSERT INTO tBitacoraCorreos (destinatarios, mensaje, subject, estatus) VALUES (?, ?, ?, ?)";
		try ( PreparedStatement stmt = connection.prepareStatement( query ) ) {
			stmt.setString( 1, correo.getDestinatarios() );
			stmt.setString( 2, correo.getMensaje().length() <= 2047 ? correo.getMensaje() : correo.getMensaje().substring( 2047 ) );
			stmt.setString( 3, correo.getSubject().length() <= 1023 ? correo.getSubject() : correo.getSubject().substring( 1023 ) );
			stmt.setString( 4, correo.getEstatus() );

			stmt.executeUpdate();
		}
	}

	public static BitacoraCorreo read( Connection connection, int id ) throws SQLException {
		String query = "SELECT * FROM tBitacoraCorreos WHERE id = ?";
		try ( PreparedStatement stmt = connection.prepareStatement( query ) ) {
			stmt.setInt( 1, id );
			try ( ResultSet rs = stmt.executeQuery() ) {
				if ( rs.next() ) {
					return mapToBitacoraCorreo( rs );
				}
			}
		}
		return null;
	}

	public static List<BitacoraCorreo> readAll( Connection connection ) throws SQLException {
		String query = "SELECT * FROM tBitacoraCorreos";
		List<BitacoraCorreo> correos = new ArrayList<>();
		try ( PreparedStatement stmt = connection.prepareStatement( query ); ResultSet rs = stmt.executeQuery() ) {
			while ( rs.next() ) {
				correos.add( mapToBitacoraCorreo( rs ) );
			}
		}
		return correos;
	}

	public static void update( Connection connection, BitacoraCorreo correo ) throws SQLException {
		String query = "UPDATE tBitacoraCorreos SET destinatarios = ?, mensaje = ?, subject = ?, estatus = ?, fecha_envio = ? WHERE id = ?";
		try ( PreparedStatement stmt = connection.prepareStatement( query ) ) {
			stmt.setString( 1, correo.getDestinatarios() );
			stmt.setString( 2, correo.getMensaje() );
			stmt.setString( 3, correo.getSubject() );
			stmt.setString( 4, correo.getEstatus() );
			stmt.setTimestamp( 5, correo.getFechaEnvio() );
			stmt.setInt( 6, correo.getId() );
			stmt.executeUpdate();
		}
	}

	public static void delete( Connection connection, int id ) throws SQLException {
		String query = "DELETE FROM tBitacoraCorreos WHERE id = ?";
		try ( PreparedStatement stmt = connection.prepareStatement( query ) ) {
			stmt.setInt( 1, id );
			stmt.executeUpdate();
		}
	}

	private static BitacoraCorreo mapToBitacoraCorreo( ResultSet rs ) throws SQLException {
		BitacoraCorreo correo = new BitacoraCorreo();
		correo.setId( rs.getInt( "id" ) );
		correo.setDestinatarios( rs.getString( "destinatarios" ) );
		correo.setMensaje( rs.getString( "mensaje" ) );
		correo.setSubject( rs.getString( "subject" ) );
		correo.setEstatus( rs.getString( "estatus" ) );
		correo.setFechaEnvio( rs.getTimestamp( "fecha_envio" ) );
		return correo;
	}
}
