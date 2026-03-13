package com.syc.solicitudviaticos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;

public class NotificacionSolicitudViaticos {

	public class AgendaNotificacion {

		private String		destino;
		private int			dias;
		private Date		fechaFin;
		private Date		fechaInicio;
		private String		motivo;
		private BigDecimal	total	= new BigDecimal(0.00d);

		/**
		 * @return the destino
		 */
		public String getDestino() {
			return destino;
		}

		/**
		 * @return the dias
		 */
		public int getDias() {
			return dias;
		}

		/**
		 * @return the fechaFin
		 */
		public Date getFechaFin() {
			return fechaFin;
		}

		/**
		 * @return the fechaInicio
		 */
		public Date getFechaInicio() {
			return fechaInicio;
		}

		/**
		 * @return the motivo
		 */
		public String getMotivo() {
			return motivo;
		}

		/**
		 * @return the total
		 */
		public BigDecimal getTotal() {
			return total;
		}

		/**
		 * @param destino
		 *            the destino to set
		 */
		public void setDestino(String destino) {
			this.destino = destino;
		}

		/**
		 * @param dias
		 *            the dias to set
		 */
		public void setDias(int dias) {
			this.dias = dias;
		}

		/**
		 * @param fechaFin
		 *            the fechaFin to set
		 */
		public void setFechaFin(Date fechaFin) {
			this.fechaFin = fechaFin;
		}

		/**
		 * @param fechaInicio
		 *            the fechaInicio to set
		 */
		public void setFechaInicio(Date fechaInicio) {
			this.fechaInicio = fechaInicio;
		}

		/**
		 * @param motivo
		 *            the motivo to set
		 */
		public void setMotivo(String motivo) {
			this.motivo = motivo;
		}

		/**
		 * @param total
		 *            the total to set
		 */
		public void setTotal(BigDecimal total) {
			this.total = total;
		}

	}

	private static final Logger			log	= Logger.getLogger(NotificacionSolicitudViaticos.class);
	private List<AgendaNotificacion>	agenda;
	private String						autorizadorIDPuesto;
	private String						autorizadorNombre;
	private String						autorizadorNumEmpleado;
	private String						autorizadorPuesto;
	private String						autorizadorCorreo;
	private String						firma;
	private String						link;

	private String						solicitanteIDPuesto;
	private String						solicitanteNombre;
	private String						solicitanteNumEmpleado;
	private String						solicitantePuesto;
	private String						solicitanteCorreo;
	private int							totalDias;

	private int							idRenglonSolicitante;
	private int							idRenglonAutorizador;

	private BigDecimal					totalViatico;

	private int							autoriza;
	private String						motivoRechazo;
	private int							folioSolicitudViaticos;
	private int							folioSNP;

	/**
	 * @return the folioSolicitudViaticos
	 */
	public int getFolioSolicitudViaticos() {
		return folioSolicitudViaticos;
	}

	/**
	 * @param folioSolicitudViaticos
	 *            the folioSolicitudViaticos to set
	 */
	public void setFolioSolicitudViaticos(int folioSolicitudViaticos) {
		this.folioSolicitudViaticos = folioSolicitudViaticos;
	}

	/**
	 * @return the autoriza
	 */
	public int getAutoriza() {
		return autoriza;
	}

	/**
	 * @param autoriza
	 *            the autoriza to set
	 */
	public void setAutoriza(int autoriza) {
		this.autoriza = autoriza;
	}

	/**
	 * @return the motivoRechazo
	 */
	public String getMotivoRechazo() {
		return motivoRechazo;
	}

