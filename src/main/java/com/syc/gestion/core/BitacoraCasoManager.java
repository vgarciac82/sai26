package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BitacoraCasoManager {

	public static BitacoraCaso select(Connection conn, BitacoraCaso bc) throws SQLException {

		BitacoraCaso retVal = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (bc.getIdCaso() > 0) {
				where.append(token + "id_caso = ?");
				token = " AND ";
			}

			if (bc.getFolio() != null) {
				where.append(token + "folio = ?");
				token = " AND ";
			}

			if (bc.getTipoCaso() > 0) {
				where.append(token + "tipo_caso = ?");
				token = " AND ";
			}

			if (bc.getStatus() > 0) {
				where.append(token + "status = ?");
				token = " AND ";
			}

			if (bc.getFechaInicio() != null) {
				where.append(token + "fecha_inicio = ?");
				token = " AND ";
			}

			if (bc.getFechaCompromiso() != null) {
				where.append(token + "fecha_compromiso = ?");
				token = " AND ";
			}

			if (bc.getFechaUltimaOperacion() != null) {
				where.append(token + "fecha_ultima_operacion = ?");
				token = " AND ";
			}

			if (bc.getResponsableId() != null) {
				where.append(token + "responsable_id = ?");
				token = " AND ";
			}

			if (bc.getResponsableArea() != null) {
				where.append(token + "responsable_area = ?");
				token = " AND ";
			}

			if (bc.getRemitenteId() != null) {
				where.append(token + "remitente_id = ?");
				token = " AND ";
			}

			if (bc.getRemitenteArea() != null) {
				where.append(token + "remitente_area = ?");
				token = " AND ";
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_bitacora_caso with(nolock) " + where.toString());

			int i = 1;
			if (bc.getIdCaso() > 0)
				pstmnt.setInt(i++, bc.getIdCaso());

			if (bc.getFolio() != null)
				pstmnt.setString(i++, bc.getFolio());

			if (bc.getTipoCaso() > 0)
				pstmnt.setInt(i++, bc.getTipoCaso());

			if (bc.getStatus() > 0)
				pstmnt.setInt(i++, bc.getStatus());

			if (bc.getFechaInicio() != null)
				pstmnt.setTimestamp(i++, bc.getFechaInicio());

			if (bc.getFechaCompromiso() != null)
				pstmnt.setTimestamp(i++, bc.getFechaCompromiso());

			if (bc.getFechaUltimaOperacion() != null)
				pstmnt.setTimestamp(i++, bc.getFechaUltimaOperacion());

			if (bc.getResponsableId() != null)
				pstmnt.setString(i++, bc.getResponsableId());

			if (bc.getResponsableArea() != null)
				pstmnt.setString(i++, bc.getResponsableArea());

			if (bc.getRemitenteId() != null)
				pstmnt.setString(i++, bc.getRemitenteId());

			if (bc.getRemitenteArea() != null)
				pstmnt.setString(i++, bc.getRemitenteArea());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				retVal = new BitacoraCaso();

				retVal.setIdCaso(rs.getInt("id_caso"));
				retVal.setFolio(rs.getString("folio"));
				retVal.setTipoCaso(rs.getInt("tipo_caso"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setFechaInicio(rs.getTimestamp("fecha_inicio"));
				retVal.setFechaCompromiso(rs.getTimestamp("fecha_compromiso"));
				retVal.setFechaUltimaOperacion(rs.getTimestamp("fecha_ultima_operacion"));

				retVal.setResponsableId(rs.getString("responsable_id"));
				retVal.setResponsableArea(rs.getString("responsable_area"));
				retVal.setRemitenteId(rs.getString("remitente_id"));
				retVal.setRemitenteArea(rs.getString("remitente_area"));
				boolean isCerrado = false;
				try {
					isCerrado = rs.getString("cerrado").toUpperCase().equals("S");
				} catch (Exception e) {
					//ignore
				}
				retVal.setCerrado(isCerrado);

				retVal.setIdGabinete(rs.getInt("id_gabinete"));
				retVal.setTituloAplicacion(rs.getString("titulo_aplicacion"));

			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return retVal;
	}

	public static int insert(Connection conn, BitacoraCaso bc) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("INSERT INTO cg_bitacora_caso (id_caso, folio, tipo_caso, status, "
					+ " fecha_inicio, fecha_compromiso, responsable_id, responsable_area, "
					+ " remitente_id, remitente_area, fecha_ultima_operacion, cerrado, id_gabinete, titulo_aplicacion) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

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
			String strCerrado = (bc.isCerrado()) ? "S" : "N";
			pstmnt.setString(12, strCerrado);
			pstmnt.setInt(13, bc.getIdGabinete());
			pstmnt.setString(14, bc.getTituloAplicacion());

			retval = pstmnt.executeUpdate();

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();

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
			pstmnt = conn.prepareStatement("UPDATE cg_bitacora_caso " +
										   " SET status = ?, fecha_ultima_operacion = ?, cerrado = ?, " +
										   " remitente_id = ?, remitente_area = ?, " +
										   " responsable_id = ?, responsable_area = ? " +
										   " WHERE id_caso = ?");

			pstmnt.setInt	   (1, bc.getStatus());
			pstmnt.setTimestamp(2, bc.getFechaUltimaOperacion());
			String strCerrado = (bc.isCerrado()) ? "S" : "N";
			pstmnt.setString   (3, strCerrado);
			pstmnt.setString   (4, bc.getRemitenteId());
			pstmnt.setString   (5, bc.getRemitenteArea());
			pstmnt.setString   (6, bc.getResponsableId());
			pstmnt.setString   (7, bc.getResponsableArea());
			pstmnt.setInt	   (8, bc.getIdCaso());

			retval = pstmnt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();

		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	//  Actualiza CG_BITACORA_CASO y CG_BITACORA_OPERACION
	public static int updateFolio(Connection conn, String oldFolio, String newFolio) throws SQLException {

		int retval = 0;

		PreparedStatement pstmntCaso = null, pstmntOper = null;

		try {
			pstmntCaso = conn.prepareStatement("UPDATE cg_bitacora_caso SET folio = ? WHERE folio = ?");

			pstmntCaso.setString(1, newFolio);
			pstmntCaso.setString(2, oldFolio);

			retval += pstmntCaso.executeUpdate();

			pstmntOper = conn.prepareStatement("UPDATE cg_bitacora_operacion SET folio = ? WHERE folio = ?");

			pstmntOper.setString(1, newFolio);
			pstmntOper.setString(2, oldFolio);

			retval += pstmntOper.executeUpdate();
		} finally {
			if (pstmntCaso != null)
				pstmntCaso.close();

			if (pstmntOper != null)
				pstmntOper.close();

			pstmntCaso = null;
			pstmntOper = null;
		}

		return retval;
	}

	public static int updateUser(Connection conn, String oldUser, String newUser) throws SQLException {

		int retval = -1;

		PreparedStatement pstmnt = null;
		//Actualiza el remitente
		try {
			pstmnt = conn.prepareStatement("UPDATE cg_bitacora_caso SET remitente_id = ? WHERE remitente_id = ?");

			pstmnt.setString(1, newUser);
			pstmnt.setString(2, oldUser);

			retval = pstmnt.executeUpdate();

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();

		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}
		//Actualiza el responsable
		try {
			pstmnt = conn.prepareStatement("UPDATE cg_bitacora_caso SET responsable_id = ? WHERE responsable_id = ?");

			pstmnt.setString(1, newUser);
			pstmnt.setString(2, oldUser);

			retval = pstmnt.executeUpdate();

		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int updateGabinete(Connection conn, int idCaso, int idGabinete) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_bitacora_caso SET id_gabinete = ? WHERE id_caso = ?");

			pstmnt.setInt(1, idGabinete);
			pstmnt.setInt(2, idCaso);

			retval = pstmnt.executeUpdate();

			//aqui actualiza la operacion (esto no debe ser necesario)
			pstmnt = conn.prepareStatement("UPDATE cg_bitacora_operacion SET id_gabinete = ? WHERE id_caso = ?");

			pstmnt.setInt(1, idGabinete);
			pstmnt.setInt(2, idCaso);

			retval = pstmnt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();

		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static BitacoraCaso nuevoBitacoraCaso(Connection conn, String remitenteId, Caso c) throws SQLException {

		if (c == null)
			throw new NullPointerException("El caso no debe ser nulo");

		BitacoraCaso bc = new BitacoraCaso();

		//bc.setFechaCompromiso(c.getFechaCompromiso());
		bc.setFechaInicio(c.getFechaInicio());
		bc.setFechaUltimaOperacion(c.getFechaInicio());
		bc.setFolio(c.getFolio());
		bc.setIdCaso(c.getIdCaso());
		bc.setStatus(c.getStatus());
		bc.setTipoCaso(c.getIdTC());

		bc.setIdGabinete(c.getIdGabinete());
		bc.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());

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
