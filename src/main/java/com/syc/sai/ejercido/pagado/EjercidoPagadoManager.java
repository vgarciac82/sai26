package com.syc.sai.ejercido.pagado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.Ejercido;
import com.syc.ejercido.pagado.Pago;
import com.syc.gestion.implementacion.AltaProveedorImpl;
import com.syc.gestion.util.Condicion;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class EjercidoPagadoManager extends DataSourceManager {
	private static final Logger	log	= Logger.getLogger(AltaProveedorImpl.class);

	/**
	 * Valida que la integracion exista ya en la informacion obtenida de
	 * SICOP/SIAFF
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param idIntegracion
	 *            Numero de integracion
	 * @return true si y solo si existe un registro con el mismo numero de
	 *         integracion en SICOP/SIAFF
	 * @throws Exception
	 */
	public static boolean existeIntegracionSICOP(Connection conn, String idIntegracion) throws Exception{
		String queryExiste = "SELECT Count(*) AS existe " 
							+ "FROM   tsicopencabezado " 
							+ "WHERE  canocontrarrecibo = '"+idIntegracion+"' " 
							+ "       AND aplicacion_contable = 1 ";
		Statement stmnt = null;
		ResultSet rs = null;
		boolean existe = false;
		try{
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(queryExiste);
			if(rs.next())
				existe = rs.getInt(1) > 0;
			return existe;	
		}finally{
			CloseObject.closeObject(stmnt);
			CloseObject.closeObject(rs);
		}
		
	}

	/**
	 * Obtiene el valor siguiente de una secuencia.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param name
	 *            Nombre de la secuencia.
	 * @return Secuencia siguiente
	 * @throws Exception
	 */
	public static int folioSiguiente(Connection conn, String name) throws Exception {
		int retVal = 1;
		PreparedStatement psUpdate = null, psSelect = null;
		ResultSet rs = null;

		try {
			// Bloqueamos el registro incrementando al nuevo valor
			psUpdate = conn.prepareStatement("UPDATE cf_sequence WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = ?");
			psUpdate.setString(1, name);
			psUpdate.executeUpdate();
			// Recuperamos el nuevo valor
			psSelect = conn.prepareStatement("SELECT seq_value FROM cf_sequence WITH (NOLOCK) WHERE seq_name = ?");
			psSelect.setString(1, name);

			rs = psSelect.executeQuery();
			if (rs.next()) {
				retVal = rs.getInt("seq_value");
			} else {
				// Si no existe creamos el registro
				insert(conn, name, retVal);
			}

		} finally {
			CloseObject.closeObject(psSelect);
			CloseObject.closeObject(psUpdate);

		}

		return retVal;
	}

	/**
	 * Inserta una secuencia en el valor especificado en value;
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param name
	 *            Nombre de la secuencia
	 * @param value
	 *            Valor inicial de la sequencia.
	 * @throws Exception
	 */
	private static void insert(Connection conn, String name, int value) throws Exception {

		PreparedStatement psInsert = null;

		try {
			psInsert = conn.prepareStatement("INSERT INTO cf_sequence (seq_name, seq_value) VALUES (?, ?)");
			psInsert.setString(1, name);
			psInsert.setInt(2, value);

			psInsert.executeUpdate();
		} finally {
			CloseObject.closeObject(psInsert);
		}

	}
	
	/**
	 * 
	 * @param conn
	 * @param integracion
	 * @return
	 */
	public static List<Ejercido> insertaInfoEjercido(Connection conn, String integracion, String usuario, String tipoPoliza) throws Exception{
		List<Ejercido> ejercer = EjercidoManager.generaEjercidoIntegracion(conn, integracion, usuario, tipoPoliza);
		int insertados = EjercidoManager.insertaEjercido( conn, ejercer );
		log.info("Se insertaron " + insertados + " ejercidos");
		return ejercer;
	}

	public static List<Pagado> insertaInfoPagado(Connection conn, String integracion, String usuario, String tipoPoliza) throws Exception {
		List<Pagado> pagar = PagadoManager.generaPagadoIntegracion(conn, integracion, usuario, tipoPoliza);
		int insertados = PagadoManager.insertaPagado(conn, pagar);

		log.info("Se insertaron " + insertados + " pagados");
		return pagar;
	}
	
	/**
	 * Inserta en la bitacora si existe algun error en la aplicacion contable.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param cTipoPago
	 *            Tipo de pago
	 * @param nFolioPago
	 *            Folio del pago
	 * @param strCaNoContrarrecibo
	 *            Cuenta por pagar
	 * @param clcSicop
	 *            Numero de CLC SICOP
	 * @param fechaAplicacion
	 *            Fecha de aplicacion
	 * @param EP
	 *            Estructura programatica en la que se encontro el error.
	 * @param importe
	 *            Importe de la solicitud de pago.
	 * @param tipoDocumento
	 *            Ejercido o Pagado
	 * @param descripcion
	 *            Descripcion del error
	 * @throws SQLException
	 */
	public static void insertaLog(Connection conn, String cTipoPago, int nFolioPago, String strCaNoContrarrecibo, String clcSicop, String fechaAplicacion, String EP, double importe, String tipoDocumento, String descripcion) throws SQLException {
		String queryInsert = "INSERT INTO tDetalleEjercidoPagado ( cTipoPago, " 
						   + "                                     nFolioPago, " 
						   + "                                     caNoContrarrecibo, " 
						   + "                                     clcSicop, "
						   + "                                     fechaAplicacion, " 
						   + "                                     EP, " 
						   + "                                     importe, " 
						   + "                                     tipoDocumento, " 
						   + "                                     descripcion) "
						   + "                              VALUES( '" 
						   + cTipoPago 
						   + "', " 
						   + nFolioPago 
						   + ", '" 
						   + strCaNoContrarrecibo 
						   + "', '" 
						   + clcSicop 
						   + "', '" 
						   + fechaAplicacion 
						   + "', '" 
						   + EP 
						   + "', " 
						   + importe 
						   + ", '" 
						   + tipoDocumento 
						   + "', '" 
						   + descripcion 
						   + "') ";
		Statement stmnt = null;
		try {
			stmnt = conn.createStatement();
			stmnt.executeUpdate(queryInsert);
		} finally {
			CloseObject.closeObject(stmnt);
		}
	}

	/**
	 * Valida que los montos de la integracion en SAI sean exactamente iguales a
	 * los reportados en SICOP/SIAFF
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param integracion
	 *            Numero de integracion a validar.
	 * @return <code>true</code> si y solo si el monto total de la integracion
	 *         en SAI es igual al monto de la integracion en SICOP/SIAFF
	 */
	public static boolean montosCorrectos(Connection conn, String integracion) throws Exception {
		BigDecimal montoSICOP = obtenMontoIntegracionSicop(conn, integracion);
		BigDecimal montoSAI = obtenMontoIntegracionSAI(conn, integracion);
		return montoSICOP.compareTo(montoSAI) == 0;
	}
	
	public static List<Pago> obtenerPagosProcesar(Connection conn, List<Condicion> condiciones) throws Exception {
		log.trace("Iniciando carga de pagos para Ejercido/Pagado");
		
		String querySelect =  "SELECT ctipopago,  " 
							+ "       Sum(impneto) AS impneto, " 
							+ "       ejercido,  " 
							+ "       pagado,  " 
							+ "       integracion,  " 
							+ "       foliopagado  " 
							+ "FROM   v_aplicarejercidopagadoencabezado WITH(nolock) " 
							+ "WHERE  pagado != 'pagado' ";
		ResultSet rs = null;
		Statement stmnt = null;
		List<Pago> resultado = new ArrayList<Pago>();
		String token = " AND ";
		String groupBy = " Group By cTipoPago, ejercido, pagado, integracion, folioPagado";
		String orderBy = " ORDER BY integracion";

		try {
			
			for (Iterator<Condicion> i = condiciones.iterator(); i.hasNext();) {
				Condicion condicion = i.next();
				log.trace("Agregando condicion: " + condicion );
				querySelect += token + condicion.getNombreCampo() + " " + condicion.getOperador() + " " + condicion.getValor();
			}
			
			querySelect += groupBy + orderBy;
			stmnt = conn.createStatement();
			log.debug( "Se ejecutara: " + querySelect  );
			
			rs = stmnt.executeQuery(querySelect);
			log.trace("Consulta ejecutada.");
			
			while (rs.next()) {
				Pago p = new Pago();
				p.setEstatusEjercido(rs.getString("ejercido"));
				p.setEstatusPagado(rs.getString("pagado"));
				p.setFolioPagado(rs.getInt("foliopagado"));
				p.setImporteNeto(rs.getBigDecimal("impneto"));
				p.setIntegracion(rs.getString("integracion"));
				p.setTipoPago(rs.getString("ctipopago"));
				resultado.add(p);
			}
			log.trace("Se termina carga de pagos para Ejercido/Pagado");
			return resultado;
			
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(stmnt);
		}
	}
	
	/**
	 * Regresa el monto total de la integracion en SAI.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param integracion
	 *            Numero de integracion
	 * @return Monto total de la integracion en SAI
	 * @throws Exception
	 */
	public static BigDecimal obtenMontoIntegracionSAI(Connection conn, String integracion) throws Exception{
		String query =  "SELECT Sum(impneto)AS mTotal  "
						+" FROM   v_aplicarejercidopagadoencabezado WITH(nolock) " 
						+" WHERE  integracion = '"+integracion +"' "
						+" GROUP  BY integracion ";
		Statement stmnt = null;
		ResultSet rs = null;
		BigDecimal montoSAI = new BigDecimal(0.0);
		montoSAI.setScale(2, RoundingMode.HALF_UP);
		try {
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);

			if (rs.next())
				montoSAI = rs.getBigDecimal(1);
			return montoSAI;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(stmnt);
		}

	}
	
	/**
	 * Regresa el monto total de la integracion en SICOP/SIAFF.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param integracion
	 *            Numero de integracion
	 * @return Monto total de la integracion reportada en SICOP/SIAGG
	 * @throws Exception
	 */
	public static BigDecimal obtenMontoIntegracionSicop(Connection conn, String integracion) throws Exception {
		String query = "SELECT	SUM(convert(money,IMP_NETO_107)) as totalSicop " 
					 + "  FROM	CLC_SICOP WITH(NOLOCK) " 
					 + " WHERE	NCTR_47 = '" + integracion + "' " 
					 + "   AND	FOLIO_SIAFF_112 <> '0'  " 
					 + "   AND DOC_HAPLICADO=1 ";
		Statement stmnt = null;
		ResultSet rs = null;
		BigDecimal total = new BigDecimal(0);
		total.setScale(2, RoundingMode.HALF_UP);
		try{
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);
			if(rs.next())
				total = rs.getBigDecimal(1);
			return total;
		}finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(stmnt);
		}

	}

	public static List<String> validaDetallePago(Connection conn, String integracion ) throws Exception{
		String query =   "SELECT ejercidoSai.integracion,  "
						+"        ejercidoSai.ep,  "
						+"        ejercidoSai.mimporteneto          AS importeSAI,  "
						+"        Isnull(ejercidoSICOP.mimporte, 0) AS importeSICOP  "
						+" FROM   (SELECT ejercidoEncabezado.integracion,  "
						+"                Substring( dbo.CambiaEPPlurianual( ejercidoDetalle.ep ), 1, 55) AS EP,  "
						+"                Sum(ejercidoDetalle.mimporteneto)    AS mImporteNeto  "
						+"         FROM   v_aplicarejercidopagadoencabezado ejercidoEncabezado WITH(nolock)  "
						+"                INNER JOIN v_aplicarejercidopagadodetalle ejercidoDetalle WITH(  "
						+"                           nolock)  "
						+"                        ON ejercidoEncabezado.ctipopago =  "
						+"                           ejercidoDetalle.ctipopago  "
						+"                           AND ejercidoEncabezado.nfolio = ejercidoDetalle.nfolio  "
						+"         GROUP  BY ejercidoEncabezado.integracion,  "
						+"                   Substring( dbo.CambiaEPPlurianual( ejercidoDetalle.ep ), 1, 55) ) AS ejercidoSai  "
						+"        LEFT OUTER JOIN (SELECT sicopEncabezado.canocontrarrecibo,  "
						+"                                sicopDetalle.ep,  "
						+"                                SUM(sicopDetalle.mimporte ) mimporte  "
						+"                         FROM   dbo.tsicopencabezado sicopEncabezado WITH(nolock)  "
						+"                                INNER JOIN dbo.tsicopdetalle sicopDetalle WITH(  "
						+"                                           nolock)  "
						+"                                        ON sicopEncabezado.nfolioclc =  "
						+"                                           sicopDetalle.nfolioclc "
						+"						   GROUP BY sicopEncabezado.canocontrarrecibo, sicopDetalle.ep"
						+"                         ) AS  "
						+"                        ejercidoSICOP  "
						+"                     ON ejercidoSai.integracion = ejercidoSICOP.canocontrarrecibo  "
						+"                        AND ejercidoSai.ep = ejercidoSICOP.ep  "
						+" WHERE  ejercidoSai.integracion = '" + integracion + "'  "
						+"        AND ejercidoSai.mimporteneto <> Isnull(ejercidoSICOP.mimporte, 0) ";
		Statement stmnt = null;
		ResultSet rs = null;
		List<String> resultado = new ArrayList<String>();
		
		try{
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);
			
			while(rs.next()){
				resultado.add("La EP " + rs.getString("ep") + " no coinicide en monto en SICOP. Monto SAI[" + Util.formatNumber( rs.getDouble("importeSAI") ) + "] Monto SICOP[" + Util.formatNumber(rs.getDouble("importeSICOP")) + "]" );
			}
			return resultado;
		}finally{
			CloseObject.closeObject(rs);
			CloseObject.closeObject(stmnt);
		}
	}

	public float montoSolicitud(String folio) throws SQLException {
		float monto = 0;
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = getConnection();
			String query;

			if (retencionSICOP(folio, conn)) {
				query = "SELECT SUM(mImporteNeto) as monto,nFolioRELACIONGASTOS as monto FROM tRELACIONGASTOSDetalle (NOLOCK) WHERE nFolioRELACIONGASTOS = " + folio + " GROUP BY nFolioRELACIONGASTOS ";
			} else {
				query = "SELECT SUM(mImporteMasIva) as monto,nFolioRELACIONGASTOS as monto FROM tRELACIONGASTOSDetalle (NOLOCK) WHERE nFolioRELACIONGASTOS = " + folio + " GROUP BY nFolioRELACIONGASTOS ";
			}

			ps = conn.prepareStatement(query);
			log.debug(ps);

			rs = ps.executeQuery();

			if (rs.next()) {
				monto = rs.getFloat("monto");
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}

		}

		return monto;
	}

	public boolean retencionSICOP(String folio, Connection conn) throws SQLException {
		boolean retSICOP = false;

		PreparedStatement ps = null;
		ResultSet rs = null;
		String cRetSICOP = "";
		try {

			conn = getConnection();

			String query = "SELECT cRetSICOP FROM tComprobacionLaudos (NOLOCK) WHERE nFolioRELACIONGASTOS = " + folio;
			ps = conn.prepareStatement(query);

			log.debug(ps);

			rs = ps.executeQuery();

			if (rs.next()) {
				cRetSICOP = rs.getString("cRetSICOP");
			}

			if ("S".equalsIgnoreCase(cRetSICOP)) {
				retSICOP = true;
				log.debug("Retenciones en SICOP");
			} else {
				retSICOP = false;
				log.debug("Pagar Retenciones");
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}

		}

		return retSICOP;
	}

	/**
	 * Indica si una integracion de laudos es de pago a de laudos devengado.
	 * Analisa el encabezado de las relaciones de gastos que forman parte de la
	 * integracion. Si el destino del gasto termina es CLRE entonces la relacion
	 * de gastos incluye al menos un pago devengado por lo que solo debera
	 * aplicarse la integracion y no cada una de sus integrantes.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param integracion
	 *            Folio de Integracion
	 * @return <code>true</code> Si en el detalle de las integradas se utilizo
	 *         el destino de gasto "CLRE" <code>false</code> en otro caso.
	 */
	public static boolean esLaudoDevengado(Connection conn, String integracion) throws Exception {

			String query = "SELECT	Count(*) AS total "
				 		 + "  FROM  tlayoutscreadosrelaciongastosheader layouts WITH (nolock) " 
				 		 + "      	INNER JOIN " 
				 		 + "		trelaciongastosencabezado encabezado WITH (nolock)  "
				 		 + "		ON layouts.snocontrarrecibo = encabezado.canocontrarrecibo  "
				 		 + " WHERE 	id_destino_gasto IN ( 'CLRE','GLRE' )  "
				 		 + "   AND	layouts.sauxiliarcomodin = ?";
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = conn.prepareStatement(query);
				ps.setString(1, integracion);

				rs = ps.executeQuery();

				if (rs.next()) {
					int total = rs.getInt("total");
					return total > 0;
				} else
					return false;

			} finally {
				CloseObject.closeObject(ps, false);
				CloseObject.closeObject(rs, false);
			}
	}
	
	/**
	 * Devuelve el estatus de una integracion. Una solicitud tiene varios
	 * estatus en SICOP/SIAFF los que importan para SAI son: Pagada y
	 * Autorizador Ramo que se asume como ejercido
	 * 
	 * @param conn
	 *            Conexion activa a base de datos.
	 * @param integracion
	 *            Numero de integracion
	 * @return El estatus de la integracion en SICOP/SIAFF
	 * @throws Exception
	 */
	public static String getEstatusIntegracion(Connection conn, String integracion) throws Exception {

		String query = "SELECT	ESTATUS_CLC " 
					 + "  FROM	CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " 
					 + " WHERE	SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC  " 
					 + "   AND	NCTR_47 = '" + integracion + "' "
					 + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SIAFF.FECHA_PAGO, SIAFF.ESTATUS_CLC";

		ResultSet rs = null;
		Statement stmnt = null;
		String estatus = null;
		try {

			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);

			if (rs.next()) {
				estatus = rs.getString(1);
			}

			return estatus;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(stmnt);
		}
	}
}
