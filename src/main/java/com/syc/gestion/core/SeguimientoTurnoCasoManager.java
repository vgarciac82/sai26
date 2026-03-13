package com.syc.gestion.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SeguimientoTurnoCasoManager 
{
	//private static Logger log = Logger.getLogger(CasoRecuperaCaso.class);
	
	public static int insert(Connection conn, SeguimientoTurnoCaso stc_new) throws SQLException {

	int retval = -1;
	PreparedStatement pstmnt = null;

	try {
		
		pstmnt = conn.prepareStatement("INSERT INTO CG_SEGUIMIENTO_TURNADO_CASO ( " +
				"org_id_caso, " +
				"org_id_caso_oper, " +
				"org_id_tc, " +
				"org_id_oper, " +
				"org_co_responsable, " +
				"des_id_caso, " +
				"des_id_caso_oper, " +
				"des_id_tc, " +
				"des_id_oper, " +
				"des_co_responsable, " +
				"des_co_instruccion, " +
				"fecha_registro " +
				")  " +
				"VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		pstmnt.setInt(1, stc_new.getOrgIdCaso());
		pstmnt.setInt(2, stc_new.getOrgIdCasoOper());
		pstmnt.setInt(3, stc_new.getOrgIdTc());
		pstmnt.setInt(4, stc_new.getOrgIdOper());
		pstmnt.setString(5, stc_new.getOrgCoResponsable());
		
		pstmnt.setInt(6, stc_new.getDesIdCaso());
		pstmnt.setInt(7, stc_new.getDesIdCasoOper());
		pstmnt.setInt(8, stc_new.getDesIdTc());
		pstmnt.setInt(9, stc_new.getDesIdOper());
		pstmnt.setString(10, stc_new.getDesCoResponsable());
		pstmnt.setString(11, stc_new.getDesCoInstruccion());
		pstmnt.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
		
		pstmnt.executeUpdate();
		
	} finally {
		if (pstmnt != null)
			pstmnt.close();

		pstmnt = null;
	}

	return retval;
}

	public static int deleteOneByOne(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {
		
		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM CG_SEGUIMIENTO_TURNADO_CASO " +
					"org_id_caso = ? and " +
					"org_id_caso_oper = ? and " +
					"org_id_tc = ? and " +
					"org_id_oper = ? and " + 
					"des_id_caso = ? and " +
					"des_id_caso_oper = ? and " +
					"des_id_tc = ? and " +
					"des_id_oper = ? " );

			pstmnt.setInt(1, stc.getOrgIdCaso());
			pstmnt.setInt(2, stc.getOrgIdCasoOper());
			pstmnt.setInt(3, stc.getOrgIdTc());
			pstmnt.setInt(4, stc.getOrgIdOper());
			
			pstmnt.setInt(6, stc.getDesIdCaso());
			pstmnt.setInt(7, stc.getDesIdCasoOper());
			pstmnt.setInt(8, stc.getDesIdTc());
			pstmnt.setInt(9, stc.getDesIdOper());
				
			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
	
	
	public static int deleteByDesIdCaso(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {
		
		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM CG_SEGUIMIENTO_TURNADO_CASO WHERE " +
					"des_id_caso = ? " );

			pstmnt.setInt(1, stc.getDesIdCaso());
				
			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
	
	public static int deleteByDes(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {
		
		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM CG_SEGUIMIENTO_TURNADO_CASO WHERE" +
					"des_id_caso = ? and " +
					"des_id_caso_oper = ? and " +
					"des_id_tc = ? and " +
					"des_id_oper = ? " );

			pstmnt.setInt(1, stc.getDesIdCaso());
			pstmnt.setInt(2, stc.getDesIdCasoOper());
			pstmnt.setInt(3, stc.getDesIdTc());
			pstmnt.setInt(4, stc.getDesIdOper());
				
			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
		
	public static int deleteByOrgIdCaso(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {
		
		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FORM CG_SEGUIMIENTO_TURNADO_CASO WHERE " +
					"org_id_caso = ? " );

			pstmnt.setInt(1, stc.getOrgIdCaso());
				
			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
	
	public static int deleteByOrg(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {
		
		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM CG_SEGUIMIENTO_TURNADO_CASO WHERE " +
					"org_id_caso = ? and " +
					"org_id_caso_oper = ? and " +
					"org_id_tc = ? and " +
					"org_id_oper = ? " );

			pstmnt.setInt(1, stc.getOrgIdCaso());
			pstmnt.setInt(2, stc.getOrgIdCasoOper());
			pstmnt.setInt(3, stc.getOrgIdTc());
			pstmnt.setInt(4, stc.getOrgIdOper());
				
			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
	
	public static SeguimientoTurnosCasos selectByDes(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {

		PreparedStatement pstmnt = null;
		SeguimientoTurnosCasos v_stcs = new SeguimientoTurnosCasos();
		ResultSet rs=null;
		
		try {
			pstmnt = conn.prepareStatement("SELECT " +
					"org_id_caso, " +
					"org_id_caso_oper, " +
					"org_id_tc, " +
					"org_id_oper, " +
					"org_co_responsable, " +
					"des_id_caso, " +
					"des_id_caso_oper, " +
					"des_id_tc, " +
					"des_id_oper, " +
					"des_co_responsable, " +
					"des_co_instruccion, " +
					"fecha_registro " +
					"FROM CG_SEGUIMIENTO_TURNADO_CASO " +
					"WHERE " +
					"des_id_caso = ? and " +
					"des_id_caso_oper = ? and " +
					"des_id_tc = ? and " +
					"des_id_oper = ? " );

			pstmnt.setInt(1, stc.getDesIdCaso());
			pstmnt.setInt(2, stc.getDesIdCasoOper());
			pstmnt.setInt(3, stc.getDesIdTc());
			pstmnt.setInt(4, stc.getDesIdOper());
			
			rs = pstmnt.executeQuery();
			while (rs.next())
			{
				SeguimientoTurnoCaso stc_a = new SeguimientoTurnoCaso();
				
				stc_a.setOrgIdCaso(rs.getInt(1));
				stc_a.setOrgIdCasoOper(rs.getInt(2));
				stc_a.setOrgIdTc(rs.getInt(3));
				stc_a.setOrgIdOper(rs.getInt(4));
				stc_a.setOrgCoResponsable(rs.getString(5));
				
				stc_a.setDesIdCaso(rs.getInt(6));
				stc_a.setDesIdCasoOper(rs.getInt(7));
				stc_a.setDesIdTc(rs.getInt(8));
				stc_a.setDesIdOper(rs.getInt(9));
				stc_a.setDesCoResponsable(rs.getString(10));
				
				
				
				v_stcs.add(stc_a);
		}
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return v_stcs;
	}
	
	public static SeguimientoTurnosCasos selectByOrg(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {

		SeguimientoTurnosCasos v_stc = new SeguimientoTurnosCasos();
		
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("SELECT " +
					"org_id_caso, " +
					"org_id_caso_oper, " +
					"org_id_tc, " +
					"org_id_oper, " +
					"org_co_responsable, " +
					"des_id_caso, " +
					"des_id_caso_oper, " +
					"des_id_tc, " +
					"des_id_oper, " +
					"des_co_responsable, " +
					"FROM CG_SEGUIMIENTO_TURNADO_CASO " +
					"WHERE " +
					"org_id_caso = ? and " +
					"org_id_caso_oper = ? and " +
					"org_id_tc = ? and " +
					"org_id_oper = ? " );

			pstmnt.setInt(1, stc.getOrgIdCaso());
			pstmnt.setInt(2, stc.getOrgIdCasoOper());
			pstmnt.setInt(3, stc.getOrgIdTc());
			pstmnt.setInt(4, stc.getOrgIdOper());
			
			ResultSet rs=null;
			rs = pstmnt.executeQuery();
			while (rs.next())
			{
				SeguimientoTurnoCaso stc_a = new SeguimientoTurnoCaso();

				stc_a.setOrgIdCaso(rs.getInt(5));
				stc_a.setOrgIdCasoOper(rs.getInt(6));
				stc_a.setOrgIdTc(rs.getInt(7));
				stc_a.setOrgIdOper(rs.getInt(8));
				stc_a.setOrgCoResponsable(rs.getString(11));
				stc_a.setDesIdCaso(rs.getInt(1));
				stc_a.setDesIdCasoOper(rs.getInt(2));
				stc_a.setDesIdTc(rs.getInt(3));
				stc_a.setDesIdOper(rs.getInt(4));
				stc_a.setDesCoResponsable(rs.getString(11));

				v_stc.add(stc_a);
			}
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return v_stc;
	}

	
	public static int selectCountByDes(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		
		try {
			pstmnt = conn.prepareStatement("SELECT " +
					"COUNT(des_id_caso) " +
					"FROM CG_SEGUIMIENTO_TURNADO_CASO " +
					"WHERE " +
					"des_id_caso = ? and " +
					"des_id_caso_oper = ? and " +
					"des_id_tc = ? and " +
					"des_id_oper = ? " );

			pstmnt.setInt(1, stc.getOrgIdCaso());
			pstmnt.setInt(2, stc.getOrgIdCasoOper());
			pstmnt.setInt(3, stc.getOrgIdTc());
			pstmnt.setInt(4, stc.getOrgIdOper());
			
			rs = pstmnt.executeQuery();
			
			if (rs.next())
			{
				retval = rs.getInt(1);
			}
			else
			{
				retval = 0;
			}
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int selectCountByOrg(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("SELECT " +
					"count (org_id_caso) " +
					"FROM CG_SEGUIMIENTO_TURNADO_CASO " +
					"WHERE " +
					"org_id_caso = ? and " +
					"org_id_caso_oper = ? and " +
					"org_id_tc = ? and " +
					"org_id_oper = ? " );

			pstmnt.setInt(1, stc.getOrgIdCaso());
			pstmnt.setInt(2, stc.getOrgIdCasoOper());
			pstmnt.setInt(3, stc.getOrgIdTc());
			pstmnt.setInt(4, stc.getOrgIdOper());
			
			ResultSet rs = null;
			rs = pstmnt.executeQuery();
			
			if (rs.next())
				retval = rs.getInt(1);
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
	
	public static int selectCountByOrgDes(Connection conn, SeguimientoTurnoCaso stc) throws SQLException {

		//SeguimientoTurnosCasos v_stc = new SeguimientoTurnosCasos();
		
		PreparedStatement pstmnt = null;
		int retVal = -1;
		
		try {
			pstmnt = conn.prepareStatement("SELECT " +
					"count(org_id_caso) " +
					"FROM CG_SEGUIMIENTO_TURNADO_CASO " +
					"WHERE " +
					"org_id_caso = ? and " +
					"org_id_caso_oper = ? and " +
					"org_id_tc = ? and " +
					"org_id_oper = ? and " +
					"des_id_caso = ? and " +
					"des_id_caso_oper = ? and " +
					"des_id_tc = ? and " +
					"des_id_oper = ? ");

			pstmnt.setInt(1, stc.getOrgIdCaso());
			pstmnt.setInt(2, stc.getOrgIdCasoOper());
			pstmnt.setInt(3, stc.getOrgIdTc());
			pstmnt.setInt(4, stc.getOrgIdOper());
			
			pstmnt.setInt(5, stc.getDesIdCaso());
			pstmnt.setInt(6, stc.getDesIdCasoOper());
			pstmnt.setInt(7, stc.getDesIdTc());
			pstmnt.setInt(8, stc.getDesIdOper());
			
			ResultSet rs=null;
			rs = pstmnt.executeQuery();
			
			while (rs.next())
			{
				retVal = rs.getInt(1);
			}
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retVal;
	}

	public static int selectCountTotalRows(Connection conn) throws SQLException {

		//SeguimientoTurnosCasos v_stc = new SeguimientoTurnosCasos();
		
		PreparedStatement pstmnt = null;
		int retVal = -1;
		
		try {
			pstmnt = conn.prepareStatement("SELECT " +
					"count(org_id_caso) " +
					"FROM CG_SEGUIMIENTO_TURNADO_CASO ");
	
			ResultSet rs=null;
			rs = pstmnt.executeQuery();
			
			while (rs.next())
			{
				retVal = rs.getInt(1);
			}
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retVal;
	}

	
	public static int update(Connection conn, SeguimientoTurnoCaso stc_new) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			
			pstmnt = conn.prepareStatement("UPDATE CG_SEGUIMIENTO_TURNADO_CASO SET " +
					//"org_co_responsable = ? , " +
					"des_co_responsable = ?, " +
					"des_co_instruccion = ? " +
					"WHERE "+
					//"org_id_caso = ? and " +
					//"org_id_caso_oper = ? and " +
					//"org_id_tc = ? and " +
					//"org_id_oper = ? " +
					"des_id_caso = ? and " +
					"des_id_caso_oper = ? and " +
					"des_id_tc = ? and " +
					"des_id_oper = ? ");

			pstmnt.setString(1, stc_new.getDesCoResponsable());
			pstmnt.setString(2, stc_new.getDesCoInstruccion());
			
			pstmnt.setInt(5, stc_new.getDesIdCaso());
			pstmnt.setInt(6, stc_new.getDesIdCasoOper());
			pstmnt.setInt(7, stc_new.getDesIdTc());
			pstmnt.setInt(8, stc_new.getDesIdOper());
			
			pstmnt.executeUpdate();
			
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	
}
