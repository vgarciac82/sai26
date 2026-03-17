<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	if( session == null){
		response.sendRedirect( "../index.jsp" );
		return;
	}
	
	Usuario u = (Usuario)session.getAttribute( GestionInterface.ATT_USER );
	if( u == null){
		response.sendRedirect( "../index.jsp" );
		return;
	}
	boolean error = false;
	
	String msgError = (String)session.getAttribute( "MSG_ERROR" );
	error =  !StringUtils.isEmpty( msgError );
	
	String msgSuccess = (String)session.getAttribute( "MSG_SUCCESS" );
	session.removeAttribute( "MSG_ERROR" );
	session.removeAttribute( "MSG_SUCCESS" );
	
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Descarga de CFDI</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<style type="text/css">
.readOnly {
	background-color: #CCCCCC;
	color: #000000;
}
</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>	
	<script type="text/javascript" src="../js/catalogo/general.js"></script>	
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/SolicitaDescargaREP.js"></script>

<script type="text/javascript">
	var msg = "<%=StringUtils.trimToEmpty( msgError ) + StringUtils.trimToEmpty( msgSuccess ) %>";
	$(document).ready(function() {
		init();
	});
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form action="../cfdi/RegistraDescarga" method="post" id="frmReqCFDI" enctype="multipart/form-data">
		<div id="container" class="container">
			<h1>Solicitud de descarga de CFDI</h1>
			<table align="center">
				<tr>
					<td align="right">
						Fecha Inicio
					</td>
					<td align="left">
						<input type="text" size="15" readonly="readonly" id="fInicio" name="fInicio" class="fecha readOnly">
					</td>
					<td align="right">
						Fecha Fin
					</td>
					<td align="left">
						<input type="text" size="15" readonly="readonly"  id="fFin" name="fFin" class="fecha readOnly">
					</td>
				</tr>
				<tr>
					<td colspan="2" align="right">Archivo .key</td>
					<td colspan="2" align="left"> <input type="file" id="keyFile" name="keyFile" size="50"/></td> 
				</tr>
				<tr>
					<td colspan="2" align="right">Archivo .cer</td>
					<td colspan="2" align="left"> 
						<input type="file" id="cerFile" name="cerFile" size="50"/>
					</td> 
				</tr>
				<tr>
					<td colspan="2" align="right">Password</td>
					<td colspan="2" align="left"> 
						<input type="password" id="pwdKey" name="pwdKey" size=30/>
					</td> 
				</tr>
				<tr>
					<td align="center" colspan="4">
						<input type="button" value="Solicitar" id="btnSolicitar">
						
					</td>
				</tr>
			</table>
			<div >
				<fieldset>
					<legend>Solicitudes Registradas.</legend>
					<table id="dt_solicitudes" class="display">
						<thead>
							<tr>
								<th width="30%">UUID Solicitud</th>
								<th width="4%">Solicitud</th>
								<th width="4%">Inicio</th>
								<th width="4%">Fin</th>
								<th width="10%">Receptor</th>
								<th width="28%">Resultado</th>
								<th width="10%">Estaus</th>
								<th width="7%">CFDIs</th>
								<th width="3%">&nbsp;</th>
							</tr>
						</thead>
						<tbody></tbody>						
					</table>
				</fieldset>
			</div>
		</div>
		<div id="bitacoraDiv">
		<fieldset><legend>Resultado de Operacion</legend>
			<table>
				<tr>
					<td align="left">
						Respuesta del sistema:		
					</td>
				</tr>
				<tr>
					<td>
						<textarea rows="6" cols="80" readonly="readonly"><%=StringUtils.trimToEmpty( msgError ) + StringUtils.trimToEmpty( msgSuccess )%></textarea>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	</form>
	
</body>

</html>