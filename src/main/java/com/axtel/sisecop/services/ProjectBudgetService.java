package com.axtel.sisecop.services;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.axtel.sisecop.dto.ProjectBudgetItemDTO;
import com.axtel.sisecop.entities.ProjectBudgetItem;
import com.axtel.sisecop.repostories.ProjectBudgetRepository;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;


public class ProjectBudgetService extends DataSourceManager {

	private static final Logger		log						= Logger.getLogger( ProjectBudgetService.class );
	private ProjectBudgetRepository	projectBudgetRepository	= new ProjectBudgetRepository();

	public ProjectBudgetService( String jniName ) {
		init( jniName );

	}

	public ProjectBudgetItem createProjectBudget( ProjectBudgetItemDTO projectBudgetDTO ) throws SQLException {
		Connection conn = null;

		try {
			conn = getConnection();
			ProjectBudgetItem projectBudget = projectBudgetRepository.create( conn, projectBudgetDTO );
			conn.commit();
			return projectBudget;
		} catch ( Exception e ) {
			log.error( e, e );
			Util.rollback( conn );
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public void deleteProjectBudget( int id ) throws SQLException {
		Connection conn = null;

		try {
			conn = getConnection();
			projectBudgetRepository.deleteProyectoServicioClave( conn, id );
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
