package com.axtel.sai.sicove.repositories.impl;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.Vehicle;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.VehicleRepository;


public class JDBCVehicleRepository implements VehicleRepository {

	private static final Logger				log				= Logger.getLogger( JDBCVehicleRepository.class );
	private final QueryRunner				runner			= new QueryRunner();
	private final ResultSetHandler<Vehicle>	vehicleHandler	= new BeanHandler<Vehicle>( Vehicle.class );

	@Override
	public Vehicle findByInventoryId( Connection conn, int inventoryId ) throws SicoveException {
		StringBuilder query = new StringBuilder();
		query.append( " SELECT id, " );
		query.append( "       id_inventory       AS idInventory, " );
		query.append( "       inventory_code     AS inventoryCode, " );
		query.append( "       description, " );
		query.append( "       type, " );
		query.append( "       brand, " );
		query.append( "       sub_brand          AS subBrand, " );
		query.append( "       model, " );
		query.append( "       color, " );
		query.append( "       doors, " );
		query.append( "       cylinders, " );
		query.append( "       transmission_type  AS transmissionType, " );
		query.append( "       registered_date    AS registeredDate, " );
		query.append( "       serial, " );
		query.append( "       unit_cost_total    AS unitCostTotal, " );
		query.append( "       agency_id          AS agencyId, " );
		query.append( "       use_id             AS useId, " );
		query.append( "       liscence_plate     AS licensePlate, " );
		query.append( "       current_kilometers AS currentKilometers, " );
		query.append( "       status_id          AS statusId " );
		query.append( "FROM   vehicle  WITH(NOLOCK) " );
		query.append( "WHERE  id_inventory = ? " );

		Vehicle vehicle;
		try {
			vehicle = runner.query( conn, query.toString(), vehicleHandler, inventoryId );
			log.debug( "Found: " + vehicle );

			return vehicle;
		} catch ( SQLException e ) {
			throw new SicoveException( e );
		}

	}

}
