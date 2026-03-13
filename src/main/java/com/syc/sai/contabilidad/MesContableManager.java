package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.AccountingEngineException;

public class MesContableManager {

	public static Logger	log	= Logger.getLogger(MesContableManager.class);

	public static int cierraMesContable(Connection conn, MesContable m) throws AccountingEngineException {
		m.setMesAbierto("N");
		return updateMesContable(conn, m);
	}

	public static List<MesContable> buscaMesContable(Connection conn, MesContable m) throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		ResultSet rs = null;
		List<MesContable> result = new ArrayList<MesContable>();
		String queryBase = "SELECT * FROM tmesescontables WITH(nolock) ";
		String qry;
		String where = "";
		String token = " WHERE ";

		try {
			if (m.getnMes() > 0) {
				where += token + " nMes = ? ";
				token = " AND ";
			}
			if (m.getcCentroContable() != null && !"".equals(m.getcCentroContable())) {
				where += token + " cCentroContable = ? ";
				token = " AND ";
			}
			if (m.getaEjercicioFiscal() > 0) {
				where += token + " aEjercicioFiscal = ? ";
				token = " AND ";
			}
			if (m.getMesAbierto() != null && !"".equals(m.getMesAbierto())) {
				where += token + " mesAbierto = ? ";
				token = " AND ";
			}
			if (m.getfCierre() != null) {
				where += token + " fcierre = ? ";
				token = " AND ";
			}
			if (m.getcUnidadResponsable() != null && !"".equals(m.getcUnidadResponsable())) {
				where += token + " cUnidadResponsable = ? ";
				token = " AND ";
			}
			if (m.getUsuarioCerro() != null && !"".equals(m.getUsuarioCerro())) {
				where += token + " usuarioCerro = ?";
			}

			qry = queryBase + where;
			log.info(qry);
			pStmnt = conn.prepareStatement(qry);
			int cnt = 1;
			if (m.getnMes() > 0) {
				pStmnt.setInt(cnt++, m.getnMes());
				log.info(m.getnMes());
			}
			if (m.getcCentroContable() != null && !"".equals(m.getcCentroContable())) {
				pStmnt.setString(cnt++, m.getcCentroContable());
				log.info(m.getcCentroContable());
			}
			if (m.getaEjercicioFiscal() > 0) {
				pStmnt.setInt(cnt++, m.getaEjercicioFiscal());
				log.info(m.getaEjercicioFiscal());
			}
			if (m.getMesAbierto() != null && !"".equals(m.getMesAbierto())) {
				pStmnt.setString(cnt++, m.getMesAbierto());
				log.info(m.getMesAbierto());
			}
			if (m.getfCierre() != null) {
				pStmnt.setDate(cnt++, new Date(m.getfCierre().getTime()));
				log.info(new Date(m.getfCierre().getTime()));
			}
			if (m.getfCierre() != null) {
				pStmnt.setDate(cnt++, new Date(m.getfCierre().getTime()));
				log.info(new Date(m.getfCierre().getTime()));
			}
			if (m.getcUnidadResponsable() != null && !"".equals(m.getcUnidadResponsable())) {
				pStmnt.setString(cnt++, m.getcUnidadResponsable());
				log.info(m.getcUnidadResponsable());
			}
			if (m.getUsuarioCerro() != null && !"".equals(m.getUsuarioCerro())) {
				pStmnt.setString(cnt++, m.getUsuarioCerro());
				log.info(m.getUsuarioCerro());
			}
			rs = pStmnt.executeQuery();
			while (rs.next()) {
				MesContable mAux = new MesContable();

				mAux.setnMes(rs.getInt("nMes"));
				mAux.setcCentroContable(rs.getString("cCentroContable"));
				mAux.setaEjercicioFiscal(rs.getInt("aEjercicioFiscal"));
				mAux.setMesAbierto(rs.getString("mesAbierto"));
				mAux.setfCierre(rs.getDate("fcierre"));
				mAux.setcUnidadResponsable(rs.getString("cUnidadResponsable"));
				mAux.setUsuarioCerro(rs.getString("usuarioCerro"));

				result.add(mAux);
			}
			return result;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null) {
				try {
					pStmnt.close();
				} catch (Exception e) {
					log.warn("Problemas cerrando PreparedStatemnt " + e.toString(), e);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					log.warn("Problemas cerrando PreparedStatemnt " + e.toString(), e);
				}
			}
		}

	}

	public static int updateMesContable(Connection conn, MesContable m) throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		String querySet = "UPDATE tMesesContables SET ";
		String queryWhere = "WHERE nMes = ? " + (m.getcCentroContable() != null && !"".equals(m.getcCentroContable()) ? " AND cCentroContable = ? " : "") + " AND aEjercicioFiscal = ?" + (m.getcUnidadResponsable() != null && !"".equals(m.getcUnidadResponsable()) ? " AND cUnidadResponsable = ? " : ""); 
		String tokenSet = "";
		String qry = "";
		try {
			if (m.getMesAbierto() != null) {
				querySet += tokenSet + " mesAbierto = ? ";
				tokenSet = ",";
			}
			if (m.getfCierre() != null) {
				querySet += tokenSet + " fcierre = ? ";
				tokenSet = ",";
			}
			if (m.getUsuarioCerro() != null) {
				querySet += tokenSet + " usuarioCerro = ? ";
			}

			qry = querySet + queryWhere;
			pStmnt = conn.prepareStatement(qry);
			int cnt = 1;

			if (m.getMesAbierto() != null) {
				pStmnt.setString(cnt++, m.getMesAbierto());
			}
			if (m.getfCierre() != null) {
				pStmnt.setDate(cnt++, new Date(m.getfCierre().getTime()));
			}
			if (m.getUsuarioCerro() != null) {
				pStmnt.setString(cnt++, m.getUsuarioCerro());
			}

			pStmnt.setInt(cnt++, m.getnMes());
			if (m.getcCentroContable() != null && !"".equals(m.getcCentroContable()))
				pStmnt.setString(cnt++, m.getcCentroContable());						

			pStmnt.setInt(cnt++, m.getaEjercicioFiscal());
			
			if (m.getcUnidadResponsable() != null && !"".equals(m.getcUnidadResponsable()))
				pStmnt.setString(cnt++, m.getcUnidadResponsable());

			int r = pStmnt.executeUpdate();
			return r;
		} catch (Exception e) {
			throw new AccountingEngineException("Error cerrando mes contable " + e.toString(), e);
		} finally {
			if (pStmnt != null)
				try {
					pStmnt.close();
				} catch (Exception e) {
					log.warn("Problemas cerrando PreparedStament", e);
				}

		}
	}

	public static int abreMesContable(Connection conn, MesContable m) throws AccountingEngineException {
		m.setMesAbierto("S");
		MesContable toFind = new MesContable();

		toFind.setnMes(m.getnMes());
		toFind.setaEjercicioFiscal(m.getaEjercicioFiscal());
		toFind.setcCentroContable(m.getcCentroContable());
		toFind.setcUnidadResponsable(m.getcUnidadResponsable());

		if (m.getcCentroContable() != null && !"".equals(m.getcCentroContable())) {
			List<MesContable> l = buscaMesContable(conn, toFind);
			if (l.size() > 0)
				return updateMesContable(conn, m);
			else
				return insertaMesContable(conn, m);
		} else {
			insertaMesContableEnBloque(conn, m);
			return updateMesContable(conn, m);
		}
	}

	public static int insertaMesContable(Connection conn, MesContable m) throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		int r = 0;
		String qry = "INSERT INTO tMesesContables(nMes, cCentroContable, aEjercicioFiscal, mesAbierto, fcierre, usuarioCerro, cUnidadResponsable ) " + " VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			pStmnt = conn.prepareStatement(qry);
			pStmnt.setInt(1, m.getnMes());
			pStmnt.setString(2, m.getcCentroContable());
			pStmnt.setInt(3, m.getaEjercicioFiscal());
			pStmnt.setString(4, m.getMesAbierto());
			pStmnt.setDate(5, new Date(m.getfCierre().getTime()));
			pStmnt.setString(6, m.getUsuarioCerro());
			pStmnt.setString(7, m.getcUnidadResponsable());
			r = pStmnt.executeUpdate();
			return r;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null)
				try {
					pStmnt.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando el PreparedStatement " + e2.toString());
				}
			pStmnt = null;
		}
	}

	public static int insertaMesContableEnBloque(Connection conn, MesContable m) throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		int r = 0;
		String qry = "INSERT INTO tmesescontables " + "				            (nmes, " + "				             ccentrocontable, " + "				             aejerciciofiscal, " + "				             mesabierto, " + "				             fcierre, " + "				             usuariocerro) " + "				SELECT ?         AS nMes, "
			+ "				       ccentrocontable, " + "				         ?       AS aEjercicioFiscal, " + "				         ?       AS mesAbierto, " + "				       Getdate() AS fcierre, " + "				       ?  AS usuarioCerro " + "				FROM   tcatalogocentrocontable WITH(NOLOCK) "
			+ "				WHERE  ccentrocontable NOT IN (SELECT ccentrocontable " + "				                               FROM   tmesescontables WITH(NOLOCK) " + "				                               WHERE  nmes = ?); ";

		try {
			pStmnt = conn.prepareStatement(qry);
			pStmnt.setInt(1, m.getnMes());
			pStmnt.setInt(2, m.getaEjercicioFiscal());
			pStmnt.setString(3, m.getMesAbierto());
			pStmnt.setString(4, m.getUsuarioCerro());
			pStmnt.setInt(5, m.getnMes());

			r = pStmnt.executeUpdate();

			return r;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null)
				try {
					pStmnt.close();
				} catch (Exception e2) {
					log.warn("Problemas cerrando el PreparedStatement " + e2.toString());
				}
			pStmnt = null;
		}

	}

	public static int ultimoMesContableAbierto(Connection conn, MesContable m) throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		ResultSet rs = null;
		String qryBase = "SELECT	MAX(nMes) as nMes " + " FROM	tMesesContables WITH(NOLOCK) " + " WHERE	mesAbierto = 'S' " + "		AND	aEjercicioFiscal = ? ";
		if (m.getcCentroContable() != null && !"".equals(m.getcCentroContable()))
			qryBase += "		AND	cCentroContable = ? ";
		int maxNMes = -1;
		try {

			pStmnt = conn.prepareStatement(qryBase);
			int cnt = 1;

			pStmnt.setInt(cnt++, m.getaEjercicioFiscal());
			if (m.getcCentroContable() != null && !"".equals(m.getcCentroContable()))
				pStmnt.setString(cnt++, m.getcCentroContable());

			rs = pStmnt.executeQuery();

			if (rs.next()) {
				maxNMes = rs.getInt("nMes");
			}

			return maxNMes;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null) {
				try {
					pStmnt.close();
				} catch (Exception e) {
					log.warn("Problemas cerrando PreparedStatemnt " + e.toString(), e);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					log.warn("Problemas cerrando PreparedStatemnt " + e.toString(), e);
				}
			}
		}

	}

	public static List<MesContable> estadoMesesAnteriores(Connection conn, MesContable m) throws AccountingEngineException {
		PreparedStatement pStmnt = null;
		ResultSet rs = null;
		String qryBase = " SELECT	* " + " FROM	tMesesContables t WITH(nolock) " + " WHERE	t.aEjercicioFiscal = ?" + " 		AND	t.nMes < ? " + " 		AND	t.nMes > 0 " + " 		AND	t.mesAbierto = ?";
		List<MesContable> result = new ArrayList<MesContable>();
		try {

			pStmnt = conn.prepareStatement(qryBase);
			int cnt = 1;
			pStmnt.setInt(cnt++, m.getaEjercicioFiscal());
			pStmnt.setInt(cnt++, m.getnMes());
			pStmnt.setString(cnt++, m.getMesAbierto());

			rs = pStmnt.executeQuery();
			while (rs.next()) {
				MesContable mAux = new MesContable();
				mAux.setnMes(rs.getInt("nMes"));
				mAux.setcCentroContable(rs.getString("cCentroContable"));
				mAux.setaEjercicioFiscal(rs.getInt("aEjercicioFiscal"));
				mAux.setMesAbierto(rs.getString("mesAbierto"));
				mAux.setfCierre(rs.getDate("fcierre"));
				mAux.setUsuarioCerro(rs.getString("usuarioCerro"));

				result.add(mAux);
			}
			return result;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (pStmnt != null) {
				try {
					pStmnt.close();
				} catch (Exception e) {
					log.warn("Problemas cerrando PreparedStatemnt " + e.toString(), e);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					log.warn("Problemas cerrando PreparedStatemnt " + e.toString(), e);
				}
			}
		}

	}

	/**
	 * Cambia la fecha de aplicacion para un pago.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param tipoPago
	 *            Tipo de pago.
	 * @param nFolioPago
	 *            FOlio del pago
	 * @return Numero de registros afectados.
	 * @throws Exception
	 */
	public static int cambiaFechaAplicacion(Connection conn, String tipoPago, int nFolioPago) throws Exception {
		log.trace("Cambiando fecha de aplicacion para el pago [" + tipoPago + "] con folio[" + nFolioPago + "]");
		int afectados = 0;
		String tableName = "t" + tipoPago + "Encabezado";
		String field = "nFolio" + tipoPago;
		String query = "UPDATE " + tableName + " set fAplicacion = GETDATE() WHERE " + field + " = " + nFolioPago;

		log.debug("Tabla[" + tableName + "] Campo Folio[" + field + "]");
		log.debug(query);
		Statement stmnt = null;
		try {
			stmnt = conn.createStatement();
			afectados = stmnt.executeUpdate(query);
			return afectados;
		} finally {
			CloseObject.closeObject(stmnt, false);
		}

	}
}
