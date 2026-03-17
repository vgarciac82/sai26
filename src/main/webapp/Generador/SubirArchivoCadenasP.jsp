<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.apache.commons.fileupload.DiskFileUpload"%>

<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.CallableStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>

<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	System.out.println("caso_" + c);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	System.out.println("usuario_" + usuario);
	String mensaje = (request.getParameter("mensaje") == null) ? "sin_mensaje"
			: request.getParameter("mensaje");
	System.out.println("mensaje:" + mensaje);

	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
			
		<title>Subir Archivos</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Subir Archivo">
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
		
	</style>
		
			<script type="text/javascript" src="../js/catalogo/general.js"></script>				
			<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>		
			<script type="text/javascript" src="../js/jsquery.js"></script>	
			
			<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
			<script type="text/javascript" src="js/styletable.jquery.plugin.js"></script>  
			<script type="text/javascript" src="js/jquery-1.6.2.min.js">
</script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js">
</script>										
		<style>
table, td, th
{
border:1px solid silver;
}
th
{
background-color:4D8EB9;
color:white;
font-size: 25px;
}
</style>
		<script type="text/javascript">
		
$(document).ready(function() {
	
	//$("#tblSubirArchivo").styleTable();   
	
	$("#btnLimpiar").button();
	$("#btnEnviar1").button();
	$("#btnEnviar2").button();
	$("#flArchivo1").button();
	$("#flArchivo2").button();
	
	$("#btnLimpiar").click(function() {
		limpiar();
	});
	$("#btnArchivo").click(function() {
		enviarArchivos();
	});
	$("#tblCarga").hide();
	$("#tblAyuda").toggle(200);
	var mensaje = $("#mensaje").val();
	if (mensaje)
		if (mensaje != "sin_mensaje") {
			alert($("#mensaje").val());
			$("#mensaje").val("sin_mensaje");
		}
	
	


});

function enviarArchivos(tipo, archivo1) {
	ext = new Array(".csv");

	if (!archivo1) {
		alert("No se ha cargado el archivo");
		return;
	} else {
		permitida = false;
		archivo1 = document.getElementById(archivo1).value;
		extension = (archivo1.substring(archivo1.lastIndexOf(".")))
				.toLowerCase();
		if (ext[0] == extension) {
			permitida = true;
		}
		if (!permitida) {
			alert("Comprueba la extensión de los archivos a subir. \n S\u00f3lo se pueden subir archivos con extensiones: "
					+ ext.join());
			$("#btnLimpiar1").click();
		} else {
			$("#btnEnviar1").attr("disabled", "true");
			$("#btnEnviar2").attr("disabled", "true");
			$("#btnLimpiar").attr("disabled", "true");
			$("#esperar").attr("style", "visibility=visible");

			if (tipo == "btnEnviar1") {
				document.getElementById("frmSubirArchivo1").submit();
			}

			if (tipo == "btnEnviar2") {
				document.getElementById("frmSubirArchivo2").submit();
			}

		}
	}
}


</script>

	</head>
	<body>
	
		<input type="hidden" name="mensaje" id="mensaje"value="<%=mensaje%>">
		
			<div align="center">
				<label id="esperar" style="visibility: hidden">

					Espere por favor....
					<img border="0" src="../imagenes/espera.gif" height="30">

				</label>
			</div>
			
			
			<table id="tblSubirArchivo"  border="1px" align="center">
				<tr>
					<th colspan="3" align="center">
			
					Agregar Archivo.csv
						
					</th>					
				</tr>
				<tr>
					<td>
						Cadenas Productivas
					</td>
					<td align="center">
						<form method="post"
							action="../gstnmngr/SubirArchivosCadenasP?tipoArchivo=SICOP"
							name="frmSubirArchivo1" id="frmSubirArchivo1"
							enctype="multipart/form-data">
							<input type="file" id="flArchivo1" name="flArchivo1" size=50 />
						</form>
					</td>
					<td align="center">
						<input type="button" id="btnEnviar1" name="btnEnviar1"
							onclick="enviarArchivos('btnEnviar1','flArchivo1')"
							value="Enviar">
					</td>
				</tr>
				<tr>
					<td>
						Cadenas Errores
					</td>
					<td align="center">
						<form method="post"
							action="../gstnmngr/SubirArchivosCadenasP?tipoArchivo=ERRORES"
							name="frmSubirArchivo2" id="frmSubirArchivo2"
							enctype="multipart/form-data">
							<input type="file" id="flArchivo2" name="flArchivo2" size=50 />
						</form>
					</td>
					<td align="center">
						<input type="button" id="btnEnviar2" name="btnEnviar2"
							onclick="enviarArchivos('btnEnviar2','flArchivo2')"
							value="Enviar">

					</td>
				</tr>


				<tr>
					<td colspan="3" align="center">
						<input type="reset" id="btnLimpiar" name="btnLimpiar"
							value="Limpiar" onClick="Limpiar();" />
					</td>
				</tr>

			</table>
		</div>

	</body>
</html>
