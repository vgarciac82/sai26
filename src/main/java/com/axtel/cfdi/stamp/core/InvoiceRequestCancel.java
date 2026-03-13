package com.axtel.cfdi.stamp.core;

public class InvoiceRequestCancel {

	private String motivoDeCancelacion;

	private String rfcReceptor;

	private String uuid;

	private String uuidReemplazo;

	public String getMotivoDeCancelacion() {
		return motivoDeCancelacion;
	}

	public String getRfcReceptor() {
		return rfcReceptor;
	}

	public String getUuid() {
		return uuid;
	}

	public String getUuidReemplazo() {
		return uuidReemplazo;
	}

	public void setMotivoDeCancelacion(String motivoDeCancelacion) {
		this.motivoDeCancelacion = motivoDeCancelacion;
	}

	public void setRfcReceptor(String rfcReceptor) {
		this.rfcReceptor = rfcReceptor;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setUuidReemplazo(String uuidReemplazo) {
		this.uuidReemplazo = uuidReemplazo;
	}

	@Override
	public String toString() {
		return "InvoiceRequestCancel [uuid=" + uuid + ", rfcReceptor=" + rfcReceptor + ", motivoDeCancelacion="
				+ motivoDeCancelacion + ", uuidReemplazo=" + uuidReemplazo + "]";
	}
}
