package com.syc.alertas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class AlertaManager {
	
	private static final Logger	log	= Logger.getLogger(AlertaManager.class);
	
	public static Map<String, List<Integer>> procesaVencidos(DataSourceManager dsManager) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
				conn = dsManager.getConnection();
				
				String query = "" + "SELECT ID_CASO, " 
								 + "       co.ID_TC, " 
								 + "       o.ID_OPER, " 
								 + "       Round (Cast(( Datediff(hh, co.CO_FECHA_INI, Getdate()) * 100 ) / " 
								 + "            o.O_TIEMPO_LIMITE AS " 
								 + "            decimal),0) AS PORC "
								 + "FROM   dbo.CG_CASO_OPERACION co WITH (nolock) " 
								 + "       INNER JOIN dbo.CG_OPERACION o WITH (nolock) " 
								 + "               ON co.ID_TC = o.ID_TC " 
								 + "                  AND co.ID_OPER = o.ID_OPER " 
								 + "WHERE  o.O_TIEMPO_LIMITE > 0 "
								 + "       AND Round (Cast(( Datediff(hh, co.CO_FECHA_INI, Getdate()) * 100 ) / " 
								 + "                o.O_TIEMPO_LIMITE AS " 
								 + "                    decimal),0) > 80"
								 + "ORDER BY ID_CASO	";

				ps = conn.prepareStatement(query);
				rs = ps.executeQuery();
				log.info(ps);
				Map<String, List<Integer>> casosRetrazados = new HashMap<String, List<Integer>>();
				
				while (rs.next()) {
					List<Integer> dataList = new ArrayList<Integer>();
					dataList.add(rs.getInt("ID_CASO"));
					dataList.add(rs.getInt("ID_TC"));
					dataList.add(rs.getInt("ID_OPER"));
					dataList.add(rs.getInt("PORC"));
					
					casosRetrazados.put(String.valueOf(rs.getInt("ID_CASO")), dataList );
					
				}
				
				return casosRetrazados;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
			CloseObject.closeObject(conn);
		}
		
	}
//	log.info("Procesando el caso: " + id_caso + " ID_TC: " + id_tc + " ID_OPER: " + id_oper + " en Porcentaje de vencido: " + porc);
	//procesoAlerta(conn, id_caso, id_tc, id_oper, porc);
	public static void procesoAlerta(Connection conn, int id_caso, int id_tc,int id_oper, int porc) throws Exception {
		log.info("Obteniendo Interface...");
		TipoCasoInterface tci = instanciaInterface(conn, id_tc);
		String folio=extraeFolio(conn,id_caso,id_tc);
		log.info("Extrayendo Folio: "+folio);
		tci.onVenceCaso(conn,folio, id_caso, id_tc, id_oper, porc);
	}
	
	public static void procesoAlerta(DataSourceManager ds, int id_caso, int id_tc,int id_oper, int porc) throws Exception {
		
		Connection conn = null;
		try{
			
		}finally{
			CloseObject.closeObject(conn);
		}
		
	}
	
	private static String extraeFolio(Connection conn, int id_caso, int id_tc) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String folio=null;
		String query = "SELECT C_FOLIO FROM CG_CASO  WITH (nolock) WHERE ID_CASO=? AND ID_TC=?";
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_caso);
			ps.setInt(2, id_tc);

			rs = ps.executeQuery();
			log.info(ps);

			if (rs.next()) {
				folio = rs.getString("C_FOLIO");
			}

		} catch (Exception e) {
			log.warn(e, e);
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
		return folio;
	}
	
	public static boolean realizarProceso(Connection conn) {
		boolean activo = false;
		try {
			ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
			String valor = cabl.getSystemSetting("ALERTAS CASOS VENCIDOS");

			if (valor.equalsIgnoreCase("S"))
				activo = true;
		} catch (Exception e) {
			log.warn(e, e);
		}
		log.info("EL Proceso de Alertas en Casos Vencidos se encuentra activo: " + activo);
		return activo;
	}
	
	private static TipoCasoInterface instanciaInterface(Connection conn, int id_tc) throws Exception{
		TipoCasoInterface tci = null;
		ClassLoader cl = AlertaManager.class.getClassLoader();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "SELECT TC_INTERFACE FROM CG_TIPO_CASO WITH(NOLOCK) WHERE ID_TC = ?";
		String nombreTCI = null;
		
		try{
			
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_tc);
			
			rs = ps.executeQuery();
			log.info(ps);
			if(rs.next()){
				nombreTCI = rs.getString("TC_INTERFACE");
			}
			
			if( !StringUtils.isEmpty(nombreTCI) ){
				Class<?> clase = cl.loadClass(nombreTCI);
				tci = (TipoCasoInterface) clase.newInstance();
			}
			return tci;
		}finally{
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}

	}
}
