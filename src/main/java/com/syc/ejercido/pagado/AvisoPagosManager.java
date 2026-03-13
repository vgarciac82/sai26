package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.cfdi.utils.CloseObject;

public class AvisoPagosManager {

	private static final Logger	log	= Logger.getLogger(AvisoPagosManager.class);

	public static List<AvisoPago> selectPagosNotificacion(Connection conn) throws Exception{
				
		String query =   "SELECT ctipopago, "
						+"       nfoliopago, "
						+"       CONVERT(VARCHAR(24), faplicacion, 103) AS fPagoSAI, "
						+"       canocontrarrecibo "
						+"FROM   tpagadoencabezado WITH( nolock) "
						+"WHERE  cdocumentohaplicado = 'S' "
						+"       AND ccorreoenviado = 'N' ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<AvisoPago> avisos = new ArrayList<AvisoPago>();
		
		try{
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next()){
				avisos.add(AvisoPago.instance(rs));
			}
				
			return avisos;
		}finally{
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}
	}

}
