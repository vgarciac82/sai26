package com.axtel.sisecop.repostories;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.entities.TipoProyecto;
import com.axtel.sisecop.services.ProyectoExcepcionRepository;
import com.syc.cfdi.db.CloseObject;


public class ProyectoServicioRepositorio {

	private ProjectBudgetRepository				budgetRepository;

	private ProyectoConfidencialidadRepositorio	confidencialidadRepositorio;

	private ProyectoEstatusRepositorio			estatusRepositorio;

	private ProductRepository					productRepository;

	private ProjectActivityRepository			projectActivityRepository;

	private ProjectPaymentRepository			projectPaymentRepository;

	private ProjectProcurementProcessRepository	tdrRepositorio;

	private ProyectoTerritorioRepositorio		territorioRepositorio;

	private ProyectoTipoRepositorio				tipoRepositorio;

	private ProyectoExcepcionRepository			excepcionRepositorio;

	public int create( Connection connection, ProyectoServicio servicio ) throws SQLException {
		String sql = "INSERT INTO sisecop_servicios (usuarioUsuario, servicioTitulo, servicioFolioPre, servicioObjetivos, confidencialidadId, servicioDuracion, servicioVinculacion, estatusId, servicioCompleto, servicioCreacion, servicioModificacion, servicioFolioAnio, servicioFolioNum, servicioGerencia, servicioCoordinacion, tipoId, servicioListo, process_id, servicioDuracionDias, managment_unit, responsible_unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try ( PreparedStatement statement = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS ) ) {
			fillPreparedStatement( statement, servicio );
			int affectedRows = statement.executeUpdate();

			if ( affectedRows == 0 ) {
				throw new SQLException( "Creating user failed, no rows affected." );
			}

			try ( ResultSet generatedKeys = statement.getGeneratedKeys() ) {
				if ( generatedKeys.next() ) {
					return generatedKeys.getInt( 1 );
				} else {
					throw new SQLException( "Creating user failed, no ID obtained." );
				}
			}
		} catch ( SQLException e ) {
			throw e;
		}
	}

	public boolean delete( Connection connection, int id ) throws SQLException {
		String sql = "DELETE FROM sisecop_servicios WHERE servicioId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setInt( 1, id );
			return statement.executeUpdate() > 0;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	private void fillPreparedStatement( PreparedStatement statement, ProyectoServicio servicio ) throws SQLException {
		statement.setString( 1, servicio.getLoginUsuario() );
		statement.setString( 2, servicio.getServicioTitulo() );
		statement.setString( 3, servicio.getServicioFolioPre() );
		statement.setString( 4, servicio.getServicioObjetivos() );

		if ( servicio.getConfidencialidad() == null )
			statement.setInt( 5, 3 );
		else
			statement.setInt( 5, servicio.getConfidencialidad().getConfidencialidadId() );

		statement.setInt( 6, servicio.getServicioDuracion() );
		statement.setString( 7, servicio.getServicioVinculacion() );

		if ( servicio.getEstatus() == null )
			statement.setInt( 8, 1 );
		else
			statement.setInt( 8, servicio.getEstatus().getEstatusId() );

		statement.setBoolean( 9, servicio.isServicioCompleto() );
		statement.setTimestamp( 10, servicio.getServicioCreacion() );
		statement.setTimestamp( 11, servicio.getServicioModificacion() );
		statement.setInt( 12, servicio.getServicioFolioAnio() );
		statement.setInt( 13, servicio.getServicioFolioNum() );
		statement.setString( 14, servicio.getServicioGerencia() );
		statement.setString( 15, servicio.getServicioCoordinacion() );

		if ( servicio.getProyectoTipo() == null )
			statement.setInt( 16, TipoProyecto.SIN_REGISTRAR.getId() );
		else
			statement.setInt( 16, servicio.getProyectoTipo().getTipoProyectoId() );

		statement.setBoolean( 17, servicio.isServicioListo() );
		statement.setInt( 18, servicio.getIdProcess() );
		statement.setInt( 19, servicio.getServicioDuracionDias() );

		statement.setString( 20, servicio.getManagmentUnit() );
		statement.setString( 21, servicio.getResponsibleUnit() );

	}

	public List<ProyectoServicio> findAll( Connection connection ) throws SQLException {
		List<ProyectoServicio> servicios = new ArrayList<>();
		String sql = "SELECT * FROM sisecop_servicios";
		try ( Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery( sql ) ) {
			while ( resultSet.next() ) {
				servicios.add( mapToProyectoServicio( connection, resultSet ) );
			}
			return servicios;
		} catch ( SQLException e ) {
			throw e;
		}

	}

	public ProyectoServicio findById( Connection connection, int id ) throws SQLException {
		ProyectoServicio servicio = null;
		String sql = "SELECT * FROM sisecop_servicios WHERE servicioId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {
			statement.setInt( 1, id );
			ResultSet resultSet = statement.executeQuery();
			if ( resultSet.next() ) {
				servicio = mapToProyectoServicio( connection, resultSet );
			}
			return servicio;
		} catch ( SQLException e ) {
			throw e;
		}

	}

	public ProjectBudgetRepository getBudgetRepository() {
		return budgetRepository;
	}

	public ProyectoConfidencialidadRepositorio getConfidencialidadRepositorio() {
		return confidencialidadRepositorio;
	}

	public ProyectoEstatusRepositorio getEstatusRepositorio() {
		return estatusRepositorio;
	}

	public ProductRepository getProductRepository() {
		return productRepository;
	}

	public ProjectActivityRepository getProjectActivityRepository() {
		return projectActivityRepository;
	}

	public ProjectPaymentRepository getProjectPaymentRepository() {
		return projectPaymentRepository;
	}

	public ProjectProcurementProcessRepository getTdrRepositorio() {
		return tdrRepositorio;
	}

	public ProyectoTerritorioRepositorio getTerritorioRepositorio() {
		return territorioRepositorio;
	}

	public ProyectoTipoRepositorio getTipoRepositorio() {
		return tipoRepositorio;
	}

	private ProyectoServicio mapToProyectoServicio( Connection conn, ResultSet resultSet ) throws SQLException {
		ProyectoServicio servicio = new ProyectoServicio();

		servicio.setServicioId( resultSet.getInt( "servicioId" ) );
		servicio.setLoginUsuario( resultSet.getString( "usuarioUsuario" ) );
		servicio.setServicioTitulo( resultSet.getString( "servicioTitulo" ) );
		servicio.setServicioFolioPre( resultSet.getString( "servicioFolioPre" ) );
		servicio.setServicioObjetivos( resultSet.getString( "servicioObjetivos" ) );
		servicio.setConfidencialidad( confidencialidadRepositorio.findById( conn, resultSet.getInt( "confidencialidadId" ) ) );

		servicio.setTerritorios( territorioRepositorio.readByServicioId( conn, resultSet.getInt( "servicioId" ) ) );
		servicio.setProductos( productRepository.readByServicioId( conn, resultSet.getInt( "servicioId" ) ) );
		servicio.setServiciosTDR( tdrRepositorio.readByServicioId( conn, resultSet.getInt( "servicioId" ) ) );
		servicio.setServicioClaves( budgetRepository.readByServicioId( conn, resultSet.getInt( "servicioId" ) ) );
		servicio.setActividades( projectActivityRepository.selectProyectoServicioActividadByServiceId( conn, resultSet.getInt( "servicioId" ) ) );
		servicio.setPagos( projectPaymentRepository.selectProyectoServicioPagoByServiceId( conn, resultSet.getInt( "servicioId" ) ) );

		servicio.setServicioDuracion( resultSet.getInt( "servicioDuracion" ) );
		servicio.setServicioDuracionDias( resultSet.getInt( "servicioDuracionDias" ) );
		servicio.setServicioVinculacion( resultSet.getString( "servicioVinculacion" ) );
		servicio.setEstatus( estatusRepositorio.findById( conn, resultSet.getInt( "estatusId" ) ) );
		servicio.setServicioCompleto( resultSet.getBoolean( "servicioCompleto" ) );
		servicio.setServicioCreacion( resultSet.getTimestamp( "servicioCreacion" ) );
		servicio.setServicioModificacion( resultSet.getTimestamp( "servicioModificacion" ) );
		servicio.setServicioFolioAnio( resultSet.getInt( "servicioFolioAnio" ) );
		servicio.setServicioFolioNum( resultSet.getInt( "servicioFolioNum" ) );
		servicio.setServicioGerencia( resultSet.getString( "servicioGerencia" ) );
		servicio.setServicioCoordinacion( resultSet.getString( "servicioCoordinacion" ) );
		servicio.setProyectoTipo( tipoRepositorio.findById( conn, resultSet.getInt( "tipoId" ) ) );

		servicio.setServicioListo( resultSet.getBoolean( "servicioListo" ) );
		servicio.setIdProcess( resultSet.getInt( "process_id" ) );

		servicio.setObservaciones( resultSet.getString( "observaciones" ) );
		servicio.setProyectoExcepcion( getExcepcionRepositorio().findProyectoExcepcionByChild( conn, resultSet.getInt( "servicioId" ) ) );
		servicio.setManagmentUnit( StringUtils.trimToEmpty( resultSet.getString( "managment_unit" ) ) );
		servicio.setResponsibleUnit( StringUtils.trimToEmpty( resultSet.getString( "responsible_unit" ) ) );

		if ( StringUtils.isNotBlank( resultSet.getString( "managment_unit" ) ) )
			servicio.setGerenciaNombre( getUnitName( conn, resultSet.getString( "managment_unit" ) ) );

		if ( StringUtils.isNotBlank( resultSet.getString( "responsible_unit" ) ) )
			servicio.setCoordinacionNombre( getUnitName( conn, resultSet.getString( "responsible_unit" ) ) );

		return servicio;
	}

	private String getUnitName( Connection connection, String unitCode ) throws SQLException {
		StringBuilder sql = new StringBuilder( "SELECT	cDescripcion FROM nom_Unidad_Ejecutora WITH(NOLOCK) where  cUadministrativa = ?" );
		ResultSet rs = null;
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement( sql.toString() );
			statement.setString( 1, unitCode );
			rs = statement.executeQuery();

			if ( rs.next() )
				return rs.getString( 1 );
			else
				return "";
		} catch ( SQLException e ) {
			throw e;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( statement );
		}
	}

	public boolean nextStep( Connection connection, ProyectoServicio servicio ) {

		StringBuilder sql = new StringBuilder( "UPDATE sisecop_servicios " );
		sql.append( "SET estatusId = ?  " );
		sql.append( ",   observaciones = ?  " );
		sql.append( "  WHERE servicioId = ?" );
		try ( PreparedStatement statement = connection.prepareStatement( sql.toString() ) ) {

			int idParam = 1;

			statement.setInt( idParam++, servicio.getEstatus().getEstatusId() );
			statement.setString( idParam++, servicio.getObservaciones() );
			statement.setInt( idParam++, servicio.getServicioId() );

			return statement.executeUpdate() > 0;
		} catch ( SQLException e ) {
			throw new RuntimeException( e.toString(), e );
		}

	}

	public void setBudgetRepository( ProjectBudgetRepository budgetRepository ) {
		this.budgetRepository = budgetRepository;
	}

	public void setConfidencialidadRepositorio( ProyectoConfidencialidadRepositorio confidencialidadRepositorio ) {
		this.confidencialidadRepositorio = confidencialidadRepositorio;
	}

	public void setEstatusRepositorio( ProyectoEstatusRepositorio estatusRepositorio ) {
		this.estatusRepositorio = estatusRepositorio;
	}

	public void setProductRepository( ProductRepository productRepository ) {
		this.productRepository = productRepository;
	}

	public void setProjectActivityRepository( ProjectActivityRepository projectActivityRepository ) {
		this.projectActivityRepository = projectActivityRepository;
	}

	public void setProjectPaymentRepository( ProjectPaymentRepository projectPaymentRepository ) {
		this.projectPaymentRepository = projectPaymentRepository;
	}

	public void setTdrRepositorio( ProjectProcurementProcessRepository tdrRepositorio ) {
		this.tdrRepositorio = tdrRepositorio;
	}

	public void setTerritorioRepositorio( ProyectoTerritorioRepositorio territorioRepositorio ) {
		this.territorioRepositorio = territorioRepositorio;

	}

	public void setTipoRepositorio( ProyectoTipoRepositorio tipoRepositorio ) {
		this.tipoRepositorio = tipoRepositorio;
	}

	public boolean update( Connection connection, ProyectoServicio servicio ) throws SQLException {
		String sql = "UPDATE sisecop_servicios " + "   SET servicioTitulo = ?, " + "       servicioObjetivos = ?, " + "       confidencialidadId = ?, " + "       servicioDuracion = ?, " + "       servicioVinculacion = ?, " + "       servicioModificacion = ?, " + "       servicioGerencia = ?, " + "       servicioCoordinacion = ?, " + "       tipoId = ?, " + "       servicioDuracionDias = ? " + "  WHERE servicioId = ?";
		try ( PreparedStatement statement = connection.prepareStatement( sql ) ) {

			int idParam = 1;

			statement.setString( idParam++, servicio.getServicioTitulo() );
			statement.setString( idParam++, servicio.getServicioObjetivos() );
			statement.setInt( idParam++, servicio.getConfidencialidad().getConfidencialidadId() );
			statement.setInt( idParam++, servicio.getServicioDuracion() );
			statement.setString( idParam++, servicio.getServicioVinculacion() );
			statement.setTimestamp( idParam++, servicio.getServicioModificacion() );
			statement.setString( idParam++, servicio.getServicioGerencia() );
			statement.setString( idParam++, servicio.getServicioCoordinacion() );
			statement.setInt( idParam++, servicio.getProyectoTipo().getTipoProyectoId() );
			statement.setInt( idParam++, servicio.getServicioDuracionDias() );

			statement.setInt( idParam++, servicio.getServicioId() );

			return statement.executeUpdate() > 0;
		} catch ( SQLException e ) {
			throw e;
		}
	}

	public ProyectoExcepcionRepository getExcepcionRepositorio() {
		return excepcionRepositorio;
	}

	public void setExcepcionRepositorio( ProyectoExcepcionRepository excepcionRepositorio ) {
		this.excepcionRepositorio = excepcionRepositorio;
	}
}
