package com.syc.adquisiciones.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ComparativaManager {
	public static List callComparativa(Connection conn, String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo, int Linea, String orden) throws SQLException{
		List comparativaList = new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		CallableStatement cs1 = null;
		try {
	

			pstmnt=conn.prepareStatement("SELECT * FROM dbo.fn_mProcedimientoReporteComparativa(?,?,?,?,?) "+orden);
		
			pstmnt.setString(1, cEjercicio);
			pstmnt.setString(2, cIdTipoProcedimiento);
			pstmnt.setString(3, cIdUnidadEjecutora);
			pstmnt.setInt(4, nIdConsecutivo);
			pstmnt.setInt(5, Linea);
			
			rs=pstmnt.executeQuery();
			ProcedimientoComparativa rComparativa;
			while (rs.next()) {
				 rComparativa =new ProcedimientoComparativa();
			        rComparativa.setDescripcion(rs.getString("cDescripcion"));
				    rComparativa.setIdRFC(rs.getString("cIdRFC"));
				    rComparativa.setcrazonSocial(rs.getString("razonSocial"));
					rComparativa.setConsecutivo (rs.getInt("cCantidad"));
					rComparativa.setmontoMinimoTexto(rs.getString("mMontoMinimoTexto"));
					rComparativa.setmontoMinimo(rs.getFloat("mMontoMinimo"));
					rComparativa.setEvaluacionTecnica(rs.getString("evaluacionTec"));
					rComparativa.setEvaluacion(rs.getInt("evaluacion"));
					rComparativa.setGanador(rs.getInt("cGanador"));
					rComparativa.setcganadorSug(rs.getInt("ganadorSug"));
					rComparativa.setcObservaciones (rs.getString("Observaciones"));
					rComparativa.setcFirmante(rs.getString("estadoPartida"));		 ///ESTADO DE LA PARTIDA			
					rComparativa.setcTipoCambio(rs.getFloat("tipoAdjudicacion"));
					rComparativa.setNumRfc(rs.getInt("lugarPrecioTabla"));
					comparativaList.add(rComparativa);
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			if (cs1 != null)
				cs1.close();

			cs1= null;
			rs = null;
			pstmnt = null;
		}
	//}
			
		return comparativaList;
	}
	public static List callLista(Connection conn, String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo) throws SQLException{
		
		List lineaConsolidadoList = new ArrayList();
		
		PreparedStatement pstmnt1 = null;
		
		ResultSet rs1 = null;
		CallableStatement cs1 = null;
		String Salida="";
		try {
	
			
			pstmnt1=conn.prepareStatement("select distinct nidLineaConsolidado from mProcedimientoCompleto pc WITH(NOLOCK) where"+ 
										"  cEjercicio=? and cIdTipoProcedimiento=?  and cIdUnidadEjecutora=? and nIdConsecutivo=?");
			pstmnt1.setString(1, cEjercicio);
			pstmnt1.setString(2, cIdTipoProcedimiento);
			pstmnt1.setString(3, cIdUnidadEjecutora);
			pstmnt1.setInt(4, nIdConsecutivo);
			rs1=pstmnt1.executeQuery();
			ProcedimientoComparativa rLinea;
			while (rs1.next()) {
				rLinea =new ProcedimientoComparativa();
				rLinea.setLineaConsolidado(rs1.getInt("nidLineaConsolidado"));
				 lineaConsolidadoList.add(rLinea);
			}	
			//System.out.println("Salida: "+Salida);
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (rs1 != null)
				rs1.close();

			if (pstmnt1 != null)
				pstmnt1.close();

			if (cs1 != null)
				cs1.close();

			cs1= null;
			rs1 = null;
			pstmnt1 = null;
		}
	//}
			
		return lineaConsolidadoList;
	}
	//////////////////////PROVEEDORES/////////////////
	public static List callProveedor(Connection conn, String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo) throws SQLException{
		
		List lineaProveedorList = new ArrayList();	
		PreparedStatement pstmnt1 = null;
		ResultSet rs1 = null;
		CallableStatement cs1 = null;
		String Salida="";
		try {
			pstmnt1=conn.prepareStatement(" select distinct pc.cIdRFC, (select cp.cRazonSocial from mCatalogoProveedor cp WITH(NOLOCK) where " +
					" cp.cIdRFC=pc.cIdRFC) as razonSocial,fecha, cestadoProveedor from mProcedimientoCompleto pc WITH(NOLOCK) " +
					" inner join mProcedimientoFechas pf WITH(NOLOCK) on pc.cIdProcedimiento=pf.nIdProcedimiento inner " +
					" join mCatalogoFechas cf WITH(NOLOCK) on cf.nIdFecha=pf.nIdFecha inner join mProcedimientoAdjudicacion pa on pc.cIdRFC=pa.cIdRFC and pc.cIdProcedimiento=pa.cIdProcedimiento" +
					" and pa.nIdconsecutivoAdj=pc.nIdconsecutivoAdj where pc.cEjercicio=? and pc.cIdTipoProcedimiento=? and  pc.cIdUnidadEjecutora=? and pc.nIdConsecutivo=? and pf.nIdFecha=8 and cEstadoProveedor=1");
			
			
			pstmnt1.setString(1, cEjercicio);
			pstmnt1.setString(2, cIdTipoProcedimiento);
			pstmnt1.setString(3, cIdUnidadEjecutora);
			pstmnt1.setInt(4, nIdConsecutivo);
			rs1=pstmnt1.executeQuery();
			ProcedimientoComparativa rProveedor;
			while (rs1.next()) {
				rProveedor =new ProcedimientoComparativa();
				rProveedor.setIdRFC(rs1.getString("cIdRFC"));
				rProveedor.setcrazonSocial(rs1.getString("razonSocial"));
				rProveedor.setFecha(rs1.getString("fecha"));
				lineaProveedorList.add(rProveedor);
			}	
			//System.out.println("Salida: "+Salida);
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (rs1 != null)
				rs1.close();

			if (pstmnt1 != null)
				pstmnt1.close();

			if (cs1 != null)
				cs1.close();

			cs1= null;
			rs1 = null;
			pstmnt1 = null;
		}
	//}
			
		return lineaProveedorList;
	}
	
public static List callMontosTotales(Connection conn, String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo, String rfc) throws SQLException{
		
		List MontosTotalesList = new ArrayList();	
		PreparedStatement pstmnt1 = null;
		ResultSet rs1 = null;
		CallableStatement cs1 = null;
		String Salida="";
		float total1=0;
		float totalP=0;
		float iva=0;
	
		try {
			pstmnt1=conn.prepareStatement("SELECT * FROM dbo.fn_mProcedimientoMontosTotalesComparativa(?,?,?,?,?)");
			pstmnt1.setString(1, cEjercicio);
			pstmnt1.setString(2, cIdTipoProcedimiento);
			pstmnt1.setString(3, cIdUnidadEjecutora);
			pstmnt1.setInt(4, nIdConsecutivo);
			pstmnt1.setString(5, rfc);
			rs1=pstmnt1.executeQuery();
			ProcedimientoComparativa rMontos;
			while (rs1.next()) {
				rMontos =new ProcedimientoComparativa();
				rMontos.setmontoMaximo(rs1.getFloat("total"));
				rMontos.setmIva(rs1.getFloat("nPorcentajeIVA"));
				iva=rMontos.getmIva();
				total1=rMontos.getmontoMaximo();
				totalP=totalP+total1;
			}
			MontosTotalesList.add(totalP);
			MontosTotalesList.add(iva);
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (rs1 != null)
				rs1.close();

			if (pstmnt1 != null)
				pstmnt1.close();

			if (cs1 != null)
				cs1.close();

			cs1= null;
			rs1 = null;
			pstmnt1 = null;
		}
	//}
		
		return MontosTotalesList;
	}
public static List callFirmantes(Connection conn,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo) throws SQLException{
	List FirmantesList = new ArrayList();	
	PreparedStatement pstmnt1 = null;
	ResultSet rs1 = null;
	CallableStatement cs1 = null;
	String idProcedimiento=cIdTipoProcedimiento+'-'+cIdUnidadEjecutora+'-'+nIdConsecutivo;
	
	try {
		pstmnt1=conn.prepareStatement(" select cNombre+' '+cPaterno+' '+cMaterno as Nombre from mFirmantesComparativa fc WITH(NOLOCK) inner join mCatalogoFirmantes f WITH(NOLOCK) on" +
				" fc.cIdUnidadEjecutora=f.cIdUnidadEjecutora and fc.nIdFirmante=f.nIdFirmante where fc.cIdProcedimiento=?");
		pstmnt1.setString(1,idProcedimiento); 
		rs1=pstmnt1.executeQuery();
		ProcedimientoComparativa rFirmantes;
		while (rs1.next()) {
			rFirmantes =new ProcedimientoComparativa();
			rFirmantes.setcFirmante(rs1.getString("Nombre"));
			
			FirmantesList.add(rFirmantes);
		}
		
		
	}catch(Exception e){
		e.printStackTrace();
	} finally {
		if (rs1 != null)
			rs1.close();

		if (pstmnt1 != null)
			pstmnt1.close();

		if (cs1 != null)
			cs1.close();

		cs1= null;
		rs1 = null;
		pstmnt1 = null;
	}
//}
	
	return FirmantesList;
}

public static List callProcedimiento(Connection conn,String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora,int nIdConsecutivo, int nIdFecha) throws SQLException{


	List ProveedorList = new ArrayList();	
	PreparedStatement pstmnt1 = null;
	ResultSet rs1 = null;
	CallableStatement cs1 = null;
	String idProcedimiento=cIdTipoProcedimiento+'-'+cIdUnidadEjecutora+'-'+nIdConsecutivo;
	try {
		pstmnt1=conn.prepareStatement(" select cCategoria,cidProcedimiento from mProcedimiento p WITH(NOLOCK) inner join " +
				"mCatalogoCategoriaProcedimiento c WITH(NOLOCK) on p.nIdCategoria=c.nIdCategoria where cIdProcedimiento=? " 
				//"and pf.nIdFecha=6"
				);
		pstmnt1.setString(1,idProcedimiento); 
		rs1=pstmnt1.executeQuery();
		ProcedimientoComparativa rFirmantes;
		while (rs1.next()) {
			rFirmantes =new ProcedimientoComparativa();
			rFirmantes.setcCategoria(rs1.getString("cCategoria"));
			rFirmantes.setProcedimiento(rs1.getString("cidProcedimiento"));
		//	rFirmantes.setDescripcion(rs1.getString("cDescripcion"));
		//	rFirmantes.setFecha(rs1.getString("fecha"));
			
			ProveedorList.add(rFirmantes);
		}
		
		
	}catch(Exception e){
		e.printStackTrace();
	} finally {
		if (rs1 != null)
			rs1.close();

		if (pstmnt1 != null)
			pstmnt1.close();

		if (cs1 != null)
			cs1.close();

		cs1= null;
		rs1 = null;
		pstmnt1 = null;
	}
//}
	
	return ProveedorList;
}
public static List callProveedorOrdenado(Connection conn, String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo, int Linea, int query) throws SQLException{
	
	List lineaProveedorList = new ArrayList();
	
	PreparedStatement pstmnt1 = null;
	
	ResultSet rs = null;
	CallableStatement cs1 = null;
	String Salida="";
	String query1="";
	
	try {
		if (query==1){
			query1=" and 1=1";
		}else{
			if (query==2){
				query1=" and mMontoMinimo<>0.00 ";
			}else{
				query1=" and mMontoMinimo=0.00 ";
			}
		}
		//style='border-bottom:1.0pt solid black;'
		pstmnt1=conn.prepareStatement(" select distinct mMontoMinimo, '<td align=''right''  style=''border-bottom:1.0pt solid black''>$'+ltrim(Str(pa.mMontoMinimo,12,2))+'</td>'  as mMontoMinimoTexto,\r\n"
				+ "				  '<td colspan=''4''>'+pa.cIdRFC+'</td>'as cIdRFC,  '<td>'+cast(pa.nidlineaConsolidado as varchar)+'</td>', '<td colspan=''4'' \r\n"
				+ "				 style=''border-right:1.0pt solid black;border-bottom:1.0pt solid black''>'+(select cp.cRazonSocial from mCatalogoProveedor cp where \r\n"
				+ "				 cp.cIdRFC=pa.cIdRFC)+'</td>' as razonSocial,  dbo.fn_mConsolidadoLineaCantidadArticulos(cl.cEjercicio,cl.cIdTipoConsolidado,\r\n"
				+ "				 cl.cIdUnidadEjecutora,cl.nIdConsecutivo,cl.nIdLineaConsolidado) as cantidad, cl.agregaProveedor, ISNULL((pa.mMontoMinimo * dbo.fn_mConsolidadoLineaCantidadArticulos(cl.cEjercicio,cl.cIdTipoConsolidado,cl.cIdUnidadEjecutora,cl.nIdConsecutivo,\r\n"
				+ "				 cl.nIdLineaConsolidado)* dbo.fn_mProcedimientoTipoCambioAdjudicacion(pa.cEjercicio,pa.cIdTipoProcedimiento,pa.cIdUnidadEjecutora,\r\n"
				+ "				 pa.nIdConsecutivo,pa.cIdRFC,pa.nIdconsecutivoAdj)),0 ) AS mMontoTotal, ISNULL(dbo.fn_mProcedimientoTipoCambioAdjudicacion(pa.cEjercicio,pa.cIdTipoProcedimiento\r\n"
				+ "				 ,pa.cIdUnidadEjecutora ,pa.nIdConsecutivo ,pa.cIdRFC,pa.nIdconsecutivoAdj),0) as tipoCambio,ISNULL((pa.mMontoMaximo * dbo.fn_mConsolidadoLineaCantidadArticulos(\r\n"
				+ "				 cl.cEjercicio,cl.cIdTipoConsolidado,cl.cIdUnidadEjecutora,cl.nIdConsecutivo,cl.nIdLineaConsolidado)* \r\n"
				+ "				 dbo.fn_mProcedimientoTipoCambioAdjudicacion(pa.cEjercicio,pa.cIdTipoProcedimiento,pa.cIdUnidadEjecutora,pa.nIdConsecutivo,pa.cIdRFC,pa.nIdconsecutivoAdj)),0)\r\n"
				+ "				 AS mMontoTotalBruto, cEstadoProveedor \r\n"
				+ "				 from mConsolidadoLineas cl WITH(NOLOCK) \r\n"
				+ "				 INNER JOIN mProcedimiento p WITH(NOLOCK) ON cl.cEjercicio = p.cEjercicio \r\n"
				+ "				 AND  cl.cIdTipoConsolidado =  p.cIdTipoConsolidado AND cl.cIdUnidadEjecutora = p.cIdUnidadEjecutora AND cl.nIdConsecutivo = p.nIdConsecutivoConsolidado\r\n"
				+ "				 LEFT JOIN mProcedimientoCompleto pa ON p.cEjercicio = pa.cEjercicio AND p.cIdTipoProcedimiento = pa.cIdTipoProcedimiento AND \r\n"
				+ "				 p.cIdUnidadEjecutora = pa.cIdUnidadEjecutora AND p.nIdConsecutivo = pa.nIdConsecutivo AND cl.nIdLineaConsolidado = pa.nIdLineaConsolidado\r\n"
				+ "				 INNER JOIN mProcedimientoAdjudicacion pad WITH(NOLOCK) ON pa.cidrfc=pad.cIdRFC and pa.cIdProcedimiento=pad.cIdProcedimiento\r\n"
				+ "				 and pad.nIdconsecutivoAdj=pa.nIdconsecutivoAdj\r\n"
				+ "				 where pa.cEjercicio=? and pa.cIdTipoProcedimiento=? and pa.cIdUnidadEjecutora=? and pa.nIdConsecutivo=? and\r\n"
				+ "				 pa.nIdLineaConsolidado=? and pad.cEstadoProveedor=1  " + query1+" ORDER BY mMontoMinimo ");
		
		
		
		pstmnt1.setString(1, cEjercicio);
		pstmnt1.setString(2, cIdTipoProcedimiento);
		pstmnt1.setString(3, cIdUnidadEjecutora);
		pstmnt1.setInt(4, nIdConsecutivo);
		pstmnt1.setInt(5, Linea);
		rs=pstmnt1.executeQuery();
		ProcedimientoComparativa rProveedor;
		while (rs.next()) {
			rProveedor =new ProcedimientoComparativa();
			rProveedor.setIdRFC(rs.getString("cIdRFC"));
			rProveedor.setcrazonSocial(rs.getString("razonSocial"));
			rProveedor.setmontoMinimoTexto(rs.getString("mMontoMinimoTexto"));
			rProveedor.setmontoMinimo(rs.getFloat("mMontoMinimo"));
			rProveedor.setCantidad(rs.getInt("cantidad"));
			rProveedor.setmontoMaximo(rs.getFloat("mMontoTotal")); /// monto minimoBruto
			rProveedor.setcTipoCambio(rs.getFloat("tipoCambio"));
			rProveedor.setmontoMaximoBruto(rs.getFloat("mMontoTotalBruto")); /// monto total con el tipo de cambio
			rProveedor.setNumRfc(rs.getInt("agregaProveedor")); /// partida desierta
			lineaProveedorList.add(rProveedor);
		}	
		//System.out.println("Salida: "+Salida);
	}catch(Exception e){
		e.printStackTrace();
	} finally {
		if (rs != null)
			rs.close();

		if (pstmnt1 != null)
			pstmnt1.close();

		if (cs1 != null)
			cs1.close();

		cs1= null;
		rs = null;
		pstmnt1 = null;
	}
//}
		
	return lineaProveedorList;
}

public static List callPartidaOrdenado(Connection conn, String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo, int Linea) throws SQLException{

	
	List PartidaOrdenadoList = new ArrayList();
	
	PreparedStatement pstmnt1 = null;
	
	ResultSet rs = null;
	CallableStatement cs1 = null;
	String Salida="";
	try {
		pstmnt1=conn.prepareStatement("select cl.nidlineaConsolidado, " +
				" '<td rowspan=''3'' style=''border-right:1.0pt solid black;border-bottom:1.0pt solid black;''>'+cl.cDescripcion+'</td>' as descripcion," +
				" '<td colspan=''3'' style=''border-right:1.0pt solid black;border-bottom:1.0pt solid black;''>'+cl.cDescripcion+'</td>' as descripcionObs from mConsolidadoLineas cl " +
				" INNER JOIN mProcedimiento p ON cl.cEjercicio = p.cEjercicio AND cl.cIdTipoConsolidado = p.cIdTipoConsolidado " +
				" AND cl.cIdUnidadEjecutora = p.cIdUnidadEjecutora AND cl.nIdConsecutivo = p.nIdConsecutivoConsolidado" +
				" inner join mProcedimientoCompleto pc ON p.cEjercicio = pc.cEjercicio" +
				" where  p.cEjercicio=? and p.cIdTipoProcedimiento=?  and p.cIdUnidadEjecutora=? and p.nIdConsecutivo=? and cl.nidlineaConsolidado=? " +
				" AND p.cIdTipoProcedimiento = pc.cIdTipoProcedimiento  AND p.cIdUnidadEjecutora = pc.cIdUnidadEjecutora " +
				" AND p.nIdConsecutivo = pc.nIdConsecutivo AND cl.nIdLineaConsolidado = pc.nIdLineaConsolidado  group by cl.nidlineaConsolidado, cl.cDescripcion");
		pstmnt1.setString(1, cEjercicio);
		pstmnt1.setString(2, cIdTipoProcedimiento);
		pstmnt1.setString(3, cIdUnidadEjecutora);
		pstmnt1.setInt(4, nIdConsecutivo);
		pstmnt1.setInt(5, Linea);
		rs=pstmnt1.executeQuery();
		ProcedimientoComparativa rPartida;
		while (rs.next()) {
			rPartida =new ProcedimientoComparativa();
			rPartida.setDescripcion(rs.getString("descripcion"));
			rPartida.setLineaConsolidado(rs.getInt("nidLineaConsolidado"));
			rPartida.setUnidadEjecutora(rs.getString("descripcionObs"));
		
			PartidaOrdenadoList.add(rPartida);
		}	
		//System.out.println("Salida: "+Salida);
	}catch(Exception e){
		e.printStackTrace();
	} finally {
		if (rs != null)
			rs.close();

		if (pstmnt1 != null)
			pstmnt1.close();

		if (cs1 != null)
			cs1.close();

		cs1= null;
		rs = null;
		pstmnt1 = null;
	}
//}
		
	return PartidaOrdenadoList;
}


public static int ordena(Connection conn,String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo, 
		int Linea,String rfc,int posicion) throws SQLException{
	int val = -1;
	PreparedStatement pstmnt = null;
	//String rfc="";
//	String aux="";

	
	//	for (int i=0; i<listRfc.size();i++){
		try {
		//	rfc=listRfc.get(i).toString();
		//	aux=posicion.get(i).toString();
			conn.setAutoCommit(false);

			pstmnt = conn.prepareStatement("update mprocedimientoCompleto set ganadorSug=? where cEjercicio=? and cIdTipoProcedimiento=? " +
			" and cIdUnidadEjecutora=? and nIdConsecutivo=? and nIdLineaConsolidado=? AND cIdRFC=?");
			pstmnt.setInt(1, posicion);
			pstmnt.setString(2, cEjercicio);
			pstmnt.setString(3, cIdTipoProcedimiento);
			pstmnt.setString(4, cIdUnidadEjecutora);
			pstmnt.setInt	(5, nIdConsecutivo);
			pstmnt.setInt	(6, Linea);
			pstmnt.setString(7, rfc);
			val=pstmnt.executeUpdate();
			conn.commit();
		} catch (SQLException e ) { 
			conn.rollback();
		}finally {
			if (pstmnt != null)
				pstmnt.close();
			pstmnt = null;
		}
        
	
	return  val;
}

public static int ordenaPrecio(Connection conn,String cEjercicio,String cIdTipoProcedimiento, String cIdUnidadEjecutora, int nIdConsecutivo, 
	int Linea,String rfc,int posicion) throws SQLException{
int val = -1;
PreparedStatement pstmnt = null;
//String rfc="";
//String aux="";


//	for (int i=0; i<listRfc.size();i++){
	try {
	//	rfc=listRfc.get(i).toString();
	//	aux=posicion.get(i).toString();
		conn.setAutoCommit(false);

		pstmnt = conn.prepareStatement("update mprocedimientoCompleto set lugarPrecio=? where cEjercicio=? and cIdTipoProcedimiento=? " +
		" and cIdUnidadEjecutora=? and nIdConsecutivo=? and nIdLineaConsolidado=? AND cIdRFC=?");
		pstmnt.setInt(1, posicion);
		pstmnt.setString(2, cEjercicio);
		pstmnt.setString(3, cIdTipoProcedimiento);
		pstmnt.setString(4, cIdUnidadEjecutora);
		pstmnt.setInt	(5, nIdConsecutivo);
		pstmnt.setInt	(6, Linea);
		pstmnt.setString(7, rfc);
		val=pstmnt.executeUpdate();
		conn.commit();
	} catch (SQLException e ) { 
		conn.rollback();
	}finally {
		if (pstmnt != null)
			pstmnt.close();
		pstmnt = null;
	}
    

return  val;
}
}
