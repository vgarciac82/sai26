<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
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
    <title>Configuraci&oacute;n de parametros fechas procedimiento</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link type="text/css" href="../css/gestion.css" rel="stylesheet">
	<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
</head>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script>
$(document).ready(function() {
	//Obtiene el parametro que indica si se realiza la verificacion o no
	$("#cParametro").val("mEnviarMailFechasProcedimiento");
	queryFormPost("consultaParametrosSistema", {async : false});
	queryInnerDivPost("mCatalogoFechasConfiguracion", {async : false});
	$("#cValorTemp").val($("#cValor").val());
	if($("#cValor").val() == 1){
		document.getElementById("mEnviarMailFechasProcedimiento").checked = true;
	}
	$("#guardar").button();
	//obtiene el parametro que indica la hora en que se realiza la verificacion
	$("#cParametro").val("mHoraRevisarFechasVencidas");
	queryFormPost("consultaParametrosSistema", {async : false});
	$("#mHoraRevisarFechasVencidas").val($("#cValor").val());
});
function guardarCambiosParametrosFechaProcedimiento(){
	var objeto;
	var inputCatalogoFechasProcedimiento = $('input','#divCatalogoFechasProcedimiento');
	
	try{
		for(var i=0;i<inputCatalogoFechasProcedimiento.length;i++){
			if(inputCatalogoFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
				if(inputCatalogoFechasProcedimiento[i].id.indexOf("cFechaProcedimiento") >= 0){
					if(inputCatalogoFechasProcedimiento[i].value == ""){
						alert("Falta insertar el n\xFAmero de d\xEDas en alguna de las fechas");
						return;
					}
				}
			}
		}
		if(confirm("¿Estas seguro de guardar los cambios?")){
			if($("#mHoraRevisarFechasVencidas").val() != "" && parseInt($("#mHoraRevisarFechasVencidas").val()) >= 0){
				//Guarda cambios en la hora de verificacion
				$("#cParametro").val("mHoraRevisarFechasVencidas");
				$("#cValor").val($("#mHoraRevisarFechasVencidas").val());
				queryFormPost("actualizaParametros", {async : false});
				//Guarda cambios en el indicador de la verificacion
				$("#cParametro").val("mEnviarMailFechasProcedimiento");
				if(document.getElementById("mEnviarMailFechasProcedimiento").checked){
					if($("#cValorTemp").val() == 0){
						$("#cValor").val("1");
						$("#cValorTemp").val($("#cValor").val());
						queryFormPost("actualizaParametros", {async : false});
						document.getElementById("formulario").submit();
					}
				}
				else{
					if($("#cValorTemp").val() == 1){
						$("#cValor").val("0");
						$("#cValorTemp").val($("#cValor").val());
						queryFormPost("actualizaParametros", {async : false});
						document.getElementById("formulario").submit();
					}
				}
				for(var i=0;i<inputCatalogoFechasProcedimiento.length;i++){
					if(inputCatalogoFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputCatalogoFechasProcedimiento[i].id.indexOf("cFechaProcedimiento") >= 0){
							$("#nIdFecha").val(inputCatalogoFechasProcedimiento[i].id.toString().replace("cFechaProcedimiento",""));
							$("#cDiasAviso").val(inputCatalogoFechasProcedimiento[i].value);
							queryFormPost("mUpdateDiasAvisoFechaProcedimiento", {async : false});
						}
					}
				}
				alert("Se guardaron los datos correctamente");
			}
			else{
				alert("Debe introducir la hora de verificacion");
			}
		}
	}
	catch(e){
		alert("Ocurrio un error al actualizar los datos");
	}
}
</script>  
<body>
<center>
<form>
<input type="hidden" name="nIdFecha" id="nIdFecha">
<input type="hidden" name="cDiasAviso" id="cDiasAviso">
<table>
	<tr>
		<td width="100">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<div id="divCatalogoFechasProcedimiento">
			</div>
		</td>
	</tr>
	<tr>
		<td width="250" align="right">
			Hora de Verificaci&oacute;n (0-23 Hrs.):
		</td>
		<td>
			<input type="text" name="mHoraRevisarFechasVencidas" id="mHoraRevisarFechasVencidas" size="2" class="form-control">
		</td>
	</tr>
	<tr>
		<td align="right">
			Activar:
		</td>
		<td>
			<input type="hidden" name="cParametro" id="cParametro">
			<input type="hidden" name="cValor" id="cValor">
			<input type="checkbox" name="mEnviarMailFechasProcedimiento" id="mEnviarMailFechasProcedimiento">
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
						<input type="button" name="guardar" id="guardar" onClick="guardarCambiosParametrosFechaProcedimiento();" value="Guardar" class="btn btn-secondary"/>	
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
<form action="../servlet/EnviaMailFechaProcedimiento" name="formulario" id="formulario" method="post" target="content-iframe-notificacion">
	<input type="hidden" name="cValorTemp" id="cValorTemp">
</form>
<iframe name="content-iframe-notificacion" marginwidth="0" marginheight="0" align="top" frameborder="0"></iframe>
</center>    
</body>
</html>
