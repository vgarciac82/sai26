package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class LayoutGeneralManager {

	public LayoutGeneralManager() {
		super();
	}
	
	
	public static ArrayList<String> SaldosCompromisoCapituloMil(Connection conn, String compromiso, String estatus)throws Exception{
		
		ArrayList<String> arrDoc = new ArrayList<String>();
		PreparedStatement pstm = null, pstmTotal = null;
		ResultSet rs = null, rsT = null;
		
		String sqlDet = "", detalle = "", sqlTotal = "", total = "" ;
		
		String titulo = "Numero, Compromiso, Folio Compromiso Nomina, Estructura Programatica, Mes, Importe, Estatus";
		titulo += "\r\n";
		arrDoc.add(titulo);
		
		try{
			
			if(compromiso.equals("")){
				compromiso = " WHERE retencionTe > 0.00 ";
			
			}else{
				compromiso = " WHERE caNoCompromiso = '"+compromiso+"' AND  retencionTe > 0.00";
			}
						
			sqlDet = "SELECT renglon, caNoCompromiso, nFolioCompromisoNomina, EPtxt, cMestxt, retencionTe, cDocumentoHaplicado FROM v_CompromisoDevCLC with(nolock) "+compromiso+ " " +estatus+ " ORDER BY renglon ASC ";
			
			pstm = conn.prepareStatement(sqlDet);
			//pstm.setString(1, compromiso);
			rs = pstm.executeQuery();
			
			while(rs.next()){
				
				detalle = rs.getString("renglon")+","+rs.getString("caNoCompromiso")+","+rs.getString("nFolioCompromisoNomina")+","+rs.getString("EPtxt")+","+rs.getString("cMestxt")+","+rs.getString("retencionTe")+","+rs.getString("cDocumentoHaplicado");
				detalle += "\r\n";
				arrDoc.add(detalle.toString());
				
			}
			
			sqlTotal = "SELECT SUM(retencionTe) AS retencion FROM V_COMPROMISODEVCLC WITH(NOLOCK) "+compromiso+ " " +estatus;
			pstmTotal = conn.prepareStatement(sqlTotal);
			//pstmTotal.setString(1, compromiso);
			rsT = pstmTotal.executeQuery();
			
			if(rsT.next()){
				
				total = ",,,,Total:," +rsT.getString("retencion");
				arrDoc.add(total);
			}
	
		}catch(Exception e){
			
			if(pstm != null){ pstm.close(); }
			if(rs != null){ rs.close(); }
			if(pstmTotal != null){ pstmTotal.close(); }
			if(rsT != null){ rsT.close(); }
			
		}finally{
			
			pstm = null;
			rs = null;
			pstmTotal = null;
			rsT = null;
		}
			
		return arrDoc; 
	}
	
	public static ArrayList<String> SaldosCapituloMil(Connection conn, String caNoContrarrecibo, String conceptoFiltro, String movimientoFiltro, String estatusTxt)throws Exception{
		
		ArrayList<String> arrDoc = new ArrayList<String>();
		PreparedStatement pstm = null, pstmTotal = null;
		ResultSet rs = null, rsT = null;
		String detalle = ""; 
		double total = 0.00;
		
		String titulo = "Cuenta Por Pagar, Estructura Programatica, Mes, Movimiento, Concepto, Importe, Compromiso, Estatus";
		titulo += "\r\n";
		arrDoc.add(titulo);
	
		try{
			
			if(!caNoContrarrecibo.equals("")){
				
				caNoContrarrecibo = " AND caNoContrarreciboTxt = '"+caNoContrarrecibo+"' ";
			}
			
			String sql = "SELECT caNoContrarreciboTxt, EPTxt, cMesTxt, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, importeNeto, caNoCompromisoTxt, estatus FROM v_NominaCapituloMilDev WITH(NOLOCK) WHERE 1 = 1 " +caNoContrarrecibo + conceptoFiltro + movimientoFiltro + estatusTxt;
			//System.out.println(sql);
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			
			while(rs.next()){
				
				detalle = rs.getString("caNoContrarreciboTxt")+","+rs.getString("EPTxt")+","+rs.getString("cMesTxt")+","+rs.getString("ID_TIPO_MOVIMIENTO")+","+rs.getString("ID_TIPO_CONCEPTO")+","+rs.getString("importeNeto")+","+rs.getString("caNoCompromisoTxt")+","+rs.getString("estatus");
				detalle += "\r\n";
				arrDoc.add(detalle.toString());
				
				total = total + Double.parseDouble(rs.getString("importeNeto"));
				
			}
			
			NumberFormat formatter = new DecimalFormat("###.##");
			double totalGeneral = Double.parseDouble(formatter.format(total));
			
			String totalGen = ",,,,Total:," +total;
			arrDoc.add(totalGen);
			
			
		}catch(Exception e){
			
			if(pstm != null ){ pstm.close(); }
			if(rs != null ) { rs.close(); }
			
		}finally{
			
			pstm = null;
			rs = null;
			
		}
		return arrDoc; 
	}
	
	public static ArrayList<String> ConsultaIngresosEgreso(Connection conn, String sWhereCla)throws Exception{
		
		ArrayList<String> arrDoc = new ArrayList<String>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String detalle = ""; 
		
		String titulo = "Folio, Banco, Número de Cuenta, Fecha, Medio de Pago, Concepto, Movimiento, Importe"; 
		titulo += "\r\n";
		arrDoc.add(titulo);
	
		try{
			
			String sql = "SELECT folioSai, banco, cuentaBancaria, dFecha, cMedioPago, cConcepto, TipoMovimiento, Convert(varchar(20), mImporte) as mImporteSinF " + 
						 "FROM v_RdbCargaArchivoRDB tbl WITH(NOLOCK) WHERE " + sWhereCla;
			//System.out.println(sql);
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			
			while(rs.next()){
				
				detalle = rs.getString("folioSai")+","+rs.getString("banco")+","+rs.getString("cuentaBancaria")+","+rs.getString("dFecha")+","+rs.getString("cMedioPago")+","+rs.getString("cConcepto")+","+rs.getString("TipoMovimiento")+", "+rs.getString("mImporteSinF") ;
				detalle += "\r\n";
				arrDoc.add(detalle.toString());
								
			}
						
			
		}catch(Exception e){
			
			if(pstm != null ){ pstm.close(); }
			if(rs != null ) { rs.close(); }
			
		}finally{
			
			pstm = null;
			rs = null;
			
		}
		return arrDoc; 
	}
}
