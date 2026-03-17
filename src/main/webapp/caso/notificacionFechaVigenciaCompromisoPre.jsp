<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.io.*"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Configuraci&oacute;n de parametros fecha vigencia compromiso presupuestal</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link type="text/css" href="../css/gestion.css" rel="stylesheet">
	<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
	
</head>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-1.2.6.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script>
$(document).ready(function() {
	//Obtiene el parametro que indica si se realiza la verificacion o no
	//$("#cParametro").val("mEnviarMailFechaVigenciaCP");
	//queryFormPost("consultaParametrosSistema", {async : false});
	//$("#cValorTemp").val($("#cValor").val());
	//if($("#cValor").val() == 1){
	//	document.getElementById("mEnviarMailFechaVigenciaCP").checked = true;
	//}
	//obtiene el parametro que indica la hora en que se realiza la verificacion
	$("#cParametro").val("mHoraRevisarFechaVigenciaCP");
	queryFormPost("consultaParametrosSistema", {async : false});
	$("#mHoraRevisarFechaVigenciaCP").val($("#cValor").val());
	//obtiene el parametro que indica los dias de vencimiento de las fechas de vigencia de CP
	$("#cParametro").val("mDiasVigenciaCP");
	queryFormPost("consultaParametrosSistema", {async : false});
	$("#mDiasVigenciaCP").val($("#cValor").val());
	$("#guardar").button();
});
function guardarCambiosParametrosFechaProcedimiento(){
	var objeto;
	
	try{
		if($("#mHoraRevisarFechaVigenciaCP").val() != "" && parseInt($("#mHoraRevisarFechaVigenciaCP").val()) >= 0){
			if($("#mDiasVigenciaCP").val() != "" && parseInt($("#mDiasVigenciaCP").val()) >= 0){
				if(confirm("¿Estas seguro de guardar los cambios?")){
					//Guarda cambios de los dias de vigencia
					$("#cParametro").val("mDiasVigenciaCP");
					$("#cValor").val($("#mDiasVigenciaCP").val());
					queryFormPost("actualizaParametros", {async : false});
					//Guarda cambios en la hora de verificacion
					$("#cParametro").val("mHoraRevisarFechaVigenciaCP");
					$("#cValor").val($("#mHoraRevisarFechaVigenciaCP").val());
					queryFormPost("actualizaParametros", {async : false});
					//Guarda cambios en el indicador de la verificacion
					
					/*
					$("#cParametro").val("mEnviarMailFechaVigenciaCP");
					if(document.getElementById("mEnviarMailFechaVigenciaCP").checked){
						//if($("#cValorTemp").val() == 0){
							$("#cValor").val("1");
							$("#cValorTemp").val($("#cValor").val());
							queryFormPost("actualizaParametros", {async : false});
							document.getElementById("formulario").submit();
						//}
					}
					else{
						//if($("#cValorTemp").val() == 1){
							$("#cValor").val("0");
							$("#cValorTemp").val($("#cValor").val());
							queryFormPost("actualizaParametros", {async : false});
							document.getElementById("formulario").submit();
						//}
					}
					*/
					alert("Se guardaron los datos correctamente.");
				}
			}
			else{
				alert("Debe introducir los d\xEDas de vencimiento de las fechas.");
			}
		}
		else{
			alert("Debe introducir la hora de verificacion.");
		}
	}
	catch(e){
		alert("Ocurrio un error al actualizar los datos.");
	}
}
</script>  
<body>
<center>
<form>
<table>
	<tr>
		<td width="100">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td width="250" align="right">
			D&iacute;as de Vigencia:
		</td>
		<td>
			<input type="text" name="mDiasVigenciaCP" id="mDiasVigenciaCP" class="form-control" size="2" style="text-align:right;">
		</td>
	</tr>
	<tr>
		<td align="right">
			Hora de Verificaci&oacute;n (0-23 Hrs.):
		</td>
		<td>
			<input type="text" name="mHoraRevisarFechaVigenciaCP" id="mHoraRevisarFechaVigenciaCP" size="2" style="text-align:right;" class="form-control">
		</td>
	</tr>
	<tr>
	<!--
		<td align="right">
			Activar:
		</td>
	-->	
		<td>
			<input type="hidden" name="cParametro" id="cParametro">
			<input type="hidden" name="cValor" id="cValor">
			<!-- <input type="checkbox" name="mEnviarMailFechaVigenciaCP" id="mEnviarMailFechaVigenciaCP"> -->
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<table>
				<tr>
					<td>
						<input type="button" name="guardar" id="guardar" onClick="guardarCambiosParametrosFechaProcedimiento();" value ="Guardar" class="btn btn-secondary btn-sm"/>	
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
<form action="../servlet/EnviaMailFechaVigenciaCP" name="formulario" id="formulario" method="post" target="content-iframe-notificacion">
	<input type="hidden" name="cValorTemp" id="cValorTemp">
</form>
<iframe name="content-iframe-notificacion" marginwidth="0" marginheight="0" align="top" frameborder="0"></iframe>
</center>    
</body>
</html>
