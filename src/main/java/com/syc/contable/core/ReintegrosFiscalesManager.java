package com.syc.contable.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReintegrosFiscalesManager {
	
	public static final Logger log = Logger.getLogger(ReintegrosManager.class);
	
	public ReintegrosFiscalesManager() {
		super();
	}
	
	public static boolean insertaDetReintegroPaso(Connection conn,int folio, ReintegroDetalle datos, String evento, String cCentro, int consecutivo, String folioDep) throws SQLException{
		PreparedStatement pstmnt = null;
		PreparedStatement pstmntIns = null;
		ResultSet rs = null;
		boolean retval =false;
		try {
			String queryRFC="SELECT ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.nDocRenglon=?";
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
		    pstmntIns.setInt(5, datos.getMes());
		    pstmntIns.setInt(6, datos.getMvto());
		    //pstmntIns.setString(7, datos.getCxp());
		    //pstmntIns.setString(8, datos.getClcSicop());
			/*pstmntIns.setDouble(15, datos.getIsr());
	   		pstmntIns.setDouble(16, datos.getIva());
		    pstmntIns.setDouble(17, datos.getMillar());
		    pstmntIns.setDouble(18, datos.getIvaDes());*/
		    pstmntIns.setString(7, evento);
		    pstmntIns.setDouble(8, datos.getmImporteCLC());
		    pstmntIns.setDouble(9, datos.getmImporteCLC()*-1);
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
		} catch(SQLException s){
			s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
			if (pstmntIns != null)
				pstmntIns.close();
			pstmntIns = null;
		}
		return retval;
	}
	
	public static boolean insertaDetReintegro(Connection conn, int folioReintegro, ReintegroDetalle datos, String evento, String cCentro, int nDocRenglon) throws SQLException{
		PreparedStatement pstmnt = null;
		PreparedStatement pstmntIns = null;
		//PreparedStatement pstmntUp = null;
		boolean retval =false;
		//boolean existe = false;
		try {
			String query="INSERT INTO tReintegroIngresoDetalle SELECT * FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			//retval = pstmnt.execute();
			pstmnt.execute();
			
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
			if (pstmntIns != null)
				pstmntIns.close();
			pstmntIns = null;
		}
		return retval;
	}
	
	public static boolean borraDetReintegro(Connection conn, int folioReintegro) throws SQLException{
		PreparedStatement pstmnt = null;
		boolean retval =false;
		try {
			String query="DELETE FROM tReintegroIngresoDetalle WHERE nFolioReintegroIngreso = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			pstmnt.execute();
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static boolean borraDetReintegroPaso(Connection conn, int folioReintegro) throws SQLException{
		PreparedStatement pstmnt = null;
		boolean retval =false;
		try {
			String query="DELETE FROM tReintegroDetallePaso WHERE nFolioReintegro = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			pstmnt.execute();
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}

	public static boolean insertaEncReintegro(Connection conn, int folioReintegro, ReintegroEncabezado datos,  String cCentro, Date fecha) throws SQLException, ParseException{
		PreparedStatement pstmnt = null;
		PreparedStatement pstmntIns = null;
		PreparedStatement pstmntUp = null;
		ResultSet rs = null;
		boolean retval =false;
		boolean existe = false;
		try {
			String query="SELECT * FROM tReintegroIngresoEncabezado with(NOLOCK) WHERE nFolioReintegroIngreso = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			//retval = pstmnt.execute();
			rs = pstmnt.executeQuery();
			if (rs.next()) {
			    existe = true;
			}
			if(existe){ //SI existe el registro le hacemos un update de datos, si no existe lo insertamos
			    pstmntUp = conn.prepareStatement("UPDATE tReintegroIngresoEncabezado SET cTipoReintegro=?, aEjercicioFiscal=?, observaciones=?, concepto=?, formaDePago=?, clvRastreo=?, fichaDeposito=?, clvBanco=?, cuentaBancaria=?, lc=?, importeLC=? WHERE nFolioReintegroIngreso=?");
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
			}else if(datos!=null){
			    pstmntIns = conn.prepareStatement("INSERT INTO tReintegroIngresoEncabezado(nFolioReintegroIngreso,fSolicitud,cTipoReintegro,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			    pstmntIns.setInt(1, datos.getnFolioReintegro());			 //folio del reintegro
			    pstmntIns.setDate(2,  java.sql.Date.valueOf(datos.getfSolicitud()));			 //fecha de hoy, pasar como parametro o recalcular
			    pstmntIns.setString(3, datos.getcTipoReintegro()); 		 //cTipoReintegro dependiendo si es de compromiso, directa 
			    pstmntIns.setString(4, datos.getcRamo()); 		 			//cRamo
			    pstmntIns.setString(5, datos.getcUnidadResponsable()); 			 //cUnidadResponsable
			    pstmntIns.setString(6, datos.getcDocumentoHaplicado()); 			 
			    pstmntIns.setString(7, datos.getaEjercicioFiscal());  //aEjercicioFiscal (2012,2013...)
			    pstmntIns.setString(8, datos.getObservaciones());  //observaciones
			    pstmntIns.setString(9, datos.getConcepto()); //concepto
			    pstmntIns.setString(10, datos.getU_login());  
			    pstmntIns.setInt(11, datos.getFormaDePago());		 //forma de pago (efectivo, transferencia) [catalogo]
			    pstmntIns.setString(12, datos.getClvRastreo());	 //clave de rastreo
			    pstmntIns.setString(13, datos.getFichaDeposito());	//ficha de deposito en el banco
			    pstmntIns.setString(14, datos.getClvBanco());			//clave del pago en el banco
			    pstmntIns.setString(15, datos.getCuentaBancaria());			//cuenta bancaria
			    pstmntIns.setString(16, datos.getLc());					//linea de captura clave
			    pstmntIns.setDouble(17, Double.parseDouble(datos.getImporteLC()));		//importe de la linea de captura
			    pstmntIns.setString(18, datos.getcTipoPoliza());		//cTipoPoliza
			    pstmntIns.setDate(19, fecha);
			    pstmntIns.setString(20, "RHQ");
			    //pstmntIns.setString(21, datos.getCuentaPorPagar());
			    pstmntIns.setInt(21, datos.getFolioDependencia());
			    pstmntIns.setString(22, datos.getMovimiento());
			    pstmntIns.setInt(23, datos.getAviso());
			    pstmntIns.setInt(24, datos.getTipoAviso());
			    pstmntIns.setInt(25, datos.getCausaAviso());
			pstmntIns.setString(26,  datos.getfAcreditacion());
			    retval = pstmntIns.execute();
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
			if (pstmntIns != null)
				pstmntIns.close();
			pstmntIns = null;
		}
		return retval;
	}
	
	public static String getEvento(Connection conn, String cOBGINI, String cCTGA, String tipoCLC) throws SQLException{
	    PreparedStatement pstmnt = null;
	    ResultSet rs = null;
	    String evento="";
	    try {
		String query="SELECT '"+tipoCLC+"' + cEVTO FROM tEventoConcepto with (nolock) WHERE cOBGINI = ? AND cCTGA = ?";
		pstmnt = conn.prepareStatement(query);
		pstmnt.setString(1, cOBGINI);
		pstmnt.setString(2, cCTGA);
		pstmnt.execute();
		
		rs = pstmnt.executeQuery();
		if (rs.next()) {
			evento = rs.getString(1);
		}
		
	    } finally {
		if (pstmnt != null)
		    pstmnt.close();
		pstmnt = null;
	    }
	    return evento;
	}
	
	public static String getCLCPagada(Connection conn, int nCLC, String cxp) throws SQLException{
	    PreparedStatement pstmnt = null;
	    ResultSet rs = null;
	    boolean retVal = false;
	    String aplicado="";
	    String datos = "";
	    try {
			String query="SELECT cDocumentoHaplicado FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?";
			pstmnt = conn.prepareStatement(query);
			//pstmnt.setInt(1, nCLC);
			pstmnt.setString(1, cxp);
			
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				aplicado = rs.getString(1);
				if("S".equals(aplicado)){
				    retVal = true;
				}else if(aplicado==null){
					retVal = false;
					datos= "La CLC con Cuenta Por Pagar: "+cxp+" NO se encuentra pagada";
				}
			}else{
				retVal = false;
				datos= "La CLC con Cuenta Por Pagar: "+cxp+" NO existe";
			}
	    } finally {
		if (pstmnt != null)
		    pstmnt.close();
		pstmnt = null;
	    }
	    return datos;
	}
	
	public static String tipoPago(Connection conn, int nCLC, String cxp) throws SQLException{
	    PreparedStatement pstmnt = null;
	    ResultSet rs = null;
	    String tipoCLC = "";
	    try {
			String query="SELECT ctipoPago FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setString(1, cxp);
			
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				tipoCLC = rs.getString(1);
			}
	    } finally {
		if (pstmnt != null)
		    pstmnt.close();
		pstmnt = null;
	    }
	    return tipoCLC;
	}
	
	public static String getTipoPago(Connection conn, String cxp) throws SQLException{
	    PreparedStatement pstmnt = null;
	    ResultSet rs = null;
	    String tipo="";
	    try {
			String query="SELECT CASE cTipoPago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' "
			+ "ELSE '' END AS dTipoPago FROM tPagadoEncabezado with (nolock) WHERE caNoContrarrecibo = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setString(1, cxp);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
				tipo = rs.getString(1);
			}    
	    } finally {
			if (pstmnt != null)
			    pstmnt.close();
			pstmnt = null;
	    }
	    return tipo;
	}
	
	public static ReintegroDetalle[] getReintegrosDetalle(Connection conn, String[] ids, int folio) throws SQLException{
	    PreparedStatement pstmnt = null;
	    ResultSet rs = null;
	    ReintegroDetalle[] reintegros;
	    try {
		String query="SELECT * FROM tReintegroIngresoDetalle rd with (nolock) WHERE nFolioReintegroIngreso = ? AND (";
		for(int j=0;j<ids.length;j++){
			query+=" nDocRenglon=? OR";
		}
		query = query.substring(0, query.length()-3);
		query+=")";
		pstmnt = conn.prepareStatement(query);
		pstmnt.setInt(1, folio);
		for(int j=0;j<ids.length;j++){
			pstmnt.setInt(j+2, Integer.parseInt(ids[j],10));
		}
		
		rs = pstmnt.executeQuery();
		//reintegros = new ReintegroDetalle[rs.getFetchSize()];
		reintegros = new ReintegroDetalle[ids.length];
		int i=0;
		while (rs.next()) {
			ReintegroDetalle rd = new ReintegroDetalle();
			rd.setNoCLC(rs.getInt("noCLC"));
			rd.setSecCLC(rs.getInt("secCLC"));
			//rd.setCodigoSaf(rs.getInt("codigoSaf"));
			rd.setEP(rs.getString("EP"));
			//rd.setCop(rs.getInt("cop"));
			rd.setmImporteCLC(rs.getDouble("mImporteCLC"));
			DecimalFormat formatter = new DecimalFormat("###.####");
			rd.setmImporteCLCFormat(formatter.format(rs.getDouble("mImporteCLC")));
			/*rd.setnCompromiso(rs.getString("nCompromiso"));
			rd.setBeneficiario(rs.getString("beneficiario"));
			rd.setSuficiencia(rs.getString("suficiencia"));
			rd.setSolOli(rs.getInt("solOli"));
			rd.setTipoCon(rs.getString("tipoCon"));
			rd.setTipoDeCon(rs.getString("tipoDeCon"));*/
			rd.setMes(rs.getInt("cMes"));
			rd.setCxp(rs.getString("cxp"));
			/*rd.setMvto(rs.getInt("movto"));
			rd.setIsr(rs.getInt("isr"));
			rd.setIva(rs.getInt("iva"));
			rd.setMillar(rs.getInt("millar"));
			rd.setIvaDes(rs.getInt("ivaDes"));*/
			reintegros[i] = rd;
			i++;
		}		
	    } finally {
		if (pstmnt != null)
		    pstmnt.close();
		pstmnt = null;
	    }
	    return reintegros;
	}
	
	public static void autorizaReintegro(Connection conn, Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP,String ulogin,String prefixPath, String fAcredit) throws SQLException, GestionException {
		PreparedStatement pstmnt = null;
		int nIdCaso;
		String cSQLString = "";
		ResultSet rs = null;
		int nExiste = 0;

		try {
			nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
				pstmnt = conn.prepareStatement("INSERT INTO tReintegroIngresoAutEncabezado (nFolioReintegroIngreso, fSolicitud, cTipoReintegro, cRamo, cUnidadResponsable, cDocumentoHaplicado, aEjercicioFiscal, observaciones, concepto, u_login, "
				        + "cUnidadResponsableContable,nFolioTramiteSicop, cTipoPoliza, fAplicacion,cdescripcionpoliza, formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso) "
				        + "SELECT nFolioReintegroIngreso, fSolicitud, cTipoReintegro, cRamo,cUnidadResponsable, 'N', aEjercicioFiscal, observaciones, concepto, "
				        + "u_login, 'RHQ',nFolioTramiteSicop, 'EG','" + fAcredit + "','AUTORIZACION DE REINTEGRO PRESUPUESTAL FOLIO ' + CAST(nFolioReintegroIngreso as varchar),formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso FROM tReintegroIngresoEncabezado WITH (NOLOCK) WHERE nFolioReintegroIngreso = ?");
				//pstmnt.setString(1, nNumSicop);
				/*pstmnt.setString(2, cRecMotivSicop);
				pstmnt.setString(3, nNumMAP);
				pstmnt.setString(4, cRecMotivMAP);*/
				pstmnt.setInt(1, nIdCaso);
				pstmnt.execute();

				pstmnt = conn.prepareStatement("SELECT tipoAviso FROM tReintegroIngresoEncabezado WITH (NOLOCK) WHERE nFolioReintegroIngreso = ?");
				pstmnt.setInt(1, nIdCaso);
				rs = pstmnt.executeQuery();
				String tipoRein = "";
				
				if (rs.next()) {
					tipoRein = rs.getString(1);
				}
	
				pstmnt = conn.prepareStatement("INSERT INTO tReintegroIngresoAutDetalle(nFolioReintegroIngreso,nDocRenglon,noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento, mImporte, mImporteNegativo, cCentroContable, nCapitulo,cxp,RFC,nFolioDependencia,ALM,nRenglonPagado, obgt,ctab) "
			        +  " SELECT rd.nfolioreintegro, "
			        +"       rd.ndocrenglon, "
			        +"       rd.noclc, "
			        +"       rd.secclc, "
			        +"       rd.ep, "
			        +"       rd.mimporteclc, "
			        +"       rd.cmes, "
			        +"       rd.movto, "
			        +"       (SELECT CASE ctipopago "
			        +"                 WHEN 'PAGOOBRA' THEN 'RD' "
			        +"                 WHEN 'PAGODIVERSO' THEN 'RD' "
			        +"                 WHEN 'PAGODIRECTO' THEN 'RD' "
			        +"                 WHEN 'RELACIONGASTOS' THEN 'RD' "
			        +"                 WHEN 'NOMINA' THEN 'RD' "
			        +"                 WHEN 'AJENAS' THEN (SELECT CASE ctipodoc "
			        +"                                              WHEN 'PAGODIRECTO' THEN 'RD' "
			        +"                                              ELSE 'RD' "
			        +"                                            END "
			        +"                                     FROM   toperajenasdetalle WITH(NOLOCK)"
			        +"                                     WHERE  rd.cxp = canocontrarrecibo) "
			        +"                 WHEN 'FEDERALIZADO' THEN 'RD' "
			        +"                 ELSE '' "
			        +"               END AS dTipoPago "
			        +"        FROM   tpagadoencabezado WITH(nolock) "
			        +"        WHERE  canocontrarrecibo = rd.cxp)"
			        +"         "
			        +"       + Substring( (SELECT DISTINCT cevento FROM tPagadoDetalle ed WITH(NOLOCK) WHERE "
			        +"       ed.ep=rd.ep "
			        +"       AND ed.nfoliopago=(SELECT nfoliopago FROM tpagadoencabezado WITH(NOLOCK) WHERE "
			        +"       canocontrarrecibo=rd.cxp) AND ed.ctipopago=(SELECT ctipopago FROM "
			        +"       tpagadoencabezado WITH(NOLOCK) WHERE canocontrarrecibo=rd.cxp)) , 2, "
			        +"       "
			        +"       Len((SELECT "
			        +"       DISTINCT "
			        +"       cevento FROM tPagadoDetalle ed WITH(NOLOCK) WHERE ed.ep=rd.ep AND "
			        +"       ed.nfoliopago=(SELECT "
			        +"       nfoliopago FROM tpagadoencabezado WITH(NOLOCK) WHERE canocontrarrecibo=rd.cxp) AND "
			        +"       ed.ctipopago=(SELECT ctipopago FROM tpagadoencabezado WITH(NOLOCK) WHERE "
			        +"       canocontrarrecibo=rd.cxp)))), "
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
			        +"		 rd.ctab "	
			        +"FROM   tReintegroIngresoDetalle rd WITH (nolock) "
			        +"WHERE  rd.nfolioreintegro = ?");
				pstmnt.setInt(1, nIdCaso);
				pstmnt.execute();
				//CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
				// avanza casos

				//cbl.avanzaCaso(c, ulogin, "", new String[] { "CONSULTA_REINTEGRO" }, new String[] { "consulta_reintegro" }, new HashMap(), prefixPath);
			
			conn.commit();
		} catch(SQLException s){
			conn.rollback();
			s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();

			if (rs != null)
				rs.close();

			pstmnt = null;
			rs = null;
		}
	}

	public static String getTipoPoliza(Connection conn, String cDocumento) throws SQLException {
		String retTipoPoliza = "";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		pstm = conn.prepareStatement("select cTipoPoliza from tDocumentoTipoPoliza with (nolock) where cDocumento=?");
		pstm.setString(1, cDocumento);
		rs = pstm.executeQuery();
		if (rs.next()) {
			retTipoPoliza = rs.getString(1);
		}
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		return retTipoPoliza;
	}

	public static String getcPartida(Connection conn, String EP) throws SQLException {
		String cPartida = "";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		pstm = conn.prepareStatement("select cPartida from tCatalogoEP with (nolock) where EP=?");
		pstm.setString(1, EP);
		rs = pstm.executeQuery();
		if (rs.next()) {
			cPartida = rs.getString(1);
		}
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		String res="";
		if(cPartida!=null && !cPartida.equals("")){
			res = cPartida.substring(0, 1);
			for(int i=1;i<=4;i++)
				res+="0";
		}else{
			res = "La EP "+EP+" no existe en el catalogo";
		}
		return res;
	}
	
	public static String getEntidadFederativa(Connection conn, String EP) throws SQLException {
		String cEntidadFederativa = "";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		pstm = conn.prepareStatement("select cEntidadFederativa from tCatalogoEP with (nolock) where EP=?");
		pstm.setString(1, EP);
		rs = pstm.executeQuery();
		if (rs.next()) {
			cEntidadFederativa = rs.getString(1);
		}
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		return cEntidadFederativa;
	}

	public static double getRemanente(Connection conn,String ep,String cxp,int nRenglon) throws SQLException {
		double remanente = 0.00;
		ResultSet rs = null;
		PreparedStatement pstm = null;
	
		String queryRemanente="" +
							  "SELECT mimporteneto - imprect - impreint AS Remanente "
							 +"FROM   (SELECT pd.mimporteneto, "
							 +"               (SELECT Count(*) AS sumaRect "
							 +"                FROM   trectificacionencabezado re WITH (nolock), "
							 +"                       trectificaciondetalle rd WITH (nolock) "
							 +"                WHERE  rd.ep = ? " // 1
							 +"                       AND re.cdocumentohaplicado = 'S' "
							 +"                       AND re.nfoliorectificacion = rd.nfoliorectificacion "
							 +"                       AND re.canocontrarrecibo = ? " // 2
							 +"                       AND rd.ep = pd.ep "
							 +"                       AND rd.ndocrenglon = ?)               AS tRectificacion, " // 3
							 +"               (SELECT Count(*) AS sumaReint "
							 +"                FROM   tReintegroIngresoEncabezado ree WITH (nolock), "
							 +"                       tReintegroIngresoDetalle red WITH (nolock) "
							 +"                WHERE  red.ep = ? "	// 4
							 +"                       AND ree.cdocumentohaplicado = 'S' "
							 +"                       AND ree.nfolioreintegro = red.nfolioreintegro "
							 +"                       AND red.cxp = ? " // 5
							 +"                       AND red.ep = pd.ep "
							 +"                       AND red.nrenglonpagado = ?)           AS tReintegros, " // 6
							 +"               Isnull((SELECT Isnull(Sum(rd.mimporte), 0) AS impR "
							 +"                       FROM   tReintegroIngresoEncabezado re WITH(nolock), "
							 +"                              tReintegroIngresoDetalle rd WITH(nolock) "
							 +"                       WHERE  re.nfolioreintegro = rd.nfolioreintegro "
							 +"                              AND re.cdocumentohaplicado = 'S' "
							 +"                              AND rd.cxp = ? "// 7
							 +"                              AND rd.ep = ? "// 8
							 +"               AND rd.nrenglonpagado = ?), 0) AS impReint, " // 9
							 +"               Isnull((SELECT Isnull(Sum(CASE Substring(cevento, 1, 4) "
							 +"                                           WHEN 'DICE' THEN mimporte "
							 +"                                           WHEN 'DEBE' THEN mimportenegativo "
							 +"                                         END), 0) AS imp "
							 +"                       FROM   trectificaciondetalle r WITH (nolock), "
							 +"                              trectificacionencabezado h WITH (nolock) "
							 +"                       WHERE  r.nfoliorectificacion = h.nfoliorectificacion "
							 +"                              AND h.canocontrarrecibo = ? " //10
							 +"                              AND r.ep =  ? " //11
							 +"               AND h.cdocumentohaplicado = 'S' "
							 +"                       GROUP  BY r.ndocrenglon), 0)          AS impRect "
							 +"        FROM   tpagadoencabezado pe WITH (nolock), "
							 +"               tpagadodetalle pd WITH (nolock) "
							 +"        WHERE  canocontrarrecibo = ? " //12
							 +"               AND pd.ep = ? " //13
							 +"               AND pe.cdocumentohaplicado = 'S' "
							 +"               AND pe.nfoliopagado = pd.nfoliopagado "
							 +"               AND pd.ndocrenglon = ?) tNueva " //15
							 +"WHERE  1 = 1 ";
		//                                                                1  2  3  4  5  6  7  8  9  10 11 12 13 14                     1   2     3     4   5     6      7  8     9      10  11  12  13  14     
		log.trace(  String.format( "Ejecutando query para remanente. [%s][%s,%s,%d,%s,%s,%d,%s,%s,%d,%s,%s,%s,%s,%d]" ,queryRemanente, ep, cxp,nRenglon,ep,cxp,nRenglon,cxp,ep, nRenglon,cxp,ep,cxp,ep,nRenglon ) ); 
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
		pstm.setString(12, cxp);
		pstm.setString(13, ep);
		pstm.setInt(14, nRenglon);
		rs = pstm.executeQuery();
		if (rs.next()) {
			remanente = rs.getDouble(1);
		}
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		return remanente;
	}
	
	public static ReintegroEncabezado getReintegroEncabezado(Connection conn,int folio) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroEncabezado re = new ReintegroEncabezado();
		pstm = conn.prepareStatement("SELECT * FROM tReintegroIngresoEncabezado with(nolock) WHERE nFolioReintegroIngreso = ?");
		pstm.setInt(1, folio);
		res = pstm.executeQuery();
		if(res.next()){
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
			//re.setCuentaPorPagar(res.getString("cuentaPorPagar"));
			//re.setFolioDependencia(Integer.parseInt(res.getString("folioDependencia"))); //este ya lo tiene en el detalle, por lo que no se toma en cuenta
		}
		if (pstm != null) {
			pstm.close();
		}
		if (res != null) {
			res.close();
		}
		return re;
	}
	
	public static ReintegroIngresoEncabezado getReintegroEncabezadoNuevo(Connection conn,int folio) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroIngresoEncabezado re = null;
		pstm = conn.prepareStatement("SELECT * FROM tReintegroIngresoEncabezado with(nolock) WHERE nFolioReintegroIngreso = ?");
		pstm.setInt(1, folio);
		res = pstm.executeQuery();
		if(res.next()){
			re = new ReintegroIngresoEncabezado();
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
		}
		if (pstm != null) {
			pstm.close();
		}
		if (res != null) {
			res.close();
		}
		return re;
	}
	
	public static ArrayList<ReintegroDetalle> getReintegroDetalle(Connection conn,int folio) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroDetalle rd = null;
		ArrayList<ReintegroDetalle> detalles = new ArrayList<ReintegroDetalle>();
		pstm = conn.prepareStatement("SELECT * FROM tReintegroIngresoDetalle with(nolock) WHERE nFolioReintegroIngreso = ?");
		pstm.setInt(1, folio);
		res = pstm.executeQuery();
		while(res.next()){
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
		if (pstm != null) {
			pstm.close();
		}
		if (res != null) {
			res.close();
		}
		return detalles;
	}
	
	public static boolean getReintegroHApartado(Connection conn,int folio) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean aplicado = false;
		pstm = conn.prepareStatement("SELECT cDocumentoHaplicado FROM tReintegroIngresoEncabezado with(nolock) WHERE nFolioReintegroIngreso = ?");
		pstm.setInt(1, folio);
		res = pstm.executeQuery();
		if(res.next()){
			if("S".equals(res.getString("cDocumentoHaplicado")))
				aplicado = true;
		}
		if (pstm != null) {
			pstm.close();
		}
		if (res != null) {
			res.close();
		}
		return aplicado;
	}
	
	public static ArrayList<ReintegroDetalle> getReintegroLayout(Connection conn,int folio) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		ArrayList<ReintegroDetalle> detalles = new ArrayList<ReintegroDetalle>();
		String tipoPago = "";
		String query = "";
		
		tipoPago = getTipoPagoReint(conn, folio);
		
		query = "SELECT DISTINCT " +
				"	d.nDocRenglon , " +
				"	ep.cRamoEP , " +
				"	ep.cUnidadResponsableEP , " +
				"	ep.aEjercicioFiscal , " +
				"	ep.cGrupoFuncional , " +
				"	ep.cFuncion , " +
				"	ep.cSubFuncion , " +
				"	CASE WHEN ep.cProgramaGeneral IN ( SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK) ) THEN '00' ELSE ep.cProgramaGeneral END AS cProgramaGeneral , " + // ep.cProgramaGeneral 
				"	ep.cActividadInstitucional , " +
				"	ep.cProgramaPresupuestario , " +
				"	SUBSTRING(ep.cPartida, 1, 1) AS cCapitulo , " +
				"	SUBSTRING(ep.cPartida, 2, 1) AS cConcepto , " +
				"	SUBSTRING(ep.cPartida, 3, 1) AS cPartida , " +
				"	SUBSTRING(ep.cPartida, 4, 2) AS cPartidaEspecifica , " +
				"	ep.cTipoGasto , " +
				"	ep.cFuenteFinanciamiento , " +
				"	ep.cEntidadFederativa , " +
				"	ep.cCartera , " +
				"	LEFT(REPLICATE('0', 7) + ep.cUnidadEjecutora, 10) AS cUnidadEjecutora , " +
				"	SUBSTRING(ep.cUnidadNorativa, 2, 2) AS cUnidadNormativa , " +
				"	'0' , " +
				"	'0' , " +
				"	'0' , " +
				"	'0' , " +
				"	'0' , " +
				"	CONVERT(VARCHAR, d.mImporte) AS mImporte , " +
				"	d.cMes , " +
				"	ISNULL(NCOM_15,'') 'NCOM_15', " +
				"	sCBEN CBEN, " +
				"	'' AS cFillRellen2 , " +
				"	CASE WHEN NOIF_18 IS NULL THEN 0 " +
				"	WHEN ( noif_18 = '' OR noif_18 = 'NULL' ) " +
				"		THEN 0 " +
				"		ELSE CONVERT(INT, NOIF_18) " +
				"	END AS sol_oli , " +
				"	ISNULL(TPAG_117,'')TPAG_117, " +
				"	CASE WHEN clc.CONC_MOV_50 IS NULL OR clc.CONC_MOV_50 = '' " +
				"		THEN '' " +
				"		ELSE REPLICATE('0', 3 - LEN(LTRIM(clc.CONC_MOV_50))) + LTRIM(CAST(clc.CONC_MOV_50 AS VARCHAR(3))) " +
                "	END AS ID_TIPO_CONCEPTO , " +
                "	0 AS retencion_isr , " +
                "	'0' , " +
                "	d.cEvento, " +
                "	NCLC_43 nFolioSICOP, " +
                "	d.EP, " +
                "	SECU_86 secCLC, " +
                "	SPAG_176, " +
                "	TCONC_49, " +
                "	ISNULL(NRES_17, '') AS NRES_17 " +
                "	FROM tReintegroIngresoDetalle d WITH(NOLOCK) " +
                "	INNER JOIN tCatalogoEP ep WITH(NOLOCK) " +
                "		ON d.EP = ep.EP " +
                "	INNER JOIN dbo.tLayoutsCreadosRegistroIngresoEncabezado lay WITH(NOLOCK) " +
                "		ON sAuxiliarComodin = d.cxp " +
                "	INNER JOIN dbo.CLC_SICOP clc WITH(NOLOCK) " +
                "		ON NCTR_47=d.cxp " +
                "			AND ( CASE WHEN SUBSTRING(EP.EP, 20, 2) IN ( SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK) ) THEN (SUBSTRING(EP.EP, 1, 19) + '00' + SUBSTRING(EP.EP, 22, 34))  ELSE SUBSTRING(EP.EP, 1, 55) END ) =" + // SUBSTRING(EP.EP,1,55)
                "					  CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+ " +
				"					  CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END + '.'+ " +
				"					  CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+ " +
				"					  CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+ " +
				"					  CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+ " +
				"					  CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+ " +
				"					  CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END " +
				//"			AND clc.MES_149=D.cMes " +
                "	WHERE nFolioReintegroIngreso = ?";
		ReintegroDetalle rd;
		pstm = conn.prepareStatement(query);
		pstm.setInt(1, folio);
		res = pstm.executeQuery();
		int numRows = 0; 
		log.debug(query);
		while(res.next()){
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
		
		if (pstm != null) {
			pstm.close();
		}
		if (res != null) {
			res.close();
		}
		return detalles;
	}

	private static String getTipoPagoReint(Connection conn, int folio) throws SQLException {
		ResultSet rs = null;		
		PreparedStatement pstm = null;
		String tipoPago = "";
		
		pstm = conn.prepareStatement("SELECT cTipoPago FROM dbo.tReintegroIngresoDetalle AS rd WITH (NOLOCK) JOIN dbo.tPagadoEncabezado AS p WITH (NOLOCK) ON cxp =caNoContrarrecibo WHERE nFolioReintegroIngreso = ?");
		pstm.setInt(1, folio);
		rs = pstm.executeQuery();
		
		if(rs.next()){
			tipoPago = rs.getString(1);
		}
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			
		return tipoPago;
		
	}

	public static String getMesesImportes(Connection conn,String EP, String CXP) throws SQLException{
		String res="";
		ResultSet rs = null;
		PreparedStatement pstm = null;
		pstm = conn.prepareStatement("SELECT pd.nDocRenglon,pd.cMes, pd.mImporteNeto FROM tPagadoEncabezado pe with(nolock), tPagadoDetalle pd with(nolock) WHERE pd.nFolioPagado=pe.nFolioPagado AND pd.EP=? AND pe.caNoContrarrecibo=? AND pe.cDocumentoHaplicado='S'");
		pstm.setString(1, EP);
		pstm.setString(2, CXP);
		rs = pstm.executeQuery();
		int contador=0;
		res = "--Para la EP "+EP+ " en la CXP "+CXP+", hay:\\n";
		while(rs.next()){
			res+="en el Renglon "+rs.getInt("nDocRenglon")+" Mes "+rs.getInt("cMes")+" el Importe de "+rs.getDouble("mImporteNeto")+"\\n";
			contador++;
		}
		if(contador==0)
			res = "La cuenta por pagar proporcionada no se encuentra pagada\\n";
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		
		return res;
	}
	
	public static boolean getEPCatalogo(Connection conn,String EP) throws SQLException{
		boolean res=false;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		pstm = conn.prepareStatement("SELECT COUNT(*) FROM tCatalogoEP with(nolock) WHERE EP=?");
		pstm.setString(1, EP);
		rs = pstm.executeQuery();
		if(rs.next()){
			int cuantos = rs.getInt(1);
			if(cuantos>0)
				res = true;
		}
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		
		return res;
	}
	
	public static boolean actualizaInfoPagos(Connection conn, int folio,String clvRastreo, String lc,String ficha,String clvBanco, String cuenta, String fAcredit, String ctab, String aviso, String folioDep) throws SQLException, ParseException{
		PreparedStatement pstmntUp = null;
		PreparedStatement pstmntUp2 = null;
		boolean retval =false;
		boolean retval2 =false;
		
		try {
		    pstmntUp = conn.prepareStatement("UPDATE tReintegroIngresoEncabezado SET clvRastreo=?, lc=?, fichaDeposito=?, clvBanco=?, cuentaBancaria=?, aviso=?, folioDependencia=?  WHERE nFolioReintegroIngreso=?");
		    pstmntUp.setString(1, clvRastreo);
			pstmntUp.setString(2, lc);
			pstmntUp.setString(3, ficha);
			pstmntUp.setString(4, clvBanco);
			pstmntUp.setString(5, cuenta);
			pstmntUp.setString(6, aviso);
			pstmntUp.setString(7, folioDep);
			pstmntUp.setInt(8, folio);
			retval = pstmntUp.execute();
			
			if(ctab!=null && ctab != ""){
				pstmntUp2 = conn.prepareStatement("UPDATE dbo.tReintegroIngresoDetalle SET CTAB = ? WHERE nFolioReintegroIngreso = ?");
				pstmntUp2.setString(1, ctab);
				pstmntUp2.setInt(2, folio);
								
				retval2 = pstmntUp2.execute();
			}
						
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmntUp != null)
				pstmntUp.close();
			pstmntUp = null;
		}
		if(!retval || !retval2)
			return false;
		else
			return true;
	}
	
	public static boolean catalogoMovimiento(Connection conn, String movimiento) throws SQLException, ParseException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean retval =false;
		pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoMvto with(nolock) WHERE tipo = ?");
		pstm.setString(1, movimiento);
		rs = pstm.executeQuery();
		if(rs.next()){
			int cuantos = rs.getInt(1);
			if(cuantos>0)
				retval = true;
		}
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		return retval;
	}
	
	public static boolean catalogoCausaAviso(Connection conn, int causa, int tipo ) throws SQLException, ParseException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean retval =false;
		pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoCausaAviso with(nolock) WHERE aCausa = ?");
		pstm.setInt(1, causa);
		rs = pstm.executeQuery();
		if(rs.next()){
			int cuantos = rs.getInt(1);
			if(cuantos>0)
				retval = true;
		}
		if (pstm != null) {
			pstm.close();
		}
		if (rs != null) {
			rs.close();
		}
		return retval;
	}
	
	public static int secCLC(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval =0;
		try {
		    pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? and SEC not in (SELECT secCLC FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=? and noCLC=? and EP=?)");
		    pstmnt.setString(1, caNoContrarrecibo);
		    pstmnt.setString(2, EP);
		    pstmnt.setInt(3, folio);
		    pstmnt.setString(4, caNoContrarrecibo);
		    pstmnt.setString(5, EP);
			
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getInt(1);
			}else{
				retval = -1;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static int secCLCRect(Connection conn, String caNoContrarrecibo, String EP) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval =0;
		String ep = EP.substring(0, 55);
		try {
		    pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
		    pstmnt.setString(1, String.format("%.0f", Double.parseDouble( caNoContrarrecibo ) ));
		    pstmnt.setString(2, ep);
		    
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getInt(1);
			}else{
				retval = -1;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static String tipoConcepto(Connection conn, String caNoContrarrecibo, String EP) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval ="";
		try {
		    pstmnt = conn.prepareStatement("SELECT TCONC_49 FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
		    pstmnt.setString(1, caNoContrarrecibo);
		    pstmnt.setString(2, EP);
		    
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getString(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static String folioDependencia(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval ="";
		try {
		    pstmnt = conn.prepareStatement("SELECT NCLC_43 FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? ");
		    pstmnt.setString(1, caNoContrarrecibo);
		    pstmnt.setString(2, EP);
			
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getString(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static String getRFC(Connection conn, String caNoContrarrecibo, String ep) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval ="";
		try {
			String queryRFC="SELECT top(1) ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.EP=?";
			pstmnt = conn.prepareStatement(queryRFC);
			pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, ep);
			rs = pstmnt.executeQuery();
			if (rs.next()) {
			    retval = rs.getString(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}

	public static int[] getNDocRenglon(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int[] retval =new int[100];
		try {
			int i=0;
		    //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=?  order by pd.mImporteNeto");
		    pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			//pstmnt.setInt(4, folio);
			rs = pstmnt.executeQuery();
			retval[0]=-1;
			while(rs.next()){
				retval[i] = rs.getInt(1);
				i++;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static int getDetallePasoTotal(Connection conn, int folio) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval =0;
		try {
		    pstmnt = conn.prepareStatement("SELECT count(*) FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?");
		    pstmnt.setInt(1, folio);
			
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getInt(1);
			}else{
				retval = -1;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static boolean borraReintegro(Connection conn, int folioReintegro) throws SQLException{
		PreparedStatement pstmnt = null;
		boolean retval =false;
		try {
			String queryEnc="DELETE FROM tReintegroIngresoEncabezado WHERE nFolioReintegroIngreso = ?";
			String queryDet="DELETE FROM tReintegroIngresoDetalle WHERE nFolioReintegroIngreso = ?";
			pstmnt = conn.prepareStatement(queryDet);
			pstmnt.setInt(1, folioReintegro);
			pstmnt.execute();
			pstmnt = conn.prepareStatement(queryEnc);
			pstmnt.setInt(1, folioReintegro);
			pstmnt.execute();
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}

	public static void insertaReintegro(Connection conn, ReintegroIngresoEncabezado reinE, ArrayList<ReintegroIngresoDetalle> reinDetalles,int folio,String folioCompleto,Usuario usuario) throws Exception {
		PreparedStatement psInsertDetalle = null;
		PreparedStatement psInsertEncabezado = null;
		try {
			
			String cDescripcionPoliza = "";
			if("IP".equals(reinE.getcTipoReintegro())){
				cDescripcionPoliza = "REINTEGRO RECURSO INGRESO PROPIO FOLIO " + Integer.toString(folio);				
			}else{
				cDescripcionPoliza = "APARTADO DE REINTEGRO RECURSO FISCAL FOLIO " + Integer.toString(folio);				
			}
			
			psInsertEncabezado = conn.prepareStatement("INSERT INTO tReintegroIngresoEncabezado(nFolioReintegroIngreso,fSolicitud,cTipoReintegro,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit,cDescripcionPoliza) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		    psInsertEncabezado.setInt(1, folio);			 //folio del reintegro
		    psInsertEncabezado.setString(2,  reinE.getfSolicitud());			 //fecha de hoy, pasar como parametro o recalcular
		    psInsertEncabezado.setString(3, reinE.getcTipoReintegro()); 		 //cTipoReintegro dependiendo si es de compromiso, directa 
		    psInsertEncabezado.setString(4, reinE.getcRamo()); 		 			//cRamo
		    psInsertEncabezado.setString(5, reinE.getcUnidadResponsable()); 			 //cUnidadResponsable
		    psInsertEncabezado.setString(6, reinE.getcDocumentoHaplicado()); 			 
		    psInsertEncabezado.setString(7, reinE.getaEjercicioFiscal());  //aEjercicioFiscal (2012,2013...)
		    psInsertEncabezado.setString(8, reinE.getObservaciones());  //observaciones
		    psInsertEncabezado.setString(9, reinE.getConcepto()); //concepto
		    psInsertEncabezado.setString(10, usuario.getLogin());  
		    psInsertEncabezado.setString(11, reinE.getFormaDePago());		 //forma de pago (efectivo, transferencia) [catalogo]
		    psInsertEncabezado.setString(12, reinE.getClvRastreo());	 //clave de rastreo
		    psInsertEncabezado.setString(13, reinE.getFichaDeposito());	//ficha de deposito en el banco
		    psInsertEncabezado.setString(14, reinE.getClvBanco());			//clave del pago en el banco
		    psInsertEncabezado.setString(15, reinE.getCuentaBancaria());			//cuenta bancaria
		    psInsertEncabezado.setString(16, reinE.getLc());					//linea de captura clave
		    psInsertEncabezado.setDouble(17, Double.parseDouble(reinE.getImporteLC()));		//importe de la linea de captura
		    psInsertEncabezado.setString(18, reinE.getcTipoPoliza());		//cTipoPoliza
		    psInsertEncabezado.setString(19, reinE.getfAplicacion());
		    psInsertEncabezado.setString(20, reinE.getcUnidadResponsableContable());
		    psInsertEncabezado.setString(21, reinE.getFolioDependencia());
		    psInsertEncabezado.setString(22, reinE.getMovimiento());
		    psInsertEncabezado.setString(23, reinE.getAviso());
		    psInsertEncabezado.setString(24, reinE.getTipoAviso());
		    psInsertEncabezado.setString(25, reinE.getCausaAviso());
		    psInsertEncabezado.setString(26, reinE.getfAcreditacion());
		    psInsertEncabezado.setString(27, cDescripcionPoliza);
			psInsertEncabezado.execute();
			
			psInsertDetalle = conn.prepareStatement("INSERT INTO tReintegroIngresoDetalle(nFolioReintegroIngreso,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,obgt,ctab)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			for (Iterator<ReintegroIngresoDetalle> i = reinDetalles.iterator(); i.hasNext();) {
				ReintegroIngresoDetalle rd = i.next();
				psInsertDetalle.setInt(1, reinE.getnFolioReintegro());
				psInsertDetalle.setInt(2, Integer.parseInt(rd.getnDocRenglon().replace(".0", ""),10));
				psInsertDetalle.setString(3, rd.getnSIAFF());
				psInsertDetalle.setString(4, rd.getSecCLC());
				psInsertDetalle.setString(5, rd.getcEvento());
				psInsertDetalle.setString(6, rd.getEP());
				psInsertDetalle.setDouble(7, rd.getmImporteCLC());
				psInsertDetalle.setDouble(8, rd.getmImporteCLC());
				psInsertDetalle.setDouble(9, rd.getmImporteCLC()*-1);
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
				psInsertDetalle.addBatch();
			}

			psInsertDetalle.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			throw e;
		} finally {
			CloseObject.closeObject(psInsertDetalle, false);
		}

	}

	public static boolean validaCatMovimiento(Connection conn,String texto) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoMvto with(nolock) WHERE tipo = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if(res.next()){
				if(res.getInt(1)>0)
					existe = true;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstm != null) {
				pstm.close();
			}
			if (res != null) {
				res.close();
			}
		}
		
		return existe;
	}
	
	public static boolean validaCatTipoAviso(Connection conn,String texto) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoTipoCausaAviso with(nolock) WHERE cTipoCausaAviso = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if(res.next()){
				if(res.getInt(1)>0)
					existe = true;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstm != null) {
				pstm.close();
			}
			if (res != null) {
				res.close();
			}
		}
		
		return existe;
	}
	
	public static boolean validaCatFormaPago(Connection conn,String texto) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoFormaPago with(nolock) WHERE cFormaPago = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if(res.next()){
				if(res.getInt(1)>0)
					existe = true;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstm != null) {
				pstm.close();
			}
			if (res != null) {
				res.close();
			}
		}
		
		return existe;
	}
	
	public static boolean validaCatCausaAviso(Connection conn,String texto) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		boolean existe = false;
		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM tCatalogoCausaAviso with(nolock) WHERE cCausaAviso = ?");
			pstm.setString(1, texto);
			res = pstm.executeQuery();
			if(res.next()){
				if(res.getInt(1)>0)
					existe = true;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstm != null) {
				pstm.close();
			}
			if (res != null) {
				res.close();
			}
		}
		
		return existe;
	}
	
	public static ReintegroIngresoDetalle datosSICOP(Connection conn, String fSIAFF, String EP, String folio, String movimiento, String concepto) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroIngresoDetalle mil = new ReintegroIngresoDetalle();
		try {
			pstm = conn.prepareStatement("SELECT * FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? and CONC_MOV_50=? and TCONC_49=?");
			pstm.setString(1, fSIAFF);
			pstm.setString(2, EP);
			pstm.setString(3, movimiento);
			pstm.setString(4, concepto);
			res = pstm.executeQuery();
			if(res.next()){
				mil.setSecCLC(res.getString("SEC"));
				mil.setFolioDependenciaSicop("NCLC_43");
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstm != null) {
				pstm.close();
			}
			if (res != null) {
				res.close();
			}
		}
		
		return mil;
		
	}
	
	//TODO: No se utiliza 
	public static int getRenglonPagadoMil(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, String concepto, int movimiento) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval=-1;
		try {
			//pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=?  and ID_TIPO_MOVIMIENTO=? and ID_TIPO_CONCEPTO=?");
		    pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			pstmnt.setInt(4, movimiento);
			pstmnt.setString(5, concepto);
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getInt(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}

	public static String getALM(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval="";
		try {
			//pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			pstmnt = conn.prepareStatement("SELECT ALM FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon=?");
		    pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			pstmnt.setInt(4, renglon);
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getString(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
		
	public static String getCCNormal(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval="";
		try {
			//pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			//pstmnt = conn.prepareStatement("SELECT  pd.cCentroContable FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock),tNOMINACLCEncabezado ne with(nolock), tNOMINACLCDetalle nd with(nolock) WHERE pe.caNoContrarrecibo=? and ne.caNoContrarreciboCLC=? and pd.EP=? and nd.EP=? and pd.nMes=? and nd.cMes=?  and nd.caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and nd.nDocRenglonCLC=pd.nDocRenglon and nd.nFolioNOMINACLC=ne.nFolioNOMINACLC");
			pstmnt = conn.prepareStatement("SELECT  pd.cCentroContable FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE pe.caNoContrarrecibo=? and pd.EP=? and pd.nMes=? and pd.nDocRenglon=? and pd.nFolioPagado=pe.nFolioPagado");
		    pstmnt.setString(1, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			pstmnt.setInt(3, cMes);
			pstmnt.setInt(4, renglon);
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getString(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static boolean getTipoCLC(Connection conn, int folioCLC) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		boolean retval=false;
		try {
			//pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			pstmnt = conn.prepareStatement("SELECT TIPO_CLC FROM CLC_SIAFF_ENC with(nolock), tReintegroDetalleMil with(nolock) WHERE FOLIO_CLC=tReintegroDetalleMil.noCLC and tReintegroDetalleMil.nFolioReintegroMil=?");  
		    pstmnt.setInt(1, folioCLC);
			rs = pstmnt.executeQuery();
			while(rs.next()){
				if("COMPENSADA".equals(rs.getString(1)))
					retval=true;
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
		return retval;
	}
	
	public static String getListaCorreos(Connection conn,Caso c)throws SQLException{
		String to="";
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		
		String sSQL = 	"select distinct u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=?)";
		
		pstmnt = conn.prepareStatement(sSQL);
		if(c!=null && c.getFolio()!=null)
			pstmnt.setString(1, c.getFolio());
		else
			pstmnt.setString(1,"BXX");//no encontrara nada, solo es para que corra el qry
		rs = pstmnt.executeQuery();
		boolean enviar=false;
		while(rs.next()){
			to += (enviar?";":"")+ rs.getString(1);
			enviar=true;
		}
		return to;
	}

	public static String getCorreoRevisor(Connection conn,Caso c)throws SQLException{
		String to="";
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		
		String sSQL = "select top(1) u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=? and B_ID_OPER=2)";
		
		pstmnt = conn.prepareStatement(sSQL);
		if(c!=null && c.getFolio()!=null)
			pstmnt.setString(1, c.getFolio());
		else
			pstmnt.setString(1,"BXX");//no encontrara nada, solo es para que corra el qry
		rs = pstmnt.executeQuery();
		boolean enviar=false;
		if(rs.next()){
			to = (enviar?";":"")+ rs.getString(1);
			enviar=true;
		}
		return to;
	}

	public static boolean actualizaFechaAplicacion(Connection conn, int folio,String fAplicacion) throws SQLException, ParseException{
		PreparedStatement pstmntUp = null;
		boolean retval =false;
		try {
		    pstmntUp = conn.prepareStatement("UPDATE tReintegroIngresoEncabezado SET fAplicacion=? WHERE nFolioReintegroIngreso=?");
		    pstmntUp.setString(1, fAplicacion);
			pstmntUp.setInt(2, folio);
			retval = pstmntUp.execute();
			// TODO: Afectar la tabla de Ingreso x Pago para tener todos los documentos que afectan el ingreso fiscal
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmntUp != null)
				pstmntUp.close();
			pstmntUp = null;
		}
		return retval;
	}

	// TODO: Esta duplicada y no se utiliza esta. Velidar y eliminar 
	/*public static String validaMes(Connection conn,int folio, String fAcredit) {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval="";
		int mes = Integer.parseInt(fAcredit.substring(3,5));;
		
		try {
			
			pstmnt = conn.prepareStatement("SELECT mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cCentroContable = (SELECT TOP 1 cCentroContable FROM dbo.tReintegroIngresoDetalle (NOLOCK) WHERE nFolioReintegroIngreso = ?) AND nMes = ?");
			pstmnt.setInt(1, folio);
			pstmnt.setInt(2, mes);
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getString(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt = null;
		}		
		return retval;
	}*/

	public static String validaMes(Connection conn, Caso c, int folio, String fAcredit) {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval="";
		int mes = Integer.parseInt(fAcredit.substring(3,5),10);
		
		try {
			
			pstmnt = conn.prepareStatement("SELECT mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cCentroContable = (SELECT TOP 1 cCentroContable FROM dbo.tReintegroIngresoDetalle (NOLOCK) WHERE nFolioReintegroIngreso = ?) AND nMes = ?");
			pstmnt.setInt(1, folio);
			pstmnt.setInt(2, mes);
			rs = pstmnt.executeQuery();
			if(rs.next()){
				retval = rs.getString(1);
			}
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmnt != null)
				pstmnt = null;
		}		
		return retval;
	}

	public static boolean actualizaEventoIF(Connection conn, int folio) throws SQLException {
		PreparedStatement pstmntUp = null, ps = null;
		boolean retval =false;
		ResultSet rs = null;
		String tipoIngreso = "";
		
		try {			
			
			ps = conn.prepareStatement("SELECT cTipoReintegro FROM tReintegroIngresoEncabezado WHERE nFolioReintegroIngreso = ?");
			ps.setInt(1, folio);			
			rs = ps.executeQuery();
			if(rs.next()){
				tipoIngreso = rs.getString(1);
			}
			
			if ("IF".equals(tipoIngreso)) {
			    pstmntUp = conn.prepareStatement("UPDATE tReintegroIngresoDetalle SET cEvento = (\r\n" + 
											    	"CASE WHEN SUBSTRING(EP,32,1) IN (1,2,3) AND cEvento = 'RN' THEN 'RN_IF_CAP123' \r\n" + 
											    	"	  WHEN SUBSTRING(EP,32,1) IN (1,2,3) AND cEvento <> 'RN' THEN 'RD_IF_CAP123' \r\n" +
											    	"	  WHEN SUBSTRING(EP,32,1) IN (4) AND cEvento = 'RN' THEN 'RN_IF_CAP4' \r\n" + 
											    	"	  WHEN SUBSTRING(EP,32,1) IN (4) AND cEvento <> 'RN' THEN 'RD_IF_CAP4' \r\n" +
											    	"	  WHEN SUBSTRING(EP,32,1) IN (5,6) AND cEvento = 'RN' THEN 'RN_IF_CAP56' \r\n" +
											    	"	  WHEN SUBSTRING(EP,32,1) IN (5,6) AND cEvento <> 'RN' THEN 'RD_IF_CAP56' \r\n" +
											    	"END) \r\n" + 
											    	"FROM tReintegroIngresoDetalle WHERE nFolioReintegroIngreso = ?");		    
				pstmntUp.setInt(1, folio);
				retval = pstmntUp.execute();	
			}
			
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmntUp != null)
				pstmntUp.close();
			pstmntUp = null;
		}		
		
		return retval;
	}
	
}
