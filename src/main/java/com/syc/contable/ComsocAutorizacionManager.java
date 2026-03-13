package com.syc.contable;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ComsocAutorizacionManager {

	private static Logger	log	= Logger.getLogger(ComsocAutorizacionManager.class);

	// ObraPublicaContract opc

	
	
	
	public static byte InsertaComsoc(Connection conn,int intFolioPago, String cTipoPago,String cFolioSai,String cEjercicio, String cCentroContable) throws SQLException {
		byte retVal = -1;

			String query = " insert into tComsocAutorizacion (nFolioPago,cTipoPago,cFolioSai,aEjercicioFiscal,cCentroContable) values (?,?,?,?,?) ";
	
		PreparedStatement ps = null;
		ps = conn.prepareStatement(query);
		ps.setInt(1, intFolioPago);
		ps.setString(2,cTipoPago);
		ps.setString(3, cFolioSai);
		ps.setString(4, cEjercicio);
		ps.setString(5, cCentroContable);
		

		try {
			//ps.executeUpdate();
			ps.execute();
			conn.commit();
			retVal = 0;
		} finally {
			if (ps != null)
				ps.close();

			ps = null;
		}
		return retVal;

	}

	
	

	
	
	
	
	
	
	
}
