package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.syc.cfdi.db.CloseObject;
import com.syc.contable.core.MovimientosPresupuestalesManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas SA de CV
 *         desarrollo gestion_conagua_sif México D.F. 09/07/2012
 * 
 */
public class MovimientosPresupuestalesBussinessLogic extends DataSourceManager{
	public List<StringBuffer> listaMovimientos = new ArrayList<StringBuffer>();

	public MovimientosPresupuestalesBussinessLogic(String jniName) {
		super.init(jniName);
	}
	
	public List<StringBuffer> filtraMovimientos(String ep, String tipoCuenta, String strDesde, String strHasta) throws Exception{
		Connection conn=null;

		try{
			conn = getConnection();
			listaMovimientos = MovimientosPresupuestalesManager.BuscaMovimientos(conn, ep, tipoCuenta, strDesde, strHasta);
			
		}
		catch (SQLException e) {
			if(conn != null){
				conn.rollback();
				throw  new GestionException(e.getMessage());
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			CloseObject.closeObject( conn );
		}
		return listaMovimientos;
	}	
}
