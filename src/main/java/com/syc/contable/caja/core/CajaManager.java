package com.syc.contable.caja.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.caja.CajaBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.solicitudviaticos.SolicitudViaticosManager;

public class CajaManager {

	private static final Logger log = Logger.getLogger(CajaManager.class);

	/**
	 * Constructor por defecto.
	 */
	public CajaManager() {
		super();
	}

	/**
	 * Inserta el detalle de los viaticos a cancelar. Se toma la informacion de
	 * la relacion de gastos para insertarla en la tabla.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param nFolioCaja
	 *            Folio de la solicitud a comprobar.
	 * @param nFolioComprobacion
	 *            Folio de la relacion de gastos con la que se comprueba la
	 *            solicitud
	 * @param fFechaComprobacion
	 *            Fecha en la que se realiza la comprobacion.
	 * @param mMontoComprobacion
	 *            Monto a comprobar. Se decrementa de la solicitud para tener el
	 *            saldo real.
	 * @return Numero de filas insertadas.
	 * @throws Exception
	 *             Si ocurre alguun error.
	 */
	public static int insertViaticosDetalle(Connection conn, int nFolioCaja, int nFolioComprobacion, String fFechaComprobacion, double mMontoComprobacion) throws Exception {

		PreparedStatement pstmnt = null;
		PreparedStatement psSel = null;
		ResultSet rs = null;
		int retval = -1;
		int nDocRenglon = 0;

		log.info("Insertando detalle para el estado de cuenta de la solicitud " + nFolioCaja + " por " + mMontoComprobacion);
		try {
			String queryDocRenglon = "SELECT MAX( nDocRenglon ) AS nDocRenglon FROM tEstadoDeCuentaViaticosDetalle WITH( NOLOCK ) WHERE nFolioCaja = ? ";
			String queryInsert = "INSERT INTO tEstadoDeCuentaViaticosDetalle(nFolioCaja, nFoliocomprobacion, nDocRenglon, mMontoComprobacion,fFechaComprobacion) VALUES (?,?,?,?," + (fFechaComprobacion == null ? "GETDATE()" : "?") + ")";

			psSel = conn.prepareStatement(queryDocRenglon);
			psSel.setInt(1, nFolioCaja);

			rs = psSel.executeQuery();

			if (rs.next())
				nDocRenglon = rs.getInt("nDocRenglon");

			nDocRenglon++;

			pstmnt = conn.prepareStatement(queryInsert);
			pstmnt.setInt(1, nFolioCaja);
			pstmnt.setInt(2, nFolioComprobacion);
			pstmnt.setInt(3, nDocRenglon);
			pstmnt.setDouble(4, mMontoComprobacion);
			if (fFechaComprobacion != null)
				pstmnt.setString(5, fFechaComprobacion);
			retval = pstmnt.executeUpdate();
			return retval;
		} finally {
			CloseObject.closeObject(pstmnt, false);
			CloseObject.closeObject(psSel, false);
			CloseObject.closeObject(rs, false);
		}

	}

	public static int updateViaticosEncabezado(Connection conn, int nFolioCaja, double mMontoComprobacion) throws Exception {

		PreparedStatement pstmnt = null;
		int retval = -1;

		log.info("Actualizando el remanente de la solicitud" + nFolioCaja);
		try {
			String queryUpdate = "UPDATE tEstadoDeCuentaViaticosEncabezado SET mMontoRemanente = mMontoRemanente + ? WHERE nFolioCaja = ?";

			pstmnt = conn.prepareStatement(queryUpdate);
			pstmnt.setDouble(1, mMontoComprobacion);
			pstmnt.setInt(2, nFolioCaja);
			retval = pstmnt.executeUpdate();
			return retval;
		} finally {
			CloseObject.closeObject(pstmnt, false);
		}

	}

	public static int borraDetalleViaticos(Connection conn, int nFolioCaja, int nFolioComprobacion, String us) throws Exception {

		PreparedStatement pstmntD = null;
		PreparedStatement pstmntU = null;
		PreparedStatement pstmntV = null;
		int result = -1;

		try {
			String queryUpdate = " UPDATE tEstadoDeCuentaViaticosEncabezado " + " SET mMontoRemanente = mMontoRemanente + (SELECT mMontoComprobacion " + " FROM tEstadoDeCuentaViaticosDetalle WITH (NOLOCK) " + " WHERE nFolioCaja = ? AND nFolioComprobacion = ?) " + " WHERE nFolioCaja = ? ";

			String queryDelete = "DELETE tEstadoDeCuentaViaticosDetalle WHERE nFolioCaja = ? AND nFolioComprobacion = ? ";

			String queryV = "UPDATE dbo.tVolante_Devolucion SET FolioCaja = ?, usuario = ? WHERE NumeroFolio LIKE 'RELG%' AND Folio = ?";

			pstmntU = conn.prepareStatement(queryUpdate);
			pstmntU.setInt(1, nFolioCaja);
			pstmntU.setInt(2, nFolioComprobacion);
			pstmntU.setInt(3, nFolioCaja);
			result = pstmntU.executeUpdate();

			pstmntD = conn.prepareStatement(queryDelete);
			pstmntD.setInt(1, nFolioCaja);
			pstmntD.setInt(2, nFolioComprobacion);
			result += pstmntD.executeUpdate();

			pstmntV = conn.prepareStatement(queryV);
			pstmntV.setInt(1, nFolioCaja);
			pstmntV.setString(2, us);
			pstmntV.setInt(3, nFolioComprobacion);
			result += pstmntV.executeUpdate();

			return result;

		} finally {
			CloseObject.closeObject(pstmntD, false);
			CloseObject.closeObject(pstmntU, false);
		}

	}

