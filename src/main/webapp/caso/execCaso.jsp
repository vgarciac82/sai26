<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*" %>
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
<script type="text/javascript" src="../js/gestion.js"></script>
<script for="datawork" event="onLoad" type="text/javascript">onLoad();</script>
<script type="text/javascript">
	var	docSaved = false;
	function onLoad() {
		gestion.requestXMLGestion();
	<%if (c.getCasoOperacion(0).getOperacion().getOnLoad() != null) {%>
		<%=c.getCasoOperacion(0).getOperacion().getOnLoad()%>
	<%}%>
	<%if (c.getCasoOperacion(0).getOperacion().getPostDisplay() != null) {%>
		<%=c.getCasoOperacion(0).getOperacion().getPostDisplay()%>
	<%}%>
	}
	function doSave() {
		try {
			if (!processData()) return;
			gestion.sendFormData(onSave);
		} catch (e) {
			if( e.name || e.message)
				alert("Numero: " + e.number + "\nNombre: " + e.name + "\nMensaje: " + e.message + "\nDescripcion: " + e.description);
			else
				alert(e);
		}
	}
	function onSave(xmlData) {
		docSaved = true;
		document.getElementById('pb_save').disabled = true;
		document.getElementById('pb_send').disabled = false;
	}
	function actionSend(httpRequest) {
		document.getElementById('frmCancel').submit();
	}
	function doSubmit() {
		try {
		if (!processData()) return false;
		gestion.sendFormData(actionSend);
		} catch (e) {
			alert("Numero: " + e.number + "\nNombre: " + e.name + "\nMensaje: " + e.message + "\nDescripci�n: " + e.description);
			return false;
		}
		return true;
	}<%String token = " = ";%>
	function processData() {
		try {
		<%=c.getCasoOperacion(0).getOperacion().getOnSubmit()%>
		execResponsable();
		execOperacion();
		gestion.setGestionData();
		} catch (e) {
			throw e;
		}
		return true;
	}
	function execResponsable() {
		var resp = document.getElementById("gstnTo");<%for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
		resp.value<%=token%>resp<%=i%>() + ";";<%token = " += \" \" + "; }%>
		document.getElementById("<%=GestionInterface.PRM_RESP%>").value = resp.value;
	}<%token = " = ";%>
	function execOperacion() {
		var oper = document.getElementById("gstnSubject");<%for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
		oper.value<%=token%>oper<%=i%>() + ";";<%token = " += \" \" + "; }%>
		document.getElementById("<%=GestionInterface.PRM_OPER%>").value = oper.value;
	}<%for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
	function resp<%=i%>() {
		<%=c.getCasoOperacion(0).getOperacion().getOperacionSgte(i).getResponsable()%>
	}<%	} for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
	function oper<%=i%>() {
		<%=c.getCasoOperacion(0).getOperacion().getOperacionSgte(i).getOperacion()%>
	}<%}%>
	function validaGabinete() {
		if ((<%=c.getIdGabinete()%> == -1) && (docSaved === false)) {
			window.alert("No existe el gabinete para este Caso\n\n1. Capture los datos del documento\n2. Y posteriormente de click en el bot�n Guardar");
			return false;
		}
		return true;
	}
</script>
<title>Caso en Ejecuci&oacute;n</title>
</head>
<body scroll="no">
<table width="100%" height="100%">
	<tr>
		<td height="0.25%">
			<table>
				<tr>
					<td>
						<table>
							<tr>
								<td align="right"><strong>Operaci&oacute;n:</strong></td>
								<td><%=c.getTipoCaso().getDescripcion()%>&nbsp;-&nbsp;<%=c.getCasoOperacion(0).getOperacion().getDescripcion()%></td>
							</tr>
							<tr>
								<td align="right"><strong>De:</strong></td>
								<td><%=c.getCasoOperacion(0).getResponsable()%></td>
							</tr>
						</table>
					</td>
					<td>
						<table>
							<tr>
								<td align="right"><strong>Enviado:</strong></td>
								<td><%=Util.getFechaHoraActual("America/Mexico", c.getCasoOperacion(0).getFechaInicio())%></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td height="99%">
			<table width="100%" height="100%">
				<tr>
					<td height="100%"><iframe id="datawork" name="datawork" frameborder="0" height="100%" width="100%" src="../<%=c.getCasoOperacion(0).getOperacion().getPlantilla()%>"></iframe>
				    <!-- iframe id="datawork" name="datawork" frameborder="0" height="100%" width="100%" src="../< %=c.getTipoCaso().getTipoCasoArchivo("html").getURL()% >"></iframe--></td>
				</tr>
				<tr>
					<td height="5%" align="right">
						<table>
							<tr>
								<td>
									<form action="../tofortimax" method="post" target="frtimx" onSubmit="return validaGabinete()">
										<input type="submit" id="pb_frtimx" class="button" value="FortImax">
									</form>								</td>
								<td>
									<input type="button" id="pb_wizard1" class="button" value="Digitalizar" onClick="doWizard(1)">								</td>
								<td>
									<input type="button" id="pb_wizard2" class="button" value="Adicionar documento" onClick="doWizard(2)">								</td>
								<td>
									<input type="button" id="pb_save" class="button" value="Guardar" onClick="doSave()">								</td>
								<td>
									<form id="frmCancel" action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_OPEN_INBOX%>" method="post">
										<input type="submit" id="pb_cancel" class="button" value="Cancelar">
									</form>								</td>
							</tr>
						</table>					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td height="0.5%">
			<form id="frmSend" action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_ADVANCE_CASE%>" method="post" onSubmit="doSubmit()">
				<input type="hidden" name="<%=GestionInterface.PRM_RESP%>">
				<input type="hidden" name="<%=GestionInterface.PRM_OPER%>">
				<table width="100%">
					<tr>
						<td>
							<table>
								<tr>
									<td><strong>De:</strong></td>
									<td><%=c.getCasoOperacion(0).getResponsable()%></td>
								</tr>
								<tr>
									<td><strong>Para:</strong></td>
									<td><input id="gstnTo" type="text" size="80" value="" disabled>
									</td>
								</tr>
								<tr>
									<td><strong>Operaci&oacute;n:</strong></td>
									<td><input id="gstnSubject" type="text" size="80" value="" disabled>
									</td>
								</tr>
							</table>
						</td>
						<td align="right" valign="bottom">
							<table width="100%" align="right">
								<tr><td align="right">
							<input type="submit" id="pb_send" class="button" value="Enviar" disabled>
							</td></tr></table>
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
</body>
</html>
