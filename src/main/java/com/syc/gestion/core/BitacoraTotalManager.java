package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BitacoraTotalManager {
	
	public static BitacoraCaso select(Connection conn, BitacoraCaso bc) throws SQLException {
		BitacoraCaso retVal = null;
		return retVal;
	}
	

	public static int insert(Connection conn, BitacoraCaso bc) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("INSERT INTO cg_bitacora_caso "					
				   + "(id_caso, folio, tipo_caso, status, "
				   + " fecha_inicio, fecha_compromiso, responsable_id, responsable_area, "
				   + " remitente_id, remitente_area, fecha_ultima_operacion, cerrado) " 
				   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmnt.setInt(1, bc.getIdCaso());
			pstmnt.setString(2, bc.getFolio());
			pstmnt.setInt(3, bc.getTipoCaso());
			pstmnt.setInt(4, bc.getStatus());
			pstmnt.setTimestamp(5, bc.getFechaInicio());
			pstmnt.setTimestamp(6, bc.getFechaCompromiso());
			pstmnt.setString(7, bc.getResponsableId());
			pstmnt.setString(8, bc.getResponsableArea());
			pstmnt.setString(9, bc.getRemitenteId());
			pstmnt.setString(10, bc.getRemitenteArea());
			pstmnt.setTimestamp(11, bc.getFechaUltimaOperacion());
			String strCerrado = (bc.isCerrado()) ? "S":"N";
			pstmnt.setString(12, strCerrado);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int update(Connection conn, BitacoraCaso bc) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_bitacora_caso "					
				   + " SET status=?, fecha_ultima_operacion=?, cerrado=? " 
				   + "WHERE id_caso=?");

			pstmnt.setInt(1, bc.getStatus());
			pstmnt.setTimestamp(2, bc.getFechaUltimaOperacion());
			String strCerrado = (bc.isCerrado()) ? "S":"N";
			pstmnt.setString(3, strCerrado);
			pstmnt.setInt(4, bc.getIdCaso());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static BitacoraCaso nuevoBitacoraCaso(Connection conn,
											   	 String remitenteId,
											   	 Caso c) throws SQLException {

		if (c == null)
		throw new NullPointerException("El caso no debe ser nulo");
		
		BitacoraCaso bc = new BitacoraCaso();
		
		bc.setFechaCompromiso(c.getFechaTiempoLimite());
		bc.setFechaInicio(c.getFechaInicio());
		bc.setFechaUltimaOperacion(c.getFechaInicio());
		bc.setFolio(c.getFolio());
		bc.setIdCaso(c.getIdCaso());
		bc.setStatus(c.getStatus());
		bc.setTipoCaso(c.getIdTC());
		
		Empleado eRemitente = new Empleado();
		eRemitente.setClaveUsuario(remitenteId);
		eRemitente = EmpleadoManager.select(conn, eRemitente);
		bc.setRemitenteId(eRemitente.getClaveUsuario());
		bc.setRemitenteArea(eRemitente.getClaveArea());
		
		//A esta altura del proceso no hay responsable
		//bc.setResponsableId(eResponsable.getClaveUsuario());
		//bc.setResponsableArea(eResponsable.getClaveArea());
		
		bc.setCerrado(false);
		
		return bc;
	}
	
}
