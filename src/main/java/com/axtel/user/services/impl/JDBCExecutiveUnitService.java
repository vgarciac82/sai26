package com.axtel.user.services.impl;


import java.sql.Connection;
import java.sql.SQLException;

import com.axtel.user.UserException;
import com.axtel.user.entities.ExecutiveUnit;
import com.axtel.user.repositories.ExecutiveUnitRepository;
import com.axtel.user.services.ExecutiveUnitService;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;


public class JDBCExecutiveUnitService extends DataSourceManager implements ExecutiveUnitService {

	private ExecutiveUnitRepository executiveUnitRepository;

	public JDBCExecutiveUnitService( String jniName, ExecutiveUnitRepository executiveUnitRepository ) {
		super.init( jniName );
		this.executiveUnitRepository = executiveUnitRepository;
	}

	@Override
	public ExecutiveUnit getExecutiveUnitByAU( String administrativeUnit ) throws UserException {
		Connection connection = null;
		try {
			connection = getConnection();
			return executiveUnitRepository.getExecutiveUnitByAU( connection, administrativeUnit );
		} catch ( SQLException e ) {
			throw new UserException( "Error de base de datos al obtener unidad ejecutora: " + e.toString(), e );
		} finally {
			CloseObject.closeObject( connection );
		}
	}

}
