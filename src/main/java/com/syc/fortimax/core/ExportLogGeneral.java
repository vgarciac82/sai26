package com.syc.fortimax.core;

public class ExportLogGeneral extends ExportLog {
	private int		cfdis				= 0;
	private String	cuentaPorPagar;
	private String	log;
	private int		oficiosDeTransito	= 0;
	private int		solicitudesDePago	= 0;

	/**
	 * @return the cfdis
	 */
	public int getCfdis() {
		return cfdis;
	}

	/**
	 * @return the cuentaPorPagar
	 */
	public String getCuentaPorPagar() {
		return cuentaPorPagar;
	}

	/**
	 * @return the log
	 */
	public String getLog() {
		return log;
	}

	/**
	 * @return the oficiosDeTransito
	 */
	public int getOficiosDeTransito() {
		return oficiosDeTransito;
	}

	/**
	 * @return the solicitudesDePago
	 */
	public int getSolicitudesDePago() {
		return solicitudesDePago;
	}

	/**
	 * @param cfdis
	 *            the cfdis to set
	 */
	public void setCfdis(int cfdis) {
		this.cfdis = cfdis;
	}

	/**
	 * @param cuentaPorPagar
	 *            the cuentaPorPagar to set
	 */
	public void setCuentaPorPagar(String cuentaPorPagar) {
		this.cuentaPorPagar = cuentaPorPagar;
	}

	/**
	 * @param log
	 *            the log to set
	 */
	public void setLog(String log) {
		this.log = log;
	}

	/**
	 * @param oficiosDeTransito
	 *            the oficiosDeTransito to set
	 */
	public void setOficiosDeTransito(int oficiosDeTransito) {
		this.oficiosDeTransito = oficiosDeTransito;
	}

	/**
	 * @param solicitudesDePago
	 *            the solicitudesDePago to set
	 */
	public void setSolicitudesDePago(int solicitudesDePago) {
		this.solicitudesDePago = solicitudesDePago;
	}

	public String toCSV() {
		return cuentaPorPagar + "," + solicitudesDePago + "," + cfdis + "," + oficiosDeTransito + "," + log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExportLogGeneral [cuentaPorPagar=" + cuentaPorPagar + ", solicitudesDePago=" + solicitudesDePago + ", cfdis=" + cfdis + ", oficiosDeTransito=" + oficiosDeTransito + ", log=" + log + "]";
	}

}
