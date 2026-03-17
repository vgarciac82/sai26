<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<% String msg = (String) session.getAttribute("login.message"); %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Control de Gesti&oacute;n CG-Flow</title>
		<style type="text/css">
		<!--
		.campo {
			border: 1px solid #000000;
			background-color: #FFFFFF;
			font-family: Arial, Helvetica, sans-serif;
			color: #000000;
		}
		.boton {
			border: 1px solid #003366;
			background-color: #FFFFFF;
			font-family: Arial, Helvetica, sans-serif;
			font-size: 0.9em;
			color: #000000;
			cursor: hand;
		}
		td {
			font-family: Arial, Helvetica, sans-serif;
			font-size: .9em;
		}
		.titulo1 {
			border-bottom-width: thin;
			border-bottom-style: solid;
			border-bottom-color: #025C32;
			font-family: Verdana, Arial, Helvetica, sans-serif;
			font-size: 1.5em;
			color: #333333;
		}
		body {
			background-attachment: fixed;
			background-image: url(imagenes/logotipo-cgflow.jpg);
			background-repeat: no-repeat;
			background-position: right bottom;
		}
		-->
		</style>
		<script>
			function revisaCampos(){
				if(document.getElementById("login").value=="" 
					|| document.getElementById("password").value==""
					){
					alert("No pueden haber campos vacíos.");
					return false;
				}
				
				return true;
			} 	
		</script>
	</head>
	<body scroll="no" onload="document.getElementById('login').focus()">
		<font face="arial,helvetica" color="red" size="2"><b>
		NOTA IMPORTANTE: Este sistema es para uso exclusivo del Administrador de Fortimax y Control de Gesti&oacute;n o alguna persona
		designada por el. Cualquier otro acceso esta prohibido. 
		<br/>
		La IP que utiliza para acceder al sistema será registrada: <%=request.getRemoteAddr() %> / <%=request.getRemoteHost() %>
		<br/><br/>
		NOTA: Este sistema &uacute;nicamente puede ser utilizado con Microsoft Internet Explorer,<br/>
		si utiliza otro navegador, es posible que no pueda utilizar bien el sistema.<br/>
		</b>
		
		</font>
		<form action="gstnmngr/login.jsp" method="post" onsubmit="revisaCampos();">
			<p class="titulo1">
				<img src="imagenes/logotipo-cgflow.jpg" alt="CGFLow" width="200" height="54" align="absmiddle">
			</p>
			<p>
				<br>
			</p>
			<table align="center">
				<tr>
					<td>
						<strong>Usuario:</strong>
					</td>
					<td>
						<input name="login" type="text" id="login" size="16" maxlength="32" tabindex="1">
					</td>
				</tr>
				<tr>
					<td>
						<strong>Clave:</strong>
					</td>
					<td>
						<input name="password" type="password" id="password" size="16" maxlength="32" tabindex="2">
					</td>
				</tr>

				
				<tr>
					<td align="center" colspan="2">
						<font color="red"><%=msg == null ? "&nbsp;" : msg %></font>
					</td>
				</tr>
				<tr>
					<td align="center">
						&nbsp;
					</td>
					<td align="center">
						<input name="aceptar" type="submit" class="boton" id="aceptar" value="Ingresar" tabindex="4">
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
