package com.axtel.sisecop.services;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.axtel.sisecop.dto.ProjectPaymentDTO;
import com.axtel.sisecop.entities.ProyectoServicioPago;
import com.axtel.sisecop.repostories.ProjectPaymentRepository;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;


public class ProjectPaymentService extends DataSourceManager {

	private static final Logger			log							= Logger.getLogger( ProjectPaymentService.class );
	private ProjectPaymentRepository	projectPaymentRepository	= new ProjectPaymentRepository();

	public ProjectPaymentService( String jniName ) {
		init( jniName );
	}

	public ProyectoServicioPago createProjectPayment( ProjectPaymentDTO projectPaymentDTO ) throws SQLException {
		Connection conn = null;

		try {
			conn = getConnection();
			ProyectoServicioPago projectPayment = projectPaymentRepository.create( conn, projectPaymentDTO );
			conn.commit();
			return projectPayment;
		} catch ( Exception e ) {
			log.error( e, e );
			Util.rollback( conn );
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public void deleteProjectPayment( int id ) throws SQLException {
		Connection conn = null;

		try {
			conn = getConnection();
			projectPaymentRepository.deleteProyectoServicioPago( conn, id );
			conn.commit();
		} catch ( Exception e ) {
			log.error( e, e );
			Util.rollback( conn );
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

}
