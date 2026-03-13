package com.axtel.contratos.repositories;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.axtel.contratos.entities.FuelProvisioningRequest;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.sai.sicove.SICOVE;


public class JDBCFuelProvisioningRequestRepository implements FuelProvisioningRequestRepository {

	private static final Logger								log								= Logger.getLogger( JDBCFuelProvisioningRequestRepository.class );
	private final QueryRunner								runner							= new QueryRunner();
	private final ResultSetHandler<FuelProvisioningRequest>	fuelProvisioningRequestHandler	= new BeanHandler<FuelProvisioningRequest>( FuelProvisioningRequest.class );
	private final ScalarHandler<BigDecimal>					scalarHandler					= new ScalarHandler<>();

	@Override
	public FuelProvisioningRequest insert( Connection conn, FuelProvisioningRequest fuelProvisioningRequest ) throws ContratoException {
		log.info( "Insertando: " + fuelProvisioningRequest );
		StringBuilder queryInsert = new StringBuilder();
		queryInsert.append( "INSERT INTO fuel_provisioning_request( " );
		queryInsert.append( "			id_account " );
		queryInsert.append( "		   ,user_request " );
		queryInsert.append( "           ,request_amount " );
		queryInsert.append( "           ,request_justification " );
		queryInsert.append( "           ,request_status " );
		queryInsert.append( "           ,process_id) " );
		queryInsert.append( "     VALUES(? " );
		queryInsert.append( "		    ,? " );
		queryInsert.append( "           ,? " );
		queryInsert.append( "           ,? " );
		queryInsert.append( "           ,? " );
		queryInsert.append( "           ,?) " );

		if ( fuelProvisioningRequest.getFuelProvisioningRequestId() > 0 )
			throw new RuntimeException( "La solicitud ya cuenta con ID. No puede guardarse" );

		BigDecimal newId;
		try {
			newId = runner.insert( conn, queryInsert.toString(), scalarHandler, fuelProvisioningRequest.getFuelContractAccountId(), fuelProvisioningRequest.getUserRequest(), fuelProvisioningRequest.getRequestAmount(), fuelProvisioningRequest.getRequestJustification(), fuelProvisioningRequest.getRequestStatus(), fuelProvisioningRequest.getProcessId() );
			fuelProvisioningRequest = readFuelProvisioning( conn, newId.intValue() );

			return fuelProvisioningRequest;
		} catch ( SQLException e ) {
			throw new ContratoException( e );
		}

	}

	@Override
	public FuelProvisioningRequest readFuelProvisioning( Connection conn, int id ) throws ContratoException {

		StringBuilder querySelect = new StringBuilder();
		querySelect.append( "SELECT id_fuel_provisioning_request AS fuelProvisioningRequestId " );
		querySelect.append( "      ,id_account AS fuelContractAccountId " );
		querySelect.append( "      ,request_date AS requestDate " );
		querySelect.append( "      ,user_request AS userRequest " );
		querySelect.append( "      ,request_month AS requestMonth " );
		querySelect.append( "      ,request_amount AS requestAmount " );
		querySelect.append( "      ,request_justification AS requestJustification " );
		querySelect.append( "      ,reject_justification AS rejectJustification " );
		querySelect.append( "      ,autorized_amount AS autorizedAmount " );
		querySelect.append( "      ,request_status AS requestStatus " );
		querySelect.append( "      ,autorization_date AS autorizationDate " );
		querySelect.append( "      ,process_id AS processId " );
		querySelect.append( "  FROM fuel_provisioning_request WITH(NOLOCK) " );
		querySelect.append( " WHERE	id_fuel_provisioning_request = ? " );

		FuelProvisioningRequest fuelProvisioningRequest;
		try {
			fuelProvisioningRequest = runner.query( conn, querySelect.toString(), fuelProvisioningRequestHandler, id );
			log.debug( "Se encontro: " + fuelProvisioningRequest );
			return fuelProvisioningRequest;
		} catch ( SQLException e ) {
			throw new ContratoException( e );
		}

	}

	@Override
	public FuelProvisioningRequest update( Connection conn, FuelProvisioningRequest fuelProvisioningRequest ) throws ContratoException {

		log.info( "Actualizando: " + fuelProvisioningRequest );

		StringBuilder queryUpdate = new StringBuilder();
		queryUpdate.append( "UPDATE	fuel_provisioning_request " );
		queryUpdate.append( "   SET	reject_justification = ? " );
		queryUpdate.append( "		,autorized_amount = ? " );
		queryUpdate.append( "		,request_status = ? " );
		queryUpdate.append( "		,autorization_date = ? " );
		queryUpdate.append( "		,id_account = ? " );
		queryUpdate.append( "		,request_justification = ? " );
		queryUpdate.append( "		,request_amount = ? " );
		queryUpdate.append( " WHERE	id_fuel_provisioning_request = ? " );

		if ( fuelProvisioningRequest.getFuelProvisioningRequestId() == 0 )
			throw new RuntimeException( "La solicitud no cuenta con ID. No puede actualizarse" );

		try {
			runner.update( conn, queryUpdate.toString(), 
					fuelProvisioningRequest.getRejectJustification(), 
					fuelProvisioningRequest.getAutorizedAmount(), 
					fuelProvisioningRequest.getRequestStatus(), 
				    
					(	fuelProvisioningRequest.getAutorizationDate() == null
						? 
						null:
						new java.sql.Timestamp(fuelProvisioningRequest.getAutorizationDate().getTime())
					),
					
					fuelProvisioningRequest.getFuelContractAccountId(), 
					fuelProvisioningRequest.getRequestJustification(), 
					fuelProvisioningRequest.getRequestAmount(), 
					fuelProvisioningRequest.getFuelProvisioningRequestId() );

			fuelProvisioningRequest = readFuelProvisioning( conn, fuelProvisioningRequest.getFuelProvisioningRequestId() );

			return fuelProvisioningRequest;
		} catch ( SQLException e ) {
			throw new ContratoException( e );
		}
	}

	@Override
	public boolean delete( Connection conn, int id ) throws ContratoException {
		log.info( "Deleting fuel_provisioning_request: " + id );

		StringBuilder queryUpdate = new StringBuilder();
		queryUpdate.append( "UPDATE	fuel_provisioning_request " );
		queryUpdate.append( "   SET	request_status = ? " );
		queryUpdate.append( " WHERE	id_fuel_provisioning_request = ? " );

		try {
			runner.update( conn, queryUpdate.toString(), SICOVE.FUELING_REQUEST_DISCARD, id );
			return true;
		} catch ( SQLException e ) {
			throw new ContratoException( e );
		}
	}
}