	public static int insertaSolicitudNoPresupuestal(Connection conn, SolicitudNoPresupuestal snp) throws Exception {
		int r = 0;
		r += CajaManager.insertaEncabezadoSNP(conn, snp);
		r += CajaManager.insertaDetalleSNP(conn, snp);
		return r;
	}

	private static int insertaDetalleSNP(Connection conn, SolicitudNoPresupuestal snp) throws Exception {
		int r = 0;
		String queryInsert = "INSERT INTO tcajadetalle (nFoliocaja, ndocRenglon, cEvento, mImporte, mImporteNegativo, ALM, CTAB, OBGT, RFC, EP, nCuentaBeneficiario)" + "values(?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement psInsert = null;
		try {

			psInsert = conn.prepareStatement(queryInsert);

			for (Iterator<SolicitudNoPresupuestalDetalle> i = snp.getDetalle().iterator(); i.hasNext();) {

				SolicitudNoPresupuestalDetalle renglon = i.next();
				psInsert.setInt(1, renglon.getFoliocaja());
				psInsert.setInt(2, renglon.getDocRenglon());
				psInsert.setString(3, renglon.getEvento());
				psInsert.setDouble(4, renglon.getImporte().doubleValue());
				psInsert.setDouble(5, renglon.getImporteNegativo().doubleValue());
				psInsert.setString(6, renglon.getAlmacen());
				psInsert.setString(7, renglon.getCuentaBancaria());
				psInsert.setString(8, renglon.getObjetoGasto());
				psInsert.setString(9, renglon.getRfc());
				psInsert.setString(10, renglon.getEP());
				psInsert.setString(11, renglon.getCuentaBeneficiario());

				r += psInsert.executeUpdate();
			}

			return r;
		} finally {
			CloseObject.closeObject(psInsert);
		}

	}

	private static int insertaEncabezadoSNP(Connection conn, SolicitudNoPresupuestal snp) throws Exception {
		String query = "insert into tcajaEncabezado(nFoliocaja, fcreacion, faplicacion, cMes, cCentrocontable, ctipopoliza, nfoliopoliza, cdescripcionpoliza, u_login, cdocumentohaplicado, cmotivorechazo, cunidadresponsablecontable, nfoliopolizacancelacion, fcancelacion, aejerciciofiscal, cramo, cunidadejecutora, sFirmanteVoBo, sPuestoVoBo, sFirmanteAut, sPuestoAut, id_caso, nidgrupoevento, mMontoSolicitud, nFolioComprobacion, cNombreBeneficiarioCheque, cAPaternoCheque, cAMaternoCheque, comprobado) "
			+ "values(?,CONVERT(DATE,?,103),CONVERT(DATE,?,103),?,?,?,?,?,?,?,?,?,?,CONVERT(DATE,?,103),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement psInsert = null;

		try {

			psInsert = conn.prepareStatement(query);

			psInsert.setInt(1, snp.getEncabezado().getFolioCaja());
			psInsert.setString(2, snp.getEncabezado().getFechaCreacion());
			psInsert.setString(3, snp.getEncabezado().getFechaAplicacion());
			psInsert.setInt(4, snp.getEncabezado().getMes());
			psInsert.setString(5, snp.getEncabezado().getCentroContable());
			psInsert.setString(6, snp.getEncabezado().getTipoPoliza());

			if (snp.getEncabezado().getFolioPoliza() <= 0)
				psInsert.setNull(7, java.sql.Types.INTEGER);
			else
				psInsert.setInt(7, snp.getEncabezado().getFolioPoliza());

			psInsert.setString(8, snp.getEncabezado().getDescripcionPoliza());
			psInsert.setString(9, snp.getEncabezado().getuLogin());
			psInsert.setString(10, StringUtils.isEmpty(snp.getEncabezado().getDocumentoHAplicado()) ? null : snp.getEncabezado().getDocumentoHAplicado());
			psInsert.setString(11, StringUtils.isEmpty(snp.getEncabezado().getMotivoRechazo()) ? null : snp.getEncabezado().getMotivoRechazo());
			psInsert.setString(12, snp.getEncabezado().getUnidadResponsableContable());

			if (StringUtils.isEmpty(snp.getEncabezado().getFolioPolizaCancelacion()))
				psInsert.setNull(13, java.sql.Types.INTEGER);
			else
				psInsert.setInt(13, Integer.parseInt(snp.getEncabezado().getFolioPolizaCancelacion()));

			psInsert.setString(14, snp.getEncabezado().getFechaCancelacion());
			psInsert.setString(15, snp.getEncabezado().getEjercicioFiscal());
			psInsert.setString(16, snp.getEncabezado().getRamo());
			psInsert.setString(17, snp.getEncabezado().getUnidadEjecutora());
			psInsert.setString(18, snp.getEncabezado().getFirmanteVoBo());
			psInsert.setString(19, snp.getEncabezado().getPuestoVoBo());
			psInsert.setString(20, snp.getEncabezado().getFirmanteAut());
			psInsert.setString(21, snp.getEncabezado().getPuestoAut());
			psInsert.setInt(22, snp.getEncabezado().getIdCaso());
			psInsert.setInt(23, snp.getEncabezado().getIdGrupoEvento());
			psInsert.setDouble(24, snp.getEncabezado().getMontoSolicitud().doubleValue());
			psInsert.setInt(25, snp.getEncabezado().getFolioComprobacion());
			psInsert.setString(26, snp.getEncabezado().getNombreBenCheque());
			psInsert.setString(27, snp.getEncabezado().getPaternoBenCheque());
			psInsert.setString(28, snp.getEncabezado().getMaternoBenCheque());
			psInsert.setString(29, snp.getEncabezado().getComprobado());

			return psInsert.executeUpdate();

		} finally {
			CloseObject.closeObject(psInsert);
		}

	}

