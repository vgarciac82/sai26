package com.syc.sai.procesosAutomaticos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ProcesoAdjuntaManager {

	public static int insertaProceso(Connection conn, ProcesoAdjunta proceso) throws Exception {

		String query = "INSERT INTO tProcesoAdjuntaCLC( cIDProceso, dFechaProceso, cULogin, cUnidadEjecutora, nIDEstatus, cResultadoProceso ) VALUES ( ?, ?, ?, ?, ?, ? )";
		PreparedStatement ps = null;
		int insertados = 0;

		try {

			ps = conn.prepareStatement(query);

			ps.setString(1, proceso.getIdProceso());
			ps.setTimestamp(2, new Timestamp(proceso.getFechaProceso().getTime()));
			ps.setString(3, proceso.getuLogin());
			ps.setString(4, proceso.getUnidadEjecutora());
			ps.setInt(5, proceso.getIdEstatus());
			ps.setString(6, proceso.getResultadoProceso());

			insertados = ps.executeUpdate();

			return insertados;

		} finally {
			CloseObject.closeObject(ps);
		}

	}

	public static void actualizaResultado(Connection conn, ProcesoAdjunta pa) throws Exception {
		String query = "UPDATE tProcesoAdjuntaCLC SET nIDEstatus = ?, cResultadoProceso = ? WHERE cIDProceso = ?";
		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement(query);

			ps.setInt(1, pa.getIdEstatus());
			ps.setString(2, pa.getResultadoProceso());
			ps.setString(3, pa.getIdProceso());

			ps.executeUpdate();
		} finally {
			CloseObject.closeObject(ps);
		}

	}
}
