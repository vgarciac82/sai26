package com.syc.comprobacionlaudos;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.syc.contable.core.ComprobacionLaudos;
import com.syc.gestion.core.Caso;

public class ComprobacionLaudosManager {
	
	public static int insertaInformacionLaudos(Connection conn, ComprobacionLaudos comprobacion ) throws Exception{
		String query = "INSERT INTO tComprobacionLaudos "
                     +  "         ( nFolioRELACIONGASTOS , "
                     +  "           nFolioCaja , "
                     +  "           mImporteCaja , "
                     +  "           mImporteRet , "
                     +  "           mImporteNeto , "
                     +  "           cRetSICOP , "
                     +  "           cEsDevengado , "
                     +  "           cEsLiquidacion "
                     +  "          ) "
                     +  "           VALUES  ( ?, "
                     +  "                     ?, "
                     +  "                     ?, "
                     +  "                     ?, "
                     +  "                     ?, "
                     +  "                     ?, "
                     +  "                     ?, "
                     +  "                     ?)";
		
		PreparedStatement ps = null;
		
		try{
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, comprobacion.getFolioRelacionGastos());
			ps.setInt(2, comprobacion.getFolioCaja());
			ps.setDouble(3, comprobacion.getImporteCaja());
			ps.setDouble(4, comprobacion.getImporteRetencion());
			ps.setDouble(5, comprobacion.getImporteNeto());
			ps.setString(6, comprobacion.getEsRetencionSicop());
			ps.setString(7, comprobacion.getEsDevengado() );
			ps.setString(8, comprobacion.getEsLiquidacion() );
			
			int afectados = ps.executeUpdate();
			
			if( afectados <= 0 )
				throw new Exception("No se inserto detalle de comprobacion de laudos");
			
			return afectados;
				
		}finally {
			
		}

	}
	/*public static boolean avanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean success = false;
		
		switch(id_caso_oper){
			
		}
		
		
		String query= "UPDATE taltaproveedor SET cDocumentoHaplicado = ? WHERE cFolio = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, status);
			ps.setString(2, folioSAI);

			success = ps.executeUpdate() > 0;

			return success;
		} finally {
			try {
				CloseObject.closeObject(rs, false);
				CloseObject.closeObject(ps, false);
			} catch (Exception e) {
			}
		}
	}*/

	public static void avanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) {
		switch (id_caso_oper) {
			case 1:
				//Inserta Datos
				
				break;
		}

	}
}
