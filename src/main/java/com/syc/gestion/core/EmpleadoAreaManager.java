package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpleadoAreaManager {

	public static int delete(Connection conn, String idArea) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_cat_areas " +
										   "WHERE id_area = ?");

			pstmnt.setString(1, idArea);

			retval = pstmnt.executeUpdate();
			//GAF 2010-04-16
			//En los manager no debe haber commits!
			//solamente en los businesslogic o servlets
			//conn.commit();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int insert(Connection conn, EmpleadoArea ea) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn
				.prepareStatement("INSERT INTO cg_cat_areas " +
								  "(ID_AREA, D_DESCRIPCION, TIPO_AREA, PREFIJO_FOLIO, ID_AREA_PADRE) " +
								   "VALUES (?, ?, ?, ?, ?)");

			pstmnt.setString(1, ea.getId());
			pstmnt.setString(2, ea.getDescripcion());
			pstmnt.setString(3, Integer.toString(ea.getTipoArea()));
			pstmnt.setString(4, ea.getPrefijoFolio());
			pstmnt.setString(5, ea.getAreaPadre());
			

			retval = pstmnt.executeUpdate();			
			//GAF 2010-04-16
			//En los manager no debe haber commits!
			//solamente en los businesslogic o servlets
			//conn.commit();
			
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static EmpleadoArea select(Connection conn, EmpleadoArea r) throws SQLException {
		
		EmpleadoArea retVal = null;

		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (r.getId() != null) {
				where.append(token + "ID_AREA = ?");
				token = " AND ";
			}

			if (r.getDescripcion() != null) {
				where.append(token + "D_DESCRIPCION = ?");
				token = " AND ";
			}

			if (r.getTipoArea() > -1) {
				where.append(token + "TIPO_AREA = ?");
				token = " AND ";
			}

			if (r.getPrefijoFolio() != null) {
				where.append(token + "PREFIJO_FOLIO = ?");
				token = " AND ";
			}
			
			if (r.getAreaPadre() != null) {
				where.append(token + "ID_AREA_PADRE = ?");
				token = " AND ";
			}
			
			String query = "SELECT * FROM cg_cat_areas " + where.toString();
			//System.out.println("QUERY=["+query+"],PARAMETER r.getId()=["+r.getId()+"]");
			pstmnt = conn.prepareStatement(query);

			int i = 1;
			if (r.getId() != null)
				pstmnt.setString(i++, r.getId());

			if (r.getDescripcion() != null)
				pstmnt.setString(i++, r.getDescripcion());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				retVal = new EmpleadoArea();

				retVal.setId(rs.getString("ID_AREA"));
				retVal.setDescripcion(rs.getString("D_DESCRIPCION"));
				retVal.setTipoArea(Integer.parseInt(rs.getString("TIPO_AREA")));
				retVal.setPrefijoFolio(rs.getString("PREFIJO_FOLIO"));
				retVal.setAreaPadre(rs.getString("ID_AREA_PADRE"));
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

	public static int update(Connection conn, EmpleadoArea r) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = 
				conn.prepareStatement("UPDATE cg_cat_areas "  +
									  "SET D_DESCRIPCION = ?, " +
									  "TIPO_AREA = ?, " +
									  "PREFIJO_FOLIO = ?, " +
									  "ID_AREA_PADRE = ?, " +
									  "BANDEJA_COMPARTIDA_IN = ?, " +
									  "BANDEJA_COMPARTIDA_OUT = ? " +
									  "WHERE ID_AREA = ?");
			
			pstmnt.setString(1, r.getDescripcion());
			pstmnt.setString(2, Integer.toString(r.getTipoArea()));
			pstmnt.setString(3, r.getPrefijoFolio());
			pstmnt.setString(4, r.getAreaPadre());
			pstmnt.setString(5, (r.isBandejaEntradaCompartida()?"S":"N"));
			pstmnt.setString(6, (r.isBandejaSalidaCompartida()?"S":"N"));
			pstmnt.setString(7, r.getId());

			retval = pstmnt.executeUpdate();
			//GAF 2010-04-16
			//En los manager no debe haber commits!
			//solamente en los businesslogic o servlets
			//conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
}
