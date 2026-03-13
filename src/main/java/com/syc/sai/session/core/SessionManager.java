package com.syc.sai.session.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.session.SessionBussinesLogic;
import com.syc.sai.session.SessionBussinesLogic.OperacionAtrapada;

public class SessionManager {

	public static synchronized int liberaCasosBatch(Connection conn, List<OperacionAtrapada> o) throws Exception {

		String query = 	  " UPDATE dbo.cg_caso_operacion WITH(rowlock, updlock) " 
						+ " SET    co_responsable = ? " 
						+ " WHERE  id_caso = ? "
						+ "        AND id_caso_oper = ?  "
						+ "        AND id_oper = ? ";
		PreparedStatement ps = null;
		
		
		try {
			
			ps = conn.prepareStatement(query);
			
			for( Iterator<OperacionAtrapada> i = o.iterator(); i.hasNext(); ){
				
				OperacionAtrapada oper = i.next();
				
				ps.setString(1, oper.getoResponsable() );
				ps.setInt(2, oper.getIdCaso());
				ps.setInt(3, oper.getIdCasoOper() );
				ps.setInt(4, oper.getIdOper() );
				
				ps.addBatch();
			}
			
			ps.executeBatch();
			
			return 1;
			
		} finally {
			CloseObject.closeObject(ps);
		}
	}

	public static synchronized int liberaCasos(Connection conn, Usuario u) throws Exception {
		int afectados = 0;
		String queryUpdateNombre = " UPDATE caso_operacion WITH(ROWLOCK) " + " SET    caso_operacion.co_responsable = operacion.o_responsable " + " FROM   cg_caso_operacion caso_operacion WITH(ROWLOCK)  " + "        INNER JOIN cg_operacion operacion WITH(ROWLOCK)  "
			+ "                ON caso_operacion.id_tc = operacion.id_tc  " + "                   AND caso_operacion.id_oper = operacion.o_numero " + " WHERE  CO_RESPONSABLE = ? ";

		PreparedStatement psLiberaPorNombre = null;
		try {

			psLiberaPorNombre = conn.prepareStatement(queryUpdateNombre);

			psLiberaPorNombre.setString(1, u.getNombre());
			afectados += psLiberaPorNombre.executeUpdate();

			psLiberaPorNombre.setString(1, u.getLogin());
			afectados += psLiberaPorNombre.executeUpdate();

			return afectados;
		} finally {
			CloseObject.closeObject(psLiberaPorNombre, false);
		}

	}

	public static synchronized int liberaCasosMasivo(Connection conn, Usuario u) throws Exception {
		int afectados = 0;
		String queryUpdateNombre = " UPDATE caso_operacion WITH(ROWLOCK) " + " SET    caso_operacion.co_responsable = operacion.o_responsable " + " FROM   cg_caso_operacion caso_operacion WITH(ROWLOCK)  " + "        INNER JOIN cg_operacion operacion WITH(ROWLOCK)  "
			+ "                ON caso_operacion.id_tc = operacion.id_tc  " + "                  AND caso_operacion.id_oper = operacion.o_numero " + " WHERE  CO_RESPONSABLE = ? ";

		PreparedStatement psLiberaPorNombre = null;
		try {

			psLiberaPorNombre = conn.prepareStatement(queryUpdateNombre);

			psLiberaPorNombre.setString(1, u.getNombre());
			afectados += psLiberaPorNombre.executeUpdate();

			psLiberaPorNombre.setString(1, u.getLogin());
			afectados += psLiberaPorNombre.executeUpdate();

			return afectados;
		} finally {
			CloseObject.closeObject(psLiberaPorNombre, false);
		}

	}

	public static synchronized List<OperacionAtrapada> listaOperacionesAtrapadas(Connection conn, Usuario u) throws Exception {
		List<OperacionAtrapada> operaciones = new ArrayList<OperacionAtrapada>();

		ResultSet rs = null;
		PreparedStatement ps = null;
		String query =    "SELECT casoOperacion.id_tc, " 
			+ "       casoOperacion.id_caso,  "
			+ "       casoOperacion.id_caso_oper, " 
			+ "       casoOperacion.id_oper,  "
			+ "       casoOperacion.co_responsable, " 
			+ "       operacion.o_responsable " 
			+ "FROM   cg_caso_operacion casoOperacion WITH(nolock) " 
			+ "       INNER JOIN cg_operacion operacion WITH(nolock)  "
			+ "               ON casoOperacion.id_tc = operacion.id_tc " 
			+ "                  AND casoOperacion.id_oper = operacion.id_oper " 
			+ "WHERE  co_responsable = ? ";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, u.getNombre());

			rs = ps.executeQuery();
			while (rs.next())
				operaciones.add(new SessionBussinesLogic.OperacionAtrapada(rs.getInt("id_caso"), rs.getInt("id_caso_oper"), rs.getInt("id_oper"), rs.getString("co_responsable"), rs.getString("o_responsable")));

			return operaciones;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}

	}
}