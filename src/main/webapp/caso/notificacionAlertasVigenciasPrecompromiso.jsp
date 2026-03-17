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
    <title>Configuraci&oacute;n de parametros fecha vigencia precompromiso presupuestal</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link type="text/css" href="../css/gestion.css" rel="stylesheet">
</head>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
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
	$("#cParametro").val("mHoraRevisarFechaVigenciaPrecompromiso");
	queryFormPost("consultaParametrosSistema", {async : false});
	$("#mHoraRevisarFechaVigenciaPrecom").val($("#cValor").val());
	//obtiene el parametro que indica los dias de vencimiento de las fechas de vigencia de CP
	$("#cParametro").val("mDiasVigenciaPrecompromiso");
	queryFormPost("consultaParametrosSistema", {async : false});
	$("#mDiasVigenciaPrecom").val($("#cValor").val());
	
	//obtiene el parametro que indica los dias de vencimiento de las fechas de vigencia de CP
	$("#cParametro").val("mDiasVigenciaPrecompromisoAlertas");
	queryFormPost("consultaParametrosSistema", {async : false});
	$("#mDiasVigenciaPrecomAlertas").val($("#cValor").val());
	
	
	
});
function guardarCambiosParametrosFechasPrecom(){
	//var objeto;
	
	try{
		if($("#mHoraRevisarFechaVigenciaPrecom").val() != ""   && parseInt($("#mHoraRevisarFechaVigenciaPrecom").val(), 10) >=0 ){
			if($("#mDiasVigenciaPrecom").val() != "" && parseInt($("#mDiasVigenciaPrecom").val()) >= 0  && $("#mDiasVigenciaPrecomAlertas").val() != "" && parseInt($("#mDiasVigenciaPrecomAlertas").val()) >= 0){
				if(confirm("¿Estas seguro de guardar los cambios?")){
					//Guarda cambios de los dias de vigencia
					$("#cParametro").val("mDiasVigenciaPrecompromiso");
					$("#cValor").val($("#mDiasVigenciaPrecom").val());
					queryFormPost("actualizaParametros", {async : false});
					//Guarda cambios en la hora de verificacion
					$("#cParametro").val("mHoraRevisarFechaVigenciaPrecompromiso");
					$("#cValor").val($("#mHoraRevisarFechaVigenciaPrecom").val());
					queryFormPost("actualizaParametros", {async : false});
					//Guarda cambios en el indicador de la verificacion
					
					
					
					//Guarda cambios de los dias de vigencia alertas
					$("#cParametro").val("mDiasVigenciaPrecompromisoAlertas");
					$("#cValor").val($("#mDiasVigenciaPrecomAlertas").val());
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
	<td>Envío de Correos</td>
	</tr>
	
	<tr>
		<td width="100">
			&nbsp;
		</td>
	</tr>
	
	<tr>
	 <td width="250" align="right">
			D&iacute;as Previos para env&iacute;o de correos:
		</td>
		<td>
			<input type="text" name="mDiasVigenciaPrecom" id="mDiasVigenciaPrecom" size="2" style="text-align:right;">
		</td>
	</tr>
	<tr>
		<td align="right">
			Hora de Verificaci&oacute;n (0-23 Hrs.):
		</td>
		<td>
			<input type="text" name="mHoraRevisarFechaVigenciaPrecom" id="mHoraRevisarFechaVigenciaPrecom" size="2" style="text-align:right;">
		</td>
	</tr>
	
	
		<tr>
		<td width="100">
			&nbsp;
		</td>
	</tr>
	
	<tr>
	<td>Envío de Alertas</td>
	</tr>
	
	<tr>
		<td width="100">
			&nbsp;
		</td>
	</tr>
	
	<tr>
	 <td width="250" align="right">
			D&iacute;as Previos para env&iacute;o de alertas:
		</td>
		<td>
			<input type="text" name="mDiasVigenciaPrecomAlertas" id="mDiasVigenciaPrecomAlertas" size="2" style="text-align:right;">
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
						<button name="guardar" id="guardar" onClick="guardarCambiosParametrosFechasPrecom();">Guardar</button>	
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
</center>    
</body>
</html>
