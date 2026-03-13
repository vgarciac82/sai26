package com.syc.cfdi.utils;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.sai.contabilidad.utils.db.CloseObject;


public class XMLReviewerManager {
	
	private static final Logger log = Logger.getLogger(XMLReviewerManager.class);
	
	public static String[] getPagosConCFDI(Connection conn)  throws Exception{
		String query = "SELECT	* "
					 +"  FROM ( "
					 +"		SELECT	DISTINCT cTipoPago, "
					 +"				nFolioPago "
					 +"		  FROM	tpagofactura WITH(NOLOCK) "
					 +"		 WHERE	cRFCFactura NOT IN ('OFICIOTRANSITO', 'OFICIOPAGO', 'OFICIOCTOFED', 'EXTRANJERO', 'OFICIOALIMENTACION') "
					 +"		   AND cEsNotaCredito = 'N' "
//					 +"        AND cTipoPago= 'RELACIONGASTOS' AND nFolioPago IN( 8951 ) " 
					 +") AS tbl "
					 +"ORDER BY ctipopago, nFolioPago ";
		log.debug(query);
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try{
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			List<String> facturas = new ArrayList<String>();
			while(rs.next() ){
				facturas.add( StringUtils.trim( rs.getString("cTipoPago")  ) + "," + rs.getInt("nFolioPago") );
			}
			
			return facturas.toArray( new String[facturas.size()]);
			
		}finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
		
	}

	public static Map<String, String> getXMLFiles(Connection conn, String tipoPago, int nFolioPago) throws Exception {
		int idTC = -1;
		int idGabinete = -1;

		idTC = getTipoCaso(conn, tipoPago);
		idGabinete = getIDGabinete(conn, idTC, nFolioPago);

		log.trace(" Tipo Caso: " + idTC + " Gabinete: " + idGabinete);

		return getCFDIXMLFilePath(conn, tipoPago, idGabinete);

	}

	private static Map<String, String> getCFDIXMLFilePath(Connection conn, String tituloAplicacion, int idGabinete) throws Exception {
		Map<String, String> result = null;
		ResultSet rs = null;
		Statement ps = null;
		String query =	 "SELECT	volumen.UNIDAD_DISCO + volumen.RUTA_BASE + volumen.RUTA_DIRECTORIO + pagina.NOM_ARCHIVO_VOL AS rutaArchivo,  "
						+"			documento.NOMBRE_DOCUMENTO "
						+"  FROM	imx_pagina pagina WITH (NOLOCK) "
						+"		INNER JOIN "
						+"		imx_volumen volumen WITH(NOLOCK) "
						+"			ON pagina.VOLUMEN = volumen.VOLUMEN "
						+"		INNER JOIN "
						+"		IMX_DOCUMENTO documento WITH(NOLOCK) "
						+"			ON  pagina.TITULO_APLICACION = documento.TITULO_APLICACION "
						+"			AND pagina.ID_GABINETE = documento.ID_GABINETE "
						+"			AND pagina.ID_CARPETA_PADRE = documento.ID_CARPETA_PADRE "
						+"			AND pagina.ID_DOCUMENTO = documento.ID_DOCUMENTO "
						+"		INNER JOIN "
						+"		IMX_CARPETA carpeta WITH(NOLOCK) "
						+"			ON  pagina.TITULO_APLICACION = carpeta.TITULO_APLICACION "
						+"			AND pagina.ID_GABINETE = carpeta.ID_GABINETE "
						+"			AND pagina.ID_CARPETA_PADRE = carpeta.ID_CARPETA "
						+" WHERE	pagina.TITULO_APLICACION = '" + tituloAplicacion + "' "
						+"   AND	pagina.ID_GABINETE = " + idGabinete
						+"   AND	carpeta.NOMBRE_CARPETA = 'CFDI' "
						+"   AND	pagina.NOM_ARCHIVO_ORG LIKE '%.xml'";
		try {
			
			ps = conn.createStatement();

			rs = ps.executeQuery(query);

			while (rs.next()) {
				if (result == null)
					result = new HashMap<String, String>();

				result.put(rs.getString("NOMBRE_DOCUMENTO"), rs.getString("rutaArchivo"));
			}

			return result;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
		
	}

	private static int getIDGabinete(Connection conn, int idTC, int nFolioPago) throws Exception {

		String query = "SELECT C_ID_GABINETE FROM CG_CASO with(nolock) WHERE ( CASE WHEN  id_tc = 43 then 6 else id_tc END ) = " + idTC + " AND C_FOLIO LIKE '%-%-" + nFolioPago + "'";
		Statement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.createStatement();

			rs = ps.executeQuery(query);

			if (rs.next()) {
				return rs.getInt(1);
			} else
				throw new Exception("No se encontro gabinete con folio " + nFolioPago + " para el tipo de caso " + idTC);
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}

	private static int getTipoCaso(Connection conn, String tipoPago) throws Exception{
		
		String query = "SELECT id_tc FROM cg_tipo_caso WITH(NOLOCK) WHERE TC_GAVETA_ASOCIADA = '" + tipoPago + "'";
		Statement ps = null;
		ResultSet rs = null;
		
		try{
			
			ps = conn.createStatement();
			
			rs = ps.executeQuery(query);
			
			if( rs.next() )
				return rs.getInt(1);
			else 
				throw new Exception("No se encontro Tipo Caso para la gaveta asociada: " + tipoPago );
		}finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}

	public static void actualizaFormaPago(Connection conn, String tipoPago, int nFolioPago, String uuid, String metodoPago) throws Exception {
		PreparedStatement ps = null;
		String query = "UPDATE tPagoFactura SET cMetodoPago = ? WHERE cTipoPago = ? and nFolioPago = ? AND cfactura = ? ";
		
		try {
			
			ps = conn.prepareStatement(query);
			
			ps.setString(1, metodoPago);
			ps.setString(2, tipoPago);
			ps.setInt(3, nFolioPago);
			ps.setString(4, uuid);
			
			int afectados = ps.executeUpdate();
			
			log.info(String.format( "Ejecutando: [%s] %s,%s,%d,%s Afectados: %d", query,  metodoPago, tipoPago, nFolioPago, uuid,  afectados  ));
		} finally {
			CloseObject.closeObject(ps);
		}
	}

	public static void insertaImpuestoPago(Connection conn, String tipoPago, int nFolioPago, String uuid, String impuesto, BigDecimal importe, BigDecimal tasaOCuota) throws Exception {
			String query = "INSERT INTO tPagoFacturaImpuestos( cTipoPago , nFolioPago , UUID , cNombreImpuesto , mImporteImpuesto ,nTazaImpuesto )" 
                         + "VALUES  ( ? , ? ,? ,?, ?, ?)";
			
			PreparedStatement ps = null;
			int insertados = 0;
			try{
				ps = conn.prepareStatement(query);
				ps.setString(1, tipoPago);
				ps.setInt(2, nFolioPago);
				ps.setString(3, uuid);
				ps.setString(4, impuesto);
				ps.setBigDecimal(5, importe);
				ps.setBigDecimal(6, tasaOCuota);
				
				insertados = ps.executeUpdate();
				
				log.info("Se insertaron : " + insertados + " registros");
			}finally {
				CloseObject.closeObject(ps);
			}
	}

}
