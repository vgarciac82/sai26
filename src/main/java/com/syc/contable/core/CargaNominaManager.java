package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CargaNominaManager {

	public CargaNominaManager() {
	}
	
	public static String buscaClaveCNA(Connection conn,String EP)throws Exception{
		String nClaveCNA = "";
		PreparedStatement pstmnt = null;
		ResultSet rs = null;	
		String Sql = "SELECT nClaveCNA FROM tCatalogoEP WHERE EP = '" + EP  + "'";
		
		pstmnt = conn.prepareStatement(Sql);
		rs = pstmnt.executeQuery();
		while (rs.next()){
			nClaveCNA = (rs.getString(1) != null)? rs.getString(1) : "";
			System.out.println("clave= " +nClaveCNA);
		}
		
		if(rs != null){
			
			rs.close();
		} 
		
		if(pstmnt != null){
			pstmnt.close();
		}
		
		return nClaveCNA;
	}
		
	public static boolean insertaLineaNomina(Connection conn, Integer ID_Caso, String EP, String claveInterna, String concepto, String movimiento, Double saldo, String claveCNA) throws SQLException{													
		PreparedStatement pstmnt = null;
		boolean insertReg;
		String queryInsert = "INSERT INTO tNOMINACargaArchivo(" +
					"                                             nFolioNOMINA,ClaveSIAFF,ClaveInterna," +
					"                                             ID_TIPO_CONCEPTO,ID_TIPO_MOVIMIENTO,mSaldo, nClaveCNA)" +
					"         VALUES(" +
					""                 + "'" + ID_Caso + "','" + EP + "','" + claveInterna + "','" + concepto + "','" + movimiento + "'," + saldo + ",'" + claveCNA + "')";
		System.out.println(queryInsert);
		try{
			pstmnt = conn.prepareStatement(queryInsert);
			int reg = pstmnt.executeUpdate();
			if(reg == 1){
				insertReg = true;
			} else{
				insertReg = false;
			}
			conn.commit();
		} finally {
			if (pstmnt != null){
				pstmnt.close();
			}

			pstmnt = null;
		}
		return insertReg;
	}	
	
	public static int borraRegistrosNomina(Connection conn, Integer ID_Caso, String listaEPs) throws SQLException{
		PreparedStatement pstmntDel = null;
		int reg = 0;
		String queryDelete = "DELETE FROM tNOMINACargaArchivo WHERE nFolioNOMINA = " + ID_Caso + " AND ClaveSIAFF in ('" + listaEPs + "')";
		System.out.println(queryDelete);
		try{
			pstmntDel = conn.prepareStatement(queryDelete);
			reg = pstmntDel.executeUpdate();
			conn.commit();
		} finally {
			if (pstmntDel != null){
				pstmntDel.close();
			}
			pstmntDel = null;
		}
		return reg;
	}
}