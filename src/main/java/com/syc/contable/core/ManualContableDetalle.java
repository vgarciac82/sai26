package com.syc.contable.core;

public class ManualContableDetalle {
	private int		folioManualContable;
	private String	usuarioCarga;
	private int		idManual;
	private int		idVersion;

	public ManualContableDetalle() {
		super();
	}

	public ManualContableDetalle(int folioManualContable, String usuarioCarga, int idManual, int idVersion) {
		super();
		this.folioManualContable = folioManualContable;
		this.usuarioCarga = usuarioCarga;
		this.idManual = idManual;
		this.idVersion = idVersion;
	}

	public int getFolioManualContable() {
		return folioManualContable;
	}

	public int getIdManual() {
		return idManual;
	}
	
	public int getIdVersion() {
		return idVersion;
	}

	public String getUsuarioCarga() {
		return usuarioCarga;
	}

	public void setFolioManualContable(int folioManualContable) {
		this.folioManualContable = folioManualContable;
	}

	public void setIdManual(int idManual) {
		this.idManual = idManual;
	}
	
	public void setIdVersion(int idVersion) {
		this.idVersion = idVersion;
	}

	public void setUsuarioCarga(String usuarioCarga) {
		this.usuarioCarga = usuarioCarga;
	}

	@Override
	public String toString() {
		return "ManualContableDetalle [folioManualContable=" + folioManualContable + ", usuarioCarga=" + usuarioCarga + ", idManual=" + idManual + ", idVersion=" + idVersion + "]";
	}

}
