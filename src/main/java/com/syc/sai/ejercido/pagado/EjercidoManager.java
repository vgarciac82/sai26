package com.syc.sai.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.syc.ejercido.pagado.Ejercido;
import com.syc.ejercido.pagado.EjercidoDetalle;
import com.syc.ejercido.pagado.EjercidoEncabezado;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class EjercidoManager {

	public static int existeEjercido( Connection conn, String tipoPago, int folioPago) throws Exception{
		String query =   "SELECT  nFolioEjercido  "
						+" FROM   tejercidoencabezado (NOLOCK) "
						+" WHERE  ctipopago = '"+tipoPago+"'  "
						+"        AND nfoliopago = " + folioPago;
		int nFolioEjercido = -1;
		Statement stmnt = null;
		ResultSet rs = null;
		
		try{
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery(query);
			
			if( rs.next() ){
				nFolioEjercido = rs.getInt(1);
			}
			return nFolioEjercido;
		}finally{
			CloseObject.closeObject(rs);
			CloseObject.closeObject(stmnt);
		}
	}

	/**
	 * Lee los componentes de una integracion y los datos complementarios para
	 * generar su ejercido.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param integracion
	 *            Numero de integracion
	 * @param usuario
	 *            Usuario de captura
	 * @param tipoPoliza
	 *            Tipo de poliza a generar.
	 * @return Lista con cada uno de los integrantes de la integracion para su
	 *         ejercido.
	 * @throws Exception
	 */
	public static List<Ejercido> generaEjercidoIntegracion( Connection conn, String integracion, String usuario, String tipoPoliza ) throws Exception{

		List<Ejercido> integradas = new ArrayList<Ejercido>();
		
		String queryEncabezado =  "SELECT  sai.ctipopago,  "
								+"        sai.nfolio,  "
								+"        sai.canocontrarrecibo,  "
								+"        '"+tipoPoliza+"'                    AS cTipoPoliza,  "
								+"        '"+usuario+"'                    AS usuario,  "
								+"        sai.cdescripcionpoliza,  "
								+"        sai.cunidadresponsablecontable,  "
								+"        sicop.nfolioclc,  "
								+"        sai.integracion,  "
								+"        sicop.fechaaplsicop,  "
								+"        sai.cramo,  "
								+"        '"+usuario+"'                    AS usuario,  "
								+"        sicop.fechaaplsicop   AS FechaAplicacionSicop,  "
								+"        sicop.fechapagosicop  AS FechaPagoSicop,  "
								+"        sicop.solicitudpago,  "
								+"        sicop.numproceso      AS NumeroProceso,  "
								+"        sicop.folio_siaff_112 AS nFolioSIAFF  "
								+" FROM   v_aplicarejercidopagadoencabezado sai WITH(nolock)  "
								+"        INNER JOIN dbo.tsicopencabezado sicop WITH(nolock)  "
								+"                ON sai.integracion = sicop.canocontrarrecibo  "
								+" WHERE  integracion = '" + integracion + "'";
		
		String queryDetalle = "SELECT ndocrenglon,  "
							+"        cmes,  "
							+"        cejercicio,  "
							+"        ep,  "
							+"        cidcuentacontable,  "
							+"        mcomprometido,  "
							+"        Isnull(npoliza, 0)    AS nPoliza,  "
							+"        id_tipo_movimiento,  "
							+"        id_tipo_concepto,  "
							+"        cevento,  "
							+"        ccentrocontable,  "
							+"        rfc,  "
							+"        mimporteneto,  "
							+"        alm,  "
							+"        mimportebruto,  "
							+"        mimportemasiva,  "
							+"        mimporteiva,  "
							+"        ncapitulo,  "
							+"        msancion,  "
							+"        mdevolucion,  "
							+"        mimporteamortiza,  "
							+"        mretencion,  "
							+"        mpenalizacion,  "
							+"        m2millar,  "
							+"        m23iva,  "
							+"        misrhonorarios,  "
							+"        mobra5,  "
							+"        mimporteflete4,  "
							+"        misrarrenda,  "
							+"        mretimpuestocedular,  "
							+"        mimporte,  "
							+"        mimporteivaarrenda,  "
							+"        mimporteivahonorarios,  "
							+"        mimporteflete23,  "
							+"        mimporteivaprov,  "
							+"        mimporteobra,  "
							+"        Isnull(mcnic, 0.00)   AS mCNIC,  "
							+"        Isnull(mimdt, 0.00)   AS mIMDT,  "
							+"        Isnull(mtesofe, 0.00) AS mTesofe,  "
							+"        altaalmacen,  "
							+"        ccentrocontable       AS cIdEntidadContable,  "
							+"        cidrelacion,  "
							+"        ctab, " 
							+"		  mImporteISRLaudos,   "
							+"		  cUnidadResponsable "
							+" FROM   v_aplicarejercidopagadodetalle WITH(nolock)  "
							+" WHERE  ctipopago = ?  "
							+"        AND nfolio = ?  "
							+"        AND cevento != 'ANTICIPO'  "
							+"        AND cevento != 'ANTICIPO_DIV'";
		
		Statement stmnt = null;
		PreparedStatement psDetalle =null;
		ResultSet rs = null;
		ResultSet rsDetalle = null;
		
		try{
			psDetalle = conn.prepareStatement(queryDetalle);
			stmnt = conn.createStatement();
			
			rs = stmnt.executeQuery(queryEncabezado);
			while (rs.next()) {

				EjercidoEncabezado encabezado = EjercidoManager.parseResultSetEncabezado(rs);
				List<EjercidoDetalle> detalle = new ArrayList<EjercidoDetalle>();

				psDetalle.setString(1, encabezado.getTipoPago());
				psDetalle.setInt(2, encabezado.getFolioPAGO());
				rsDetalle = psDetalle.executeQuery();

				while (rsDetalle.next()) {
					detalle.add(EjercidoManager.parseDetalle(rsDetalle, encabezado.getTipoPago(), encabezado.getFolioPAGO(), "EJERCIDO"));
				}
				
				psDetalle.clearParameters();
				rsDetalle.close();
				rsDetalle = null;
				integradas.add(new Ejercido(encabezado, detalle));
			}
			return integradas;
		}finally{
			CloseObject.closeObject(rs);
			CloseObject.closeObject(psDetalle);
			CloseObject.closeObject(stmnt);
		}
	}

	private static int insertaDetalle(Connection conn, List<EjercidoDetalle> detalle, PreparedStatement psInsertDet) throws Exception {

		int insertados = 0;
		for (Iterator<EjercidoDetalle> i = detalle.iterator(); i.hasNext();) {
			EjercidoDetalle detInsert = i.next();
			psInsertDet.setString(1, detInsert.getTipoPago());
			psInsertDet.setInt(2, detInsert.getFolioPAGO());
			psInsertDet.setInt(3, detInsert.getDocRenglon());
			psInsertDet.setInt(4, detInsert.getnMes());
			psInsertDet.setInt(5, detInsert.getEjercicioFiscal());
			psInsertDet.setString(6, detInsert.getIdEntidadContable());
			psInsertDet.setString(7, detInsert.getIdRelacion());
			psInsertDet.setString(8, detInsert.getEP());
			psInsertDet.setString(9, detInsert.getIdCuentaContable());
			psInsertDet.setBigDecimal(10, detInsert.getImporteComprometido());
			psInsertDet.setInt(11, detInsert.getPoliza());
			psInsertDet.setString(12, detInsert.getIdTipoMovimiento());
			psInsertDet.setString(13, detInsert.getIdTipoConcepto());
			psInsertDet.setString(14, detInsert.getEvento());
			psInsertDet.setInt(15, detInsert.getEjercicioFiscal());
			psInsertDet.setString(16, detInsert.getCentroContable());
			psInsertDet.setInt(17, detInsert.getnMes());
			psInsertDet.setString(18, detInsert.getRFC());
			psInsertDet.setBigDecimal(19, detInsert.getImporteNeto());
			psInsertDet.setString(20, detInsert.getALM());
			psInsertDet.setBigDecimal(21, detInsert.getImporteBruto());
			psInsertDet.setBigDecimal(22, detInsert.getImporteMasIva());
			psInsertDet.setBigDecimal(23, detInsert.getImporteIva());
			psInsertDet.setInt(24, detInsert.getCapitulo());
			psInsertDet.setString(25, null);
			psInsertDet.setInt(26, -1);
			psInsertDet.setString(27, null);
			psInsertDet.setBigDecimal(28, detInsert.getImporteSancion());
			psInsertDet.setBigDecimal(29, detInsert.getImporteDevolucion());
			psInsertDet.setBigDecimal(30, detInsert.getImporteAmortiza());
			psInsertDet.setBigDecimal(31, detInsert.getImporteRetencion());
			psInsertDet.setBigDecimal(32, detInsert.getImportePenalizacion());
			psInsertDet.setBigDecimal(33, detInsert.getImporte2Millar());
			psInsertDet.setBigDecimal(34, detInsert.getImporte23IVA());
			psInsertDet.setBigDecimal(35, detInsert.getImporteISRHonorarios());
			psInsertDet.setBigDecimal(36, detInsert.getImporteObra5());
			psInsertDet.setBigDecimal(37, detInsert.getImporteFlete4());
			psInsertDet.setBigDecimal(38, detInsert.getImporteISRArrenda());
			psInsertDet.setBigDecimal(39, detInsert.getImporteRetImpuestoCedular());
			psInsertDet.setBigDecimal(40, detInsert.getImporteBruto());
			psInsertDet.setBigDecimal(41, detInsert.getImporteAmortiza());
			psInsertDet.setBigDecimal(42, null);
			psInsertDet.setBigDecimal(43, null);
			psInsertDet.setBigDecimal(44, detInsert.getImporteObra5());
			psInsertDet.setBigDecimal(45, detInsert.getImporteFlete4());
			psInsertDet.setBigDecimal(46, detInsert.getImporteRetImpuestoCedular());
			psInsertDet.setBigDecimal(47, detInsert.getImporteNeto());
			psInsertDet.setBigDecimal(48, detInsert.getImporteIvaArrenda());
			psInsertDet.setBigDecimal(49, detInsert.getImporteIvaHonorarios());
			psInsertDet.setBigDecimal(50, detInsert.getImporteFlete23());
			psInsertDet.setBigDecimal(51, detInsert.getImporteIvaProv());
			psInsertDet.setBigDecimal(52, detInsert.getImporteObra());
			psInsertDet.setBigDecimal(53, detInsert.getImporteCNIC());
			psInsertDet.setBigDecimal(54, detInsert.getImporteIMDT());
			psInsertDet.setBigDecimal(55, detInsert.getImporteTesofe());
			psInsertDet.setString(56, detInsert.getAltaAlmacen());
			psInsertDet.setInt(57, detInsert.getFolioEjercido());
			psInsertDet.setString(58, "N");
			psInsertDet.setString(59, "N");
			psInsertDet.setBigDecimal(60, detInsert.getImporteISRLaudos());
			psInsertDet.setString(61, detInsert.getcUnidadResponsable());

			insertados += psInsertDet.executeUpdate();
		}

		return insertados;
	}

	public static int insertaEjercido(Connection conn, List<Ejercido> ejercer) throws Exception {
		String queryInsertEnc =  "INSERT INTO tejercidoencabezado  "
								+"             (ctipopago,  "
								+"              nfoliopago,  "
								+"              canocontrarrecibo,  "
								+"              ctipopoliza,  "
								+"              u_login,  "
								+"              faplicacion,  "
								+"              cdescripcionpoliza,  "
								+"              cunidadresponsablecontable,  "
								+"              nfoliosicop,  "
								+"              cramo,  "
								+"              cidusuariocaptura,  "
								+"              fechaaplicacionsicop,  "
								+"              fechapagosicop,  "
								+"              solicitudpago,  "
								+"              numeroproceso,  "
								+"              nfoliosiaff,  "
								+"              nfolioejercido," 
								+"              nFolioPoliza  )  "
								+" VALUES      ( ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 "
								+"               ?,	 " 
								+"               ?"
								+" ) ";
		String queryInsertDet =  "INSERT INTO tEjercidoDetalle "
								+"         ( cTipoPago , "				//1 
								+"           nFolioPAGO , "				//2
								+"           nDocRenglon , "			//3
								+"           nMes , "					//4
								+"           cEjercicio , "				//5
								+"           cIdEntidadContable , "		//6
								+"           cIdRelacion , "			//7			
								+"           EP , "						//8
								+"           cIdCuentaContable , "		//9
								+"           mComprometido , "			//10
								+"           nPoliza , "				//11
								+"           ID_TIPO_MOVIMIENTO , "		//12
								+"           ID_TIPO_CONCEPTO , "		//13
								+"           cEvento , "				//14
								+"           aEjercicioFiscal , "		//15
								+"           cCentroContable , "		//16
								+"           cMes , "					//17
								+"           RFC , "					//18
								+"           mImporteNeto , "			//19
								+"           ALM , "					//20
								+"           mImporteBruto , "			//21
								+"           mImporteMasIva , "			//22
								+"           mImporteIva , "			//23
								+"           nCapitulo , "				//24
								+"           cDocumentoHaplicado , "	//25
								+"           nFolioPoliza , "			//26
								+"           cTipoPoliza , "			//27
								+"           mSancion , "				//28
								+"           mDevolucion , "			//29
								+"           mImporteAmortiza , "		//30
								+"           mRetencion , "				//31
								+"           mPenalizacion , "			//32
								+"           m2Millar , "				//33
								+"           m23IVA , "					//34
								+"           mISRHonorarios , "			//35
								+"           mObra5 , "					//36
								+"           mImporteFlete4 , "			//37
								+"           mISRArrenda , "			//38
								+"           mRetImpuestoCedular , "	//39
								+"           mBruto , "					//40
								+"           mAmortizacionAnticipo , "	//41
								+"           mIVA , "					//42
								+"           mNeto , "					//43
								+"           m5Millar , "				//44
								+"           mFletes , "				//45
								+"           mCedular , "				//46
								+"           mImporte , "				//47
								+"           mImporteIvaArrenda , "		//48
								+"           mImporteIvaHonorarios , "	//49
								+"           mImporteFlete23 , "		//50
								+"           mImporteIvaProv , "		//51
								+"           mImporteObra , "			//52
								+"           mCNIC , "					//53
								+"           mIMDT , "					//54
								+"           mTesofe , "				//55
								+"           altaAlmacen , "			//56
								+"           nFolioEjercido , "			//57
								+"           Periodo13 , "				//58
								+"           ADEFAS , "					//59
								+"           mImporteISRLaudos, "		//60
								+"			 cUnidadResponsable "		//61			
								+"         ) "
								+" VALUES  ( ? , 	 "                //1   
								+"           ? , 	 "                //2   
								+"           ? , 	 "                //3   
								+"           ? , 	 "                //4   
								+"           ? , 	 "                //5   
								+"           ? , 	 "                //6   
								+"           ? , 	 "                //7		
								+"           ? , 	 "                //8   
								+"           ? , 	 "                //9   
								+"           ? , 	 "                //10  
								+"           ? , 	 "                //11  
								+"           ? , 	 "                //12  
								+"           ? , 	 "                //13  
								+"           ? , 	 "                //14  
								+"           ? , 	 "                //15  
								+"           ? , 	 "                //16  
								+"           ? , 	 "                //17  
								+"           ? , 	 "                //18  
								+"           ? , 	 "                //19  
								+"           ? , 	 "                //20  
								+"           ? , 	 "                //21  
								+"           ? , 	 "                //22  
								+"           ? , 	 "                //23  
								+"           ? , 	 "                //24  
								+"           ? , 	 "                //25  
								+"           ? , 	 "                //26  
								+"           ? , 	 "                //27  
								+"           ? , 	 "                //28  
								+"           ? , 	 "                //29  
								+"           ? , 	 "                //30  
								+"           ? , 	 "                //31  
								+"           ? , 	 "                //32  
								+"           ? , 	 "                //33  
								+"           ? , 	 "                //34  
								+"           ? , 	 "                //35  
								+"           ? , 	 "                //36  
								+"           ? , 	 "                //37  
								+"           ? , 	 "                //38  
								+"           ? , 	 "                //39  
								+"           ? , 	 "                //40  
								+"           ? , 	 "                //41  
								+"           ? , 	 "                //42  
								+"           ? , 	 "				  //43 
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ? , 	 "
								+"           ?  	 "
								+"         )";
		
		PreparedStatement psInsertEnc = null;
		PreparedStatement psInsertDet = null;
		int insertados = 0;
		
		try {

			psInsertEnc = conn.prepareStatement(queryInsertEnc);
			psInsertDet = conn.prepareStatement(queryInsertDet);

			for (Iterator<Ejercido> i = ejercer.iterator(); i.hasNext();) {
				
				Ejercido ejercido = i.next();
				int nFolioEjercido = existeEjercido(conn, ejercido.getEncabezado().getTipoPago(), ejercido.getEncabezado().getFolioPAGO() );
				if( nFolioEjercido > 0 ){
					ejercido.setFolioEjercido(nFolioEjercido);
					continue;
				}

				nFolioEjercido = EjercidoPagadoManager.folioSiguiente(conn, "EJERCIDO");
				ejercido.setFolioEjercido(nFolioEjercido);
				
				insertados += EjercidoManager.insertaEncabezado( conn, ejercido.getEncabezado(), psInsertEnc);
				insertados += EjercidoManager.insertaDetalle( conn, ejercido.getDetalle(), psInsertDet );
				
				psInsertDet.clearParameters();
				psInsertEnc.clearParameters();
			}
			
			return insertados;
		} finally {
			CloseObject.closeObject(psInsertDet);
			CloseObject.closeObject(psInsertEnc);
		}

	}

	private static int insertaEncabezado(Connection conn, EjercidoEncabezado encabezado, PreparedStatement psInsertEnc) throws Exception {

		psInsertEnc.setString(1, encabezado.getTipoPago());
		psInsertEnc.setInt(2, encabezado.getFolioPAGO());
		psInsertEnc.setString(3, encabezado.getCaNoContrarrecibo());
		psInsertEnc.setString(4, encabezado.getTipoPoliza());
		psInsertEnc.setString(5, encabezado.getLogin());
		psInsertEnc.setDate(6, new java.sql.Date(Util.stringToDate(encabezado.getfAplicacion(), "dd/MM/yyyy").getTime()));
		psInsertEnc.setString(7, encabezado.getDescripcionPoliza());
		psInsertEnc.setString(8, encabezado.getUnidadResponsableContable());
		psInsertEnc.setInt(9, encabezado.getFolioSICOP());
		psInsertEnc.setString(10, encabezado.getRamo());
		psInsertEnc.setString(11, encabezado.getLogin());
		psInsertEnc.setDate(12, new java.sql.Date(Util.stringToDate(encabezado.getFechaAplicacionSicop(), "dd/MM/yyyy").getTime()));
		psInsertEnc.setDate(13, new java.sql.Date(Util.stringToDate(encabezado.getFechaPagoSicop(), "dd/MM/yyyy").getTime()));
		psInsertEnc.setString(14, encabezado.getSolicitudPago());
		psInsertEnc.setString(15, encabezado.getNumeroProceso());
		psInsertEnc.setInt(16, encabezado.getFolioSIAFF());
		psInsertEnc.setInt(17, encabezado.getFolioEjercido());
		psInsertEnc.setInt(18, encabezado.getFolioPoliza());

		return psInsertEnc.executeUpdate();
	}

	/**
	 * Genera un objeto <code>EjercidoDetalle</code> leyendo los datos desde un
	 * <code>ResultSet</code>
	 * 
	 * @param rs
	 *            ResultSet abierto
	 * @param tipoPago
	 *            Tipo de pago
	 * @param folioPago
	 *            Folio de pago
	 * @param evento
	 *            Evento a aplicar
	 * @return {@link com.syc.ejercido.pagado.EjercidoDetalle Detalle} .
	 * @throws Exception
	 */
	private static EjercidoDetalle parseDetalle(ResultSet rs, String tipoPago, int folioPago, String evento) throws Exception {

		EjercidoDetalle detalle = new EjercidoDetalle();

		detalle.setTipoPago(tipoPago);
		detalle.setFolioPAGO(folioPago);
		detalle.setDocRenglon(rs.getInt("nDocRenglon"));
		detalle.setcMes(rs.getInt("cMes"));
		detalle.setEjercicioFiscal(rs.getInt("cEjercicio"));
		detalle.setIdEntidadContable(rs.getString("cIdEntidadContable"));
		detalle.setIdRelacion(rs.getString("cIdRelacion"));
		detalle.setEP(rs.getString("EP"));
		detalle.setIdCuentaContable(rs.getString("cIdCuentaContable"));
		detalle.setImporteComprometido(rs.getBigDecimal("mComprometido"));
		detalle.setPoliza(rs.getInt("nPoliza"));
		detalle.setIdTipoMovimiento(rs.getString("ID_TIPO_MOVIMIENTO"));
		detalle.setIdTipoConcepto(rs.getString("ID_TIPO_CONCEPTO"));
		detalle.setEvento(evento);
		detalle.setnMes(rs.getInt("cMes"));
		detalle.setCentroContable(rs.getString("cCentroContable"));
		detalle.setRFC(rs.getString("RFC"));
		detalle.setImporteNeto(rs.getBigDecimal("mImporteNeto"));
		detalle.setALM(rs.getString("ALM"));
		detalle.setImporteBruto(rs.getBigDecimal("mImporteBruto"));
		detalle.setImporteMasIva(rs.getBigDecimal("mImporteMasIva"));
		detalle.setImporteIva(rs.getBigDecimal("mImporteIva"));
		detalle.setCapitulo(rs.getInt("nCapitulo"));
		detalle.setImporteSancion(rs.getBigDecimal("mSancion"));
		detalle.setImporteDevolucion(rs.getBigDecimal("mDevolucion"));
		detalle.setImporteAmortiza(rs.getBigDecimal("mImporteAmortiza"));
		detalle.setImporteRetencion(rs.getBigDecimal("mRetencion"));
		detalle.setImportePenalizacion(rs.getBigDecimal("mPenalizacion"));
		detalle.setImporte2Millar(rs.getBigDecimal("m2Millar"));
		detalle.setImporte23IVA(rs.getBigDecimal("m23IVA"));
		detalle.setImporteISRHonorarios(rs.getBigDecimal("mISRHonorarios"));
		detalle.setImporteObra5(rs.getBigDecimal("mObra5"));
		detalle.setImporteFlete4(rs.getBigDecimal("mImporteFlete4"));
		detalle.setImporteISRArrenda(rs.getBigDecimal("mISRArrenda"));
		detalle.setImporteRetImpuestoCedular(rs.getBigDecimal("mRetImpuestoCedular"));
		detalle.setImporteIvaArrenda(rs.getBigDecimal("mImporteIvaArrenda"));
		detalle.setImporteIvaHonorarios(rs.getBigDecimal("mImporteIvaHonorarios"));
		detalle.setImporteFlete23(rs.getBigDecimal("mImporteFlete23"));
		detalle.setImporteIvaProv(rs.getBigDecimal("mImporteIvaProv"));
		detalle.setImporteObra(rs.getBigDecimal("mImporteObra"));
		detalle.setImporteCNIC(rs.getBigDecimal("mCNIC"));
		detalle.setImporteTesofe(rs.getBigDecimal("mTesofe"));
		detalle.setAltaAlmacen(rs.getString("altaAlmacen"));
		detalle.setImporteIMDT(rs.getBigDecimal("mIMDT"));
		detalle.setImporte(rs.getBigDecimal("mImporte"));
		detalle.setCTAB(rs.getString("CTAB"));
		detalle.setImporteISRLaudos( rs.getBigDecimal("mImporteISRLaudos") );
		return detalle;
	}

	/**
	 * Genera un objeto <code>EjercidoEncabezado</code> leyendo los datos desde
	 * un <code>ResultSet</code>
	 * 
	 * @param rs
	 *            ResultSet abierto
	 * @return {@link com.syc.ejercido.pagado.EjercidoEncabezado Encabezado} .
	 * @throws Exception
	 */
	private static EjercidoEncabezado parseResultSetEncabezado(ResultSet rs) throws Exception{
		
		EjercidoEncabezado encabezado = new EjercidoEncabezado();
		
		encabezado.setTipoPago(rs.getString("cTipoPago"));
		encabezado.setFolioPAGO(rs.getInt("nFolio"));
		encabezado.setCaNoContrarrecibo(rs.getString("caNoContrarrecibo"));
		encabezado.setTipoPoliza(rs.getString("cTipoPoliza"));
		encabezado.setLogin(rs.getString("usuario"));
		encabezado.setDescripcionPoliza(rs.getString("cDescripcionPoliza"));
		encabezado.setUnidadResponsableContable(rs.getString("cUnidadResponsableContable"));
		encabezado.setFolioSICOP(rs.getInt("nFolioCLC"));
		
		try{
			String fAplicacionEjercido = rs.getString("FechaAplSICOP");
			
			Calendar c = new GregorianCalendar(  );
			c.setTime(Util.stringToDate(fAplicacionEjercido, "dd/MM/yyyy"));
			
			EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
			int efActivo = Integer.parseInt( efbl.getEjercicioFiscalActivo().getaEjercicioFiscal() );
			
			if( efActivo != c.get(Calendar.YEAR) )
				throw new Exception("El año en la fecha de aplicacion " + fAplicacionEjercido +  " para el contrarrecibo " + rs.getString("caNoContrarrecibo") + " no corresponde al ejercicio fiscal activo " + efActivo );
			else
				encabezado.setfAplicacion(rs.getString("FechaAplSICOP"));
		}catch(Exception e){
			throw e;
		}
		
		
		encabezado.setRamo(rs.getString("cRamo"));
		encabezado.setFechaAplicacionSicop(rs.getString("FechaAplicacionSicop"));
		encabezado.setFechaPagoSicop(rs.getString("FechaPagoSicop"));
		encabezado.setSolicitudPago(rs.getString("SolicitudPago"));
		encabezado.setNumeroProceso(rs.getString("NumeroProceso"));
		encabezado.setFolioSIAFF(rs.getInt("nFolioSIAFF"));
		encabezado.setFolioPoliza(0);
		
		return encabezado;
	}
}
