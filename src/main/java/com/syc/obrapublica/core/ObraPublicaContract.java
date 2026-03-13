package com.syc.obrapublica.core;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class ObraPublicaContract implements Serializable {
	
	private static final long serialVersionUID = -7740203864511747270L;
    private byte _typeDocument =0;
    private byte _typeAction=0;
    private byte _applyAccountingEngine=0;
	private int _idApartadoHeader=0;
	private int _idPreCompromisoHeader=0;
	private int _idCompromisoHeader=0;
	// IRD 20140901 se incluye cancelación de convenios
	private int _idConvModHeader=0;
	// IRD 20140912 se incluye cancelación de precompromisos de proinpro
	private int _idProinproHeader=0;
	private String _folioSAI=null;
	private String cDocumentoHaplicado=null;
	private String cIdContrato=null;
	public int getidProinproHeader() {
		return _idProinproHeader;
	}

	public void setidProinproHeader(int _idProinproHeader) {
		this._idProinproHeader = _idProinproHeader;
	}
	private String cConvenioEnCaptura="";
	public String getcDocumentoHaplicado() {
		return cDocumentoHaplicado;
	}

	public void setcDocumentoHaplicado(String cDocumentoHaplicado) {
		this.cDocumentoHaplicado = cDocumentoHaplicado;
	}
	private String _message="";
	private byte _whereIsContract=-1;
	public List<String> listEvent = new ArrayList<String>();
	

	public ObraPublicaContract() {

		super();
	}
	
	public int getidConvModHeader() {
		return _idConvModHeader;
	}

	public void setidConvModHeader(int _idConvModHeader) {
		this._idConvModHeader = _idConvModHeader;
	}

	public int getIdApartadoHeader() {

		return _idApartadoHeader;
	}

	public String getcConvenioEnCaptura() {
		return cConvenioEnCaptura;
	}

	public void setcConvenioEnCaptura(String cConvenioEnCaptura) {
		this.cConvenioEnCaptura = cConvenioEnCaptura;
	}

	public void setIdApartadoHeader(int id) {

		this._idApartadoHeader = id;
	}
	public int getIdCompHeader() {

		return _idCompromisoHeader;
	}

	public void setIdCompHeader(int id) {

		this._idCompromisoHeader = id;
	}
	
	public String getFolioSAI() {

		return _folioSAI;
	}

	public void setFolioSAI(String folio) {

		this._folioSAI = folio;
	}
	public int getIdPreCompromisoHeader() {

		return _idPreCompromisoHeader;
	}

	public void setIdPreCompromisoHeader(int id) {

		this._idPreCompromisoHeader = id;
	}

	public int getIdCompromisoHeader() {

		return _idCompromisoHeader;
	}

	public void setIdCompromisoHeader(int id) {

		this._idCompromisoHeader = id;
	}
	public byte getTypeDocument()
	{
	   return _typeDocument;	
	}
	public void setTypeDocument(byte value)
	{
	  this._typeDocument=value;
	}

	public byte getTypeAction()
	{
		return _typeAction;
	}
	public void setTypeAction(byte value)
	{
	   this._typeAction=value;
	}
	public byte getApplyAccountingEngine()
	{
	   return _applyAccountingEngine;
	}
	public void setApplyAccountigEngine(byte value)
	{
		this._applyAccountingEngine = value;
	}
	
	public String getMessage()
	{
	  return _message;
	}
	
	public void setMessage(String value)
	{
	  this._message = value;
	}
	
	public byte getWhereIsContract()
	{
		return _whereIsContract;
	}
	public void setWhereIsContract(byte value)
	{
	   this._whereIsContract=value;
	}
	public String getcIdContrato() {
		return cIdContrato;
	}	
	public void setcIdContrato( String cIdContrato ) {
		this.cIdContrato = cIdContrato;
	}
}
