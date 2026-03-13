package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.syc.cfdi.db.CloseObject;

public class MovimientosPresupuestalesManager {

	public static List<StringBuffer> BuscaMovimientos(Connection conn, String ep, String tipoCuenta, String strDesde, String strHasta)throws Exception{
		List<StringBuffer> movimientosXMes = new ArrayList<StringBuffer>();
		StringBuffer listaMovimientosFiltrados1 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados2 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados3 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados4 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados5 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados6 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados7 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados8 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados9 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados10 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados11 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados12 = new StringBuffer();
		StringBuffer listaMovimientosFiltrados = new StringBuffer();

		String strCount = "";
		int j = 0;

		for(int i = 0; i<12; i++ ){
			j = i+1;
			if(j<10){
				strCount = tipoCuenta + "-0000" + String.valueOf(j) + "-%" ;
			}
			
			else{
				strCount = tipoCuenta + "-000" + String.valueOf(j) + "-%" ;
			}

			listaMovimientosFiltrados = Movimientos(conn, ep, strCount, strDesde, strHasta);

			if (j == 1){
				listaMovimientosFiltrados1 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados1);
			}
			if (j == 2){
				listaMovimientosFiltrados2 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados2);
			}
			if (j == 3){
				listaMovimientosFiltrados3 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados3);
			}
			if (j == 4){
				listaMovimientosFiltrados4 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados4);
			}
			if (j == 5){
				listaMovimientosFiltrados5 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados5);
			}
			if (j == 6){
				listaMovimientosFiltrados6 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados6);
			}
			if (j == 7){
				listaMovimientosFiltrados7 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados7);
			}
			if (j == 8){
				listaMovimientosFiltrados8 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados8);
			}
			if (j == 9){
				listaMovimientosFiltrados9 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados9);
			}
			if (j == 10){
				listaMovimientosFiltrados10 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados10);
			}
			if (j == 11){
				listaMovimientosFiltrados11 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados11);
			}
			if (j == 12){
				listaMovimientosFiltrados12 = listaMovimientosFiltrados;
				movimientosXMes.add(listaMovimientosFiltrados12);
			}
		}
		return movimientosXMes;
	}	

	public static StringBuffer Movimientos(Connection conn, String ep, String tipoCuenta, String strDesde, String strHasta)throws Exception{
		StringBuffer listaMovimientosFiltrados = new StringBuffer();
		SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		String Sql = "SELECT  Movimientos.nFolioPoliza, " +
					"	Movimientos.nDocRenglon, " +
					"	Movimientos.nCuenta, " +
					"	Movimientos.nSubCuenta, " +
					"	CAST(Movimientos.mMovimiento AS MONEY) AS mMovimiento, " +
					"	Movimientos.cTipoMovimiento, " +
					"	Movimientos.cDescripcionMovPol, " +
					"	Movimientos.cCentroContable, " +
					"	CASE " +
					"		WHEN Movimientos.cTipoDocumento = 'APARTADO' THEN Movimientos.cTipoDocumento + '/'+Apartado.cIdSolicitud " +
					"		WHEN Movimientos.cTipoDocumento = 'PAGOAPARTADO' THEN Movimientos.cTipoDocumento + '/'+PagoApartado.cTipoPago " +
					"		WHEN Movimientos.cTipoDocumento = 'APARTADO_OPC' THEN Movimientos.cTipoDocumento + '/'+ApartadoObra.FolioSAI " +
					"		WHEN Movimientos.cTipoDocumento = 'COMPROMISO' THEN Movimientos.cTipoDocumento + '/' + Compromiso.cTipoContrato+'['+REPLACE(Compromiso.cIdContrato,' ','')+']' " +
					" 		WHEN Movimientos.cTipoDocumento = 'COMP_OPC' THEN Movimientos.cTipoDocumento + '/' + CompromisoObra.FolioSAI " +
					"		WHEN Movimientos.cTipoDocumento = 'EJERCIDO' THEN Movimientos.cTipoDocumento + '/' + Ejercido.cTipoPago " +
					"		WHEN Movimientos.cTipoDocumento = 'PAGADO' THEN Movimientos.cTipoDocumento + '/' + Pagado.cTipoPago " +
					"		ELSE Movimientos.cTipoDocumento " +
					"	END cTipoDocumento, " +
					"	Movimientos.nConsecutivoMovimiento, " +
					"	Movimientos.cRamo, " +
					"	Movimientos.fOperacionMovimiento, " +
					"	CASE " +
					"		WHEN Movimientos.cTipoDocumento='PAGOAPARTADO' THEN CAST(Movimientos.cFolioDocumentoMovimiento AS VARCHAR(10)) + '/' + CAST(PagoApartado.nFolioPAGO AS NVARCHAR(10)) " +
					"		WHEN Movimientos.cTipoDocumento='EJERCIDO' THEN CAST(Movimientos.cFolioDocumentoMovimiento AS VARCHAR(10)) + '/' + CAST(Ejercido.nFolioPAGO AS NVARCHAR(10)) " +
					"		WHEN Movimientos.cTipoDocumento='PAGADO' THEN CAST(Movimientos.cFolioDocumentoMovimiento AS VARCHAR(10)) + '/' + CAST(Pagado.nFolioPAGO AS NVARCHAR(10)) " +
					"		ELSE Movimientos.cFolioDocumentoMovimiento " +
					"	END cFolioDocumentoMovimiento, " +
					"	Movimientos.cCancelaMovimiento, " +
					"	Movimientos.fMovimiento, " +
					"	Movimientos.dConceptoMovimiento, " +
					"	Movimientos.cMoneda, " +
					"	Movimientos.aEjercicioFiscal, " +
					"	Movimientos.cTipoPoliza, " +
					"	Movimientos.cUnidadResponsable, " +
					"	Movimientos.parcial, " +
					"	Movimientos.Periodo13, " +
					"	Movimientos.ADEFAS, " +
					"	Movimientos.nTipoAjuste, " +
					"	Cuentas.NaturalezaCuenta " +
					"FROM tMovimiento Movimientos WITH (NOLOCK) " +
					"INNER JOIN dbo.tCuentas Cuentas ON Movimientos.nCuenta = Cuentas.nCuenta " +
					"LEFT OUTER JOIN dbo.tApartadoEncabezado Apartado WITH (NOLOCK) " +
					"	ON Movimientos.cTipoDocumento='APARTADO' AND Apartado.nFolioApartado = Movimientos.cFolioDocumentoMovimiento " +
					"LEFT OUTER JOIN dbo.tPagoApartadoEncabezado PagoApartado WITH (NOLOCK) " +
					"	ON Movimientos.cTipoDocumento='PAGOAPARTADO' AND PagoApartado.nFolioPagoApartado = Movimientos.cFolioDocumentoMovimiento " +
					"LEFT OUTER JOIN dbo.tObraPublicaApartadoEncabezado ApartadoObra WITH (NOLOCK) " +
					"	ON Movimientos.cTipoDocumento='APARTADO_OPC' AND ApartadoObra.nFolioOPAHeader = Movimientos.cFolioDocumentoMovimiento " +
					" LEFT OUTER JOIN dbo.tCompromisoEncabezado Compromiso WITH (NOLOCK) " +
					"	ON Movimientos.cTipoDocumento = 'COMPROMISO' AND Compromiso.nFolioCompromiso = Movimientos.cFolioDocumentoMovimiento " +
					"LEFT OUTER JOIN dbo.tObraPublicaCompromisoEncabezado CompromisoObra WITH ( NOLOCK ) " +
					"	ON Movimientos.cTipoDocumento = 'COMP_OPC' AND CompromisoObra.nFolioOPComHeader = Movimientos.cFolioDocumentoMovimiento " +
					"LEFT OUTER JOIN dbo.tEjercidoEncabezado Ejercido WITH ( NOLOCK ) " +
					"	ON Movimientos.cTipoDocumento = 'EJERCIDO' AND Ejercido.nFolioEjercido = Movimientos.cFolioDocumentoMovimiento " +
					"LEFT OUTER JOIN dbo.tPagadoEncabezado Pagado WITH ( NOLOCK ) " + 
					"	ON Movimientos.cTipoDocumento = 'PAGADO' AND Pagado.nFolioPagado = Movimientos.cFolioDocumentoMovimiento " +
					"WHERE Movimientos.nSubCuenta = ? " +
					"	AND Movimientos.nCuenta like '" + tipoCuenta + "'" + 
					" 	AND convert (DATE, Movimientos.fOperacionMovimiento, 103) BETWEEN convert ( date, ? , 103 ) AND convert(date, ? , 103 ) " +
					"	AND mMovimiento <> 0 " +
					"ORDER BY Movimientos.fOperacionMovimiento ASC";

		try {
		
			pstmnt = conn.prepareStatement(Sql);
			pstmnt.setString(1, ep);
			pstmnt.setString(2,strDesde);
			pstmnt.setString(3,strHasta);
	
			rs = pstmnt.executeQuery();
			while (rs.next()){
				listaMovimientosFiltrados.append(rs.getString("nFolioPoliza"))
					.append(",").append(rs.getString("nDocRenglon"))
					.append(",").append(rs.getString("nCuenta").trim())
					.append(",").append(rs.getString("nSubCuenta"))
					.append(",").append(rs.getString("mMovimiento"))
					.append(",").append(rs.getString("cTipoMovimiento"))
					.append(",").append(rs.getString("cDescripcionMovPol"))
					.append(",").append(rs.getString("cCentroContable"))
					.append(",").append(rs.getString("cTipoDocumento"))
					.append(",").append(rs.getString("nConsecutivoMovimiento"))
					.append(",").append(rs.getString("cRamo"))
					.append(",").append(fecha.format(sdf.parse(rs.getString("fOperacionMovimiento"))))
					.append(",").append(rs.getString("cFolioDocumentoMovimiento"))
					.append(",").append(rs.getString("cCancelaMovimiento"))
					.append(",").append(fecha.format(sdf.parse(rs.getString("fMovimiento"))))
					.append(",").append(rs.getString("dConceptoMovimiento"))
					.append(",").append(rs.getString("cMoneda"))
					.append(",").append(rs.getString("aEjercicioFiscal"))
					.append(",").append(rs.getString("cTipoPoliza"))
					.append(",").append(rs.getString("cUnidadResponsable"))
					.append(",").append(rs.getString("NaturalezaCuenta"))
					.append(";");
				}
			
		} finally {	
			CloseObject.closeObject( rs );
			CloseObject.closeObject( pstmnt );
			
		}
		return listaMovimientosFiltrados;
	}
}
