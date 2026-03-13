package com.syc.utils.mail.db;
/*******************************************************/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.syc.utils.mail.ParametrosCorreo;

public class CargaParametrosBD {
	
	public CargaParametrosBD() {
		super();
	}
	public ParametrosCorreo getParameterDB(Connection conn, String nombreProceso) throws SQLException {
		ParametrosCorreo ParamMail = new ParametrosCorreo();
		PreparedStatement pstmnt = null;
		PreparedStatement pstmntIns = null;
		ResultSet rs = null;
		String ParamQuery="select * from tParamMensajeProceso PP inner join tMensajeProceso MP on " +
				" MP.idMensajeProceso=PP.tMensajeProceso_idMensajeProceso " +
				" inner join tAmbiente A on A.idAmbiente= PP.tAmbiente_idAmbiente	" +
				" where A.usado=0 and MP.descripcionMensajeProceos='"+nombreProceso+"'";
		//poner where A.usado=1 para produccion y =0 para preprod 
		try{
		pstmnt = conn.prepareStatement(ParamQuery);
		rs = pstmnt.executeQuery();
		if (rs.next()) {
			ParamMail.setIdAmbiente(rs.getInt("tAmbiente_idAmbiente"));
			ParamMail.setIdProceso(rs.getInt("tMensajeProceso_idMensajeProceso"));
			ParamMail.setAmbiente(rs.getString("Descripcion"));
			ParamMail.setMetodoProceso(rs.getString("MetodoProceso"));
			ParamMail.setNombreProceso(rs.getString("DescripcionMensajeProceos"));
			ParamMail.setPersonaDe(rs.getString("DE"));
			ParamMail.setPersonaPara(rs.getString("PARA"));
			ParamMail.setPersonaCC(rs.getString("CC"));
			ParamMail.setPlantilla(rs.getString("PlantillaMensaje"));

		}
		}catch (SQLException sqle){
			System.out.println("Error al consultar parametros   ----->"+sqle.getMessage());
			sqle.getStackTrace();
				
		}finally {
			
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
			if (pstmntIns != null)
				pstmntIns.close();
			pstmntIns = null;
		}
		return ParamMail;
	}
	

}
