package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoCasoBusquedaManager {

	public static String[] getCamposNombres(Connection conn, int id_tc) throws SQLException {

		List campos = new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT campo_nombre FROM cg_tipo_caso_busqueda WHERE id_tc = ? ORDER BY id_tcb");

			pstmnt.setInt(1, id_tc);

			rs = pstmnt.executeQuery();
			while (rs.next())
				campos.add(rs.getString(1));
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		/*String [] retVals = new String [campos.size()];
		for (int iIdx=0;iIdx<=campos.size();iIdx++)
		{
			retVals[iIdx]= campos.get(iIdx).toString();	
		}			
		return retVals;*/

		return (String[]) campos.toArray(new String[campos.size()]);

	}
}