	public static void actualizaRemanentesComprobacion(Connection conn, String nFolio) throws Exception {
		int nfoliocomprobacion = Integer.parseInt(nFolio);

		if (validaComprobacion(conn, nfoliocomprobacion)) {
			log.info("Es un evento de Comprobacion");
			PreparedStatement psSel = null;
			PreparedStatement psUpd = null;
			ResultSet rs = null;
			ResultSet rs2 = null;

			int nfoliocaja = 0;
			float montoRemanente = 0, montoComprobacion = 0;

			String query = "SELECT nfoliocaja FROM tBonificacion_Comision WITH(NOLOCK) WHERE nfoliocomprobacion=?", query2 = "";

			try {
				psSel = conn.prepareStatement(query);
				psSel.setInt(1, nfoliocomprobacion);

				log.info(psSel + "[" + nfoliocomprobacion + "]");
				rs = psSel.executeQuery();

				while (rs.next()) {
					nfoliocaja = rs.getInt("nfoliocaja");
					query2 = "SELECT mMontoRemanente FROM tEstadoDeCuentaComprobacionesEncabezado WITH(NOLOCK) WHERE nFolioCaja=?";
					psSel = conn.prepareStatement(query2);
					psSel.setInt(1, nfoliocaja);
					log.info("Intentando leer monto remante del folio: " + nfoliocaja);

					log.info(psSel + "[" + nfoliocaja + "]");
					rs2 = psSel.executeQuery();

					if (rs2.next())
						montoRemanente = rs2.getFloat("mMontoRemanente");
					else
						throw new Exception("No se pudo obtener el remanente de la solicitud " + nfoliocaja);

					query2 = "SELECT mMontoComprobacion FROM tEstadoDeCuentaComprobacionesDetalle WITH(NOLOCK) WHERE nFolioCaja=? AND nFolioComprobacion=?";
					psSel = conn.prepareStatement(query2);
					psSel.setInt(1, nfoliocaja);
					psSel.setInt(2, nfoliocomprobacion);
					log.info("Intentando leer monto de comprobacion del folio: " + nfoliocaja);

					log.info(psSel + "[" + nfoliocaja + "]" + "[" + nfoliocomprobacion + "]");
					rs2 = psSel.executeQuery();

					if (rs2.next())
						montoComprobacion = rs2.getFloat("mMontoComprobacion");
					else
						throw new Exception("No se pudo obtener el monto de comprobacion de la solicitud" + nfoliocaja);

					montoRemanente = montoRemanente - montoComprobacion;

					if (montoRemanente < 0)
						throw new Exception("No queda saldo suficiente en el remanente en la solicitud " + nfoliocaja);
					else {
						String update = "UPDATE tEstadoDeCuentaComprobacionesEncabezado SET mMontoRemanente=? WHERE nFolioCaja=?";
						psUpd = conn.prepareStatement(update);
						psUpd.setFloat(1, montoRemanente);
						psUpd.setInt(2, nfoliocaja);
						log.info("Actualizado remante de la solicitud: " + nfoliocaja);

						log.info(psUpd + "[" + montoRemanente + "]" + "[" + nfoliocaja + "]");
						psUpd.executeUpdate();

						update = "UPDATE tEstadoDeCuentaComprobacionesDetalle SET cDocumentoHaplicado='S' WHERE nFolioCaja=? AND nFolioComprobacion=?";
						psUpd = conn.prepareStatement(update);
						psUpd.setInt(1, nfoliocaja);
						psUpd.setInt(2, nfoliocomprobacion);
						log.info("Actualizado cDocumentoHaplicado='S' en tEstadoDeCuentaComprobacionesDetalle: " + nfoliocaja + "-" + nfoliocomprobacion);

						log.info(psUpd + "[" + nfoliocaja + "]" + "[" + nfoliocomprobacion + "]");
						psUpd.executeUpdate();

					}
				}

			} finally {
				CloseObject.closeObject(psSel, false);
				CloseObject.closeObject(rs, false);
				CloseObject.closeObject(rs2, false);
				CloseObject.closeObject(psUpd, false);
			}
		} else
			log.info("No es un evento de Comprobacion");
	}

	private static boolean validaComprobacion(Connection conn, int nFolioCaja) throws Exception {
		PreparedStatement psSel = null;
		ResultSet rs = null;
		String evento = "", saldoInicial = "";

		String query = "SELECT cEvento FROM tcajadetalle WITH(NOLOCK) where nFoliocaja=?";

		try {
			psSel = conn.prepareStatement(query);
			psSel.setInt(1, nFolioCaja);
			log.info("Leyendo evento de la solicitud: " + nFolioCaja);

			log.info(psSel + "[" + nFolioCaja + "]");
			rs = psSel.executeQuery();

			if (rs.next())
				evento = rs.getString("cEvento");
			else
				throw new Exception("No se pudo leer el evento de la solicitud: " + nFolioCaja);

			if (evento.equals("35_2_7") || evento.equals("35_1_2_A")) {
				query = "SELECT CASE WHEN esSaldoInicial=1 THEN 'SI' ELSE 'NO' END AS esSaldoInicial FROM tcajaencabezado WITH(NOLOCK) where nFoliocaja=?";
				psSel = conn.prepareStatement(query);
				psSel.setInt(1, nFolioCaja);

				log.info(psSel + "[" + nFolioCaja + "]");
				rs = psSel.executeQuery();

				if (rs.next())
					saldoInicial = rs.getString("esSaldoInicial");
				else
					throw new Exception("No se pudo leer esSaldoInicial de la solicitud: " + nFolioCaja);

				if (saldoInicial.equals("SI"))
					return false;
				else
					return true;
			} else
				return false;
		} finally {
			CloseObject.closeObject(psSel, false);
			CloseObject.closeObject(rs, false);
		}
	}

