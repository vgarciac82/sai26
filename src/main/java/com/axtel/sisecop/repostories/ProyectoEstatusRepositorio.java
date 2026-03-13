package com.axtel.sisecop.repostories;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.axtel.sisecop.entities.ProyectoEstatus;


public class ProyectoEstatusRepositorio {

	public ProyectoEstatus findById( Connection connection, int id ) throws SQLException {
		String sql = "SELECT * FROM sisecop_estatus WHERE estatusId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setInt( 1, id );
			ResultSet resultSet = statement.executeQuery();
			if ( resultSet.next() ) {
				return mapToProyectoEstatus( resultSet );
			}
			return null;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	public boolean insert( Connection connection, ProyectoEstatus estatus ) throws SQLException {
		String sql = "INSERT INTO sisecop_estatus (estatusNombre) VALUES (?)";
		try ( PreparedStatement statement = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS ) ) {
			statement.setString( 1, estatus.getEstatusNombre() );
			int affectedRows = statement.executeUpdate();
			if ( affectedRows > 0 ) {
				try ( ResultSet generatedKeys = statement.getGeneratedKeys() ) {
					if ( generatedKeys.next() ) {
						estatus.setEstatusId( generatedKeys.getInt( 1 ) );
						return true;
					}
				}
			}
			return false;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	public boolean update( Connection connection, ProyectoEstatus estatus ) throws SQLException {
		String sql = "UPDATE sisecop_estatus SET estatusNombre = ? WHERE estatusId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setString( 1, estatus.getEstatusNombre() );
			statement.setInt( 2, estatus.getEstatusId() );
			return statement.executeUpdate() > 0;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	public boolean delete( Connection connection, int id ) throws SQLException {
		String sql = "DELETE FROM sisecop_estatus WHERE estatusId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setInt( 1, id );
			return statement.executeUpdate() > 0;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	private ProyectoEstatus mapToProyectoEstatus( ResultSet resultSet ) throws SQLException {
		ProyectoEstatus estatus = new ProyectoEstatus();
		estatus.setEstatusId( resultSet.getInt( "estatusId" ) );
		estatus.setEstatusNombre( resultSet.getString( "estatusNombre" ) );
		return estatus;
	}
}
