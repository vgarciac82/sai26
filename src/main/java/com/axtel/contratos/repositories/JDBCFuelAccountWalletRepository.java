package com.axtel.contratos.repositories;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.axtel.contratos.entities.FuelAccountWallet;
import com.axtel.contratos.exception.ContratoException;


public class JDBCFuelAccountWalletRepository implements FuelAccountWalletRepository {

	private static final Logger							log									= Logger.getLogger( JDBCFuelAccountWalletRepository.class );
	private final QueryRunner							runner								= new QueryRunner();
	private final ResultSetHandler<FuelAccountWallet>	fuelAccountWalletRepositoryHandler	= new BeanHandler<FuelAccountWallet>( FuelAccountWallet.class );
	private final ScalarHandler<BigDecimal>				scalarHandler						= new ScalarHandler<>();

	@Override
	public FuelAccountWallet insert( Connection conn, FuelAccountWallet fuelAccountWallet ) throws ContratoException {
		log.info( "Insertando: " + fuelAccountWallet );
		StringBuilder queryInsert = new StringBuilder();
		queryInsert.append( "INSERT INTO fuelAccountWallets " );
		queryInsert.append( "           (id_contract_account " );
		queryInsert.append( "           ,wallet_number " );
		queryInsert.append( "           ,vehicle_inventory_id " );
		queryInsert.append( "           ,user_registration " );
		queryInsert.append( "           ) " );
		queryInsert.append( "     VALUES " );
		queryInsert.append( "           (?, " );
		queryInsert.append( "            ?, " );
		queryInsert.append( "            ?, " );
		queryInsert.append( "            ?) " );

		if ( fuelAccountWallet.getIdFuelAccountWallet() > 0 )
			throw new RuntimeException( "El monedero ya cuenta con ID. No puede guardarse" );

		BigDecimal newId;
		try {
			newId = runner.insert( conn, queryInsert.toString(), scalarHandler, fuelAccountWallet.getIdContractAccount(), fuelAccountWallet.getWalletNumber(), fuelAccountWallet.getVehicleInventoryId(), fuelAccountWallet.getUserRegistration() );
		} catch ( SQLException e ) {
			throw new ContratoException( e );
		}

		fuelAccountWallet = readAccountWallet( conn, newId.intValue() );

		return fuelAccountWallet;
	}

	@Override
	public FuelAccountWallet readAccountWallet( Connection conn, int id ) throws ContratoException {
		StringBuilder querySelect = new StringBuilder();
		querySelect.append( "SELECT	id_fuel_account_wallets AS idFuelAccountWallet, " );
		querySelect.append( "		id_contract_account AS idContractAccount, " );
		querySelect.append( "		wallet_number AS walletNumber, " );
		querySelect.append( "		vehicle_inventory_id AS vehicleInventoryId, " );
		querySelect.append( "		registration_date AS registrationDate, " );
		querySelect.append( "		user_registration AS userRegistration " );
		querySelect.append( "  FROM	fuelAccountWallets WITH(NOLOCK) " );
		querySelect.append( " WHERE	id_fuel_account_wallets = ? " );

		FuelAccountWallet accountWallet;
		try {
			accountWallet = runner.query( conn, querySelect.toString(), fuelAccountWalletRepositoryHandler, id );
		} catch ( SQLException e ) {
			throw new ContratoException( e );
		}
		log.debug( "Se encontro: " + accountWallet );

		return accountWallet;

	}

	@Override
	public FuelAccountWallet update( Connection conn, FuelAccountWallet fuelAccountWallet ) throws ContratoException {
		StringBuilder queryUpdate = new StringBuilder();
		queryUpdate.append("UPDATE fuelAccountWallets ");
		queryUpdate.append("   SET vehicle_inventory_id = ? ");
		queryUpdate.append("      ,status = ? ");
		queryUpdate.append(" WHERE id_fuel_account_wallets = ? ");
 
		try {
			runner.update( conn, 
					queryUpdate.toString(), 
					fuelAccountWallet.getVehicleInventoryId(),
					fuelAccountWallet.getStatus(),
					fuelAccountWallet.getIdFuelAccountWallet());
			return readAccountWallet( conn, fuelAccountWallet.getIdFuelAccountWallet() );
		} catch ( SQLException e ) {
			throw new ContratoException( e );
		}
		

	}

}
