package com.axtel.sisecop.repostories;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.axtel.sisecop.dto.ProyectoServicioActividadDTO;
import com.axtel.sisecop.entities.ProyectoMes;
import com.axtel.sisecop.entities.ProyectoServicioActividad;


public class ProjectActivityRepository {

	private static final String	INSERT_SQL					= "INSERT INTO [dbo].[sisecop_serviciosactividades] (servicioId, servicioactividadAnio, mesId, servicioactividadDescripcion) VALUES (?, ?, ?, ?)";
	private static final String	SELECT_BY_ID_SQL			= "SELECT * FROM [dbo].[sisecop_serviciosactividades] WHERE servicioactividadId = ?";
	private static final String	SELECT_BY_SERVICE_ID_SQL	= "SELECT * FROM [dbo].[sisecop_serviciosactividades] WHERE servicioId = ?";
	private static final String	SELECT_ALL_SQL				= "SELECT * FROM [dbo].[sisecop_serviciosactividades]";
	private static final String	UPDATE_SQL					= "UPDATE [dbo].[sisecop_serviciosactividades] SET servicioId = ?, servicioactividadAnio = ?, mesId = ?, servicioactividadDescripcion = ? WHERE servicioactividadId = ?";
	private static final String	DELETE_SQL					= "DELETE FROM [dbo].[sisecop_serviciosactividades] WHERE servicioactividadId = ?";

	public ProyectoServicioActividad insertProyectoServicioActividad( Connection connection, ProyectoServicioActividadDTO dto ) throws SQLException {

		try ( PreparedStatement preparedStatement = connection.prepareStatement( INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS ) ) {
			preparedStatement.setInt( 1, dto.getIdService() );
			preparedStatement.setInt( 2, dto.getServicioactividadAnio() );
			preparedStatement.setInt( 3, dto.getSisecopMes().getMesId() );
			preparedStatement.setString( 4, dto.getServicioactividadDescripcion() );
			preparedStatement.executeUpdate();

			try ( ResultSet generatedKeys = preparedStatement.getGeneratedKeys() ) {
				if ( generatedKeys.next() ) {
					int generatedId = generatedKeys.getInt( 1 );
					return selectProyectoServicioActividadById( connection, generatedId );
				} else
					throw new SQLException( "Se inserto la actividad pero no se devolvio objeto al consultar por ID" );
			}
		}
	}

	public ProyectoServicioActividad selectProyectoServicioActividadById( Connection connection, int id ) throws SQLException {
		ProyectoServicioActividad proyectoServicioActividad = null;
		try ( PreparedStatement preparedStatement = connection.prepareStatement( SELECT_BY_ID_SQL ) ) {
			preparedStatement.setInt( 1, id );
			ResultSet rs = preparedStatement.executeQuery();
			if ( rs.next() ) {
				proyectoServicioActividad = mapToProyectoServicioActividad( rs );
			}
		}
		return proyectoServicioActividad;
	}

	public List<ProyectoServicioActividad> selectProyectoServicioActividadByServiceId( Connection connection, int serviceId ) throws SQLException {
		List<ProyectoServicioActividad> proyectoServicioActividadList = new ArrayList<>();
		try ( PreparedStatement preparedStatement = connection.prepareStatement( SELECT_BY_SERVICE_ID_SQL ) ) {
			preparedStatement.setInt( 1, serviceId );
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				proyectoServicioActividadList.add( mapToProyectoServicioActividad( rs ) );
			}
		}
		return proyectoServicioActividadList;
	}

	public List<ProyectoServicioActividad> selectAllProyectoServicioActividad( Connection connection ) throws SQLException {
		List<ProyectoServicioActividad> proyectoServicioActividadList = new ArrayList<>();
		try ( PreparedStatement preparedStatement = connection.prepareStatement( SELECT_ALL_SQL ) ) {
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
				proyectoServicioActividadList.add( mapToProyectoServicioActividad( rs ) );
			}
		}
		return proyectoServicioActividadList;
	}

	public boolean updateProyectoServicioActividad( Connection connection, ProyectoServicioActividadDTO proyectoServicioActividad ) throws SQLException {
		boolean rowUpdated;
		try ( PreparedStatement preparedStatement = connection.prepareStatement( UPDATE_SQL ) ) {
			preparedStatement.setInt( 1, proyectoServicioActividad.getIdService() );
			preparedStatement.setInt( 2, proyectoServicioActividad.getServicioactividadAnio() );
			preparedStatement.setInt( 3, proyectoServicioActividad.getSisecopMes().getMesId() );
			preparedStatement.setString( 4, proyectoServicioActividad.getServicioactividadDescripcion() );
			preparedStatement.setInt( 5, proyectoServicioActividad.getServicioactividadId() );
			rowUpdated = preparedStatement.executeUpdate() > 0;
		}
		return rowUpdated;
	}

	public boolean deleteProyectoServicioActividad( Connection connection, int id ) throws SQLException {
		boolean rowDeleted;
		try ( PreparedStatement preparedStatement = connection.prepareStatement( DELETE_SQL ) ) {
			preparedStatement.setInt( 1, id );
			rowDeleted = preparedStatement.executeUpdate() > 0;
		}
		return rowDeleted;
	}

	private ProyectoServicioActividad mapToProyectoServicioActividad( ResultSet rs ) throws SQLException {
		ProyectoServicioActividad proyectoServicioActividad = new ProyectoServicioActividad();
		proyectoServicioActividad.setServicioactividadId( rs.getInt( "servicioactividadId" ) );
		proyectoServicioActividad.setServicioactividadAnio( rs.getInt( "servicioactividadAnio" ) );
		proyectoServicioActividad.setSisecopMes( new ProyectoMes( rs.getInt( "mesId" ) ) );
		proyectoServicioActividad.setServicioactividadDescripcion( rs.getString( "servicioactividadDescripcion" ) );
		return proyectoServicioActividad;
	}
}
