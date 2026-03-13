package com.axtel.presupuesto;


public class AdecuacionCancelacion {

	private String	apiKey	= "#d$FZLS6Zm*yKI*LA0T*Jh5po#j4t73V0FN6s*$NxJ@V";
	private int		folio;
	private String	justificacion;
	
	public String getApiKey() {
		return apiKey;
	}
	
	public void setApiKey( String apiKey ) {
		this.apiKey = apiKey;
	}
	
	public int getFolio() {
		return folio;
	}
	
	public void setFolio( int folio ) {
		this.folio = folio;
	}
	
	public String getJustificacion() {
		return justificacion;
	}
	
	public void setJustificacion( String justificacion ) {
		this.justificacion = justificacion;
	}

	@Override
	public String toString() {
		return "AdecuacionCancelacion [apiKey=" + apiKey + ", folio=" + folio + ", justificacion=" + justificacion + "]";
	}
}
