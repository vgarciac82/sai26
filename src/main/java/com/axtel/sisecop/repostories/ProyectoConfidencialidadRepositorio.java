package com.axtel.sisecop.repostories;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.axtel.sisecop.entities.ProyectoConfidencialidad;


public class ProyectoConfidencialidadRepositorio {

	public ProyectoConfidencialidad findById( Connection connection, int id ) throws SQLException {
		String sql = "SELECT * FROM sisecop_confidencialidades WHERE confidencialidadId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setInt( 1, id );
			ResultSet resultSet = statement.executeQuery();
			if ( resultSet.next() ) {
				return mapToProyectoConfidencialidad( resultSet );
			}
		} catch ( SQLException e ) {
			throw e;
		}
		return null;
	}

	public boolean insert( Connection connection, ProyectoConfidencialidad confidencialidad ) throws SQLException {
		String sql = "INSERT INTO sisecop_confidencialidades (confidencialidadNombre) VALUES (?)";
		try ( PreparedStatement statement = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS ) ) {
			statement.setString( 1, confidencialidad.getConfidencialidadNombre() );
			int affectedRows = statement.executeUpdate();
			if ( affectedRows > 0 ) {
				try ( ResultSet generatedKeys = statement.getGeneratedKeys() ) {
					if ( generatedKeys.next() ) {
						confidencialidad.setConfidencialidadId( generatedKeys.getInt( 1 ) );
						return true;
					}
				}
			}
		} catch ( SQLException e ) {
			throw e;
		}
		return false;
	}

	public boolean update( Connection connection, ProyectoConfidencialidad confidencialidad ) throws SQLException {
		String sql = "UPDATE sisecop_confidencialidades SET confidencialidadNombre = ? WHERE confidencialidadId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setString( 1, confidencialidad.getConfidencialidadNombre() );
			statement.setInt( 2, confidencialidad.getConfidencialidadId() );
			return statement.executeUpdate() > 0;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	public boolean delete( Connection connection, int id ) throws SQLException {
		String sql = "DELETE FROM sisecop_confidencialidades WHERE confidencialidadId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setInt( 1, id );
			return statement.executeUpdate() > 0;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	private ProyectoConfidencialidad mapToProyectoConfidencialidad( ResultSet resultSet ) throws SQLException {
		ProyectoConfidencialidad confidencialidad = new ProyectoConfidencialidad();
		confidencialidad.setConfidencialidadId( resultSet.getInt( "confidencialidadId" ) );
		confidencialidad.setConfidencialidadNombre( resultSet.getString( "confidencialidadNombre" ) );
		return confidencialidad;
	}
}
