package com.axtel.cfdi.stamp.service;


import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.cfdi.CFDI;
import com.axtel.cfdi.service.CFDIService;
import com.axtel.cfdi.stamp.core.DigitalSignature;
import com.axtel.cfdi.stamp.core.InvoiceReportPrinter;
import com.axtel.cfdi.stamp.core.InvoiceRequest;
import com.axtel.cfdi.stamp.core.InvoiceRespond;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;


public class StampCFDIService extends DataSourceManager {

	private static final Logger		log	= LogManager.getLogger( StampCFDIService.class );

	private CFDIService				cfdiService;
	private DigitalSignature		digitalSignature;

	private InvoiceReportPrinter	invoicePrinter;

	public StampCFDIService( String jniName, File reporthPath ) {
		super.init( jniName );
		cfdiService = new CFDIService( jniName, reporthPath );
		initStampService();
	}

	/*
	 * public InvoiceRespond cancelInvoice( InvoiceRequestCancel request )
	 * throws Exception {
	 * 
	 * Connection conn = null; try {
	 * 
	 * conn = getConnection(); CFDI invoice = new CFDI();
	 * 
	 * invoice.setUUID( request.getUuid() ); invoice.setRFCEmisor(
	 * StampCFDIService.RFC_EMISOR ); invoice.setMotivo(
	 * request.getMotivoDeCancelacion() );
	 * 
	 * if ( !StringUtils.isBlank( request.getUuidReemplazo() ) )
	 * invoice.setUuid_sustituto( request.getUuidReemplazo() );
	 * 
	 * InvoiceRespond acuse = invoice.cancelaCFDI( conn, invoice );
	 * invoice.saveCanceledInvoice( conn, acuse, request.getUuid() );
	 * conn.commit(); return acuse; } catch ( Exception e ) { log.error(
	 * e.toString(), e ); try { if ( conn != null ) conn.rollback(); } catch (
	 * Exception e2 ) { log.warn( "Problemas en rollback: " + e2, e2 ); } throw
	 * e; } finally { CloseObject.closeObject( conn ); } }
	 */
	private DigitalSignature initSignature( Connection conn ) {
		try {
			return DigitalSignatureService.loadDigitalSignature( conn );
		} catch ( Exception e ) {
			log.error( e.toString(), e );
			throw new RuntimeException( e.toString(), e.getCause() );
		}
	}

	public void initStampService() {
		Connection conn = null;
		try {
			conn = getConnection();
			initStampService( conn );

		} catch ( Exception e ) {
			log.error( e.toString(), e );
			throw new RuntimeException( e.toString(), e.getCause() );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public void initStampService( Connection conn ) {
		digitalSignature = initSignature( conn );
	}

	public Path printReport( Connection conn, InvoiceRequest request ) {
		return invoicePrinter.printReport( conn, request );
	}

	public void setInvoiceReportPrinter( InvoiceReportPrinter invoicePrinter ) {
		this.invoicePrinter = invoicePrinter;

	}

	private InvoiceRespond stamp( Integer idInvoice ) throws Exception {
		InvoiceRespond response = new InvoiceRespond();

		try {

			CFDI invoice = cfdiService.autoriza( idInvoice );
			invoice = cfdiService.stampInvoice( invoice, digitalSignature );
			invoice = cfdiService.generateInvoiceFiles( invoice );

			cfdiService.sendInvoice( invoice );

			invoice = cfdiService.finaliza( invoice );

			return response;
		} catch ( Exception e ) {
			throw e;
		}

	}

	public List<InvoiceRespond> stamp( List<String> invoiceIDList ) {
		List<InvoiceRespond> response = new ArrayList<InvoiceRespond>();
		for ( String idInvoice : invoiceIDList ) {
			InvoiceRespond result = new InvoiceRespond();
			try {

				result = stamp( Integer.parseInt( idInvoice ) );
			} catch ( Exception e ) {
				log.error( e.toString(), e );
				result.setEstatus( "500" );
				result.setMensaje( "Error while trying to stamp invoice: " + e );
			}
		}
		return response;
	}

}
