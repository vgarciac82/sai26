package com.syc.cfdi;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.syc.cfdi.v3332.Comprobante.Comprobante;


/**
 * Define los componentes de una factura.
 * 
 * @author Vicente Garcia Carrillo
 * 
 */
public class ComponentesFactura {

	/**
	 * Contenido de la factura
	 */
	private Comprobante		comprobante;

	/**
	 * Nombre del archivo PDF de la factura
	 */
	private String			pdfFile		= null;

	/**
	 * Ruta fisica del archivo PDF
	 */
	private String			pdfPathFile	= null;

	/**
	 * Nombre del archivo XML de la factura
	 */
	private String			xmlFile		= null;

	/**
	 * Ruta fisica del archivo XML
	 */
	private String			xmlPathFile	= null;
	
	private List<String>	errorLog	= new ArrayList<String>();

	public List<String> getErrorLog() {
		return errorLog;
	}

	public void setErrorLog( List<String> errorLog ) {
		this.errorLog = errorLog;
	}

	/**
	 * Indica si la factura cuenta con sus componentes XML y PDF
	 * 
	 * @return true si y solo se cuenta con ambas rutas, el XML y el PDF
	 */
	public boolean componentesCompletos() {
		return !StringUtils.isEmpty( getPdfFile() ) && !StringUtils.isEmpty( getXmlFile() );

	}

	/**
	 * @return the comprobante
	 */
	public Comprobante getComprobante() {
		return comprobante;
	}

	/**
	 * @return the pdfFile
	 */
	public String getPdfFile() {
		return pdfFile;
	}

	/**
	 * @return the pdfPathFile
	 */
	public String getPdfPathFile() {
		return pdfPathFile;
	}

	/**
	 * @return the xmlFile
	 */
	public String getXmlFile() {
		return xmlFile;
	}

	/**
	 * @return the xmlPathFile
	 */
	public String getXmlPathFile() {
		return xmlPathFile;
	}

	/**
	 * @param comprobante
	 *            the comprobante to set
	 */
	public void setComprobante( Comprobante comprobante ) {
		this.comprobante = comprobante;
	}

	/**
	 * @param pdfFile
	 *            the pdfFile to set
	 */
	public void setPdfFile( String pdfFile ) {
		this.pdfFile = pdfFile;
	}

	/**
	 * @param pdfPathFile
	 *            the pdfPathFile to set
	 */
	public void setPdfPathFile( String pdfPathFile ) {
		this.pdfPathFile = pdfPathFile;
	}

	/**
	 * @param xmlFile
	 *            the xmlFile to set
	 */
	public void setXmlFile( String xmlFile ) {
		this.xmlFile = xmlFile;
	}

	/**
	 * @param xmlPathFile
	 *            the xmlPathFile to set
	 */
	public void setXmlPathFile( String xmlPathFile ) {
		this.xmlPathFile = xmlPathFile;
	}
}
