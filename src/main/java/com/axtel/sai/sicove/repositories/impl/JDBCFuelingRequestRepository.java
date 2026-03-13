package com.axtel.sai.sicove.repositories.impl;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.SICOVE;
import com.axtel.sai.sicove.entities.VehicleFuelRequest;
import com.axtel.sai.sicove.entities.VehicleFuelRequestDAO;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingRequestRepository;


public class JDBCFuelingRequestRepository implements FuelingRequestRepository {

	private static final Logger								log								= Logger.getLogger( JDBCFuelingRequestRepository.class );
	private final QueryRunner								runner							= new QueryRunner();
	private final ResultSetHandler<VehicleFuelRequest>		vehicleFuelRequestHandler		= new BeanHandler<VehicleFuelRequest>( VehicleFuelRequest.class );
	private final ResultSetHandler<VehicleFuelRequestDAO>	fullVehicleFuelRequestHandler	= new BeanHandler<VehicleFuelRequestDAO>( VehicleFuelRequestDAO.class );
	private final ScalarHandler<BigDecimal>					scalarHandler					= new ScalarHandler<>();

	@Override
	public VehicleFuelRequest saveFuelRequest( Connection conn, VehicleFuelRequest vehicleFuelRequest ) throws SicoveException {

		log.info( "Saving: " + vehicleFuelRequest );
		StringBuilder insertFuelRequestSql = new StringBuilder( "INSERT INTO vehicle_fuel_request (vehicle_id, employee_responsible, user_request, justification_id, fueling_amount, estimated_kilometers, id_process, id_status, wallet_number, id_wallet)" );
		insertFuelRequestSql.append( " VALUES (?, ?, ?, ?, ?, ?, ?, ?, (SELECT wallet_number FROM fuelAccountWallets WITH(NOLOCK) WHERE id_fuel_account_wallets = ?), ?)" );

		if ( vehicleFuelRequest.getFuelingRequestId() > 0 )
			throw new RuntimeException( "La solicitud ya cuenta con ID. No puede guardarse" );

		BigDecimal newId;
		try {
			newId = runner.insert( conn, insertFuelRequestSql.toString(), scalarHandler, vehicleFuelRequest.getVehicleId(), vehicleFuelRequest.getEmployeeResponsible(), vehicleFuelRequest.getUserRequest(), vehicleFuelRequest.getJustification().getJustificationId(), vehicleFuelRequest.getFuelingAmount(), vehicleFuelRequest.getEstimatedKilometers(), vehicleFuelRequest.getIdProcess(), vehicleFuelRequest.getIdStatus(), vehicleFuelRequest.getIdWallet(), vehicleFuelRequest.getIdWallet() );

			vehicleFuelRequest = readVehicleFuelRequest( conn, newId.intValue() );
			return vehicleFuelRequest;
		} catch ( Exception e ) {
			throw new SicoveException( e );
		}

	}

