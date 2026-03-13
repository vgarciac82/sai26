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
import com.syc.obrapublica.EjercicioFiscal;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReintegrosMilManager {
	private static final Logger log = Logger.getLogger(ReintegrosMilManager.class);
	public ReintegrosMilManager() {
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
		    pstmntIns = conn.prepareStatement("INSERT INTO tReintegroDetallePaso(noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento,mImporte,mImporteNegativo,cCentroContable,nFolioReintegro,nDocRenglon,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,obgt) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
		ResultSet rs = null;
		boolean retval =false;
		//boolean existe = false;
		try {
			String query="INSERT INTO tReintegroDetalle SELECT * FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			//retval = pstmnt.execute();
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
			String query="DELETE FROM tReintegroDetalle WHERE nFolioReintegro = ?";
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
			String query="SELECT * FROM tReintegroEncabezado with(NOLOCK) WHERE nFolioReintegro = ?";
			pstmnt = conn.prepareStatement(query);
			pstmnt.setInt(1, folioReintegro);
			//retval = pstmnt.execute();
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
			}else if(datos!=null){
			    pstmntIns = conn.prepareStatement("INSERT INTO tReintegroEncabezado(nFolioReintegro,fSolicitud,cTipoReintegro,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
		String query="SELECT * FROM tReintegroDetalle rd with (nolock) WHERE nFolioReintegro = ? AND (";
		for(int j=0;j<ids.length;j++){
			query+=" nDocRenglon=? OR";
		}
		query = query.substring(0, query.length()-3);
		query+=")";
		pstmnt = conn.prepareStatement(query);
		pstmnt.setInt(1, folio);
		for(int j=0;j<ids.length;j++){
			pstmnt.setInt(j+2, Integer.parseInt(ids[j]));
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
	
	@SuppressWarnings( "resource" )
	public static void autorizaReintegro(Connection conn, Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP,String ulogin,String prefixPath, String fAcredit, String EjercicioFiscal) throws SQLException, GestionException {
		PreparedStatement pstmnt = null, pst3 = null;
		int nIdCaso;
		ResultSet rs = null, rs2 = null;		

		try {
			nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			
			/*VALIDA SI YA EXISTE EL REINTEGRO EN LA TABLA AUT*/
			pst3 = conn.prepareStatement("SELECT CASE WHEN COUNT(*) > = 1 THEN 'S' ELSE 'N' END existe FROM tReintegroAutEncabezadoMil WITH(NOLOCK) WHERE nFolioReintegroMilaut = ?");			
			pst3.setInt(1, nIdCaso);
			rs2 = pst3.executeQuery();
			String existe = "";
			
			if (rs2.next()) {
				existe = rs2.getString(1);
			}
			
			if("N".equals( existe )) {
				pstmnt = conn.prepareStatement("INSERT INTO tReintegroAutEncabezadoMil (nFolioReintegroMilaut, fSolicitud, cRamo, cUnidadResponsable, cDocumentoHaplicado, aEjercicioFiscal, observaciones, concepto, u_login, "
				        + "cUnidadResponsableContable,nFolioTramiteSicop, cTipoPoliza, fAplicacion,cdescripcionpoliza, formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso) "
				        		+"SELECT nFolioReintegroMil, fSolicitud, cRamo,cUnidadResponsable, 'N', aEjercicioFiscal, observaciones, concepto, "
				        + "u_login, 'RHQ',nFolioTramiteSicop, 'EG', '" + fAcredit +"','AUTORIZACION DE REINTEGRO PRESUPUESTAL FOLIO ' + CAST(nFolioReintegroMil as varchar),formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso FROM tReintegroEncabezadoMil WITH (NOLOCK) WHERE nFolioReintegroMil = ?");

				pstmnt.setInt(1, nIdCaso);
				pstmnt.execute();

				pstmnt = conn.prepareStatement("SELECT tipoAviso FROM tReintegroEncabezadoMil WITH (NOLOCK) WHERE nFolioReintegroMil = ?");
				pstmnt.setInt(1, nIdCaso);
				rs = pstmnt.executeQuery();
				String tipoRein = "";
				if (rs.next()) {
					tipoRein = rs.getString(1);
				}

				if("2015".equals(EjercicioFiscal))
					tipoRein = "RC";
				else 
					tipoRein = "RC_EG";

				pstmnt = conn.prepareStatement("INSERT INTO treintegroautdetallemil  "
					+"            (nfolioreintegromilaut,  "
					+"             ndocrenglon,  "
					+"             noclc,  "
					+"             secclc,  "
					+"             ep,  "
					+"             cmes,  "
					+"             cevento,  "
					+"             mimporte,  "
					+"             mimportenegativo,  "
					+"             ccentrocontable,  "
					+"             ncapitulo,  "
					+"             cxp,  "
					+"             rfc,  "
					+"             nfoliodependencia,  "
					+"             alm,  "
					+"             nrenglonpagado,  "
					+"             tipoconcepto,  "
					+"             tipomovimiento,  "
					+"             cxpnomina," 
					+"             OBGT,"
					+"             CTAB,"
					+"             mPasivoDiferido"
					+")  "
					+"SELECT rd.nfolioreintegromil,  "
					+"       rd.ndocrenglon,  "
					+"       rd.noclc,  "
					+"       rd.secclc,  "
					+"       rd.ep,  "
					+"       rd.cmes,  "
					+"		 ? + SUBSTRING(cEvento, 6, len(cEvento)), "	
					+"       rd.mimporte,  "
					+"       rd.mimportenegativo,  "
					+"       rd.ccentrocontable,  "
					+"       rd.ncapitulo,  "
					+"       rd.cxp,  "
					+"       rd.rfc,  "
					+"       rd.nfoliodependencia,  "
					+"       (SELECT TOP(1) alm  "
					+"        FROM   tpagadodetalle WITH(nolock),  "
					+"               tpagadoencabezado WITH(nolock)  "
					+"        WHERE  ep = rd.ep  "
					+"               AND nfoliosiaff = rd.noclc  "
					+"               AND tpagadodetalle.nfoliopagado = tpagadoencabezado.nfoliopagado  "
					+"               AND tpagadoencabezado.canocontrarrecibo = rd.cxp) AS ALM,  "
					+"       rd.nrenglonpagado,  "
					+"       rd.tipoconcepto,  "
					+"       rd.tipomovimiento,  "
					+"       rd.cxpnomina, " 
					+"       rd.OBGT, "
					+"       rd.CTAB, "
					+"       rd.mPasivoDiferido "			
					+"FROM   treintegrodetallemil rd WITH (nolock)  "
					+"WHERE  rd.nfolioreintegromil = ?");
				pstmnt.setString(1, tipoRein);			
				pstmnt.setInt(2, nIdCaso);				
				pstmnt.execute();

				conn.commit();
			}
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

	public static double getRemanente(Connection conn,String ep,String cxp,int nRenglon, String cxpnomina) throws SQLException {
		double remanente = 0.00;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		 
		
		String queryRemanente="SELECT CASE tReintegros+tRectificacion " +
											" WHEN 0 THEN mImporteNeto " +
										" ELSE " +
											" CASE WHEN tReintegros!=0 AND tRectificacion=0 THEN mImporteNeto-impReint ELSE" +
													" impRect-impReint" +
										" END " +
									" END as Remanente" +
							" FROM (SELECT pd.mImporteNeto," +
									" (select COUNT(*) as sumaRect FROM tRectificacionEncabezadoMil re with (nolock), tRectificacionDetalle rd with (nolock) WHERE rd.EP=? AND re.cDocumentoHaplicado='S' AND re.nFolioRectificacionMil=rd.nFolioRectificacion AND re.caNoContrarreciboMil=? AND rd.EP=pd.EP and rd.nDocRenglon=? and re.caNoContrarrecibo=?) as tRectificacion," +
									" (select COUNT(*) as sumaReint FROM tReintegroEncabezadoMil ree with (nolock), tReintegroDetalleMil red with (nolock) WHERE red.EP=?  AND ree.nFolioReintegroMil=red.nFolioReintegroMil AND ree.cDocumentoHaplicado='S' AND red.cxp=? AND red.EP=pd.EP and red.nRenglonPagado=? and red.cxpnomina=?) as tReintegros," +
									" ISNULL((select ISNULL(SUM(rd.mImporte),0) as impR from tReintegroEncabezadoMil re WITH(NOLOCK),tReintegroDetalleMil rd WITH(NOLOCK) where re.nFolioReintegroMil =rd.nFolioReintegroMil AND re.cDocumentoHaplicado='S' and rd.cxp=? and rd.EP=? and rd.nRenglonPagado=? and rd.cxpnomina=?),0) as impReint," +
									" ISNULL((select ISNULL(SUM(case substring(cEvento,1,4)  when 'DICE' then  mImporte " +
													" WHEN 'DEBE' then mImporteNegativo end ),0) as imp " +
													" FROM tRectificacionDetalleMil r with (nolock), tRectificacionEncabezadoMil h with (nolock)" +
													" WHERE r.nFolioRectificacionMil = h.nFolioRectificacionMil  " +
													" AND h.caNoContrarreciboMil = ? and h.caNoContrarrecibo=? and r.nDocRenglon  = ? AND h.cDocumentoHaplicado='S' " +
													" AND ep not in (select  ep from tRectificacionDetalle d with (nolock) WHERE d.nDocRenglon = r.nDocRenglon " +
													" AND d.cEvento like 'DICE%' and d.ep= ?) " +
													" GROUP BY r.nDocRenglon),0) as impRect "+
						" FROM tPagadoEncabezado pe with (nolock), tPagadoDetalle pd with (nolock), tNominaCLCDetalle nd with (nolock), tNominaCLCEncabezado ne with (nolock)" +
						" WHERE pe.nFolioPagado=pd.nFolioPagado and nd.nFolioNOMINACLC=ne.nFolioNOMINACLC and pe.caNoContrarrecibo=? and pd.EP=? and pd.nDocRenglon=? and pe.caNoContrarrecibo=ne.caNoContrarreciboCLC and pd.EP=nd.EP and pd.ID_TIPO_CONCEPTO=nd.ID_TIPO_CONCEPTO and pd.ID_TIPO_MOVIMIENTO=nd.ID_TIPO_MOVIMIENTO and ne.caNoContrarreciboCLC=? and nd.EP=? and nd.nDocRenglonCLC=pd.nDocRenglon) tNueva " +
						" WHERE 1=1";
		//cxpnomina 10CP
		//cxp 10NC
		pstm = conn.prepareStatement(queryRemanente);
		pstm.setString(1, ep);
		pstm.setString(2, cxp);
		pstm.setInt(3, nRenglon);
		pstm.setString(4, cxpnomina);
		pstm.setString(5, ep);
		pstm.setString(6, cxpnomina);
		pstm.setInt(7, nRenglon);
		pstm.setString(8, cxp);
		pstm.setString(9, cxpnomina);
		pstm.setString(10, ep);
		pstm.setInt(11, nRenglon);
		pstm.setString(12, cxp);
		pstm.setString(13, cxp);
		pstm.setString(14, cxpnomina);
		pstm.setInt(15, nRenglon);
		pstm.setString(16, ep);
		pstm.setString(17, cxp);
		pstm.setString(18, ep);
		pstm.setInt(19, nRenglon);
		pstm.setString(20, cxp);
		pstm.setString(21, ep);
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
		pstm = conn.prepareStatement("SELECT * FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
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
	
	public static ReintegroEncabezadoMil getReintegroEncabezadoNuevo(Connection conn,int folio) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroEncabezadoMil re = null;
		pstm = conn.prepareStatement("SELECT * FROM tReintegroEncabezadoMil with(nolock) WHERE nFolioReintegroMil = ?");
		pstm.setInt(1, folio);
		res = pstm.executeQuery();
		if(res.next()){
			re = new ReintegroEncabezadoMil();
			re.setfSolicitud(res.getString("fSolicitud"));
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
			re.setcIngreso(res.getInt("cIngreso"));
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
		pstm = conn.prepareStatement("SELECT * FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = ?");
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
		pstm = conn.prepareStatement("SELECT cDocumentoHaplicado FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
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
		ReintegroDetalle rd;
		String query = "select distinct d.nDocRenglon, e.cRamoEP, e.cUnidadResponsableEP, e.aEjercicioFiscal, e.cGrupoFuncional, e.cFuncion " +
			", e.cSubFuncion, e.cProgramaGeneral, e.cActividadInstitucional, e.cProgramaPresupuestario, substring(e.cPartida,1,1) as cCapitulo " +
			", substring(e.cPartida,2,1) as cConcepto, substring(e.cPartida,3,1) as cPartida, substring(e.cPartida,4,2) as cPartidaEspecifica " +
			", e.cTipoGasto, e.cFuenteFinanciamiento, e.cEntidadFederativa, e.cCartera, left(replicate('0', 7)+e.cUnidadEjecutora,10) as cUnidadEjecutora " +
			", substring(e.cUnidadNorativa,2,2) as cUnidadNormativa, '0','0','0','0','0', CONVERT(VARCHAR,d.mImporte) as mImporte, d.cMes, CASE WHEN NCOM_15 is null OR NCOM_15 ='' THEN '' ELSE REPLICATE('0',6-LEN(LTRIM(NCOM_15)))+LTRIM(CAST(NCOM_15 AS VARCHAR(6))) END as NCOM_15 "+
			", b.sSicop, '' as cFillRellen2 , " +
			" CASE WHEN NOIF_18 IS NULL OR '' = NOIF_18 THEN '0' ELSE CONVERT( VARCHAR(32), NOIF_18) END as sol_oli, " +
			"TPAG_117, CASE rclc.CONC_MOV_50 WHEN '0' THEN '000'ELSE rclc.CONC_MOV_50 END ID_TIPO_CONCEPTO "+
			", 0 as retencion_isr, '0', d.cEvento, p.nFolioSICOP,d.EP,rclc.SECU_86 AS secclc,SPAG_176,TCONC_49,isnull(NRES_17,'') as NRES_17 "+ 
			" from tReintegroDetalleMil d with (nolock), tReintegroEncabezadoMil r with (nolock), tCatalogoEP e with (nolock), tPagadoEncabezado P with (nolock), tPagadoDetalle dp  with (nolock), tBeneficiarioCapituloMil b with (nolock), vReintegrosCLCSICOP rclc with(nolock)" +
			" WHERE d.nFolioReintegroMil =  r.nFolioReintegroMil" +
			//" and d.EP =  e.EP and d.cxp = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.sCodigoEntidad  and rclc.EP=d.EP and rclc.EP=e.EP and rclc.caNoContrarrecibo=d.cxp and rclc.caNoContrarrecibo=p.caNoContrarrecibo and rclc.SEC=d.secCLC and d.nFolioReintegro=? and r.nFolioReintegro=?");
			" and d.EP =  e.EP and d.cxpnomina = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.sCodigoEntidad  and rclc.EP=SUBSTRING(d.EP, 1, 55-11) + '00000000000' and rclc.EP=SUBSTRING(d.EP, 1, 55-11) + '00000000000' and rclc.FolioSIAFF=d.noCLC and rclc.FolioSIAFF=p.nFolioSIAFF and d.mPasivoDiferido = 0" + /*rclc.SEC=d.secCLC " +*/
			//" and rclc.CONC_MOV_50=d.tipoMovimiento " + //esta linea es importante para discriminar
			" and d.nFolioReintegroMil=? and r.nFolioReintegroMil=?";
		log.debug(query);
		log.debug("[1]:[" + folio + "]");
		log.debug("[2]:[" + folio + "]");
		pstm = conn.prepareStatement(query);
			pstm.setInt(1, folio);
			pstm.setInt(2, folio);
			res = pstm.executeQuery();
			while(res.next()){
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
	
	public static boolean actualizaInfoPagos(Connection conn, int folio,String clvRastreo, String lc,String ficha,String clvBanco, String cuenta, String fAcredit, String ctab) throws SQLException, ParseException{
		PreparedStatement pstmntUp = null;
		PreparedStatement pstmntUp2 = null;
		boolean retval =false;
		boolean retval2 =false;
		try {					    
		    pstmntUp = conn.prepareStatement("UPDATE tReintegroEncabezadoMil SET clvRastreo=?, lc=?, fichaDeposito=?, clvBanco=?, cuentaBancaria=? WHERE nFolioReintegroMil=?");
		    pstmntUp.setString(1, clvRastreo);
			pstmntUp.setString(2, lc);
			pstmntUp.setString(3, ficha);
			pstmntUp.setString(4, clvBanco);
			pstmntUp.setString(5, cuenta);
			//pstmntUp.setString(6, fAcredit);
			pstmntUp.setInt(6, folio);
			retval = pstmntUp.execute();
			
			if(ctab!=null && ctab != ""){
				pstmntUp2 = conn.prepareStatement("UPDATE dbo.tReintegroDetalleMil SET CTAB = ? WHERE nFolioReintegroMil = ?");
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
		try {
		    pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
		    pstmnt.setString(1, caNoContrarrecibo);
		    pstmnt.setString(2, EP);
		    
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
			//String queryRFC="SELECT top(1) ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.EP=?";
			String queryRFC="SELECT TOP(1) ND.RFC FROM dbo.tNOMINADetalle ND WITH (NOLOCK), dbo.tNOMINAEncabezado NE WITH (NOLOCK) WHERE caNoContrarrecibo = ? AND ND.nFolioNOMINA = NE.nFolioNOMINA AND ND.EP = ?";
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

	

	
	public static int getNDocRenglon(Connection conn, String caNoContrarrecibo, String EP, int cMes, String cxp, String movimiento, String concepto) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		int retval = 0;
		try {
		    //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			//pstmnt = conn.prepareStatement("SELECT pd.nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock), tNOMINACLCEncabezado ne with(nolock), tNOMINACLCDetalle nd with(nolock) WHERE pe.caNoContrarrecibo=? and ne.caNoContrarreciboCLC=? and pd.EP=? and nd.EP=? and pd.nMes=? and nd.cMes=?  and nd.caNoContrarrecibo=? and pd.ID_TIPO_CONCEPTO=? and pd.ID_TIPO_MOVIMIENTO=? and pd.nFolioPagado=pe.nFolioPagado and nd.nDocRenglonCLC=pd.nDocRenglon and nd.nFolioNOMINACLC=ne.nFolioNOMINACLC");
			pstmnt = conn.prepareStatement("SELECT nDocRenglonPagado AS nDocRenglon FROM vw_PagadoNominaMil WITH (NOLOCK) WHERE caNoContrarreciboPagado = ? AND epPagado = ? AND cMesPagado = ? AND canocontrarrecibo = ? ");
		    pstmnt.setString(1, caNoContrarrecibo);
		    //pstmnt.setString(2, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			//pstmnt.setString(4, EP);
			pstmnt.setInt(3, cMes);
			//pstmnt.setInt(6, cMes);
			pstmnt.setString(4, cxp);
			//pstmnt.setString(8, concepto);
			//pstmnt.setString(9, movimiento);
			rs = pstmnt.executeQuery();
			retval=-1;
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
			String queryEnc="DELETE FROM tReintegroEncabezadoMil WHERE nFolioReintegroMil = ?";
			String queryDet="DELETE FROM tReintegroDetalleMil WHERE nFolioReintegroMil = ?";
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

	public static void insertaReintegro(Connection conn, ReintegroEncabezadoMil reinE, ArrayList<ReintegroDetalleMil> reinDetalles,int folio,String folioCompleto,Usuario usuario,Caso c) throws Exception {
		PreparedStatement psInsertDetalle = null;
		PreparedStatement psInsertEncabezado = null;
		PreparedStatement psUpdateCaso = null;
		try {
			//OTRA VEZ QUIEREN EL APARTADO, ENTONCES SE COMENTA ESTO DE ABAJO
			/*psUpdateCaso = conn.prepareStatement("UPDATE CG_CASO_DATO SET CD_VALOR=? WHERE ID_CASO = ? and ID_CD=? and ID_TC=?");
			psUpdateCaso.setString(1, "true"); //se setea con true para evitar la aplicación contable
			psUpdateCaso.setInt(2, c.getIdCaso()); 
			psUpdateCaso.setString(3, "8"); //8 es la posicion del APLICADO_CONT
			psUpdateCaso.setString(4, "26"); // ID_TC 26 REINTEGROS CAPITULO MIL
			psUpdateCaso.execute();*/
			
			psInsertEncabezado = conn.prepareStatement("INSERT INTO tReintegroEncabezadoMil(nFolioReintegroMil,fSolicitud,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso, cDescripcionPoliza) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		    psInsertEncabezado.setInt(1, folio);			 //folio del reintegro
		    psInsertEncabezado.setString(2,  reinE.getfSolicitud());			 //fecha de hoy, pasar como parametro o recalcular
		    psInsertEncabezado.setString(3, reinE.getcRamo()); 		 			//cRamo
		    psInsertEncabezado.setString(4, reinE.getcUnidadResponsable()); 			 //cUnidadResponsable
		    psInsertEncabezado.setString(5, reinE.getcDocumentoHaplicado()); 			 
		    psInsertEncabezado.setString(6, reinE.getaEjercicioFiscal());  //aEjercicioFiscal (2012,2013...)
		    psInsertEncabezado.setString(7, reinE.getObservaciones());  //observaciones
		    psInsertEncabezado.setString(8, reinE.getConcepto()); //concepto
		    psInsertEncabezado.setString(9, usuario.getLogin());  
		    psInsertEncabezado.setString(10, reinE.getFormaDePago());		 //forma de pago (efectivo, transferencia) [catalogo]
		    psInsertEncabezado.setString(11, reinE.getClvRastreo());	 //clave de rastreo
		    psInsertEncabezado.setString(12, reinE.getFichaDeposito());	//ficha de deposito en el banco
		    psInsertEncabezado.setString(13, reinE.getClvBanco());			//clave del pago en el banco
		    psInsertEncabezado.setString(14, reinE.getCuentaBancaria());			//cuenta bancaria
		    psInsertEncabezado.setString(15, reinE.getLc());					//linea de captura clave
		    psInsertEncabezado.setDouble(16, Double.parseDouble(reinE.getImporteLC()));		//importe de la linea de captura
		    psInsertEncabezado.setString(17, reinE.getcTipoPoliza());		//cTipoPoliza
		    psInsertEncabezado.setString(18, reinE.getfAplicacion());
		    psInsertEncabezado.setString(19, reinE.getcUnidadResponsableContable());
		    psInsertEncabezado.setString(20, reinE.getFolioDependencia());
		    psInsertEncabezado.setString(21, reinE.getMovimiento());
		    psInsertEncabezado.setString(22, reinE.getAviso());
		    psInsertEncabezado.setString(23, reinE.getTipoAviso());
		    psInsertEncabezado.setString(24, reinE.getCausaAviso());
		    psInsertEncabezado.setString(25, "POR LA CREACION DEL PASIVO A FAVOR DE LA TESOFE PARA REINTEGRO DE RECURSOS FISCALES REINTEGRO No. " + folio + " ");
		   // psInsertEncabezado.setString(25,  reinE.getfAcreditacion());
			psInsertEncabezado.execute();
			
			psInsertDetalle = conn.prepareStatement("INSERT INTO tReintegroDetalleMil(nFolioReintegroMil,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,tipoConcepto,tipoMovimiento,cxpnomina, obgt, CTAB, mPasivoDiferido)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			for (Iterator<ReintegroDetalleMil> i = reinDetalles.iterator(); i.hasNext();) {
				ReintegroDetalleMil rd = i.next();
				psInsertDetalle.setInt(1, reinE.getnFolioReintegro());
				psInsertDetalle.setInt(2, Integer.parseInt(rd.getnDocRenglon().replace(".0", "")));
				psInsertDetalle.setString(3, rd.getnSIAFF());
				psInsertDetalle.setString(4, rd.getSecCLC());
				psInsertDetalle.setString(5, rd.getcEvento());
				psInsertDetalle.setString(6, rd.getEP());
				psInsertDetalle.setDouble(7, rd.getmImporteCLC());
				psInsertDetalle.setDouble(8, rd.getmImporteCLC()*-1);
				psInsertDetalle.setInt(9, rd.getMes());
				psInsertDetalle.setString(10, rd.getcCentroContable());
				psInsertDetalle.setString(11, rd.getcPartida());
				psInsertDetalle.setString(12, rd.getCxp());
				psInsertDetalle.setString(13, rd.getRfc());
				psInsertDetalle.setString(14, rd.getRenglonPagado());
				psInsertDetalle.setString(15, rd.getFolioDependenciaSicop());
				psInsertDetalle.setString(16, rd.getAlm());
				psInsertDetalle.setString(17, rd.getTipoConcepto());
				psInsertDetalle.setString(18, rd.getTipoMovimiento());
				psInsertDetalle.setString(19, rd.getCLCSAI());
				psInsertDetalle.setString(20, rd.getObgt() );
				psInsertDetalle.setString(21, rd.getCtab() );
				psInsertDetalle.setInt(22, 0 );	
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
	
	public static ReintegroDetalleMil datosSICOPMil(Connection conn, String fSIAFF, String EP, String movimiento, String concepto) throws SQLException{
		ResultSet res = null;
		PreparedStatement pstm = null;
		ReintegroDetalleMil mil = new ReintegroDetalleMil();
		mil.setSecCLC("-1");
		mil.setFolioDependenciaSicop("-1");
		try {
			pstm = conn.prepareStatement("SELECT * FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
			pstm.setString(1, fSIAFF);
			pstm.setString(2, EP.substring(0,55));
			
			res = pstm.executeQuery();
			if(res.next()){
				mil.setSecCLC(res.getString("SEC"));
				mil.setFolioDependenciaSicop(res.getString("NCLC_43"));
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

	public static String getALM(Connection conn, String caNoContrarrecibo, String EP, int cMes, String cxp) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval="";
		try {
			//pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			//pstmnt = conn.prepareStatement("SELECT pd.ALM FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock),tNOMINACLCEncabezado ne with(nolock), tNOMINACLCDetalle nd with(nolock) WHERE pe.caNoContrarrecibo=? and ne.caNoContrarreciboCLC=? and pd.EP=? and nd.EP=? and pd.nMes=? and nd.cMes=?  and nd.caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and nd.nDocRenglonCLC=pd.nDocRenglon and nd.nFolioNOMINACLC=ne.nFolioNOMINACLC");
			pstmnt = conn.prepareStatement(" SELECT ALM FROM vw_PagadoNominaMil WITH (NOLOCK) WHERE caNoContrarreciboPagado = ? AND epPagado = ? AND cMesPagado = ? AND canocontrarrecibo = ? ");
			pstmnt.setString(1, caNoContrarrecibo);
		    //pstmnt.setString(2, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			//pstmnt.setString(4, EP);
			pstmnt.setInt(3, cMes);
			//pstmnt.setInt(6, cMes);
			pstmnt.setString(4, cxp);
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
	
	public static boolean getTipoCLC(Connection conn, int folioRein) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		boolean retval=false;
		try {
			//pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			pstmnt = conn.prepareStatement("select TIPO_CLC from CLC_SIAFF_ENC with(nolock), tReintegroDetalleMil with(nolock) where FOLIO_CLC=tReintegroDetalleMil.noCLC and tReintegroDetalleMil.nFolioReintegro=?");  
		    pstmnt.setInt(1, folioRein);
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
	
	public static String getCCNormal(Connection conn, String caNoContrarrecibo, String EP, int cMes, String cxp) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval="";
		try {
			//pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
			//pstmnt = conn.prepareStatement("SELECT pd.cCentroContable FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock),tNOMINACLCEncabezado ne with(nolock), tNOMINACLCDetalle nd with(nolock) WHERE pe.caNoContrarrecibo=? and ne.caNoContrarreciboCLC=? and pd.EP=? and nd.EP=? and pd.nMes=? and nd.cMes=?  and nd.caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and nd.nDocRenglonCLC=pd.nDocRenglon and nd.nFolioNOMINACLC=ne.nFolioNOMINACLC");
			pstmnt = conn.prepareStatement("SELECT cCentroContablePagado AS cCentroContable FROM vw_PagadoNominaMil WITH (NOLOCK) WHERE caNoContrarreciboPagado = ? AND epPagado = ? AND cMesPagado = ? AND canocontrarrecibo = ? ");
			pstmnt.setString(1, caNoContrarrecibo);
		    //pstmnt.setString(2, caNoContrarrecibo);
			pstmnt.setString(2, EP);
			//pstmnt.setString(4, EP);
			pstmnt.setInt(3, cMes);
			//pstmnt.setInt(6, cMes);
			pstmnt.setString(4, cxp);
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
	
	public static boolean actualizaFechaAplicacionMil(Connection conn, int folio,String fAplicacion) throws SQLException, ParseException{
		PreparedStatement pstmntUp = null;
		boolean retval =false;
		try {
		    pstmntUp = conn.prepareStatement("UPDATE tReintegroEncabezadoMil SET fAplicacion=? WHERE nFolioReintegroMil=?");
		    pstmntUp.setString(1, fAplicacion);
			pstmntUp.setInt(2, folio);
			retval = pstmntUp.execute();
		} catch(SQLException s){
		    s.printStackTrace();
		}finally {
			if (pstmntUp != null)
				pstmntUp.close();
			pstmntUp = null;
		}
		return retval;
	}
	
	public static String getCTAB(Connection conn, String caNoContrarrecibo, String ep) throws SQLException{
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval ="";
		try {
			//String queryRFC="SELECT top(1) ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.EP=?";
			String queryRFC="SELECT TOP (1) pd.ctab " 
							+ " FROM dbo.tNOMINACLCEncabezado AS nclc WITH (NOLOCK) " 
							+ " JOIN dbo.tNOMINACLCDetalle AS n WITH (NOLOCK) ON nclc.nFolioNOMINACLC = N.nFolioNOMINACLC "
							+ " JOIN dbo.tPagadoEncabezado AS p WITH (NOLOCK) ON nclc.caNoContrarreciboCLC = P.caNoContrarrecibo "
							+ " JOIN dbo.tPagadoDetalle AS pd WITH (NOLOCK) ON p.nFolioPAGO = pd.nFolioPAGO " 							
							+ " WHERE n.caNoContrarrecibo = ? AND pd.EP = ? ";
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

	public static String validaMes(Connection conn, Caso c, int folio, String fAcredit) {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String retval="";
		int mes = Integer.parseInt(fAcredit.substring(3,5));;
		
		try {
			
			pstmnt = conn.prepareStatement("SELECT CASE WHEN MONTH(GETDATE())= ? THEN 'S' ELSE mesAbierto END AS mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cCentroContable = (SELECT TOP 1 cCentroContable FROM dbo.tReintegroDetalleMil WHERE nFolioReintegroMil = ?) AND nMes = ?");
			pstmnt.setInt(1, mes);
			pstmnt.setInt(2, folio);
			pstmnt.setInt(3, mes);
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

	public static String eventoReintegroMil(Connection conn, String caNoContrarrecibo, String EP) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String evento = "";
		
		try{
			
			ps = conn.prepareStatement("SELECT  TOP 1 'RC_IN_' + SUBSTRING(cEvento, 3, LEN(cEvento)) "
									   +"	FROM    dbo.tPagadoDetalle "
									   +"	WHERE   nFolioPagado = ( SELECT nFolioPagado "
									   +"	                         FROM   dbo.tPagadoEncabezado "
									   +"	                         WHERE  caNoContrarrecibo = ? )"										                       
									   +"	    AND EP = ? "
									   +"	    AND mPasivoDiferido = 0");
			ps.setString(1, caNoContrarrecibo);
			ps.setString(2, EP);
			rs = ps.executeQuery();
			if(rs.next()){
				evento = rs.getString(1);
			}
			
		} catch(SQLException s){
			s.printStackTrace();
		}finally{
			if (ps != null)
				ps = null;
		}
		
		return evento;
	}

	public static void insertaReintegroPasivo( Connection conn, int folio )  throws Exception {
		PreparedStatement pstmnt = null;		
		
		try {
				pstmnt = conn.prepareStatement("INSERT INTO tReintegroDetalleMil (nFolioReintegroMil,nDocRenglon,noCLC,secCLC,EP,cMes,cEvento,mImporte,mImporteNegativo,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,tipoConcepto,tipoMovimiento,cxpnomina,OBGT,CTAB,mPasivoDiferido) \r\n" + 		
												"SELECT nFolioReintegroMil\r\n" + 
												"	, nDocRenglon + (SELECT COUNT(*) FROM tReintegroDetalleMil WHERE nFolioReintegroMil = ?)\r\n" + 
												"	, noCLC\r\n" + 
												"	, secCLC\r\n" + 
												"	, EP\r\n" + 
												"	, cMes\r\n" + 
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
												"	, tipoConcepto\r\n" + 
												"	, tipoMovimiento\r\n" + 
												"	, cxpnomina\r\n" + 
												"	, OBGT\r\n" + 
												"	, CTAB\r\n" + 
												"	, mImporte\r\n" + 
												"from tReintegroDetalleMil \r\n" + 
												"where nFolioReintegroMil = ?\r\n" + 
												"");
				pstmnt.setInt(1, folio);
				pstmnt.setInt(2, folio);
				pstmnt.execute();
				
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
				CloseObject.closeObject(pstmnt, false);
			}
	}
	
	public static void updateReintegroMilFA( Connection conn, int folio ) throws Exception {
		PreparedStatement pst = null, pst2 = null;		
		String poliza = "UPDATE tReintegroAutEncabezadoMil SET cTipoPoliza = 'DI' WHERE nFolioReintegroMilaut = ?";
		String evento = "UPDATE tReintegroAutDetalleMil SET cEvento = REPLACE(cEvento,'_EG_','_FA_') WHERE nFolioReintegroMilaut = ?";
		
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
	
}
