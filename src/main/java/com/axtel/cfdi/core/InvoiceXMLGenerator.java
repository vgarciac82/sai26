package com.axtel.cfdi.core;


import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.axtel.cfdi.CFDIDetalle;
import com.axtel.cfdi.CFDIEncabezado;
import com.axtel.web.utils.NumberToWordsConverter;


public class InvoiceXMLGenerator {

	private static final SimpleDateFormat	INVOICE_DATE_FORMAT	= new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
	private String							qrURL;

	public InvoiceXMLGenerator( String qrURL ) {
		this.qrURL = qrURL;
	}

	public String generateXmlFromCfdi( CFDIEncabezado encabezado, List<CFDIDetalle> detalles ) {
		StringBuilder xml = new StringBuilder( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );

		// Nodo raíz <Comprobante>
		xml.append( "<impresion:Comprobante " );
		xml.append( generateXMLGeneralInfo( encabezado ) );
		xml.append( ">\n" );

		// Sección del Emisor
		xml.append( generateXMLEmisor( encabezado ) );

		// Sección del Receptor
		xml.append( generateXMLReceptor( encabezado ) );

		// Sección de Conceptos
		xml.append( generateXMLConceptos( detalles ) );

		// Sección de Impuestos Generales
		xml.append( generateXMLImpuestos() );

		// Tipo de documento
		xml.append( "    <impresion:tipoDocumento>Factura</impresion:tipoDocumento>\n" );

		// Cierre del nodo raíz
		xml.append( "</impresion:Comprobante>" );

		return xml.toString();
	}

	private StringBuilder generateXMLGeneralInfo( CFDIEncabezado encabezado ) {
		String formattedDate = INVOICE_DATE_FORMAT.format( encabezado.getFecha() );
		StringBuilder xml = new StringBuilder();
		xml.append( "version=\"4.0\" " );
		xml.append( "folio=\"" ).append( encabezado.getFolio() ).append( "\" " );
		xml.append( "serie=\"" ).append( encabezado.getSerie().getSerie() ).append( "\" " );
		xml.append( "fecha=\"" ).append( formattedDate ).append( "\" " );
		xml.append( "formaDePago=\"" ).append( encabezado.getFormaPago().getDescripcion() ).append( "\" " );
		xml.append( "sello=\"" ).append( encabezado.getSello() ).append( "\" " );
		xml.append( "metodoDePago=\"" ).append( encabezado.getMetodoPago().getDescripcion() ).append( "\" " );
		xml.append( "noCertificado=\"" ).append( encabezado.getNoCertificado() ).append( "\" " );
		xml.append( "certificado=\"" ).append( encabezado.getCertificado() ).append( "\" " );

		xml.append( "condicionesDePago=\"" ).append( encabezado.getCondicionesDePago() ).append( "\" " );

		xml.append( "subTotal=\"" ).append( encabezado.getSubTotal() ).append( "\" " );
		xml.append( "descuento=\"" ).append( encabezado.getDescuento() ).append( "\" " );
		xml.append( "total=\"" ).append( encabezado.getTotal() ).append( "\" " );

		xml.append( "totalLetra=\"" ).append( NumberToWordsConverter.convertirCantidad( encabezado.getTotal() ) ).append( "\" " );

		xml.append( "Moneda=\"" ).append( encabezado.getMoneda().getDescripcion() ).append( "\" " );
		xml.append( "tipoDeComprobante=\"" ).append( encabezado.getTipoDeComprobante().getDescripcion() ).append( "\" " );
		xml.append( "TipoCambio=\"" ).append( encabezado.getTipoCambio() ).append( "\" " );

		xml.append( "cadenaOriginalComplemento=\"" ).append( encabezado.getCadenaOriginalComplemento() ).append( "\" " );
		xml.append( "folioFiscal=\"" ).append( encabezado.getUuid() ).append( "\" " );
		xml.append( "urlImagenCodigoQRC=\"" ).append( this.qrURL + "/invoice/generateQRCode?uuid=" + encabezado.getUuid() + "&rfcEmisor=" + encabezado.getEmisor().getRfc() + "&rfcReceptor=" + encabezado.getReceptor().getRfc() + "&total=" + encabezado.getTotal() + "&sello=" + encabezado.getSello().substring( encabezado.getSello().length() - 8 ) ).append( "\" " );
		xml.append( "noCertificadoSAT=\"" ).append( encabezado.getNoCertificadoSAT() ).append( "\" " );
		
		xml.append( "lugarExpedicion=\"" ).append( encabezado.getLugarExpedicion().getCodigoPostal() ).append( "\"" );
		
		xml.append( "numCtaPago=\"" ).append( StringUtils.trimToEmpty(  encabezado.getNumCtaPago() ) ).append( "\" " );
		xml.append( "rfcProveedorCertificacion=\"" ).append( StringUtils.trimToEmpty(  encabezado.getRfcProvCertif()  ) ).append( "\" " );
		xml.append( "xmlns:impresion=\"http://www.example.org/CFDPrinting\"" );
		return xml;
	}

