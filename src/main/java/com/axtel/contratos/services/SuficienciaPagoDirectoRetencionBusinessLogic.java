package com.axtel.contratos.services;


import java.sql.Connection;
import java.util.List;

import com.axtel.contratos.core.SuficienciaPagoDirectoRetencion;
import com.axtel.contratos.repositories.SuficienciaPagoDirectoRetencionManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;


public class SuficienciaPagoDirectoRetencionBusinessLogic extends DataSourceManager {

	public SuficienciaPagoDirectoRetencionBusinessLogic( String jndiName ) {
		super.init( jndiName );
	}

	public SuficienciaPagoDirectoRetencion insert( SuficienciaPagoDirectoRetencion bean ) {
		Connection con = null;
		try {
			con = getConnection();
			SuficienciaPagoDirectoRetencionManager.insert( con, bean );
			con.commit();
			return bean;
		} catch ( Exception ex ) {
			Util.rollback( con );
			throw new RuntimeException( "Error al insertar Retención", ex );
		} finally {
			CloseObject.closeObject( con );
		}
	}

	public SuficienciaPagoDirectoRetencion update( SuficienciaPagoDirectoRetencion bean ) {
		Connection con = null;
		try {
			con = getConnection();
			SuficienciaPagoDirectoRetencionManager.update( con, bean );
			con.commit();
			return bean;
		} catch ( Exception ex ) {
			Util.rollback( con );
			throw new RuntimeException( "Error al actualizar Retención", ex );
		} finally {
			CloseObject.closeObject( con );
		}
	}

	public void deleteByFolio( int folio, int idRetencion ) {
		Connection con = null;
		try {
			con = getConnection();
			SuficienciaPagoDirectoRetencionManager.deleteByFolio( con, folio, idRetencion );
			con.commit();
		} catch ( Exception ex ) {
			Util.rollback( con );
			throw new RuntimeException( "Error al eliminar Retenciones", ex );
		} finally {
			CloseObject.closeObject( con );
		}
	}

	public List<SuficienciaPagoDirectoRetencion> findByFolio( int folio ) {
		Connection con = null;
		try {
			con = getConnection();
			return SuficienciaPagoDirectoRetencionManager.findByFolio( con, folio );
		} catch ( Exception ex ) {
			throw new RuntimeException( "Error al buscar Retenciones", ex );
		} finally {
			CloseObject.closeObject( con );
		}
	}
}
