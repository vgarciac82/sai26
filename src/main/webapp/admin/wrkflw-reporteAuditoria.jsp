<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bitacora</title>
<script src="../js/datepickercontrol.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/jquery-1.2.6.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />

<script type="text/javascript">

	function limpiarSesion()
	{
		window.location.href="wrkflw-reporteAuditoria.jsp?id=<%=request.getParameter("id")%>";//Esto recarga la pagina
	}
	
	function fnPrintTituloReporte() {
	
			var principal = parent.parent.document;
			var asunto    = document.datawork;
			
			//var nombre   = principal.getElementById('u_nombre').innerHTML.replace(/&nbsp;/g, "");
			var fechaIni = document.getElementById('DPC_fechaInicial').value;
			var fechaFin = document.getElementById('DPC_fechaFinal').value;
			var login    = document.getElementById('res_u_login').value;
			var modulo   = escape(document.getElementById('res_modulo').value);
			var accion   = document.getElementById('res_accion').value;
			//var area   = document.getElementById('idarea').value;
			
			var param = "id=<%=request.getParameter("id")%>" 
					   + "&modulo="      + modulo
					   + "&fechaini="  + fechaIni
					   + "&fechafin="  + fechaFin
					   + "&accion="    + accion
					   + "&login="     + login
					  // + "&area="     + area
					   + "&rn=ReporteAuditoria.jasper";
					   
			//alert("param=["+param+"]");		   
			
			//Esta jsp se creo con base en las jsp wrkflw-**********.jsp que estan en webcontent/admin
			//aqui habia un monton de codigo comentado que aparentemente era el cuerpo del reporte
			//pero parece que despues decidieron generarlo mejor en reporte_resultado.jsp
			//voy a seguir la misma manera de generar el resultado que en los reportes ya existentes

			//if(fechaIni=="" & fechaFin=="" & login=="" & accion=="" & modulo=="" & area=="")
			if(fechaIni=="" & fechaFin=="" & accion=="" & modulo=="")
			{
				alert("Debes capturar algun dato");
				return false;	
			} else {
				parent.frames['resultado'].location.href = "reporte_resultado.jsp?" + param;
				return true;	
			}
		}
</script>
<script type="text/javascript" >

		$(document).ready(
			function() 
			{
				$("input.AyudaSyC").subIniciaDlg();	
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
			}
		);
</script>		
</head>
<body background="../imagenes/steel_BG.gif" >
	<form id="form1" name="form1" method="post" target="formulario" action="./wrkflw-reporteAuditoria.jsp?select=u_login=">
	<div class="Contenido" style="height: 10%">
		<table class="TituloRutaCA">
			<tr>
				<td>
					<img src="../imagenes/iconos/reportes.png" alt="" width="16"
						height="16">
					<font color="#FFFFFF">
					<strong>
						Bitácora.
					</strong>
					</font>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>
		</table>
	</div>
		
		<table border="0" cellspacing="0" cellpadding="2">
		<br>
			<tr>
				<td>
					<input type="hidden" id="lbl_fecha" name="lbl_fecha" value="FECHA"/>Fecha
				</td>
				<td align="right">De:</td>
				<td width="25%">
				&nbsp;&nbsp;<input type="text" name="DPC_fechaInicial" id="DPC_fechaInicial" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20" tabindex="11"/>
				</td>
				<td align="right">A:</td>
				<td>
					<input type="text" name="DPC_fechaFinal"   id="DPC_fechaFinal"   datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20" tabindex="11"/>
				</td>
			</tr>

			<tr>
				<td>M&oacute;dulo</td>
				<td>&nbsp;</td>
				<td>&nbsp;&nbsp;<select name="res_modulo" id="res_modulo">
						<!-- luego cambiamos estos options por una llamada a la tabla cg_opcion -->
						<!-- s1 se agrega aqui una opcion tambien debera agregarse en los if's de deleteJson.jsp, updateJson.jsp, insertJson.jsp -->
						<option value=""></option>
						<option value="Acceso">Acceso</option>
						<option value="Usuarios">Usuarios</option>
						<option value="Usuario Grupos">Usuario Grupos</option>
						<option value="Usuario Roles">Usuario Roles</option>
						<option value="Usuario Propiedades">Usuario Propiedades</option>
						<option value="Puestos">Puestos</option>
						<option value="Areas">Areas</option>
						<option value="Grupos">Grupos</option>
						<option value="Roles">Roles</option>
						<option value="Roles Opciones">Roles Opciones</option>
						<option value="Parametros Sistema">Parámetros Sistema</option>
						<option value="Normatividad Estructura Programatica">Normatividad Estructura Programática</option>
						<option value="Normatividad InvercionAGastoCorriente">Normatividad Invercion A Gasto Corriente</option>
						<option value="Normatividad de Partidas Restringidas">Normatividad de Partidas Restringidas</option>
						<option value="Normatividad financiamiento, partida y objeto del gasto">Normatividad financiamiento, partida y objeto del gasto</option>
						<option value="Normatividad relacion Partida y Prog Presupuestal">Normatividad relación Partida y Prog Presupuestal</option>
					</select>
				</td>
				<td>Acci&oacute;n</td>
				<td><select name="res_accion" id="res_accion">
						<!-- luego cambiamos estos options por una llamada a la BD alomejor por un distinct de accion en imx_bitacora-->
						<option value=""></option>
						<option value="Login">Login</option>
						<option value="Logout">Logout</option>
						<option value="Agregar">Agregar</option>
						<option value="Borrar">Borrar</option>
						<option value="Actualizar">Actualizar</option>
						<!--<option value="Transferir">Transferir</option>-->
						<!--<option value="Depurar">Depurar</option>-->
						<!--<option value="Atencion">Atender</option>-->
					</select>
				</td>
			</tr>
<!-- 
			<tr>
				<td>Seleccionar &Aacute;rea</td>
				<td>&nbsp;</td>
				<td colspan="4">
					<input id="idarea"     name="idarea"     type="hidden" value="" size="5"  maxlength="10"/>
					<input id="areaEstruc" name="areaEstruc" type="text"   value="" size="53" maxlength="90" class="AyudaSyC  autoCompletaSyC"/>
					<input id="estruc"     name="estruc"     type="hidden" value="" size="5"  maxlength="10"/>
				</td>
			</tr>
-->
			<tr>
				<td>Usuario</td>
				<td>&nbsp;</td>
				<td colspan="3">
					<input id="res_u_login"  name="res_u_login"  type="hidden" size="15" class="protegido" disabled="true"/>
					<input id="res_u_nombre" name="res_u_nombre" type="text"   size="53" class="AyudaSyC autoCompletaSyC"/>
				</td>
			</tr> 
		</table>
 	<table align="center">
		<br>
		<tr>
			<td>
				<input type="button" value="Generar Reporte" onClick="return fnPrintTituloReporte();">
				<!--  input type="submit" name="button2" id="button2" value="Buscar" onclick="return fnValida();"/ -->
				<input type="button" value="Limpiar" onClick="limpiarSesion();"/>
			</td>
		</tr>
	</table>
	<p>&nbsp;</p>
	</form>
</body>
</html>