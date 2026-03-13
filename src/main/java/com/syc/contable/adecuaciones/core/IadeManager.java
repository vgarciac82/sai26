package com.syc.contable.adecuaciones.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.syc.cfdi.utils.CloseObject;
import com.syc.gestion.util.Util;

/**
 * 
 * @author Propietario
 *
 */
public class IadeManager {
	/**
	 * 
	 * @param conn
	 * @param nFolioConsolidacion
	 * @return
	 * @throws Exception
	 */
	public static List<Fap01> cargaFAP01(Connection conn, int nFolioConsolidacion) throws Exception {
		String query = "SELECT nfolioconsolidacion ,"
						+"        ep ,"
						+"        movimiento ,"
						+"        enero ,"
						+"        febrero ,"
						+"        marzo ,"
						+"        abril ,"
						+"        mayo ,"
						+"        junio ,"
						+"        julio ,"
						+"        agosto ,"
						+"        septiembre ,"
						+"        octubre ,"
						+"        noviembre ,"
						+"        diciembre ,"
						+"        anual ,"
						+"        ejercicio ,"
						+"        ramo ,"
						+"        unidad_responsable ,"
						+"        gf ,"
						+"        f ,"
						+"        sf ,"
						+"        PG ,"
						+"        AI ,"
						+"        PP ,"
						+"        OG ,"
						+"        tg ,"
						+"        ff ,"
						+"        ef ,"
						+"        cartera ,"
						+"        AE," 
						+"        nfolioconsolidacion"
						+" FROM  vFAP01 WITH(NOLOCK) "
						+"WHERE nfolioconsolidacion = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Fap01> result = new ArrayList<Fap01>();

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, nFolioConsolidacion);
			
			rs = ps.executeQuery();
			
			while( rs.next() ){
				Fap01 tmp = new Fap01();
				
				tmp.setAE( rs.getString("AE") );
				tmp.setAI(rs.getString("AI") );
				tmp.setCartera(rs.getString("cartera"));
				tmp.setEf(rs.getString("ef"));
				tmp.setEjercicio(rs.getString("ejercicio"));
				tmp.setEp(rs.getString( "ep") );
				tmp.setF(rs.getString("f") );
				tmp.setFf(rs.getString("ff"));
				tmp.setGf(rs.getString("gf"));
				tmp.setMovimiento(rs.getString("movimiento") );
				tmp.setNfolioconsolidacion(rs.getInt("nfolioconsolidacion"));
				tmp.setOG(rs.getString("OG"));
				tmp.setPG(rs.getString("PG"));
				tmp.setPP(rs.getString("PP"));
				tmp.setRamo(rs.getString("ramo"));
				tmp.setSf(rs.getString("sf"));
				tmp.setTg(rs.getString("tg"));
				tmp.setUnidadResponsable(rs.getString("unidad_responsable"));
				
				double montos[] = new double[13];
				
				for( int i = 0; i < Util.NOMBRE_MESES_ADECUACIONES.length; i++){
					montos[i]= rs.getDouble(Util.NOMBRE_MESES_ADECUACIONES[i]);
				}
				tmp.setMontos(montos);
				
				result.add(tmp);
			}
			
			return result;
			
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}

	}

}