	@Override
	public VehicleFuelRequest readVehicleFuelRequest( Connection conn, int id ) throws SicoveException {
		log.info( "Looking for fuel request number " + id );

		StringBuilder query = new StringBuilder();

		query.append( "SELECT fuelingWallet.fueling_request_id                 AS fuelingRequestId," );
		query.append( "       fuelingWallet.vehicle_id                         AS vehicleId," );
		query.append( "       fuelingWallet.request_employee_responsible       AS employeeResponsible," );
		query.append( "       fuelingWallet.user_request                       AS userRequest," );
		query.append( "       fuelingWallet.justification_id                   AS justificationId," );
		query.append( "       fuelingWallet.fueling_amount                     AS fuelingAmount," );
		query.append( "       fuelingWallet.estimated_kilometers               AS estimatedKilometers," );
		query.append( "       fuelingWallet.id_process                         AS idProcess," );
		query.append( "       fuelingWallet.id_status                          AS idStatus," );
		query.append( "       fuelingWallet.wallet_number                      AS walletNumber," );
		query.append( "       fuelingWallet.authorized_amount                  AS authorizedAmount," );
		query.append( "       fuelingWallet.id_wallet                          AS idWallet," );
		query.append( "       Isnull(fuelingWallet.reject_justification, '') AS rejectJustification," );
		query.append( "       fuelingWallet.supplier_name                      AS walletSupplierName" );
		query.append( "  FROM vFuelingWallet fuelingWallet" );
		query.append( " WHERE fuelingWallet.fueling_request_id = ? " );

		VehicleFuelRequest vehicleFuelRequest;
		try {
			log.trace( "Executing Query: \n" + query + "\n[" + id + "]" );
			vehicleFuelRequest = runner.query( conn, query.toString(), vehicleFuelRequestHandler, id );
			log.debug( "Founded: " + vehicleFuelRequest );

			return vehicleFuelRequest;

		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

	}

	@Override
	public VehicleFuelRequest updateFuelRequest( Connection conn, VehicleFuelRequest fuelRequest ) throws SicoveException {
		log.info( "Updating: " + fuelRequest );
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE	vehicle_fuel_request " );
		query.append( "   SET	employee_responsible = ? " );
		query.append( "		,estimated_kilometers = ? " );
		query.append( "		,fueling_amount = ? " );
		query.append( "		,id_status = ? " );
		query.append( "		,vehicle_id = ? " );
		query.append( "		,wallet_number = (SELECT wallet_number FROM fuelAccountWallets WITH(NOLOCK) WHERE id_fuel_account_wallets = ?) " );
		query.append( "		,id_wallet = ? " );
		query.append( " WHERE	fueling_request_id = ? " );

		try {
			log.trace( "Executing Query: \n" + query + "\n[" + fuelRequest.getEmployeeResponsible() + "][" + fuelRequest.getEstimatedKilometers() + "]" + "][" + fuelRequest.getFuelingAmount() + "]" + "][" + fuelRequest.getIdStatus() + "]" + "][" + fuelRequest.getVehicleId() + "]" + "][" + fuelRequest.getWalletNumber() + "]" + "][" + fuelRequest.getIdWallet() + "][" + fuelRequest.getFuelingRequestId() + "]" );

			runner.update( conn, query.toString(), fuelRequest.getEmployeeResponsible(), fuelRequest.getEstimatedKilometers(), fuelRequest.getFuelingAmount(), fuelRequest.getIdStatus(), fuelRequest.getVehicleId(), fuelRequest.getIdWallet(), fuelRequest.getIdWallet(), fuelRequest.getFuelingRequestId() );

			fuelRequest = readVehicleFuelRequest( conn, fuelRequest.getFuelingRequestId() );

			return fuelRequest;
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

	}

	@Override
	public void updateFuelRequestStatus( Connection conn, int fuelRequestId, int status ) throws SicoveException {
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE	vehicle_fuel_request " );
		query.append( "   SET	id_status = ? " );
		query.append( " WHERE	fueling_request_id = ? " );

		try {
			log.trace( "Executing Query: \n" + query + "\n[" + status + "][" + fuelRequestId + "]" );
			runner.update( conn, query.toString(), status, fuelRequestId );
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

	}

	@Override
	public void authRequestStatus( Connection conn, VehicleFuelRequest fuelRequest ) throws SicoveException {
		log.info( "Updating: " + fuelRequest );
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE	vehicle_fuel_request " );
		query.append( "   SET	id_status = ? " );
		query.append( "         ,authorized_amount = ? " );
		query.append( " WHERE	fueling_request_id = ? " );

		try {
			log.trace( "Executing Query: \n" + query + "\n[" + fuelRequest.getIdStatus() + "][" + fuelRequest.getAuthorizedAmount() + "][" + fuelRequest.getFuelingRequestId() + "]" );
			runner.update( conn, query.toString(), fuelRequest.getIdStatus(), fuelRequest.getAuthorizedAmount(), fuelRequest.getFuelingRequestId() );
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

	}

	@Override
	public void finishRequest( Connection conn, VehicleFuelRequest fuelRequest ) throws SicoveException {
		log.info( "Finishing: " + fuelRequest );
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE	vehicle_fuel_request " );
		query.append( "   SET	id_status = ? " );
		query.append( " WHERE	fueling_request_id = ? " );

		try {
			log.trace( "Executing Query: \n" + query + "\n[" + fuelRequest.getIdStatus() + "][" + fuelRequest.getFuelingRequestId() + "]" );
			runner.update( conn, query.toString(), fuelRequest.getIdStatus(), fuelRequest.getFuelingRequestId() );
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}
	}

	@Override
	public void discardRequest( Connection conn, VehicleFuelRequest fuelRequest ) throws SicoveException {
		log.info( "Discarding: " + fuelRequest );
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE	vehicle_fuel_request " );
		query.append( "   SET	id_status = ? " );
		query.append( " WHERE	fueling_request_id = ? " );

		try {
			log.trace( "Executing Query: \n" + query + "\n[" + SICOVE.FUELING_REQUEST_DISCARD + "][" + fuelRequest.getFuelingRequestId() + "]" );
			runner.update( conn, query.toString(), SICOVE.FUELING_REQUEST_DISCARD, fuelRequest.getFuelingRequestId() );
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

	}

	@Override
	public BigDecimal rejectRequest( Connection conn, VehicleFuelRequest fuelRequest ) throws SicoveException {
		log.info( "Rejecting: " + fuelRequest );

		StringBuilder queryInsert = new StringBuilder();
		queryInsert.append( "INSERT INTO vehicle_fuel_request_rejected(fueling_request_id,justification) " );
		queryInsert.append( "VALUES(?,?) " );

		BigDecimal newId;
		try {
			newId = runner.insert( conn, queryInsert.toString(), scalarHandler, fuelRequest.getFuelingRequestId(), fuelRequest.getRejectJustification() );
			return newId;
		} catch ( Exception e ) {
			throw new SicoveException( e );
		}
	}

	@Override
	public VehicleFuelRequestDAO readFullVehicleFuelRequest( Connection conn, int id ) throws SicoveException {
		log.info( "Looking for fuel request number " + id );

		StringBuilder query = new StringBuilder();

		query.append( "SELECT fuelingWallet.fueling_request_id                 AS fuelingRequestId," );
		query.append( "       fuelingWallet.vehicle_id                         AS vehicleId," );
		query.append( "       fuelingWallet.request_employee_responsible       AS employeeResponsible," );
		query.append( "       fuelingWallet.user_request                       AS userRequest," );
		query.append( "       fuelingWallet.justification_id                   AS justificationId," );
		query.append( "       fuelingWallet.fueling_amount                     AS fuelingAmount," );
		query.append( "       fuelingWallet.estimated_kilometers               AS estimatedKilometers," );
		query.append( "       fuelingWallet.id_process                         AS idProcess," );
		query.append( "       fuelingWallet.id_status                          AS idStatus," );
		query.append( "       fuelingWallet.wallet_number                      AS walletNumber," );
		query.append( "       fuelingWallet.authorized_amount                  AS authorizedAmount," );
		query.append( "       fuelingWallet.id_wallet                          AS idWallet," );
		query.append( "       Isnull(fuelingWallet.reject_justification, '') AS rejectJustification," );
		query.append( "       fuelingWallet.supplier_name                      AS walletSupplierName" );
		query.append( "  FROM vFuelingWallet fuelingWallet" );
		query.append( " WHERE fuelingWallet.fueling_request_id = ? " );

		VehicleFuelRequestDAO vehicleFuelRequest;
		try {
			log.trace( "Executing Query: \n" + query + "\n[" + id + "]" );
			vehicleFuelRequest = runner.query( conn, query.toString(), fullVehicleFuelRequestHandler, id );
			log.debug( "Founded: " + vehicleFuelRequest );

			return vehicleFuelRequest;

		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}
	}
}
