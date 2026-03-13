package com.axtel.sai.sicove.repositories.impl;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.FuelAsignationVerification;
import com.axtel.sai.sicove.entities.WalletFuelRequestVerificationDetail;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelAsignationVerificationRepository;


public class JDBCFuelAsignationVerificationRepository implements FuelAsignationVerificationRepository {

	private static final Logger													log							= LogManager.getLogger( JDBCFuelAsignationVerificationRepository.class );
	private final QueryRunner													runner						= new QueryRunner();
	private final ResultSetHandler<FuelAsignationVerification>					resultHandler				= new BeanHandler<FuelAsignationVerification>( FuelAsignationVerification.class );
	private final ResultSetHandler<WalletFuelRequestVerificationDetail>			resultHandlerDetail			= new BeanHandler<WalletFuelRequestVerificationDetail>( WalletFuelRequestVerificationDetail.class );
	private final ResultSetHandler<List<WalletFuelRequestVerificationDetail>>	resultHandlerDetailLst		= new BeanListHandler<WalletFuelRequestVerificationDetail>( WalletFuelRequestVerificationDetail.class );
	private final ScalarHandler<BigDecimal>										scalarHandler				= new ScalarHandler<>();
	private final StringBuilder													querySelectHeader			= new StringBuilder();
	private final StringBuilder													verificationDetailHeader	= new StringBuilder();

	public JDBCFuelAsignationVerificationRepository( ) {
		super();

		querySelectHeader.append( "SELECT	 id_verification AS idVerification " );
		querySelectHeader.append( "		,fueling_request_id AS fuelingRequestId " );
		querySelectHeader.append( "		,current_wallet_balance AS currentWalletBalance " );
		querySelectHeader.append( "		,current_vehicle_kilometers AS currentVehicleKilometers " );
		querySelectHeader.append( "		,initial_vehicle_kilometers AS initialVehicleKilometers " );
		querySelectHeader.append( "		,validation_amount AS validationAmount " );
		querySelectHeader.append( "		,verification_captured verificationCaptured " );
		querySelectHeader.append( "		,verification_authorized AS verificationAuthorized " );
		querySelectHeader.append( "		,verification_rejected AS verificationRejected " );
		querySelectHeader.append( "		,user_capture AS userCapture " );
		querySelectHeader.append( "  FROM	wallet_fuel_request_verification WITH(NOLOCK) " );

		verificationDetailHeader.append( "SELECT id_detail AS idDetail " );
		verificationDetailHeader.append( "      ,id_verification AS idVerification " );
		verificationDetailHeader.append( "      ,ticket_number AS ticketNumber " );
		verificationDetailHeader.append( "      ,ticket_amount AS ticketAmount " );
		verificationDetailHeader.append( "      ,ticket_date AS ticketDate " );
		verificationDetailHeader.append( "      ,ticket_reference AS ticketReference " );
		verificationDetailHeader.append( "      ,ticket_observations AS ticketObservations " );
		verificationDetailHeader.append( "      ,acepted AS acepted " );
		verificationDetailHeader.append( " FROM wallet_fuel_request_verification_detail WITH(NOLOCK) " );
	}

