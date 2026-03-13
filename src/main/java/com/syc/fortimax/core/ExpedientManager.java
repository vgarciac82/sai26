package com.syc.fortimax.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.syc.sai.contabilidad.utils.db.CloseObject;


public class ExpedientManager {

	public static List<ExpedientNode> getExpedientNodes( Connection conn, String tituloAplicacion, int idGabinete ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT  '/' + dbo.fn_Path_Carpeta(docto.TITULO_APLICACION, docto.ID_GABINETE, docto.ID_CARPETA_PADRE) + '/' + docto.NOMBRE_DOCUMENTO AS documento, " );
		query.append( "        docto.TITULO_APLICACION, " );
		query.append( "        docto.ID_GABINETE, " );
		query.append( "        docto.ID_CARPETA_PADRE, " );
		query.append( "        docto.ID_DOCUMENTO, " );
		query.append( "        docto.NOMBRE_DOCUMENTO, " );
		query.append( "        docto.TITULO_APLICACION + '_' + 'G' + CONVERT( VARCHAR(32), docto.ID_GABINETE ) + 'C' + CONVERT( VARCHAR(32), docto.ID_CARPETA_PADRE ) + 'D' +  CONVERT( VARCHAR(32), docto.ID_DOCUMENTO ) AS fortimax " );
		query.append( "  FROM  IMX_DOCUMENTO docto " );
		query.append( "        INNER JOIN  " );
		query.append( "        IMX_PAGINA pagina " );
		query.append( "        ON  " );
		query.append( "        docto.TITULO_APLICACION = pagina.TITULO_APLICACION " );
		query.append( "        AND docto.ID_GABINETE = pagina.ID_GABINETE " );
		query.append( "        AND docto.ID_CARPETA_PADRE = pagina.ID_CARPETA_PADRE " );
		query.append( "        AND docto.ID_DOCUMENTO = pagina.ID_DOCUMENTO " );
		query.append( " WHERE  docto.ID_CARPETA_PADRE <> 0 " );
		query.append( "   AND  docto.TITULO_APLICACION = ? " );
		query.append( "   AND  docto.ID_GABINETE = ? " );
		query.append( "ORDER BY docto.TITULO_APLICACION, docto.ID_GABINETE, docto.ID_CARPETA_PADRE, docto.ID_DOCUMENTO " );

		List<ExpedientNode> nodes = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tituloAplicacion );
			ps.setInt( 2, idGabinete );

			rs = ps.executeQuery();
			while ( rs.next() ) {
				if ( nodes == null )
					nodes = new ArrayList<ExpedientNode>();
				
				ExpedientNode node = new ExpedientNode();
				
				node.setIdCarpetaPadre( rs.getInt( "ID_CARPETA_PADRE" ) );
				node.setIdDocumento( rs.getInt( "ID_DOCUMENTO" ) );
				node.setIdGabinete( rs.getInt( "ID_GABINETE" ) );
				node.setPath( rs.getString( "documento" ) );
				node.setTituloAplicacion( rs.getString( "TITULO_APLICACION" ) );
				node.setNombreDocumento( rs.getString( "NOMBRE_DOCUMENTO" ) );
				node.setFortimax(   rs.getString( "fortimax" ) );
				nodes.add( node );
			}
			
			return nodes;
			
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

}