	public static String validaEventoCajaChica(Connection conn, int nFolioCaja) throws Exception {

		PreparedStatement pstmnt = null;
		String retval = "";
		ResultSet rs = null;

		log.info("Buscar evento para validar si el anticipo es una asignacion de caja chica" + nFolioCaja);
		try {
			String query = "SELECT cEvento FROM tCajaDetalle WHERE nFolioCaja = ?";

			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, nFolioCaja);

			log.info(pstmnt + "[" + nFolioCaja + "]");
			rs = pstmnt.executeQuery();

			if (rs.next())
				retval = rs.getString("cEvento");

			return retval;
		} finally {
			CloseObject.closeObject(pstmnt, false);
		}

	}

	public static double buscaRemanente(Connection conn, int nFolioCaja) throws Exception {

		PreparedStatement pstmnt = null;
		double retval = 0;
		ResultSet rs = null;

		log.info("Buscar remanente del anticipo de asignacion de caja chica" + nFolioCaja);
		try {
			String query = "SELECT mMontoRemanente FROM tEstadoDeCuentaViaticosEncabezado WHERE nFolioCaja = ?";

			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, nFolioCaja);

			log.info(pstmnt + "[" + nFolioCaja + "]");
			rs = pstmnt.executeQuery();

			if (rs.next())
				retval = rs.getDouble("mMontoRemanente");

			return retval;
		} finally {
			CloseObject.closeObject(pstmnt, false);
		}

	}

	public static int eliminaRFCAsignadoConCajaChica(Connection conn, int nFolioCaja) throws Exception {

		PreparedStatement pstmntD = null;
		int result = -1;

		try {
			String queryDelete = "DELETE tEmpleadosCajaChica WHERE cRFC = (SELECT RFC FROM tcajadetalle WHERE  nFoliocaja = ?) ";

			pstmntD = conn.prepareStatement(queryDelete);
			pstmntD.setInt(1, nFolioCaja);
			result += pstmntD.executeUpdate();

			return result;

		} finally {
			CloseObject.closeObject(pstmntD, false);
		}

	}

	public static boolean existeSolicitud(Connection conn, int nFolioCaja) throws Exception {
		String queryExiste = "SELECT COUNT(*) AS existe FROM tCajaEncabezado WHERE nFoliocaja = ?";
		PreparedStatement pstmntD = null;
		ResultSet result = null;
		boolean existe = false;

		try {

			pstmntD = conn.prepareStatement(queryExiste);
			pstmntD.setInt(1, nFolioCaja);
			result = pstmntD.executeQuery();

			if (result.next())
				existe = result.getInt("existe") > 0;

			return existe;

		} finally {
			CloseObject.closeObject(pstmntD, false);
			CloseObject.closeObject(result, false);
		}

	}

	public static boolean esSolicitudAplicada(Connection conn, int nFolioCaja) throws Exception {

		String queryExiste = "SELECT COUNT(*) AS existe FROM tCajaEncabezado WITH(nolock) WHERE nFoliocaja = ? AND cDocumentoHAplicado = 'S'";
		PreparedStatement pstmntD = null;
		ResultSet result = null;
		boolean existe = false;

		try {

			pstmntD = conn.prepareStatement(queryExiste);
			pstmntD.setInt(1, nFolioCaja);
			result = pstmntD.executeQuery();

			if (result.next())
				existe = result.getInt("existe") > 0;

			return existe;

		} finally {

			CloseObject.closeObject(pstmntD, false);
			CloseObject.closeObject(result, false);

		}

	}

	public static String getFolioSAI(Connection conn, int idField) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT	C_FOLIO AS folio " + "  FROM	tcajaencabezado tramite WITH(NOLOCK) " + "		INNER JOIN CG_CASO caso WITH(NOLOCK) " + "		ON tramite.id_caso = caso.ID_CASO " + " WHERE	nFoliocaja = ?";
		try {

			ps = conn.prepareStatement(query);
			ps.setInt(1, idField);
			rs = ps.executeQuery();

			if (rs.next())
				return rs.getString("folio");
			else
				throw new Exception("No se encontro folio SAI para el folio de caja: " + idField);
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}

	public static String getMontoCajaStr(Connection conn, int idField) throws Exception {
		double montoCaja = 0.0;
		String query = "SELECT mMontoSolicitud  FROM tcajaencabezado WHERE nFoliocaja = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, idField);

			rs = ps.executeQuery();

			if (rs.next()) {
				montoCaja = rs.getDouble(1);
				return Util.formatNumber(montoCaja);
			} else {
				throw new Exception("No se encontro monto para el folio de caja: " + idField);
			}

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}

	public static int actualizaFolioSICOP(Connection conn, String nFolioCaja) throws Exception {

		PreparedStatement pstmntD = null;
		int result = -1;

		try {
			String query = "UPDATE tcajaencabezado SET nEnviadoSICOP = -3 WHERE nFoliocaja = ?";

			pstmntD = conn.prepareStatement(query);
			pstmntD.setString(1, nFolioCaja);
			result += pstmntD.executeUpdate();

			return result;
		} finally {
			CloseObject.closeObject(pstmntD, false);
		}
	}

	public static void cancelaSolicitudFirmaElectronica(Connection conn, String folio, String cancelReason) throws Exception {
		PreparedStatement ps = null;
		String query = "UPDATE tCajaEncabezado SET cDocumentoHAplicado = 'C' where nFolioCaja = ?";

		try {

			ps = conn.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(folio));

			ps.executeUpdate();

		} finally {
			CloseObject.closeObject(ps);
		}

	}

	public static void actualizaFolioComprobacion(Connection conn, String nFolioCaja) throws Exception {
		PreparedStatement ps = null;

		try {
			String query = "UPDATE tCajaEncabezado SET nFolioComprobacion = 0 WHERE nFolioCaja = ?";

			ps = conn.prepareStatement(query);
			ps.setString(1, nFolioCaja);
			ps.executeUpdate();

		} finally {
			CloseObject.closeObject(ps, false);
		}

	}

	public static void actualizaEnvioSICOP(Connection conn, int folioCaja, int estatusSICOP) throws SQLException {
		String query = "UPDATE tCajaEncabezado SET nEnviadoSICOP = ? WHERE nFolioCaja = ?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, estatusSICOP);
			ps.setInt(2, folioCaja);
			
			int afectados = ps.executeUpdate();
			log.info("Se cambio el estatus de envio SICOP a " + estatusSICOP + " de la SNP " + folioCaja + ". " + afectados + " afectados" );
			
		} finally {
			CloseObject.closeObject(ps);
		}

	}
	
	public static void insertaEstadoCtaViaticos(Connection conn, int folioCaja) throws SQLException {
		String query = "EXEC sp_inserta_encabezadoViaticos ?";
		CallableStatement clb = null;
		try {
			clb = conn.prepareCall( query );
			clb.setInt(1, folioCaja);

			clb.executeUpdate();
			log.info("Se inserto en el Estado de Cuenta Viaticos" );
			
		} finally {
			CloseObject.closeObject(clb);
		}

	}
	
	public static String obtenerEvento(Connection conn, int nFolioCaja) throws Exception {
		String evento = "";
		String query = "SELECT TOP 1 cEvento FROM tcajadetalle WHERE nFoliocaja = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, nFolioCaja);

			rs = ps.executeQuery();

			if (rs.next()) {
				evento = rs.getString(1);				
			} else {
				throw new Exception("No se encontro el evento para el folio de caja: " + nFolioCaja);
			}

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
		return evento;
	}

	public static void actualizaEsSaldoInicial(Connection conn, int nFolioCaja, String tipo) throws SQLException {		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		String query = "{call sp_actualizaSaldosAntiguedad ( ?, ? )}";
				
		try {			
			cs = conn.prepareCall(query);
			cs.setInt(1, nFolioCaja);
			cs.setString(2, tipo);
			
			cs.execute();
			
			log.info("Se ejecuta:" + query);
			
			log.info("Se actualizo el indentificador si es saldo inicial de: " + nFolioCaja );
			
		}finally {
			CloseObject.closeObject(cs);
			CloseObject.closeObject(rs);
		}
		
	}	
	
	public static String validaCC(Connection conn, int nFolioCaja , String tipo) throws Exception {
		String cc = "";
		String query = "SELECT dbo.fn_validaCCSaldosAntiguedad ( ?, ? )";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, nFolioCaja);
			ps.setString(2, tipo); 
			
			rs = ps.executeQuery();			

			if (rs.next()) {
				cc = rs.getString(1);				
			} else {
				throw new Exception("No se encontro el centro contable para el folio de caja: " + nFolioCaja);
			}

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
		return cc;
	}

	public static void actualizaEstadoCuenta( Connection conn, int nFolioCaja )  throws Exception {
		PreparedStatement ps = null;		
		
		String query = "INSERT INTO dbo.tEstadoDeCuentaViaticosEncabezado ( nFolioCaja, cUnidadEjecutora, mMontoViaticos, mMontoRemanente, cViaticos )\r\n" + 
					   "SELECT nFoliocaja, cunidadejecutora, mMontoSolicitud, mMontoSolicitud, 'S' AS cViaticos FROM dbo.tcajaencabezado WITH (NOLOCK) WHERE nFoliocaja = ?";
				
		try {			
			ps = conn.prepareStatement(query);
			ps.setInt(1, nFolioCaja);			
			
			ps.execute();
			
			log.info("Se ejecuta:" + query);
			
			log.info("Se inserta en el estado de cuenta el anticipo con folio: " + nFolioCaja );
			
		}finally {
			CloseObject.closeObject(ps);			
		}
		
	}
	
	public static String esGREENMEX( Connection conn, int parseInt ) throws Exception {
		PreparedStatement ps =null;
		ResultSet rs = null;
		String destino = "";
		String query = "";
		
		try {
			
			query = "SELECT ID_DESTINO_GASTO, mImporteMasIva FROM tPAGODIVERSOEncabezado WHERE nFolioPAGODIVERSO = ?";
			
			ps = conn.prepareStatement(query);
			ps.setInt(1, parseInt);			
			
			rs = ps.executeQuery();			

			if (rs.next()) {
				destino = rs.getString(1);			
			} else {
				throw new Exception("No se encontro el destino gasto del pago");
			}
			
			return destino;
						
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs );
		}
	}

	public static void actualizaEstadoCuentaGreenMex( Connection conn, int nFolioDocumento ) throws Exception {
		PreparedStatement ps =null, ps2 = null, ps3 = null;
		ResultSet rs = null;
		String query = "";
		String query2 = "";
		
		try {
			
			query = "UPDATE tEstadoDeCuentaGreenMexDetalle SET cdocumentohaplicado = 'S' WHERE nFolioComprobacion = ?";
			query2 = "UPDATE ENC SET  mMontoRemanente = mMontoRemanente - mMontoComprobacion\r\n"
					+ "FROM tEstadoDeCuentaGreenMexEncabezado ENC\r\n"
					+ "INNER JOIN tEstadoDeCuentaGreenMexDetalle DET\r\n"
					+ "ON ENC.nFolioCaja = DET.nFolioCaja\r\n"
					+ "WHERE nFolioComprobacion = ?";
			
			ps = conn.prepareStatement(query);
			ps.setInt(1, nFolioDocumento);							
			ps.execute();
			
			ps2 = conn.prepareStatement(query2);
			ps2.setInt(1, nFolioDocumento );			
			ps2.execute();
			
			log.info("Se actualizo el estado de cuenta." );
		
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( ps2 );
			CloseObject.closeObject( ps3 );
			CloseObject.closeObject( rs );
		}
	}

	public static int borraDetalleLaudos( Connection conn, Integer nFolioCaja, Integer nFolioComprobacion, String us ) throws Exception {
		PreparedStatement pstmntD = null, pstmntUD = null;
		PreparedStatement pstmnCaja = null;
		ResultSet rsCaja = null;
		int result = -1;

		try {
			
			pstmnCaja = conn.prepareStatement( "SELECT nFolioCaja FROM dbo.tComprobacionLaudos WITH (NOLOCK) WHERE nFolioCaja = ? AND nFolioRELACIONGASTOS = ?" );
			pstmnCaja.setInt( 1, nFolioCaja );
			pstmnCaja.setInt( 2, nFolioComprobacion );
			rsCaja = pstmnCaja.executeQuery();
			
			if ( rsCaja.next() ) {
				
				String queryDelete = "DELETE tComprobacionLaudos WHERE nFolioCaja = ? AND nFolioRELACIONGASTOS = ? ";
				String queryUpdate = "UPDATE tRELACIONGASTOSEncabezado SET nFolioCaja = 0 WHERE nFolioRELACIONGASTOS = ? ";
	
				pstmntD = conn.prepareStatement(queryDelete);
				pstmntD.setInt(1, nFolioCaja);
				pstmntD.setInt(2, nFolioComprobacion);
				result += pstmntD.executeUpdate();
				
				pstmntUD = conn.prepareStatement(queryUpdate);				
				pstmntUD.setInt(1, nFolioComprobacion);
				result += pstmntUD.executeUpdate();
	
			}
			
		} finally {
			CloseObject.closeObject(pstmntD, false);
			CloseObject.closeObject(pstmntUD, false);
			CloseObject.closeObject(pstmnCaja, false);
		}
		
		return result;
		
	}