	@Override
	public FuelAsignationVerification createFuelingVerification( Connection conn, FuelAsignationVerification fuelAsignationVerification ) throws SicoveException {

		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO	wallet_fuel_request_verification( " );
		query.append( "		 fueling_request_id " );
		query.append( "		,current_wallet_balance " );
		query.append( "		,initial_vehicle_kilometers " );
		query.append( "		,current_vehicle_kilometers " );
		query.append( "		,validation_amount " );
		query.append( "		,user_capture " );
		query.append( "		) " );
		query.append( "VALUES	( " );
		query.append( "			? " );
		query.append( "			,? " );
		query.append( "			,? " );
		query.append( "			,? " );
		query.append( "			,? " );
		query.append( "			,? " );
		query.append( "		) " );

		log.info( "Saving: " + fuelAsignationVerification );

		if ( fuelAsignationVerification.getIdVerification() > 0 )
			throw new RuntimeException( "La comprobacion ya cuenta con ID. No puede guardarse" );

		BigDecimal newId;
		try {
			newId = runner.insert( conn, query.toString(), scalarHandler, 
					fuelAsignationVerification.getFuelingRequestId(),
					fuelAsignationVerification.getCurrentWalletBalance(),
					fuelAsignationVerification.getInitialVehicleKilometers(),
					fuelAsignationVerification.getCurrentVehicleKilometers(), 
					fuelAsignationVerification.getValidationAmount(), 
					fuelAsignationVerification.getUserCapture() );
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

		fuelAsignationVerification = readFuelingVerification( conn, newId.intValue() );

		return fuelAsignationVerification;

	}

	@Override
	public FuelAsignationVerification readFuelingVerification( Connection conn, int idVerification ) throws SicoveException {

		StringBuilder query = new StringBuilder( querySelectHeader );
		query.append( " WHERE	id_verification = ? " );

		log.debug( "Looking for asignation verification with id " + idVerification );
		log.trace( "Executing: \n" + query + "\n[" + idVerification + "]" );

		try {
			FuelAsignationVerification fuelAsignationVerification = runner.query( conn, query.toString(), resultHandler, idVerification );
			return fuelAsignationVerification;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error buscando comprobacion con folio: " + idVerification + " Error:" + e.toString(), e.getCause() );
		}
	}

	@Override
	public FuelAsignationVerification updateFuelingVerification( Connection conn, FuelAsignationVerification fuelAsignationVerification ) throws SicoveException {
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE	wallet_fuel_request_verification " );
		query.append( "   SET	 current_wallet_balance = ? " );
		query.append( "		,initial_vehicle_kilometers = ? " );
		query.append( "		,current_vehicle_kilometers = ? " );
		query.append( "		,validation_amount = ? " );
		query.append( "		,verification_authorized  = ? " );
		query.append( "		,verification_rejected  = ? " );
		query.append( " WHERE	id_verification = ? " );

		log.info( "Updating: " + fuelAsignationVerification );

		try {
			runner.update( conn, query.toString(), 
					fuelAsignationVerification.getCurrentWalletBalance(),
					fuelAsignationVerification.getInitialVehicleKilometers(),
					fuelAsignationVerification.getCurrentVehicleKilometers(), 
					fuelAsignationVerification.getValidationAmount(), 
					fuelAsignationVerification.getVerificationAuthorized(), 
					fuelAsignationVerification.getVerificationRejected(), 
					fuelAsignationVerification.getIdVerification() );
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

		fuelAsignationVerification = readFuelingVerification( conn, fuelAsignationVerification.getIdVerification() );

		return fuelAsignationVerification;
	}

	@Override
	public int readFuelingVerificationIdByAsignation( Connection conn, int idAsignation ) throws SicoveException {

		StringBuilder query = new StringBuilder( "SELECT id_verification FROM wallet_fuel_request_verification WITH(NOLOCK)  WHERE fueling_request_id = ? " );

		log.debug( "Looking for verification by id asignation " + idAsignation );
		log.trace( "Executing: \n" + query + "\n[" + idAsignation + "]" );

		try {
			Integer idVerification = runner.query( conn, query.toString(), new ScalarHandler<Integer>(), idAsignation );
			if ( idVerification == null )
				return 0;
			return idVerification.intValue();
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error buscando comprobacion para el folio: " + idAsignation + " Error:" + e.toString(), e.getCause() );
		}
	}

	@Override
	public WalletFuelRequestVerificationDetail saveVerificationDetail( Connection conn, WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException {

		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO wallet_fuel_request_verification_detail( " );
		query.append( "			id_verification " );
		query.append( "           ,ticket_number " );
		query.append( "           ,ticket_amount " );
		query.append( "           ,ticket_date " );
		query.append( "           ,ticket_reference " );
		query.append( "			) " );
		query.append( "VALUES " );
		query.append( "           (? " );
		query.append( "           ,? " );
		query.append( "           ,? " );
		query.append( "           ,? " );
		query.append( "           ,? " );
		query.append( "		   )" );

		log.info( "Saving Detail: " + verificationDetail );

		if ( verificationDetail.getIdDetail() > 0 )
			throw new RuntimeException( "El detalle de comprobacion ya cuenta con ID. No puede guardarse" );

		BigDecimal newId;
		try {
			newId = runner.insert( conn, query.toString(), scalarHandler, verificationDetail.getIdVerification(), verificationDetail.getTicketNumber(), verificationDetail.getTicketAmount(), verificationDetail.getTicketDate(), verificationDetail.getTicketReference() );
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

		verificationDetail = readVerificationDetail( conn, newId.intValue() );

		return verificationDetail;
	}

	@Override
	public WalletFuelRequestVerificationDetail readVerificationDetail( Connection conn, int idDetail ) throws SicoveException {
		StringBuilder query = new StringBuilder( verificationDetailHeader.toString() );
		query.append( "WHERE id_detail = ? " );

		log.debug( "Looking for verification detail with id " + idDetail );
		log.trace( "Executing: \n" + query + "\n[" + idDetail + "]" );

		try {
			WalletFuelRequestVerificationDetail walletFuelRequestVerificationDetail = runner.query( conn, query.toString(), resultHandlerDetail, idDetail );
			return walletFuelRequestVerificationDetail;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error buscando detalle de comprobacion con folio: " + idDetail + " Error:" + e.toString(), e.getCause() );
		}

	}

	@Override
	public List<WalletFuelRequestVerificationDetail> readFuelingVerificationDetailList( Connection conn, int idVerification ) throws SicoveException {

		StringBuilder query = new StringBuilder( verificationDetailHeader.toString() );
		query.append( "WHERE id_verification = ? " );

		log.debug( "Looking for verification detail list with id " + idVerification );
		log.trace( "Executing: \n" + query + "\n[" + idVerification + "]" );

		try {
			List<WalletFuelRequestVerificationDetail> verificationDetailList = runner.query( conn, query.toString(), resultHandlerDetailLst, idVerification );
			return verificationDetailList;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error buscando detalle de comprobacion con folio: " + idVerification + " Error:" + e.toString(), e.getCause() );
		}
	}

	 
	@Override
	public WalletFuelRequestVerificationDetail updateFuelingVerificationDetail( Connection conn, WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE wallet_fuel_request_verification_detail ");
		query.append("   SET ticket_observations = ? ");
		query.append("      ,acepted = ? ");
		query.append(" WHERE id_detail = ? ");
		try {
			
			runner.update( conn, 
					query.toString(), 
					verificationDetail.getTicketObservations(),
					verificationDetail.isAcepted(),
					verificationDetail.getIdDetail());
			return readVerificationDetail(conn, verificationDetail.getIdDetail() );
		}catch (Exception e) {
			log.error( e,e );
			throw new SicoveException("Error actualizando detalle de comprobacion " + e.toString(), e);
		}
	}

	@Override
	public void deleteFuelingVerificationDetail( Connection conn, WalletFuelRequestVerificationDetail detail ) throws SicoveException {

		StringBuilder query = new StringBuilder();
		query.append( "DELETE FROM wallet_fuel_request_verification_detail " );
		query.append( " WHERE id_detail = ? " );

		try {

			runner.update( conn, query.toString(), detail.getIdDetail() );
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error actualizando detalle de comprobacion " + e.toString(), e );
		}

	}

	@Override
	public void updatePendingAmount( Connection conn, WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException {
		
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE wallet_fuel_request_verification " );
		query.append( "SET  validation_amount = validation_amount - ? " );
		query.append( " WHERE id_verification = ? " );

		try {
			log.trace( "Executing: \n" + query + "\n[" + verificationDetail.getTicketAmount() + "][" +verificationDetail.getIdVerification() +"]"   );
			runner.update( conn, query.toString(), verificationDetail.getTicketAmount(), verificationDetail.getIdVerification() );
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error actualizando detalle de comprobacion " + e.toString(), e );
		}
		
	}

}