	private StringBuilder generateXMLEmisor( CFDIEncabezado encabezado ) {
		StringBuilder xml = new StringBuilder();
		xml.append( "    <impresion:Emisor " );
		xml.append( "rfc=\"" ).append( encabezado.getEmisor().getRfc() ).append( "\" " );
		xml.append( "nombre=\"" ).append( encabezado.getEmisor().getNombre() ).append( "\" " );
		xml.append( "regimenFiscal=\"" ).append( encabezado.getRegimenFiscal().getRegimenFiscal() ).append( "\">\n" );
		xml.append( "        <impresion:DomicilioFiscal " );
		xml.append( "calle=\"" ).append( encabezado.getEmisor().getDomicilio().getCalle() ).append( "\" " );
		xml.append( "noExterior=\"" ).append( encabezado.getEmisor().getDomicilio().getNoExterior() ).append( "\" " );
		xml.append( "colonia=\"" ).append( encabezado.getEmisor().getDomicilio().getColonia() ).append( "\" " );
		xml.append( "municipio=\"" ).append( encabezado.getEmisor().getDomicilio().getMunicipio() ).append( "\" " );
		xml.append( "estado=\"" ).append( encabezado.getEmisor().getDomicilio().getEstado() ).append( "\" " );
		xml.append( "pais=\"" ).append( encabezado.getEmisor().getDomicilio().getPais() ).append( "\" " );
		xml.append( "codigoPostal=\"" ).append( encabezado.getEmisor().getDomicilio().getCodigoPostal() ).append( "\"/>\n" );
		xml.append( "    </impresion:Emisor>\n" );
		return xml;
	}

	private StringBuilder generateXMLReceptor( CFDIEncabezado encabezado ) {
		StringBuilder xml = new StringBuilder();
		xml.append( "    <impresion:Receptor " );
		xml.append( "rfc=\"" ).append( encabezado.getReceptor().getRfc() ).append( "\" " );
		xml.append( "nombre=\"" ).append( encabezado.getReceptor().getNombre() ).append( "\" " );
		xml.append( "usoCFDI=\"" ).append( encabezado.getUsoCFDI().getDescripcion() ).append( "\">\n" );
		xml.append( "        <impresion:Domicilio " );

		// xml.append( "calle=\"" ).append( encabezado.getReceptor().getCalle()
		// ).append( "\" " );
		// xml.append( "noExterior=\"" ).append(
		// encabezado.getReceptor().getNoExterior() ).append( "\" " );
		// xml.append( "colonia=\"" ).append(
		// encabezado.getReceptor().getColonia() ).append( "\" " );
		// xml.append( "localidad=\"" ).append(
		// encabezado.getReceptor().getLocalidad() ).append( "\" " );
		// xml.append( "municipio=\"" ).append(
		// encabezado.getReceptor().getMunicipio() ).append( "\" " );
		// xml.append( "estado=\"" ).append(
		// encabezado.getReceptor().getEstado() ).append( "\" " );
		// xml.append( "pais=\"" ).append( encabezado.getReceptor().getPais()
		// ).append( "\" " );
		xml.append( "calle=\"" ).append( "" ).append( "\" " );
		xml.append( "noExterior=\"" ).append( "" ).append( "\" " );
		xml.append( "colonia=\"" ).append( "" ).append( "\" " );
		xml.append( "localidad=\"" ).append( "" ).append( "\" " );
		xml.append( "municipio=\"" ).append( "" ).append( "\" " );
		xml.append( "estado=\"" ).append( "" ).append( "\" " );
		xml.append( "pais=\"" ).append( "" ).append( "\" " );

		xml.append( "codigoPostal=\"" ).append( encabezado.getReceptor().getDomicilioFiscal() ).append( "\"/>\n" );
		xml.append( "    </impresion:Receptor>\n" );
		return xml;
	}

	private StringBuilder generateXMLConceptos( List<CFDIDetalle> detalles ) {
		StringBuilder xml = new StringBuilder();
		xml.append( "    <impresion:Conceptos>\n" );
		for ( CFDIDetalle detalle : detalles ) {
			xml.append( "        <impresion:Concepto " );
			xml.append( "claveProdServ=\"" ).append( detalle.getClaveProdServ() ).append( "\" " );
			xml.append( "cantidad=\"" ).append( detalle.getCantidad() ).append( "\" " );
			xml.append( "claveUnidad=\"" ).append( detalle.getClaveUnidad() ).append( "\" " );
			xml.append( "unidad=\"" ).append( detalle.getUnidad() ).append( "\" " );
			xml.append( "descripcion=\"" ).append( detalle.getDescripcion() ).append( "\" " );
			xml.append( "valorUnitario=\"" ).append( detalle.getValorUnitario() ).append( "\" " );
			xml.append( "importe=\"" ).append( detalle.getImporte() ).append( "\" " );
			xml.append( "partida=\"" ).append( "1" ).append( "\">\n" );

			// Impuestos por concepto
			xml.append( "            <impresion:Impuestos>\n" );
			xml.append( "                <impresion:Impuesto " );
			xml.append( "Tipo=\"Traslado\" " );
			xml.append( "Base=\"" ).append( detalle.getImporte() ).append( "\" " );
			xml.append( "Impuesto=\"IVA\" " );
			xml.append( "TipoFactor=\"Exento\" " );
			xml.append( "Partida=\"" ).append( "1" ).append( "\"/>\n" );
			xml.append( "            </impresion:Impuestos>\n" );

			xml.append( "        </impresion:Concepto>\n" );
		}
		xml.append( "    </impresion:Conceptos>\n" );
		return xml;
	}

	private StringBuilder generateXMLImpuestos() {
		StringBuilder xml = new StringBuilder();
		xml.append( "    <impresion:Impuestos>\n" );
		xml.append( "        <impresion:ivaMonto>0</impresion:ivaMonto>\n" );
		xml.append( "    </impresion:Impuestos>\n" );
		return xml;
	}
}