public static int insertaSolicitudNoPresupuestalISN( Connection conn, String nIDIntegracion, String reportPath ) throws Exception {		
		
		Usuario u = new Usuario();	
		u.setLogin("ADMIN");
		u = UsuarioManager.select(conn, u);
		u = UsuarioManager.getRamoUR(conn, u);
						
		FolioGeneratorInterface fg = null;
		ClassLoader cl = SolicitudViaticosManager.class.getClassLoader();
		Class<?> clase = cl.loadClass(GestionInterface.FOLIO_GENERATOR);
		fg = (FolioGeneratorInterface) clase.newInstance();

		Caso c = CasoManager.nuevoCaso(conn, u, 42, fg);
		String fecha = Util.getTodayESMX();
		String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
				
		c.getCasoDato("FOLIO").setValor(c.getFolio());
		c.getCasoDato("OPERADOR").setValor(u.getLogin());
		c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
		c.getCasoDato("EJERCICIO_FISCAL").setValor(ejercicioFiscal);

		Map<String, String> m = new HashMap<String, String>();
		m.put("FOLIO", c.getFolio());
		m.put("OPERADOR", u.getLogin());
		m.put("FECHA_DOCUMENTO", fecha);
		m.put("EJERCICIO_FISCAL", ejercicioFiscal);

		Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
		int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
		c.setIdGabinete(id_gabinete);
		
		CasoManager.update(conn, c);
		
		int nFolioCaja = Integer.parseInt(  c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1 ) );
		
		CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
		
		CajaManager.insertaEncabezadoSNPISNQ(conn, nFolioCaja, u.getLogin(), nIDIntegracion, c);
		CajaManager.insertaDetalleSNPISNQ(conn, nFolioCaja, u.getLogin(), nIDIntegracion, c);			
		CajaManager.insertaCxpCaja(conn, nFolioCaja, nIDIntegracion);
		CajaBusinessLogic cajaBusinessLogic = new CajaBusinessLogic();
		cajaBusinessLogic.solicitaFirmaElectronica( conn, c, u, reportPath );

		return nFolioCaja;
	}	

	public static void insertaEncabezadoSNPISNQ( Connection conn, int nFolioCaja, String login, String nIDIntegracion, Caso c ) throws Exception {		
		PreparedStatement psInsert = null;
		
		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tcajaencabezado (nFoliocaja, fcreacion, faplicacion, cMes, cCentrocontable, ctipopoliza, cdescripcionpoliza, u_login, cunidadresponsablecontable, aejerciciofiscal, cramo, cunidadejecutora, sFirmanteVoBo, sPuestoVoBo, sFirmanteAut, sPuestoAut, id_caso, nidgrupoevento, mMontoSolicitud, nFolioComprobacion,cEsFirmaElectronica, nNumEmpleadoVoBo, nNumEmpleadoAut, nNumEmpleadoElab, nEnviadoSICOP)\r\n");
		query.append( "SELECT " + nFolioCaja + "\r\n");
		query.append( "	, ENC.fAplicacion\r\n");
		query.append( "	, ENC.fAplicacion\r\n");
		query.append( "	, MONTH(ENC.fAplicacion)\r\n");
		query.append( "	, ccentrocontable\r\n");
		query.append( "	, (SELECT DISTINCT cTipoPoliza FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) JOIN tEventoManual EM WITH (NOLOCK) ON GP.GP_VALOR = CONVERT(VARCHAR(5),cIdGrupoEvento) + '_' + CONVERT(VARCHAR(5),cIdSubGrupoEvento) + '_' + CONVERT(VARCHAR(5),cIdEventoManual) WHERE GP_NOMBRE = 'ISN_QUERETARO_EVENTO') AS tipopoliza\r\n");
		query.append( "	, 'Transferencia de Bancos para pagos de ISN Queretaro CxP: ' + sNoContrarrecibo\r\n");
		query.append( "	, '" + login + "'\r\n");
		query.append( "	, cUnidadResponsableContable	\r\n");
		query.append( "	, aEjercicioFiscal\r\n");
		query.append( "	, cRamo\r\n");
		query.append( "	, ENC.cUnidadResponsable\r\n");
		query.append( "	, (SELECT NOMBREN + ' ' + NOMBREP + ' ' + NOMBREM FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) JOIN v_empleados_giro US WITH (NOLOCK) ON GP.GP_VALOR = CLAVE WHERE GP_NOMBRE = 'ISN_QUERETARO_FIRMA_VOBO') AS FirmaVoBo\r\n");
		query.append( "	, (SELECT DESCRIPCION_PUESTO FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) JOIN v_empleados_giro US WITH (NOLOCK) ON GP.GP_VALOR = CLAVE WHERE GP_NOMBRE = 'ISN_QUERETARO_FIRMA_VOBO') AS PuestoVoBo\r\n");
		query.append( "	, (SELECT NOMBREN + ' ' + NOMBREP + ' ' + NOMBREM FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) JOIN v_empleados_giro US WITH (NOLOCK) ON GP.GP_VALOR = CLAVE WHERE GP_NOMBRE = 'ISN_QUERETARO_FIRMA_AUT') AS FirmaAut\r\n");
		query.append( "	, (SELECT DESCRIPCION_PUESTO FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) JOIN v_empleados_giro US WITH (NOLOCK) ON GP.GP_VALOR = CLAVE WHERE GP_NOMBRE = 'ISN_QUERETARO_FIRMA_AUT') AS PuestoAut\r\n");
		query.append( "	, " + c.getIdCaso() + "\r\n");
		query.append( "	, (SELECT SUBSTRING(GP_VALOR,1,2) FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) WHERE GP_NOMBRE = 'ISN_QUERETARO_EVENTO') AS GrupoEvento\r\n");
		query.append( "	, SUM(mimportemasiva) AS mimportemasiva\r\n");
		query.append( "	, 0\r\n");
		query.append( "	, 'S'\r\n");
		query.append( "	, (SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) WHERE GP_NOMBRE = 'ISN_QUERETARO_FIRMA_VOBO') AS NumEmpleadoVoBo\r\n");
		query.append( "	, (SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) WHERE GP_NOMBRE = 'ISN_QUERETARO_FIRMA_AUT') AS NumEmpleadoAut\r\n");
		query.append( "	, (SELECT cNumeroEmpleado FROM CG_USUARIO US WITH (NOLOCK) WHERE U_LOGIN = 'ADMINISTRADOR') AS NumEmpleadoElab\r\n");
		query.append( "	, NULL AS nEnviadoSICOP\r\n");
		query.append( "FROM tconsolidacionrelaciongastosEncabezado ENC WITH (NOLOCK)\r\n");
		query.append( "JOIN tconsolidacionrelaciongastosdetalle DET WITH (NOLOCK) ON ENC.nFolioConsolidacion = DET.nFolioConsolidacion\r\n");
		query.append( "JOIN tLayoutsCreadosRelacionGastosHeader L WITH (NOLOCK) ON ENC.nIdIntegracion = L.sAuxiliarComodin\r\n");
		query.append( "WHERE ENC.nIdIntegracion = '" + nIDIntegracion + "'\r\n");
		query.append( "GROUP BY ENC.fAplicacion, ENC.fAplicacion, MONTH(ENC.fAplicacion), ccentrocontable, cUnidadResponsableContable, aEjercicioFiscal, cRamo, ENC.cUnidadResponsable, sNoContrarrecibo");		
		try {
			
			log.debug(query);
			
			psInsert = conn.prepareStatement(query.toString());
			int insertados = psInsert.executeUpdate();
			
			log.debug( "Se insertaron " + insertados + " regsitros. " );
	
		} finally {
			CloseObject.closeObject(psInsert);
		}
		
	}

	public static void insertaDetalleSNPISNQ( Connection conn, int nFolioCaja, String login, String nIDIntegracion, Caso c ) throws Exception {		
		PreparedStatement psInsert = null;
		
		StringBuilder queryInsert = new StringBuilder();
		queryInsert.append( "INSERT INTO tcajadetalle (nFoliocaja, ndocRenglon, cEvento, mImporte, mImporteNegativo, CTAB, cUnidadResponsable)\r\n");
		queryInsert.append( "SELECT " + nFolioCaja + "\r\n");
		queryInsert.append( "	, 1\r\n");
		queryInsert.append( "	, (SELECT GP_VALOR  + '_A' FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) WHERE GP_NOMBRE = 'ISN_QUERETARO_EVENTO')	\r\n");
		queryInsert.append( "	, SUM(mimportemasiva) AS mimportemasiva\r\n");
		queryInsert.append( "	, SUM(mimportemasiva)*-1 AS mImporteNegativo\r\n");
		queryInsert.append( "	, (SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) WHERE GP_NOMBRE = 'ISN_QUERETARO_CTAB')\r\n");
		queryInsert.append( "	, ENC.cUnidadResponsable	\r\n");
		queryInsert.append( "FROM tconsolidacionrelaciongastosEncabezado ENC WITH (NOLOCK)\r\n");
		queryInsert.append( "JOIN tconsolidacionrelaciongastosdetalle DET WITH (NOLOCK) ON ENC.nFolioConsolidacion = DET.nFolioConsolidacion\r\n");
		queryInsert.append( "JOIN tLayoutsCreadosRelacionGastosHeader L WITH (NOLOCK) ON ENC.nIdIntegracion = L.sAuxiliarComodin\r\n");
		queryInsert.append( "WHERE ENC.nIdIntegracion = '" + nIDIntegracion + "'\r\n");
		queryInsert.append( "GROUP BY ENC.cUnidadResponsable\r\n" );
		queryInsert.append( "UNION \r\n");
		queryInsert.append( "SELECT " + nFolioCaja + "\r\n");
		queryInsert.append( "	, 2\r\n");
		queryInsert.append( "	, (SELECT GP_VALOR  + '_B' FROM CG_GRUPO_PROPIEDADES GP WITH (NOLOCK) WHERE GP_NOMBRE = 'ISN_QUERETARO_EVENTO')	\r\n");
		queryInsert.append( "	, SUM(mimportemasiva) AS mimportemasiva\r\n");
		queryInsert.append( "	, SUM(mimportemasiva)*-1 AS mImporteNegativo\r\n");
		queryInsert.append( "	, CTAB\r\n");
		queryInsert.append( "	, ENC.cUnidadResponsable	\r\n");
		queryInsert.append( "FROM tconsolidacionrelaciongastosEncabezado ENC WITH (NOLOCK)\r\n");
		queryInsert.append( "JOIN tconsolidacionrelaciongastosdetalle DET WITH (NOLOCK) ON ENC.nFolioConsolidacion = DET.nFolioConsolidacion\r\n");
		queryInsert.append( "JOIN tLayoutsCreadosRelacionGastosHeader L WITH (NOLOCK) ON ENC.nIdIntegracion = L.sAuxiliarComodin\r\n");
		queryInsert.append( "WHERE ENC.nIdIntegracion = '" + nIDIntegracion + "'\r\n");
		queryInsert.append( "GROUP BY ENC.cUnidadResponsable, CTAB");
		
		try {
			
			log.debug(queryInsert);
			
			psInsert = conn.prepareStatement(queryInsert.toString());
			int insertados = psInsert.executeUpdate();
			
			log.debug( "Se insertaron " + insertados + " regsitros. " );
	
		} finally {
			CloseObject.closeObject(psInsert);
		}
	}

	public static void insertaCxpCaja( Connection conn, int nFolioCaja, String nIDIntegracion ) throws Exception {
		PreparedStatement psInsert = null;
		
		StringBuilder queryInsert = new StringBuilder();
		queryInsert.append( "INSERT INTO tCaja_CxPISN (nFoliocaja, caNoContrarrecibo)\r\n");
		queryInsert.append( "SELECT DISTINCT " + nFolioCaja + "\r\n");
		queryInsert.append( "		, sNoContrarrecibo\r\n");
		queryInsert.append( "FROM tconsolidacionrelaciongastosEncabezado ENC WITH (NOLOCK)\r\n");
		queryInsert.append( "JOIN tconsolidacionrelaciongastosdetalle DET WITH (NOLOCK) ON ENC.nFolioConsolidacion = DET.nFolioConsolidacion\r\n");
		queryInsert.append( "JOIN tLayoutsCreadosRelacionGastosHeader L WITH (NOLOCK) ON ENC.nIdIntegracion = L.sAuxiliarComodin\r\n");
		queryInsert.append( "WHERE ENC.nIdIntegracion = '" + nIDIntegracion + "'");
		
		try {
			
			log.debug(queryInsert);
			
			psInsert = conn.prepareStatement(queryInsert.toString());
			int insertados = psInsert.executeUpdate();
			
			log.debug( "Se insertaron " + insertados + " regsitros. " );
	
		} finally {
			CloseObject.closeObject(psInsert);
		}
	
	}

	public static void actualizaRemanentesDevolucion( Connection conn, String nFolio ) throws Exception {
		PreparedStatement ps = null, pst = null, pst2 = null;
		ResultSet rs = null;
		int folioAnticipo;
		Double importe;
		
		try {
			
			String query = "SELECT nFolioComprobacion, mMontoSolicitud FROM tcajaencabezado WHERE nFoliocaja = ?";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, nFolio);			
			
			rs = ps.executeQuery();			

			if (rs.next()) {
				folioAnticipo = rs.getInt(1);
				importe = rs.getDouble(2);
			} else {
				throw new Exception("No se encontro el anticipo");
			}
			
			pst = conn.prepareStatement( "UPDATE tEstadoDeCuentaViaticosEncabezado SET mMontoRemanente = mMontoRemanente - ? WHERE nFolioCaja = ?" );			
			pst.setDouble( 1, importe );
			pst.setInt( 2, folioAnticipo );
			
			pst2 = conn.prepareStatement( "INSERT INTO tEstadoDeCuentaViaticosDetalle (nFolioCaja, nFolioComprobacion, nDocRenglon, fFechaComprobacion, mMontoComprobacion) " + "VALUES (?, ?, (SELECT isnull(MAX(NDOCRENGLON),0) + 1 FROM tEstadoDeCuentaViaticosDetalle WITH (NOLOCK) WHERE nFolioCaja = ?), GETDATE(), ?)" );
			pst2.setInt( 1, folioAnticipo );
			pst2.setString( 2, nFolio );
			pst2.setInt( 3, folioAnticipo );
			pst2.setDouble( 4, importe );

			log.debug( pst );
			pst2.executeUpdate();

			log.debug( pst2 );
			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}
		
	}

}
