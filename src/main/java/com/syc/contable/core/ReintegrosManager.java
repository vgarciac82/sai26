package com.syc.contable.core;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReintegrosManager {

	public static final Logger	log	= Logger.getLogger(ReintegrosManager.class);

	public ReintegrosManager() {
		super();
	}

	public static boolean insertaDetReintegroPaso(Connection conn, int folio, ReintegroDetalle datos, String evento, String cCentro, int consecutivo, String folioDep) throws SQLException {
		PreparedStatement pstmnt = null;
		PreparedStatement pstmntIns = null;
		ResultSet rs = null;
		boolean retval = false;
		try {
			String queryRFC = "SELECT ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.nDocRenglon=?";
			String RFC = "";
			pstmnt = conn.prepareStatement(queryRFC);
			pstmnt.setString(1, datos.getCxp());
			pstmnt.setInt(2, datos.getnDocRenglon());
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				RFC = rs.getString(1);
			}
		    pstmntIns = conn.prepareStatement("INSERT INTO tReintegroDetallePaso(noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento,mImporte,mImporteNegativo,cCentroContable,nFolioReintegro,nDocRenglon,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia, OBGT) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmntIns.setInt(1, datos.getNoCLC());
			pstmntIns.setInt(2, datos.getSecCLC());
			pstmntIns.setString(3, datos.getEP());
			pstmntIns.setDouble(4, datos.getmImporteCLC());
			pstmntIns.setInt(5, datos.getMes());
			pstmntIns.setInt(6, datos.getMvto());
			pstmntIns.setString(7, evento);
			pstmntIns.setDouble(8, datos.getmImporteCLC());
			pstmntIns.setDouble(9, datos.getmImporteCLC() * -1);
			pstmntIns.setString(10, cCentro);
			pstmntIns.setInt(11, folio);
			pstmntIns.setInt(12, consecutivo);
			pstmntIns.setString(13, datos.getnPartida());
			pstmntIns.setString(14, datos.getCxp());
			pstmntIns.setString(15, RFC);
			pstmntIns.setInt(16, datos.getnDocRenglon());
			pstmntIns.setString(17, folioDep);
			pstmntIns.setString(18, datos.getObgt());

			retval = pstmntIns.execute();
		}  finally {
			CloseObject.closeObject( pstmntIns );
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return retval;
	}

	public static boolean insertaDetReintegro(Connection conn, int folioReintegro, ReintegroDetalle datos, String evento, String cCentro, int nDocRenglon) throws SQLException {
		PreparedStatement pstmnt = null;
		PreparedStatement pstmntIns = null;
		boolean retval = false;
		try {
			String query = "INSERT INTO tReintegroDetalle (nFolioReintegro,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,obgt ) "
					+ " SELECT nFolioReintegro,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,obgt "
					+ " FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
		
			pstmnt.execute();

			/*String query="SELECT * FROM tReintegroDetalle with(NOLOCK) WHERE nFolioReintegro = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			//retval = pstmnt.execute();
			rs = pstmnt.executeQuery();
			if (rs.next()) {
			    existe = true;
			}
			if(existe){ //SI existe el registro le hacemos un update de datos, si no existe lo insertamos
			    /*pstmntUp = conn.prepareStatement("UPDATE tReintegroDetalle SET noCLC=?, secCLC=?, codigoSaf=?, EP=?, cop=?, mImporteCLC=?, nCompromiso=?, beneficiario=?, suficiencia=?, solOli=?, tipoCon=?, tipoDeCon=?, cMes=?, movto=?, isr=?, iva=?, millar=?, ivaDes=? WHERE secCLC=?");
			    pstmntUp.setInt(1, datos.getNoCLC());
			    pstmntUp.setInt(2, datos.getSecCLC());
			    pstmntUp.setInt(3, datos.getCodigoSaf());
			    pstmntUp.setString(4, datos.getEP());
			    pstmntUp.setInt(5, datos.getCop());
			    pstmntUp.setDouble(6, datos.getmImporteCLC());
			    pstmntUp.setString(7, datos.getnCompromiso());
			    pstmntUp.setString(8, datos.getBeneficiario());
			    pstmntUp.setString(9, datos.getSuficiencia());
			    pstmntUp.setInt(10, datos.getSolOli());
			    pstmntUp.setString(11, datos.getTipoCon());
			    pstmntUp.setString(12, datos.getTipoDeCon());
			    pstmntUp.setInt(13, datos.getMes());
			    pstmntUp.setInt(14, datos.getMvto());
			    pstmntUp.setDouble(15, datos.getIsr());
			    pstmntUp.setDouble(16, datos.getIva());
			    pstmntUp.setDouble(17, datos.getMillar());
			    pstmntUp.setDouble(18, datos.getIvaDes());
			    pstmntUp.setDouble(19, datos.getSecCLC());*/
			/*}
				String queryRFC="SELECT ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioPAGO = ed.nFolioPAGO and ed.nDocRenglon=?";
				String RFC = "";
				pstmnt = conn.prepareStatement(queryRFC);
				pstmnt.setString(1, datos.getCxp());
				pstmnt.setInt(2, datos.getnDocRenglon());
				rs = pstmnt.executeQuery();
				if (rs.next()) {
				    RFC = rs.getString(1);
				}
			    pstmntIns = conn.prepareStatement("INSERT INTO tReintegroDetalle(noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento,mImporte,mImporteNegativo,cCentroContable,nFolioReintegro,nDocRenglon,nCapitulo,cxp,RFC) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			    pstmntIns.setInt(1, datos.getNoCLC());
			    pstmntIns.setInt(2, datos.getSecCLC());
			    //pstmntIns.setInt(3, datos.getCodigoSaf());
			    pstmntIns.setString(3, datos.getEP());
			    //pstmntIns.setInt(5, datos.getCop());
			    pstmntIns.setDouble(4, datos.getmImporteCLC());
			    /*pstmntIns.setString(7, datos.getnCompromiso());
			    pstmntIns.setString(8, datos.getBeneficiario());
			    pstmntIns.setString(9, datos.getSuficiencia());
			    pstmntIns.setInt(10, datos.getSolOli());
			    pstmntIns.setString(11, datos.getTipoCon());
			    pstmntIns.setString(12, datos.getTipoDeCon());*/
			    /*pstmntIns.setInt(5, datos.getMes());
			    pstmntIns.setInt(6, datos.getMvto());
			    //pstmntIns.setString(7, datos.getCxp());
			    //pstmntIns.setString(8, datos.getClcSicop());
			    /*pstmntIns.setDouble(15, datos.getIsr());
			    pstmntIns.setDouble(16, datos.getIva());
			    pstmntIns.setDouble(17, datos.getMillar());
			    pstmntIns.setDouble(18, datos.getIvaDes());*/
			    /*pstmntIns.setString(7, evento);
			    pstmntIns.setDouble(8, datos.getmImporteCLC());
			    pstmntIns.setDouble(9, datos.getmImporteCLC()*-1);
			    pstmntIns.setString(10, cCentro);
			    pstmntIns.setInt(11, folioReintegro);
			    pstmntIns.setInt(12, datos.getnDocRenglon());
			    pstmntIns.setString(13, datos.getnPartida());
			    pstmntIns.setString(14, datos.getCxp());
			    pstmntIns.setString(15, RFC);
			    retval = pstmntIns.execute();*/
		} finally {
			CloseObject.closeObject( pstmntIns );
			CloseObject.closeObject( pstmnt );
		}
		return retval;
	}

	public static boolean borraDetReintegro(Connection conn, int folioReintegro) throws SQLException {
		PreparedStatement pstmnt = null;
		boolean retval = false;
		try {
			String query = "DELETE FROM tReintegroDetalle WHERE nFolioReintegro = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			pstmnt.execute();
		} finally {
			CloseObject.closeObject( pstmnt );
		}
		return retval;
	}

	public static boolean borraDetReintegroPaso(Connection conn, int folioReintegro) throws SQLException {
		PreparedStatement pstmnt = null;
		boolean retval = false;
		try {
			String query = "DELETE FROM tReintegroDetallePaso WHERE nFolioReintegro = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			pstmnt.execute();
		} finally {
			CloseObject.closeObject( pstmnt );
		}
		return retval;
	}

	public static boolean insertaEncReintegro(Connection conn, int folioReintegro, ReintegroEncabezado datos, String cCentro, Date fecha) throws SQLException, ParseException {
		PreparedStatement pstmnt = null;
		PreparedStatement pstmntIns = null;
		PreparedStatement pstmntUp = null;
		ResultSet rs = null;
		boolean retval = false;
		boolean existe = false;
		try {
			String query = "SELECT * FROM tReintegroEncabezado with(NOLOCK) WHERE nFolioReintegro = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			// retval = pstmnt.execute();
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				existe = true;
			}
			if(existe){ //SI existe el registro le hacemos un update de datos, si no existe lo insertamos
				pstmntUp = conn.prepareStatement("UPDATE tReintegroEncabezado SET cTipoReintegro=?, aEjercicioFiscal=?, observaciones=?, concepto=?, formaDePago=?, clvRastreo=?, fichaDeposito=?, clvBanco=?, cuentaBancaria=?, lc=?, importeLC=? WHERE nFolioReintegro=?");
				pstmntUp.setString(1, datos.getcTipoReintegro());
				pstmntUp.setString(2, datos.getaEjercicioFiscal());
				pstmntUp.setString(3, datos.getObservaciones());
				pstmntUp.setString(4, datos.getConcepto());
				pstmntUp.setInt(5, datos.getFormaDePago());
				pstmntUp.setString(6, datos.getClvRastreo());
				pstmntUp.setString(7, datos.getFichaDeposito());
				pstmntUp.setString(8, datos.getClvBanco());
				pstmntUp.setString(9, datos.getCuentaBancaria());
				pstmntUp.setString(10, datos.getLc());
				pstmntUp.setString(11, datos.getImporteLC());
				pstmntUp.setDouble(12, datos.getnFolioReintegro());
				retval = pstmntUp.execute();
			} else if (datos != null) {
			    pstmntIns = conn.prepareStatement("INSERT INTO tReintegroEncabezado(nFolioReintegro,fSolicitud,cTipoReintegro,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			    pstmntIns.setInt(1, datos.getnFolioReintegro());			 //folio del reintegro
			    pstmntIns.setDate(2,  java.sql.Date.valueOf(datos.getfSolicitud()));			 //fecha de hoy, pasar como parametro o recalcular
			    pstmntIns.setString(3, datos.getcTipoReintegro()); 		 //cTipoReintegro dependiendo si es de compromiso, directa 
				pstmntIns.setString(4, datos.getcRamo()); // cRamo
				pstmntIns.setString(5, datos.getcUnidadResponsable()); // cUnidadResponsable
				pstmntIns.setString(6, datos.getcDocumentoHaplicado());
			    pstmntIns.setString(7, datos.getaEjercicioFiscal());  //aEjercicioFiscal (2012,2013...)
				pstmntIns.setString(8, datos.getObservaciones()); // observaciones
				pstmntIns.setString(9, datos.getConcepto()); // concepto
				pstmntIns.setString(10, datos.getU_login());
			    pstmntIns.setInt(11, datos.getFormaDePago());		 //forma de pago (efectivo, transferencia) [catalogo]
			    pstmntIns.setString(12, datos.getClvRastreo());	 //clave de rastreo
			    pstmntIns.setString(13, datos.getFichaDeposito());	//ficha de deposito en el banco
			    pstmntIns.setString(14, datos.getClvBanco());			//clave del pago en el banco
			    pstmntIns.setString(15, datos.getCuentaBancaria());			//cuenta bancaria
			    pstmntIns.setString(16, datos.getLc());					//linea de captura clave
			    pstmntIns.setDouble(17, Double.parseDouble(datos.getImporteLC()));		//importe de la linea de captura
				pstmntIns.setString(18, datos.getcTipoPoliza()); // cTipoPoliza
				pstmntIns.setDate(19, fecha);
				pstmntIns.setString(20, "RHQ");
				pstmntIns.setInt(21, datos.getFolioDependencia());
				pstmntIns.setString(22, datos.getMovimiento());
				pstmntIns.setInt(23, datos.getAviso());
				pstmntIns.setInt(24, datos.getTipoAviso());
				pstmntIns.setInt(25, datos.getCausaAviso());
				pstmntIns.setString(26, datos.getfAcreditacion());
				retval = pstmntIns.execute();
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( pstmntIns );
			CloseObject.closeObject( pstmntUp );
			CloseObject.closeObject( rs );
		}
		return retval;
	}

	public static String getEvento(Connection conn, String cOBGINI, String cCTGA, String tipoCLC) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String evento = "";
		try {
			String query = "SELECT '" + tipoCLC + "' + cEVTO FROM tEventoConcepto with (nolock) WHERE cOBGINI = ? AND cCTGA = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setString(1, cOBGINI);
			pstmnt.setString(2, cCTGA);
			pstmnt.execute();

			rs = pstmnt.executeQuery();
			if (rs.next()) {
				evento = rs.getString(1);
			}

		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return evento;
	}

	public static String getCLCPagada(Connection conn, int nCLC, String cxp) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		boolean retVal = false;
		String aplicado = "";
		String datos = "";
		try {
			String query = "SELECT cDocumentoHaplicado FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?";
			pstmnt = conn.prepareStatement(query);
			// pstmnt.setInt(1, nCLC);
			pstmnt.setString(1, cxp);

			rs = pstmnt.executeQuery();
			if (rs.next()) {
				aplicado = rs.getString(1);
				if ("S".equals(aplicado)) {
					retVal = true;
				} else if (aplicado == null) {
					retVal = false;
					datos = "La CLC con Cuenta Por Pagar: " + cxp + " NO se encuentra pagada";
				}
			} else {
				retVal = false;
				datos = "La CLC con Cuenta Por Pagar: " + cxp + " NO existe";
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return datos;
	}

	public static String tipoPago(Connection conn, int nCLC, String cxp) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String tipoCLC = "";
		try {
			String query = "SELECT ctipoPago FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setString(1, cxp);

			rs = pstmnt.executeQuery();
			if (rs.next()) {
				tipoCLC = rs.getString(1);
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return tipoCLC;
	}

	public static String getTipoPago(Connection conn, String cxp) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String tipo = "";
		try {
			String query = "SELECT CASE cTipoPago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' "
				+ "ELSE '' END AS dTipoPago FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setString(1, cxp);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				tipo = rs.getString(1);
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return tipo;
	}

	public static ReintegroDetalle[] getReintegrosDetalle(Connection conn, String[] ids, int folio) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		ReintegroDetalle[] reintegros;
		try {
			String query = "SELECT * FROM tReintegroDetalle rd with (nolock) WHERE nFolioReintegro = ? AND (";
			for (int j = 0; j < ids.length; j++) {
				query += " nDocRenglon=? OR";
			}
			query = query.substring(0, query.length() - 3);
			query += ")";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folio);
			for (int j = 0; j < ids.length; j++) {
				pstmnt.setInt(j + 2, Integer.parseInt(ids[j]));
			}

			rs = pstmnt.executeQuery();
			reintegros = new ReintegroDetalle[ids.length];
			int i = 0;
			while (rs.next()) {
				ReintegroDetalle rd = new ReintegroDetalle();
				rd.setNoCLC(rs.getInt("noCLC"));
				rd.setSecCLC(rs.getInt("secCLC"));
				rd.setEP(rs.getString("EP"));
				rd.setmImporteCLC(rs.getDouble("mImporteCLC"));
				DecimalFormat formatter = new DecimalFormat("###.####");
				rd.setmImporteCLCFormat(formatter.format(rs.getDouble("mImporteCLC")));
				rd.setMes(rs.getInt("cMes"));
				rd.setCxp(rs.getString("cxp"));
				reintegros[i] = rd;
				i++;
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return reintegros;
	}

	public static void autorizaReintegro(Connection conn, Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, String ulogin, String prefixPath, String fAcredit, String year) throws SQLException, GestionException {
		PreparedStatement pstmnt = null, pst1 = null, pst2 = null, pst3 = null;
		int nIdCaso;
		ResultSet rs = null, rs2 = null;

		try {
									
			nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			
			/*VALIDA SI YA EXISTE EL REINTEGRO EN LA TABLA AUT*/
			pst3 = conn.prepareStatement("SELECT CASE WHEN COUNT(*) > = 1 THEN 'S' ELSE 'N' END existe FROM tReintegroAutEncabezado WITH(NOLOCK) WHERE nFolioReintegroaut = ?");			
			pst3.setInt(1, nIdCaso);
			rs2 = pst3.executeQuery();
			String existe = "";
			
			if (rs2.next()) {
				existe = rs2.getString(1);
			}
			
			if("N".equals( existe )) {
				pstmnt = conn.prepareStatement("INSERT INTO tReintegroAutEncabezado (nFolioReintegroaut, fSolicitud, cTipoReintegro, cRamo, cUnidadResponsable, cDocumentoHaplicado, aEjercicioFiscal, observaciones, concepto, u_login, "
					+ "cUnidadResponsableContable,nFolioTramiteSicop, cTipoPoliza, fAplicacion,cdescripcionpoliza, formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso) "
					+ "SELECT nFolioReintegro, fSolicitud, cTipoReintegro, cRamo,cUnidadResponsable, 'N', aEjercicioFiscal, observaciones, concepto, "
				        + "u_login, 'RHQ',nFolioTramiteSicop, 'EG','" + fAcredit + "','AUTORIZACION DE REINTEGRO PRESUPUESTAL FOLIO ' + CAST(nFolioReintegro as varchar),formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso FROM tReintegroEncabezado WITH (NOLOCK) WHERE nFolioReintegro = ?");
			
				pstmnt.setInt(1, nIdCaso);
				pstmnt.execute();
	
				pst1 = conn.prepareStatement("SELECT tipoAviso FROM tReintegroEncabezado WITH (NOLOCK) WHERE nFolioReintegro = ?");
				pst1.setInt(1, nIdCaso);
				rs = pst1.executeQuery();
				String tipoRein = "";
	
				if (rs.next()) {
					tipoRein = rs.getString(1);
				}
				
				/*PAGOS QUE REINTEGRAN AL COMPROMISO APARTIR DE 2018 DIVERSO, OBRA Y FEDERALIZADO*/
				pst2 = conn.prepareStatement("INSERT INTO tReintegroAutDetalle(nFolioReintegroaut,nDocRenglon,noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento, mImporte, mImporteNegativo, cCentroContable, nCapitulo,cxp,RFC,nFolioDependencia,ALM,nRenglonPagado, obgt,ctab,nidprograma,cSubPrograma, ffm, mAmortizacionAnticipo,mPasivoDiferido,cPasivo,mImportePC) "
				        +  " SELECT rd.nfolioreintegro, "
				        +"       rd.ndocrenglon, "
				        +"       rd.noclc, "
				        +"       rd.secclc, "
				        +"       rd.ep, "
				        +"       rd.mimporteclc, "
				        +"       rd.cmes, "
				        +"       rd.movto, "
				        +"       (SELECT CASE "
				        +"                 WHEN ctipopago='PAGOOBRA' AND cEvento NOT IN( 'REIN_TRAM_IR','REIN_TRAM_IP' ) THEN 'RC_EG' "
				        +"				   WHEN ctipopago='PAGOOBRA' AND cEvento IN ('REIN_TRAM_IP') THEN 'RD' "
				        +"                 WHEN ctipopago='PAGODIVERSO' AND cEvento NOT IN( 'REIN_TRAM_IR','REIN_TRAM_IP' ) THEN 'RC_EG' "
				        +"				   WHEN ctipopago='PAGODIVERSO' AND cEvento IN ('REIN_TRAM_IP') THEN 'RD' "
				        +"                 WHEN ctipopago='PAGODIRECTO' THEN 'RD_EG' "
				        +"				   WHEN ctipopago='PAGODIRECTO' AND cEvento IN ('REIN_TRAM_IP') THEN 'RD' "
				        +"                 WHEN ctipopago='RELACIONGASTOS' AND cEvento NOT IN( 'REIN_TRAM_IR','REIN_TRAM_IP' ) THEN 'RD_EG' "
				        +"				   WHEN ctipopago='RELACIONGASTOS' AND cEvento IN ('REIN_TRAM_IP') THEN 'RD_EG' "
				        +"                 WHEN ctipopago='NOMINA' THEN 'RD_EG' "
				        +"                 WHEN ctipopago='AJENAS' THEN CASE "  
				        +"												    WHEN cEvento LIKE '%LAUDOS' THEN 'RD_' "  
				        +"													WHEN cEvento LIKE '%IP'	THEN 'RD_' " 
				        +"													ELSE 'RC_EG' " 
				        +"												END "
				        +"                 WHEN ctipopago='FEDERALIZADO' THEN 'RC_EG' "
				        +"				   WHEN ctipopago='FEDERALIZADO' AND cEvento IN ('REIN_TRAM_IP') THEN 'RD' "			        
				        +"               END AS dTipoPago "
				        +"        FROM   tpagadoencabezado WITH(nolock) "
				        +"        WHERE  canocontrarrecibo = rd.cxp)"
				        +"         "
				        +"       + SUBSTRING(cEvento, 6, LEN(cEvento)), "
				        +"       rd.mimporte, "
				        +"       rd.mimportenegativo, "
				        +"       rd.ccentrocontable, "
				        +"       rd.ncapitulo, "
				        +"       rd.cxp, "
				        +"       rd.rfc, "
				        +"       rd.nfoliodependencia, "
				        +"       (SELECT TOP(1) alm "
				        +"        FROM   tpagadodetalle WITH(nolock), "
				        +"               tpagadoencabezado WITH(nolock) "
				        +"        WHERE  ep = rd.ep "
				        +"               AND nfoliosiaff = rd.noclc "
				        +"               AND tpagadodetalle.nfoliopagado = tpagadoencabezado.nfoliopagado "
				        +"               AND tpagadoencabezado.canocontrarrecibo = rd.cxp) AS ALM, "
				        +"       rd.nrenglonpagado," 
				        +"       rd.obgt, "
				        +"		 rd.ctab, "	
				        +"       nidprograma ,"
				        +"		 cSubPrograma ,"
				        +"       rd.OBGT + CASE WHEN RIGHT('00' + LTRIM(RTRIM(nidprograma)),2) = '13' THEN '0000' ELSE '" + year + "' END + RIGHT('00' + LTRIM(RTRIM(nidprograma)),2) + RIGHT('00' + LTRIM(RTRIM(cSubPrograma)),2) + '00' AS ffm, "
				        +"		 0.00 AS mAmortizacionAnticipo, "
				        +"		 mPasivoDiferido, "
				        +"		 cpasivo, "
				        +"		 mImportePC "
				        +" FROM   treintegrodetalle rd WITH (nolock) "
				        +" JOIN dbo.tReintegroEncabezado re WITH (NOLOCK) ON rd.nFolioReintegro = re.nFolioReintegro "
				        +" WHERE  rd.nfolioreintegro = ?"); 
				pst2.setInt(1, nIdCaso);
				pst2.execute();
					
				conn.commit();
			}
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( pst1 );
			CloseObject.closeObject( pst2 );
			CloseObject.closeObject( rs );
		}
	}

	public static String getTipoPoliza(Connection conn, String cDocumento) throws SQLException {
		String retTipoPoliza = "";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		try {
		
			pstm = conn.prepareStatement("select cTipoPoliza from tDocumentoTipoPoliza with (nolock) where cDocumento=?");
			pstm.setString(1, cDocumento);
			rs = pstm.executeQuery();
			
			if (rs.next()) {
				retTipoPoliza = rs.getString(1);
			}
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
		
		return retTipoPoliza;
	}

	public static String getcPartida(Connection conn, String EP) throws SQLException {
		String cPartida = "";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement("select cPartida from tCatalogoEP with (nolock) where EP=?");
			pstm.setString(1, EP);
			rs = pstm.executeQuery();
			
			if (rs.next()) {
				cPartida = rs.getString(1);
			}
			
			String res = "";
			if (cPartida != null && !cPartida.equals("")) {
				res = cPartida.substring(0, 1);
				for (int i = 1; i <= 4; i++)
					res += "0";
			} else {
				res = "La EP " + EP + " no existe en el catalogo";
			}
			return res;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
		
		
	}

	public static String getEntidadFederativa(Connection conn, String EP) throws SQLException {
		String cEntidadFederativa = "";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement("select cEntidadFederativa from tCatalogoEP with (nolock) where EP=?");
			pstm.setString(1, EP);
			rs = pstm.executeQuery();
			if (rs.next()) {
				cEntidadFederativa = rs.getString(1);
			}
			return cEntidadFederativa;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
	}

	public static double getRemanente(Connection conn, String ep, String cxp, int nRenglon) throws SQLException {
		double remanente = 0.00;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		String queryRemanente=" SELECT impRect - impReint AS  Remanente\r\n" + 
							  " FROM (\r\n" + 
							  "  SELECT ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN PAGDET.mImporte - RECDET.mImporte END AS IMPORTE FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND PAGDET.cMes = RECDET.cMes),PAGDET.mImporte) AS impRect\r\n" + 
							  "	, ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.EP END AS IMPORTE FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND PAGDET.cMes = RECDET.cMes), PAGDET.EP) AS EPPAG\r\n" + 
							  "	, ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.nDocRenglon END AS ndogrenglon FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND PAGDET.cMes = RECDET.cMes), PAGDET.nDocRenglon) AS ndogrenglonP\r\n" + 
							  "	, ISNULL(REINTDET.mImporte, 0) AS impReint\r\n" + 
							  "  FROM tPagadoEncabezado AS PAGENC WITH (NOLOCK)\r\n" + 
							  "  JOIN tPagadoDetalle AS PAGDET WITH (NOLOCK) ON PAGENC.nFolioPagado = PAGDET.nFolioPagado\r\n" + 
							  "  LEFT JOIN tRectificacionAutEncabezado AS RECENC WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = RECENC.caNoContrarrecibo	\r\n" + 
							  "  LEFT JOIN tReintegroAutDetalle AS REINTDET WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = CXP\r\n" + 
							  "  WHERE PAGENC.caNoContrarrecibo = ?	\r\n" + //1
							  "	AND (PAGDET.mPasivoDiferido = 0 OR PAGDET.mPasivoDiferido IS NULL)\r\n" + 
							  "	UNION\r\n" + 
							  "  SELECT \r\n"
							  + "	ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.mImporte END),0) AS impRect\r\n"
							  + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.EP END), '') AS EPPAG\r\n"
							  + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.nDocRenglon END), 0) AS ndogrenglonP\r\n"
							  + "	, ISNULL(REINTDET.mImporte, 0) AS impReint\r\n"
							  + "  FROM tPagadoEncabezado AS PAGENC WITH (NOLOCK)\r\n"
							  + "  JOIN tPagadoDetalle AS PAGDET WITH (NOLOCK) ON PAGENC.nFolioPagado = PAGDET.nFolioPagado\r\n"
							  + "  LEFT JOIN tRectificacionAutEncabezado AS RECENC WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = RECENC.caNoContrarrecibo	\r\n"
							  + "  LEFT JOIN tRectificacionAutDetalle AS RECDET WITH (NOLOCK) ON RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut and RECDET.cEvento LIKE 'DEBE%'	\r\n" + 
							  "  LEFT JOIN tReintegroAutDetalle AS REINTDET WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = CXP\r\n" + 
							  "  WHERE PAGENC.caNoContrarrecibo = ?	\r\n" + //2
							  "	AND (PAGDET.mPasivoDiferido = 0 OR PAGDET.mPasivoDiferido IS NULL)" +  ") tbl " + 
							  " WHERE EPPAG = ? " + //3 
							  "	AND ndogrenglonP = ?"; //4
		// 1 2 3 4
		log.trace(String.format("Ejecutando query para remanente. [%s][%s,%s,%s,%d]", queryRemanente, cxp, cxp, ep, nRenglon));
		try {
			pstm = conn.prepareStatement(queryRemanente);
			pstm.setString(1, cxp);
			pstm.setString(2, cxp);
			pstm.setString(3, ep);
			pstm.setInt(4, nRenglon);		
			
			rs = pstm.executeQuery();
			if (rs.next()) {
				remanente = rs.getDouble(1);
			}
			return remanente;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
	}
	
	public static double getRemanenteOA(Connection conn, String ep, String cxp, int nRenglon, String caNoContrarreciboOA) throws SQLException {
		double remanente = 0.00;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		String queryRemanente=  "SELECT mimporteneto - imprect - impreint AS Remanente "
			+ " FROM   (SELECT pd.mimporteneto, "
			+ "               (SELECT Count(*) AS sumaRect "
			+ "                FROM   trectificacionencabezado re WITH (nolock), "
			+ "                       trectificaciondetalle rd WITH (nolock) "
			+ "                WHERE  rd.ep = ? " // 1
			+ "                       AND re.cdocumentohaplicado = 'S' "
			+ "                       AND re.nfoliorectificacion = rd.nfoliorectificacion "
			+ "                       AND re.canocontrarrecibo = ? " // 2
			+ "                       AND rd.ep = pd.ep "
			+ "                       AND rd.ndocrenglon = ?)               AS tRectificacion, " // 3
			+ "               (SELECT Count(*) AS sumaReint "
			+ "                FROM   treintegroencabezado ree WITH (nolock), "
			+ "                       treintegrodetalle red WITH (nolock) "
			+ "                WHERE  red.ep = ? " // 4
			+ "                       AND ree.cdocumentohaplicado = 'S' "
			+ "                       AND ree.nfolioreintegro = red.nfolioreintegro "
			+ "                       AND red.cxp = ? " // 5
			+ "                       AND red.ep = pd.ep "
			+ "                       AND red.nrenglonpagado = ?)           AS tReintegros, " // 6
			+ "               Isnull((SELECT Isnull(Sum(rd.mimporte), 0) AS impR "
			+ "                       FROM   treintegroencabezado re WITH(nolock), "
			+ "                              treintegrodetalle rd WITH(nolock) "
			+ "                       WHERE  re.nfolioreintegro = rd.nfolioreintegro "
			+ "                              AND re.cdocumentohaplicado = 'S' "
			+ "                              AND rd.cxp = ? "// 7
			+ "                              AND rd.ep = ? "// 8
			+ "               AND rd.nrenglonpagado = ?), 0) AS impReint, " // 9
			+"               Isnull((SELECT Isnull(Sum(CASE Substring(cevento, 1, 4) "
			+"                                           WHEN 'DICE' THEN mimporte "
			+"                                           WHEN 'DEBE' THEN mimportenegativo "
			+"                                         END), 0) AS imp "
			+"                       FROM   trectificaciondetalle r WITH (nolock), "
			+"                              trectificacionencabezado h WITH (nolock) "
			+"                       WHERE  r.nfoliorectificacion = h.nfoliorectificacion "
			+"                              AND h.canocontrarrecibo = ? " //10
			+ "                              AND r.ep =  ? " // 11
			+"               AND h.cdocumentohaplicado = 'S' "							
			+ "              AND r.canocontrarrecibo =  ? " // 12
			+"                       GROUP  BY r.ndocrenglon), 0)          AS impRect "
			+"        FROM   tpagadoencabezado pe WITH (nolock), "
			+"               tpagadodetalle pd WITH (nolock) "
			+"        WHERE  canocontrarrecibo = ? " //13
			+ "               AND pd.ep = ? " // 14
			+"               AND pe.cdocumentohaplicado = 'S' "
			+"               AND pe.nfoliopagado = pd.nfoliopagado "
			+"               AND pd.ndocrenglon = ?) tNueva " //15
			+ " WHERE  1 = 1 ";
		// 1 2 3 4 5 6 7 8 9 10 11 12 13 14 1 2 3 4 5 6 7 8 9 10 11 12 13 14
		log.trace(String.format("Ejecutando query para remanente. [%s][%s,%s,%d,%s,%s,%d,%s,%s,%d,%s,%s,%s,%s,%d]", queryRemanente, ep, cxp, nRenglon, ep, cxp, nRenglon, cxp, ep, nRenglon, cxp, ep, cxp, ep, nRenglon));
		try {
		
			pstm = conn.prepareStatement(queryRemanente);
			pstm.setString(1, ep);
			pstm.setString(2, cxp);
			pstm.setInt(3, nRenglon);
			pstm.setString(4, ep);
			pstm.setString(5, cxp);
			pstm.setInt(6, nRenglon);
			pstm.setString(7, cxp);
			pstm.setString(8, ep);
			pstm.setInt(9, nRenglon);
			pstm.setString(10, cxp);
			pstm.setString(11, ep);
			pstm.setString(12, caNoContrarreciboOA);
			pstm.setString(13, cxp);
			pstm.setString(14, ep);
			pstm.setInt(15, nRenglon);
			
			rs = pstm.executeQuery();
			if (rs.next()) {
				remanente = rs.getDouble(1);
			}
			
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
		return remanente;
	}
	
	public static ReintegroEncabezado getReintegroEncabezado(Connection conn, int folio) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroEncabezado re = new ReintegroEncabezado();
		
		try {	
				pstm = conn.prepareStatement("SELECT * FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
				pstm.setInt(1, folio);
				res = pstm.executeQuery();
				
				if (res.next()) {
					re = new ReintegroEncabezado();
					re.setfSolicitud(res.getString("fSolicitud"));
					re.setcTipoReintegro(res.getString("cTipoReintegro"));
					re.setfAplicacion(res.getString("fAplicacion"));
					re.setcRamo(res.getString("cRamo"));
					re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
					re.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
					re.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
					re.setnFolioPoliza(res.getInt("nFolioPoliza"));
					re.setcTipoPoliza(res.getString("cTipoPoliza"));
					re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
					re.setObservaciones(res.getString("observaciones"));
					re.setConcepto(res.getString("concepto"));
					re.setU_login(res.getString("U_LOGIN"));
					re.setfCancelacion(res.getString("fCancelacion"));
					re.setfExpiracion(res.getString("fExpiracion"));
					re.setfAcreditacion(res.getString("fAcredit"));
					re.setFormaDePago(res.getInt("formaDePago"));
					re.setClvRastreo(res.getString("clvRastreo"));
					re.setFichaDeposito(res.getString("fichaDeposito"));
					re.setClvBanco(res.getString("clvBanco"));
					re.setCuentaBancaria(res.getString("cuentaBancaria"));
					re.setLc(res.getString("lc"));
					DecimalFormat formatter = new DecimalFormat("###,###.##");
					re.setImporteLC(formatter.format(res.getDouble("importeLC")));
					re.setCausaAviso(res.getInt("causaAviso"));
					re.setTipoAviso(res.getInt("tipoAviso"));
					re.setMovimiento(res.getString("mvto"));
				}
				return re;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
	}

	public static ReintegroEncabezadoMil getReintegroEncabezadoNuevo(Connection conn, int folio) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroEncabezadoMil re = null;
		try {
		
				pstm = conn.prepareStatement("SELECT * FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
				pstm.setInt(1, folio);
				res = pstm.executeQuery();
				if (res.next()) {
					re = new ReintegroEncabezadoMil();
					re.setfSolicitud(res.getString("fSolicitud"));
					re.setcTipoReintegro(res.getString("cTipoReintegro"));
					re.setfAplicacion(res.getString("fAplicacion"));
					re.setcRamo(res.getString("cRamo"));
					re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
					re.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
					re.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
					re.setnFolioPoliza(res.getInt("nFolioPoliza"));
					re.setcTipoPoliza(res.getString("cTipoPoliza"));
					re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
					re.setObservaciones(res.getString("observaciones"));
					re.setConcepto(res.getString("concepto"));
					re.setfCancelacion(res.getString("fCancelacion"));
					re.setfExpiracion(res.getString("fExpiracion"));
					re.setfAcreditacion(res.getString("fAcredit"));
					re.setFormaDePago(res.getString("formaDePago"));
					re.setClvRastreo(res.getString("clvRastreo"));
					re.setFichaDeposito(res.getString("fichaDeposito"));
					re.setClvBanco(res.getString("clvBanco"));
					re.setCuentaBancaria(res.getString("cuentaBancaria"));
					re.setLc(res.getString("lc"));
					DecimalFormat formatter = new DecimalFormat("###,###.##");
					re.setImporteLC(formatter.format(res.getDouble("importeLC")));
					re.setCausaAviso(res.getString("causaAviso"));
					re.setTipoAviso(res.getString("tipoAviso"));
					re.setMovimiento(res.getString("mvto"));
					re.setFolioDependencia(res.getString("folioDependencia"));
					re.setAviso(res.getString("aviso"));
					re.setnidprograma(res.getString("nidprograma"));
					re.setcSubPrograma(res.getString("cSubPrograma") );;
				}
				return re;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
	}
	
	public static ReintegroDetalleMil getReintegroDetalleNuevo(Connection conn, int folio) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroDetalleMil rd = null;
		try {
		
			pstm = conn.prepareStatement("SELECT TOP 1 * FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = ?");
			pstm.setInt(1, folio);
			res = pstm.executeQuery();
			if (res.next()) {
				rd = new ReintegroDetalleMil();
				rd.setCtab(res.getString("CTAB"));
				rd.setRfc(res.getString("RFC"));	
			}
			return rd;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
	}

	public static ArrayList<ReintegroDetalle> getReintegroDetalle(Connection conn, int folio) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroDetalle rd = null;
		ArrayList<ReintegroDetalle> detalles = new ArrayList<ReintegroDetalle>();
		try {
		
			pstm = conn.prepareStatement("SELECT * FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = ?");
			pstm.setInt(1, folio);
			res = pstm.executeQuery();
			while (res.next()) {
				rd = new ReintegroDetalle();
				rd.setNoCLC(res.getInt("noCLC"));
				rd.setnDocRenglon(res.getInt("nDocRenglon"));
				rd.setSecCLC(res.getInt("secCLC"));
				rd.setEP(res.getString("EP"));
				rd.setmImporteCLC(res.getDouble("mImporteCLC"));
				DecimalFormat formatter = new DecimalFormat("###.####");
				rd.setmImporteCLCFormat(formatter.format(res.getDouble("mImporteCLC")));
				rd.setMes(res.getInt("cMes"));
				rd.setCxp(res.getString("cxp"));
				detalles.add(rd);
			}
			return detalles;
	
		} finally {
				CloseObject.closeObject( pstm );
				CloseObject.closeObject( res );
		}
	}

	public static boolean getReintegroHApartado(Connection conn, int folio) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean aplicado = false;
		try {
				pstm = conn.prepareStatement("SELECT cDocumentoHaplicado FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
				pstm.setInt(1, folio);
				res = pstm.executeQuery();
				if (res.next()) {
					if ("S".equals(res.getString("cDocumentoHaplicado")))
						aplicado = true;
				}
			return aplicado;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
		
	}

	public static ArrayList<ReintegroDetalle> getReintegroLayout(Connection conn, int folio) throws SQLException {
		ResultSet res = null, res2 = null;
		PreparedStatement pst1 = null;
		CallableStatement cs = null;
		ArrayList<ReintegroDetalle> detalles = new ArrayList<ReintegroDetalle>();
		String tipoPago = "";
		String query = "";
		try {				
				tipoPago = getTipoPagoReint(conn, folio);
		
				if ("RELACIONGASTOS".equals(tipoPago)) {
					query = "{call sp_generaLayoutReintegrosRG( ? )}";
				}
				else{
					query = "{call sp_generaLayoutReintegrosPD( ? )}";
						
				}
				ReintegroDetalle rd;
				cs = conn.prepareCall(query);
				cs.setInt(1, folio);
				res = cs.executeQuery();
				int numRows = 0;
				log.debug(query);
				while (res.next()) {
					numRows++;
					rd = new ReintegroDetalle();
					rd.setBeneficiario(res.getString("CBEN"));
					rd.setnDocRenglon(res.getInt("nDocRenglon"));
					rd.setTipoCon(res.getString("ID_TIPO_CONCEPTO"));
					rd.setSolOli(res.getInt("sol_oli"));
					rd.setIsr(res.getInt("retencion_isr"));
					rd.setClcSicop(res.getString("nFolioSICOP"));
					rd.setnCompromiso(res.getString("cUnidadNormativa")); //lo puse en nCompromiso porque ese no lo uso y cUnidadNormativa si lo tengo que recuperar, es temporal
					rd.setnPartida(res.getString("mImporte")); //nPartida tendrá el importe que se hace varchar para que se muestre en buen formato
					rd.setMes(res.getInt("cMes"));
					rd.setEP(res.getString("EP"));
					rd.setSecCLC(res.getInt("secCLC"));
					rd.setCxp(res.getString("NCOM_15")); //Sirve para alamacenar temporalmente el ncom_15
					rd.setTpag(res.getString("TPAG_117"));
					rd.setTipoDeCon(res.getString("TCONC_49"));
					rd.setSuficiencia(res.getString("SPAG_176"));//SIrve para almacenar temporalmente spag_176
					rd.setNres(res.getString("NRES_17"));
					detalles.add(rd);
				}
				if (numRows == 0) { // si no hubo registros buscamos en capitulo mil
					pst1 = conn.prepareStatement("select distinct d.nDocRenglon, e.cRamoEP, e.cUnidadResponsableEP, e.aEjercicioFiscal, e.cGrupoFuncional, e.cFuncion " +
							", e.cSubFuncion, e.cProgramaGeneral, e.cActividadInstitucional, e.cProgramaPresupuestario, substring(e.cPartida,1,1) as cCapitulo " +
							", substring(e.cPartida,2,1) as cConcepto, substring(e.cPartida,3,1) as cPartida, substring(e.cPartida,4,2) as cPartidaEspecifica " +
							", e.cTipoGasto, e.cFuenteFinanciamiento, e.cEntidadFederativa, e.cCartera, left(replicate('0', 7)+e.cUnidadEjecutora,10) as cUnidadEjecutora " +
							", substring(e.cUnidadNorativa,2,2) as cUnidadNormativa, '0','0','0','0','0', CONVERT(VARCHAR,d.mImporte) as mImporte, d.cMes, CASE WHEN NCOM_15 is null OR NCOM_15='' THEN '' ELSE REPLICATE('0',6-LEN(LTRIM(NCOM_15)))+LTRIM(CAST(NCOM_15 AS VARCHAR(6))) END as NCOM_15 "+
							", b.sSicop, '' as cFillRellen2 , isnull(NOIF_18,0) as sol_oli, TPAG_117, rclc.CONC_MOV_50 as ID_TIPO_CONCEPTO "+
							", 0 as retencion_isr, '0', d.cEvento, p.nFolioSICOP,d.EP,d.secCLC,SPAG_176,TCONC_49,isnull(NRES_17,'') as NRES_17 "+ 
							" from tReintegroDetalle d with (nolock), tReintegroEncabezado r with (nolock), tCatalogoEP e with (nolock), tPagadoEncabezado P with (nolock), tPagadoDetalle dp  with (nolock), tBeneficiarioCapituloMil b with (nolock), vReintegrosCLCSICOP rclc with(nolock)" +
							" WHERE d.nFolioReintegro =  r.nFolioReintegro" +
							" and d.mPasivoDiferido = 0 and d.EP =  e.EP and d.cxp = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.sCodigoEntidad  and rclc.EP =SUBSTRING(d.EP, 1, 55) and rclc.EP = SUBSTRING(e.EP, 1, 55) and rclc.FolioSIAFF=d.noCLC and rclc.FolioSIAFF=p.nFolioSIAFF " /*and rclc.SEC=d.secCLC*/ + " and d.nFolioReintegro=? and r.nFolioReintegro=?");
					pst1.setInt(1, folio);
					pst1.setInt(2, folio);
					res2 = pst1.executeQuery();
					while (res2.next()) {
						rd = new ReintegroDetalle();
						rd.setBeneficiario(res.getString("sSicop"));
						rd.setnDocRenglon(res.getInt("nDocRenglon"));
						rd.setTipoCon(res.getString("ID_TIPO_CONCEPTO"));
						rd.setSolOli(res.getInt("sol_oli"));
						rd.setIsr(res.getInt("retencion_isr"));
						rd.setClcSicop(res.getString("nFolioSICOP"));
						rd.setnCompromiso(res.getString("cUnidadNormativa")); //lo puse en nCompromiso porque ese no lo uso y cUnidadNormativa si lo tengo que recuperar, es temporal
						rd.setnPartida(res.getString("mImporte")); //nPartida tendrá el importe que se hace varchar para que se muestre en buen formato
						rd.setMes(res.getInt("cMes"));
						rd.setEP(res.getString("EP"));
						rd.setSecCLC(res.getInt("secCLC"));
						rd.setCxp(res.getString("NCOM_15")); //Sirve para alamacenar temporalmente el ncom_15
						rd.setTpag(res.getString("TPAG_117"));
						rd.setTipoDeCon(res.getString("TCONC_49"));
						rd.setSuficiencia(res.getString("SPAG_176"));//SIrve para almacenar temporalmente spag_176
						rd.setNres(res.getString("NRES_17"));
						rd.setcProgramaGeneral(res.getString("cProgramaGeneral"));
						detalles.add(rd);
					}
				}
				return detalles;
		} finally {
			CloseObject.closeObject( cs );
			CloseObject.closeObject( pst1 );
			CloseObject.closeObject( res );
			CloseObject.closeObject( res2 );
		}
		
	}

	private static String getTipoPagoReint(Connection conn, int folio) throws SQLException {
		ResultSet rs = null;
		PreparedStatement pstm = null;
		String tipoPago = "";
		try {
		
			pstm = conn.prepareStatement("SELECT cTipoPago FROM dbo.tReintegroDetalle AS rd WITH (NOLOCK) JOIN dbo.tPagadoEncabezado AS p WITH (NOLOCK) ON cxp =caNoContrarrecibo WHERE nFolioReintegro = ?");
			pstm.setInt(1, folio);
			rs = pstm.executeQuery();
	
			if (rs.next()) {
				tipoPago = rs.getString(1);
			}
			return tipoPago;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
	}

	public static String getMesesImportes(Connection conn, String EP, String CXP) throws SQLException {
		String res = "";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		
		try {
			pstm = conn.prepareStatement("SELECT pd.nDocRenglon,pd.cMes, pd.mImporteNeto FROM tPagadoEncabezado pe with(nolock), tPagadoDetalle pd with(nolock) WHERE pd.nFolioPagado=pe.nFolioPagado AND pd.EP=? AND pe.caNoContrarrecibo=? AND pe.cDocumentoHaplicado='S'");
			pstm.setString(1, EP);
			pstm.setString(2, CXP);
			rs = pstm.executeQuery();
			int contador = 0;
			res = "--Para la EP " + EP + " en la CXP " + CXP + ", hay:\\n";
			while (rs.next()) {
				res += "en el Renglon " + rs.getInt("nDocRenglon") + " Mes " + rs.getInt("cMes") + " el Importe de " + rs.getDouble("mImporteNeto") + "\\n";
				contador++;
			}
			if (contador == 0)
				res = "La cuenta por pagar proporcionada no se encuentra pagada\\n";
			
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
		return res;
	}

	public static boolean getEPCatalogo(Connection conn, String EP) throws SQLException {
		boolean res = false;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		
		try {
		
			pstm = conn.prepareStatement("SELECT COUNT(*) FROM tCatalogoEP with(nolock) WHERE EP=?");
			pstm.setString(1, EP);
			rs = pstm.executeQuery();
			if (rs.next()) {
				int cuantos = rs.getInt(1);
				if (cuantos > 0)
					res = true;
			}
			return res;
		} finally {
		
		CloseObject.closeObject( pstm );
		CloseObject.closeObject( rs );
		
		}
	}

	public static boolean actualizaInfoPagos(Connection conn, int folio, String clvRastreo, String lc, String ficha, String clvBanco, String cuenta, String fAcredit, String ctab) throws SQLException, ParseException {
		PreparedStatement pstmntUp = null;
		PreparedStatement pstmntUp2 = null;
		boolean retval = false;
		boolean retval2 = false;

		try {
			pstmntUp = conn.prepareStatement("UPDATE tReintegroEncabezado SET clvRastreo=?, lc=?, fichaDeposito=?, clvBanco=?, cuentaBancaria=? WHERE nFolioReintegro=?");
			pstmntUp.setString(1, clvRastreo);
			pstmntUp.setString(2, lc);
			pstmntUp.setString(3, ficha);
			pstmntUp.setString(4, clvBanco);
			pstmntUp.setString(5, cuenta);
			pstmntUp.setInt(6, folio);
			retval = pstmntUp.execute();

			if (ctab != null && ctab != "") {
				pstmntUp2 = conn.prepareStatement("UPDATE dbo.tReintegroDetalle SET CTAB = ? WHERE nFolioReintegro = ?");
				pstmntUp2.setString(1, ctab);
				pstmntUp2.setInt(2, folio);

				retval2 = pstmntUp2.execute();
			}
			if (!retval || !retval2)
				return false;
			else
				return true;
		} finally {
			CloseObject.closeObject( pstmntUp );
			CloseObject.closeObject( pstmntUp2 );
			
		}
		
	}

	public static boolean catalogoMovimiento(Connection conn, String movimiento) throws SQLException, ParseException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean retval = false;
		try {
		
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoMvto with(nolock) WHERE tipo = ?");
			pstm.setString(1, movimiento);
			rs = pstm.executeQuery();
			if (rs.next()) {
				int cuantos = rs.getInt(1);
				if (cuantos > 0)
					retval = true;
			}
			return retval;
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );
		}
	}

	public static boolean catalogoCausaAviso(Connection conn, int causa, int tipo) throws SQLException, ParseException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean retval = false;
		try {
				
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoCausaAviso with(nolock) WHERE aCausa = ?");
			pstm.setInt(1, causa);
			rs = pstm.executeQuery();
			if (rs.next()) {
				int cuantos = rs.getInt(1);
				if (cuantos > 0)
					retval = true;
			}
			return retval;
		} finally {
			
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( rs );

		}
	}

	public static int secCLC(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval = 0;
		try {
			pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? and SEC not in (SELECT secCLC FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=? and noCLC=? and EP=?)");
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, folio);
			pstmnt.setString(4, caNoContrarrecibo);
			pstmnt.setString(5, EP);

			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				retval = rs.getInt(1);
			} else {
				retval = -1;
			}
			
			return retval;
			
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
	}

	public static int secCLCRect(Connection conn, String caNoContrarrecibo, String EP) throws SQLException {
		PreparedStatement pstmnt = null, pstmnt2 = null;
		ResultSet rs = null, rs2 = null;
		int retval = 0;
		String ep = EP.substring(0, 55);
		String EP2 = "";
		try {
			
			pstmnt2 = conn.prepareStatement("SELECT dbo.CambiaEPCarteraMeta(?)");		
			pstmnt2.setString(1, ep);
			
			rs2 = pstmnt2.executeQuery();
			if (rs2.next()) {
				EP2 = rs2.getString(1);
			}
			
			pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
			pstmnt.setString(1, String.format("%.0f", Double.parseDouble(caNoContrarrecibo)));
			pstmnt.setString(2, EP2);

			rs = pstmnt.executeQuery();
			if (rs.next()) {
				retval = rs.getInt(1);
			} else {
				retval = -1;
			}
			return retval;
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static String tipoConcepto(Connection conn, String caNoContrarrecibo, String EP) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			pstmnt = conn.prepareStatement("SELECT TCONC_49 FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);

			rs = pstmnt.executeQuery();
			if (rs.next()) {
				retval = rs.getString(1);
			}
			return retval;
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static String folioDependencia(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			pstmnt = conn.prepareStatement("SELECT NCLC_43 FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? ");
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);

			rs = pstmnt.executeQuery();
			if (rs.next()) {
				retval = rs.getString(1);
			}
			return retval;
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static String getRFC(Connection conn, String caNoContrarrecibo, String ep) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			String queryRFC = "SELECT top(1) RFC FROM v_RFCReintegros with(NOLOCK) WHERE caNoContrarrecibo = ? and EP = ?";
			pstmnt = conn.prepareStatement(queryRFC);
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, ep);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				retval = rs.getString(1);
			}
			return retval;
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	
	}

	public static int[] getNDocRenglon(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int[] retval = new int[100];
		try {
			int i = 0;
			pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM vPagadoyRectificacion with(nolock) WHERE caNoContrarrecibo=? AND EP=? and nMes=? order by mImporte");
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			rs = pstmnt.executeQuery();
			retval[0] = -1;
			while (rs.next()) {
				retval[i] = rs.getInt(1);
				i++;
			}
			return retval;
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static int getDetallePasoTotal(Connection conn, int folio) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval = 0;
		try {
			pstmnt = conn.prepareStatement("SELECT count(*) FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?");
			pstmnt.setInt(1, folio);

			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				retval = rs.getInt(1);
			} else {
				retval = -1;
			}
			return retval;
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}	
	}

	public static boolean borraReintegro(Connection conn, int folioReintegro) throws SQLException {
		PreparedStatement pstmnt = null, pst2 = null;
		boolean retval = false;
		String queryEnc = "DELETE FROM tReintegroEncabezado WHERE nFolioReintegro = ?";
		String queryDet = "DELETE FROM tReintegroDetalle WHERE nFolioReintegro = ?";
		
		try {
			pstmnt = conn.prepareStatement(queryDet);
			pstmnt.setInt(1, folioReintegro);
			pstmnt.execute();
			
			pst2 = conn.prepareStatement(queryEnc);
			pst2.setInt(1, folioReintegro);
			pst2.execute();
		}  finally {
			
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( pst2 );
		}
		return retval;
	}

	public static void insertaReintegro(Connection conn, ReintegroEncabezadoMil reinE, ArrayList<ReintegroDetalleMil> reinDetalles, int folio, String folioCompleto, Usuario usuario, String cIngresoPropio) throws Exception {
		PreparedStatement psInsertDetalle = null;
		PreparedStatement psInsertEncabezado = null;
		try {
			psInsertEncabezado = conn.prepareStatement("INSERT INTO tReintegroEncabezado(nFolioReintegro,fSolicitud,cTipoReintegro,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit,ID_TIPOREINTEGRO,cDescripcionPoliza ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			psInsertEncabezado.setInt(1, folio); // folio del reintegro
		    psInsertEncabezado.setString(2, reinE.getfSolicitud());			 //fecha de hoy, pasar como parametro o recalcular
		    psInsertEncabezado.setString(3, reinE.getcTipoReintegro()); 		 //cTipoReintegro dependiendo si es de compromiso, directa		    
			psInsertEncabezado.setString(4, reinE.getcRamo()); // cRamo
			psInsertEncabezado.setString(5, reinE.getcUnidadResponsable()); // cUnidadResponsable
			psInsertEncabezado.setString(6, reinE.getcDocumentoHaplicado());
		    psInsertEncabezado.setString(7, reinE.getaEjercicioFiscal());  //aEjercicioFiscal (2012,2013...)
			psInsertEncabezado.setString(8, reinE.getObservaciones()); // observaciones
			psInsertEncabezado.setString(9, reinE.getConcepto()); // concepto
			psInsertEncabezado.setString(10, usuario.getLogin());
		    psInsertEncabezado.setString(11, reinE.getFormaDePago());		 //forma de pago (efectivo, transferencia) [catalogo]
		    psInsertEncabezado.setString(12, reinE.getClvRastreo());	 //clave de rastreo
		    psInsertEncabezado.setString(13, reinE.getFichaDeposito());	//ficha de deposito en el banco
		    psInsertEncabezado.setString(14, reinE.getClvBanco());			//clave del pago en el banco
		    psInsertEncabezado.setString(15, reinE.getCuentaBancaria());			//cuenta bancaria
		    psInsertEncabezado.setString(16, reinE.getLc());					//linea de captura clave
		    psInsertEncabezado.setDouble(17, Double.parseDouble(reinE.getImporteLC()));		//importe de la linea de captura
			if("S".equals(cIngresoPropio))
				psInsertEncabezado.setString(18, "PD"); // cTipoPoliza
			else psInsertEncabezado.setString(18, reinE.getcTipoPoliza()); // cTipoPoliza
			psInsertEncabezado.setString(19, reinE.getfAplicacion());
			psInsertEncabezado.setString(20, reinE.getcUnidadResponsableContable());
			psInsertEncabezado.setString(21, reinE.getFolioDependencia());
			psInsertEncabezado.setString(22, reinE.getMovimiento());
			psInsertEncabezado.setString(23, reinE.getAviso());
			psInsertEncabezado.setString(24, reinE.getTipoAviso());
			psInsertEncabezado.setString(25, reinE.getCausaAviso());
			psInsertEncabezado.setString(26, reinE.getfAcreditacion());
			if("S".equals(cIngresoPropio)){
				psInsertEncabezado.setInt(27, 2);					//tipo Presupuestal IF - 0 Presupuestal IP - 2 Contable IF - 1 Contable IP - 5
		    }else{
		    	psInsertEncabezado.setInt(27, reinE.getnId_TipoReintegro());
		    }
			psInsertEncabezado.setString(28, "POR LA CREACION DEL PASIVO A FAVOR DE LA TESOFE PARA REINTEGRO DE RECURSOS FISCALES REINTEGRO No. " + folio + " " );
			psInsertEncabezado.execute();

			psInsertDetalle = conn.prepareStatement("INSERT INTO tReintegroDetalle (nFolioReintegro,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,obgt,ctab,FFM,mPasivoDiferido,cPasivo,mImportePC)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");  /*,mPasivoDiferido*/
			for (Iterator<ReintegroDetalleMil> i = reinDetalles.iterator(); i.hasNext();) {
				ReintegroDetalleMil rd = i.next();
				psInsertDetalle.setInt(1, reinE.getnFolioReintegro());
				psInsertDetalle.setInt(2, Integer.parseInt(rd.getnDocRenglon().replace(".0", "")));
				psInsertDetalle.setString(3, rd.getnSIAFF());
				psInsertDetalle.setString(4, rd.getSecCLC());
				psInsertDetalle.setString(5, rd.getcEvento());
				psInsertDetalle.setString(6, rd.getEP());
				psInsertDetalle.setDouble(7, rd.getmImporteCLC());
				psInsertDetalle.setDouble(8, rd.getmImporteCLC());
				psInsertDetalle.setDouble(9, rd.getmImporteCLC() * -1);
				psInsertDetalle.setInt(10, rd.getMes());
				psInsertDetalle.setString(11, rd.getcCentroContable());
				psInsertDetalle.setString(12, rd.getcPartida());
				psInsertDetalle.setString(13, rd.getCxp());
				psInsertDetalle.setString(14, rd.getRfc());
				psInsertDetalle.setString(15, rd.getRenglonPagado());
				psInsertDetalle.setString(16, rd.getFolioDependenciaSicop());
				psInsertDetalle.setString(17, rd.getAlm());
				psInsertDetalle.setString(18, rd.getObgt());
				psInsertDetalle.setString(19, rd.getCtab());
				psInsertDetalle.setString(20, rd.getFFM());
				psInsertDetalle.setDouble(21, 0.00);
				psInsertDetalle.setString(22, rd.getcPasivo());
				psInsertDetalle.setDouble(23, rd.getmImportePC());
				psInsertDetalle.addBatch();
			}

			psInsertDetalle.executeBatch();
			conn.commit();
		}  finally {
			CloseObject.closeObject(psInsertDetalle, false);
			CloseObject.closeObject( psInsertEncabezado , false);
		}

	}

	public static boolean validaCatMovimiento(Connection conn, String texto) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoMvto with(nolock) WHERE tipo = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if (res.next()) {
				if (res.getInt(1) > 0)
					existe = true;
			}
			return existe;
		}  finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
	}

	public static boolean validaCatTipoAviso(Connection conn, String texto) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoTipoCausaAviso with(nolock) WHERE cTipoCausaAviso = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if (res.next()) {
				if (res.getInt(1) > 0)
					existe = true;
			}
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}

		return existe;
	}

	public static boolean validaCatFormaPago(Connection conn, String texto) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoFormaPago with(nolock) WHERE cFormaPago = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if (res.next()) {
				if (res.getInt(1) > 0)
					existe = true;
			}

			return existe;
			
		}  finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
	}

	public static boolean validaCatCausaAviso(Connection conn, String texto) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoCausaAviso with(nolock) WHERE cCausaAviso = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if (res.next()) {
				if (res.getInt(1) > 0)
					existe = true;
			}
			return existe;
			
		} finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
	}

	public static ReintegroDetalleMil datosSICOPMil(Connection conn, String fSIAFF, String EP, String folio, String movimiento, String concepto) throws SQLException {
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroDetalleMil mil = new ReintegroDetalleMil();
		try {
			pstm = conn.prepareStatement("SELECT * FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? and CONC_MOV_50=? and TCONC_49=?");
			pstm.setString(1, fSIAFF);
			pstm.setString(2, EP);
			pstm.setString(3, movimiento);
			pstm.setString(4, concepto);
			
			res = pstm.executeQuery();
			if (res.next()) {
				mil.setSecCLC(res.getString("SEC"));
				mil.setFolioDependenciaSicop("NCLC_43");
			}

			return mil;
			
		}  finally {
			CloseObject.closeObject( pstm );
			CloseObject.closeObject( res );
		}
	}

	public static int getRenglonPagadoMil(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, String concepto, int movimiento) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval = -1;
		try {
			pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=?  and ID_TIPO_MOVIMIENTO=? and ID_TIPO_CONCEPTO=?");
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			pstmnt.setInt(4, movimiento);
			pstmnt.setString(5, concepto);
			
			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				retval = rs.getInt(1);
			}
			
			return retval;
			
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
	}

	public static String getALM(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			pstmnt = conn.prepareStatement("SELECT ALM FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon=?");
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			pstmnt.setInt(4, renglon);
			
			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				retval = rs.getString(1);
			}
			
			return retval;
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static String getCCNormal(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			pstmnt = conn.prepareStatement("SELECT  pd.cCentroContable FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE pe.caNoContrarrecibo=? and pd.EP=? and pd.nMes=? and pd.nDocRenglon=? and pd.nFolioPagado=pe.nFolioPagado");
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			pstmnt.setInt(4, renglon);
		
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				retval = rs.getString(1);
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return retval;
	}

	public static boolean getTipoCLC(Connection conn, int folioCLC) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		boolean retval = false;
		try {
			pstmnt = conn.prepareStatement("select TIPO_CLC from CLC_SIAFF_ENC with(nolock), tReintegroDetalleMil with(nolock) where FOLIO_CLC=tReintegroDetalleMil.noCLC and tReintegroDetalleMil.nFolioReintegroMil=?");
			pstmnt.setInt(1, folioCLC);
			rs = pstmnt.executeQuery();
			while (rs.next()) {
				if ("COMPENSADA".equals(rs.getString(1)))
					retval = true;
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return retval;
	}

	public static String getListaCorreos(Connection conn, Caso c) throws SQLException {
		String to = "";
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		String sSQL = "select distinct u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=?)";
		try {
				pstmnt = conn.prepareStatement(sSQL);
				if (c != null && c.getFolio() != null)
					pstmnt.setString(1, c.getFolio());
				else
					pstmnt.setString(1,"BXX");//no encontrara nada, solo es para que corra el qry
				rs = pstmnt.executeQuery();
				boolean enviar = false;
				while (rs.next()) {
					to += (enviar ? ";" : "") + rs.getString(1);
					enviar = true;
				}
				return to;		
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static String getCorreoRevisor(Connection conn, Caso c) throws SQLException {
		String to = "";
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String sSQL = "select top(1) u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=? and B_ID_OPER=2)";
		boolean enviar = false;
		
		try {
			pstmnt = conn.prepareStatement(sSQL);
			if (c != null && c.getFolio() != null)
				pstmnt.setString(1, c.getFolio());
			else
				pstmnt.setString(1,"BXX");//no encontrara nada, solo es para que corra el qry
		
			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				to = (enviar ? ";" : "") + rs.getString(1);
				enviar = true;
			}
		
			return to;
			
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
	}

	public static boolean actualizaFechaAplicacion(Connection conn, int folio, String fAplicacion) throws SQLException, ParseException {
		PreparedStatement pstmntUp = null;
		boolean retval = false;
		try {
			pstmntUp = conn.prepareStatement("UPDATE tReintegroEncabezado SET fAplicacion=? WHERE nFolioReintegro=?");
			pstmntUp.setString(1, fAplicacion);
			pstmntUp.setInt(2, folio);
			retval = pstmntUp.execute();
			return retval;
		} finally {
			CloseObject.closeObject( pstmntUp );
		}
	}

	public static String validaMes(Connection conn, int folio, String fAcredit) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		int mes = Integer.parseInt(fAcredit.substring(3,5));;

		try {
			pstmnt = conn.prepareStatement("SELECT mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cCentroContable = (SELECT TOP 1 cCentroContable FROM dbo.tReintegroDetalle WHERE nFolioReintegro = ?) AND nMes = ?");
			pstmnt.setInt(1, folio);
			pstmnt.setInt(2, mes);
			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				retval = rs.getString(1);
			}
			return retval;
			
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}		
	}

	public static String validaMes(Connection conn, Caso c, int folio, String fAcredit) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		int mes = Integer.parseInt(fAcredit.substring(3,5));;

		try {
			pstmnt = conn.prepareStatement("SELECT CASE WHEN MONTH(GETDATE())= ? THEN 'S' ELSE mesAbierto END AS mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cCentroContable = (SELECT TOP 1 cCentroContable FROM dbo.tReintegroDetalle WHERE nFolioReintegro = ?) AND nMes = ?");
			pstmnt.setInt(1, mes);
			pstmnt.setInt(2, folio);
			pstmnt.setInt(3, mes);
			
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				retval = rs.getString(1);
			}
			return retval;
			
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static boolean getValidaGeneraRecepcion(Connection conn, int folioReint) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		PreparedStatement pstmnt2 = null;
		ResultSet rs2 = null;
		Boolean bValor = false;
		String sEsPagoDiv = "";
		String cIdContrato = "";
		String cIdRecepcion = "";
		String query = " SELECT 	CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS esPagoDiv, "
				+  " LTRIM(RTRIM(cFolioPAGODIVERSO)) AS cIdContrato, cIdRecepMat AS cIdRecepcion "
				+  " FROM 	tPAGODIVERSOEncabezado WITH (NOLOCK) "
				+  " WHERE 	caNoContrarrecibo = (SELECT TOP 1 cxp FROM tReintegroDetalle WITH (NOLOCK) WHERE nFolioReintegro = ? ) "
				+  " GROUP BY cFolioPAGODIVERSO, cIdRecepMat ";

		try {
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReint);
			rs = pstmnt.executeQuery();

			if (rs.next()) {
				sEsPagoDiv = rs.getString(1);
				cIdContrato = rs.getString(2);
				cIdRecepcion = rs.getString(3);

				if ("1".equals(sEsPagoDiv)) {
					String sTieneRecep = "";
					String query2 =    " SELECT CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS tieneRecep "
									+  " FROM 	tReintegroRecepcionEncabezado WITH (NOLOCK) "
									+  " WHERE 	nFolioReintegro = ? AND cIdContrato = ? AND cIdRecepcion = ? ";

					pstmnt2 = conn.prepareStatement(query2);
					pstmnt2.setInt(1, folioReint);
					pstmnt2.setString(2, cIdContrato);
					pstmnt2.setString(3, cIdRecepcion);
					rs2 = pstmnt2.executeQuery();

					if (rs2.next()) {
						sTieneRecep = rs2.getString(1);
						if ("1".equals(sTieneRecep)) {
							if (generaRecepcionMaterial(conn, folioReint, cIdContrato, cIdRecepcion)) {
								bValor = true;
							}
						}
					} else {
						bValor = true;
					}
				}
			} else {
				bValor = true;
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( pstmnt2 );
			CloseObject.closeObject( rs );
			CloseObject.closeObject( rs2 );
		}
		return bValor;
	}

	public static boolean generaRecepcionMaterial(Connection conn, int folioReint, String cIdContrato, String cIdRecepcion) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		boolean retval = false;
		try {

			pstmnt = conn.prepareStatement(" EXEC generaRecepcionMaterialReintegro ?, ?, ? ");
			pstmnt.setInt(1, folioReint);
			pstmnt.setString(2, cIdContrato);
			pstmnt.setString(3, cIdRecepcion);
			rs = pstmnt.executeQuery();

			if (rs.next()) {
				if ("Exito".equals(rs.getString(1))) {
					retval = true;
				}
			}
			return retval;
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
	}

	public static ArrayList<String> getDocumentosAnexos(Connection conn, int idGabinete, String destino) throws SQLException {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		ArrayList<String> documentos = new ArrayList<String>();

		StringBuffer sSQL = new StringBuffer();

		sSQL.append(" SELECT d.nombre_documento");
		sSQL.append(" FROM imx_documento d, imx_pagina p, imx_volumen v ");
		sSQL.append(" WHERE d.titulo_aplicacion = p.titulo_aplicacion AND d.id_gabinete = p.id_gabinete ");
		sSQL.append(" AND d.id_carpeta_padre = p.id_carpeta_padre AND d.id_documento = p.id_documento ");
		sSQL.append(" AND v.volumen = p.volumen AND p.titulo_aplicacion = '" + destino + "' AND p.id_gabinete = " + idGabinete);
		sSQL.append(" ORDER BY p.numero_pagina");

		String sql = sSQL.toString();
		try {
			pstmnt = conn.prepareStatement(sql);

			rs = pstmnt.executeQuery();
			while (rs.next()) {
				documentos.add(rs.getString("nombre_documento"));
			}

			return documentos;

		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}
	
	public static String getEsRadicado(Connection conn, String caNoContrarrecibo) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			String queryEsRadicado = "SELECT cRadicado FROM tPagadoEncabezado AS PAG WITH (NOLOCK) " +
									 "JOIN ( " +
									 "		SELECT caNoContrarrecibo, cRadicado FROM tPAGODIVERSOEncabezado AS DIV WITH (NOLOCK) " +
									 "		WHERE cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, 'N' AS cRadicado FROM tPagoDirectoEncabezado AS DIR WITH (NOLOCK) " +
									 "		WHERE cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, cRadicado FROM tPAGOOBRAEncabezado AS OBR WITH (NOLOCK) " +
									 "		WHERE cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, cRadicado FROM tRELACIONGASTOSEncabezado AS REL WITH (NOLOCK) " +
									 "		WHERE cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, 'N' AS cRadicado FROM tPAGOFEDERALIZADOEncabezado AS FED WITH (NOLOCK) " +
									 "		WHERE cDocumentoHaplicado = 'S' " +
									 "	) AS TRA ON PAG.caNoContrarrecibo = TRA.caNoContrarrecibo " +
									 "WHERE cDocumentoHaplicado = 'S' AND PAG.caNoContrarrecibo = ? ";
			pstmnt = conn.prepareStatement(queryEsRadicado);
			pstmnt.setString(1, caNoContrarrecibo);
			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				retval = rs.getString(1);
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		return retval;
	}
	
	public static String getEventoReintegroApartado(Connection conn, String CxP, String EP) throws SQLException {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String evento = "";		
		
		String sSQL = "SELECT DISTINCT CASE "
                     +"    WHEN ctipopago='PAGOOBRA' AND cEvento NOT IN( 'REIN_TRAM_IR','REIN_TRAM_IP' ) THEN 'RC_IN_' " 
                     +"    WHEN ctipopago='PAGODIVERSO' AND cEvento NOT IN( 'REIN_TRAM_IR','REIN_TRAM_IP' ) THEN 'RC_IN_' "
                     +"    WHEN ctipopago='PAGODIRECTO' THEN 'RD_IN_' "                    
                     +"    WHEN ctipopago='NOMINA' THEN 'RD_IN_' "                     
                     +"    WHEN ctipopago='FEDERALIZADO' THEN 'RC_IN_' "
                     +"    WHEN ctipopago='RELACIONGASTOS' THEN 'RD_IN_' "
                     +"  END + SUBSTRING(cEvento, 3, LEN(cEvento)) AS cEvento "
					 +" FROM    vPagadoyRectificacion "
					 +" WHERE   caNoContrarrecibo = '" + CxP + "' "													   
					 +"		AND EP = '" + EP + "' ";								 
		try {
			pstmnt = conn.prepareStatement(sSQL);

			rs = pstmnt.executeQuery();
			while (rs.next()) {				
				evento = rs.getString("cEvento");
			}
			return evento;

		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}
	
	public static String getEsIngresoPropio(Connection conn, String caNoContrarrecibo) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			String queryEsRadicado = "SELECT cIngresosPropios FROM tPagadoEncabezado AS PAG WITH (NOLOCK) " +
									 "JOIN ( " +
									 "		SELECT caNoContrarrecibo, cIngresosPropios FROM tPAGODIVERSOEncabezado AS DIV WITH (NOLOCK) " +
									 "		WHERE cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, CASE WHEN SUBSTRING(EP,40,1) = '4' THEN 'S' ELSE 'N' END AS cIngresosPropios FROM tPagoDirectoEncabezado AS DIR WITH (NOLOCK) " +
									 "		JOIN tPagoDirectoDetalle AS PDET WITH(NOLOCK) ON DIR.nFolioPagoDirecto = PDET.nFolioPagoDirecto " +
									 "		WHERE DIR.cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, cIngresosPropios FROM tPAGOOBRAEncabezado AS OBR WITH (NOLOCK) " +
									 "		WHERE cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, CASE WHEN SUBSTRING(EP,40,1) = '4' THEN 'S' ELSE 'N' END AS cIngresosPropios FROM tRELACIONGASTOSEncabezado AS REL WITH (NOLOCK) " +
									 "		JOIN tRELACIONGASTOSDetalle AS RDET WITH(NOLOCK) ON REL.nFolioRELACIONGASTOS = RDET.nFolioRELACIONGASTOS " +
									 "		WHERE REL.cDocumentoHaplicado = 'S' " +
									 "		UNION " +
									 "		SELECT caNoContrarrecibo, CASE WHEN SUBSTRING(EP,40,1) = '4' THEN 'S' ELSE 'N' END AS cIngresosPropios FROM tPAGOFEDERALIZADOEncabezado AS FED WITH (NOLOCK) " +
									 "		JOIN tPAGOFEDERALIZADODetalle AS PDET WITH(NOLOCK) ON FED.nFolioPAGOFEDERALIZADO = PDET.nFolioPAGOFEDERALIZADO " +		
									 "		WHERE FED.cDocumentoHaplicado = 'S' " +
									 "	) AS TRA ON PAG.caNoContrarrecibo = TRA.caNoContrarrecibo " +
									 "WHERE cDocumentoHaplicado = 'S' AND PAG.caNoContrarrecibo = ? ";
			pstmnt = conn.prepareStatement(queryEsRadicado);
			pstmnt.setString(1, caNoContrarrecibo);
			
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				retval = rs.getString(1);
			}
			return retval;
			
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
	}

	public static String getSubcuentaFFM(Connection conn, String cxp, String ep) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String ffm = "";			
		String sSQL = "SELECT FFM FROM ( " 
					+ "SELECT DISTINCT enc.caNoContrarrecibo "
					+ ", CASE WHEN RECTIFICACIONDET.EP IS NULL THEN det.EP ELSE RECTIFICACIONDET.EP END EP "
					+ ", CASE WHEN RECTIFICACIONDET.FFM IS NULL THEN det.FFM ELSE RECTIFICACIONDET.FFM END FFM FROM tPAGOFEDERALIZADOEncabezado enc  "
					+ "JOIN tPAGOFEDERALIZADODetalle det ON enc.nFolioPAGOFEDERALIZADO = det.nFolioPAGOFEDERALIZADO  "
					+ "LEFT JOIN tRectificacionAutEncabezado RECTIFICACION ON enc.caNoContrarrecibo = RECTIFICACION.caNoContrarrecibo AND RECTIFICACION.cDocumentoHaplicado = 'S' "
					+ "LEFT JOIN tRectificacionAutDetalle RECTIFICACIONDET ON RECTIFICACION.nFolioRectificacionAut = RECTIFICACIONDET.nFolioRectificacionAut AND RECTIFICACIONDET.cEvento LIKE 'DEBE%' "
					+ ") TABLA_FFM  "
					+ "WHERE caNoContrarrecibo = ? AND EP = ? ";			       
		
		try {
			pstmnt = conn.prepareStatement(sSQL);
			pstmnt.setString(1, cxp);
			pstmnt.setString(2, ep);

			rs = pstmnt.executeQuery();
			while (rs.next()) {				
				ffm = rs.getString("FFM");
			}

			return ffm;
			
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}

	public static void insertaPasivoDiferido(Connection conn, ReintegroEncabezadoMil reinE, ArrayList<ReintegroDetalleMil> reinDetalles, int folio, String folioCompleto, Usuario usuario, String cIngresoPropio) throws Exception {
		PreparedStatement psInsertDetalle = null;		
		
		try {
			psInsertDetalle = conn.prepareStatement("INSERT INTO tReintegroDetalle (nFolioReintegro, nDocRenglon, noCLC, secCLC, EP, mImporteCLC, cMes, movto, cEvento, mImporte, mImporteNegativo, cCentroContable, nCapitulo, cxp, RFC, nRenglonPagado,  nFolioDependencia, ALM, OBGT, CTAB, FFM, mPasivoDiferido) "
													+ " SELECT nFolioReintegro "
													+ "			, nDocRenglon + (SELECT COUNT(nFolioReintegro) " 
													+ "							 FROM tReintegroDetalle WITH (NOLOCK) " 
													+ "							 WHERE nFolioReintegro = ?) AS nDocRenglon "
													+ "			, noCLC "
													+ "			, secCLC "
													+ "			, EP "
													+ "			, 0 AS mImporteCLC "
													+ "			, cMes "
													+ "			, NULL AS movto "
													+ "			, cEvento "
													+ "			, 0 AS mImporte "
													+ "			, 0 AS mImporteNegativo " 
													+ "			, cCentroContable " 
													+ "			, nCapitulo " 
													+ "			, cxp "
													+ "			, 'TESOFE' AS RFC " 
													+ "			, nRenglonPagado "
													+ "			, nFolioDependencia "
													+ "			, ALM "
													+ "			, OBGT "
													+ "			, CTAB "
													+ "			, FFM "
													+ "			, mImporte "
													+ "	FROM tReintegroDetalle WITH (NOLOCK) " 
													+ "	WHERE nFolioReintegro = ?");								
			psInsertDetalle.setInt(1, folio);
			psInsertDetalle.setInt(2, folio);
			psInsertDetalle.execute();
			
		} finally {
			CloseObject.closeObject(psInsertDetalle, false);
		}

	}
	
