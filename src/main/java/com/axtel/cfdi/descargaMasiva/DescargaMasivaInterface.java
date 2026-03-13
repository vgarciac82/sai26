package com.axtel.cfdi.descargaMasiva;


public interface DescargaMasivaInterface {

	public final static String	URL_AUTENTICA					= "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/Autenticacion/Autenticacion.svc";
	public final static String	URL_AUTENTICA_ACTION			= "http://DescargaMasivaTerceros.gob.mx/IAutenticacion/Autentica";
	public final static String	URL_SOLICITUD					= "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
	public final static String	URL_SOLICITUD_ACTION			= "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescarga";
	public final static String	RFC								= "CNF010405EG1";
	public final static String	DATE_FORMAT						= "yyyy-MM-dd";
	public final static String	URL_VERIFICAR_SOLICITUD			= "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/VerificaSolicitudDescargaService.svc";
	public final static String	URL_VERIFICAR_SOLICITUD_ACTION	= "http://DescargaMasivaTerceros.sat.gob.mx/IVerificaSolicitudDescargaService/VerificaSolicitudDescarga";
	public final static String	URL_DESCARGAR_SOLICITUD			= "https://cfdidescargamasiva.clouda.sat.gob.mx/DescargaMasivaService.svc";
	public final static String	URL_DESCARGAR_SOLICITUD_ACTION	= "http://DescargaMasivaTerceros.sat.gob.mx/IDescargaMasivaTercerosService/Descargar";

}
