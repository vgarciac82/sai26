package com.axtel.sai.sicove.services.impl;


import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.FuelAsignationVerification;
import com.axtel.sai.sicove.entities.WalletFuelRequestVerificationDetail;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.expedient.repositories.ExpedientRepository;
import com.axtel.sai.sicove.repositories.FuelAsignationVerificationRepository;
import com.axtel.sai.sicove.services.FuelAsignationVerificationService;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.util.Util;


public class JDBCFuelAsignationVerificationService extends DataSourceManager implements FuelAsignationVerificationService {

	private static final Logger						log	= LogManager.getLogger( JDBCFuelAsignationVerificationService.class );

	private FuelAsignationVerificationRepository	fuelAsignationVerificationRepository;

	private ExpedientRepository						expedientRepository;

	private String									jniName;

	public JDBCFuelAsignationVerificationService( String jniName, FuelAsignationVerificationRepository fuelAsignationVerificationRepository ) {
		super.init( jniName );
		this.jniName = jniName;
		this.fuelAsignationVerificationRepository = fuelAsignationVerificationRepository;
	}

	@Override
	public FuelAsignationVerification createFuelingVerification( FuelAsignationVerification fuelAsignationVerification ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			fuelAsignationVerification = fuelAsignationVerificationRepository.createFuelingVerification( conn, fuelAsignationVerification );
			conn.commit();
			return fuelAsignationVerification;
		} catch ( Exception e ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( e2.toString(), e2 );
				}
			log.error( e, e );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public FuelAsignationVerification readFuelingVerification( int id ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			return fuelAsignationVerificationRepository.readFuelingVerification( conn, id );
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public int readFuelingVerificationIdByAsignation( int idAsignation ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			return fuelAsignationVerificationRepository.readFuelingVerificationIdByAsignation( conn, idAsignation );
		} catch ( Exception e ) {
			log.error( e, e );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public FuelAsignationVerification updateFuelingVerification( FuelAsignationVerification fuelAsignationVerification ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			fuelAsignationVerification = fuelAsignationVerificationRepository.updateFuelingVerification( conn, fuelAsignationVerification );
			conn.commit();
			return fuelAsignationVerification;
		} catch ( Exception e ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( e2.toString(), e2 );
				}
			log.error( e, e );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public WalletFuelRequestVerificationDetail addVerificationDetail( int idProcess, String folderName, File file, WalletFuelRequestVerificationDetail verificationDetail, String userName ) throws SicoveException {

		Connection conn = null;
		Fortimax fmx = null;
		try {

			conn = getConnection();
			fmx = expedientRepository.saveDocument( conn, idProcess, folderName, userName, verificationDetail.getTicketNumber(), file );
			verificationDetail.setTicketReference( fmx.toString() );
			verificationDetail = fuelAsignationVerificationRepository.saveVerificationDetail( conn, verificationDetail );
			fuelAsignationVerificationRepository.updatePendingAmount( conn, verificationDetail );
			
			conn.commit();
			return verificationDetail;
		} catch ( Exception e ) {
			Util.rollback( conn );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}

	}

	public void setExpedientRepository( ExpedientRepository expedientRepository ) {
		this.expedientRepository = expedientRepository;
	}

	@Override
	public List<WalletFuelRequestVerificationDetail> readFuelingVerificationDetailList( int idVerification ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			return fuelAsignationVerificationRepository.readFuelingVerificationDetailList( conn, idVerification );
		} catch ( Exception e ) {
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public WalletFuelRequestVerificationDetail readFuelingVerificationDetail( int idDetail ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			return fuelAsignationVerificationRepository.readVerificationDetail( conn, idDetail );
		} catch ( Exception e ) {
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public WalletFuelRequestVerificationDetail updateFuelingVerificationDetail( WalletFuelRequestVerificationDetail verificationDetail ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			WalletFuelRequestVerificationDetail detail = fuelAsignationVerificationRepository.updateFuelingVerificationDetail( conn, verificationDetail );
			conn.commit();
			return detail;
		} catch ( Exception e ) {
			Util.rollback( conn );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public void deleteFuelingVerificationDetail( WalletFuelRequestVerificationDetail detail ) throws SicoveException {

		Connection conn = null;
		CasoBusinessLogic processService = new CasoBusinessLogic( jniName );

		try {

			conn = getConnection();
			log.debug( "Deleting document from expedient: " + detail.getTicketReference() );
			processService.deleteDocument( conn, detail.getTicketReference() );
			log.debug( "Deleting verification detail info: " + detail );
			
			fuelAsignationVerificationRepository.deleteFuelingVerificationDetail( conn, detail );
			detail.setTicketAmount( detail.getTicketAmount().negate());
			
			fuelAsignationVerificationRepository.updatePendingAmount( conn, detail );
			
			conn.commit();
		} catch ( Exception e ) {
			Util.rollback( conn );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

}