public static boolean validaSIAmortiza( Connection conn, int folio ) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String amortizacion = null;
		boolean esAmortizacion = false;		
		
		try {
			ps = conn.prepareStatement( "SELECT CASE WHEN SUM(DET.mAmortizacionAnticipo) > 0 THEN 'S' ELSE 'N' END AS amortizacion \r\n" +
										" FROM tPAGODIVERSOEncabezado ENC \r\n" + 
										" JOIN tPAGODIVERSODetalle DET ON ENC.nFolioPAGODIVERSO = DET.nFolioPAGODIVERSO\r\n" + 
										" JOIN tReintegroDetalle REINT ON ENC.caNoContrarrecibo = cxp\r\n" + 
										" WHERE nFolioReintegro = ? " );
			ps.setInt(1, folio);
			
			rs = ps.executeQuery();
			if(rs.next()){
				amortizacion = rs.getString(1);
			}
			
			if(amortizacion.equals( "S" ))
				esAmortizacion =  true;
						
			return esAmortizacion;
			
		} finally {
			CloseObject.closeObject(ps, false);
			CloseObject.closeObject(rs, false);
		}
	}

	public static boolean actualizaAmortizacion( Connection conn, int folio ) throws Exception {
		PreparedStatement pstmntUp = null;
		boolean retval =false;
		
		try {
		    pstmntUp = conn.prepareStatement("UPDATE REINT SET REINT.mAmortizacionAnticipo = DET.mAmortizacionAnticipo " + 
								    		 "FROM tPAGODIVERSOEncabezado ENC " + 
								    		 "	JOIN tPAGODIVERSODetalle DET ON ENC.nFolioPAGODIVERSO = DET.nFolioPAGODIVERSO " + 
								    		 "	JOIN tReintegroAutDetalle REINT ON ENC.caNoContrarrecibo = cxp " + 
								    		 "WHERE nFolioReintegroaut = ? AND DET.nDocRenglon = REINT.nDocRenglon");		    
			pstmntUp.setInt(1, folio);
			retval = pstmntUp.execute();
				
			return retval;
			
		} finally {
			CloseObject.closeObject(pstmntUp, false);
		}
	}
	/*
	public static void updateEventoFA(Connection conn, int nFolio) throws Exception {
		
		String queryUpdate =  " UPDATE	treintegroautdetalle "
							+ " SET		cevento = REPLACE(cevento, '_EG_', '_EG_FA_') "
							+ " WHERE	nFolioReintegroaut = ? ";
		
		PreparedStatement psUpdate = null;		
		
		try {

			psUpdate = conn.prepareStatement(queryUpdate);

			psUpdate.setInt(1, nFolio);					

			int afectados = psUpdate.executeUpdate();
			log.debug("Se actualizaron : " + afectados + " registros del detalle del Folio de Consolidación: " + nFolio);			
		
		} finally {			
			CloseObject.closeObject(psUpdate);
		}
	}*/

	public static boolean esPagoSICOP( Connection conn, int folio ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String pagoSICOP = null;
		boolean espagoSICOP = false;		
		
		try {
			ps = conn.prepareStatement( "SELECT DISTINCT CASE WHEN cTipoPago IN ('RELACIONGASTOS') OR SUBSTRING(EP,42,1) = 4  THEN 'N' ELSE 'S' END pagoSICOP " + 
										"FROM tReintegroDetalle REINT " + 
										"JOIN tPagadoEncabezado PAG ON REINT.cxp = PAG.caNoContrarrecibo " + 
										"WHERE nFolioReintegro = ?" );
			ps.setInt(1, folio);
			
			rs = ps.executeQuery();
			if(rs.next()){
				pagoSICOP = rs.getString(1);
			}
			
			if(pagoSICOP.equals( "S" ))
				espagoSICOP =  true;
						
			return espagoSICOP;
				
		}  finally {
			CloseObject.closeObject( ps, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static void insertaReintegroPasivo( Connection conn, int folio ) throws Exception {
		PreparedStatement pstmnt = null;		
		
		try {
				pstmnt = conn.prepareStatement("INSERT INTO tReintegroDetalle (nFolioReintegro,nDocRenglon,noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento,mImporte,mImporteNegativo,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,OBGT,CTAB,FFM,mPasivoDiferido) " +
												"SELECT nFolioReintegro\r\n" + 
												"	, nDocRenglon + (SELECT COUNT(*) FROM tReintegroDetalle WHERE nFolioReintegro = ?)\r\n" + 
												"	, noCLC\r\n" + 
												"	, secCLC\r\n" + 
												"	, EP\r\n" + 
												"	, 0\r\n" + 
												"	, cMes\r\n" + 
												"	, movto\r\n" + 
												"	, cEvento\r\n" + 
												"	, 0\r\n" + 
												"	, 0\r\n" + 
												"	, cCentroContable\r\n" + 
												"	, nCapitulo\r\n" + 
												"	, cxp\r\n" + 
												"	, 'TESOFE'\r\n" + 
												"	, 0\r\n" + 
												"	, nFolioDependencia\r\n" + 
												"	, ALM\r\n" + 
												"	, OBGT\r\n" + 
												"	, CTAB\r\n" + 
												"	, FFM\r\n" + 
												"	, mImporte\r\n" + 
												"FROM tReintegroDetalle \r\n" + 
												"WHERE nFolioReintegro = ?\r\n" + 
												"");
				pstmnt.setInt(1, folio);
				pstmnt.setInt(2, folio);
				pstmnt.execute();
				
			}  finally {
				CloseObject.closeObject(pstmnt, false);
			}		
	}
	
	public static String getEsAjena(Connection conn, String caNoContrarrecibo) throws SQLException {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval = "";
		try {
			String queryEsAjena = "SELECT CASE WHEN cTipoPago = 'AJENAS' THEN 'S' ELSE 'N' END cAjena FROM tPagadoEncabezado WHERE caNoContrarrecibo = ? ";
			
			pstmnt = conn.prepareStatement(queryEsAjena);
			pstmnt.setString(1, caNoContrarrecibo);
			
			rs = pstmnt.executeQuery();
			
			if (rs.next()) {
				retval = rs.getString(1);
			}
			
			return retval;
		}  finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
	}
	
	public static String getEventoReintegroAjena(Connection conn, String CxP, String EP) throws SQLException {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String evento = "";				
		String sSQL = "SELECT DISTINCT CASE "
                     +"    WHEN cEvento LIKE '%LAUDOS' THEN 'RD_' " 
                     +"    WHEN cEvento LIKE '%IP'	THEN 'RD_' "                     
                     +"    ELSE 'RC_IN_' "
                     +"  END + SUBSTRING(cEvento, 3, LEN(cEvento)) AS cEvento "
					 +" FROM    vPagadoyRectificacion "
					 +" WHERE   caNoContrarrecibo = '" + CxP + "' "													   
					 +"		AND EP = '" + EP + "' ";								 					 		
		try {
			pstmnt = conn.prepareStatement(sSQL);

			rs = pstmnt.executeQuery();
			while (rs.next()) {				
				evento = rs.getString("cEvento");
			}

			return evento;

		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
	}
	
	public static boolean insertarDetalle( Connection conn, List<ReintegroBoletos> boletos ) throws Exception {
		PreparedStatement pstmnt = null;
		String insertQuery = "INSERT INTO tReintegroBoletajeAvion (nfolioReintegro, nFolioPagoDiverso,RFC,cNombre,cReferencia,mTotal,	cPartida) "
				+ " VALUES (?,?,?,?,?,?,?)";
		
		try {
			pstmnt = conn.prepareStatement( insertQuery );
			
			for ( int i = 0; i < boletos.size(); i++ ) {
				ReintegroBoletos rb = new ReintegroBoletos();
				rb = boletos.get( i );
				
				pstmnt.setInt( 1, rb.getNfolioReintegro());
				pstmnt.setInt( 2, rb.getnFolioPagoDiverso());
				pstmnt.setString( 3, rb.getRFC());
				pstmnt.setString( 4, rb.getcNombre() );
				pstmnt.setString( 5, rb.getcReferencia() );
				pstmnt.setDouble( 6, rb.getmTotal() );
				pstmnt.setString( 7, rb.getcPartida() );
				
				pstmnt.execute();
				log.debug( "Boletos insertados" );
				pstmnt.clearParameters();
			}
			return true;
			
		} finally {
			CloseObject.closeObject( pstmnt );
		}
	}
	
	public static int insertaDetalleCompleto (Connection conn, String cxp, int nFolio) throws Exception {
		PreparedStatement pst = null;
		int renglones = 0;
		String query = "INSERT INTO tReintegroBoletajeAvion (nfolioreintegro, nFolioPagoDiverso, RFC, cNombre, cReferencia, mTotal, cPartida) VALUES "
				+ "					SELECT ? ,  bol.nFolioPagoDiverso, bol.RFC, cNombre, cReferencia, mTotal, cPartida "
				+ "					FROM tPagoDiversoBoletajeAvion BOL "
				+ "					INNER JOIN tPAGODIVERSOEncabezado PAGO ON BOL.nFolioPagoDiverso = PAGO.nFolioPAGODIVERSO "
				+ "					WHERE PAGO.caNoContrarrecibo = ? ";
		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, nFolio );
			pst.setString( 2, cxp );
			renglones = pst.executeUpdate();
			
			log.debug( "Boletos insertados" );			
			
			return renglones;
		} finally {
			CloseObject.closeObject( pst );
		}
		
		
	}
	
	public static void descartaReintegro (Connection conn,String u_login,int nFolioReintegro) throws Exception {
		String queryEnc = "DELETE dbo.tReintegroRecepcionEncabezado WHERE nFolioReintegro = ?";
		String queryDet = "DELETE dbo.tReintegroRecepcionDetalle WHERE nFolioReintegro = ?";
		String query = "DELETE dbo.tReintegroBoletajeAvion WHERE nFolioReintegro = ?";
		
		PreparedStatement pstmnt = null, pst2 = null, pst3 = null;
		try {
			pstmnt = conn.prepareStatement( query );
			pstmnt.setInt( 1, nFolioReintegro );
			pstmnt.execute();
			
			pst2 = conn.prepareStatement( queryDet );
			pst2.setInt( 1, nFolioReintegro );
			pst2.execute();
			
			pst3 = conn.prepareStatement( queryEnc );
			pst3.setInt( 1, nFolioReintegro );
			pst3.execute();
		
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( pst2 );
			CloseObject.closeObject( pst3 );
		}
	}

	public static String getPasivo(Connection conn, String cxp) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String cPasivo = "";
		try {
			String query = "SELECT CASE WHEN cEvento IN ('P_PNCLRE01','P_DDNORE01') THEN cPasivo ELSE NULL END cPasivo\r\n"
						 + "FROM tPagadoEncabezado PE WITH (NOLOCK) JOIN tPagadoDetalle PD WITH (NOLOCK) ON PE.nFolioPagado = PD.nFolioPagado\r\n"
						 + "WHERE caNoContrarrecibo = ? AND RFC NOT IN ('LAUD000000000')";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setString(1, cxp);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				cPasivo = rs.getString(1);
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
		return cPasivo;
	}

	public static int tieneBoletosManager( Connection conn, String folio ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int valor = 0;
		String query = "select count (*) num from tReintegroBoletajeAvion where nfolioReintegro = ?";
		
		try {
			pst = conn.prepareStatement( query );
			pst.setString( 1, folio );
			rs = pst.executeQuery();
			
			log.debug( query );
			
			if (rs.next()) {
				valor = rs.getInt( 1 );
			}
				
			return valor;
			
		} finally {
			
			CloseObject.closeObject( rs );
			CloseObject.closeObject( pst );
			
		}
		
	}

	public static void liberarBoletos( Connection conn, String folio ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String query = "UPDATE vuelos set Status = 'A' FROM tReintegroBoletajeAvion reintegro "
				+ "inner join tLayoutVuelosDet vuelos on reintegro.cReferencia = vuelos.cReferencia and reintegro.RFC = vuelos.RFC "
				+ "WHERE reintegro.nFolioReintegro =  ? ";
		
		try {
			pst = conn.prepareStatement( query );
			pst.setString( 1, folio );
			pst.executeUpdate();
				
			log.debug( query );
			
		} finally {
			
			CloseObject.closeObject( rs );
			CloseObject.closeObject( pst );
			
		}
		
		
	}
	
	public static double getImportePC(Connection conn, String cxp) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		double mImportePC = 0.00;
		try {
			String query = "SELECT CASE WHEN cEvento IN ('P_PNCLRE01','P_DDNORE01') THEN SUM(mImporteBruto)*-1 ELSE 0 END AS mImportePC\r\n "
						+ " FROM tPagadoEncabezado PE WITH (NOLOCK) JOIN tPagadoDetalle PD WITH (NOLOCK) ON PE.nFolioPagado = PD.nFolioPagado\r\n "
						+ " WHERE caNoContrarrecibo = ? \r\n "
						+ " GROUP BY cEvento ";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setString(1, cxp);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				mImportePC = rs.getDouble(1);
			}
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( rs );
		}
		
		return mImportePC;
	}

	public static void updateReintegroFA( Connection conn, int folio ) throws Exception {
		PreparedStatement pst = null, pst2 = null;		
		String poliza = "UPDATE tReintegroAutEncabezado SET cTipoPoliza = 'DI' WHERE nFolioReintegroaut = ?";
		String evento = "UPDATE tReintegroAutDetalle SET cEvento = REPLACE(cEvento,'_EG_','_FA_') WHERE nFolioReintegroaut = ?";
		
		try {
			pst = conn.prepareStatement( poliza );
			pst.setInt( 1, folio );
			pst.executeUpdate();
			
			pst2 = conn.prepareStatement( evento );
			pst2.setInt( 1, folio );
			pst2.executeUpdate();
				
			log.debug( poliza );
			log.debug( evento );
			
		} finally {
						
			CloseObject.closeObject( pst );
			CloseObject.closeObject( pst2 );
			
		}
	}

	public void eliminarRelacionReintegroComision( Connection conn, Caso c ) throws Exception {
		int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
		
		PreparedStatement pst = null;		
		String comision = "DELETE FROM tComisionComprobacion WHERE nFolioRelacion = ? AND cTipoPago = 'REINTEGRO'";		
		
		try {
			pst = conn.prepareStatement( comision );
			pst.setInt( 1, folio );
			pst.executeUpdate();
				
			log.debug( comision );
						
		} finally {
						
			CloseObject.closeObject( pst );			
			
		}
		
		
	}

	public static void descartaReintegroComision( Connection conn, String u_login, int nFolioReintegro ) throws Exception {
		PreparedStatement pstmnt = null, pst2 = null;
		ResultSet rs = null;
		
		String esViatico = null;		
		
		try {
			String queryEsViatico = "SELECT CASE WHEN COUNT(*) > 0 THEN 'SI' ELSE 'NO' END esViatico \r\n"
					+ "				FROM tReintegroDetalle REINT WITH (NOLOCK)\r\n"
					+ "				JOIN tPagadoEncabezado PAG WITH (NOLOCK) ON REINT.cxp = PAG.caNoContrarrecibo\r\n"
					+ "				JOIN tPagadoDetalle PAGDET WITH (NOLOCK) ON PAG.nFolioPagado = PAGDET.nFolioPagado \r\n"
					+ "				JOIN tEventoViaticos EVENTO WITH (NOLOCK) ON SUBSTRING(PAGDET.cEvento,3 + LEN(ID_TIPO_CONCEPTO),4) = EVENTO.cEvento\r\n"
					+ "				WHERE nFolioReintegro = ?";
			
			pstmnt = conn.prepareStatement(queryEsViatico);
			pstmnt.setInt(1, nFolioReintegro);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				esViatico = rs.getString(1);
			}
			
			if ("SI".equals(esViatico)) {						
				String query = "DELETE dbo.tComisionComprobacion WHERE nFolioRelacion = ? AND cTipoPago = 'REINTEGRO'";
				
				pst2 = conn.prepareStatement( query );
				pst2.setInt( 1, nFolioReintegro );
				pst2.execute();
			}			
		
		} finally {
			CloseObject.closeObject( pstmnt );
			CloseObject.closeObject( pst2 );
			CloseObject.closeObject( rs );
		}		
	}
	
	public static String buscaCXP( Connection conn, int folioCasoReintegro ) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String cxp = "";
		try {
		    pstmnt = conn.prepareStatement("SELECT TOP 1 cTipoPago FROM tReintegroDetalle RDER WITH (NOLOCK) JOIN tPagadoEncabezado PAG WITH (NOLOCK) ON RDER.cxp = PAG.caNoContrarrecibo WHERE nFolioReintegro = ?");
		    pstmnt.setInt(1, folioCasoReintegro);
		    
			rs = pstmnt.executeQuery();
			if(rs.next()){
				cxp = rs.getString(1);
			}else{
				cxp = "";
			}
		} catch(SQLException s){
			log.warn(s);
			s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return cxp;
	}
	
	public static int insertaCompromisoReintegro(Connection conn, ReintegroEncabezadoMil re, ArrayList<ReintegroDetalleMil> rd,int folio,String folioCompleto,Usuario usuario) throws Exception{		
		int insertados = 0;
		int strFolioCompromiso = 0;		
		PreparedStatement pstmntE = null;
		PreparedStatement pstmntD = null;
		
		ResultSet rs = null;
		
		PreparedStatement pstmUpSeqCompromiso = null;
		PreparedStatement pstmSeqCompromiso = null;
		
		//OBTENER SEQUENCE DEL COMRPMISO
		pstmUpSeqCompromiso= conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'COMPROMISO' ");
		pstmUpSeqCompromiso.executeUpdate();
		pstmSeqCompromiso = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'COMPROMISO' ");
		rs = pstmSeqCompromiso.executeQuery();
		
		if(rs.next()){
			strFolioCompromiso = rs.getInt("seq_value");
		}
		
		CFSequenceManager sequence = CFSequenceManager.getInstance();
		int seqFolio = 100000 + sequence.nextVal("CO-" + re.getcCentroContable());
		String contrarecibo = re.getcCentroContable() + "CO" + EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal() + String.valueOf(seqFolio);
		
		StringBuilder sqlInsertCompromisoEnc = new StringBuilder();
		sqlInsertCompromisoEnc.append("INSERT INTO tCompromisoEncabezado "); 
		sqlInsertCompromisoEnc.append("            (nFolioCompromiso,"); 
		sqlInsertCompromisoEnc.append("             fCarga, "); 
		sqlInsertCompromisoEnc.append("             cIdContrato, ");
		sqlInsertCompromisoEnc.append("             cTipoContrato, "); 
		sqlInsertCompromisoEnc.append("             fAplicacion, ");
		sqlInsertCompromisoEnc.append("             cCentroContable, "); 
		sqlInsertCompromisoEnc.append("             cRamo, ");
		sqlInsertCompromisoEnc.append("             cUnidadResponsable, ");
		sqlInsertCompromisoEnc.append("             cDocumentoHaplicado, ");
		sqlInsertCompromisoEnc.append("             caNoCompromiso, "); 
		sqlInsertCompromisoEnc.append("             nEnviadoSICOP, ");
		sqlInsertCompromisoEnc.append("             cTipoPoliza, ");
		sqlInsertCompromisoEnc.append("             nMes, ");
		sqlInsertCompromisoEnc.append("             aEjercicioFiscal, ");
		sqlInsertCompromisoEnc.append("             cUnidadResponsableContable, ");
		sqlInsertCompromisoEnc.append("             cDescripcionPoliza, ");
		sqlInsertCompromisoEnc.append("             usuario, ");
		sqlInsertCompromisoEnc.append("             cRadicado) ");
		sqlInsertCompromisoEnc.append("SELECT ? AS nFolioCompromiso, ");
		sqlInsertCompromisoEnc.append("       GETDATE() AS fCarga, ");
		sqlInsertCompromisoEnc.append("       ? AS nFolioReintegro, ");
		sqlInsertCompromisoEnc.append("       'AV' AS cTipoContrato, ");
		sqlInsertCompromisoEnc.append("       GETDATE() AS fAplicacion, ");
		sqlInsertCompromisoEnc.append("       ? AS cCentroContable, ");
		sqlInsertCompromisoEnc.append("       ENCABEZADO.cRamo, ");
		sqlInsertCompromisoEnc.append("       ENCABEZADO.cUnidadResponsable, ");
		sqlInsertCompromisoEnc.append("       'S' AS cDocumentoHaplicado, ");
		sqlInsertCompromisoEnc.append("       ? AS caNoCompromiso, ");
		sqlInsertCompromisoEnc.append("       NULL AS nEnviadoSICOP, ");
		sqlInsertCompromisoEnc.append("       'DI' AS cTipoPoliza, ");
		sqlInsertCompromisoEnc.append("       MONTH(GETDATE()) AS nMes, ");
		sqlInsertCompromisoEnc.append("       ENCABEZADO.aEjercicioFiscal, ");
		sqlInsertCompromisoEnc.append("       ENCABEZADO.cUnidadResponsableContable, ");
		sqlInsertCompromisoEnc.append("       'REGISTRO DEL DECREMENTO DEL REINTEGRO ' + ? AS cDescripcionPoliza, ");
		sqlInsertCompromisoEnc.append("       ENCABEZADO.U_LOGIN, ");
		sqlInsertCompromisoEnc.append("       'N' AS cRadicado  ");
		sqlInsertCompromisoEnc.append("FROM   dbo.tReintegroEncabezado ENCABEZADO WITH (nolock) ");
		sqlInsertCompromisoEnc.append("WHERE  ENCABEZADO.nFolioReintegro = ? ");
		
		StringBuilder sqlInsertCompromisoDet = new StringBuilder();
		sqlInsertCompromisoDet.append("INSERT INTO dbo.tcompromisodetalle");
		sqlInsertCompromisoDet.append("        ( nfoliocompromiso ,");
		sqlInsertCompromisoDet.append("          ndocrenglon ,");
		sqlInsertCompromisoDet.append("          ep ,");
		sqlInsertCompromisoDet.append("          cevento ,");
		sqlInsertCompromisoDet.append("          mimporte ,");
		sqlInsertCompromisoDet.append("          mimportenegativo ,");
		sqlInsertCompromisoDet.append("          cmes ,");
		sqlInsertCompromisoDet.append("          ccentrocontable) ");
		sqlInsertCompromisoDet.append("SELECT ? AS nFolioCompromiso, ");
		sqlInsertCompromisoDet.append("       Row_number()  OVER( ORDER BY ep, cMes ASC) AS nDocRenglon, ");
		sqlInsertCompromisoDet.append("       ep,  ");
		sqlInsertCompromisoDet.append("       'CMP001' AS cevento, ");
		sqlInsertCompromisoDet.append("       Sum(DETALLE.mImporte)*-1 AS mimportemasiva, ");
		sqlInsertCompromisoDet.append("       Sum(DETALLE.mImporte)*-1 AS mImporteNegativo, ");
		sqlInsertCompromisoDet.append("       DETALLE.cMes, ");
		sqlInsertCompromisoDet.append("       DETALLE.cCentroContable ");	
		sqlInsertCompromisoDet.append("FROM   dbo.tReintegroEncabezado ENCABEZADO WITH (nolock) ");
		sqlInsertCompromisoDet.append("INNER JOIN dbo.tReintegroDetalle DETALLE WITH (nolock) ");
		sqlInsertCompromisoDet.append("		  ON ENCABEZADO.nFolioReintegro = DETALLE.nFolioReintegro ");
		sqlInsertCompromisoDet.append("WHERE  ENCABEZADO.nFolioReintegro = ? ");
		sqlInsertCompromisoDet.append("GROUP  BY ep, ");
		sqlInsertCompromisoDet.append("       DETALLE.ccentrocontable, ");
		sqlInsertCompromisoDet.append("       DETALLE.cMes ");

		PreparedStatement psInsertaEncabezado = null;
		PreparedStatement psInsertaDetalle = null;
		
		ResultSet rsFolioConsolidacion = null;
	
		try{			
			log.debug("Query Insert Encabezado[" + sqlInsertCompromisoEnc + "]");
			log.debug("Query Insert Detalle[" + sqlInsertCompromisoDet + "]");
			
			pstmntE = conn.prepareStatement(sqlInsertCompromisoEnc.toString());
			pstmntD = conn.prepareStatement(sqlInsertCompromisoDet.toString());
			
			pstmntE.setInt(1, strFolioCompromiso);
			pstmntE.setString(2, folioCompleto);
			pstmntE.setString(3, re.getcCentroContable() );
			pstmntE.setString(4, contrarecibo);			
			pstmntE.setString(5, folioCompleto);
			pstmntE.setInt(6, folio);
			
			insertados += pstmntE.executeUpdate();
			log.debug("Insertados en encabezado: " + insertados + " registros ");
						
			pstmntD.setInt(1, strFolioCompromiso);
			pstmntD.setInt(2, folio);
			
			insertados += pstmntD.executeUpdate();
			log.debug("Insertados en detalle: " + insertados + " registros ");
			
			return insertados;
			
		}finally{
			CloseObject.closeObject(rsFolioConsolidacion, false);
			CloseObject.closeObject(psInsertaEncabezado, false);
			CloseObject.closeObject(psInsertaDetalle, false);
		}		
	}
	
	public static String getDecrementoEncabezado(Connection conn, int folio, String cFolio) throws Exception {
		String arrListaComp = "";
		PreparedStatement pstmntH = null;
		ResultSet rs = null;
		
        StringBuilder Sql = new StringBuilder();

        //RELACION DE GASTOS INTEGRADAS
        
        Sql.append("SELECT tCE.nfoliocompromiso,\n")
           .append("       'H' 								AS Header,\n")
           .append("       tCE.faplicacion 					AS FECHA_APL,\n")
           .append("       tCE.fcarga 						AS FECHA_EXP,\n")
           .append("       tCE.cramo 						AS ID_RAMO,\n")
           .append("       tCE.cramo 						AS ID_RAMO_CR,\n")
           .append("       tCE.cramo 						AS ID_RAMO_REC,\n")
           .append("       tCE.cUnidadResponsableContable 	AS ID_UNIDAD,\n")
           .append("       tCE.cUnidadResponsableContable 	AS ID_UNIDAD_CR,\n")
           .append("       tCE.cUnidadResponsableContable 	AS ID_UNIDAD_REC,\n")
           .append("       'R' 								AS TIPO_MOVTO_49,\n")
           .append("       COMEINT.nFolioAutSICOP 			AS NCOM_20,\n")
           .append("       tCE.cidcontrato 					AS CTOEXT_30,\n")
           .append("       REPLACE(REPLACE(REPLACE(LEFT(tCE.cDescripcionPoliza, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), '') AS CPAG_31,\n")
           .append("       'S04929' 						AS CBEN_25,\n")
           .append("       'CNF010405EG1' 					AS RFC_26,\n")
           .append("       'TANIA ANANI LIMON MAGAÑA' 		AS REPRESENTANTE_LGL_38,\n")
           .append("       '0' 								AS TPROC_172,\n")
           .append("       0 								AS ESQ_PRECIO_36,\n")
           .append("       '0' 								AS CONTRATACION_35,\n")
           .append("       Getdate() 						AS FECHA_INI_27,\n")
           .append("       DATEADD(DAY,5,GETDATE()) 		AS FECHA_FIN_28,\n")
           .append("       GETDATE() - 1 					AS FECHA_COM_24,\n")
           .append("       'N' 								AS ES_PLURIANUAL_33,\n")
           .append("       '' 								AS APROB_PLA_34,\n")
           .append("       '' 								AS ACTO_JURIDICO_32,\n")
           .append("       SUM(tCD.mimporte)				AS MONTO_MONORI_42,\n")
           .append("       'MXN' 							AS TMON_41,\n")
           .append("       '1' 								AS TCAM_64,\n")
           .append("       SUM(ABS(tCD.mimporte)) 			AS MONTO_EJER_45,\n")
           .append("       SUM(ABS(tCD.mimporte)) 			AS MONTO_MIN_43,\n")
           .append("       SUM(ABS(tCD.mimporte))			AS MONTO_MAX_44,\n")
           .append("       'N' 								AS CONV_MOD_173,\n")
           .append("       '' 								AS NUM_CONVENIO_46,\n")
           .append("       '' 								AS FECHA_MODIFICACION_47,\n")
           .append("       '' 								AS CODIGO_EXPEDIENTE_37,\n")
           .append("       '' 								AS NUM_PROCEDIMIENTO_39,\n")
           .append("       '' 								AS CODIGO_CONTRATO_40,\n")
           .append("       tCE.nmes							AS MES_149,\n")
           .append("       tCE.canocompromiso 				AS ID_CTR_INT_301,\n")
           .append("       '' 								AS TTRANS_21,\n")
           .append("       tCE.canocompromiso 				AS COMODIN4_500,\n")
           .append("       tCE.canocompromiso 				AS COMODIN4_501,\n")
           .append("       'N' 								AS ETIQUETA_COMPRANET_178,\n")
           .append("       'REPOSICION DE FONDO REVOLVENTE' AS JUSTIFICA_COMPRANET_416,\n")
           .append("       '0' 								AS IVA_MON_ORIG_414,\n")
           .append("       '0' 								AS IMP_CONT_SIVA_413,\n")
           .append("       '0' 								AS IMP_CONV_MOD_415,\n")
           .append("       '' 								AS FECHA_FIN_MOD_435\n")
           .append("FROM tcompromisoencabezado tCE WITH (NOLOCK)\n")
           .append("INNER JOIN tcompromisodetalle tCD WITH (NOLOCK) ON tCE.nfoliocompromiso = tCD.nfoliocompromiso\n")
           .append("INNER JOIN tReintegroDetalle RDET WITH (NOLOCK) ON nFolioReintegro = " + folio + "\n")
           .append("INNER JOIN tLayoutsCreadosRelacionGastosHeader LAYOUTINT WITH (NOLOCK) ON cxp = sNoContrarrecibo\n")
           .append("INNER JOIN tCompromisoEncabezado COMEINT WITH (NOLOCK) ON sAuxiliarComodin = COMEINT.cIdContrato\n")
           .append("WHERE tCE.cIdContrato = '" + cFolio + "' ")
           .append("GROUP BY tCE.nfoliocompromiso, tCE.faplicacion, COMEINT.nFolioAutSICOP,\n")
           .append("         tCE.fcarga, tCE.cramo, tCD.nfoliocompromiso, tCE.cunidadresponsable,\n")
           .append("         tCE.cidcontrato, tCE.canocompromiso, tCE.nmes,\n")
           .append("         tCE.cUnidadResponsableContable, tCE.ccentrocontable, tCE.cDescripcionPoliza");
 
		log.debug( Sql );
		pstmntH = conn.prepareStatement( Sql.toString() );
		rs = pstmntH.executeQuery();

		while ( rs.next() ) {
			SimpleDateFormat fecha = new SimpleDateFormat( "dd/MM/yyyy" );
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );

			String fechaALayout = fecha.format( sdf.parse( rs.getString( "FECHA_APL" ) ) );
			String fechaLayout = fecha.format( sdf.parse( rs.getString( "FECHA_EXP" ) ) );
			String fContratoIni = fecha.format( sdf.parse( rs.getString( "FECHA_INI_27" ) ) );
			String fContratoFin = fecha.format( sdf.parse( rs.getString( "FECHA_FIN_28" ) ) );
			String fContratoFirma = fecha.format( sdf.parse( rs.getString( "FECHA_COM_24" ) ) );
			String fContratoModifica = StringUtils.isEmpty( rs.getString( "FECHA_FIN_MOD_435" ) ) ? "" : fecha.format( sdf.parse( rs.getString( "FECHA_FIN_MOD_435" ) ) );
			String anio = "";
			if ( !StringUtils.isBlank( fContratoModifica ) ) {
				anio = fContratoModifica.substring( 6, 10 );
				if ( "1900".equals( anio ) ) {
					fContratoModifica = "";
				}
			}
			
			String compromiso = "2";
			String erogacion = "2";
	
			String tipoOp = "1";

			DecimalFormat df = new DecimalFormat( "#.00" );
			String montoMonori = df.format( rs.getDouble( "MONTO_MONORI_42" ) );
			String montoEjer = df.format( rs.getDouble( "MONTO_EJER_45" ) );
			String montoMin = df.format( rs.getDouble( "MONTO_MIN_43" ) );
			String montoMax = df.format( rs.getDouble( "MONTO_MAX_44" ) );

			String folioSICOP = rs.getString("NCOM_20");
			String sFolioSICOP =  (folioSICOP != null ? folioSICOP.trim() : "");
			
			StringBuilder encabezado = new StringBuilder();

			encabezado.append(rs.getString("Header").trim()) // A
			         .append(",").append(fechaALayout.trim()) // B
			         .append(",").append(fechaLayout.trim()) // C
			         .append(",").append(rs.getString("ID_RAMO").trim()) // D
			         .append(",").append(rs.getString("ID_RAMO_CR").trim()) // E
			         .append(",").append(rs.getString("ID_RAMO_REC").trim()) // F
			         .append(",").append(rs.getString("ID_UNIDAD").trim()) // G
			         .append(",").append(rs.getString("ID_UNIDAD_CR").trim()) // H
			         .append(",").append(rs.getString("ID_UNIDAD_REC").trim()) // I
			         .append(",").append(rs.getString("TIPO_MOVTO_49").trim()) // J
			         .append(",").append(compromiso) // K
			         .append(",").append(erogacion) // L
			         .append(",").append(sFolioSICOP) // M
			         .append(",").append(tipoOp) // N
			         .append(",").append(rs.getString("CTOEXT_30").trim()) // O
			         .append(",").append(rs.getString("CPAG_31").trim().replaceAll(",", " ")) // P
			         .append(",").append(rs.getString("CBEN_25").trim()) // Q
			         .append(",").append(rs.getString("RFC_26").trim()) // R
			         .append(",").append(rs.getString("REPRESENTANTE_LGL_38").trim()) // S
			         .append(",").append(rs.getString("TPROC_172")) // T
			         .append(",").append(rs.getString("ESQ_PRECIO_36")) // U
			         .append(",").append(rs.getString("CONTRATACION_35")) // V
			         .append(",").append(fContratoIni.trim()) // W
			         .append(",").append(fContratoFin.trim()) // X
			         .append(",").append(fContratoFirma.trim()) // Y
			         .append(",").append(rs.getString("ES_PLURIANUAL_33")) // Z
			         .append(",").append(rs.getString("APROB_PLA_34")) // AA *
			         .append(",").append(rs.getString("ACTO_JURIDICO_32")) // AB*
			         .append(",").append(montoMonori) // AC
			         .append(",").append(rs.getString("TMON_41")) // AD
			         .append(",").append(rs.getString("TCAM_64")) // AE
			         .append(",").append(montoEjer) // AF
			         .append(",").append(montoMin) // AG
			         .append(",").append(montoMax) // AH
			         .append(",").append(rs.getString("CONV_MOD_173")) // AI
			         .append(",").append(rs.getString("NUM_CONVENIO_46")) // AJ
			         .append(",").append(rs.getString("FECHA_MODIFICACION_47")) // AK
			         .append(",").append(org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("CODIGO_EXPEDIENTE_37"))) // AL
			         .append(",").append(org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("NUM_PROCEDIMIENTO_39"))) // AM
			         .append(",").append(org.apache.commons.lang.StringUtils.trimToEmpty(rs.getString("CODIGO_CONTRATO_40"))) // AN
			         .append(",").append(rs.getString("MES_149").trim()) // AO
			         .append(",").append(rs.getString("ID_CTR_INT_301")) // AP
			         .append(",").append(rs.getString("TTRANS_21")) // AQ
			         .append(",").append(rs.getString("COMODIN4_500")) // AR
			         .append(",").append(rs.getString("COMODIN4_501")) // AS
			         .append(",").append(rs.getString("ETIQUETA_COMPRANET_178")) // AT
			         .append(",").append(rs.getString("JUSTIFICA_COMPRANET_416")) // AU
			         .append(",").append(rs.getString("IVA_MON_ORIG_414")) // AV
			         .append(",").append(rs.getString("IMP_CONT_SIVA_413")) // AW
			         .append(",").append(rs.getString("IMP_CONV_MOD_415")) // AX
			         .append(",").append(fContratoModifica.trim()) // AY
			         .append("\r\n");			
			
			arrListaComp =  encabezado.toString();
		}

		CloseObject.closeObject( rs );
		CloseObject.closeObject( pstmntH );

		return arrListaComp;
		
	}
	
	public static String getDecrementoDetalle(Connection conn, int folio, String cFolio) throws SQLException {
		String arrListaComp = "";
		PreparedStatement pstmntD = null;
		ResultSet rs2 = null;
		
		// RELACION DE GASTOS INTEGRADAS 
				
		StringBuilder Sql2 = new StringBuilder();
		
		Sql2.append( " SELECT '668' as ID_EVENTO " );
		Sql2.append( " , '304_TOCN' as EVENTO" );
		Sql2.append( " , tCEP.cRamo" );
		Sql2.append( " , tCEP.cUnidadResponsableEP" );
		Sql2.append( " , tCEP.aEjercicioFiscal" );
		Sql2.append( " , tCEP.cGrupoFuncional" );
		Sql2.append( " , tCEP.cFuncion" );
		Sql2.append( " , tCEP.cSubFuncion" );
		Sql2.append( " , SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2) AS cProgramaGeneral" );
		Sql2.append( " , tCEP.cActividadInstitucional" );
		Sql2.append( " , tCEP.cProgramaPresupuestario" );
		Sql2.append( " , SUBSTRING(tCEP.cPartida,1,1)" );
		Sql2.append( " , SUBSTRING(tCEP.cPartida,2,1)" );
		Sql2.append( " , SUBSTRING(tCEP.cPartida,3,1)" );
		Sql2.append( " , SUBSTRING(tCEP.cPartida,4,2)" );
		Sql2.append( " , tCEP.cTipoGasto" );
		Sql2.append( " , tCEP.cFuenteFinanciamiento" );
		Sql2.append( " , tCEP.cEntidadFederativa" );
		Sql2.append( " , SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11) AS cCartera" );
		Sql2.append( " , '000000' + '0000' as CAU " );
		Sql2.append( " , '00' as COP " );
		Sql2.append( " , '000' as PL" );
		Sql2.append( " , '000' as OF_" );
		Sql2.append( " , '00000' as AUX1" );
		Sql2.append( " , '00000' as AUX2" );
		Sql2.append( " , '0000000000' as AUX3" );
		Sql2.append( " , isnull(nFolioSuficiencia,0) as Suficiencia" );
		Sql2.append( " , '' as Sol_OLI" );
		Sql2.append( " , SUM(CASE tCD.cMes when 1 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Enero" );
		Sql2.append( " , SUM(CASE tCD.cMes when 2 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Febrero" );
		Sql2.append( " , SUM(CASE tCD.cMes when 3 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Marzo" );
		Sql2.append( " , SUM(CASE tCD.cMes when 4 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Abril" );
		Sql2.append( " , SUM(CASE tCD.cMes when 5 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Mayo" );
		Sql2.append( " , SUM(CASE tCD.cMes when 6 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Junio" );
		Sql2.append( " , SUM(CASE tCD.cMes when 7 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Julio" );
		Sql2.append( " , SUM(CASE tCD.cMes when 8 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Agosto" );
		Sql2.append( " , SUM(CASE tCD.cMes when 9 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Septiembre" );
		Sql2.append( " , SUM(CASE tCD.cMes when 10 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Octubre" );
		Sql2.append( " , SUM(CASE tCD.cMes when 11 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Noviembre" );
		Sql2.append( " , SUM(CASE tCD.cMes when 12 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Diciembre" );
		Sql2.append( " , SUM( convert(decimal(14,2), abs(tCD.mImporte)) ) as Importe" );
		Sql2.append( " 	FROM tCompromisoDetalle tCD WITH (NOLOCK) " );
		Sql2.append( "	JOIN tCompromisoEncabezado tCE WITH (NOLOCK) ON tCD.nFolioCompromiso = tCE.nFolioCompromiso" );
		Sql2.append( "	JOIN tCatalogoEP tCEP WITH (NOLOCK) ON tCD.EP = tCEP.EP" );
        Sql2.append( "  WHERE tCE.cIdContrato = '" + cFolio + "' ");
		Sql2.append( " 	AND tCD.EP = tCEP.EP" );
		Sql2.append( " 	AND tCE.nFolioCompromiso = tCD.nFolioCompromiso " );
		Sql2.append( " 	group by tCEP.cRamo,  tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, " );
		Sql2.append( "  SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2), tCEP.cActividadInstitucional, SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11)," );
		Sql2.append( "  tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, nFolioSuficiencia " );

		log.debug( Sql2 );
		pstmntD = conn.prepareStatement( Sql2.toString() );		
		rs2 = pstmntD.executeQuery();

		while ( rs2.next() ) {
			StringBuilder detalle = new StringBuilder();

			detalle.append(rs2.getString("ID_EVENTO")) // ID_EVENTO
			       .append(",").append(rs2.getString("EVENTO")) // EVENTO
			       .append(",").append(rs2.getString("cRamo").trim()) // tCEP.cRamo
			       .append(",").append(rs2.getString("cUnidadResponsableEP").trim()) // tCEP.cUnidadResponsableEP
			       .append(",").append(rs2.getString("aEjercicioFiscal").trim()) // tCEP.aEjercicioFiscal
			       .append(",").append(rs2.getString("cGrupoFuncional").trim()) // tCEP.cGrupoFuncional
			       .append(",").append(rs2.getString("cFuncion").trim()) // tCEP.cFuncion
			       .append(",").append(rs2.getString("cSubFuncion").trim()) // tCEP.cSubFuncion
			       .append(",").append(rs2.getString("cProgramaGeneral")) // tCEP.cProgramaGeneral
			       .append(",").append(rs2.getString("cActividadInstitucional")) // tCEP.cActividadInstitucional
			       .append(",").append(rs2.getString("cProgramaPresupuestario")) // tCEP.cProgramaPresupuestario
			       .append(",").append(rs2.getString(12)) // SUBSTRING(tCEP.cPartida,1,1)
			       .append(",").append(rs2.getString(13)) // SUBSTRING(tCEP.cPartida,2,1)
			       .append(",").append(rs2.getString(14)) // SUBSTRING(tCEP.cPartida,3,1)
			       .append(",").append(rs2.getString(15)) // SUBSTRING(tCEP.cPartida,4,2)
			       .append(",").append(rs2.getString("cTipoGasto")) // tCEP.cTipoGasto
			       .append(",").append(rs2.getString("cFuenteFinanciamiento")) // tCEP.cFuenteFinanciamiento
			       .append(",").append(rs2.getString("cEntidadFederativa")) // tCEP.cEntidadFederativa
			       .append(",").append(rs2.getString("cCartera")) // tCEP.cCartera
			       .append(",").append(rs2.getString("CAU")) // CAU
			       .append(",").append(rs2.getString("COP")) // COP
			       .append(",").append(rs2.getString("PL")) // PL
			       .append(",").append(rs2.getString("OF_")) // OF_
			       .append(",").append(rs2.getString("AUX1")) // AUX1
			       .append(",").append(rs2.getString("AUX2")) // AUX2
			       .append(",").append(rs2.getString("AUX3")) // AUX3
			       .append(",").append(rs2.getString("Suficiencia")) // Suficiencia
			       .append(",").append(rs2.getString("Sol_OLI")) // Sol_OLI
			       .append(",").append(rs2.getString("Enero")) // Enero
			       .append(",").append(rs2.getString("Febrero")) // Febrero
			       .append(",").append(rs2.getString("Marzo")) // Marzo
			       .append(",").append(rs2.getString("Abril")) // Abril
			       .append(",").append(rs2.getString("Mayo")) // Mayo
			       .append(",").append(rs2.getString("Junio")) // Junio
			       .append(",").append(rs2.getString("Julio")) // Julio
			       .append(",").append(rs2.getString("Agosto")) // Agosto
			       .append(",").append(rs2.getString("Septiembre")) // Septiembre
			       .append(",").append(rs2.getString("Octubre")) // Octubre
			       .append(",").append(rs2.getString("Noviembre")) // Noviembre
			       .append(",").append(rs2.getString("Diciembre")) // Diciembre
			       .append(",").append(rs2.getString("Importe")) // Importe
			       .append("\r\n");
			
			arrListaComp =  detalle.toString();
		}
		
		CloseObject.closeObject( rs2 );
		CloseObject.closeObject( pstmntD );

		return arrListaComp;
		
	}

	public static int actualizaEnvioSICOPCompromiso( Connection conn, String cFolio ) throws Exception {
		String query = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = 1 WHERE cIdContrato in ('" + cFolio + "')";
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement( query );
			return ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
		}		
	}

}
