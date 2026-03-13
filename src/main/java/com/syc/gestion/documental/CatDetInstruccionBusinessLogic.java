package com.syc.gestion.documental;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import org.apache.log4j.Logger;

public class CatDetInstruccionBusinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger(CatDetInstruccionBusinessLogic.class);

	public CatDetInstruccionBusinessLogic(String jniName) {

		super.init(jniName);
	}

	public List consultaCatDetInstruccion() {

		Connection conn = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		List l = new ArrayList();

		try {
			conn   = getConnection();
			pstmnt = conn.prepareStatement("SELECT * FROM cat_det_instruccion ORDER BY di_descripcion");
			rs     = pstmnt.executeQuery();                               
                                                                                  
			while (rs.next()) {                                       
				CatDetInstruccion di = new CatDetInstruccion();                              
                                                                                  
				di.setId_det_instruccion(rs.getInt("id_det_instruccion"));                    
				di.setDi_descripcion(rs.getString("di_descripcion"));         
                                                                                  
				l.add(di);                                         
			}                                                         
		} catch (SQLException exc) {
			log.error("Obteniendo lista de detalle de instruccion", exc);
		} finally {
			try {
				if (rs != null)                                           
					rs.close();                                       
	                                                                                  
				if (pstmnt != null)                                       
					pstmnt.close();                                   	                                                                                  

				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
			rs = null;                                                
			pstmnt = null;                                            
		}

		return l;
	}

}


