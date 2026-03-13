package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class ExtraeEdoCtaManager {

	public ExtraeEdoCtaManager() {
		super();
	}

	public static ArrayList<String> BuscaCompromisos(Connection conn,String szTemp)throws Exception{
		ArrayList<String> arrListaComp = new ArrayList<String>();
		PreparedStatement pstmntH = null;
		ResultSet rs = null;
		
		szTemp = (szTemp != "")? " where " + szTemp: "" ;
		String Sql = " select * from vExportaEdoCta " + szTemp;  
				

		pstmntH = conn.prepareStatement(Sql);
		//System.out.println(Sql);

		//pstmntH.setString(1, listaIds);
		rs = pstmntH.executeQuery();

		while (rs.next()){
			String var_01 = (rs.getString(1)!= null)? rs.getString(1).trim().trim(): "";
			String var_02 = (rs.getString(2)!= null)? rs.getString(2).trim().trim(): "";
			String var_03 = (rs.getString(3)!= null)? rs.getString(3).trim().trim(): "";
			String var_04 = (rs.getString(4)!= null)? rs.getString(4).trim().trim(): "";
			String var_05 = (rs.getString(5)!= null)? rs.getString(5).trim().trim(): "";
			String var_06 = (rs.getString(6)!= null)? rs.getString(6).trim().trim(): "";
			String var_07 = (rs.getString(7)!= null)? rs.getString(7).trim().trim(): "";
			String var_08 = (rs.getString(8)!= null)? rs.getString(8).trim().trim(): "";
			String var_09 = (rs.getString(9)!= null)? rs.getString(9).trim().trim(): "";
			String var_10 = (rs.getString(10)!= null)? rs.getString(10).trim().trim(): "";
			String var_11 = (rs.getString(11)!= null)? rs.getString(11).trim().trim(): "";
			String var_12 = (rs.getString(12)!= null)? rs.getString(12).trim().trim(): "";
			String var_13 = (rs.getString(13)!= null)? rs.getString(13).trim().trim(): "";
			String var_14 = (rs.getString(14)!= null)? rs.getString(14).trim().trim(): "";
			String var_15 = (rs.getString(15)!= null)? rs.getString(15).trim().trim(): "";
			String var_16 = (rs.getString(16)!= null)? rs.getString(16).trim().trim(): "";
			String var_17 = (rs.getString(17)!= null)? rs.getString(17).trim().trim(): "";
			String var_18 = (rs.getString(18)!= null)? rs.getString(18).trim().trim(): "";
			
			String encabezado = var_01 + "|" + var_02 + "|" + var_03 + "|" + var_04 + "|" +	var_05 + "|" + var_06 + "|" + var_07 + "|" + var_08 + "|" + 
						var_09 + "|" + var_10 + "|" + var_11 + "|" + var_12 + "|" + var_13 + "|" + var_14 + "|" + var_15 + "|" + var_16 + "|" + var_17 + "|" + var_18;
			
			encabezado = encabezado + "\r\n";
			arrListaComp.add(encabezado);

		}
		if(rs != null){
			rs.close();
		}
		if(pstmntH != null){
			pstmntH.close();
		}
		return arrListaComp;
	}


}
