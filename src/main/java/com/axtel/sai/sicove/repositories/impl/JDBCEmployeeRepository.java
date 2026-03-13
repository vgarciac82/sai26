package com.axtel.sai.sicove.repositories.impl;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.EmployeeDAO;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.EmployeeRepository;


public class JDBCEmployeeRepository implements EmployeeRepository {

	private static final Logger					log				= Logger.getLogger( JDBCEmployeeRepository.class );
	private final QueryRunner					runner			= new QueryRunner();
	private final ResultSetHandler<EmployeeDAO>	employeeHandler	= new BeanHandler<EmployeeDAO>( EmployeeDAO.class );

	private StringBuilder						queryHeader;

	public JDBCEmployeeRepository( ) {
		queryHeader = new StringBuilder();
		queryHeader.append( "SELECT nombren + ' ' + nombrep + ' ' + nombrem AS employeeResponsibleName, " );
		queryHeader.append( "       ua                                      AS executiveUnit, " );
		queryHeader.append( "       cuejecutora                             AS budgetUnit, " );
		queryHeader.append( "       cargo                                   AS position " );
		queryHeader.append( "FROM   v_empleados_giro " );

	}

	@Override
	public EmployeeDAO readEmployee( Connection conn, String idEmployee ) throws SicoveException {
		StringBuilder query = new StringBuilder( queryHeader ).append( " WHERE  clave = ?   " );
		EmployeeDAO employee;
		try {
			employee = runner.query( conn, query.toString(), employeeHandler, idEmployee );
			log.debug( "Found: " + employee );

			return employee;
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}
	}

	@Override
	public EmployeeDAO readEmployeeByLogin( Connection conn, String login ) throws SicoveException {
		StringBuilder query = new StringBuilder( queryHeader ).append( " WHERE REPLACE(d_email,'@conafor.gob.mx','' ) = ? " );
		EmployeeDAO employee;
		try {
			employee = runner.query( conn, query.toString(), employeeHandler, login );
			log.debug( "Found: " + employee );

			return employee;
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

	}

}
