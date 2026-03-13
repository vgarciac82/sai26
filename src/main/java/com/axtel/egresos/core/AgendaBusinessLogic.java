package com.axtel.egresos.core;


import java.sql.Connection;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.egresos.viaticos.Agenda;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class AgendaBusinessLogic extends DataSourceManager {

	private static final Logger log = LogManager.getLogger( AgendaBusinessLogic.class );
	
	public AgendaBusinessLogic (String jiniName) {
		log.info( "Creating AgendaBusinessLogic with reference to " + jiniName );
		super.release();
		log.trace( "Data source released initializng new one" );
		super.init(jiniName);
		log.trace( "Data source created" );
	}

	public List<Agenda> listaAgendas( int idEmpleado ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			List<Agenda> agendas = AgendaManager.consultaAgendas( conn, idEmpleado );
			
			return agendas;
			
		} finally {
			CloseObject.closeObject( conn );
			release();
		}
	}

	public Agenda consultaAgenda( int idAgenda ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			Agenda agenda = AgendaManager.consultaAgenda( conn, idAgenda );
			
			return agenda;
			
		} finally {
			CloseObject.closeObject( conn );
			release();
		}
	}
	
	public String guardaAgenda( Agenda agenda ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			String folio = AgendaManager.guardaAgenda( conn, agenda );
			conn.commit();
			
			return folio;
			
		} finally {
			CloseObject.closeObject( conn );
			
		}
	}

	public boolean actualizaAgenda( Agenda agenda, int idComision ) throws Exception{
		// TODO Auto-generated method stub
		Connection conn = null;
		boolean actualizo = false;
		try {
			conn = getConnection();
			actualizo = AgendaManager.actualizaAgenda( conn, agenda, idComision );
			conn.commit();
			
			return actualizo;
		} finally {
			CloseObject.closeObject( conn );
			
		}
		
	}
	
}
