<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*" %>
<%	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="../css/gestion.css" rel="stylesheet">
<title>Caso en Ejecuci&oacute;n</title>
</head>

<body>
<table>
	<tr>
		<td><strong>Caso</strong></td>
		<td align="right">
			<form method="post" action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_OPEN_INBOX%>">
				<input type="submit" name="Submit" value="Regresar" />
			</form>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table border="1">
				<tr>
					<th>Id Caso</th>
					<th>Folio</th>
					<th>Id Tipo Caso</th>
					<th>Fecha de Inicio</th>
					<th>Tiempo L&iacute;mite</th>
					<th>Id Gabinete</th>
				</tr>
				<tr>
					<td><%=c.getIdCaso()%></td>
					<td><%=c.getFolio()%></td>
					<td><%=c.getIdTC()%></td>
					<td><%=c.getFechaInicio()%></td>
					<td><%=c.getTiempoLimite()%>&nbsp;hrs.</td>
					<td><%=c.getIdGabinete()%></td>
				</tr>
						</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<table>
				<tr>
					<td colspan="2"><strong>Tipo de Caso</strong></td>
				</tr>
				<tr>
					<td colspan="2">
						<table border="1">
							<tr>
								<th>Id Tipo Caso</th>
								<th>Descripci&oacute;n</th>
								<th>Gaveta</th>
								<th>Tiempo L&iacute;mite</th>
								<th>Quien Puede Inicar</th>
							</tr>
							<tr>
								<td><%=c.getTipoCaso().getIdTC()%></td>
								<td><%=c.getTipoCaso().getDescripcion()%></td>
								<td><%=c.getTipoCaso().getGavetaAsociada()%></td>
								<td><%=c.getTipoCaso().getTiempoLimite()%>&nbsp;hrs.</td>
								<td><%=c.getTipoCaso().getWhoCanInit()%></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2"><strong>Caso Operaci&oacute;n</strong></td>
	</tr>
<%	for (Iterator iter = c.getCasoOperacion().iterator(); iter.hasNext();) {
	CasoOperacion co = (CasoOperacion) iter.next(); %>
	<tr>
		<td colspan="2">
			<table border="1">
				<tr>
					<th>Id Caso</th>
					<th>Id Caso Operaci&oacute;n</th>
					<th>Id Tipo Caso</th>
					<th>Id Operaci&oacute;n</th>
					<th>Fecha de Inicio</th>
					<th>Tiempo L&iacute;mite</th>
					<th>Responsable</th>
				</tr>
				<tr>
					<td><%=co.getIdCaso()%></td>
					<td><%=co.getIdCasoOper()%></td>
					<td><%=co.getIdTC()%></td>
					<td><%=co.getIdOperacion()%></td>
					<td><%=co.getFechaInicio()%></td>
					<td><%=co.getTiempoLimite()%>&nbsp;min.</td>
					<td><%=co.getResponsable()%></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<table>
				<tr>
					<td colspan="2"><strong>Operaci&oacute;n</strong></td>
				</tr>
				<tr>
					<td colspan="2">
						<table border="1">
							<tr>
								<th>Id Tipo Caso</th>
								<th>Id Operaci&oacute;n</th>
								<th>N&uacute;mero</th>
								<th>Nombre</th>
								<th>Responsable</th>
								<th>Descripci&oacute;n</th>
								<th>Plantilla</th>
								<th>Tiempo L&iacute;mite</th>
								<th>Alarma</th>
								<th>Post Display</th>
								<th>Post Submit</th>
								<th>On Load</th>
								<th>On Submit</th>
							</tr>
							<tr>
								<td><%=co.getOperacion().getIdTC()%></td>
								<td><%=co.getOperacion().getIdOperacion()%></td>
								<td><%=co.getOperacion().getNumero()%></td>
								<td><%=co.getOperacion().getNombre()%></td>
								<td><%=co.getOperacion().getResponsable()%></td>
								<td><%=co.getOperacion().getDescripcion()%></td>
								<td><%=co.getOperacion().getPlantilla()%></td>
								<td><%=co.getOperacion().getTiempoLimite()%></td>
								<td><%=co.getOperacion().getAlarma()%></td>
								<td><%=co.getOperacion().getPostDisplay()%></td>
								<td><%=co.getOperacion().getPostSubmit()%></td>
								<td><%=co.getOperacion().getOnLoad()%></td>
								<td><%=co.getOperacion().getOnSubmit()%></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<table>
							<tr>
								<td><strong>Operaci&oacute;n Siguiente</strong></td>
							</tr>
							<tr>
								<td>
									<table border="1">
										<tr>
											<th>Id Tipo Caso</th>
											<th>Id Operaci&oacute;n</th>
											<th>Id Operaci&oacute;n Siguiente</th>
											<th>Responsable</th>
											<th>Operaci&oacute;n</th>
										</tr>
<%	for (int i = 0; i < co.getOperacion().getOperacionSgte().size(); i++) {
		OperacionSiguiente os = co.getOperacion().getOperacionSgte(i); %>
										<tr>
											<td><%=os.getIdTC()%></td>
											<td><%=os.getIdOperacion()%></td>
											<td><%=os.getIdOperacionSigte()%></td>
											<td><%=os.getResponsable()%></td>
											<td><%=os.getOperacion()%></td>
										</tr>
<%	} %>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
<%	} %>
	<tr>
		<td colspan="2"><strong>Caso Datos / Tipo Caso Variables</strong></td>
	</tr>
	<tr>
		<td colspan="2">
			<table border="1">
				<tr>
					<th>Id Caso</th>
					<th>Id Caso Dato</th>
					<th>Id Tipo Caso</th>
					<th>Nombre</th>
					<th>Etiqueta</th>
					<th>Tipo</th>
					<th>Longitud</th>
					<th>Indice</th>
					<th>En Gaveta</th>
					<th>Valor</th>
				</tr>
<%	for (Iterator iter = c.getCasoDato().keySet().iterator(); iter.hasNext();) {
		CasoDato cd = c.getCasoDato((String) iter.next()); %>
				<tr>
					<td><%=cd.getIdCaso()%></td>
					<td><%=cd.getIdCD()%></td>
					<td><%=cd.getIdTC()%></td>
					<td><%=cd.getTipoCasoVariable().getNombre()%></td>
					<td><%=cd.getTipoCasoVariable().getEtiqueta()%></td>
					<td><%=Util.getTipoDato(cd.getTipoCasoVariable().getTipo())%></td>
					<td><%=cd.getTipoCasoVariable().getLongitud()%></td>
					<td><%=cd.getTipoCasoVariable().getIndice()%></td>
					<td><%=cd.getTipoCasoVariable().getEnGaveta()%></td>
					<td><%=cd.getValor()%></td>
				</tr>
<%	} %>
			</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>
</body>
</html>