	/**
	 * @param motivoRechazo
	 *            the motivoRechazo to set
	 */
	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}

	/**
	 * @return the agenda
	 */
	public List<AgendaNotificacion> getAgenda() {
		return agenda;
	}

	/**
	 * @return the autorizadorIDPuesto
	 */
	public String getAutorizadorIDPuesto() {
		return autorizadorIDPuesto;
	}

	/**
	 * @return the autorizadorNombre
	 */
	public String getAutorizadorNombre() {
		return autorizadorNombre;
	}

	/**
	 * @return the autorizadorNumEmpleado
	 */
	public String getAutorizadorNumEmpleado() {
		return autorizadorNumEmpleado;
	}

	/**
	 * @return the autorizadorPuesto
	 */
	public String getAutorizadorPuesto() {
		return autorizadorPuesto;
	}

	/**
	 * @return the firma
	 */
	public String getFirma() {
		return firma;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @return the solicitanteIDPuesto
	 */
	public String getSolicitanteIDPuesto() {
		return solicitanteIDPuesto;
	}

	/**
	 * @return the solicitanteNombre
	 */
	public String getSolicitanteNombre() {
		return solicitanteNombre;
	}

	/**
	 * @return the solicitanteNumEmpleado
	 */
	public String getSolicitanteNumEmpleado() {
		return solicitanteNumEmpleado;
	}

	/**
	 * @return the solicitantePuesto
	 */
	public String getSolicitantePuesto() {
		return solicitantePuesto;
	}

	/**
	 * @return the totalDias
	 */
	public int getTotalDias() {

		totalDias = 0;

		for (Iterator<AgendaNotificacion> i = getAgenda().iterator(); i.hasNext();) {
			AgendaNotificacion agendaDia = i.next();
			log.debug("Sumando dias de viaticos: " + agendaDia.getDias());
			totalDias = totalDias + agendaDia.getDias();
		}

		return totalDias;
	}

	/**
	 * @return the totalViatico
	 */
	public BigDecimal getTotalViatico() {

		totalViatico = new BigDecimal(0.00d);
		totalViatico.setScale(2, RoundingMode.HALF_UP);

		for (Iterator<AgendaNotificacion> i = getAgenda().iterator(); i.hasNext();) {
			AgendaNotificacion agendaDia = i.next();
			log.debug("Sumando monto de viatico: " + agendaDia.getTotal());
			totalViatico = totalViatico.add(agendaDia.getTotal());
		}

		return totalViatico;
	}

	/**
	 * @param agenda
	 *            the agenda to set
	 */
	public void setAgenda(List<AgendaNotificacion> agenda) {
		this.agenda = agenda;
	}

	/**
	 * @param autorizadorIDPuesto
	 *            the autorizadorIDPuesto to set
	 */
	public void setAutorizadorIDPuesto(String autorizadorIDPuesto) {
		this.autorizadorIDPuesto = autorizadorIDPuesto;
	}

	/**
	 * @param autorizadorNombre
	 *            the autorizadorNombre to set
	 */
	public void setAutorizadorNombre(String autorizadorNombre) {
		this.autorizadorNombre = autorizadorNombre;
	}

	/**
	 * @param autorizadorNumEmpleado
	 *            the autorizadorNumEmpleado to set
	 */
	public void setAutorizadorNumEmpleado(String autorizadorNumEmpleado) {
		this.autorizadorNumEmpleado = autorizadorNumEmpleado;
	}

	/**
	 * @param autorizadorPuesto
	 *            the autorizadorPuesto to set
	 */
	public void setAutorizadorPuesto(String autorizadorPuesto) {
		this.autorizadorPuesto = autorizadorPuesto;
	}

	/**
	 * @param firma
	 *            the firma to set
	 */
	public void setFirma(String firma) {
		this.firma = firma;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @param solicitanteIDPuesto
	 *            the solicitanteIDPuesto to set
	 */
	public void setSolicitanteIDPuesto(String solicitanteIDPuesto) {
		this.solicitanteIDPuesto = solicitanteIDPuesto;
	}

	/**
	 * @param solicitanteNombre
	 *            the solicitanteNombre to set
	 */
	public void setSolicitanteNombre(String solicitanteNombre) {
		this.solicitanteNombre = solicitanteNombre;
	}

	/**
	 * @param solicitanteNumEmpleado
	 *            the solicitanteNumEmpleado to set
	 */
	public void setSolicitanteNumEmpleado(String solicitanteNumEmpleado) {
		this.solicitanteNumEmpleado = solicitanteNumEmpleado;
	}

	/**
	 * @param solicitantePuesto
	 *            the solicitantePuesto to set
	 */
	public void setSolicitantePuesto(String solicitantePuesto) {
		this.solicitantePuesto = solicitantePuesto;
	}

	/**
	 * @param totalDias
	 *            the totalDias to set
	 */
	public void setTotalDias(int totalDias) {
		this.totalDias = totalDias;
	}

	/**
	 * @param totalViatico
	 *            the totalViatico to set
	 */
	public void setTotalViatico(BigDecimal totalViatico) {
		this.totalViatico = totalViatico;
	}

	public NotificacionSolicitudViaticos() {
		super();
		this.setAgenda(new ArrayList<NotificacionSolicitudViaticos.AgendaNotificacion>());
	}

	/**
	 * @return the autorizadorCorreo
	 */
	public String getAutorizadorCorreo() {
		return autorizadorCorreo;
	}

	/**
	 * @param autorizadorCorreo
	 *            the autorizadorCorreo to set
	 */
	public void setAutorizadorCorreo(String autorizadorCorreo) {
		this.autorizadorCorreo = autorizadorCorreo;
	}

	/**
	 * @return the solicitanteCorreo
	 */
	public String getSolicitanteCorreo() {
		return solicitanteCorreo;
	}

	/**
	 * @param solicitanteCorreo
	 *            the solicitanteCorreo to set
	 */
	public void setSolicitanteCorreo(String solicitanteCorreo) {
		this.solicitanteCorreo = solicitanteCorreo;
	}

	public String generaHTMLNotificacion(int folioSolicitud) throws Exception {
		ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
		String urlAutorizacion = cabl.getSystemSetting("URL_SAI") + "/viaticos/IngresoAutorizacion";//?token=" + URLEncoder.encode(generaAccessoAutToken(folioSolicitud) );

		String mailBody = "<html>";
		mailBody += "\n\t<head>";
		mailBody += "\n\t<meta charset=\"UTF-8\">";
		mailBody += "\n\t<style type=\"text/css\">";
		mailBody += "\n\tbody {";
		mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
		mailBody += "\n\t\t	font-size: 12px;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable {";
		mailBody += "\n\t\tfont-size: 12px;";
		mailBody += "\n\t\tcolor: #333333;";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tborder-collapse: collapse;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable th {";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tpadding: 8px;";
		mailBody += "\n\t\tborder-style: solid;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tbackground-color: #dedede;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable td {";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tpadding: 8px;";
		mailBody += "\n\t\tborder-style: solid;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tbackground-color: #ffffff;";
		mailBody += "\n\t}";
		mailBody += "\n\t</style>";
		mailBody += "</head>";
		mailBody += "\n\t<body>";
		mailBody += "\n\t\t<form id=\"FormViaticos\" name=\"FormViaticos\" >";
		mailBody += "	<b>" + getAutorizadorNombre() + "</b>";
		mailBody += "	<br>";
		mailBody += "	<b>" + getAutorizadorPuesto() + "</b>";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Se hace de su conocimiento que el empleado <b>" + getSolicitanteNombre() + "</b> con el cargo de <b>" + getSolicitantePuesto() + "</b> solicita de su autorización de la comisión con la siguiente agenda:";
		mailBody += "	</p>";

		mailBody += "	<table>";
		mailBody += "		<thead>";
		mailBody += "			<tr>";
		mailBody += "				<th>Destino</th>";
		mailBody += "				<th>Motivo</th>";
		mailBody += "				<th>Fecha Inicio</th>";
		mailBody += "				<th>Fecha Fin</th>";
		mailBody += "				<th>Dias</th>";
		mailBody += "				<th>Total</th>";
		mailBody += "			</tr>";
		mailBody += "		</thead>";
		mailBody += "		<tbody>";

		for (AgendaNotificacion agenda : getAgenda()) {
			mailBody += "<tr>";
			mailBody += "\n<td>" + agenda.getDestino() + "</td>";
			mailBody += "\n<td>" + agenda.getMotivo() + "</td>";
			mailBody += "\n<td>" + Util.dateToString(agenda.getFechaInicio(), "dd/MM/yyyy") + "</td>";
			mailBody += "\n<td>" + Util.dateToString(agenda.getFechaFin(), "dd/MM/yyyy") + "</td>";
			mailBody += "\n<td>" + agenda.getDias() + "</td>";
			mailBody += "\n<td>" + Util.formatNumber(agenda.getTotal()) + "</td>";
			mailBody += "</tr>";
		}

		mailBody += "			<tr>";
		mailBody += "				<td colspan=\"3\" style=\"border:0\">&nbsp;</td>";
		mailBody += "				<td>Total</td>";
		mailBody += "				<td>" + getTotalDias() + "</td>";
		mailBody += "				<td>" + Util.formatNumber(getTotalViatico()) + "</td>";
		mailBody += "			</tr>";
		mailBody += "		</tbody>";
		mailBody += "	</table>";
		mailBody += "	<br />";
		mailBody += "	<br />";
		mailBody += "\n<b>Para autorizar esta solicitud por favor de click <a href=\"" + urlAutorizacion +  generaAccessoAutToken(folioSolicitud) +  "\" > aquí </a>.</b>";
		mailBody += "	<br />";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
		mailBody += "	</p>";
		mailBody += "	</form>";
		mailBody += "</body>";
		mailBody += "</html>";

		return mailBody;
	}

	private String generaAccessoAutToken(int folioSolicitud) throws Exception {

		StringBuffer parametrosReales = null;

		try {

			/*
			 * Concatena los parametros. El separador sera el caracter | (pipe)
			 */
			parametrosReales = new StringBuffer("?");
			parametrosReales.append("usuario=").append(getAutorizadorNumEmpleado());
			parametrosReales.append("&");
			parametrosReales.append("folio=").append(String.valueOf(folioSolicitud));
			parametrosReales.append("&");
			parametrosReales.append("autorizacion=").append(String.valueOf(true));
			parametrosReales.append("&");
			parametrosReales.append("IdRenglonAutorizador=").append( getIdRenglonAutorizador() );
			
			log.debug("Cadena generada: " + parametrosReales);
			return parametrosReales.toString();
		} catch (Exception e) {
			throw new Exception("FMX-SEC-10001 Error generando Token", e);
		}
	}

	public static Map<String, String> consumeToken(HttpServletRequest request) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		Enumeration<?> paramNames = request.getParameterNames();

		while ( paramNames.hasMoreElements() ) {
			String paramName = (String)paramNames.nextElement();
			param.put( paramName, request.getParameter(paramName) );
		}
		
		return param;

	}

	/**
	 * @return the idRenglon
	 */
	public int getIdRenglonSolicitante() {
		return idRenglonSolicitante;
	}

	/**
	 * @param idRenglon
	 *            the idRenglon to set
	 */
	public void setIdRenglonSolicitante(int idRenglonSolicitante) {
		this.idRenglonSolicitante = idRenglonSolicitante;
	}

	/**
	 * @return the idRenglonAutorizador
	 */
	public int getIdRenglonAutorizador() {
		return idRenglonAutorizador;
	}

	/**
	 * @param idRenglonAutorizador
	 *            the idRenglonAutorizador to set
	 */
	public void setIdRenglonAutorizador(int idRenglonAutorizador) {
		this.idRenglonAutorizador = idRenglonAutorizador;
	}

	public String generaHTMLAutorizacion() {
		String mailBody = "<html>";
		mailBody += "<head>";
		mailBody += "<meta charset=\"UTF-8\">";
		mailBody += "<style type=\"text/css\">";
		mailBody += "body {";
		mailBody += "	font-family: verdana, arial, sans-serif;";
		mailBody += "	font-size: 12px;";
		mailBody += "}";

		mailBody += "table {";
		mailBody += "	font-size: 12px;";
		mailBody += "	color: #333333;";
		mailBody += "	border-width: 1px;";
		mailBody += "	border-color: #666666;";
		mailBody += "	border-collapse: collapse;";
		mailBody += "}";

		mailBody += "table th {";
		mailBody += "	border-width: 1px;";
		mailBody += "	padding: 8px;";
		mailBody += "	border-style: solid;";
		mailBody += "	border-color: #666666;";
		mailBody += "	background-color: #dedede;";
		mailBody += "}";

		mailBody += "table td {";
		mailBody += "	border-width: 1px;";
		mailBody += "	padding: 8px;";
		mailBody += "	border-style: solid;";
		mailBody += "	border-color: #666666;";
		mailBody += "	background-color: #ffffff;";
		mailBody += "}";

		mailBody += "</style>";
		mailBody += "</head>";
		mailBody += "<body>";
		mailBody += "	<b>" + getSolicitanteNombre() + "</b>";
		mailBody += "	<br>";
		mailBody += "	<b>" + getSolicitantePuesto() + "</b>";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Se hace de su conocimiento que su solicitud de viaticos con folio " + getFolioSolicitudViaticos() + " <b>ha sido autorizdo </b> con la siguiente agenda:";
		mailBody += "	</p>";

		mailBody += "	<table>";
		mailBody += "		<thead>";
		mailBody += "			<tr>";
		mailBody += "				<th>Destino</th>";
		mailBody += "				<th>Motivo</th>";
		mailBody += "				<th>Fecha Inicio</th>";
		mailBody += "				<th>Fecha Fin</th>";
		mailBody += "				<th>Dias</th>";
		mailBody += "				<th>Total</th>";
		mailBody += "			</tr>";
		mailBody += "		</thead>";
		mailBody += "		<tbody>";

		for (AgendaNotificacion agenda : getAgenda()) {
			mailBody += "<tr>";
			mailBody += "\n<td>" + agenda.getDestino() + "</td>";
			mailBody += "\n<td>" + agenda.getMotivo() + "</td>";
			mailBody += "\n<td>" + Util.dateToString(agenda.getFechaInicio(), "dd/MM/yyyy") + "</td>";
			mailBody += "\n<td>" + Util.dateToString(agenda.getFechaFin(), "dd/MM/yyyy") + "</td>";
			mailBody += "\n<td>" + agenda.getDias() + "</td>";
			mailBody += "\n<td>" + Util.formatNumber(agenda.getTotal()) + "</td>";
			mailBody += "</tr>";
		}

		mailBody += "			<tr>";
		mailBody += "				<td colspan=\"3\" style=\"border:0\">&nbsp;</td>";
		mailBody += "				<td>Total</td>";
		mailBody += "				<td>" + getTotalDias() + "</td>";
		mailBody += "				<td>" + Util.formatNumber(getTotalViatico()) + "</td>";
		mailBody += "			</tr>";
		mailBody += "		</tbody>";
		mailBody += "	</table>";
		mailBody += "	<br />";
		mailBody += "	<br />";
		mailBody += "<b> Adjunto encontrara la solicitud no presupuestal que debera presentar para continuar con su tramite.</b> Para consulta, los datos de los tramites realizados son:";
		mailBody += "	<br />";
		mailBody += "	<table>";
		mailBody += "	<tr>";
		mailBody += "	<td> Tramite";
		mailBody += "	</td>";
		mailBody += "	<td> Folio";
		mailBody += "	</td>";
		mailBody += "	</tr>";

		mailBody += "	<tr>";
		mailBody += "	<td>Solicitud de Viaticos.";
		mailBody += "	</td>";
		mailBody += "	<td> " + getFolioSolicitudViaticos();
		mailBody += "	</td>";
		mailBody += "	</tr>";

		mailBody += "	<tr>";
		mailBody += "	<td>Solicitud no Presupuestal (CAJA)";
		mailBody += "	</td>";
		mailBody += "	<td> " + getFolioSNP();
		mailBody += "	</td>";
		mailBody += "	</tr>";

		mailBody += "	</table>";
		mailBody += "	<p>";
		mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
		mailBody += "	</p>";
		mailBody += "</body>";
		mailBody += "</html>";

		return mailBody;
	}

	/**
	 * @return the folioSNP
	 */
	public int getFolioSNP() {
		return folioSNP;
	}

	/**
	 * @param folioSNP
	 *            the folioSNP to set
	 */
	public void setFolioSNP(int folioSNP) {
		this.folioSNP = folioSNP;
	}

	public String generaHTMLRechazo(int folioSolicitudViaticos2) {
		
		String mailBody = "<html>";
		mailBody += "<head>";
		mailBody += "<meta charset=\"UTF-8\">";
		mailBody += "<style type=\"text/css\">";
		mailBody += "body {";
		mailBody += "	font-family: verdana, arial, sans-serif;";
		mailBody += "	font-size: 12px;";
		mailBody += "}";

		mailBody += "</style>";
		mailBody += "</head>";
		mailBody += "<body>";
		mailBody += "	<b>" + getSolicitanteNombre() + "</b>";
		mailBody += "	<br>";
		mailBody += "	<b>" + getSolicitantePuesto() + "</b>";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Se hace de su conocimiento que su solicitud de viaticos con folio " + getFolioSolicitudViaticos() + " <b>ha sido rechazado </b> por el autorizador: " + getAutorizadorNombre() ;
		if( !StringUtils.isBlank( getMotivoRechazo() ) ){
			mailBody += "<br>";
			mailBody += "El motivo del rechazo es el siguiente:<br>";
			mailBody += "<i>" + getMotivoRechazo() + "</i>";
			mailBody += "<br>";
		}
			
		mailBody += "	</p>";
		
		
		mailBody += "	<p>";
		mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
		mailBody += "	</p>";
		mailBody += "</body>";
		mailBody += "</html>";

		return mailBody;
	}

}
