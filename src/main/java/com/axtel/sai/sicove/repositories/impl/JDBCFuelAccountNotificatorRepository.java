package com.axtel.sai.sicove.repositories.impl;


import java.sql.Connection;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.RequestAuthChain;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingNotificatorRepository;


public class JDBCFuelAccountNotificatorRepository implements FuelingNotificatorRepository {

	private static final Logger							log				= LogManager.getLogger( JDBCFuelAccountNotificatorRepository.class );
	private final QueryRunner							runner			= new QueryRunner();
	private final ResultSetHandler<RequestAuthChain>	resultHandler	= new BeanHandler<RequestAuthChain>( RequestAuthChain.class );

	@Override
	public RequestAuthChain getRequestAuthChain( Connection conn, int requestId ) throws SicoveException {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	account_number AS accountNumber, " );
		query.append( "		employee_responsible_name AS applicantName, " );
		query.append( "		position AS applicantPosition, " );
		query.append( "		employee_responsible_mail AS applicantMail, " );
		query.append( "		authorizer_name AS authorizerName, " );
		query.append( "		authorizer_position AS authorizerPosition, " );
		query.append( "		authorizer_mail AS authorizerMail, " );
		query.append( "		authorizer_employee_number AS authorizerEmployeeNumber,* " );
		query.append( "  FROM	vFuelingRequest " );
		query.append( " WHERE	id_fuel_provisioning_request = ? " );
		log.debug( "Looking for authorization chain for id " + requestId );
		log.trace( "Executing: \n" + query + "\n[" + requestId + "]" );
		try {
			RequestAuthChain requestAuthChain = runner.query( conn, query.toString(), resultHandler, requestId );
			return requestAuthChain;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error buscando unidad ejecutora: " + e.toString(), e.getCause() );
		}

	}
}
