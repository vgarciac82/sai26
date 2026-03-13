package com.axtel.sai.sicove.repositories.impl;


import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.FuelAccountWalletRefund;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelAccountWalletRefundRepository;


public class JDBCFuelAccountWalletRefundRepository implements FuelAccountWalletRefundRepository {

	private static final Logger								log				= LogManager.getLogger( JDBCFuelAccountWalletRefundRepository.class );
	private final QueryRunner								runner			= new QueryRunner();
	private final ResultSetHandler<FuelAccountWalletRefund>	resultHandler	= new BeanHandler<FuelAccountWalletRefund>( FuelAccountWalletRefund.class );
	private final ScalarHandler<BigDecimal>					scalarHandler	= new ScalarHandler<>();
	private final StringBuilder								queryInsert;
	private final StringBuilder								querySelect;
	private final StringBuilder								selectHeader;
	private final StringBuilder								queryUpdate;
	private final StringBuilder								queryDelete;
	
	
	public JDBCFuelAccountWalletRefundRepository( ) {
		
		selectHeader = new  StringBuilder()
				.append("SELECT id_refund AS idRefund ")
				.append("      ,id_fuel_account_wallets AS idFuelAccountWallets ")
				.append("      ,wallet_number AS walletNumber ")
				.append("      ,refund_amount AS refundAmount ")
				.append("      ,registration_date AS registrationDate ")
				.append("      ,user_capture AS userCapture ")
				.append("      ,(case when is_supplier_refund = 1 then 'true' else 'false' end) AS supplierRefund ")
				.append("  FROM fuel_account_wallet_refund WITH(NOLOCK) ");
		
		queryInsert = new StringBuilder( "INSERT INTO fuel_account_wallet_refund( " )
				.append( "            id_fuel_account_wallets " )
				.append( "           ,wallet_number " )
				.append( "           ,refund_amount " )
				.append( "           ,user_capture " )
				.append( "           ,registration_date " )
				.append( "           ,is_supplier_refund) " )
				.append( "     VALUES(?, " )
				.append( "           ?, " )
				.append( "           ?, " )
				.append( "           ?, " )
				.append( "           ?, " )
				.append( "           ?)  " );
		
		querySelect = new StringBuilder(selectHeader) 
				.append(" WHERE id_refund = ? ");
		
		queryDelete = new StringBuilder("DELETE FROM fuel_account_wallet_refund WHERE id_refund = ? ");
		
		queryUpdate = new StringBuilder("UPDATE fuel_account_wallet_refund ")
				.append("   SET refund_amount = ? ")
				.append("      ,registration_date = GETDATE() ")
				.append(" WHERE id_refund = ? ");
	}

	@Override
	public FuelAccountWalletRefund create( Connection conn, FuelAccountWalletRefund fuelAccountWalletRefund ) throws SicoveException {
		
		log.info( "Saving FuelAccountWalletRefund: \n" + fuelAccountWalletRefund + "\n");

		if ( fuelAccountWalletRefund.getIdRefund() != null && fuelAccountWalletRefund.getIdRefund() > 0 )
			throw new RuntimeException( "La devolucion ya cuenta con ID. No puede guardarse" );

		BigDecimal newId;
		
		try {
			
			newId = runner.insert( conn, 
						queryInsert.toString(), 
						scalarHandler, 
						fuelAccountWalletRefund.getIdFuelAccountWallets(),
						fuelAccountWalletRefund.getWalletNumber() ,
						fuelAccountWalletRefund.getRefundAmount() ,
						fuelAccountWalletRefund.getUserCapture(),
						fuelAccountWalletRefund.getRegistrationDate(),
						(fuelAccountWalletRefund.isSupplierRefund()?1:0)
					);
			fuelAccountWalletRefund = read( conn, newId.intValue()  );

			return fuelAccountWalletRefund;
		} catch ( Exception e ) {
			throw new SicoveException( e );
		}
		
	}

	@Override
	public FuelAccountWalletRefund read( Connection conn, Integer idRefund ) throws SicoveException {
		log.debug( "Looking for FuelAccountWalletRefund with id " + idRefund );
		log.trace( "Executing: \n" + querySelect + "\n[" + idRefund + "]" );
		try {
			FuelAccountWalletRefund fuelAccountWalletRefund = runner.query( conn, querySelect.toString(), resultHandler, idRefund );
			return fuelAccountWalletRefund;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error buscando devolucion de saldo de tarjeta con folio: " + idRefund + " Error:" + e.toString(), e.getCause() );
		}
	}

	@Override
	public FuelAccountWalletRefund update( Connection conn, Integer idRefund, FuelAccountWalletRefund fuelAccountWalletRefund ) throws SicoveException {
		log.info( "Updating FuelAccountWalletRefund with id " + idRefund + " new Object: \n" + fuelAccountWalletRefund );

		try {
			log.trace( "Executing: \n" + queryUpdate + "\n[" + idRefund + "]" );
			runner.update( conn, queryUpdate.toString(), fuelAccountWalletRefund.getRefundAmount(), idRefund );
			fuelAccountWalletRefund = read( conn, idRefund );
			return fuelAccountWalletRefund;
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error eliminando devolucion de saldo de tarjeta con folio: " + idRefund + " Error:" + e.toString(), e.getCause() );
		}
	}

	@Override
	public void delete( Connection conn, Integer idRefund ) throws SicoveException {
		log.info( "Deleting FuelAccountWalletRefund with id " + idRefund );

		try {
			log.trace( "Executing: \n" + queryDelete + "\n[" + idRefund + "]" );
			runner.update( conn, queryDelete.toString(), idRefund );
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( "Error eliminando devolucion de saldo de tarjeta con folio: " + idRefund + " Error:" + e.toString(), e.getCause() );
		}

	}

}
