package com.axtel.contratos.services.implementation;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.contratos.entities.FuelProvisioningRequest;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.contratos.repositories.FuelProvisioningRequestRepository;
import com.axtel.contratos.services.FuelProvisioningRequestService;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


public class JDBCFuelProvisioningRequestService extends DataSourceManager implements FuelProvisioningRequestService {

	private static final Logger						log				= LogManager.getLogger( JDBCFuelProvisioningRequestService.class );
	private FuelProvisioningRequestRepository		fuelProvisioningRequestRepository;
	private static final FolioGeneratorInterface	FOLIO_GENERATOR	= Util.getFolioGenerator( GestionInterface.FOLIO_GENERATOR );

	public JDBCFuelProvisioningRequestService( FuelProvisioningRequestRepository fuelProvisioningRequestRepository, String jniName ) {
		super.init( jniName );
		this.fuelProvisioningRequestRepository = fuelProvisioningRequestRepository;
	}

	@Override
	public FuelProvisioningRequest insert( FuelProvisioningRequest fuelProvisioningRequest ) throws ContratoException {
		Connection conn = null;
		try {
			conn = getConnection();
			Caso c = initProcess( conn, fuelProvisioningRequest );
			fuelProvisioningRequest.setProcessId( c.getIdCaso() );
			fuelProvisioningRequest = fuelProvisioningRequestRepository.insert( conn, fuelProvisioningRequest );
			conn.commit();
			return fuelProvisioningRequest;
		} catch ( Exception e ) {
			log.error( e, e );
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( e2 );
				}

			throw new ContratoException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public Caso initProcess( Connection conn, FuelProvisioningRequest fuelProvisioningRequest ) throws SicoveException {
		try {
			Usuario u = new Usuario( fuelProvisioningRequest.getUserRequest() );
			u = UsuarioManager.select( conn, u );
			u = UsuarioManager.getRamoUR( conn, u );
			Map<String, String> variables = new HashMap<>();
			return Util.generaCaso( conn, u, 99, FOLIO_GENERATOR, "REQFUELWALLET", variables );
		} catch ( Exception e ) {
			throw new SicoveException( e );
		}
	}

	@Override
	public FuelProvisioningRequest readFuelProvisioning( int id ) throws ContratoException {
		Connection conn = null;
		try {
			conn = getConnection();
			return fuelProvisioningRequestRepository.readFuelProvisioning( conn, id );
		} catch ( SQLException e ) {
			log.error( e, e );
			throw new ContratoException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public FuelProvisioningRequest update( FuelProvisioningRequest fuelProvisioningRequest ) throws ContratoException {
		Connection conn = null;
		try {
			conn = getConnection();
			fuelProvisioningRequest = fuelProvisioningRequestRepository.update( conn, fuelProvisioningRequest );
			conn.commit();
			return fuelProvisioningRequest;
		} catch ( Exception e ) {
			log.error( e, e );
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( e2 );
				}

			throw new ContratoException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public boolean deleteFuelProvisioning( int id ) throws ContratoException {
		Connection conn = null;
		try {
			conn = getConnection();
			fuelProvisioningRequestRepository.delete( conn, id );
			conn.commit();
			return true;
		} catch ( Exception e ) {
			log.error( e, e );
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( e2 );
				}

			throw new ContratoException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

}
