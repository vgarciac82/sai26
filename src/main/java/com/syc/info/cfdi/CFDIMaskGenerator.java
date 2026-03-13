package com.syc.info.cfdi;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.input.BOMInputStream;

import com.google.common.io.ByteStreams;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.cfdi.v3332.Comprobante.Comprobante;
import com.syc.cfdi.v3332.Comprobante.Concepto;
import com.syc.cfdi.v3332.Comprobante.Conceptos;

import mx.grupocorasa.sat.cfdi.v3.CFDv33;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import mx.grupocorasa.sat.exceptions.UnsupportedVersionException;
import net.sf.jasperreports.engine.JasperRunManager;


public class CFDIMaskGenerator {

	private static final String	RUTA_SALIDA			= "/CFDI/PDF/";
	private static final String	queryExisteFactura	= "SELECT	nIdFactura " + "  FROM	tFacturasCFDIEncabezado WITH(NOLOCK) " + " WHERE	cUUID = ? ";
	private static final String	queryEFActivo		= "SELECT aEjercicioFiscal FROM tEjercicioFiscal WITH(nolock) WHERE cActivo = 1";
	private static final String	queryInsertEnc		= "INSERT INTO tFacturasCFDIEncabezado" + "        ( cUUID ," + "          cNoCertificado ," + "          cLugarExp ," + "          cFecha ," + "          cTipoComprob ," + "          cFolioSerie ," + "          cRegimen ," + "          cNombreEmisor ," + "          cRFCReceptor ," + "          cNombreReceptor ," + "          cMoneda ," + "          cFormaPago ," + "          cMetodoPago ," + "          mSubtotal ," + "          mImpustosTras ," + "          mImpuestosRet ," + "          mTotal ," + "          cSelloCFD ," + "          cSelloSAT ," + "          cNoCertificadoSAT ," + "          cFechaCertificado ," + "          cVersion ," + "          cFolio," + "          cRFCEmisor" + "        )" + "VALUES" + " (        ?," + "          ?," + "          ?," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ?" + "        )";

	private static final String	queryInsertDetalle	= "INSERT INTO tFacturasCFDIDetalle" + "        ( nIdFactura ," + "          nCantidad ," + "          cUnidad ," + "          cNumIdentif ," + "          cDescripcion ," + "          mValorUnitario ," + "          mImporte" + "        )" + "VALUES  (" + "		     ?," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ?" + "		 )";

	private PreparedStatement	psInsertEnc			= null;
	private PreparedStatement	psInsertDet			= null;
	private PreparedStatement	psExisteFactura		= null;
	private PreparedStatement	psEFActivo			= null;

	private Connection			conn;
	private String				pathSalida			= null;

	public CFDIMaskGenerator( Connection conn ) throws Exception {

		String generatedColumns[] = { "ID" };

		psInsertEnc = conn.prepareStatement( queryInsertEnc, generatedColumns );
		psInsertDet = conn.prepareStatement( queryInsertDetalle );
		psExisteFactura = conn.prepareStatement( queryExisteFactura );
		psEFActivo = conn.prepareStatement( queryEFActivo );
		this.conn = conn;
		generaPathSalida();
	}

	private void generaPathSalida() throws Exception {
		ResultSet rsEF = null;
		try {
			rsEF = psEFActivo.executeQuery();
			if ( rsEF.next() ) {
				String efActivo = rsEF.getString( 1 );
				this.pathSalida = CFDIMaskGenerator.RUTA_SALIDA + efActivo;
				File fSalida = new File( this.pathSalida );
				if ( !fSalida.exists() )
					fSalida.mkdirs();
				fSalida = null;
			}
		} finally {
			CloseObject.closeObject( rsEF );
		}

	}

	public String generaPDFFactura( String rutaXML ) throws Exception {

		File archivoXML = new File( rutaXML );

		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "SUBREPORT_DIR", "/reports/" );
		params.put( "UUID", "" );

		Comprobante comprobante = null;
		InputStream in = new FileInputStream( archivoXML );
		// LAOP - Detect and exclude a UTF-8 BOM
		InputStream inBOM = new BOMInputStream( in );
		try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
			ByteStreams.copy( inBOM, baos );
			byte[] data = baos.toByteArray();
			switch ( FacturaUtils.getVersion( data ) ) {
				case "3.3":
					try ( ByteArrayInputStream bais = new ByteArrayInputStream( data ) ) {
						comprobante = new Comprobante( CFDv33.newComprobante( bais ) );
					}
				break;
				case "4.0":
					try ( ByteArrayInputStream bais = new ByteArrayInputStream( data ) ) {
						comprobante = new Comprobante( CFDv40.newComprobante( bais ) );
					}
				break;
				default:
					throw new UnsupportedVersionException( "La versión  no es soportada en esta librería" );
			}
		}

		insertaFactura( comprobante );

		params.put( "UUID", comprobante.getUUID() );

		return ejecutaReporte( params, archivoXML );

	}

	private static void printQuery( Comprobante comprobante ) {
		System.out.println( "Ejecutando query" );
		System.out.println( queryInsertEnc );

		System.out.print( String.format( "[%s],", comprobante.getUUID() ) );
		System.out.print( String.format( "[%s],", comprobante.getNoCertificado() ) );
		System.out.print( String.format( "[%s],", comprobante.getLugarExpedicion() ) );
		System.out.print( String.format( "[%s],", CFDIUtils.toDate( comprobante.getFechaExpedicionMillis() ) ) );
		System.out.print( String.format( "[%s],", comprobante.getTipoComprobante() ) );
		System.out.print( String.format( "[%s],", comprobante.getFolioSerie() ) );
		System.out.print( String.format( "[%s],", comprobante.getRegimenEmisor() ) );
		System.out.print( String.format( "[%s],", comprobante.getNombreEmisor() ) );
		System.out.print( String.format( "[%s],", comprobante.getRFCReceptor() ) );
		System.out.print( String.format( "[%s],", comprobante.getNombreReceptor() ) );
		System.out.print( String.format( "[%s],", comprobante.getMoneda() ) );
		System.out.print( String.format( "[%s],", comprobante.getFormaDePago() ) );
		System.out.print( String.format( "[%s],", comprobante.getMetodoPago() ) );
		System.out.print( String.format( "[%s],", comprobante.getSubTotal() ) );
		System.out.print( String.format( "[%s],", comprobante.getTotalImpuestosTrasladados() ) );
		System.out.print( String.format( "[%s],", comprobante.getTotalRetenciones() ) );
		System.out.print( String.format( "[%s],", comprobante.getTotal() ) );
		System.out.print( String.format( "[%s],", comprobante.getSelloCFD() ) );
		System.out.print( String.format( "[%s],", comprobante.getSelloSAT() ) );
		System.out.print( String.format( "[%s],", comprobante.getCertificadoSAT() ) );
		System.out.print( String.format( "[%s],", CFDIUtils.toDate( comprobante.getFechaCertificadoMillis() ) ) );
		System.out.print( String.format( "[%s],", "1" ) );
		System.out.print( String.format( "[%s],", comprobante.getFolioFiscalOrig() ) + "\n" );
		System.out.print( String.format( "[%s],", comprobante.getRFCEmisor() ) + "\n" );

	}

	private int insertaFactura( Comprobante comprobante ) throws Exception {
		ResultSet rsIDFactura = null;
		int nFolioFactura = -1;

		try {

			psExisteFactura.clearParameters();
			psExisteFactura.setString( 1, comprobante.getUUID() );

			rsIDFactura = psExisteFactura.executeQuery();

			if ( rsIDFactura.next() )
				nFolioFactura = rsIDFactura.getInt( 1 );
			else {

				psInsertEnc.clearParameters();

				psInsertEnc.setString( 1, comprobante.getUUID() );
				psInsertEnc.setString( 2, comprobante.getNoCertificado() );
				psInsertEnc.setString( 3, comprobante.getLugarExpedicion() );
				psInsertEnc.setString( 4, CFDIUtils.toDate( comprobante.getFechaExpedicionMillis() ) );
				psInsertEnc.setString( 5, comprobante.getTipoComprobante() );
				psInsertEnc.setString( 6, comprobante.getFolioSerie() );
				psInsertEnc.setString( 7, comprobante.getRegimenEmisor() );
				psInsertEnc.setString( 8, comprobante.getNombreEmisor() );
				psInsertEnc.setString( 9, comprobante.getRFCReceptor() );
				psInsertEnc.setString( 10, comprobante.getNombreReceptor() );
				psInsertEnc.setString( 11, comprobante.getMoneda() );
				psInsertEnc.setString( 12, comprobante.getFormaDePago() );
				psInsertEnc.setString( 13, comprobante.getMetodoPago() );
				psInsertEnc.setBigDecimal( 14, comprobante.getSubTotal() );
				psInsertEnc.setBigDecimal( 15, comprobante.getTotalImpuestosTrasladados() );
				psInsertEnc.setBigDecimal( 16, comprobante.getTotalRetenciones() );
				psInsertEnc.setBigDecimal( 17, comprobante.getTotal() );
				psInsertEnc.setString( 18, comprobante.getSelloCFD() );
				psInsertEnc.setString( 19, comprobante.getSelloSAT() );
				psInsertEnc.setString( 20, comprobante.getCertificadoSAT() );
				psInsertEnc.setString( 21, CFDIUtils.toDate( comprobante.getFechaCertificadoMillis() ) );
				psInsertEnc.setString( 22, "1" );
				psInsertEnc.setString( 23, comprobante.getFolioFiscalOrig() );
				psInsertEnc.setString( 24, comprobante.getRFCEmisor() );
				CFDIMaskGenerator.printQuery( comprobante );
				psInsertEnc.executeUpdate();
				ResultSet generatedKeys = psInsertEnc.getGeneratedKeys();

				if ( generatedKeys.next() ) {
					nFolioFactura = generatedKeys.getInt( 1 );
					insertaDetalleFactura( comprobante, nFolioFactura );
				}

				generatedKeys.close();
				generatedKeys = null;

			}

			return nFolioFactura;

		} finally {
			CloseObject.closeObject( rsIDFactura );
		}
	}

	private void insertaDetalleFactura( Comprobante comprobante, int nFolioFactura ) throws Exception {
		Conceptos detalle = comprobante.getConceptos();

		for ( Concepto renglon : detalle.getConceptos() ) {
			psInsertDet.clearParameters();
			psInsertDet.setInt( 1, nFolioFactura );
			psInsertDet.setBigDecimal( 2, renglon.getCantidad() );
			psInsertDet.setString( 3, renglon.getClaveUnidad() );
			psInsertDet.setString( 4, "" );
			psInsertDet.setString( 5, renglon.getDescripcion() );
			psInsertDet.setBigDecimal( 6, renglon.getValorUnitario() );
			psInsertDet.setBigDecimal( 7, renglon.getImporte() );
			psInsertDet.executeUpdate();

		}
	}

	private String ejecutaReporte( Map<String, Object> params, File rutaXMLOrigen ) throws Exception {
		InputStream is = new FileInputStream( "/reports/reportFactura.jasper" );

		String path[] = rutaXMLOrigen.getAbsolutePath().split( "\\\\" );
		String fileNameOutput = path[path.length - 1];
		fileNameOutput = fileNameOutput.substring( 0, fileNameOutput.lastIndexOf( "." ) ) + ".pdf";

		String pathResult = this.pathSalida + "/" + fileNameOutput;

		OutputStream out = new FileOutputStream( pathResult );
		JasperRunManager.runReportToPdfStream( is, out, params, conn );
		out.flush();
		out.close();

		return pathResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {

		CloseObject.closeObject( psInsertEnc );
		CloseObject.closeObject( psInsertDet );
		CloseObject.closeObject( psExisteFactura );
		CloseObject.closeObject( psEFActivo );

	}

}
