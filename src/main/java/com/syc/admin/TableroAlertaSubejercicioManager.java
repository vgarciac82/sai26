package com.syc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.sai.contabilidad.utils.db.CloseObject;

/**
 * Manager para el tablero de alertas Subejercicio.
 */
public class TableroAlertaSubejercicioManager {
	private static final Logger	log	= Logger.getLogger(TableroAlertaSubejercicioManager.class);

	public static List<Map<String, String>> obtenDatos(Connection conn) throws Exception {
		log.trace("Iniciando consulta a saldos en subejercicio");
		String query = "select * from vsaldo_programa_disponible_mofificado_un";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map<String, String>> r = new ArrayList<Map<String, String>>();

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Map<String, String> renglon = new HashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					renglon.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
				}
				r.add(renglon);
			}

		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}
		return r;
	}

}
