package com.syc.ws.inventario;

public class RespuestaWS {
	private int		id;
	private String	estatus;
	private int		code;
	private int		idRePublicWork;
	private int		idRePublicWorkPartial;
	private String 	regimen;
	private String 	lastAppraisalDate;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the idRePublicWork
	 */
	public int getIdRePublicWork() {
		return idRePublicWork;
	}

	/**
	 * @param idRePublicWork
	 *            the idRePublicWork to set
	 */
	public void setIdRePublicWork(int idRePublicWork) {
		this.idRePublicWork = idRePublicWork;
	}

	/**
	 * @return the idRePublicWorkPartial
	 */
	public int getIdRePublicWorkPartial() {
		return idRePublicWorkPartial;
	}
		
	/**
	 * @param idRePublicWorkPartial
	 *            the idRePublicWorkPartial to set
	 */
	public void setIdRePublicWorkPartial(int idRePublicWorkPartial) {
		this.idRePublicWorkPartial = idRePublicWorkPartial;
	}
	
	public String getRegimen() {		
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}
	
	public String getLastAppraisalDate() {		
		return lastAppraisalDate;
	}

	public void setLastAppraisalDate(String lastAppraisalDate) {
		this.lastAppraisalDate = lastAppraisalDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RespuestaWS [id=" + id + ", estatus=" + estatus + ", code=" + code + ", idRePublicWork=" + idRePublicWork + ", idRePublicWorkPartial=" + idRePublicWorkPartial + "]";
	}

}
