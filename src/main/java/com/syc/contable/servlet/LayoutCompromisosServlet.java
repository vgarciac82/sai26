package com.syc.contable.servlet;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;


/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         desarrollo gestion_conagua_sif México D.F. 23/01/2012
 *
 */
public class LayoutCompromisosServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1L;

	private static Logger		log					= Logger.getLogger( LayoutCompromisosServlet.class );

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		CompromisoBussinessLogic cmpbl = new CompromisoBussinessLogic( GestionInterface.ATT_CONEXION );
		// ServletOutputStream out = null;

		try {
			// recupera los campos caNoCompromisos de la lista de los que serán enviados
			String compromisos = ( request.getParameter( "sDataHCompromiso" ) != null ) ? request.getParameter( "sDataHCompromiso" ).trim() : "";
			int valor = compromisos.length() - 1;
			String compromisosQuery = compromisos.substring( 0, valor );

			// recupera los campos idContratos de la lista de los que serán enviados
			String contratos = ( request.getParameter( "sDataHContrato" ) != null ) ? request.getParameter( "sDataHContrato" ).trim() : "";
			int valor2 = contratos.length() - 1;
			String contratosQuery = contratos.substring( 0, valor2 );

			DateFormat fecha = new SimpleDateFormat( "yyyyMMddhhmmsss" );
			String sufijo = fecha.format( new Date( System.currentTimeMillis() ) );

			File filename = new File( System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "Compromisos"  + sufijo.trim() + ".csv");

			ArrayList<String> cvsDataSQL = cmpbl.buscaCompromisos( compromisosQuery, contratosQuery );

			BufferedWriter out = new BufferedWriter( new FileWriter( filename ) ); 
			StringBuffer archivoPago = new StringBuffer();
			for ( int i = 0; i < cvsDataSQL.size(); i++ ) {
				archivoPago.append( cvsDataSQL.get( i ) );
			}
			
			String outTextPago = archivoPago.toString();
			out.write( outTextPago );
			out.flush();
			out.close(); // fin de guarda pago

			ServletOutputStream outS = null;
			ByteArrayInputStream byteArrayInputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			try {
				response.setContentType( "text/csv" );
				String disposition = "attachment; fileName=" + filename.getName();
				response.setHeader( "Content-Disposition", disposition );
				outS = response.getOutputStream();
				
				byte[] blobData = outTextPago.getBytes(); 
				byteArrayInputStream = new ByteArrayInputStream( blobData );
				bufferedOutputStream = new BufferedOutputStream( outS );
				int length = blobData.length;
				response.setContentLength( length );
				byte[] buff = new byte [ ( 1024 * 1024 ) * 2];
				// now lets shove the data down
				int bytesRead;
				// Simple read/write loop.
				while ( -1 != ( bytesRead = byteArrayInputStream.read( buff, 0, buff.length ) ) ) {
					bufferedOutputStream.write( buff, 0, bytesRead );
				}
				out.close();
				
			} catch ( Exception e ) {
				System.err.println( e );
				throw e;
			} finally {
				if ( out != null )
					out.close();
				if ( byteArrayInputStream != null ) {
					byteArrayInputStream.close();
				}
				if ( bufferedOutputStream != null ) {
					bufferedOutputStream.close();
				}
			}

			filename.delete();

			cmpbl.estatusCompromiso( compromisosQuery );

		} catch ( Exception exc ) {
			log.error( exc );
			throw new ServletException( exc );
		} finally {
		}
	}

}
