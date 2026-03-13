package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class CargaRefasManager {
	
	public static boolean insertaDataCargaRefas(Connection conn, ArrayList<ArrayList<String>> lst,String ejercicio )  throws SQLException {
		PreparedStatement pstmnt = null;
		try{
			for (int i = 1; i < lst.size(); i++) {
				ArrayList<String> arrayLst = (ArrayList<String>) lst.get(i);
				ejercicio = quitarEspacios(ejercicio);
				pstmnt = conn.prepareStatement("insert into [CTRL_DOC].[dbo].[t_Refas"+ejercicio+"] (CLC,[CLAVE_SIAFF],[IMPORTE_ORIGINAL],[REMANENTE_ORIGINAL],[REINTEGRO_ORIGINAL],[REFAS_ORIGINAL],[REMANENTE_SAI],[REMANENTE_TEMPORAL]) values (?,?,?,?,?,?,?,?)");
				pstmnt.setInt(1, Integer.parseInt(quitarEspacios(arrayLst.get(0)).replace('.','d').split("d")[0]));
				pstmnt.setString(2, quitarEspacios(arrayLst.get(1)));
				pstmnt.setDouble(3, validaFloat(arrayLst.get(2)));
				pstmnt.setDouble(4, validaFloat(arrayLst.get(3)));
				pstmnt.setDouble(5, validaFloat(arrayLst.get(4)));
				pstmnt.setDouble(6, validaFloat(arrayLst.get(5)));
				pstmnt.setDouble(7, validaFloat(arrayLst.get(6)));
				pstmnt.setDouble(8, validaFloat(arrayLst.get(6)));
	
			    pstmnt.execute();
			}	
			
		} finally {

			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}
		return true;
	}
	
	public static boolean insertaCargaRefas(Connection conn, String u_logion,String u_nombre,String ejercicio )  throws SQLException {
		PreparedStatement pstmnt = null;
		try{
			ejercicio = quitarEspacios(ejercicio);
			pstmnt = conn.prepareStatement("INSERT INTO [CTRL_DOC].[dbo].[t_RegistroCarga] ([U_LOGIN],[U_NOMBRE],[EJERCICIO]) VALUES (?,?,?)");
			pstmnt.setString(1, u_logion);
			pstmnt.setString(2, u_nombre);
			pstmnt.setString(3, ejercicio);

		    pstmnt.execute();
			
		} finally {

			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return true;
		
	}
	
	public static double validaFloat(String cValor){
		double resp;
		try {
			resp = Double.parseDouble(quitarEspacios(cValor)) ;
		} catch (Exception e) {
			resp = 0;
		}
		return  resp;
		
	}
	public static String quitarEspacios(String cValor) {
		cValor = quitarCaracteresUTF8(cValor);
		cValor = cValor.replaceAll("\\s", "");
		cValor = cValor.replaceAll("\\t", "");
		cValor = cValor.replaceAll("\\n", "");
		cValor = cValor.replaceAll(" ", "");
		cValor = cValor.replaceAll(",", "");
		cValor = cValor.trim();
		return cValor;
	}

	private static String quitarCaracteresUTF8(String cValor) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cValor.length(); i++) {
			if(cValor.charAt(i)<128){
				sb.append(cValor.charAt(i));
			}
		}
		return sb.toString();
	}

	public static void insertaCargaEjercicio(Connection conn, String ejercicio_fiscal) throws SQLException {
		PreparedStatement pstmnt = null;
		try{
			ejercicio_fiscal = quitarEspacios(ejercicio_fiscal);
			pstmnt = conn.prepareStatement("UPDATE [CTRL_DOC].[dbo].[t_EjerciciosCargados] SET [CARGADO] = 'S' WHERE [EJERCICIO] ="+ejercicio_fiscal+"");
		    pstmnt.execute();
			
		} finally {

			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		
	}

	public static ArrayList<String> getEjercicios(Connection conn,String cargado) throws SQLException {
		PreparedStatement pstmnt = null;
		ArrayList<String> respuesta = new ArrayList<String>();
		try{
			pstmnt = conn.prepareStatement("SELECT [EJERCICIO]  FROM [CTRL_DOC].[dbo].[t_EjerciciosCargados] WHERE [CARGADO] = '"+cargado+"'");
		    
		    ResultSet rs = pstmnt.executeQuery();;
		    while (rs.next()) {
		    	respuesta.add(rs.getString(1));
		    }
		} finally {

			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}
		
		return respuesta;
	}

}
