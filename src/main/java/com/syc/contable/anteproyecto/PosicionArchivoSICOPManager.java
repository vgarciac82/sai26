package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.syc.sai.contabilidad.utils.db.CloseObject;

public class PosicionArchivoSICOPManager {

	public static List<PosicionArchivoSICOP> cargaMomentos(Connection conn) throws Exception {

		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQuery = " select momento_sicop, cuentas_sai, col_inicio, posiciones from t_posiciones_archivo_sicop with(nolock) order by col_inicio";
		List<PosicionArchivoSICOP> resultado = new ArrayList<PosicionArchivoSICOP>();
		try {
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				resultado.add(new PosicionArchivoSICOP(rs.getString("momento_sicop"), rs.getString("cuentas_sai"), rs.getInt("col_inicio"), rs.getInt("posiciones")));
			}
			if( resultado.size() == 0 )
				throw new Exception("No se ha definido información en la tabla de posiciones del archivo sicop");
			return resultado;
		} finally {
		CloseObject.closeObject(rs, false);
		CloseObject.closeObject(pstm, false);
		
		}

	}
}
